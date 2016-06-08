package com.anarchy.classify.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.VelocityTracker;
import android.view.View;

import java.util.List;

/**
 * Version 1.0
 * <p/>
 * Date: 16/6/1 15:10
 * Author: zhendong.wu@shoufuyou.com
 * <p/>
 * Copyright © 2014-2016 Shanghai Xiaotu Network Technology Co., Ltd.
 */
public interface MainRecyclerViewCallBack<Sub extends SubRecyclerViewCallBack> {
    void setDragPosition(int position);
    boolean canDragOnLongPress(int position, View pressedView);
    boolean canDropOVer(int selectedPosition,int targetPosition);
    boolean onMergeStart(RecyclerView parent,int selectedPosition, int targetPosition);
    boolean onMerge(RecyclerView parent,int selectedPosition,int targetPosition);
    void onMergeCancel(RecyclerView parent,int selectedPosition,int targetPosition);
    boolean onMove(int selectedPosition,int targetPosition);
    void moved(int selectedPosition,int targetPosition);
    boolean canMergeItem(int selectedPosition, int targetPosition);

    /**
     * 当次级目录移出范围时添加到 主目录
     * @param selectedPosition
     * @param subAdapterReference
     * @return 添加到主目录的位置
     */
    int onLeaveSubRegion(int selectedPosition,SubAdapterReference<Sub> subAdapterReference);

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

    /**
     * 是否要展开这个view
     * @param position
     * @param pressedView
     * @return 如果返回空 或者 长度小于 2 则不会展开 之后会调用 {@link #onItemClick(int, View)}
     * 通知这是一个点击item的事件,其他情况会根据返回的List 通知 subAdapter 进行数据更新并打开显示subview的窗口
     */
    List explodeItem(int position, View pressedView);
}
