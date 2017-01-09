package org.dcxz.designdigger.adapter;

import android.annotation.SuppressLint;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.dcxz.designdigger.R;
import org.dcxz.designdigger.app.App;
import org.dcxz.designdigger.bean.ShotInfo;
import org.dcxz.designdigger.bean.UserInfo;
import org.dcxz.designdigger.framework.BaseRecyclerViewAdapter;
import org.dcxz.designdigger.view.AutoHeightGifImageView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * <br/>
 * Created by OvO on 2017/1/5.<br/>
 * ChangeLog :
 * <pre>
 * </pre>
 */

public class ShotsAdapter extends BaseRecyclerViewAdapter<ShotInfo> {
    public static final String TAG = "ShotsAdapter";
    private static final int HEADER = 0;
    private static final int NORMAL = 1;
    private UserInfo user = null;

    public ShotsAdapter(LayoutInflater inflater, ArrayList<ShotInfo> data) {
        super(inflater, data);
    }

    public ShotsAdapter(LayoutInflater inflater, ArrayList<ShotInfo> data, UserInfo user) {
        super(inflater, data);
        this.user = user;
    }

    @Override
    protected RecyclerView.ViewHolder onCreateViewHolderImp(ViewGroup parent, int viewType, LayoutInflater inflater) {
        switch (viewType) {
            case HEADER:
                return new ViewHolder(inflater.inflate(R.layout.profile_header, parent, false), user);
            default:
                return new ViewHolder(inflater.inflate(R.layout.item, parent, false));
        }
    }

    @Override
    protected void onBindViewHolderImp(RecyclerView.ViewHolder holder, int position, ArrayList<ShotInfo> data) {
        ShotInfo temp;
        if (user == null) {//没有用户对象,只显示item
            temp = data.get(position);
            String imagePath = temp.getImages().getNormal();
            updateViewHolder((ViewHolder) holder, temp, imagePath);
        } else if (position != 0) {//有用户对象且不是头部,更新viewHolder
            temp = data.get(position - 1);//由于头部的存在,需要修正对应关系
            String imagePath = temp.getImages().getNormal();
            updateViewHolder((ViewHolder) holder, temp, imagePath);
        }//有用户对象且是头部,什么也不做
    }

    @SuppressLint("SetTextI18n")
    private void updateViewHolder(ViewHolder viewHolder, ShotInfo temp, String imagePath) {
        viewHolder.avatar.setImageResource(R.drawable.progress_rotate);//使用图像占位,避免重用过程中出现图像突变现象
        App.imageRequest(temp.getUser().getAvatar_url(), viewHolder.avatar, TAG);

        viewHolder.content.setImageResource(R.mipmap.item_content);//使用图像占位,避免重用过程中出现图像突变现象
        viewHolder.content.setTag(imagePath);
        if (temp.isAnimated()) {
            App.gifRequest(imagePath, viewHolder.content, viewHolder.gif, TAG);
            viewHolder.gif.setVisibility(View.VISIBLE);
        } else {
            App.imageRequest(imagePath, viewHolder.content, TAG);
            viewHolder.gif.setVisibility(View.INVISIBLE);
            viewHolder.gif.setOnClickListener(null);
        }
        if (temp.getRebounds_count() == 0) {
            viewHolder.rebound.setVisibility(View.INVISIBLE);
        } else {
            viewHolder.rebound.setVisibility(View.VISIBLE);
            viewHolder.attachment.setText(temp.getRebounds_count() + "");
        }
        if (temp.getAttachments_count() == 0) {
            viewHolder.attachment.setVisibility(View.INVISIBLE);
        } else {
            viewHolder.attachment.setVisibility(View.VISIBLE);
            viewHolder.attachment.setText(temp.getAttachments_count() + "");
        }
        viewHolder.view.setText(temp.getViews_count() + "");// 1 -> "1"
        viewHolder.comment.setText(temp.getComments_count() + "");
        viewHolder.like.setText(temp.getLikes_count() + "");
        viewHolder.userName.setText(temp.getUser().getUsername());
        viewHolder.title.setText(temp.getTitle());
        viewHolder.time.setText(temp.getCreated_at());
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
            return HEADER;
        } else {
            return NORMAL;
        }
    }

    @SuppressWarnings("WeakerAccess")
    class ViewHolder extends RecyclerView.ViewHolder {
        /**
         * 头像
         */
        @BindView(R.id.item_avatar)
        CircleImageView avatar;
        /**
         * 作品
         */
        @BindView(R.id.item_content)
        AutoHeightGifImageView content;
        /**
         * Gif控制按键
         */
        @BindView(R.id.item_gif)
        ImageView gif;

        @BindView(R.id.item_rebound)
        TextView rebound;
        @BindView(R.id.item_attachment)
        TextView attachment;
        @BindView(R.id.item_view)
        TextView view;
        @BindView(R.id.item_comment)
        TextView comment;
        @BindView(R.id.item_like)
        TextView like;
        /**
         * 用户名
         */
        @BindView(R.id.item_userName)
        TextView userName;
        /**
         * 作品名
         */
        @BindView(R.id.item_title)
        TextView title;
        /**
         * 作品创作时间
         */
        @BindView(R.id.item_time)
        TextView time;

        /**
         * 没有用户对象时使用的构造器
         *
         * @param itemView 将要修改的视图
         */
        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        /**
         * 有用户对象时使用的构造器,会产生类似ListView头部的结构
         *
         * @param itemView 将要修改的视图
         * @param user     用户对象
         */
        @SuppressLint("SetTextI18n")
        ViewHolder(View itemView, UserInfo user) {
            super(itemView);
            App.imageRequest(user.getAvatar_url(), (CircleImageView) itemView.findViewById(R.id.profile_avatar), TAG);// TODO: 2017/1/6 test this
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

}
