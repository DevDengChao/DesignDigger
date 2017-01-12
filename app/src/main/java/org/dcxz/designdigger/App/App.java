package org.dcxz.designdigger.app;

import android.app.Application;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.dcxz.designdigger.R;
import org.dcxz.designdigger.dao.DaoManager;
import org.dcxz.designdigger.util.API;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

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
    private static DaoManager manager;

    /**
     * 获取请求队列
     *
     * @return 当前应用持有的请求队列
     */
    public static RequestQueue getQueue() {
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

    /**
     * 通过DribbbleAPI进行图像请求(原尺寸);
     *
     * @param url        目标地址
     * @param imageView  将要设置图像的ImageView
     * @param controller 用于控制播放/暂停的控件
     * @param tag        请求标签,用于取消请求
     */
    public static void gifRequest(String url, final GifImageView imageView, final ImageView controller, String tag) {
        queue.add(new Request<byte[]>(Request.Method.GET, url, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                imageView.setImageResource(R.drawable.connection_error);
                error.printStackTrace();
            }
        }) {//实现抽象方法
            /**
             * 用于展示Gif的drawable对象
             */
            private GifDrawable gifDrawable;

            @Override
            protected Response<byte[]> parseNetworkResponse(NetworkResponse response) {
                if (response.statusCode != HttpURLConnection.HTTP_OK) {
                    return Response.error(new VolleyError("Response code error:" + response.statusCode));
                }
                return Response.success(response.data, HttpHeaderParser.parseCacheHeaders(response));//模仿ImageRequest，设置响应成功时的返回结果
            }

            /**
             * 重写响应成功的回调方法（也可以利用监听模式设计该方法{@link StringRequest#deliverResponse(String)}）
             */
            @Override
            protected void deliverResponse(byte[] response) {
                try {//直接将获得的字节数组交给GifDrawable
                    gifDrawable = new GifDrawable(response);
                    imageView.setImageDrawable(gifDrawable);// FIXME: 2017/1/2 Gif始终不动
                    controller.setOnClickListener(
                            new View.OnClickListener() {
                                private static final String TAG = "Controller";

                                @Override
                                public void onClick(View v) {
                                    if (gifDrawable.isPlaying()) {
                                        controller.setImageResource(R.drawable.item_gif_play);
                                        gifDrawable.pause();
                                        Log.i(TAG, "onClick: playing->pause");
                                    } else {
                                        controller.setImageResource(R.drawable.item_gif_pause);
                                        gifDrawable.start();
                                        Log.i(TAG, "onClick: pause->playing");
                                    }
                                }
                            });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return header;
            }
        }).setTag(tag);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        queue = Volley.newRequestQueue(this);
        manager = DaoManager.getInstance(this);
        header = new HashMap<>();
        header.put("Accept", "application/json");
        header.put("Accept-Charset", "utf-8");
        updateHeader();
    }

}
