package org.dcxz.designdigger.activity;

import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.dcxz.designdigger.R;
import org.dcxz.designdigger.adapter.CommentsAdapter;
import org.dcxz.designdigger.app.App;
import org.dcxz.designdigger.bean.CommentInfo;
import org.dcxz.designdigger.bean.ShotInfo;
import org.dcxz.designdigger.framework.BaseActivity;
import org.dcxz.designdigger.util.API;

import java.lang.reflect.Type;
import java.util.ArrayList;

import butterknife.BindView;

/**
 * <br/>
 * Created by OvO on 2017/1/15.<br/>
 * ChangeLog :
 * <pre>
 * </pre>
 */

public class CommentActivity extends BaseActivity {
    public static final String TAG = "CommentActivity";
    @BindView(R.id.activity_comment_toolbar)
    Toolbar toolbar;
    @BindView(R.id.activity_comment_recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.activity_comment_swipeRefreshLayout)
    SwipeRefreshLayout refreshLayout;
    /**
     * 指定Shot的评论
     */
    private ArrayList<CommentInfo> data;
    private ShotInfo shotInfo;
    private CommentsAdapter adapter;
    /**
     * 页码
     */
    private int page;
    private Gson gson;
    private Type type;
    /**
     * 刷新锁,控制recyclerView在滑动过程中是否触发自动加载更多
     */
    private boolean refreshable;
    private LinearLayoutManager linearLayoutManager;

    @Override
    protected int setContentViewImp() {
        return R.layout.activity_comment;
    }

    @Override
    protected void initView() {
        toolbar.setNavigationIcon(R.drawable.toolbar_back);
        setSupportActionBar(toolbar);
        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        refreshLayout.setColorSchemeResources(
                android.R.color.holo_red_light,
                android.R.color.holo_green_light,
                android.R.color.holo_blue_bright);
    }

    @Override
    protected void initData() {
        shotInfo = (ShotInfo) getIntent().getSerializableExtra(SERIALIZABLE);
        data = new ArrayList<>();
        page = 1;
        gson = new Gson();
        type = new TypeToken<ArrayList<CommentInfo>>() {
        }.getType();
        refreshable = false;
    }

    @Override
    protected void initAdapter() {
        adapter = new CommentsAdapter(this, data, shotInfo, TAG);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void initListener() {
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                doPullToRefresh();
            }
        });
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (refreshable && linearLayoutManager.findLastVisibleItemPosition() > adapter.getItemCount() - 4) {
                    refreshable = false;//上锁,禁止滑动时多次触发,产生重复请求
                    App.stringRequest(
                            String.format(API.EndPoint.SHOT_COMMENTS_PAGE, shotInfo.getId(), page),
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    Log.i(TAG, "onResponse: onScroll refresh success at page " + page);
                                    page++;
                                    ArrayList<CommentInfo> commentInfos = gson.fromJson(response, type);
                                    if (commentInfos.size() == 0) {
                                        toast(R.string.no_more_comments);
                                        Log.i(TAG, "onResponse: No more comments");
                                        refreshable = false;//锁死,全部评论已经加载完毕,禁止继续触发请求
                                    } else {
                                        refreshable = true;//解锁
                                        for (CommentInfo commentInfo : commentInfos) {
                                            commentInfo.setCreated_at(commentInfo.getCreated_at().replace("T", " ").replace("Z", ""));
                                        }
                                        adapter.addDataToBottom(commentInfos);
                                    }
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Log.w(TAG, "onResponse: onScroll refresh failed at page " + page);
                                    Log.w(TAG, "onErrorResponse: " + error.getMessage());
                                    refreshable = true;
                                }
                            }, TAG);
                }
            }
        });
        doPullToRefresh();//强制刷新数据集合;
    }

    /**
     * 下拉刷新
     */
    private void doPullToRefresh() {
        page = 1;//重置页码
        App.getQueue().cancelAll(TAG);//取消未完成的请求
        App.stringRequest(
                String.format(API.EndPoint.SHOT_COMMENTS_PAGE, shotInfo.getId(), page),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i(TAG, "onResponse: On swipe refresh success");
                        refreshLayout.setRefreshing(false);
                        refreshable = true;//解锁
                        page++;
                        ArrayList<CommentInfo> commentInfos = gson.fromJson(response, type);
                        for (CommentInfo commentInfo : commentInfos) {
                            //"2015-05-29T08:59:36Z" -> "2015-05-29 08:59:36"
                            commentInfo.setCreated_at(commentInfo.getCreated_at().replace("T", " ").replace("Z", ""));
                        }
                        adapter.setData(commentInfos);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.w(TAG, "onErrorResponse: On swipe refresh failed.");
                        Log.w(TAG, "onErrorResponse: " + error.getMessage());
                        refreshLayout.setRefreshing(false);
                        toast(R.string.connection_error);
                        refreshable = true;
                    }
                }, TAG);
    }

    @Override
    public void handleMessageImp(Message msg) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        App.getQueue().cancelAll(TAG);
    }
}
