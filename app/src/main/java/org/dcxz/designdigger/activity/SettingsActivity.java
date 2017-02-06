package org.dcxz.designdigger.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import org.dcxz.designdigger.R;
import org.dcxz.designdigger.app.App;
import org.dcxz.designdigger.dao.DaoManager;
import org.dcxz.designdigger.framework.BaseActivity;
import org.dcxz.designdigger.util.API;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * <br/>
 * Created by OvO on 2017/1/12.<br/>
 * ChangeLog :
 * <pre>
 * </pre>
 */

public class SettingsActivity extends BaseActivity {
    public static final String TAG = "SettingsActivity";

    @BindView(R.id.activity_settings_toolBar)
    Toolbar toolbar;
    @BindView(R.id.activity_settings_sign_out)
    Button signOut;
    private DaoManager manager;

    @Override
    protected int setContentViewImp() {
        return R.layout.activity_settings;
    }

    @Override
    protected void initView() {
        toolbar.setNavigationIcon(R.drawable.arrow_left);
        setSupportActionBar(toolbar);
        manager = DaoManager.getInstance(this);
        if (manager.getAccessToken().equals(API.Oauth2.ACCESS_TOKEN_DEFAULT)) {
            disableSignOut(signOut);
        }
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initAdapter() {

    }

    @Override
    protected void initListener() {
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(MainActivity.class);
                finish();
            }
        });
    }

    /**
     * 设置移动网络下预览图的精度
     */
    @OnClick(R.id.activity_settings_preview_mobile)
    public void setPreviewImageQualityMobile() {
        setImageQuality(DaoManager.PREVIEW_IMAGE_QUALITY_MOBILE, manager.getPreviewImageQualityMobile());
    }

    /**
     * 设置WIFI网络下预览图的精度
     */
    @OnClick(R.id.activity_settings_preview_wifi)
    public void setPreviewImageQualityWifi() {
        setImageQuality(DaoManager.PREVIEW_IMAGE_QUALITY_WIFI, manager.getPreviewImageQualityWifi());
    }

    /**
     * 设置移动网络下详情图的精度
     */
    @OnClick(R.id.activity_settings_detail_mobile)
    public void setDetailImageQualityMobile() {
        setImageQuality(DaoManager.DETAIL_IMAGE_QUALITY_MOBILE, manager.getDetailImageQualityMobile());
    }

    /**
     * 设置WIFI网络下详情图的精度
     */
    @OnClick(R.id.activity_settings_detail_wifi)
    public void setDetailImageQualityWifi() {
        setImageQuality(DaoManager.DETAIL_IMAGE_QUALITY_WIFI, manager.getDetailImageQualityWifi());
    }

    private void setImageQuality(final String key, String oldPreference) {
        int checkedItem = 1;
        switch (oldPreference) {
            case DaoManager.IMAGE_QUALITY_LIGHT:
                checkedItem = 0;
                break;
            case DaoManager.IMAGE_QUALITY_NORMAL:
                checkedItem = 1;
                break;
            case DaoManager.IMAGE_QUALITY_LARGE:
                checkedItem = 2;
                break;
        }
        new AlertDialog.Builder(this)
                .setSingleChoiceItems(R.array.settings_imageQuality, checkedItem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                manager.putString(key, DaoManager.IMAGE_QUALITY_LIGHT);
                                break;
                            case 1:
                                manager.putString(key, DaoManager.IMAGE_QUALITY_NORMAL);
                                break;
                            case 2:
                                manager.putString(key, DaoManager.IMAGE_QUALITY_LARGE);
                                break;
                        }
                        dialog.dismiss();
                    }
                }).show();
    }

    @OnClick(R.id.activity_settings_sign_out)
    public void signOut(final Button button) {
        new AlertDialog.Builder(this)
                .setTitle("Sign out")
                .setMessage("You are going to sign out")
                .setPositiveButton(
                        "Sign out",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                manager.setAccessToken(API.Oauth2.ACCESS_TOKEN_DEFAULT);//用默认access_token替换用户access_token
                                manager.setUser(null);
                                disableSignOut(button);
                                App.updateHeader();//更新内存和请求头部的access_token
                                sendBroadcast(new Intent(TAG));//用户注销广播
                            }
                        })
                .setNegativeButton("Cancel", null)
                .show();
    }

    /**
     * 禁用SignOut按钮
     */
    private void disableSignOut(Button button) {
        button.setEnabled(false);
        //noinspection deprecation
        button.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
    }

    @Override
    public void handleMessageImp(Message msg) {

    }
}
