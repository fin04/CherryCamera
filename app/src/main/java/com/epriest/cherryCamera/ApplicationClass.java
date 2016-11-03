package com.epriest.cherryCamera;

import java.util.List;

import android.app.Activity;
import android.app.Application;
import android.content.res.Configuration;
import android.hardware.Camera.Size;
import android.util.DisplayMetrics;

import com.epriest.cherryCamera.main.cCameraMenuSet;
import com.epriest.cherryCamera.main.cCameraParameters;
import com.epriest.cherryCamera.main.cCameraPreview;
import com.epriest.cherryCamera.util.IN;

public class ApplicationClass extends Application {
	
//	public ArrayList<String> PhotoId = new ArrayList<String>();
//	public ArrayList<String> PhotoName = new ArrayList<String>();
//	public ArrayList<String> PhotoDate = new ArrayList<String>();
//	public ArrayList<String> PhotoDataSize = new ArrayList<String>();
//	public ArrayList<String> PhotoData = new ArrayList<String>();
	
	public List<String> ListFlashMode = null;
	public List<String> ListColorEffect = null;
	public List<String> ListFocus = null;
	public List<String> ListScene = null;
	public List<Size> ListPictureSize = null;
	public List<Size> ListPreviewSize = null;
	public List<String> ListWhiteBalance = null;
	public List<String> ListAntibanding = null;
//	public Size ListThumbnailSize = null;
	
	
	//flowerFilterMode
	public int isCherryFilter;
	
	public int expoMaxSize;
	public int zoomSize;
	
	public int lcdWidth, lcdHeight;
	
	//isBeepSound
	public boolean isTimerBeep = true;
	
	
	//cameraResolusion
	public int pictureSize;
	public int cameraFacingMode = IN.CAMERA_ID_BACK_FRONT;
	
	//int helpState
	public boolean helpFlag;
	
	//optionMenuState
	public int openOption;
	
	
	public int orientation;
//	public int state;
	
	//isLowRam
	public boolean isOutOfMem = false;
	
	
	public Activity mActivity;
	public cCameraPreview mPreview;
	public cCameraMenuSet mMenuset;
	public cCameraParameters mParameter;
	public boolean isGPS;
	
	@Override
	public void onCreate() {
		super.onCreate();		
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
	}

	public void setActivity(Activity mActivity){
		this.mActivity = mActivity;
		
		DisplayMetrics displayMetrics = new DisplayMetrics();
		mActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		lcdWidth = displayMetrics.widthPixels;
		lcdHeight = displayMetrics.heightPixels;
	}
	
	public Activity getActivity(){
		return this.mActivity;
	}
	
}
