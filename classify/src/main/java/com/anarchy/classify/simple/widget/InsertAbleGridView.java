package com.anarchy.classify.simple.widget;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.widget.ScrollerCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;

import com.anarchy.classify.R;
import com.anarchy.classify.simple.ChangeInfo;
import com.anarchy.classify.simple.PrimitiveSimpleAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * 显示merge状态及以列表形式排列子view
 * 接收一个 view 的集合 或则 图片的集合
 */
public class InsertAbleGridView extends ViewGroup implements CanMergeView {
    private int mRowCount;
    private int mColumnCount;
    private int mRowGap;
    private int mColumnGap;
    private int mOutLinePadding;
    private int mInnerPadding;
    public static final int ISFOLDERMODEL = 1;
    public static final int NOTFOLDERMODEL = 0;
    private BagDrawable mBagDrawable;
    private PrimitiveSimpleAdapter mPrimitiveSimpleAdapter;
    private List<View> mRecycledViews;
    private int parentIndex;
    private ChangeInfo mReturnInfo = new ChangeInfo();
    private ScrollerCompat mScroller;

    public InsertAbleGridView(Context context) {
        this(context, null);
    }

    public InsertAbleGridView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public InsertAbleGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }


    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.InsertAbleGridView, defStyleAttr, R.style.InsertAbleGridViewDefaultStyle);
        mRowCount = a.getInt(R.styleable.InsertAbleGridView_RowCount, 2);
        mColumnCount = a.getInt(R.styleable.InsertAbleGridView_ColumnCount, 2);
        mRowGap = a.getDimensionPixelSize(R.styleable.InsertAbleGridView_RowGap, 10);
        mColumnGap = a.getDimensionPixelSize(R.styleable.InsertAbleGridView_ColumnGap, 10);
        mOutLinePadding = a.getDimensionPixelSize(R.styleable.InsertAbleGridView_OutlinePadding, 10);
        mInnerPadding = a.getDimensionPixelOffset(R.styleable.InsertAbleGridView_InnerPadding, 10);
        mBagDrawable = new BagDrawable(mOutLinePadding);
        mBagDrawable.setOutlineStyle(a.getColor(R.styleable.InsertAbleGridView_OutlineColor, 0), a.getDimensionPixelSize(R.styleable.InsertAbleGridView_OutlineWidth, 3));
        setBackgroundDrawable(mBagDrawable);
        a.recycle();
        mScroller = ScrollerCompat.create(context);
    }

    private void layoutFolder(int childCount, int itemWidth, int itemHeight, int itemTotal) {
        mBagDrawable.setKeepShow(true);
        int row, col;
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            row = i / mColumnCount;
            col = i % mColumnCount;
            if (row < mRowCount) {
                int left = getPaddingLeft() + mInnerPadding + mOutLinePadding + col * (itemWidth + mColumnGap);
                int right = left + itemWidth;
                int top = getPaddingTop() + mInnerPadding + mOutLinePadding + row * (itemHeight + mRowGap);
                int bottom = top + itemHeight;
                child.layout(left, top, right, bottom);
            } else if (i >= childCount - itemTotal && childCount % itemTotal != 0) {
                int newI = i % itemTotal;
                row = newI / mColumnCount;
                col = newI % mColumnCount;
                int left = getPaddingLeft() + mInnerPadding + mOutLinePadding + col * (itemWidth + mColumnGap);
                int right = left + itemWidth;
                int top = getHeight() + getPaddingTop() + mInnerPadding + mOutLinePadding + row * (itemHeight + mRowGap);
                int bottom = top + itemHeight;
                child.layout(left, top, right, bottom);
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childCount = getChildCount();
        int width = Math.max(r - l - getPaddingLeft() - getPaddingRight() - 2 * mOutLinePadding, 0);
        int height = Math.max(b - t - getPaddingBottom() - getPaddingTop() - 2 * mOutLinePadding, 0);
        int itemWidth = getItemWidth(width);
        int itemHeight = getItemHeight(height);
        int itemTotal = mRowCount * mColumnCount;
        if (childCount > 0) {
            if (childCount == 1) {
                final View child = getChildAt(0);
                MiViewHolder michaelViewHolder;
                try {
                    michaelViewHolder = (MiViewHolder) child.getTag();
                } catch (ClassCastException e) {
                    michaelViewHolder = null;
                }
                if (michaelViewHolder == null) {
                    mBagDrawable.setKeepShow(false);
                    getChildAt(0).layout(getPaddingLeft() + mOutLinePadding, getPaddingTop() + mOutLinePadding, getPaddingLeft() + mOutLinePadding + width, getPaddingTop() + mOutLinePadding + height);
                } else {
                    switch (michaelViewHolder.childTag) {
                        case NOTFOLDERMODEL:
                            mBagDrawable.setKeepShow(false);
                            getChildAt(0).layout(getPaddingLeft() + mOutLinePadding, getPaddingTop() + mOutLinePadding, getPaddingLeft() + mOutLinePadding + width, getPaddingTop() + mOutLinePadding + height);
                            break;
                        case ISFOLDERMODEL:
                            layoutFolder(childCount, itemWidth, itemHeight, itemTotal);
                            break;
                    }
                }
            } else {
                layoutFolder(childCount, itemWidth, itemHeight, itemTotal);
            }
        }
    }

    @Deprecated
    private ValueAnimator createConvertAnimator(final View view) {
        int width = getWidth() - getPaddingLeft() - getPaddingRight() - 2 * mOutLinePadding;
        int height = getHeight() - getPaddingBottom() - getPaddingTop() - 2 * mOutLinePadding;
        PropertyValuesHolder left = PropertyValuesHolder.ofInt("left", view.getLeft(), getPaddingLeft() + mInnerPadding + mOutLinePadding);
        PropertyValuesHolder right = PropertyValuesHolder.ofInt("right", view.getRight(), getPaddingLeft() + mInnerPadding + mOutLinePadding + getItemWidth(width));
        PropertyValuesHolder top = PropertyValuesHolder.ofInt("top", view.getTop(), getPaddingTop() + mInnerPadding + mOutLinePadding);
        PropertyValuesHolder bottom = PropertyValuesHolder.ofInt("bottom", view.getBottom(), getPaddingTop() + mInnerPadding + mOutLinePadding + getItemHeight(height));
        ValueAnimator animator = ObjectAnimator.ofPropertyValuesHolder(left, right, top, bottom);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int left = (int) animation.getAnimatedValue("left");
                int right = (int) animation.getAnimatedValue("right");
                int top = (int) animation.getAnimatedValue("top");
                int bottom = (int) animation.getAnimatedValue("bottom");
                view.layout(left, top, right, bottom);
            }
        });
        animator.setInterpolator(new DecelerateInterpolator());
        return animator;
    }

    private Animator createTransformAnimator(final View view) {
        view.setPivotX(mInnerPadding);
        view.setPivotY(mInnerPadding);
        float width = view.getWidth();
        float height = view.getHeight();
        float scaleX = getItemWidth((int) width) / width;
        float scaleY = getItemHeight((int) height) / height;
        PropertyValuesHolder scaleXPvh = PropertyValuesHolder.ofFloat("scaleX", scaleX);
        PropertyValuesHolder scaleYPvh = PropertyValuesHolder.ofFloat("scaleY", scaleY);
        ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(view, scaleXPvh, scaleYPvh);
        animator.setInterpolator(new DecelerateInterpolator());
        return animator;
    }

    private int getItemWidth(int width) {
        return (width - 2 * mInnerPadding - (mColumnCount - 1) * mRowGap) / mColumnCount;
    }

    private int getItemHeight(int height) {
        return (height - 2 * mInnerPadding - (mRowCount - 1) * mColumnGap) / mRowCount;
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
        int childCount = getChildCount();
        int width = Math.max(widthSize - getPaddingLeft() - getPaddingRight() - 2 * mOutLinePadding, 0);
        int height = Math.max(heightSize - getPaddingBottom() - getPaddingTop() - 2 * mOutLinePadding, 0);
        int itemWidth = getItemWidth(width);
        int itemHeight = getItemHeight(height);
        if (childCount > 0) {
            if (childCount == 1) {
                final View child = getChildAt(0);
                MiViewHolder michaelViewHolder = (MiViewHolder) child.getTag();
                int widthSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
                int heightSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
                if (michaelViewHolder == null) {
                    getChildAt(0).measure(widthSpec, heightSpec);
                } else {
                    switch (michaelViewHolder.childTag) {// 若包含其他类型TAG ，建议使用if else
                        case NOTFOLDERMODEL:
                            getChildAt(0).measure(widthSpec, heightSpec);
                            break;
                        case ISFOLDERMODEL:
                            for (int i = 0; i < getChildCount(); i++) {
                                getChildAt(i).measure(widthSpec, heightSpec);
                            }
                            break;
                    }
                }
            } else {
                int widthSpec = MeasureSpec.makeMeasureSpec(itemWidth, MeasureSpec.EXACTLY);
                int heightSpec = MeasureSpec.makeMeasureSpec(itemHeight, MeasureSpec.EXACTLY);
                for (int i = 0; i < getChildCount(); i++) {
                    getChildAt(i).measure(widthSpec, heightSpec);
                }
            }
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public void onMergeStart() {
        mBagDrawable.startMergeAnimation();
        if (getChildCount() >= mRowCount * mColumnCount) {
            mScroller.startScroll(0, 0, 0, getHeight(), 500);
            invalidate();
        }
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            invalidate();
        }
    }

    @Override
    public void onMergeCancel() {
        mBagDrawable.cancelMergeAnimation();
        if (getChildCount() >= mRowCount * mColumnCount) {
            mScroller.startScroll(0, getHeight(), 0, -getHeight(), 500);
        }
    }

    @Override
    public void onMerged() {
        mBagDrawable.setKeepShow(true);
        mBagDrawable.cancelMergeAnimation();
        if (getChildCount() >= mRowCount * mColumnCount) {
            mScroller.startScroll(0, getHeight(), 0, -getHeight(), 500);
        }
    }

    @Override
    public void startMergeAnimation(int duration) {
        if (getChildCount() == 1) {
            View child = getChildAt(0);
            createTransformAnimator(child).setDuration(duration).start();
        }
    }

    @Override
    public ChangeInfo prepareMerge() {
        int futureCount = getChildCount() + 1;
        if (futureCount > 1) {
            if (futureCount > mRowCount * mColumnCount)
                futureCount = futureCount % (mRowCount * mColumnCount);
            if (futureCount == 0) futureCount = mRowCount * mColumnCount;
            futureCount--;
            int row = futureCount / mColumnCount;
            int col = futureCount % mColumnCount;
            int width = getWidth() - getPaddingLeft() - getPaddingRight() - 2 * mOutLinePadding;
            int height = getHeight() - getPaddingTop() - getPaddingBottom() - 2 * mOutLinePadding;
            int itemWidth = getItemWidth(width);
            int itemHeight = getItemHeight(height);
            int left = getPaddingLeft() + mInnerPadding + mOutLinePadding + col * (itemWidth + mColumnGap);
            int top = getPaddingTop() + mInnerPadding + mOutLinePadding + row * (itemHeight + mRowGap);
            mReturnInfo.targetLeft = getLeft() + left;
            mReturnInfo.targetTop = getTop() + top;
            mReturnInfo.targetWidth = itemWidth;
            mReturnInfo.targetHeight = itemHeight;
            mReturnInfo.sourceLeft = getLeft() + getPaddingLeft() + mOutLinePadding;
            mReturnInfo.sourceTop = getTop() +getPaddingTop() + mOutLinePadding;
            mReturnInfo.sourceWidth = width;
            mReturnInfo.sourceHeight = height;
            return mReturnInfo;
        }
        return null;
    }

    @Override
    public void setAdapter(PrimitiveSimpleAdapter primitiveSimpleAdapter) {
        mPrimitiveSimpleAdapter = primitiveSimpleAdapter;
    }


    @Override
    public void initOrUpdateMain(int parentIndex, int requestCount) {
        this.parentIndex = parentIndex;
        int childCount = getChildCount();
        initOrUpdateMainInternal(parentIndex, requestCount, childCount);
    }

    @Override
    public void initOrUpdateSub(int parentIndex, int subIndex) {
        this.parentIndex = parentIndex;
        int childCount = getChildCount();
        initOrUpdateSubInternal(parentIndex, 1, childCount, subIndex);
    }

    private void initOrUpdateMainInternal(int parentIndex, int requestCount, int childCount) {
        if (requestCount < childCount) {
            for (int i = childCount - 1; i > requestCount - 1; i--) {
                detachAnRecycleView(i);
            }
        }
        int onePageShowCount = mRowCount * mColumnCount;
        if (requestCount < onePageShowCount * 2) {
            for (int i = 0; i < requestCount; i++) {
                attachOrAddView(parentIndex, i, i);
            }
        } else {
            for (int i = 0; i < onePageShowCount; i++) {
                attachOrAddView(parentIndex, i, i);
            }
            for (int i = requestCount - onePageShowCount - 1; i < requestCount; i++) {
                attachOrAddView(parentIndex, i, i - requestCount + 2 * onePageShowCount);
            }
        }
        invalidate();
        requestLayout();
    }


    private void initOrUpdateSubInternal(int parentIndex, int requestCount, int childCount, int subIndex) {
        if (requestCount < childCount) {
            for (int i = childCount - 1; i > requestCount - 1; i--) {
                detachAnRecycleView(i);
            }
        }
        attachOrAddView(parentIndex, subIndex, 0);
        invalidate();
        requestLayout();
    }

    private void detachAnRecycleView(int index) {
        if (mRecycledViews == null) mRecycledViews = new ArrayList<>();
        mRecycledViews.add(getChildAt(index));
        detachViewFromParent(index);
    }

    private void attachAndReusedView(View child, int layoutPosition) {
        resetScale(child);
        mRecycledViews.remove(child);
        attachViewToParent(child, layoutPosition, generateDefaultLayoutParams());
    }

    private void attachOrAddView(int parentIndex, int subIndex, int layoutPosition) {
        if (layoutPosition > getChildCount() - 1) {
            //需要创建 或者从 recycled 中获取一个View
            View convertView = null;
            if (mRecycledViews != null && mRecycledViews.size() > 0) {
                convertView = mRecycledViews.get(mRecycledViews.size() - 1);
            }
            View item = null;
            if (mPrimitiveSimpleAdapter != null) {
                item = mPrimitiveSimpleAdapter.getView(this, convertView, parentIndex, subIndex);
            }
            if (item == null) {
                return;
            }
            if (convertView != null && item == convertView) {
                attachAndReusedView(item, layoutPosition);
            } else {
                addViewInLayout(item, layoutPosition, generateDefaultLayoutParams());
            }
        } else {
            View convertView = getChildAt(layoutPosition);
            resetScale(convertView);
            View item = null;
            if (mPrimitiveSimpleAdapter != null) {
                item = mPrimitiveSimpleAdapter.getView(this, convertView, parentIndex, subIndex);
            }
            if (item == null) {
                return;
            }
            if (item != convertView) {//返回了一个新的View
                Log.w("InsertAbleGridView", "should reuse cached view");
                removeViewInLayout(convertView);
                addViewInLayout(item, layoutPosition, generateDefaultLayoutParams());
            }
        }
    }


    static void resetScale(View view) {
        view.setScaleX(1f);
        view.setScaleY(1f);
    }
}
