package org.dcxz.designdigger.fragment;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.dcxz.designdigger.R;
import org.dcxz.designdigger.activity.LoginActivity;
import org.dcxz.designdigger.adapter.ShotsAdapter;
import org.dcxz.designdigger.app.App;
import org.dcxz.designdigger.bean.ShotInfo;
import org.dcxz.designdigger.bean.UserInfo;
import org.dcxz.designdigger.framework.BaseActivity;
import org.dcxz.designdigger.framework.BaseFragment;
import org.dcxz.designdigger.util.API;

import java.lang.reflect.Type;
import java.util.ArrayList;

import butterknife.BindView;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;


/**
 * <br/>
 * Created by DC on 2016/12/20.<br/>
 */

public class MainFragment extends BaseFragment {
    /**
     * 日志标签,请求标签
     */
    public static final String TAG = "MainFragment";
    /**
     * 销毁/再实例化时存取{@link #shots}时需要的参数
     */
    public static final String SHOTS = "SHOTS";
    /**
     * 是否允许内容筛选
     */
    private static final String ENABLE_FILTER = "ENABLE_FILTER";
    /**
     * 是否允许recyclerView加载头布局
     */
    private static final String ENABLE_HEAD_VIEW = "ENABLE_HEAD_VIEW";
    /**
     * 将要展示的用户信息
     */
    private static final String USER_INFO = "USER_INFO";
    /**
     * 用于提示用户网络异常
     */
    @BindView(R.id.fragment_main_connectionError)
    TextView connectionError;
    /**
     * 用于遮罩的进度条
     */
    @BindView(R.id.fragment_main_progressBar)
    ProgressBar progressBar;
    /**
     * 展示内容用的RecyclerView
     */
    @BindView(R.id.fragment_main_recyclerView)
    RecyclerView recyclerView;
    /**
     * 下拉刷新需要的控件
     */
    @BindView(R.id.fragment_main_ptrFrameLayout)
    PtrFrameLayout ptrFrameLayout;
    /**
     * 内容筛选<br/>
     * 控制流行程度,控制分类,控制时间范围
     */
    @BindView(R.id.fragment_main_sort)
    Spinner sortFilter;
    @BindView(R.id.fragment_main_list)
    Spinner listFilter;
    @BindView(R.id.fragment_main_timeFrame)
    Spinner timeFrameFilter;
    /**
     * 日志副标签
     */
    private String subTag;
    /**
     * recyclerView的适配器
     */
    private ShotsAdapter adapter;
    /**
     * recyclerView的布局管理器
     */
    private GridLayoutManager gridLayoutManager;
    /**
     * recyclerView将要展示的数据集合
     */
    private ArrayList<ShotInfo> shots;
    /**
     * 内容筛选<br/>
     * 界面上显示的文本,来自资源文件<br/>
     * 由于显示在页面上的文本与实际需要填入url中的文本不一致,因此需要进行键值对映射
     */
    private String[] sortKeys, listKeys, timeFrameKeys;
    /**
     * 内容筛选<br/>
     * 请求实际发送的文本,来自API.EndPoint.Parameter<br/>
     */
    private String[] sortValues, listValues, timeFrameValues;
    /**
     * 内容筛选<br/>
     * 被选中的条件
     */
    private String sortSelected, listSelected, timeFrameSelected;
    /**
     * 分页查询时所需要的页码标记
     */
    private int page;
    /**
     * 滑动过程中的状态锁,控制recyclerView滑动到指定位置时只发送一次数据请求
     */
    private boolean refreshable;
    /**
     * ArrayList<ShotInfo>的类型
     */
    private Type type;
    private Gson gson;
    /**
     * 是否允许内容筛选
     */
    private boolean enableFilter;
    /**
     * 是否允许recyclerView加载头布局
     */
    private boolean enableHeadView;
    /**
     * 将要展示的用户信息
     */
    private UserInfo userInfo;
    /**
     * 是否需要强制刷新以修正数据集合
     */
    private boolean needForceRefresh;
    /**
     * 检查用户登录状态的广播接收器
     */
    private BroadcastReceiver receiver;

    /**
     * 请使用newInstance来构建带有参数的对象
     */
    @Deprecated()
    public MainFragment() {
        gson = new Gson();
        type = new TypeToken<ArrayList<ShotInfo>>() {
        }.getType();
        refreshable = true;
        needForceRefresh = true;
        page = 1;
    }

