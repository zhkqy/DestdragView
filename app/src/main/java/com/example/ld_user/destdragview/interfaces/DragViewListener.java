package com.example.ld_user.destdragview.interfaces;


/**
 * Created by chenlei
 */
public interface DragViewListener {


    /**
     * 拖出边界回调
     * @param dragPosition  这个方法执行需要删除 position位置的元素
     */
    public void actionDragExited(int dragPosition) ;

}
