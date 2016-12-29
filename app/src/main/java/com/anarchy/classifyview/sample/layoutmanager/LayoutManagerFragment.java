package com.anarchy.classifyview.sample.layoutmanager;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;

import com.anarchy.classifyview.R;
import com.anarchy.classifyview.core.BaseFragment;
import com.anarchy.classifyview.core.MyAdapter;
import com.anarchy.classifyview.databinding.FragmentLayoutmanagerBinding;
import com.anarchy.classifyview.databinding.StubClassifyHhBinding;
import com.anarchy.classifyview.databinding.StubClassifyHvBinding;
import com.anarchy.classifyview.utils.DataGenerate;

/**
 * User:  Anarchy
 * Email:  rsshinide38@163.com
 * CreateTime: 十二月/25/2016  11:45.
 * Description:
 */

public class LayoutManagerFragment extends BaseFragment {
    private FragmentLayoutmanagerBinding mBinding;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_layoutmanager,container,false);
        mBinding.classifyViewVv.setAdapter(new MyAdapter(DataGenerate.generateBean()));
        return mBinding.getRoot();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_layout_manager_type,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.isChecked()) return false;
        item.setChecked(true);
        if(mBinding.stubClassifyViewHv.isInflated() && mBinding.stubClassifyViewHv.getRoot().getVisibility() == View.VISIBLE){
            mBinding.stubClassifyViewHv.getRoot().setVisibility(View.GONE);
        }
        if(mBinding.stubClassifyViewHh.isInflated() && mBinding.stubClassifyViewHh.getRoot().getVisibility() == View.VISIBLE){
            mBinding.stubClassifyViewHh.getRoot().setVisibility(View.GONE);
        }
        if(mBinding.classifyViewVv.getVisibility() == View.VISIBLE){
            mBinding.classifyViewVv.setVisibility(View.GONE);
        }
        switch (item.getItemId()){
            case R.id.menu_hh:
                if(mBinding.stubClassifyViewHh.isInflated()){
                   StubClassifyHhBinding binding = (StubClassifyHhBinding) mBinding.stubClassifyViewHh.getBinding();
                    binding.classifyViewHh.setVisibility(View.VISIBLE);
                }else {
                    mBinding.stubClassifyViewHh.setOnInflateListener(new ViewStub.OnInflateListener() {
                        @Override
                        public void onInflate(ViewStub stub, View inflated) {
                            StubClassifyHhBinding binding = (StubClassifyHhBinding) mBinding.stubClassifyViewHh.getBinding();
                            binding.classifyViewHh.setAdapter(new HHAdapter(DataGenerate.generateBean()));
                        }
                    });
                    mBinding.stubClassifyViewHh.getViewStub().inflate();
                }
                return true;
            case R.id.menu_hv:
                if(mBinding.stubClassifyViewHv.isInflated()){
                    StubClassifyHvBinding binding = (StubClassifyHvBinding) mBinding.stubClassifyViewHv.getBinding();
                    binding.classifyViewHv.setVisibility(View.VISIBLE);
                }else {
                    mBinding.stubClassifyViewHv.setOnInflateListener(new ViewStub.OnInflateListener() {
                        @Override
                        public void onInflate(ViewStub stub, View inflated) {
                            StubClassifyHvBinding binding = (StubClassifyHvBinding) mBinding.stubClassifyViewHv.getBinding();
                            binding.classifyViewHv.setAdapter(new HVAdapter(DataGenerate.generateBean()));
                        }
                    });
                    mBinding.stubClassifyViewHv.getViewStub().inflate();
                }
                return true;
            case R.id.menu_vv:
                mBinding.classifyViewVv.setVisibility(View.VISIBLE);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
