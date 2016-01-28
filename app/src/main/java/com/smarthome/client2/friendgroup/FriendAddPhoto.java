package com.smarthome.client2.friendgroup;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.smarthome.client2.R;
import com.smarthome.client2.common.Constants;
import com.smarthome.client2.friendgroup.PhotoLayout.BitmapListener;
import com.smarthome.client2.manager.AppManager;
import com.umeng.analytics.MobclickAgent;

public class FriendAddPhoto extends Activity implements OnClickListener
{

    private EditText photo_content;

    private ImageButton photo_add, photo_minus, photo_take;

    private Button photo_confirm;

    private LinearLayout image_wall;

    private List<Map<String, Bitmap>> bitmapList = new ArrayList<Map<String, Bitmap>>();

    private List<Bitmap> bitmapBigAddList = new ArrayList<Bitmap>();

    private List<Bitmap> bitmapSmallAddList = new ArrayList<Bitmap>();

    private List<Bitmap> bitmapBigMinusList = new ArrayList<Bitmap>();

    private List<Bitmap> bitmapSmallMinusList = new ArrayList<Bitmap>();

    private ImageButton title_pengyouquan_back;

    private TextView title_pengyouquan_text;

    private static List<Uri> uriList = new ArrayList<Uri>();

    private PhotoLayout photoLayout;

    private String takePicturePath;

    private Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            switch (msg.what)
            {
                case 1:
                    takePicturePath = (String) msg.obj;
                    break;

                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friend_share);

        image_wall = (LinearLayout) findViewById(R.id.image_wall);
        image_wall.removeAllViews();
        photo_content = (EditText) findViewById(R.id.photo_content);
        photo_take = (ImageButton) findViewById(R.id.photo_take);
        // photo_add = (ImageButton) findViewById(R.id.photo_add);
        // photo_minus = (ImageButton) findViewById(R.id.photo_minus);
        photo_confirm = (Button) findViewById(R.id.photo_confirm);
        photo_take.setOnClickListener(this);
        // photo_add.setOnClickListener(this);
        // photo_minus.setOnClickListener(this);
        photo_confirm.setOnClickListener(this);

        photoLayout = new PhotoLayout(getApplicationContext(),
                FriendAddPhoto.this);
        photoLayout.setBitmapListener(bitmapListener);
        photoLayout.setHandler(handler);
        uriList.clear();

