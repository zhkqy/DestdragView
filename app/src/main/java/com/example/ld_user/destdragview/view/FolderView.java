package com.example.ld_user.destdragview.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.ld_user.destdragview.R;
import com.example.ld_user.destdragview.model.Bean;
import com.example.ld_user.destdragview.model.FolderPlaceModel;
import com.example.ld_user.destdragview.utils.DisplayUtil;

import java.util.ArrayList;
import java.util.List;

import static android.R.attr.padding;

/**
 * Created by chenlei
 * <p>
 * 文件夹排列view
 */

public class FolderView extends View  {

    public int matrixWidth = 3;   //显示矩阵个数   3*3
    public int gap ; //间隙

    public int padding; //以备放大作用

    public boolean isDisplayMergeStatus = false;  //是否显示合并状态

    private List<Bean> data;
    public Context mContext;

    public ImageView backgroundImg;
    public ArrayList<FolderPlaceModel> folderPlaceModels = new ArrayList<>();

    public Paint p;

    public int minwidth = 0;

    public int viewWidth, viewHeight;


    public FolderView(Context context) {
        super(context);
    }

    public FolderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        padding = DisplayUtil.dipToPixels(mContext,3);
        gap = DisplayUtil.dipToPixels(mContext,5);
        backgroundImg = new ImageView(context);
        backgroundImg.setBackgroundResource(R.drawable.folder_icon);
        backgroundImg.setScaleType(ImageView.ScaleType.FIT_XY);
        p = new Paint();
    }

    public FolderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }


    public boolean isDisplayMergeStatus() {
        return isDisplayMergeStatus;
    }

    public void setDisplayMergeStatus(boolean displayMergeStatus) {
        isDisplayMergeStatus = displayMergeStatus;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        folderPlaceModels.clear();

        viewWidth = getMeasuredWidth();
        viewHeight = getMeasuredHeight();

        int min = viewWidth < viewHeight ? viewWidth : viewHeight;
        minwidth = (min - ((matrixWidth + 1) * gap) -2*padding) / matrixWidth;  //获得小块每个宽度

        if (data!=null && data.size() > 0) {

            for (int x = 0; x < data.size(); x++) {

                FolderPlaceModel model = new FolderPlaceModel();

                folderPlaceModels.add(model);

            }
        }
    }

    public void setData(List<Bean> data) {
        this.data = data;
        requestLayout();
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {

        Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.board_default_gridview);

        if (folderPlaceModels.size() > 0) {
            if (folderPlaceModels.size() == 1) {

                if(isDisplayMergeStatus){
                    Bitmap background  = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.folder_icon);
                    canvas.drawBitmap(background, null, new Rect(0, 0, viewWidth, viewHeight), new Paint());
                    canvas.drawBitmap(bitmap, null, new Rect(0+padding, 0+padding, viewWidth-padding, viewHeight-padding), new Paint());

                }else{
                    canvas.drawBitmap(bitmap, null, new Rect(0+padding, 0+padding, viewWidth-padding, viewHeight-padding), new Paint());
                }
            } else {

                if(isDisplayMergeStatus){
                    Bitmap background  = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.folder_icon);
                    canvas.drawBitmap(background, null, new Rect(0, 0, viewWidth, viewHeight), new Paint());
                }else{
                    Bitmap background  = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.folder_icon);
                    canvas.drawBitmap(background, null, new Rect(0+padding, 0+padding, viewWidth-padding, viewHeight-padding), new Paint());
                }

                int size = folderPlaceModels.size()<(matrixWidth*matrixWidth)?folderPlaceModels.size():matrixWidth*matrixWidth;
                for (int x = 0; x < size; x++) {

                    int num = x % matrixWidth;
                    int shu = x / matrixWidth;
                    int left = (num * minwidth) + (num * gap)+gap;
                    int top = (shu * minwidth) + (shu * gap)+gap;

                    canvas.drawBitmap(bitmap,
                           null,
                            new Rect(left+padding, top+padding, left+minwidth+padding,  top+minwidth+padding), null);

                }

            }
        }
    }

}
