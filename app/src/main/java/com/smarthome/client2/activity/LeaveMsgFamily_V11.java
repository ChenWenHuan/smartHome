package com.smarthome.client2.activity;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.View.OnLayoutChangeListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.smarthome.client2.R;
import com.smarthome.client2.activity.LeaveMsgAdapterFamily_V11.FamilyReplyListener;
import com.smarthome.client2.common.Constants;
import com.smarthome.client2.config.Preferences;
import com.smarthome.client2.familySchool.adapter.HomeFamilyClassListAdapter;
import com.smarthome.client2.familySchool.model.LeaveMessage;
import com.smarthome.client2.familySchool.model.MessageReply;
import com.smarthome.client2.familySchool.ui.WriteMsgActivity;
import com.smarthome.client2.familySchool.utils.FsConstants;
import com.smarthome.client2.familySchool.utils.HttpJson;
import com.smarthome.client2.familySchool.utils.LogUtil;
import com.smarthome.client2.familySchool.utils.MyHttpUtil;
import com.smarthome.client2.familySchool.utils.ResultParsers;
import com.smarthome.client2.familySchool.view.AbOnListViewListener;
import com.smarthome.client2.familySchool.view.AbPullListView;
import com.smarthome.client2.widget.CircleImageView;

public class LeaveMsgFamily_V11 extends BaseActivity {
	
	 private ImageView iv_back;

	    private AbPullListView lv;

	    private ArrayList<LeaveMessage> list;

	    private LeaveMsgAdapterFamily_V11 adapter;

	    private long pageID = 1;
	    
	    private String familyID;

	    private String mMemID;

	    private String name;

	    private boolean isAll = false;

	    private LinearLayout llReply;
	    
	    private TextView tvNewMsg;

	    /**
	     * 回复输入框
	     */
	    private EditText etReply;

	    /**
	     * 回复发送按钮
	     */
	    private TextView tvReply;

	    private FamilyReplyListener replyListener;

	    /**
	     * 被选中回复的留言
	     */
	    private LeaveMessage repliedMsg;

	    private String repliedContent;

	    /**
	     * 记录llReply在最底部，即从隐藏到显示时在父布局中的bottom位置
	     */
	    private int replyBottom;

	    private float downY;

	    private float upY;

	    private Handler mHandler = new Handler()
	    {
	        @Override
	        public void handleMessage(Message msg)
	        {
	            super.handleMessage(msg);
	            switch (msg.what)
	            {
	                case FsConstants.HTTP_SUCCESS:
	                    if (lv.isRefreshing())
	                    {
	                        lv.stopRefresh();
	                    }
	                    if (lv.isLoading())
	                    {
	                        lv.stopLoadMore();
	                    }
	                    String result = (String) msg.obj;
	                    String code = ResultParsers.getCode(result);
	                    if (code.equals("200"))
	                    {
	                        ArrayList<LeaveMessage> temp = parserMessage(result);
	                        if (temp == null)
	                        {
	                        	 lv.getFooterView()
                                 .setState("暂无家庭留言！");
	                        }
	                        else if (temp.isEmpty())
	                        {
	                            if (list.isEmpty())
	                            {
		                        	 lv.getFooterView().setState("暂无家庭留言！");
	                            }
	                            else
	                            {
	                                lv.getFooterView()
	                                        .setState(getString(R.string.already_all));
	                                isAll = true;
	                            }
	                        }
	                        else
	                        {
	                            if (pageID == 1)
	                            {
	                                isAll = false;
	                                list.clear();
	                            }
	                            list.addAll(temp);
	                            pageID ++;
	                            if (temp.size() < 10)
	                            {
	                                lv.getFooterView()
	                                        .setState(getString(R.string.already_all));
	                            }
	                            else
	                            {
	                                lv.getFooterView()
	                                        .setState(getString(R.string.pulllist_load_more));
	                            }
	                        }
	                    }
	                    else
	                    {
	                        showToast(R.string.server_offline);
	                    }
	                    break;
	                case FsConstants.HTTP_FAILURE:
	                    if (lv.isRefreshing())
	                    {
	                        lv.stopRefresh();
	                    }
	                    if (lv.isLoading())
	                    {
	                        lv.stopLoadMore();
	                    }
	                    showToast(R.string.no_network);
	                    break;
	                case FsConstants.HTTP_FINISH:
	                    adapter.notifyDataSetChanged();
	                    break;
	                default:
	                    break;
	            }
	        }
	    };
	    
