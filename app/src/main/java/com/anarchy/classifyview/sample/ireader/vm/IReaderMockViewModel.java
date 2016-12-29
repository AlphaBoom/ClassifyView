package com.anarchy.classifyview.sample.ireader.vm;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import java.util.List;

/**
 * Version 2.1.1
 * <p>
 * Date: 16/12/29 10:54
 * Author: zhendong.wu@shoufuyou.com
 * <p/>
 * Copyright © 2016 Shanghai Xiaotu Network Technology Co., Ltd.
 */

public class IReaderMockViewModel extends BaseObservable{
    public static final int CHECKED = 1<<29;
    public static final int UNCHECKED = 1<<30;
    private boolean isFolder;
    private List<IReaderMockViewModel> mViewModels;
    private String mCategory;
    private int mCheckState;

    @Bindable
    public boolean isFolder() {
        return isFolder;
    }

    public void setFolder(boolean folder) {
        isFolder = folder;
    }

    public List<IReaderMockViewModel> getViewModels() {
        return mViewModels;
    }

    public void setViewModels(List<IReaderMockViewModel> viewModels) {
        mViewModels = viewModels;
    }
    @Bindable
    public String getCategory() {
        return mCategory;
    }

    public void setCategory(String category) {
        mCategory = category;
    }
    @Bindable
    public int getCheckState() {
        return mCheckState;
    }

    public void setCheckState(int checkState) {
        mCheckState = checkState;
    }
}
