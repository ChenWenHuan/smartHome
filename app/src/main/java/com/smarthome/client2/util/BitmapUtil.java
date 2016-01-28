package com.smarthome.client2.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.smarthome.client2.SmartHomeApplication;
import com.smarthome.client2.common.Constants;
import com.smarthome.client2.common.TLog;

public class BitmapUtil
{
    private static final int TIMEOUT_CONNECTION = 500;

    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap)
    {
        Bitmap outBitmap = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(),
                Config.ARGB_8888);
        Canvas canvas = new Canvas(outBitmap);
        final int color = 0xff424242;
        final Paint paint = new Paint();
        float roundPX = 0;
        //      if(isSmall){
        //          roundPX = bitmap.getWidth()>=bitmap.getHeight()?bitmap.getHeight()/2:bitmap.getWidth()/2;
        //      }else{
        roundPX = bitmap.getWidth() >= bitmap.getHeight() ? bitmap.getHeight()
                : bitmap.getWidth();
        //      }
        final Rect rect = new Rect(0, 0, (int) roundPX, (int) roundPX);
        final RectF rectF = new RectF(rect);
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPX, roundPX, paint);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return outBitmap;
    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res,
            int resId, int reqWidth, int reqHeight)
    {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);
        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options,
                reqWidth,
                reqHeight);
        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    public static int calculateInSampleSize(BitmapFactory.Options options,
            int reqWidth, int reqHeight)
    {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;

        TLog.Log("zxl---gloable---calculate---" + width + "---" + height);

        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth)
        {
            if (width > height)
            {
                inSampleSize = Math.round((float) height / (float) reqHeight);
            }
            else
            {
                inSampleSize = Math.round((float) width / (float) reqWidth);
            }
        }
        return inSampleSize;
    }

    /**
     * 根据网址获得图片，优先从本地获取，本地没有则从网络下载
     * 
     * @param url 图片网址
     * @param context 上下文
     * @return 图片
     */
    public static Bitmap getBitmap(String url, Context context)
    {
        //        Log.e(TAG, "------url="+url);
        String imageName = url.substring(url.lastIndexOf("/") + 1, url.length());
        File file = new File(getPath(context), imageName);
        //        if(file.exists()){
        ////            Log.e(TAG, "getBitmap from Local");
        //            return BitmapFactory.decodeFile(file.getPath());
        //        }
        return getNetBitmap(url, file, context);
    }

    /**
     * 根据传入的list中保存的图片网址，获取相应的图片列表
     * 
     * @param list 保存图片网址的列表
     * @param context 上下文
     * @return 图片列表
     */
    public static ArrayList<Bitmap> getBitmap(List<String> list, Context context)
    {
        synchronized (list)
        {
            ArrayList<Bitmap> result = new ArrayList<Bitmap>();
            for (int i = 0; i < list.size(); i++)
            {
                Bitmap bitmap = getBitmap(list.get(i), context);
                result.add(bitmap);
            }
            return result;
        }
    }

    /**
     * 获取图片的存储目录，在有sd卡的情况下为 “/sdcard/apps_images/本应用包名/cach/images/”
     * 没有sd的情况下为“/data/data/本应用包名/cach/images/”
     * 
     * @param context 上下文
     * @return 本地图片存储目录
     */
    private static String getPath(Context context)
    {
        String path = null;
        boolean hasSDCard = Environment.getExternalStorageState()
                .equals(Environment.MEDIA_MOUNTED);
        String packageName = context.getPackageName() + "/ImageCache/";
        if (hasSDCard)
        {
            path = Constants.IMAGE_FILE_PATH;
        }
        else
        {
            path = "/data/data/" + packageName;
        }
        File file = new File(path);
        boolean isExist = file.exists();
        if (!isExist)
        {

            file.mkdirs();

        }
        return file.getPath();
    }

    /**
     * 网络可用状态下，下载图片并保存在本地
     * 
     * @param strUrl 图片网址
     * @param file 本地保存的图片文件
     * @param context 上下文
     * @return 图片
     */
    private static Bitmap getNetBitmap(String strUrl, File file, Context context)
    {
        //        Log.e(TAG, "getBitmap from net");
        if (TextUtils.isEmpty(strUrl))
        {
            return null;
        }
        Bitmap bitmap = null;
        SmartHomeApplication smartHomeApp = SmartHomeApplication.getInstance();
        if (smartHomeApp.isNetworkAvailable())
        {
            try
            {
                URL url = new URL(strUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(TIMEOUT_CONNECTION);
                conn.setRequestMethod("GET");
                if (conn.getResponseCode() == 200)
                {
                    InputStream inputStream = conn.getInputStream();
                    bitmap = BitmapFactory.decodeStream(inputStream);
                    return bitmap;
                }
            }
            catch (MalformedURLException e)
            {
                e.printStackTrace();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            finally
            {

            }
        }
        return bitmap;
    }

    /**
     * 根据图片地址从网络上下载图片<BR>
     * @param path 网络地址
     * @return 图片
     * @throws IOException
     */
    public static Bitmap getBitmap(String path) throws IOException
    {
        URL url = new URL(path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(TIMEOUT_CONNECTION);
        conn.setRequestMethod("GET");
        if (conn.getResponseCode() == Constants.SC_OK)
        {
            InputStream inputStream = conn.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            return bitmap;
        }
        return null;
    }

    public static Bitmap comp(Bitmap image, Context ctx)
    {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        if (baos.toByteArray().length / 1024 > 1024)
        {// 判断如果图片大于1M,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出
            baos.reset();// 重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, 50, baos);// 这里压缩50%，把压缩后的数据存放到baos中
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        // 开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        // 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
        float hh = ScreenUtils.getScreenHeight(ctx);// 这里设置高度为800f
        float ww = ScreenUtils.getScreenWidth(ctx);// 这里设置宽度为480f
        // 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;// be=1表示不缩放
        if (w > h && w > ww)
        {// 如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / ww);
        }
        else if (w < h && h > hh)
        {// 如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;// 设置缩放比例
        // 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        isBm = new ByteArrayInputStream(baos.toByteArray());
        bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        return compressImage(bitmap);// 压缩好比例大小后再进行质量压缩
    }

    private static Bitmap compressImage(Bitmap image)
    {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > 100)
        { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();// 重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;// 每次都减少10
            Log.d("", "daitm----compress--running-----" + options);
        }
        Log.d("",
                "daitm----compress--finish--baos.toByteArray()---"
                        + baos.toByteArray().length);
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
        return bitmap;
    }

    /**
     * 将图片保存为图片文件<BR>
     * 将图片保存为图片文件
     * @param bitmap 图片
     * @param filePath 保存路径
     * @param fileName 保存文件名
     */
    public static void setPicToView(Bitmap bitmap, String filePath,
            String fileName)
    {
        if (bitmap != null)
        {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            FileOutputStream b = null;
            File file = new File(filePath);
            file.mkdirs();
            try
            {
                b = new FileOutputStream(filePath + fileName);
                // 把数据写入文件
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
                //                datas = os.toByteArray();
                os.writeTo(b);

            }
            catch (Exception e)
            {
                // TODO: handle exception
                e.printStackTrace();
            }
            finally
            {
                try
                {
                    os.flush();
                    os.close();
                    b.close();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 裁剪图片方法实现
     * @param uri
     */
    public static void startPhotoZoom(Context context, Uri uri, int resultcode)
    {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // 下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 150);
        intent.putExtra("outputY", 150);
        intent.putExtra("return-data", true);
        ((Activity) context).startActivityForResult(intent, resultcode);
    }

    /**
     * 裁剪图片方法实现
     * @param uri
     */
    public static void startFamilyPhotoZoom(Context context, Uri uri, int resultcode)
    {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // 下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 9);
        intent.putExtra("aspectY", 5);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 360);
        intent.putExtra("outputY", 200);
        intent.putExtra("return-data", true);
        ((Activity) context).startActivityForResult(intent, resultcode);
    }
}