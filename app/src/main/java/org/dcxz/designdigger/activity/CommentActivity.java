package org.dcxz.designdigger.activity;

import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import org.dcxz.designdigger.R;
import org.dcxz.designdigger.bean.ShotInfo;
import org.dcxz.designdigger.framework.BaseActivity;

import butterknife.BindView;

/**
 * <br/>
 * Created by OvO on 2017/1/15.<br/>
 * ChangeLog :
 * <pre>
 * </pre>
 */

public class CommentActivity extends BaseActivity {
    public static final String TAG = "CommentActivity";
    @BindView(R.id.activity_comment_toolbar)
    Toolbar toolbar;

    @Override
    protected int setContentViewImp() {
        return R.layout.activity_comment;
    }

    @Override
    protected void initView() {
        toolbar.setNavigationIcon(R.drawable.toolbar_back);
        setSupportActionBar(toolbar);
    }

    @Override
    protected void initData() {
        ShotInfo shotInfo = (ShotInfo) getIntent().getSerializableExtra(SERIALIZABLE);
        Log.i(TAG, "initData: " + shotInfo);
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
                        finish();
                    }
                });
    }

    @Override
    public void handleMessageImp(Message msg) {

    }
}
