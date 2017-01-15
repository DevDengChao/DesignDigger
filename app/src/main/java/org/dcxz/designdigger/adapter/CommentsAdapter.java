package org.dcxz.designdigger.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import org.dcxz.designdigger.R;
import org.dcxz.designdigger.bean.CommentInfo;
import org.dcxz.designdigger.bean.ShotInfo;
import org.dcxz.designdigger.framework.BaseActivity;
import org.dcxz.designdigger.framework.BaseRecyclerViewAdapter;

import java.util.ArrayList;

/**
 * <br/>
 * Created by OvO on 2017/1/15.<br/>
 * ChangeLog :
 * <pre>
 * </pre>
 */

public class CommentsAdapter extends BaseRecyclerViewAdapter<CommentInfo> {
    /**
     * viewType,控制item的布局与viewHolder的更新
     */
    private static final int SHOT = 0;
    /**
     * viewType,控制item的布局与viewHolder的更新
     */
    private static final int COMMENT = 1;
    /**
     * 头部展示的ShotInfo对象
     */
    private ShotInfo shotInfo;
    private String tag;

    public CommentsAdapter(BaseActivity activity, ArrayList<CommentInfo> data, ShotInfo shotInfo, String tag) {
        super(activity, data);
        this.shotInfo = shotInfo;
        this.tag = tag;
    }

    @Override
    protected RecyclerView.ViewHolder onCreateViewHolderImp(ViewGroup parent, int viewType, LayoutInflater inflater) {
        if (viewType == SHOT) {
            return new ShotHolder(inflater.inflate(R.layout.item_shot, parent, false), tag);
        } else {
            return new CommentHolder(inflater.inflate(R.layout.item_comment, parent, false), tag);
        }
    }

    @Override
    protected void onBindViewHolderImp(RecyclerView.ViewHolder holder, int position, ArrayList<CommentInfo> data, BaseActivity activity) {
        if (position == 0) {
            ((ShotHolder) holder).update(shotInfo, shotInfo.getImages().getNormal(), activity, false);
        } else {
            ((CommentHolder) holder).update(data.get(position - 1));
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return SHOT;
        } else {
            return COMMENT;
        }
    }

    @Override
    public int getItemCount() {
        return data.size() + 1;
    }
}
