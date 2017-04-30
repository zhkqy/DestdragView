package com.example.ld_user.destdragview.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.example.ld_user.destdragview.R;
import com.example.ld_user.destdragview.adapter.SubFolderAdapter;
import com.example.ld_user.destdragview.interfaces.SubGridViewListener;
import com.example.ld_user.destdragview.model.Bean;
import com.example.ld_user.destdragview.view.DragGridView.DragDrawable;
import com.example.ld_user.destdragview.view.DragGridView.DragSubGridView;

import java.util.List;

/**
 * Created by Administrator on 2017/4/30.
 */
public class SubDialog extends Dialog implements SubGridViewListener {

    private Context mContext;
    private DragSubGridView mSubGridView;
    private SubFolderAdapter subFolderAdapter;
    private List<Bean> data;

    /**
     * dialog的高度比例
     */
    private float mSubHeightRatio = 0.6f;
    private float mSubWidthRatio = 0.8f;

    private int screenWidth;
    private WindowManager mWindowManager;

    private View mDragView;  //拖动的view

    private WindowManager.LayoutParams mDragLayoutParams;

    private boolean mDragViewIsShow;

    public SubDialog(Context context) {
        super(context);
    }

    public SubDialog(Context context, int themeResId, List<Bean> data) {
        super(context, themeResId);
        mContext = context;
        this.data = data;
        mDragView = new View(getContext());

        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        screenWidth = mWindowManager.getDefaultDisplay().getWidth();
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        setCancelable(true);
        setCanceledOnTouchOutside(true);

        mDragLayoutParams = createDragLayoutParams();
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
        layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_ATTACHED_DIALOG;
        layoutParams.token = getWindow().getDecorView().getWindowToken();
        return layoutParams;
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

    /**
     * 创建拖动的镜像
     *
     * @param downX 按下的点相对父控件的X坐标
     * @param downY 按下的点相对父控件的X坐标
     */
    private void createDragImage(View mSelected, int downX, int downY) {

//        mWindowManager.addView(mDragView, mDragLayoutParams);
//        mDragViewIsShow = true;
//        mDragView.setBackgroundDrawable(getDragDrawable(mSelected));
//        mDragView.setX(selected.getLeft() + fixWindowLocation[0]);
//        mDragView.setY(selected.getTop() + fixWindowLocation[1]);
//        callBack.setDragPosition(selectedPosition, true);
//        setViewPivot(mDragView, mGravity);
    }

    protected Drawable getDragDrawable(View view) {
        return new DragDrawable(view);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View v = View.inflate(mContext, R.layout.dialog_sub_item, null);

        mSubGridView = (DragSubGridView) v.findViewById(R.id.subGridView);

        mSubGridView.setMergeSwitch(true);

        subFolderAdapter = new SubFolderAdapter(mContext, mSubGridView);
        mSubGridView.setAdapter(subFolderAdapter);
        mSubGridView.setSubGridViewListener(this);
        subFolderAdapter.setData(data);

        setContentView(v);
    }

    public void setData(List<Bean> data) {
        this.data = data;
        if (subFolderAdapter != null) {
            subFolderAdapter.setData(data);
            subFolderAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void actionDragExited() {

        if (isShowing()) {
            hide();
        }
    }


    @Override
    public void show() {

        super.show();
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.gravity = Gravity.CENTER;

        layoutParams.height = (int) (screenWidth * mSubWidthRatio);
        layoutParams.width = (int) (screenWidth * mSubWidthRatio);
        layoutParams.dimAmount = 0.6f;

        getWindow().setAttributes(layoutParams);//设置大小
    }
}
