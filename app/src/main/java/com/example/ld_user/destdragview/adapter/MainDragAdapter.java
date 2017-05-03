package com.example.ld_user.destdragview.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.example.ld_user.destdragview.R;
import com.example.ld_user.destdragview.model.Bean;
import com.example.ld_user.destdragview.view.DragGridView.DragGridBaseAdapter;
import com.example.ld_user.destdragview.view.FolderView;

import java.util.List;

/**
 * Created by ld-user on 2017/4/27.
 */

public class MainDragAdapter extends BaseAdapter implements DragGridBaseAdapter {

    List<List<Bean>> beans;

    Context mContext;

    public int mergePosition = -1;

    public int hidePosition = -1;


    public MainDragAdapter(Context mContext) {
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
            convertView = View.inflate(mContext, R.layout.adapter_main_item, null);
            holder = new ViewHolder();
            holder.folderView = (FolderView) convertView.findViewById(R.id.folder_place_view);
            holder.title   = (TextView) convertView.findViewById(R.id.tv);
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

        holder.title.setText("未命名"+bean.get(0).position);
        holder.folderView.setData(bean);
        return convertView;
    }

    public class ViewHolder {
        FolderView folderView;
        TextView title;
    }

    public void setData(List<List<Bean>> b) {
        this.beans = b;
    }

    @Override
    public void reorderItems(int oldPosition, int newPosition) {

        List<Bean> tempBean = beans.get(oldPosition);
        beans.remove(oldPosition);
        beans.add(newPosition,tempBean);
        notifyDataSetChanged();

    }

    @Override
    public void setHideItem(int hidePosition,int viewPosition,View convertView) {
        this.hidePosition = hidePosition;
        if(viewPosition<0 || convertView==null){
            return;
        }
        Log.i("uuuuu","hide 局部刷新");
        refreshItemForPosition(viewPosition,convertView);
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
    public void setDisplayMerge(int mergePosition,int viewPosition,View convertView) {
        this.mergePosition = mergePosition;

        if(viewPosition<0  || convertView==null){
            return;
        }
        Log.i("uuuuu","merge 局部刷新");

        refreshItemForPosition(viewPosition, convertView);
    }

    @Override
    public void myMotifyDataSetChanged() {
        notifyDataSetChanged();
    }

    @Override
    public View refreshItemForPosition(int position, View convertView) {
        return getView(position,convertView,null);
    }

    @Override
    public void setmMergeItem(final int oldPosition, final int newPosition) {

        if(newPosition<0  || oldPosition==newPosition){
            return;
        }

        List<Bean> tempBean = beans.get(oldPosition);
        List<Bean> newBean = beans.get(newPosition);

        if(tempBean.size()>0){
            for(int x = 0;x<tempBean.size();x++){
                newBean.add(tempBean.get(x));
            }
        }
        beans.remove(oldPosition);

        notifyDataSetChanged();
    }

    @Override
    public List<Bean> getOnclickPosition(int position) {

        if(position==-1){
            return null;
        }
        return beans.get(position);
    }
}