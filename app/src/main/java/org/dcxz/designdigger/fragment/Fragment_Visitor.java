package org.dcxz.designdigger.fragment;

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

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.dcxz.designdigger.App;
import org.dcxz.designdigger.R;
import org.dcxz.designdigger.adapter.Adapter_Visitor;
import org.dcxz.designdigger.entity.Entity_Shot;
import org.dcxz.designdigger.framework.Framework_Adapter;
import org.dcxz.designdigger.framework.Framework_Fragment;
import org.dcxz.designdigger.util.API;

import java.lang.reflect.Type;
import java.util.ArrayList;


/**
 * <br/>
 * Created by DC on 2016/12/20.<br/>
 */

public class Fragment_Visitor extends Framework_Fragment {
    /**
     * 日志标签,请求标签
     */
    public static final String TAG = "Fragment_Visitor";
    /**
     * 用于遮罩的进度条
     */
    private ProgressBar progressBar;
    /**
     * 内容筛选,控制流行程度,控制分类,控制时间范围
     */
    private Spinner spinner_sort, spinner_list, spinner_timeFrame;
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
     * 展示内容用的GridView
     */
    private GridView gridView;// TODO: 2016/12/24 pullToRefresh
    /**
     * GridView中的内容
     */
    private ArrayList<Entity_Shot> content;
    /**
     * 滑动过程中的状态锁,控制gridView滑动到指定位置时只发送一次数据请求
     */
    private boolean refreshEnable = true;

    private Framework_Adapter<Entity_Shot> adapter;
    /**
     * content的类型
     */
    private Type type;

    @Override
    protected int setContentViewImp() {
        return R.layout.fragment_visitor;
    }

    @Override
    protected void initView(Activity activity, View view) {
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        spinner_sort = (Spinner) view.findViewById(R.id.fragment_visitor_sort);
        spinner_list = (Spinner) view.findViewById(R.id.fragment_visitor_list);
        spinner_timeFrame = (Spinner) view.findViewById(R.id.fragment_visitor_timeFrame);
        gridView = (GridView) view.findViewById(R.id.fragment_visitor_gridView);
        gridView.setNumColumns(1);
    }

    @Override
    protected void initData(Activity activity) {
        mapping();
        content = new ArrayList<>();
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
        adapter = new Adapter_Visitor(activity, content);
        gridView.setAdapter(adapter);
    }

    @Override
    protected void initListener(Activity activity) {
        spinner_sort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sortSelected = sortValue[position];
                Log.i(TAG, "onItemSelected: sortSelected=" + sortSelected);
                reRequest();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinner_list.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                listSelected = listValue[position];
                Log.i(TAG, "onItemSelected: listSelected=" + listSelected);
                reRequest();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinner_timeFrame.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                timeFrameSelected = timeFrameValue[position];
                Log.i(TAG, "onItemSelected: timeFrameSelected=" + timeFrameSelected);
                reRequest();
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
             * 由于在滑动过程中会多次出发位置判定,因此需要额外进行状态判定{@link Fragment_Visitor#refreshEnable}<br/>
             * 请求到数据后重置状态锁,将反射生成的数据进行修正后追加到内容池中
             */
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem + visibleItemCount >= totalItemCount - 6) {
                    //当滑动到倒数第7个item以内时尝试加载新数据
                    Log.i(TAG, "onScroll: refreshEnable=" + refreshEnable);
                    if (refreshEnable) {//状态锁,当前状态是否可以请求数据
                        Log.i(TAG, "onScroll: try onScroll refresh");
                        refreshEnable = false;
                        App.stringRequest(String.format(API.EndPoint.SHOTS_PAGE_SORT_LIST_TIMEFRAME,
                                pageSelected + "", sortSelected, listSelected, timeFrameSelected),
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        refreshEnable = true;//重置状态锁
                                        pageSelected++;//更新页码
                                        Log.i(TAG, "onResponse: onScroll refresh success");
                                        ArrayList<Entity_Shot> temp = new Gson().fromJson(response, type);
                                        for (Entity_Shot shot : temp) {
                                            //"2015-05-29T08:59:36Z" -> "2015-05-29 08:59:36"
                                            shot.setCreated_at(shot.getCreated_at().replace("T", " ").replace("Z", ""));
                                        }
                                        content.addAll(temp);//将请求到的数据追加到原有内容的尾部
                                        adapter.setData(content);
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        refreshEnable = true;//重置状态锁
                                        Log.i(TAG, "onErrorResponse: onScroll refresh failed");
                                    }
                                }, TAG);//标记这个请求,因为它可能会被用户的操作取消
                    }
                }
            }
        });
    }

    /**
     * 因筛选条件改变而需要再次请求合适的数据
     */
    private void reRequest() {
        progressBar.setVisibility(View.VISIBLE);
        pageSelected = 1;//重置页码
        content.clear();//清空已有内容
        App.getQueue().cancelAll(TAG);//取消尚未完成的请求
        App.stringRequest(String.format(API.EndPoint.SHOTS_PAGE_SORT_LIST_TIMEFRAME,
                pageSelected + "", sortSelected, listSelected, timeFrameSelected),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        refreshEnable = true;
                        Log.i(TAG, "onResponse: reRequest refresh success");
                        progressBar.setVisibility(View.INVISIBLE);
                        content = new Gson().fromJson(response, type);
                        for (Entity_Shot shot : content) {
                            //"2015-05-29T08:59:36Z" -> "2015-05-29 08:59:36"
                            shot.setCreated_at(shot.getCreated_at().replace("T", " ").replace("Z", ""));
                        }
                        adapter.setData(content);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        refreshEnable = true;
                        Log.i(TAG, "onErrorResponse: reRequest refresh failed");
                        progressBar.setVisibility(View.INVISIBLE);
                        toast(R.string.connect_error);
                    }
                }, TAG);
    }

    @Override
    public void handleMessageImp(Message msg) {
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        App.getQueue().cancelAll(Adapter_Visitor.TAG);//取消掉未完成的全部图像请求
    }
}
