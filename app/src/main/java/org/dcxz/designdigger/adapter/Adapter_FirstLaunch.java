package org.dcxz.designdigger.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Activity_FirstLaunch需要的适配器
 * <br/>
 * Created by DC on 2016/12/17.<br/>
 */

public class Adapter_FirstLaunch<T extends View> extends PagerAdapter {
    private ArrayList<T> data;

    public Adapter_FirstLaunch(ArrayList<T> data) {
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(data.get(position));
        return data.get(position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(data.get(position));
    }
}
