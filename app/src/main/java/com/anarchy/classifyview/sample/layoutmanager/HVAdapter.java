package com.anarchy.classifyview.sample.layoutmanager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.anarchy.classify.simple.SimpleAdapter;
import com.anarchy.classify.simple.widget.InsertAbleGridView;
import com.anarchy.classifyview.R;
import com.anarchy.classifyview.core.Bean;

import java.util.List;

/**
 * Date: 16/12/26 12:00
 * Author: rsshinide38@163.com
 */

public class HVAdapter extends SimpleAdapter<Bean,HVAdapter.ViewHolder>{


    public HVAdapter(List<List<Bean>> data) {
        super(data);
    }


    @Override
    protected ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if(getSpecialType(viewType) == TYPE_MAIN){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sample_horizontal,parent,false);
        }else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sample_vertical,parent,false);
        }
        return new HVAdapter.ViewHolder(view);
    }

    @Override
    protected boolean haveSpecialType() {
        return true;
    }

    /**
     * 用于显示{@link InsertAbleGridView} 的item布局
     *
     * @param parent       父View
     * @param convertView  缓存的View 可能为null
     * @param mainPosition 主层级位置
     * @param subPosition  副层级位置
     * @return
     */
    @Override
    public View getView(ViewGroup parent, View convertView, int mainPosition, int subPosition) {
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_inner, parent, false);
        }
        return convertView;
    }

    static class ViewHolder extends SimpleAdapter.ViewHolder{

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
