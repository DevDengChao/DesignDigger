package org.dcxz.designdigger.activity;

import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import org.dcxz.designdigger.R;
import org.dcxz.designdigger.framework.Framework_Activity;

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
    private ImageView content[];
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
        return R.layout.activity_first_launch;
    }

    @Override
    protected void initView() {
        // TODO: 2016/12/15 调试完成后应当修正跳转逻辑
        /*Util_SP_Manager manager = Util_SP_Manager.getInstance(this);
        if (manager.getBoolean(Util_SP_Manager.IS_NOT_FIRST_LAUNCH)) {//不是第一次启动应用
            Log.i(TAG, "initView: Not first launch");
            handler.sendEmptyMessage(TO_SLASH_ACTIVITY);
        } else {//是第一次启动应用
            Log.i(TAG, "initView: First launch");
            manager.putBoolean(Util_SP_Manager.IS_NOT_FIRST_LAUNCH, true);
            viewPager = (ViewPager) this.findViewById(R.id.firstLaunch_viewPager);
            circleIndicator = (CircleIndicator) this.findViewById(R.id.firstLaunch_indicator);
        }*/
        viewPager = (ViewPager) this.findViewById(R.id.firstLaunch_viewPager);
        circleIndicator = (CircleIndicator) this.findViewById(R.id.firstLaunch_indicator);
    }

    @Override
    protected void initData() {
        // TODO: 2016/12/13 需要准备素材图像
        content = new ImageView[pageCount];
        for (int i = 0; i < pageCount; i++) {
            content[i] = new ImageView(this);
            content[i].setScaleType(ImageView.ScaleType.FIT_CENTER);
            content[i].setImageResource(R.mipmap.ic_launcher);
        }
    }

    @Override
    protected void initAdapter() {
        viewPager.setAdapter(new PagerAdapter() {
            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public int getCount() {
                return pageCount;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView(content[position]);
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                container.addView(content[position]);
                return content[position];
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
