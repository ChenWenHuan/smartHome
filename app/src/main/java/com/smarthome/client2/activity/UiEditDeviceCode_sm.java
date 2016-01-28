package com.smarthome.client2.activity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.smarthome.client2.R;
import com.smarthome.client2.config.Preferences;
import com.smarthome.client2.util.TopBarUtils;
import com.smarthome.client2.view.CustomActionBar;


public class UiEditDeviceCode_sm extends BaseActivity {
	private EditText newDeviceCode;
	private EditText newWatchAkey;
	private TextView mTVhint;
	private LinearLayout mllWatchAkey;
	private FrameLayout mTitleBar;
	private CustomActionBar mActionBar;
	private boolean isWatchDevice = false;
	private Preferences tmpPreferences;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.e_device_code_sm);
		addTopBarToHead();
		if (this.getIntent().getAction().equals("GET_WATCH_DEVICE_ID_KEY")){
			isWatchDevice = true;	
			tmpPreferences = Preferences.getInstance(this.getApplicationContext());
		}
		newDeviceCode=(EditText)this.findViewById(R.id.new_device_code);
		newWatchAkey = (EditText)this.findViewById(R.id.et_watch_akey);
		if (isWatchDevice){
			newDeviceCode.setText(tmpPreferences.getWatchBindID());
			newWatchAkey.setText(tmpPreferences.getWatchBindAkey());
		}
		mTVhint = (TextView)this.findViewById(R.id.tv_show_id);
		mllWatchAkey = (LinearLayout)this.findViewById(R.id.ll_watch_akey);
		if (isWatchDevice){
			mTVhint.setText("请输入手表 Device ID:");
			mllWatchAkey.setVisibility(View.VISIBLE);
		}
	}


	private void addTopBarToHead() {
		mTitleBar = (FrameLayout) findViewById(R.id.edit_device_code_sm_header);
		if (mActionBar != null) {
			mTitleBar.removeView(mActionBar);
		}
		mActionBar = TopBarUtils.createCustomActionBar(this,
				R.drawable.btn_back_selector,
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						UiEditDeviceCode_sm.this.finish();
					}
				},
				"输入设备标识号",
				"提交",
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						if (isWatchDevice){
							String deviceid = newDeviceCode.getText().toString().trim();
							String akey = newWatchAkey.getText().toString().trim();
							if (deviceid.length() > 0 && akey.length() > 0){
								Bundle param = new Bundle();
								param.putString("newDeviceCode", deviceid);
								param.putString("akey", akey);
								tmpPreferences.setWatchBindID(deviceid);
								tmpPreferences.setWatchBindAkey(akey);
								UiEditDeviceCode_sm.this.setResult(RESULT_OK, UiEditDeviceCode_sm.this.getIntent().putExtras(param));
								UiEditDeviceCode_sm.this.finish();
							}else{
								Toast.makeText(getApplicationContext(),
										"输入框不能为空，请确认！",
										Toast.LENGTH_SHORT)
										.show();
							}
						}else{
							String name = newDeviceCode.getText().toString().trim();
							if (name.length() > 0) {
								Bundle param = new Bundle();
								param.putString("newDeviceCode", name);
								UiEditDeviceCode_sm.this.setResult(RESULT_OK, UiEditDeviceCode_sm.this.getIntent().putExtras(param));
								UiEditDeviceCode_sm.this.finish();
							}
						}
					}
				});
		mTitleBar.addView(mActionBar);
	}


}
