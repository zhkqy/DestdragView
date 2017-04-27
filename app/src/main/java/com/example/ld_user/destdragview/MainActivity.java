package com.example.ld_user.destdragview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.GridView;

import com.example.ld_user.destdragview.adapter.MyAdapter;
import com.example.ld_user.destdragview.model.Bean;
import com.example.ld_user.destdragview.utils.DataGenerate;
import com.example.ld_user.destdragview.view.DragGridView.DragGridView;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    GridView mGridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mGridView = (GridView) findViewById(R.id.gridView);

        MyAdapter adapter = new MyAdapter(this);

        mGridView.setAdapter(adapter);

        List<List<Bean>> datas = DataGenerate.generateBean();
        adapter.setData(datas);

        adapter.notifyDataSetChanged();

    }
}
