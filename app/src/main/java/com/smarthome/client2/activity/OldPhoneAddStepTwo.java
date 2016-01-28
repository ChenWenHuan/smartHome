package com.smarthome.client2.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.smarthome.client2.R;
import com.smarthome.client2.SmartHomeApplication;
import com.smarthome.client2.common.TLog;
import com.smarthome.client2.model.addOldPhoneMem.AddOldPhoneReqParameter;
import com.smarthome.client2.model.addOldPhoneMem.AddOldPhoneResult;
import com.smarthome.client2.model.getRegisterCode.GetRegisterCodeResult;
import com.smarthome.client2.model.getRegisterCode.GetRegisterVerifyCode;
import com.smarthome.client2.model.retrofitServices.AddOldPhoneService;
import com.smarthome.client2.model.retrofitServices.GetRegisterCodeService;
import com.smarthome.client2.model.retrofitServices.ServiceGenerator;
import com.smarthome.client2.util.TopBarUtils;
import com.smarthome.client2.view.CustomActionBar;
import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.squareup.okhttp.ResponseBody;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.HttpException;
import retrofit.Response;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class OldPhoneAddStepTwo extends Activity {

    @Bind(R.id.fl_header)
    FrameLayout flHeader;

    @Bind(R.id.et_old_phone_number)
    EditText etOldPhoneNumber;

    @Bind(R.id.et_old_phone_verify)
    EditText etOldPhoneVerify;

    @Bind(R.id.ll_verify)
    LinearLayout llVerify;

    @Bind(R.id.bt_old_phone_binding)
    Button btOldPhoneBinding;

    @Bind(R.id.bt_old_phone_verify_code)
    Button btOldPhoneVerifyCode;

    @Bind(R.id.ll_input_phone_number)
    LinearLayout llInputPhoneNumber;

    private CustomActionBar actionBar;
    private String mOldPhoneIMEI;
    private String oldPhoneNumber;
    private String mFamilyId;
    private ProgressDialog mProgressBar;
    private String oldPhoneVerifyCodeInput; // 输入的验证码
    public static final String ADD_OLD_PHONE_SUCCESS = "com.smarthome.client2.activity.OldPhoneStepTwo.sucess";

    private CompositeSubscription mCompositeSubscription
            = new CompositeSubscription();

    private BroadcastReceiver addSucessRevcer = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            OldPhoneAddStepTwo.this.finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.old_phone_add_step_two_main);
        ButterKnife.bind(this);

        mOldPhoneIMEI = this.getIntent().getStringExtra("imei");
        mFamilyId = this.getIntent().getStringExtra("familyId");
        addTopBarToHead();

        mProgressBar = new ProgressDialog(OldPhoneAddStepTwo.this);
        mProgressBar.setCanceledOnTouchOutside(false);
        mProgressBar.setOnKeyListener((arg0, keyCode, event) -> {

            return false;
        });

        initUI();

        registerReceiver(addSucessRevcer, new IntentFilter(WatchAddConfirm.ADD_WATCH_SUCCESS));

        mCompositeSubscription.add(
                RxTextView.textChanges(etOldPhoneNumber)
                        .map(CharSequence::toString)
                        .filter(s -> s.length() >= 10)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(s1 -> setVerifyCodeButton(s1))
        );

        mCompositeSubscription.add(
                RxView.clicks(btOldPhoneVerifyCode)
                        .switchMap((v) -> this.getRegCodeRawBody()
                                .doOnSubscribe(() -> onStartGetVerifyShow())
                                .subscribeOn(AndroidSchedulers.mainThread())
                                .doOnError(throwable -> showVerifyError(throwable))
                                .onErrorResumeNext(Observable.empty())
                        )
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::updateVerifyRepos, this::showVerifyError)
        );

        mCompositeSubscription.add(
                RxTextView.textChanges(etOldPhoneVerify)
                        .map(CharSequence::toString)
                        .filter(s -> s.length() >= 4)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(s1 -> setBindingButton(s1))
        );

        mCompositeSubscription.add(
                RxView.clicks(btOldPhoneBinding)
                        .switchMap((v) -> this.bindingOldPhoneRawResp()
                                .doOnSubscribe(() -> onStartBindingShow())
                                .subscribeOn(AndroidSchedulers.mainThread())
                                .doOnError(throwable -> showBindingError(throwable))
                                .onErrorResumeNext(Observable.empty())
                        )
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::bindingSuccessRepos, this::showBindingError)
        );
        onEndingShow();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCompositeSubscription.unsubscribe();
        ButterKnife.unbind(this);
        unregisterReceiver(addSucessRevcer);
    }

    private void addTopBarToHead() {
        actionBar = TopBarUtils.createCustomActionBar(getApplicationContext(),
                R.drawable.btn_back_selector,
                v -> finish(),
                "老人机绑定-步骤二",
                null,
                null);

        flHeader.addView(actionBar);
    }

    private void initUI() {
        btOldPhoneVerifyCode.setBackground(getResources().getDrawable(R.drawable.btn_gray_shape));
        btOldPhoneVerifyCode.setEnabled(false);
        btOldPhoneBinding.setEnabled(false);
    }

    private boolean parseResp(String body) {
        JSONObject json = null;
        int retcode = 0;
        String retmsg = "";
        try {
            json = new JSONObject(body);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (json.has("retcode")) {

            try {
                retcode = json.getInt("retcode");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (json.has("retmsg")) {
            try {
                retmsg = json.getString("retmsg");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (retcode != 200) {
            Toast.makeText(OldPhoneAddStepTwo.this,
                    retmsg,
                    Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void updateVerifyRepos(Response<ResponseBody> repos) {
        String body = "{\"retcode\":200,\"retmsg\":\"\",\"data\":\"528734\"}";
        try {
            body = repos.body().string();
            log("updateVerifyReposRawResp " + body);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (parseResp(body)) {
            llInputPhoneNumber.setVisibility(View.GONE);
            btOldPhoneVerifyCode.setVisibility(View.GONE);
            llVerify.setVisibility(View.VISIBLE);
            btOldPhoneBinding.setBackground(getResources().getDrawable(R.drawable.btn_gray_shape));
            btOldPhoneBinding.setVisibility(View.VISIBLE);
            btOldPhoneBinding.setEnabled(false);
        }
        onEndingShow();
    }

    protected Observable<Response<ResponseBody>> getRegCodeRawBody() {
        log(" getValCode Observable");
        String phone1 = oldPhoneNumber;
        GetRegisterVerifyCode para = new GetRegisterVerifyCode(phone1);
        GetRegisterCodeService getRegisterCodeService = ServiceGenerator.getInstance(OldPhoneAddStepTwo.this).createService(GetRegisterCodeService.class);
        Observable<Response<ResponseBody>> regCode = getRegisterCodeService.getRegisterCodeRawResp(para)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
        return regCode;
    }

    private void setVerifyCodeButton(String str) {
        boolean isShow = str.length() == 11;
        if (isShow) {
            oldPhoneNumber = etOldPhoneNumber.getText().toString();
            btOldPhoneVerifyCode.setBackground(getResources().getDrawable(R.drawable.btn_blue_shape));
            btOldPhoneVerifyCode.setEnabled(true);
        } else {
            btOldPhoneVerifyCode.setBackground(getResources().getDrawable(R.drawable.btn_gray_shape));
            btOldPhoneVerifyCode.setEnabled(false);
        }
    }

    private void setBindingButton(String str) {
        if (str.length() > 6) {
            Toast.makeText(OldPhoneAddStepTwo.this,
                    "验证码超过长度！",
                    Toast.LENGTH_SHORT).show();
        }
        oldPhoneVerifyCodeInput = str;
        if (oldPhoneVerifyCodeInput.length() > 3) {
            btOldPhoneBinding.setBackground(getResources().getDrawable(R.drawable.btn_blue_shape));
            btOldPhoneBinding.setText("点击进行绑定");
            btOldPhoneBinding.setEnabled(true);
            return;
        } else {
            btOldPhoneBinding.setBackground(getResources().getDrawable(R.drawable.btn_gray_shape));
            btOldPhoneBinding.setEnabled(false);
        }

    }

    private void onStartGetVerifyShow() {
        log(" onStartGetVerifyShow");
        mProgressBar.setMessage(getResources().getString(R.string.validecode_getting));
        mProgressBar.show();

    }

    private void showVerifyError(Throwable t) {
        log(t.toString());
        log("showVerifyError");
        onEndingShow();
        String message = t.getMessage();
        if (t instanceof HttpException) {
            HttpException httpException = (HttpException) t;
            if (httpException.code() == 403)
                message = "403 error";
            else if (httpException.code() == 404)
                message = "404 error";
        }
        Toast.makeText(OldPhoneAddStepTwo.this,
                message,
                Toast.LENGTH_SHORT).show();
    }

    private void onStartBindingShow() {
        log(" onStartBindingShow");
        mProgressBar.setMessage("正在创建成员并绑定设备，请耐心等待...");
        mProgressBar.show();
    }

    private void showBindingError(Throwable t) {
        log(" showBindingError");
        if (mProgressBar != null && mProgressBar.isShowing()) {
            mProgressBar.dismiss();
        }
        Toast.makeText(OldPhoneAddStepTwo.this,
                "创建成员失败，请稍后再试！",
                Toast.LENGTH_SHORT).show();
    }

    private void onEndingShow() {
        log(" onEndingShow");
        mProgressBar.dismiss();
    }

    private void bindingSuccessRepos(Response<ResponseBody> repos) {
        String body = "{\"retcode\":\"200\",\"retmsg\":\"创建成功,密码:888888\",\"data\":{}}";
        try {
            body = repos.body().string();
            log("bindingSuccessReposRaw " + body);
        } catch (IOException e) {
            e.printStackTrace();
        }

        onEndingShow();
        if (parseResp(body)) {
            Toast.makeText(OldPhoneAddStepTwo.this,
                    "用户创建成功,初始密码为6个8！",
                    Toast.LENGTH_SHORT).show();
            SmartHomeApplication.getInstance().getMainActivity().setRefreshHomeFragment(true);
            sendAddOldPhoneSucessBroadcastMsg();
            OldPhoneAddStepTwo.this.finish();
        }
    }

    protected Observable<Response<ResponseBody>> bindingOldPhoneRawResp() {
        log("getValCode Observable bindingOldPhoneRawResponse");
        String phone1 = oldPhoneNumber;
        AddOldPhoneReqParameter para = new AddOldPhoneReqParameter(mFamilyId, mOldPhoneIMEI, phone1, oldPhoneVerifyCodeInput);
        AddOldPhoneService addOldPhoneService = ServiceGenerator.getInstance(OldPhoneAddStepTwo.this).createService(AddOldPhoneService.class);

        Observable<Response<ResponseBody>> resultCode = addOldPhoneService.addOldPhoneRawRespons(para)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
        return resultCode;
    }

    private void sendAddOldPhoneSucessBroadcastMsg() {
        //通知其他界面，创建手表成功
        Intent intent = new Intent(OldPhoneAddStepTwo.ADD_OLD_PHONE_SUCCESS);
        OldPhoneAddStepTwo.this.sendBroadcast(intent);
    }

    private static final boolean DEBUG = SmartHomeApplication.PRINT_LOG;

    private static void log(String msg) {
        if (DEBUG) {
            TLog.Log(msg);
        }
    }

    // 以下的代码 没有用到，主要是现在没有用model的方式来解析json数据。
    // 而是使用了 responsebody的方式来取原始的返回字符串。

    private void setVerifyDisable() {
        llInputPhoneNumber.setEnabled(false);
        btOldPhoneVerifyCode.setBackground(getResources().getDrawable(R.drawable.btn_gray_shape));
        btOldPhoneVerifyCode.setEnabled(false);
    }

    private void setVerifyEnable() {
        llInputPhoneNumber.setEnabled(true);
        btOldPhoneVerifyCode.setBackground(getResources().getDrawable(R.drawable.btn_blue_shape));
        btOldPhoneVerifyCode.setEnabled(true);
        llVerify.setVisibility(View.GONE);
        btOldPhoneBinding.setVisibility(View.GONE);
    }

    private void updateVerifyRepos(GetRegisterCodeResult repos) {
        log(" updateVerifyRepos");
        llInputPhoneNumber.setVisibility(View.GONE);
        btOldPhoneVerifyCode.setVisibility(View.GONE);

        llVerify.setVisibility(View.VISIBLE);
        btOldPhoneBinding.setBackground(getResources().getDrawable(R.drawable.btn_gray_shape));
        btOldPhoneBinding.setVisibility(View.VISIBLE);
        btOldPhoneBinding.setEnabled(false);

        onEndingShow();

    }

    private boolean parseVerifyRespData(String body) {
        JSONObject json = null;
        JSONObject jdata;
        try {
            json = new JSONObject(body);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (json.has("data")) {
            try {
                jdata = json.getJSONObject("data");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    protected Observable<GetRegisterCodeResult> getRegCode() {
        log("getValCode Observable");
        String phone1 = oldPhoneNumber;
        GetRegisterVerifyCode para = new GetRegisterVerifyCode(phone1);
        GetRegisterCodeService getRegisterCodeService = ServiceGenerator.getInstance(OldPhoneAddStepTwo.this).createService(GetRegisterCodeService.class);
        Observable<GetRegisterCodeResult> regCode = getRegisterCodeService.getRegisterCode(para)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
        return regCode;
    }

    protected Observable<AddOldPhoneResult> bindingOldPhone() {
        log("getValCode Observable bindingOldPhone");
        String phone1 = oldPhoneNumber;
        AddOldPhoneReqParameter para = new AddOldPhoneReqParameter(mFamilyId, mOldPhoneIMEI, phone1, oldPhoneVerifyCodeInput);
        AddOldPhoneService addOldPhoneService = ServiceGenerator.getInstance(OldPhoneAddStepTwo.this).createService(AddOldPhoneService.class);
        Observable<AddOldPhoneResult> resultCode = addOldPhoneService.addOldPhone(para)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
        return resultCode;
    }

    private void bindingSuccessRepos(AddOldPhoneResult repos) {
        log(" bindingSuccessRepos");
        onEndingShow();
        Toast.makeText(OldPhoneAddStepTwo.this,
                "用户创建成功,初始密码为6个8！",
                Toast.LENGTH_SHORT).show();
        SmartHomeApplication.getInstance().getMainActivity().setRefreshHomeFragment(true);
        sendAddOldPhoneSucessBroadcastMsg();
        OldPhoneAddStepTwo.this.finish();

    }

    //The End file
}
