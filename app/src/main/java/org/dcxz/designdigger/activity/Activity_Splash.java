package org.dcxz.designdigger.activity;

import android.os.Message;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.dcxz.designdigger.App;
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
    private static final String TAG = "Activity_Splash";
    /**
     * what:跳转至Activity_Main
     */
    private static final int TO_MAIN_ACTIVITY = 0;


    @Override
    protected int setContentViewImp() {
        if (getActionBar() != null) {
            getActionBar().hide();
        }
        return R.layout.activity_splash;
    }

    @Override
    protected void initView() {
    }

    @Override
    protected void initData() {
        //不论是否成功获取数据均应当在2500ms后跳转
        handler.sendEmptyMessageDelayed(TO_MAIN_ACTIVITY, 2500);
        App.pageRequest(
                1,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i(TAG, "onResponse: cache created");
                        // handler.sendEmptyMessage(TO_MAIN_ACTIVITY);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        toast(R.string.connect_error);
                        Log.i(TAG, "onErrorResponse: connect error");
                        error.printStackTrace();
                        //  handler.sendEmptyMessage(TO_MAIN_ACTIVITY);
                    }
                });
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