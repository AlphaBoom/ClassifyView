package com.anarchy.classify.adapter;

import android.content.Context;
import android.view.VelocityTracker;
import android.view.View;

import com.anarchy.classify.ClassifyView;

/**
 * Version 1.0
 * <p>
 * Date: 16/6/14 17:12
 * Author: zhendong.wu@shoufuyou.com
 * <p>
 */
public interface BaseCallBack {
    int SELECT_UNKNOWN = -1;
    /**
     * 设置当前拖动的位置位于Adapter
     * @see #getDragPosition()
     * @param position 拖动的位置
     * @param shouldNotify 是否通知更新界面
     */
    void setDragPosition(int position,boolean shouldNotify);

    /**
     * 获取当前拖动的位置位于Adapter
     * @see #setDragPosition(int, boolean)
     * @return 当前拖动的位置 如果是-1 则当前没用拖动的View
     */
    int getDragPosition();

    /**
     *
     * @param position
     * @param pressedView
     * @return true 长按可拖动 false 不可拖动
     */
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
    @ClassifyView.MoveState
    int getCurrentState(View selectedView, View targetView, int x, int y, VelocityTracker velocityTracker, int selectedPosition, int targetPosition);
    /**
     * item 点击事件
     * @param position
     * @param pressedView
     */
    void onItemClick(int position,View pressedView);
}
