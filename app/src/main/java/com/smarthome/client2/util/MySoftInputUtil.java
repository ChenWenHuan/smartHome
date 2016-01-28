package com.smarthome.client2.util;

import android.content.Context;
import android.os.IBinder;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class MySoftInputUtil {
	/**
	 * Hides the input method.
	 */
	public static void hideInputMethod(Context context, View focusView) {
		if (null == context || null == focusView) {
			return;
		}
		InputMethodManager imm = (InputMethodManager) context
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		if (imm != null) {
			boolean isHideSoftInput = imm.hideSoftInputFromWindow(
					focusView.getWindowToken(), 0);
		}
	}

	public static void toggleInputMethod(Context context) {
		InputMethodManager imm = (InputMethodManager) context
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		// 得到InputMethodManager的实例
		if (imm.isActive()) {
			// 如果开启
			imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT,
					InputMethodManager.HIDE_NOT_ALWAYS);
			// 关闭软键盘，开启方法相同，这个方法是切换开启与关闭状态的
		}
	}
	
	public static void showInputMethod(Context context, View focusView) {
		if (null == context || null == focusView) {
			return;
		}
		InputMethodManager imm = (InputMethodManager) context
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		if (imm != null) {
			boolean isShowSoftInput = imm.showSoftInput(
					focusView, InputMethodManager.SHOW_FORCED);
		}
	}

}
