package com.example.android.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class CustomView extends View {

    private static int SELECTION_COUNT = 4, mActiveSelection, mSelectionCount;
    private float mWidth, mHeight, mRadius;
    private Paint mDialPaint, mTextPaint;

    private int mFanOnColor = Color.CYAN, mFanOffColor = Color.GRAY;

    private final StringBuffer mTempLabel = new StringBuffer(8);
    private final float[] mTempResult = new float[2];

    public CustomView(Context context) {
        super(context);
        init();
    }

    public void setSelectionCount(int count){
        mSelectionCount = count;
        mActiveSelection = 0;
        mDialPaint.setColor(mFanOffColor);
        invalidate();
    }

    public CustomView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        if(attrs != null){
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.CustomView, 0, 0);
            mFanOffColor = typedArray.getColor(R.styleable.CustomView_fanOffColor, mFanOffColor);
            mFanOnColor = typedArray.getColor(R.styleable.CustomView_fanOnColor, mFanOnColor);
            mSelectionCount = typedArray.getInteger(R.styleable.CustomView_selectionIndicators, SELECTION_COUNT);
            typedArray.recycle();
        }
        init();
    }

    public CustomView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if(attrs != null){
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.CustomView, 0, 0);
            mFanOffColor = typedArray.getColor(R.styleable.CustomView_fanOffColor, mFanOffColor);
            mFanOnColor = typedArray.getColor(R.styleable.CustomView_fanOnColor, mFanOnColor);
            mSelectionCount = typedArray.getInteger(R.styleable.CustomView_selectionIndicators, SELECTION_COUNT);
            typedArray.recycle();
        }
        init();
    }

    private void init(){
        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(Color.BLACK);
        mTextPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setTextSize(40f);
        mDialPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mDialPaint.setColor(mFanOffColor);

        mActiveSelection = 0;

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mActiveSelection = (mActiveSelection + 1) % mSelectionCount;
                if(mActiveSelection >= 1){
                    mDialPaint.setColor(mFanOnColor);
                }else{
                    mDialPaint.setColor(mFanOffColor);
                }
                invalidate();
            }
        });
    }

    private float[] computeXYForPosition(final int pos, final float radius, boolean isLabel){
        float[] result = mTempResult;
        Double startAngle;
        Double angle;
        if(mSelectionCount > 4){
            startAngle = Math.PI * (3/2d);
            angle = startAngle + (pos * (Math.PI /mSelectionCount));
            result[0] = (float) (radius * Math.cos(angle*2)) + (mWidth / 2);
            result[1] = (float) (radius * Math.sin(angle*2)) + (mHeight / 2);
            if(angle > Math.toRadians(360) && isLabel){
                result[1] += 20;
            }
        } else{
            startAngle = Math.PI * (9/8d);
            angle = startAngle + (pos * (Math.PI /4));
            result[0] = (float) (radius * Math.cos(angle)) + (mWidth / 2);
            result[1] = (float) (radius * Math.sin(angle)) + (mHeight / 2);
        }

        return result;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawCircle(mWidth/2, mHeight/2, mRadius, mDialPaint);

        final float labelRadius = mRadius + 20;
        StringBuffer label = mTempLabel;
        for(int i = 0; i < mSelectionCount; i++){
            float[] xyData = computeXYForPosition(i, labelRadius, true);
            float x = xyData[0];
            float y = xyData[1];
            label.setLength(0);
            label.append(i);
            canvas.drawText(label, 0, label.length(), x, y, mTextPaint);
        }

        final float markerRadius = mRadius - 35;
        float[] xyData = computeXYForPosition(mActiveSelection, markerRadius, false);
        float x =xyData[0];
        float y = xyData[1];
        canvas.drawCircle(x, y, 20, mTextPaint);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mWidth = w;
        mHeight = h;
        mRadius = (float) ((Math.min(mWidth, mHeight))/2*0.8);
    }
}