    /**
     * 根据不同的参数获取一个新的实例对象
     *
     * @param enableFilter   是否允许内容筛选:<br/>
     *                       True:允许内容筛选,并访问{@link API.EndPoint#SHOTS_PAGE_SORT_LIST_TIMEFRAME}的内容;<br/>
     *                       False:禁止内容筛选,Spinner将不可见(GONE);
     * @param enableHeadView 是否允许recyclerView加载头布局:<br/>
     *                       True:加载头布局,并访问{@link API.EndPoint#USERS_SHOTS_PAGE}的内容;<br/>
     *                       False:不加载头布局,并访问{@link API.EndPoint#FOLLOWING_SHOTS_PAGE}的内容;
     * @param user           将要在头布局中展示的用户对象:<br/>
     *                       当enableHeadView=true时不能为null;<br/>
     *                       当enableHeadView=false时被忽略;
     * @return 新的实例对象;
     */
    public static MainFragment newInstance(boolean enableFilter, boolean enableHeadView, UserInfo user) {
        //noinspection deprecation
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        args.putBoolean(ENABLE_FILTER, enableFilter);
        args.putBoolean(ENABLE_HEAD_VIEW, enableHeadView);
        args.putSerializable(USER_INFO, user);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int setContentViewImp() {
        return R.layout.fragment_main;
    }

    @SuppressLint("InflateParams")
    @Override
    protected void initView(BaseActivity activity, View view) {
        ptrFrameLayout.setPullToRefresh(true);
        ptrFrameLayout.setHeaderView(activity.getLayoutInflater().inflate(R.layout.refresh_header, null, false));
        gridLayoutManager = new GridLayoutManager(activity, 1);
        recyclerView.setLayoutManager(gridLayoutManager);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void initData(BaseActivity activity, Bundle savedInstanceState) {
        Bundle args = getArguments();
        enableFilter = args.getBoolean(ENABLE_FILTER);
        enableHeadView = args.getBoolean(ENABLE_HEAD_VIEW);
        userInfo = (UserInfo) args.getSerializable(USER_INFO);

        subTag = enableHeadView ? "Profile" : "Following";
        if (enableFilter) {//Rank允许内容筛选
            initFilterData();
            subTag = "Rank";
        } else {//Following & Profile
            listFilter.setVisibility(View.GONE);
            sortFilter.setVisibility(View.GONE);
            timeFrameFilter.setVisibility(View.GONE);
            if (!enableHeadView) {//Following需要监听用户登录注销事件
                receiver = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        switch (intent.getAction()) {
                            case LoginActivity.TAG:
                                doPullToRefresh();
                                break;
                            case MenuFragment.TAG:
                                App.getQueue().cancelAll(subTag);
                                isUserLogined();
                                break;
                        }
                    }
                };
                IntentFilter filter = new IntentFilter();
                filter.addAction(LoginActivity.TAG);//监听登录成功事件
                filter.addAction(MenuFragment.TAG);//监听注销事件
                activity.registerReceiver(receiver, filter);
            }
        }

        shots = new ArrayList<>();
        if (savedInstanceState != null) {//再实例化,需要修正数据集合
            Log.i(TAG + ":" + subTag, "initData: savedInstanceState != null");
            shots = (ArrayList<ShotInfo>) savedInstanceState.getSerializable(SHOTS);
            progressBar.setVisibility(View.INVISIBLE);
            needForceRefresh = false;
        }
    }

    /**
     * 初始化映射关系,string-array资源需与API.EndPoint.Parameter.XXX顺序严格一致
     */
    private void initFilterData() {
        Resources resources = getResources();
        sortKeys = resources.getStringArray(R.array.sort);
        sortValues = new String[sortKeys.length];
        API.EndPoint.Parameter.Sort sorts[] = API.EndPoint.Parameter.Sort.values();
        for (int i = 0; i < sorts.length; i++) {
            sortValues[i] = sorts[i].toString();
        }
        sortSelected = sortValues[0];

        listKeys = resources.getStringArray(R.array.list);
        listValues = new String[listKeys.length];
        API.EndPoint.Parameter.List lists[] = API.EndPoint.Parameter.List.values();
        for (int i = 0; i < lists.length; i++) {
            listValues[i] = lists[i].toString();
        }
        listSelected = listValues[0];

        timeFrameKeys = resources.getStringArray(R.array.timeFrame);
        timeFrameValues = new String[timeFrameKeys.length];
        API.EndPoint.Parameter.TimeFrame timeFrames[] = API.EndPoint.Parameter.TimeFrame.values();
        for (int i = 0; i < timeFrames.length; i++) {
            timeFrameValues[i] = timeFrames[i].toString();
        }
        timeFrameSelected = timeFrameValues[0];
    }

