package com.anarchy.classify;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.annotation.TargetApi;
import android.content.ClipData;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.DragEvent;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;

import com.anarchy.classify.adapter.BaseMainAdapter;
import com.anarchy.classify.adapter.BaseSubAdapter;
import com.anarchy.classify.adapter.MainRecyclerViewCallBack;
import com.anarchy.classify.adapter.SubAdapterReference;
import com.anarchy.classify.adapter.SubRecyclerViewCallBack;
import com.anarchy.classify.simple.BaseSimpleAdapter;
import com.anarchy.classify.util.L;

import java.util.ArrayList;
import java.util.List;

/**
 * Version 1.0
 * <p>
 * Date: 16/6/1 14:16
 * Author: zhendong.wu@shoufuyou.com
 * <p>
 * Copyright © 2014-2016 Shanghai Xiaotu Network Technology Co., Ltd.
 */
public class ClassifyView extends FrameLayout {
    /**
     * 不做处理的状态
     */
    public static final int STATE_NONE = 0;
    /**
     * 当前状态为 可移动
     */
    public static final int STATE_MOVE = 1;
    /**
     * 当前状态为 可合并
     */
    public static final int STATE_MERGE = 2;


    private static final int ACTIVE_POINTER_ID_NONE = -1;
    private static final String DESCRIPTION = "Long press";
    private static final String MAIN = "main";
    private static final String SUB = "sub";


    /**
     * 放置主要RecyclerView的容器
     */
    private ViewGroup mMainContainer;
    /**
     * 放置次级RecyclerView的容器
     */
    private ViewGroup mSubContainer;
    /**
     * 被拖动的View
     */
    private View mDragView;


    private View mMainShadowView;
    private RecyclerView mMainRecyclerView;
    private RecyclerView mSubRecyclerView;

    private int mMainSpanCount;
    private int mSubSpanCount;
    private GestureDetectorCompat mMainGestureDetector;
    private GestureDetectorCompat mSubGestureDetector;

    private RecyclerView.OnItemTouchListener mMainItemTouchListener;
    private RecyclerView.OnItemTouchListener mSubItemTouchListener;

    private MainRecyclerViewCallBack mMainCallBack;
    private SubRecyclerViewCallBack mSubCallBack;

    private float mSubRatio;
    private int mMainActivePointerId = ACTIVE_POINTER_ID_NONE;
    private int mSubActivePointerId = ACTIVE_POINTER_ID_NONE;
    private int mShadowColor;
    private int mAnimationDuration;


    private int mSelectedStartX;
    private int mSelectedStartY;
    private float mInitialTouchX;
    private float mInitialTouchY;
    private float mDx;
    private float mDy;
    private View mSelected;
    private int mSelectedPosition;
    /**
     * 触发滑动距离
     */
    private int mEdgeWidth;

    private boolean inMainRegion;
    private boolean inSubRegion;

    private VelocityTracker mVelocityTracker;

    public ClassifyView(Context context) {
        super(context);
        init(context, null, 0);
    }

