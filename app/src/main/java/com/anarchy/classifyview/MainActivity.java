package com.anarchy.classifyview;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{
    public static final String EXTRA_POSITION = "com.anarchy.classifyview.MainActivity.EXTRA_POSITION";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ListView sampleList = (ListView) findViewById(R.id.sample_list);
        sampleList.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,getResources().getStringArray(R.array.list_name)));
        sampleList.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent i = new Intent(this,ContentActivity.class);
        i.putExtra(EXTRA_POSITION,position);
        startActivity(i);
    }
}
