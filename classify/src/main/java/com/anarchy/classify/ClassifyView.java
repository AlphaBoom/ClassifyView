package com.anarchy.classify;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.SystemClock;
import android.support.annotation.IntDef;
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
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;

import com.anarchy.classify.adapter.BaseMainAdapter;
import com.anarchy.classify.adapter.BaseSubAdapter;
import com.anarchy.classify.callback.BaseCallBack;
import com.anarchy.classify.callback.MainRecyclerViewCallBack;
import com.anarchy.classify.callback.SubRecyclerViewCallBack;
import com.anarchy.classify.simple.BaseSimpleAdapter;
import com.anarchy.classify.util.L;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * <p>
 * Date: 16/6/1 14:16
 * Author: rsshinide38@163.com
 * <p>
 */
public class ClassifyView extends FrameLayout {

    /**
     * 不做处理的状态
     */
    public static final int MOVE_STATE_NONE = 0;
    /**
     * 当前状态为 可移动
     */
    public static final int MOVE_STATE_MOVE = 1;
    /**
     * 当前状态为 可合并
     */
    public static final int MOVE_STATE_MERGE = 2;


    public static final int STATE_IDLE = 0;
    public static final int STATE_DRAG = 1;
    public static final int STATE_SETTLE = 2;

    public static final int UNKNOWN_REGION = 0;
    public static final int IN_MAIN_REGION = 1;
    public static final int IN_SUB_REGION = 2;
    public static final int SUB_REGION_LEAVE_TYPE = 0x10;


    public static final int LEFT_TOP = 0;
    public static final int RIGHT_TOP = 1;
    public static final int LEFT_BOTTOM = 2;
    public static final int RIGHT_BOTTOM = 3;
    public static final int CENTER = 4;

