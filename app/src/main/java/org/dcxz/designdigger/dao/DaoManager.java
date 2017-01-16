package org.dcxz.designdigger.dao;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.gson.Gson;

import org.dcxz.designdigger.R;
import org.dcxz.designdigger.bean.UserInfo;
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
public class DaoManager {
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
    private static DaoManager instance;
    private static SharedPreferences preferences;
    /**
     * 当前应用的文件目录
     */
    private static File fileDir;
    private final Resources resources;

    private DaoManager(Context context) {
        preferences = context.getSharedPreferences("DesignDiggerConfig", Context.MODE_PRIVATE);
        fileDir = context.getFilesDir();
        resources = context.getResources();
    }

    public static DaoManager getInstance(Context context) {
        if (instance == null) {
            synchronized (DaoManager.class) {
                if (instance == null) {
                    instance = new DaoManager(context);
                }
            }
        }
        return instance;
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
    public UserInfo getUser() {
        String json = preferences.getString(USER, null);
        if (json != null) {
            return new Gson().fromJson(json, UserInfo.class);
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
     * 返回偏好设置中"移动网络下的预览图精度"
     */
    public String getPreviewImageQualityMobile() {
        return preferences.getString(
                resources.getString(R.string.settings_image_quality_preview_mobile),
                resources.getString(R.string.settings_image_quality_normal));
    }

    public void setPreviewImageQualityMobile(String value) {
        preferences.edit().putString(resources.getString(R.string.settings_image_quality_preview_mobile), value).apply();
    }

    /**
     * 返回偏好设置中"Wifi网络下的预览图精度"
     */
    public String getPreviewImageQualityWifi() {
        return preferences.getString(
                resources.getString(R.string.settings_image_quality_preview_wifi),
                resources.getString(R.string.settings_image_quality_normal));
    }

    public void setPreviewImageQualityWifi(String value) {
        preferences.edit().putString(resources.getString(R.string.settings_image_quality_preview_wifi), value).apply();
    }

    /**
     * 返回偏好设置中"移动网络下的详情精度"
     */
    public String getDetailImageQualityMobile() {
        return preferences.getString(
                resources.getString(R.string.settings_image_quality_detail_mobile),
                resources.getString(R.string.settings_image_quality_normal));
    }

    public void setDetailImageQualityMobile(String value) {
        preferences.edit().putString(resources.getString(R.string.settings_image_quality_detail_mobile), value).apply();
    }

    /**
     * 返回偏好设置中"Wifi网络下的详情精度"
     */
    public String getDetailImageQualityWifi() {
        return preferences.getString(
                resources.getString(R.string.settings_image_quality_detail_wifi),
                resources.getString(R.string.settings_image_quality_normal));
    }

    public void setDetailImageQualityWifi(String value) {
        preferences.edit().putString(resources.getString(R.string.settings_image_quality_detail_wifi), value).apply();
    }
}