    @Override
    protected void initAdapter(BaseActivity activity) {
        if (enableFilter) {
            initFilterAdapter(activity);
        }
        recyclerView.setAdapter(adapter = new ShotsAdapter(activity, shots, userInfo, subTag));
    }

    /**
     * 初始化Spinner的适配器
     */
    private void initFilterAdapter(BaseActivity activity) {
        int layoutID = android.R.layout.simple_spinner_item;
        sortFilter.setAdapter(new ArrayAdapter<>(activity, layoutID, sortKeys));
        listFilter.setAdapter(new ArrayAdapter<>(activity, layoutID, listKeys));
        timeFrameFilter.setAdapter(new ArrayAdapter<>(activity, layoutID, timeFrameKeys));
    }

    @Override
    protected void initListener(BaseActivity activity) {
        if (enableFilter) {
            initFilterListener();
        }
        ptrFrameLayout.setPtrHandler(new PtrHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                if (!enableFilter && !enableHeadView) {//试图刷新Following时需要检查用户是否已登录
                    if (isUserLogined()) {
                        doPullToRefresh();
                        Log.i(TAG + ":" + subTag, "onRefreshBegin: pull to refresh");
                    } else {
                        ptrFrameLayout.refreshComplete();
                        Log.i(TAG + ":" + subTag, "onRefreshBegin: pull to refresh cancelled");
                    }
                } else {
                    doPullToRefresh();
                    Log.i(TAG + ":" + subTag, "onRefreshBegin: pull to refresh");
                }
            }

            /**
             * 覆盖触发下拉事件的检查
             */
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                //对gridView是否可滚动进行检测,当gridView无法向下滚动时允许进行下拉刷新
                return !recyclerView.canScrollVertically(-1);
            }
        });
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            /**
             * 下滑自动请求数据成功时调用的监听器
             */
            private Response.Listener<String> scrollRefreshSuccess = new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.i(TAG + ":" + subTag, "onResponse: onScroll refresh success at page " + page);
                    refreshable = true;//重置状态锁
                    progressBar.setVisibility(View.INVISIBLE);
                    page++;//更新页码
                    ArrayList<ShotInfo> shots = gson.fromJson(response, type);
                    for (ShotInfo shot : shots) {
                        //"2015-05-29T08:59:36Z" -> "2015-05-29 08:59:36"
                        shot.setCreated_at(shot.getCreated_at().replace("T", " ").replace("Z", ""));
                    }
                    adapter.addDataToBottom(shots);//将请求到的数据追加到原有内容的尾部
                }
            };
            /**
             * 下滑自动请求数据失败时调用的监听器
             */
            private Response.ErrorListener scrollRefreshFailed = new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    refreshable = true;//重置状态锁
                    Log.i(TAG + ":" + subTag, "onErrorResponse: onScroll refresh failed at page " + page);
                }
            };

            /**
             * 当GridView滑动到一定位置时自动进行新数据的请求<br/>
             * 由于在滑动过程中会多次出发位置判定,因此需要额外进行状态判定{@link MainFragment#refreshable}<br/>
             * 请求到数据后重置状态锁,将反射生成的数据进行修正后追加到内容池中
             */
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (gridLayoutManager.findLastVisibleItemPosition() > adapter.getItemCount() - 6) {
                    if (refreshable) {
                        refreshable = false;//上锁
                        Log.i(TAG + ":" + subTag, "onScrolled: try refresh at page " + page);
                        if (enableFilter) {
                            App.stringRequest(
                                    String.format(API.EndPoint.SHOTS_PAGE_SORT_LIST_TIMEFRAME, page + "", sortSelected, listSelected, timeFrameSelected),
                                    scrollRefreshSuccess, scrollRefreshFailed, subTag);//标记这个请求,因为它可能会被用户的操作取消
                        } else if (enableHeadView) {
                            App.stringRequest(String.format(API.EndPoint.USERS_SHOTS_PAGE, userInfo.getId(), page),
                                    scrollRefreshSuccess, scrollRefreshFailed, subTag);
                        } else {
                            App.stringRequest(String.format(API.EndPoint.FOLLOWING_SHOTS_PAGE, page + ""),
                                    scrollRefreshSuccess, scrollRefreshFailed, subTag);
                        }
                    }
                }
            }
        });

        if (needForceRefresh && getUserVisibleHint()) {
            if (!enableFilter && !enableHeadView) {//Following
                if (isUserLogined()) {
                    Log.i(TAG + ":" + subTag, "initListener: force refreshing");
                    doPullToRefresh();
                }
            } else {//Rank & Profile
                Log.i(TAG + ":" + subTag, "initListener: force refreshing");
                doPullToRefresh();
            }
        }
    }

    /**
     * 初始化Spinner的监听器
     */
    private void initFilterListener() {
        sortFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sortSelected = sortValues[position];
                progressBar.setVisibility(View.VISIBLE);
                doPullToRefresh();
                Log.i(TAG + ":" + subTag, "onItemSelected: sortSelected=" + sortSelected);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        listFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                listSelected = listValues[position];
                progressBar.setVisibility(View.VISIBLE);
                doPullToRefresh();
                Log.i(TAG + ":" + subTag, "onItemSelected: listSelected=" + listSelected);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        timeFrameFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                timeFrameSelected = timeFrameValues[position];
                progressBar.setVisibility(View.VISIBLE);
                doPullToRefresh();
                Log.i(TAG + ":" + subTag, "onItemSelected: timeFrameSelected=" + timeFrameSelected);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    /**
     * 检查用户是否已登录
     */
    private boolean isUserLogined() {
        if (!API.Oauth2.ACCESS_TOKEN.equals(API.Oauth2.ACCESS_TOKEN_DEFAULT)) {
            return true;
        } else {
            toast(R.string.youAreNotLoginYet);
            connectionError.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
            return false;
        }
    }

    /**
     * 因筛选条件改变而需要再次请求合适的数据<br/>
     * 或因为网络连接异常导致完全没有获得任何数据而需要再次请求合适的数据<br/>
     * 请务必注意此方法与onScroll()的调用顺序,并注意pageSelected变更的时机
     */
    private void doPullToRefresh() {
        page = 1;//重置页码
        Response.Listener<String> ptrSuccess = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {//下拉刷新成功时调用的监听器
                Log.i(TAG + ":" + subTag, "onResponse: doPullToRefresh refresh success at page " + page);
                refreshable = true;
                page++;
                ptrFrameLayout.refreshComplete();
                progressBar.setVisibility(View.INVISIBLE);
                connectionError.setVisibility(View.INVISIBLE);
                ArrayList<ShotInfo> shots = gson.fromJson(response, type);
                for (ShotInfo shot : shots) {
                    //"2015-05-29T08:59:36Z" -> "2015-05-29 08:59:36"
                    shot.setCreated_at(shot.getCreated_at().replace("T", " ").replace("Z", ""));
                }
                adapter.setData(shots);
            }
        };
        Response.ErrorListener ptrFailed = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {//下拉刷新失败时调用的监听器
                refreshable = true;
                ptrFrameLayout.refreshComplete();
                Log.i(TAG + ":" + subTag, "onErrorResponse: doPullToRefresh refresh failed at page " + page);
                progressBar.setVisibility(View.INVISIBLE);
                connectionError.setVisibility(View.VISIBLE);
                toast(R.string.connection_error);
            }
        };
        App.getQueue().cancelAll(subTag);//取消尚未完成的请求
        Log.i(TAG + ":" + subTag, "doPullToRefresh: last request with TAG=\"" + subTag + "\" canceled");

        if (enableFilter) {
            App.stringRequest(
                    String.format(API.EndPoint.SHOTS_PAGE_SORT_LIST_TIMEFRAME, page, sortSelected, listSelected, timeFrameSelected),
                    ptrSuccess, ptrFailed, subTag);
        } else if (enableHeadView) {
            App.stringRequest(String.format(API.EndPoint.USERS_SHOTS_PAGE, userInfo.getId(), page),
                    ptrSuccess, ptrFailed, subTag);
        } else {
            App.stringRequest(String.format(API.EndPoint.FOLLOWING_SHOTS_PAGE, page),
                    ptrSuccess, ptrFailed, subTag);
        }
    }

    @Override
    public void handleMessageImp(Message msg) {
    }

    /**
     * 当MainActivity从tab3切换到tab1时,this会被销毁,需要保留部分参数
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(SHOTS, adapter.getData());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        App.getQueue().cancelAll(subTag);//取消掉未完成的全部Shots请求以及ShotsAdapter对象内的全部图像请求
        if (receiver != null) {
            getActivity().unregisterReceiver(receiver);
        }
    }
}
