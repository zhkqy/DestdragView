package com.example.ld_user.destdragview.helper;

import android.content.Context;
import com.example.ld_user.destdragview.R;
import com.example.ld_user.destdragview.adapter.DragGridBaseAdapter;
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
    public void showSubContainer(int mainPosition, List<Bean> b, DragGridBaseAdapter adapter) {

        if (mSubDialog == null) {
            mSubDialog = initSubDialog(mainPosition, b, adapter);

        } else {
            mSubDialog.setData(b);
        }
        mSubDialog.show();
    }

    private SubDialog initSubDialog(final int mainPosition, List<Bean> b, final DragGridBaseAdapter adapter) {

        SubDialog dialog = new SubDialog(mContext, R.style.ClassifyViewTheme, b);

        dialog.setSubDialogListener(new SubDialogListener() {
            @Override
            public void removeSubDialogItem(int subPosition) {

                adapter.removeSubDialogMiddleData(mainPosition, subPosition);
            }
        });

        return dialog;
    }

}
