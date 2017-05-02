package com.example.ld_user.destdragview.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ld_user.destdragview.R;
import com.example.ld_user.destdragview.adapter.DragPageAdapter;
import com.example.ld_user.destdragview.adapter.MainDragAdapter;
import com.example.ld_user.destdragview.interfaces.DragFragmentListener;
import com.example.ld_user.destdragview.model.Bean;
import com.example.ld_user.destdragview.view.DragGridView.DragGridView;

import java.util.List;

/**
 * Created by chenlei on 2017/5/1.
 */

public class DragFragment extends  BaseDragFragment {


    private DragFragmentListener listener;

    private List<List<Bean>> data;

    private  DragGridView dragGridView;
    private MainDragAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View v = View.inflate(getActivity(), R.layout.fragment_drag_view,null);

        dragGridView = (DragGridView) v.findViewById(R.id.gridView);
         adapter = new MainDragAdapter(getActivity());

        dragGridView.setAdapter(adapter);

        if(listener!=null){
            listener.getGridView(dragGridView);
        }
        if(data!=null){
            adapter.setData(data);
            adapter.notifyDataSetChanged();
        }
        return v;
    }


    public void setData(List<List<Bean>> data) {

        this.data = data;

    }

    @Override
    public DragPageAdapter getAdapter() {
        return getAdapter();
    }


    @Override
    public DragGridView getGridView() {
        return dragGridView;
    }


    public void setDragFragmentListener( DragFragmentListener listener){
        this.listener = listener;
    }
}
