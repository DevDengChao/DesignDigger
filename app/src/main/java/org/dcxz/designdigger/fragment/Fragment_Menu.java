package org.dcxz.designdigger.fragment;

import android.app.Activity;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import org.dcxz.designdigger.R;
import org.dcxz.designdigger.framework.Framework_Fragment;

/**
 * <br/>
 * Created by OvO on 2016/12/16.<br/>
 * ChangeLog :
 * <pre>
 * </pre>
 */

public class Fragment_Menu extends Framework_Fragment {
    private ImageView avatar;

    @Override
    protected int setContentViewImp() {
        return R.layout.fragment_menu;
    }

    @Override
    protected void initView(Activity activity) {
        avatar = (ImageView) activity.findViewById(R.id.menu_avatar);
    }

    @Override
    protected void initData(Activity activity) {

    }

    @Override
    protected void initAdapter(Activity activity) {

    }

    @Override
    protected void initListener(final Activity activity) {
        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(activity, "avatar", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void handleMessageImp(Message msg) {

    }
}
