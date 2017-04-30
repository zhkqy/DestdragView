package com.example.ld_user.destdragview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.GridView;

import com.example.ld_user.destdragview.adapter.MyAdapter;
import com.example.ld_user.destdragview.model.Bean;
import com.example.ld_user.destdragview.utils.DataGenerate;
import com.example.ld_user.destdragview.view.DragGridView.DragGridView;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    DragGridView  mGridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       FrameLayout ff = (FrameLayout) findViewById(R.id.ff);
        mGridView = (DragGridView ) findViewById(R.id.gridView);

        MyAdapter adapter = new MyAdapter(this,mGridView);
        mGridView.setAdapter(adapter);

        List<List<Bean>> datas = DataGenerate.generateBean();
        adapter.setData(datas);

        adapter.notifyDataSetChanged();

        ff.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {

                Log.i("SubDilaog", "ffffffffff ff");
                return true;
            }
        });

    }
}
