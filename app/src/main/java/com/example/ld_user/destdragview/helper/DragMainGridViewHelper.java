package com.example.ld_user.destdragview.helper;

import android.content.Context;
import com.example.ld_user.destdragview.R;
import com.example.ld_user.destdragview.dialog.SubDialog;
import com.example.ld_user.destdragview.interfaces.SubDialogListener;
import com.example.ld_user.destdragview.model.Bean;

import java.util.List;

/**
 * Created by ld-user on 2017/5/4.
 */
public class DragMainGridViewHelper {
    private SubDialog mSubDialog;
    private Context mContext;


    public DragMainGridViewHelper(Context mContext) {
        this.mContext = mContext;
    }

    /**
     * 显示次级窗口
     */
    public void showSubContainer(List<Bean> b) {

        if (mSubDialog == null) {
            mSubDialog = initSubDialog(b);

        } else {
            mSubDialog.setData(b);
        }
        mSubDialog.show();
    }

    private SubDialog initSubDialog(List<Bean> b) {

        SubDialog dialog = new SubDialog(mContext, R.style.ClassifyViewTheme, b);

        dialog.setSubDialogListener(new SubDialogListener() {
            @Override
            public void removeSubDialogItem(int position) {

            }
        });

        return dialog;
    }

}
