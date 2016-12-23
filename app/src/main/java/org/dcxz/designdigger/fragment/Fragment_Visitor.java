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
import android.widget.SimpleAdapter;
import android.widget.Spinner;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
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
    public static final String TAG = "Fragment_Visitor";
    private ProgressBar progressBar;
    /**
     * 内容筛选,控制流行程度
     */
    private Spinner spinner_sort;
    /**
     * 内容筛选,控制分类
     */
    private Spinner spinner_list;
    /**
     * 内容筛选,控制时间范围
     */
    private Spinner spinner_timeFrame;
    /**
     * 由于显示在页面上的文本与实际需要填入url中的文本不一致,因此需要进行键值对映射
     */
    private String[] sortKey, listKey, timeFrameKey, sortValue, listValue, timeFrameValue;
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
    private GridView gridView;

    /**
     * GridView中的内容
     */
    private ArrayList<Entity_Shot> content;
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
        adapter = new Adapter_Visitor(activity, content);
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
        gridView.setAdapter(adapter);
    }

    @Override
    protected void initListener(Activity activity) {
        spinner_sort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sortSelected = sortValue[position];
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
                reRequest();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        /*gridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem + visibleItemCount == totalItemCount - 3) {//当滑动到倒数第3个item时开始加载新数据
                    pageSelected++;
                    App.getQueue().add(
                            new StringRequest(
                                    String.format(API.EndPoint.SHOTS_PAGE_SORT_LIST_TIMEFRAME,
                                            pageSelected + "", sortSelected, listSelected, timeFrameSelected),
                                    new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            Log.i(TAG, "onResponse: onScroll refresh");
                                            ArrayList<Entity_Shot> temp = new Gson().fromJson(response, type);
                                            for (Entity_Shot shot : temp) {
                                                //"2015-05-29T08:59:36Z" -> "2015-05-29 08:59:36"
                                                shot.setCreated_at(shot.getCreated_at().replace("T", " ").replace("Z", ""));
                                            }
                                            content.addAll(temp);
                                            adapter.setData(content);
                                        }
                                    },
                                    new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            Log.i(TAG, "onErrorResponse: onScroll refresh error");
                                        }
                                    }
                            )).setTag(TAG);//添加新的请求,并标记tag
                }
            }
        });*/

        App.pageRequest(pageSelected,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i(TAG, "onResponse: test refresh");
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
                        Log.i(TAG, "onErrorResponse: test failed");
                        toast(R.string.connect_error);
                    }
                }
        );
    }

    /**
     * 因筛选条件改变而需要再次请求合适的数据
     */
    private void reRequest() {
        progressBar.setVisibility(View.VISIBLE);// TODO: 2016/12/24  API.EndPoint.SHOTS_PAGE_SORT_LIST_TIMEFRAME
        pageSelected = 1;//重置页码
        /*content.clear();//清空已有内容
        App.getQueue().cancelAll(TAG);//取消尚未完成的请求
        App.getQueue().add(
                new StringRequest(
                        String.format(API.EndPoint.SHOTS_PAGE_SORT_LIST_TIMEFRAME,
                                pageSelected + "", sortSelected, listSelected, timeFrameSelected),
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.i(TAG, "onResponse: reRequest refresh");
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
                                Log.i(TAG, "onErrorResponse: reRequest error");
                                toast(R.string.connect_error);
                            }
                        }
                )).setTag(TAG);//添加新的请求,并标记tag*/
    }

    @Override
    public void handleMessageImp(Message msg) {

    }
}
