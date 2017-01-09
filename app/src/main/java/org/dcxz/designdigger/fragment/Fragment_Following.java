package org.dcxz.designdigger.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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

import org.dcxz.designdigger.App;
import org.dcxz.designdigger.R;
import org.dcxz.designdigger.activity.Activity_Login;
import org.dcxz.designdigger.adapter.Adapter_Main;
import org.dcxz.designdigger.adapter.Adapter_Main2;
import org.dcxz.designdigger.entity.Entity_Shot;
import org.dcxz.designdigger.framework.BaseRecyclerViewAdapter;
import org.dcxz.designdigger.framework.Framework_Fragment;
import org.dcxz.designdigger.util.API;

import java.lang.reflect.Type;
import java.util.ArrayList;

import butterknife.BindView;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;


/**
 * <br/>
 * Created by DC on 2016/12/20.<br/>
 */

public class Fragment_Following extends Framework_Fragment {
    /**
     * 日志标签,请求标签
     */
    public static final String TAG = "Fragment_Following";
    /**
     * 用于遮罩的进度条
     */
    @BindView(R.id.fragment_main_progressBar)
    ProgressBar progressBar;
    /**
     * 展示内容用的GridView
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
     * 被选中的页面
     */
    private int pageSelected = 1;
    /**
     * 滑动过程中的状态锁,控制gridView滑动到指定位置时只发送一次数据请求
     */
    private boolean refreshable = true;

    private BaseRecyclerViewAdapter<Entity_Shot> adapter;
    private ArrayList<Entity_Shot> shots;
    /**
     * ArrayList<Entity_Shot>的类型
     */
    private Type type;
    private Gson gson;
    /**
     * 检查用户登录状态的广播接收器
     */
    private BroadcastReceiver receiver;
    private GridLayoutManager gridLayoutManager;

    @Override
    protected int setContentViewImp() {
        return R.layout.fragment_main;
    }

