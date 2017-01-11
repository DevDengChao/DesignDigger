package org.dcxz.designdigger.activity;

import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.view.View;

import org.dcxz.designdigger.R;
import org.dcxz.designdigger.bean.UserInfo;
import org.dcxz.designdigger.fragment.MainFragment;
import org.dcxz.designdigger.framework.BaseActivity;

import java.io.Serializable;

import butterknife.BindView;

/**
 * 需要以{@link org.dcxz.designdigger.bean.UserInfo}作为参数,
 * 并使用{@link #startActivity(Class, Serializable)}启动这个Activity
 * <br/>
 * Created by OvO on 2017/1/10.<br/>
 * ChangeLog :
 * <pre>
 * </pre>
 */

public class ProfileActivity extends BaseActivity {

    @BindView(R.id.activity_profile_toolBar)
    Toolbar toolbar;

    @Override
    protected int setContentViewImp() {
        overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_right);
        return R.layout.activity_profile;
    }

    @Override
    protected void initView() {
        toolbar.setNavigationIcon(R.drawable.toolbar_back);
        setSupportActionBar(toolbar);
    }

    @Override
    protected void initData() {
        UserInfo user = (UserInfo) getIntent().getSerializableExtra(SERIALIZABLE);
        MainFragment fragment = MainFragment.newInstance(false, true, user);
        getSupportFragmentManager().beginTransaction().replace(R.id.activity_profile_container,fragment).commit();
    }

    @Override
    protected void initAdapter() {

    }

    @Override
    protected void initListener() {
        toolbar.setNavigationOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(MainActivity.class);
                        finish();
                    }
                });
    }

    @Override
    public void handleMessageImp(Message msg) {

    }
}
