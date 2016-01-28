package com.smarthome.client2.activity;

import java.io.IOException;
import java.util.ArrayList;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.jpush.android.api.JPushInterface;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

import com.smarthome.client2.R;
import com.smarthome.client2.SmartHomeApplication;
import com.smarthome.client2.common.TLog;
import com.smarthome.client2.config.Preferences;
import com.smarthome.client2.fragment.HomeFragement_V11;
import com.smarthome.client2.fragment.MyInfoFragment;
import com.smarthome.client2.fragment.UserMessageFragment;
import com.smarthome.client2.manager.AppManager;
import com.smarthome.client2.model.retrofitServices.ServiceGenerator;
import com.smarthome.client2.util.IchaoCameraUtil;
import com.smarthome.client2.util.LinkTopSDKUtil;
import com.smarthome.client2.util.LoginUtil;
import com.smarthome.client2.util.MyExceptionDialog;
import com.squareup.okhttp.ResponseBody;
import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;

public class MainActivity extends FragmentActivity implements
        OnClickListener
{
    public static final String NICK_NAME_CHANGE = "com.smarthome.TodayFragment.NickNameChange";
    public static final int NEW_MSG_REFRESH = 1;


    private Fragment mContent;
    private TextView mTitleTextView;
    private TextView mSubTitleTextView;
    private FragmentControlCenter mControlCenter;

    private boolean isRefreshHomeFragment = false;
    private LinkTopSDKUtil instance;    
    private int mCurrentTabIndex;
    private LinearLayout mLlTab;
    private LinearLayout mLlNewMsg;
    private FrameLayout mTabF1;
    private FrameLayout mTabF2;
    private FrameLayout mTabF3;
    private ServiceGenerator serviceGenerator;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mControlCenter = FragmentControlCenter.getInstance(this);
        mCurrentTabIndex = 0;
        AppManager.getAppManager().addActivity(this);
        //极光推送初始化
        JPushInterface.init(getApplicationContext());
        loadSdkLib();
        //摄像头相关初始化
        IchaoCameraUtil.getInstance().initSDK();
        
        mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				JPushInterface.setAlias(getApplicationContext(), 
						                 SmartHomeApplication.getInstance().openID, 
						                 null);
                Log.e("MainActivity", "JPush setAlias openID=" + SmartHomeApplication.getInstance().openID);
			}
		}, 40000);
        // 友盟检查更新
        UmengUpdateAgent.setUpdateOnlyWifi(false);
        UmengUpdateAgent.setUpdateListener(null);
        UmengUpdateAgent.update(getApplicationContext());        
        SmartHomeApplication.getInstance().setMainActivity(this);
        
        setupViews();
        initBottomViews();
        initWatchData();
        String flag = getIntent().getStringExtra("flag");
        if (flag != null) {
            FragmentModel fragmentModel;
            setSelectedTab(1);
            fragmentModel = mControlCenter.getSMMessagesFragmentModel();
            switchContent(fragmentModel);
        }
        FragmentModel fragmentModel = mControlCenter.getSMHomeFragmentModel();
        switchContent(fragmentModel);
    }

    private void loadSdkLib()
    {
        if(!SmartHomeApplication.getInstance().isCameraLibLoaded) {
            System.loadLibrary("gnustl_shared");
            System.loadLibrary("ffmpeg");
            System.loadLibrary("avdecoder");
            System.loadLibrary("sdk30");
            System.loadLibrary("viewer30");
            SmartHomeApplication.getInstance().isCameraLibLoaded = true;
        }

    }

    private void initWatchData(){
        Preferences tmpPreferences = Preferences.getInstance(this.getApplicationContext());
        String userTelNum = tmpPreferences.getUserTelNum();
    	instance =LinkTopSDKUtil.getInstance();
    	instance.initSDK(this, handle_linktop);
    	instance.setupAccount(userTelNum, "888888");
    	instance.loginToken();
    }
    
    public void sendNewMsgRefresh(){
    	Message msg = handle_linktop.obtainMessage();
        msg.what = NEW_MSG_REFRESH;
        msg.sendToTarget();
    }
    
    private Handler  handle_linktop = new Handler(){
    	
        public void handleMessage(android.os.Message msg)
        {
            switch (msg.what)
            {
            case LinkTopSDKUtil.LINK_SDK_LOGIN_TOKEN:
            	if (msg.arg1 == 200){
            		SmartHomeApplication.getInstance().isLinkTopAccout = true;
            		String registerID = JPushInterface.getRegistrationID(getApplicationContext());
            		Log.e("MainActivity", "JPush RegisterID =" + registerID);
            		if (registerID == null || registerID.equals("")){
            			Message msgNew = new Message();
            			msgNew.what = LinkTopSDKUtil.LINK_SDK_LOGIN_TOKEN;
            			msgNew.arg1 = 200;
            			handle_linktop.sendMessageDelayed(msgNew, 4000);
            		}else{
            			instance.registerPushParam(registerID);
            		}
            	}else{
            		SmartHomeApplication.getInstance().isLinkTopAccout = false;
            	}
            	break;
            case NEW_MSG_REFRESH:
            	showNewMsgComing();
            	break;
            default:
               break;
            }
        }
    };

    private void initBottomViews()
    {
    	
    	mLlTab = (LinearLayout)findViewById(R.id.ll_tab);

        mTabF1 = (FrameLayout) findViewById(R.id.frame_1);
        mTabF2 = (FrameLayout) findViewById(R.id.frame_2);
        mTabF3 = (FrameLayout) findViewById(R.id.frame_3);
        
        mLlNewMsg = (LinearLayout)findViewById(R.id.ll_new_msg);

        mTabF1.setOnClickListener(this);
        mTabF2.setOnClickListener(this);
        mTabF3.setOnClickListener(this);
    }
    public void onClick(View v)
    {
        FragmentModel fragmentModel;

        switch (v.getId())
        {
            case R.id.frame_1:
            	setSelectedTab(0);
                fragmentModel = mControlCenter.getSMHomeFragmentModel();
                switchContent(fragmentModel);
                break;
            case R.id.frame_3:
            	setSelectedTab(2);
            	fragmentModel = mControlCenter.getMyInfoFragmentModel();
                switchContent(fragmentModel);
                break;
            case R.id.frame_2:
            	setSelectedTab(1);
            	fragmentModel = mControlCenter.getSMMessagesFragmentModel();
                switchContent(fragmentModel);
                break;

            default:
                break;
        }
    }
    
    public void setRefreshHomeFragment(boolean flag){    	
    	isRefreshHomeFragment = flag;    	
    }
    
    public boolean getRefreshHomeFragment(){    	
    	return isRefreshHomeFragment;    }
    
    private void setSelectedTab(int arg0) {
    	
    	if (arg0 == 1){
			mLlNewMsg.setVisibility(View.GONE);
		}
    	if (mCurrentTabIndex == 1  && arg0 != 1){
    		mLlNewMsg.setVisibility(View.GONE);
    	}    	
    	mCurrentTabIndex = arg0;    	
		for (int i = 0; i < 3; i++) {
			
			LinearLayout line_lay_bg = (LinearLayout) mLlTab.findViewWithTag("line_bg_" + Integer.toString(i+1));
			if (arg0 == i) {
				line_lay_bg.setVisibility(View.VISIBLE);
			} else {
				line_lay_bg.setVisibility(View.GONE);
			}
		}
	}

    private void setupViews()
    {

        setContentView(R.layout.main_slidemenu_layout);

        netDialog = new ProgressDialog(this);
        netDialog.setCanceledOnTouchOutside(false);
        netDialog.setCancelable(true);

        myExceptionDialog = new MyExceptionDialog(this);
        myExceptionDialog.setSubmitClick(new OnClickListener() {
            @Override
            public void onClick(View v) {
                myExceptionDialog.dismissMyDialog();
            }
        });

    }

    /**
     * 调用相应的fragment
     * @param fragment
     */
    public void switchContent(final FragmentModel fragment)
    {
        mContent = fragment.mFragment;

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, mContent)
                .commitAllowingStateLoss();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        String flag = intent.getStringExtra("flag");
        //TLog.Log("flag  onNewIntent is "+ flag);
        if (flag != null) {
            // 如果intent不为null，且flag的值为ok，则表示点击了通知栏消息，那么切换到车库界面
            FragmentModel fragmentModel;
            setSelectedTab(1);
            fragmentModel = mControlCenter.getSMMessagesFragmentModel();
            switchContent(fragmentModel);
        }
    }

    public void updateTitle(String title, String subtitle, boolean isShown)
    {
        mTitleTextView.setText(title);
        mSubTitleTextView.setText(subtitle);
        if (isShown)
            mSubTitleTextView.setVisibility(View.VISIBLE);
        else
        {
            mSubTitleTextView.setVisibility(View.GONE);
        }
    }

    public void updateTitle(boolean isShown)
    {
        if (isShown)
            mSubTitleTextView.setVisibility(View.VISIBLE);
        else
        {
            mSubTitleTextView.setVisibility(View.GONE);
        }
    }

    private long main_key_down_start_time = 0;

    private boolean isFirstMainKeyDown = true;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK)
        {
            if (mContent instanceof HomeFragement_V11)
            {
                if (isFirstMainKeyDown)
                {
                    main_key_down_start_time = System.currentTimeMillis();
                    isFirstMainKeyDown = false;
                    Toast.makeText(MainActivity.this,"再次点击退出", Toast.LENGTH_SHORT).show();
                    return false;
                }
                else
                {
                    //如果是在首页，三秒内第二次按返回键，如果菜单栏是关闭状态，则退出应用
                    if (System.currentTimeMillis() - main_key_down_start_time < 3000)
                    {
                        return super.onKeyDown(keyCode, event);
                    }
                    else
                    {
                        main_key_down_start_time = System.currentTimeMillis();
                        isFirstMainKeyDown = false;
                        Toast.makeText(MainActivity.this,"再次点击退出", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                }
            }
            else if (mContent instanceof UserMessageFragment)
            {
                mHandler.postDelayed(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        mContent = null;
                    }
                }, 300);
                return true;
            }
            else
            {//如果不是在首页点击返回键，则跳转到首页
                switchContent(mControlCenter.getSMHomeFragmentModel());
                setSelectedTab(0);
                return false;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    public void nofity2MsgPage() {
        Intent intent = new Intent();
        String flag = intent.getStringExtra("flag");
        if (flag != null) {
            FragmentModel fragmentModel;
            setSelectedTab(1);
            fragmentModel = mControlCenter.getSMMessagesFragmentModel();
            switchContent(fragmentModel);
        }
    }


    @Override
    public void onResume()
    {
        nofity2MsgPage();
        Log.e("MainActivity", "--resume------------------");
        super.onResume();
        getMainFromServer();
        MobclickAgent.onResume(this);
    }

    @Override
    public void onPause()
    {
        super.onPause();
        MobclickAgent.onPause(this);
    }
    
    public void showNewMsgComing(){
    	
    	if (mLlNewMsg != null){
    		mLlNewMsg.setVisibility(View.VISIBLE);
    	}
    	
    }

    protected void onDestroy()
    {
        if (myExceptionDialog != null)
        {
            myExceptionDialog.dismissMyDialog();
        }
        TLog.Log("MainActivityonDestroy");
        
        AppManager.getAppManager().removeActivity(this);
        
        SmartHomeApplication.getInstance().setMainActivity(null);        
//        IchaoCameraUtil.getInstance().logout();
//        IchaoCameraUtil.getInstance().destroy();
        super.onDestroy();
        
    }

    private void getMainFromServer(){

        serviceGenerator = ServiceGenerator.getInstance(this);

        Call call = serviceGenerator.getApiService().getMain();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Response<ResponseBody> response, Retrofit retrofit) {
                try {
                    String result = response.body().string();
                    Log.e("GetMain", "result=" + result);
                    LoginUtil.getFmilyData(MainActivity.this, result);
                    if (mContent instanceof HomeFragement_V11){
                        ((HomeFragement_V11) mContent).refreshFamilyView();
                    }else if (mContent instanceof MyInfoFragment){
                        ((MyInfoFragment) mContent).refreshData();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });

    }



    private Handler mHandler = new Handler();

    private ProgressDialog netDialog;

    private MyExceptionDialog myExceptionDialog;

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        // TODO Auto-generated method stub
        for (MyOntouchListener listener : touchListeners)
        {
            listener.onTouchEvent(event);
        }
        return super.onTouchEvent(event);
    }

    private ArrayList<MyOntouchListener> touchListeners = new ArrayList<MainActivity.MyOntouchListener>();

    public void registerListener(MyOntouchListener listener)
    {
        touchListeners.add(listener);
    }

    public void unRegisterListener(MyOntouchListener listener)
    {
        touchListeners.remove(listener);
    }

    public void clearListener()
    {
        touchListeners.clear();
    }

    public interface MyOntouchListener  {
        void onTouchEvent(MotionEvent event);
    }

}