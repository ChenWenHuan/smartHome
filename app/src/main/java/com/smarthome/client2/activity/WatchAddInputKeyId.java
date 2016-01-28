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
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.smarthome.client2.R;
import com.smarthome.client2.config.Preferences;
import com.smarthome.client2.util.TopBarUtils;
import com.smarthome.client2.view.CustomActionBar;

/**
 * Created by Administrator on 2015/12/2.
 */
public class WatchAddInputKeyId extends Activity{

    private FrameLayout fl_header;
    private CustomActionBar actionBar;
    private EditText  edtWatchId;
    private EditText  edtWatchAkey;
    private Button    btnNextStep;
    private String familyId;
    private String watchTelNum;
    private String watchId;
    private String watchAkey;
    private Preferences tmpPreferences;
    private TextView tvMsgConfirm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.watch_add_input_main);
        edtWatchId = (EditText)this.findViewById(R.id.et_watch_id);
        edtWatchAkey = (EditText)this.findViewById(R.id.et_watch_key);
        btnNextStep = (Button)this.findViewById(R.id.bt_watch_next);
        tvMsgConfirm = (TextView)findViewById(R.id.tv_msg_confirm);
        btnNextStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doNextSteps();
            }
        });
        familyId = this.getIntent().getStringExtra("familyId");
        watchTelNum = this.getIntent().getStringExtra("watchTelNum");
        addTopBarToHead();
        //注册接收手表创建成功的消息
        registerReceiver(addSucessRevcer, new IntentFilter(WatchAddConfirm.ADD_WATCH_SUCCESS));
        tmpPreferences = Preferences.getInstance(this.getApplicationContext());

        if (!TextUtils.isEmpty(tmpPreferences.getWatchBindID())){
            edtWatchId.setText(tmpPreferences.getWatchBindID());
        }
        if(!TextUtils.isEmpty(tmpPreferences.getWatchBindAkey())){
            edtWatchAkey.setText(tmpPreferences.getWatchBindAkey());
            tvMsgConfirm.setVisibility(View.VISIBLE);
        }
    }

    private BroadcastReceiver addSucessRevcer = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            tmpPreferences.setWatchBindID("");
            tmpPreferences.setWatchBindAkey("");
            WatchAddInputKeyId.this.finish();
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
                "手表绑定-ID输入",
                null,
                null);

        fl_header.addView(actionBar);
    }

    private  void doNextSteps(){

        watchId = edtWatchId.getEditableText().toString().trim();
        watchAkey = edtWatchAkey.getEditableText().toString().trim();
        if(!TextUtils.isEmpty(watchId) && !TextUtils.isEmpty(watchAkey)){
            Intent intent= null;
            intent = new Intent(WatchAddInputKeyId.this, WatchAddConfirm.class);
            intent.putExtra("watchTelNum",watchTelNum);
            intent.putExtra("familyId",familyId);
            intent.putExtra("watchid",watchId);
            intent.putExtra("watchkey", watchAkey);
            intent.setAction(WatchAddConfirm.ID_ACTION);
            tmpPreferences.setWatchBindID(watchId);
            tmpPreferences.setWatchBindAkey(watchAkey);
            startActivity(intent);
        }else{
            Toast.makeText(WatchAddInputKeyId.this,"请输入手表ID和Akey ！",Toast.LENGTH_SHORT).show();
            return;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(addSucessRevcer);
    }
}
