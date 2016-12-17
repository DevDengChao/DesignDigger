package org.dcxz.designdigger.activity;

import android.os.Message;
import android.support.v7.app.ActionBar;

import org.dcxz.designdigger.view.flowing_drawer.FlowingView;
import org.dcxz.designdigger.view.flowing_drawer.LeftDrawerLayout;

import org.dcxz.designdigger.R;
import org.dcxz.designdigger.fragment.Fragment_Menu;
import org.dcxz.designdigger.framework.Framework_Activity;

public class Activity_Main extends Framework_Activity {
    private ActionBar actionBar;

    private LeftDrawerLayout drawerLayout;
    private Fragment_Menu menu;

    @Override
    protected int setContentViewImp() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        initActionBar();
        initFlowingDrawer();
    }

    // TODO: 2016/12/15 调试ActionBar与侧滑菜单
    @SuppressWarnings("ConstantConditions")
    private void initActionBar() {
        actionBar = getSupportActionBar();
        actionBar.setLogo(R.mipmap.dribbble_logo);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_USE_LOGO);
    }

    private void initFlowingDrawer() {
        //https://github.com/mxn21/FlowingDrawer
        drawerLayout = (LeftDrawerLayout) findViewById(R.id.main_drawerLayout);
        menu = new Fragment_Menu();
        getSupportFragmentManager().beginTransaction().replace(R.id.main_menuContainer, menu).commit();
        drawerLayout.setFluidView((FlowingView) findViewById(R.id.flowingView));
        drawerLayout.setMenuFragment(menu);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initAdapter() {

    }

    @Override
    protected void initListener() {

    }

    @Override
    public void handleMessageImp(Message msg) {

    }
}
