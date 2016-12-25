package org.dcxz.designdigger.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.dcxz.designdigger.App;
import org.dcxz.designdigger.R;
import org.dcxz.designdigger.entity.Entity_Shot;
import org.dcxz.designdigger.framework.Framework_Adapter;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * <br/>
 * Created by DC on 2016/12/17.<br/>
 */

public class Adapter_Main extends Framework_Adapter<Entity_Shot> {
    /**
     * 请求标签
     */
    public static final String TAG = "Adapter_Main";

    /**
     * @param context 用于初始化{@link #inflater}的上下文
     * @param data    将要被适配的数据集合
     */
    public Adapter_Main(Context context, ArrayList<Entity_Shot> data) {
        super(context, data);
    }

    @Override
    protected View getViewImp(int position, View convertView, ViewGroup parent) {
        Entity_Shot temp = data.get(position);
        ViewHolder holder;
        if (convertView == null) {// TODO: 2016/12/18 优化:Item布局
            convertView = inflater.inflate(R.layout.item, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
            //检查该对象是否已经显示,若已经显示则直接返回,否则进行初始化.
            //用于优化GirdView在更新数据集合过程中出现的闪烁现象.
            if (temp.getImages().getTeaser().equals(holder.content.getTag())) {
                return convertView;
            }
        }
        initView(holder, temp);
        return convertView;
    }

    /**
     * 将ViewHolder与Entity_Shot对象组装起来
     *
     * @param holder 持有控件的ViewHolder
     * @param temp   持有数据的Entity_Shot
     */
    @SuppressLint("SetTextI18n")
    private void initView(final ViewHolder holder, Entity_Shot temp) {
        holder.avatar.setImageResource(R.drawable.progress_rotate);//使用图像占位,避免重用过程中出现图像突变现象
        App.imageRequest(temp.getUser().getAvatar_url(), holder.avatar, TAG);

        holder.content.setImageResource(R.mipmap.item_content);
        holder.content.setTag(temp.getImages().getTeaser());
        App.imageRequest(temp.getImages().getTeaser(), holder.content, TAG);
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

    /**
     * 静态内部类,避免引用
     */
    private static class ViewHolder {
        CircleImageView avatar;
        ImageView content;

        TextView rebound;
        TextView attachment;

        TextView view;
        TextView comment;
        TextView like;

        TextView userName;
        TextView title;
        TextView time;

        ViewHolder(View convertView) {
            avatar = (CircleImageView) convertView.findViewById(R.id.item_avatar);
            content = (ImageView) convertView.findViewById(R.id.item_content);
            rebound = (TextView) convertView.findViewById(R.id.item_rebound);
            attachment = (TextView) convertView.findViewById(R.id.item_attachment);
            view = (TextView) convertView.findViewById(R.id.item_view);
            comment = (TextView) convertView.findViewById(R.id.item_comment);
            like = (TextView) convertView.findViewById(R.id.item_like);

            userName = (TextView) convertView.findViewById(R.id.item_userName);
            title = (TextView) convertView.findViewById(R.id.item_title);
            time = (TextView) convertView.findViewById(R.id.item_time);
        }
    }
}
