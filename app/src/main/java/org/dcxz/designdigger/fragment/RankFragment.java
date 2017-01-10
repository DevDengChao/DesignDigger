package org.dcxz.designdigger.fragment;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.dcxz.designdigger.R;
import org.dcxz.designdigger.adapter.ShotsAdapter;
import org.dcxz.designdigger.app.App;
import org.dcxz.designdigger.bean.ShotInfo;
import org.dcxz.designdigger.framework.BaseActivity;
import org.dcxz.designdigger.framework.BaseFragment;
import org.dcxz.designdigger.framework.BaseRecyclerViewAdapter;
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

public class RankFragment extends BaseFragment {
    /**
     * 日志标签,请求标签
     */
    public static final String TAG = "RankFragment";
    /**
     * 用于遮罩的进度条
     */
    @BindView(R.id.fragment_main_progressBar)
    ProgressBar progressBar;
    /**
     * 内容筛选,控制流行程度,控制分类,控制时间范围
     */
    @BindView(R.id.fragment_main_sort)
    Spinner spinner_sort;
    @BindView(R.id.fragment_main_list)
    Spinner spinner_list;
    @BindView(R.id.fragment_main_timeFrame)
    Spinner spinner_timeFrame;
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
     * 界面上显示的文本,来自资源文件<br/>
     * 由于显示在页面上的文本与实际需要填入url中的文本不一致,因此需要进行键值对映射
     */
    private String[] sortKey, listKey, timeFrameKey;
    /**
     * 请求实际发送的文本,来自API.EndPoint.Parameter<br/>
     */
    private String[] sortValue, listValue, timeFrameValue;
    /**
     * 被选中的条件
     */
    private String sortSelected, listSelected, timeFrameSelected;
    /**
     * 被选中的页面
     */
    private int page = 1;
    /**
     * 滑动过程中的状态锁,控制gridView滑动到指定位置时只发送一次数据请求
     */
    private boolean refreshable = true;

    private BaseRecyclerViewAdapter<ShotInfo> adapter;
    private ArrayList<ShotInfo> shots;
    /**
     * content的类型
     */
    private Type type;
    private Gson gson;
    private GridLayoutManager gridLayoutManager;

    @Override
    protected int setContentViewImp() {
        return R.layout.fragment_main;
    }

