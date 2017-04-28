package com.example.ld_user.destdragview.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.example.ld_user.destdragview.R;
import com.example.ld_user.destdragview.model.Bean;
import com.example.ld_user.destdragview.view.DragGridView.DragGridBaseAdapter;
import com.example.ld_user.destdragview.view.FolderView;

import java.util.List;

/**
 * Created by ld-user on 2017/4/27.
 */

public class MyAdapter extends BaseAdapter implements DragGridBaseAdapter {

    List<List<Bean>> beans;

    Context mContext;

    public int mergePosition = -1;

    public int hidePosition = -1;

    public MyAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public int getCount() {

        return beans != null ? beans.size() : 0;

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

        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.adapter_item, null);
            holder = new ViewHolder();
            holder.folderView = (FolderView) convertView.findViewById(R.id.folder_place_view);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        List<Bean> bean = beans.get(position);

        if (mergePosition == position) {
            holder.folderView.setDisplayMergeStatus(true);
        } else {
            holder.folderView.setDisplayMergeStatus(false);
        }

        if (hidePosition == position) {
            convertView.setVisibility(View.INVISIBLE);
        } else {
            convertView.setVisibility(View.VISIBLE);
        }

        holder.folderView.setData(bean);
        return convertView;
    }

    public class ViewHolder {
        FolderView folderView;
    }

    public void setData(List<List<Bean>> b) {
        this.beans = b;
    }

    @Override
    public void reorderItems(int oldPosition, int newPosition) {

    }

    @Override
    public void setHideItem(int hidePosition) {
        this.hidePosition = hidePosition;
        notifyDataSetChanged();
    }


    @Override
    public boolean isFolder(int position) {
        List<Bean> b = beans.get(position);

        if (b.size() > 0) {
            if (b.size() == 1) {
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    @Override
    public void setDisplayMerge(int Position) {
        mergePosition = Position;
        notifyDataSetChanged();
    }

    @Override
    public void myMotifyDataSetChanged() {
        notifyDataSetChanged();
    }
}
