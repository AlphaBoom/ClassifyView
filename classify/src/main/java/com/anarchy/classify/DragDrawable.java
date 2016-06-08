package com.anarchy.classify;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewCompat;
import android.view.View;

import com.anarchy.classify.util.L;

/**
 * Version 1.0
 * <p/>
 * Date: 16/6/2 15:41
 * Author: zhendong.wu@shoufuyou.com
 * <p/>
 * Copyright © 2014-2016 Shanghai Xiaotu Network Technology Co., Ltd.
 */
public class DragDrawable extends Drawable {
    final private View mView;
    final private Bitmap mBitmap;
    public DragDrawable(View view){
        mView  = view;
        mView.setDrawingCacheEnabled(true);
        mView.buildDrawingCache();
        mBitmap = mView.getDrawingCache();
    }
    @Override
    public void draw(Canvas canvas) {
        if(mBitmap == null) {
            mView.draw(canvas);
        }else {
            canvas.drawBitmap(mBitmap,0,0,null);
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
        return mView.getWidth();
    }

    @Override
    public int getIntrinsicHeight() {
        return mView.getHeight();
    }
}
