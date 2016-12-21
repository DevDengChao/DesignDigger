package org.dcxz.designdigger.activity;

import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;

import org.dcxz.designdigger.R;
import org.dcxz.designdigger.fragment.Fragment_Menu;
import org.dcxz.designdigger.fragment.Fragment_Visitor;
import org.dcxz.designdigger.framework.Framework_Activity;
import org.dcxz.designdigger.framework.Framework_Fragment;
import org.dcxz.designdigger.view.flowing_drawer.FlowingView;
import org.dcxz.designdigger.view.flowing_drawer.LeftDrawerLayout;

public class Activity_Main extends Framework_Activity {
    /**
     * 日志TAG
     */
    @SuppressWarnings("unused")
    private static final String TAG = "Activity_Main";
    /**
     * 用于切换Fragment的ViewPager
     */
    private ViewPager viewPager;
    /**
     * 可用的Fragment
     */
    private Framework_Fragment[] fragments;
    /**
     * FlowingDrawer容器
     */
    private LeftDrawerLayout drawerLayout;
    /**
     * 侧滑菜单
     */
    private Fragment_Menu menu;
    /**
     * 工具栏
     */
    private Toolbar toolbar;

    @Override
    protected int setContentViewImp() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        viewPager = (ViewPager) findViewById(R.id.activity_main_viewPager);
        initToolBar();
        initFlowingDrawer();
    }

    private void initToolBar() {
        toolbar = (Toolbar) findViewById(R.id.activity_main_toolBar);
        toolbar.setNavigationIcon(R.mipmap.dribbble_ball_mark);
        setSupportActionBar(toolbar);
    }

    private void initFlowingDrawer() {
        //https://github.com/mxn21/FlowingDrawer
        // TODO: 2016/12/21 drawerLayout事件监听
        drawerLayout = (LeftDrawerLayout) findViewById(R.id.main_drawerLayout);
        menu = (Fragment_Menu) getFragmentManager().findFragmentById(R.id.main_menu);
        drawerLayout.setFluidView((FlowingView) findViewById(R.id.flowingView));
        drawerLayout.setMenuFragment(menu);
    }

    @Override
    protected void initData() {
        fragments = new Framework_Fragment[1];
        fragments[0] = new Fragment_Visitor();
    }

    @Override
    protected void initAdapter() {
        viewPager.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return fragments[position];
            }

            @Override
            public int getCount() {
                return fragments.length;
            }
        });
    }

    @Override
    protected void initListener() {
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.toggle();
            }
        });
    }

    @Override
    public void handleMessageImp(Message msg) {

    }
}
