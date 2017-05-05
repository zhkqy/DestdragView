package com.example.ld_user.destdragview.view;

import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import android.view.View;
import android.view.WindowManager;

import com.example.ld_user.destdragview.adapter.DragPageAdapter;
import com.example.ld_user.destdragview.adapter.MainDragAdapter;
import com.example.ld_user.destdragview.eventbus.PandaEventBusObject;
import com.example.ld_user.destdragview.fragment.BaseDragFragment;
import com.example.ld_user.destdragview.interfaces.DragFragmentListener;
import com.example.ld_user.destdragview.model.Bean;
import com.example.ld_user.destdragview.model.DragModel;
import com.example.ld_user.destdragview.utils.DisplayUtil;
import com.example.ld_user.destdragview.view.DragGridView.BaseDragGridView;
import com.example.ld_user.destdragview.view.DragGridView.DragDrawable;
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

    public static int pagerCurrentItem = 0;

    public static boolean isCanMerge;

    public static View mainDragView;  //拖动的view

    private WindowManager mWindowManager;

    private WindowManager.LayoutParams mDragLayoutParams;

    boolean mDragViewIsShow;
    /**
     * 当前页面的所有数据备份一份 切换的时候需要还原
     */
    public static List<List<Bean>> crrentPageAllBeans = new ArrayList<>();

    public int itemWidth;
    public int itemHeight;

    public static boolean isOpenDragSwitch;

    public DragViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        mainDragView = new View(getContext());
        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        mDragLayoutParams = createDragLayoutParams();
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

    /**
     * 创建拖动的镜像
     */
    protected void createDragImage(View mSelected, int[] fixWindowLocation) {
        restoreDragView();
        mWindowManager.addView(mainDragView, mDragLayoutParams);
        mDragViewIsShow = true;
        mainDragView.setBackgroundDrawable(getDragDrawable(mSelected));
        mainDragView.setX(mSelected.getLeft() + fixWindowLocation[0]);
        mainDragView.setY(mSelected.getTop() + fixWindowLocation[1]);
    }

    /**
     * 生成拖拽view的布局参数
     *
     * @return
     */
    @NonNull
    protected WindowManager.LayoutParams createDragLayoutParams() {
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
        if (Build.VERSION.SDK_INT >= 19)
            layoutParams.flags |= WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        layoutParams.format = PixelFormat.TRANSPARENT;
//        layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_ATTACHED_DIALOG;
        layoutParams.token = this.getWindowToken();
        return layoutParams;
    }

    private void restoreDragView() {
//        L.d("restore drag view:"+mDragView.getLeft()+","+mDragView.getTop()+","+mDragView.getTranslationX()+","+mDragView.getTranslationY());
        mainDragView.setScaleX(1f);
        mainDragView.setScaleY(1f);
        mainDragView.setTranslationX(0f);
        mainDragView.setTranslationX(0f);
        if (mDragViewIsShow) {
            mWindowManager.removeViewImmediate(mainDragView);
            mDragViewIsShow = false;
        }
    }

    private Drawable getDragDrawable(View view) {
        return new DragDrawable(view);
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
            case MotionEvent.ACTION_MOVE:
                if (isOpenDragSwitch) {

                    mainDragView.setX(ev.getRawX() - itemWidth / 2);
                    mainDragView.setY(ev.getRawY() - itemHeight / 2);
                    if (mGridView != null) {
                        mGridView.onSubTouchEvent(ev);
                    }

                    return  false;
                }

                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:

                Log.i("ccvvccvv", "133333ACTION_UP  = " + isOpenDragSwitch);
                if (isOpenDragSwitch) {
                    restoreDragView();

                    if (mGridView != null) {
                        mGridView.onSubTouchEvent(ev);
                    }
                }
                isOpenDragSwitch = false;
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
        } else if (pandaEventBusObject.getType().equals(PandaEventBusObject.DRAG_ITEM_LONG_CLICK)) {

            DragModel model = (DragModel) pandaEventBusObject.getObj();

            this.itemWidth = model.itemWidth;
            this.itemHeight = model.itemHeight;

            createDragImage(model.dragView, model.Location);

            isOpenDragSwitch = true;
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
    }

    public void setPagerCurrentItem(int page) {
        dragPosition = -1;
        MainDragAdapter.hidePosition = -1;
        MainDragAdapter.mergePosition = -1;

        if (page != pagerCurrentItem) {
            Log.i("lllllll", " fragment.setDatas");

            if (fragment != null) {
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
