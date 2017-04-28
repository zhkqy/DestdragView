package com.example.ld_user.destdragview.view.DragGridView;

import android.view.View;
import android.view.ViewGroup;

public interface DragGridBaseAdapter {

	/**
	 * 重新排列数据
	 * @param oldPosition
	 * @param newPosition
	 */
	public void reorderItems(int oldPosition, int newPosition);


	/**
	 * 设置某个item隐藏
	 * @param hidePosition
	 */
	public void setHideItem(int hidePosition,int viewPosition,View convertView);


	/**
	 * 根据view 获取是否为文件夹
	 * @param position
	 * @return
     */
	public boolean  isFolder(int position);


	/**显示即将要merge的item*/
	public void setDisplayMerge(int mergePosition,int viewPosition,View convertView);


	public void  myMotifyDataSetChanged();


	public View refreshItemForPosition(int position, View convertView);


	/**
	 * 设置merge合并
	 */
	public void setmMergeItem(int oldPosition, int newPosition);



}
