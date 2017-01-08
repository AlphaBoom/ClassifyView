package com.anarchy.classifyview.sample.ireader.model;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * User:  Anarchy
 * Email:  rsshinide38@163.com
 * CreateTime: 一月/02/2017  22:50.
 * Description:
 */
public class IReaderMockDataGroup extends IReaderMockData{
    private List<IReaderMockData> mChild = new ArrayList<>();
    private String mCategory;

    public void addChild(@NonNull IReaderMockData iReaderMockData){
        iReaderMockData.setParent(this);
        mChild.add(iReaderMockData);
    }

    public void addChild(int location,@NonNull IReaderMockData iReaderMockData){
        iReaderMockData.setParent(this);
        mChild.add(location,iReaderMockData);
    }

    public IReaderMockData removeChild(int location){
        IReaderMockData mockData = mChild.remove(location);
        mockData.setParent(null);
        return mockData;
    }

    public boolean removeChild(@NonNull IReaderMockData iReaderMockData){
        iReaderMockData.setParent(null);
        return mChild.remove(iReaderMockData);
    }


    public int getChildCount(){
        return mChild.size();
    }


    public IReaderMockData getChild(int position){
        return mChild.get(position);
    }


    public String getCategory() {
        return mCategory;
    }

    public void setCategory(String category) {
        mCategory = category;
    }

    public int getCheckedCount(){
        if(mChild != null){
            int i = 0;
            for(IReaderMockData data:mChild){
                if(data.isChecked()){
                    i++;
                }
            }
            return i;
        }
        return 0;
    }
}
