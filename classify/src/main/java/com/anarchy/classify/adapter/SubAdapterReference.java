package com.anarchy.classify.adapter;

/**
 * Version 1.0
 * <p/>
 * Date: 16/6/6 17:17
 * Author: zhendong.wu@shoufuyou.com
 * <p/>
 * Copyright © 2014-2016 Shanghai Xiaotu Network Technology Co., Ltd.
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
