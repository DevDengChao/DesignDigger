package org.dcxz.designdigger.fragment;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.support.v7.app.AlertDialog;

import org.dcxz.designdigger.R;

/**
 * <br/>
 * Created by OvO on 2017/1/12.<br/>
 * ChangeLog :
 * <pre>
 * </pre>
 */

public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
        getPreferenceManager().setSharedPreferencesName("DesignDiggerConfig");
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        switch (preference.getKey()) {//Design Digger is an Android application for practice
            case "settings_about_us":
                new AlertDialog.Builder(getActivity())
                        .setMessage(// TODO: 2017/1/12 complete this
                                "Design Digger is an Android application for Dribbble.\r\n" +
                                        "Project start at 2016/10/01\r\n" +
                                        "Producer list:XieEDeHeiShou\r\n" +
                                        "              qilvzi\r\n" +
                                        "Home page:https://github.com/XieEDeHeiShou/DesignDigger")
                        .show();
                break;
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }
}
