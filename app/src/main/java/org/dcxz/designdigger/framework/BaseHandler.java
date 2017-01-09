package org.dcxz.designdigger.framework;

import android.os.Handler;
import android.os.Message;

/**
 * Handler包装类.<br/>
 * 用于处理Framework_Activity和Framework_Fragment的消息.<br/>
 * Created by DC on 2016/12/12.<br/>
 */

@SuppressWarnings("WeakerAccess")
public class BaseHandler extends Handler {
    private BaseActivity activity;
    private BaseFragment fragment;

    public BaseHandler(BaseActivity activity) {
        this.activity = activity;
    }

    public BaseHandler(BaseFragment fragment) {
        this.fragment = fragment;
    }

    /**
     * 根据实例持有的成员而调用不同的事件处理方法
     *
     * @param msg 待处理的消息
     */
    @Override
    public void handleMessage(Message msg) {
        if (null != activity) {
            activity.handleMessageImp(msg);
        } else if (fragment != null) {
            fragment.handleMessageImp(msg);
        }
    }
}
