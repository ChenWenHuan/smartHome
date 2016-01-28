package com.smarthome.client2.util;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.smarthome.client2.R;
import com.smarthome.client2.activity.RegisterActivity.ServerExceptionListener;
import com.smarthome.client2.common.Constants;

public class ExceptionReciver extends BroadcastReceiver
{
    //listeners
    private static ServerExceptionListener mServerExceptionListener;

    private static ProgressDialog mDialog;//used for register

    private static ProgressDialog mNetLisenerDialog;//userd for netstatuslisener

    public static void setDialog(ProgressDialog dialog)
    {
        mDialog = dialog;
    }

    public static void setNetLisenerDialog(ProgressDialog dialog)
    {
        mNetLisenerDialog = dialog;
    }

    @Override
    public void onReceive(Context ctx, Intent intent)
    {
        String action = intent.getAction();
        if (action.equals(Constants.SOCKET_FLITER_EXCEPTION))
        {
            Toast.makeText(ctx,
                    ctx.getString(R.string.netlistener_server_socket_time),
                    Toast.LENGTH_SHORT).show();
            if (mDialog != null && mDialog.isShowing())
            {
                mDialog.dismiss();
            }
            else if (mNetLisenerDialog != null && mNetLisenerDialog.isShowing())
            {
                mNetLisenerDialog.dismiss();
                NetStatusListener.mClickflag = false;
            }
        }
        else if (action.equals(Constants.CONNECT_FLITER_EXCEPTION))
        {
            Toast.makeText(ctx,
                    ctx.getString(R.string.netlistener_server_socket_time),
                    Toast.LENGTH_SHORT).show();
            if (mDialog != null && mDialog.isShowing())
            {
                mDialog.dismiss();
                mServerExceptionListener.getConnectionFail();
                mServerExceptionListener = null;
            }
            else if (mNetLisenerDialog != null && mNetLisenerDialog.isShowing())
            {
                mNetLisenerDialog.dismiss();
                NetStatusListener.mClickflag = false;
            }
        }
        mDialog = null;
    }

    public static void setRegisterExceptionListener(
            ServerExceptionListener serverExceptionListener)
    {
        mServerExceptionListener = serverExceptionListener;
    }
}
