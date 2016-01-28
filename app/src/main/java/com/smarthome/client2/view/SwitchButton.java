package com.smarthome.client2.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.smarthome.client2.R;

public class SwitchButton extends LinearLayout {
	
	private ImageView iv_switch;
	private TextView tv_switch;
	
	public boolean isSelected;

	public SwitchButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.button_switcher, this);
		iv_switch = (ImageView) findViewById(R.id.iv_switch);
		tv_switch = (TextView) findViewById(R.id.tv_switch);
		this.isSelected = false;
	}
	
	public void setImageResource(int resId)
	{
		this.iv_switch.setImageResource(resId);
	}
	
	public void setText(String text)
	{
		this.tv_switch.setText(text);
	}
	
	public SwitchButton(Context context)
	{
		super(context);
	}
	
	public void setTextColor(int alpha, int red, int green, int blue)
	{
		this.tv_switch.setTextColor(Color.argb(alpha, red, green, blue));
	}

	public void setSelected()
	{
		isSelected = true;
		setTextColor(255, 80, 184, 95);
		setBackgroundColor(Color.argb(255, 226, 226, 226));
	}
	
	public void setNormal()
	{
		isSelected = false;
		setTextColor(255, 92, 92, 92);
		setBackgroundColor(Color.argb(255, 215, 215, 215));
	}
}























