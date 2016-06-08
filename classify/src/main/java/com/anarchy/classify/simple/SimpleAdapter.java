package com.anarchy.classify.simple;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.anarchy.classify.R;
import com.anarchy.classify.adapter.BaseMainAdapter;
import com.anarchy.classify.adapter.BaseSubAdapter;
import com.anarchy.classify.adapter.SubAdapterReference;
import com.anarchy.classify.simple.widget.CanMergeView;
import com.anarchy.classify.util.L;

import java.util.ArrayList;
import java.util.List;

/**
 * Version 1.0
 * <p/>
 * Date: 16/6/7 11:55
 * Author: zhendong.wu@shoufuyou.com
 * <p/>
 * Copyright © 2014-2016 Shanghai Xiaotu Network Technology Co., Ltd.
 */
public abstract class SimpleAdapter<T, VH extends SimpleAdapter.ViewHolder> implements BaseSimpleAdapter {
    protected List<List<T>> mData;
    private SimpleMainAdapter mSimpleMainAdapter;
    private SimpleSubAdapter mSimpleSubAdapter;

    public SimpleAdapter(List<List<T>> mData) {
        mSimpleMainAdapter = new SimpleMainAdapter(this, mData);
        mSimpleSubAdapter = new SimpleSubAdapter(this);
    }

    @Override
    public BaseMainAdapter getMainAdapter() {
        return mSimpleMainAdapter;
    }

    @Override
    public BaseSubAdapter getSubAdapter() {
        return mSimpleSubAdapter;
    }

