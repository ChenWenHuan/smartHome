package com.smarthome.client2.familySchool.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Environment;

/**
 * @author n003913 文件工具类，主要用来缓存和加载图片
 */
public class FileUtil
{
    /**
     * sd卡的根目录
     */
    private static String mSdRootPath = Environment.getExternalStorageDirectory()
            .getPath();

    /**
     * 手机的缓存根目录
     */
    private static String mDataRootPath = null;

    /**
     * 保存Image的目录名
     */
    private final static String FOLDER_IMAGE = "/SmartHome/ImageCache";

    private String cachePath;

    public FileUtil(Context context)
    {
        cachePath = Environment.getExternalStorageState()
                .equals(Environment.MEDIA_MOUNTED) ? context.getExternalCacheDir()
                .getAbsolutePath()
                : context.getCacheDir().getAbsolutePath();
        mDataRootPath = context.getCacheDir().getPath();
    }

    /**
     * 获取其他目录（非系统默认缓存目录）
     * @return
     */
    public String getOtherDirectory()
    {
        return Environment.getExternalStorageState()
                .equals(Environment.MEDIA_MOUNTED) ? mSdRootPath + FOLDER_IMAGE
                : mDataRootPath + FOLDER_IMAGE;
    }

    /**
     * 保存Image的方法，有sd卡存储到sd卡，没有就存储到手机目录
     * @param fileName
     * @param bitmap
     * @throws IOException
     */
    public void savaBitmap(String fileName, Bitmap bitmap) throws IOException
    {
        if (bitmap == null)
        {
            return;
        }
        File folderFile = new File(cachePath);
        if (!folderFile.exists())
        {
            folderFile.mkdirs();
        }
        File file = new File(cachePath + File.separator + fileName);
        file.createNewFile();
        FileOutputStream fos = new FileOutputStream(file);
        bitmap.compress(CompressFormat.JPEG, 100, fos);
        fos.flush();
        fos.close();
    }

    /**
     * 从手机或者sd卡获取Bitmap
     * @param fileName
     * @return
     */
    public Bitmap getBitmap(String fileName)
    {
        return BitmapFactory.decodeFile(cachePath + File.separator + fileName);
    }

    /**
     * 判断文件是否存在
     * @param fileName
     * @return
     */
    public boolean isFileExists(String fileName)
    {
        return new File(cachePath + File.separator + fileName).exists();
    }

    /**
     * 获取文件的大小
     * @param fileName
     * @return
     */
    public long getFileSize(String fileName)
    {
        return new File(cachePath + File.separator + fileName).length();
    }

    /**
     * 删除SD卡或者手机的非默认位置缓存图片和目录
     */
    public void deleteFile()
    {
        File dirFile = new File(getOtherDirectory());
        if (!dirFile.exists())
        {
            return;
        }
        if (dirFile.isDirectory())
        {
            String[] children = dirFile.list();
            for (int i = 0; i < children.length; i++)
            {
                new File(dirFile, children[i]).delete();
            }
        }
        dirFile.delete();
    }

}
