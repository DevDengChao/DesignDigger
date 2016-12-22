package org.dcxz.designdigger.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.dcxz.designdigger.R;
import org.dcxz.designdigger.activity.Activity_Login;
import org.dcxz.designdigger.util.API;
import org.dcxz.designdigger.dao.Dao_Manager;
import org.dcxz.designdigger.view.flowing_drawer.MenuFragment;

/**
 * <br/>
 * Created by OvO on 2016/12/16.<br/>
 * ChangeLog :
 * <pre>
 * </pre>
 */

public class Fragment_Menu extends MenuFragment {
    private Dao_Manager manager;
    private ImageView avatar;
    private TextView signUp, signIn, signOut;
    private TextView settings;
    private AlertDialog dialog;


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
        signOut = (TextView) view.findViewById(R.id.menu_signOut);
        settings = (TextView) view.findViewById(R.id.menu_settings);
    }

    private void initData(final Activity activity) {
        manager = Dao_Manager.getInstance(activity);
        String accessToken = manager.getAccessToken();
        setSignOutVisible(activity, !accessToken.equals(API.Oauth2.ACCESS_TOKEN_DEFAULT));

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Sign out");
        builder.setMessage("You are going to sign out");
        builder.setPositiveButton("Sign out", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                manager.setAccessToken(API.Oauth2.ACCESS_TOKEN_DEFAULT);
                setSignOutVisible(activity, false);
            }
        });
        builder.setNegativeButton("Cancel", null);
        dialog = builder.create();
    }

    /**
     * Sign in/Sign up 是否不可见,以及Sign out是否可见
     *
     * @param activity   用于初始化Dao_Manager
     * @param visibility true:Sign out 可见,其他不可见
     */
    private void setSignOutVisible(Activity activity, boolean visibility) {
        if (visibility) {
            signUp.setVisibility(View.INVISIBLE);
            signIn.setVisibility(View.INVISIBLE);
            signOut.setVisibility(View.VISIBLE);
            avatar.setImageBitmap(Dao_Manager.getInstance(activity).getAvatar());
        } else {
            signUp.setVisibility(View.VISIBLE);
            signIn.setVisibility(View.VISIBLE);
            signOut.setVisibility(View.INVISIBLE);
            avatar.setImageResource(R.mipmap.dribbble_ball_mark);
            API.Oauth2.setAccessToken(API.Oauth2.ACCESS_TOKEN_DEFAULT);// TODO: 2016/12/23 注销再登录逻辑
        }
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
                startActivity(new Intent(activity, Activity_Login.class));
            }
        });
        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
            }
        });
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(activity, "Settings", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 监听Activity_Login发送的登陆成功事件
     */
    private class LoginReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
// TODO: 2016/12/22 登录成功监听
        }
    }
}
