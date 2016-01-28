package com.smarthome.client2.activity;

import android.app.Activity;
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
import com.smarthome.client2.SmartHomeApplication;
import com.smarthome.client2.util.TopBarUtils;
import com.smarthome.client2.view.CustomActionBar;


public class UiEditUserInfoNickname_sm extends BaseActivity {
	private EditText newnickname;
	private ImageView img_head_ico;
	private TextView tv_head_title;
	private Button head_button;
	private FrameLayout mTitleBar;
	private CustomActionBar mActionBar;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.editnickname_sm);
		addTopBarToHead();
		//initHeadView();
		//setHeadTitle("设置昵称");
		newnickname=(EditText)this.findViewById(R.id.new_nickname);
	}
	
//	private void initHeadView() {
//		MyClickListener clickListener = new MyClickListener();
//		img_head_ico = (ImageView) this.findViewById(R.id.img_head_bar_icon);
//		img_head_ico.setOnClickListener(clickListener);
//		head_button = (Button) this.findViewById(R.id.head_button);
//		head_button.setOnClickListener(clickListener);
//		tv_head_title = (TextView) this.findViewById(R.id.tv_head_bar_title);
//		tv_head_title.setOnClickListener(clickListener);
//	}

//	private void setHeadTitle(String title) {
//
//		tv_head_title = (TextView) this.findViewById(R.id.tv_head_bar_title);
//		tv_head_title.setText(title);
//
//	}
//
//	private class MyClickListener implements OnClickListener {
//
//		@Override
//		public void onClick(View v) {
//			// TODO Auto-generated method stub
//			switch (v.getId()) {
//			case R.id.img_head_bar_icon:
//			case R.id.tv_head_bar_title:
//				UiEditUserInfoNickname_sm.this.finish();
//				break;
//			case R.id.head_button:
//				String name=newnickname.getText().toString().trim();
//				if(name.length()>0){
//					Bundle param=new Bundle();
//					param.putString("nickname", name);
//					UiEditUserInfoNickname_sm.this.setResult(RESULT_OK, UiEditUserInfoNickname_sm.this.getIntent().putExtras(param));
//					UiEditUserInfoNickname_sm.this.finish();
//				}else{
//					//toast("您还没有起个名字呢~");
//				}
//				break;
//			default:
//				break;
//			}
//		}

//	}


	private void addTopBarToHead() {
		mTitleBar = (FrameLayout) findViewById(R.id.edit_nickname_sm_header);
		if (mActionBar != null) {
			mTitleBar.removeView(mActionBar);
		}
		mActionBar = TopBarUtils.createCustomActionBar(this,
				R.drawable.btn_back_selector,
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						UiEditUserInfoNickname_sm.this.finish();
					}
				},
				"设置名字",
				"提交",
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						String name = newnickname.getText().toString().trim();
						if (name.length() > 0) {
							Bundle param = new Bundle();
							param.putString("nickname", name);
							UiEditUserInfoNickname_sm.this.setResult(RESULT_OK, UiEditUserInfoNickname_sm.this.getIntent().putExtras(param));
							UiEditUserInfoNickname_sm.this.finish();
						}
					}
				});
		mTitleBar.addView(mActionBar);
	}


}
