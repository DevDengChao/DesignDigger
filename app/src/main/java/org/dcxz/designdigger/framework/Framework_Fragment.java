package org.dcxz.designdigger.framework;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

/**
 * <br/>
 * Created by DC on 2016/12/12.<br/>
 */

public abstract class Framework_Fragment extends Fragment {
    /**
     * Framework_Fragment持有的Activity对象
     */
    protected Activity activity;
    /**
     * Framework_Fragment持有的Framework_Handler对象,为子类提供消息机制接口
     */
    protected Framework_Handler handler;
    /**
     * Framework_Fragment持有的Toast对象,为子类提供消息弹出接口
     */
    private Toast toast;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(setContentViewImp(), container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = getActivity();
        handler = new Framework_Handler(this);
        toast = new Toast(context);
        toast.setDuration(Toast.LENGTH_SHORT);

        initView(context);
        initData(context);
        initAdapter(context);
        initListener(context);
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
     * @param context 当前上下文
     */
    protected abstract void initView(Context context);

    /**
     * 收集数据
     *
     * @param context 当前上下文
     */
    protected abstract void initData(Context context);

    /**
     * 初始化适配器
     *
     * @param context 当前上下文
     */
    protected abstract void initAdapter(Context context);

    /**
     * 为控件添加时间监听
     *
     * @param context 当前上下文
     */
    protected abstract void initListener(Context context);

    /**
     * 消息机制接口,处理由{@link #handler}发送的消息
     *
     * @param msg 待处理的消息
     */
    public abstract void handleMessageImp(Message msg);

    /**
     * Toast接口
     *
     * @param msg 待弹出的消息
     */
    protected void toast(String msg) {
        toast.setText(msg);
        toast.show();
    }
}
