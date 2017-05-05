package com.example.ld_user.destdragview.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ld_user.destdragview.R;
import com.example.ld_user.destdragview.model.Bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ld-user on 2017/4/27.
 */

public class SubFolderAdapter extends BaseAdapter implements DragGridBaseAdapter {

    List<Bean> beans;

    Context mContext;

    public int hidePosition = -1;

    public GridView gridView;

    public SubFolderAdapter(Context mContext, GridView gridView) {
        this.mContext = mContext;
        this.gridView = gridView;
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
            convertView = View.inflate(mContext, R.layout.adapter_sub_item, null);
            holder = new ViewHolder();
            holder.img = (ImageView) convertView.findViewById(R.id.img);
            holder.subTitle = (TextView) convertView.findViewById(R.id.sub_title);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Bean b = beans.get(position);

        if (hidePosition == position) {
            convertView.setVisibility(View.INVISIBLE);
        } else {
            convertView.setVisibility(View.VISIBLE);
        }

        holder.img.setBackgroundResource(R.drawable.board_default_gridview);
        holder.subTitle.setText("未命名"+b.position+"");
        return convertView;
    }

    public class ViewHolder {
        ImageView img;
        TextView subTitle;
    }

    public void setData(List<Bean> b) {
        hidePosition = -1;
        this.beans = b;
    }

    @Override
    public void reorderItems(int oldPosition, int newPosition) {

        if(oldPosition<0){

        }
        Bean tempBean = beans.get(oldPosition);
        beans.remove(oldPosition);
        beans.add(newPosition,tempBean);
        notifyDataSetChanged();
    }

    @Override
    public void reorderItems(int oldPosition, int newPosition, List<Bean> beans) {

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
        return false;
    }

    @Override
    public void setDisplayMerge(int mergePosition,int viewPosition,View convertView) {
    }

    @Override
    public void mNotifyDataSetChanged() {
        notifyDataSetChanged();
    }

    @Override
    public View refreshItemForPosition(int position, View convertView) {
        return getView(position,convertView,gridView);
    }


    @Override
    public List<Bean> getOnclickPosition(int position) {

        if(position==-1){
            return null;
        }
        List<Bean> b  =  new ArrayList<>();
        b. add(beans.get(position));
        return b;
    }

    @Override
    public int getmCount() {
        return getCount();
    }

    @Override
    public List<Bean> removeSubDialogMiddleData(int MainPosition, int subPosition) {
        return null;
    }

    @Override
    public List<Bean> removeMainData(int mainPosition) {
        return null;
    }

    @Override
    public void setmMergeItem(int newPosition, List<Bean> beans) {

    }

    @Override
    public void addtailOfTheQueue(List<Bean> beans) {

    }

    @Override
    public List<List<Bean>> getAllData() {
        return null;
    }

    @Override
    public void setmMergeItem(int oldPosition, int newPosition) {}


}
