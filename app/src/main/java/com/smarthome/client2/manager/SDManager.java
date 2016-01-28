package com.smarthome.client2.manager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.os.StatFs;

import com.smarthome.client2.common.SysParamers;
import com.smarthome.client2.common.TLog;
import com.smarthome.client2.config.Preferences;

/**
 * SD卡管理类
 * 
 */
public class SDManager {

	private static final int MB = 1024 * 1024;
	private static final long ALARM_CLEAR_LIMIT = 50 * MB;

	/**
	 * 向SD存储文件
	 * 
	 * @param fileName
	 * @return
	 * @throws IOException
	 */
	public static boolean storeFile(Context ctx, File file, Bitmap bitmap) throws IOException {
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(file);
			if (file.getAbsolutePath().toLowerCase().endsWith(".png")) {
				if (bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)) {
					out.flush();
				}
			} else {
				if (bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)) {
					out.flush();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != out) {
				out.close();
			}
		}
		long size = getFileSize(file);
		long time = System.currentTimeMillis();
		Preferences.getInstance(ctx).addSDsize(size);
		file.setLastModified(time);
//		TLog.Log("添加一个文件：" + size + "&&" + time);
//		TLog.Log("目前的文件总数为" + Preferences.getInstance(ctx).getSDcardSize());
		return true;
	}

	/**
	 * 从SD卡读取文件
	 * 
	 * @param fileName
	 * @return
	 */
	public static File getFile(final String fileName) {
		File file = new File(SysParamers.APP_FILE_PATH + fileName);
		if (file != null) {
			file.setLastModified(System.currentTimeMillis());
		}
		return file;
	}

	/**
	 * 每次启动App的时候检测或者重置一下缓存大小
	 * 
	 * @return
	 * @throws IOException
	 */
	public static void intSDcardStoredSpace(final Context ctx) {
		new Thread() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				super.run();
				String BasePath = SysParamers.APP_FILE_PATH;
				File basefile = new File(BasePath);
				long size = 0;
				File flist[] = basefile.listFiles();
				if (flist != null && flist.length > 0) {
					for (int i = 0; i < flist.length; i++) {
						try {
							size = size + getFileSize(flist[i]);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
				if (ctx != null) {
					Preferences.getInstance(ctx).setSDcardSize(size);
//					TLog.Log("目前的文件总数为" + size + "&&" + Preferences.getInstance(ctx).getSDcardSize());
				}
			}
		}.start();

	}

	private static long getFileSize(File file) throws IOException {
		long s = 0L;
		if (file.exists()) {
			FileInputStream fis = null;
			try {
				fis = new FileInputStream(file);
				s = fis.available();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (null != fis) {
					fis.close();
				}
			}
		}
		return s;
	}

	/**
	 * 清除部分缓存文件
	 * 
	 * @return
	 * @throws IOException
	 */
	public static void clearPartStoreSpaceIfNeed(final Context ctx) {
		new Thread() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				super.run();
//				TLog.Log("目前的文件总数为" + Preferences.getInstance(ctx).getSDcardSize());
				if (Preferences.getInstance(ctx).getSDcardSize() > ALARM_CLEAR_LIMIT) {
					String BasePath = SysParamers.APP_FILE_PATH;
					try {
						File basefile = new File(BasePath);
						File[] files = basefile.listFiles();
						Arrays.sort(files, new Comparator<File>() {
							public int compare(File arg0, File arg1) {
								if (arg0 == arg1) {
									return 0;
								}
								if (arg0.lastModified() > arg1.lastModified()) {
									return 1;
								} else if (arg0.lastModified() == arg1.lastModified()) {
									return 0;
								} else {
									return -1;
								}
							}
						});
						int length = files.length / 2;
						for (int i = 0; i < length; i++) {
							TLog.Log(files[i].getName() + "：文件删除");
							files[i].delete();
						}
					} catch (Exception e) {
						// TODO: handle exception
					}
				}
			}
		}.start();
	}

	/**
	 * 检测SD卡是否有足够的空间可用
	 */
	public static boolean isSDCardSizeEnough(int sizeMB) {
		// 1.检测SD卡的剩余空间
		File path = Environment.getExternalStorageDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize(); // unit: byte
		long blocks = stat.getAvailableBlocks();
		long availableSpare = (blocks * blockSize) / (1024 * 1024);
		TLog.Log("剩余空间:" + availableSpare);
		return availableSpare > sizeMB;
	}

	/**
	 * 检测SD卡是否挂载
	 */
	public static boolean isSDCardMounted() {
		return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
	}

}
