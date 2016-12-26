package org.dcxz.designdigger.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.Resources;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.dcxz.designdigger.App;
import org.dcxz.designdigger.R;
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

public class Fragment_Rank extends Framework_Fragment {
    /**
     * 日志标签,请求标签
     */
    public static final String TAG = "Fragment_Rank";
    /**
     * 用于遮罩的进度条
     */
    private ProgressBar progressBar;
    /**
     * 内容筛选,控制流行程度,控制分类,控制时间范围
     */
    private Spinner spinner_sort, spinner_list, spinner_timeFrame;
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
        spinner_sort = (Spinner) view.findViewById(R.id.fragment_main_sort);
        spinner_list = (Spinner) view.findViewById(R.id.fragment_main_list);
        spinner_timeFrame = (Spinner) view.findViewById(R.id.fragment_main_timeFrame);
        gridView = (GridView) view.findViewById(R.id.fragment_main_gridView);
        gridView.setNumColumns(1);
    }

    @Override
    protected void initData(Activity activity) {
        mapping();
        gson = new Gson();
        type = new TypeToken<ArrayList<Entity_Shot>>() {
        }.getType();
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
    protected void initAdapter(Activity activity) {
        int layoutID = android.R.layout.simple_spinner_item;
        spinner_sort.setAdapter(new ArrayAdapter<>(activity, layoutID, sortKey));
        spinner_list.setAdapter(new ArrayAdapter<>(activity, layoutID, listKey));
        spinner_timeFrame.setAdapter(new ArrayAdapter<>(activity, layoutID, timeFrameKey));
        adapter = new Adapter_Main(activity, new ArrayList<Entity_Shot>());
        gridView.setAdapter(adapter);
    }

    @Override
    protected void initListener(Activity activity) {
        ptrFrameLayout.setPtrHandler(new PtrDefaultHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                reRequest();
                Log.i(TAG, "onRefreshBegin: pull to refresh");
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

        spinner_sort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sortSelected = sortValue[position];
                progressBar.setVisibility(View.VISIBLE);
                reRequest();
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
                reRequest();
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
                reRequest();
                Log.i(TAG, "onItemSelected: timeFrameSelected=" + timeFrameSelected);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        gridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            /**
             * 当GridView滑动到一定位置时自动进行新数据的请求<br/>
             * 由于在滑动过程中会多次出发位置判定,因此需要额外进行状态判定{@link Fragment_Rank#refreshEnable}<br/>
             * 请求到数据后重置状态锁,将反射生成的数据进行修正后追加到内容池中
             */
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem + visibleItemCount >= totalItemCount - 6) {
                    //当滑动到倒数第7个item以内时尝试加载新数据
                    if (refreshEnable) {//状态锁,当前状态是否可以请求数据
                        refreshEnable = false;
                        Log.i(TAG, "onScroll: try onScroll refresh at page " + pageSelected);
                        App.stringRequest(String.format(API.EndPoint.SHOTS_PAGE_SORT_LIST_TIMEFRAME,
                                pageSelected + "", sortSelected, listSelected, timeFrameSelected),
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        Log.i(TAG, "onResponse: onScroll refresh success at page " + pageSelected);
                                        refreshEnable = true;//重置状态锁
                                        pageSelected++;//更新页码
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
                                        Log.i(TAG, "onErrorResponse: onScroll refresh failed at page " + pageSelected);
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
    private void reRequest() {
        pageSelected = 1;//重置页码
        App.getQueue().cancelAll(TAG);//取消尚未完成的请求
        Log.i(TAG, "reRequest: last request with TAG canceled");
        App.stringRequest(String.format(API.EndPoint.SHOTS_PAGE_SORT_LIST_TIMEFRAME,
                pageSelected + "", sortSelected, listSelected, timeFrameSelected),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i(TAG, "onResponse: reRequest refresh success at page " + pageSelected);
                        refreshEnable = true;
                        pageSelected++;
                        ptrFrameLayout.refreshComplete();
                        progressBar.setVisibility(View.INVISIBLE);
                        connectionError.setVisibility(View.INVISIBLE);
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
                        Log.i(TAG, "onErrorResponse: reRequest refresh failed at page " + pageSelected);
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
    public void onDestroyView() {
        super.onDestroyView();
        App.getQueue().cancelAll(Adapter_Main.TAG);//取消掉未完成的全部图像请求
    }
}
