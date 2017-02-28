package com.anarchy.classify.callback;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.anarchy.classify.MergeInfo;

import java.util.List;

/**
 * Version 1.0
 * <p/>
 * Date: 16/6/1 15:10
 * Author: rsshinide38@163.com
 * <p/>
 */
public interface MainRecyclerViewCallBack<Sub extends SubRecyclerViewCallBack> extends BaseCallBack{
    /**
     * 进入准备合并状态
     * @param parent
     * @param selectedPosition
     * @param targetPosition
     * @return
     */
    boolean onMergeStart(RecyclerView parent,int selectedPosition, int targetPosition);

    /**
     * 合并结束
     * @param parent
     * @param selectedPosition
     * @param targetPosition
     */
    void onMerged(RecyclerView parent, int selectedPosition, int targetPosition);

    /**
     * 准备执行合并
     * @param parent
     * @param selectedPosition
     * @param targetPosition
     * @return 返回执行合并动画所需参数
     */
    MergeInfo onPrepareMerge(RecyclerView parent, int selectedPosition, int targetPosition);

    /**
     * 合并动画开始
     * @param parent
     * @param selectedPosition
     * @param targetPosition
     * @param duration
     */
    void onStartMergeAnimation(RecyclerView parent,int selectedPosition,int targetPosition,int duration);

    /**
     * 离开准备合并状态
     * @param parent
     * @param selectedPosition
     * @param targetPosition
     */
    void onMergeCancel(RecyclerView parent,int selectedPosition,int targetPosition);

    /**
     * 是否进行移动 可以在这里做数据改变
     * @param selectedPosition
     * @param targetPosition
     * @return
     */
    boolean onMove(int selectedPosition,int targetPosition);

    /**
     * 移动完成
     * @param selectedPosition
     * @param targetPosition
     */
    void moved(int selectedPosition,int targetPosition);

    /**
     *
     * @param selectedPosition
     * @param targetPosition
     * @return true 可以合并 false
     */
    boolean canMergeItem(int selectedPosition, int targetPosition);

    /**
     * 当次级目录移出范围时的回调
     * @param selectedPosition
     * @param subAdapter
     * @return 添加到主目录的位置
     */
    int onLeaveSubRegion(int selectedPosition,Sub subAdapter);

    /**
     * 是否展开当前项
     * @param position
     * @return
     */
    boolean canExplodeItem(int position, View pressedView);
    /**
     * 是否要展开这个view
     * @param position
     * @param pressedView
     * @return 如果返回空 则不会展开 之后会调用 {@link #onItemClick(int, View)}
     * 通知这是一个点击item的事件,其他情况会根据返回的List 通知 subAdapter 进行数据更新并打开显示subview的窗口
     * @deprecated 使用数据无关类型的回调 {@link #canExplodeItem(int, View)}
     */
    List explodeItem(int position, View pressedView);

}
