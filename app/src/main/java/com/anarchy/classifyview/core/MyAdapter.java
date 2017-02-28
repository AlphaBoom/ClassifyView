package com.anarchy.classifyview.core;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.anarchy.classify.simple.SimpleAdapter;
import com.anarchy.classify.simple.widget.InsertAbleGridView;
import com.anarchy.classifyview.R;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * <p/>
 * Date: 16/6/7 16:40
 * Author: rsshinide38@163.com
 * <p/>
 */
public class MyAdapter extends SimpleAdapter<Bean, MyAdapter.ViewHolder> {


    public MyAdapter(List<List<Bean>> mData) {
        super(mData);
    }


    @Override
    protected ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sample_vertical, parent, false);
        return new MyAdapter.ViewHolder(view);
    }


    @Override
    public View getView(ViewGroup parent, View convertView, int mainPosition, int subPosition) {
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_inner, parent, false);
        }
        return convertView;
    }

    @Override
    protected void onItemClick(View view, int parentIndex, int index) {
        Toast.makeText(view.getContext(), "parentIndex: " + parentIndex + "\nindex: " + index, Toast.LENGTH_SHORT).show();
    }

    static class ViewHolder extends SimpleAdapter.ViewHolder {

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

}
