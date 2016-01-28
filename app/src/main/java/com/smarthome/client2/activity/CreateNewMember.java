package com.smarthome.client2.activity;

import com.smarthome.client2.R;
import com.smarthome.client2.SmartHomeApplication;
import com.smarthome.client2.util.TopBarUtils;
import com.smarthome.client2.view.CustomActionBar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

public class CreateNewMember extends Activity {
	
	private FrameLayout fl_family_member_head;

    private CustomActionBar actionBar;
    
    private TextView tv_relative_sel;
    
    private String relativeTitle = "";
    
    private String relativeCode = "";
    
    private String deviceType = "";
    
    private String deviceTypeCode = "";
    
    private EditText   ed_nick_name;
    
    private EditText  ed_tel_num;
    
    private TextView tv_device_type;
    private TextView tv_show_hint;
    
    private Button  bt_next_step;
    
    private String mTelNum = "";
    
    private String mFamilyID = "";
    

    private String mDeviceType = "";
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.e_create_new_person);
        mFamilyID = this.getIntent().getStringExtra("groupid");
        mDeviceType = this.getIntent().getStringExtra("memtype");
       
        initView();
        if(mDeviceType.equals("6")){
        	deviceType = "儿童手表";
        	deviceTypeCode = mDeviceType;
            tv_device_type.setText(deviceType);
            tv_show_hint.setVisibility(View.VISIBLE);
        }else if (mDeviceType.equals("2")){
        	deviceType = "老人机";
        	deviceTypeCode = mDeviceType;
            tv_device_type.setText(deviceType);
            tv_show_hint.setVisibility(View.GONE);
        }else if (mDeviceType.equals("1")){
        	deviceType = "学生证";
        	deviceTypeCode = mDeviceType;
            tv_device_type.setText(deviceType);
            tv_show_hint.setVisibility(View.GONE);
        }else if (mDeviceType.equals("4")){
        	deviceType = "智能机";
        	deviceTypeCode = mDeviceType;
            tv_device_type.setText(deviceType);
            tv_show_hint.setVisibility(View.GONE);
        }
    }
    

    
    @Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();	
	}

	private void initView(){
    	addTopBarToHead();
    	
    	tv_relative_sel = (TextView)this.findViewById(R.id.tv_relative_select);
    	tv_relative_sel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(CreateNewMember.this,
						SelectFamilyRelative.class);
				intent.setAction("SEL_FAMILY_RELATIVE");
                startActivityForResult(intent, 0);
			}
		});
    	tv_show_hint = (TextView)this.findViewById(R.id.tv_show_hint);
    	tv_device_type = (TextView)this.findViewById(R.id.tv_device_type);
    	tv_device_type.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(CreateNewMember.this,
						SelectFamilyRelative.class);
				intent.setAction("SEL_DEVICE_TYPE");
                startActivityForResult(intent, 1);
			}
		});
    	
    	bt_next_step = (Button)this.findViewById(R.id.bt_next_step);
    	
    	bt_next_step.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mTelNum = ed_tel_num.getEditableText().toString();
                if (TextUtils.isEmpty(mTelNum))
                {
                    Toast.makeText(CreateNewMember.this,
                            "手机号不能为空",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                if (mTelNum.length() != 11)
                {
                    Toast.makeText(CreateNewMember.this,
                            R.string.phone_num_error,
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                
                if (relativeCode.equals("")){
                	Toast.makeText(CreateNewMember.this,
                            "请选择成员家庭关系",
                            Toast.LENGTH_SHORT).show();
                	return;
                }

                mDeviceType = tv_device_type.getText().toString();
                if (TextUtils.isEmpty(mDeviceType) || mDeviceType.equalsIgnoreCase("点击选择")) {
                    Toast.makeText(CreateNewMember.this,
                            "请选择设备类型",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

				Intent intent = new Intent(CreateNewMember.this,CreateNewNextSteps.class);
				intent.putExtra("telnum", mTelNum);
				intent.putExtra("showname", ed_nick_name.getEditableText().toString());
				intent.putExtra("relativecode", relativeCode);
				intent.putExtra("devicetypecode", deviceTypeCode);
				intent.putExtra("groupid", mFamilyID);
				startActivityForResult(intent, 2);
				
			}
		});
    	
    	ed_nick_name = (EditText)this.findViewById(R.id.et_nick_name);
    	ed_tel_num = (EditText)this.findViewById(R.id.et_telphone_number);
    }
    
    //添加actionbar
    private void addTopBarToHead()
    {
        fl_family_member_head = (FrameLayout) findViewById(R.id.fl_header_home);
        if (actionBar != null)
        {
            fl_family_member_head.removeView(actionBar);
        }
        actionBar = TopBarUtils.createCustomActionBar(SmartHomeApplication.getInstance(),
                R.drawable.btn_back_selector,
                new OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        finish();
                    }
                },
                "创建成员",
                "",
                null);

        fl_family_member_head.addView(actionBar);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode,
            Intent intent) {
        // 当requestCode和resultCode都为0时（处理特定的结果）。
        if ( resultCode!= RESULT_CANCELED) {
            if(requestCode == 0){
	            Bundle bundleData = intent.getExtras();
	            relativeTitle = bundleData.getString("title");
	            relativeCode = bundleData.getString("code");
	            tv_relative_sel.setText(relativeTitle);
	            ed_nick_name.setText(relativeTitle);
	            
            }else if(requestCode == 1){
            	Bundle bundleData = intent.getExtras();
            	deviceType = bundleData.getString("title");
            	deviceTypeCode = bundleData.getString("code");
	            tv_device_type.setText(deviceType);
	            if(deviceTypeCode.equals("6")){
	            	tv_show_hint.setVisibility(View.VISIBLE);
	            }else{
	            	tv_show_hint.setVisibility(View.GONE);
	            }
            }else if (requestCode == 2){
                Bundle data = new Bundle();
                data.putString("title", relativeTitle);
                intent.putExtras(data);
                CreateNewMember.this.setResult(RESULT_OK, intent);
            	this.finish();
            }
        }
    }
    


}
