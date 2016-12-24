package org.dcxz.designdigger;

import android.app.Application;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.dcxz.designdigger.dao.Dao_Manager;
import org.dcxz.designdigger.util.API;

import java.util.HashMap;
import java.util.Map;

/**
 * Application对象,用于初始化{@link #queue}
 * <br/>
 * Created by DC on 2016/12/13.<br/>
 */

public class App extends Application {
    /**
     * Volley的请求队列静态实例
     */
    private static RequestQueue queue;
    /**
     * 访问DribbbleAPI必要的头信息
     */
    private static HashMap<String, String> header;
    private static Dao_Manager manager;

    /**
     * 获取请求队列
     *
     * @return 当前应用持有的请求队列
     */
    public static RequestQueue getQueue() {// TODO: 2016/12/22 findUsage
        return queue;
    }

    /**
     * 更新AccessToken
     */
    public static void updateHeader() {
        API.Oauth2.setAccessToken(manager.getAccessToken());
        header.put(API.Oauth2.AUTHORIZATION, API.Oauth2.AUTHORIZATION_TYPE + API.Oauth2.ACCESS_TOKEN);
    }

    /**
     * 通过DribbbleAPI进行字符串请求
     *
     * @param url           目标地址
     * @param listener      响应成功监听器
     * @param errorListener 响应失败监听器
     */
    public static void stringRequest(String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        queue.add(new StringRequest(url, listener, errorListener) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return header;
            }
        });
    }

    /**
     * 通过DribbbleAPI进行字符串请求
     *
     * @param url           目标地址
     * @param listener      响应成功监听器
     * @param errorListener 响应失败监听器
     * @param tag           请求标签,用于取消请求
     */
    public static void stringRequest(String url, Response.Listener<String> listener, Response.ErrorListener errorListener, String tag) {
        queue.add(new StringRequest(url, listener, errorListener) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return header;
            }
        }.setTag(tag));
    }

    /**
     * 通过DribbbleAPI进行图像请求(原尺寸);
     *
     * @param url       目标地址
     * @param imageView 将要设置图像的ImageView
     * @param tag       请求标签,用于取消请求
     */
    public static void imageRequest(String url, final ImageView imageView, String tag) {
        // TODO: 2016/12/22 定制图像大小
        queue.add(new ImageRequest(url, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                imageView.setImageBitmap(response);
            }
        }, 0, 0, ImageView.ScaleType.FIT_CENTER, Bitmap.Config.ARGB_8888, null) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return header;
            }
        }.setTag(tag));
    }

    @Override
    public void onCreate() {
        super.onCreate();
        queue = Volley.newRequestQueue(this);
        manager = Dao_Manager.getInstance(this);
        header = new HashMap<>();
        updateHeader();
    }

}
