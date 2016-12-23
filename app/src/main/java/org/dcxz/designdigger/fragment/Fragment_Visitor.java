package org.dcxz.designdigger.fragment;

import android.app.Activity;
import android.os.Message;
import android.view.View;
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

import java.util.ArrayList;
import java.util.HashMap;


/**
 * <br/>
 * Created by DC on 2016/12/20.<br/>
 */

public class Fragment_Visitor extends Framework_Fragment {
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
    private HashMap<String, String> sort,list,timeFrame;
    /**
     * 展示内容用的GridView
     */
    private GridView gridView;

    /**
     * GridView中的内容
     */
    private ArrayList<Entity_Shot> content;
    private Framework_Adapter<Entity_Shot> adapter;

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
        initConvertMap();
        content = new ArrayList<>();
        adapter = new Adapter_Visitor(activity, content);
        App.pageRequest(
                1,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressBar.setVisibility(View.INVISIBLE);
                        content = new Gson().fromJson(response, new TypeToken<ArrayList<Entity_Shot>>() {
                        }.getType());
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
                        // TODO: 2016/12/17 优化失败响应
                        toast(R.string.connect_error);
                    }
                }
        );
    }

    /**
     * 初始化映射表
     */
    private void initConvertMap() {
        sort  = new HashMap<>();
        sort .put("Popular",null);
        sort .put("Most Viewed", API.EndPoint.Parameter.Sort.VIEWS.toString());
        sort .put("Most Commented",null);

    }

    @Override
    protected void initAdapter(Activity activity) {
        spinner_sort.setAdapter(ArrayAdapter.createFromResource(activity, R.array.sort, android.R.layout.simple_spinner_item));
        spinner_list.setAdapter(ArrayAdapter.createFromResource(activity, R.array.list, android.R.layout.simple_spinner_item));
        spinner_timeFrame.setAdapter(ArrayAdapter.createFromResource(activity, R.array.timeFrame, android.R.layout.simple_spinner_item));
        gridView.setAdapter(adapter);
    }

    @Override
    protected void initListener(Activity activity) {

    }

    @Override
    public void handleMessageImp(Message msg) {

    }
}
