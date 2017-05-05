package com.example.ld_user.destdragview.view.DragGridView;

import android.content.Context;
import android.os.Handler;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ListAdapter;

import com.example.ld_user.destdragview.adapter.DragGridBaseAdapter;
import com.example.ld_user.destdragview.eventbus.PandaEventBusObject;
import com.example.ld_user.destdragview.model.Bean;
import com.example.ld_user.destdragview.utils.ToastUtils;
import com.example.ld_user.destdragview.view.DragViewPager;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;

import java.util.LinkedList;
import java.util.List;

import de.greenrobot.event.EventBus;


/**
 * @author zhkqy
 */
public class DragGridView extends BaseDragGridView {

    float xRatio = 3.5f;
    float yRatio = 5;

    /**
     * 防止多次触发 如果拖动到当前item位置没有变化  只要执行一次就行
     */
    private int tempItemPosition = -1;

    /**
     * 是否可以拖拽，默认不可以
     */
    private boolean isDrag = false;

    private int mDownX;
    private int mDownY;
    private int moveX;
    private int moveY;
    /**
     * 正在拖拽的position
     */
    private int mDragPosition;

    /**
     * 刚开始拖拽的item对应的View
     */
    private View mStartDragItemView = null;


    /**
     * 震动器
     */
    private Vibrator mVibrator;

    /**
     * DragGridView自动向下滚动的边界值
     */
    private int mDownScrollBorder;

    /**
     * DragGridView自动向上滚动的边界值
     */
    private int mUpScrollBorder;

    /**
     * DragGridView自动滚动的速度
     */
    private static final int speed = 20;

    private boolean mAnimationEnd = true;

    private DragGridBaseAdapter mDragAdapter;
    private int mNumColumns;
    private int mColumnWidth;
    private boolean mNumColumnsSet;
    private int mHorizontalSpacing;


    /**
     * 是否为文件夹展开状态   如果是 移出item时候需要回复状态
     */
    public boolean isFolderStatus = false;

    /***
     * 用于局部刷新
     */
    public int folderStatusPosition = -1;


    /**
     * item相对坐标
     */
    private int itemleft;
    private int itemtop;
    private int itemright;
    private int itembottom;

    private int itemMoveX;
    private int itemMoveY;
    private int itemMoveXoffset;
    private int itemMoveYoffset;

    private int mTouchSlop;
    private Context mContext;

    public DragGridView(Context context) {
        this(context, null);
    }

    public DragGridView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DragGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mContext = context;
        mVibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();

