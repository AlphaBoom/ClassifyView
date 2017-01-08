package com.anarchy.classify.simple;

import android.support.v7.widget.RecyclerView;

/**
 * User:  Anarchy
 * Email:  rsshinide38@163.com
 * CreateTime: 六月/09/2016  15:54.
 * Description:
 * 用于告知如何将当前拖拽的view移动和缩放到目标位置
 * @see com.anarchy.classify.simple.SimpleAdapter.SimpleMainAdapter#onPrePareMerge(RecyclerView.ViewHolder, RecyclerView.ViewHolder, int, int)
 */
public class ChangeInfo {
    /**
     * 目标位置横坐标 相对于ItemView
     */
    public int targetLeft;
    /**
     * 目标位置纵坐标 相对于ItemView
     */
    public int targetTop;
    /**
     * 目标位置宽度
     */
    public float targetWidth;
    /**
     * 目标位置高度
     */
    public float targetHeight;
    /**
     * 源位置横坐标 相对于ItemView
     */
    public int sourceLeft;
    /**
     * 源位置纵坐标 相对于ItemView
     */
    public int sourceTop;
    /**
     * 源位置宽度
     */
    public float sourceWidth;
    /**
     * 源位置高度
     */
    public float sourceHeight;

    @Override
    public String toString() {
        return "ChangeInfo{" +
                "targetLeft=" + targetLeft +
                ", targetTop=" + targetTop +
                ", targetWidth=" + targetWidth +
                ", targetHeight=" + targetHeight +
                ", sourceLeft=" + sourceLeft +
                ", sourceTop=" + sourceTop +
                ", sourceWidth=" + sourceWidth +
                ", sourceHeight=" + sourceHeight +
                '}';
    }
}
