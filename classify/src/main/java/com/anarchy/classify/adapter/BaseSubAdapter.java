package com.anarchy.classify.adapter;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.VelocityTracker;
import android.view.View;


import com.anarchy.classify.ClassifyView;
import com.anarchy.classify.callback.SubRecyclerViewCallBack;

import java.util.List;

/**
 * Version 1.0
 * <p>
 * Date: 16/6/1 15:34
 * Author: rsshinide38@163.com
 * <p>
 */
public abstract class BaseSubAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> implements SubRecyclerViewCallBack {
    private final static int VELOCITY = 1;
    @Override
    public boolean canDragOnLongPress(int position, View pressedView) {
        return true;
    }

    private int mSelectedPosition = SELECT_UNKNOWN;

    @Override
    public void setDragPosition(int position,boolean shouldNotify) {
        if(position >= getItemCount()||position<-1) return;
        if(position == -1 && mSelectedPosition != -1){
//            int oldPosition = mSelectedPosition;
            mSelectedPosition = position;
            if(shouldNotify) notifyDataSetChanged();
//            notifyItemChanged(oldPosition);
        }else {
            mSelectedPosition = position;
            if(shouldNotify) notifyItemChanged(mSelectedPosition);
        }
    }

    @Override
    public void initData(int parentIndex, List data) {
        prepareExplodeItem(parentIndex);
    }

    @Override
    public void prepareExplodeItem(int parentPosition) {

    }

    @Override
    public void onDialogShow(Dialog subDialog, int parentPosition) {

    }

    @Override
    public void onDialogCancel(Dialog subDialog, int parentPosition) {

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


    public void onDragStart(VH selectedViewHolder,int selectedPosition){

    }

    public void onDragAnimationEnd(VH selectedViewHolder,int selectedPosition){

    }

    @Override
    public int getDragPosition() {
        return mSelectedPosition;
    }

    @Override
    public void onBindViewHolder(VH holder, int position, List<Object> payloads) {
        if(position == mSelectedPosition){
            holder.itemView.setVisibility(View.INVISIBLE);
        }else {
            holder.itemView.setVisibility(View.VISIBLE);
        }
        super.onBindViewHolder(holder, position, payloads);
    }

    @Override
    public boolean canDropOver(int selectedPosition, int targetPosition) {
        return true;
    }

    @Override
    public boolean canDragOut(int selectedPosition) {
        return true;
    }

    @Override
    public void moved(int selectedPosition, int targetPosition) {

    }
    @Override
    public int getCurrentState(View selectedView, View targetView, int x, int y,
                               VelocityTracker velocityTracker, int selectedPosition,
                               int targetPosition) {
        if(velocityTracker == null) return ClassifyView.MOVE_STATE_NONE;
        int left = x;
        int top = y;
        int right = left + selectedView.getWidth();
        int bottom = top + selectedView.getHeight();
        if((Math.abs(left - targetView.getLeft())+Math.abs(right - targetView.getRight())+
                Math.abs(top - targetView.getTop())+ Math.abs(bottom - targetView.getBottom()))
                <(targetView.getWidth()+targetView.getHeight()
        )/2){
            velocityTracker.computeCurrentVelocity(100);
            float xVelocity = velocityTracker.getXVelocity();
            float yVelocity = velocityTracker.getYVelocity();
            float limit = getVelocity(targetView.getContext());
            if(xVelocity < limit && yVelocity < limit){
                return ClassifyView.MOVE_STATE_MOVE;
            }
        }
        return ClassifyView.MOVE_STATE_NONE;
    }

    @Override
    public float getVelocity(Context context) {
        float density = context.getResources().getDisplayMetrics().density;
        return density*VELOCITY + .5f;
    }

    @Override
    public void onItemClick(RecyclerView recyclerView, int position, View pressedView) {
        onItemClick(position,pressedView);
    }

    @Override
    public void onItemClick(int position, View pressedView) {

    }
}