    protected VH onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.simple_item, parent, false);
        return (VH) new ViewHolder(view);
    }

    protected void onBindMainViewHolder(VH holder, int position) {
    }

    protected void onBindSubViewHolder(VH holder, int position) {
    }

    /**
     * @param parentIndex
     * @param index       if -1  in main region
     */
    protected void onItemClick(View view, int parentIndex, int index) {
    }

    /**
     * 显示一个item的布局
     *
     * @return
     */
    public abstract View getView(ViewGroup parent, int mainPosition, int subPosition);

    class SimpleMainAdapter extends BaseMainAdapter<VH, SimpleSubAdapter> {
        private List<List<T>> mData;
        private SimpleAdapter<T, VH> mSimpleAdapter;

        public SimpleMainAdapter(SimpleAdapter<T, VH> simpleAdapter, List<List<T>> data) {
            mData = data;
            mSimpleAdapter = simpleAdapter;
        }

        @Override
        public VH onCreateViewHolder(ViewGroup parent, int viewType) {
            VH vh = mSimpleAdapter.onCreateViewHolder(parent, viewType);
            CanMergeView canMergeView = vh.getCanMergeView();
            if (canMergeView != null) {
                canMergeView.setAdapter(mSimpleAdapter);
            }
            return vh;
        }

        @Override
        public void onBindViewHolder(VH holder, int position) {
            CanMergeView canMergeView = holder.getCanMergeView();
            if (canMergeView != null) {
                canMergeView.init(position, mData.get(position));
            }
            mSimpleAdapter.onBindMainViewHolder(holder, position);
        }

        @Override
        public int getItemCount() {
            return mData.size();
        }

        @Override
        public boolean canDragOnLongPress(int position, View pressedView) {
            return true;
        }


        @Override
        public boolean onMergeStart(VH selectedViewHolder, VH targetViewHolder,
                                    int selectedPosition, int targetPosition) {
            L.d("on mergeStart:" + targetPosition);
            CanMergeView canMergeView = targetViewHolder.getCanMergeView();
            if (canMergeView != null) {
                canMergeView.onMergeStart();
            }
            return true;
        }

        @Override
        public void onMergeCancel(VH selectedViewHolder, VH targetViewHolder,
                                  int selectedPosition, int targetPosition) {
            L.d("on mergeCancel:" + targetPosition);
            CanMergeView canMergeView = targetViewHolder.getCanMergeView();
            if (canMergeView != null) {
                canMergeView.onMergeCancel();
            }
        }

        @Override
        public boolean onMerge(VH selectedViewHolder, VH targetViewHolder,
                               int selectedPosition, int targetPosition) {
            CanMergeView canMergeView = targetViewHolder.getCanMergeView();
            if (canMergeView != null) {
                canMergeView.onMerge();
            }
            mData.get(targetPosition).add(mData.get(selectedPosition).get(0));
            mData.remove(selectedPosition);
            notifyItemRemoved(selectedPosition);
            if(selectedPosition < targetPosition) {
                notifyItemChanged(targetPosition-1);
            }else {
                notifyItemChanged(targetPosition);
            }
            return true;
        }


        @Override
        public boolean onMove(int selectedPosition, int targetPosition) {
            notifyItemMoved(selectedPosition, targetPosition);
            List<T> list = mData.remove(selectedPosition);
            mData.add(targetPosition, list);
            return true;
        }

        @Override
        public boolean canMergeItem(int selectedPosition, int targetPosition) {
            List<T> currentSelected = mData.get(selectedPosition);
            return currentSelected.size() < 2;
        }


        @Override
        public int onLeaveSubRegion(int selectedPosition, SubAdapterReference<SimpleSubAdapter> subAdapterReference) {
            SimpleSubAdapter simpleSubAdapter = subAdapterReference.getAdapter();
            T t = simpleSubAdapter.getData().remove(selectedPosition);
            List<T> list = new ArrayList<>();
            list.add(t);
            mData.add(list);
            int parentIndex = simpleSubAdapter.getParentIndex();
            if (parentIndex != -1) notifyItemChanged(parentIndex);
            return mData.size() - 1;
        }

        @Override
        public void onItemClick(int position, View pressedView) {
            mSimpleAdapter.onItemClick(pressedView, position, -1);
        }

        @Override
        public List<T> explodeItem(int position, View pressedView) {
            if (position < mData.size())
                return mData.get(position);
            return null;
        }
    }

    class SimpleSubAdapter extends BaseSubAdapter<VH> {
        private List<T> mData;
        private int parentIndex = -1;
        private SimpleAdapter<T, VH> mSimpleAdapter;

        public SimpleSubAdapter(SimpleAdapter<T, VH> simpleAdapter) {
            mSimpleAdapter = simpleAdapter;
        }

        @Override
        public VH onCreateViewHolder(ViewGroup parent, int viewType) {
            VH vh = mSimpleAdapter.onCreateViewHolder(parent, viewType);
            CanMergeView canMergeView = vh.getCanMergeView();
            if (canMergeView != null) {
                canMergeView.setAdapter(mSimpleAdapter);
            }
            return vh;
        }

        public int getParentIndex() {
            return parentIndex;
        }

        @Override
        public void onBindViewHolder(VH holder, int position) {
            CanMergeView canMergeView = holder.getCanMergeView();
            if (canMergeView != null) {
                List<T> list = new ArrayList<>();
                list.add(mData.get(position));
                canMergeView.init(parentIndex, list);
            }
            mSimpleAdapter.onBindSubViewHolder(holder, position);
        }

        @Override
        public int getItemCount() {
            if (mData == null) return 0;
            return mData.size();
        }

        @Override
        public void onItemClick(int position, View pressedView) {
            mSimpleAdapter.onItemClick(pressedView, parentIndex, position);
        }

        @Override
        public void initData(int parentIndex, List data) {
            mData = data;
            this.parentIndex = parentIndex;
            notifyDataSetChanged();
        }

        @Override
        public boolean onMove(int selectedPosition, int targetPosition) {
            notifyItemMoved(selectedPosition, targetPosition);
            T t = mData.remove(selectedPosition);
            mData.add(targetPosition, t);
            return true;
        }

        public List<T> getData() {
            return mData;
        }

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private CanMergeView mCanMergeView;

        public ViewHolder(View itemView) {
            super(itemView);
            if (itemView instanceof CanMergeView) {
                mCanMergeView = (CanMergeView) itemView;
            } else if (itemView instanceof ViewGroup) {
                ViewGroup group = (ViewGroup) itemView;
                //只遍历一层 寻找第一个符合条件的view
                for (int i = 0; i < group.getChildCount(); i++) {
                    View child = group.getChildAt(i);
                    if (child instanceof CanMergeView) {
                        mCanMergeView = (CanMergeView) child;
                        break;
                    }
                }
            }
        }

        public CanMergeView getCanMergeView() {
            return mCanMergeView;
        }
    }
}
