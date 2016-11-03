package com.epriest.cherryCamera;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.epriest.cherryCamera.gallery.ccGalleryGridLoader;
import com.epriest.cherryCamera.main.cCameraActivity;
import com.epriest.cherryCamera.util.IN;
import com.epriest.cherryCamera.util.ccCamUtil;
import com.epriest.cherryCamera.util.ccPicUtil;
import com.epriest.cherryCamera.util.logline;
import com.google.zxing.client.android.integration.IntentIntegrator;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.Vibrator;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
/**
 * @author Camera UI_Key Setting
 *
 */
public class ccMenuset{

	private String intentGetAction;
	String sceneName = IN.SCENE_DEFAULT;
	int menuModePage = 0;
	Vibrator vibe;		
	private int cameraFacingMode = IN.CAMERA_ID_BACK_FRONT;
	private String TAG = this.getClass().getSimpleName();
	Activity mActivity;
//	ccAppliClass appClass;
	
	public ccMenuset(final Activity mActivity, String intentGetAction) {
		logline.d(TAG, "=======ccMenuset===");
		this.mActivity = mActivity;
		this.intentGetAction = intentGetAction;
		List<String> ListScene;
//		List<String> ListScene = getSceneList();
//		
//		if(ListScene == null){
		Camera c = ccCamUtil.getCameraInstance(cameraFacingMode, false);
		if(c == null){
			Toast toast = Toast.makeText(mActivity, mActivity.getText(R.string.cannot_camera), Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
			mActivity.finish();
			return;
		}else{
			Parameters params = c.getParameters();
			ListScene = setSceneList(params);
			c.release();
	        c = null;
		}
//		}
			
		//get scene
		setScnMenu(ListScene);
		
		String ver = null;
		try {
			ver = mActivity.getPackageManager().getPackageInfo(mActivity.getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		TextView tv0 = (TextView)mActivity.findViewById(R.id.menutext_version);
		tv0.setText(ver);
		
		vibe = (Vibrator) mActivity.getSystemService(Context.VIBRATOR_SERVICE);
		final Button galleryButton = (Button)mActivity.findViewById(R.id.btnGallery);
		galleryButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(ccCamUtil.isDoubleClick())
					return;
				vibe.vibrate(IN.ViberateTime);
				int allPhotoNum = ccPicUtil.getContentCount(mActivity);
				logline.d(TAG,"allPhotoNum : "+ allPhotoNum);
				if(allPhotoNum == 0){
					Toast.makeText(mActivity, R.string.alert_none_image, Toast.LENGTH_SHORT).show();
					return;
				}
				Intent intent = new Intent(mActivity, ccGalleryGridLoader.class);
				mActivity.startActivityForResult(intent, IN.galleryRequestCode);
			}
		});
	}
	
	private void setScnMenu(List<String> sceneList) {
		if(sceneList == null || sceneList.size() == 0){
			sceneList = new ArrayList<String>();
			for(int i=0; i<4; i++){
				sceneList.add(IN.menuScnString[i]);
			}			
		}else{
			sceneList.add(1, IN.CAMERA_FACING);
		}
			
		GridLayout gl = (GridLayout)mActivity.findViewById(R.id.scn_gridlayout);
        LayoutInflater m_inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		for(int i=0; i< IN.menuScnString.length; i++){			
    		for(int j=0; j<sceneList.size() ; j++){
    			String scnStr = sceneList.get(j).trim(); 
    			if(scnStr.contains(IN.SCENE_ACTION))
    				scnStr = IN.SCENE_SPORTS;
    			if(scnStr.contains(IN.SCENE_CANDLELIGHT))
    				scnStr = IN.SCENE_SUNSET; 
    			if(scnStr.contains(IN.SCENE_SNOW))
    				scnStr = IN.SCENE_BEACH; 
    			if(i<4 || scnStr.equalsIgnoreCase(IN.menuScnString[i])){
    				final View row = m_inflater.inflate(R.layout.menu_scn_button, null);
    				FrameLayout listFormLayout = (FrameLayout)row.findViewById(R.id.scnMenuLayout);
    				listFormLayout.setTag(i);
    				listFormLayout.setId(4);
    				final int iCnt = i;
    				
    				ImageView iv = (ImageView)row.findViewById(R.id.menu_image);
    				Button btn =  (Button)row.findViewById(R.id.menu_button);
    				TextView tv = (TextView)row.findViewById(R.id.menu_text);
    				
    				iv.setImageResource(IN.iconImageId[iCnt]);
    				tv.setText(IN.menuScnStringID[iCnt]);
    				btn.setOnClickListener(new View.OnClickListener() {
    					
    					@Override
    					public void onClick(View v) {    						
    						if(IN.menuScnString[iCnt].contains(IN.SCENE_BARCODE))
    							IntentIntegrator.initiateScan(mActivity);
    						else
    							setSceneIntent(IN.menuScnString[iCnt]);
    					}
    				});
    				
    				gl.addView(row);
    				break;
    			}
    		}
    	}		
	}

