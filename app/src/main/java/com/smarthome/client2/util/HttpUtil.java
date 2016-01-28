package com.smarthome.client2.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.smarthome.client2.R;
import com.smarthome.client2.SmartHomeApplication;
import com.smarthome.client2.common.Constants;
import com.smarthome.client2.common.TLog;
import com.smarthome.client2.config.Preferences;

public class HttpUtil
{
    //声明Base URL常量

    public static final String BASE_URL_WEAR = "http://wear.cufamily.cn";

//    public static final String BASE_URL_SMART = "http://api.cufamily.cn";
//    public static String BASE_URL_SMART = "http://123.57.206.32:8081/";
//    public static final String BASE_URL_SMART = "http://papp.ejcom.cn/";

   public static String FIRST_ENTRY_ADDR = "http://123.57.206.32:8080/AccountMS";

//    public static String FIRST_ENTRY_ADDR = "http://acct.ejcom.cn";

    public static Boolean g_bIsGetNewAddrDone = false; // if 1,don't need get new server address again
    
    public static String BASE_URL_SMART = "http://papp.ejcom.cn/";

    public static final String DISCLAIMSER_URL = "http://www.woofamily.cn/faq/agreement";



    public static String BASE_URL = BASE_URL_SMART;

    public static final int BASE_URL_WEAR_TYPE = 1;

    public static final int BASE_URL_SMART_TYPE = 2;

    private static Context mContext;

    /**
     * 1为WEAR，2为SMART
     * @param type
     */
    public static void initUrl(int type)
    {
        switch (type)
        {
            case 1:
                BASE_URL = BASE_URL_WEAR;
                break;
            case 2:
                BASE_URL = BASE_URL_SMART;
                break;
        }
    }

    private static String mJsessionId = null;

    public static String getJessionId()
    {
        return getJessionId(mContext);
    }

    public static String getJessionId(Context context)
    {
        if (mJsessionId == null && context != null)
        {
            mContext = context;
            mJsessionId = Preferences.getInstance(context.getApplicationContext())
                    .getJessionId();
        }
        return mJsessionId;
    }

    public static void setJessionId(String jessionId)
    {
        mJsessionId = jessionId;
        if (mContext != null)
        {
            Preferences.getInstance(mContext.getApplicationContext())
                    .setJessionId(jessionId);
        }
    }

    /**
     * get HttpClient<BR>
     * @return HttpClient
     */
    public static HttpClient getHttpClient(String action)
    {
        HttpClient httpClient = new DefaultHttpClient();
        int socketTime = 3*60*1000;
        String type = Preferences.getInstance(mContext).getDeviceModel();
        if (action.equals(Constants.SYN)
                && (type.equalsIgnoreCase("gk309") || type.equalsIgnoreCase("gs300")))
        {
            socketTime = Constants.TIMEOUT_SOCKET_GK309;
        }
        else
        {
            socketTime = Constants.TIMEOUT_SOCKET;
        }
        ConnManagerParams.setTimeout(httpClient.getParams(), Constants.TIMEOUT_CONNECTION);
        HttpConnectionParams.setConnectionTimeout(httpClient.getParams(),
                Constants.TIMEOUT_CONNECTION);
        HttpConnectionParams.setSoTimeout(httpClient.getParams(), socketTime);
        return httpClient;

    }

