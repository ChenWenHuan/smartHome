package com.smarthome.client2.familySchool.ui;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.smarthome.client2.R;
import com.smarthome.client2.activity.CreateNewNextSteps;
import com.smarthome.client2.common.Constants;
import com.smarthome.client2.config.Preferences;
import com.smarthome.client2.familySchool.utils.FsConstants;
import com.smarthome.client2.familySchool.utils.HttpJson;
import com.smarthome.client2.familySchool.utils.MyHttpUtil;
import com.smarthome.client2.familySchool.utils.ResultParsers;
import com.smarthome.client2.util.HttpUtil;
import com.smarthome.client2.util.LinkTopSDKUtil;
import com.baidu.mapapi.model.LatLng;

/**
 * @author n003913
 * 发布留言、家庭作业、通知公告页面（教师）
 */
public class WriteMsgActivity extends BaseActivity
{

    private String ids;

    private String names;

    private EditText et_message;

    private TextView tv_submit;

    private ImageView iv_back;

    private String msgContent;

    private String classId;

    private String className;

    private String subjectId;

    private String subjectName;

    private TextView tvTitle;

    private int type = 0;

    private String linkDeviceID = "";
    private String linkDeviceAccount = "";
    private LinkTopSDKUtil linkInstance = null;
    
    private Handler mHandler = new Handler()
    {
        public void handleMessage(android.os.Message msg)
        {
            switch (msg.what)
            {
                case FsConstants.HTTP_START:
                    showProgressDialog(R.string.is_submitting);
                    break;
                case FsConstants.HTTP_SUCCESS:
                    String result = (String) msg.obj;
                    String code = ResultParsers.getCode(result);
                    if (code.equals("200"))
                    {
                        Preferences.getInstance(WriteMsgActivity.this)
                                .setPublishNew(true);
                        if(type == FsConstants.TYPE_ADD_FAMILY_MSG){
                        	showToast("发布成功！");
                        }else{
                        	showToast(R.string.publish_success);
                        }
                        Intent intent = WriteMsgActivity.this.getIntent();
                        WriteMsgActivity.this.setResult(RESULT_OK, intent);
                        finish();
                    }
                    else
                    {
                        if (type != FsConstants.TYPE_ADD_MSG
                                && code.equals("404"))
                        {
                            showToast("没有学生，发布失败");
                        }
                        else
                        {
                            showToast(R.string.server_offline);
                        }
                    }
                    break;
                case FsConstants.HTTP_FAILURE:
                    showToast(R.string.no_network);
                    break;
                case FsConstants.HTTP_FINISH:
                    removeProgressDialog();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fs_activity_add_msg);
        iv_back = (ImageView) findViewById(R.id.iv_back);
        et_message = (EditText) findViewById(R.id.et_message);
        tv_submit = (TextView) findViewById(R.id.tv_submit);
        tvTitle = (TextView) findViewById(R.id.tv_title);

        iv_back.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                iv_back.setImageResource(R.drawable.back_in);
                msgContent = et_message.getText().toString();
                if (msgContent != null && !msgContent.isEmpty())
                {
                    showCancelDialog();
                }
                else
                {
                    finish();
                }
            }
        });

        tv_submit.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                msgContent = et_message.getText().toString();
                if (msgContent != null && !msgContent.isEmpty())
                {
                    if (type == FsConstants.TYPE_ADD_MSG)
                    {
                        addMsg();
                    }
                    else if (type == FsConstants.TYPE_ADD_FAMILY_MSG){
                    	Log.e("----wzl--WriteMsgActivity-", "---before addFamilyMsg ");
                    	addFamilyMsg();
                    }
                    else if (type == FsConstants.TYPE_ADD_NOTICE)
                    {
                        addNotice();
                    }
                    else if (type == FsConstants.TYPE_ADD_HOMEWORK)
                    {
                        addHomework();
                    }
                    else if (type == FsConstants.TYPE_SEND_PERSONAL_MSG){
                    	sendMsgToDevice(linkDeviceID, msgContent);
                    }
                }
                else
                {
                    showToast(R.string.content_can_not_empty);
                }
            }
        });

        // 避免和ScrollView的滚动冲突
        et_message.setOnTouchListener(new OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                switch (event.getAction() & MotionEvent.ACTION_MASK)
                {
                    case MotionEvent.ACTION_UP:
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }
                return false;
            }
        });

        Intent intent = getIntent();
        type = intent.getIntExtra(FsConstants.TYPE_ADD_FLAG, -1);
        if (type == FsConstants.TYPE_ADD_MSG)
        {
            ids = intent.getStringExtra("ids");
            names = intent.getStringExtra("names");
            classId = intent.getStringExtra("classId");
            tvTitle.setText("发布留言");
        }
        else if (type == FsConstants.TYPE_ADD_NOTICE)
        {
            classId = intent.getStringExtra("classId");
            className = intent.getStringExtra("className");
            tvTitle.setText("发布通知");
        }
        else if (type == FsConstants.TYPE_ADD_HOMEWORK)
        {
            classId = intent.getStringExtra("classId");
            className = intent.getStringExtra("className");
            subjectId = intent.getStringExtra("subjectId");
            subjectName = intent.getStringExtra("subjectName");
            tvTitle.setText("发布作业");
        }else if (type == FsConstants.TYPE_ADD_FAMILY_MSG){
        	ids = intent.getStringExtra("ids");
        	tvTitle.setText("发布留言");
        }else if (type == FsConstants.TYPE_SEND_PERSONAL_MSG){
        	tvTitle.setText("发布留言");
        	linkDeviceID = intent.getStringExtra("deviceid");
        	linkDeviceAccount = intent.getStringExtra("deviceaccount");
        	if (!linkDeviceAccount.equals("")){
        		initLinkSDK(linkDeviceAccount);
        	}
        }
        
    }
    
    private void initLinkSDK(String linkaccount){
    	linkInstance = LinkTopSDKUtil.getInstance();
    	linkInstance.initSDK(this, mLinkTopHandler);
    	linkInstance.setupAccount(linkaccount, "888888");
//    	linkInstance.starLocationWatch("c20167de");
//    	linkInstance.getLatestPosition("c20167de");
//    	linkInstance.sentTxtMsg("c20167de", "发个文字信息看看！！！！");
    }
    
    private Handler mLinkTopHandler = new Handler()
    {

        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case LinkTopSDKUtil.LINK_SDK_COMMU_TXT_ACTION:
                	Log.e("-COMMU_TXT ACTION-", "-arg1=" + msg.arg1 + "--obj=" + msg.obj);
                	if(msg.arg1 == 200){
                		//文字信息发送成功
                		showToast("留言发送成功！！");
                		WriteMsgActivity.this.finish();
                	}else{
                		//文字信息发送失败
                		showToast("留言发送失败！！");
                	}
                	break;
                default:
                    break;
            }
        }
    };
    
    private void sendMsgToDevice(String deviceid, String msgContent){
    	
    	linkInstance.sentTxtMsg(deviceid, msgContent);
    }

    /**
     * 发布留言
     */
    private void addMsg()
    {
        HttpJson params = new HttpJson();
        params.put("students", ids);
        params.put("nameArray", names);
        params.put("content", msgContent);
        params.put("classId", classId);
        MyHttpUtil.post("/homeandschool/leaveMsg.action", params, mHandler);
    }
    
    private void addFamilyMsg(){
    	
    	HttpJson params = new HttpJson();
        params.put("familyId", ids);
        params.put("title","");
        params.put("content",msgContent);
        MyHttpUtil.post("/family/addFamilyLeavMsgTxt.action", params, mHandler);
    	
//          new AddFamilyMsgTask().execute();
    	
    }
    
	class AddFamilyMsgTask extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {
			String result = null;
			try {
				String serverUrl = HttpUtil.BASE_URL + "family/addFamilyLeavMsg.action";
				Map<String, String> paramMap = new HashMap<String, String>();
				paramMap.put("familyId",ids);
				paramMap.put("msgType","01");
				paramMap.put("title","");
				paramMap.put("content",msgContent);
				result = HttpUtil.addReplyFamilyMsg(paramMap, null, serverUrl);
			} catch (ClientProtocolException e) {
				Log.e("---wzl---AddFamilyMsgTask----", "--ClientProtocolException--");
			} catch (IOException e) {
				Log.e("---wzl---AddFamilyMsgTask----", "--IOException--");
			}
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			int resultcode = Constants.UNKNOW_RESULT;
			if (!TextUtils.isEmpty(result)) {
				boolean isCode = false;
				try {
					resultcode = Integer.parseInt(result);
					isCode = true;
				} catch (NumberFormatException e) {
					isCode = false;
				}
				if (isCode) {
				} else {
					
				}
			}
		}
	}
	


    
   

    /**
     * 发布通知公告
     */
    private void addNotice()
    {
        HttpJson params = new HttpJson();
        params.put("classes", classId);
        params.put("content", msgContent);
        params.put("incept_desc", className);
        MyHttpUtil.post("/homeandschool/sendNotice.action", params, mHandler);
    }

    /**
     * 发布家庭作业
     */
    private void addHomework()
    {
        HttpJson params = new HttpJson();
        params.put("classes", classId);
        params.put("content", msgContent);
        params.put("subjectid", subjectId);
        params.put("subjectname", subjectName);
        MyHttpUtil.post("/homeandschool/assignHomeWork.action",
                params,
                mHandler);
    }

    @Override
    public void onBackPressed()
    {
        msgContent = et_message.getText().toString();
        if (msgContent != null && !msgContent.isEmpty())
        {
            showCancelDialog();
        }
        else
        {
            finish();
        }
    }

}
