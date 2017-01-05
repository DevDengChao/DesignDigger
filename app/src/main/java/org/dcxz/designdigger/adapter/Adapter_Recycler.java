package org.dcxz.designdigger.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.dcxz.designdigger.App;
import org.dcxz.designdigger.R;
import org.dcxz.designdigger.dao.Dao_Manager;
import org.dcxz.designdigger.entity.Entity_Shot;
import org.dcxz.designdigger.entity.Entity_User;
import org.dcxz.designdigger.view.AutoHeightGifImageView;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * <br/>
 * Created by OvO on 2017/1/5.<br/>
 * ChangeLog :
 * <pre>
 * </pre>
 */

public class Adapter_Recycler<Holder> extends RecyclerView.Adapter {
    public static final String TAG="Adapter_Recycler";
    public static final int HEADER = 0;
    public static final int NORMAL = 1;
    private static Dao_Manager manager;
    //    public static final int FOOTER = 2;
    private LayoutInflater inflater;
    private ArrayList<Entity_Shot> data;
    private static Entity_User user;
    private static View headerView;
    private static View normalView;

    public Adapter_Recycler(Context context, Entity_User player) {
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        manager = Dao_Manager.getInstance(context);
        user = player;
//        App.stringRequest(API.EndPoint.);
    }

    @Override
    public Adapter_Recycler.Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case HEADER:
                headerView = inflater.inflate(R.layout.profile_header, parent, false);
                return new Adapter_Recycler.Holder(headerView);
            default:
                normalView = inflater.inflate(R.layout.item, parent, false);
                return new Adapter_Recycler.Holder(normalView);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Entity_Shot temp=data.get(position-1);// TODO: 2017/1/5 complete this class
        String imagePath = temp.getImages().getNormal();
        initView((Adapter_Recycler.Holder) holder, temp, imagePath);
    }

    @SuppressLint("SetTextI18n")
    private void initView(Adapter_Recycler.Holder holder, Entity_Shot temp, String imagePath) {
        holder.avatar.setImageResource(R.drawable.progress_rotate);//使用图像占位,避免重用过程中出现图像突变现象
        App.imageRequest(temp.getUser().getAvatar_url(), holder.avatar, TAG);

        holder.content.setImageResource(R.mipmap.item_content);//使用图像占位,避免重用过程中出现图像突变现象
        holder.content.setTag(imagePath);
        if (temp.isAnimated()) {
            App.gifRequest(imagePath, holder.content, holder.gif, TAG);
            holder.gif.setVisibility(View.VISIBLE);
        } else {
            App.imageRequest(imagePath, holder.content, TAG);
            holder.gif.setVisibility(View.INVISIBLE);
            holder.gif.setOnClickListener(null);
        }
        if (temp.getRebounds_count() == 0) {
            holder.rebound.setVisibility(View.INVISIBLE);
        } else {
            holder.rebound.setVisibility(View.VISIBLE);
            holder.attachment.setText(temp.getRebounds_count() + "");
        }
        if (temp.getAttachments_count() == 0) {
            holder.attachment.setVisibility(View.INVISIBLE);
        } else {
            holder.attachment.setVisibility(View.VISIBLE);
            holder.attachment.setText(temp.getAttachments_count() + "");
        }
        holder.view.setText(temp.getViews_count() + "");// 1 -> "1"
        holder.comment.setText(temp.getComments_count() + "");
        holder.like.setText(temp.getLikes_count() + "");
        holder.userName.setText(temp.getUser().getUsername());
        holder.title.setText(temp.getTitle());
        holder.time.setText(temp.getCreated_at());
    }


    @Override
    public int getItemCount() {
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return HEADER;
        } else {
            return NORMAL;
        }
    }

    private static class Holder extends RecyclerView.ViewHolder {
        CircleImageView avatar;
        AutoHeightGifImageView content;
        ImageView gif;

        TextView rebound;
        TextView attachment;

        TextView view;
        TextView comment;
        TextView like;

        TextView userName;
        TextView title;
        TextView time;

        @SuppressWarnings("deprecation")
        @SuppressLint("SetTextI18n")
        Holder(View itemView) {
            super(itemView);
            if (headerView == itemView && user != null) {
                ((CircleImageView) itemView.findViewById(R.id.profile_avatar)).setImageBitmap(manager.getAvatar());
                ((TextView) itemView.findViewById(R.id.profile_bucketsCount)).setText(user.getBuckets_count() + "");
                ((TextView) itemView.findViewById(R.id.profile_followersCount)).setText(user.getFollowers_count() + "");
                ((TextView) itemView.findViewById(R.id.profile_followingCount)).setText(user.getFollowings_count() + "");
                ((TextView) itemView.findViewById(R.id.profile_introduction)).setText(Html.fromHtml(user.getBio()));
                ((TextView) itemView.findViewById(R.id.profile_location)).setText(user.getLocation());
                ((TextView) itemView.findViewById(R.id.profile_shotsCount)).setText(user.getShots_count() + "");
                ((TextView) itemView.findViewById(R.id.profile_userName)).setText(user.getUsername());
            } else if (normalView == itemView) {
                avatar = (CircleImageView) itemView.findViewById(R.id.item_avatar);
                content = (AutoHeightGifImageView) itemView.findViewById(R.id.item_content);
                gif = (ImageView) itemView.findViewById(R.id.item_gif);
                rebound = (TextView) itemView.findViewById(R.id.item_rebound);
                attachment = (TextView) itemView.findViewById(R.id.item_attachment);
                view = (TextView) itemView.findViewById(R.id.item_view);
                comment = (TextView) itemView.findViewById(R.id.item_comment);
                like = (TextView) itemView.findViewById(R.id.item_like);

                userName = (TextView) itemView.findViewById(R.id.item_userName);
                title = (TextView) itemView.findViewById(R.id.item_title);
                time = (TextView) itemView.findViewById(R.id.item_time);
            }
        }
    }

}
