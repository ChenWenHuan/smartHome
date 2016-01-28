package com.smarthome.client2.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;

import com.smarthome.client2.R;
import com.smarthome.client2.common.TLog;
import com.smarthome.client2.config.Preferences;
import com.smarthome.client2.manager.AppManager;
import com.smarthome.client2.util.HttpUtil;
import com.umeng.analytics.MobclickAgent;

public class RunTypeActivity extends Activity
{

    public static final int MY_RESULT_FINISH = 1;

    private Handler mHandler = new Handler()
    {
        public void handleMessage(android.os.Message msg)
        {
            TLog.Log("zxl---runtype---mhandler---" + msg.what);
            HttpUtil.responseHandler(getApplicationContext(), msg.what);
            switch (msg.what)
            {
                case 200:

                    Intent intent = new Intent(RunTypeActivity.this,
                            MainActivity.class);
                    startActivity(intent);
                    RunTypeActivity.this.finish();
                    break;

                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.layout_runtype);
        AppManager.getAppManager().addActivity(this);
        //		UmengUpdateAgent.update(this);
        //		UmengUpdateAgent.setUpdateOnlyWifi(false);
        //		UmengUpdateAgent.setDownloadListener(null);
        //		UmengUpdateAgent.setDialogListener(null);
        //		UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {
        //			
        //			@Override
        //			public void onUpdateReturned(int updateStatus, UpdateResponse updateInfo) {
        //				if(isFinishing()){
        //					return;
        //				}
        //				switch (updateStatus) {
        //				case 0:
        //					UmengUpdateAgent.showUpdateDialog(RunTypeActivity.this, updateInfo);
        //					break;
        //				case 1:
        //					
        //					break;
        //				case 3:
        //					
        //					break;
        //				default:
        //					break;
        //				}
        //			}
        //		});

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == MY_RESULT_FINISH)
        {
            finish();
        }
    }

    public void Click_login(View view)
    {
        Intent intent = new Intent(RunTypeActivity.this, LoginActivity_sm.class);
        if (Preferences.getInstance(RunTypeActivity.this).getIsLogout())
        {
            intent.putExtra(LoginActivity_sm.KEY_SHOW_USERNAME, true);
        }
        startActivityForResult(intent, MY_RESULT_FINISH);
    }

    public void Click_register(View view)
    {
        Intent intent = new Intent(RunTypeActivity.this, RegisterActivity.class);
        startActivityForResult(intent, MY_RESULT_FINISH);
    }

    public void Click_no_ring(View view)
    {

    }

    @Override
    protected void onDestroy()
    {
        AppManager.getAppManager().removeActivity(this);
        super.onDestroy();
    }

    /* (non-Javadoc)
     * @see android.app.Activity#onResume()
     */
    @Override
    protected void onResume()
    {
        MobclickAgent.onPageStart(getClass().getSimpleName());
        MobclickAgent.onResume(this);
        super.onResume();
    }

    /* (non-Javadoc)
     * @see android.app.Activity#onPause()
     */
    @Override
    protected void onPause()
    {
        MobclickAgent.onPageEnd(getClass().getSimpleName());
        MobclickAgent.onPause(this);
        super.onPause();
    }
}
