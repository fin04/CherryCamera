package com.epriest.cherryCamera;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Process;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.epriest.cherryCamera.main.MediaPlayerService;
import com.epriest.cherryCamera.util.ccCamUtil;
import com.epriest.cherryCamera.util.ccEffectBlur;
import com.epriest.cherryCamera.util.ccPicUtil;
import com.epriest.cherryCamera.util.ccUtil;
import com.epriest.cherryCamera.util.logline;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

/**
 * @author Cherry Camera Main
 */
public class ccActivity extends Activity implements OnClickListener {

    //	public static final Collection<String> PRODUCT_CODE_TYPES = list("UPC_A", "UPC_E", "EAN_8", "EAN_13", "RSS_14");
//	public static final Collection<String> ONE_D_CODE_TYPES =
//			list("UPC_A", "UPC_E", "EAN_8", "EAN_13", "CODE_39", "CODE_93", "CODE_128",
//					"ITF", "RSS_14", "RSS_EXPANDED");
//	public static final Collection<String> QR_CODE_TYPES = Collections.singleton("QR_CODE");
//	public static final Collection<String> DATA_MATRIX_TYPES = Collections.singleton("DATA_MATRIX");
//
//	public static final Collection<String> ALL_CODE_TYPES = null;
    private String TAG = this.getClass().getSimpleName();
    private String intentGetAction;

    private ccMenuset mSet;
    //    private MediaPlayer mediaPlayer;
    private MediaPlayerService player;
    boolean serviceBound = false;

    private final int REQUEST_ACCESS_CAMERA_PERMISSION = 122;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        logline.d(TAG, "==onCreate==");

        // permission set
        if (isGrantedPermission(Manifest.permission.CAMERA) &&
                isGrantedPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            setCreate();
        } else {
//			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_ACCESS_CAMERA_PERMISSION);
//			}
        }
    }

    public boolean isGrantedPermission(String permission) {
        return PackageManager.PERMISSION_GRANTED ==
                ContextCompat.checkSelfPermission(getApplicationContext(), permission);
    }

    private void setCreate() {
        // ===========
        // check camera
        // ===========
        if (!ccCamUtil.checkCameraHardware(this)) {
            Toast.makeText(this, "this device have not Camera", Toast.LENGTH_SHORT).show();
            finish();
        }

        // ===========
        // menuset
        // ===========
        mSet = new ccMenuset(this, intentGetAction);

        // ===========
        // check storage mount
        // ===========
        String ext = Environment.getExternalStorageState();
        if (!ext.equals(Environment.MEDIA_MOUNTED)) {
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setMessage(getResources().getString(R.string.unmount_sdcard));
            alert.setPositiveButton(getResources().getString(R.string.msg_yes), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    finish();
                }
            });
            alert.show();
            return;
        }


//        getWindow().setFormat(PixelFormat.TRANSLUCENT);//|LayoutParams.FLAG_BLUR_BEHIND);

        // ADMOB SET
        MobileAds.initialize(getApplicationContext(), getString(R.string.banner_ad_unit_id));
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)        // All emulators
                .addTestDevice("F51F2A6C4A6290BDB0C711BAE2697457")
                .addTestDevice("7763B915C09B3BF4C69010EE31F744D6")
                .build();
        mAdView.loadAd(adRequest);

//        Animation_Flowers();
//        mediaPlayer = MediaPlayer.create(this, R.raw.river_flows_in_you);
        mBlurHandler = new Handler();
//        menuSetBlur();

        // privacy info
        TextView privacy = (TextView) findViewById(R.id.tv_appinfo);
        privacy.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "http://epriest.tistory.com/3";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
            }
        });

        // ===========
        //
        // ===========
