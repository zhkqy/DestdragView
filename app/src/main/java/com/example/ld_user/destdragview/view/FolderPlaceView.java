package com.example.ld_user.destdragview.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.example.ld_user.destdragview.R;
import com.example.ld_user.destdragview.model.Bean;
import com.example.ld_user.destdragview.model.FolderPlaceModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ld-user on 2017/4/27.
 * <p>
 * 文件夹排列view
 */

public class FolderPlaceView extends View {

    public int matrixWidth = 3;   //显示矩阵个数   3*3
    public int gap = 8; //间隙
    private List<Bean> data;
    public Context mContext;

    public ImageView backgroundImg;
    public ArrayList<FolderPlaceModel> folderPlaceModels = new ArrayList<>();

    public Paint p;

    public int minwidth = 0;

    public int viewWidth, viewHeight;


    public FolderPlaceView(Context context) {
        super(context);
    }

    public FolderPlaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        backgroundImg = new ImageView(context);
        backgroundImg.setBackgroundResource(R.drawable.folder_icon);
        backgroundImg.setScaleType(ImageView.ScaleType.FIT_XY);
        p = new Paint();
    }

    public FolderPlaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        Log.i("ssssss","onLayout1111");
        folderPlaceModels.clear();

        viewWidth = getMeasuredWidth();
        viewHeight = getMeasuredHeight();

        int min = viewWidth < viewHeight ? viewWidth : viewHeight;
        minwidth = (min - ((matrixWidth + 1) * gap)) / matrixWidth;  //获得小块每个宽度

        if (data.size() > 0) {

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
        Log.i("ssssss","onDraw");

        Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.board_default_gridview);

        if (folderPlaceModels.size() > 0) {
            if (folderPlaceModels.size() == 1) {
                canvas.drawBitmap(bitmap, new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()), new Rect(0, 0, viewWidth, viewHeight), new Paint());
            } else {

                Bitmap background  = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.folder_icon);
                canvas.drawBitmap(background, new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()), new Rect(0, 0, viewWidth, viewHeight), new Paint());

                for (int x = 0; x < folderPlaceModels.size(); x++) {

                    int num = x % matrixWidth;
                    int shu = x / matrixWidth;
                    int left = (num * minwidth) + (num * gap)+gap;
                    int top = (shu * minwidth) + (shu * gap)+gap;

                    canvas.drawBitmap(bitmap, new Rect(left, top, left+bitmap.getWidth(), top+bitmap.getHeight()),
                            new Rect(left, top, left+minwidth,  top+minwidth), null);

                }

            }
        }
    }
}
