package com.anarchy.classify.adapter;

/**
 * <p/>
 * Date: 16/6/6 17:17
 * Author: zhendong.wu@shoufuyou.com
 * <p/>
 */
public class SubAdapterReference<T extends SubRecyclerViewCallBack> {
    private final SubRecyclerViewCallBack mSubRecyclerViewCallBack;

    public SubAdapterReference(SubRecyclerViewCallBack subRecyclerViewCallBack) {
        mSubRecyclerViewCallBack = subRecyclerViewCallBack;
    }

    public T getAdapter() {
        return (T) mSubRecyclerViewCallBack;
    }
}
