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
import org.dcxz.designdigger.view.AutoHeightGifImageView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        Entity_Shot temp = data.get(position);
        ViewHolder holder;
        String imagePath = temp.getImages().getNormal();// TODO: 2016/12/27 允许用户自定义预览图的精度
        if (convertView == null) {// TODO: 2016/12/18 优化:Item布局
            convertView = inflater.inflate(R.layout.item, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
            //检查该对象是否已经显示,若已经显示则直接返回,否则进行初始化.
            //用于优化GirdView在更新数据集合过程中出现的闪烁现象.
            if (imagePath.equals(holder.content.getTag())) {
                return convertView;
            }
        }
        initView(holder, temp, imagePath);
        return convertView;
    }

    /**
     * 将ViewHolder与Entity_Shot对象组装起来
     *
     * @param holder    持有控件的ViewHolder
     * @param temp      持有数据的Entity_Shot
     * @param imagePath 将要加载的图像
     */
    @SuppressLint("SetTextI18n")
    private void initView(final ViewHolder holder, final Entity_Shot temp, String imagePath) {
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

    /**
     * 静态内部类,避免引用
     */
    @SuppressWarnings("WeakerAccess")
    static class ViewHolder {
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

        ViewHolder(View convertView) {
            ButterKnife.bind(this, convertView);
        }
    }
}
