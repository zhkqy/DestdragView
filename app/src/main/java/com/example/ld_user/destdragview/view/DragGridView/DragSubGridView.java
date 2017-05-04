package com.example.ld_user.destdragview.view.DragGridView;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListAdapter;

import com.example.ld_user.destdragview.R;
import com.example.ld_user.destdragview.adapter.DragGridBaseAdapter;
import com.example.ld_user.destdragview.dialog.SubDialog;
import com.example.ld_user.destdragview.interfaces.DragViewListener;
import com.example.ld_user.destdragview.model.Bean;
import com.example.ld_user.destdragview.utils.ToastUtils;
import com.example.ld_user.destdragview.utils.Utils;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;

import java.util.LinkedList;
import java.util.List;

/**
 * @author zhkqy
 */
public class DragSubGridView extends GridView {

    public int itemDelayTime = 250;

    public boolean isCanMerge = false;  //是否可以合并
    public boolean mergeSwitch = false;  //merge开关  外部设置开启的话  内部及时能merge也不好用

    float xRatio = 3.5f;
    float yRatio = 5;

    /**
     * DragGridView的item长按响应的时间， 默认是1000毫秒，也可以自行设置
     */
    private long dragResponseMS = 600;

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

    private WindowManager mWindowManager;

    /**
     * 我们拖拽的item对应的Bitmap
     */
    private Bitmap mDragBitmap;

    /**
     * 按下的点到所在item的上边缘的距离
     */
    private int mPoint2ItemTop;

    /**
     * 按下的点到所在item的左边缘的距离
     */
    private int mPoint2ItemLeft;

    /**
     * DragGridView距离屏幕顶部的偏移量
     */
    private int mOffset2Top;

    /**
     * DragGridView距离屏幕左边的偏移量
     */
    private int mOffset2Left;

    /**
     * 状态栏的高度
     */
    private int mStatusHeight;

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

    private static final String DESCRIPTION = "Long press";
    private static final String MAIN = "main";

    public DragSubGridView(Context context) {
        this(context, null);
    }

    public DragSubGridView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DragSubGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mContext = context;
        mVibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        mStatusHeight = Utils.getStatusHeight(context); //获取状态栏的高度
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();

        if (!mNumColumnsSet) {
            mNumColumns = AUTO_FIT;
        }

