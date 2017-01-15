package org.dcxz.designdigger.adapter;

import android.annotation.SuppressLint;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.dcxz.designdigger.R;
import org.dcxz.designdigger.activity.CommentActivity;
import org.dcxz.designdigger.activity.ProfileActivity;
import org.dcxz.designdigger.app.App;
import org.dcxz.designdigger.bean.ShotInfo;
import org.dcxz.designdigger.framework.BaseActivity;
import org.dcxz.designdigger.view.AutoHeightGifImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * <br/>
 * Created by OvO on 2017/1/15.<br/>
 * ChangeLog :
 * <pre>
 * </pre>
 */

@SuppressWarnings("WeakerAccess")
public class ShotHolder extends RecyclerView.ViewHolder {
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

    private String Tag;

    /**
     * 没有用户对象时使用的构造器
     *
     * @param itemView 将要修改的视图
     */
    public ShotHolder(View itemView, String Tag) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.Tag = Tag;
    }

    /**
     * 更新Holder持有的数据
     */
    @SuppressLint("SetTextI18n")
    public void update(final ShotInfo shotInfo, String imagePath, final BaseActivity activity) {
        avatar.setImageResource(R.drawable.progress_rotate);//使用图像占位,避免重用过程中出现图像突变现象
        if (shotInfo.getUser() == null) {//当点击头像进入profileFragment时,服务器返回的信息中不包含user数据
            avatar.setVisibility(View.GONE);
        } else {//普通请求包含user数据
            avatar.setVisibility(View.VISIBLE);
            App.imageRequest(shotInfo.getUser().getAvatar_url(), avatar, Tag);
            avatar.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            activity.startActivity(ProfileActivity.class, shotInfo.getUser());
                        }
                    });
        }
        content.setImageResource(R.mipmap.item_content);//使用图像占位,避免重用过程中出现图像突变现象
        content.setTag(imagePath);
        if (shotInfo.isAnimated()) {// FIXME: 2017/1/12 gif
            App.gifRequest(imagePath, content, gif, Tag);
            gif.setVisibility(View.INVISIBLE);
        } else {
            App.imageRequest(imagePath, content, Tag);
            gif.setVisibility(View.INVISIBLE);
        }
        content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.startActivity(CommentActivity.class, shotInfo);
            }
        });
        if (shotInfo.getRebounds_count() == 0) {
            rebound.setVisibility(View.INVISIBLE);
        } else {
            rebound.setVisibility(View.VISIBLE);
            attachment.setText(shotInfo.getRebounds_count() + "");
        }
        if (shotInfo.getAttachments_count() == 0) {
            attachment.setVisibility(View.INVISIBLE);
        } else {
            attachment.setVisibility(View.VISIBLE);
            attachment.setText(shotInfo.getAttachments_count() + "");
        }
        view.setText(shotInfo.getViews_count() + "");// 1 -> "1"
        comment.setText(shotInfo.getComments_count() + "");
        like.setText(shotInfo.getLikes_count() + "");
        if (shotInfo.getUser() == null) {//当点击头像进入profileFragment时,服务器返回的信息中不包含user数据
            userName.setVisibility(View.GONE);
        } else {//普通请求包含user数据
            userName.setVisibility(View.VISIBLE);
            userName.setText(shotInfo.getUser().getUsername());
        }
        title.setText(shotInfo.getTitle());
        time.setText(shotInfo.getCreated_at());
    }
}

