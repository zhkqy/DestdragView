package com.example.ld_user.destdragview.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.example.ld_user.destdragview.fragment.BaseDragFragment;

import java.util.List;

/**
 * Created by chenlei on 2017/5/1.
 */

public class DragPageAdapter extends PagerAdapter {


    private FragmentManager manager;
    private List<? extends BaseDragFragment> fragments;


    public DragPageAdapter(FragmentManager manager,List<? extends BaseDragFragment> fragments){
        this.manager = manager;
        this.fragments = fragments;
    }

    @Override
    public int getCount() {
        return fragments == null ? 0 : fragments.size();
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(fragments.get(position).getView());
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = fragments.get(position);
        if (!fragment.isAdded()) {
            FragmentTransaction ft = manager.beginTransaction();
            ft.add(fragment, fragment.getClass().getSimpleName());
            ft.commitAllowingStateLoss();
            manager.executePendingTransactions();
        }
        if (null == fragment.getView().getParent()) {
            container.addView(fragment.getView());
        }
        return fragment.getView();
    }


    public List<? extends BaseDragFragment> getFragments() {
        return fragments;
    }

    public BaseDragFragment getFragment(int position) {
        return fragments.get(position);
    }
}
