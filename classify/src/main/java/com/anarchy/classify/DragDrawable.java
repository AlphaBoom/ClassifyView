package com.anarchy.classify;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.view.View;

import java.lang.ref.WeakReference;

/**
 * <p/>
 * Date: 16/6/2 15:41
 * Author: rsshinide38@163.com
 * Description:用于绘制拖动的View 在拖动状况下显示的效果
 * 默认绘制整个拖动的Item
 * <p/>
 */
public class DragDrawable extends Drawable {
    private static final int KEY_SHADOW_COLOR = 0x1E000000;
    private static final int FILL_SHADOW_COLOR = 0x3D000000;
    private static final float Y_OFFSET = 1.75f;
    final private static float SHADOW_RADIUS = 3.5f;
    final private View mView;
    private Bitmap mBitmap;
    final private Paint mPaint;
    private boolean showShadow;
    private int shadowOffset;
    private Rect mShadowRect;

    public DragDrawable(@NonNull View view) {
        this(view, false);
    }

    public DragDrawable(@NonNull View view, boolean showShadow) {
        mShadowRect = new Rect();
        this.showShadow = showShadow;
        float density = view.getContext().getResources().getDisplayMetrics().density;
        this.shadowOffset = (int) (density * SHADOW_RADIUS);
        mView = view;
        mView.setDrawingCacheEnabled(true);
        mView.destroyDrawingCache();
        mView.buildDrawingCache();
        mBitmap = Bitmap.createBitmap(mView.getDrawingCache());
        mPaint = new Paint();
        int radius = (getIntrinsicHeight() + getIntrinsicWidth()) / 2;
        mPaint.setShader(new RadialGradient(getIntrinsicWidth() / 2, getIntrinsicHeight() / 2, radius, new int[]{FILL_SHADOW_COLOR, 0}, null, Shader.TileMode.CLAMP));
    }


    @Override
    public void draw(Canvas canvas) {
        if (mBitmap == null || mBitmap.isRecycled()) {
            mView.setDrawingCacheEnabled(true);
            mView.destroyDrawingCache();
            mView.buildDrawingCache();
            mBitmap = Bitmap.createBitmap(mView.getDrawingCache());
            if(mBitmap == null || mBitmap.isRecycled()) {
                mView.draw(canvas);
            }
        } else {
            if (showShadow) {
//                mShadowRect.set(shadowOffset,shadowOffset,getIntrinsicWidth(),getIntrinsicHeight());
//                canvas.drawRect(mShadowRect,mPaint);
                canvas.drawBitmap(mBitmap, 0, 0, null);
            } else {
                canvas.drawBitmap(mBitmap, 0, 0, null);
            }
        }
    }

    @Override
    public void setAlpha(int alpha) {
        //nothing
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {//nothing
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    @Override
    public int getIntrinsicWidth() {
        return showShadow ? mView.getWidth() + shadowOffset : mView.getWidth();
    }

    @Override
    public int getIntrinsicHeight() {
        return showShadow ? mView.getHeight() + shadowOffset : mView.getHeight();
    }


    public Paint getPaint() {
        return mPaint;
    }
}
