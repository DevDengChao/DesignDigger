package org.dcxz.designdigger.activity;

import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;

import org.dcxz.designdigger.R;
import org.dcxz.designdigger.fragment.Fragment_Following;
import org.dcxz.designdigger.fragment.Fragment_Rank;
import org.dcxz.designdigger.framework.Framework_Activity;
import org.dcxz.designdigger.framework.Framework_Fragment;

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
    private String titles[];
    /**
     * 可用的Fragment
     */
    private Framework_Fragment[] fragments;
    /**
     * 工具栏
     */
    private Toolbar toolbar;
    /**
     * 侧滑菜单
     */
    private DrawerLayout drawerLayout;

    @Override
    protected int setContentViewImp() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        viewPager = (ViewPager) findViewById(R.id.activity_main_viewPager);
        toolbar = (Toolbar) findViewById(R.id.activity_main_toolBar);
        setSupportActionBar(toolbar);
        drawerLayout = (DrawerLayout) findViewById(R.id.main_drawerLayout);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.activity_main_tabLayout);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    protected void initData() {
        fragments = new Framework_Fragment[2];
        fragments[0] = new Fragment_Following();
        fragments[1] = new Fragment_Rank();
        titles = new String[2];
        titles[0] = "Following";
        titles[1] = "Rank";
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

            @Override
            public CharSequence getPageTitle(int position) {
                return titles[position];
            }
        });
    }

    @Override
    protected void initListener() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.main_drawerOpened, R.string.main_drawerClosed);
        toggle.syncState();
        drawerLayout.addDrawerListener(toggle);
    }

    @Override
    public void handleMessageImp(Message msg) {

    }
}
