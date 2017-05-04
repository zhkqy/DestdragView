package com.example.ld_user.destdragview.adapter;

import android.view.View;
import android.view.ViewGroup;

import com.example.ld_user.destdragview.model.Bean;

import java.util.List;

public interface DragGridBaseAdapter {

	/**
	 * 重新排列数据
	 * @param oldPosition
	 * @param newPosition
	 */
	public void reorderItems(int oldPosition, int newPosition);

	/**
	 *
	 * @param oldPosition
	 * @param newPosition
	 * @param beans   子层操作的话  这里有数据
	 */
	public void reorderItems(int oldPosition, int newPosition,List<Bean> beans);


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

	public List<Bean> getOnclickPosition(int position);


	/***
	 * 获取所有元素个数
	 */

	public int getmCount();

	/***
	 * 删除镜像的view 数据  如果划出了dialog
	 * @param position
	 * @return
	 */
	public List<Bean> removeSubData(int position);

}