        setOnDragListener(new SubOnDragListener());

    }

    private Handler mHandler = new Handler();

    //用来处理是否为长按的Runnable
    private Runnable mLongClickRunnable = new Runnable() {

        @Override
        public void run() {
            isDrag = true; //设置可以拖拽
            mVibrator.vibrate(50); //震动一下

            mDragAdapter.setHideItem(mDragPosition, mDragPosition, getChildAt(mDragPosition - getFirstVisiblePosition()));

//            mStartDragItemView.startDrag(ClipData.newPlainText(DESCRIPTION, MAIN),
//                    new DragShadowBuilder(mStartDragItemView), mStartDragItemView, 0);
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

    public void setMergeSwitch(boolean mergeSwitch) {
        this.mergeSwitch = mergeSwitch;
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
                isCanMerge = !mDragAdapter.isFolder(mDragPosition);

                if (mergeSwitch) {
                    isCanMerge = false;
                }

                //下面这几个距离大家可以参考我的博客上面的图来理解下
                mPoint2ItemTop = mDownY - mStartDragItemView.getTop();
                mPoint2ItemLeft = mDownX - mStartDragItemView.getLeft();

                mOffset2Top = (int) (ev.getRawY() - mDownY);
                mOffset2Left = (int) (ev.getRawX() - mDownX);

                //获取DragGridView自动向上滚动的偏移量，小于这个值，DragGridView向下滚动
                mDownScrollBorder = getHeight() / 5;
                //获取DragGridView自动向下滚动的偏移量，大于这个值，DragGridView向上滚动
                mUpScrollBorder = getHeight() * 4 / 5;

                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:

                mHandler.removeCallbacks(mLongClickRunnable);
                mHandler.removeCallbacks(mScrollRunnable);
                mHandler.removeCallbacks(mItemLongClickRunnable);

                int upX = (int) ev.getX();
                int upY = (int) ev.getY();

                if (Math.abs(upX - mDownX) < mTouchSlop && Math.abs(upY - mDownY) < mTouchSlop) {
                    onClick(upX, upY);
                }

                break;

        }
        return super.dispatchTouchEvent(ev);
    }

    private void onClick(int upX, int upY) {
        int p = pointToPosition(upX, upY);

        List<Bean> bean = mDragAdapter.getOnclickPosition(p);

        if (bean != null && bean.size() > 0) {
            if (bean.size() == 1) {
                ToastUtils.showText(mContext, "选中了文件" + bean.get(0).position);
            } else {
                showSubContainer(bean);
            }
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
     *
     * @param moveX
     * @param moveY
     */
    private void onDragItem(int moveX, int moveY) {
        onSwapItem(moveX, moveY);

        //GridView自动滚动
        mHandler.post(mScrollRunnable);
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

                if (isCanMerge) {
//                    Log.i("cccccc", "开始合并逻辑");

                    mDragAdapter.setDisplayMerge(tempItemPosition, tempItemPosition, getChildAt(tempItemPosition - getFirstVisiblePosition()));

                } else {
                    //这里直接走交换的逻辑
                    swapIten(tempItemPosition);
//                    Log.i("cccccc", "移动---- position = " + tempItemPosition);
                }
            } else {
                //这里直接走交换的逻辑
                swapIten(tempItemPosition);
//                Log.i("cccccc", "移动 position = " + tempItemPosition);
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

//                        Log.i("yyyyy", "  location0 =   " + currentDragedLocation[0] + "   location1 = " + currentDragedLocation[1]);
//                        Log.i("yyyyy", "  movex =   " + moveX + "   movey = " + (moveY + currentDragedLocation[1]));

            itemMoveX = moveX;
            itemMoveY = moveY + currentDragedLocation[1];

            /**计算移动的点的偏移量*/

            if (mStartDragItemView != null) {
                int w = mStartDragItemView.getWidth();
                int h = mStartDragItemView.getHeight();

                int[] currentDragOffset = new int[2];
                mStartDragItemView.getLocationOnScreen(currentDragOffset);
                int left = currentDragOffset[0];
                int top = currentDragOffset[1];

                int tempLeft = left + (w / 2);
                int tempTop = top + (h / 2);

                itemMoveXoffset = 0;
                itemMoveYoffset = 0;
                Log.i("yyyyy", "  itemMoveXoffset =   " + itemMoveXoffset + "   itemMoveYoffset = " + itemMoveYoffset);
            }

            if (tempPosition != tempItemPosition) {
                initFolderItemStatus();

                Log.i("SubDilaog", "tempPosition = " + tempPosition + "   tempItemPosition =  " + tempItemPosition +
                        "  mDragPosition = " + mDragPosition);

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

//                Log.i("yyyyy", "  itemleft =   " + itemleft + "   itemtop = " + itemtop + "   w = " + w + "   h = " + h);

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

        mDragAdapter.reorderItems(mDragPosition, tempPosition);
        mDragAdapter.setHideItem(tempPosition, tempPosition, getChildAt(tempPosition - getFirstVisiblePosition()));

        final ViewTreeObserver observer = getViewTreeObserver();
        observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {

            @Override
            public boolean onPreDraw() {
                observer.removeOnPreDrawListener(this);
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
        resultSet.setDuration(180);
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


    private SubDialog mSubDialog;

    /**
     * 显示次级窗口
     */
    private void showSubContainer(List<Bean> b) {

        if (mSubDialog == null) {
            mSubDialog = initSubDialog(b);

        } else {
            mSubDialog.setData(b);
        }

        mSubDialog.show();
    }

    DragSubGridView mSubGridView;

    private SubDialog initSubDialog(List<Bean> b) {
        SubDialog dialog = new SubDialog(mContext, R.style.ClassifyViewTheme, b);
        return dialog;
    }


    public class SubOnDragListener implements OnDragListener {

        @Override
        public boolean onDrag(View v, DragEvent event) {

            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_EXITED:
                    mHandler.removeCallbacks(mItemLongClickRunnable);
                    isFolderStatus = false;
                    folderStatusPosition = -1;
                    Log.i("SubDilaog", "SubOnDragListener ACTION_DRAG_EXITED isFolderStatus = " + isFolderStatus);

                   if(subGridViewListener!=null){
                       subGridViewListener.actionDragExited();
                   }
                    break;
                case DragEvent.ACTION_DRAG_LOCATION:
                    Log.i("SubDilaog", "SubOnDragListener ACTION_DRAG_LOCATION");
                    moveX = (int) event.getX();
                    moveY = (int) event.getY();

                    if (isDrag) {
//                    拖动item
                        onDragItem(moveX, moveY);
                    }

                    //如果我们在按下的item上面移动，只要不超过item的边界我们就不移除mRunnable
                    if (!isTouchInItem(mStartDragItemView, moveX, moveY)) {
                        mHandler.removeCallbacks(mLongClickRunnable);
                    }
                    break;
                case DragEvent.ACTION_DRAG_ENDED:

                    if (isDrag) {
                        Log.i("SubDilaog", "SubOnDragListener  ACTION_DRAG_ENDED");
                        onStopDrag();
                        isDrag = false;
                    }

                    mHandler.removeCallbacks(mLongClickRunnable);
                    mHandler.removeCallbacks(mScrollRunnable);
                    mHandler.removeCallbacks(mItemLongClickRunnable);

                    Log.i("SubDilaog", " SubOnDragListener   isFolderStatus = " + isFolderStatus);

                    if (isFolderStatus) {
                        isFolderStatus = false;

                        mDragAdapter.setDisplayMerge(-1, -1, getChildAt(folderStatusPosition - getFirstVisiblePosition()));

                        Log.i("SubDilaog", "SubOnDragListener setmMergeItem  mDragPosition = " + mDragPosition + "    folderStatusPosition = " + folderStatusPosition);

                        mDragAdapter.setmMergeItem(mDragPosition, folderStatusPosition);
                    }

                    break;
                case DragEvent.ACTION_DROP:
                    Log.i("SubDilaog", "SubOnDragListener  ACTION_DROP");
                    break;
                default:
            }
            return true;
        }
    }

    DragViewListener subGridViewListener;

    public void setSubGridViewListener(DragViewListener subGridViewListener) {
        this.subGridViewListener = subGridViewListener;
    }


    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        Log.i("SubDilaog", "  sub draggird onTouchEvent");

        return super.onTouchEvent(ev);
    }

}
