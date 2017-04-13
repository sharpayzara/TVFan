package tvfan.tv;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;

import java.io.File;

/**
 * 屏保页面
 */
public class ScreenProtectActivity extends Activity {

    private static final int CHANGE_IMAGE = 1001;
    private static final int CHANGE_TIME = 8000; // 更换时间间隔
    private static final int SCREEN_PROTECT_IMAGES_COUNT = 5;
    private HomeKeyEventBroadCastReceiver receiver;
    ImageView img1;
    ImageView img2; // 用于和img轮流显示，实现渐入渐出效果
    int[] images = {R.drawable.bja, R.drawable.bjb, R.drawable.bjc, R.drawable.bjd, R.drawable.bje, R.drawable.bjf};
    int index = 0;

    Bitmap[] bitmaps = new Bitmap[SCREEN_PROTECT_IMAGES_COUNT];

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            index++;
            if (index >= SCREEN_PROTECT_IMAGES_COUNT) {
                index = 0;
            }
            // 之前显示的那张图片，加渐变动画，消失，消失后设置下一张图；之前未显示的那张图片，渐变动画，出现；
//            img1.setImageResource(images[index]);
            if (img1Flag) {
                Log.i("ScreenProtectActivity", "img1消失，img2显示");
                img1.startAnimation(aaHide);
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        img1.setVisibility(View.INVISIBLE);
                    }
                }, 800);
//                img2.setImageResource(images[index]);
                setImage(img2, index);
                img2.setVisibility(View.VISIBLE);
                img2.startAnimation(aaShow);
                img1Flag = false;
            } else {
                Log.i("ScreenProtectActivity", "img2消失，img1显示");
                img2.startAnimation(aaHide);
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        img2.setVisibility(View.INVISIBLE);
                    }
                }, 800);
//                img1.setImageResource(images[index]);
                setImage(img1, index);
                img1.setVisibility(View.VISIBLE);
                img1.startAnimation(aaShow);
                img1Flag = true;
            }

            mHandler.sendEmptyMessageDelayed(CHANGE_IMAGE, CHANGE_TIME);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_protect_screen);
        checkDownload();
        initView();
        mHandler.sendEmptyMessageDelayed(CHANGE_IMAGE, CHANGE_TIME);
        receiver = new HomeKeyEventBroadCastReceiver();
        registerReceiver(receiver, new IntentFilter(
                Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
    }

    /**
     * 检查屏保图是否下载完成
     */
    private void checkDownload() {
        String filePath = getExternalFilesDir(null).getPath();
        for (int i = 0; i < SCREEN_PROTECT_IMAGES_COUNT; i++) {
            File imageFile = new File(filePath + File.separator + (i + 1));
            if (imageFile.exists()) {
                bitmaps[i] = BitmapFactory.decodeFile(imageFile.getPath());
            } else {
                downloadFlag = false;
                break;
            }
        }
    }

    private AlphaAnimation aaShow;
    private AlphaAnimation aaHide;
    private boolean img1Flag; // 表示img1是否是当前显示的图片

    private void initView() {
        img1 = (ImageView) findViewById(R.id.protect_screen_img1);
//        img1.setImageResource(images[index]);
        setImage(img1, index);

        img2 = (ImageView) findViewById(R.id.protect_screen_img2);
//        img2.setImageResource(images[(index++) % images.length]);
//        index++;
//        setImage(img2, index);

        //图片渐变模糊度始终
        aaShow = new AlphaAnimation(0.0f,1.0f);
        aaHide = new AlphaAnimation(1.0f,0.0f);
        //渐变时间
        aaShow.setDuration(800);
        aaHide.setDuration(800);

        img1Flag = true;
    }

    private boolean downloadFlag = true; // 是否使用下载的图片
    private void setImage(ImageView img, int index) {
        if (index >= SCREEN_PROTECT_IMAGES_COUNT) {
            index = 0;
        }
        if (downloadFlag) {
            img.setImageBitmap(bitmaps[index]);
        } else {
            img.setImageResource(images[index]);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
            finish();
        mHandler.removeMessages(CHANGE_IMAGE);
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(receiver);
        super.onDestroy();
    }
    /**
     * 监听Home键事件的广播接收器
     * 进行退出
     * @author ddd
     *
     */
    class HomeKeyEventBroadCastReceiver extends BroadcastReceiver {
        static final String SYSTEM_REASON = "reason";
        static final String SYSTEM_HOME_KEY = "homekey";// home key

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
                String reason = intent.getStringExtra(SYSTEM_REASON);
                if (reason != null) {
                    if (reason.equals(SYSTEM_HOME_KEY)) {
                        unregisterReceiver(receiver);
                        System.exit(0);
                        android.os.Process.killProcess(android.os.Process.myPid());
                    }
                }
            }
        }
    }
}
