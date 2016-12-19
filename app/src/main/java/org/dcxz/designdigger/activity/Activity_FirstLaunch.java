package org.dcxz.designdigger.activity;

import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import org.dcxz.designdigger.R;
import org.dcxz.designdigger.adapter.Adapter_FirstLaunch;
import org.dcxz.designdigger.framework.Framework_Activity;
import org.dcxz.designdigger.util.Util_SP_Manager;
import org.dcxz.designdigger.view.cricle_indicator.CircleIndicator;

import java.util.ArrayList;

/**
 * 首次启动应用时进入的介绍页面
 * <br/>
 * Created by OvO on 2016/12/13.<br/>
 * ChangeLog :
 * <pre>
 * </pre>
 */

public class Activity_FirstLaunch extends Framework_Activity {
    private static final String TAG = "Activity_FirstLaunch";
    /**
     * what:跳转至Activity_Slash
     */
    private static final int TO_SLASH_ACTIVITY = 0;
    /**
     * ViewPager容器
     */
    private ViewPager viewPager;
    /**
     * ViewPager中的内容
     */
    private ArrayList<ImageView> content;
    /**
     * 底部的指示器
     */
    private CircleIndicator circleIndicator;
    /**
     * 当前页面的数量
     */
    private int pageCount = 3;

    @Override
    protected int setContentViewImp() {
        if (getActionBar() != null) {
            getActionBar().hide();
        }
        return R.layout.activity_first_launch;
    }

    @Override
    protected void initView() {
        Util_SP_Manager manager = Util_SP_Manager.getInstance(this);
        if (manager.getBoolean(Util_SP_Manager.IS_NOT_FIRST_LAUNCH)) {//不是第一次启动应用
            Log.i(TAG, "initView: Not first launch");
            handler.sendEmptyMessage(TO_SLASH_ACTIVITY);
        }//是第一次启动应用
        Log.i(TAG, "initView: First launch");
        manager.putBoolean(Util_SP_Manager.IS_NOT_FIRST_LAUNCH, true);
        viewPager = (ViewPager) this.findViewById(R.id.firstLaunch_viewPager);
        circleIndicator = (CircleIndicator) this.findViewById(R.id.firstLaunch_indicator);
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
        for (int i = 0; i < pageCount; i++) {
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
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if ((pageCount - 1) == position) {
                    handler.sendEmptyMessageDelayed(TO_SLASH_ACTIVITY, 2000);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void handleMessageImp(Message msg) {
        if (msg.what == TO_SLASH_ACTIVITY) {
            startActivity(Activity_Slash.class);//父类的包装方法
            finish();
        }
    }
}
