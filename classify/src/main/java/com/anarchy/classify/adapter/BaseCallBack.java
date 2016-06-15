package com.anarchy.classify.adapter;

import android.content.Context;
import android.view.VelocityTracker;
import android.view.View;

/**
 * Version 1.0
 * <p>
 * Date: 16/6/14 17:12
 * Author: zhendong.wu@shoufuyou.com
 * <p>
 * Copyright © 2014-2016 Shanghai Xiaotu Network Technology Co., Ltd.
 */
public interface BaseCallBack {
    void setDragPosition(int position,boolean shouldNotify);
    int getDragPosition();
    boolean canDragOnLongPress(int position, View pressedView);
    boolean canDropOver(int selectedPosition,int targetPosition);

    /**
     * 返回判断移动需要的速度范围
     * 单位默认100 如果你没有重写 {@link #getCurrentState(View, View, int, int, VelocityTracker, int, int)}这个方法
     * @param context
     * @return
     */
    float getVelocity(Context context);
    /**
     * 返回当前的状态 是移动 还是在merge范围中
     * @param selectedView
     * @param targetView
     * @param x
     * @param y
     * @return
     */
    int getCurrentState(View selectedView, View targetView, int x, int y, VelocityTracker velocityTracker, int selectedPosition, int targetPosition);
    /**
     *
     * @param position
     * @param pressedView
     */
    void onItemClick(int position,View pressedView);
}
