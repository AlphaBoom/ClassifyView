package com.anarchy.classifyview.sample.ireader;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;

import com.anarchy.classify.ClassifyView;
import com.anarchy.classifyview.R;
import com.anarchy.classifyview.core.BaseFragment;
import com.anarchy.classifyview.databinding.ExtraIreaderBottomBarBinding;
import com.anarchy.classifyview.databinding.FragmentMockIreaderBinding;
import com.anarchy.classifyview.sample.ireader.model.IReaderMockData;

import java.util.Random;

/**
 * User:  Anarchy
 * Email:  rsshinide38@163.com
 * Date: 16/12/26 15:01
 * Description:
 */
public class IReaderMockFragment extends BaseFragment {
    private FragmentMockIreaderBinding mBinding;
    private ExtraIreaderBottomBarBinding mBottomBinding;
    private IReaderAdapter mAdapter;
    private Random mRandom;
    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mLayoutParams;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setRetainInstance(true);
        mWindowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        mLayoutParams = new WindowManager.LayoutParams();
        mLayoutParams.format = PixelFormat.TRANSPARENT;
        mLayoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_SUB_PANEL;
        mLayoutParams.token = getActivity().getWindow().getDecorView().getWindowToken();
        mLayoutParams.gravity = Gravity.BOTTOM;
        mLayoutParams.flags |= WindowManager.LayoutParams.FLAG_SPLIT_TOUCH|WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        mLayoutParams.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE;
        mLayoutParams.width = -1;
        mLayoutParams.height = -2;
    }



    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_mock_ireader, container, false);
        mBottomBinding = DataBindingUtil.inflate(inflater, R.layout.extra_ireader_bottom_bar, null, false);
        mRandom = new Random(System.currentTimeMillis());
        mAdapter = new IReaderAdapter();
        mAdapter.registerObserver(new IReaderAdapter.IReaderObserver() {
            int count = 0;




            @Override
            public void onChecked(boolean isChecked) {
                count += isChecked ? 1 : -1;
                if (count <= 0) {
                    count = 0;
                    mBottomBinding.icDeleteBadge.setVisibility(View.INVISIBLE);
                    setBottomEnable(false);
                } else {
                    if (mBottomBinding.icDeleteBadge.getVisibility() == View.INVISIBLE) {
                        mBottomBinding.icDeleteBadge.setVisibility(View.VISIBLE);
                    }
                    mBottomBinding.icDeleteBadge.setText(String.valueOf(count));
                    setBottomEnable(true);
                }
            }

            @Override
            public void onEditChanged(boolean inEdit) {
                if(inEdit){
                    showEditMode();
                }else {
                    hideEditMode();
                }
            }

            @Override
            public void onRestore() {
                count = 0;
                mBottomBinding.icDeleteBadge.setVisibility(View.INVISIBLE);
                setBottomEnable(false);
            }

            @Override
            public void onHideSubDialog() {
                mBinding.classifyView.hideSubContainer();
            }
        });
        mBinding.classifyView.setAdapter(mAdapter);
        mBinding.classifyView.setDebugAble(true);
        mBinding.textComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAdapter.setEditMode(false);
            }
        });
        mBottomBinding.containerDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAdapter.removeAllCheckedBook();
            }
        });
        final float density = getResources().getDisplayMetrics().density;
        mBinding.getRoot().post(new Runnable() {
            @Override
            public void run() {
                mBottomBinding.getRoot().setTranslationY(55*density);
                mWindowManager.addView(mBottomBinding.getRoot(),mLayoutParams);
            }
        });
        mBinding.toolBar.setTranslationY(-60*density);
        return mBinding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void setBottomEnable(boolean enable) {
        mBottomBinding.containerDelete.setEnabled(enable);
        mBottomBinding.containerMove.setEnabled(enable);
        mBottomBinding.containerShare.setEnabled(enable);
        mBottomBinding.containerOrder.setEnabled(enable);
        mBottomBinding.containerDetail.setEnabled(enable);
    }


    private void showEditMode() {
        mBinding.toolBar.animate().translationY(0).start();
        mBottomBinding.getRoot().animate().translationY(0).start();
    }

    private void hideEditMode() {
        mBinding.toolBar.animate().translationY(-mBinding.toolBar.getHeight()).start();
        mBottomBinding.getRoot().animate().translationY(mBottomBinding.getRoot().getHeight()).start();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_i_reader, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_add) {
            mAdapter.addBook(generateRandomMockData());
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private IReaderMockData generateRandomMockData() {
        IReaderMockData mockData = new IReaderMockData();
        mockData.setColor(Color.rgb(mRandom.nextInt(256), mRandom.nextInt(256), mRandom.nextInt(256)));
        return mockData;
    }

    @Override
    public void onDestroyView() {
        mWindowManager.removeViewImmediate(mBottomBinding.getRoot());
        super.onDestroyView();
    }

    @Override
    public boolean onBackPressed() {
        if (mAdapter.isEditMode()) {
            mAdapter.setEditMode(false);
            return true;
        }
        return super.onBackPressed();
    }
}