	    public  ArrayList<LeaveMessage> parserMessage(String jsonStr)
	    {
	        try
	        {
	            ArrayList<LeaveMessage> list = new ArrayList<LeaveMessage>();
	            JSONObject jObject = new JSONObject(jsonStr);
	            JSONArray jArray = jObject.getJSONArray("data");
	            int length = jArray.length();
	            for (int i = 0; i < length; i++)
	            {
	                jObject = jArray.getJSONObject(i);
	                String id = jObject.getString("id");
	                String publisher = jObject.getString("cwName");
	                String publisherId = jObject.getString("pubUserId");
	                String content = jObject.getString("content");
	                String time = jObject.getString("crtTime");
	                if (time != null && time.length() >= 19)
	                {
	                    time = time.substring(0, 19);
	                }
	                String headurl = jObject.getString("headPicPath");
	                JSONArray childArray = jObject.getJSONArray("leavMsgReplyList");
	                ArrayList<MessageReply> replyList = new ArrayList<MessageReply>();
	                for (int j = 0; j < childArray.length(); j++)
	                {
	                    jObject = childArray.getJSONObject(j);
	                    String reply_user_id = jObject.getString("replyUserId");
	                    String itemContent = jObject.getString("content");
	                    String replyUsername = jObject.getString("cwName");
	                    // 家长身份查看留言，把其他家庭的留言回复过滤掉
                        replyList.add(new MessageReply(null, "",
                                reply_user_id, "", itemContent, null,
                                replyUsername, ""));
	                }
	                list.add(new LeaveMessage(id, headurl, publisher, publisherId,
	                        time, content, replyList));
	            }
	            return list;
	        }
	        catch (JSONException e)
	        {
	            e.printStackTrace();
	            LogUtil.e("JSONException", "JSONException");
	            return null;
	        }
	    }

	    private Handler replyHandler = new Handler()
	    {
	        public void handleMessage(Message msg)
	        {
	            switch (msg.what)
	            {
	                case FsConstants.HTTP_START:
	                    tvReply.setEnabled(false);
	                    break;
	                case FsConstants.HTTP_SUCCESS:
	                    String result = (String) msg.obj;
	                    String code = ResultParsers.getCode(result);
	                    if (code.equals("200"))
	                    {
	                        showToast("回复成功");
	                        // 隐藏软键盘
	                        InputMethodManager imm = (InputMethodManager) LeaveMsgFamily_V11.this.getSystemService(Context.INPUT_METHOD_SERVICE);
	                        imm.hideSoftInputFromWindow(etReply.getWindowToken(), 0);
	                        repliedMsg.addReply(new MessageReply(null,
	                                repliedMsg.getId(), mMemID,
	                                repliedMsg.getPublisherId(), repliedContent,
	                                null, "我", repliedMsg.getPublisher()));
	                        adapter.notifyDataSetChanged();
	                    }
	                    else
	                    {
	                        showToast(R.string.server_offline);
	                    }
	                    break;
	                case FsConstants.HTTP_FAILURE:
	                    showToast(R.string.no_network);
	                    break;
	                case FsConstants.HTTP_FINISH:
	                    tvReply.setEnabled(true);
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
	        requestWindowFeature(Window.FEATURE_NO_TITLE);
	        setContentView(R.layout.fs_activity_leave_message_family);
	        tvNewMsg = (TextView)findViewById(R.id.tv_new_msg);
	        tvNewMsg.setVisibility(View.VISIBLE);
	        tvNewMsg.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = null;
					intent = new Intent(LeaveMsgFamily_V11.this, WriteMsgActivity.class);
					intent.putExtra("ids", familyID);
					intent.putExtra(FsConstants.TYPE_ADD_FLAG, FsConstants.TYPE_ADD_FAMILY_MSG);
//					LeaveMsgFamily_V11.this.startActivity(intent);
					LeaveMsgFamily_V11.this.startActivityForResult(intent, 0);
				}
			});
	        iv_back = (ImageView) findViewById(R.id.iv_back);
	        lv = (AbPullListView) findViewById(R.id.lv_leave_message);
	        llReply = (LinearLayout) findViewById(R.id.ll_reply);
	        etReply = (EditText) findViewById(R.id.et_leave_message);
	        tvReply = (TextView) findViewById(R.id.tv_submit);

	        lv.getHeaderView().setHeaderProgressBarDrawable(this.getResources()
	                .getDrawable(R.drawable.fs_pull_progress));
	        lv.getFooterView().setFooterProgressBarDrawable(this.getResources()
	                .getDrawable(R.drawable.fs_pull_progress));

	        Intent mIntent = getIntent();
	        familyID = mIntent.getStringExtra("familyid");
	        mMemID = mIntent.getStringExtra("id");
	        name = mIntent.getStringExtra("name");

	        initReplyListener();

	        list = new ArrayList<LeaveMessage>();
	        adapter = new LeaveMsgAdapterFamily_V11(this, list,imgHandler,replyListener);
	        lv.setAdapter(adapter);
	        lv.setAbOnListViewListener(new AbOnListViewListener()
	        {
	            @Override
	            public void onRefresh()
	            {
	                pageID =1;
	                getMessage();
	            }

	            @Override
	            public void onLoadMore()
	            {
	                if (isAll)
	                {
	                    lv.stopLoadMore();
	                    lv.getFooterView()
	                            .setState(getString(R.string.already_all));
	                    return;
	                }
	                getMessage();
	            }
	        });