    /**
     * 响应状态
     * @param context
     * @param code
     * @return
     */
    public static String responseHandler(Context context, int code)
    {
        if (null == context)
        {
            return "";
        }
        mContext = context;
        String str = "";
        switch (code)
        {
            case Constants.SC_OK:
                break;
            case Constants.DEVICE_ALREADY_EXIT:
                str = "该设备已存在";
                break;
            case Constants.DEVICE_OPEN_OR_NOT:
                str = "请确认设备是否开机";
                break;
            case Constants.UNKNOW_RESULT:
                str = "未知错误.";
                break;
            case Constants.FAIL_SEARCH_CONTENT:
                str = context.getResources()
                        .getString(R.string.fail_search_content);
                break;
            case Constants.INVALID_PARAM:
                str = context.getResources().getString(R.string.invalid_param);
                break;
            case Constants.EXIST_ID:
                str = context.getResources().getString(R.string.exist_id);
                break;
            case Constants.UNEXIST_ID:
                str = context.getResources().getString(R.string.unexist_id);
                break;
            case Constants.ERROR_PASSWORD:
                str = context.getResources().getString(R.string.error_password);
                break;
            case Constants.LOGIN_TIMEOUT:
                str = context.getResources().getString(R.string.login_timeout);
                break;
            case Constants.FLAG_NO_PHOTO:
                str = context.getResources().getString(R.string.flag_no_photo);
                break;
            case Constants.REQUEST_TIMEOUT:
                str = context.getResources()
                        .getString(R.string.request_timeout);
                break;
            case Constants.SERVER_OFFLINE:
            case Constants.SERVER_TIMEOUT:
                str = context.getResources().getString(R.string.server_offline);
                break;
            case Constants.NO_NETWORK:
                str = context.getResources().getString(R.string.no_network);
                break;
            case Constants.NAME_OR_PASSWORD_ERROR:
                str = context.getResources()
                        .getString(R.string.login_error_name_or_password);
                break;
            case Constants.NAME_PASSWORD_EMPTY:
                str = context.getResources().getString(R.string.nil_name_pwd);
                break;
            case Constants.JSON_ERROR:
                str = context.getResources().getString(R.string.json_error);
                break;
            case Constants.INVALID_PARAMETER:
                str = context.getResources().getString(R.string.invalid_param);
                break;
            default:
                str = "未知错误";
                break;
        }
        return str;
    }
    
