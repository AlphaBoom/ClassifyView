package com.anarchy.classify.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.VelocityTracker;
import android.view.View;

import com.anarchy.classify.MergeInfo;
import com.anarchy.classify.ClassifyView;
import com.anarchy.classify.callback.MainRecyclerViewCallBack;
import com.anarchy.classify.callback.SubRecyclerViewCallBack;

import java.util.ArrayList;
import java.util.List;

/**
 * <p/>
 * Date: 16/6/1 15:33
 * Author: rsshinide38@163.com
 * <p/>
 */
public abstract class BaseMainAdapter<VH extends RecyclerView.ViewHolder, Sub extends SubRecyclerViewCallBack> extends RecyclerView.Adapter<VH> implements MainRecyclerViewCallBack<Sub> {
    private final static int VELOCITY = 1;
    private int mSelectedPosition = SELECT_UNKNOWN;
    private List mDummySource = new ArrayList();

    @Override
    public void setDragPosition(int position,boolean shouldNotify) {
        if (position >= getItemCount() || position < -1) return;
        if (position == -1 && mSelectedPosition != -1) {
            int oldPosition = mSelectedPosition;
            mSelectedPosition = position;
            if(shouldNotify) notifyItemChanged(oldPosition);
        } else {
            mSelectedPosition = position;
            if(shouldNotify) notifyItemChanged(mSelectedPosition);
        }
    }


