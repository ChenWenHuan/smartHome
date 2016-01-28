package com.smarthome.client2.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.smarthome.client2.R;
import com.smarthome.client2.util.TopBarUtils;
import com.smarthome.client2.view.CustomActionBar;
import com.smarthome.client2.zxing.CaptureActivity;

public class CameraAddStepTwo extends Activity {

    private FrameLayout fl_header;
    private CustomActionBar actionBar;
    private Button btnQrcode;
    private Button btnInputKey;
    private String mFamilyId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.camera_add_step_two_main);

        mFamilyId = this.getIntent().getStringExtra("familyId");
        btnQrcode = (Button)this.findViewById(R.id.bt_qrcode);
        btnQrcode.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                qrCodeClick();
            }
        });
        btnInputKey = (Button)this.findViewById(R.id.bt_input_key);
        btnInputKey.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                keyInputClick();
            }
        });
        addTopBarToHead();
        registerReceiver(addSucessRevcer, new IntentFilter(CameraAddByIdActivity.ADD_CAMERA_SUCCESS));
    }

    private BroadcastReceiver addSucessRevcer = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            CameraAddStepTwo.this.finish();
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
                "添加摄像头-步骤二",
                null,
                null);

        fl_header.addView(actionBar);
    }

    private void qrCodeClick(){

        Intent intent = new Intent(CameraAddStepTwo.this, CaptureActivity.class);
        CameraAddStepTwo.this.startActivityForResult(intent, 0);

    }

    private void keyInputClick(){
        Intent intent;
        intent = new Intent(CameraAddStepTwo.this,CameraAddByIdActivity.class);
        intent.putExtra("familyid", mFamilyId);
        intent.setAction(CameraAddByIdActivity.CAMERA_ADD_ACTION);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 0 && resultCode == RESULT_OK){
            String szQrcode;
            Intent intent = null;
            szQrcode = data.getStringExtra("qrcode");
            if(!TextUtils.isEmpty(szQrcode) && szQrcode.startsWith("http")){
                Toast.makeText(CameraAddStepTwo.this, "此为软件下载二维码，请扫描另一二维码！", Toast.LENGTH_LONG).show();
                return;
            }
            if(!TextUtils.isEmpty(szQrcode)) {
                intent = new Intent(CameraAddStepTwo.this, CameraAddByIdActivity.class);
                intent.putExtra("cameraQrcode", szQrcode);
                intent.putExtra("familyId", mFamilyId);
                intent.setAction(WatchAddConfirm.QRCODE_ACTION);
                CameraAddStepTwo.this.startActivity(intent);
            }else{
                Toast.makeText(CameraAddStepTwo.this, "二维码识别错误，请重试！", Toast.LENGTH_SHORT).show();
                return;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(addSucessRevcer);
    }

}
