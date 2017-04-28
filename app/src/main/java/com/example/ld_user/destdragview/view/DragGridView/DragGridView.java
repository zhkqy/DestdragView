package com.example.ld_user.destdragview.view.DragGridView;

import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.util.Log;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;

import com.example.ld_user.destdragview.R;
import com.example.ld_user.destdragview.adapter.MyAdapter;
import com.example.ld_user.destdragview.adapter.SubFolderAdapter;
import com.example.ld_user.destdragview.model.Bean;
import com.example.ld_user.destdragview.utils.DataGenerate;
import com.example.ld_user.destdragview.utils.ToastUtils;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;

import java.util.LinkedList;
import java.util.List;

/**
 * @author zhkqy
 */
public class DragGridView extends GridView {

    public int itemDelayTime = 300;

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
    /**
     * dialog的高度比例
     */
    private float mSubHeightRatio = 0.6f;
    private float mSubWidthRatio = 0.8f;

    private int screenWidth;

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
        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        mStatusHeight = getStatusHeight(context); //获取状态栏的高度
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();

        screenWidth = mWindowManager.getDefaultDisplay().getWidth();

        if (!mNumColumnsSet) {
            mNumColumns = AUTO_FIT;
        }

        setOnDragListener(new MainOnDragListener());

    }

    private Handler mHandler = new Handler();

    //用来处理是否为长按的Runnable
    private Runnable mLongClickRunnable = new Runnable() {

        @Override
        public void run() {
            isDrag = true; //设置可以拖拽
            mVibrator.vibrate(50); //震动一下

            mDragAdapter.setHideItem(mDragPosition, mDragPosition, getChildAt(mDragPosition - getFirstVisiblePosition()));

            mStartDragItemView.startDrag(ClipData.newPlainText(DESCRIPTION, MAIN),
                    new DragShadowBuilder(mStartDragItemView), mStartDragItemView, 0);
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
                Log.i("gggggg", " tempPosition != tempItemPosition ");
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
     * 初始化item folder状态
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

    /**
     * 获取状态栏的高度
     *
     * @param context
     * @return
     */
    private static int getStatusHeight(Context context) {
        int statusHeight = 0;
        Rect localRect = new Rect();
        ((Activity) context).getWindow().getDecorView().getWindowVisibleDisplayFrame(localRect);
        statusHeight = localRect.top;
        if (0 == statusHeight) {
            Class<?> localClass;
            try {
                localClass = Class.forName("com.android.internal.R$dimen");
                Object localObject = localClass.newInstance();
                int i5 = Integer.parseInt(localClass.getField("status_bar_height").get(localObject).toString());
                statusHeight = context.getResources().getDimensionPixelSize(i5);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return statusHeight;
    }

    private Dialog mSubDialog;

    /**
     * 显示次级窗口
     */
    private void showSubContainer(List<Bean> b) {
        if (mSubDialog == null) {
            mSubDialog = initSubDialog(b);
            mSubDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialog) {
//                    getLocationAndFixHeight(mSubRecyclerView, mSubLocation);
                }
            });
            mSubDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {

                }
            });
        } else {
            if (subFolderAdapter != null) {
                subFolderAdapter.setData(b);
            }
        }

        WindowManager.LayoutParams layoutParams = mSubDialog.getWindow().getAttributes();
        layoutParams.gravity = Gravity.CENTER;

        layoutParams.height = (int) (screenWidth * mSubWidthRatio);
        layoutParams.width = (int) (screenWidth * mSubWidthRatio);
        layoutParams.dimAmount = 0.6f;

        mSubDialog.getWindow().setAttributes(layoutParams);//设置大小

        mSubDialog.show();
//        mSubCallBack.onDialogShow(mSubDialog,position);
    }

    SubFolderAdapter subFolderAdapter;


    private Dialog initSubDialog(List<Bean> b) {
        Dialog dialog = createSubDialog();

        View v = View.inflate(mContext, R.layout.dialog_sub_item, null);

        DragGridView mGridView = (DragGridView) v.findViewById(R.id.subGridView);

        mGridView.setOnDragListener(new SubDilaogOnDragListener());
        mGridView.setMergeSwitch(true);

        subFolderAdapter = new SubFolderAdapter(mContext, mGridView);
        mGridView.setAdapter(subFolderAdapter);

        subFolderAdapter.setData(b);

        dialog.setContentView(v);

        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();

        return dialog;
    }

    protected Dialog createSubDialog() {
        Dialog dialog = new Dialog(getContext(), R.style.ClassifyViewTheme);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        return dialog;
    }


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        if (mSubDialog != null && mSubDialog.isShowing()) {
            mSubDialog.dismiss();
        }
    }

    public class MainOnDragListener implements OnDragListener {

        @Override
        public boolean onDrag(View v, DragEvent event) {

            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_EXITED:
                    Log.i("SubDilaog", "MainOn ACTION_DRAG_EXITED");
                    break;
                case DragEvent.ACTION_DRAG_LOCATION:

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
//                    Log.i("ssssss"," dispatch ACTION_UP");
                        onStopDrag();
                        isDrag = false;
                    }

                    mHandler.removeCallbacks(mLongClickRunnable);
                    mHandler.removeCallbacks(mScrollRunnable);
                    mHandler.removeCallbacks(mItemLongClickRunnable);

                    if (isFolderStatus) {
                        isFolderStatus = false;

                        mDragAdapter.setDisplayMerge(-1, -1, getChildAt(folderStatusPosition - getFirstVisiblePosition()));
                        mDragAdapter.setmMergeItem(mDragPosition, folderStatusPosition);
                    }

                    break;
                case DragEvent.ACTION_DROP:
                    Log.i("SubDilaog", "MainOn ACTION_DROP");
                    break;
                default:
            }
            return true;
        }
    }

    public class SubDilaogOnDragListener implements OnDragListener {

        @Override
        public boolean onDrag(View v, DragEvent event) {

            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_EXITED:

                    Log.i("SubDilaog", "sub ACTION_DRAG_EXITED");

                    //当子文件拖出dialog 关闭dialog
                    if (mSubDialog != null && mSubDialog.isShowing()) {
                        mSubDialog.dismiss();
                    }
                    break;
            }
            return true;
        }
    }

}
