package org.dcxz.designdigger.adapter;

import android.annotation.SuppressLint;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import org.dcxz.designdigger.R;
import org.dcxz.designdigger.app.App;
import org.dcxz.designdigger.bean.UserInfo;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * <br/>
 * Created by OvO on 2017/1/15.<br/>
 * ChangeLog :
 * <pre>
 * </pre>
 */

@SuppressWarnings("WeakerAccess")
public class ProfileHolder extends RecyclerView.ViewHolder {

    /**
     * 有用户对象时使用的构造器,会产生类似ListView头部的结构
     *
     * @param itemView 将要修改的视图
     * @param user     用户对象
     */
    @SuppressLint("SetTextI18n")
    public ProfileHolder(View itemView, UserInfo user, String Tag) {
        super(itemView);
        App.imageRequest(user.getAvatar_url(), (CircleImageView) itemView.findViewById(R.id.profile_avatar), Tag);
        ((TextView) itemView.findViewById(R.id.profile_bucketsCount)).setText(user.getBuckets_count() + "");
        ((TextView) itemView.findViewById(R.id.profile_followersCount)).setText(user.getFollowers_count() + "");
        ((TextView) itemView.findViewById(R.id.profile_followingCount)).setText(user.getFollowings_count() + "");
        //noinspection deprecation
        ((TextView) itemView.findViewById(R.id.profile_introduction)).setText(Html.fromHtml(user.getBio()));
        ((TextView) itemView.findViewById(R.id.profile_location)).setText(user.getLocation());
        ((TextView) itemView.findViewById(R.id.profile_shotsCount)).setText(user.getShots_count() + "");
        ((TextView) itemView.findViewById(R.id.profile_userName)).setText(user.getUsername());
    }
}