package com.example.ld_user.destdragview.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;

import com.example.ld_user.destdragview.R;
import com.example.ld_user.destdragview.model.Bean;
import com.example.ld_user.destdragview.view.DragGridView.DragGridBaseAdapter;
import com.example.ld_user.destdragview.view.FolderGridview;
import com.example.ld_user.destdragview.view.FolderPlaceView;
import com.example.ld_user.destdragview.view.FolderView;

import java.util.List;

/**
 * Created by ld-user on 2017/4/27.
 */

public class MyAdapter extends BaseAdapter implements DragGridBaseAdapter {

    List<List<Bean>> beans;

    Context mContext;

    public  MyAdapter (Context mContext){
        this.mContext = mContext;
    }

    @Override
    public int getCount() {

        return beans!=null?beans.size():0;

    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;

        if(convertView==null){
            convertView =  View.inflate(mContext, R.layout.adapter_item,null);
            holder = new ViewHolder();
            holder.folderView = (FolderPlaceView) convertView.findViewById(R.id.folder_place_view);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        List<Bean> bean  = beans.get(position);
        holder.folderView.setData(bean);

        return convertView;
    }

    public class ViewHolder{
        FolderPlaceView folderView;
    }

    public void setData(List<List<Bean>> b){
        this.beans = b;
    }

    @Override
    public void reorderItems(int oldPosition, int newPosition) {

    }

    @Override
    public void setHideItem(int hidePosition) {

    }
}
