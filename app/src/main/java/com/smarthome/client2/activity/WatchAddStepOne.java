package com.smarthome.client2.activity;

import com.smarthome.client2.R;
import com.smarthome.client2.config.Preferences;
import com.smarthome.client2.util.TopBarUtils;
import com.smarthome.client2.view.CustomActionBar;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class WatchAddStepOne extends Activity {
	
    private FrameLayout fl_header;
    private CustomActionBar actionBar;
    private Button btnNextStep;
    private EditText edtWatchTelNum;
    private String familyId;
    private Preferences tmpPreferences;
    private ImageView imgContact;
    private TextView tvConfrimAgain;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
	    setContentView(R.layout.watch_add_step_one_main);
        familyId = this.getIntent().getStringExtra("groupid");
	    btnNextStep = (Button)this.findViewById(R.id.bt_watch_next);
	    btnNextStep.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				nextStepClick();
			}
		});
	    edtWatchTelNum = (EditText)this.findViewById(R.id.et_watch_tel_num);
	    addTopBarToHead();
        tvConfrimAgain = (TextView)findViewById(R.id.tv_show_confirm_again);
        registerReceiver(addSucessRevcer, new IntentFilter(WatchAddConfirm.ADD_WATCH_SUCCESS));
        tmpPreferences = Preferences.getInstance(this.getApplicationContext());
        if(!TextUtils.isEmpty(tmpPreferences.getWatchTelNum())){
            edtWatchTelNum.setText(tmpPreferences.getWatchTelNum());
            tvConfrimAgain.setVisibility(View.VISIBLE);
        }


        imgContact = (ImageView)findViewById(R.id.img_contact);
        imgContact.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_PICK);
                intent.setData(ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(intent, 1);
            }
        });
	}

    private BroadcastReceiver addSucessRevcer = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            tmpPreferences.setWatchTelNum("");
            WatchAddStepOne.this.finish();
        }
    };
	
    private void addTopBarToHead() {
    	fl_header = (FrameLayout) findViewById(R.id.fl_header);

        actionBar = TopBarUtils.createCustomActionBar(getApplicationContext(),
                R.drawable.btn_back_selector,
                new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                },
                "手表绑定-步骤一",
                null,
                null);

        fl_header.addView(actionBar);
    }
    
    private void nextStepClick(){
    	String watchTelNum;
    	Intent intent = null;
    	watchTelNum = edtWatchTelNum.getText().toString();
    	if(watchTelNum.length() != 11){
    		Toast.makeText(WatchAddStepOne.this,
    				("请输入正确的手表号码！"),
    				Toast.LENGTH_SHORT)
    				.show();
    		return;
    	}
    	intent = new Intent(WatchAddStepOne.this, WatchAddStepTwo.class);
    	intent.putExtra("watchTelNum", watchTelNum);
        intent.putExtra("familyId", familyId);
        tmpPreferences.setWatchTelNum(watchTelNum);
    	WatchAddStepOne.this.startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(addSucessRevcer);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                String phoneNum = getContactPhoneNum(data);
                edtWatchTelNum.setText(phoneNum);
                break;
            default:
                break;
        }
    }

    private String getContactPhoneNum(Intent data){
        String telnum = "";
        //ContentProvider展示数据类似一个单个数据库表
        //ContentResolver实例带的方法可实现找到指定的ContentProvider并获取到ContentProvider的数据
        ContentResolver reContentResolverol = getContentResolver();
        //URI,每个ContentProvider定义一个唯一的公开的URI,用于指定到它的数据集
        Uri contactData = data.getData();
        if (contactData == null){
            return "";
        }
        //查询就是输入URI等参数,其中URI是必须的,其他是可选的,如果系统能找到URI对应的ContentProvider将返回一个Cursor对象.
        Cursor cursor = reContentResolverol.query(contactData, null, null, null, null);
        cursor.moveToFirst();
        //条件为联系人ID
        String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
        // 获得DATA表中的电话号码，条件为联系人ID,因为手机号码可能会有多个
        Cursor phone = reContentResolverol.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId,
                null,
                null);
        while (phone.moveToNext()) {
            telnum = phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
        }

        return telnum;
    }

}
