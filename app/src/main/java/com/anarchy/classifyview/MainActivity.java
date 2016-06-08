package com.anarchy.classifyview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.anarchy.classify.ClassifyView;
import com.anarchy.classify.adapter.BaseMainAdapter;
import com.anarchy.classify.adapter.BaseSubAdapter;
import com.anarchy.classify.adapter.SubAdapterReference;
import com.anarchy.classify.util.L;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ClassifyView classifyView = (ClassifyView) findViewById(R.id.classify_view);
        List<List<Bean>> data = new ArrayList<>();
        for(int i=0;i<30;i++){
            List<Bean> inner = new ArrayList<>();
            if(i>10) {
                int c = (int) (Math.random() * 15+1);
                for(int j=0;j<c;j++){
                    inner.add(new Bean());
                }
            }else {
                inner.add(new Bean());
            }
            data.add(inner);
        }
        classifyView.setAdapter(new MyAdapter(data));
    }

}
