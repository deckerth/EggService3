package com.deckerth.thomas.eggservice.controls;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.deckerth.thomas.eggservice.R;

public class Hourglass extends View {

    private static final float offset = 50;

    private Paint mBorderPaint = new Paint();
    private Paint mUpperSandPaint = new Paint();
    private Paint mLowerSandPaint = new Paint();

    private float mMax;
    private float mProgress;
    private float mPercentDone;

    private void setPercentDone() {
        mPercentDone = 0F;
        if ((mMax > 0F) && (mProgress >= 0F)) {
            mPercentDone = mProgress / mMax * 100F;
            if (mPercentDone > 100F) mPercentDone = 100F;
        } else {
            mPercentDone = 100F;
        }
        invalidate();
        requestLayout();
    }

    public float getMax() {
        return mMax;
    }

    public void setMax(float max) {
        if (mMax != max) {
            mMax = max;
            setPercentDone();
        }
    }

    public float getProgress() {
        return mProgress;
    }

    public void setProgress(float progress) {
        if (mProgress != progress) {
            mProgress = progress;
            setPercentDone();
        }
    }

    private Path mHourGlassPath;

    public Hourglass(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.Hourglass,
                0, 0);

        try {
            mMax = a.getFloat(R.styleable.Hourglass_max, 100);
            mProgress = a.getFloat(R.styleable.Hourglass_progress, 100);
        } finally {
            a.recycle();
        }

        mPercentDone = 100;
        if (mMax > 0F) {
            mPercentDone = mProgress / mMax * 100F;
            if (mPercentDone > 100F) mPercentDone = 100F;
        }

        mHourGlassPath = hourGlassPath();
        mBorderPaint.setPathEffect(new CornerPathEffect(20));
        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setColor(context.getColor(R.color.colorHourglass));
        mBorderPaint.setStrokeWidth(30);

        mUpperSandPaint.setStyle(Paint.Style.FILL);
        mUpperSandPaint.setColor(context.getColor(R.color.colorSand));

        mLowerSandPaint.setStyle(Paint.Style.FILL);
        mLowerSandPaint.setColor(context.getColor(R.color.colorSand));
        mLowerSandPaint.setPathEffect(new CornerPathEffect(50));
    }

    private Path hourGlassPath() {
        Path path = new Path();

        path.moveTo(offset+0,offset+0);
        path.lineTo(offset+200,offset+0);
        path.lineTo(offset+0,offset+300);
        path.lineTo(offset+200,offset+300);
        path.lineTo(offset+0,offset+0);
        path.close();

        return path;
    }

    private Path upperSand(Float percent) {
        Path path = new Path();

        path.moveTo(offset+100,offset+150);
        path.lineTo(offset+100+percent, offset+(150 - percent * 1.5F));
        path.lineTo(offset+100-percent,offset+(150 - percent * 1.5F));
        path.lineTo(offset+100,offset+150);
        path.close();

        return path;
    }

    private Path lowerSand(Float percent) {
        Path path = new Path();

        path.moveTo(offset+0,offset+300);
        path.lineTo(offset+200, offset+300);
        path.lineTo(offset+(200 - (2-percent/100) * percent),offset+(300 - percent * 1.5F));
        path.lineTo(offset+((2-percent/100) * percent),offset+(300 - percent * 1.5F) );
        path.lineTo(offset+0,offset+300);
        path.close();

        return path;
    }

    private Path mediumSand() {
        Path path = new Path();

        path.moveTo(offset+98,offset+150);
        path.lineTo(offset+102, offset+150);
        path.lineTo(offset+102,offset+300);
        path.lineTo(offset+98,offset+300);
        path.lineTo(offset+98,offset+150);
        path.close();

        return path;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawPath(upperSand(100F-mPercentDone), mUpperSandPaint);
        canvas.drawPath(lowerSand(mPercentDone), mLowerSandPaint);
        if (mPercentDone < 100F) canvas.drawPath(mediumSand(),mUpperSandPaint);
        canvas.drawPath(mHourGlassPath, mBorderPaint);
    }

    private int measureDimension(int desiredSize, int measureSpec) {

        int result;

        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            result = desiredSize;
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }

        if (result < desiredSize){
            Log.e("ChartView", "The view is too small, the content might get cut");
        }

        return result;

    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        //int desiredWidth = getSuggestedMinimumWidth() + getPaddingLeft() + getPaddingRight();
        //int desiredHeight = getSuggestedMinimumHeight() + getPaddingTop() + getPaddingBottom();

        int desiredWidth = 350;
        int desiredHeight = 400;

        setMeasuredDimension(measureDimension(desiredWidth, widthMeasureSpec),
                measureDimension(desiredHeight, heightMeasureSpec));
    }
}
