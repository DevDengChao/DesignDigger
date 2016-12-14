package org.dcxz.designdigger.activity;

import android.graphics.Bitmap;
import android.os.Message;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.dcxz.designdigger.App;
import org.dcxz.designdigger.R;
import org.dcxz.designdigger.entity.Entity_Shot;
import org.dcxz.designdigger.framework.Framework_Activity;
import org.dcxz.designdigger.util.Util_DribbbleAPI;

import java.text.Format;
import java.util.Formatter;

/**
 * <br/>
 * Created by OvO on 2016/12/13.<br/>
 * ChangeLog :
 * <pre>
 * </pre>
 */

public class Activity_Slash extends Framework_Activity {
    /**
     * 日志Tag
     */
    private static final String TAG = "Activity_Slash";
    /**
     * what:跳转至Activity_Main
     */
    private static final int TO_MAIN_ACTIVITY = 0;


    @Override
    protected int setContentViewImp() {
        return R.layout.activity_slash;
    }

    @Override
    protected void initView() {
    }

    @Override
    protected void initData() {
        //尝试访问数据库,查看今天是否已经加载过数
        // 是:1500ms后进入Activity_Main;
        // 否:则发送字符串请求和后续的图像请求,将数据存入数据库
        //    生成的缓存图像将在Activity_Main调用,预览图大概29k每幅.
        //    1500ms后进入Activity_Main;
        //
        App.stringRequest(
                //TODO: 利用String.format("%d",1);修改StringRequest
                Util_DribbbleAPI.END_POINT_SHOTS + "/?page=1",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {//访问成功,解析数据为对象并存入数据库
                        Entity_Shot shots[] = new Gson().fromJson(
                                response,
                                new TypeToken<Entity_Shot[]>() {
                                }.getType());
                        Log.i(TAG, "onResponse: " + shots.length);
                        for (Entity_Shot s : shots) {
                            Log.i(TAG, "onResponse: " + s.getId());
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        handler.sendEmptyMessage(TO_MAIN_ACTIVITY);
                    }
                });
        App.stringRequest(
                Util_DribbbleAPI.END_POINT_SHOTS + "/?page=2",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {//访问成功,解析数据为对象并存入数据库
                        Entity_Shot shots[] = new Gson().fromJson(
                                response,
                                new TypeToken<Entity_Shot[]>() {
                                }.getType());
                        Log.i(TAG, "onResponse: " + shots.length);
                        for (Entity_Shot s : shots) {
                            Log.i(TAG, "onResponse: " + s.getId());
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        handler.sendEmptyMessage(TO_MAIN_ACTIVITY);
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
