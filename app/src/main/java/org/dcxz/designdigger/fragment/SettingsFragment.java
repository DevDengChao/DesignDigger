package org.dcxz.designdigger.fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import org.dcxz.designdigger.R;
import org.dcxz.designdigger.app.App;
import org.dcxz.designdigger.dao.DaoManager;
import org.dcxz.designdigger.util.API;

/**
 * <br/>
 * Created by OvO on 2017/1/12.<br/>
 * ChangeLog :
 * <pre>
 * </pre>
 */

public class SettingsFragment extends PreferenceFragment {
    public static final String TAG = "SettingsFragment";
    private DaoManager manager;
    private boolean isUserLogined;
    private Activity activity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
        getPreferenceManager().setSharedPreferencesName("DesignDiggerConfig");//个人偏向

//        findPreference("preview_image_mobile").setOnPreferenceChangeListener(this);
//        findPreference("preview_image_wifi").setOnPreferenceChangeListener(this);
//        findPreference("detail_image_mobile").setOnPreferenceChangeListener(this);
//        findPreference("detail_image_wifi").setOnPreferenceChangeListener(this);

        activity = getActivity();
        manager = DaoManager.getInstance(activity);
        isUserLogined = !manager.getAccessToken().equals(API.Oauth2.ACCESS_TOKEN_DEFAULT);
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if (preference.getKey() != null) {
            switch (preference.getKey()) {
                case "settings_about_us":
                    new AlertDialog.Builder(activity)
                            .setMessage(// TODO: 2017/1/12 complete this
                                    "Design Digger is an Android application for Dribbble.\r\n" +
                                            "Project start at 2016/10/01\r\n" +
                                            "Producer list:XieEDeHeiShou\r\n" +
                                            "              qilvzi\r\n" +
                                            "Home page:https://github.com/XieEDeHeiShou/DesignDigger")
                            .show();
                    break;
                case "sign_out":
                    if (isUserLogined) {
                        new AlertDialog.Builder(activity)
                                .setTitle("Sign out")
                                .setMessage("You are going to sign out")
                                .setPositiveButton(
                                        "Sign out",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                isUserLogined = false;
                                                manager.setAccessToken(API.Oauth2.ACCESS_TOKEN_DEFAULT);//用默认access_token替换用户access_token
                                                manager.setUser(null);
                                                App.updateHeader();//更新内存和请求头部的access_token
                                                activity.sendBroadcast(new Intent(TAG));//用户注销广播
                                            }
                                        })
                                .setNegativeButton("Cancel", null)
                                .show();
                    } else {
                        Toast.makeText(activity, R.string.you_are_not_login_yet, Toast.LENGTH_SHORT).show();
                    }
                    break;
                case "preview_image_mobile":
            }
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }
}
