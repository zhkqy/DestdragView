package com.example.ld_user.destdragview.view;

import android.content.Context;
import android.os.SystemClock;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import com.example.ld_user.destdragview.adapter.DragPageAdapter;
import com.example.ld_user.destdragview.eventbus.PandaEventBusObject;
import com.example.ld_user.destdragview.fragment.BaseDragFragment;
import com.example.ld_user.destdragview.interfaces.DragFragmentListener;
import com.example.ld_user.destdragview.model.Bean;
import com.example.ld_user.destdragview.view.DragGridView.DragGridView;

import de.greenrobot.event.EventBus;

import java.util.List;

/**
 * Created by chenlei on 2017/5/1.
 */

public class DragViewPager extends ViewPager {

    private DragGridView mGridView;

    BaseDragFragment fragment;

    public DragPageAdapter dragPageAdapter;

    private int pagerCurrentItem;

    public static List<Bean> beans;

    public DragViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void setAdapter(PagerAdapter adapter) {
        super.setAdapter(adapter);
        if (adapter instanceof DragPageAdapter) {
            dragPageAdapter = (DragPageAdapter) adapter;
        } else {
            throw new IllegalStateException("the adapter must be DragPageAdapter");
        }
    }

    /**
     * 回调
     *
     * @param pandaEventBusObject
     */
    public void onEventMainThread(PandaEventBusObject pandaEventBusObject) {
        if (pandaEventBusObject.getType().equals(PandaEventBusObject.SUB_DRAG_GRIDVIEW_TOUCH_EVENT_DOWN)) {

            if (mGridView != null) {
                long downTime = SystemClock.uptimeMillis();
                long eventTime = SystemClock.uptimeMillis() + 100;
                float x = 0.0f;
                float y = 0.0f;
                int metaState = 0;
                MotionEvent motionEvent = MotionEvent.obtain(downTime, eventTime,
                        MotionEvent.ACTION_DOWN, x, y, metaState
                );

                mGridView.onSubTouchEvent(motionEvent);
            }
        }
        if (pandaEventBusObject.getType().equals(PandaEventBusObject.SUB_DRAG_GRIDVIEW_TOUCH_EVENT_MOVE)) {
            MotionEvent ev = (MotionEvent) pandaEventBusObject.getObj();
            if (mGridView != null) {
                mGridView.onSubTouchEvent(ev);
            }
        } else if (pandaEventBusObject.getType().equals(PandaEventBusObject.SUB_DRAG_GRIDVIEW_TOUCH_EVENT_UP)) {
            MotionEvent ev = (MotionEvent) pandaEventBusObject.getObj();
            if (mGridView != null) {
                mGridView.onSubTouchEvent(ev);
            }
        } else if (pandaEventBusObject.getType().equals(PandaEventBusObject.OVERSTEP_LEFT_RANGE)) {

            Log.i("jjjjjj","OVERSTEP_LEFT_RANGE");
            int currentIten = getCurrentItem();
            if (currentIten > 0) {
                setCurrentItem(currentIten - 1);
            }

        } else if (pandaEventBusObject.getType().equals(PandaEventBusObject.OVERSTEP_RIGHT_RANGE)) {

            Log.i("jjjjjj","OVERSTEP_RIGHT_RANGE");

            int currentIten = getCurrentItem();
            if (currentIten < dragPageAdapter.getCount() - 1) {
                setCurrentItem(currentIten + 1);
            }
        }
    }


    public void setPagerCurrentItem(int pagerCurrentItem) {
        this.pagerCurrentItem = pagerCurrentItem;

        fragment = dragPageAdapter.getFragment(pagerCurrentItem);

        mGridView = fragment.getGridView();

        fragment.setDragFragmentListener(new DragFragmentListener() {
            @Override
            public void getGridView(DragGridView gridView) {

                mGridView = gridView;
            }
        });
    }
}
