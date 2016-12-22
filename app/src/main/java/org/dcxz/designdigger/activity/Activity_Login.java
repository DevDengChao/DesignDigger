package org.dcxz.designdigger.activity;

import android.graphics.Bitmap;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;

import org.dcxz.designdigger.App;
import org.dcxz.designdigger.R;
import org.dcxz.designdigger.entity.Entity_AccessToken;
import org.dcxz.designdigger.entity.Entity_User;
import org.dcxz.designdigger.framework.Framework_Activity;
import org.dcxz.designdigger.util.API;
import org.dcxz.designdigger.dao.Dao_Manager;
import org.json.JSONObject;

/**
 * 用户进行登录认证的测试模块
 * <br/>
 * Created by DC on 2016/12/21.<br/>
 */

public class Activity_Login extends Framework_Activity {
    public static final String TAG = "Activity_Login";
    /**
     * what:跳转至Activity_Splash
     */
    private static final int TO_ACTIVITY_SPLASH = 0;
    private WebView webView;

    private ProgressBar progressBar;

    @Override
    protected int setContentViewImp() {
        return R.layout.activity_test;
    }

    @Override
    protected void initView() {
        progressBar = (ProgressBar) findViewById(R.id.login_progressBar);
        // TODO: 2016/12/22 WebView+JS实现登录 http://www.jb51.net/article/84957.htm
        webView = (WebView) findViewById(R.id.login_webView);
        webView.setWebViewClient(new WebViewClient() {
            // https://dribbble.com/login?return_to=%2Foauth%2Fauthorize%3Fclient_id%3De8e27bfbaa6d35bfe58255c68957c70aecfb444b20fc8197bf1c5f9acc1181ce%26redirect_uri%3Dhttps%3A%2F%2Fgithub.com%2FXieEDeHeiShou%2FDesignDigger
            // https://dribbble.com/oauth/authorize?client_id=e8e27bfbaa6d35bfe58255c68957c70aecfb444b20fc8197bf1c5f9acc1181ce&redirect_uri=https://github.com/XieEDeHeiShou/DesignDigger
            // https://github.com/XieEDeHeiShou/DesignDigger?code=f209a6fe11188064a0d4d0976a42947236c231b885db772880e33fe8bf217f18
            @SuppressWarnings("deprecation")
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.i(TAG, "UrlLoading: " + url);
                String callback_url = API.Oauth2.REDIRECT_URI_VALUE;
                if (url.startsWith(callback_url)) {//已经过用户认证,拦截重定向
                    Log.i(TAG, "Intercept redirecting: " + url);
                    String code = url.replace(callback_url + "?code=", "");//获取Dribbble返回的验证码
                    String tokenURL = String.format(API.Oauth2.TOKEN, code);//尝试获取access_token
                    getAccessToken(tokenURL);
                } else {//
                    view.loadUrl(url);
                }
                return true;
            }

            //{"created_at":1482373210,"token_type":"bearer","access_token":"a9e84ce4e146ad945ea83cdeca6166b1035eb1cb9f521f4bbd05a5a1b123e24e","scope":"public"}
            private void getAccessToken(String tokenURL) {
                App.getQueue().add(
                        new JsonObjectRequest(
                                Request.Method.POST,
                                tokenURL,
                                null,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        Entity_AccessToken token = new Gson().fromJson(response.toString(), Entity_AccessToken.class);
                                        String access_token = token.getAccess_token();
                                        Log.i(TAG, "onResponse: token=" + access_token);
                                        saveUser(access_token);
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        toast("Authorize failed");
                                        if (webView.canGoBack()) {
                                            webView.goBack();
                                        }
                                    }
                                }
                        ));
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                toast(R.string.connect_error);
            }
        });
        webView.loadUrl(API.Oauth2.AUTHORIZE);
    }

    /**
     * 利用ACCESS_TOKEN获取对应的用户对象
     *
     * @param access_token 链接口令
     */
    private void saveUser(String access_token) {
        Dao_Manager.getInstance(this).setAccessToken(access_token);//登录成功,更新文件中的口令
        API.Oauth2.setAccessToken(access_token);//登录成功,更新内存中的口令
        App.stringRequest(
                API.END_POINT.USER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Dao_Manager.getInstance(Activity_Login.this).setUser(response);
                        Entity_User user = new Gson().fromJson(response, Entity_User.class);
                        Log.i(TAG, "onResponse: " + user.getId() + " " + user.getUsername());
                        App.getQueue().add(new ImageRequest(user.getAvatar_url(), new Response.Listener<Bitmap>() {
                            @Override
                            public void onResponse(Bitmap response) {
                                Log.i(TAG, "onResponse: save avatar "
                                        + (Dao_Manager.getInstance(Activity_Login.this).setAvatar(response) ?
                                        "success" : "failed"));
                            }
                        }, 0, 0, Bitmap.Config.ARGB_8888, null));
                        handler.sendEmptyMessage(TO_ACTIVITY_SPLASH);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        toast("Authorize failed");
                        if (webView.canGoBack()) {
                            webView.goBack();
                        }
                    }
                });
    }

    @Override
    protected void initData() {
    }

    @Override
    protected void initAdapter() {

    }

    @Override
    protected void initListener() {
    }

    @Override
    public void handleMessageImp(Message msg) {
        if (msg.what == TO_ACTIVITY_SPLASH) {
            startActivity(Activity_Splash.class);
        }
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {//拦截返回键时间监听
            webView.canGoBack();
        } else {
            super.onBackPressed();
        }
    }
}
