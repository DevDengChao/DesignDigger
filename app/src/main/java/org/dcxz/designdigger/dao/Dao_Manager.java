package org.dcxz.designdigger.dao;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.gson.Gson;

import org.dcxz.designdigger.entity.Entity_User;
import org.dcxz.designdigger.util.API;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 * 基于SharedPreference与FileIO实现的数据管理器
 * <br/>
 * Created by OvO on 2016/12/13.<br/>
 * ChangeLog :
 * <pre>
 * </pre>
 */
public class Dao_Manager {
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
    /**
     * 用户头像文件名
     */
    private static final String AVATAR = "AVATAR.PNG";
    /**
     * 单例模式
     */
    private static Dao_Manager instance;
    private static SharedPreferences preferences;
    /**
     * 当前应用的文件目录
     */
    private static File fileDir;

    private Dao_Manager(Context context) {
        preferences = context.getSharedPreferences("DesignDiggerConfig", Context.MODE_PRIVATE);
        fileDir = context.getFilesDir();
    }

    public static Dao_Manager getInstance(Context context) {
        if (instance == null) {
            synchronized (Dao_Manager.class) {
                if (instance == null) {
                    instance = new Dao_Manager(context);
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
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public boolean setUser(String user) {
        File file = new File(fileDir, AVATAR);
        if (file.exists()) {//移除原有的头像文件,以免获取头像失败后显示原有头像
            file.delete();
        }
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
     * 将用户头像存入文件
     *
     * @param avatar 带储存的头像
     * @return 是否成功存入文件
     */
    public boolean setAvatar(Bitmap avatar) {
        try {
            return avatar.compress(Bitmap.CompressFormat.PNG, 100, new FileOutputStream(new File(fileDir, AVATAR)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 获取头像文件
     *
     * @return 头像
     */
    public Bitmap getAvatar() {
        File file = new File(fileDir, AVATAR);
        if (file.exists()) {
            return BitmapFactory.decodeFile(file.getAbsolutePath());
        } else {
            return null;
        }
    }

    /**
     * 获取布尔值
     *
     * @param key          关键字
     * @param defaultValue 默认值
     * @return 关键字在配置文件中对应的布尔值;
     */
    public boolean getBoolean(String key, boolean defaultValue) {
        return preferences.getBoolean(key, defaultValue);
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