    @Override
    public int getDragPosition() {
        return mSelectedPosition;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onDragStart(RecyclerView recyclerView, int position) {
        VH selectedViewHolder = (VH) recyclerView.findViewHolderForAdapterPosition(position);
        if(selectedViewHolder == null) return;
        onDragStart(selectedViewHolder,position);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onDragAnimationEnd(RecyclerView recyclerView, int position) {
        VH selectedViewHolder = (VH) recyclerView.findViewHolderForAdapterPosition(position);
        if(selectedViewHolder == null) return;
        onDragAnimationEnd(selectedViewHolder,position);
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean onMergeStart(RecyclerView parent, int selectedPosition, int targetPosition) {
        VH selectedViewHolder = (VH) parent.findViewHolderForAdapterPosition(selectedPosition);
        VH targetViewHolder = (VH) parent.findViewHolderForAdapterPosition(targetPosition);
        if(selectedViewHolder == null || targetViewHolder == null) return false;
        return onMergeStart(selectedViewHolder, targetViewHolder, selectedPosition, targetPosition);
    }
    @SuppressWarnings("unchecked")
    @Override
    public void onMergeCancel(RecyclerView parent, int selectedPosition, int targetPosition) {
        VH selectedViewHolder = (VH) parent.findViewHolderForAdapterPosition(selectedPosition);
        VH targetViewHolder = (VH) parent.findViewHolderForAdapterPosition(targetPosition);
        if(selectedViewHolder == null || targetViewHolder == null) return;
         onMergeCancel(selectedViewHolder, targetViewHolder, selectedPosition, targetPosition);
    }
    @SuppressWarnings("unchecked")
    @Override
    public void onMerged(RecyclerView parent, int selectedPosition, int targetPosition) {
        VH selectedViewHolder = (VH) parent.findViewHolderForAdapterPosition(selectedPosition);
        VH targetViewHolder = (VH) parent.findViewHolderForAdapterPosition(targetPosition);
        if(selectedViewHolder == null || targetViewHolder == null) return;
        onMerged(selectedViewHolder, targetViewHolder, selectedPosition, targetPosition);
    }
    @SuppressWarnings("unchecked")
    @Override
    public MergeInfo onPrepareMerge(RecyclerView parent, int selectedPosition, int targetPosition) {
        VH selectedViewHolder = (VH) parent.findViewHolderForAdapterPosition(selectedPosition);
        VH targetViewHolder = (VH) parent.findViewHolderForAdapterPosition(targetPosition);
        if(selectedViewHolder == null || targetViewHolder == null) return null;
        return onPrePareMerge(selectedViewHolder, targetViewHolder, selectedPosition, targetPosition);
    }
    @SuppressWarnings("unchecked")
    @Override
    public void onStartMergeAnimation(RecyclerView parent, int selectedPosition, int targetPosition,int duration) {
        VH selectedViewHolder = (VH) parent.findViewHolderForAdapterPosition(selectedPosition);
        VH targetViewHolder = (VH) parent.findViewHolderForAdapterPosition(targetPosition);
        if(selectedViewHolder == null || targetViewHolder == null) return;
        onStartMergeAnimation(selectedViewHolder, targetViewHolder, selectedPosition, targetPosition,duration);
    }

    public void onDragStart(VH selectedViewHolder,int selectedPosition){

    }

    public void onDragAnimationEnd(VH selectedViewHolder,int selectedPosition){

    }

    public abstract boolean onMergeStart(VH selectedViewHolder, VH targetViewHolder, int selectedPosition, int targetPosition);

    public abstract void onMergeCancel(VH selectedViewHolder, VH targetViewHolder, int selectedPosition, int targetPosition);

    public abstract void onMerged(VH selectedViewHolder, VH targetViewHolder, int selectedPosition, int targetPosition);

    public abstract MergeInfo onPrePareMerge(VH selectedViewHolder, VH targetViewHolder, int selectedPosition, int targetPosition);

    public abstract void onStartMergeAnimation(VH selectedViewHolder, VH targetViewHolder, int selectedPosition, int targetPosition,int duration);

    @Override
    public void onBindViewHolder(VH holder, int position, List<Object> payloads) {
        if (position == mSelectedPosition) {
            holder.itemView.setVisibility(View.INVISIBLE);
        } else {
            holder.itemView.setVisibility(View.VISIBLE);
        }
        super.onBindViewHolder(holder, position, payloads);
    }

    @Override
    public boolean canDropOver(int selectedPosition, int targetPosition) {
        return true;
    }

    @Override
    public int getCurrentState(View selectedView, View targetView, int x, int y,
                               VelocityTracker velocityTracker, int selectedPosition,
                               int targetPosition) {
        if (velocityTracker == null) return ClassifyView.MOVE_STATE_NONE;
        int left = x;
        int top = y;
        int right = left + selectedView.getWidth();
        int bottom = top + selectedView.getHeight();
        if (canMergeItem(selectedPosition, targetPosition)) {
            if ((Math.abs(left - targetView.getLeft()) + Math.abs(right - targetView.getRight()) +
                    Math.abs(top - targetView.getTop()) + Math.abs(bottom - targetView.getBottom()))
                    < (targetView.getWidth() + targetView.getHeight()
            ) / 3) {
                return ClassifyView.MOVE_STATE_MERGE;
            }
        }
        if ((Math.abs(left - targetView.getLeft()) + Math.abs(right - targetView.getRight()) +
                Math.abs(top - targetView.getTop()) + Math.abs(bottom - targetView.getBottom()))
                < (targetView.getWidth() + targetView.getHeight()
        ) / 2) {
            velocityTracker.computeCurrentVelocity(100);
            float xVelocity = velocityTracker.getXVelocity();
            float yVelocity = velocityTracker.getYVelocity();
            float limit = getVelocity(targetView.getContext());
            if (xVelocity < limit && yVelocity < limit) {
                return ClassifyView.MOVE_STATE_MOVE;
            }
        }
        return ClassifyView.MOVE_STATE_NONE;
    }

    @Override
    public boolean canExplodeItem(int position, View pressedView) {
        return false;
    }

    @Override
    public List explodeItem(int position, View pressedView) {
        return canExplodeItem(position,pressedView)? mDummySource :null;
    }

    @Override
    public float getVelocity(Context context) {
        float density = context.getResources().getDisplayMetrics().density;
        return density * VELOCITY + .5f;
    }

    @Override
    public void moved(int selectedPosition, int targetPosition) {

    }

    @Override
    public void onItemClick(RecyclerView recyclerView, int position, View pressedView) {
        onItemClick(position,pressedView);
    }

    @Override
    public void onItemClick(int position, View pressedView) {

    }
}
