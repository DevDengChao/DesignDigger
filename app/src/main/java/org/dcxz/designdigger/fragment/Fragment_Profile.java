package org.dcxz.designdigger.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.android.volley.Response;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.dcxz.designdigger.App;
import org.dcxz.designdigger.R;
import org.dcxz.designdigger.adapter.Adapter_Main2;
import org.dcxz.designdigger.entity.Entity_Shot;
import org.dcxz.designdigger.entity.Entity_User;
import org.dcxz.designdigger.framework.Framework_Fragment;
import org.dcxz.designdigger.util.API;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * <br/>
 * Created by DC on 2017/1/2.<br/>
 */

public class Fragment_Profile extends Framework_Fragment {
    public static final String TAG = "Fragment_Profile";
    private RecyclerView recyclerView;
    private Adapter_Main2 adapter;
    private GridLayoutManager gridLayoutManager;
    private Entity_User user;
    /**
     * ArrayList<Entity_Shot>的类型
     */
    private Type type;
    private Gson gson;

    /**
     * Use {@link #newInstance(Entity_User)} instead.
     */
    @Deprecated()
    public Fragment_Profile() {
        gson = new Gson();
        type = new TypeToken<ArrayList<Entity_Shot>>() {
        }.getType();
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
        adapter = new Adapter_Main2(activity.getLayoutInflater(), new ArrayList<Entity_Shot>(), user);
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
                                Log.i(TAG, "onScrolled: try refresh at page " + page);
                                App.stringRequest(
                                        String.format(API.EndPoint.USERS_SHOTS_PAGE, user.getId(), page),
                                        new Response.Listener<String>() {
                                            @Override
                                            public void onResponse(String response) {
                                                Log.i(TAG, "onResponse: query user's shots success at page " + page);
                                                page++;
                                                refreshable = true;//解锁
                                                ArrayList<Entity_Shot> shots = gson.fromJson(response, type);
                                                for (Entity_Shot shot : shots) {
                                                    //"2015-05-29T08:59:36Z" -> "2015-05-29 08:59:36"
                                                    shot.setCreated_at(shot.getCreated_at().replace("T", " ").replace("Z", ""));
                                                }
                                                adapter.addDataToBottom(shots);
                                            }
                                        },
                                        null, TAG);
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
