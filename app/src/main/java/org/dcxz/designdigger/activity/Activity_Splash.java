package org.dcxz.designdigger.activity;

import android.os.Message;

import org.dcxz.designdigger.R;
import org.dcxz.designdigger.framework.Framework_Activity;

/**
 * <br/>
 * Created by OvO on 2016/12/13.<br/>
 * ChangeLog :
 * <pre>
 * </pre>
 */

public class Activity_Splash extends Framework_Activity {
    /**
     * 日志Tag
     */
    @SuppressWarnings("unused")
    private static final String TAG = "Activity_Splash";
    /**
     * what:跳转至Activity_Main
     */
    private static final int TO_MAIN_ACTIVITY = 0;


    @Override
    protected int setContentViewImp() {
        return R.layout.activity_splash;
    }

    @Override
    protected void initView() {
    }

    @Override
    protected void initData() {
        handler.sendEmptyMessageDelayed(TO_MAIN_ACTIVITY, 1500);// TODO: 2017/1/5 update check (github release)
    }

    @Override
    protected void initAdapter() {

    }

    @Override
    protected void initListener() {

    }

    @Override
    public void handleMessageImp(Message msg) {
        if (msg.what == TO_MAIN_ACTIVITY) {
            startActivity(Activity_Main.class);
            finish();
        }
    }
}
