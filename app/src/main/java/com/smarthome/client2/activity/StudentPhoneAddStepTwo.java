package com.smarthome.client2.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.smarthome.client2.R;
import com.smarthome.client2.SmartHomeApplication;
import com.smarthome.client2.common.Constants;
import com.smarthome.client2.common.TLog;
import com.smarthome.client2.model.addOldPhoneMem.AddOldPhoneReqParameter;
import com.smarthome.client2.model.addStudentPhoneMem.AddStudentPhoneParameter;
import com.smarthome.client2.model.retrofitServices.AddOldPhoneService;
import com.smarthome.client2.model.retrofitServices.AddStudentPhoneService;
import com.smarthome.client2.model.retrofitServices.ServiceGenerator;
import com.smarthome.client2.util.HttpUtil;
import com.smarthome.client2.util.TopBarUtils;
import com.smarthome.client2.view.CustomActionBar;
import com.jakewharton.rxbinding.view.RxView;
import com.squareup.okhttp.ResponseBody;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnTextChanged;
import retrofit.Response;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class StudentPhoneAddStepTwo extends Activity {

    @Bind(R.id.fl_header)
    FrameLayout flHeader;

    @Bind(R.id.et_student_phone_number)
    EditText etStudentPhoneNumber;

    @Bind(R.id.bt_student_phone_binding)
    Button btStudentPhoneBinding;

    private CustomActionBar actionBar;
    private String mStudentPhoneIMEI;
    private String mStrStudentPhoneNumber;
    private String mFamilyId;
    private ProgressDialog mProgressBar;
    private CompositeSubscription mCompositeSubscription
            = new CompositeSubscription();

    public static final String ADD_STUDENT_PHONE_SUCCESS = "com.smarthome.client2.activity.StudentPhoneStepTwo.sucess";

    private BroadcastReceiver addSucessRevcer = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            StudentPhoneAddStepTwo.this.finish();
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.student_phone_add_step_two_main);
        ButterKnife.bind(this);

        mStudentPhoneIMEI = this.getIntent().getStringExtra("imei");
        mFamilyId = this.getIntent().getStringExtra("familyId");

        mProgressBar = new ProgressDialog(StudentPhoneAddStepTwo.this);
        mProgressBar.setCanceledOnTouchOutside(false);
        mProgressBar.setOnKeyListener((arg0, keyCode, event) -> {
            return false;
        });

        btStudentPhoneBinding.setBackground(getResources().getDrawable(R.drawable.btn_gray_shape));
        btStudentPhoneBinding.setEnabled(false);
        mCompositeSubscription.add(
                RxView.clicks(btStudentPhoneBinding)
                        .switchMap((v) -> this.bindingStudentPhoneRawResp()
                                        .doOnSubscribe(() -> onStartBindingShow())
                                        .subscribeOn(AndroidSchedulers.mainThread())
                                        .doOnError(throwable -> showBindingError(throwable))
                                        .onErrorResumeNext(Observable.empty())
                        )
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::bindingSuccessRepos, this::showBindingError)
        );
        onEndingShow();

        addTopBarToHead();
        registerReceiver(addSucessRevcer, new IntentFilter(WatchAddConfirm.ADD_WATCH_SUCCESS));
    }


    private void addTopBarToHead() {
        actionBar = TopBarUtils.createCustomActionBar(getApplicationContext(),
                R.drawable.btn_back_selector,
                v -> finish(),
                "学生机绑定-步骤二",
                null,
                null);

        flHeader.addView(actionBar);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCompositeSubscription.unsubscribe();
        ButterKnife.unbind(this);
        unregisterReceiver(addSucessRevcer);
    }

    @OnTextChanged(R.id.et_student_phone_number)
    public void showVerifyCodeButton() {
        mStrStudentPhoneNumber = etStudentPhoneNumber.getText().toString();
        if (mStrStudentPhoneNumber.length() == 11) {
            btStudentPhoneBinding.setBackground(getResources().getDrawable(R.drawable.btn_blue_shape));
            btStudentPhoneBinding.setEnabled(true);
        } else {
            btStudentPhoneBinding.setBackground(getResources().getDrawable(R.drawable.btn_gray_shape));
            btStudentPhoneBinding.setEnabled(false);
        }
    }

    protected Observable<Response<ResponseBody>> bindingStudentPhoneRawResp() {
        log("getValCode Observable bindingStudentPhoneRawResp");
        String phone1 = mStrStudentPhoneNumber;
        AddStudentPhoneParameter para = new AddStudentPhoneParameter(mFamilyId, mStudentPhoneIMEI, phone1);
        AddStudentPhoneService addStudentPhoneService = ServiceGenerator.getInstance(StudentPhoneAddStepTwo.this).createService(AddStudentPhoneService.class);

        Observable<Response<ResponseBody>> resultCode = addStudentPhoneService.addStudentPhoneRawRespons(para)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
        return resultCode;
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
        Toast.makeText(StudentPhoneAddStepTwo.this,
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
            Toast.makeText(StudentPhoneAddStepTwo.this,
                    "用户创建成功,初始密码为6个8！",
                    Toast.LENGTH_SHORT).show();
            SmartHomeApplication.getInstance().getMainActivity().setRefreshHomeFragment(true);
            sendAddStudnetPhoneSucessBroadcastMsg();
            StudentPhoneAddStepTwo.this.finish();
        }
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
            Toast.makeText(StudentPhoneAddStepTwo.this,
                    retmsg,
                    Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private static final boolean DEBUG = SmartHomeApplication.PRINT_LOG;

    private static void log(String msg) {
        if (DEBUG) {
            TLog.Log(msg);
        }
    }

    private void sendAddStudnetPhoneSucessBroadcastMsg() {
        //通知其他界面，创建学生机成功
        Intent intent = new Intent(StudentPhoneAddStepTwo.ADD_STUDENT_PHONE_SUCCESS);
        StudentPhoneAddStepTwo.this.sendBroadcast(intent);
    }

    //The End file
}
