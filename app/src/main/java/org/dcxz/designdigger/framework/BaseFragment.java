package org.dcxz.designdigger.framework;

import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.Unbinder;


/**
 * <br/>
 * Created by DC on 2016/12/12.<br/>
 */

public abstract class BaseFragment extends Fragment {
    /**
     * Framework_Fragment持有的Framework_Handler对象,为子类提供消息机制接口
     */
    protected BaseHandler handler;
    private Unbinder unbinder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(setContentViewImp(), container, false);
        unbinder = ButterKnife.bind(this, view);
        handler = new BaseHandler(this);
        BaseActivity activity = (BaseActivity) getActivity();
        initView(activity, view);
        initData(activity, savedInstanceState);
        initAdapter(activity);
        initListener(activity);
        return view;
    }

    /**
     * {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}接口
     *
     * @return 用于充填布局的资源id
     */
    protected abstract int setContentViewImp();

    /**
     * 捕捉布局中已有的控件
     *
     * @param activity 当前活动
     * @param view     将要显示的控件
     */
    protected abstract void initView(BaseActivity activity, View view);

    /**
     * 收集数据
     *
     * @param activity           当前活动
     * @param savedInstanceState 上一个实例保存的状态
     */
    protected abstract void initData(BaseActivity activity, Bundle savedInstanceState);

    /**
     * 初始化适配器
     *
     * @param activity 当前活动
     */
    protected abstract void initAdapter(BaseActivity activity);

    /**
     * 为控件添加时间监听
     *
     * @param activity 当前活动
     */
    protected abstract void initListener(BaseActivity activity);

    /**
     * 消息机制接口,处理由{@link #handler}发送的消息
     *
     * @param msg 待处理的消息
     */
    public abstract void handleMessageImp(Message msg);

    /**
     * Toast接口
     *
     * @param resID 待弹出的消息
     */
    protected void toast(int resID) {
        if (getActivity() != null) {//activity依然活跃
            Toast.makeText(getActivity(), resID, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
