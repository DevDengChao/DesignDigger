package org.dcxz.designdigger.framework;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * <br/>
 * Created by OvO on 2017/1/9.<br/>
 * ChangeLog :
 * <pre>
 * </pre>
 */

public abstract class BaseRecyclerViewAdapter<T> extends RecyclerView.Adapter {
    /**
     * 适配器持有的数据集合
     */
    protected ArrayList<T> data;
    /**
     * 用于提供上下文,获取layoutInflater,设置监听器
     */
    private BaseActivity activity;
    /**
     * 当前是否为Wifi网络环境
     */
    private boolean isWifiNetwork;

    public BaseRecyclerViewAdapter(BaseActivity activity, ArrayList<T> data) {
        this.activity = activity;
        this.data = data;
        NetworkInfo networkInfo = ((ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            isWifiNetwork = true;
        }
    }

    public ArrayList<T> getData() {
        return data;
    }

    /**
     * 更新数据集合<br/>
     * 自动notifyItemRangeChanged
     *
     * @param data 新的数据集合
     */
    public void setData(ArrayList<T> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    /**
     * 追加新数据至集合末尾<br/>
     * 自动notifyItemRangeInserted
     *
     * @param newData 要追加的新数据
     */
    public void addDataToBottom(ArrayList<T> newData) {
        int start = data.size();
        data.addAll(newData);
        notifyItemRangeInserted(start, newData.size());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public final RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return onCreateViewHolderImp(parent, viewType, activity.getLayoutInflater());
    }

    protected abstract RecyclerView.ViewHolder onCreateViewHolderImp(ViewGroup parent, int viewType, LayoutInflater inflater);

    @Override
    public final void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        onBindViewHolderImp(holder, position, data, activity, isWifiNetwork);
    }

    protected abstract void onBindViewHolderImp(RecyclerView.ViewHolder holder, int position, ArrayList<T> data, BaseActivity activity, boolean isWifiNetwork);
}
