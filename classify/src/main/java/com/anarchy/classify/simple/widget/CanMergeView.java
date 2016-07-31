package com.anarchy.classify.simple.widget;

import com.anarchy.classify.simple.ChangeInfo;
import com.anarchy.classify.simple.SimpleAdapter;

import java.util.List;

/**
 * Version 1.0
 * <p>
 * Date: 16/6/7 10:29
 * Author: zhendong.wu@shoufuyou.com
 * <p>
 * Copyright © 2014-2016 Shanghai Xiaotu Network Technology Co., Ltd.
 */
public interface CanMergeView {
    /**
     * 进入merge状态
     */
    void onMergeStart();

    /**
     * 离开merge状态
     */
    void onMergeCancel();

    /**
     * 结束merge事件
     */
    void onMerged();

    /**
     * 开始merge动画
     * @param duration  动画持续时间
     */
    void startMergeAnimation(int duration);

    /**
     * 准备merge
     * @return 返回新添加的view 应该放置在布局中的位置坐标
     */
    ChangeInfo prepareMerge();
    /**
     * 设置适配器
     * @param simpleAdapter
     */
    void setAdapter(SimpleAdapter simpleAdapter);

    /**
     * 初始化或更新主层级
     * @param list
     */
    void initOrUpdateMain(int parentIndex, List list);

    /**
     * 初始化或更新次级层级
     * @param parentIndex
     * @param subIndex
     */
    void initOrUpdateSub(int parentIndex, int subIndex);


    int getOutlinePadding();
}
