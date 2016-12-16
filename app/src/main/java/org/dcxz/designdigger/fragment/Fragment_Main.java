package org.dcxz.designdigger.fragment;

import android.app.Activity;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ExpandableListView;
import android.widget.GridView;
import android.widget.ImageView;

import org.dcxz.designdigger.R;
import org.dcxz.designdigger.framework.Framework_Fragment;

/**
 * <br/>
 * Created by DC on 2016/12/15.<br/>
 */

public class Fragment_Main extends Framework_Fragment {
    private ExpandableListView elv_popular;
    private ExpandableListView elv_type;
    private ExpandableListView elv_time;
    private GridView gridView;


    @Override
    protected int setContentViewImp() {
        return R.layout.fragment_main;
    }

    @Override
    protected void initView(Activity activity) {
        elv_popular = (ExpandableListView) activity.findViewById(R.id.fragment_main_elv_popular);
        elv_type = (ExpandableListView) activity.findViewById(R.id.fragment_main_elv_type);
        elv_time = (ExpandableListView) activity.findViewById(R.id.fragment_main_elv_time);
        gridView = (GridView) activity.findViewById(R.id.fragment_main_gv);
        gridView.setNumColumns(2);
    }

    @Override
    protected void initData(Activity activity) {

    }

    @Override
    protected void initAdapter(Activity activity) {
        final ImageView textView[] = new ImageView[4];
        for (int i = 0; i < 4; i++) {
            textView[i] = new ImageView(activity);
            textView[i].setImageResource(R.mipmap.ic_launcher);
        }

        gridView.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return textView.length;
            }

            @Override
            public Object getItem(int position) {
                return textView[position];
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                return textView[position];
            }
        });
    }

    @Override
    protected void initListener(Activity activity) {

    }

    @Override
    public void handleMessageImp(Message msg) {

    }
}
