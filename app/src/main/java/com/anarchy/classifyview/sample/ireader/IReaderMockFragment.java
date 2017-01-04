package com.anarchy.classifyview.sample.ireader;

import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.anarchy.classify.ClassifyView;
import com.anarchy.classifyview.R;
import com.anarchy.classifyview.core.BaseFragment;
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
    private IReaderAdapter mAdapter;
    private boolean inEditMode;
    private Random mRandom;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_mock_ireader, container, false);
        mRandom = new Random(System.currentTimeMillis());
        mAdapter = new IReaderAdapter();
        mBinding.classifyView.addDragListener(new ClassifyView.DragListener() {
            @Override
            public void onDragStart(ViewGroup parent, float startX, float startY, int region) {
                if (!inEditMode) {
                    showEditMode();
                    int[] dragPosition = mAdapter.getCurrentDragAdapterPosition();
                    IReaderMockData mockData = mAdapter.getCurrentSingleDragData();
                    if(mockData != null){
                        mockData.setChecked(true);
                        mAdapter.notifyItemChanged(dragPosition[0]);
                        if(dragPosition[1] != -1){
                            mAdapter.getSubAdapter().notifyItemChanged(dragPosition[1]);
                        }
                    }
                }
            }

            @Override
            public void onDragEnd(ViewGroup parent, int region) {

            }

            @Override
            public void onDragRelease(ViewGroup parent, float releaseX, float releaseY, int region) {

            }

            @Override
            public void onMove(ViewGroup parent, float touchX, float touchY, int region) {

            }
        });
        mBinding.classifyView.setAdapter(mAdapter);
        mBinding.classifyView.setDebugAble(true);
        mBinding.textComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideEditMode();
            }
        });
        return mBinding.getRoot();
    }

    private void showEditMode() {
        inEditMode = true;
        mAdapter.setEditMode(true);
        mBinding.toolBar.animate().translationY(mBinding.toolBar.getHeight()).start();
        mBinding.bottomBar.animate().translationY(-mBinding.bottomBar.getHeight()).start();
    }

    private void hideEditMode() {
        inEditMode = false;
        mAdapter.setEditMode(false);
        mBinding.toolBar.animate().translationY(0).start();
        mBinding.bottomBar.animate().translationY(0).start();
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
    public boolean onBackPressed() {
        if (inEditMode) {
            hideEditMode();
            return true;
        }
        return super.onBackPressed();
    }
}
