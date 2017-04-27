package com.example.ld_user.destdragview.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.example.ld_user.destdragview.R;
import com.example.ld_user.destdragview.model.Bean;

import java.util.List;

/**
 * Created by ld-user on 2017/4/27.
 */

public class FolderView  extends FrameLayout{

    private List<Bean> data;
    public FolderPlaceView folderPlace;


    public FolderView(Context context) {
        super(context);
    }

    public FolderView(Context context, AttributeSet attrs) {
        super(context, attrs);

        inflate(context, R.layout.view_folder,this);
        folderPlace = (FolderPlaceView) findViewById(R.id.folderplace);
    }

    public FolderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }

    public void setData(List<Bean> data) {
        this.data = data;
        folderPlace.setData(data);
    }

}
