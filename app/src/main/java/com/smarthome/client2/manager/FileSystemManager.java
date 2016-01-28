package com.smarthome.client2.manager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.graphics.Bitmap;

import com.smarthome.client2.common.SysParamers;

/**
 * 文件系统相关的管理类 <br/>
 * 主要包含： <br/>
 * 1.SD卡的相关管理操作; <br/>
 * 
 */
public class FileSystemManager {

	/** sd卡未挂载 */
	public static String SD_UNMOUNT = "SD_UNMOUNT";
	/** sd卡空间不足 */
	public static String SD_NOT_ENOUGH = "SD_NOT_ENOUGH";
	/** sd卡挂载成功 */
	public static String SD_OK = "SD_OK";

	private static final int SD_ENOUGH_LEVEL = 30; //MB

	/**
	 * 检测SD卡状态
	 * 
	 * @return
	 */
	public static String checkSDCard() {
		if (SDManager.isSDCardMounted()) {
			if (!SDManager.isSDCardSizeEnough(SD_ENOUGH_LEVEL)) {
				return SD_NOT_ENOUGH;
			} else {
				return SD_OK;
			}
		} else {
			return SD_UNMOUNT;
		}
	}

	/**
	 * 向 SD卡写入一个文件
	 * 
	 * @param file
	 */
	public static boolean storeFile(Context ctx,File file, Bitmap bmp) throws IOException {
		return SDManager.storeFile(ctx,file, bmp);
	}

	/**
	 * 从SD卡读出一个文件
	 * 
	 * @param file
	 */
	public static File getFile(final String fileName) {
		return SDManager.getFile(fileName);
	}

	/**
	 * 清除SD卡部分缓存
	 * 
	 * @param file
	 */
	public static void clearPartStoreSpace(Context ctx){
		SDManager.clearPartStoreSpaceIfNeed(ctx);
	}

	/**
	 * 计算APP所占SD卡的大小 - APP启动的时候需要调用
	 * 
	 * @param file
	 */
	public static void intSDcardStoredSpace(Context ctx) {
		 SDManager.intSDcardStoredSpace(ctx);
	}
	
	public static void createFileIfNeed(){
		File file_app = new File(SysParamers.APP_FILE_PATH);
		if(!file_app.exists()){
			file_app.mkdirs();
		}
		File file_img = new File(SysParamers.IMAGE_HEAD_FILE_PATH);
		if(!file_img.exists()){
			file_img.mkdirs();
		}
	}
	
	/**
	 * 
	 * 写入文件到APP私有目录
	 * @date 2013-04-19
	 */
	
	public static int writeFileData(Context ctx,String filename,InputStream is){
		if (is == null) {
			return -1;
		}
		FileOutputStream fos = null;
		
		try {
			fos = ctx.openFileOutput(filename, Context.MODE_PRIVATE);  //获得FileOutputStream
			byte[] bytes = new byte[1024];
			int len = -1;
			while((len = is.read(bytes))!=-1){
				fos.write(bytes, 0, len);
			}
			return 1;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return -1;
		} catch (Exception e){
			e.printStackTrace();
			return -1;
		}finally{
			try {
				is.close();
				fos.close();
			} catch (Exception e) {
				e.printStackTrace();
			} 
		}
		
		
	}

}
