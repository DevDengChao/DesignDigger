package org.dcxz.designdigger.activity;

import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import org.dcxz.designdigger.R;
import org.dcxz.designdigger.framework.Framework_Activity;
import org.dcxz.designdigger.util.Util_SP_Manager;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import pers.medusa.circleindicator.widget.CircleIndicator;

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
     * ViewPager容器
     */
    private ViewPager viewPager;
    private CircleIndicator circleIndicator;
    private List<View> viewList;
    /**
     * ViewPager中的内容
     */
    private ImageView content[];

    /**
     * 底部的指示器
     */
    private ImageView indicators[];

    /**
     * 当前页面的数量
     */
    private int count = 3;

    /**
     * what:跳转至Activity_Slash
     */
    private static final int TO_SLASH_ACTIVITY = 0;


    @Override
    protected int setContentViewImp() {
        return R.layout.activity_first_launch;
    }

    @Override
    protected void initView() {
        viewPager = (ViewPager) this.findViewById(R.id.firstLaunch_viewPager);
        Log.i(TAG, "initView: " + (viewPager == null));
        circleIndicator = (CircleIndicator) this.findViewById(R.id.indicator);
        Log.i(TAG, "initView: " + (circleIndicator == null));

    }

    @Override
    protected void initData() {
        // TODO: 2016/12/13 需要准备素材图像
        viewList = new ArrayList<View>();
        Random random = new Random();
        for (int i = 0; i < 5; i++) {
            View view = new View(this);
            view.setBackgroundColor(0xff000000 | random.nextInt(0x00ffffff));
            viewList.add(view);
        }
    }

    @Override
    protected void initAdapter() {
        viewPager.setAdapter(new PagerAdapter() {

            @Override
            public boolean isViewFromObject(View arg0, Object arg1) {

                return arg0 == arg1;
            }

            @Override
            public int getCount() {

                return viewList.size();
            }

            @Override
            public void destroyItem(ViewGroup container, int position,
                                    Object object) {
                container.removeView(viewList.get(position));

            }

            @Override
            public int getItemPosition(Object object) {

                return super.getItemPosition(object);
            }

            @Override
            public CharSequence getPageTitle(int position) {

                return "title";
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                container.addView(viewList.get(position));

                return viewList.get(position);
            }


        });
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

                if ((count - 1) == position) {
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
