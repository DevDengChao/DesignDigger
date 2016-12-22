package org.dcxz.designdigger.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import org.dcxz.designdigger.entity.Entity_User;

/**
 * SharedPreference管理工具
 * <br/>
 * Created by OvO on 2016/12/13.<br/>
 * ChangeLog :
 * <pre>
 * </pre>
 */

public class Util_SP_Manager {
    /**
     * 单例模式
     */
    private static Util_SP_Manager instance;

    private static SharedPreferences preferences;
    /**
     * 是否第一次启动应用
     */
    private static final String IS_FIRST_LAUNCH = "IS_FIRST_LAUNCH";
    /**
     * 动态口令
     */
    private static final String ACCESS_TOKEN = "ACCESS_TOKEN";
    /**
     * 用户对象(Json)
     */
    private static final String USER = "USER";

    private Util_SP_Manager(Context context) {
        preferences = context.getSharedPreferences("DesignDiggerConfig", Context.MODE_PRIVATE);
    }

    public static Util_SP_Manager getInstance(Context context) {
        if (instance == null) {
            synchronized (Util_SP_Manager.class) {
                if (instance == null) {
                    instance = new Util_SP_Manager(context);
                }
            }
        }
        return instance;
    }

    /**
     * 是否第一次启动应用
     *
     * @return 是否第一次启动应用
     */
    public boolean isFirstLaunch() {
        return preferences.getBoolean(IS_FIRST_LAUNCH, true);
    }

    /**
     * 修改第一次启动应用的判定值
     *
     * @param firstLaunch 判定值
     * @return 是否修改成功
     */
    public boolean setFirstLaunch(boolean firstLaunch) {
        return preferences.edit().putBoolean(IS_FIRST_LAUNCH, firstLaunch).commit();
    }

    /**
     * 获取动态口令
     *
     * @return 动态口令
     */
    public String getAccessToken() {
        return preferences.getString(ACCESS_TOKEN, API.Oauth2.ACCESS_TOKEN_DEFAULT);
    }

    /**
     * 存入动态口令
     *
     * @param accessToken 将要存入的值
     * @return 是否存入成功
     */
    public boolean setAccessToken(String accessToken) {
        return preferences.edit().putString(ACCESS_TOKEN, accessToken).commit();
    }

    /**
     * 存入用户对象
     *
     * @param user 将要存入的用户对象
     * @return 是否存入成功
     */
    public boolean setUser(String user) {
        return preferences.edit().putString(USER, user).commit();
    }

    /**
     * 获取用户对象
     *
     * @return 当前用户
     */
    public Entity_User getUser() {
        String json = preferences.getString(USER, null);
        if (json != null) {
            return new Gson().fromJson(json, Entity_User.class);
        }
        return null;
    }



    /**
     * 获取布尔值
     *
     * @param key 关键字
     * @return 关键字在配置文件中对应的布尔值;若获取失败则返回FALSE;
     */
    public boolean getBoolean(String key) {
        return preferences.getBoolean(key, false);
    }

    /**
     * 存入布尔值
     *
     * @param key  关键字
     * @param flag 将要存入的值
     * @return 是否存入成功
     */
    public boolean putBoolean(String key, boolean flag) {
        return preferences.edit().putBoolean(key, flag).commit();
    }
}
