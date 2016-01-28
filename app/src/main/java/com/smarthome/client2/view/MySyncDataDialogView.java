package com.smarthome.client2.view;

import java.util.LinkedList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

public class MySyncDataDialogView extends SurfaceView implements Callback, Runnable {
	// 用于控制SurfaceView
	public SurfaceHolder sfh;
	// 声明一个画笔
	private Paint paint1, paint2;
	// 声明一条线程
	public Thread th;
	// 线程消亡的标识位
	private boolean flag;
	// 声明一个画布
	public Canvas canvas;
	// 设置画布绘图无锯齿
	private PaintFlagsDrawFilter pfd = new PaintFlagsDrawFilter(0,
			Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);

	private Context ctx;

	public int w = 0;
	public int h = 0;
	int x_every = 10;
	int ra_every = 0;
	int ra_every_last = 0;
	int x_last = 0;
	int count_last = 0;
	public boolean isStopThread = false;

	private LinkedList<MyDot> list_dot_now = new LinkedList<MyDot>();
	private LinkedList<MyDot> list_dot_last = new LinkedList<MyDot>();
	
	private LinkedList<MyDot> list_dot_now2 = new LinkedList<MyDot>();
	private LinkedList<MyDot> list_dot_last2 = new LinkedList<MyDot>();
	
	private LinkedList<MyDot> list_dot_now3 = new LinkedList<MyDot>();
	private LinkedList<MyDot> list_dot_last3 = new LinkedList<MyDot>();
	
	private LinkedList<MyDot> list_dot_now4 = new LinkedList<MyDot>();
	private LinkedList<MyDot> list_dot_last4 = new LinkedList<MyDot>();

	private int speed1 = (int) (0*Math.random()+1);
	private int speed2 = (int) (0*Math.random()+1);
	private int speed3 = (int) (0*Math.random()+1);
	private int speed4 = (int) (0*Math.random()+1);
	
	Paint paint = new Paint() {
		{
			setStyle(Paint.Style.STROKE);
			setStrokeCap(Paint.Cap.ROUND);
			setStrokeWidth(3.0f);
			setAntiAlias(true);
		}
	};
	Path path = new Path();
	
	private int color_red = Color.parseColor("#f1746c");
	private int color_blue = Color.parseColor("#4aacfe");

	/**
	 * SurfaceView初始化函数
	 */
	public MySyncDataDialogView(Context context) {
		super(context);
		this.ctx = context;
		init();
	}

