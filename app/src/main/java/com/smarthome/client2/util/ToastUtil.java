package com.smarthome.client2.util;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.smarthome.client2.R;

public class ToastUtil
{
    private Context context;

    private Toast customToast;

    public ToastUtil(Context ctx)
    {
        this.context = ctx;
    }

    public void initToast(Handler mHandler, int what)
    {
        customToast = Toast.makeText(context,
                context.getString(R.string.netlistener_set_success),
                Toast.LENGTH_LONG);
        execToast(7, mHandler, what);
    }

    private void execToast(final int cnt, final Handler mHandler, final int what)
    {
        Timer timer = new Timer();
        timer.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                mHandler.sendEmptyMessage(what);
                showMyToast(cnt - 1, mHandler, what);
                if (cnt - 1 < 0)
                {
                    customToast = null;
                }
            }
        }, 2000);
    }

    private void showMyToast(int cnt, Handler mHandler, int what)
    {
        if (cnt < 0)
            return;
        if (customToast != null)
        {
            customToast.show();
            execToast(cnt, mHandler, what);
        }
    }

    public Toast getCustomToast()
    {
        return customToast;
    }

    public void setCustomToast(Toast customToast)
    {
        this.customToast = customToast;
    }

    public boolean cancleToast()
    {
        if (ToastUtil.this != null && ToastUtil.this.getCustomToast() != null)
        {
            ToastUtil.this.getCustomToast().cancel();
            ToastUtil.this.setCustomToast(null);
            Log.d("", "daitm----toast is cancled");
            return false;
        }
        return true;
    }

}
