package com.smarthome.client2.util;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.smarthome.client2.R;

public class MyExceptionDialog {
	private MyExceptionDialog dialog = null;
	
	private Builder builder;
	private AlertDialog alertDialog;
	private Context ctx;
	private TextView tv_msg_my_excption_dialog,tv_submit_my_excption_dialog;
	
	public MyExceptionDialog(Context ctx){
		this.ctx = ctx;
		builder = new Builder(ctx);
		
		View my_dialog_view = LayoutInflater.from(ctx).inflate(R.layout.my_exception_dialog_view, null);
		builder.setView(my_dialog_view);
		
		tv_msg_my_excption_dialog = (TextView) my_dialog_view.findViewById(R.id.tv_msg_my_excption_dialog);
		tv_submit_my_excption_dialog = (TextView) my_dialog_view.findViewById(R.id.tv_submit_my_excption_dialog);
		
		alertDialog = builder.create();
	}
	
    public MyExceptionDialog getInstance(Context ctx){
		if(null == dialog){
			dialog = new MyExceptionDialog(ctx);
		}
		return dialog;
	}
	
	public void setMsg(String msg){
		tv_msg_my_excption_dialog.setText(msg);
	}
	
	public void setSubmitMsg(String submitMsg){
		tv_submit_my_excption_dialog.setText(submitMsg);
	}
	
	public void setSubmitClick(OnClickListener clickListener){
		if(clickListener != null){
			tv_submit_my_excption_dialog.setOnClickListener(clickListener);
		}
	}
	
	public void showMyDialog(){
		if(alertDialog != null && !TextUtils.isEmpty(tv_msg_my_excption_dialog.getText()))
		alertDialog.show();
	}
	
	public void dismissMyDialog(){
		if(alertDialog != null){
			alertDialog.dismiss();
		}
	}
	
}
