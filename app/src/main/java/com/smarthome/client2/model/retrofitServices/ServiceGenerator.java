package com.smarthome.client2.model.retrofitServices;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import android.content.Context;

import com.smarthome.client2.SmartHomeApplication;
import com.smarthome.client2.common.TLog;
import com.smarthome.client2.config.Preferences;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.logging.HttpLoggingInterceptor;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.Request;

import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;


/**
 * Created by wenhuanchen on 11/19/15.
 */

public class ServiceGenerator {

    private String BASE_URL_SMART = "http://papp.ejcom.cn/";
    private Context ctx;
    private static ServiceGenerator instance = null;

    private ServiceGenerator() {
    }

    private OkHttpClient httpClient = new OkHttpClient();
    private HttpLoggingInterceptor logging = new HttpLoggingInterceptor();

    //静态工厂方法
    public static ServiceGenerator getInstance(Context ctx) {
        if (instance == null) {
            synchronized (ServiceGenerator.class) {
                if (instance == null) {

                    instance = new ServiceGenerator(ctx);
                }
            }
        }
        return instance;
    }

    private ServiceGenerator(Context ctx) {
        this.ctx = ctx.getApplicationContext();
    }

    private Retrofit retrofit = null;

    public <S> S createService(Class<S> serviceClass) {
        if (retrofit == null) {
            log("createService" + BASE_URL_SMART);
            setClientPara();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL_SMART)
                    .client(httpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .build();
        }

        return retrofit.create(serviceClass);
    }

    public ApiService getApiService() {
        return createService(ApiService.class);
    }

    public void setBaseUrlSmart(String urlSmart) {
        log("setBaseUrlSmart" + urlSmart);
        if (!urlSmart.equalsIgnoreCase(this.BASE_URL_SMART)) {
            setClientPara();
            retrofit = new Retrofit.Builder()
                    .baseUrl(urlSmart)
                    .client(httpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .build();
        }
        this.BASE_URL_SMART = urlSmart;
    }

    public String getBaseUrlSmart() {
        return BASE_URL_SMART;
    }


    private void setClientPara() {
        httpClient.setConnectTimeout(10, TimeUnit.MINUTES);
        httpClient.setReadTimeout(10, TimeUnit.MINUTES);
        // set your desired log level
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        // add logging as last interceptor
        httpClient.interceptors().add(logging);  // <-- this is the important line!

        httpClient.interceptors().add(new Interceptor() {
            @Override
            public com.squareup.okhttp.Response intercept(Chain chain) throws IOException {
                String token = Preferences.getInstance(ctx).getToken();
                String sessionId = "JSESSIONID=" + Preferences.getInstance(ctx).getJessionId();
                Request newRequest = chain.request().newBuilder()
                        .addHeader("Authorization", token)
                        .addHeader("Cookie", sessionId)
                        .build();
                return chain.proceed(newRequest);

            }
        }

        );
    }

    //LoginService loginService = retrofit.create(LoginService.class);
    private static final boolean DEBUG = SmartHomeApplication.PRINT_LOG;

    private static void log(String msg) {
        if (DEBUG) {
            TLog.Log(msg);
        }
    }
}
