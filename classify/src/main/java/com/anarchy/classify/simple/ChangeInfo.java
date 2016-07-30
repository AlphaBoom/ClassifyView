package com.anarchy.classify.simple;

import android.graphics.Point;
import android.support.v7.widget.RecyclerView;

/**
 * User:  Anarchy
 * Email:  rsshinide38@163.com
 * CreateTime: 六月/09/2016  15:54.
 * Description:
 * 以{@link com.anarchy.classify.simple.widget.InsertAbleGridView}为例,
 * @see #left 是子view 在 InsertAbleGridView 中 向左侧的位置child.getLeft().
 * @see #top  是子view 在 InsertAbleGridView 中 距上方的位置child.getTop().
 * @see #itemWidth 子view 的宽度
 * @see #itemHeight 子view 的高度
 * @see #paddingLeft InsertAbleGridView 距离 最外层布局的左侧padding值,
 * eg.一个InsertAbleGridView放置在FrameLayout下 FrameLayout 设置了 paddingLeft
 * 如果对于复杂的View结构 可能在FrameLayout 除了InsertAbleGridView 还有其他布局可以依然改变paddingleft
 * 及其他padding值用来定位InsertAbleGridView 位于 Adapter中一个ItemView的相对位置
 * @see #paddingBottom ditto
 * @see #paddingRight ditto
 * @see #paddingTop ditto
 * @see #outlinePadding  用于修正拖动的view 正确做出动画到合并之后的位置 例如InsertAbleGridView
 * 显示内容的部分距离view的四周有一个为 outlinePadding 的距离.outlinePadding 用于做出外部包裹框的扩展.
 * 也可以把这个outLinePadding 看做是InsertAbleGridView 的Padding属性
 * @see com.anarchy.classify.simple.SimpleAdapter.SimpleMainAdapter#onPrePareMerge(RecyclerView.ViewHolder, RecyclerView.ViewHolder, int, int)
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
