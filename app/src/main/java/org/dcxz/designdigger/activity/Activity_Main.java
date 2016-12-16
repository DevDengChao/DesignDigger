package org.dcxz.designdigger.activity;

import android.os.Message;
import android.support.v7.app.ActionBar;

import org.dcxz.designdigger.R;
import org.dcxz.designdigger.framework.Framework_Activity;

public class Activity_Main extends Framework_Activity {
    private ActionBar actionBar;

    @Override
    protected int setContentViewImp() {
        return R.layout.activity_main_reforge;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void initView() {
        // TODO: 2016/12/15 调试ActionBar与侧滑菜单
        actionBar = getSupportActionBar();
        actionBar.setLogo(R.mipmap.dribbble_logo);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_USE_LOGO);

        //https://github.com/mxn21/FlowingDrawer

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
