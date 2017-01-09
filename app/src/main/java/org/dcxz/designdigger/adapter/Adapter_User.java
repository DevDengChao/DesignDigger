package org.dcxz.designdigger.adapter;

import android.annotation.SuppressLint;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.dcxz.designdigger.App;
import org.dcxz.designdigger.R;
import org.dcxz.designdigger.entity.Entity_Shot;
import org.dcxz.designdigger.entity.Entity_User;
import org.dcxz.designdigger.util.API;
import org.dcxz.designdigger.view.AutoHeightGifImageView;

import java.lang.reflect.Type;
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

@SuppressWarnings({"unused", "WeakerAccess"})
public class Adapter_User extends RecyclerView.Adapter {
    public static final String TAG = "Adapter_User";
    public static final int HEADER = 0;
    public static final int NORMAL = 1;
    //    public static final int FOOTER = 2;
    private LayoutInflater inflater;
    private ArrayList<Entity_Shot> data;
    private static Entity_User user;
    /**
     * 头部视图
     */
    private View headerView;
    /**
     * Item视图
     */
    private View normalView;
    /**
     * ArrayList<Entity_Shot>的类型
     */
    private Type type;
    private Gson gson;

    public Adapter_User(LayoutInflater inflater, Entity_User user) {
        this.inflater = inflater;
        Adapter_User.user = user;
        data = new ArrayList<>();
        gson = new Gson();
        type = new TypeToken<ArrayList<Entity_Shot>>() {
        }.getType();
    }

    /**
     * 查询该用户指定页的shots
     *
     * @param page     将查询的页码
     * @param listener 查询成功的监听器
     */
    public void queryPage(final int page, final OnQueryPageSuccessListener listener) {
        App.stringRequest(
                String.format(API.EndPoint.USERS_SHOTS_PAGE, user.getId(), page),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i(TAG, "onResponse: query user's shots success at page " + page);
                        ArrayList<Entity_Shot> shots = gson.fromJson(response, type);
                        for (Entity_Shot shot : shots) {
                            //"2015-05-29T08:59:36Z" -> "2015-05-29 08:59:36"
                            shot.setCreated_at(shot.getCreated_at().replace("T", " ").replace("Z", ""));
                        }
                        int start = data.size();
                        data.addAll(shots);
                        notifyItemRangeInserted(start, shots.size());
                        listener.onQueryPageSuccess();
                    }
                },
                null, TAG);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case HEADER:
                headerView = inflater.inflate(R.layout.profile_header, parent, false);
                return new ViewHolder(headerView);
            default:
                normalView = inflater.inflate(R.layout.item, parent, false);
                return new ViewHolder(normalView);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (position != 0) {
            Entity_Shot temp = data.get(position - 1);
            String imagePath = temp.getImages().getNormal();
            initViewHolder((ViewHolder) holder, temp, imagePath);
        }
    }

    @SuppressLint("SetTextI18n")
    private void initViewHolder(ViewHolder viewHolder, Entity_Shot temp, String imagePath) {
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
        return data.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return HEADER;
        } else {
            return NORMAL;
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_avatar)
        CircleImageView avatar;
        @BindView(R.id.item_content)
        AutoHeightGifImageView content;
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
        @BindView(R.id.item_userName)
        TextView userName;
        @BindView(R.id.item_title)
        TextView title;
        @BindView(R.id.item_time)
        TextView time;

        @SuppressWarnings("deprecation")
        @SuppressLint("SetTextI18n")
        ViewHolder(View itemView) {
            super(itemView);
            if (headerView == itemView && user != null) {
                App.imageRequest(user.getAvatar_url(), (CircleImageView) itemView.findViewById(R.id.profile_avatar), TAG);// TODO: 2017/1/6 test this
//                ((CircleImageView) itemView.findViewById(R.id.profile_avatar)).setImageBitmap(manager.getAvatar());
                ((TextView) itemView.findViewById(R.id.profile_bucketsCount)).setText(user.getBuckets_count() + "");
                ((TextView) itemView.findViewById(R.id.profile_followersCount)).setText(user.getFollowers_count() + "");
                ((TextView) itemView.findViewById(R.id.profile_followingCount)).setText(user.getFollowings_count() + "");
                ((TextView) itemView.findViewById(R.id.profile_introduction)).setText(Html.fromHtml(user.getBio()));
                ((TextView) itemView.findViewById(R.id.profile_location)).setText(user.getLocation());
                ((TextView) itemView.findViewById(R.id.profile_shotsCount)).setText(user.getShots_count() + "");
                ((TextView) itemView.findViewById(R.id.profile_userName)).setText(user.getUsername());
            } else if (normalView == itemView) {
                ButterKnife.bind(this, itemView);
            }
        }
    }

    public interface OnQueryPageSuccessListener {
        void onQueryPageSuccess();
    }
}
