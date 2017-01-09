package org.dcxz.designdigger.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;

import org.dcxz.designdigger.R;
import org.dcxz.designdigger.dao.Dao_Manager;
import org.dcxz.designdigger.fragment.Fragment_Profile;
import org.dcxz.designdigger.fragment.Fragment_Rank;
import org.dcxz.designdigger.framework.Framework_Activity;
import org.dcxz.designdigger.framework.Framework_Fragment;

import butterknife.BindView;

public class Activity_Main extends Framework_Activity {
    /**
     * 日志TAG
     */
    @SuppressWarnings("unused")
    private static final String TAG = "Activity_Main";
    /**
     * 用于切换Fragment的ViewPager
     */
    @BindView(R.id.activity_main_viewPager)
    ViewPager viewPager;
    /**
     * 工具栏
     */
    @BindView(R.id.activity_main_toolBar)
    Toolbar toolbar;
    /**
     * 侧滑菜单
     */
    @BindView(R.id.main_drawerLayout)
    DrawerLayout drawerLayout;
    /**
     * 标签
     */
    @BindView(R.id.activity_main_tabLayout)
    TabLayout tabLayout;
    /**
     * Fragment的title
     */
    private String titles[];
    /**
     * 可用的Fragment
     */
    private Framework_Fragment[] fragments;
    private BroadcastReceiver receiver;

    @Override
    protected int setContentViewImp() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        setSupportActionBar(toolbar);
        tabLayout.setupWithViewPager(viewPager);
        //监听登录成功事件,收起侧滑菜单
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                drawerLayout.closeDrawer(GravityCompat.START);
            }
        };
        registerReceiver(receiver, new IntentFilter(Activity_Login.TAG));
    }

    @Override
    protected void initData() {
        int count = 2;
        fragments = new Framework_Fragment[count];
//        fragments[0] = new Fragment_Following();
        fragments[0] = Fragment_Profile.newInstance(Dao_Manager.getInstance(this).getUser());
        fragments[1] = new Fragment_Rank();
//        fragments[2] = Fragment_Profile.newInstance(Dao_Manager.getInstance(this).getUser());

        titles = new String[count];
//        titles[0] = "Following";
        titles[0] = "Profile";
        titles[1] = "Rank";
//        titles[2] = "Profile";
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }
}
