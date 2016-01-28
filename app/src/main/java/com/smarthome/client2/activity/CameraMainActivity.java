package com.smarthome.client2.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.smarthome.client2.R;
import com.smarthome.client2.SmartHomeApplication;
import com.smarthome.client2.config.Preferences;
import com.smarthome.client2.util.IchaoCameraUtil;
import com.smarthome.client2.util.TopBarUtils;
import com.smarthome.client2.view.CustomActionBar;
import com.smarthome.client2.common.Constants;
import com.ichano.rvs.viewer.Command;
import com.ichano.rvs.viewer.Media;
import com.ichano.rvs.viewer.Viewer;
import com.ichano.rvs.viewer.bean.MediaDataDesc;
import com.ichano.rvs.viewer.bean.RvsCameraStreamInfo;
import com.ichano.rvs.viewer.callback.MediaStreamStateCallback;
import com.ichano.rvs.viewer.callback.StreamerStateListener;
import com.ichano.rvs.viewer.codec.AudioType;
import com.ichano.rvs.viewer.constant.MediaStreamState;
import com.ichano.rvs.viewer.constant.StreamerConfigState;
import com.ichano.rvs.viewer.constant.StreamerPresenceState;
import com.ichano.rvs.viewer.render.GLViewYuvRender;
import com.ichano.rvs.viewer.render.GLViewYuvRender.RenderYUVFrame;
import com.squareup.picasso.Picasso;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class CameraMainActivity extends Activity implements StreamerStateListener, 
										OnTouchListener,OnClickListener,
										MediaStreamStateCallback, RenderYUVFrame{

	private static final String TAG = "CameraMain";
	public static final int CAMERA_CONNECTED_MSG_ID = 0x0001;
	public static final int CAMERA_PTZ_STEPS = 5;
	public static final int MSG_TAKE_PIC_OK = 6;


	private Viewer viewer;
	private Media media;
	private Command command;

	private long liveStreamId;
	private long decoderId;
	

	private long streamerCid;// 测试采集端cid
	
	private FrameLayout fl_head;
    private CustomActionBar actionBar;

	private RelativeLayout surfaceViewLayout;
	private GLSurfaceView glSurfaceView;

	private GLViewYuvRender myRenderer;
	private AudioHandler audioHandler;
	private AudioSend    audioSend;

	private ProgressDialog loadingDialog;
//	private LinearLayout add_layout;
	private ImageView imgVideoRecord;
	private ImageView imgTakePicture;
	private ImageView imgVoiceSend;
	private ImageView imgPictureShow;
	private String familyId;
	private String cameraName;
	private String cameraCid;
	private String cameraUserName;
	private String cameraPass;
	private int mConntectCount = 0;
	private boolean isVideoRecording = false;
	private long sendAudioStreamId;
	private boolean takePictureFlag = false;
	
	private GestureDetector mGestureDetector;
    private int surfaceViewWidth;
    private int surfaceViewHeight;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
		
		setContentView(R.layout.camera_main_activity);
		
		Bundle bundleData = this.getIntent().getExtras();
		cameraName = bundleData.getString(CameraDeviceList.CAMERA_SHOW_NAME_KEY);
		cameraCid = bundleData.getString(CameraDeviceList.CAMERA_CID_KEY);
		cameraUserName = bundleData.getString(CameraDeviceList.CAMERA_USER_NAME_KEY);
		cameraPass = bundleData.getString(CameraDeviceList.CAMERA_USER_PASS_KEY);
		familyId = bundleData.getString("familyId");
		mGestureDetector = new GestureDetector(this, new CameraGestureListener());

		loadingDialog = new ProgressDialog(this);
		imgPictureShow = (ImageView)findViewById(R.id.img_picture);
		imgPictureShow.setOnClickListener(this);
		String picName = Preferences.getInstance(CameraMainActivity.this).getCameraPicPath();
		if(!TextUtils.isEmpty(picName)){
			Picasso.with(this).load(new File(picName)).into(imgPictureShow);
		}
		imgVideoRecord = (ImageView)this.findViewById(R.id.img_record);
		imgVideoRecord.setOnClickListener(this);
		imgTakePicture = (ImageView)this.findViewById(R.id.img_pic);
		imgTakePicture.setOnClickListener(this);
		imgVoiceSend = (ImageView)this.findViewById(R.id.img_voice);
		imgVoiceSend.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				if (event.getAction() == MotionEvent.ACTION_UP){
					Log.e(TAG, "MotionEvent.ACTION_UP");
					audioSend.stopAudioSend();
				}
				if (event.getAction() == MotionEvent.ACTION_DOWN){
					Log.e(TAG, "MotionEvent.ACTION_DOWN");
					audioSend.startAudioSend();
				}
				return false;
			}
		});
