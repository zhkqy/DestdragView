package com.example.ld_user.destdragview.fragment;


import android.support.v4.app.Fragment;

import com.example.ld_user.destdragview.adapter.DragPageAdapter;
import com.example.ld_user.destdragview.interfaces.DragFragmentListener;
import com.example.ld_user.destdragview.model.Bean;
import com.example.ld_user.destdragview.view.DragGridView.DragGridView;

import java.util.List;

/**
 * Created by chenlei on 2017/5/1.
 */

public abstract class BaseDragFragment extends Fragment {


    public abstract DragPageAdapter getAdapter( );

    public abstract void setDragFragmentListener( DragFragmentListener listener);

    public abstract DragGridView getGridView() ;


}
