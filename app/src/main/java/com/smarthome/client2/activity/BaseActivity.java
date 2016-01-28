package com.smarthome.client2.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

import com.smarthome.client2.common.TLog;
import com.smarthome.client2.manager.AppManager;
import com.umeng.analytics.MobclickAgent;

public abstract class BaseActivity extends Activity
{
    /**
     * 日志开关
     */
    private static final boolean DEBUG = true;

    private Toast mToast;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        AppManager.getAppManager().addActivity(this);
    }

    /**
     * show toast<BR>
     * @param msg alert info
     */
    protected void showToast(String msg)
    {
        if (mToast == null)
        {
            mToast = Toast.makeText(getApplicationContext(),
                    msg,
                    Toast.LENGTH_SHORT);
        }
        mToast.setText(msg);
        mToast.show();
    }

    /**
     * show toast<BR>
     * @param msgid the string id of alert info
     */
    protected void showToast(int msgid)
    {
        showToast(getString(msgid));
    }

    /**
     * [日志输出]<BR>
     * 打印日志内容
     * @param msg 日志内容
     */
    protected void log(String msg)
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

    /* (non-Javadoc)
     * @see android.app.Activity#onDestroy()
     */
    @Override
    protected void onDestroy()
    {
        AppManager.getAppManager().removeActivity(this);
        super.onDestroy();
    }
}
