package com.anarchy.classify.simple.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.anarchy.classify.R;
import com.anarchy.classify.simple.SimpleAdapter;
import com.anarchy.classify.util.L;

import java.util.List;

/**
 * 显示merge状态及以列表形式排列子view
 * 接收一个 view 的集合 或则 图片的集合
 */
public class InsertAbleGridView extends ViewGroup implements CanMergeView{
    private int mRowCount;
    private int mColumnCount;
    private int mRowGap;
    private int mColumnGap;
    private int mOutLinePadding;
    private int mInnerPadding;
    private BagDrawable mBagDrawable;
    private SimpleAdapter mSimpleAdapter;
    private List mList;
    private int parentIndex;
    public InsertAbleGridView(Context context) {
        this(context,null);
    }

    public InsertAbleGridView(Context context, AttributeSet attrs) {
        this(context,attrs,0);
    }

    public InsertAbleGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs,defStyleAttr);
    }


    private void init(Context context,AttributeSet attrs,int defStyleAttr){
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.InsertAbleGridView,defStyleAttr,R.style.InsertAbleGridViewDefaultStyle);
        mRowCount = a.getInt(R.styleable.InsertAbleGridView_RowCount,2);
        mColumnCount = a.getInt(R.styleable.InsertAbleGridView_ColumnCount,2);
        mRowGap = a.getDimensionPixelSize(R.styleable.InsertAbleGridView_RowGap,10);
        mColumnGap = a.getDimensionPixelSize(R.styleable.InsertAbleGridView_ColumnGap,10);
        mOutLinePadding = a.getDimensionPixelSize(R.styleable.InsertAbleGridView_OutlinePadding,10);
        mInnerPadding = a.getDimensionPixelOffset(R.styleable.InsertAbleGridView_InnerPadding,10);
        mBagDrawable = new BagDrawable(mOutLinePadding);
        mBagDrawable.setOutlineStyle(a.getColor(R.styleable.InsertAbleGridView_OutlineColor,0),a.getDimensionPixelSize(R.styleable.InsertAbleGridView_OutlineWidth,3));
        setBackgroundDrawable(mBagDrawable);
        a.recycle();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childCount = getChildCount();
        int width = Math.max(r - l - getPaddingLeft()-getPaddingRight(),0);
        int height = Math.max(b - t - getPaddingBottom()-getPaddingTop(),0);
        int itemWidth = (width-2*mInnerPadding - (mColumnCount-1)*mRowGap)/mColumnCount;
        int itemHeight = (height-2*mInnerPadding -(mRowCount-1)*mColumnGap)/mRowCount;
        if(childCount>0){
            if(childCount == 1){
                mBagDrawable.setKeepShow(false);
                getChildAt(0).layout(getPaddingLeft(),getPaddingTop(),getPaddingLeft()+width,getPaddingTop()+height);
            }else {
                mBagDrawable.setKeepShow(true);
                int row,col;
                for(int i=0;i<childCount;i++){
                    View child = getChildAt(i);
                    row = i/mColumnCount;
                    col = i%mColumnCount;
                    if(row >= mRowCount) break;
                    int left = getPaddingLeft()+mInnerPadding+col*(itemWidth+mColumnGap);
                    int right = left + itemWidth;
                    int top = getPaddingTop()+mInnerPadding+row*(itemHeight + mRowGap);
                    int bottom = top+itemHeight;
                    child.layout(left,top,right,bottom);
                }
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        if (widthMode != MeasureSpec.EXACTLY)
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(widthSize, MeasureSpec.EXACTLY);
        if (heightMode != MeasureSpec.EXACTLY)
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(heightSize, MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public void onMergeStart() {
        L.d("merge start:"+parentIndex);
        mBagDrawable.startMergeAnimation();
    }

    @Override
    public void onMergeCancel() {
        L.d("merge cancel:"+parentIndex);
        mBagDrawable.cancelMergeAnimation();
    }

    @Override
    public void onMerge() {
        mBagDrawable.setKeepShow(true);
        mBagDrawable.cancelMergeAnimation();
    }

    @Override
    public void setAdapter(SimpleAdapter simpleAdapter) {
        mSimpleAdapter = simpleAdapter;
    }

    @Override
    public void init(int parentIndex, List list) {
        removeAllViewsInLayout();
        this.parentIndex = parentIndex;
        mList = list;
        for(int i =0;i<list.size();i++){
            if(mSimpleAdapter!=null){
                View child = mSimpleAdapter.getView(this,parentIndex,i);
                addViewInLayout(child,i,generateDefaultLayoutParams());
            }
        }
        invalidate();
        requestLayout();
    }


}
