package org.dcxz.designdigger.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.dcxz.designdigger.R;
import org.dcxz.designdigger.view.flowing_drawer.MenuFragment;

/**
 * <br/>
 * Created by OvO on 2016/12/16.<br/>
 * ChangeLog :
 * <pre>
 * </pre>
 */

public class Fragment_Menu extends MenuFragment {
    private ImageView avatar;
    private TextView signUp, signIn;
    private TextView settings;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: 2016/12/21 菜单颜色
        View view = inflater.inflate(R.layout.fragment_menu, container, false);
        Activity activity = getActivity();
        initView(activity, view);
        initData(activity);
        initAdapter(activity);
        initListener(activity);
        return setupReveal(view);
    }

    private void initView(Activity activity, View view) {
        avatar = (ImageView) view.findViewById(R.id.menu_avatar);
        signUp = (TextView) view.findViewById(R.id.menu_signUp);
        signIn = (TextView) view.findViewById(R.id.menu_signIn);
        settings = (TextView) view.findViewById(R.id.menu_settings);
    }

    private void initData(Activity activity) {
    }

    private void initAdapter(Activity activity) {
    }

    private void initListener(final Activity activity) {
        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(activity, "Avatar", Toast.LENGTH_SHORT).show();
            }
        });
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(activity, "Sign Up", Toast.LENGTH_SHORT).show();
            }
        });
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(activity, "Sign In", Toast.LENGTH_SHORT).show();
            }
        });
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(activity, "Settings", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
