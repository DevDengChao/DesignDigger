package org.dcxz.designdigger.adapter;

import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import org.dcxz.designdigger.R;
import org.dcxz.designdigger.bean.ImageInfo;
import org.dcxz.designdigger.bean.ShotInfo;
import org.dcxz.designdigger.bean.UserInfo;
import org.dcxz.designdigger.dao.DaoManager;
import org.dcxz.designdigger.framework.BaseActivity;
import org.dcxz.designdigger.framework.BaseRecyclerViewAdapter;

import java.util.ArrayList;

/**
 * <br/>
 * Created by OvO on 2017/1/5.<br/>
 * ChangeLog :
 * <pre>
 * </pre>
 */

public class ShotsAdapter extends BaseRecyclerViewAdapter<ShotInfo> {
    /**
     * viewType,控制item的布局与viewHolder的更新
     */
    private static final int PROFILE = 0;
    /**
     * viewType,控制item的布局与viewHolder的更新
     */
    private static final int SHOT = 1;
    /**
     * 图像质量
     */
    private static String light, normal, large;
    private static DaoManager manager;
    /**
     * 请求标签,用于取消未完成的请求
     */
    private String tag;
    /**
     * 用户对象
     */
    private UserInfo user;

    /**
     * @param activity 用于获取layoutInflater,设置监听器
     * @param data     适配器持有的数据集合
     * @param user     null:只显示Shots;not null:显示头部布局以及Shots
     * @param tag      请求标签,用于取消未完成的请求
     */
    public ShotsAdapter(BaseActivity activity, ArrayList<ShotInfo> data, UserInfo user, String tag) {
        super(activity, data);
        this.user = user;
        this.tag = tag;
        manager = DaoManager.getInstance(activity);
        Resources resources = activity.getResources();
        light = resources.getString(R.string.settings_image_quality_light);
        normal = resources.getString(R.string.settings_image_quality_normal);
        large = resources.getString(R.string.settings_image_quality_large);
    }

    @Override
    protected RecyclerView.ViewHolder onCreateViewHolderImp(ViewGroup parent, int viewType, LayoutInflater inflater) {
        switch (viewType) {
            case PROFILE:
                return new ProfileHolder(inflater.inflate(R.layout.item_header, parent, false), user, tag);
            default:
                return new ShotHolder(inflater.inflate(R.layout.item_shot, parent, false), tag);
        }
    }

    @Override
    protected void onBindViewHolderImp(RecyclerView.ViewHolder holder, int position, ArrayList<ShotInfo> data, BaseActivity activity, boolean isWifiNetwork) {
        ShotInfo temp;
        if (user == null) {//没有用户对象,只显示item
            temp = data.get(position);
            ((ShotHolder) holder).update(temp, getImagePath(temp, isWifiNetwork), activity, true);
        } else if (position != 0) {//有用户对象且不是头部,更新viewHolder
            temp = data.get(position - 1);//由于头部的存在,需要修正对应关系
            ((ShotHolder) holder).update(temp, getImagePath(temp, isWifiNetwork), activity, true);
        }//有用户对象且是头部,什么也不做
    }

    /**
     * 根据网络环境与个人偏向获取图像地址
     */
    private String getImagePath(ShotInfo shotInfo, boolean isWifiNetWork) {
        ImageInfo imageInfo = shotInfo.getImages();
        String preference;
        if (isWifiNetWork) {
            preference = manager.getPreviewImageQualityWifi();
        } else {
            preference = manager.getPreviewImageQualityMobile();
        }
        if (preference.equals(light)) {
            return imageInfo.getTeaser();
        } else if (preference.equals(large) && imageInfo.getHidpi() != null) {
            return imageInfo.getHidpi();
        } else {
            return imageInfo.getNormal();
        }
    }

    @Override
    public int getItemCount() {
        if (user == null) {
            return data.size();
        } else {
            return data.size() + 1;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0 && user != null) {
            return PROFILE;
        } else {
            return SHOT;
        }
    }

}
