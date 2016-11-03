package com.epriest.cherryCamera.main;

import java.util.List;

import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.epriest.cherryCamera.ApplicationClass;
import com.epriest.cherryCamera.R;
import com.epriest.cherryCamera.util.IN;
import com.epriest.cherryCamera.util.ccCamUtil;
import com.epriest.cherryCamera.util.logline;
/**
 * @author Camera Parameters setting
 *
 */
public class cCameraParameters {

	private ApplicationClass appClass;
	
	
	public cCameraParameters(ApplicationClass appClass){
		this.appClass = appClass;
	}
	
	public void setParameters(Parameters params){		
		appClass.mPreview.mCamera.setParameters(params);
	}
	
	public Parameters getParameters(){
		return appClass.mPreview.mCamera.getParameters();
	}	
	
	public boolean cameraParameterSetting(Camera mSetCamera){
		mSetCamera.stopPreview();
		mSetCamera.startPreview();
		return true;
	}
	
	private Size getOptimalPreviewSize(List<Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.17;
        double targetRatio = (double) w / h;
        if (sizes == null) return null;
 
        Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        // Try to find an size match aspect ratio and size
        for (Size size : sizes) {
            double ratio = (double) size.width / size.height;
            logline.d("ratio="+ratio+", target ratio="+targetRatio);
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            logline.d("size.width."+size.width+",size.height."+size.height+", targetHeight="+targetHeight+", minDiff="+minDiff);
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
            if((int)minDiff == 0 && targetRatio == ratio){
            	optimalSize = size;
            	break;
            }
        }

        // Cannot find the one match the aspect ratio, ignore the requirement
        if (optimalSize == null) {
        	logline.d("optimalSize : null");
            minDiff = Double.MAX_VALUE;
            for (Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }else
        	logline.d("optimalSize : "+optimalSize.width+","+optimalSize.height);
        
        return optimalSize;
    }
		
	public Parameters previewSize(Parameters params, int width, int height){
		logline.d("width="+width+" height="+height);
		appClass.ListPreviewSize = params.getSupportedPreviewSizes();
		appClass.mPreview.previewW = params.getPreviewSize().width;
		appClass.mPreview.previewH = params.getPreviewSize().height;
		logline.d("getPreviewSize : "+appClass.mPreview.previewW+appClass.mPreview.previewH);
		if(appClass.ListPreviewSize == null){
			logline.d("<<none get support preview Size>>");
			return null;
		}else{		
			for(int i=0; i<appClass.ListPreviewSize.size();i++){
				Size size = appClass.ListPreviewSize.get(i);
				logline.d("ListPreviewSize("+i+"):"+size.width + ","+size.height);
			}

			Size size = getOptimalPreviewSize(appClass.ListPreviewSize, width, height);
			if(size != null){
				appClass.mPreview.previewW = size.width;
				appClass.mPreview.previewH = size.height;
			}
		}
		params.setPreviewSize(appClass.mPreview.previewW, appClass.mPreview.previewH);
		setPreviewFrameSize();
		logline.d("getPreviewSize : "+params.getPreviewSize().width+","+params.getPreviewSize().height);
		return params;
	}
	
	private void setPreviewFrameSize(){
		float previewRatio = (float)appClass.lcdHeight/(float)appClass.mPreview.previewH;
		
		float preW = (float)appClass.mPreview.previewW * previewRatio;
		float preH = (float)appClass.mPreview.previewH * previewRatio;
		logline.d("previewRatio : "+previewRatio+","+preW+","+preH);
		FrameLayout preview = (FrameLayout)appClass.getActivity().findViewById(R.id.camera_preview);
		preview.setLayoutParams(new FrameLayout.LayoutParams(
				(int)preW, (int)preH, Gravity.CENTER));
		ImageView mFilter = (ImageView)appClass.getActivity().findViewById(R.id.camera_filter);
		mFilter.setLayoutParams(new FrameLayout.LayoutParams(
				(int)preW, (int)preH, Gravity.CENTER));
		int px = appClass.mPreview.previewW-appClass.lcdWidth;		 
		if(px < 0)			 
			px = 0;		 
		int py = appClass.mPreview.previewH-appClass.lcdHeight;		 
		if(py < 0)			 
			py = 0;
		ImageView mFilterSave = (ImageView)appClass.getActivity().findViewById(R.id.camera_filter_save);
		mFilterSave.setLayoutParams(new FrameLayout.LayoutParams(
				(int)appClass.mPreview.previewW-px, (int)appClass.mPreview.previewH-py, Gravity.CENTER));
	}

	public Parameters whiteBalance(Parameters params){
		appClass.ListWhiteBalance = params.getSupportedWhiteBalance();
		
		if(appClass.ListWhiteBalance == null){
			logline.d("<<none get support WhiteBalance>>");
			return null;
		}else{
			logline.d("\n");
			for(int i=0; i<appClass.ListWhiteBalance.size();i++){
				String str = appClass.ListWhiteBalance.get(i);
				logline.d("ListWhiteBalance("+i+"):"+str);
			}
			params.setWhiteBalance(Camera.Parameters.WHITE_BALANCE_AUTO);
		}
		logline.d("\n");
		return params;
	}
	
	public Parameters supportedAntibanding(Parameters params){
		appClass.ListAntibanding = params.getSupportedAntibanding();
		if(appClass.ListAntibanding == null){
			logline.d("<<none get support antibanding>>");
			return null;
		}else{
			logline.d("\n");
			for(int i=0; i<appClass.ListAntibanding.size();i++){
				String str = appClass.ListAntibanding.get(i);
				logline.d("ListAntibanding("+i+"):"+str);
			}
			if(appClass.ListAntibanding.contains(Camera.Parameters.ANTIBANDING_AUTO)){
				params.setAntibanding(Camera.Parameters.ANTIBANDING_AUTO);
			}
		}
		logline.d("\n");
		return params;
	}
	
	public void colorEffect(Parameters params){
		if(params == null)
			logline.d("<<none params>>");
		appClass.ListColorEffect = params.getSupportedColorEffects();
		if(appClass.ListColorEffect == null){
			logline.d("<<none get support colorEffect mode>>");
			return;
		}else{
			logline.d("\n");
			for(int i=0; i< appClass.ListColorEffect.size(); i++){
				String str = appClass.ListColorEffect.get(i);
				logline.d("ListColorEffect("+i+"):"+str);
			}
		}
		logline.d("\n");
//		app.refreshCameraInfo(mCamera);
	}
	
	public Parameters scene(Parameters params, String sceneName){
		logline.d("sceneName = "+sceneName);
		appClass.ListScene = params.getSupportedSceneModes();
		if(appClass.ListScene == null || appClass.ListScene.size() == 0){
			logline.d("<<none get support scene mode>>");
			return null;
		}else{
//			saveSceneList(appClass.ListScene);
		}
//		logline.d("\n");
		String scnStr = appClass.ListScene.get(0);
		
		for(String scn : appClass.ListScene){
			if(scn.contains(sceneName)){
				scnStr = scn;
				break;
			}
		}		
		params.setSceneMode(scnStr);
		//params.setSceneMode(ListScene.get(0));
//		app.refreshCameraInfo(mCamera);
		return params;
	}
	
//	private void saveSceneList(List<String> listScene) {
//		try{	        
//	        //파라메터가 없을때 처리
//	        if(listScene == null)
//	        	return;
//	        
//	        JSONObject jObj = new JSONObject();
//	        JSONArray jArr = new JSONArray();
//	        try {				
//				for(int i=0; i<listScene.size(); i++){					
//					jArr.put(i, listScene.get(i));
//					logline.d("ListScene("+i+"):"+app.ListScene.get(i));
//				}				
//				jObj.put("scenemode", jArr);
//			}catch (JSONException e) {
//				e.printStackTrace();
//			}
//	        
//	        //파라메터에서 불러온 scene을 저장
//	        ccUtil.saveData(app.mActivity.getBaseContext(), "scene", jObj.toString());
//		}catch(Exception e){
////			logline.d(TAG,"ccMenuset Parameters error : " + e);
//		}
//	}

	public Parameters supportedFocusModes(Parameters params){
		appClass.ListFocus = params.getSupportedFocusModes();
		if(appClass.ListFocus == null || appClass.ListFocus.size() == 0){
			logline.d("<<none get support focus mode>>");
			appClass.ListFocus = null;
			return null;
		}else{
			logline.d("\n");
			for(int i=0; i< appClass.ListFocus.size(); i++){
				String str = appClass.ListFocus.get(i);
				logline.d("ListFocus("+i+"):"+str);
			}
			if(appClass.ListFocus.contains(ccCamUtil.loadStatus(IN.saveFocusMode, appClass)))
				params.setFocusMode(ccCamUtil.loadStatus(IN.saveFocusMode, appClass));
			else
				params.setFocusMode(appClass.ListFocus.get(0));
		}
//		app.refreshCameraInfo(mCamera);
		return params;
	}
	
	public Parameters supportedFlashModes(Parameters params){
		appClass.ListFlashMode = params.getSupportedFlashModes();
		if(appClass.ListFlashMode == null){
			logline.d("<<none get support flash mode>>");
			return null;
		}else{
			logline.d("\n");
			for(int i=0; i< appClass.ListFlashMode.size(); i++){
				String str = appClass.ListFlashMode.get(i);
				logline.d("ListFlashMode("+i+"):"+str);
			}
			if(appClass.ListFlashMode.contains(ccCamUtil.loadStatus(IN.saveFlashMode, appClass)))
				params.setFlashMode(ccCamUtil.loadStatus(IN.saveFlashMode, appClass));
			else
				params.setFlashMode(Parameters.FLASH_MODE_OFF);
		}
		
		logline.d("\n");
//		app.refreshCameraInfo(mCamera);
		return params;
	}
	
	public void pictureSize(Parameters params){
		appClass.ListPictureSize = params.getSupportedPictureSizes();		
		if(appClass.ListPictureSize == null){
			logline.d("<<none get support pictureSize mode>>");
			return;
		}else{
			logline.d("\n");
//			for(int i=0; i< ListPictureSize.size(); i++){
////				Size size = ListPictureSize.get(i);
//				logline.d("ListPictureSize("+i+"):"+ListPictureSize.get(i).width
//						+","+ListPictureSize.get(i).height);
//			}
			
			
			
			// small memory device fixed resolution to 2048
			long totalRam = IN.LOW_RAM;
			if(ccCamUtil.getSdkVer() < 9){
				totalRam = ccCamUtil.getTotalMemory();
				logline.d("totalRam : "+totalRam);
			}	
			logline.d("totalRam = "+totalRam);

			int picW, picH;
			if(totalRam < IN.LOW_RAM){
				logline.d("=="+appClass.ListPictureSize.size());
				for(int i=appClass.ListPictureSize.size()-1; i >= 0; i--){
					logline.d("ListPictureSize("+i+"):"+appClass.ListPictureSize.get(i).width
							+","+appClass.ListPictureSize.get(i).height);
					if(appClass.ListPictureSize.get(i).width > 2048){
						appClass.ListPictureSize.remove(i);
					}
				}
				
//				if(ListPictureSize.get(app.pictureSize).width > 2048){
//					if(ListPictureSize.get(0).width < 2048){
//						picW = ListPictureSize.get(0).width;
//						picH = ListPictureSize.get(0).height;
//					}else{
//						picW = ListPictureSize.get(ListPictureSize.size()-1).width;
//						picH = ListPictureSize.get(ListPictureSize.size()-1).height;
//					}
//				}else{
//					picW = ListPictureSize.get(app.pictureSize).width;
//					picH = ListPictureSize.get(app.pictureSize).height;
//				}
//			}else{
//				picW = ListPictureSize.get(app.pictureSize).width;
//				picH = ListPictureSize.get(app.pictureSize).height;
			}
			
			//first setting is max resolution
			int defTag = 0;
			if(appClass.ListPictureSize.get(0).width < appClass.ListPictureSize.get(appClass.ListPictureSize.size()-1).width)
				defTag = appClass.ListPictureSize.size()-1;
			if(appClass.mMenuset.frontCameraAccess)
				appClass.pictureSize = ccCamUtil.loadStatusInt(defTag, IN.saveFrontPictureSize, appClass);
			else
				appClass.pictureSize = ccCamUtil.loadStatusInt(defTag, IN.saveBackPictureSize, appClass);

			if(appClass.pictureSize > appClass.ListPictureSize.size()-1)
				appClass.pictureSize = appClass.ListPictureSize.size()-1;
			
			picW = appClass.ListPictureSize.get(appClass.pictureSize).width;
			picH = appClass.ListPictureSize.get(appClass.pictureSize).height;
			
			params.setPictureSize(picW, picH);			
		}
//		logline.d("app.pictureSize = "+mCamera.getParameters().getPictureSize().width);
	}
	
	public int exposure(Camera camera, int expoNum){
		Parameters params = camera.getParameters();
		try{
			int expos = params.getMaxExposureCompensation();
			
			if(expos == 0){
				logline.d("<<none get support pictureSize mode>>");
			}else{			
				if(expoNum > camera.getParameters().getMaxExposureCompensation())
					expoNum = camera.getParameters().getMaxExposureCompensation();
				else if(expoNum < camera.getParameters().getMinExposureCompensation())
					expoNum = camera.getParameters().getMinExposureCompensation();
					
				params.setExposureCompensation(expoNum);
				camera.setParameters(params);
				expos = params.getExposureCompensation();
	
				logline.d("-----------------exposure = "+expoNum+" , "+expos);
			}
//			mCamera.setParameters(params);
//			app.refreshCameraInfo(mCamera);
		}catch(Exception e){
			TextView tv = (TextView)appClass.getActivity().findViewById(R.id.text_camera_exposure);
			tv.setText("not support");
		}
		return expoNum;		
	}
	
	public void keepExposure(Camera camera){
		Parameters params = camera.getParameters();
		int expos = params.getExposureCompensation();
//		exposure(camera, 0);
//		camera.setParameters(params);
		exposure(camera, expos);	
//		camera.setParameters(params);
		logline.d("---keepExposure : "+params.getExposureCompensation());
	}

}
