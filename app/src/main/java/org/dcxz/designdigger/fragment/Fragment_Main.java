package org.dcxz.designdigger.fragment;

import android.content.Context;
import android.os.Message;
import android.widget.ExpandableListView;

import org.dcxz.designdigger.R;
import org.dcxz.designdigger.activity.Activity_Main;
import org.dcxz.designdigger.framework.Framework_Fragment;

/**
 * <br/>
 * Created by DC on 2016/12/15.<br/>
 */

public class Fragment_Main extends Framework_Fragment {
    private ExpandableListView elv_popular;
    private ExpandableListView elv_type;
    private ExpandableListView elv_time;

    @Override
    protected int setContentViewImp() {
        return R.layout.fragment_main;
    }

    @Override
    protected void initView(Context context) {
        elv_popular = (ExpandableListView) activity.findViewById(R.id.fragment_main_elv_popular);
        elv_type = (ExpandableListView) activity.findViewById(R.id.fragment_main_elv_type);
        elv_time = (ExpandableListView) activity.findViewById(R.id.fragment_main_elv_time);
    }

    @Override
    protected void initData(Context context) {

    }

    @Override
    protected void initAdapter(Context context) {
    }

    @Override
    protected void initListener(Context context) {

    }

    @Override
    public void handleMessageImp(Message msg) {

    }
}
