package org.dcxz.designdigger.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.GridView;
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
import org.dcxz.designdigger.entity.Entity_Shot;
import org.dcxz.designdigger.framework.Framework_Adapter;
import org.dcxz.designdigger.framework.Framework_Fragment;
import org.dcxz.designdigger.util.API;

import java.lang.reflect.Type;
import java.util.ArrayList;

import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;


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
    private ProgressBar progressBar;
    /**
     * 展示内容用的GridView
     */
    private GridView gridView;
    /**
     * 用于提示用户连接异常
     */
    private TextView connectionError;
    /**
     * 下拉刷新需要的控件
     */
    private PtrFrameLayout ptrFrameLayout;
    /**
     * 被选中的页面
     */
    private int pageSelected = 1;
    /**
     * 滑动过程中的状态锁,控制gridView滑动到指定位置时只发送一次数据请求
     */
    private boolean refreshEnable = true;

    private Framework_Adapter<Entity_Shot> adapter;
    /**
     * content的类型
     */
    private Type type;
    private Gson gson;
    /**
     * 检查用户登录状态的广播接收器
     */
    private BroadcastReceiver receiver;

    @Override
    protected int setContentViewImp() {
        return R.layout.fragment_main;
    }

    @SuppressLint("InflateParams")
    @Override
    protected void initView(Activity activity, View view) {
        progressBar = (ProgressBar) view.findViewById(R.id.fragment_main_progressBar);
        connectionError = (TextView) view.findViewById(R.id.fragment_main_connectionError);
        ptrFrameLayout = (PtrFrameLayout) view.findViewById(R.id.fragment_main_ptrFrameLayout);
        ptrFrameLayout.setPullToRefresh(true);
        ptrFrameLayout.setHeaderView(activity.getLayoutInflater().inflate(R.layout.header, null));
        view.findViewById(R.id.fragment_main_sort).setVisibility(View.GONE);
        view.findViewById(R.id.fragment_main_list).setVisibility(View.GONE);
        view.findViewById(R.id.fragment_main_timeFrame).setVisibility(View.GONE);
        gridView = (GridView) view.findViewById(R.id.fragment_main_gridView);
        gridView.setNumColumns(1);
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                doPullToRefresh();
            }
        };
        activity.registerReceiver(receiver, new IntentFilter(Activity_Login.TAG));
    }

    @Override
    protected void initData(Activity activity) {
        gson = new Gson();
        type = new TypeToken<ArrayList<Entity_Shot>>() {
        }.getType();
    }


    @Override
    protected void initAdapter(Activity activity) {
        adapter = new Adapter_Main(activity, new ArrayList<Entity_Shot>());
        gridView.setAdapter(adapter);
    }

    @Override
    protected void initListener(Activity activity) {
        ptrFrameLayout.setPtrHandler(new PtrDefaultHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                //判断用户是否已登录
                if (isUserLogined()) {
                    doPullToRefresh();
                }
            }

            /**
             * 覆盖触发下拉事件的检查
             */
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                //对gridView是否可滚动进行检测,当gridView无法向下滚动时允许进行下拉刷新
                return !gridView.canScrollVertically(-1);
            }
        });

        gridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            /**
             * 当GridView滑动到一定位置时自动进行新数据的请求<br/>
             * 由于在滑动过程中会多次出发位置判定,因此需要额外进行状态判定{@link Fragment_Following#refreshEnable}<br/>
             * 请求到数据后重置状态锁,将反射生成的数据进行修正后追加到内容池中
             */
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem + visibleItemCount >= totalItemCount - 6) {
                    //当滑动到倒数第7个item以内时尝试加载新数据
                    if (refreshEnable && isUserLogined()) {//状态锁,当前状态是否可以请求数据以及用户是否已登录
                        refreshEnable = false;
                        Log.i(TAG, "onScroll: try onScroll refresh at page " + pageSelected);
                        App.stringRequest(String.format(API.EndPoint.FOLLOWING_SHOTS_PAGE,
                                pageSelected + ""),
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        Log.i(TAG, "onResponse: onScroll refresh success at page " + pageSelected);
                                        refreshEnable = true;//重置状态锁
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
                                        refreshEnable = true;//重置状态锁
                                        progressBar.setVisibility(View.INVISIBLE);
                                        if (adapter.getCount() == 0) {
                                            connectionError.setVisibility(View.VISIBLE);
                                        }
                                        Log.i(TAG, "onErrorResponse: onScroll refresh failed at page " + pageSelected);
                                    }
                                }, TAG);//标记这个请求,因为它可能会被用户的操作取消
                    }
                }
            }
        });
    }

    /**
     * 检查用户是否已登录
     */
    private boolean isUserLogined() {
        if (!API.Oauth2.ACCESS_TOKEN.equals(API.Oauth2.ACCESS_TOKEN_DEFAULT)) {
            return true;
        } else {
            connectionError.setText(R.string.youAreNotLoginYet);
            connectionError.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
            return false;
        }
    }

    /**
     * 执行一次下拉刷新
     */
    private void doPullToRefresh() {
        connectionError.setVisibility(View.INVISIBLE);
        pageSelected = 1;//重置页码
        App.getQueue().cancelAll(TAG);//取消尚未完成的请求
        Log.i(TAG, "onRefreshBegin: last request with TAG canceled");
        Log.i(TAG, "onRefreshBegin: try pull to refresh");
        App.stringRequest(String.format(API.EndPoint.FOLLOWING_SHOTS_PAGE, pageSelected + ""),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i(TAG, "onResponse: pull to refresh success at page " + pageSelected);
                        refreshEnable = true;
                        pageSelected++;
                        ptrFrameLayout.refreshComplete();
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
                        refreshEnable = true;
                        ptrFrameLayout.refreshComplete();
                        Log.i(TAG, "onErrorResponse: pull to refresh failed at page " + pageSelected);
                        progressBar.setVisibility(View.INVISIBLE);
                        connectionError.setText(R.string.connection_error);
                        connectionError.setVisibility(View.VISIBLE);
                        toast(R.string.connection_error);
                    }
                }, TAG);
    }

    @Override
    public void handleMessageImp(Message msg) {
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        App.getQueue().cancelAll(Adapter_Main.TAG);//取消掉未完成的全部图像请求
        getActivity().unregisterReceiver(receiver);
    }
}