        final ActionBar actionBar = this.getActionBar();
        actionBar.setCustomView(R.layout.friend_group_title);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.show();
        title_pengyouquan_back = (ImageButton) findViewById(R.id.title_pengyouquan_back);
        title_pengyouquan_back.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View arg0)
            {
                final Dialog alertDialog = new AlertDialog.Builder(
                        FriendAddPhoto.this).setMessage("确定取消发布吗？")
                        .setNegativeButton("确定",
                                new DialogInterface.OnClickListener()
                                {
                                    @Override
                                    public void onClick(DialogInterface arg0,
                                            int arg1)
                                    {
                                        finish();
                                    }
                                })
                        .setPositiveButton("取消",
                                new DialogInterface.OnClickListener()
                                {
                                    @Override
                                    public void onClick(DialogInterface arg0,
                                            int arg1)
                                    {
                                        finish();
                                    }
                                })
                        .create();
                alertDialog.show();
            }
        });
        title_pengyouquan_text = (TextView) findViewById(R.id.title_pengyouquan_text);
        title_pengyouquan_text.setText("发布内容");
        AppManager.getAppManager().addActivity(this);
    }

    @Override
    protected void onDestroy()
    {
        AppManager.getAppManager().removeActivity(this);
        super.onDestroy();
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.photo_take:
                photoLayout.initPhotoFrame(R.layout.friend_share,
                        R.id.photo_take);
                break;
            // case R.id.photo_add:
            // photoLayout.initPhotoFrame(R.layout.friend_share, R.id.photo_add);
            // break;
            // case R.id.photo_minus:
            // for(Bitmap bitmap : bitmapMinusList){
            // if(bitmapAddList.contains(bitmap)){
            // bitmapAddList.remove(bitmap);
            // }
            // addImage();
            // }
            // break;
            case R.id.photo_confirm:
                //			if(triggleImgList!=null){
                //				triggleImgList.triggleImgList();
                //			}
                Intent intent = new Intent(FriendAddPhoto.this,
                        FriendGroup.class);
                Bundle bundle = new Bundle();
                /***********/
                bundle.putString("userId", "1");
                /***********/
                bundle.putString("item_content_text", photo_content.getText()
                        .toString());
                bundle.putParcelableArrayList("item_content_img",
                        (ArrayList<Bitmap>) bitmapSmallAddList);
                bundle.putParcelableArrayList("item_content_img_uri",
                        (ArrayList<Uri>) uriList);
                Log.d("", "daitm----uriList---size---" + uriList.size());
                //			bundle.putParcelableArrayList("item_content_img_big",
                //					(ArrayList<Bitmap>) bitmapBigAddList);
                intent.putExtras(bundle);
                FriendAddPhoto.this.setResult(RESULT_OK, intent);
                //			startActivity(intent);
                FriendAddPhoto.this.finish();
                break;
            default:
                break;
        }
    }

    private void addImage()
    {
        image_wall.removeAllViews();
        for (final Map<String, Bitmap> map : bitmapList)
        {
            ImageView imageView = new ImageView(this);
            BitmapDrawable bd = new BitmapDrawable(map.get("small"));
            Drawable d = bd;
            imageView.setImageDrawable(d);
            // imageView.setOnClickListener(new OnClickListener() {
            // @Override
            // public void onClick(View v) {
            // if(!bitmapMinusList.contains(bitmap)){
            // bitmapMinusList.add(bitmap);
            // }else{
            // bitmapMinusList.remove(bitmap);
            // }
            // }
            // });

            imageView.setOnClickListener(new OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                    View layout = inflater.inflate(R.layout.show_big_img, null);
                    Dialog dialog = new Dialog(FriendAddPhoto.this,
                            R.style.MyDialog);
                    dialog.setContentView(layout);
                    dialog.show();
                    ImageView show_big_img = (ImageView) layout.findViewById(R.id.show_big_img);
                    show_big_img.setImageBitmap(map.get("big"));
                }
            });
            image_wall.addView(imageView, 0);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        try
        {
            if (resultCode == Activity.RESULT_OK)
            {
                switch (requestCode)
                {
                // 如果是直接从相册获取
                    case Constants.REQUEST_CODE_FOR_PHOTO_ALBUM:
                        photoLayout.startPhotoZoom(data.getData());
                        uriList.add(data.getData());
                        break;
                    // 如果是调用相机拍照时
                    case Constants.REQUEST_CODE_FOR_TAKE_CAMERA:
                        File temp = new File(takePicturePath);
                        photoLayout.startPhotoZoom(Uri.fromFile(temp));
                        uriList.add(Uri.fromFile(temp));
                        break;
                    // 取得裁剪后的图片
                    case Constants.REQUEST_CODE_START_PHOTO_ZOOM:
                        if (data != null)
                        {
                            photoLayout.setPicToView(data);
                        }
                        break;
                    default:
                        break;
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public BitmapListener bitmapListener = new BitmapListener()
    {
        @Override
        public void onBitmapListener(Bitmap smallMap, Bitmap bigMap)
        {
            Map<String, Bitmap> map = new HashMap<String, Bitmap>();
            map.put("small", smallMap);
            map.put("big", bigMap);
            bitmapList.add(map);
            bitmapSmallAddList.add(smallMap);
            bitmapBigAddList.add(bigMap);
            addImage();
        }
    };

    //	private TriggleImgList triggleImgList;
    //	public void setTriggleImgList(TriggleImgList triggleImgList){
    //		this.triggleImgList = triggleImgList;
    //	}

    /* (non-Javadoc)
     * @see android.app.Activity#onResume()
     */
    @Override
    protected void onResume()
    {
        MobclickAgent.onPageStart(getClass().getSimpleName());
        MobclickAgent.onResume(this);
        super.onResume();
    }

    /* (non-Javadoc)
     * @see android.app.Activity#onPause()
     */
    @Override
    protected void onPause()
    {
        MobclickAgent.onPageEnd(getClass().getSimpleName());
        MobclickAgent.onPause(this);
        super.onPause();
    }
}
