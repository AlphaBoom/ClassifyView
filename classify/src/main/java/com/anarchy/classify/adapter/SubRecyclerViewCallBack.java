package com.anarchy.classify.adapter;

import java.util.List;

/**
 * <p/>
 * Date: 16/6/1 15:11
 * Author: zhendong.wu@shoufuyou.com
 * <p/>
 */
public interface SubRecyclerViewCallBack extends BaseCallBack{


    /**
     * 下面进行数据初始化 和 显示
     * @param data
     */
    void initData(int parentIndex,List data);


    boolean onMove(int selectedPosition,int targetPosition);
    void moved(int selectedPosition,int targetPosition);

    /**
     * 是否支持移出次级目录
     * @param selectedPosition
     * @return
     */
    boolean canDragOut(int selectedPosition);

}
