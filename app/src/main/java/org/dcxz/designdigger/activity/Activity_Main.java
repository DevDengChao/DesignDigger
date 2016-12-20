package org.dcxz.designdigger.activity;

import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;

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
    private static final String TAG = "Activity_Main";
    /**
     * 用于切换Fragment的ViewPager
     */
    private ViewPager viewPager;
    /**
     * FlowingDrawer容器
     */
    private LeftDrawerLayout drawerLayout;
    /**
     * 侧滑菜单
     */
    private Fragment_Menu menu;

    private Framework_Fragment[] fragments;

    @Override
    protected int setContentViewImp() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        viewPager = (ViewPager) findViewById(R.id.activity_main_viewPager);
        initFlowingDrawer();
    }

    private void initFlowingDrawer() {
        //https://github.com/mxn21/FlowingDrawer
        drawerLayout = (LeftDrawerLayout) findViewById(R.id.main_drawerLayout);
        menu = new Fragment_Menu();
        getFragmentManager().beginTransaction().replace(R.id.main_menuContainer, menu).commit();
        drawerLayout.setFluidView((FlowingView) findViewById(R.id.flowingView));
        drawerLayout.setMenuFragment(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // getMenuInflater().inflate(R.menu.actionbar_menu, menu);
        return true;
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

    }

    @Override
    public void handleMessageImp(Message msg) {

    }
}
