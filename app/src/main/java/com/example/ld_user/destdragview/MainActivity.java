package com.example.ld_user.destdragview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.GridView;

import com.example.ld_user.destdragview.adapter.MyAdapter;
import com.example.ld_user.destdragview.eventbus.PandaEventBusObject;
import com.example.ld_user.destdragview.model.Bean;
import com.example.ld_user.destdragview.utils.DataGenerate;
import com.example.ld_user.destdragview.view.DragGridView.DragGridView;

import java.util.List;

import de.greenrobot.event.EventBus;

public class MainActivity extends AppCompatActivity {

    DragGridView  mGridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EventBus.getDefault().register(this);
        mGridView = (DragGridView ) findViewById(R.id.gridView);

        MyAdapter adapter = new MyAdapter(this,mGridView);
        mGridView.setAdapter(adapter);

        List<List<Bean>> datas = DataGenerate.generateBean();
        adapter.setData(datas);

        adapter.notifyDataSetChanged();
    }


    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    /**
     * 回调
     * @param pandaEventBusObject
     */
    public void onEventMainThread(PandaEventBusObject pandaEventBusObject) {
        if (pandaEventBusObject.getType().equals(PandaEventBusObject.DRAG_GRIDVIEW_TUODONG_MOVE)) {
            MotionEvent ev  = (MotionEvent) pandaEventBusObject.getObj();
            if(mGridView!=null){
                mGridView.onMyTouchEvent(ev);
            }
        }else if(pandaEventBusObject.getType().equals(PandaEventBusObject.DRAG_GRIDVIEW_TUODONG_UP)){
            MotionEvent ev  = (MotionEvent) pandaEventBusObject.getObj();
            if(mGridView!=null){
                mGridView.onMyTouchEvent(ev);
            }
        }

    }



}
