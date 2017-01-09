package com.anarchy.classifyview.sample.normalfolder.logic;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.anarchy.classify.simple.widget.MiViewHolder;
import com.anarchy.classifyview.sample.normalfolder.Constants;
import com.anarchy.classifyview.sample.normalfolder.bean.BaseBean;
import com.anarchy.classifyview.R;

import java.util.List;

/**
 * Created by lizhiming211223 on 2016/12/29.
 */
public class MyFolderAdapter extends FolderAdapter<MyFolderAdapter.ViewHolder> {


    public MyFolderAdapter(List<BaseBean> mData) {
        super(mData);
    }


    @Override
    protected ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        super.onCreateViewHolder(parent,viewType);
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sample_vertical, parent, false);
        return new MyFolderAdapter.ViewHolder(view);
    }
    @Override
    public View getView(ViewGroup parent, View convertView, int mainPosition, int subPosition) {
        Log.i(Constants.CLASSIFY_VIEW_INIT,"CLASSIFY_VIEW_INIT");
        BaseBean baseBean=mData.get(mainPosition);
        MiViewHolder michaelViewHolder;
        if (convertView == null) {
            michaelViewHolder = new MiViewHolder();
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_inner, parent, false);
            if(baseBean.getBookList().size()==1&&!baseBean.isGroup){
                michaelViewHolder.childTag=0;
            }else{
                michaelViewHolder.childTag=1;//绘制 Folder
            }
            convertView.setTag(michaelViewHolder);
        }else {
            michaelViewHolder = (MiViewHolder) convertView.getTag();
        }
        return convertView;
    }

    @Override
    protected void onItemClick(View view, int parentIndex, int index) {
        Toast.makeText(view.getContext(), "parentIndex: " + parentIndex + "\nindex: " + index, Toast.LENGTH_SHORT).show();
    }

    static class ViewHolder extends FolderAdapter.ViewHolder {

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }


}
