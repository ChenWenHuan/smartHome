package com.smarthome.client2.activity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.smarthome.client2.R;
import com.smarthome.client2.util.TopBarUtils;
import com.smarthome.client2.view.CustomActionBar;


public class UiEditUserInfoBirthday_sm extends BaseActivity {
	private EditText newBirthday;
	private ImageView img_head_ico;
	private TextView tv_head_title;
	private Button head_button;
	private FrameLayout mTitleBar;
	private CustomActionBar mActionBar;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.e_birthday_sm);
		addTopBarToHead();
		newBirthday=(EditText)this.findViewById(R.id.new_birthday);
	}


	private void addTopBarToHead() {
		mTitleBar = (FrameLayout) findViewById(R.id.edit_birthday_sm_header);
		if (mActionBar != null) {
			mTitleBar.removeView(mActionBar);
		}
		mActionBar = TopBarUtils.createCustomActionBar(this,
				R.drawable.btn_back_selector,
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						UiEditUserInfoBirthday_sm.this.finish();
					}
				},
				"设置出生年月",
				"提交",
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						String name = newBirthday.getText().toString().trim();
						if (name.length() > 0) {
							Bundle param = new Bundle();
							param.putString("newBirth", name);
							UiEditUserInfoBirthday_sm.this.setResult(RESULT_OK, UiEditUserInfoBirthday_sm.this.getIntent().putExtras(param));
							UiEditUserInfoBirthday_sm.this.finish();
						}
					}
				});
		mTitleBar.addView(mActionBar);
	}


}
