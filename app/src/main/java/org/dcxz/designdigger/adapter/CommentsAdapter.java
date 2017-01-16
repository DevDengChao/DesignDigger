package org.dcxz.designdigger.adapter;

import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import org.dcxz.designdigger.R;
import org.dcxz.designdigger.bean.CommentInfo;
import org.dcxz.designdigger.bean.ImageInfo;
import org.dcxz.designdigger.bean.ShotInfo;
import org.dcxz.designdigger.dao.DaoManager;
import org.dcxz.designdigger.framework.BaseActivity;
import org.dcxz.designdigger.framework.BaseRecyclerViewAdapter;

import java.util.ArrayList;

/**
 * <br/>
 * Created by OvO on 2017/1/15.<br/>
 * ChangeLog :
 * <pre>
 * </pre>
 */

public class CommentsAdapter extends BaseRecyclerViewAdapter<CommentInfo> {
    /**
     * viewType,控制item的布局与viewHolder的更新
     */
    private static final int SHOT = 0;
    /**
     * viewType,控制item的布局与viewHolder的更新
     */
    private static final int COMMENT = 1;
    /**
     * 图像质量
     */
    private static String light, normal, large;
    private static DaoManager manager;
    private String tag;
    /**
     * 头部展示的ShotInfo对象
     */
    private ShotInfo shotInfo;

    public CommentsAdapter(BaseActivity activity, ArrayList<CommentInfo> data, ShotInfo shotInfo, String tag) {
        super(activity, data);
        this.shotInfo = shotInfo;
        this.tag = tag;
        manager = DaoManager.getInstance(activity);
        Resources resources = activity.getResources();
        light = resources.getString(R.string.settings_image_quality_light);
        normal = resources.getString(R.string.settings_image_quality_normal);
        large = resources.getString(R.string.settings_image_quality_large);
    }

    @Override
    protected RecyclerView.ViewHolder onCreateViewHolderImp(ViewGroup parent, int viewType, LayoutInflater inflater) {
        if (viewType == SHOT) {
            return new ShotHolder(inflater.inflate(R.layout.item_shot, parent, false), tag);
        } else {
            return new CommentHolder(inflater.inflate(R.layout.item_comment, parent, false), tag);
        }
    }

    @Override
    protected void onBindViewHolderImp(RecyclerView.ViewHolder holder, int position, ArrayList<CommentInfo> data, BaseActivity activity, boolean isWifiNetwork) {
        if (position == 0) {
            ((ShotHolder) holder).update(shotInfo, getImagePath(shotInfo, isWifiNetwork), activity, false);
        } else {
            ((CommentHolder) holder).update(data.get(position - 1));
        }
    }

    /**
     * 根据网络环境与个人偏向获取图像地址
     */
    private String getImagePath(ShotInfo shotInfo, boolean isWifiNetWork) {
        ImageInfo imageInfo = shotInfo.getImages();
        String preference;
        if (isWifiNetWork) {
            preference = manager.getDetailImageQualityWifi();
        } else {
            preference = manager.getDetailImageQualityMobile();
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
    public int getItemViewType(int position) {
        if (position == 0) {
            return SHOT;
        } else {
            return COMMENT;
        }
    }

    @Override
    public int getItemCount() {
        return data.size() + 1;
    }
}
