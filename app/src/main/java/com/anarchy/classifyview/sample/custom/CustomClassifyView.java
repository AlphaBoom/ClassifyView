package com.anarchy.classifyview.sample.custom;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.anarchy.classify.ClassifyItemAnimator;
import com.anarchy.classify.ClassifyView;

/**
 * <p/>
 * Date: 16/6/13 11:58
 * Author: zhendong.wu@shoufuyou.com
 * <p/>
 */
public class CustomClassifyView extends ClassifyView {
    public CustomClassifyView(Context context) {
        super(context);
    }

    public CustomClassifyView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomClassifyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @NonNull
    @Override
    protected RecyclerView getMain(Context context, AttributeSet parentAttrs) {
        return super.getMain(context, parentAttrs);
    }

    /**
     * 设置次级目录的RecyclerView 的布局为 竖直排列
     * @param context
     * @param parentAttrs
     * @return
     */
    @NonNull
    @Override
    protected RecyclerView getSub(Context context, AttributeSet parentAttrs) {
        RecyclerView recyclerView = new RecyclerView(context);
        recyclerView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setItemAnimator(new ClassifyItemAnimator());
        return recyclerView;
    }


    @Override
    protected Drawable getDragDrawable(View view) {
        return new MyDragDrawable(view);
    }
}
