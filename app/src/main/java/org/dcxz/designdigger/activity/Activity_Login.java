package org.dcxz.designdigger.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
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
import org.dcxz.designdigger.dao.Dao_Manager;
import org.dcxz.designdigger.entity.Entity_AccessToken;
import org.dcxz.designdigger.entity.Entity_User;
import org.dcxz.designdigger.fragment.Fragment_Menu;
import org.dcxz.designdigger.framework.Framework_Activity;
import org.dcxz.designdigger.util.API;
import org.json.JSONObject;

/**
 * 用户通过Oauth2进行登录认证<br/>
 * 启动这个Activity的方式分别来自Activity_FirstLaunch和Fragment_Menu
 * <br/>
 * Created by DC on 2016/12/21.<br/>
 */

public class Activity_Login extends Framework_Activity {
    public static final String TAG = "Activity_Login";
    /**
     * what:跳转至Activity_Splash
     */
    private static final int TO_ACTIVITY_SPLASH = 0;
    /**
     * what:跳转至Activity_Main
     */
    private static final int TO_ACTIVITY_MAIN = 1;
    private WebView webView;

    private ProgressBar progressBar;
    /**
     * 标识哪个Activity唤醒了这个Activity,用于区分跳转目标
     */
    private String extraString;
    /**
     * 用于从文件中存取数据
     */
    private Dao_Manager manager;

    @Override
    protected int setContentViewImp() {
        return R.layout.activity_login;
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void initView() {
        manager = Dao_Manager.getInstance(Activity_Login.this);
        extraString = getIntent().getStringExtra(STATE);
        if (extraString.equals(Fragment_Menu.TAG)) {//从Fragment_Menu启动,说明是注销后再登录,因此需要清理前一个用户的登录信息
            CookieManager.getInstance().removeAllCookie();
            Log.i(TAG, "initView: Cookie cleared");
        }

        progressBar = (ProgressBar) findViewById(R.id.login_progressBar);
        // TODO: 2016/12/22 优化:WebView+JS实现登录 http://www.jb51.net/article/84957.htm
        webView = (WebView) findViewById(R.id.login_webView);
        webView.setWebViewClient(
                new WebViewClient() {
                    // https://dribbble.com/login?return_to=%2Foauth%2Fauthorize%3F
                    // client_id%3De8e27bfbaa6d35bfe58255c68957c70aecfb444b20fc8197bf1c5f9acc1181ce%26
                    // redirect_uri%3Dhttps%3A%2F%2Fgithub.com%2FXieEDeHeiShou%2FDesignDigger

                    // https://dribbble.com/oauth/authorize?
                    // client_id=e8e27bfbaa6d35bfe58255c68957c70aecfb444b20fc8197bf1c5f9acc1181ce&
                    // redirect_uri=https://github.com/XieEDeHeiShou/DesignDigger

                    // https://github.com/XieEDeHeiShou/DesignDigger?
                    // code=f209a6fe11188064a0d4d0976a42947236c231b885db772880e33fe8bf217f18
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

                    //{"created_at":1482373210,
                    // "token_type":"bearer",
                    // "access_token":"a9e84ce4e146ad945ea83cdeca6166b1035eb1cb9f521f4bbd05a5a1b123e24e",
                    // "scope":"public"}
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
                                                toast("Authorize failed:Cannot getAccess Token");
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

                });
        webView.loadUrl(API.Oauth2.AUTHORIZE);
    }

    /**
     * 利用ACCESS_TOKEN获取对应的用户对象
     *
     * @param access_token 链接口令
     */
    private void saveUser(String access_token) {
        manager.setAccessToken(access_token);//登录成功,更新文件中的口令
        API.Oauth2.setAccessToken(access_token);//登录成功,更新内存中的口令
        App.updateHeader();//登录成功,更新请求头中的口令
        App.stringRequest(
                API.EndPoint.USER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        manager.setUser(response);
                        Entity_User user = new Gson().fromJson(response, Entity_User.class);
                        Log.i(TAG, "onResponse: " + user.getId() + " " + user.getUsername());
                        App.getQueue().add(
                                new ImageRequest(user.getAvatar_url(),
                                        new Response.Listener<Bitmap>() {
                                            @Override
                                            public void onResponse(Bitmap response) {
                                                if (manager.setAvatar(response)) {
                                                    Log.i(TAG, "onResponse: download avatar success");
                                                    sendBroadcast(new Intent(TAG));
                                                } else {
                                                    Log.i(TAG, "onResponse: download avatar failed:File IO Exception");
                                                    toast("Download avatar failed:File IO Exception");
                                                }
                                            }
                                        },
                                        0,
                                        0,
                                        Bitmap.Config.ARGB_8888,
                                        new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                toast("Download avatar failed:Connection Error");
                                            }
                                        }));
                        Log.i(TAG, "onResponse: start this activity form " + extraString);
                        if (extraString.equals(Fragment_Menu.TAG)) {
                            handler.sendEmptyMessage(TO_ACTIVITY_MAIN);
                        } else if (extraString.equals(Activity_FirstLaunch.TAG)) {
                            handler.sendEmptyMessage(TO_ACTIVITY_SPLASH);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        toast("Authorize failed:Cannot get user");
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
        switch (msg.what) {
            case TO_ACTIVITY_SPLASH:
                startActivity(Activity_Splash.class);
                break;
            case TO_ACTIVITY_MAIN:
                startActivity(Activity_Main.class);
                break;
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
