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

public class CancelSubmitDialog
{

    private CancelSubmitDialog dialog = null;
    
    private Builder builder;
    private AlertDialog alertDialog;
    private Context ctx;
    private TextView tv_msg_cancel_submit;
    private TextView tv_submit_ok;
    private TextView tv_submit_cancel;
    
    public CancelSubmitDialog(Context ctx){
        this.ctx = ctx;
        builder = new Builder(ctx);
        
        View my_dialog_view = LayoutInflater.from(ctx).inflate(R.layout.cancel_submit_dialog_view, null);
        builder.setView(my_dialog_view);
        
        tv_msg_cancel_submit = (TextView) my_dialog_view.findViewById(R.id.tv_msg_cancel_submit);
        tv_submit_ok = (TextView) my_dialog_view.findViewById(R.id.tv_cancel_submit_ok);
        tv_submit_cancel = (TextView) my_dialog_view.findViewById(R.id.tv_cancel_submit_cancel);
        
        alertDialog = builder.create();
    }
    
    public CancelSubmitDialog getInstance(Context ctx){
        if(null == dialog){
            dialog = new CancelSubmitDialog(ctx);
        }
        return dialog;
    }
    
    public void setMsg(String msg){
        tv_msg_cancel_submit.setText(msg);
    }
    
    public void setSubmitMsg(String submitMsg){
        tv_submit_ok.setText(submitMsg);
    }
    
    public void setSubmitClick(OnClickListener clickListener){
        if(clickListener != null){
            tv_submit_ok.setOnClickListener(clickListener);
        }
    }
    
    public void clickCancel(OnClickListener clickListener)
    {
        if(clickListener != null){
            tv_submit_cancel.setOnClickListener(clickListener);
        }
    }
    
    public void showMyDialog(){
        if(alertDialog != null && !TextUtils.isEmpty(tv_msg_cancel_submit.getText()))
        alertDialog.show();
    }
    
    public void dismissMyDialog(){
        if(alertDialog != null){
            alertDialog.dismiss();
        }
    }
    

}