	public MySyncDataDialogView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.ctx = context;
		init();
	}

	private void init() {

		paint.setTextSize(50);

		// 实例SurfaceHolder
		sfh = this.getHolder();
		setZOrderOnTop(true);
		getHolder().setFormat(PixelFormat.TRANSLUCENT);
		// 为SurfaceView添加状态监听
		sfh.addCallback(this);
		// 实例一个画笔
		paint1 = new Paint();
		paint2 = new Paint();
		paint1.setColor(Color.RED);
		paint2.setColor(Color.BLUE);
		// 设置画笔颜色为白色
		// 设置焦点
		setFocusable(true);
	}

	private void initMyDot() {
		initFirstOneCircle();
		initSecondOneCircle();
		initThreeOneCircle();
		initFourOneCircle();
		
		for(MyDot d:list_dot_now){
			System.out.println("zxl---myview4---inimydot--->"+d.x+"--->"+d.y);
		}
	}

	private void initFirstOneCircle() {
		list_dot_now.clear();
		list_dot_last.clear();

		for (int i = 0; i < 13; i++) {
			MyDot dot = new MyDot();
			if (i % 4 == 0) {
				dot.y = h / 2;
				if (i / 4 == 0) {
					dot.x = -w;
				} else if (i / 4 == 1) {
					dot.x = 0;
				} else if (i / 4 == 2) {
					dot.x = w;
				} else if (i / 4 == 3) {
					dot.x = w*2;
				}
			} else if (i % 4 == 1) {
				dot.y = h/8;
				if (i / 4 == 0) {
					dot.x = -w + w / 4;
				} else if (i / 4 == 1) {
					dot.x = 0 + w / 4;
				} else if (i / 4 == 2) {
					dot.x = w + w / 4;
				}
			} else if (i % 4 == 2) {
				dot.y = h / 2;
				if (i / 4 == 0) {
					dot.x = -w + w / 2;
				} else if (i / 4 == 1) {
					dot.x = 0 + w / 2;
				} else if (i / 4 == 2) {
					dot.x = w + w / 2;
				}
			} else if (i % 4 == 3) {
				dot.y = h-h/8;
				if (i / 4 == 0) {
					dot.x = -w + w / 4 * 3;
				} else if (i / 4 == 1) {
					dot.x = 0 + w / 4 * 3;
				} else if (i / 4 == 2) {
					dot.x = w + w / 4 * 3;
				}
			}
			list_dot_now.add(dot);
			list_dot_last.add(dot);
		}
	}
	
	private void initSecondOneCircle() {
		list_dot_now2.clear();
		list_dot_last2.clear();

		int x = -w;
		int y = 0;
		for (int i = 0; i < 25; i++) {
			MyDot dot = new MyDot();
			
			x = -w + i*w/8;
			switch(i%8){
			case 0:
				y = h/4*3-h/8;
				break;
			case 1:
				y = h/2-h/8;
				break;
			case 2:
				y = h/4*3-h/8;
				break;
			case 3:
				y = h-h/8;
				break;
			case 4:
				y = h/4*3-h/8;
				break;
			case 5:
				y = h/2-h/8;
				break;
			case 6:
				y = h/4*3-h/8;
				break;
			case 7:
				y = h-h/8;
				break;
			}
			
			dot.x = x;
			dot.y = y;
			
			list_dot_now2.add(dot);
			list_dot_last2.add(dot);
		}
	}
	
	private void initThreeOneCircle() {
		list_dot_now3.clear();
		list_dot_last3.clear();

		int x = -w;
		int y = 0;
		for (int i = 0; i < 25; i++) {
			MyDot dot = new MyDot();
			
			x = -w + i*w/8;
			switch(i%8){
			case 0:
				y = h/4+h/4;
				break;
			case 1:
				y = 0+h/4;
				break;
			case 2:
				y = h/4+h/4;
				break;
			case 3:
				y = h/2+h/4;
				break;
			case 4:
				y = h/4+h/4;
				break;
			case 5:
				y = 0+h/4;
				break;
			case 6:
				y = h/4+h/4;
				break;
			case 7:
				y = h/2+h/4;
				break;
			}
			
			dot.x = x;
			dot.y = y;
			
			list_dot_now3.add(dot);
			list_dot_last3.add(dot);
		}
	}
	
	private void initFourOneCircle() {
		list_dot_now4.clear();
		list_dot_last4.clear();

		int x = -w;
		int y = 0;
		for (int i = 0; i < 13; i++) {
			MyDot dot = new MyDot();
			
			x = -w + i*w/4;
			switch(i%4){
			case 0:
				y = h/2;
				break;
			case 1:
				y = h-h/8;
				break;
			case 2:
				y = h/2;
				break;
			case 3:
				y = h/8;
				break;
			}
			
			dot.x = x;
			dot.y = y;
			
			list_dot_now4.add(dot);
			list_dot_last4.add(dot);
		}
	}

	/**
	 * 第一个时间点，只需要绘制一个点，不需要绘制线
	 */
	public void myDrawOneCircle() {
		try {
			canvas = sfh.lockCanvas();
			if (canvas != null) {
				// ----设置画布绘图无锯齿
				canvas.setDrawFilter(pfd);
				// ----利用填充画布，刷屏
				canvas.drawColor(Color.TRANSPARENT, Mode.CLEAR);

				paint.setColor(color_blue);
				path.reset();
				int count = list_dot_now.size();
				for (int i = 0; i < count; i = i + 2) {
					if (i >= 2) {
						path.moveTo(list_dot_now.get(i - 2).x,
								list_dot_now.get(i - 2).y+h/2);
						path.quadTo(list_dot_now.get(i - 1).x,
								list_dot_now.get(i - 1).y+h/2,
								list_dot_now.get(i).x, list_dot_now.get(i).y+h/2);
					}
				}
				canvas.drawPath(path, paint);
				
				paint.setColor(color_red);
				path.reset();
				int count2 = list_dot_now2.size();
				for (int i = 0; i < count2; i = i + 2) {
					if (i >= 2) {
						path.moveTo(list_dot_now2.get(i - 2).x,
								list_dot_now2.get(i - 2).y+h/2);
						path.quadTo(list_dot_now2.get(i - 1).x,
								list_dot_now2.get(i - 1).y+h/2,
								list_dot_now2.get(i).x, list_dot_now2.get(i).y+h/2);
					}
				}
				canvas.drawPath(path, paint);
				
				paint.setColor(color_blue);
				path.reset();
				int count3 = list_dot_now3.size();
				for (int i = 0; i < count3; i = i + 2) {
					if (i >= 2) {
						path.moveTo(list_dot_now3.get(i - 2).x,
								list_dot_now3.get(i - 2).y+h/2);
						path.quadTo(list_dot_now3.get(i - 1).x,
								list_dot_now3.get(i - 1).y+h/2,
								list_dot_now3.get(i).x, list_dot_now3.get(i).y+h/2);
					}
				}
				canvas.drawPath(path, paint);
				
				paint.setColor(color_red);
				path.reset();
				int count4 = list_dot_now4.size();
				for (int i = 0; i < count4; i = i + 2) {
					if (i >= 2) {
						path.moveTo(list_dot_now4.get(i - 2).x,
								list_dot_now4.get(i - 2).y+h/2);
						path.quadTo(list_dot_now4.get(i - 1).x,
								list_dot_now4.get(i - 1).y+h/2,
								list_dot_now4.get(i).x, list_dot_now4.get(i).y+h/2);
					}
				}
				canvas.drawPath(path, paint);

				for (MyDot d : list_dot_now) {
					d.x = d.x + x_every*2;
				}
				
				for (MyDot d : list_dot_now2) {
					d.x = d.x + x_every*4;
				}
				
				for (MyDot d : list_dot_now3) {
					d.x = d.x + x_every*3;
				}
				
				for (MyDot d : list_dot_now4) {
					d.x = d.x + x_every*2;
				}

				boolean isOverRight1 = (list_dot_now.get(count - 1).x >= w * 3);
				if (isOverRight1) {
					initFirstOneCircle();
				}
				
				boolean isOverRight2 = (list_dot_now2.get(count2 - 1).x >= w * 3);
				if (isOverRight2) {
					initSecondOneCircle();
				}
				
				boolean isOverRight3 = (list_dot_now3.get(count3 - 1).x >= w * 3);
				if (isOverRight3) {
					initThreeOneCircle();
				}
				
				boolean isOverRight4 = (list_dot_now4.get(count4 - 1).x >= w * 3);
				if (isOverRight4) {
					initFourOneCircle();
				}

			}

		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			if (canvas != null)
				sfh.unlockCanvasAndPost(canvas);
		}
	}

	@Override
	public void run() {

		while (!isStopThread) {
			myDrawOneCircle();

			if(h > 0){
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	private static final int CONTINUE_DRAW_1 = 1;
	private Handler mHandler = new Handler() {
		public void handleMessage(final android.os.Message msg) {
			switch (msg.what) {
			case CONTINUE_DRAW_1:
				new Thread(new Runnable() {
					@Override
					public void run() {

						if (!isStopThread) {
							Message message = mHandler.obtainMessage();
							message.what = CONTINUE_DRAW_1;
							message.sendToTarget();
						}
					}
				}).start();

				break;
			}
		}
	};

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		int count_last = 0;
		// isFirstDraw = true;
		// 实例线程
		th = new Thread(this);
		// 启动线程
		th.start();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		w = width;
		h = height/2;
		initMyDot();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {

	}

	class MyDot {
		public int x, y;
	}

}
