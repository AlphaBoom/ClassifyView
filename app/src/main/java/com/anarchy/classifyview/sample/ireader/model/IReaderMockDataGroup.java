package com.anarchy.classifyview.sample.ireader.model;

import java.util.List;

/**
 * User:  Anarchy
 * Email:  rsshinide38@163.com
 * CreateTime: 一月/02/2017  22:50.
 * Description:
 */
public class IReaderMockDataGroup extends IReaderMockData{
    private List<IReaderMockData> mChild;
    private String mCategory;

    public List<IReaderMockData> getChild() {
        return mChild;
    }

    public void setChild(List<IReaderMockData> child) {
        mChild = child;
    }

    public String getCategory() {
        return mCategory;
    }

    public void setCategory(String category) {
        mCategory = category;
    }
}
