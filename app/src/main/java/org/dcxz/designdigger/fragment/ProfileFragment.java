package org.dcxz.designdigger.fragment;

import android.os.Bundle;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.dcxz.designdigger.R;
import org.dcxz.designdigger.adapter.ShotsAdapter;
import org.dcxz.designdigger.app.App;
import org.dcxz.designdigger.bean.ShotInfo;
import org.dcxz.designdigger.bean.UserInfo;
import org.dcxz.designdigger.framework.BaseActivity;
import org.dcxz.designdigger.framework.BaseFragment;
import org.dcxz.designdigger.util.API;

import java.lang.reflect.Type;
import java.util.ArrayList;

import butterknife.BindView;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;

/**
 * <br/>
 * Created by DC on 2017/1/2.<br/>
 */

public class ProfileFragment extends BaseFragment {
    public static final String TAG = "ProfileFragment";
    /**
     * 用于遮罩的进度条
     */
    @BindView(R.id.fragment_main_progressBar)
    ProgressBar progressBar;
    /**
     * 展示内容用的RecyclerView
     */
    @BindView(R.id.fragment_main_recyclerView)
    RecyclerView recyclerView;
    /**
     * 用于提示用户连接异常
     */
    @BindView(R.id.fragment_main_connectionError)
    TextView connectionError;
    /**
     * 下拉刷新需要的控件
     */
    @BindView(R.id.fragment_main_ptrFrameLayout)
    PtrFrameLayout ptrFrameLayout;

    /**
     * 滑动过程中的状态锁,控制recyclerView滑动到指定位置时只发送一次数据请求
     */
    private boolean refreshable = true;
    /**
     * 实例化以后是否需要刷新数据
     */
    private boolean needRefresh;

    private ShotsAdapter adapter;
    private GridLayoutManager gridLayoutManager;
    private UserInfo user;
    /**
     * ArrayList<ShotInfo>的类型
     */
    private Type type;
    private Gson gson;
    /**
     * 当前页码
     */
    private int page = 1;
    private ArrayList<ShotInfo> data;

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
        return R.layout.fragment_main_content;
    }


    @Override
    protected void initView(BaseActivity activity, View view) {
        gridLayoutManager = new GridLayoutManager(activity, 1);
        recyclerView.setLayoutManager(gridLayoutManager);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void initData(BaseActivity activity, Bundle savedInstanceState) {
        user = (UserInfo) getArguments().getSerializable(TAG);
        if (savedInstanceState != null) {
            Log.i(TAG, "initData: savedInstanceState != null");
            needRefresh = false;//已有数据,不需要重复请求
            data = (ArrayList<ShotInfo>) savedInstanceState.getSerializable(TAG);
        } else {
            data = new ArrayList<>();
            needRefresh = true;//没有数据,需要请求新数据
        }
    }

    @Override
    protected void initAdapter(BaseActivity activity) {
        recyclerView.setAdapter(adapter = new ShotsAdapter(activity, data, user));
    }

    @Override
    protected void initListener(final BaseActivity activity) {
        ptrFrameLayout.setPtrHandler(new PtrHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                doPullToRefresh();
            }

            /**
             * 覆盖触发下拉事件的检查
             */
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                //对recyclerView是否可滚动进行检测,当recyclerView无法向下滚动时允许进行下拉刷新
                return !recyclerView.canScrollVertically(-1);
            }
        });
        recyclerView.addOnScrollListener(
                new RecyclerView.OnScrollListener() {

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
                                                progressBar.setVisibility(View.INVISIBLE);
                                                connectionError.setVisibility(View.INVISIBLE);
                                                ArrayList<ShotInfo> shots = gson.fromJson(response, type);
                                                for (ShotInfo shot : shots) {
                                                    //"2015-05-29T08:59:36Z" -> "2015-05-29 08:59:36"
                                                    shot.setCreated_at(shot.getCreated_at().replace("T", " ").replace("Z", ""));
                                                }
                                                adapter.addDataToBottom(shots);
                                            }
                                        },
                                        new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                refreshable = true;//重置状态锁
                                                progressBar.setVisibility(View.INVISIBLE);
                                                if (adapter.getItemCount() == 0) {
                                                    connectionError.setVisibility(View.VISIBLE);
                                                }
                                                Log.i(TAG, "onErrorResponse: onScroll refresh failed at page " + page);
                                            }
                                        }, TAG);
                            }
                        }
                    }
                });
        if (needRefresh) {//触发刷新事件(recyclerView不自动调用onScrolled())
            doPullToRefresh();
        }
    }


    /**
     * 执行一次下拉刷新
     */
    private void doPullToRefresh() {
        page = 1;//重置页码
        App.getQueue().cancelAll(TAG);//取消尚未完成的请求
        Log.i(TAG, "doPullToRefresh: last request with TAG canceled");
        Log.i(TAG, "doPullToRefresh: try pull to refresh");
        App.stringRequest(
                String.format(API.EndPoint.USERS_SHOTS_PAGE, user.getId(), page),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i(TAG, "onResponse: pull to refresh success at page " + page);
                        refreshable = true;
                        page++;
                        ptrFrameLayout.refreshComplete();
                        connectionError.setVisibility(View.INVISIBLE);
                        progressBar.setVisibility(View.INVISIBLE);
                        ArrayList<ShotInfo> shots = gson.fromJson(response, type);
                        for (ShotInfo shot : shots) {
                            //"2015-05-29T08:59:36Z" -> "2015-05-29 08:59:36"
                            shot.setCreated_at(shot.getCreated_at().replace("T", " ").replace("Z", ""));
                        }
                        adapter.setData(shots);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        refreshable = true;
                        ptrFrameLayout.refreshComplete();
                        Log.i(TAG, "onErrorResponse: pull to refresh failed at page " + page);
                        progressBar.setVisibility(View.INVISIBLE);
                        connectionError.setVisibility(View.VISIBLE);
                    }
                }, TAG);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(TAG, adapter.getData());
    }

    @Override
    public void handleMessageImp(Message msg) {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        App.getQueue().cancelAll(TAG);
    }
}
