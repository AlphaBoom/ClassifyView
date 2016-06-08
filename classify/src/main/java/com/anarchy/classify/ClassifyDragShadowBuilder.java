package com.anarchy.classify;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.view.View;

import java.lang.ref.WeakReference;

/**
 *只是为了获取drag的触发事件 不绘制拖动的view
 */
public class ClassifyDragShadowBuilder extends View.DragShadowBuilder {
    private final WeakReference<View> mView;
    public ClassifyDragShadowBuilder(){
        mView = new WeakReference<>(null);
    }
    public ClassifyDragShadowBuilder(View view){
        mView = new WeakReference<>(view);
    }

    @Override
    public void onDrawShadow(Canvas canvas) {
      //nothing
    }

    @Override
    public void onProvideShadowMetrics(Point shadowSize, Point shadowTouchPoint) {
        final View view = mView.get();
        if (view != null) {
            shadowSize.set(view.getWidth(), view.getHeight());
            shadowTouchPoint.set(shadowSize.x/2, shadowSize.y/2);
        }
    }

    public void showShadow(){

    }
}
