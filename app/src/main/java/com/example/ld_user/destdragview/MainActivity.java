package com.example.ld_user.destdragview;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.ld_user.destdragview.adapter.DragPageAdapter;
import com.example.ld_user.destdragview.fragment.BaseDragFragment;
import com.example.ld_user.destdragview.fragment.DragFragment;
import com.example.ld_user.destdragview.model.Bean;
import com.example.ld_user.destdragview.utils.DataGenerate;
import com.example.ld_user.destdragview.view.DragViewPager;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

public class MainActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {

    private List<List<List<Bean>>> beans = new ArrayList<>();


    private List<BaseDragFragment> fragments = new ArrayList<>();

    DragViewPager mViewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mViewPager = (DragViewPager) findViewById(R.id.viewpager);

        beans.clear();
        fragments.clear();

        for(int x = 0;x<8;x++){
            beans.add(DataGenerate.generateBean());
        }

        for(int x = 0;x<beans.size();x++){
            DragFragment fragment  = new DragFragment();
            fragment.setData(beans.get(x));
            fragments.add(fragment);
        }

        DragPageAdapter dragPageAdapter = new DragPageAdapter(getSupportFragmentManager(),fragments);
        mViewPager.setAdapter(dragPageAdapter);

        mViewPager.setOnPageChangeListener(this);
        mViewPager.setPagerCurrentItem(0);
    }


    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

        Log.i("wewewew","position = "+position);
        mViewPager.setPagerCurrentItem(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
