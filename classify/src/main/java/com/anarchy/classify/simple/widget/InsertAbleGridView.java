package com.anarchy.classify.simple.widget;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Point;
import android.support.v4.widget.ScrollerCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;

import com.anarchy.classify.ChangeInfo;
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
    private ChangeInfo mReturnInfo = new ChangeInfo();
    private ScrollerCompat mScroller;
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
        mScroller = ScrollerCompat.create(context);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childCount = getChildCount();
        int width = Math.max(r - l - getPaddingLeft()-getPaddingRight(),0);
        int height = Math.max(b - t - getPaddingBottom()-getPaddingTop(),0);
        int itemWidth = getItemWidth(width);
        int itemHeight = getItemHeight(height);
        int itemTotal = mRowCount*mColumnCount;
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
                    if(row < mRowCount){
                        int left = getPaddingLeft()+mInnerPadding+col*(itemWidth+mColumnGap);
                        int right = left + itemWidth;
                        int top = getPaddingTop()+mInnerPadding+row*(itemHeight + mRowGap);
                        int bottom = top+itemHeight;
                        child.layout(left,top,right,bottom);
                    }else if(i>=childCount-itemTotal && childCount%itemTotal != 0){
                        int newI = i%itemTotal;
                        row = newI/mColumnCount;
                        col = newI%mColumnCount;
                        int left = getPaddingLeft()+mInnerPadding+col*(itemWidth+mColumnGap);
                        int right = left + itemWidth;
                        int top = getHeight()+getPaddingTop()+mInnerPadding+row*(itemHeight + mRowGap);
                        int bottom = top+itemHeight;
                        child.layout(left,top,right,bottom);
                    }

                }
            }
        }
    }
    private ValueAnimator createConvertAnimator(final View view){
        int width = getWidth() - getPaddingLeft()-getPaddingRight();
        int height = getHeight() - getPaddingBottom() - getPaddingTop();
        PropertyValuesHolder left = PropertyValuesHolder.ofInt("left",view.getLeft(),getPaddingLeft()+mInnerPadding);
        PropertyValuesHolder right = PropertyValuesHolder.ofInt("right",view.getRight(),getPaddingLeft()+mInnerPadding+getItemWidth(width));
        PropertyValuesHolder top = PropertyValuesHolder.ofInt("top",view.getTop(),getPaddingTop()+mInnerPadding);
        PropertyValuesHolder bottom = PropertyValuesHolder.ofInt("bottom",view.getBottom(),getPaddingTop()+mInnerPadding+getItemHeight(height));
        ValueAnimator animator = ObjectAnimator.ofPropertyValuesHolder(left,right,top,bottom);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int left = (int) animation.getAnimatedValue("left");
                int right = (int) animation.getAnimatedValue("right");
                int top = (int) animation.getAnimatedValue("top");
                int bottom = (int) animation.getAnimatedValue("bottom");
                view.layout(left,top,right,bottom);
                L.d("do animation left:%1$d right:%2$d top:%3$d bottom:%4$d",left,right,top,bottom);
            }
        });
        animator.setInterpolator(new DecelerateInterpolator());
        return animator;
    }

    private int getItemWidth(int width){
        return (width-2*mInnerPadding - (mColumnCount-1)*mRowGap)/mColumnCount;
    }
    private int getItemHeight(int height){
        return (height-2*mInnerPadding -(mRowCount-1)*mColumnGap)/mRowCount;
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
        if(getChildCount() >= mRowCount*mColumnCount){
            mScroller.startScroll(0,0,0,getHeight(),500);
            invalidate();
        }
    }

    @Override
    public void computeScroll() {
        if(mScroller.computeScrollOffset()){
            scrollTo(mScroller.getCurrX(),mScroller.getCurrY());
            invalidate();
        }
    }

    @Override
    public void onMergeCancel() {
        L.d("merge cancel:"+parentIndex);
        mBagDrawable.cancelMergeAnimation();
        if(getChildCount() >= mRowCount*mColumnCount){
            mScroller.startScroll(0,getHeight(),0,-getHeight(),500);
        }
    }

    @Override
    public void onMerged() {
        mBagDrawable.setKeepShow(true);
        mBagDrawable.cancelMergeAnimation();
        if(getChildCount() >= mRowCount*mColumnCount){
            mScroller.startScroll(0,getHeight(),0,-getHeight(),500);
        }
    }

    @Override
    public void startMergeAnimation(int duration) {
        if(getChildCount() == 1){
            View child = getChildAt(0);
            createConvertAnimator(child).setDuration(duration).start();
        }
    }

    @Override
    public ChangeInfo prepareMerge() {
        int futureCount = getChildCount() + 1;
        if(futureCount > 1){
            if(futureCount > mRowCount*mColumnCount) futureCount = futureCount%(mRowCount*mColumnCount+1);
            futureCount--;
            int row = futureCount/mColumnCount;
            int col = futureCount%mColumnCount;
            int width = getWidth() - getPaddingLeft()-getPaddingRight();
            int height = getHeight() - getPaddingTop() - getPaddingBottom();
            int itemWidth = getItemWidth(width);
            int itemHeight = getItemHeight(height);
            int left = getPaddingLeft()+mInnerPadding+col*(itemWidth+mColumnGap);
            int top = getPaddingTop()+mInnerPadding+row*(itemHeight + mRowGap);
            mReturnInfo.left = left;
            mReturnInfo.top = top;
            mReturnInfo.itemWidth = itemWidth;
            mReturnInfo.itemHeight = itemHeight;
            return mReturnInfo;
        }
        return null;
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

    @Override
    public int getOutlinePadding() {
        return mOutLinePadding;
    }


}
