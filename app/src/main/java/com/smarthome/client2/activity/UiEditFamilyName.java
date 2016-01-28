package com.smarthome.client2.activity;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.smarthome.client2.R;
import com.smarthome.client2.common.Constants;
import com.smarthome.client2.familySchool.utils.FsConstants;
import com.smarthome.client2.familySchool.utils.HttpJson;
import com.smarthome.client2.familySchool.utils.MyHttpUtil;
import com.smarthome.client2.familySchool.utils.ResultParsers;
import com.smarthome.client2.util.TopBarUtils;
import com.smarthome.client2.view.CustomActionBar;

import org.w3c.dom.Text;

public class UiEditFamilyName extends BaseActivity {
	
	
	public static final String EDIT_FAMILY_NAME = "edit family name";
	
	public static final String EDIT_FAMILY_MEM_MARK = "edit family mem mark";
	
	private EditText newnickname;

	private FrameLayout mTitleBar;
	private CustomActionBar mActionBar;
    private TextView tvNotify;
	
	private String familyID;
	private String newName;
	
	private String mStrTitle = "";
	
	private String actionType = "";
	
	private String mRemarkUserID = "";
	

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.editnickname_sm);
		familyID = this.getIntent().getStringExtra("familyid");
		actionType = this.getIntent().getAction();
		newnickname=(EditText)this.findViewById(R.id.new_nickname);
        tvNotify = (TextView)this.findViewById(R.id.tv_notify);
		if (actionType.equals(EDIT_FAMILY_NAME)){
			mStrTitle = "修改家庭名称";
            tvNotify.setText("请设置新的家庭名称");
			newnickname.setHint("给家庭起个好名字");
		}else if (actionType.equals(EDIT_FAMILY_MEM_MARK)){
			mStrTitle = "修改成员标签";
            tvNotify.setText("设置新的显示标签");
			mRemarkUserID = this.getIntent().getStringExtra("userid");
			newnickname.setText(this.getIntent().getStringExtra("name"));
		}
		addTopBarToHead();
	}
	

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
						UiEditFamilyName.this.finish();
					}
				},
				mStrTitle,
				"提交",
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						newName = newnickname.getText().toString().trim();
						if (newName.length() > 0) {
							if (actionType.equals(EDIT_FAMILY_NAME)){
								updateFamilyName();
							}else if (actionType.equals(EDIT_FAMILY_MEM_MARK)){
								updateMemShowName();
							}
							
						}
					}
				});
		mTitleBar.addView(mActionBar);
	}
	
    private void updateFamilyName()
    {
        HttpJson pJson = new HttpJson();
        pJson.put("familyId", familyID);
        pJson.put("title", newName);

        MyHttpUtil.post(Constants.UPDATE_FAMILY_NAME_ACTION, pJson, mHandler);
    }
    
    private void updateMemShowName()
    {
        HttpJson pJson = new HttpJson();
        pJson.put("famId", familyID);
        pJson.put("tarUserId", mRemarkUserID);
        pJson.put("remark", newName);
        Log.e("----wzl-----", "--updateMemShowName----");
        MyHttpUtil.post(Constants.UPDATE_FAMILY_MEM_NAME_ACTION, pJson, mHandler);
    }
    
    private Handler mHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            switch (msg.what)
            {
                case FsConstants.HTTP_SUCCESS:
   
                    String result = (String) msg.obj;
                    String code = ResultParsers.getCode(result);
                    if (code.equals("200")){
                    	showToast("修改成功！");
                    	Bundle param = new Bundle();
						param.putString("familyname", newName);
						UiEditFamilyName.this.setResult(RESULT_OK, UiEditFamilyName.this.getIntent().putExtras(param));
						UiEditFamilyName.this.finish();
                    }
                    else{
                        showToast(R.string.server_offline);
                    }
                    break;
                case FsConstants.HTTP_FAILURE:

                    showToast(R.string.no_network);
                    break;
                case FsConstants.HTTP_FINISH:
                    
                    break;
                default:
                    break;
            }
        }
    };
    



}
