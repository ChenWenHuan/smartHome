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

public class OldPhoneAddStepOne extends Activity {

    @Bind(R.id.fl_header)
    FrameLayout flHeader;

    @Bind(R.id.et_old_phone_imei)
    EditText etOldPhoneImei;

    @Bind(R.id.bt_old_phone_next)
    Button btOldPhoneNext;

    private CustomActionBar actionBar;
    private String familyId;
    private Preferences tmpPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.old_phone_add_step_one_main);
        ButterKnife.bind(this);
        familyId = this.getIntent().getStringExtra("groupid");
        btOldPhoneNext.setBackground(getResources().getDrawable(R.drawable.btn_gray_shape));
        btOldPhoneNext.setEnabled(false);
        btOldPhoneNext.setOnClickListener((v) -> nextStepClick());
        addTopBarToHead();

        registerReceiver(addSucessRevcer, new IntentFilter(OldPhoneAddStepTwo.ADD_OLD_PHONE_SUCCESS));
        tmpPreferences = Preferences.getInstance(this.getApplicationContext());
        if (!TextUtils.isEmpty(tmpPreferences.getOldPhoneImei())) {
            etOldPhoneImei.setText(tmpPreferences.getOldPhoneImei());
        }
    }

    private BroadcastReceiver addSucessRevcer = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            tmpPreferences.setOldPhoneImei("");
            OldPhoneAddStepOne.this.finish();
        }
    };

    private void addTopBarToHead() {
        actionBar = TopBarUtils.createCustomActionBar(getApplicationContext(),
                R.drawable.btn_back_selector,
                v -> finish(),
                "老人机绑定-步骤一",
                null,
                null);

        flHeader.addView(actionBar);
    }

    private void nextStepClick() {
        String oldPhoneIMEI;
        Intent intent = null;
        oldPhoneIMEI = etOldPhoneImei.getText().toString();

        intent = new Intent(OldPhoneAddStepOne.this, OldPhoneAddStepTwo.class);
        intent.putExtra("imei", oldPhoneIMEI);
        intent.putExtra("familyId", familyId);
        tmpPreferences.setWatchTelNum(oldPhoneIMEI);
        OldPhoneAddStepOne.this.startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(addSucessRevcer);
    }

    @OnTextChanged(R.id.et_old_phone_imei)
    public void showNextStep() {
        String oldPhoneIMEI;
        oldPhoneIMEI = etOldPhoneImei.getText().toString();
        if ((oldPhoneIMEI.length() == 15) || (oldPhoneIMEI.length() == 14)) {
            btOldPhoneNext.setBackground(getResources().getDrawable(R.drawable.btn_blue_shape));
            btOldPhoneNext.setEnabled(true);
        } else {
            btOldPhoneNext.setBackground(getResources().getDrawable(R.drawable.btn_gray_shape));
            btOldPhoneNext.setEnabled(false);
        }
    }
}
