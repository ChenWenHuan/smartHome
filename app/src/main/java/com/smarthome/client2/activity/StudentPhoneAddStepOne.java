package com.smarthome.client2.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.smarthome.client2.R;
import com.smarthome.client2.config.Preferences;
import com.smarthome.client2.util.TopBarUtils;
import com.smarthome.client2.view.CustomActionBar;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnTextChanged;

public class StudentPhoneAddStepOne extends Activity {

    @Bind(R.id.fl_header)
    FrameLayout flHeader;

    @Bind(R.id.et_student_phone_imei)
    EditText etStudentPhoneImei;

    @Bind(R.id.bt_student_phone_next)
    Button btStudentPhoneNext;

    private CustomActionBar actionBar;
    private String familyId;
    private Preferences tmpPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.student_phone_add_step_one_main);
        ButterKnife.bind(this);
        familyId = this.getIntent().getStringExtra("groupid");

        btStudentPhoneNext.setOnClickListener((v) -> nextStepClick());
        btStudentPhoneNext.setBackground(getResources().getDrawable(R.drawable.btn_gray_shape));
        btStudentPhoneNext.setEnabled(false);

        addTopBarToHead();

        registerReceiver(addSucessRevcer, new IntentFilter(StudentPhoneAddStepTwo.ADD_STUDENT_PHONE_SUCCESS));
        tmpPreferences = Preferences.getInstance(this.getApplicationContext());
        if (!TextUtils.isEmpty(tmpPreferences.getStudentPhoneImei())) {
            etStudentPhoneImei.setText(tmpPreferences.getStudentPhoneImei());
        }

    }

    private BroadcastReceiver addSucessRevcer = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            tmpPreferences.setStudentPhoneImei("");
            StudentPhoneAddStepOne.this.finish();
        }
    };

    private void addTopBarToHead() {
        actionBar = TopBarUtils.createCustomActionBar(getApplicationContext(),
                R.drawable.btn_back_selector,
                v -> finish(),
                "学生机绑定-步骤一",
                null,
                null);

        flHeader.addView(actionBar);
    }

    private void nextStepClick() {
        String studentPhoneIMEI;
        Intent intent = null;
        studentPhoneIMEI = etStudentPhoneImei.getText().toString();

        intent = new Intent(StudentPhoneAddStepOne.this, StudentPhoneAddStepTwo.class);
        intent.putExtra("imei", studentPhoneIMEI);
        intent.putExtra("familyId", familyId);
        tmpPreferences.setWatchTelNum(studentPhoneIMEI);
        StudentPhoneAddStepOne.this.startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(addSucessRevcer);
    }

    @OnTextChanged(R.id.et_student_phone_imei)
    public void showNextStepButton() {
        String studentPhoneIMEI;
        studentPhoneIMEI = etStudentPhoneImei.getText().toString();
        if ((studentPhoneIMEI.length() == 15) || (studentPhoneIMEI.length() == 14)) {
            btStudentPhoneNext.setBackground(getResources().getDrawable(R.drawable.btn_blue_shape));
            btStudentPhoneNext.setEnabled(true);
        } else {
            btStudentPhoneNext.setBackground(getResources().getDrawable(R.drawable.btn_gray_shape));
            btStudentPhoneNext.setEnabled(false);
        }
    }

}
