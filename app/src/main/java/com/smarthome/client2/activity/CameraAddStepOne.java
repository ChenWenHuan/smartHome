package com.smarthome.client2.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;

import com.smarthome.client2.R;
import com.smarthome.client2.util.TopBarUtils;
import com.smarthome.client2.view.CustomActionBar;

public class CameraAddStepOne extends Activity {

    private FrameLayout fl_header;
    private CustomActionBar actionBar;
    private Button btnNext;
    private String mFamilyId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.camera_add_step_one_main);
        addTopBarToHead();
        btnNext = (Button)findViewById(R.id.bt_camera_next);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CameraAddStepOne.this, CameraAddStepTwo.class);
                intent.putExtra("familyId", mFamilyId);
                CameraAddStepOne.this.startActivity(intent);
            }
        });
        mFamilyId = this.getIntent().getStringExtra("familyId");
        registerReceiver(addSucessRevcer, new IntentFilter(CameraAddByIdActivity.ADD_CAMERA_SUCCESS));
    }

    private BroadcastReceiver addSucessRevcer = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            CameraAddStepOne.this.finish();
        }
    };

    private void addTopBarToHead() {
        fl_header = (FrameLayout) findViewById(R.id.fl_header);
        actionBar = TopBarUtils.createCustomActionBar(getApplicationContext(),
                R.drawable.btn_back_selector,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                },
                "添加摄像头-步骤一",
                null,
                null);

        fl_header.addView(actionBar);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(addSucessRevcer);
    }
}
