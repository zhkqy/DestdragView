package com.example.ld_user.destdragview.view.DragGridView;

import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.view.WindowManager;
import android.widget.GridView;

import com.example.ld_user.destdragview.eventbus.PandaEventBusObject;
import com.example.ld_user.destdragview.interfaces.DragViewListener;
import com.example.ld_user.destdragview.utils.Utils;

/**
 * Created by Administrator on 2017/4/30.
 */
public class BaseDragGridView extends GridView {

    protected   View mDragView;  //拖动的view
    private  WindowManager.LayoutParams mDragLayoutParams;
    private boolean mDragViewIsShow;
    private WindowManager mWindowManager;

    private static final long DEFAULT_DELAYED = 10;

    public int itemDelayTime = 350;
    /**
     * DragGridView的item长按响应的时间， 默认是1000毫秒，也可以自行设置
     */
    protected long dragResponseMS = 600;

    protected boolean isItemOverstepGridView ;  //item是否超出了gridview

    protected  DragViewListener dragViewListener;


    protected  PandaEventBusObject eventBusObject;

    /**
     * 状态栏的高度
     */
    private int mStatusHeight;

    public int[] Location = new int[2];

    public BaseDragGridView(Context context) {
        super(context, null);
    }

    public BaseDragGridView(Context context, AttributeSet attrs) {

        super(context, attrs, -1);
    }

    public BaseDragGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        eventBusObject = new PandaEventBusObject();
        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        mStatusHeight = Utils.getStatusHeight(context); //获取状态栏的高度
        mDragLayoutParams = createDragLayoutParams();
        mDragView = new View(getContext());

    }

    private Drawable getDragDrawable(View view) {
        return new DragDrawable(view);
    }

    /**
     * 创建拖动的镜像
     */
    protected void createDragImage(View mSelected, int[] fixWindowLocation) {
        restoreDragView();
        mWindowManager.addView(mDragView, mDragLayoutParams);
        mDragViewIsShow = true;
        mDragView.setBackgroundDrawable(getDragDrawable(mSelected));
        mDragView.setX(mSelected.getLeft() + fixWindowLocation[0]);
        mDragView.setY(mSelected.getTop() + fixWindowLocation[1]);
//        callBack.setDragPosition(selectedPosition, true);
//        setViewPivot(mDragView, mGravity);
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

    protected void getLocationAndFixHeight(@NonNull View container, @NonNull int[] holder) {
        container.getLocationOnScreen(holder);
        fixHeight(holder);
    }

    private void fixHeight(@NonNull int[] ints) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            ints[1] -= mStatusHeight;
        }
    }

    private void restoreDragViewDelayed(long delayed) {
        postDelayed(new Runnable() {
            @Override
            public void run() {
                restoreDragView();
            }
        }, delayed);
    }

    private void restoreDragView() {
//        L.d("restore drag view:"+mDragView.getLeft()+","+mDragView.getTop()+","+mDragView.getTranslationX()+","+mDragView.getTranslationY());
        mDragView.setScaleX(1f);
        mDragView.setScaleY(1f);
        mDragView.setTranslationX(0f);
        mDragView.setTranslationX(0f);
        if (mDragViewIsShow) {
            mWindowManager.removeViewImmediate(mDragView);
            mDragViewIsShow = false;
        }
    }

    protected void restoreToInitial() {
            restoreDragViewDelayed(DEFAULT_DELAYED);
    }

    public void setDragViewListener(DragViewListener dragViewListener) {
        this.dragViewListener = dragViewListener;
    }


}