	private List<String> getSceneList(){
		String sceneStr = ccCamUtil.loadData(mActivity, "scene", null);
		if(sceneStr != null){
			try {
				List<String> list = new ArrayList<String>();
				JSONObject jsonObj = new JSONObject(sceneStr);
				JSONArray jArray = jsonObj.getJSONArray("scenemode");
				for(int i=0; i<jArray.length(); i++){
					String str = jArray.getString(i);
					Log.d(TAG,i+"==="+str);
					list.add(str);
				}
				return list;
			}catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	private List<String> setSceneList(Parameters params){
		
		//저장된 scene 불러옴
//		String sceneStr = ccCamUtil.loadData(mActivity, "scene", null);
		String sceneStr = null;
		//저장된 scene가 없으면 파라메터에서 다시 생성
		if(sceneStr == null){			
			try{				
		        List<String> list = params.getSupportedSceneModes();		        
		        //파라메터가 없을때 처리
		        if(list == null)
		        	return null;
		        
		        JSONObject jObj = new JSONObject();
		        JSONArray jArr = new JSONArray();
		        try {				
					for(int i=0; i<list.size(); i++){
						if(list.get(i).contains(IN.SCENE_BARCODE)){
							list.remove(i);
							continue;
						}
						jArr.put(i, list.get(i));
					}				
					jObj.put("scenemode", jArr);
				}catch (JSONException e) {
					e.printStackTrace();
				}
		        
		        //파라메터에서 불러온 scene을 저장
		        ccCamUtil.saveData(mActivity, "scene", jObj.toString());
		        if(list.size() > 0)
		        	return list;
			}catch(Exception e){
				logline.d(TAG,"ccMenuset Parameters error : " + e);
			}
//		}else{
//			try {
//				List<String> list = new ArrayList<String>();
//				JSONObject jsonObj = new JSONObject(sceneStr);
//				JSONArray jArray = jsonObj.getJSONArray("scenemode");
//				for(int i=0; i<jArray.length(); i++){
//					String str = jArray.getString(i);
//					list.add(str);
//				}
//				return list;
//			}catch (JSONException e) {
//				e.printStackTrace();
//			}
		}
		return null;		
	}
	
	//
	// go to camera on scene
	//
	public void setSceneIntent(String sceneName){
		logline.d(TAG,"==sceneName = "+sceneName);
		if (ccCamUtil.getSdkVer() > 8){
			 if(onlyFrontCamera())
				 sceneName = ""+IN.MODE_FACING_CAMERA;
		}					
		Intent intent = new Intent(mActivity, cCameraActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra("sceneName", sceneName);
		intent.putExtra("getAction", intentGetAction);
		mActivity.startActivity(intent);
	}
	
	private boolean onlyFrontCamera(){
		int cameraMode = ccCamUtil.checkCameraFacingMode();
		if(cameraMode == IN.CAMERA_ID_FRONT)
			return true;
		else
			return false;
		
//    	Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
//	    Camera.getCameraInfo( 0, cameraInfo );
//	    logline.d("cameraInfo ="+cameraInfo.facing);
//	    if(Camera.getNumberOfCameras() == 1 && 
//	    		cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT){		    	
//	    	return true;
//	    }else
//	    	return false;
	}
	
}
