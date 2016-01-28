package com.smarthome.client2;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import cn.jpush.android.api.JPushInterface;

import com.smarthome.client2.R;
import com.smarthome.client2.activity.IntroduceActivity;
import com.smarthome.client2.activity.LoginActivity_sm;
import com.smarthome.client2.activity.MainActivity;
import com.smarthome.client2.common.Constants;
import com.smarthome.client2.common.GloableData;
import com.smarthome.client2.common.TLog;
import com.smarthome.client2.config.Preferences;
import com.smarthome.client2.familySchool.utils.FsConstants;
import com.smarthome.client2.manager.VersionManager;
import com.smarthome.client2.message.MessageUtil;
import com.smarthome.client2.util.HttpUtil;
import com.smarthome.client2.util.LoginUtil;
import com.umeng.analytics.MobclickAgent;

/**
 * 打开应用进入的第一个界面，在该页面判断是进入引导页面，还是进入欢迎界面
 * @author xiaolong.zhang
 */
public class StartActivity extends Activity
{
    /**
     * 日志开关
     */
    private final static boolean DEBUG = true;

    private final static long START_TIME = 500;

    private String username = "";

    private String password = "";

    private Handler mHandler = new Handler();

    private Runnable mStartTask = new Runnable()
    {
        public void run()
        {
            if (Preferences.getInstance(StartActivity.this)
                    .getLastVersionCode() < VersionManager.getSofewareVersionCode(StartActivity.this))
            {
                startActivity(new Intent(StartActivity.this,
                        IntroduceActivity.class));
            }
            else if (Preferences.getInstance(StartActivity.this).getAutoLogin())
            {
                if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password))
                {
                    Intent intentRunType = new Intent(StartActivity.this,
                            LoginActivity_sm.class);
                    startActivity(intentRunType);
                    StartActivity.this.finish();
                }
                else
                {
                    if (HttpUtil.isNetworkAvailable(StartActivity.this))
                    {
                    	HttpUtil.BASE_URL_SMART = Preferences.getInstance(StartActivity.this).getUserBaseUrl(username);
                    	HttpUtil.initUrl(2);
                    	new LoginTask().execute();
                    }
                    else
                    {
                        autoLoginToMain(false, Constants.NO_NETWORK);
                    }
                }
                return;
            }
            else
            {
                startActivity(new Intent(StartActivity.this,
                        LoginActivity_sm.class));

            }
            StartActivity.this.finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.introduce_view1);

        initData();
        mHandler.postDelayed(mStartTask, START_TIME);
        // 友盟统计设置
        MobclickAgent.setDebugMode(false);
        MobclickAgent.updateOnlineConfig(getApplicationContext());
        MobclickAgent.openActivityDurationTrack(false);
    }

    private void initData()
    {
        //初始化屏幕参数
        GloableData.initScreen(this);
        //初始化iemi和imsi
        GloableData.initIMSIIMEI(this);

        username = Preferences.getInstance(StartActivity.this).getUserName();
        password = Preferences.getInstance(StartActivity.this).getPassWord();
    }

    class LoginTask extends AsyncTask<Void, Void, String>
    {

        @Override
        protected String doInBackground(Void... params)
        {
            String result = LoginUtil.requestLogin(StartActivity.this,
                    username,
                    password);
            return result;
        }

        protected void onPostExecute(String result)
        {
            if (null == result || TextUtils.isEmpty(result))
            {
                autoLoginToMain(false, Constants.JSON_ERROR);
            }
            else
            {
                boolean isCode = false;
                int errorCode = Constants.UNKNOW_RESULT;
                try
                {
                    errorCode = Integer.parseInt(result);
                    isCode = true;
                }
                catch (Exception e)
                {
                    isCode = false;
                }
                if (isCode)
                {
                    autoLoginToMain(false, errorCode);
                }
                else
                {
                    Boolean isLogin = LoginUtil.doLogin(StartActivity.this,
                            result);
                    if (isLogin)
                    {
                        Preferences preferences = Preferences.getInstance(getApplicationContext());
                        preferences.setAutoLogin(true);
                        preferences.setUserName(username);
                        preferences.setPassWord(password);
                        preferences.setIsLogout(false);

                        Intent intentWidget = new Intent(
                                FsConstants.WIDGET_ACCOUNT_CHANGGE);
                        sendBroadcast(intentWidget);
                        autoLoginToMain(true, Constants.SC_OK);
                    }
                    else
                    {
                        autoLoginToMain(false, Constants.JSON_ERROR);
                    }
                }

            }
        }
    }

    private void autoLoginToMain(boolean isToMain, final int error)
    {
        log("------autoLoginToMain----" + isToMain);
        if (isToMain)
        {
            Intent intent = new Intent(StartActivity.this, MainActivity.class);
            intent.putExtra("isNeedCheckUpdate", true);
            startActivity(intent);
            if (!MessageUtil.checkService(this))
            {
                MessageUtil.startMessageService(this);
            }
        }
        else
        {
            Intent intent = new Intent(StartActivity.this, LoginActivity_sm.class);
            if (Preferences.getInstance(StartActivity.this).getAutoLogin())
            {
                intent.putExtra(LoginActivity_sm.KEY_SHOW_USERNAME, true);
                intent.putExtra(LoginActivity_sm.KEY_SHOW_PSW, true);
            }

            if (Preferences.getInstance(StartActivity.this).getIsLogout()
                    || error == Constants.ERROR_PASSWORD
                    || error == Constants.UNEXIST_ID)
            {
                intent.putExtra(LoginActivity_sm.KEY_SHOW_USERNAME, true);
            }

            intent.putExtra(LoginActivity_sm.KEY_LOGIN_FAILED, true);
            intent.putExtra(LoginActivity_sm.KEY_ERROR_CODE, error);
            startActivity(intent);
        }
        finish();
    }

    private void log(String msg)
    {
        if (DEBUG)
        {
            TLog.Log(msg);
        }
    }

    /* (non-Javadoc)
     * @see android.app.Activity#onResume()
     */
    @Override
    protected void onResume()
    {
        MobclickAgent.onPageStart("StartActivity"); //统计页面
        MobclickAgent.onResume(this);
        JPushInterface.onResume(this);
        super.onResume();
        
    }

    /* (non-Javadoc)
     * @see android.app.Activity#onPause()
     */
    @Override
    protected void onPause()
    {
        MobclickAgent.onPageEnd("StartActivity");
        MobclickAgent.onPause(this);
        JPushInterface.onPause(this);
        super.onPause();
    }
}