    public ClassifyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public ClassifyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ClassifyView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr);
    }

    /**
     * 初始化容器
     */
    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        mMainContainer = new FrameLayout(context);
        mSubContainer = new FrameLayout(context);
        mMainContainer.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ClassifyView, defStyleAttr, R.style.DefaultStyle);
        mSubRatio = a.getFraction(R.styleable.ClassifyView_SubRatio, 1, 1, 0.7f);
        mMainSpanCount = a.getInt(R.styleable.ClassifyView_MainSpanCount, 3);
        mSubSpanCount = a.getInt(R.styleable.ClassifyView_SubSpanCount, 3);
        mShadowColor = a.getColor(R.styleable.ClassifyView_ShadowColor, 0x83585858);
        mAnimationDuration = a.getInt(R.styleable.ClassifyView_AnimationDuration, 200);
        mEdgeWidth = a.getDimensionPixelSize(R.styleable.ClassifyView_EdgeWidth, 15);
        a.recycle();
        mMainRecyclerView = getMain(context, attrs);
        mSubRecyclerView = getSub(context, attrs);
        mMainContainer.addView(mMainRecyclerView);
        mMainShadowView = new View(context);
        mMainShadowView.setBackgroundColor(mShadowColor);
        mMainShadowView.setVisibility(View.GONE);
        mMainShadowView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mHideSubAnim != null && mHideSubAnim.isRunning()) return;
                hideSubContainer();
            }
        });
        mMainShadowView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        mMainContainer.addView(mMainShadowView);
        mSubContainer.addView(mSubRecyclerView);
        mSubContainer.setBackgroundColor(Color.CYAN);
        addViewInLayout(mMainContainer, 0, mMainContainer.getLayoutParams());
        mDragView = new View(context);
        mDragView.setVisibility(GONE);
        addViewInLayout(mDragView, -1, generateDefaultLayoutParams());
        setUpTouchListener(context);
    }

    protected
    @NonNull
    RecyclerView getMain(Context context, AttributeSet parentAttrs) {
        RecyclerView recyclerView = new RecyclerView(context);
        recyclerView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        recyclerView.setLayoutManager(new GridLayoutManager(context, mMainSpanCount));
        recyclerView.setItemAnimator(new ClassifyItemAnimator());
        return recyclerView;
    }


    protected
    @NonNull
    RecyclerView getSub(Context context, AttributeSet parentAttrs) {
        RecyclerView recyclerView = new RecyclerView(context);
        recyclerView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        recyclerView.setLayoutManager(new GridLayoutManager(context, mSubSpanCount));
        recyclerView.setItemAnimator(new ClassifyItemAnimator());
        return recyclerView;
    }


    public RecyclerView getMainRecyclerView() {
        return mMainRecyclerView;
    }

    public RecyclerView getSubRecyclerView() {
        return mSubRecyclerView;
    }


    private View findChildView(RecyclerView recyclerView, MotionEvent event) {
        // first check elevated views, if none, then call RV
        final float x = event.getX();
        final float y = event.getY();
        return recyclerView.findChildViewUnder(x, y);
    }

    /**
     * 设置adapter
     *
     * @param mainAdapter
     * @param subAdapter
     */
    public void setAdapter(BaseMainAdapter mainAdapter, BaseSubAdapter subAdapter) {
        mMainRecyclerView.setAdapter(mainAdapter);
        mMainRecyclerView.addOnItemTouchListener(mMainItemTouchListener);
        mMainCallBack = mainAdapter;
        mSubRecyclerView.setAdapter(subAdapter);
        mSubRecyclerView.addOnItemTouchListener(mSubItemTouchListener);
        mSubCallBack = subAdapter;
        mMainRecyclerView.setOnDragListener(new MainDragListener());
        mSubRecyclerView.setOnDragListener(new SubDragListener());
    }

    /**
     * @param baseSimpleAdapter
     */
    public void setAdapter(BaseSimpleAdapter baseSimpleAdapter) {
        setAdapter(baseSimpleAdapter.getMainAdapter(), baseSimpleAdapter.getSubAdapter());
    }

    public RecyclerView.LayoutManager getMainLayoutManager() {
        return mMainRecyclerView.getLayoutManager();
    }

    public RecyclerView.LayoutManager getSubLayoutManager() {
        return mSubRecyclerView.getLayoutManager();
    }

    /**
     * 初始化 触摸事件监听
     *
     * @param context
     */
    private void setUpTouchListener(Context context) {
        mMainGestureDetector = new GestureDetectorCompat(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                View pressedView = findChildView(mMainRecyclerView, e);
                if (pressedView == null) return false;
                int position = mMainRecyclerView.getChildAdapterPosition(pressedView);
                List list = mMainCallBack.explodeItem(position, pressedView);
                if (list == null || list.size() < 2) {
                    mMainCallBack.onItemClick(position, pressedView);
                    return true;
                } else {
                    mSubCallBack.initData(position, list);
                    if (ViewCompat.isAttachedToWindow(mSubContainer)) {
                        //取消之前进行的动画
                        if (mShowSubAnim != null && mShowSubAnim.isRunning()) {
                            mShowSubAnim.cancel();
                        }
                        //确保次级窗口在屏幕外
                        resetSubContainerPlace();
                        showSubContainer();
                    } else {
                        final int height = (int) (getHeight() * mSubRatio);
                        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
                        params.gravity = Gravity.BOTTOM;
                        mSubContainer.setLayoutParams(params);
                        addView(mSubContainer);
                        ViewCompat.postOnAnimation(mSubContainer, new Runnable() {
                            @Override
                            public void run() {
                                mSubContainer.setTranslationY(height);
                                showSubContainer();
                            }
                        });
                    }
                    return true;
                }

            }

            @Override
            public void onLongPress(MotionEvent e) {
                View pressedView = findChildView(mMainRecyclerView, e);
                if (pressedView == null) return;
                L.d("Main recycler view on long press: x: %1$s + y: %2$s", e.getX(), e.getY());
                int position = mMainRecyclerView.getChildAdapterPosition(pressedView);

                int pointerId = MotionEventCompat.getPointerId(e, 0);
                if (pointerId == mMainActivePointerId) {
                    if (mMainCallBack.canDragOnLongPress(position, pressedView)) {
                        mSelectedPosition = position;
                        mSelectedStartX = pressedView.getLeft();
                        mSelectedStartY = pressedView.getTop();
                        mDx = mDy = 0f;
                        int index = MotionEventCompat.findPointerIndex(e, mMainActivePointerId);
                        mInitialTouchX = MotionEventCompat.getX(e, index);
                        mInitialTouchY = MotionEventCompat.getY(e, index);
                        L.d("handle event on long press:X: %1$s , Y: %2$s ", mInitialTouchX, mInitialTouchY);
                        inMainRegion = true;
                        mSelected = pressedView;
                        pressedView.startDrag(ClipData.newPlainText(DESCRIPTION, MAIN),
                                new ClassifyDragShadowBuilder(pressedView), mSelected, 0);
                    }
                }
            }
        });
        mMainItemTouchListener = new RecyclerView.OnItemTouchListener() {

            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                mMainGestureDetector.onTouchEvent(e);
                int action = MotionEventCompat.getActionMasked(e);
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        mMainActivePointerId = MotionEventCompat.getPointerId(e, 0);
                        break;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        mMainActivePointerId = ACTIVE_POINTER_ID_NONE;
                        break;
                }
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {
                mMainGestureDetector.onTouchEvent(e);
                int action = MotionEventCompat.getActionMasked(e);
                switch (action) {
                    case MotionEvent.ACTION_MOVE:
                        break;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        mMainActivePointerId = ACTIVE_POINTER_ID_NONE;
                        break;
                    case MotionEvent.ACTION_POINTER_UP:
                        int pointerIndex = MotionEventCompat.getActionIndex(e);
                        int pointerId = MotionEventCompat.getPointerId(e, pointerIndex);
                        if (pointerId == mSubActivePointerId) {
                            int newPointerId = pointerIndex == 0 ? 1 : 0;
                            mMainActivePointerId = MotionEventCompat.getPointerId(e, newPointerId);
                        }
                        break;
                }
            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        };
        mSubGestureDetector = new GestureDetectorCompat(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                View pressedView = findChildView(mSubRecyclerView, e);
                if (pressedView == null) return false;
                int position = mSubRecyclerView.getChildAdapterPosition(pressedView);
                mSubCallBack.onItemClick(position, pressedView);
                return true;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                View pressedView = findChildView(mSubRecyclerView, e);
                if (pressedView == null) return;
                L.d("Sub recycler view on long press: x: %1$s + y: %2$s", e.getX(), e.getY());
                int position = mSubRecyclerView.getChildAdapterPosition(pressedView);
                int pointerId = MotionEventCompat.getPointerId(e, 0);
                if (pointerId == mSubActivePointerId) {
                    if (mSubCallBack.canDragOnLongPress(position, pressedView)) {
                        mSelectedPosition = position;
                        mSelectedStartX = pressedView.getLeft();
                        mSelectedStartY = pressedView.getTop();
                        mDx = mDy = 0f;
                        int index = MotionEventCompat.findPointerIndex(e, mSubActivePointerId);
                        mInitialTouchX = MotionEventCompat.getX(e, index);
                        mInitialTouchY = MotionEventCompat.getY(e, index);
                        inSubRegion = true;
                        mSelected = pressedView;
                        pressedView.startDrag(ClipData.newPlainText(
                                DESCRIPTION, SUB),
                                getShadowBuilder(pressedView), mSelected, 0);
                    }
                }
            }
        });
        mSubItemTouchListener = new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                mSubGestureDetector.onTouchEvent(e);
                int action = MotionEventCompat.getActionMasked(e);
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        mSubActivePointerId = MotionEventCompat.getPointerId(e, 0);
                        mInitialTouchX = e.getX();
                        mInitialTouchY = e.getY();
                        break;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        mSubActivePointerId = ACTIVE_POINTER_ID_NONE;
                        break;
                }
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {
                mSubGestureDetector.onTouchEvent(e);
                int action = MotionEventCompat.getActionMasked(e);
                switch (action) {
                    case MotionEvent.ACTION_MOVE:
                        break;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        mSubActivePointerId = ACTIVE_POINTER_ID_NONE;
                        break;
                    case MotionEvent.ACTION_POINTER_UP:
                        int pointerIndex = MotionEventCompat.getActionIndex(e);
                        int pointerId = MotionEventCompat.getPointerId(e, pointerIndex);
                        if (pointerId == mSubActivePointerId) {
                            int newPointerId = pointerIndex == 0 ? 1 : 0;
                            mSubActivePointerId = MotionEventCompat.getPointerId(e, newPointerId);
                        }
                        break;

                }
            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        };
    }

    private void resetSubContainerPlace() {
        int height = mSubContainer.getHeight();
        mSubContainer.setTranslationY(height);
    }

    private AnimatorSet mShowSubAnim;
    private AnimatorSet mHideSubAnim;

    /**
     * 显示次级窗口
     */
    private void showSubContainer() {
        if (mShowSubAnim != null && mShowSubAnim.isRunning()) return;
        mShowSubAnim = new AnimatorSet();
        ObjectAnimator subAnim = ObjectAnimator.ofFloat(mSubContainer, "translationY", 0);
        ObjectAnimator shadowAnim = ObjectAnimator.ofFloat(mMainShadowView, "alpha", 0f, 1f);
        mShowSubAnim.setDuration(mAnimationDuration);
        mShowSubAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        mShowSubAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
            }

            @Override
            public void onAnimationStart(Animator animation) {
                mMainShadowView.setVisibility(VISIBLE);
            }
        });
        mShowSubAnim.play(subAnim).with(shadowAnim);
        mShowSubAnim.start();
    }

    /**
     * 隐藏次级窗口
     */
    private void hideSubContainer() {
        if (mHideSubAnim != null && mHideSubAnim.isRunning()) return;
        int height = mSubContainer.getHeight();
        mHideSubAnim = new AnimatorSet();
        ObjectAnimator subAnim = ObjectAnimator.ofFloat(mSubContainer, "translationY", height);
        ObjectAnimator shadowAnim = ObjectAnimator.ofFloat(mMainShadowView, "alpha", 1f, 0f);
        mHideSubAnim.setDuration(mAnimationDuration);
        mHideSubAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        mHideSubAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationCancel(Animator animation) {
                mMainShadowView.setVisibility(GONE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mMainShadowView.setVisibility(GONE);
            }

            @Override
            public void onAnimationStart(Animator animation) {
                mMainShadowView.setVisibility(VISIBLE);
            }
        });
        mHideSubAnim.playTogether(subAnim, shadowAnim);
        mHideSubAnim.start();

    }

    private boolean mergeSuccess = false;

    class MainDragListener implements View.OnDragListener {
        @Override
        public boolean onDrag(View v, DragEvent event) {
            int action = event.getAction();
            int width = mSelected.getWidth();
            int height = mSelected.getHeight();
            float x = event.getX();
            float y = event.getY();
            float centerX = x - width / 2;
            float centerY = y - height / 2;
            switch (action) {
                case DragEvent.ACTION_DRAG_STARTED:
                    if (inMainRegion) {
                        obtainVelocityTracker();
                        restoreDragView();
                        mDragView.setBackgroundDrawable(new DragDrawable(mSelected));
                        mDragView.setVisibility(VISIBLE);
                        mMainCallBack.setDragPosition(mSelectedPosition);
                        mDragView.setX(mInitialTouchX - width / 2);
                        mDragView.setY(mInitialTouchY - height / 2);
                        mDragView.bringToFront();
                        mElevationHelper.floatView(mMainRecyclerView, mDragView);
                    }
                    break;
                case DragEvent.ACTION_DRAG_LOCATION:
                    mVelocityTracker.addMovement(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(),
                            MotionEvent.ACTION_MOVE, x, y, 0));
                    mDragView.setX(centerX);
                    mDragView.setY(centerY);
                    mDx = x - mInitialTouchX;
                    mDy = y - mInitialTouchY;
                    moveIfNecessary(mSelected);
                    removeCallbacks(mScrollRunnable);
                    mScrollRunnable.run();
                    invalidate();
                    break;
                case DragEvent.ACTION_DRAG_ENDED:
                    if (mergeSuccess) {
                        mergeSuccess = false;
                        break;
                    }
                    if (inMainRegion) {
                        doRecoverAnimation();
                    }
                    releaseVelocityTracker();
                    break;
                case DragEvent.ACTION_DRAG_EXITED:
                    break;
                case DragEvent.ACTION_DROP:
                    if (inMergeState) {
                        inMergeState = false;
                        if (mLastMergeStartPosition == -1) break;
                        ChangeInfo changeInfo = mMainCallBack.onPrepareMerge(mMainRecyclerView, mSelectedPosition, mLastMergeStartPosition);
                        RecyclerView.ViewHolder target = mMainRecyclerView.findViewHolderForAdapterPosition(mLastMergeStartPosition);
                        if (target == null || changeInfo == null) {
                            mergeSuccess = false;
                            break;
                        }
                        float scaleX = ((float) changeInfo.itemWidth)/((float) (mSelected.getWidth()-changeInfo.paddingLeft-changeInfo.paddingRight -2*changeInfo.outlinePadding));
                        float scaleY = ((float) changeInfo.itemHeight)/((float) (mSelected.getHeight()-changeInfo.paddingTop-changeInfo.paddingBottom-2*changeInfo.outlinePadding));
                        int targetX = (int) (target.itemView.getLeft() + changeInfo.left + changeInfo.paddingLeft - (changeInfo.paddingLeft+changeInfo.outlinePadding)*scaleX);
                        int targetY = (int) (target.itemView.getTop() + changeInfo.top +changeInfo.paddingTop- (changeInfo.paddingTop+changeInfo.outlinePadding)*scaleY);
                        mDragView.setPivotX(0);
                        mDragView.setPivotY(0);
                        mDragView.animate().x(targetX).y(targetY).scaleX(scaleX).scaleY(scaleY).setListener(mMergeAnimListener).setDuration(mAnimationDuration).start();
                        mergeSuccess = true;
                    }
                    break;
            }
            return true;
        }
    }

    private AnimatorListenerAdapter mMergeAnimListener = new AnimatorListenerAdapter() {
        @Override
        public void onAnimationStart(Animator animation) {
            mMainCallBack.onStartMergeAnimation(mMainRecyclerView, mSelectedPosition, mLastMergeStartPosition,mAnimationDuration);
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            mMainCallBack.onMerged(mMainRecyclerView, mSelectedPosition, mLastMergeStartPosition);
            restoreToInitial();
        }

        @Override
        public void onAnimationCancel(Animator animation) {
            mMainCallBack.onMerged(mMainRecyclerView, mSelectedPosition, mLastMergeStartPosition);
            restoreToInitial();
        }
    };

    class SubDragListener implements View.OnDragListener {
        @Override
        public boolean onDrag(View v, DragEvent event) {
            int action = event.getAction();
            int width = mSelected.getWidth();
            int height = mSelected.getHeight();
            float x = event.getX();
            float y = event.getY();
            float centerX = x - width / 2;
            float centerY = y - height / 2;
            float marginTop = getHeight() - mSubContainer.getHeight();
            switch (action) {
                case DragEvent.ACTION_DRAG_STARTED:
                    if (inSubRegion) {
                        obtainVelocityTracker();
                        restoreDragView();
                        mDragView.setBackgroundDrawable(new DragDrawable(mSelected));
                        mDragView.setVisibility(VISIBLE);
                        mSubCallBack.setDragPosition(mSelectedPosition);
                        mDragView.setX(mInitialTouchX - width / 2);
                        mDragView.setY(mInitialTouchY - height / 2 + marginTop);
                        mDragView.bringToFront();
                        mElevationHelper.floatView(mSubRecyclerView, mDragView);
                    }
                    break;
                case DragEvent.ACTION_DRAG_LOCATION:
                    mVelocityTracker.addMovement(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(),
                            MotionEvent.ACTION_MOVE, x, y, 0));
                    mDragView.setX(centerX);
                    mDragView.setY(centerY + marginTop);
                    mDx = x - mInitialTouchX;
                    mDy = y - mInitialTouchY;
                    moveIfNecessary(mSelected);
                    removeCallbacks(mScrollRunnable);
                    mScrollRunnable.run();
                    invalidate();
                    break;
                case DragEvent.ACTION_DRAG_ENDED:
                    if (inSubRegion) {
                        doRecoverAnimation();
                    }
                    releaseVelocityTracker();
                    break;
                case DragEvent.ACTION_DRAG_EXITED:
                    if (mSubCallBack.canDragOut(mSelectedPosition)) {
                        inSubRegion = false;
                        inMainRegion = true;
                        hideSubContainer();
                        mSelectedPosition = mMainCallBack.onLeaveSubRegion(mSelectedPosition, new SubAdapterReference(mSubCallBack));
                        mMainCallBack.setDragPosition(mSelectedPosition);
                        mSubCallBack.setDragPosition(-1);
                    }
                    break;
                case DragEvent.ACTION_DROP:
                    break;
            }
            return true;
        }
    }

    /**
     * 做恢复到之前状态的动画
     */
    private void doRecoverAnimation() {
        Animator recoverAnimator = null;
        if (inSubRegion) {
            RecyclerView.ViewHolder holder = mSubRecyclerView.findViewHolderForAdapterPosition(mSelectedPosition);
            if (holder == null) {
                PropertyValuesHolder yOffset = PropertyValuesHolder.ofFloat("y", getHeight() + mSelected.getHeight());
                recoverAnimator = ObjectAnimator.ofPropertyValuesHolder(mDragView, yOffset);
            } else {
                PropertyValuesHolder xOffset = PropertyValuesHolder.ofFloat("x", mSubContainer.getLeft() + holder.itemView.getLeft());
                PropertyValuesHolder yOffset = PropertyValuesHolder.ofFloat("y", mSubContainer.getTop() + holder.itemView.getTop());
                recoverAnimator = ObjectAnimator.ofPropertyValuesHolder(mDragView, xOffset, yOffset);
            }
        }

        if(inMainRegion){
            RecyclerView.ViewHolder holder = mMainRecyclerView.findViewHolderForAdapterPosition(mSelectedPosition);
            if (holder == null) {
                PropertyValuesHolder yOffset = PropertyValuesHolder.ofFloat("y", getHeight() + mSelected.getHeight());
                recoverAnimator = ObjectAnimator.ofPropertyValuesHolder(mDragView, yOffset);
            } else {
                PropertyValuesHolder xOffset = PropertyValuesHolder.ofFloat("x", holder.itemView.getLeft());
                PropertyValuesHolder yOffset = PropertyValuesHolder.ofFloat("y", holder.itemView.getTop());
                recoverAnimator = ObjectAnimator.ofPropertyValuesHolder(mDragView, xOffset, yOffset);
            }
        }
        if(recoverAnimator == null) return;
        recoverAnimator.setDuration(mAnimationDuration);
        recoverAnimator.setInterpolator(sDragScrollInterpolator);
        recoverAnimator.addListener(mRecoverAnimatorListener);
        recoverAnimator.start();
    }

    private AnimatorListenerAdapter mRecoverAnimatorListener = new AnimatorListenerAdapter() {
        @Override
        public void onAnimationEnd(Animator animation) {
            restoreToInitial();
        }
    };

    private void restoreToInitial() {

        if (inSubRegion) {
            restoreDragView();
            mSubCallBack.setDragPosition(-1);
            inSubRegion = false;
        }
        if (inMainRegion) {
            restoreDragView();
            mMainCallBack.setDragPosition(-1);
            inMainRegion = false;
        }
    }

    private void restoreDragView() {
        mDragView.setVisibility(GONE);
        mDragView.setScaleX(1f);
        mDragView.setScaleY(1f);
        mDragView.setTranslationX(0f);
        mDragView.setTranslationX(0f);
    }

    /**
     * If user drags the view to the edge, trigger a scroll if necessary.
     */
    private boolean scrollIfNecessary() {
        RecyclerView recyclerView = null;
        if (inMainRegion) {
            recyclerView = mMainRecyclerView;
        }
        if (inSubRegion) {
            recyclerView = mSubRecyclerView;
        }
        if (recyclerView == null) return false;
        final long now = System.currentTimeMillis();
        final long scrollDuration = mDragScrollStartTimeInMs
                == Long.MIN_VALUE ? 0 : now - mDragScrollStartTimeInMs;
        RecyclerView.LayoutManager lm = recyclerView.getLayoutManager();

        int scrollX = 0;
        int scrollY = 0;
        if (lm.canScrollHorizontally()) {
            int curX = (int) (mInitialTouchX + mDx - mSelected.getWidth() / 2);
            final int leftDiff = curX - mEdgeWidth - recyclerView.getPaddingLeft();
            if (mDx < 0 && leftDiff < 0) {
                scrollX = leftDiff;
            } else if (mDx > 0) {
                final int rightDiff =
                        curX + mSelected.getWidth() + mEdgeWidth - (recyclerView.getWidth() - recyclerView.getPaddingRight());
                if (rightDiff > 0) {
                    scrollX = rightDiff;
                }
            }
        }
        if (lm.canScrollVertically()) {
            int curY = (int) (mInitialTouchY + mDy - mSelected.getHeight() / 2);
            final int topDiff = curY - mEdgeWidth - recyclerView.getPaddingTop();
            if (mDy < 0 && topDiff < 0) {
                scrollY = topDiff;
            } else if (mDy > 0) {
                final int bottomDiff = curY + mSelected.getHeight() + mEdgeWidth -
                        (recyclerView.getHeight() - recyclerView.getPaddingBottom());
                if (bottomDiff > 0) {
                    scrollY = bottomDiff;
                }
            }
        }
        if (scrollX != 0) {
            scrollX = interpolateOutOfBoundsScroll(recyclerView,
                    mSelected.getWidth(), scrollX,
                    recyclerView.getWidth(), scrollDuration);
        }
        if (scrollY != 0) {
            scrollY = interpolateOutOfBoundsScroll(recyclerView,
                    mSelected.getHeight(), scrollY,
                    recyclerView.getHeight(), scrollDuration);
        }
        if (scrollX != 0 || scrollY != 0) {
            if (mDragScrollStartTimeInMs == Long.MIN_VALUE) {
                mDragScrollStartTimeInMs = now;
            }
            recyclerView.scrollBy(scrollX, scrollY);
            return true;
        }
        mDragScrollStartTimeInMs = Long.MIN_VALUE;
        return false;
    }


    private int interpolateOutOfBoundsScroll(RecyclerView recyclerView,
                                             int viewSize, int viewSizeOutOfBounds,
                                             int totalSize, long msSinceStartScroll) {
        final int maxScroll = getMaxDragScroll(recyclerView);
        final int absOutOfBounds = Math.abs(viewSizeOutOfBounds);
        final int direction = (int) Math.signum(viewSizeOutOfBounds);
        // might be negative if other direction
        float outOfBoundsRatio = Math.min(1f, 1f * absOutOfBounds / viewSize);
        final int cappedScroll = (int) (direction * maxScroll *
                sDragViewScrollCapInterpolator.getInterpolation(outOfBoundsRatio));
        final float timeRatio;
        if (msSinceStartScroll > DRAG_SCROLL_ACCELERATION_LIMIT_TIME_MS) {
            timeRatio = 1f;
        } else {
            timeRatio = (float) msSinceStartScroll / DRAG_SCROLL_ACCELERATION_LIMIT_TIME_MS;
        }
        final int value = (int) (cappedScroll * sDragScrollInterpolator
                .getInterpolation(timeRatio));
        if (value == 0) {
            return viewSizeOutOfBounds > 0 ? 1 : -1;
        }
        return value;
    }

    private static final long DRAG_SCROLL_ACCELERATION_LIMIT_TIME_MS = 2000;
    private static final Interpolator sDragViewScrollCapInterpolator = new Interpolator() {
        public float getInterpolation(float t) {
            t -= 1.0f;
            return t * t * t * t * t + 1.0f;
        }
    };
    private static final Interpolator sDragScrollInterpolator = new Interpolator() {
        public float getInterpolation(float t) {
            return t * t * t * t * t;
        }
    };
    private int mCachedMaxScrollSpeed = -1;

    private int getMaxDragScroll(RecyclerView recyclerView) {
        if (mCachedMaxScrollSpeed == -1) {
            mCachedMaxScrollSpeed = recyclerView.getResources().getDimensionPixelSize(
                    R.dimen.item_touch_helper_max_drag_scroll_per_frame);
        }
        return mCachedMaxScrollSpeed;
    }

    /**
     * When user started to drag scroll. Reset when we don't scroll
     */
    private long mDragScrollStartTimeInMs;


    /**
     * When user drags a view to the edge, we start scrolling the LayoutManager as long as View
     * is partially out of bounds.
     */
    private final Runnable mScrollRunnable = new Runnable() {
        @Override
        public void run() {
            if (mSelected != null && scrollIfNecessary()) {
                if (mSelected != null) { //it might be lost during scrolling
                    moveIfNecessary(mSelected);
                }
                removeCallbacks(mScrollRunnable);
                ViewCompat.postOnAnimation(ClassifyView.this, this);
            }
        }
    };
    private boolean inMergeState = false;
    private int mLastMergeStartPosition = -1;

    private void moveIfNecessary(View view) {
        final int x = (int) (mSelectedStartX + mDx);
        final int y = (int) (mSelectedStartY + mDy);
        //如果移动范围在自身范围内
        if (Math.abs(y - view.getTop()) < view.getHeight() * 0.5f
                && Math.abs(x - view.getLeft())
                < view.getWidth() * 0.5f) {
            return;
        }
        List<View> swapTargets = findSwapTargets(view);
        if (swapTargets.size() == 0) return;
        View target = chooseTarget(view, swapTargets, x, y);
        if (target == null) return;
        if (inSubRegion) {//次级目录下 没有merge形式
            int targetPosition = mSubRecyclerView.getChildAdapterPosition(target);
            int state = mSubCallBack.getCurrentState(mSelected, target, x, y, mVelocityTracker, mSelectedPosition,
                    targetPosition);
            if (state == STATE_MOVE) {
                if (mSubCallBack.onMove(mSelectedPosition, targetPosition)) {
                    mSelectedPosition = targetPosition;
                    RecyclerView.ViewHolder viewHolder = mSubRecyclerView.findViewHolderForAdapterPosition(mSelectedPosition);
                    if(viewHolder != null) mSelected = viewHolder.itemView;
                    mSubCallBack.setDragPosition(targetPosition);
                    mSubCallBack.moved(mSelectedPosition, targetPosition);
                }
            }
        }
        if (inMainRegion) {//在主层级下 有merge状况 以及次级目录拖动到主层级的状况
            int targetPosition = mMainRecyclerView.getChildAdapterPosition(target);
            int state = mMainCallBack.getCurrentState(mSelected, target, x, y, mVelocityTracker, mSelectedPosition,
                    targetPosition);
            boolean mergeState = state == STATE_MERGE;
            if (mergeState ^ inMergeState) {
                inMergeState = mergeState;
                if (inMergeState) {
                    if (mMainCallBack.onMergeStart(mMainRecyclerView, mSelectedPosition, targetPosition)) {
                        mLastMergeStartPosition = targetPosition;
                    }
                } else {
                    if (mLastMergeStartPosition != -1) {
                        mMainCallBack.onMergeCancel(mMainRecyclerView, mSelectedPosition, mLastMergeStartPosition);
                        mLastMergeStartPosition = -1;
                    }
                }
            }
            if (state == STATE_MOVE) {
                if(inMergeState && mLastMergeStartPosition != -1){
                    //makeSure trigger mergeCancel
                    mMainCallBack.onMergeCancel(mMainRecyclerView, mSelectedPosition, mLastMergeStartPosition);
                    mLastMergeStartPosition = -1;
                    inMergeState = false;
                }
                if (mMainCallBack.onMove(mSelectedPosition, targetPosition)) {
                    mSelectedPosition = targetPosition;
                    RecyclerView.ViewHolder viewHolder = mMainRecyclerView.findViewHolderForAdapterPosition(mSelectedPosition);
                    if(viewHolder != null) mSelected = viewHolder.itemView;
                    mMainCallBack.setDragPosition(targetPosition);
                    mMainCallBack.moved(mSelectedPosition, targetPosition);
                }
            }
        }
    }

    private List<View> mSwapTargets;

    /**
     * 找到当前移动View 有覆盖的view
     *
     * @return
     */
    private List<View> findSwapTargets(View view) {
        if (mSwapTargets == null) {
            mSwapTargets = new ArrayList<>();
        } else {
            mSwapTargets.clear();
        }
        int left = Math.round(mSelectedStartX + mDx);
        int top = Math.round(mSelectedStartY + mDy);
        int right = left + view.getWidth();
        int bottom = top + view.getHeight();
        RecyclerView.LayoutManager lm = null;
        RecyclerView recyclerView = null;
        if (inMainRegion) {
            lm = getMainLayoutManager();
            recyclerView = mMainRecyclerView;
        }
        if (inSubRegion) {
            lm = getSubLayoutManager();
            recyclerView = mSubRecyclerView;
        }
        if (lm == null || recyclerView == null) return mSwapTargets;
        int childCount = lm.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = lm.getChildAt(i);
            if (child == view) {
                //本身
                continue;
            }
            if (child.getBottom() < top || child.getTop() > bottom || child.getLeft() > right || child.getRight() < left) {
                continue;//没有覆盖到
            }
            int targetPosition = recyclerView.getChildAdapterPosition(child);
            //检验目标位置是否能移动
            if (inMainRegion) {
                if (!mMainCallBack.canDropOVer(mSelectedPosition, targetPosition)) continue;
            }
            if (inSubRegion) {
                if (!mSubCallBack.canDropOver(mSelectedPosition, targetPosition)) continue;
            }
            mSwapTargets.add(child);
        }
        return mSwapTargets;
    }

    private void obtainVelocityTracker() {
        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
        }
        mVelocityTracker = VelocityTracker.obtain();
    }

    private void releaseVelocityTracker() {
        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }

    /**
     * 从候选项中找到最有优势的目标
     *
     * @param selected
     * @param swapTargets
     * @param curX
     * @param curY
     * @return
     */
    protected View chooseTarget(View selected, List<View> swapTargets, int curX, int curY) {
        int right = curX + selected.getWidth();
        int bottom = curY + selected.getHeight();
        View winner = null;
        int winnerScore = Integer.MAX_VALUE;
        final int dx = curX - selected.getLeft();
        final int dy = curY - selected.getTop();
        final int targetsSize = swapTargets.size();
        for (int i = 0; i < targetsSize; i++) {
            final View target = swapTargets.get(i);
            final int score = Math.abs(target.getLeft() - curX) + Math.abs(target.getTop() - curY)
                    + Math.abs(target.getBottom() - bottom) + Math.abs(target.getRight() - right);
            if (score < winnerScore) {
                winnerScore = score;
                winner = target;
            }
//            if (dx > 0) {
//                int diff = target.getRight() - right;
//                if (diff < 0) {
//                    final int score = Math.abs(diff);
//                    if (score > winnerScore) {
//                        winnerScore = score;
//                        winner = target;
//                    }
//                }
//            }
//            if (dx < 0) {
//                int diff = target.getLeft() - curX;
//                if (diff > 0) {
//                    final int score = Math.abs(diff);
//                    if (score > winnerScore) {
//                        winnerScore = score;
//                        winner = target;
//                    }
//                }
//            }
//            if (dy < 0) {
//                int diff = target.getTop() - curY;
//                if (diff > 0) {
//                    final int score = Math.abs(diff);
//                    if (score > winnerScore) {
//                        winnerScore = score;
//                        winner = target;
//                    }
//                }
//            }
//
//            if (dy > 0) {
//                int diff = target.getBottom() - bottom;
//                if (diff < 0) {
//                    final int score = Math.abs(diff);
//                    if (score > winnerScore) {
//                        winnerScore = score;
//                        winner = target;
//                    }
//                }
//            }
        }
        return winner;
    }

    /**
     * 获取DragShadowBuilder 用于渲染 被拖动的view
     * 默认使用 {@link ClassifyDragShadowBuilder} 实现 自定义时请重写该方法
     *
     * @param view 被拖动item 的 root view
     * @return
     */
    protected DragShadowBuilder getShadowBuilder(View view) {
        return new ClassifyDragShadowBuilder(view);
    }

    private ElevationHelper mElevationHelper = new ElevationHelper();

    static class ElevationHelper {


        public void floatView(RecyclerView recyclerView, View dragView) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                float maxElevation = findMaxElevation(recyclerView) + 1f;
                dragView.setElevation(maxElevation);
            } else {
                Drawable drawable = dragView.getBackground();
                if (drawable instanceof DragDrawable) {
                    DragDrawable dragDrawable = (DragDrawable) drawable;
                    dragDrawable.showShadow();
                    dragView.setLayerType(View.LAYER_TYPE_SOFTWARE, dragDrawable.getPaint());
                }
            }
        }

        private float findMaxElevation(RecyclerView recyclerView) {
            final int childCount = recyclerView.getChildCount();
            float max = 0;
            for (int i = 0; i < childCount; i++) {
                final View child = recyclerView.getChildAt(i);

                final float elevation = ViewCompat.getElevation(child);
                if (elevation > max) {
                    max = elevation;
                }
            }
            return max;
        }
    }


}