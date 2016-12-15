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

import org.dcxz.designdigger.util.Util_DribbbleAPI;

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

    @Override
    public void onCreate() {
        super.onCreate();
        queue = Volley.newRequestQueue(this);
        header = new HashMap<>();
        header.put(Util_DribbbleAPI.AUTHORIZATION_KEY, Util_DribbbleAPI.AUTHORIZATION_VALUE);
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
     * 通过DribbbleAPI进行字符串请求,获取指定页的Shots
     *
     * @param page          页码
     * @param listener      响应成功监听器
     * @param errorListener 响应失败监听器
     */
    public static void pageRequest(int page, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        queue.add(new StringRequest(Util_DribbbleAPI.END_POINT_SHOTS_PAGE + page, listener, errorListener) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return header;
            }
        });
    }

    /**
     * 通过DribbbleAPI进行图像请求(原尺寸);
     *
     * @param url           目标地址
     * @param listener      响应成功监听器
     * @param errorListener 响应失败监听器
     */
    public static void imageRequest(String url, Response.Listener<Bitmap> listener, Response.ErrorListener errorListener) {
        queue.add(new ImageRequest(url, listener, 0, 0, ImageView.ScaleType.FIT_CENTER, Bitmap.Config.ARGB_8888, errorListener) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return header;
            }
        });
    }
}