        if (!mNumColumnsSet) {
            mNumColumns = AUTO_FIT;
        }
    }

    @Override
    public void addtailOfTheQueue(List<Bean> beans) {
        mDragAdapter.addtailOfTheQueue(beans);
    }

    private Handler mHandler = new Handler();

    //用来处理是否为长按的Runnable
    private Runnable mLongClickRunnable = new Runnable() {

        @Override
        public void run() {

            DragViewPager.DRAG_LAYER = DragViewPager.SUB_ABOVE_MAIN_LAYER;

            isDrag = true; //设置可以拖拽
//            mVibrator.vibrate(50); //震动一下

            //拖动开始之前修正位置
            getLocationAndFixHeight(DragGridView.this, Location);

            createDragImage(mStartDragItemView, Location);

            /**----------------------------**/

            DragViewPager.beans = mDragAdapter.getOnclickPosition(mDragPosition);
            DragViewPager.crrentPageAllBeans.remove(mDragPosition);
            mDragAdapter.setHideItem(mDragPosition, mDragPosition, getChildAt(mDragPosition - getFirstVisiblePosition()));
            DragViewPager.dragPosition = mDragPosition;
            eventBusObject.setType(PandaEventBusObject.SUB_DRAG_GRIDVIEW_TOUCH_EVENT_DOWN);
            EventBus.getDefault().post(eventBusObject);

            /**----------------------------**/
        }
    };


    //检测滑动边缘 切换viewpager

    private Runnable edgeViewPagerRunnable = new Runnable() {

        @Override
        public void run() {

            if (isViewPagerLeftSwap) {
                EventBus.getDefault().post(new PandaEventBusObject(PandaEventBusObject.OVERSTEP_LEFT_RANGE));
            } else if (isViewPagerRightSwap) {
                EventBus.getDefault().post(new PandaEventBusObject(PandaEventBusObject.OVERSTEP_RIGHT_RANGE));
            }
            isViewPagerLeftSwap = false;
            isViewPagerRightSwap = false;

        }
    };


    @Override
    public void setAdapter(ListAdapter adapter) {
        super.setAdapter(adapter);

        if (adapter instanceof DragGridBaseAdapter) {
            mDragAdapter = (DragGridBaseAdapter) adapter;
        } else {
            throw new IllegalStateException("the adapter must be implements DragGridAdapter");
        }
    }


    @Override
    public void setNumColumns(int numColumns) {
        super.setNumColumns(numColumns);
        mNumColumnsSet = true;
        this.mNumColumns = numColumns;
    }


    @Override
    public void setColumnWidth(int columnWidth) {
        super.setColumnWidth(columnWidth);
        mColumnWidth = columnWidth;
    }


    @Override
    public void setHorizontalSpacing(int horizontalSpacing) {
        super.setHorizontalSpacing(horizontalSpacing);
        this.mHorizontalSpacing = horizontalSpacing;
    }


    /**
     * 若设置为AUTO_FIT，计算有多少列
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mNumColumns == AUTO_FIT) {
            int numFittedColumns;
            if (mColumnWidth > 0) {
                int gridWidth = Math.max(MeasureSpec.getSize(widthMeasureSpec) - getPaddingLeft()
                        - getPaddingRight(), 0);
                numFittedColumns = gridWidth / mColumnWidth;
                if (numFittedColumns > 0) {
                    while (numFittedColumns != 1) {
                        if (numFittedColumns * mColumnWidth + (numFittedColumns - 1)
                                * mHorizontalSpacing > gridWidth) {
                            numFittedColumns--;
                        } else {
                            break;
                        }
                    }
                } else {
                    numFittedColumns = 1;
                }
            } else {
                numFittedColumns = 2;
            }
            mNumColumns = numFittedColumns;
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    /**
     * 设置响应拖拽的毫秒数，默认是1000毫秒
     *
     * @param dragResponseMS
     */
    public void setDragResponseMS(long dragResponseMS) {
        this.dragResponseMS = dragResponseMS;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.i("ggggggg", "ACTION_DOWN");

                mDownX = (int) ev.getX();
                mDownY = (int) ev.getY();

                //根据按下的X,Y坐标获取所点击item的position
                mDragPosition = pointToPosition(mDownX, mDownY);

                if (mDragPosition == AdapterView.INVALID_POSITION) {
                    return super.dispatchTouchEvent(ev);
                }

                //使用Handler延迟dragResponseMS执行mLongClickRunnable
                mHandler.postDelayed(mLongClickRunnable, dragResponseMS);

                //根据position获取该item所对应的View
                mStartDragItemView = getChildAt(mDragPosition - getFirstVisiblePosition());

                //判断是否可以merge
                DragViewPager.isCanMerge = !mDragAdapter.isFolder(mDragPosition);

                //获取DragGridView自动向上滚动的偏移量，小于这个值，DragGridView向下滚动
                mDownScrollBorder = getHeight() / 5;
                //获取DragGridView自动向下滚动的偏移量，大于这个值，DragGridView向上滚动
                mUpScrollBorder = getHeight() * 4 / 5;

                break;

            case MotionEvent.ACTION_MOVE:
                Log.i("ggggggg", "ACTION_MOVE");
                moveX = (int) ev.getX();
                moveY = (int) ev.getY();

                if (isDrag) {
//                    拖动item
                    onDragItem(ev, ev.getRawX(), ev.getRawY(), mStartDragItemView.getWidth(), mStartDragItemView.getHeight());
                }

                //如果我们在按下的item上面移动，只要不超过item的边界我们就不移除mRunnable
                if (!isTouchInItem(mStartDragItemView, moveX, moveY)) {
                    mHandler.removeCallbacks(mLongClickRunnable);
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                Log.i("ggggggg", "ACTION_UP");

                if (eventBusObject != null) {
                    eventBusObject.setType(PandaEventBusObject.SUB_DRAG_GRIDVIEW_TOUCH_EVENT_UP);
                    eventBusObject.setObj(ev);
                    EventBus.getDefault().post(eventBusObject);
                }

                if (isDrag) {
                    onStopDrag();
                    isDrag = false;
                }

                restoreToInitial();

                int upX = (int) ev.getX();
                int upY = (int) ev.getY();

                if (Math.abs(upX - mDownX) < mTouchSlop && Math.abs(upY - mDownY) < mTouchSlop) {
                    onClick(upX, upY);
                }

                mHandler.removeCallbacks(mLongClickRunnable);
                mHandler.removeCallbacks(mScrollRunnable);
                mHandler.removeCallbacks(mItemLongClickRunnable);
                mHandler.removeCallbacks(edgeViewPagerRunnable);

                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    private void onClick(int upX, int upY) {
        int p = pointToPosition(upX, upY);

        if (p != -1) {
            Log.i("qqqqqqq", "p = " + p);
            List<Bean> bean = mDragAdapter.getOnclickPosition(p);

            if (bean != null && bean.size() > 0) {
                if (bean.size() == 1) {
                    ToastUtils.showText(mContext, "选中了文件" + bean.get(0).position);
                } else {
                    mainGridViewHelper.showSubContainer(p, bean, mDragAdapter);
                }
            }

            initFolderItemStatus();
        }
    }

    /**
     * 是否点击在GridView的item上面
     *
     * @param x
     * @param y
     * @return
     */
    private boolean isTouchInItem(View dragView, int x, int y) {
        if (dragView == null) {
            return false;
        }

        if (!isDrag) {
            //按住item 滑动 这种情况会一直在一个item里面  滑动则不触发长按的操作
            if (Math.abs(x - mDownX) > mTouchSlop || Math.abs(y - mDownY) > mTouchSlop) {
                return false;
            }
        }

        int leftOffset = dragView.getLeft();
        int topOffset = dragView.getTop();
        if (x < leftOffset || x > leftOffset + dragView.getWidth()) {
            return false;
        }

        if (y < topOffset || y > topOffset + dragView.getHeight()) {
            return false;
        }

        return true;
    }

    /**
     * 拖动item，在里面实现了item镜像的位置更新，item的相互交换以及GridView的自行滚动
     */
    private void onDragItem(MotionEvent event, float rawX, float rawY, int width, int height) {

        getParent().requestDisallowInterceptTouchEvent(true);


        mDragView.setX(rawX - width / 2);
        mDragView.setY(rawY - height / 2);

        if (eventBusObject != null) {
            eventBusObject.setType(PandaEventBusObject.SUB_DRAG_GRIDVIEW_TOUCH_EVENT_MOVE);
            eventBusObject.setObj(event);
            EventBus.getDefault().post(eventBusObject);
        }

    }


    /**
     * 当moveY的值大于向上滚动的边界值，触发GridView自动向上滚动
     * 当moveY的值小于向下滚动的边界值，触发GridView自动向下滚动
     * 否则不进行滚动
     */
    private Runnable mScrollRunnable = new Runnable() {

        @Override
        public void run() {
            int scrollY;
            if (getFirstVisiblePosition() == 0 || getLastVisiblePosition() == getCount() - 1) {
                mHandler.removeCallbacks(mScrollRunnable);
            }

            if (moveY > mUpScrollBorder) {
                scrollY = speed;
                mHandler.postDelayed(mScrollRunnable, 25);
            } else if (moveY < mDownScrollBorder) {
                scrollY = -speed;
                mHandler.postDelayed(mScrollRunnable, 25);
            } else {
                scrollY = 0;
                mHandler.removeCallbacks(mScrollRunnable);
            }

            smoothScrollBy(scrollY, 10);
        }
    };

    //用来处理是否为 item移动或合并 的Runnable
    private Runnable mItemLongClickRunnable = new Runnable() {

        @Override
        public void run() {

            isFolderStatus = true;
            Log.i("SubDilaog", "init isFolderStatus = " + isFolderStatus);
            folderStatusPosition = tempItemPosition;

            /**检测区间范围*/

            int width = itemright - itemleft;
            int leftOffset = (int) (width / xRatio);

            int height = itembottom - itemtop;
            int topOffset = (int) (height / yRatio);

            /* 合并逻辑*/
            if (itemMoveX + itemMoveXoffset > (itemleft + leftOffset) && itemMoveX + itemMoveXoffset < (itemright - leftOffset) &&
                    itemMoveY + itemMoveYoffset > itemtop + topOffset && itemMoveY + itemMoveYoffset < itembottom - topOffset) {

                Log.i("cccccc", "DragViewPager.DRAG_LAYER = " + DragViewPager.DRAG_LAYER);

                //主层如果设置了合并  或是  子层都是文件
                if (DragViewPager.isCanMerge) {
                    Log.i("cccccc", "开始合并逻辑");
                    mDragAdapter.setDisplayMerge(tempItemPosition, tempItemPosition, getChildAt(tempItemPosition - getFirstVisiblePosition()));
                } else {
                    //这里直接走交换的逻辑
                    swapIten(tempItemPosition);
                    Log.i("cccccc", "移动---- position = " + tempItemPosition);
                }
            } else {
                //这里直接走交换的逻辑
                swapIten(tempItemPosition);
                Log.i("cccccc", "移动 position = " + tempItemPosition);
            }
        }
    };

    /**
     * 交换item,并且控制item之间的显示与隐藏效果
     *
     * @param moveX
     * @param moveY
     */
    private void onSwapItem(int moveX, int moveY) {
        //获取我们手指移动到的那个item的position
        final int tempPosition = pointToPosition(moveX, moveY);

        //假如tempPosition 改变了并且tempPosition不等于-1,则进行交换
        if (tempPosition != mDragPosition && tempPosition != AdapterView.INVALID_POSITION && mAnimationEnd) {

            /**相对于屏幕的 moveX moveY*/
            int[] currentDragedLocation = new int[2];
            this.getLocationOnScreen(currentDragedLocation);

            itemMoveX = moveX;
            itemMoveY = moveY + currentDragedLocation[1];

            /**计算移动的点的偏移量*/

            if (mStartDragItemView != null) {
                int w = mStartDragItemView.getWidth();
                int h = mStartDragItemView.getHeight();

                int[] currentDragOffset = new int[2];
                mStartDragItemView.getLocationOnScreen(currentDragOffset);

                itemMoveXoffset = 0;
                itemMoveYoffset = 0;
            }

            if (tempPosition != tempItemPosition) {
                initFolderItemStatus();

                mHandler.removeCallbacks(mItemLongClickRunnable);
                mHandler.postDelayed(mItemLongClickRunnable, itemDelayTime);

                //// 测试代码
//            根据position获取该item所对应的View
                View v = getChildAt(tempPosition - getFirstVisiblePosition());

                if (v == null) {
                    return;
                }

                /**获取item 相对于屏幕的区间范围*/

                int w = v.getWidth();
                int h = v.getHeight();

                int[] location = new int[2];
                v.getLocationOnScreen(location);

                itemleft = location[0];
                itemtop = location[1];
                itemright = itemleft + w;
                itembottom = itemtop + h;

            }

            int width = itemright - itemleft;
            int leftOffset = (int) (width / xRatio);

            int height = itembottom - itemtop;
            int topOffset = (int) (height / yRatio);

            /***
             * 这里的逻辑 当出发合并文件夹的时候  isFolderStatus  = true  判断如果划出了焦点 则交换
             */
            if (isFolderStatus) {
                 /* 合并逻辑*/
                if (itemMoveX + itemMoveXoffset > (itemleft + leftOffset) && itemMoveX + itemMoveXoffset < (itemright - leftOffset) &&
                        itemMoveY + itemMoveYoffset > itemtop + topOffset && itemMoveY + itemMoveYoffset < itembottom - topOffset) {

                } else {
                    mHandler.removeCallbacks(mItemLongClickRunnable);
                    initFolderItemStatus();
                    //这里直接走交换的逻辑
                    swapIten(tempPosition);
//                Log.i("cccccc", "移动 position = " + tempItemPosition);
                }
            }

            tempItemPosition = tempPosition;
        } else {
            mHandler.removeCallbacks(mItemLongClickRunnable);
            tempItemPosition = -1;
            initFolderItemStatus();
        }
    }

    /**
     * 初始化item folder状态  恢复没有merge前的状态
     */
    public void initFolderItemStatus() {
        if (isFolderStatus) {
            isFolderStatus = false;
            mDragAdapter.setDisplayMerge(-1, folderStatusPosition, getChildAt(folderStatusPosition - getFirstVisiblePosition()));
        }
    }

    private void swapIten(final int tempPosition) {

        //从其他页面过来的drag  mDragPosition初始值 = -1   这里会有数据DragViewPager.beans
        //否则 mDragPosition不等于-1    mDragPosition初始值无数据

        Log.i("kkkkkk", "mDragPosition = "+mDragPosition+"   "+
                " DragViewPager.beans   = "+DragViewPager.beans +"  "+
                "   tempPosition =  "+tempPosition);
        if (mDragPosition == -1  && DragViewPager.beans != null) {
            DragViewPager.dragPosition = tempPosition;
        }else{
            DragViewPager.dragPosition = mDragPosition;
        }
        mDragAdapter.reorderItems(mDragPosition, tempPosition, DragViewPager.beans);
        mDragAdapter.setHideItem(tempPosition, tempPosition, getChildAt(tempPosition - getFirstVisiblePosition()));

        final ViewTreeObserver observer = getViewTreeObserver();
        observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {

            @Override
            public boolean onPreDraw() {
                observer.removeOnPreDrawListener(this);

                if (mDragPosition == -1) {
                    animateReorder(mDragAdapter.getmCount() - 1, tempPosition);
                } else {
                    animateReorder(mDragPosition, tempPosition);
                }
                animateReorder(mDragPosition, tempPosition);
                mDragPosition = tempPosition;
                return true;
            }
        });
    }

    /**
     * 创建移动动画
     *
     * @param view
     * @param startX
     * @param endX
     * @param startY
     * @param endY
     * @return
     */
    private AnimatorSet createTranslationAnimations(View view, float startX,
                                                    float endX, float startY, float endY) {
        ObjectAnimator animX = ObjectAnimator.ofFloat(view, "translationX",
                startX, endX);
        ObjectAnimator animY = ObjectAnimator.ofFloat(view, "translationY",
                startY, endY);
        AnimatorSet animSetXY = new AnimatorSet();
        animSetXY.playTogether(animX, animY);
        return animSetXY;
    }

    /**
     * item的交换动画效果
     *
     * @param oldPosition
     * @param newPosition
     */
    private void animateReorder(final int oldPosition, final int newPosition) {
        boolean isForward = newPosition > oldPosition;
        List<Animator> resultList = new LinkedList<Animator>();
        if (isForward) {
            for (int pos = oldPosition; pos < newPosition; pos++) {
                View view = getChildAt(pos - getFirstVisiblePosition());
                System.out.println(pos);

                if (view == null) {
                    continue;
                }

                if ((pos + 1) % mNumColumns == 0) {
                    resultList.add(createTranslationAnimations(view,
                            -view.getWidth() * (mNumColumns - 1), 0,
                            view.getHeight(), 0));
                } else {
                    resultList.add(createTranslationAnimations(view,
                            view.getWidth(), 0, 0, 0));
                }
            }
        } else {
            for (int pos = oldPosition; pos > newPosition; pos--) {
                View view = getChildAt(pos - getFirstVisiblePosition());

                if (view == null) {
                    continue;
                }

                if ((pos + mNumColumns) % mNumColumns == 0) {
                    resultList.add(createTranslationAnimations(view,
                            view.getWidth() * (mNumColumns - 1), 0,
                            -view.getHeight(), 0));
                } else {
                    resultList.add(createTranslationAnimations(view,
                            -view.getWidth(), 0, 0, 0));
                }
            }
        }

        AnimatorSet resultSet = new AnimatorSet();
        resultSet.playTogether(resultList);
        resultSet.setDuration(200);
        resultSet.setInterpolator(new AccelerateDecelerateInterpolator());
        resultSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                mAnimationEnd = false;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mAnimationEnd = true;
            }
        });
        resultSet.start();
    }

    /**
     * 停止拖拽我们将之前隐藏的item显示出来，并将镜像移除
     */
    private void onStopDrag() {
        View view = getChildAt(mDragPosition - getFirstVisiblePosition());
        mDragAdapter.setHideItem(-1, mDragPosition, view);
    }

    private void onStopSubDrag() {
        mDragAdapter.setHideItem(-1, -1, null);
        mDragAdapter.mNotifyDataSetChanged();
        mDragPosition = -1;
    }


    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        //获取DragGridView自动向上滚动的偏移量，小于这个值，DragGridView向下滚动
        mDownScrollBorder = getHeight() / 5;
        //获取DragGridView自动向下滚动的偏移量，大于这个值，DragGridView向上滚动
        mUpScrollBorder = getHeight() * 4 / 5;
    }

    public void onSubTouchEvent(MotionEvent ev) {

        switch (ev.getAction()) {

            case MotionEvent.ACTION_MOVE:

                DragViewPager.DRAG_LAYER.equals(DragViewPager.SUB_ABOVE_MAIN_LAYER);

                Log.i("tttttt", "ACTION_MOVE");
                /**
                 *  修正dialog中gridview传过来  x 和 y 轴
                 */
                moveX = (int) ev.getRawX();
                moveY = (int) ev.getRawY() - (screenHeight - getHeight());

//              拖动item
                onSubDragItem(moveX, moveY, ev.getRawX());
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:

                mHandler.removeCallbacks(mLongClickRunnable);
                mHandler.removeCallbacks(mScrollRunnable);
                mHandler.removeCallbacks(mItemLongClickRunnable);
                mHandler.removeCallbacks(edgeViewPagerRunnable);

                Log.i("tttttt", "ACTION_UP");

                /**判断是否需要合并的逻辑*/
                if (isFolderStatus) {
                    if (DragViewPager.dragPosition == -1) {
                        mDragAdapter.setmMergeItem(folderStatusPosition, DragViewPager.beans);
                    } else {
                        mDragAdapter.setmMergeItem(DragViewPager.dragPosition, folderStatusPosition);
                    }
                }

                Log.i("kkkkkk", "isfolderstatus = "+isFolderStatus+"   "+
                "  DragViewPager.dragPosition  = "+DragViewPager.dragPosition+"  "+
                "  ");

                /**
                 * 这里添加无交换 添加到队列情况
                 */
                if (!isFolderStatus && DragViewPager.dragPosition == -1 && DragViewPager.beans != null && DragViewPager.beans.size() > 0) {
                    mDragAdapter.addtailOfTheQueue(DragViewPager.beans);
                }

                mDragAdapter.setDisplayMerge(-1, -1, getChildAt(folderStatusPosition - getFirstVisiblePosition()));

                restoreToInitial();

                onStopSubDrag();

                isFolderStatus = false;
                DragViewPager.dragPosition = -1;
                DragViewPager.beans = null;
                isViewPagerLeftSwap = false;
                isViewPagerRightSwap = false;
                DragViewPager.isCanMerge = false;

                DragViewPager.crrentPageAllBeans.clear();

                List<List<Bean>> list = mDragAdapter.getAllData();
                if (list != null && list.size() > 0) {
                    for (int x = 0; x < list.size(); x++) {
                        DragViewPager.crrentPageAllBeans.add(list.get(x));
                    }
                }
                break;
        }
    }

    /**
     * 拖动item，在里面实现了item镜像的位置更新，item的相互交换以及GridView的自行滚动
     *
     * @param moveX
     * @param moveY
     */
    private void onSubDragItem(int moveX, int moveY, float rawX) {

        /**
         * 判断主层拖动 是否到超过左右girdview边界 切换viewpager
         */
        if (isMainLayer) {

            if (rawX <= DragViewPager.leftDistance) {
                isViewPagerLeftSwap = true;
            } else {
                isViewPagerLeftSwap = false;
            }

            if (rawX >= DragViewPager.rightDistance) {
                isViewPagerRightSwap = true;
            } else {
                isViewPagerRightSwap = false;
            }

            Log.i("jjjjjj", "isViewPagerLeftSwap = " + isViewPagerLeftSwap + "   isViewPagerRightSwap=  " +
                    isViewPagerRightSwap + "  raw");

            if (isViewPagerLeftSwap || isViewPagerRightSwap) {
                if (!isOpenViewPagerSwap) {
                    mHandler.postDelayed(edgeViewPagerRunnable, itemDelayTime + 300);
                    isOpenViewPagerSwap = true;
                }
            } else {
                isOpenViewPagerSwap = false;
                mHandler.removeCallbacks(edgeViewPagerRunnable);
            }
        }
        onSwapItem(moveX, moveY);
        //GridView自动滚动
        mHandler.post(mScrollRunnable);
    }

}
