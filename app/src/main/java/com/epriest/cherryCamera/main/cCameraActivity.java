package com.epriest.cherryCamera.main;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.hardware.Camera.Area;
import android.media.ExifInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;

import com.epriest.cherryCamera.ApplicationClass;
import com.epriest.cherryCamera.R;
import com.epriest.cherryCamera.util.IN;
import com.epriest.cherryCamera.util.ccCamUtil;
import com.epriest.cherryCamera.util.ccPicUtil;
import com.epriest.cherryCamera.util.logline;

import java.util.ArrayList;

/**
 * @author Camera Activity
 *
 */
public class cCameraActivity extends Activity{
	
	private String TAG = this.getClass().getSimpleName();
	private ApplicationClass appClass;
	
    private String sceneName;
    private float oldTouchValue;    
    private String intentGetAction;
    
    PorterDuffColorFilter mColorFilter;
    ImageView ivPitch;
    ImageView ivPitchOn;
    
    OrientationEventListener myOrientationEventListener;

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		logline.d(TAG, "cCamera Activity onCreate");
		setContentView(R.layout.main_camera);
		
		appClass = (ApplicationClass)getApplication();
		appClass.setActivity(this);
		
		appClass.mMenuset = new cCameraMenuSet(appClass);
		appClass.mParameter = new cCameraParameters(appClass);

		myOrientationEventListener
		   = new OrientationEventListener(getBaseContext()){
			@Override
			public void onOrientationChanged(int orientation) {
//				logline.d("Orientation: " + String.valueOf(orientation));
		        drawPitch(orientation);
		        int prevDegree = appClass.orientation;
		        appClass.orientation =  ccCamUtil.setDegreeToExifOrientation(orientation);
//		        logline.d(""+appClass.orientation);
				if(prevDegree != appClass.orientation)
					setChangeOrientation();
			}			
		};
		myOrientationEventListener.enable();
		
		// ===========
		//  scene load
		//  ===========
		Intent intent = getIntent();
		sceneName = intent.getExtras().getString("sceneName", "auto");	
		intentGetAction = intent.getExtras().getString("getAction");
		logline.d(TAG, "getAction="+intentGetAction+"\nsceneName = "+sceneName);
//		sceneButtonSetBg();

		if(sceneName.equalsIgnoreCase(IN.CAMERA_FACING))
			appClass.mMenuset.frontCameraAccess = true;
		appClass.cameraFacingMode = ccCamUtil.checkCameraFacingMode();
		
		appClass.mPreview = new cCameraPreview(this, sceneName, appClass);
		
        // Create our Preview view and set it as the content of our activity.        
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(appClass.mPreview);
        
        mColorFilter = new PorterDuffColorFilter(Color.GREEN, Mode.SRC_ATOP);
    	ivPitch = (ImageView)findViewById(R.id.camera_preview_pitch);
    	ivPitchOn = (ImageView)findViewById(R.id.camera_preview_pitch_on);
//        drawView = new cCameraPreviewDraw(this, app);
//        preview.addView(drawView);

