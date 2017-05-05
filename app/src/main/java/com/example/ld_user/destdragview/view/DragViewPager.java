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
import com.example.ld_user.destdragview.utils.DisplayUtil;
import com.example.ld_user.destdragview.view.DragGridView.BaseDragGridView;
import com.example.ld_user.destdragview.view.DragGridView.DragGridView;

import de.greenrobot.event.EventBus;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by chenlei on 2017/5/1.
 */

public class DragViewPager extends ViewPager {

    private DragGridView mGridView;

    BaseDragFragment fragment;

    public DragPageAdapter dragPageAdapter;

    /**
     * 需要交换的数据
     */
    public static List<Bean> beans;

    /**
     * 交换的位置
     */
    public static int dragPosition = -1;

    public static int leftDistance;
    public static int rightDistance;

    private Context mContext;

    /**
     * 在哪个层
     */
    public final static String MAIN_LAYER = "main_layer";   //主层
    public final static String SUB_LAYER = "sub_layer";     //子层
    public final static String SUB_ABOVE_MAIN_LAYER = "sub_above_main_layer";   //主层在子层拖拽

    /**
     * 默认主层拖动
     */
    public static String DRAG_LAYER ="" ;

    public static int pagerCurrentItem = 0;

    public static boolean isCanMerge;

    /**
     * 当前页面的所有数据备份一份 切换的时候需要还原
     */
    public static List<List<Bean>> crrentPageAllBeans = new ArrayList<>();


    public DragViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);

        super.onFinishInflate();
        int[] gvLocation = new int[2];
        this.getLocationOnScreen(gvLocation);
        int gvLeft = gvLocation[0];
        int gvRight = gvLeft + this.getWidth();

        int distance = DisplayUtil.dipToPixels(mContext, BaseDragGridView.viewpagerLeftRightDistance);
        leftDistance = gvLeft + distance;
        rightDistance = gvRight - distance;

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

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        Log.i("zzzzz", "viewpager  = " + ev.getRawX());


        switch (ev.getAction()) {
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:

                break;
        }
        return super.dispatchTouchEvent(ev);
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

            int currentIten = getCurrentItem();
            if (currentIten > 0) {
                setCurrentItem(currentIten - 1);
            }

        } else if (pandaEventBusObject.getType().equals(PandaEventBusObject.OVERSTEP_RIGHT_RANGE)) {

            int currentIten = getCurrentItem();
            if (currentIten < dragPageAdapter.getCount() - 1) {
                setCurrentItem(currentIten + 1);
            }
        }
    }

    public void setPagerCurrentItem(int page) {

        dragPosition = -1;

        if (page != pagerCurrentItem) {
            Log.i("lllllll", " fragment.setDatas");

            if(fragment!=null){
                fragment.setDatas(crrentPageAllBeans);
            }
        }
        this.pagerCurrentItem = page;

        fragment = dragPageAdapter.getFragment(pagerCurrentItem);

//        Log.i("lllllll", "page = " + page + "   pagerCurrentItem = " + pagerCurrentItem +
//                "    size =  " +fragment.getDatas().size());
        crrentPageAllBeans.clear();

        List<List<Bean>> list = fragment.getDatas();
        if (list != null && list.size() > 0) {
            for (int x = 0; x < list.size(); x++) {
                crrentPageAllBeans.add(list.get(x));
            }
        }
        mGridView = fragment.getGridView();

        fragment.setDragFragmentListener(new DragFragmentListener() {
            @Override
            public void getGridView(DragGridView gridView) {
                mGridView = gridView;
            }
        });

    }
}
