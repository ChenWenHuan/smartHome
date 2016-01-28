package com.smarthome.client2.view;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.smarthome.client2.R;
import com.smarthome.client2.unit.listener.OnNextClickListener;

public class BindGuideDialog extends Dialog {

	public BindGuideDialog(Context context, boolean cancelable,
			OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
		// TODO Auto-generated constructor stub
	}

	public BindGuideDialog(Context context, int theme) {
		super(context, theme);
		// TODO Auto-generated constructor stub
	}

	public BindGuideDialog(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	private ImageView iv_blink;
	private Button btn_next;
	private Timer timer;
	private int count = 0;
	private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			if(msg.what % 2 == 0)
			{
				iv_blink.setImageResource(R.drawable.wrist_red);
			}else {
				iv_blink.setImageResource(R.drawable.wrist_none);
			}
		}
	};
	
	private OnNextClickListener nextClickListener;
	
	public void setOnNextClickListener(OnNextClickListener nextClickListener)
	{
		this.nextClickListener = nextClickListener;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_bind_guide);
		
		iv_blink = (ImageView) findViewById(R.id.iv_blink);
		btn_next = (Button) findViewById(R.id.btn_next);
		timer = new Timer();
		timer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				count++;
				mHandler.sendEmptyMessage(count);
			}
		}, 0, 1500);
		btn_next.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				BindGuideDialog.this.dismiss();
				nextClickListener.onNext();
			}
		});
	}

}
