package org.dcxz.designdigger.framework;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

/**
 * Adapter包装类;<br/>
 * 支持重新载入数据并自动刷新{@link #setData(ArrayList)};<br/>
 * 支持在集合顶部添加数据并自动刷新{@link #addDataToTop(ArrayList)};<br/>
 * 支持在集合底部添加数据并自动刷新{@link #addDataToBottom(ArrayList)};<br/>
 * <br/>
 * Created by OvO on 2016/12/13.<br/>
 * ChangeLog :
 * <pre>
 * </pre>
 */

public abstract class Framework_Adapter<T> extends BaseAdapter {
    /**
     * 实例所持有的数据集合
     */
    protected ArrayList<T> data;
    /**
     * 布局充填器
     */
    protected LayoutInflater inflater;

    /**
     * @param context 用于初始化{@link #inflater}的上下文
     * @param data    将要被适配的数据集合
     */
    public Framework_Adapter(Context context, ArrayList<T> data) {
        this.data = data;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    /**
     * 重新载入数据并自动刷新
     *
     * @param newData 新的数据集合
     */
    public void setData(ArrayList<T> newData) {
        data = newData;
        notifyDataSetChanged();
    }

    /**
     * 在集合顶部添加数据并自动刷新
     *
     * @param newData 将要添加的数据
     */
    public void addDataToTop(ArrayList<T> newData) {
        data.addAll(0, newData);
        notifyDataSetChanged();
    }

    /**
     * 在集合底部添加数据并自动刷新
     *
     * @param newData 将要添加的数据
     */
    public void addDataToBottom(ArrayList<T> newData) {
        data.addAll(newData);
        notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getViewImp(position, convertView, parent);
    }

    /**
     * getView实现
     *
     * @param position    数据集合中的特定位置
     * @param convertView 重用控件
     * @param parent      这个控件最终将依附的控件组
     * @return 对应特定位置上数据的控件
     */
    protected abstract View getViewImp(int position, View convertView, ViewGroup parent);
}
