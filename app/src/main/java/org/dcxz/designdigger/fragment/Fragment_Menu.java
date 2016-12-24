package org.dcxz.designdigger.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.dcxz.designdigger.App;
import org.dcxz.designdigger.R;
import org.dcxz.designdigger.activity.Activity_Login;
import org.dcxz.designdigger.dao.Dao_Manager;
import org.dcxz.designdigger.framework.Framework_Fragment;
import org.dcxz.designdigger.util.API;

/**
 * <br/>
 * Created by OvO on 2016/12/16.<br/>
 * ChangeLog :
 * <pre>
 * </pre>
 */

public class Fragment_Menu extends Framework_Fragment {
    public static final String TAG = "Fragment_Menu";
    private Dao_Manager manager;
    private ImageView avatar;
    private TextView signUp, signIn, signOut;
    private TextView settings;
    private AlertDialog dialog;
    private AvatarReceiver receiver;

    @Override
    protected int setContentViewImp() {
        return R.layout.fragment_menu;
    }

    protected void initView(Activity activity, View view) {
        avatar = (ImageView) view.findViewById(R.id.menu_avatar);
        signUp = (TextView) view.findViewById(R.id.menu_signUp);
        signIn = (TextView) view.findViewById(R.id.menu_signIn);
        signOut = (TextView) view.findViewById(R.id.menu_signOut);
        settings = (TextView) view.findViewById(R.id.menu_settings);
        receiver = new AvatarReceiver();
    }

    protected void initData(final Activity activity) {
        manager = Dao_Manager.getInstance(activity);
        String accessToken = manager.getAccessToken();
        activity.registerReceiver(receiver, new IntentFilter("AvatarUpdated"));
        setSignOutVisible(!accessToken.equals(API.Oauth2.ACCESS_TOKEN_DEFAULT));

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Sign out");
        builder.setMessage("You are going to sign out");
        builder.setPositiveButton("Sign out", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                manager.setAccessToken(API.Oauth2.ACCESS_TOKEN_DEFAULT);
                setSignOutVisible(false);
            }
        });
        builder.setNegativeButton("Cancel", null);
        dialog = builder.create();
    }

    /**
     * Sign in/Sign up 是否不可见,以及Sign out是否可见
     *
     * @param visibility true:Sign out 可见,其他不可见
     */
    private void setSignOutVisible(boolean visibility) {
        if (visibility) {
            signUp.setVisibility(View.INVISIBLE);
            signIn.setVisibility(View.INVISIBLE);
            signOut.setVisibility(View.VISIBLE);
            avatar.setImageBitmap(manager.getAvatar());
        } else {
            signUp.setVisibility(View.VISIBLE);
            signIn.setVisibility(View.VISIBLE);
            signOut.setVisibility(View.INVISIBLE);
            avatar.setImageResource(R.mipmap.dribbble_ball_mark);
            manager.setAccessToken(API.Oauth2.ACCESS_TOKEN_DEFAULT);//用默认access_token替换用户access_token
            App.updateHeader();//更新内存和请求头部的access_token
        }
    }

    protected void initAdapter(Activity activity) {
    }

    protected void initListener(final Activity activity) {
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
                Intent intent = new Intent(activity, Activity_Login.class);
                intent.putExtra("STATE", Fragment_Menu.TAG);
                startActivity(intent);
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

    @Override
    public void handleMessageImp(Message msg) {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getActivity().unregisterReceiver(receiver);
    }

    private class AvatarReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            setSignOutVisible(true);
        }
    }
}
