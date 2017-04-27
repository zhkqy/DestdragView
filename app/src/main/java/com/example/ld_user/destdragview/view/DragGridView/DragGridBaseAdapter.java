package com.example.ld_user.destdragview.view.DragGridView;

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
	public void setHideItem(int hidePosition);


	/**
	 * 根据view 获取是否为文件夹
	 * @param position
	 * @return
     */
	public boolean  isFolder(int position);


}
