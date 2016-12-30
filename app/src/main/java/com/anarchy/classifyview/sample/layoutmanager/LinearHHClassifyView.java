package com.anarchy.classifyview.sample.layoutmanager;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.ViewGroup;

import com.anarchy.classify.ClassifyItemAnimator;
import com.anarchy.classify.ClassifyView;

/**
 * User:  Anarchy
 * Email:  rsshinide38@163.com
 * CreateTime: 十二月/25/2016  12:03.
 * Description:
 */

public class LinearHHClassifyView extends ClassifyView {
    public LinearHHClassifyView(Context context) {
        super(context);
    }

    public LinearHHClassifyView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LinearHHClassifyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public LinearHHClassifyView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @NonNull
    @Override
    protected RecyclerView getMain(Context context, AttributeSet parentAttrs) {
        RecyclerView recyclerView = new RecyclerView(context);
        recyclerView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        recyclerView.setLayoutManager(new LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false));
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setChangeDuration(10);
        recyclerView.setItemAnimator(itemAnimator);
        return recyclerView;
    }

    @NonNull
    @Override
    protected RecyclerView getSub(Context context, AttributeSet parentAttrs) {
        RecyclerView recyclerView = new RecyclerView(context);
        recyclerView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        recyclerView.setLayoutManager(new LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false));
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setChangeDuration(10);
        recyclerView.setItemAnimator(itemAnimator);
        return recyclerView;
    }
}