    @SuppressLint("InflateParams")
    @Override
    protected void initView(Activity activity, View view) {
        ptrFrameLayout.setPullToRefresh(true);
        ptrFrameLayout.setHeaderView(activity.getLayoutInflater().inflate(R.layout.header, null));
        view.findViewById(R.id.fragment_main_sort).setVisibility(View.GONE);
        view.findViewById(R.id.fragment_main_list).setVisibility(View.GONE);
        view.findViewById(R.id.fragment_main_timeFrame).setVisibility(View.GONE);
        gridLayoutManager = new GridLayoutManager(activity, 1);
        recyclerView.setLayoutManager(gridLayoutManager);
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (intent.getAction()) {
                    case Activity_Login.TAG:
                        doPullToRefresh();
                        break;
                    case Fragment_Menu.TAG:
                        App.getQueue().cancelAll(TAG);
                        isUserLogined();
                        break;
                }
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(Activity_Login.TAG);//监听登录成功事件
        filter.addAction(Fragment_Menu.TAG);//监听注销事件
        activity.registerReceiver(receiver, filter);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void initData(Activity activity, Bundle savedInstanceState) {
        gson = new Gson();
        type = new TypeToken<ArrayList<Entity_Shot>>() {
        }.getType();
        if (savedInstanceState != null) {
            Log.i(TAG, "initData: savedInstanceState != null");
            shots = (ArrayList<Entity_Shot>) savedInstanceState.getSerializable(TAG);
            progressBar.setVisibility(View.INVISIBLE);
        } else {
            shots = new ArrayList<>();
        }
    }


    @Override
    protected void initAdapter(Activity activity) {
        recyclerView.setAdapter(adapter = new Adapter_Main2(activity.getLayoutInflater(), shots));
    }

    @Override
    protected void initListener(Activity activity) {
        ptrFrameLayout.setPtrHandler(new PtrHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                //判断用户是否已登录
                if (isUserLogined()) {
                    doPullToRefresh();
                } else {
                    ptrFrameLayout.refreshComplete();
                }
            }

            /**
             * 覆盖触发下拉事件的检查
             */
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                //对gridView是否可滚动进行检测,当gridView无法向下滚动时允许进行下拉刷新
                return !recyclerView.canScrollVertically(-1);
            }
        });
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            /**
             * 当GridView滑动到一定位置时自动进行新数据的请求<br/>
             * 由于在滑动过程中会多次出发位置判定,因此需要额外进行状态判定{@link Fragment_Rank#refreshable}<br/>
             * 请求到数据后重置状态锁,将反射生成的数据进行修正后追加到内容池中
             */
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (gridLayoutManager.findLastVisibleItemPosition() > adapter.getItemCount() - 6) {
                    if (refreshable) {
                        refreshable = false;//上锁
                        Log.i(TAG, "onScrolled: try refresh at page " + pageSelected);
                        App.stringRequest(String.format(API.EndPoint.FOLLOWING_SHOTS_PAGE,
                                pageSelected + ""),
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        Log.i(TAG, "onResponse: onScroll refresh success at page " + pageSelected);
                                        refreshable = true;//重置状态锁
                                        pageSelected++;//更新页码
                                        progressBar.setVisibility(View.INVISIBLE);
                                        connectionError.setVisibility(View.INVISIBLE);
                                        ArrayList<Entity_Shot> shots = gson.fromJson(response, type);
                                        for (Entity_Shot shot : shots) {
                                            //"2015-05-29T08:59:36Z" -> "2015-05-29 08:59:36"
                                            shot.setCreated_at(shot.getCreated_at().replace("T", " ").replace("Z", ""));
                                        }
                                        adapter.addDataToBottom(shots);//将请求到的数据追加到原有内容的尾部
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
                                        Log.i(TAG, "onErrorResponse: onScroll refresh failed at page " + pageSelected);
                                    }
                                }, TAG);//标记这个请求,因为它可能会被用户的操作取消
                    }
                }
            }
        });
        if (isUserLogined()) {//触发刷新事件(recyclerView不自动调用onScrolled())
            doPullToRefresh();
        }
    }

    /**
     * 检查用户是否已登录
     */
    private boolean isUserLogined() {
        if (!API.Oauth2.ACCESS_TOKEN.equals(API.Oauth2.ACCESS_TOKEN_DEFAULT)) {
            return true;
        } else {
            if (getUserVisibleHint()) {
                toast(R.string.youAreNotLoginYet);
            }
            connectionError.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
            return false;
        }
    }

    /**
     * 执行一次下拉刷新
     */
    private void doPullToRefresh() {
        pageSelected = 1;//重置页码
        App.getQueue().cancelAll(TAG);//取消尚未完成的请求
        Log.i(TAG, "doPullToRefresh: last request with TAG canceled");
        Log.i(TAG, "doPullToRefresh: try pull to refresh");
        App.stringRequest(String.format(API.EndPoint.FOLLOWING_SHOTS_PAGE, pageSelected + ""),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i(TAG, "onResponse: pull to refresh success at page " + pageSelected);
                        refreshable = true;
                        pageSelected++;
                        ptrFrameLayout.refreshComplete();
                        connectionError.setVisibility(View.INVISIBLE);
                        progressBar.setVisibility(View.INVISIBLE);
                        ArrayList<Entity_Shot> shots = gson.fromJson(response, type);
                        for (Entity_Shot shot : shots) {
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
                        Log.i(TAG, "onErrorResponse: pull to refresh failed at page " + pageSelected);
                        progressBar.setVisibility(View.INVISIBLE);
                        connectionError.setVisibility(View.VISIBLE);
                    }
                }, TAG);
    }

    @Override
    public void handleMessageImp(Message msg) {
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(TAG, adapter.getData());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        App.getQueue().cancelAll(Adapter_Main.TAG);//取消掉未完成的全部图像请求
        getActivity().unregisterReceiver(receiver);
    }
}
