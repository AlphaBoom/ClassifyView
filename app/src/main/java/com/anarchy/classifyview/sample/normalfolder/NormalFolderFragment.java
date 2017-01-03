package com.anarchy.classifyview.sample.normalfolder;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.anarchy.classify.ClassifyView;
import com.anarchy.classifyview.sample.normalfolder.bean.BaseBean;
import com.anarchy.classifyview.R;
import com.anarchy.classifyview.sample.normalfolder.logic.MyFolderAdapter;
import com.anarchy.classifyview.utils.DataGenerate;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lizhiming211223 on 2016/12/29.
 */
public class NormalFolderFragment extends Fragment {
    private ClassifyView mClassifyView;
    private List<BaseBean> baseBeans=new ArrayList<>();
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.normal,container,false);
        mClassifyView = (ClassifyView) view.findViewById(R.id.classify_view);
        initDatas();
        mClassifyView.setAdapter(new MyFolderAdapter(baseBeans));
        mClassifyView.setDebugAble(true);
        Constants.IS_FOLDER_ADAPTER=true;
        return view;
    }

    private void initDatas() {
        baseBeans= DataGenerate.generateBaseBean();
    }

    @Override
    public void onDestroyView() {
        Constants.IS_FOLDER_ADAPTER=false;
        super.onDestroyView();
    }
}