    @SuppressLint("InflateParams")
    @Override
    protected void initView(BaseActivity activity, View view) {
        ptrFrameLayout.setPullToRefresh(true);
        ptrFrameLayout.setHeaderView(activity.getLayoutInflater().inflate(R.layout.header, null));
        gridLayoutManager = new GridLayoutManager(activity, 1);
        recyclerView.setLayoutManager(gridLayoutManager);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void initData(BaseActivity activity, Bundle savedInstanceState) {
        mapping();
        gson = new Gson();
        type = new TypeToken<ArrayList<ShotInfo>>() {
        }.getType();
        if (savedInstanceState != null) {
            Log.i(TAG, "initData: savedInstanceState != null");
            shots = (ArrayList<ShotInfo>) savedInstanceState.getSerializable(TAG);
            progressBar.setVisibility(View.INVISIBLE);
        } else {
            shots = new ArrayList<>();
        }
    }

    /**
     * 初始化映射关系,string-array资源需与API.EndPoint.Parameter.XXX顺序严格一致
     */
    private void mapping() {
        Resources resources = getResources();
        sortKey = resources.getStringArray(R.array.sort);
        sortValue = new String[sortKey.length];
        API.EndPoint.Parameter.Sort sorts[] = API.EndPoint.Parameter.Sort.values();
        for (int i = 0; i < sorts.length; i++) {
            sortValue[i] = sorts[i].toString();
        }
        sortSelected = sortValue[0];

        listKey = resources.getStringArray(R.array.list);
        listValue = new String[listKey.length];
        API.EndPoint.Parameter.List lists[] = API.EndPoint.Parameter.List.values();
        for (int i = 0; i < lists.length; i++) {
            listValue[i] = lists[i].toString();
        }
        listSelected = listValue[0];

        timeFrameKey = resources.getStringArray(R.array.timeFrame);
        timeFrameValue = new String[timeFrameKey.length];
        API.EndPoint.Parameter.TimeFrame timeFrames[] = API.EndPoint.Parameter.TimeFrame.values();
        for (int i = 0; i < timeFrames.length; i++) {
            timeFrameValue[i] = timeFrames[i].toString();
        }
        timeFrameSelected = timeFrameValue[0];
    }

    @Override
    protected void initAdapter(BaseActivity activity) {
        int layoutID = android.R.layout.simple_spinner_item;
        spinner_sort.setAdapter(new ArrayAdapter<>(activity, layoutID, sortKey));
        spinner_list.setAdapter(new ArrayAdapter<>(activity, layoutID, listKey));
        spinner_timeFrame.setAdapter(new ArrayAdapter<>(activity, layoutID, timeFrameKey));
        recyclerView.setAdapter(adapter = new ShotsAdapter(activity, shots, null));
    }

    @Override
    protected void initListener(BaseActivity activity) {
        ptrFrameLayout.setPtrHandler(new PtrHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                doPullToRefresh();
                Log.i(TAG, "onRefreshBegin: pull to refresh");
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

        spinner_sort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sortSelected = sortValue[position];
                progressBar.setVisibility(View.VISIBLE);
                doPullToRefresh();
                Log.i(TAG, "onItemSelected: sortSelected=" + sortSelected);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinner_list.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                listSelected = listValue[position];
                progressBar.setVisibility(View.VISIBLE);
                doPullToRefresh();
                Log.i(TAG, "onItemSelected: listSelected=" + listSelected);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinner_timeFrame.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                timeFrameSelected = timeFrameValue[position];
                progressBar.setVisibility(View.VISIBLE);
                doPullToRefresh();
                Log.i(TAG, "onItemSelected: timeFrameSelected=" + timeFrameSelected);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            /**
             * 当GridView滑动到一定位置时自动进行新数据的请求<br/>
             * 由于在滑动过程中会多次出发位置判定,因此需要额外进行状态判定{@link RankFragment#refreshable}<br/>
             * 请求到数据后重置状态锁,将反射生成的数据进行修正后追加到内容池中
             */
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (gridLayoutManager.findLastVisibleItemPosition() > adapter.getItemCount() - 6) {
                    if (refreshable) {
                        refreshable = false;//上锁
                        Log.i(TAG, "onScrolled: try refresh at page " + page);
                        App.stringRequest(String.format(API.EndPoint.SHOTS_PAGE_SORT_LIST_TIMEFRAME,
                                page + "", sortSelected, listSelected, timeFrameSelected),
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        Log.i(TAG, "onResponse: onScroll refresh success at page " + page);
                                        refreshable = true;//重置状态锁
                                        page++;//更新页码
                                        ArrayList<ShotInfo> shots = gson.fromJson(response, type);
                                        for (ShotInfo shot : shots) {
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
                                        Log.i(TAG, "onErrorResponse: onScroll refresh failed at page " + page);
                                    }
                                }, TAG);//标记这个请求,因为它可能会被用户的操作取消
                    }
                }
            }
        });
    }

    /**
     * 因筛选条件改变而需要再次请求合适的数据<br/>
     * 或因为网络连接异常导致完全没有获得任何数据而需要再次请求合适的数据<br/>
     * 请务必注意此方法与onScroll()的调用顺序,并注意pageSelected变更的时机
     */
    private void doPullToRefresh() {
        page = 1;//重置页码
        App.getQueue().cancelAll(TAG);//取消尚未完成的请求
        Log.i(TAG, "doPullToRefresh: last request with TAG canceled");
        App.stringRequest(String.format(API.EndPoint.SHOTS_PAGE_SORT_LIST_TIMEFRAME,
                page + "", sortSelected, listSelected, timeFrameSelected),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i(TAG, "onResponse: doPullToRefresh refresh success at page " + page);
                        refreshable = true;
                        page++;
                        ptrFrameLayout.refreshComplete();
                        progressBar.setVisibility(View.INVISIBLE);
                        connectionError.setVisibility(View.INVISIBLE);
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
                        Log.i(TAG, "onErrorResponse: doPullToRefresh refresh failed at page " + page);
                        progressBar.setVisibility(View.INVISIBLE);
                        connectionError.setVisibility(View.VISIBLE);
                        toast(R.string.connection_error);
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
        App.getQueue().cancelAll(ShotsAdapter.TAG);//取消掉未完成的全部图像请求
    }
}
