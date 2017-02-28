package com.anarchy.classify.simple.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.util.Property;


/**
 * <p/>
 * Date: 16/6/7 10:32
 * Author: rsshinide38@163.com
 * <p/>
 */
class BagDrawable extends Drawable {
    private RectF mRectF;
    private Paint mPaint;
    private Paint mOutlinePaint;
    private boolean keepShow = false;
    private boolean inMerge = false;
    private int mOutLineWidth;
    private int mOutLineColor;
    private int mOutlinePadding;
    private int mSavedOutlinePadding;
    private float mRadius = 5;
    private int mAnimationDuration = 200;
    private ObjectAnimator mStarAnimator;
    private ObjectAnimator mCancelAnimator;
    //    private int[] mColors = new int[]{0xFF808080,0xFF808080,0xFFDDDDDD,0xFFFFFFFF,0xFFDDDDDD,0xFF808080,0xFF808080,
//    0xFFDDDDDD,0xFFFFFFFF,0xFFDDDDDD,0xFF808080,0xFF808080};
//    private float[] mPositions = new float[]{0f,0.11f,0.11f,0.125f,0.14f,0.14f,0.61f,0.61f,0.625f,0.64f,0.64f,1f};
    private int mCenterColor = 0xFFFFFFFF;
    private int mEdgeColor = 0xFF808080;

    public BagDrawable(int outlinePadding) {
        mRectF = new RectF();
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mOutlinePaint = new Paint();
        mOutlinePaint.setAntiAlias(true);
        mOutlinePaint.setStyle(Paint.Style.STROKE);
        mOutlinePaint.setStrokeCap(Paint.Cap.ROUND);
        mOutlinePadding = outlinePadding;
        mSavedOutlinePadding = outlinePadding;
        mRadius = outlinePadding;
    }

    public void setKeepShow(boolean keepShow) {
        this.keepShow = keepShow;
    }

    public void setOutlineStyle(int color, int width) {
        mOutLineColor = color;
        mOutLineWidth = width;
    }

    @Override
    public void draw(Canvas canvas) {
        if (keepShow||inMerge) {
        canvas.save();
        canvas.clipRect(getBounds());
        mRectF.set(getBounds());

        mRectF.inset(mOutlinePadding, mOutlinePadding);
        if (mOutLineWidth > 0) {
            mOutlinePaint.setStrokeWidth(mOutLineWidth);
            mRectF.inset(mOutLineWidth, mOutLineWidth);
            mOutlinePaint.setColor(mOutLineColor);
            canvas.drawRoundRect(mRectF, mRadius, mRadius, mOutlinePaint);
        }
//            mPaint.setShader(new SweepGradient(mRectF.centerX(),mRectF.centerY(),mColors,mPositions));
        mPaint.setShader(new RadialGradient(mRectF.centerX(), mRectF.centerY(), mRectF.width(), mCenterColor, mEdgeColor, Shader.TileMode.CLAMP));
        canvas.drawRoundRect(mRectF, mRadius, mRadius, mPaint);
        canvas.restore();
        }
    }

    @Override
    public void setAlpha(int alpha) {
        mPaint.setAlpha(alpha);
        invalidateSelf();
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        mPaint.setColorFilter(colorFilter);
        invalidateSelf();
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    public void startMergeAnimation() {
        if(mCancelAnimator != null&&mCancelAnimator.isRunning()){
            mCancelAnimator.cancel();
        }
        if(mStarAnimator == null) {
            mStarAnimator = ObjectAnimator.ofInt(this, mOutlineProperty, 0);
            mStarAnimator.setDuration(mAnimationDuration);
            mStarAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    inMerge = true;
                }
            });
        }else if(mStarAnimator.isRunning()){
            mStarAnimator.cancel();
        }
        mStarAnimator.start();
    }
    public void cancelMergeAnimation(){
        if(mStarAnimator != null && mStarAnimator.isRunning()){
            mStarAnimator.cancel();
        }
        if(mCancelAnimator == null) {
            mCancelAnimator = ObjectAnimator.ofInt(this, mOutlineProperty, mSavedOutlinePadding);
            mCancelAnimator.setDuration(mAnimationDuration);
            mCancelAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    inMerge = false;
                }
            });
        }else if(mCancelAnimator.isRunning()){
            mCancelAnimator.cancel();
        }
        mCancelAnimator.start();
    }

    private Property<BagDrawable,Integer> mOutlineProperty = new Property<BagDrawable, Integer>(Integer.class,"outline") {
        @Override
        public Integer get(BagDrawable object) {
            return object.mOutlinePadding;
        }

        @Override
        public void set(BagDrawable object, Integer value) {
            object.mOutlinePadding = value;
            invalidateSelf();
        }
    };
}
