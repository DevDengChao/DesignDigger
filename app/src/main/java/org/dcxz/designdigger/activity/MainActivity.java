package org.dcxz.designdigger.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.dcxz.designdigger.R;
import org.dcxz.designdigger.bean.UserInfo;
import org.dcxz.designdigger.dao.DaoManager;
import org.dcxz.designdigger.fragment.MainFragment;
import org.dcxz.designdigger.framework.BaseActivity;
import org.dcxz.designdigger.framework.BaseFragment;
import org.dcxz.designdigger.util.API;

import butterknife.BindView;
import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends BaseActivity {
    /**
     * 日志TAG
     */
    @SuppressWarnings("unused")
    private static final String TAG = "MainActivity";
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
    @BindView(R.id.activity_main_navigationView)
    NavigationView navigationView;
    /**
     * Fragment的title
     */
    private String titles[];
    /**
     * 可用的Fragment
     */
    private BaseFragment[] fragments;
    /**
     * 监听登录和注销事件
     */
    private BroadcastReceiver receiver;
    /**
     * 用户头像
     */
    private CircleImageView avatar;
    /**
     * 用户名
     */
    private TextView userName;
    /**
     * 用户id
     */
    private TextView id;
    /**
     * 用户个人投递的作品数
     */
    private TextView shotsCount;
    private DaoManager manager;
    /**
     * 用户是否已登录,影响侧滑菜单的文本显示与点击事件
     */
    private boolean isUserLogined;

    @Override
    protected int setContentViewImp() {
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        setSupportActionBar(toolbar);
        tabLayout.setupWithViewPager(viewPager);
        manager = DaoManager.getInstance(this);
        isUserLogined = !manager.getAccessToken().equals(API.Oauth2.ACCESS_TOKEN_DEFAULT);

        initMenuHeader();
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (intent.getAction()) {//监听登录/注销事件,收起侧滑菜单
                    case LoginActivity.TAG:
                        isUserLogined = true;
                        signIn();
                        break;
                    case SettingsActivity.TAG:
                        drawerLayout.closeDrawer(GravityCompat.START, true);
                        isUserLogined = false;
                        signOut();
                        break;
                }
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(LoginActivity.TAG);
        filter.addAction(SettingsActivity.TAG);
        registerReceiver(receiver, filter);
    }

    /**
     * 初始化navigationView上的headerView
     */
    private void initMenuHeader() {
        View menuHeader = navigationView.getHeaderView(0);
        avatar = (CircleImageView) menuHeader.findViewById(R.id.menu_avatar);
        userName = (TextView) menuHeader.findViewById(R.id.menu_userName);
        id = (TextView) menuHeader.findViewById(R.id.menu_id);
        shotsCount = (TextView) menuHeader.findViewById(R.id.menu_shotsCount);
        if (isUserLogined) {
            signIn();
        } else {
            signOut();
        }
    }

    /**
     * 用户已登录
     */
    private void signIn() {
        UserInfo userInfo = manager.getUser();
        avatar.setImageBitmap(manager.getAvatar());
        String userNameString = userInfo.isPro() ? getString(R.string.menu_userName_pro) : "";
        userNameString += userInfo.getType() + " " + userInfo.getName();
        userName.setText(userNameString);
        String idString = getString(R.string.menu_id) + userInfo.getId();
        id.setText(idString);
        String shotsCountString = getString(R.string.menu_shotsCount) + userInfo.getShots_count();
        shotsCount.setText(shotsCountString);
    }

    /**
     * 用户已注销
     */
    private void signOut() {
        avatar.setImageResource(R.mipmap.dribbble_ball_mark);
        userName.setText(R.string.menu_clickToSignIn);
        id.setText("");
        shotsCount.setText("");
    }

    @Override
    protected void initData() {
        int count = 3;
        fragments = new BaseFragment[count];
        fragments[0] = MainFragment.newInstance(false, false, null);
        fragments[1] = MainFragment.newInstance(true, false, null);
        fragments[2] = MainFragment.newInstance(false, true, DaoManager.getInstance(this).getUser());

        titles = new String[count];
        titles[0] = "Following";
        titles[1] = "Rank";
        titles[2] = "Profile";
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
        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.main_drawerOpened, R.string.main_drawerClosed);
        toggle.syncState();
        drawerLayout.addDrawerListener(toggle);

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isUserLogined) {
                    viewPager.setCurrentItem(3, true);
                    drawerLayout.closeDrawer(GravityCompat.START, true);
                } else {
                    startActivity(LoginActivity.class);
                }
            }
        };
        avatar.setOnClickListener(listener);
        userName.setOnClickListener(listener);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_settings:
                        startActivity(SettingsActivity.class);
                }
                return true;
            }
        });
    }

    @Override
    public void handleMessageImp(Message msg) {

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }
}