//		add_layout = (LinearLayout) findViewById(R.id.add_layout);		
		surfaceViewLayout = (RelativeLayout) findViewById(R.id.glsurfaceviewlayout);
		surfaceViewLayout.setOnTouchListener(this);
		addTopBarToHead();
		glSurfaceView = new GLSurfaceView(this);
		glSurfaceView.setEGLContextClientVersion(2);
		myRenderer = new GLViewYuvRender();
		glSurfaceView.setRenderer(myRenderer);
		viewer = IchaoCameraUtil.getInstance().getViewer();
		media = IchaoCameraUtil.getInstance().getMedia();
		command = IchaoCameraUtil.getInstance().getCommand();
		IchaoCameraUtil.getInstance().setHandler(mHandle);


		viewer.setStreamerStateListener(this);
		media.setMediaStreamStateCallback(this);
		loadingDialog.setMessage("视频加载中...");
		loadingDialog.show();
		long delayTime = 1000;
		if (IchaoCameraUtil.getInstance().getLoginState()){
			delayTime = 1000;
		}else{
			delayTime = 10000;
		}
		mHandle.postDelayed(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				bindAvs();
			}
		}, delayTime);
	}
	
	 private void addTopBarToHead()    {
    	fl_head = (FrameLayout) findViewById(R.id.fl_header_home);
        if (actionBar != null){
        	fl_head.removeView(actionBar);
        }
        actionBar = TopBarUtils.createCustomActionBar(SmartHomeApplication.getInstance(),
                R.drawable.btn_back_selector,
                new OnClickListener() {
                    @Override
                    public void onClick(View v){
                        finish();
                    }
                },
                cameraName,
                R.drawable.ic_action_bar_menu_no_pressed,
                new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						finish();
						Intent intent = new Intent(CameraMainActivity.this, CameraDeviceList.class);
						intent.putExtra("familyid", familyId);
						CameraMainActivity.this.startActivity(intent);
						
					}
				});
        fl_head.addView(actionBar);
    }
	 
	 
	private Handler mHandle = new Handler()	 {
	    @Override
	    public void handleMessage(Message msg)
	    {
	        super.handleMessage(msg);
	        switch (msg.what)
	        {
	        case IchaoCameraUtil.MSG_CAMERA_CONNECTED_ID:
	        	if(cameraCid.equals(msg.obj.toString())){
	        		mConntectCount++;
	        		if(mConntectCount < 2){
	        			viewAvs();
	        		}
	        	}
	        	break;
	        case IchaoCameraUtil.MSG_CAMERA_DISCONNECTED_ID:
	        	if(cameraCid.equals(msg.obj.toString())){
	        		mConntectCount = 0;
	        	}
	        	break;
	        case MSG_TAKE_PIC_OK:
				Picasso.with(CameraMainActivity.this).load(new File(msg.obj.toString())).into(imgPictureShow);
				Preferences.getInstance(CameraMainActivity.this).setCameraPicPath(msg.obj.toString());
//	        	Toast.makeText(CameraMainActivity.this,"图片保存在：" + Constants.CAMERA_VIDEO_RECORD_PATH,
//	        					Toast.LENGTH_LONG).show();
	        	break;
            default:
                break;
	        }
	    }
	};

	@Override
	public void onStreamerPresenceState(long streamerCID,
			StreamerPresenceState state) {
		Log.d(TAG, "streamerCID :" + streamerCID + ",state:" + state);// 监听已绑定的采集端状态:在线、离线、用户名密码错误
	}

	@Override
	public void onStreamerConfigState(long streamerCID,
			StreamerConfigState state) {

	}

	public void bindAvs( ) {
		streamerCid = Long.parseLong(cameraCid);		
		Viewer.getViewer().connectStreamer(streamerCid, cameraUserName, cameraPass);
	}

	public void viewAvs() {	
//			surfaceViewLayout.addView(glSurfaceView);
			liveStreamId = media.openLiveStream(streamerCid, 0, 0, 0);// 测试打开实时视频流
			Log.e(TAG, "liveStreamId :" + liveStreamId);
			loadingDialog.setMessage("实时视频等待中...");
			loadingDialog.show();
	}

	@Override
	public void onMediaStreamState(long streamId, MediaStreamState state) {

		Log.e(TAG, "streamId :" + streamId + ",state:" + state.intValue());
        loadingDialog.dismiss();
		if (streamId ==liveStreamId && state == MediaStreamState.CREATED) {
			if(null == glSurfaceView.getParent()) {
                MediaDataDesc desc = media.getStreamDesc(liveStreamId);
                if (desc == null) {
                    Log.e(TAG, "get media desc error!");
                    return;
                }
                Log.d("media", "video :" + desc.getVideoType().toString() + ","
                        + desc.getVideoWidth() + "," + desc.getVideoHeight());
                Log.e("media", "audio :" + desc.getAudioType().toString() + ","
                        + desc.getSampRate());

                // 根据对端的音视频格式进行编码器初始化，不使用sdk内置h264解码器可以不用关心
                decoderId = media.initAVDecoder(desc.getAudioType(),
                        desc.getSampRate());


                myRenderer.setVideoDimension(desc.getVideoWidth(),
                        desc.getVideoHeight());

                myRenderer.setYuvDataRender(this);

                audioHandler = new AudioHandler(desc.getSampRate(),
                        desc.getChannel(), liveStreamId, decoderId, media);
                audioHandler.startAudioWorking();
                RvsCameraStreamInfo info = viewer.getStreamerInfoMgr().getStreamerInfo(streamerCid).getCameraInfo()[0].getCameraStreams()[0];
                int videoW = info.getWidth();
                int videoH = info.getHeight();
                if (0 != videoW && 0 != videoH) {
                    surfaceViewWidth = getResources().getDisplayMetrics().widthPixels;
                    surfaceViewHeight = (int) ((float) surfaceViewWidth / (float) videoW * (float) videoH);
                }
                if (0 != surfaceViewWidth && 0 != surfaceViewHeight) {
                    RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(surfaceViewWidth, surfaceViewHeight);
                    surfaceViewLayout.addView(glSurfaceView, lp);
                } else {
                    surfaceViewLayout.addView(glSurfaceView);
                }
//            surfaceViewLayout.addView(glSurfaceView);

                audioSend = new AudioSend(media, desc.getSampRate(), desc.getChannel(),
                        desc.getDepth(), Long.parseLong(cameraCid));
            }

		}else{
            stopWatch();
        }
	}

	public void sendCmd(View view) {
		viewer.getCommand().sendCustomData(streamerCid, "test cmd".getBytes());
	}
	
	private boolean yuvToJPEG(String filename, int width, int height, byte[] y, byte[] u, byte[] v){
		
		Bitmap bitmap;
		int yuvSize = width*height*3/2;
		int rgbSize = width*height*3;
		byte[] yuv = new byte[yuvSize];
		byte[] rgb = new byte[rgbSize];
		composizeYUV(yuv,y,u,v);
		YUV420SPtoRGB(rgb,yuv,width,height);
		bitmap = createMyBitmap(rgb,width,height);
		if (bitmap == null){
			return false;
		}
		File f = new File(filename);
        FileOutputStream fOut = null;
        try {
                fOut = new FileOutputStream(f);               
        } catch (FileNotFoundException e) {
                e.printStackTrace();
                return false;
        }
        
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
        try {
                fOut.flush();
        } catch (IOException e) {
                e.printStackTrace();
                return false;
        }
        try {
                fOut.close();
        } catch (IOException e) {
                e.printStackTrace();
                return false;
        }
        return true;
	}
	
	private void composizeYUV(byte[] yuv, byte[] y, byte[] u, byte[] v){
		
		int size = u.length;
		int yuvIndex = 4*size;
		Log.e(TAG,"composizeYUV yuvIndex=" + yuvIndex);
		for(int j=0; j<yuvIndex; j++){
			yuv[j] = y[j];
		}
		for (int i=0; i<size; i++ ){
			yuv[yuvIndex++] = u[i];
			yuv[yuvIndex++] = v[i];
		}
		
	}
	
	private void YUV420SPtoRGB(byte[] rgbBuf,byte[] yuv420sp,int width,int height){
		  //定义单通道数据长度  
        final int frameSize = width * height;     
     
        int i = 0, y = 0;     
        int uvp = 0, u = 0, v = 0;    
        int y1192 = 0, r = 0, g = 0, b = 0;     
        for (int j = 0, yp = 0; j < height; j++) {     
             uvp = frameSize + (j >> 1) * width;     
             u = 0;     
             v = 0;     
            for (i = 0; i < width; i++, yp++) {     
                 y = (0xff & ((int) yuv420sp[yp])) - 16;     
                if (y < 0) y = 0;     
                if ((i & 1) == 0) {     
                     v = (0xff & yuv420sp[uvp++]) - 128;     
                     u = (0xff & yuv420sp[uvp++]) - 128;     
                 }     
                     
                 y1192 = 1192 * y;     
                 r = (y1192 + 1634 * v);     
                 g = (y1192 - 833 * v - 400 * u);     
                 b = (y1192 + 2066 * u);     
                   //始终持 r g b在0 - 262143  
                if (r < 0) r = 0; else if (r > 262143) r = 262143;     
                if (g < 0) g = 0; else if (g > 262143) g = 262143;     
                if (b < 0) b = 0; else if (b > 262143) b = 262143;     
                   //安位运算，分别将一个像素点中的r g b 存贮在rgbBuf中  
                 rgbBuf[yp * 3] = (byte)(r >> 10);     
                 rgbBuf[yp * 3 + 1] = (byte)(g >> 10);     
                 rgbBuf[yp * 3 + 2] = (byte)(b >> 10);     
             }     
         }     
	}
	
	private  int[] convertByteToColor(byte[] data){
		int size = data.length;
		if (size == 0){
			return null;
		}		
		// 理论上data的长度应该是3的倍数，这里做个兼容
		int arg = 0;
		if (size % 3 != 0){
			arg = 1;
		}
		int []color = new int[size / 3 + arg];
		
		if (arg == 0){									//  正好是3的倍数
			for(int i = 0; i < color.length; ++i){
		
				color[i] = (data[i * 3] << 16 & 0x00FF0000) | 
						   (data[i * 3 + 1] << 8 & 0x0000FF00 ) | 
						   (data[i * 3 + 2] & 0x000000FF ) | 
						    0xFF000000;
			}
		}else{										// 不是3的倍数
			for(int i = 0; i < color.length - 1; ++i){
				color[i] = (data[i * 3] << 16 & 0x00FF0000) | 
				   (data[i * 3 + 1] << 8 & 0x0000FF00 ) | 
				   (data[i * 3 + 2] & 0x000000FF ) | 
				    0xFF000000;
			}
			
			color[color.length - 1] = 0xFF000000;					// 最后一个像素用黑色填充
		}
		return color;
	}
	
	public Bitmap createMyBitmap(byte[] data, int width, int height){	
		int []colors = convertByteToColor(data);
		if (colors == null){
			return null;
		}			
		Bitmap bmp = null;
		try {
			bmp = Bitmap.createBitmap(colors, 0, width, width, height, 
					Bitmap.Config.ARGB_8888);
		} catch (Exception e) {
			// TODO: handle exception	
			return null;
		}						
		return bmp;
	}


	@Override
	public void onRenderData(byte[] y, byte[] u, byte[] v) {

		media.getVideoDecodedData(liveStreamId, decoderId, y, u, v);
		
		if(takePictureFlag) {
			String jpgName;
			boolean ret = false;
			jpgName = Constants.CAMERA_VIDEO_RECORD_PATH + Long.toString(System.currentTimeMillis()) + ".jpg";
			ret = yuvToJPEG(jpgName, media.getStreamDesc(liveStreamId).getVideoWidth(),
							   media.getStreamDesc(liveStreamId).getVideoHeight(),
							   y, 
							   u, 
							   v);
			if(ret){
				Message msg = new Message();
				msg.what = MSG_TAKE_PIC_OK;
				msg.obj = jpgName;
				mHandle.sendMessage(msg);
			}
			
			takePictureFlag = false;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if (keyCode == KeyEvent.KEYCODE_BACK)
		{
			this.finish();
		}
		return true;
	}
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		Log.d("media", "onConfigurationChanged");
		super.onConfigurationChanged(newConfig);
		// 竖屏
		if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
			WindowManager.LayoutParams windowparams = getWindow().getAttributes();
			windowparams.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
			getWindow().setAttributes(windowparams);
			getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
//			add_layout.setVisibility(View.VISIBLE);
		} else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			// 横屏
			WindowManager.LayoutParams windowparams = getWindow().getAttributes();
			windowparams.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
			getWindow().setAttributes(windowparams);
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
//			add_layout.setVisibility(View.GONE);
		}
	}
	@Override
	protected void onDestroy() {

		Log.d("media", "onDestroy");
		stopWatch();
		super.onDestroy();
		
	}

	private void stopWatch() {
		if (audioHandler != null) {
			audioHandler.releaseAudio();// stop audio play
			audioHandler = null;
		}
		surfaceViewLayout.removeView(glSurfaceView);// stop video render

		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if (decoderId != 0) {
			media.destoryAVDecoder(decoderId);// 销毁解码器
			decoderId = 0;
		}
		if (liveStreamId != 0) {
			media.closeStream(liveStreamId);// 关闭实时流
			liveStreamId = 0;
		}
	}
	
	class CameraGestureListener extends GestureDetector.SimpleOnGestureListener{
	    @Override
	    public boolean onSingleTapUp(MotionEvent ev) {
	        return true;
	    }
	    @Override
	    public void onShowPress(MotionEvent ev) {
	    }
	    @Override
	    public void onLongPress(MotionEvent ev) {
	    }
	    @Override
	    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
	        Log.e("onScroll",e1.toString() + "distanceX=" + distanceX + "  distanceY=" + distanceY);
	        if(distanceX > 1.0 && Math.abs(distanceY) < 1.0){
	        	//向左滑动	        	
	        	command.ctrlPTZorMove(Long.parseLong(cameraCid), 0, Command.PTZMOVECTRL_PTZ, -CAMERA_PTZ_STEPS, 0, 0);
	        }
	        if(distanceX < -1.0 && Math.abs(distanceY) < 1.0){
	        	//向右滑动
	        	command.ctrlPTZorMove(Long.parseLong(cameraCid), 0, Command.PTZMOVECTRL_PTZ, CAMERA_PTZ_STEPS, 0, 0);
	        }
	        if (distanceY > 1.0 && Math.abs(distanceX) < 1.0){
	        	//向下滑动
	        	command.ctrlPTZorMove(Long.parseLong(cameraCid), 0, Command.PTZMOVECTRL_PTZ, 0, CAMERA_PTZ_STEPS, 0);
	        }
	        if (distanceY < -1.0 && Math.abs(distanceX) < 1.0){
	        	//向上滑动
	        	command.ctrlPTZorMove(Long.parseLong(cameraCid), 0, Command.PTZMOVECTRL_PTZ, 0, -CAMERA_PTZ_STEPS, 0);
	        }
	        return true;
	    }
	    @Override
	    public boolean onDown(MotionEvent ev) {
	        return true;
	    }
	    @Override
	    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
	        return true;
	    }
	}
	
	private class AudioHandler {

		private static final String TAG = "AudioHandler";
		private AudioTrack audioPlay;
		private byte[] mAudioPlayData;
		// ////////////////new avs /////////////////////////
		private Media media;
		private long liveStreamId;
		private long decoderId;

		private Thread workingThread;

		private int channel_configuration;

		public AudioHandler(int sampleRateInHz, int channel, long streamId,
				long decoderId, Media media) {
			if (channel == 1) {
				channel_configuration = AudioFormat.CHANNEL_OUT_MONO;
			} else {
				channel_configuration = AudioFormat.CHANNEL_OUT_STEREO;
			}
			this.media = media;
			this.liveStreamId = streamId;
			this.decoderId = decoderId;

			int minBufferSize = AudioTrack.getMinBufferSize(sampleRateInHz,
															channel_configuration, AudioFormat.ENCODING_PCM_16BIT);
			audioPlay = new AudioTrack(AudioManager.STREAM_MUSIC, sampleRateInHz,
										channel_configuration, AudioFormat.ENCODING_PCM_16BIT,
										minBufferSize, AudioTrack.MODE_STREAM);
			mAudioPlayData = new byte[2048];
		}

		public void releaseAudio() {
			if (workingThread != null && workingThread.isAlive()) {
				workingThread.interrupt();
				workingThread = null;
			}
		}

		public void startAudioWorking() {
			if (workingThread == null) {
				workingThread = new Thread(new Runnable() {
					public void run() {
						try {
							if (audioPlay.getState() == AudioTrack.STATE_INITIALIZED) {
								audioPlay.play();
							}
							int size =0;
							while (true) {
								size = media.getAudioDecodedData(
										liveStreamId, decoderId,mAudioPlayData);
								if (size > 0) {
									audioPlay.write(mAudioPlayData, 0,size);

								}
								Thread.sleep(1);
							}
						} catch (InterruptedException e) {
							Log.e(TAG, e.toString());
						} finally {
							audioPlay.release();
							audioPlay = null;
						}

					}
				});
				workingThread.start();
			}
		}
	}
	
	private class AudioSend{
		
		private AudioRecord audioRecord; 
	    private int         minBufSize;
	    private Thread 		audioSendThread = null;
	    private Media       media;

	    private long cid;
	    private short []     audioData ;
		
		public AudioSend(Media media, int sampleRateInHz, int channel, int deepth, long cid){
			
			this.media = media;
			this.cid = cid;
			minBufSize = AudioRecord.getMinBufferSize(sampleRateInHz,channel,
														AudioFormat.ENCODING_PCM_16BIT);
			audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, sampleRateInHz, 
											channel,
											AudioFormat.ENCODING_PCM_16BIT,
											minBufSize) ; 
			audioData = new short[minBufSize];
			MediaDataDesc audioFormat = new MediaDataDesc(AudioType.PCM16,
											sampleRateInHz,
											channel,
											deepth);
			media.setRevAudioStreamProperty(audioFormat);
			
		}
		
		public void stopAudioSend(){
			if (audioSendThread != null) {
				media.stopRevAudioStream(sendAudioStreamId);
				audioSendThread.interrupt();
				audioSendThread = null;
			}
		}
		
		public void startAudioSend(){

			if (audioSendThread == null) {
				audioSendThread = new Thread(new Runnable() {
					public void run() {
						try {
							    audioRecord.startRecording();
							    sendAudioStreamId = media.startRevAudioStream(cid);
								int size =0;
								while (true) {
									size = audioRecord.read(audioData, 0,minBufSize);
									Log.e(TAG, "AUDIO Record Size=" + size);
									media.writeRevAudioStreamData(audioData, minBufSize);
									Thread.sleep(1);
							}
						} catch (InterruptedException e) {
							Log.e(TAG, e.toString());
						} finally {
						}
					}
				});
				audioSendThread.start();
			}
		}
	}
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		if(v.getId() == R.id.glsurfaceviewlayout){
			return mGestureDetector.onTouchEvent(event);
		}
		return false;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
		switch(v.getId()){
			case R.id.img_record:
				cameraVideoRecord();
				break;
			case R.id.img_pic:
				takePictureFlag = true;
				break;
			case R.id.img_voice:
				break;
			case R.id.img_picture:
				showCameraJpgPic();
				break;
			default:
				break;
		}
		
	}

	private void showCameraJpgPic(){

		File file = new File(Preferences.getInstance(CameraMainActivity.this).getCameraPicPath());
//		Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
//		Uri uri = Uri.fromFile(file);
//		intent.setData(uri);
//		sendBroadcast(intent);

		Intent intent = new Intent();
		intent.setAction(android.content.Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(file), "image/*");
		startActivity(intent);
	}
	
	private void cameraVideoRecord(){
		
		String videoFilename = "";
		if(isVideoRecording){
			//停止视频录制
			if(media.stopLocalRecord(liveStreamId)){
				imgVideoRecord.setImageResource(R.drawable.btn_camera);
				isVideoRecording = false;
				 Toast.makeText(CameraMainActivity.this,"视频保存在：" + videoFilename,Toast.LENGTH_LONG).show();
			}else{
				Log.e(TAG, "停止视频录制失败！！！");
			}
		}else{
			//开始录制
			videoFilename = Constants.CAMERA_VIDEO_RECORD_PATH + Long.toString(System.currentTimeMillis()) + ".mp4";
			if(media.startLocalRecord(liveStreamId, videoFilename)){
				imgVideoRecord.setImageResource(R.drawable.btn_stop);
				isVideoRecording = true;
			}else{
				Toast.makeText(CameraMainActivity.this,"视频录制失败，稍后重试！",Toast.LENGTH_SHORT).show();
			}					
		}
		
	}
}
