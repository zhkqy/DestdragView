package com.example.ld_user.destdragview.adapter;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;

import com.example.ld_user.destdragview.R;
import com.example.ld_user.destdragview.adapter.MyAdapter;
import com.example.ld_user.destdragview.model.Bean;

import java.util.List;

/**
 * Created by ld-user on 2017/4/27.
 */

public class FolderZGridAdapter extends BaseAdapter {

List<Bean> beans;

    Context mContext;

    public FolderZGridAdapter(Context mContext){
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

        MyAdapter.ViewHolder holder = null;

        if(convertView==null){
            convertView =  View.inflate(mContext, R.layout.adapter_foler_item,null);
            convertView.setTag(holder);
        }else{
            holder = (MyAdapter.ViewHolder) convertView.getTag();
        }

        return convertView;
    }

    public class ViewHolder{
        GridView folderView;
    }

    public void setData(List<Bean> b){
        this.beans = b;
    }


}