//        contentListCooking();
//        ViewServer.get(this).addWindow(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        switch (requestCode) {
            case REQUEST_ACCESS_CAMERA_PERMISSION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    setCreate();
                } else {
                    new AlertDialog.Builder(this)
                            .setCancelable(false)
                            .setMessage(getResources().getString(R.string.permission_denied))
                            .setPositiveButton(getResources().getString(R.string.button_ok),
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish();
                                        }
                                    })
                            .show();
                }
            }
            break;
        }
    }

    //Binding this Client to the AudioPlayer Service
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            MediaPlayerService.LocalBinder binder = (MediaPlayerService.LocalBinder) service;
            player = binder.getService();
            serviceBound = true;

            Toast.makeText(ccActivity.this, "Service Bound", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceBound = false;
        }
    };

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean("ServiceState", serviceBound);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        serviceBound = savedInstanceState.getBoolean("ServiceState");
    }

    private void playAudio(String media) {
        //Check is service is active
        if (!serviceBound) {
            Intent playerIntent = new Intent(this, MediaPlayerService.class);
            playerIntent.putExtra("media", media);
            startService(playerIntent);
            bindService(playerIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        } else {
            //Service is active
            //Send media with BroadcastReceiver
        }
    }

    private Handler mBlurHandler;
    private Runnable mRunnable;

    private void menuSetBlur() {
        mRunnable = new Runnable() {
            @Override
            public void run() {
                findViewById(R.id.adView).setVisibility(View.INVISIBLE);
                Animation_Fadeout();
                Animation_Flowers();
                playMedia();
            }
        };
    }

    private void Animation_Fadeout() {
        Bitmap blurBitmap = ccPicUtil.getViewDrawingCache(findViewById(R.id.menu_layout_bg));
        if (blurBitmap == null)
            return;
        FrameLayout fl_fade = (FrameLayout) findViewById(R.id.blurBg_fade);
        fl_fade.setBackgroundDrawable(new BitmapDrawable(blurBitmap));
        fl_fade.setVisibility(View.VISIBLE);
        Bitmap capBitmap = Bitmap.createScaledBitmap(blurBitmap, blurBitmap.getWidth() / 2, blurBitmap.getHeight() / 2, true);
        Bitmap newImg = ccEffectBlur.fastblur(ccActivity.this, capBitmap, 15);
        findViewById(R.id.blurBg).setBackgroundDrawable(new BitmapDrawable(newImg));
        capBitmap.recycle();

        Animation fadeOutAnimation = AnimationUtils.loadAnimation(ccActivity.this, R.anim.fadeout);
        fadeOutAnimation.setAnimationListener(FadeoutAnimationListener);
        fadeOutAnimation.setFillAfter(true);
// 		fl_fade.setAnimation(fadeOutAnimation);
        fl_fade.startAnimation(fadeOutAnimation);
    }

    private Animation anim_translate(ImageView view, int startX, int endX, int startY, int endY,
                                     int duration, int startOffset) {
        Animation an = new TranslateAnimation(
                Animation.ABSOLUTE, startX,
                Animation.ABSOLUTE, endX,
                Animation.ABSOLUTE, startY,
                Animation.ABSOLUTE, endY);
        an.setRepeatCount(Animation.INFINITE);
        an.setStartOffset(startOffset);
        an.setDuration(duration);
        an.setFillAfter(false);// to keep the state after animation is finished
//		an.setAnimationListener(FlowerAnimationListener);
        return an;
    }

    private void Animation_Flowers() {
//		AnimationSet snowMov1 = new AnimationSet(true);
//        RotateAnimation rotate1 = new RotateAnimation(0,360, Animation.RELATIVE_TO_SELF,0.5f , Animation.RELATIVE_TO_SELF,0.5f );
//        rotate1.setStartOffset(50);
//        rotate1.setDuration(9500);
//        snowMov1.addAnimation(rotate1);
//        TranslateAnimation trans1 =  new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0.1f, Animation.RELATIVE_TO_PARENT, 0.3f, Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.9f);
//        trans1.setDuration(12000);
//        snowMov1.addAnimation(trans1);

        for (int i = 0; i < 40; i++) {
            AnimationSet animationSet = new AnimationSet(true);
            int imgId = ccUtil.getResId(getResources(), "imageFlower" + i, "id", getPackageName());
            ImageView iv = (ImageView) findViewById(imgId);
            String flowerStr = "cherry_0" + ccUtil.gerRandom(5, 1);
            int imageRes = ccUtil.getResId(getResources(), flowerStr, "drawable", getPackageName());
            iv.setImageResource(imageRes);

//			Animation rotation = AnimationUtils.loadAnimation(getBaseContext(), R.anim.rotate360);
            int startOffset = ccUtil.gerRandom(5000, 3000);
            RotateAnimation anim = new RotateAnimation(0f, 360f,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f);
            anim.setDuration(ccUtil.gerRandom(15000, 6000));
            anim.setRepeatCount(Animation.INFINITE);
            anim.setStartOffset(startOffset);
            animationSet.addAnimation(anim);
            int imgX, imgY;
            if (i < 20) {
                imgX = (int) (iv.getX());//+ccUtil.gerRandom(iv.getWidth()/2,0));
                imgY = (int) (iv.getY());//+ccUtil.gerRandom(iv.getWidth()/2,0));
            } else {
                imgX = ccUtil.gerRandom(ccUtil.getScreenSize(ccActivity.this).widthPixels - iv.getWidth(), 0);
                imgY = ccUtil.gerRandom((int) getResources().getDimension(R.dimen.px50), -100);
            }
            animationSet.addAnimation(anim_translate(iv, imgX, imgX + ccUtil.gerRandom(300, -150), imgY, ccUtil.getScreenSize(ccActivity.this).heightPixels,
                    ccUtil.gerRandom(7000, 3000), startOffset));
            float hueValue = (float) (ccUtil.gerRandom(60, -40));
            iv.setColorFilter(ccPicUtil.adjustHue(hueValue));

            iv.startAnimation(animationSet);
        }
    }


    AnimationListener FadeoutAnimationListener = new AnimationListener() {

        @Override
        public void onAnimationStart(Animation animation) {
            findViewById(R.id.scn_gridlayout).setVisibility(View.INVISIBLE);
            findViewById(R.id.btnGallery).setVisibility(View.INVISIBLE);
        }

        @Override
        public void onAnimationEnd(Animation animation) {

        }

        @Override
        public void onAnimationRepeat(Animation animation) {
            animation.setDuration(ccUtil.gerRandom(15000, 6000));
            animation.setStartOffset(ccUtil.gerRandom(5000, 3000));
        }

    };

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (findViewById(R.id.blurBg).getBackground() != null) {
                removeBlur();
                startBlur();
                return false;
            }
        }

        return super.onTouchEvent(event);
    }

    private void startBlur() {
        if (mBlurHandler != null)
            mBlurHandler.postDelayed(mRunnable, 5000);
    }

    private void removeBlur() {
        stopMedia();
        findViewById(R.id.adView).setVisibility(View.VISIBLE);
        if (mBlurHandler != null)
            mBlurHandler.removeCallbacks(mRunnable);
        recycleView(findViewById(R.id.blurBg_fade));
        recycleView(findViewById(R.id.blurBg));
        findViewById(R.id.scn_gridlayout).setVisibility(View.VISIBLE);
        findViewById(R.id.btnGallery).setVisibility(View.VISIBLE);
    }

    private void playMedia() {
        playAudio("river_flows_in_you.mp3");
    }

    private void stopMedia() {
        if (serviceBound) {
            unbindService(serviceConnection);
            //service is active
            player.stopSelf();
        }
    }

    private void recycleView(View view) {
        if (view != null) {
            Drawable bg = view.getBackground();
            if (bg != null) {
                bg.setCallback(null);
                ((BitmapDrawable) bg).getBitmap().recycle();
                view.setBackgroundDrawable(null);
            }
        }
    }

    @Override
    protected void onResume() {
        logline.d(TAG, "==onResume==");
//        startBlur();
//		app.menuBgimageLoad();
//		if (adView != null) {
//			adView.resume();
//		}


        super.onResume();
//		ViewServer.get(this).setFocusedWindow(this);
    }

    @Override
    protected void onPause() {
        logline.d(TAG, "==onPause==");
//		if (adView != null) {
//			adView.pause();
//		}
//        removeBlur();
        super.onPause();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // QR reader
        final IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
//		final String resultQrUri = result.getContents();
        String resultFormat = null;
        if (result != null)
            resultFormat = result.getFormatName();

        if (resultFormat == null) {
        } else if (resultFormat.equals("QR_CODE")) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.msg_sbc_results + " [" + result.getFormatName() + "]")
                    .setMessage(result.getContents() + " \n")
                    .setPositiveButton(R.string.button_open_browser, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Uri uri = Uri.parse(result.getContents());
                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .show();
        } else if (resultFormat.equals("UPC_A") || resultFormat.equals("UPC_E") ||
                resultFormat.equals("EAN_8") || resultFormat.equals("EAN_13") ||
                resultFormat.equals("CODE_39") || resultFormat.equals("CODE_93") ||
                resultFormat.equals("CODE_128") || resultFormat.equals("ITF") || resultFormat.equals("DATA_MATRIX") ||
                resultFormat.equals("RSS_14") || resultFormat.equals("RSS_EXPANDED")) {
            new AlertDialog.Builder(this)
                    .setTitle(" [" + result.getFormatName() + "]")
                    .setMessage(result.getContents() + " \n")
                    .setPositiveButton(R.string.button_web_search, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_WEB_SEARCH);
                            intent.putExtra(SearchManager.QUERY, result.getContents());
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .show();
        } else {
        }
    }

//	private static Collection<String> list(String... values) {
//	    return Collections.unmodifiableCollection(Arrays.asList(values));
//	  }

    public void exitCamera() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(getResources().getString(R.string.alerttitle));
        alert.setMessage(getResources().getString(R.string.finishmessage));
        alert.setPositiveButton(getResources().getString(R.string.msg_yes), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });
        alert.setNegativeButton(getResources().getString(R.string.msg_no), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alert.show();
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
//			exitCamera();

//			Bitmap bitmap = BitmapFactory.decodeFile(file.getPath());
//			setResult(Activity.RESULT_OK
//					, new Intent("inline-data").putExtra("data", bitmap));
            finish();
            return true;
        }
        return true;
    }

    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {

        if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_CAMERA) {
            return true;
        }
        return super.onKeyLongPress(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        logline.d(TAG, "==onDestroy==");
//        removeBlur();
//		if(!intentGetAction.contains("IMAGE_CAPTURE"))
//		if (adView != null)
//			adView.destroy();
        System.gc();
//		ViewServer.get(this).removeWindow(this);
        super.onDestroy();
        Process.killProcess(Process.myPid());
    }

    @Override
    public void onClick(View v) {

    }

}
