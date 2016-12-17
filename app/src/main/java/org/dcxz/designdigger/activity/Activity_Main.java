package org.dcxz.designdigger.activity;

import android.app.ActionBar;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Spinner;

import org.dcxz.designdigger.R;
import org.dcxz.designdigger.fragment.Fragment_Menu;
import org.dcxz.designdigger.framework.Framework_Activity;
import org.dcxz.designdigger.framework.Framework_Adapter;
import org.dcxz.designdigger.view.flowing_drawer.FlowingView;
import org.dcxz.designdigger.view.flowing_drawer.LeftDrawerLayout;

import java.util.ArrayList;

public class Activity_Main extends Framework_Activity {
    /**
     * 动作栏
     */
    private android.app.ActionBar actionBar;
    /**
     * FlowingDrawer容器
     */
    private LeftDrawerLayout drawerLayout;
    /**
     * 侧滑菜单
     */
    private Fragment_Menu menu;
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
    private ArrayList<Integer> content;

    @Override
    protected int setContentViewImp() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        // TODO: 2016/12/17 测试Fragment与Spinner的兼容性
        initActionBar();
        initFlowingDrawer();
        initSpinner();
        initGridView();
    }

    // TODO: 2016/12/15 调试ActionBar与侧滑菜单
    @SuppressWarnings("ConstantConditions")
    private void initActionBar() {
        actionBar = getActionBar();
        actionBar.setLogo(R.mipmap.dribbble_logo);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_USE_LOGO);
    }

    private void initFlowingDrawer() {
        //https://github.com/mxn21/FlowingDrawer
        drawerLayout = (LeftDrawerLayout) findViewById(R.id.main_drawerLayout);
        menu = new Fragment_Menu();
        getFragmentManager().beginTransaction().replace(R.id.main_menuContainer, menu).commit();
        drawerLayout.setFluidView((FlowingView) findViewById(R.id.flowingView));
        drawerLayout.setMenuFragment(menu);
    }

    private void initSpinner() {
        spinner_popularity = (Spinner) findViewById(R.id.main_sp_popular);
        spinner_type = (Spinner) findViewById(R.id.main_sp_type);
        spinner_timeLine = (Spinner) findViewById(R.id.main_sp_time);
    }

    private void initGridView() {
        gridView = (GridView) findViewById(R.id.main_gridView);
        gridView.setNumColumns(2);
    }

    @Override
    protected void initData() {
        // TODO: 2016/12/17 初始化数据
        content = new ArrayList<>();
        content.add(R.mipmap.ic_launcher);
        content.add(R.mipmap.ic_launcher);
        content.add(R.mipmap.ic_launcher);
    }

    @Override
    protected void initAdapter() {
        spinner_popularity.setAdapter(ArrayAdapter.createFromResource(this, R.array.popularity, android.R.layout.simple_spinner_item));
        spinner_type.setAdapter(ArrayAdapter.createFromResource(this, R.array.type, android.R.layout.simple_spinner_item));
        spinner_timeLine.setAdapter(ArrayAdapter.createFromResource(this, R.array.timeLine, android.R.layout.simple_spinner_item));

        gridView.setAdapter(
                new Framework_Adapter<Integer>(this, content) {
                    @Override
                    protected View getViewImp(int position, View convertView, ViewGroup parent) {
                        ViewHolder holder;
                        if (convertView == null) {
                            convertView = new ImageView(Activity_Main.this);
                            holder = new ViewHolder((ImageView) convertView);
                            convertView.setTag(holder);
                        } else {
                            holder = (ViewHolder) convertView.getTag();
                        }
                        holder.imageView.setImageResource(data.get(position));
                        return convertView;
                    }

                    class ViewHolder {
                        ImageView imageView;

                        ViewHolder(ImageView imageView) {
                            this.imageView = imageView;
                        }
                    }
                });
    }

    @Override
    protected void initListener() {

    }

    @Override
    public void handleMessageImp(Message msg) {

    }
}
