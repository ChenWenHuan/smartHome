package com.smarthome.client2.familySchool.ui;

import com.smarthome.client2.manager.AppManager;
import com.smarthome.client2.util.CancelSubmitDialog;
import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class BaseActivity extends Activity
{

    private ProgressDialog progressDialog;

    private CancelSubmitDialog cancelSumitDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        AppManager.getAppManager().addActivity(this);
    }

    public void showProgressDialog(String message)
    {
        if (progressDialog == null)
        {
            progressDialog = new ProgressDialog(this);
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setMessage(message);
            progressDialog.show();
        }
    }

    public void showProgressDialog(int resId)
    {
        String text = getString(resId);
        showProgressDialog(text);
    }

    public void removeProgressDialog()
    {
        if (progressDialog != null)
        {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    public void showToast(String text)
    {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    public void showToast(int resId)
    {
        String text = getString(resId);
        showToast(text);
    }

    public void showCancelDialog()
    {
        if (cancelSumitDialog == null)
        {
            cancelSumitDialog = new CancelSubmitDialog(this);
            cancelSumitDialog.setSubmitClick(new OnClickListener()
            {
                @Override
                public void onClick(View arg0)
                {
                    cancelSumitDialog.dismissMyDialog();
                    finish();
                }
            });
            cancelSumitDialog.clickCancel(new OnClickListener()
            {
                @Override
                public void onClick(View arg0)
                {
                    cancelSumitDialog.dismissMyDialog();
                }
            });
        }
        cancelSumitDialog.showMyDialog();
    }

    @Override
    protected void onDestroy()
    {
        removeProgressDialog();
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
