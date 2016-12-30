package com.anarchy.classifyview.sample.ireader.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.IntDef;
import android.support.v7.widget.GridLayout;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.anarchy.classify.simple.ChangeInfo;
import com.anarchy.classify.simple.FolderAdapter;
import com.anarchy.classify.simple.PrimitiveSimpleAdapter;
import com.anarchy.classify.simple.SimpleAdapter;
import com.anarchy.classify.simple.widget.CanMergeView;
import com.anarchy.classifyview.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

/**
 * User:  Anarchy
 * Email:  rsshinide38@163.com
 * Date: 16/12/27 14:44
 * Description:
 */
public class IReaderFolder extends RelativeLayout implements CanMergeView {
    /**
     * 根据子view数量是否显示older
     */
    public static final int STATE_AUTO = 0;
    /**
     * 永久显示Folder
     */
    public static final int STATE_FOLDER = 1;

    @IntDef({STATE_AUTO,STATE_FOLDER})
    @Retention(RetentionPolicy.SOURCE)
    @interface State{

    }
    private static final int FOLDER_ID = R.id.i_reader_folder_bg;
    private static final int TAG_ID = R.id.i_reader_folder_tag;
    private static final int CONTAINER_GRID_ID = R.id.i_reader_folder_grid;
    private static final int CHECK_BOX_ID = R.id.i_reader_folder_check_box;
    private static final int CONTENT_ID = R.id.i_reader_folder_content;
    private SimpleAdapter mSimpleAdapter;
    private GridLayout mGridLayout;
    private FrameLayout mContent;
    private TextView mTagView;
    private View mFolderBg;
    private CheckBox mCheckBox;
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
        switch (mState){
            case STATE_AUTO:
                mFolderBg.setVisibility(getChildCount()>1?View.VISIBLE:View.GONE);
                break;
            case STATE_FOLDER:
                mFolderBg.setVisibility(View.VISIBLE);
                break;
        }
    }

    /**
     * 设置显示状态
     * @param state
     */
    public void setState(@State int state){
        mState = state;
    }

    /**
     * 设置是否选中
     * @param check
     */
    public void setCheckBoxCheck(boolean check){
        mCheckBox.setChecked(check);
    }

    /**
     * 判断是否为选中
     * @return
     */
    public boolean isCheckBoxCheck(){
        return mCheckBox.isChecked();
    }


    /**
     * 进入merge状态
     */
    @Override
    public void onMergeStart() {
        mFolderBg.setVisibility(View.VISIBLE);
        mFolderBg.animate().scaleX(1.2f).scaleY(1.2f).setDuration(200).start();
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
                switch (mState){
                    case STATE_AUTO:
                        if(getChildCount() <= 1){
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
    public void startMergeAnimation(int duration) {

    }

    /**
     * 准备merge
     *
     * @return 返回新添加的view 应该放置在布局中的位置坐标
     */
    @Override
    public ChangeInfo prepareMerge() {
        return null;
    }

    /**
     * 设置适配器
     *
     * @param primitiveSimpleAdapter
     */
    @Override
    public void setAdapter(PrimitiveSimpleAdapter primitiveSimpleAdapter) {

    }

    @Override
    public void setAdapter(FolderAdapter folderAdapter) {

    }

    /**
     * 初始化或更新主层级
     *
     * @param parentIndex
     * @param requestCount 需要显示里面有几个子view
     */
    @Override
    public void initOrUpdateMain(int parentIndex, int requestCount) {

    }

    @Override
    public void initOrUpdateMain(int parentIndex, List list) {

    }


    /**
     * 初始化或更新次级层级
     *
     * @param parentIndex
     * @param subIndex
     */
    @Override
    public void initOrUpdateSub(int parentIndex, int subIndex) {
        View view  = mSimpleAdapter.getView(this,mContent.getChildAt(0),parentIndex,subIndex);
        if(view != null && view != mContent.getChildAt(0)){
            mContent.removeAllViews();
            mContent.addView(view);
        }
    }

    @Override
    public int getOutlinePadding() {
        return 0;
    }



    private void ensureViewFound(){
        if(mFolderBg == null | mContent == null | mTagView == null | mGridLayout == null | mCheckBox == null) {
            mFolderBg = findViewById(FOLDER_ID);
            mFolderBg.setPivotX(mFolderBg.getWidth() / 2);
            mFolderBg.setPivotY(mFolderBg.getHeight() / 2);
            mContent = (FrameLayout) findViewById(CONTENT_ID);
            mTagView = (TextView) findViewById(TAG_ID);
            mGridLayout = (GridLayout) findViewById(CONTAINER_GRID_ID);
            mCheckBox = (CheckBox) findViewById(CHECK_BOX_ID);
        }
    }
}
