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

import org.dcxz.designdigger.R;
import org.dcxz.designdigger.app.App;
import org.dcxz.designdigger.bean.AccessTokenInfo;
import org.dcxz.designdigger.bean.UserInfo;
import org.dcxz.designdigger.dao.DaoManager;
import org.dcxz.designdigger.framework.BaseActivity;
import org.dcxz.designdigger.util.API;
import org.json.JSONObject;

import butterknife.BindView;

/**
 * 用户通过Oauth2进行登录认证<br/>
 * 启动这个Activity的方式分别来自Activity_FirstLaunch和Fragment_Menu
 * <br/>
 * Created by DC on 2016/12/21.<br/>
 */

public class LoginActivity extends BaseActivity {
    public static final String TAG = "LoginActivity";
    /**
     * what:跳转至Activity_Main
     */
    private static final int TO_ACTIVITY_MAIN = 1;
    /**
     * 用于登录的WebView
     */
    @BindView(R.id.login_webView)
    WebView webView;

    @BindView(R.id.login_progressBar)
    ProgressBar progressBar;
    /**
     * 用于从文件中存取数据
     */
    private DaoManager manager;

    @Override
    protected int setContentViewImp() {
        return R.layout.activity_login;
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void initView() {
        manager = DaoManager.getInstance(LoginActivity.this);
        //从Fragment_Menu启动,说明是注销后再登录,因此需要清理前一个用户的登录信息
        CookieManager.getInstance().removeAllCookie();
        Log.i(TAG, "initView: Cookie cleared");

        // TODO: 2016/12/22 优化:WebView+JS实现登录 http://www.jb51.net/article/84957.htm
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
                            progressBar.setVisibility(View.VISIBLE);//重定向过程中禁止用户再次操作webView
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
                                                AccessTokenInfo token = new Gson().fromJson(response.toString(), AccessTokenInfo.class);
                                                String access_token = token.getAccess_token();
                                                //Log.i(TAG, "onResponse: token=" + access_token);
                                                manager.setAccessToken(access_token);//登录成功,更新文件中的口令
                                                App.updateHeader();//登录成功,更新请求头（内存）中的口令
                                                saveUser();
                                            }
                                        },
                                        new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                progressBar.setVisibility(View.INVISIBLE);
                                                toast(R.string.Authorize_failed_Cannot_get_Access_Token);
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
     */
    private void saveUser() {
        App.stringRequest(
                API.EndPoint.USER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        manager.setUser(response);
                        UserInfo user = new Gson().fromJson(response, UserInfo.class);
                        Log.i(TAG, "onResponse: UserName=" + user.getUsername() + " UID=" + user.getId());
                        App.getQueue().add(
                                new ImageRequest(user.getAvatar_url(),
                                        new Response.Listener<Bitmap>() {
                                            @Override
                                            public void onResponse(Bitmap response) {
                                                progressBar.setVisibility(View.INVISIBLE);
                                                if (manager.setAvatar(response)) {
                                                    Log.i(TAG, "onResponse: download avatar success");
                                                    sendBroadcast(new Intent(TAG));
                                                } else {
                                                    Log.i(TAG, "onResponse: download avatar failed:File IO Exception");
                                                    toast(R.string.Download_avatar_failed_File_IOException);
                                                }
                                            }
                                        },
                                        0,
                                        0,
                                        Bitmap.Config.ARGB_8888,
                                        new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                progressBar.setVisibility(View.INVISIBLE);
                                                toast(R.string.Download_avatar_failed_Connection_Error);
                                            }
                                        }));
                        handler.sendEmptyMessage(TO_ACTIVITY_MAIN);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressBar.setVisibility(View.INVISIBLE);
                        toast(R.string.Authorize_failed_Cannot_get_user);
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
            case TO_ACTIVITY_MAIN:
                startActivity(MainActivity.class);
                break;
        }
        finish();
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {//拦截返回键
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
