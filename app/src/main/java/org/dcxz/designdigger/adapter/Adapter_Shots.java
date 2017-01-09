package org.dcxz.designdigger.adapter;

import android.annotation.SuppressLint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.dcxz.designdigger.App;
import org.dcxz.designdigger.R;
import org.dcxz.designdigger.entity.Entity_Shot;
import org.dcxz.designdigger.view.AutoHeightGifImageView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * <br/>
 * Created by OvO on 2017/1/9.<br/>
 * ChangeLog :
 * <pre>
 * </pre>
 */

public class Adapter_Shots extends RecyclerView.Adapter {
    public static final String TAG = "Adapter_Shots";
    private LayoutInflater inflater;
    private ArrayList<Entity_Shot> data;

    public Adapter_Shots(LayoutInflater inflater, ArrayList<Entity_Shot> data) {
        this.inflater = inflater;
        this.data = data;
    }

    public void setData(ArrayList<Entity_Shot> data) {
        this.data = data;
    }


    public void addDataToBottom(ArrayList<Entity_Shot> shots) {
        int start = data.size();
        data.addAll(shots);
        notifyItemRangeChanged(start, shots.size());
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(inflater.inflate(R.layout.item, parent, false));
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Entity_Shot temp = data.get(position);
        String imagePath = temp.getImages().getNormal();
        initViewHolder((ViewHolder) holder, temp, imagePath);
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
        return data.size();
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

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
