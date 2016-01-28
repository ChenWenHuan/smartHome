package com.smarthome.client2.activity;

import android.support.v4.app.Fragment;

public class FragmentModel {

	public String mTitle = "";
	public Fragment mFragment;
	
	public FragmentModel(String title, Fragment fg){
		mTitle = title;
		mFragment = fg;
	}
}
