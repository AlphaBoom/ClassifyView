package com.anarchy.classifyview;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.helper.ItemTouchHelper;

import com.anarchy.classifyview.core.BaseFragment;
import com.anarchy.classifyview.sample.demonstrate.DemonstrateFragment;
import com.anarchy.classifyview.sample.ireader.IReaderMockFragment;
import com.anarchy.classifyview.sample.layoutmanager.LayoutManagerFragment;
import com.anarchy.classifyview.sample.normal.NormalFragment;
import com.anarchy.classifyview.sample.normalfolder.NormalFolderFragment;
import com.anarchy.classifyview.sample.viewpager.ViewPagerFragment;

/**
 * <p/>
 * Date: 16/6/12 09:40
 * Author: rsshinide38@163.com
 * <p/>
 */
public class ContentActivity extends AppCompatActivity {
    @SuppressWarnings("unchecked")
    private Class<? extends Fragment>[] mClasses = new Class[]{NormalFragment.class,
            DemonstrateFragment.class, ViewPagerFragment.class, LayoutManagerFragment.class,
            NormalFolderFragment.class,IReaderMockFragment.class};//,
    private int position;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);
        position = getIntent().getIntExtra(MainActivity.EXTRA_POSITION, 0);
        try {
            getSupportFragmentManager().beginTransaction().add(R.id.container, mClasses[position].newInstance()).commit();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.container);
        if (!(fragment instanceof BaseFragment && ((BaseFragment) fragment).onBackPressed())) {
            super.onBackPressed();
        }
    }
}
