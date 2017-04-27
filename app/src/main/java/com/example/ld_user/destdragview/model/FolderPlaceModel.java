package com.example.ld_user.destdragview.model;

import android.graphics.Bitmap;

/**
 * Created by ld-user on 2017/4/27.
 */

public class FolderPlaceModel {

    public Bitmap bitmap;
    public int left;
    public int top;

    public FolderPlaceModel(Bitmap bitmap, int left, int top) {
        this.bitmap = bitmap;
        this.left = left;
        this.top = top;
    }


    public FolderPlaceModel() {
    }
}
