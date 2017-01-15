package org.dcxz.designdigger.adapter;

import android.annotation.SuppressLint;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import org.dcxz.designdigger.R;
import org.dcxz.designdigger.app.App;
import org.dcxz.designdigger.bean.CommentInfo;
import org.dcxz.designdigger.bean.UserInfo;

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
public class CommentHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.item_comment_avatar)
    CircleImageView avatar;
    @BindView(R.id.item_comment_userName)
    TextView userName;
    @BindView(R.id.item_comment_createAt)
    TextView createAt;
    @BindView(R.id.item_comment_body)
    TextView body;
    @BindView(R.id.item_comment_likesCount)
    TextView likesCount;
    private String tag;

    public CommentHolder(View itemView, String tag) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.tag = tag;
    }

    @SuppressLint("SetTextI18n")
    public void update(CommentInfo commentInfo) {
        UserInfo userInfo = commentInfo.getUser();
        App.imageRequest(userInfo.getAvatar_url(), avatar, tag);
        userName.setText(userInfo.getUsername());
        createAt.setText(commentInfo.getCreated_at());
        //noinspection deprecation
        body.setText(Html.fromHtml(commentInfo.getBody()));
        likesCount.setText(commentInfo.getLikes_count() + "");// TODO: 2017/1/15 like
    }
}
