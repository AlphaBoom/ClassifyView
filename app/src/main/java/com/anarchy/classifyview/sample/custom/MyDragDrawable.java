package com.anarchy.classifyview.sample.custom;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;

import com.anarchy.classify.simple.widget.CanMergeView;

/**
 * <p/>
 * Date: 16/6/13 12:00
 * Author: zhendong.wu@shoufuyou.com
 * <p/>
 * 只对CanMergeView 做拖拽显示
 */
public class MyDragDrawable extends Drawable {
    private  View mView;
    private  Bitmap mCacheBitmap;

    public MyDragDrawable(View view){
        if(view instanceof ViewGroup){
            mView = getCanMergeView((ViewGroup) view);
            if(mView == null) mView = view;
        }else {
            mView = view;
        }
        mView.setDrawingCacheEnabled(true);
        mView.buildDrawingCache();
        mCacheBitmap = mView.getDrawingCache();
    }



    private View getCanMergeView(ViewGroup viewGroup){
        for(int i=0;i<viewGroup.getChildCount();i++){
            View child = viewGroup.getChildAt(i);
            if(child instanceof CanMergeView){
                return child;
            }
            if(child instanceof ViewGroup){
                return getCanMergeView((ViewGroup) child);
            }
        }
        return null;
    }

    @Override
    public void draw(Canvas canvas) {
        if(mCacheBitmap == null){
            mView.draw(canvas);
        }else {
            canvas.drawBitmap(mCacheBitmap,0,0,null);
        }
    }

    @Override
    public void setAlpha(int alpha) {

    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {

    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    @Override
    public int getIntrinsicWidth() {
        return mView.getWidth();
    }

    @Override
    public int getIntrinsicHeight() {
        return mView.getHeight();
    }
}
