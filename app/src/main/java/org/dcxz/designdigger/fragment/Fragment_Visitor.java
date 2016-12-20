package org.dcxz.designdigger.fragment;

import android.app.Activity;
import android.os.Message;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.GridView;
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

import java.util.ArrayList;

/**
 * <br/>
 * Created by DC on 2016/12/20.<br/>
 */

public class Fragment_Visitor extends Framework_Fragment {

    /**
     * 内容筛选,控制流行程度
     */
    private Spinner spinner_popularity;
    /**
     * 内容筛选,控制分类
     */
    private Spinner spinner_type;
    /**
     * 内容筛选,控制时间范围
     */
    private Spinner spinner_timeLine;
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
        spinner_popularity = (Spinner) view.findViewById(R.id.fragment_visitor_popular);
        spinner_type = (Spinner) view.findViewById(R.id.fragment_visitor_type);
        spinner_timeLine = (Spinner) view.findViewById(R.id.fragment_visitor_timeLine);
        gridView = (GridView) view.findViewById(R.id.fragment_visitor_gridView);
        gridView.setNumColumns(1);
    }

    @Override
    protected void initData(Activity activity) {
// TODO: 2016/12/17 初始化数据
        content = new ArrayList<>();
        adapter = new Adapter_Visitor(activity, content);
        App.pageRequest(
                1,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
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

    @Override
    protected void initAdapter(Activity activity) {
        spinner_popularity.setAdapter(ArrayAdapter.createFromResource(activity, R.array.popularity, android.R.layout.simple_spinner_item));
        spinner_type.setAdapter(ArrayAdapter.createFromResource(activity, R.array.type, android.R.layout.simple_spinner_item));
        spinner_timeLine.setAdapter(ArrayAdapter.createFromResource(activity, R.array.timeLine, android.R.layout.simple_spinner_item));
        gridView.setAdapter(adapter);
    }

    @Override
    protected void initListener(Activity activity) {

    }

    @Override
    public void handleMessageImp(Message msg) {

    }
}
