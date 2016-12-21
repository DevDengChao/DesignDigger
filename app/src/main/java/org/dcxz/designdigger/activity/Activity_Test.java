package org.dcxz.designdigger.activity;

import android.os.Message;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;

import org.dcxz.designdigger.App;
import org.dcxz.designdigger.R;
import org.dcxz.designdigger.entity.Entity_AccessToken;
import org.dcxz.designdigger.framework.Framework_Activity;
import org.dcxz.designdigger.util.API;

/**
 * 用户进行登录认证的测试模块
 * <br/>
 * Created by DC on 2016/12/21.<br/>
 */

public class Activity_Test extends Framework_Activity {
    public static final String TAG = "Activity_Test";
    private WebView webView;

    @Override
    protected int setContentViewImp() {
        return R.layout.activity_test;
    }

    @Override
    protected void initView() {
        webView = (WebView) findViewById(R.id.webView);
        webView.setWebViewClient(new WebViewClient() {
            // https://dribbble.com/login?return_to=%2Foauth%2Fauthorize%3Fclient_id%3De8e27bfbaa6d35bfe58255c68957c70aecfb444b20fc8197bf1c5f9acc1181ce%26redirect_uri%3Dhttps%3A%2F%2Fgithub.com%2FXieEDeHeiShou%2FDesignDigger
            // https://dribbble.com/oauth/authorize?client_id=e8e27bfbaa6d35bfe58255c68957c70aecfb444b20fc8197bf1c5f9acc1181ce&redirect_uri=https://github.com/XieEDeHeiShou/DesignDigger
            // https://github.com/XieEDeHeiShou/DesignDigger?code=f209a6fe11188064a0d4d0976a42947236c231b885db772880e33fe8bf217f18
            @SuppressWarnings("deprecation")
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.i(TAG, "OverrideUrlLoading: " + url);
                String callback_url = API.Oauth2.REDIRECT_URI_VALUE;
                if (url.startsWith(callback_url)) {//已经过用户认证,WebView正准备重定向
                    Log.i(TAG, "Trying to redirect to: " + url);
                    String code = url.replace(callback_url + "?code=", "");//获取Dribbble返回的验证码
                    Log.i(TAG, "Code: " + code);
                    String tokenURL = String.format(API.Oauth2.TOKEN, code);//尝试获取access_token
                    getToken(tokenURL);
                    return false;
                } else {//
                    view.loadUrl(url);
                    return true;
                }
            }


            private void getToken(String tokenURL) {// TODO: 2016/12/22 POST!!!
                App.stringRequest(
                        tokenURL,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Entity_AccessToken token = new Gson().fromJson(response, Entity_AccessToken.class);
                                Log.i(TAG, "onResponse: " + token.getAccess_token());
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                toast("Authorize failed");
                                error.printStackTrace();
                            }
                        });
            }
        });
    }

    @Override
    protected void initData() {
        webView.loadUrl(API.Oauth2.AUTHORIZE);
    }

    @Override
    protected void initAdapter() {

    }

    @Override
    protected void initListener() {

    }

    @Override
    public void handleMessageImp(Message msg) {

    }
}
