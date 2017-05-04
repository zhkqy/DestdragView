package com.example.ld_user.destdragview.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.example.ld_user.destdragview.R;
import com.example.ld_user.destdragview.adapter.SubFolderAdapter;
import com.example.ld_user.destdragview.interfaces.DragViewListener;
import com.example.ld_user.destdragview.interfaces.SubDialogListener;
import com.example.ld_user.destdragview.model.Bean;
import com.example.ld_user.destdragview.view.DragGridView.DragGridView;

import java.util.List;

/**
 * Created by Administrator on 2017/4/30.
 */
public class SubDialog extends Dialog implements DragViewListener {

    private Context mContext;
    private DragGridView mSubGridView;
    private SubFolderAdapter subFolderAdapter;
    private List<Bean> data;

    /**
     * dialog的高度比例
     */
    private float mSubHeightRatio = 0.6f;
    private float mSubWidthRatio = 0.8f;

    private int screenWidth;
    private WindowManager mWindowManager;

    SubDialogListener listener;

    public SubDialog(Context context) {
        super(context);
    }

    public SubDialog(Context context, int themeResId, List<Bean> data) {
        super(context, themeResId);
        mContext = context;
        this.data = data;

        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        screenWidth = mWindowManager.getDefaultDisplay().getWidth();
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        setCancelable(true);
        setCanceledOnTouchOutside(true);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View v = View.inflate(mContext, R.layout.dialog_sub_item, null);

        mSubGridView = (DragGridView) v.findViewById(R.id.subGridView);

        mSubGridView.setSubLayer();
        mSubGridView.setMergeSwitch(true);

        subFolderAdapter = new SubFolderAdapter(mContext, mSubGridView);
        mSubGridView.setAdapter(subFolderAdapter);
        mSubGridView.setDragViewListener(this);
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
    public void actionDragExited(int dragPosition) {

        if (listener != null) {
            listener.removeSubDialogItem(dragPosition);
        }
        if (isShowing()) {
            hide();
        }
    }

    public void setSubDialogListener(SubDialogListener listener) {
        this.listener = listener;
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
