package com.smarthome.client2.util;

import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import com.smarthome.client2.R;
import com.smarthome.client2.activity.LocationActivity.GK309FenceListener;
import com.smarthome.client2.activity.LocationActivity.LastPosListener;
import com.smarthome.client2.activity.OCAlarmActivity.GK309OCAlarmListener;
import com.smarthome.client2.common.Constants;

public class NetStatusListener {

	public static boolean mClickflag = false;

	private final static int SUM_MINUTE = 3 * 1000 * 60;

	private final static int PEIROD_MINUTE = 1000 * 5;

	private final static int WAITING = 1;

	private final static int ORDERING = 2;

	private final static int EXECUTING_SUCCESS = 3;

	private final static int EXECUTING_FAIL = 4;

	private final static int SERVER_ERROR = 5;

	private final static int TIME_OUT = 6;

	private final static int EXECUTING_SUCCESS_WITH_DIALOG_DISMISS = 7;

	private final static int EXECUTING_ABORT_WITH_DIALOG_DISMISS = 8;

	public static NetStatusListener instance;

	public static NetStatusListener getInstance() {
		if (instance == null) {
			instance = new NetStatusListener();
		}
		return instance;
	}

	private Context context;

	private Dialog dialog;

	private String content;

	private int status = 0;

	private boolean isSettingFinish = false;

	private boolean isSettingFail = false;

	private static boolean isActivityFinish = false;

	private Toast customToast;