    public static String convert2BaiDuJWD(String jd, String wd) {

        try {

            BasicHttpParams httpParameters = new BasicHttpParams();

            HttpConnectionParams.setConnectionTimeout(httpParameters, 15000);

            HttpConnectionParams.setSoTimeout(httpParameters, 15000);

            DefaultHttpClient client = new DefaultHttpClient(httpParameters);

            HttpClientParams.setCookiePolicy(client.getParams(), CookiePolicy.NETSCAPE);

            String getStr = "http://api.map.baidu.com/geoconv/v1/?coords="
            		        + jd + "," + wd + "&from=3&to=5&output=json&ak=6wETrkSzxlQuO6yS3whfWSSw"; 
            HttpGet get = new HttpGet(getStr);

            HttpResponse resp = client.execute(get);

            return EntityUtils.toString(resp.getEntity());

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    /**
     * 发送请求 ---recommend doing select method
     * @param obj
     * @param action
     * @param handler
     * @param successWhat
     * @param failWhat
     */
    public static void postRequest(final JSONObject obj, final String action,
            final Handler handler, final int successWhat, final int failWhat)
    {
        Thread task = new Thread(new Runnable()
        {

            @Override
            public void run()
            {
                SmartHomeApplication smartHomeApp = SmartHomeApplication.getInstance();
                mContext = smartHomeApp.getApplicationContext();
                if (!smartHomeApp.isNetworkAvailable())
                {
                    //code!=201 当前网络没有联通
                    Message msg = handler.obtainMessage();
                    msg.what = 201;
                    msg.arg1 = Constants.NO_NETWORK;
                    msg.sendToTarget();
                    return;
                }
                String uriAPI = BASE_URL + action;
                HttpPost httpPost = new HttpPost(uriAPI);
                HttpClient httpClient = getHttpClient(action);
                httpPost.addHeader("Content-Type", "application/json");
                httpPost.addHeader("Authorization",
                        Preferences.getInstance(mContext).getToken()); //认证token
                Log.e("REQUEST", "Action:" + uriAPI);
                Log.e("REQUEST", "Data:" + obj.toString());
                if (null != getJessionId())
                {
                    httpPost.setHeader("Cookie", "JSESSIONID=" + getJessionId());
                }
                try
                {
                    if (obj != null)
                    {
                        httpPost.setEntity(new StringEntity(obj.toString(),
                                "UTF-8"));
                    }
                    HttpResponse response = httpClient.execute(httpPost);
                    try
                    {
                        Thread.sleep(100);
                    }
                    catch (InterruptedException e1)
                    {
                        NetStatusListener.mClickflag = false;
                        e1.printStackTrace();
                    }
                    int code = response.getStatusLine().getStatusCode();
                    if (code == 200)
                    {
                        String recvStr = EntityUtils.toString(response.getEntity());

                        JSONObject result = null;
                        int code2 = 0;
                        try
                        {
                            result = new JSONObject(recvStr);
                            if (BASE_URL.equals(BASE_URL_SMART)
                                    || result.has("retcode"))
                            {
                                code2 = result.getInt("retcode");
                            }
                        }
                        catch (JSONException e)
                        {
                            NetStatusListener.mClickflag = false;
                            e.printStackTrace();
                        }
                        if (code2 == 200)
                        {
                            List<Cookie> cookies = ((DefaultHttpClient) httpClient).getCookieStore()
                                    .getCookies();
                            if (!cookies.isEmpty())
                            {
                                for (int i = 0; i < cookies.size(); i++)
                                {
                                    if ("JSESSIONID".equals(cookies.get(i)
                                            .getName()))
                                    {
                                        setJessionId(cookies.get(i).getValue());
                                    }
                                }
                            }

                            Message msg = handler.obtainMessage();
                            msg.what = successWhat;
                            msg.obj = recvStr;
                            msg.sendToTarget();
                        }
                        else if (Constants.LOGIN_V11_ACTION.equals(action)
                                && Constants.LOGIN_THIRD_FIRST_SUCCESS == code)
                        {
                            List<Cookie> cookies = ((DefaultHttpClient) httpClient).getCookieStore()
                                    .getCookies();
                            if (!cookies.isEmpty())
                            {
                                for (int i = 0; i < cookies.size(); i++)
                                {
                                    if ("JSESSIONID".equals(cookies.get(i)
                                            .getName()))
                                    {
                                        setJessionId(cookies.get(i).getValue());
                                    }
                                }
                            }
                        }
                        else
                        {
                            try
                            {
                                //code!=200
                                Message msg = handler.obtainMessage();
                                msg.what = failWhat;
                                msg.arg1 = code2;
                                if ((result !=null) && result.has("retmsg")
                                        && !result.isNull("retmsg"))
                                {
                                    msg.obj = result.getString("retmsg");
                                }
                                msg.sendToTarget();
                            }
                            catch (JSONException e)
                            {
                                NetStatusListener.mClickflag = false;
                                e.printStackTrace();
                            }
                        }
                        Log.e("REQUEST", "Result:" + recvStr);
                    }
                    else
                    {
                        //code!=200
                        Message msg = handler.obtainMessage();
                        msg.what = failWhat;
                        msg.arg1 = code;
                        msg.sendToTarget();
                    }

                }
                catch (UnsupportedEncodingException e)
                {
                    NetStatusListener.mClickflag = false;
                    e.printStackTrace();
                }
                catch (ClientProtocolException e)
                {
                    NetStatusListener.mClickflag = false;
                    e.printStackTrace();
                }
                catch (IOException e)
                {
                    NetStatusListener.mClickflag = false;
                    e.printStackTrace();
                    ExceptionUtil.catchIOException(e, mContext);
                }
                initUrl(BASE_URL_SMART_TYPE);
            }
        });
        executeMyTask(task);
    }
    
    public static void postRequest(final JSONObject obj, final String action,
            final Handler handler, final int successWhat, final int failWhat, final String tag)
    {
        Thread task = new Thread(new Runnable()
        {

            @Override
            public void run()
            {
                SmartHomeApplication smartHomeApp = SmartHomeApplication.getInstance();
                mContext = smartHomeApp.getApplicationContext();
                if (!smartHomeApp.isNetworkAvailable())
                {
                    //code!=201 当前网络没有联通
                    Message msg = handler.obtainMessage();
                    msg.what = 201;
                    msg.arg1 = Constants.NO_NETWORK;
                    msg.sendToTarget();
                    return;
                }
                String uriAPI = BASE_URL + action;
                HttpPost httpPost = new HttpPost(uriAPI);
                HttpClient httpClient = getHttpClient(action);
                httpPost.addHeader("Content-Type", "application/json");
                httpPost.addHeader("Authorization",
                        Preferences.getInstance(mContext).getToken()); //认证token

                Log.e("REQUEST", "Action:" + uriAPI);
                Log.e("REQUEST", "Data:" + obj.toString());
                if (null != getJessionId())
                {
                    httpPost.setHeader("Cookie", "JSESSIONID=" + getJessionId());
                }
                try
                {
                    if (obj != null)
                    {
                        httpPost.setEntity(new StringEntity(obj.toString(),
                                "UTF-8"));
                    }
                    HttpResponse response = httpClient.execute(httpPost);
                    try
                    {
                        Thread.sleep(100);
                    }
                    catch (InterruptedException e1)
                    {
                        NetStatusListener.mClickflag = false;
                        e1.printStackTrace();
                    }
                    int code = response.getStatusLine().getStatusCode();
                    if (code == 200)
                    {
                        String recvStr = EntityUtils.toString(response.getEntity());
                        JSONObject result = null;
                        try
                        {
                            result = new JSONObject(recvStr);
                            if (BASE_URL.equals(BASE_URL_SMART))
                            {
                                code = result.getInt("retcode");
                            }
                        }
                        catch (JSONException e)
                        {
                            NetStatusListener.mClickflag = false;
                            e.printStackTrace();
                        }
                        if (code == 200)
                        {
                            List<Cookie> cookies = ((DefaultHttpClient) httpClient).getCookieStore()
                                    .getCookies();
                            if (!cookies.isEmpty())
                            {
                                for (int i = 0; i < cookies.size(); i++)
                                {                                   
                                    if ("JSESSIONID".equals(cookies.get(i)
                                            .getName()))
                                    {
                                        setJessionId(cookies.get(i).getValue());
                                    }
                                }
                            }
                            Message msg = handler.obtainMessage();
                            Bundle urlData = new Bundle();
                            urlData.putString("tag", tag);
                            msg.what = successWhat;
                            msg.obj = recvStr;
                            msg.setData(urlData);
                            msg.sendToTarget();
                        }
                        else if (Constants.LOGIN_V11_ACTION.equals(action)
                                && Constants.LOGIN_THIRD_FIRST_SUCCESS == code)
                        {
                            List<Cookie> cookies = ((DefaultHttpClient) httpClient).getCookieStore()
                                    .getCookies();
                            if (!cookies.isEmpty())
                            {
                                for (int i = 0; i < cookies.size(); i++)
                                {                                    
                                    if ("JSESSIONID".equals(cookies.get(i)
                                            .getName()))
                                    {
                                        setJessionId(cookies.get(i).getValue());
                                    }
                                }
                            }
                        }
                        else
                        {
                            try
                            {
                                //code!=200
                                Message msg = handler.obtainMessage();
                                msg.what = failWhat;
                                msg.arg1 = code;
                                if (result.has("retmsg")
                                        && !result.isNull("retmsg"))
                                {
                                    msg.obj = result.getString("retmsg");
                                }
                                msg.sendToTarget();
                            }
                            catch (JSONException e)
                            {
                                NetStatusListener.mClickflag = false;
                                e.printStackTrace();
                            }
                        }
                        Log.e("REQUEST", "Result:" + recvStr);
                    }
                    else
                    {
                        //code!=200
                        Message msg = handler.obtainMessage();
                        msg.what = failWhat;
                        msg.arg1 = code;
                        msg.sendToTarget();
                    }

                }
                catch (UnsupportedEncodingException e)
                {
                    NetStatusListener.mClickflag = false;
                    e.printStackTrace();
                }
                catch (ClientProtocolException e)
                {
                    NetStatusListener.mClickflag = false;
                    e.printStackTrace();
                }
                catch (IOException e)
                {
                    NetStatusListener.mClickflag = false;
                    e.printStackTrace();
                    ExceptionUtil.catchIOException(e, mContext);
                }
                initUrl(BASE_URL_SMART_TYPE);
            }
        });
        executeMyTask(task);
    }

    private static List<Thread> taskList = new ArrayList<Thread>();

    private static void executeMyTask(Thread thread)
    {
        taskList.add(thread);
        synchronized (taskList)
        {
            if (!taskList.isEmpty())
            {
                Thread task = taskList.get(0);
                taskList.remove(0);
                task.start();
            }
        }
    }

    /**
     * 发送请求 ---recommend doing insert/delete/update method
     * @param obj
     * @param action
     */
    public static RequestResult postRequest(final JSONObject obj,
            final String action)
    {
        final RequestResult result = new RequestResult();
        new Thread(new Runnable()
        {

            @Override
            public void run()
            {
                String uriAPI = BASE_URL + action;
                HttpPost httpPost = new HttpPost(uriAPI);
                HttpClient httpClient = getHttpClient(action);
                httpPost.addHeader("Content-Type", "application/json");
                httpPost.addHeader("Authorization",
                        Preferences.getInstance(mContext).getToken()); //认证token
                Log.e("REQUEST", "Action:" + uriAPI);
                Log.e("REQUEST", "Data:" + obj.toString());
                if (null != getJessionId())
                {
                    httpPost.setHeader("Cookie", "JSESSIONID=" + getJessionId());
                }
                try
                {
                    if (obj != null)
                    {
                        httpPost.setEntity(new StringEntity(obj.toString(),
                                "UTF-8"));
                    }
                    HttpResponse response = httpClient.execute(httpPost);
                    int code = response.getStatusLine().getStatusCode();
                    if (code == 200)
                    {
                        String recvStr = EntityUtils.toString(response.getEntity());
                        try
                        {
                            JSONObject result = new JSONObject(recvStr);
                            if (BASE_URL.equals(BASE_URL_SMART))
                            {
                                code = result.getInt("retcode");
                            }
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                        if (code == 200)
                        {
                            result.setResult(recvStr);
                            result.setCode(code);

                            List<Cookie> cookies = ((DefaultHttpClient) httpClient).getCookieStore()
                                    .getCookies();
                            if (!cookies.isEmpty())
                            {
                                for (int i = 0; i < cookies.size(); i++)
                                {
                                    if ("JSESSIONID".equals(cookies.get(i)
                                            .getName()))
                                    {
                                        setJessionId(cookies.get(i).getValue());
                                    }
                                }
                            }

                        }
                        else if (Constants.LOGIN_V11_ACTION.equals(action)
                                && Constants.LOGIN_THIRD_FIRST_SUCCESS == code)
                        {
                            List<Cookie> cookies = ((DefaultHttpClient) httpClient).getCookieStore()
                                    .getCookies();
                            if (!cookies.isEmpty())
                            {
                                for (int i = 0; i < cookies.size(); i++)
                                {
                                    if ("JSESSIONID".equals(cookies.get(i)
                                            .getName()))
                                    {
                                        setJessionId(cookies.get(i).getValue());
                                    }
                                }
                            }
                        }
                        Log.e("REQUEST", "Result:" + recvStr);
                    }
                    else
                    {
                        result.setCode(code);
                    }
                }
                catch (UnsupportedEncodingException e)
                {
                    e.printStackTrace();
                }
                catch (ClientProtocolException e)
                {
                    e.printStackTrace();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
                initUrl(BASE_URL_SMART_TYPE);
            }
        }).start();
        return result;
    }

    /**
     * 发送客户端请求
     * @param obj
     * @param action
     * @param result
     * @param context
     */
    public static void postSmartRequest(JSONObject obj, String action,
            RequestResult result, Context context)
    {
        mContext = context;
        String uriAPI = BASE_URL_SMART + action;
        HttpPost httpPost = new HttpPost(uriAPI);
        HttpClient httpClient = getHttpClient(action);
        httpPost.setHeader("Content-Type", "application/json; charset=utf-8");
        httpPost.addHeader("Authorization", Preferences.getInstance(mContext)
                .getToken()); //认证token
        Log.e("REQUEST", "Action:" + uriAPI);
        Log.e("REQUEST", "Data:" + obj.toString());
        if (null != getJessionId())
        {
            httpPost.setHeader("Cookie", "JSESSIONID=" + getJessionId());
        }
        try
        {
            if (obj != null)
            {
                httpPost.setEntity(new StringEntity(obj.toString(), "UTF-8"));
            }
            HttpResponse response = httpClient.execute(httpPost);
            int code = response.getStatusLine().getStatusCode();
            if (code == 200)
            {
                String recvStr = EntityUtils.toString(response.getEntity());
                try
                {
                    JSONObject resultObj = new JSONObject(recvStr);
                    code = resultObj.getInt("retcode");
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                    result.setCode(Constants.JSON_ERROR);
                }
                if (code == 200)
                {
                    result.setResult(recvStr);
                    result.setCode(code);

                    List<Cookie> cookies = ((DefaultHttpClient) httpClient).getCookieStore()
                            .getCookies();
                    if (!cookies.isEmpty())
                    {
                        for (int i = 0; i < cookies.size(); i++)
                        {
                            if ("JSESSIONID".equals(cookies.get(i).getName()))
                            {
                                setJessionId(cookies.get(i).getValue());
                            }
                        }
                    }
                }
                else
                {
                    result.setResult(recvStr);
                    result.setCode(code);
                }
                Log.e("REQUEST", "Result:" + recvStr);
            }
            else
            {
                result.setCode(code);
            }
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();           
        }
        catch (ClientProtocolException e)
        {
            e.printStackTrace();           
        }
        catch (IOException e)
        {
            e.printStackTrace();            
        }
    }

    /**
     * 发送请求 --- recommend doing photo stuff
     * @param obj
     * @param action
     * @param result
     * @param context
     */
    public static void postRequest(JSONObject obj, String action,
            RequestResult result, Context context)
    {
        mContext = context;
        String uriAPI = BASE_URL + action;
        HttpPost httpPost = new HttpPost(uriAPI);
        HttpClient httpClient = getHttpClient(action);
        httpPost.setHeader("Content-Type", "application/json; charset=utf-8");
        httpPost.addHeader("Authorization", Preferences.getInstance(mContext)
                .getToken()); //认证token

        Log.e("REQUEST", "Action:" + uriAPI);
        Log.e("REQUEST", "Data:" + obj.toString());
        if (null != getJessionId())
        {
            httpPost.setHeader("Cookie", "JSESSIONID=" + getJessionId());
        }
        try
        {
            if (obj != null)
            {
                httpPost.setEntity(new StringEntity(obj.toString(), "UTF-8"));
            }
            HttpResponse response = httpClient.execute(httpPost);
            int code = response.getStatusLine().getStatusCode();
            if (code == 200)
            {
                String recvStr = EntityUtils.toString(response.getEntity());
                try
                {
                    JSONObject resultObj = new JSONObject(recvStr);
                    if (BASE_URL.equals(BASE_URL_SMART))
                    {
                        code = resultObj.getInt("retcode");
                    }
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
                if (code == 200)
                {
                    result.setResult(recvStr);
                    result.setCode(code);

                    List<Cookie> cookies = ((DefaultHttpClient) httpClient).getCookieStore()
                            .getCookies();
                    if (!cookies.isEmpty())
                    {
                        for (int i = 0; i < cookies.size(); i++)
                        {
                            TLog.Log("zxl---httputil---cook--->"
                                    + cookies.get(i).getName() + ":"
                                    + cookies.get(i).getValue());
                            if ("JSESSIONID".equals(cookies.get(i).getName()))
                            {
                                setJessionId(cookies.get(i).getValue());
                            }
                        }
                    }
                }
                else
                {
                    result.setResult(recvStr);
                    result.setCode(code);
                }
                Log.e("REQUEST", "Result:" + recvStr);
            }
            else
            {
                result.setCode(code);
            }
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        catch (ClientProtocolException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        initUrl(BASE_URL_SMART_TYPE);
    }

    /**
     * 上传照片
     * @param action
     * @param data
     * @param context
     * @return
     */
    public static int uploadPhoto(String action, byte[] data, String userId)
    {
        int code = 0;
        try
        {
            URL url = new URL(BASE_URL + action);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestMethod("POST");
            connection.setConnectTimeout(Constants.TIMEOUT_CONNECTION);
            connection.setReadTimeout(Constants.READ_TIMEOUT);

            connection.addRequestProperty("userId", userId);
            connection.setRequestProperty("Cookie", "JSESSIONID="
                    + getJessionId());
            connection.connect();
            OutputStream os = connection.getOutputStream();

            TLog.Log("字节长度" + data.length);
            os.write(data);
            os.flush();
            os.close();
            TLog.Log("" + connection.getResponseCode());
            code = connection.getResponseCode();
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));
            String lines;
            while ((lines = reader.readLine()) != null)
            {
                TLog.Log(lines);
            }
            reader.close();
            connection.disconnect();
            return code;
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return code;
    }

    /**
     * 载入图片
     * @param action
     * @param context
     * @return
     */
    public static byte[] loadPhoto(String action)
    {
        byte[] data;
        try
        {
            URL url = new URL(BASE_URL + action);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(Constants.TIMEOUT_CONNECTION);
            connection.setReadTimeout(Constants.READ_TIMEOUT);
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestMethod("POST");

            connection.setRequestProperty("Cookie", "JSESSIONID="
                    + getJessionId());
            connection.connect();
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            InputStream inputStream = connection.getInputStream();
            byte[] buffer = new byte[4096];
            int len = 0;
            while ((len = inputStream.read(buffer)) != -1)
            {
                bos.write(buffer, 0, len);
            }
            data = bos.toByteArray();
            inputStream.close();
            bos.close();
            connection.disconnect();
            return data;
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
        }
        catch (ProtocolException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 载入图片
     * @param action
     * @param userId
     * @param context
     * @return
     */
    public static byte[] loadPhoto(String action, String userId, Context context)
    {
        mContext = context;
        byte[] data;
        try
        {
            URL url = new URL(BASE_URL + action);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(Constants.TIMEOUT_CONNECTION);
            connection.setReadTimeout(Constants.READ_TIMEOUT);
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestMethod("POST");

            connection.setRequestProperty("Cookie", "JSESSIONID="
                    + getJessionId());

            StringBuffer params = new StringBuffer();
            params.append("{\"userId\"").append(":").append(userId + "}");
            byte[] bypes = params.toString().getBytes();
            connection.getOutputStream().write(bypes);

            connection.connect();
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            InputStream inputStream = connection.getInputStream();
            byte[] buffer = new byte[4096];
            int len = 0;
            while ((len = inputStream.read(buffer)) != -1)
            {
                bos.write(buffer, 0, len);
            }
            data = bos.toByteArray();
            inputStream.close();
            bos.close();
            connection.disconnect();
            return data;
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
        }
        catch (ProtocolException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 批量载入图片
     * @param action
     * @param key
     * @param context
     * @return
     */
    public static byte[] loadPhotoByList(String action, String key,
            Context context)
    {
        mContext = context;
        byte[] data;
        try
        {
            URL url = new URL(BASE_URL + action);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(Constants.TIMEOUT_CONNECTION);
            connection.setReadTimeout(Constants.READ_TIMEOUT);
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Cookie", "JSESSIONID="
                    + getJessionId());

            StringBuffer params = new StringBuffer();
            params.append("{\"userId\"").append(":").append(key + "}");
            byte[] bypes = params.toString().getBytes();
            connection.getOutputStream().write(bypes);

            connection.connect();
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            InputStream inputStream = connection.getInputStream();
            byte[] buffer = new byte[4096];
            int len = 0;
            while ((len = inputStream.read(buffer)) != -1)
            {
                bos.write(buffer, 0, len);
            }
            data = bos.toByteArray();
            inputStream.close();
            bos.close();
            connection.disconnect();
            return data;
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
        }
        catch (ProtocolException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }
    
    public static final String SUCCESS = "1";

    public static final String FAILURE = "0";

    public static int upLoadFile(String userId, File file, String urlServer,
            final Handler handlerfinal, int successWhat, final int failWhat)
            throws IOException, JSONException
    {

        SmartHomeApplication smartHomeApp = SmartHomeApplication.getInstance();
        if (!smartHomeApp.isNetworkAvailable())
        {
            //code!=201 当前网络没有联通
            Message msg = handlerfinal.obtainMessage();
            msg.what = 201;
            msg.arg1 = Constants.NO_NETWORK;
            msg.sendToTarget();
            return 201;
        }

        HttpClient httpclient = new DefaultHttpClient();
        //设置通信协议版本      
        httpclient.getParams()
                .setParameter(CoreProtocolPNames.PROTOCOL_VERSION,
                        HttpVersion.HTTP_1_1);

        HttpPost httppost = new HttpPost(urlServer);
        httppost.setHeader("Authorization", Preferences.getInstance(mContext)
                .getToken()); //认证token
        httppost.setHeader("userId", userId);

        MultipartEntity mpEntity = new MultipartEntity();
        //文件传输       
        ContentBody cbFile = new FileBody(file);
        mpEntity.addPart("userfile", cbFile); // <input type="file" name="userfile" />  对应的

        httppost.setEntity(mpEntity);
        System.out.println("executing request " + httppost.getRequestLine());

        HttpResponse response = httpclient.execute(httppost);
        int code = response.getStatusLine().getStatusCode();

        HttpEntity resEntity = response.getEntity();
        String recvStr = EntityUtils.toString(resEntity);

        if (code == 200)
        {
            Message msg = handlerfinal.obtainMessage();
            msg.what = successWhat;
            msg.obj = recvStr;
            msg.sendToTarget();
        }
        if (resEntity != null)
        {
            resEntity.consumeContent();
        }

        httpclient.getConnectionManager().shutdown();
        return code;
    }

    public static String upLoadFile(String userId, File file, String urlServer,String strID)
            throws IOException
    {
        int code = Constants.UNKNOW_RESULT;

        String recvStr = "";
        HttpClient httpclient = new DefaultHttpClient();
        //设置通信协议版本
        httpclient.getParams()
                .setParameter(CoreProtocolPNames.PROTOCOL_VERSION,
                        HttpVersion.HTTP_1_1);

        HttpPost httppost = new HttpPost(urlServer);
        httppost.setHeader("Authorization", Preferences.getInstance(mContext)
                .getToken()); //认证token
        httppost.setHeader(strID, userId);
        //httppost.setHeader(strID"userId", userId);
        if (null != getJessionId())
        {
            httppost.setHeader("Cookie", "JSESSIONID=" + getJessionId());
        }

        MultipartEntity mpEntity = new MultipartEntity();
        //文件传输
        ContentBody cbFile = new FileBody(file);
        mpEntity.addPart("userfile", cbFile);

        httppost.setEntity(mpEntity);
        TLog.Log("executing request " + httppost.getRequestLine());

        HttpResponse response = httpclient.execute(httppost);
        code = response.getStatusLine().getStatusCode();

        HttpEntity resEntity = response.getEntity();

        TLog.Log(response.getStatusLine().toString());
        /*if (resEntity != null)
        {
            resEntity.consumeContent();
        }*/
        if (code == Constants.SC_OK)
        {
            recvStr = EntityUtils.toString(resEntity, HTTP.UTF_8);
        }
        else
        {
            recvStr = String.valueOf(code);
        }
        httpclient.getConnectionManager().shutdown();
        return recvStr;
    }    
    
    public static String addReplyFamilyMsg(Map<?, ?> paramMap, File file, String urlServer)
            throws IOException
    {
    	
        String recvStr = "";
        HttpClient httpclient = new DefaultHttpClient();
        SmartHomeApplication smartHomeApp = SmartHomeApplication.getInstance();
        mContext = smartHomeApp.getApplicationContext();
        //设置通信协议版本
        httpclient.getParams()
                .setParameter(CoreProtocolPNames.PROTOCOL_VERSION,
                        HttpVersion.HTTP_1_1);

        HttpPost httppost = new HttpPost(urlServer);
        Set<?> set = paramMap.entrySet();
		Iterator<?> i = set.iterator();
		while(i.hasNext()){
		     Map.Entry<String, String> entry1=(Map.Entry<String, String>)i.next();		     
		     httppost.setHeader("Cookie", "JSESSIONID=" + getJessionId());
		     httppost.setHeader(entry1.getKey(), paramMap.get(entry1.getKey())+"");
		}
		
		if (file != null){

	        MultipartEntity mpEntity = new MultipartEntity();
	        //文件传输
	        ContentBody cbFile = new FileBody(file);
	        mpEntity.addPart("userfile", cbFile);
	
	        httppost.setEntity(mpEntity);
		}

        HttpResponse response = httpclient.execute(httppost);
        HttpEntity resEntity = response.getEntity();
        recvStr = EntityUtils.toString(resEntity, HTTP.UTF_8);
        httpclient.getConnectionManager().shutdown();
        return recvStr;
    }

    public static boolean isNetworkAvailable(Context context)
    {
        mContext = context;
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null)
        {

        }
        else
        {
            NetworkInfo[] info = cm.getAllNetworkInfo();
            if (info != null)
            {
                for (int i = 0; i < info.length; i++)
                {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED)
                    {
                        return true;
                    }
                }
            }
        }
        return false;
    }


}
