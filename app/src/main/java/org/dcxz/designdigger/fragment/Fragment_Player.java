package org.dcxz.designdigger.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Message;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import org.dcxz.designdigger.R;
import org.dcxz.designdigger.dao.Dao_Manager;
import org.dcxz.designdigger.entity.Entity_User;
import org.dcxz.designdigger.framework.Framework_Fragment;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * <br/>
 * Created by DC on 2017/1/2.<br/>
 */

public class Fragment_Player extends Framework_Fragment {
    public static final String TAG = "Fragment_Player";

    @Override
    protected int setContentViewImp() {
        return R.layout.fragment_player;
    }

    @SuppressWarnings("deprecation")
    @SuppressLint("SetTextI18n")
    @Override
    protected void initView(Activity activity, View view) {
        Dao_Manager manager = Dao_Manager.getInstance(activity);
        Entity_User user = manager.getUser();
        if (user != null) {
            ((CircleImageView) view.findViewById(R.id.player_avatar)).setImageBitmap(manager.getAvatar());
            ((TextView) view.findViewById(R.id.player_bucketsCount)).setText(user.getBuckets_count() + "");
            ((TextView) view.findViewById(R.id.player_followersCount)).setText(user.getFollowers_count() + "");
            ((TextView) view.findViewById(R.id.player_followingCount)).setText(user.getFollowings_count() + "");
            ((TextView) view.findViewById(R.id.player_introduction)).setText(Html.fromHtml(user.getBio()));
            ((TextView) view.findViewById(R.id.player_location)).setText(user.getLocation());
            ((TextView) view.findViewById(R.id.player_shotsCount)).setText(user.getShots_count() + "");
            ((TextView) view.findViewById(R.id.player_userName)).setText(user.getUsername());
        }
    }

    @Override
    protected void initData(Activity activity) {

    }

    @Override
    protected void initAdapter(Activity activity) {

    }

    @Override
    protected void initListener(Activity activity) {

    }

    @Override
    public void handleMessageImp(Message msg) {

    }
}
