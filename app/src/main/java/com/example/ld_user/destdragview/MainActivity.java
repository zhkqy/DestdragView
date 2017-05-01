package com.example.ld_user.destdragview;

import android.app.Fragment;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;

import com.example.ld_user.destdragview.adapter.DragPageAdapter;
import com.example.ld_user.destdragview.adapter.MainDragAdapter;
import com.example.ld_user.destdragview.eventbus.PandaEventBusObject;
import com.example.ld_user.destdragview.fragment.BaseDragFragment;
import com.example.ld_user.destdragview.fragment.DragFragment;
import com.example.ld_user.destdragview.model.Bean;
import com.example.ld_user.destdragview.utils.DataGenerate;
import com.example.ld_user.destdragview.view.DragGridView.DragGridView;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

public class MainActivity extends AppCompatActivity {

    private List<List<List<Bean>>> beans = new ArrayList<>();


    private List<BaseDragFragment> fragments = new ArrayList<>();

    ViewPager mViewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        EventBus.getDefault().register(this);
        mViewPager = (ViewPager) findViewById(R.id.viewpager);

        beans.clear();
        fragments.clear();

        for(int x = 0;x<4;x++){
            beans.add(DataGenerate.generateBean());
        }

        for(int x = 0;x<beans.size();x++){
            DragFragment fragment  = new DragFragment();
            fragment.setData(beans.get(x));
            fragments.add(fragment);
        }

        DragPageAdapter dragPageAdapter = new DragPageAdapter(getSupportFragmentManager(),fragments);
        mViewPager.setAdapter(dragPageAdapter);
    }


    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    /**
     * 回调
     *
     * @param pandaEventBusObject
     */
    public void onEventMainThread(PandaEventBusObject pandaEventBusObject) {
//        if (pandaEventBusObject.getType().equals(PandaEventBusObject.SUB_DRAG_GRIDVIEW_TOUCH_EVENT_DOWN)) {
//            long downTime = SystemClock.uptimeMillis();
//            long eventTime = SystemClock.uptimeMillis() + 100;
//            float x = 0.0f;
//            float y = 0.0f;
//            int metaState = 0;
//            MotionEvent motionEvent = MotionEvent.obtain(
//                    downTime,
//                    eventTime,
//                    MotionEvent.ACTION_DOWN,
//                    x,
//                    y,
//                    metaState
//            );
//
//            if (mGridView != null) {
//                mGridView.onSubTouchEvent(motionEvent);
//            }
//        }
//        if (pandaEventBusObject.getType().equals(PandaEventBusObject.SUB_DRAG_GRIDVIEW_TOUCH_EVENT_MOVE)) {
//            MotionEvent ev = (MotionEvent) pandaEventBusObject.getObj();
//            if (mGridView != null) {
//                mGridView.onSubTouchEvent(ev);
//            }
//        } else if (pandaEventBusObject.getType().equals(PandaEventBusObject.SUB_DRAG_GRIDVIEW_TOUCH_EVENT_UP)) {
//            MotionEvent ev = (MotionEvent) pandaEventBusObject.getObj();
//            if (mGridView != null) {
//                mGridView.onSubTouchEvent(ev);
//            }
//        }

    }


}