	public NetStatusListener() {
		isRunning = true;
	}

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (isActivityFinish) {
				this.removeCallbacksAndMessages(null);
				isActivityFinish = false;
				return;
			}
			if (context != null) {
				if (!HttpUtil.isNetworkAvailable(context)) {
					Toast.makeText(context,
							HttpUtil.responseHandler(context,
									Constants.NO_NETWORK),
							Toast.LENGTH_SHORT).show();
					return;
				}
			}
			try {
				switch (msg.what) {
					case WAITING:

						if (dialog != null) {
							dialog.setOnKeyListener(new OnKeyListener() {

								@Override
								public boolean onKey(DialogInterface arg0,
								                     int keyCode, KeyEvent event) {
									if (keyCode == KeyEvent.KEYCODE_BACK
											&& isRunning) {
										isRunning = false;
									}
									return false;
								}
							});
							content = context.getString(R.string.netlistener_waiting);
							if (lastPosListener != null) {

							}

						}
						break;
					case ORDERING:
						content = context.getString(R.string.netlistener_order);
						if (lastPosListener != null) {

						}

						break;
					case EXECUTING_SUCCESS:
						isSettingFinish = true;
						if (mGK309OCAlarmListener == null) {

							if (dialog != null && dialog.isShowing()) {
								dialog.dismiss();
							}
                            Toast.makeText(context,
                                    "指令执行成功！",
                                    Toast.LENGTH_SHORT).show();
						}
						break;
					case EXECUTING_FAIL:

						interuptThread();
						Toast.makeText(context,
								context.getString(R.string.netlistener_set_fail),
								Toast.LENGTH_SHORT)
								.show();
						break;
					case SERVER_ERROR:
						interuptThread();
						Log.d("", "网络连接失败");
						break;
					case TIME_OUT:
						interuptThread();
						Toast.makeText(context,
								context.getString(R.string.netlistener_timeout),
								Toast.LENGTH_SHORT)
								.show();
						if (dialog != null) {
							dialog.dismiss();
						}
						break;
					case EXECUTING_SUCCESS_WITH_DIALOG_DISMISS:
                        if (dialog != null && dialog.isShowing())
                        {
                            dialog.dismiss();
                        }

						break;
					case EXECUTING_ABORT_WITH_DIALOG_DISMISS:
						abortTask();
						break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	};

	private void interuptThread() {
		if (!Thread.interrupted()) {
			Log.d("", "daitm-----task is abort");
			if (taskThread != null) {
				taskThread.interrupt();
			}
		}
		if (dialog != null && dialog.isShowing()) {
			dialog.dismiss();
		}
		isRunning = false;
		dialog = null;
		mClickflag = false;
		isSettingFinish = false;
	}

	public void abortTask() {
		interuptThread();
		Toast.makeText(context,
				context.getString(R.string.netlistener_interupt),
				Toast.LENGTH_SHORT).show();
	}

	private static boolean isRunning = false;

	private Thread taskThread;

	private Thread checkThread;

	public void executeTask(final int taskId, Context ctx) {
		mHandler.sendEmptyMessage(WAITING);
		Log.d("", "daitm----ready");
		taskThread = new Thread(new Runnable() {
			@Override
			public void run() {
				boolean isExecute = false;
				int newId = taskId;
				long t1 = System.currentTimeMillis();
				long t2 = t1;
				while (t2 - t1 < SUM_MINUTE) {
					if (!isRunning) {
						Log.d("", "daitm-----netstatus-------task is abort");
						break;
					}
					if (isSettingFinish) {
						Log.d("",
								"daitm-----netstatus-------device set success");
						if (dialog != null && dialog.isShowing()) {
							dialog.dismiss();
						}
						break;
					}
					isExecute = false;
					t2 = System.currentTimeMillis();
					if ((t2 - t1) % PEIROD_MINUTE == 0) {
						Log.d("", "daitm----in the net");
						if (!isExecute) {
							//                                Thread.sleep(100);
							isExecute = true;
							getCommandStatusFromServer(newId, context);
							Log.d("", "daitm---------taskId:" + newId
									+ "-------running");
						}
					}
				}
				if (isRunning) {
					if (status != EXECUTING_SUCCESS && dialog != null
							&& dialog.isShowing()) {
						isSettingFail = true;
						mHandler.sendEmptyMessage(TIME_OUT);
						return;
					}
					Log.d("", "daitm---------taskId:" + newId + "-------stoped");
					if (lastPosListener != null) {
						Log.d("", "daitm-------execute-----lastPos");
						lastPosListener.getLastPosListener();
						lastPosListener = null;
					}
					if (mFenceListener != null) {
						Log.d("", "daitm-------execute-----GK309 fence");
						mFenceListener.getGK309FenceSuccess();
						mFenceListener = null;
					}
					if (mGK309OCAlarmListener != null) {
						Log.d("", "daitm-------execute-----GK309 ocalarm");
						mGK309OCAlarmListener.getGK309OCAlarmSuccess();
						mGK309OCAlarmListener = null;
					}
					mClickflag = false;
					isRunning = false;
				}
			}
		});
		taskThread.start();
	}

	private void getCommandStatusFromServer(int dataId, Context ctx) {
		JSONObject obj = new JSONObject();
		try {
			obj.put("seriaNum", dataId);
			Log.d("shishi", "dataId is " + dataId);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		RequestResult result = new RequestResult();
		HttpUtil.postRequest(obj, Constants.GET_COMMAND_STATUS, result, ctx);
		try {
			if (!TextUtils.isEmpty(result.getResult())) {
				JSONObject json = new JSONObject(result.getResult());
				JSONObject data = json.getJSONObject("data");
				if (data.getInt("status") == 200) {
					Log.d("", "daitm-------status---" + data.getInt("data"));
					status = data.getInt("data");
					mHandler.sendEmptyMessage(data.getInt("data"));
				} else {
					mHandler.sendEmptyMessage(SERVER_ERROR);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private LastPosListener lastPosListener;

	public void setLastPosListener(LastPosListener lastPosListener) {
		this.lastPosListener = lastPosListener;
	}

	private GK309FenceListener mFenceListener;

	public void setGK309FenceListener(GK309FenceListener fenceListener) {
		this.mFenceListener = fenceListener;
	}

	private GK309OCAlarmListener mGK309OCAlarmListener;

	public void setGK309OCAlarmListener(GK309OCAlarmListener alarmListener) {
		this.mGK309OCAlarmListener = alarmListener;
	}

	public void parseNetStatusJson(String msg, Context ctx,
	                               Dialog dialog) {
		try {
			checkAbortTimeing();
			this.dialog = dialog;
			this.context = ctx;
			JSONObject json = new JSONObject(msg);
			int retcode = json.getInt("retcode");
			if (json.has("data")) {
				JSONObject data = json.getJSONObject("data");
				if (TextUtils.isEmpty(data.getString("data"))
						&& !TextUtils.isEmpty(data.getString("errmsg"))) {
					Toast.makeText(ctx,
							data.getString("errmsg"),
							Toast.LENGTH_SHORT).show();
					if (dialog != null && dialog.isShowing()) {
						dialog.dismiss();
					}
					mClickflag = false;
					isSettingFail = true;
					if (mFenceListener != null) {
						mFenceListener.getGK309FenceSuccess();
					}
					return;
				}
				if (!TextUtils.isEmpty(data.getString("data"))) {
					executeTask(data.getInt("data"), ctx);
				}
			} else {
				Toast.makeText(ctx,
						ctx.getString(R.string.netlistener_server_off),
						Toast.LENGTH_SHORT).show();
				if (dialog != null && dialog.isShowing()) {
					dialog.dismiss();
				}
				mClickflag = false;
				isSettingFail = true;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public void setActivityFinish() {
		Log.d("", "daitm-----activity is finish and netstatuslistener is over");
		isActivityFinish = true;
		if (taskThread != null) {
			mHandler.removeCallbacks(taskThread);
		}
		if (checkThread != null) {
			mHandler.removeCallbacks(checkThread);
		}
	}

	private void initToast() {
		customToast = Toast.makeText(context,
				context.getString(R.string.netlistener_set_success),
				Toast.LENGTH_LONG);
		execToast(customToast, 3);
	}



	private void execToast(final Toast toast, final int cnt) {
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			               @Override
			               public void run() {
				               Log.d("", "daitm---toast---times---" + cnt);
				               mHandler.sendEmptyMessage(EXECUTING_SUCCESS_WITH_DIALOG_DISMISS);
				               showMyToast(customToast, cnt - 1);
				               if (cnt - 1 < 0) {
					               customToast = null;
				               }
			               }
		               },
				2000);
	}

	private void showMyToast(Toast toast, int cnt) {
		if (cnt < 0)
			return;
		if (toast != null) {
			toast.show();
			execToast(toast, cnt);
		}
	}

	private void checkAbortTimeing() {
		checkThread = new Thread(new Runnable() {

			@Override
			public void run() {
				while (true) {
					if (!isRunning && !isSettingFinish && !isSettingFail) {
						Log.d("", "daitm----task is abort or execute success");
						mHandler.sendEmptyMessage(EXECUTING_ABORT_WITH_DIALOG_DISMISS);
						break;
					}
				}
			}
		});
		checkThread.start();
	}

	public Toast getCustomToast() {
		return customToast;
	}

	public void setCustomToast(Toast customToast) {
		this.customToast = customToast;
	}

	public boolean isRunning() {
		return isRunning;
	}

	public void setRunning(boolean isRunning) {
		NetStatusListener.isRunning = isRunning;
	}

	public boolean cancleToast() {
		if (NetStatusListener.this != null
				&& NetStatusListener.this.getCustomToast() != null) {
			NetStatusListener.this.getCustomToast().cancel();
			NetStatusListener.this.setCustomToast(null);
			Log.d("", "daitm----toast is cancled");
			return false;
		}
		return true;
	}

}