		/*LayoutParams layoutParamsDrawing
				= new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT);
		this.addContentView(appClass.mPreview.drawView, layoutParamsDrawing);*/
	}
		
    /*private void hiddenSoftkey(){
        int currentApiVersion = android.os.Build.VERSION.SDK_INT;

        final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_FULLSCREEN
            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

        // This work only for android 4.4+
        if(currentApiVersion >= Build.VERSION_CODES.KITKAT)
        {

            getWindow().getDecorView().setSystemUiVisibility(flags);

            // Code below is to handle presses of Volume up or Volume down.
            // Without this, after pressing volume buttons, the navigation bar will
            // show up and won't hide
            final View decorView = getWindow().getDecorView();
            decorView
                .setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener()
                {

                    @Override
                    public void onSystemUiVisibilityChange(int visibility)
                    {
                        if((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0)
                        {
                            decorView.setSystemUiVisibility(flags);
                        }
                    }
                });
        }
    }*/
    
    /*@SuppressLint("NewApi")
    @Override
    public void onWindowFocusChanged(boolean hasFocus)
    {
        super.onWindowFocusChanged(hasFocus);
        int currentApiVersion = android.os.Build.VERSION.SDK_INT;
        if(currentApiVersion >= Build.VERSION_CODES.KITKAT && hasFocus)
        {
            getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }*/
    
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		logline.d(TAG, "onActivityResult resultCode : "+resultCode );
		if(resultCode == RESULT_OK){
			if(requestCode == IN.galleryRequestCode){
				String picData = data.getExtras().getString("pic_data");
				if(picData.equals("null")){
					appClass.mPreview.previewImageChange(null);					
				}else{
					appClass.mPreview.previewImageChange(picData);
				}
			}
		}
	}
	
	public BroadcastReceiver batteryRcv = new BroadcastReceiver(){
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if(Intent.ACTION_BATTERY_CHANGED.equals(action)){
				int level = intent.getIntExtra("level",-1);
				int maxValue = intent.getIntExtra("scale", -1);
				int batteryStatus = intent.getIntExtra("status", -1);
				int batteryHealth = intent.getIntExtra("health", -1);
				int batteryPlugged = intent.getIntExtra("plugged", -1);
				float batteryTemp = (float)intent.getIntExtra("temperature", -1)/10;
				String batteryTech = intent.getStringExtra("technology");
				
				int chargePct = (level * 100)/maxValue;
				String batteryInfo = "Battery Info:\n" +
				"Health="+batteryHealth+"\n"+
				"Charge % = "+chargePct+"%\n"+
				"Battery Type="+batteryTech+"\n"+
				"Battery Temp="+batteryTemp+"\n"+
				"Battery Status="+batteryStatus+"\n"+
				"Battery Plugged = "+batteryPlugged;
				
				logline.d(batteryInfo);
				
				Button tv = (Button)findViewById(R.id.btn_camera_bettery);
				tv.setText(chargePct+"%");//+batteryTemp+"˚"+batteryPlugged);
				tv.setTag((int)(batteryTemp)+"ºC/"+(int)(batteryTemp*1.8+32)+"ºF");
				if(chargePct <= 25){				
					tv.setBackgroundResource(R.drawable.battery25);
				}else if(chargePct <= 50){
					tv.setBackgroundResource(R.drawable.battery50);
				}
			}
		}		
	};
	
	@Override
	public boolean onTouchEvent(MotionEvent touchevent) {
//		final ImageView iv = (ImageView)findViewById(R.id.helpImage);
		
		 switch (touchevent.getAction()){
		 case MotionEvent.ACTION_DOWN:
           oldTouchValue = touchevent.getX();
           break;   
		 case MotionEvent.ACTION_UP:
//       	if(app.helpFlag){
//   			iv.setVisibility(View.INVISIBLE);
//   			app.helpFlag = false;
//   		}
       	
           //if(this.searchOk==false) return false;
           float currentX = touchevent.getX();
//           if (!app.openOption && oldTouchValue < currentX){
//        	   app.openOption = true;
//        	   app.vf.setInAnimation(ccUtil.inFromLeftAnimation());
//        	   app.vf.setOutAnimation(ccUtil.outToRightAnimation());
//        	   app.vf.showNext();
//           }
           if (appClass.openOption != IN.MODE_OPENMENU_CLOSE && oldTouchValue > currentX){
        	   appClass.mMenuset.openOptionClose();
           }else{
			   return true;
			   /*ImageView focusArea = (ImageView)findViewById(R.id.camera_preview_focusarea);
			   appClass.mPreview.takeAutoFocusArea(focusArea, touchevent.getX(), touchevent.getY(), new Point(appClass.lcdWidth, appClass.lcdHeight));
			   if (appClass.mPreview.mFocusArea == null) {
				   appClass.mPreview.mFocusArea = new ArrayList<Area>();
				   appClass.mPreview.mFocusArea.add(new Area(new Rect(), 1));
			   }

			   appClass.mPreview.mCamera.autoFocus(appClass.mPreview.mFocusCallback);*/
		   }
           break;
		 }
		return true;
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		logline.d(TAG, "key = "+keyCode);
		switch(keyCode){
		case KeyEvent.KEYCODE_VOLUME_DOWN:
		case KeyEvent.KEYCODE_VOLUME_UP:
		case KeyEvent.KEYCODE_ENTER:
		case KeyEvent.KEYCODE_CAMERA:
			if(appClass.mPreview.iShutter)
				return true;	
			if(appClass.mMenuset.timerCount > 0){
				appClass.mMenuset.timerCountMil = appClass.mMenuset.timerCount*1000;
				TextView tv = (TextView)appClass.getActivity().findViewById(R.id.camera_timer_text);
				appClass.mMenuset.timer.setTextView(tv);
				appClass.mMenuset.timer.Start();
			}else{
				appClass.mPreview.shootOn();
			}
			break;
		case KeyEvent.KEYCODE_BACK:
			if(appClass.mMenuset.timerCountMil > 0){
				appClass.mMenuset.timer.Stop();
			}else
			if (appClass.openOption != IN.MODE_OPENMENU_CLOSE){
				appClass.mMenuset.openOptionClose();
			}
//			else if(app.helpFlag){
//				final ImageView iv = (ImageView)findViewById(R.id.helpImage);
//				iv.setVisibility(ImageView.INVISIBLE);
//				app.helpFlag = false;
//			}			
			else{
//				app.cameraFinish(intentGetAction, mPreview.pictureFile);
				finish();
			}
			break;
		}
		return true;
	}
	
	//볼륨키 작동 금지
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if(event.getAction() == KeyEvent.ACTION_UP){
			logline.d(TAG,"===ACTION_UP===");
		}else{
			logline.d(TAG,"===ACTION_DN===");
			switch(event.getKeyCode()){		
			case KeyEvent.KEYCODE_VOLUME_UP:
				super.onKeyDown(KeyEvent.KEYCODE_DPAD_UP, new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DPAD_UP));
				return true;
			case KeyEvent.KEYCODE_VOLUME_DOWN:
				super.onKeyDown(KeyEvent.KEYCODE_DPAD_DOWN, new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DPAD_UP));
				return true;
			case KeyEvent.KEYCODE_CAMERA:				
			case KeyEvent.KEYCODE_ENTER:
				return true;
			}
		}
		return super.dispatchKeyEvent(event);
	}
	
	@Override
	public void onLowMemory() {
		super.onLowMemory();
	}

	@Override
	protected void onStart() {
		super.onStart();
		logline.d(TAG,"cCamera Activity onStart");
//		mCamera = getCameraInstance();
//		mPreview.connectCamera();
		registerReceiver(batteryRcv, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
//		app.refreshMemory();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		logline.d(TAG,"cCamera Activity onResume");
//		appClass.isGPS = ccUtil.getGpsState(appClass.getBaseContext(), IN.GPS_CHECK);
//		mPreview.reconnectCamera();
	}

	@Override
    protected void onPause() {
        super.onPause();
        logline.d(TAG,"cCamera Activity onPause");
        appClass.mPreview.isCameraPause = true;
//        mPreview.pauseCamera();              // release the camera immediately on pause event
    }
/*	
	@Override
	protected void onStop() {
		super.onStop();
		logline.d(TAG,"cCamera Activity onStop");
	}*/
	
	@Override
	protected void onDestroy() {
		logline.d(TAG,"cCamera Activity onDestroy"+intentGetAction);
		unregisterReceiver(batteryRcv);
//		mPreview.releaseCamera();
		System.gc();
		super.onDestroy();
	}
	
	private void drawPitch(float angle) {		
		int horizonAng = (int) Math.abs(angle%90);
//		logline.d("angle = "+angle);
		if(horizonAng < 3 || horizonAng > 87)
			ivPitchOn.setColorFilter(mColorFilter);			
		else
			ivPitchOn.setColorFilter(null);
		 
		Matrix matrix = new Matrix();
		ivPitch.setScaleType(ScaleType.MATRIX);   //required
		matrix.postRotate(270-angle, ivPitch.getWidth()/2, ivPitch.getHeight()/2);
		ivPitch.setImageMatrix(matrix);
	}

	private void setChangeOrientation() {
		ImageView ivGallery = (ImageView)findViewById(R.id.img_photo_gallery);
		ImageView ivCameraShutter = (ImageView)findViewById(R.id.img_camera_shutter_icon);
		TextView ivCountTimer = (TextView)findViewById(R.id.camera_timer_text);
		Button ivCameraMemory = (Button)findViewById(R.id.btn_camera_freememory);
		Button ivCameraBattery = (Button)findViewById(R.id.btn_camera_bettery);
		ImageButton ivCameraScene = (ImageButton)findViewById(R.id.btn_camera_scene);
		ImageView ivCameraFlash = (ImageView)findViewById(R.id.icon_camera_flash);
		ImageView ivCameraFocus = (ImageView)findViewById(R.id.icon_camera_focus);
		TextView ivCameraExpo = (TextView)findViewById(R.id.text_camera_exposure);

		switch(appClass.orientation){
		case ExifInterface.ORIENTATION_ROTATE_90:
		case ExifInterface.ORIENTATION_ROTATE_270:
			ivCountTimer.startAnimation(ccPicUtil.rotateAnimation(this, 0, -90, 0, 0));
			ivGallery.startAnimation(ccPicUtil.rotateAnimation(this, 0, -90, 0, 0));
			ivCameraShutter.startAnimation(ccPicUtil.rotateAnimation(this, 0, -90, 0, 0));
			ivCameraScene.startAnimation(ccPicUtil.rotateAnimation(this, 0, -90, 0, 0));
			ivCameraMemory.startAnimation(ccPicUtil.rotateAnimation(this, 0, -90, 0, 0));
			ivCameraBattery.startAnimation(ccPicUtil.rotateAnimation(this, 0, -90, 0, 0));
			ivCameraFlash.startAnimation(ccPicUtil.rotateAnimation(this, 0, -90, 0, 0));
			ivCameraFocus.startAnimation(ccPicUtil.rotateAnimation(this, 0, -90, 0, 0));
			ivCameraExpo.startAnimation(ccPicUtil.rotateAnimation(this, 0, -90, 0, 0));
			break;
		case ExifInterface.ORIENTATION_NORMAL:
		case ExifInterface.ORIENTATION_ROTATE_180:		
			ivCountTimer.startAnimation(ccPicUtil.rotateAnimation(this, -90, 0, 0, 0));
			ivGallery.startAnimation(ccPicUtil.rotateAnimation(this, -90, 0, 0, 0));
			ivCameraShutter.startAnimation(ccPicUtil.rotateAnimation(this, -90, 0, 0, 0));
			ivCameraScene.startAnimation(ccPicUtil.rotateAnimation(this, -90, 0, 0, 0));
			ivCameraMemory.startAnimation(ccPicUtil.rotateAnimation(this, -90, 0, 0, 0));
			ivCameraBattery.startAnimation(ccPicUtil.rotateAnimation(this, -90, 0, 0, 0));
			ivCameraFlash.startAnimation(ccPicUtil.rotateAnimation(this, -90, 0, 0, 0));
			ivCameraFocus.startAnimation(ccPicUtil.rotateAnimation(this, -90, 0, 0, 0));
			ivCameraExpo.startAnimation(ccPicUtil.rotateAnimation(this, -90, 0, 0, 0));
			break;
		}
	}
}