    @IntDef({MOVE_STATE_NONE, MOVE_STATE_MOVE, MOVE_STATE_MERGE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface MoveState {
    }
    @IntDef(value = {IN_MAIN_REGION,IN_SUB_REGION, UNKNOWN_REGION,SUB_REGION_LEAVE_TYPE},flag = true)
    @Retention(RetentionPolicy.SOURCE)
    private @interface Region{

    }
    @IntDef({STATE_IDLE,STATE_DRAG, STATE_SETTLE})
    @Retention(RetentionPolicy.SOURCE)
    private @interface State{

    }
    @IntDef({CENTER,LEFT_TOP,RIGHT_TOP,LEFT_BOTTOM,RIGHT_BOTTOM})
    @Retention(RetentionPolicy.SOURCE)
    private @interface MyGravity{

    }




    public boolean inScrollMode;


    private static final int ACTIVE_POINTER_ID_NONE = -1;
    private static final String DESCRIPTION = "Long press";
    private static final String MAIN = "main";
    private static final String SUB = "sub";
    private static final long DEFAULT_DELAYED = 150;

    private static final int CHANGE_DURATION = 10;


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
    private int mAnimationDuration;


    private int mSelectedStartX;
    private int mSelectedStartY;
    private float mInitialTouchX;
    private float mInitialTouchY;
    private int mStatusBarHeight;
    private float mDx;
    private float mDy;
    private View mSelected;
    private int mSelectedPosition;
    private Dialog mSubDialog;
    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mDragLayoutParams;
    private int mSubContainerWidth;
    private int mSubContainerHeight;
    private List<DragListener> mDragListeners;
    private int[] mMainLocation = new int[2];
    private int[] mSubLocation = new int[2];
    private boolean mMoveListenerEnable = true;
    private boolean mDragViewIsShow = false;
    private boolean mPendingRecover = false;
    @State
    private int mState;
    @Region
    private int mRegion;
    private int mGravity;
    private float mDragScaleX;
    private float mDragScaleY;
    private float mDragInMergeScaleX;
    private float mDragInMergeScaleY;
    /**
     * 储存所有进入了merge状态的position
     */
    private Queue<Integer> mInMergeQueue = new LinkedList<>();
    /**
     * 触发滑动距离
     */
    private int mEdgeWidth;


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
        mAnimationDuration = a.getInt(R.styleable.ClassifyView_AnimationDuration, 200);
        mEdgeWidth = a.getDimensionPixelSize(R.styleable.ClassifyView_EdgeWidth, 15);
        mDragScaleX = a.getFloat(R.styleable.ClassifyView_DragScaleX,1f);
        mDragScaleY = a.getFloat(R.styleable.ClassifyView_DragScaleY,1f);
        mDragInMergeScaleX = a.getFloat(R.styleable.ClassifyView_DragInMergeScaleX,1f);
        mDragInMergeScaleY = a.getFloat(R.styleable.ClassifyView_DragInMergeScaleY,1f);
        mGravity = a.getInt(R.styleable.ClassifyView_DragScalePivotGravity,LEFT_TOP);
        int mainPadding = a.getDimensionPixelSize(R.styleable.ClassifyView_MainPadding,0);
        int mainPaddingLeft = a.getDimensionPixelSize(R.styleable.ClassifyView_MainPaddingLeft,0);
        int mainPaddingTop = a.getDimensionPixelSize(R.styleable.ClassifyView_MainPaddingTop,0);
        int mainPaddingRight = a.getDimensionPixelSize(R.styleable.ClassifyView_MainPaddingRight,0);
        int mainPaddingBottom = a.getDimensionPixelSize(R.styleable.ClassifyView_MainPaddingBottom,0);
        boolean mainClipToPadding = a.getBoolean(R.styleable.ClassifyView_MainClipToPadding,true);
        int subPadding = a.getDimensionPixelSize(R.styleable.ClassifyView_SubPadding,0);
        int subPaddingLeft = a.getDimensionPixelSize(R.styleable.ClassifyView_SubPaddingLeft,0);
        int subPaddingTop = a.getDimensionPixelSize(R.styleable.ClassifyView_SubPaddingTop,0);
        int subPaddingRight = a.getDimensionPixelSize(R.styleable.ClassifyView_SubPaddingRight,0);
        int subPaddingBottom = a.getDimensionPixelSize(R.styleable.ClassifyView_SubPaddingBottom,0);
        boolean subClipToPadding = a.getBoolean(R.styleable.ClassifyView_SubClipToPadding,true);
        a.recycle();
        mMainRecyclerView = getMain(context, attrs);
        if(mainPadding != 0){
            mMainRecyclerView.setPadding(mainPadding,mainPadding,mainPadding,mainPadding);
        }else if(mainPaddingLeft != 0 || mainPaddingTop != 0 || mainPaddingRight != 0 || mainPaddingBottom != 0){
            mMainRecyclerView.setPadding(mainPaddingLeft,mainPaddingTop,mainPaddingRight,mainPaddingBottom);
        }
        mMainRecyclerView.setClipToPadding(mainClipToPadding);
        mSubRecyclerView = getSub(context, attrs);
        if(subPadding > 0){
            mSubRecyclerView.setPadding(subPadding,subPadding,subPadding,subPadding);
        }else if(subPaddingLeft != 0 || subPaddingTop != 0 || subPaddingRight != 0 || subPaddingBottom != 0){
            mSubRecyclerView.setPadding(subPaddingLeft,subPaddingTop,subPaddingRight,subPaddingBottom);
        }
        mSubRecyclerView.setClipToPadding(subClipToPadding);
        mMainContainer.addView(mMainRecyclerView);
        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        addViewInLayout(mMainContainer, 0, mMainContainer.getLayoutParams());
        mDragView = new View(context);
        int id = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (id > 0) {
            mStatusBarHeight = getResources().getDimensionPixelSize(id);
        }
        setUpTouchListener(context);
        mDragListeners = new ArrayList<>();
    }

    /**
     * 设置被拖拽的视图缩放的中心点
     * @param gravity
     */
    public void setDragGravity(@MyGravity int gravity){
        mGravity = gravity;
    }

    /**
     * 设置被拖拽时的x轴缩放大小(这个缩放大小只影响显示效果)
     * @param dragScaleX
     */
    public void setDragScaleX(float dragScaleX) {
        mDragScaleX = dragScaleX;
    }
    /**
     * 设置被拖拽时的y轴缩放大小(这个缩放大小只影响显示效果)
     * @param dragScaleY
     */
    public void setDragScaleY(float dragScaleY) {
        mDragScaleY = dragScaleY;
    }

    /**
     * 设置被拖拽的选项在可合并状态下的x轴缩放大小(这个缩放大小只影响显示效果)
     * @param dragInMergeScaleX
     */
    public void setDragInMergeScaleX(float dragInMergeScaleX) {
        mDragInMergeScaleX = dragInMergeScaleX;
    }
    /**
     * 设置被拖拽的选项在可合并状态下的y轴缩放大小(这个缩放大小只影响显示效果)
     * @param dragInMergeScaleY
     */
    public void setDragInMergeScaleY(float dragInMergeScaleY) {
        mDragInMergeScaleY = dragInMergeScaleY;
    }


    public void addDragListener(DragListener listener) {
        mDragListeners.add(listener);
    }

    public void removeDragListener(DragListener listener) {
        mDragListeners.remove(listener);
    }

    public void removeAllDragListener() {
        mDragListeners.clear();
    }

    /**
     * 是否开启移动监听
     *
     * @param enable false disable true enable
     */
    public void enableMoveListener(boolean enable) {
        mMoveListenerEnable = enable;
    }


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mSubDialog != null && mSubDialog.isShowing()) {
            mSubDialog.dismiss();
        }
        if (mDragViewIsShow) {
            mWindowManager.removeViewImmediate(mDragView);
            mDragViewIsShow = false;
        }
    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mDragLayoutParams = createDragLayoutParams();
        mDragView.setPivotX(0);
        mDragView.setPivotY(0);
    }


    /**
     * 生成拖拽view的布局参数
     * @return
     */
    @NonNull
    protected WindowManager.LayoutParams createDragLayoutParams(){
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
        if (Build.VERSION.SDK_INT >= 19)
            layoutParams.flags |= WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        layoutParams.format = PixelFormat.TRANSPARENT;
        layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_ATTACHED_DIALOG;
        layoutParams.token = this.getWindowToken();
        return layoutParams;
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        getLocationAndFixHeight(mMainRecyclerView, mMainLocation);
    }

    @NonNull
    protected RecyclerView getMain(Context context, AttributeSet parentAttrs) {
        RecyclerView recyclerView = new RecyclerView(context);
        recyclerView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        recyclerView.setLayoutManager(new GridLayoutManager(context, mMainSpanCount));
        RecyclerView.ItemAnimator itemAnimator = new ClassifyItemAnimator();
        itemAnimator.setChangeDuration(CHANGE_DURATION);
        recyclerView.setItemAnimator(itemAnimator);
        return recyclerView;
    }

    @NonNull
    protected RecyclerView getSub(Context context, AttributeSet parentAttrs) {
        RecyclerView recyclerView = new RecyclerView(context);
        recyclerView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        recyclerView.setLayoutManager(new GridLayoutManager(context, mSubSpanCount));
        RecyclerView.ItemAnimator itemAnimator = new ClassifyItemAnimator();
        itemAnimator.setChangeDuration(CHANGE_DURATION);
        recyclerView.setItemAnimator(itemAnimator);
        return recyclerView;
    }


    public RecyclerView getMainRecyclerView() {
        return mMainRecyclerView;
    }

    public RecyclerView getSubRecyclerView() {
        return mSubRecyclerView;
    }

    /**
     * 设置主层级与副层级使用一个RecycledViewPool
     *
     * @param viewPool
     */
    public void setShareViewPool(RecyclerView.RecycledViewPool viewPool) {
        getMainRecyclerView().setRecycledViewPool(viewPool);
        getSubRecyclerView().setRecycledViewPool(viewPool);
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
    }

    /**
     * @param baseSimpleAdapter
     * @see ClassifyView#addDragListener(DragListener)
     */
    public void setAdapter(BaseSimpleAdapter baseSimpleAdapter) {
        setAdapter(baseSimpleAdapter.getMainAdapter(), baseSimpleAdapter.getSubAdapter());
        if (baseSimpleAdapter.isShareViewPool()) {
            setShareViewPool(new RecyclerView.RecycledViewPool());
        }
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
                if (list == null) {
                    mMainCallBack.onItemClick(mMainRecyclerView,position, pressedView);
                    return true;
                } else {
                    mSubCallBack.initData(position, list);
                    showSubContainer(position);
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
                        mRegion = IN_MAIN_REGION;
//                        inMainRegion = true;
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
                        inMergeState = false;
                        break;
                }
                return mSelected != null;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {
                mMainGestureDetector.onTouchEvent(e);
                int action = MotionEventCompat.getActionMasked(e);
                switch (action) {
                    case MotionEvent.ACTION_MOVE:
                        L.d("main move");
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
                mSubCallBack.onItemClick(mSubRecyclerView,position, pressedView);
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
                        if(mState == STATE_DRAG) {
                            L.d("already have item in drag state");
                            return;
                        }
                        mSelectedPosition = position;
                        mSelectedStartX = pressedView.getLeft();
                        mSelectedStartY = pressedView.getTop();
                        mDx = mDy = 0f;
                        int index = MotionEventCompat.findPointerIndex(e, mSubActivePointerId);
                        mInitialTouchX = MotionEventCompat.getX(e, index);
                        mInitialTouchY = MotionEventCompat.getY(e, index);
                        mRegion = IN_SUB_REGION;
                        mSelected = pressedView;
                        restoreDragView();
                        obtainVelocityTracker();
                        float targetX = mInitialTouchX - mSelected.getWidth() / 2 + mSubLocation[0];
                        float targetY = mInitialTouchY - mSelected.getHeight() / 2 + mSubLocation[1];
                        mSubCallBack.setDragPosition(mSelectedPosition,false);
                        mState = STATE_DRAG;
                        mSubCallBack.onDragStart(mSubRecyclerView,mSelectedPosition);
                        for (DragListener listener : mDragListeners) {
                            listener.onDragStart(ClassifyView.this,mSelected,mInitialTouchX, mInitialTouchY, IN_MAIN_REGION);
                        }
                        doStartDragWithAnimation(mSubRecyclerView,mSelected,mSelectedPosition, targetX, targetY, mSubLocation, mSubCallBack);

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
                        L.d("sub intercept action up or cancel");
                        if(mRegion == UNKNOWN_REGION){
                            mPendingRecover = true;
                        }else {
                            doRecoverAnimation();
                        }
                        break;
                }
                return mSelected != null;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {
                if(mSelected == null) return;
                mSubGestureDetector.onTouchEvent(e);
                float x = MotionEventCompat.getX(e, mSubActivePointerId);
                float y = MotionEventCompat.getY(e, mSubActivePointerId);
                float rawX = e.getRawX();
                float rawY = e.getRawY();
                int height = mSelected.getHeight();
                int width = mSelected.getWidth();
                int action = MotionEventCompat.getActionMasked(e);
                switch (action) {
                    case MotionEvent.ACTION_MOVE:
                        if(mRegion == UNKNOWN_REGION) break;
                        if ((mRegion&IN_SUB_REGION) != 0 && (x < 0 || y < 0 || x > mSubContainerWidth || y > mSubContainerHeight)) {
                            //离开次级目录范围
                            if (mSubCallBack.canDragOut(mSelectedPosition)) {
                                mRegion = IN_MAIN_REGION | SUB_REGION_LEAVE_TYPE;
                                hideSubContainer();
                                mSelectedPosition = mMainCallBack.onLeaveSubRegion(mSelectedPosition, mSubCallBack);
                                mMainCallBack.setDragPosition(mSelectedPosition, true);
                                mSubCallBack.setDragPosition(-1, true);
                                mSelectedStartX = mSelectedStartX + mSubLocation[0] - mMainLocation[0];
                                mSelectedStartY = mSelectedStartY + mSubLocation[1] - mMainLocation[1];
                            }
                            break;
                        }
                        if (mVelocityTracker != null)
                            mVelocityTracker.addMovement(e);
                        mDx = x - mInitialTouchX;
                        mDy = y - mInitialTouchY;
                        mDragView.setX(rawX - width / 2 );
                        mDragView.setY(rawY - height / 2 );
                        moveIfNecessary(mSelected);
                        removeCallbacks(mScrollRunnable);
                        mScrollRunnable.run();
                        if (mMoveListenerEnable) {
                            for (DragListener listener : mDragListeners) {
                                listener.onMove(ClassifyView.this, x, y, IN_SUB_REGION);
                            }
                        }
                        break;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        mSubActivePointerId = ACTIVE_POINTER_ID_NONE;
                        L.d("sub action up or cancel");
                        if(mRegion == UNKNOWN_REGION){
                            mPendingRecover = true;
                        }
                        if ((mRegion &IN_SUB_REGION) != 0) {
                            doRecoverAnimation();
                            mState = STATE_SETTLE;
                            for (DragListener listener : mDragListeners) {
                                listener.onDragRelease(ClassifyView.this, x, y, IN_SUB_REGION);
                            }
                        }
                        if ((mRegion & IN_MAIN_REGION)!= 0) {
                            if (inMergeState) {
                                inMergeState = false;
                                if (mInMergeQueue.isEmpty()) break;
                                mLastMergePosition = mInMergeQueue.poll();
                                if (mSelectedPosition == mLastMergePosition) break;
                                MergeInfo mergeInfo = mMainCallBack.onPrepareMerge(mMainRecyclerView, mSelectedPosition, mLastMergePosition);
                                RecyclerView.ViewHolder target = mMainRecyclerView.findViewHolderForAdapterPosition(mLastMergePosition);
                                if (target == null || mergeInfo == null || target.itemView == mSelected) {
                                    mergeSuccess = false;
                                    break;
                                }
                                float scaleX = mergeInfo.scaleX;
                                float scaleY = mergeInfo.scaleY;
                                float targetX = mMainLocation[0] + mergeInfo.targetX;
                                float targetY = mMainLocation[1] + mergeInfo.targetY;
                                setViewPivot(mDragView,LEFT_TOP);
                                L.d("targetX:%1$s,targetY:%2$s,scaleX:%3$s,scaleY:%4$s", targetX, targetY, scaleX, scaleY);
                                mDragView.animate().x(targetX).y(targetY).scaleX(scaleX).scaleY(scaleY).setListener(mMergeAnimListener).setDuration(mAnimationDuration).start();
                                mergeSuccess = true;
                            } else {
                                doRecoverAnimation();
                            }
                            mState = STATE_SETTLE;
                            for (DragListener listener : mDragListeners) {
                                listener.onDragRelease(ClassifyView.this, x, y, IN_MAIN_REGION);
                            }
                        }
                        releaseVelocityTracker();
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


    /**
     * 创建次级目录的弹窗
     * 可以重写该方法修改弹窗的样式 及 动画
     * 注意添加自定义View是无效的
     * 自定义View需要重写{@link #getSubContent()}
     *
     * @return
     */
    protected Dialog createSubDialog() {
        Dialog dialog = new Dialog(getContext(), R.style.ClassifyViewTheme);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
        layoutParams.gravity = Gravity.BOTTOM;
        layoutParams.height = (int) (getHeight() * mSubRatio);
        layoutParams.dimAmount = 0.6f;
        layoutParams.windowAnimations = R.style.DefaultAnimation;
        layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_PANEL;
        layoutParams.flags |= WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS;
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        return dialog;
    }

    /**
     * 找到需要添加sub recyclerView 的容器
     *
     * @param group
     * @return
     */
    protected ViewGroup findHaveSubTagContainer(ViewGroup group) {
        String tag = getContext().getString(R.string.sub_container);
        if (tag.equals(group.getTag())) return group;
        for (int i = 0; i < group.getChildCount(); i++) {
            View child = group.getChildAt(i);
            if (child instanceof ViewGroup) {
                ViewGroup temp = findHaveSubTagContainer((ViewGroup) child);
                if (temp != null) return temp;
            }
        }
        return null;
    }


    public void setDebugAble(boolean debugAble) {
        L.setDebugAble(debugAble);
    }

    /**
     * 返回的布局 可以定义一个tag作为容器被用来添加次级目录的RecyclerView
     * 你可以修改这部分逻辑通过{@link #findHaveSubTagContainer(ViewGroup)}
     *
     * @return 返回次级目录的跟布局
     */
    protected View getSubContent() {
        return inflate(getContext(), R.layout.sub_content, null);
    }

    private Dialog initSubDialog() {
        Dialog dialog = createSubDialog();
        View content = getSubContent();
        if (content instanceof ViewGroup) {
            ViewGroup group = findHaveSubTagContainer((ViewGroup) content);
            if (group == null) {
                group = (ViewGroup) content;
            }
            group.addView(mSubRecyclerView);
        }
        dialog.setContentView(content);
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        int width = params.width;
        int height = params.height;
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int screenHeight = getResources().getDisplayMetrics().widthPixels;
        switch (width) {
            case LayoutParams.MATCH_PARENT:
                width = screenWidth;
                break;
            case LayoutParams.WRAP_CONTENT:
                int childWidth = content.getLayoutParams().width;
                width = getChildMeasureSpec(MeasureSpec.makeMeasureSpec(screenWidth, MeasureSpec.AT_MOST), 0, childWidth);
                break;
            default:
                break;
        }
        switch (height) {
            case LayoutParams.MATCH_PARENT:
                height = screenHeight;
                break;
            case LayoutParams.WRAP_CONTENT:
                int childHeight = content.getLayoutParams().height;
                height = getChildMeasureSpec(MeasureSpec.makeMeasureSpec(screenHeight, MeasureSpec.AT_MOST), 0, childHeight);
                break;
            default:
                break;
        }
        mSubContainerWidth = width;
        mSubContainerHeight = height;
        return dialog;
    }


    /**
     * 显示次级窗口
     */
    private void showSubContainer(final int position) {
        if (mSubDialog == null) {
            mSubDialog = initSubDialog();
            mSubDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialog) {
                    getLocationAndFixHeight(mSubRecyclerView, mSubLocation);
                }
            });
            mSubDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {

                }
            });
            mSubDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    mSubCallBack.onDialogCancel(mSubDialog,position);
                }
            });
        }
        mSubDialog.show();
        mSubCallBack.onDialogShow(mSubDialog,position);
    }

    private void getLocationAndFixHeight(@NonNull View container, @NonNull int[] holder) {
        container.getLocationOnScreen(holder);
        fixHeight(holder);
    }

    private void fixHeight(@NonNull int[] ints) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            ints[1] -= mStatusBarHeight;
        }
    }

    /**
     * 隐藏次级窗口
     */
    public void hideSubContainer() {
        if (mSubDialog == null) return;
        mSubDialog.hide();
    }

    private boolean mergeSuccess = false;

    /**
     * 监听在拖拽的View在主层级移动的逻辑处理
     */
    class MainDragListener implements View.OnDragListener {
        @Override
        public boolean onDrag(View v, DragEvent event) {
            if (mSelected == null) return false;
            int action = event.getAction();
            int width = mSelected.getWidth();
            int height = mSelected.getHeight();
            float x = event.getX();
            float y = event.getY();
//            L.d("Main onDrag X:%1$s,Y:%2$s", x, y);
            float centerX = x - width / 2;
            float centerY = y - height / 2;
            switch (action) {
                case DragEvent.ACTION_DRAG_STARTED:
                    if ((mRegion & IN_MAIN_REGION) != 0) {
                        if(mState == STATE_DRAG){
                            L.d("already have item in drag state");
                            return false;
                        }
                        restoreDragView();
                        obtainVelocityTracker();
                        //拖动开始之前修正位置
                        getLocationAndFixHeight(mMainRecyclerView, mMainLocation);
                        float targetX = mInitialTouchX - width / 2 + mMainLocation[0];
                        float targetY = mInitialTouchY - height / 2 + mMainLocation[1];
                        mMainCallBack.setDragPosition(mSelectedPosition,false);
                        mState = STATE_DRAG;
                        mMainCallBack.onDragStart(mMainRecyclerView,mSelectedPosition);
                        for (DragListener listener : mDragListeners) {
                            listener.onDragStart(ClassifyView.this,mSelected,mInitialTouchX, mInitialTouchY, IN_MAIN_REGION);
                        }
                        doStartDragWithAnimation(mMainRecyclerView,mSelected,mSelectedPosition, targetX, targetY, mMainLocation,  mMainCallBack);
                    }
                    break;
                case DragEvent.ACTION_DRAG_LOCATION:
                    if(mRegion == UNKNOWN_REGION) break;
                    mVelocityTracker.addMovement(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(),
                            MotionEvent.ACTION_MOVE, x, y, 0));
                    mDragView.setX(centerX + mMainLocation[0]);
                    mDragView.setY(centerY + mMainLocation[1]);
                    mDx = x - mInitialTouchX;
                    mDy = y - mInitialTouchY;
                    moveIfNecessary(mSelected);
                    removeCallbacks(mScrollRunnable);
                    mScrollRunnable.run();
                    invalidate();
                    if (mMoveListenerEnable) {
                        for (DragListener listener : mDragListeners) {
                            listener.onMove(ClassifyView.this, x, y, IN_MAIN_REGION);
                        }
                    }
                    break;
                case DragEvent.ACTION_DRAG_ENDED:
                    L.d("main ended");
                    if (mergeSuccess) {
                        mergeSuccess = false;
                        break;
                    }
                    if ((mRegion & IN_MAIN_REGION) != 0) {
                        doRecoverAnimation();
                    }
                    if(mRegion == UNKNOWN_REGION){
                        mPendingRecover = true;
                    }
                    releaseVelocityTracker();
                    break;
                case DragEvent.ACTION_DRAG_EXITED:
                    break;
                case DragEvent.ACTION_DROP:
                    if (inMergeState) {
                        inMergeState = false;
                        if (mInMergeQueue.isEmpty()) break;
                        mLastMergePosition = mInMergeQueue.poll();
                        if (mSelectedPosition == mLastMergePosition) break;
                        MergeInfo mergeInfo = mMainCallBack.onPrepareMerge(mMainRecyclerView, mSelectedPosition, mLastMergePosition);
                        RecyclerView.ViewHolder target = mMainRecyclerView.findViewHolderForAdapterPosition(mLastMergePosition);
                        if (target == null || mergeInfo == null || target.itemView == mSelected) {
                            mergeSuccess = false;
                            break;
                        }
                        float scaleX = mergeInfo.scaleX;
                        float scaleY = mergeInfo.scaleY;
                        float targetX = mMainLocation[0] + mergeInfo.targetX;
                        float targetY = mMainLocation[1] + mergeInfo.targetY;
                        setViewPivot(mDragView,LEFT_TOP);
                        L.d("targetX:%1$s,targetY:%2$s,scaleX:%3$s,scaleY:%4$s", targetX, targetY, scaleX, scaleY);
                        mDragView.animate().x(targetX).y(targetY).scaleX(scaleX).scaleY(scaleY).setListener(mMergeAnimListener).setDuration(mAnimationDuration).start();
                        mergeSuccess = true;
                    }
                    break;
            }
            return true;
        }
    }

    private int mLastMergePosition;
    private AnimatorListenerAdapter mMergeAnimListener = new AnimatorListenerAdapter() {
        @Override
        public void onAnimationStart(Animator animation) {
            mMainCallBack.onStartMergeAnimation(mMainRecyclerView, mSelectedPosition, mLastMergePosition, mAnimationDuration);
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            mMainCallBack.onMerged(mMainRecyclerView, mSelectedPosition, mLastMergePosition);
            restoreToInitial();
        }

        @Override
        public void onAnimationCancel(Animator animation) {
            mMainCallBack.onMerged(mMainRecyclerView, mSelectedPosition, mLastMergePosition);
            restoreToInitial();
        }
    };

    protected Drawable getDragDrawable(View view) {
        return new DragDrawable(view);
    }



    private void doStartDragWithAnimation(final RecyclerView recyclerView,final View selected, final int selectedPosition, final float targetX, final float targetY, @NonNull final int[] fixWindowLocation, final BaseCallBack callBack) {
        final int recordRegion = mRegion;//记录当前位置 动画中让位置信息处于位置区域
        selected.post(new Runnable() {
            @Override
            public void run() {
                mWindowManager.addView(mDragView, mDragLayoutParams);
                mDragViewIsShow = true;
                mDragView.setBackgroundDrawable(getDragDrawable(mSelected));
                mDragView.setX(selected.getLeft() + fixWindowLocation[0]);
                mDragView.setY(selected.getTop() + fixWindowLocation[1]);
                callBack.setDragPosition(selectedPosition, true);
                setViewPivot(mDragView,mGravity);
                mDragView.animate()
                        .x(targetX).y(targetY)
                        .scaleX(mDragScaleX).scaleY(mDragScaleY)
                        .setDuration(200).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationCancel(Animator animation) {
                        mRegion = recordRegion;
                        if(mPendingRecover){
                            mPendingRecover = false;
                            doRecoverAnimation();
                        }
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mRegion = recordRegion;
                        mDragView.setScaleX(mDragScaleX);
                        mDragView.setScaleY(mDragScaleY);
                        if(mPendingRecover){
                            mPendingRecover = false;
                            doRecoverAnimation();
                        }else {
                            callBack.onDragAnimationEnd(recyclerView,selectedPosition);
                            for(DragListener listener:mDragListeners){
                                listener.onDragStartAnimationEnd(ClassifyView.this,selected,mRegion);
                            }
                        }
                    }
                }).setStartDelay(100).start();
                mRegion = UNKNOWN_REGION;
            }
        });
    }


    private void setViewPivot(@NonNull View target,@MyGravity int gravity){
        switch (gravity){
            case CENTER:
                target.setPivotX(target.getWidth()/2);
                target.setPivotY(target.getHeight()/2);
                break;
            case LEFT_TOP:
                target.setPivotX(0);
                target.setPivotY(0);
                break;
            case LEFT_BOTTOM:
                target.setPivotX(0);
                target.setPivotY(target.getHeight());
                break;
            case RIGHT_TOP:
                target.setPivotX(target.getWidth());
                target.setPivotY(0);
                break;
            case RIGHT_BOTTOM:
                target.setPivotX(target.getWidth());
                target.setPivotY(target.getHeight());
                break;
        }
    }



    private float findMaxElevation(RecyclerView recyclerView, View itemView) {
        final int childCount = recyclerView.getChildCount();
        float max = 0;
        for (int i = 0; i < childCount; i++) {
            final View child = recyclerView.getChildAt(i);
            if (child == itemView) {
                continue;
            }
            final float elevation = ViewCompat.getElevation(child);
            if (elevation > max) {
                max = elevation;
            }
        }
        return max;
    }

    /**
     * 做恢复到之前状态的动画
     */
    private void doRecoverAnimation() {

        Animator recoverAnimator = null;
        if ((mRegion & IN_SUB_REGION) != 0) {
            RecyclerView.ViewHolder holder = mSubRecyclerView.findViewHolderForAdapterPosition(mSelectedPosition);
            if (holder == null) {
                PropertyValuesHolder yOffset = PropertyValuesHolder.ofFloat("y", getHeight() + mSelected.getHeight() + mSubLocation[1]);
                PropertyValuesHolder scaleX = PropertyValuesHolder.ofFloat("scaleX",1f);
                PropertyValuesHolder scaleY = PropertyValuesHolder.ofFloat("scaleY",1f);
                recoverAnimator = ObjectAnimator.ofPropertyValuesHolder(mDragView, yOffset,scaleX,scaleY);
            } else {
                PropertyValuesHolder xOffset = PropertyValuesHolder.ofFloat("x", mSubLocation[0] + mSubContainer.getLeft() + holder.itemView.getLeft());
                PropertyValuesHolder yOffset = PropertyValuesHolder.ofFloat("y", mSubLocation[1] + mSubContainer.getTop() + holder.itemView.getTop());
                PropertyValuesHolder scaleX = PropertyValuesHolder.ofFloat("scaleX",1f);
                PropertyValuesHolder scaleY = PropertyValuesHolder.ofFloat("scaleY",1f);
                recoverAnimator = ObjectAnimator.ofPropertyValuesHolder(mDragView, xOffset, yOffset,scaleX,scaleY);
            }
        }

        if ((mRegion & IN_MAIN_REGION) != 0) {
            RecyclerView.ViewHolder holder = mMainRecyclerView.findViewHolderForAdapterPosition(mSelectedPosition);
            if (holder == null) {
                PropertyValuesHolder yOffset = PropertyValuesHolder.ofFloat("y", getHeight() + mSelected.getHeight() + mMainLocation[1]);
                PropertyValuesHolder scaleX = PropertyValuesHolder.ofFloat("scaleX",1f);
                PropertyValuesHolder scaleY = PropertyValuesHolder.ofFloat("scaleY",1f);
                recoverAnimator = ObjectAnimator.ofPropertyValuesHolder(mDragView, yOffset,scaleX,scaleY);
            } else {
                PropertyValuesHolder xOffset = PropertyValuesHolder.ofFloat("x", holder.itemView.getLeft() + mMainLocation[0]);
                PropertyValuesHolder yOffset = PropertyValuesHolder.ofFloat("y", holder.itemView.getTop() + mMainLocation[1]);
                PropertyValuesHolder scaleX = PropertyValuesHolder.ofFloat("scaleX",1f);
                PropertyValuesHolder scaleY = PropertyValuesHolder.ofFloat("scaleY",1f);
                recoverAnimator = ObjectAnimator.ofPropertyValuesHolder(mDragView, xOffset, yOffset,scaleX,scaleY);
            }
        }
        if (recoverAnimator == null) return;
        recoverAnimator.setDuration(mAnimationDuration);
        recoverAnimator.setInterpolator(sDragScrollInterpolator);
        recoverAnimator.addListener(mRecoverAnimatorListener);
        recoverAnimator.start();
    }

    private AnimatorListenerAdapter mRecoverAnimatorListener = new AnimatorListenerAdapter() {
        @Override
        public void onAnimationEnd(Animator animation) {
            for (DragListener listener : mDragListeners) {
                listener.onDragEnd(ClassifyView.this, mRegion);
            }
            restoreToInitial();
        }

        @Override
        public void onAnimationCancel(Animator animation) {
            restoreToInitial();
        }
    };

    private void restoreToInitial() {
        mState = STATE_IDLE;
        mSelected = null;
        mSelectedPosition = -1;
        if ((mRegion & IN_SUB_REGION) != 0) {
            restoreDragViewDelayed(DEFAULT_DELAYED);
            notifyDragCancel(mSubCallBack,mSubRecyclerView);
            mRegion = UNKNOWN_REGION;
        }
        if ((mRegion & IN_MAIN_REGION) != 0) {
            restoreDragViewDelayed(DEFAULT_DELAYED);
            notifyDragCancel(mMainCallBack,mMainRecyclerView);
            mRegion = UNKNOWN_REGION;
        }
    }

    private void notifyDragCancel(BaseCallBack callBack, RecyclerView recyclerView) {
        int oldPosition = callBack.getDragPosition();
        if (oldPosition != -1) {
            RecyclerView.ViewHolder holder = recyclerView.findViewHolderForAdapterPosition(oldPosition);
            if (holder != null) {
                holder.itemView.setVisibility(VISIBLE);
                callBack.setDragPosition(-1, false);
            } else {
                callBack.setDragPosition(-1, true);
            }
        } else {
            callBack.setDragPosition(-1, true);
        }
    }


    private void restoreDragView() {
        L.d("restore drag view:"+mDragView.getLeft()+","+mDragView.getTop()+","+mDragView.getTranslationX()+","+mDragView.getTranslationY());
        mDragView.setScaleX(1f);
        mDragView.setScaleY(1f);
        mDragView.setTranslationX(0f);
        mDragView.setTranslationX(0f);
        if (mDragViewIsShow) {
            mWindowManager.removeViewImmediate(mDragView);
            mDragViewIsShow = false;
        }
    }


    private void restoreDragViewDelayed(long delayed) {
        postDelayed(new Runnable() {
            @Override
            public void run() {
                restoreDragView();
            }
        }, delayed);
    }

    @Override
    public void setPadding(int left, int top, int right, int bottom) {
        super.setPadding(left, top, right, bottom);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void setPaddingRelative(int start, int top, int end, int bottom) {
        super.setPaddingRelative(start, top, end, bottom);
    }

    @Override
    public void setClipToPadding(boolean clipToPadding) {
        super.setClipToPadding(clipToPadding);
    }

    /**
     * If user drags the view to the edge, trigger a scroll if necessary.
     */
    private boolean scrollIfNecessary() {
        RecyclerView recyclerView = null;
        if ((mRegion & IN_MAIN_REGION) != 0) {
            recyclerView = mMainRecyclerView;
        }
        if ((mRegion & IN_SUB_REGION) != 0) {
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
            int curX = (int) (mSelectedStartX + mDx);
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
            int curY = (int) (mSelectedStartY + mDy);
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
                inScrollMode = true;
                if (mSelected != null) { //it might be lost during scrolling
                    moveIfNecessary(mSelected);
                }
                removeCallbacks(mScrollRunnable);
                ViewCompat.postOnAnimation(ClassifyView.this, this);
            } else {
                inScrollMode = false;
            }
        }
    };
    private boolean inMergeState = false;

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
        if ((mRegion & IN_SUB_REGION) != 0) {//次级目录下 没有merge形式
            int targetPosition = mSubRecyclerView.getChildAdapterPosition(target);
            int state = mSubCallBack.getCurrentState(mSelected, target, x, y, mVelocityTracker, mSelectedPosition,
                    targetPosition);
            if (state == MOVE_STATE_MOVE) {
                if (mSubCallBack.onMove(mSelectedPosition, targetPosition)) {
                    mSelectedPosition = targetPosition;
                    RecyclerView.ViewHolder viewHolder = mSubRecyclerView.findViewHolderForAdapterPosition(mSelectedPosition);
                    if (viewHolder != null) mSelected = viewHolder.itemView;
                    mSubCallBack.setDragPosition(targetPosition, true);
                    mSubCallBack.moved(mSelectedPosition, targetPosition);
                }
            }
        }
        if ((mRegion & IN_MAIN_REGION) != 0) {//在主层级下 有merge状况 以及次级目录拖动到主层级的状况
            int targetPosition = mMainRecyclerView.getChildAdapterPosition(target);
            while (!mInMergeQueue.isEmpty() && mInMergeQueue.peek() != targetPosition) {
                int i = mInMergeQueue.poll();
                mMainCallBack.onMergeCancel(mMainRecyclerView, mSelectedPosition, i);
                inMergeState = false;
            }
            int state = mMainCallBack.getCurrentState(mSelected, target, x, y, mVelocityTracker, mSelectedPosition,
                    targetPosition);
            boolean mergeState = state == MOVE_STATE_MERGE;
            if (!inScrollMode && mergeState ^ inMergeState) {
                if (mSelectedPosition == targetPosition) return;
                if (mergeState) {
                    if (mMainCallBack.onMergeStart(mMainRecyclerView, mSelectedPosition, targetPosition)) {
                        inMergeState = true;
                        mInMergeQueue.offer(targetPosition);
                        setViewPivot(mDragView,mGravity);
                        mDragView.animate().scaleX(mDragInMergeScaleX).scaleY(mDragInMergeScaleY).setListener(null).start();
                    }
                } else {
                    if (!mInMergeQueue.isEmpty() && inMergeState) {
                        while (!mInMergeQueue.isEmpty()) {
                            int i = mInMergeQueue.poll();
                            mMainCallBack.onMergeCancel(mMainRecyclerView, mSelectedPosition, i);
                        }
                        inMergeState = false;
                        mDragView.animate().scaleX(mDragScaleX).scaleY(mDragScaleY).start();
                    }
                }
            }
            if (state == MOVE_STATE_MOVE) {
                if (inMergeState && !mInMergeQueue.isEmpty()) {
                    //makeSure trigger mergeCancel
                    while (!mInMergeQueue.isEmpty()) {
                        int i = mInMergeQueue.poll();
                        mMainCallBack.onMergeCancel(mMainRecyclerView, mSelectedPosition, i);
                    }
                    inMergeState = false;
                    mDragView.animate().scaleX(mDragScaleX).scaleY(mDragScaleY).start();
                }
                if (mMainCallBack.onMove(mSelectedPosition, targetPosition)) {
                    mSelectedPosition = targetPosition;
                    RecyclerView.ViewHolder viewHolder = mMainRecyclerView.findViewHolderForAdapterPosition(mSelectedPosition);
                    if (viewHolder != null) mSelected = viewHolder.itemView;
                    mMainCallBack.setDragPosition(targetPosition, true);
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
        if ((mRegion & IN_MAIN_REGION) != 0) {
            lm = getMainLayoutManager();
            recyclerView = mMainRecyclerView;
        }
        if ((mRegion & IN_SUB_REGION) != 0) {
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
            if ((mRegion & IN_MAIN_REGION) != 0) {
                if (!mMainCallBack.canDropOver(mSelectedPosition, targetPosition)) continue;
            }
            if ((mRegion & IN_SUB_REGION) != 0) {
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


    public interface DragListener {
        /**
         * 开始拖拽
         *
         * @param parent parent is ClassifyView
         * @param startX start touch x relative classify view
         * @param startY start touch y relative classify view
         * @param region start drag  region either  main or sub
         */
        void onDragStart(ViewGroup parent,View selectedView, float startX, float startY,@Region int region);

        /**
         * star drag animation end
         * @param parent
         * @param selectedView
         * @param region
         */
        void onDragStartAnimationEnd(ViewGroup parent,View selectedView,int region);

        /**
         * 拖拽结束(recover animation end)
         */
        void onDragEnd(ViewGroup parent,@Region int region);

        /**
         * 释放被拖拽的View
         */
        void onDragRelease(ViewGroup parent, float releaseX, float releaseY,@Region int region);

        /**
         * move callback by touch location
         *
         * @param touchX 触摸的X坐标
         * @param touchY 触摸的Y坐标
         */
        void onMove(ViewGroup parent, float touchX, float touchY,@Region int region);
    }

}