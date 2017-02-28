package com.anarchy.classify.simple;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.anarchy.classify.R;

import java.util.ArrayList;
import java.util.List;

/**
 * <p/>
 * Date: 16/6/7 11:55
 * Author: rsshinide38@163.com
 * <p/>
 */
public abstract class SimpleAdapter<T, VH extends SimpleAdapter.ViewHolder> extends PrimitiveSimpleAdapter<List<T>, VH> {

    protected List<List<T>> mData;

    public SimpleAdapter(List<List<T>> data) {
        mData = data;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected VH onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.simple_item, parent, false);
        return (VH) new ViewHolder(view);
    }

    /**
     * 返回主层级数量
     *
     * @return
     */
    @Override
    protected int getItemCount() {
        if (mData == null) return 0;
        return mData.size();
    }

    /**
     * 副层级的数量，用于主层级上的显示效果
     *
     * @return
     */
    @Override
    protected int getSubItemCount(int parentPosition) {
        if(mData == null) return 0;
        return mData.get(parentPosition).size();
    }


    @Override
    protected List<T> getSubSource(int parentPosition) {
        return mData.get(parentPosition);
    }

    @Override
    protected boolean canExplodeItem(int position, View pressedView) {
        if (position < mData.size() && mData.get(position).size() > 1) {
            return true;
        }
        return false;
    }


    /**
     * 在主层级触发move事件 在这里进行数据改变
     *
     * @param selectedPosition 当前选择的item位置
     * @param targetPosition   要移动到的位置
     */
    @Override
    protected void onMove(int selectedPosition, int targetPosition) {
        List<T> list = mData.remove(selectedPosition);
        mData.add(targetPosition, list);
    }


    @Override
    protected void onSubMove(List<T> ts, int selectedPosition, int targetPosition) {
        ts.add(targetPosition, ts.remove(selectedPosition));
    }

    @Override
    protected int onLeaveSubRegion(int parentPosition,List<T> ts, int selectedPosition) {
        List<T> list = new ArrayList<>();
        list.add(ts.remove(selectedPosition));
        mData.add(list);
        return mData.size() - 1;
    }

    /**
     * 两个选项能否合并
     *
     * @param selectPosition
     * @param targetPosition
     * @return
     */
    @Override
    protected boolean canMergeItem(int selectPosition, int targetPosition) {
        List<T> currentSelected = mData.get(selectPosition);
        return currentSelected.size() < 2;
    }

    /**
     * 合并数据处理
     *
     * @param selectedPosition
     * @param targetPosition
     */
    @Override
    protected void onMerged(int selectedPosition, int targetPosition) {
        mData.get(targetPosition).add(mData.get(selectedPosition).get(0));
        mData.remove(selectedPosition);
    }


    /**
     * 主层级数据绑定
     *
     * @param holder
     * @param position
     */
    @Override
    protected void onBindMainViewHolder(VH holder, int position) {

    }

    /**
     * 副层级数据绑定
     *
     * @param holder
     * @param mainPosition
     * @param subPosition
     */
    @Override
    protected void onBindSubViewHolder(VH holder, int mainPosition, int subPosition) {

    }

    public static class ViewHolder extends PrimitiveSimpleAdapter.ViewHolder {

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
