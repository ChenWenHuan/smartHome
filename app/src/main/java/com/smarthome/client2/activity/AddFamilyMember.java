package com.smarthome.client2.activity;

import org.json.JSONException;
import org.json.JSONObject;

import com.smarthome.client2.R;
import com.smarthome.client2.SmartHomeApplication;
import com.smarthome.client2.common.Constants;
import com.smarthome.client2.util.HttpUtil;
import com.smarthome.client2.util.TopBarUtils;
import com.smarthome.client2.view.CustomActionBar;
import com.smarthome.client2.widget.CircleImageView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnKeyListener;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

public class AddFamilyMember extends Activity {
	
	private FrameLayout fl_family_member_head;

    private CustomActionBar actionBar;
    
    private ProgressDialog mProgressBar;
    
    private String mMemberID;
    private String mMemberName;
    private Bitmap mImgHead;
    private String mTelNum;
    private String mGroupID;
    
    private CircleImageView imgHead;
    private TextView tvName;
    private TextView tvTelNum;
    private TextView tvRelative;
    private EditText etShowName;
    private Button  btSubmit;
    private String strRelativeTitle;
    private String strRelativeCode = "";
    private String strShowName;
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.e_add_family_person);
        Intent intent = getIntent();
        mMemberID = intent.getStringExtra("id");
        mMemberName = intent.getStringExtra("name");
        mTelNum = intent.getStringExtra("telnum");
        mImgHead = intent.getParcelableExtra("imghead");
        mGroupID = intent.getStringExtra("groupid"); 
        Log.e("---wzl---AddFamilyMember--", "---group id = " + mGroupID);
        
        initView();       
    }
    
    private void initView(){
    	
    	addTopBarToHead();
    	
    	tvRelative = (TextView)this.findViewById(R.id.tv_sel_relative);
    	tvRelative.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(AddFamilyMember.this,
						SelectFamilyRelative.class);
				intent.setAction("SEL_FAMILY_RELATIVE");
                startActivityForResult(intent, 0);
			}
		});
    	etShowName = (EditText)this.findViewById(R.id.et_show_name);
    	btSubmit = (Button)this.findViewById(R.id.bt_submit);
    	btSubmit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				doAddFamilyMem();
				// 发命令增加此家庭关系
			}
		});
    	
        mProgressBar = new ProgressDialog(AddFamilyMember.this);
        mProgressBar.setCanceledOnTouchOutside(false);
        mProgressBar.setOnKeyListener(new OnKeyListener()
        {
            @Override
            public boolean onKey(DialogInterface arg0, int keyCode,
                    KeyEvent event)
            {
                if (keyCode == KeyEvent.KEYCODE_BACK)
                {

                }
                return false;
            }
        });
    }
    
    
    protected void doAddFamilyMem(){
    	
		if(strRelativeCode.equals("")){
			Toast.makeText(AddFamilyMember.this,
                    "请选择成员家庭关系",
                    Toast.LENGTH_SHORT).show();
        	return;
		}
		
		strShowName = etShowName.getEditableText().toString().trim();
    	
    	new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                //设置登录请求参数
                JSONObject obj = new JSONObject();
                try
                {
                    obj.put("targetUserId", mMemberID);
                    obj.put("appellationCode", strRelativeCode);
                    obj.put("groupId", mGroupID);
                    obj.put("alias", strShowName);
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
                
                Log.e("---wzl--addFamily-", "---obj=" + obj.toString());

                handlerAddFamilyMem.sendEmptyMessage(Constants.GET_DATA_START);
                HttpUtil.postRequest(obj,
                        Constants.ADD_FAMILY_MEM_V11,
                        handlerAddFamilyMem,
                        Constants.GET_DATA_SUCCESS,
                        Constants.GET_DATA_FAIL);
            }
        }).start();
    	
    }
    
    private Handler handlerAddFamilyMem = new Handler()
    {
        public void handleMessage(android.os.Message msg)
        {
            switch (msg.what)
            {
                case Constants.GET_DATA_START:
                	mProgressBar.setMessage("正在添加成员...");
                    mProgressBar.show();
                    break;
                case Constants.GET_DATA_SUCCESS:
                    if (msg.obj != null) {
                        try {
                            JSONObject job = new JSONObject(msg.obj.toString());
                            int retCode = job.getInt("data");
                            if(retCode ==  Constants.GET_DATA_DOUBLE_SENT) {
                                Toast.makeText(AddFamilyMember.this,
                                        "重复的请求...",
                                        Toast.LENGTH_SHORT).show();
                            } else if (retCode == Constants.GET_DATA_RELATION_EXIST) {
                                     Toast.makeText(AddFamilyMember.this,
                                        "已经添加过此成员！",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(AddFamilyMember.this,
                                        "添加申请已发出，等待对方同意..",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                        catch (JSONException e) {
                            e.printStackTrace();
                        }

                        mProgressBar.dismiss();
                        SmartHomeApplication.getInstance().getMainActivity().setRefreshHomeFragment(true);
                        AddFamilyMember.this.finish();
                    }
                    break;

                case 201:
                case Constants.GET_DATA_FAIL:
                	mProgressBar.dismiss();
                	Toast.makeText(AddFamilyMember.this,
                            "添加成员失败，请重试！",
                            Toast.LENGTH_SHORT).show();
                    break;
              
                default:
                    break;
            }
        }
    };
    
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
                "添加成员",
                null,
                null);

        fl_family_member_head.addView(actionBar);
        imgHead = (CircleImageView)this.findViewById(R.id.touxiang);
        if (mImgHead != null){
        	imgHead.setImageBitmap(mImgHead);
        }else{
        	imgHead.setImageDrawable(this.getResources().getDrawable(R.drawable.default_pictures));
        }
        tvName = (TextView)this.findViewById(R.id.tv_nick_name);
        tvName.setText(mMemberName);
        tvTelNum = (TextView)this.findViewById(R.id.tv_telphone_number);
        tvTelNum.setText(mTelNum);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode,
            Intent intent) {
        // 当requestCode和resultCode都为0时（处理特定的结果）。
        if ( resultCode!= RESULT_CANCELED) {
            if(requestCode == 0){
	            Bundle bundleData = intent.getExtras();
	            strRelativeTitle = bundleData.getString("title");
	            strRelativeCode = bundleData.getString("code");
	            tvRelative.setText(strRelativeTitle);
	            etShowName.setText(strRelativeTitle);
            }
            
        }
    }
}
