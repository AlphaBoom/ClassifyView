package com.anarchy.classifyview.sample.ireader.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.LayoutTransition;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.IntDef;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.anarchy.classify.simple.ChangeInfo;
import com.anarchy.classify.simple.PrimitiveSimpleAdapter;
import com.anarchy.classify.simple.widget.CanMergeView;
import com.anarchy.classify.util.L;
import com.anarchy.classifyview.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * User:  Anarchy
 * Email:  rsshinide38@163.com
 * Date: 16/12/27 14:44
 * Description:
 */
public class IReaderFolder extends RelativeLayout implements CanMergeView {

    public static final int STATE_AUTO = 0;
    /**
     * 永久显示Folder
     */
    public static final int STATE_FOLDER = 1;

    @IntDef({STATE_AUTO, STATE_FOLDER})
    @Retention(RetentionPolicy.SOURCE)
    @interface State {

    }

    private static final int FOLDER_ID = R.id.i_reader_folder_bg;
    private static final int TAG_ID = R.id.i_reader_folder_tag;
    private static final int CONTAINER_GRID_ID = R.id.i_reader_folder_grid;
    private static final int CHECK_BOX_ID = R.id.i_reader_folder_check_box;
    private static final int CONTENT_ID = R.id.i_reader_folder_content;
    private PrimitiveSimpleAdapter mSimpleAdapter;
    private IReaderGridLayout mGridLayout;
    private FrameLayout mContent;
    private TextView mTagView;
    private View mFolderBg;
    private int mState = STATE_AUTO;

    public IReaderFolder(Context context) {
        super(context);
    }

    public IReaderFolder(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public IReaderFolder(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public IReaderFolder(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        ensureViewFound();
        switch (mState) {
            case STATE_AUTO:
                mFolderBg.setVisibility(getChildCount() > 1 ? View.VISIBLE : View.GONE);
                break;
            case STATE_FOLDER:
                mFolderBg.setVisibility(View.VISIBLE);
                break;
        }
    }

    /**
     * 设置显示状态
     *
     * @param state
     */
    public void setState(@State int state) {
        mState = state;
    }


    /**
     * 进入merge状态
     */
    @Override
    public void onMergeStart() {
        mFolderBg.setVisibility(View.VISIBLE);
        mFolderBg.setPivotX(mFolderBg.getWidth() / 2);
        mFolderBg.setPivotY(mFolderBg.getHeight() / 2);
        mFolderBg.animate().scaleX(1.2f).scaleY(1.1f).setDuration(200).start();
    }

    /**
     * 离开merge状态
     */
    @Override
    public void onMergeCancel() {
        mFolderBg.animate().scaleX(1f).scaleY(1f).setDuration(200).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mFolderBg.animate().setListener(null);
                switch (mState) {
                    case STATE_AUTO:
                        if (getChildCount() <= 1) {
                            mFolderBg.setVisibility(View.GONE);
                        }
                        break;
                    case STATE_FOLDER:
                        //nope
                        break;
                }
            }
        });
    }


    /**
     * 结束merge事件
     */
    @Override
    public void onMerged() {
        mFolderBg.animate().scaleX(1f).scaleY(1f).setDuration(200).start();
    }

    /**
     * 开始merge动画
     *
     * @param duration 动画持续时间
     */
    @Override
    public void startMergeAnimation(final int duration) {
        if(mContent.getVisibility() == View.VISIBLE){
            ChangeInfo info = mGridLayout.getSecondItemChangeInfo();
            float scaleX = info.targetWidth/mContent.getWidth();
            float scaleY = info.targetHeight/mContent.getHeight();
            mContent.setPivotX(0);
            mContent.setPivotY(0);
            mContent.animate()
                    .scaleX(scaleX).scaleY(scaleY)
                    .translationX(info.targetLeft).translationY(info.targetTop)
                    .setDuration(duration)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationCancel(Animator animation) {
                            restoreViewDelayed(mContent,duration);
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            restoreViewDelayed(mContent,duration);
                        }
                    })
                    .start();
        }else {
            final View dummyView = new View(getContext());
            mGridLayout.getLayoutTransition().setDuration(duration);
            mGridLayout.getLayoutTransition().addTransitionListener(new LayoutTransition.TransitionListener() {
                @Override
                public void startTransition(LayoutTransition transition, ViewGroup container, View view, int transitionType) {

                }

                @Override
                public void endTransition(LayoutTransition transition, ViewGroup container, View view, int transitionType) {
                    mGridLayout.removeView(dummyView);
                }
            });
            mGridLayout.addView(dummyView,0);
        }
    }


    private void restoreViewDelayed(final View view,int delayTime){
        postDelayed(new Runnable() {
            @Override
            public void run() {
                view.setScaleX(1f);
                view.setScaleY(1f);
                view.setTranslationX(0f);
                view.setTranslationY(0f);
            }
        },delayTime);
    }

    /**
     * 准备merge
     *
     * @return 返回新添加的view 应该放置在布局中的位置坐标
     */
    @Override
    public ChangeInfo prepareMerge() {
        ChangeInfo changeInfo = mGridLayout.getChangeInfo();
        int left = getLeft();
        int top = getTop();
        //修正数值
        changeInfo.targetLeft += left + mGridLayout.getLeft();
        changeInfo.targetTop += top + mGridLayout.getTop();
        changeInfo.sourceLeft = left + mContent.getLeft();
        changeInfo.sourceTop = top + mContent.getTop();
        changeInfo.sourceWidth = mContent.getWidth();
        changeInfo.sourceHeight = mContent.getHeight();
        return changeInfo;
    }

    /**
     * 设置适配器
     *
     * @param primitiveSimpleAdapter
     */
    @Override
    public void setAdapter(PrimitiveSimpleAdapter primitiveSimpleAdapter) {
        mSimpleAdapter = primitiveSimpleAdapter;
    }


    /**
     * 初始化或更新主层级
     *
     * @param parentIndex
     * @param requestCount 需要显示里面有几个子view
     */
    @Override
    public void initOrUpdateMain(int parentIndex, int requestCount) {
        if(mGridLayout == null){
            ensureViewFound();
        }
        if (mGridLayout == null || requestCount <= 0) return;
        int childCount = mGridLayout.getChildCount();
        if (childCount > requestCount) {
            mGridLayout.removeViews(requestCount,childCount - requestCount);
        }
        childCount = mGridLayout.getChildCount();
        for (int i = 0; i < requestCount; i++) {
            View convertView = null;
            if (i < childCount) {
                convertView = mGridLayout.getChildAt(i);
            }
            View adapterChild = mSimpleAdapter.getView(this, convertView, parentIndex, i);
            if (adapterChild == null) continue;
            if (adapterChild == convertView) {
                //nope
            } else if (i < childCount) {
                mGridLayout.removeViewAt(i);
                mGridLayout.addView(adapterChild, i);
            } else {
                mGridLayout.addView(adapterChild, i);
            }
        }
    }


    /**
     * 初始化或更新次级层级
     *
     * @param parentIndex
     * @param subIndex
     */
    @Override
    public void initOrUpdateSub(int parentIndex, int subIndex) {
        //nope
    }




    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    private void ensureViewFound() {
        if (mFolderBg == null | mContent == null | mTagView == null | mGridLayout == null) {
            mFolderBg = findViewById(FOLDER_ID);
            mContent = (FrameLayout) findViewById(CONTENT_ID);
            mTagView = (TextView) findViewById(TAG_ID);
            mGridLayout = (IReaderGridLayout) findViewById(CONTAINER_GRID_ID);
            mGridLayout.setLayoutTransition(new LayoutTransition());
        }
    }
}