	        lv.setOnTouchListener(new OnTouchListener()
	        {
	            @Override
	            public boolean onTouch(View v, MotionEvent event)
	            {
	                if (llReply.getVisibility() == View.VISIBLE)
	                {
	                    if (event.getAction() == MotionEvent.ACTION_DOWN)
	                    {
	                        downY = event.getY();
	                    }
	                    else if (event.getAction() == MotionEvent.ACTION_UP)
	                    {
	                        upY = event.getY();
	                    }
	                    // 判定用户未滚动lv，隐藏回复视图
	                    if (Math.abs(upY - downY) <= 10)
	                    {
	                        InputMethodManager imm = (InputMethodManager) LeaveMsgFamily_V11.this.getSystemService(Context.INPUT_METHOD_SERVICE);
	                        imm.hideSoftInputFromWindow(etReply.getWindowToken(), 0);
	                    }
	                }
	                return false;
	            }
	        });

	        // 按下back实体键后，软键盘消失，希望回复视图一同消失，所以添加此监听
	        llReply.addOnLayoutChangeListener(new OnLayoutChangeListener()
	        {
	            @Override
	            public void onLayoutChange(View v, int left, int top, int right,
	                    int bottom, int oldLeft, int oldTop, int oldRight,
	                    int oldBottom)
	            {
	                LogUtil.i("bottom", "old=" + oldBottom + "new=" + bottom);
	                if (llReply.getVisibility() == View.VISIBLE
	                        && bottom == replyBottom && replyBottom != 0)
	                {
	                    mHandler.post(new Runnable()
	                    {
	                        public void run()
	                        {
	                            llReply.setVisibility(View.GONE);
	                            replyBottom = 0;
	                        }
	                    });
	                }
	                if (replyBottom == 0)
	                {
	                    replyBottom = bottom;
	                }
	            }
	        });

	        OnClickListener listener = new OnClickListener()
	        {
	            @Override
	            public void onClick(View v)
	            {
	                switch (v.getId())
	                {
	                    case R.id.iv_back:
	                        if (llReply.getVisibility() == View.VISIBLE)
	                        {
	                            InputMethodManager imm = (InputMethodManager) LeaveMsgFamily_V11.this.getSystemService(Context.INPUT_METHOD_SERVICE);
	                            imm.hideSoftInputFromWindow(etReply.getWindowToken(),
	                                    0);
	                        }
	                        else
	                        {
	                            iv_back.setImageResource(R.drawable.back_in);
	                            finish();
	                        }
	                        break;
	                    case R.id.tv_submit:
	                        replyJudge();
	                        break;
	                    default:
	                        break;
	                }

	            }
	        };
	        iv_back.setOnClickListener(listener);
	        tvReply.setOnClickListener(listener);

	        lv.performRefresh();
	    }

	    /**
	     * 加载留言
	     */
	    private void getMessage()
	    {
	        HttpJson pJson = new HttpJson();
	        pJson.put("familyId", familyID);
	        pJson.put("pageNum", pageID);

	        pJson.put("limit", 10);
	        MyHttpUtil.post("/family/getFamLeavMsgWithReply.action", pJson, mHandler);
	    }

	    private void initReplyListener()
	    {
	        replyListener = new FamilyReplyListener()
	        {
	            @Override
	            public void onReply(LeaveMessage msg)
	            {
	                llReply.setVisibility(View.VISIBLE);
	                repliedMsg = msg;
	                etReply.setText(null);
	                etReply.setHint("回复" + msg.getPublisher() + "（不能超过100字）");
	                etReply.requestFocus();
	                InputMethodManager imm = (InputMethodManager) LeaveMsgFamily_V11.this.getSystemService(Context.INPUT_METHOD_SERVICE);
	                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,
	                        InputMethodManager.HIDE_IMPLICIT_ONLY);
	            }
	        };
	    }

	    private void replyJudge()
	    {
	        repliedContent = etReply.getText().toString();
	        if (repliedContent != null && !repliedContent.isEmpty())
	        {
	            HttpJson params = new HttpJson();
	            params.put("familyLeavMsgId", repliedMsg.getId());
	            params.put("content", repliedContent);
	            MyHttpUtil.post("/family/addFamilyLeavMsgReplyTxt.action",
	                    params,
	                    replyHandler);
	        }
	        else
	        {
	            showToast(R.string.content_can_not_empty);
	        }
	    }
	    

	    
		private Handler imgHandler = new Handler()
	    {
	        public void handleMessage(Message msg)
	        {

	            switch (msg.what)
	            {
	                
	                case FsConstants.HANDLE_IMAGE:
	                	handleImageLoader(msg);
	                	break;
	                default:
	                	break;

	            }
	        }
		};
	    
	    private void handleImageLoader(Message msg){
	    	
	    	  Bitmap bm;
	          String tag;
	          
	          bm = (Bitmap)msg.obj;
	          tag = msg.getData().getString("tag");

	      	CircleImageView headView = (CircleImageView)lv.findViewWithTag(tag);

	      	if (bm != null  && headView != null){
	      		headView.setImageBitmap(bm);
	      	}
	    }

		@Override
		protected void onActivityResult(int requestCode, int resultCode,
				Intent data) {
			// TODO Auto-generated method stub
			if(resultCode == RESULT_OK && requestCode == 0){
	            pageID =1;
	            getMessage();
			}
			super.onActivityResult(requestCode, resultCode, data);
		}
	    
	    
	    
	    
}
