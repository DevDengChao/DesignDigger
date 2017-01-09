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

import org.dcxz.designdigger.R;
import org.dcxz.designdigger.adapter.ShotsAdapter;
import org.dcxz.designdigger.app.App;
import org.dcxz.designdigger.bean.ShotInfo;
import org.dcxz.designdigger.bean.UserInfo;
import org.dcxz.designdigger.framework.BaseFragment;
import org.dcxz.designdigger.util.API;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * <br/>
 * Created by DC on 2017/1/2.<br/>
 */

public class ProfileFragment extends BaseFragment {
    public static final String TAG = "ProfileFragment";
    private RecyclerView recyclerView;
    private ShotsAdapter adapter;
    private GridLayoutManager gridLayoutManager;
    private UserInfo user;
    /**
     * ArrayList<ShotInfo>的类型
     */
    private Type type;
    private Gson gson;

    /**
     * Use {@link #newInstance(UserInfo)} instead.
     */
    @Deprecated()
    public ProfileFragment() {
        gson = new Gson();
        type = new TypeToken<ArrayList<ShotInfo>>() {
        }.getType();
    }

    public static ProfileFragment newInstance(UserInfo user) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(TAG, user);
        //noinspection deprecation
        ProfileFragment fragmentProfile = new ProfileFragment();
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
            user = (UserInfo) savedInstanceState.getSerializable(TAG);
        } else {
            user = (UserInfo) getArguments().getSerializable(TAG);
        }
    }

    @Override
    protected void initAdapter(Activity activity) {
        adapter = new ShotsAdapter(activity.getLayoutInflater(), new ArrayList<ShotInfo>(), user);
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
                                                ArrayList<ShotInfo> shots = gson.fromJson(response, type);
                                                for (ShotInfo shot : shots) {
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
