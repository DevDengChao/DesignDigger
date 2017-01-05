package org.dcxz.designdigger.activity;

import android.os.Message;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.widget.ImageView;

import org.dcxz.designdigger.R;
import org.dcxz.designdigger.adapter.Adapter_FirstLaunch;
import org.dcxz.designdigger.dao.Dao_Manager;
import org.dcxz.designdigger.framework.Framework_Activity;
import org.dcxz.designdigger.view.cricle_indicator.CircleIndicator;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 首次启动应用时进入的介绍页面
 * <br/>
 * Created by OvO on 2016/12/13.<br/>
 * ChangeLog :
 * <pre>
 * </pre>
 */

public class Activity_FirstLaunch extends Framework_Activity {
    public static final String TAG = "Activity_FirstLaunch";
    /**
     * what:跳转至Activity_Splash
     */
    private static final int TO_SPLASH_ACTIVITY = 0;
    /**
     * what:跳转至Activity_Login
     */
    private static final int TO_LOGIN_ACTIVITY = 1;
    /**
     * ViewPager容器
     */
    @BindView(R.id.firstLaunch_viewPager)
    ViewPager viewPager;
    /**
     * 底部的指示器
     */
    @BindView(R.id.firstLaunch_indicator)
    CircleIndicator circleIndicator;
    /**
     * ViewPager中的内容
     */
    private ArrayList<ImageView> content;

    @Override
    protected int setContentViewImp() {
        return R.layout.activity_first_launch;
    }

    @Override
    protected void initView() {
        Dao_Manager manager = Dao_Manager.getInstance(this);
        if (manager.isFirstLaunch()) {
            Log.i(TAG, "initView: First launch");
            manager.setFirstLaunch(false);
        } else {
            Log.i(TAG, "initView: Not first launch");
            handler.sendEmptyMessage(TO_SPLASH_ACTIVITY);
        }
    }

    @Override
    protected void initData() {
        content = new ArrayList<>();
        // TODO: 2016/12/13 需要准备素材图像
        int resID[] = new int[]{
                R.mipmap.ic_launcher,
                R.mipmap.ic_launcher,
                R.mipmap.ic_launcher
        };
        ImageView imageView;
        for (int i = 0; i < 3; i++) {
            imageView = new ImageView(this);
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            imageView.setImageResource(resID[i]);
            content.add(imageView);
        }
    }

    @Override
    protected void initAdapter() {
        viewPager.setAdapter(new Adapter_FirstLaunch<>(content));
        circleIndicator.setViewPager(viewPager);
    }

    @Override
    protected void initListener() {
    }

    @OnClick(R.id.firstLaunch_visit)
    public void visit() {
        handler.sendEmptyMessage(TO_SPLASH_ACTIVITY);
    }

    @OnClick(R.id.firstLaunch_signIn)
    public void signIn() {
        handler.sendEmptyMessage(TO_LOGIN_ACTIVITY);
    }

    @OnClick(R.id.firstLaunch_signUp)
    public void signUp() {
        Log.i(TAG, "signUp");// TODO: 2017/1/2 Sign up
    }

    @Override
    public void handleMessageImp(Message msg) {
        switch (msg.what) {
            case TO_SPLASH_ACTIVITY:
                startActivity(Activity_Splash.class);//父类的包装方法
                break;
            case TO_LOGIN_ACTIVITY:
                startActivity(Activity_Login.class, Activity_FirstLaunch.TAG);
                break;
        }
        finish();
    }
}
