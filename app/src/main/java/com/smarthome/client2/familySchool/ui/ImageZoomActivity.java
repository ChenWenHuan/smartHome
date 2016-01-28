package com.smarthome.client2.familySchool.ui;

import com.smarthome.client2.R;
import com.smarthome.client2.familySchool.utils.FsConstants;
import com.smarthome.client2.familySchool.utils.ImageDownLoader;
import com.smarthome.client2.familySchool.utils.ImageDownLoader.onImageLoaderListener;
import com.smarthome.client2.familySchool.view.ZoomImageView;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

/**
 * @author n003913
 *
 */
public class ImageZoomActivity extends BaseActivity
{
    /**
     * 自定义的ImageView控制，可对图片进行多点触控缩放和拖动
     */
    private ZoomImageView zoomImageView;

    /**
     * 待展示的图片
     */
    private Bitmap bitmap;

    /* (non-Javadoc)
     * @see com.smarthome.client2.familySchool.ui.BaseActivity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fs_activity_image_zoom);
        zoomImageView = (ZoomImageView) findViewById(R.id.zoom_image_view);
        // 取出图片路径，并解析成Bitmap对象，然后在ZoomImageView中显示
        String imagePath = getIntent().getStringExtra("image_path");
        ImageDownLoader loader = ImageDownLoader.getInstance();
        onImageLoaderListener listener = new onImageLoaderListener()
        {
            @Override
            public void onImageLoader(Bitmap bitmap, String url)
            {
                removeProgressDialog();
                ImageZoomActivity.this.bitmap = bitmap;
                zoomImageView.setImageBitmap(bitmap);
            }
        };
        loader.addListener(FsConstants.ZOOM_IMAGE, listener);
        bitmap = loader.downloadImage(imagePath, FsConstants.ZOOM_IMAGE);
        if (bitmap == null)
        {
            bitmap = BitmapFactory.decodeResource(getResources(),
                    R.drawable.refresh);
            zoomImageView.setImageBitmap(bitmap);
        }
        else
        {
            zoomImageView.setImageBitmap(bitmap);
        }
    }
}
