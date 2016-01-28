package com.smarthome.client2.view;

import android.content.Context;
import android.graphics.drawable.PaintDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.smarthome.client2.R;

public class FriendDetailEditWindow{
	private Context ctx;
	private View current_view;
	
	private PopupWindow popupWindow;
	private TextView tv_delete_f_d_e_d,tv_cancle_f_d_e_d;
	
	public FriendDetailEditWindow(Context ctx){
		this.ctx = ctx;
		View current_view = LayoutInflater.from(ctx).inflate(R.layout.friend_detail_edit_dialog, null);
		
		tv_delete_f_d_e_d = (TextView) current_view.findViewById(R.id.tv_delete_f_d_e_d);
		tv_cancle_f_d_e_d = (TextView) current_view.findViewById(R.id.tv_cancle_f_d_e_d);
		
		popupWindow = new PopupWindow(current_view,LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT);
		popupWindow.setBackgroundDrawable(new PaintDrawable());
		popupWindow.setFocusable(true);
		popupWindow.setOutsideTouchable(true);
		
		tv_delete_f_d_e_d.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(deleteClickListener != null){
					deleteClickListener.delete();
				}
			}
		});
		
		tv_cancle_f_d_e_d.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(cancleClickListener != null){
					cancleClickListener.cancle();
				}else{
					dismissMyWindow();
				}
			}
		});
	}
	
	public void showMyWindowAt(View v,int gravity,int x,int y){
		popupWindow.showAtLocation(v, gravity, 0, 0);
	}
	
	public void dismissMyWindow(){
		if(popupWindow != null){
			popupWindow.dismiss();
		}
	}
	
	private DeleteClickListener deleteClickListener;
	private CancleClickListener cancleClickListener;
	
	public void setDeleteClickListener(DeleteClickListener deleteClickListener){
		this.deleteClickListener = deleteClickListener;
	}
	
	public void setCancleClickListener(CancleClickListener cancleClickListener){
		this.cancleClickListener = cancleClickListener;
	}
	
	public interface DeleteClickListener{
		void delete();
	}
	
	public interface CancleClickListener{
		void cancle();
	}
}
