package com.anarchy.classify;

import android.graphics.Point;

/**
 * User:  Anarchy
 * Email:  rsshinide38@163.com
 * CreateTime: 六月/09/2016  15:54.
 * Description:
 */
public class ChangeInfo {
    public int left;
    public int top;
    public int itemWidth;
    public int itemHeight;
    public int paddingLeft;
    public int paddingTop;
    public int paddingBottom;
    public int paddingRight;
    public int outlinePadding;
    @Override
    public String toString() {
        return "ChangeInfo{" +
                "left=" + left +
                ", top=" + top +
                ", itemWidth=" + itemWidth +
                ", itemHeight=" + itemHeight +
                '}';
    }
}
