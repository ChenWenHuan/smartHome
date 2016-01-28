package com.smarthome.client2.familySchool.utils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.os.Handler;

import com.smarthome.client2.SmartHomeApplication;
import com.smarthome.client2.config.Preferences;
import com.smarthome.client2.util.HttpUtil;
import com.smarthome.client2.util.LoginUtil;

public class MyHttpUtil
{

    private final static String TAG = MyHttpUtil.class.getSimpleName();

    public static void post(final String action, final HttpJson params,
            final Handler handler)
    {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                if (action == null || handler == null)
                {
                    return;
                }
                handler.obtainMessage(FsConstants.HTTP_START).sendToTarget();
                String url = HttpUtil.BASE_URL_SMART + action;
                try
                {
                    if (!SmartHomeApplication.getInstance()
                            .isNetworkAvailable())
                    {
                        handler.obtainMessage(FsConstants.HTTP_FAILURE)
                                .sendToTarget();
                        LogUtil.e(TAG, "网络不可用");
                        return;
                    }
                    HttpPost httpRequest = new HttpPost(url);
                    if (params != null)
                    {
                        StringEntity se = (StringEntity) params.getEntity();
                        httpRequest.setEntity(se);
                    }
                    BasicHttpParams httpParams = new BasicHttpParams();
                    // 从连接池中取连接的超时时间，设置为1秒
                    ConnManagerParams.setTimeout(httpParams, 10000);
                    ConnManagerParams.setMaxConnectionsPerRoute(httpParams,
                            new ConnPerRouteBean(10));
                    ConnManagerParams.setMaxTotalConnections(httpParams, 10);
                    // 读响应数据的超时时间
                    HttpConnectionParams.setSoTimeout(httpParams, 10000);
                    HttpConnectionParams.setConnectionTimeout(httpParams, 10000);
                    HttpConnectionParams.setTcpNoDelay(httpParams, true);
                    HttpConnectionParams.setSocketBufferSize(httpParams, 8192);
                    HttpProtocolParams.setVersion(httpParams,
                            HttpVersion.HTTP_1_1);
                    // 设置请求参数
                    httpRequest.setHeader("Accept-Charset", HTTP.UTF_8);
                    httpRequest.addHeader("Authorization",
                            Preferences.getInstance(SmartHomeApplication.getInstance()
                                    .getApplicationContext())
                                    .getToken());
                    if (HttpUtil.getJessionId() != null)
                    {
                        httpRequest.setHeader("Cookie", "JSESSIONID="
                                + HttpUtil.getJessionId());
                    }
                    httpRequest.setParams(httpParams);
                    // 取得默认的HttpClient
                    HttpClient httpclient = new DefaultHttpClient();
                    // 取得HttpResponse
                    HttpResponse httpResponse = httpclient.execute(httpRequest);
                    // 请求成功
                    int statusCode = httpResponse.getStatusLine()
                            .getStatusCode();
                    // 取得返回的字符串
                    HttpEntity mHttpEntity = httpResponse.getEntity();
                    String content = EntityUtils.toString(mHttpEntity,
                            HTTP.UTF_8);
                    LogUtil.i(TAG, statusCode + action + content);
                    if (statusCode == HttpStatus.SC_OK)
                    {
                        handler.obtainMessage(FsConstants.HTTP_SUCCESS, content)
                                .sendToTarget();
                    }
                    else
                    {
                        handler.obtainMessage(FsConstants.HTTP_FAILURE, content)
                                .sendToTarget();
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    LogUtil.e(TAG, "Exception");
                    handler.obtainMessage(FsConstants.HTTP_FAILURE)
                            .sendToTarget();
                }
                finally
                {
                    handler.obtainMessage(FsConstants.HTTP_FINISH)
                            .sendToTarget();
                }
            }
        }).start();

    }

    public static String post(final String url, final HttpJson params)
    {
        try
        {
            if (!SmartHomeApplication.getInstance().isNetworkAvailable())
            {
                LogUtil.e(TAG, "网络不可用");
                return null;
            }
            HttpPost httpRequest = new HttpPost(url);
            if (params != null)
            {
                StringEntity se = (StringEntity) params.getEntity();
                httpRequest.setEntity(se);
            }
            BasicHttpParams httpParams = new BasicHttpParams();
            // 从连接池中取连接的超时时间，设置为1秒
            ConnManagerParams.setTimeout(httpParams, 10000);
            ConnManagerParams.setMaxConnectionsPerRoute(httpParams,
                    new ConnPerRouteBean(10));
            ConnManagerParams.setMaxTotalConnections(httpParams, 10);
            // 读响应数据的超时时间
            HttpConnectionParams.setSoTimeout(httpParams, 10000);
            HttpConnectionParams.setConnectionTimeout(httpParams, 10000);
            HttpConnectionParams.setTcpNoDelay(httpParams, true);
            HttpConnectionParams.setSocketBufferSize(httpParams, 8192);
            HttpProtocolParams.setVersion(httpParams, HttpVersion.HTTP_1_1);
            // 设置请求参数
            httpRequest.setHeader("Accept-Charset", HTTP.UTF_8);
            String token = Preferences.getInstance(SmartHomeApplication.getInstance()
                    .getApplicationContext())
                    .getToken();
            httpRequest.addHeader("Authorization", token);
            httpRequest.setHeader("Cookie",
                    "JSESSIONID=" + HttpUtil.getJessionId());
            httpRequest.setParams(httpParams);
            // 取得默认的HttpClient
            HttpClient httpclient = new DefaultHttpClient();
            // 取得HttpResponse
            HttpResponse httpResponse = httpclient.execute(httpRequest);
            // 请求成功
            int statusCode = httpResponse.getStatusLine().getStatusCode();
            // 取得返回的字符串
            HttpEntity mHttpEntity = httpResponse.getEntity();
            String content = EntityUtils.toString(mHttpEntity, HTTP.UTF_8);
            LogUtil.i(TAG, statusCode + "@#$%" + content);
            if (statusCode == HttpStatus.SC_OK)
            {
                return content;
            }
            else
            {
                return null;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            LogUtil.e(TAG, "Exception");
            return null;
        }
        finally
        {
        }
    }

}
