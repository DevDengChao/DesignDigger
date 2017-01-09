package org.dcxz.designdigger.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import org.dcxz.designdigger.R;
import org.dcxz.designdigger.adapter.Adapter_User;
import org.dcxz.designdigger.entity.Entity_User;
import org.dcxz.designdigger.framework.Framework_Fragment;

/**
 * <br/>
 * Created by DC on 2017/1/2.<br/>
 */

public class Fragment_Profile extends Framework_Fragment {
    public static final String TAG = "Fragment_Profile";
    private RecyclerView recyclerView;
    private Adapter_User adapter;
    private GridLayoutManager gridLayoutManager;
    private Entity_User user;

    /**
     * Use {@link #newInstance(Entity_User)} instead.
     */
    @Deprecated()
    public Fragment_Profile() {
    }

    public static Fragment_Profile newInstance(Entity_User user) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(TAG, user);
        //noinspection deprecation
        Fragment_Profile fragmentProfile = new Fragment_Profile();
        fragmentProfile.setArguments(bundle);
        return fragmentProfile;
    }

    @Override
    protected int setContentViewImp() {
        return R.layout.fragment_profile;
    }


    @Override
    protected void initView(Activity activity, View view) {
        recyclerView = (RecyclerView) view;
        gridLayoutManager = new GridLayoutManager(activity, 1);
        recyclerView.setLayoutManager(gridLayoutManager);
    }

    @Override
    protected void initData(Activity activity, Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            user = (Entity_User) savedInstanceState.getSerializable(TAG);
        } else {
            user = (Entity_User) getArguments().getSerializable(TAG);
        }
    }

    @Override
    protected void initAdapter(Activity activity) {
        adapter = new Adapter_User(activity.getLayoutInflater(), user);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void initListener(final Activity activity) {
        recyclerView.addOnScrollListener(
                new RecyclerView.OnScrollListener() {

                    private int page = 1;
                    /**
                     * 刷新锁
                     */
                    private boolean refreshable = true;

                    @Override
                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                        if (gridLayoutManager.findLastVisibleItemPosition() > adapter.getItemCount() - 6) {
                            if (refreshable) {
                                refreshable = false;//上锁
                                adapter.queryPage(
                                        page,
                                        new Adapter_User.OnQueryPageSuccessListener() {
                                            @Override
                                            public void onQueryPageSuccess() {
                                                page++;
                                                refreshable = true;//解锁
                                            }
                                        });
                            }
                        }
                    }
                });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(TAG, user);
    }

    @Override
    public void handleMessageImp(Message msg) {

    }
}
