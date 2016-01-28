package com.smarthome.client2.util;

import java.io.IOException;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.widget.Toast;

import com.smarthome.client2.R;
import com.smarthome.client2.common.Constants;

public class ExceptionUtil
{
    private final static String SOCKET_TIME_EXCEPTION = "SocketTimeoutException";

    private final static String CONNECT_EXCEPTION = "ConnectException";

    private static Intent exceptionIntent = new Intent();

    public static void catchIOException(IOException e, Context ctx)
    {
        if (e.toString().contains(SOCKET_TIME_EXCEPTION))
        {
            exceptionIntent.setAction(Constants.SOCKET_FLITER_EXCEPTION);
            ctx.sendBroadcast(exceptionIntent);
        }
        else if (e.toString().contains(CONNECT_EXCEPTION))
        {
            exceptionIntent.setAction(Constants.CONNECT_FLITER_EXCEPTION);
            ctx.sendBroadcast(exceptionIntent);
        }
    }
}
