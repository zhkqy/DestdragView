package com.example.ld_user.destdragview.adapter;

import android.view.View;
import android.view.ViewGroup;

import com.example.ld_user.destdragview.model.Bean;

import java.util.List;

public interface DragGridBaseAdapter {

    /**
     * 重新排列数据
     *
     * @param oldPosition
     * @param newPosition
     */
    public void reorderItems(int oldPosition, int newPosition);

    /**
     * @param oldPosition
     * @param newPosition
     * @param beans       子层操作的话  这里有数据
     */
    public void reorderItems(int oldPosition, int newPosition, List<Bean> beans);

    /**
     * 设置某个item隐藏
     *
     * @param hidePosition
     */
    public void setHideItem(int hidePosition, int viewPosition, View convertView);


    /**
     * 根据view 获取是否为文件夹
     *
     * @param position
     * @return
     */
    public boolean isFolder(int position);


    /**
     * 显示即将要merge的item
     */
    public void setDisplayMerge(int mergePosition, int viewPosition, View convertView);


    public void mNotifyDataSetChanged();


    public View refreshItemForPosition(int position, View convertView);


    /**
     * 设置merge合并
     */
    public void setmMergeItem(int oldPosition, int newPosition);

    public List<Bean> getOnclickPosition(int position);


    /***
     * 获取所有元素个数
     */

    public int getmCount();

    /***
     * 删除dialog中的数据
     * @param mainPosition   主文件夹的位置
     * @param subPosition    子文件夹中的位置
     * @return
     */
    public List<Bean> removeSubDialogMiddleData(int mainPosition, int subPosition);


    /***
     * 移除主层数据
     */
    public List<Bean> removeMainData(int mainPosition);


    /**
     * 设置merge合并
     */
    public void setmMergeItem(int newPosition, List<Bean> beans);

    /**
     * 队尾插入数据
     */
    public void addtailOfTheQueue(List<Bean> beans);


    public List<List<Bean>> getAllData();

}
