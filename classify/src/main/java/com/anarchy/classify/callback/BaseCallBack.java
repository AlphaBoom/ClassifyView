package com.anarchy.classify.callback;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.VelocityTracker;
import android.view.View;

import com.anarchy.classify.ClassifyView;

/**
 * Version 1.0
 * <p>
 * Date: 16/6/14 17:12
 * Author: rsshinide38@163.com
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
     * 拖拽发生时的回调
     * @param recyclerView
     * @param position
     */
    void onDragStart(RecyclerView recyclerView,int position);

    /**
     * 拖拽动画执行结束的回调
     * @param recyclerView
     * @param position
     */
    void onDragAnimationEnd(RecyclerView recyclerView,int position);

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
     * item点击事件
     * @param recyclerView
     * @param position
     * @param pressedView
     */
    void onItemClick(RecyclerView recyclerView,int position,View pressedView);
    /**
     * item 点击事件
     * @param position
     * @param pressedView
     * @deprecated {@link #onItemClick(RecyclerView, int, View)}
     */
    void onItemClick(int position,View pressedView);

}
