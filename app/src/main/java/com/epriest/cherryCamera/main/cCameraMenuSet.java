package com.epriest.cherryCamera.main;

import com.epriest.cherryCamera.ApplicationClass;
import com.epriest.cherryCamera.R;
import com.epriest.cherryCamera.gallery.ccGalleryPicviewAct;
import com.epriest.cherryCamera.util.IN;
import com.epriest.cherryCamera.util.ccCamUtil;
import com.epriest.cherryCamera.util.ccPicUtil;
import com.epriest.cherryCamera.util.logline;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Camera.Parameters;
import android.os.Handler;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import java.security.Policy;

/**
 * @author Main Menu Setting
 *
 */
public class cCameraMenuSet implements OnClickListener, RadioGroup.OnCheckedChangeListener
, OnCheckedChangeListener, OnSeekBarChangeListener{

	private String TAG = this.getClass().getSimpleName();
	
	Toast toast;
	int effectNumber;
	int expoNum;
	int sceneNum;
	
	int timerCountMil = 0;
	int timerCount = 0;
	
	String lastImageData;	
	ApplicationClass appClass;

	RadioGroup flash_rg;
	RadioGroup focus_rg;
	RadioGroup timer_rg;
	RadioGroup filter_rg;
	
	public boolean frontCameraAccess = false;
	
	private ViewFlipper vf;
	private ViewFlipper vf_option;
	public MyCountDownTimer timer;
	public cCameraMenuSet(ApplicationClass appClass) {
		this.appClass = appClass;
		timer  = new MyCountDownTimer(1000);
	}
	
	public void btnSet(){
		
		appClass.getActivity().findViewById(R.id.btn_camera_option).setOnClickListener(this);
				
		appClass.getActivity().findViewById(R.id.btn_camera_shutter).setOnClickListener(this);
		appClass.getActivity().findViewById(R.id.camera_preview_pitch_on).setOnClickListener(this);
				
		appClass.getActivity().findViewById(R.id.btn_optionmenu_close).setOnClickListener(this);
		appClass.getActivity().findViewById(R.id.btn_optionmenu_filter_close).setOnClickListener(this);
		appClass.getActivity().findViewById(R.id.btn_optionmenu_flash_close).setOnClickListener(this);
		appClass.getActivity().findViewById(R.id.btn_optionmenu_focus_close).setOnClickListener(this);
		appClass.getActivity().findViewById(R.id.btn_optionmenu_timer_close).setOnClickListener(this);
		
		
//		appClass.getActivity().findViewById(R.id.btn_option_help).setOnClickListener(this);
		
//		appClass.getActivity().findViewById(R.id.btn_option_changecamera).setOnClickListener(this);		
	
//		appClass.getActivity().findViewById(R.id.btn_camera_flash).setOnClickListener(this);
		appClass.getActivity().findViewById(R.id.btn_option_flash).setOnClickListener(this);
		appClass.getActivity().findViewById(R.id.icon_camera_flash).setOnClickListener(this);
		
//		appClass.getActivity().findViewById(R.id.btn_camera_focus).setOnClickListener(this);
		appClass.getActivity().findViewById(R.id.btn_option_focus).setOnClickListener(this);
		appClass.getActivity().findViewById(R.id.icon_camera_focus).setOnClickListener(this);
		
//		appClass.getActivity().findViewById(R.id.btn_option_timer).setOnClickListener(this);
//		appClass.getActivity().findViewById(R.id.icon_camera_timer).setOnClickListener(this);
		appClass.getActivity().findViewById(R.id.btn_camera_timer).setOnClickListener(this);
		
		appClass.getActivity().findViewById(R.id.btn_camera_face).setOnClickListener(this);
		
		appClass.getActivity().findViewById(R.id.btn_option_effect).setOnClickListener(this);
		
		appClass.getActivity().findViewById(R.id.btn_option_resolution).setOnClickListener(this);
		appClass.getActivity().findViewById(R.id.info_resolution).setOnClickListener(this);		

		appClass.getActivity().findViewById(R.id.btn_cherryfilter).setOnClickListener(this);
		
		appClass.getActivity().findViewById(R.id.btn_camera_scene).setOnClickListener(this);
		
//		appClass.getActivity().findViewById(R.id.btn_camera_zoom).setOnClickListener(this);		
		
		ImageView mGalleryBtn = (ImageView)appClass.getActivity().findViewById(R.id.img_photo_gallery);
		mGalleryBtn.setOnClickListener(this);
//		appClass.thumW = mGalleryBtn.getWidth();
//		appClass.thumH = mGalleryBtn.getHeight();
//		mGalleryBtn.getLayoutParams().width = appClass.thumW;
//		mGalleryBtn.getLayoutParams().height = appClass.thumH;
		
//		appClass.getActivity().findViewById(R.id.btn_camera_freememory).setOnClickListener(this);
		appClass.getActivity().findViewById(R.id.layout_info).setOnClickListener(this);

		appClass.getActivity().findViewById(R.id.text_camera_exposure).setOnClickListener(this);
		SeekBar expoSeekBar = (SeekBar)appClass.getActivity().findViewById(R.id.seekbar_expo);
		expoSeekBar.setOnSeekBarChangeListener(this);
		if(appClass.expoMaxSize == 0 || ccCamUtil.getSdkVer() < 8){
        	expoSeekBar.setVisibility(View.INVISIBLE);
        }else{
        	try{
	        	int expoNum = appClass.mParameter.getParameters().getMaxExposureCompensation() -
	        			appClass.mParameter.getParameters().getMinExposureCompensation();
	        	expoSeekBar.setMax(expoNum);
	        	expoSeekBar.setProgress(expoNum/2);
        	}catch(Exception e){
        		
        	}
        }
		vf=(ViewFlipper)appClass.getActivity().findViewById(R.id.ViewFlipper01);
		vf_option = (ViewFlipper)appClass.getActivity().findViewById(R.id.ViewFlipper_option);
		
		SeekBar zoomSeekBar = (SeekBar)appClass.getActivity().findViewById(R.id.seekbar_zoom);
		zoomSeekBar.setOnSeekBarChangeListener(this);
		try{
			int zoomNum = appClass.mParameter.getParameters().getMaxZoom();
	    	zoomSeekBar.setMax(zoomNum);
	    	zoomSeekBar.setProgress(0);
		}catch(Exception e){
			
		}

//		final Parameters params = appClass.mParameter.getParameters();

	}
	
	public void openOptionClose(){	
		if(vf_option.getDisplayedChild() == 0 || appClass.openOption == IN.MODE_OPENMENU_FLASH 
				|| appClass.openOption == IN.MODE_OPENMENU_FOCUS || appClass.openOption == IN.MODE_OPENMENU_FILTER
				|| appClass.openOption == IN.MODE_OPENMENU_TIMER){
			appClass.openOption = IN.MODE_OPENMENU_CLOSE;
			vf.setInAnimation(ccPicUtil.inFromRightAnimation());
            vf.setOutAnimation(ccPicUtil.outToLeftAnimation());
            vf.showPrevious();
		}else{
			vf_option.setDisplayedChild(0);
			TextView optionTitleText = (TextView)appClass.getActivity().findViewById(R.id.option_title_text);
			optionTitleText.setText(R.string.camera_option);
		}
	}
	
	public void optionSet(){
		if(appClass.ListFlashMode == null){
			FrameLayout fl = (FrameLayout)appClass.getActivity().findViewById(R.id.optionmenu_flash);
			fl.setVisibility(View.GONE);
			ImageView iv = (ImageView) appClass.getActivity().findViewById(R.id.icon_camera_flash);
			iv.setVisibility(View.GONE);
		}else{
			flash_rg = (RadioGroup)appClass.getActivity().findViewById(R.id.radioGroup_flash);
			if(!appClass.ListFlashMode.contains(Parameters.FLASH_MODE_AUTO))flash_rg.removeViewInLayout(appClass.getActivity().findViewById(R.id.flash_radio0));
			if(!appClass.ListFlashMode.contains(Parameters.FLASH_MODE_ON))flash_rg.removeViewInLayout(appClass.getActivity().findViewById(R.id.flash_radio1));
			if(!appClass.ListFlashMode.contains(Parameters.FLASH_MODE_OFF))flash_rg.removeViewInLayout(appClass.getActivity().findViewById(R.id.flash_radio2));
			if(!appClass.ListFlashMode.contains(Parameters.FLASH_MODE_RED_EYE))flash_rg.removeViewInLayout(appClass.getActivity().findViewById(R.id.flash_radio3));
			if(!appClass.ListFlashMode.contains(Parameters.FLASH_MODE_TORCH))flash_rg.removeViewInLayout(appClass.getActivity().findViewById(R.id.flash_radio4));						
			flash_rg.setOnCheckedChangeListener(this);
		}

		if(appClass.ListFocus == null){
			FrameLayout fl = (FrameLayout)appClass.getActivity().findViewById(R.id.optionmenu_focus);
			fl.setVisibility(View.GONE);
			ImageView iv = (ImageView) appClass.getActivity().findViewById(R.id.icon_camera_focus);
			iv.setVisibility(View.GONE);
		}else{
			focus_rg = (RadioGroup)appClass.getActivity().findViewById(R.id.radioGroup_focus);
			if(!appClass.ListFocus.contains(Parameters.FOCUS_MODE_AUTO))focus_rg.removeViewInLayout(appClass.getActivity().findViewById(R.id.focus_radio0));
			if(!appClass.ListFocus.contains(Parameters.FOCUS_MODE_MACRO))focus_rg.removeViewInLayout(appClass.getActivity().findViewById(R.id.focus_radio1));
			if(!appClass.ListFocus.contains(Parameters.FOCUS_MODE_INFINITY))focus_rg.removeViewInLayout(appClass.getActivity().findViewById(R.id.focus_radio2));
			if(!appClass.ListFocus.contains(Parameters.FOCUS_MODE_FIXED))focus_rg.removeViewInLayout(appClass.getActivity().findViewById(R.id.focus_radio3));
			focus_rg.setOnCheckedChangeListener(this);
		}
		
		if(appClass.ListColorEffect == null){
			FrameLayout fl = (FrameLayout)appClass.getActivity().findViewById(R.id.optionmenu_effect);
			fl.setVisibility(View.GONE);
		}

		if(appClass.ListWhiteBalance == null){
			
		}else{
			
		}
		
		if(appClass.expoMaxSize == 0){
		}
		
		CheckBox cb=(CheckBox)appClass.getActivity().findViewById(R.id.check_option_timerbeep);
		cb.setOnCheckedChangeListener(this);
		
		timer_rg = (RadioGroup)appClass.getActivity().findViewById(R.id.radioGroup_timer);
		timer_rg.setOnCheckedChangeListener(this);
		
		filter_rg = (RadioGroup)appClass.getActivity().findViewById(R.id.radioGroup_filter);
		filter_rg.setOnCheckedChangeListener(this);
	}
	
	int effectNum;
	public void cameraEffectList(final Parameters params){		
		String[] effectArr = appClass.ListColorEffect.toArray(
				new String[appClass.ListColorEffect.size()]);

		new AlertDialog.Builder(appClass.getActivity())
		.setTitle(R.string.camera_option_effect)
		.setIcon(R.drawable.effect_other)
		
		.setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener() {			
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		})
		
		.setSingleChoiceItems(effectArr, effectNum, new DialogInterface.OnClickListener() {			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				effectNum = which;
				params.setColorEffect(appClass.ListColorEffect.get(effectNum));
				appClass.mParameter.setParameters(params);				
				ccCamUtil.refreshCameraInfo(appClass);
				dialog.dismiss();
			}
		})
		.show();
	}
	
	int scnNum;
	public void cameraSceneList(final Parameters params){
		String[] sceneArr = appClass.ListScene.toArray(
				new String[appClass.ListScene.size()]);
		
		new AlertDialog.Builder(appClass.getActivity())
		.setTitle(R.string.menutab_scene)
		.setIcon(R.drawable.scene_nightportrait)
		
		.setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener() {			
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		})
		
		.setSingleChoiceItems(sceneArr, scnNum, new DialogInterface.OnClickListener() {			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				scnNum = which;
				params.setSceneMode(appClass.ListScene.get(scnNum));
				appClass.mParameter.setParameters(params);				
				ccCamUtil.refreshCameraInfo(appClass);
				dialog.dismiss();
			}
		})
		.show();
	}
	
	public void cameraResolutionList(final Parameters params){
//		if(appClass.frontCameraAccess)
//			return;
		
		long totalRam = IN.LOW_RAM;
		if(ccCamUtil.getSdkVer() < 9){
			totalRam = ccCamUtil.getTotalMemory();
			logline.d(TAG, "totalRam : "+totalRam);
		}		
		String[] picsizeArr;	
//		if(totalRam < IN.LOW_RAM){
//			int num = 0;
//			String[] tempArr = new String[appClass.ListPictureSize.size()];
//			for(int i=0; i<tempArr.length ; i++){
//				String pSizeMsg = appClass.ListPictureSize.get(i).width+"x"+appClass.ListPictureSize.get(i).height;
//				if(appClass.ListPictureSize.get(i).width <= 2048){
//					tempArr[i] = pSizeMsg;
//					num++;
//				}						
//			}
//			picsizeArr = new String[num];
//			System.arraycopy(tempArr, 0, picsizeArr, 0, num);
//		}else{
			picsizeArr = new String[appClass.ListPictureSize.size()];
			for(int i=0; i<picsizeArr.length ; i++){
				String pSizeMsg = appClass.ListPictureSize.get(i).width+"x"+appClass.ListPictureSize.get(i).height;			
				picsizeArr[i] = pSizeMsg;									
			}
//		}
		logline.d(TAG,"picsizeArr = "+picsizeArr.length);
		new AlertDialog.Builder(appClass.getActivity())
		.setTitle(R.string.camera_option_resolution)
		.setIcon(R.drawable.picsize)		
		.setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener() {			
			@Override
			public void onClick(DialogInterface dialog, int which) {				
			}
		})
		
		.setSingleChoiceItems(picsizeArr, appClass.pictureSize, new DialogInterface.OnClickListener() {			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				appClass.pictureSize = which;
//				try {
//					mPreview.mCamera.stopPreview();
//		        } catch (Exception e){
//		          logline.d("mPreview.stopPreviewException : "+e.getMessage());
//		        }

				params.setPictureSize(appClass.ListPictureSize.get(which).width
						,appClass.ListPictureSize.get(which).height);
				appClass.mParameter.setParameters(params);
				//save camera status
				if(frontCameraAccess)
					ccCamUtil.statusSave(appClass.getApplicationContext(), IN.saveFrontPictureSize, appClass.pictureSize, null);
				else
					ccCamUtil.statusSave(appClass.getApplicationContext(), IN.saveBackPictureSize, appClass.pictureSize, null);
				ccCamUtil.refreshCameraInfo(appClass);
				dialog.dismiss();
			}
		})
		.show();
	}
	
	public void cameraFlashSet(String mode){
		Parameters params = appClass.mParameter.getParameters();
		params.setFlashMode(mode);
		try{
			appClass.mParameter.setParameters(params);
		}catch(Exception e){
			logline.d(TAG,"Set Parameters Exception : "+e);
		}
		
		if(!frontCameraAccess)
			ccCamUtil.statusSave(appClass.getActivity(), IN.saveFlashMode, 0, appClass.mParameter.getParameters().getFlashMode());
		ccCamUtil.refreshCameraInfo(appClass);
	}
	
	public void cameraFocusSet(String mode){
		Parameters params = appClass.mParameter.getParameters();
		params.setFocusMode(mode);
		try{
			appClass.mParameter.setParameters(params);
		}catch(Exception e){
			logline.d(TAG,"Set Parameters Exception : "+e);
		}
		
		appClass.mParameter.setParameters(params);
		if(!frontCameraAccess)
			ccCamUtil.statusSave(appClass.getActivity(), IN.saveFocusMode, 0, appClass.mParameter.getParameters().getFocusMode());
		
		ccCamUtil.refreshCameraInfo(appClass);	
	}
	
	private void cameraFilter(){
		selectFilter(appClass.isCherryFilter);
		ccCamUtil.refreshCameraInfo(appClass);
	}
	
	private void selectFilter(int isCherryFilter){
		ImageButton cherryFilterButton = (ImageButton)appClass.getActivity().findViewById(R.id.btn_cherryfilter);
		ImageView outlineFilter = (ImageView)appClass.getActivity().findViewById(R.id.camera_filter);
		ImageView outlineFilterSave = (ImageView)appClass.getActivity().findViewById(R.id.camera_filter_save);
		switch(isCherryFilter){
		case 0:
			cherryFilterButton.setImageResource(R.drawable.cherry_off);
			outlineFilter.setImageResource(0);
			outlineFilterSave.setImageResource(0);
//			String rStr = appClass.ListPictureSize.get(appClass.pictureSize).width+"x"+
//					appClass.ListPictureSize.get(appClass.pictureSize).height;
//			resButton.setText(rStr);
//			Toast.makeText(appClass.getActivity(), appClass.getActivity().getText(R.string.change_resolution)+" : "+rStr, Toast.LENGTH_SHORT).show();
			break;
		case 1:
			cherryFilterButton.setImageResource(R.drawable.cherry_on);
			outlineFilter.setImageResource(R.drawable.cherry_filter1);
			outlineFilterSave.setImageResource(R.drawable.cherry_filter1);
//			Toast.makeText(appClass.getActivity(), appClass.getActivity().getText(R.string.change_resolution)+" : 800x480", Toast.LENGTH_SHORT).show();
			break;
		case 2:
			cherryFilterButton.setImageResource(R.drawable.cherry_on);
			outlineFilter.setImageResource(R.drawable.cherry_filter2);
			outlineFilterSave.setImageResource(R.drawable.cherry_filter2);
			break;
		case 3:
			cherryFilterButton.setImageResource(R.drawable.cherry_on);
			outlineFilter.setImageResource(R.drawable.cherry_filter3);
			outlineFilterSave.setImageResource(R.drawable.cherry_filter3);
			break;
		}
	}

	@Override
	public void onClick(View v) {		
		ImageView focusAim = (ImageView)appClass.getActivity().findViewById(R.id.camera_preview_focus);
		Button captureButton = (Button)appClass.getActivity().findViewById(R.id.btn_camera_shutter);
		Parameters params = appClass.mParameter.getParameters();
//		ImageView optionTitleIcon = (ImageView)appClass.getActivity().findViewById(R.id.option_title_icon);
//		TextView optionTitleText = (TextView)appClass.getActivity().findViewById(R.id.option_title_text);
		
		if(appClass.mPreview.iShutter)
			return;	
		final Vibrator vibe = (Vibrator)appClass.getActivity().getSystemService(Context.VIBRATOR_SERVICE);
		vibe.vibrate(IN.ViberateTime);
		
		switch(v.getId()){
		//option menu
		case R.id.btn_optionmenu_close:
		case R.id.btn_optionmenu_filter_close:
		case R.id.btn_optionmenu_focus_close:
		case R.id.btn_optionmenu_flash_close:
		case R.id.btn_optionmenu_timer_close:
			logline.d(TAG,"btn_option_close = "+appClass.openOption);
			if(appClass.openOption != IN.MODE_OPENMENU_CLOSE){
				openOptionClose();
			}
			break;
		case R.id.btn_option_help:
//			final ImageView iv = (ImageView)appClass.getActivity().findViewById(R.id.helpImage);
//			if(!appClass.helpFlag){
//				iv.setVisibility(View.VISIBLE);
//				appClass.helpFlag = true;
//			}
			break;
		case R.id.btn_option_resolution:
			cameraResolutionList(params);
			break;
		case R.id.btn_option_effect:
			cameraEffectList(params);
			break;
		case R.id.btn_option_timer:
//			vf_option.setDisplayedChild(3);
//			optionTitleText.setText(R.string.camera_option_timer);
			break;
			
		//menu upper
		case R.id.btn_option_flash:		
//			appClass.openOption = IN.MODE_OPENMENU_FLASH;
//			vf.setInAnimation(ccUtil.inFromLeftAnimation());
//			vf.setOutAnimation(ccUtil.outToRightAnimation());
//			vf.showNext();
			if(params.getFlashMode().contains(Parameters.FLASH_MODE_AUTO))flash_rg.check(R.id.flash_radio0);
			else if(params.getFlashMode().contains(Parameters.FLASH_MODE_ON))flash_rg.check(R.id.flash_radio1);
			else if(params.getFlashMode().contains(Parameters.FLASH_MODE_OFF))flash_rg.check(R.id.flash_radio2);
			else if(params.getFlashMode().contains(Parameters.FLASH_MODE_RED_EYE))flash_rg.check(R.id.flash_radio3);
			else if(params.getFlashMode().contains(Parameters.FLASH_MODE_TORCH))flash_rg.check(R.id.flash_radio4);
			vf_option.setDisplayedChild(1);
			break;
		case R.id.btn_option_focus:		
//			appClass.openOption = IN.MODE_OPENMENU_FOCUS;
//			appClassation(ccUtil.inFromLeftAnimation());
//			vf.setOutAnimation(ccUtil.outToRightAnimation());
//			vf.showNext();
			if(params.getFocusMode().contains(Parameters.FOCUS_MODE_AUTO))focus_rg.check(R.id.focus_radio0);
			else if(params.getFocusMode().contains(Parameters.FOCUS_MODE_MACRO))focus_rg.check(R.id.focus_radio1);
			else if(params.getFocusMode().contains(Parameters.FOCUS_MODE_INFINITY))focus_rg.check(R.id.focus_radio2);
			else if(params.getFocusMode().contains(Parameters.FOCUS_MODE_FIXED))focus_rg.check(R.id.focus_radio3);
			vf_option.setDisplayedChild(2);
			break;		
		case R.id.info_resolution:
			if(appClass.isCherryFilter == 0)
				cameraResolutionList(params);
			else{
				String pictureSize = appClass.mPreview.previewW
						+"x"+appClass.mPreview.previewH;
				Toast.makeText(appClass.getActivity(), 
						appClass.getActivity().getText(R.string.camera_option_resolution)
						+": "+pictureSize, Toast.LENGTH_SHORT).show();
			}
			break;
//		case R.id.btn_camera_freememory:
		case R.id.layout_info:
			AlertDialog dial = InfoFreeMemDialog();
			dial.show();
			break;
		//menu left	
//		case R.id.btn_option_changecamera:
//			
//			break;
		case R.id.btn_camera_face:
			appClass.mPreview.releaseCamera();
			if(frontCameraAccess)
				frontCameraAccess = false;
			else
				frontCameraAccess = true;
			appClass.mPreview.connectCamera();
			appClass.mPreview.setParam();
			break;
		case R.id.btn_camera_timer:
		case R.id.icon_camera_timer:
			if(appClass.openOption != IN.MODE_OPENMENU_CLOSE)
				return;
			appClass.openOption = IN.MODE_OPENMENU_TIMER;
			vf.setInAnimation(ccPicUtil.inFromLeftAnimation());
			vf.setOutAnimation(ccPicUtil.outToRightAnimation());
			vf.showNext();
			vf_option.setDisplayedChild(3);
//			optionTitleIcon.setImageResource(R.drawable.menu);
//			optionTitleText.setText(R.string.camera_option_timer);
//			Toast.makeText(appClass.getActivity(), appClass.getActivity().getText(R.string.), Toast.LENGTH_SHORT).show();
			break;
//		case R.id.btn_camera_flash:
		case R.id.icon_camera_flash:
			if(appClass.openOption != IN.MODE_OPENMENU_CLOSE)
				return;
			appClass.openOption = IN.MODE_OPENMENU_FLASH;
			vf.setInAnimation(ccPicUtil.inFromLeftAnimation());
			vf.setOutAnimation(ccPicUtil.outToRightAnimation());
			vf.showNext();
			if(params.getFlashMode().contains(Parameters.FLASH_MODE_AUTO))flash_rg.check(R.id.flash_radio0);
			else if(params.getFlashMode().contains(Parameters.FLASH_MODE_ON))flash_rg.check(R.id.flash_radio1);
			else if(params.getFlashMode().contains(Parameters.FLASH_MODE_OFF))flash_rg.check(R.id.flash_radio2);
			else if(params.getFlashMode().contains(Parameters.FLASH_MODE_RED_EYE))flash_rg.check(R.id.flash_radio3);
			else if(params.getFlashMode().contains(Parameters.FLASH_MODE_TORCH))flash_rg.check(R.id.flash_radio4);
			vf_option.setDisplayedChild(1);
//			optionTitleText.setText(R.string.camera_option_flash);
//			optionTitleIcon.setImageResource(R.drawable.flash_on);
			break;
//		case R.id.btn_camera_focus:
		case R.id.icon_camera_focus:
			if(appClass.openOption != IN.MODE_OPENMENU_CLOSE)
				return;
			appClass.openOption = IN.MODE_OPENMENU_FOCUS;
			vf.setInAnimation(ccPicUtil.inFromLeftAnimation());
			vf.setOutAnimation(ccPicUtil.outToRightAnimation());
			vf.showNext();
			if(params.getFocusMode().contains(Parameters.FOCUS_MODE_AUTO))focus_rg.check(R.id.focus_radio0);
			else if(params.getFocusMode().contains(Parameters.FOCUS_MODE_MACRO))focus_rg.check(R.id.focus_radio1);
			else if(params.getFocusMode().contains(Parameters.FOCUS_MODE_INFINITY))focus_rg.check(R.id.focus_radio2);
			else if(params.getFocusMode().contains(Parameters.FOCUS_MODE_FIXED))focus_rg.check(R.id.focus_radio3);
			vf_option.setDisplayedChild(2);
//			optionTitleIcon.setImageResource(R.drawable.focus_macro);
//			optionTitleText.setText(R.string.camera_option_focus);
			break;
		case R.id.text_camera_exposure:
			int expoNum = appClass.mParameter.getParameters().getMaxExposureCompensation() -
			appClass.mParameter.getParameters().getMinExposureCompensation();
			SeekBar expoSeekBar = (SeekBar)appClass.getActivity().findViewById(R.id.seekbar_expo);
			expoSeekBar.setMax(expoNum);
			expoSeekBar.setProgress(expoNum/2);
//			params.setExposureCompensation(0);
			break;
		case R.id.btn_cherryfilter:
			appClass.openOption = IN.MODE_OPENMENU_FILTER;
			vf.setInAnimation(ccPicUtil.inFromLeftAnimation());
			vf.setOutAnimation(ccPicUtil.outToRightAnimation());
			vf.showNext();			
			vf_option.setDisplayedChild(4);
//			optionTitleText.setText(R.string.camera_option_filter);
//			optionTitleIcon.setImageResource(R.drawable.cherry_on);
			break;
		case R.id.btn_camera_zoom:
			appClass.openOption = IN.MODE_OPENMENU_ZOOM;
			vf.setInAnimation(ccPicUtil.inFromLeftAnimation());
			vf.setOutAnimation(ccPicUtil.outToRightAnimation());
			vf.showNext();			
			vf_option.setDisplayedChild(5);
			break;
		case R.id.btn_camera_option:
			if(appClass.openOption != IN.MODE_OPENMENU_OPTION){
				appClass.openOption = IN.MODE_OPENMENU_OPTION;
				vf.setInAnimation(ccPicUtil.inFromLeftAnimation());
				vf.setOutAnimation(ccPicUtil.outToRightAnimation());
				vf.showNext();
				vf_option.setDisplayedChild(0);
//				optionTitleIcon.setImageResource(R.drawable.menu);
//				optionTitleText.setText(R.string.camera_option);
			}
			break;
			
		//menu right
		case R.id.btn_camera_scene:
			if(appClass.ListScene == null){
				appClass.getActivity().finish();
			}else {
				cameraSceneList(params);
			}
			break;
		case R.id.btn_camera_shutter:
			if(appClass.openOption != IN.MODE_OPENMENU_CLOSE){
				openOptionClose();
			}			
			if(timerCount > 0){
				timerCountMil = timerCount*1000;
				
				TextView tv = (TextView)appClass.getActivity().findViewById(R.id.camera_timer_text);
				timer.setTextView(tv);
				timer.Start();
			}else{
				appClass.mPreview.shootOn();
			}
			break;		
		case R.id.img_photo_gallery:
			if(ccCamUtil.isDoubleClick())
				return;
			int allPhotoSize = ccPicUtil.getContentCount(appClass.getActivity());

			//내부갤러리 이용
//			File file = new File(lastImageData);
//			Intent intent = new Intent(android.content.Intent.ACTION_VIEW);
//			intent.setDataAndType(Uri.parse("file://" + file.getAbsolutePath()), "image/*");
//			appClass.getActivity().startActivity(intent);

			logline.d(TAG,"allPhotoNum : "+allPhotoSize);
			if(allPhotoSize == 0){
				Toast.makeText(appClass.getActivity(), R.string.alert_none_image, Toast.LENGTH_SHORT).show();
				return;
			}
			
			Intent intent = new Intent(appClass.getActivity(), ccGalleryPicviewAct.class);
			intent.putExtra("pos", 0);
			appClass.getActivity().startActivityForResult(intent, IN.galleryRequestCode);
			break;
			
		//etc
		case R.id.camera_preview_pitch_on:
			if(appClass.mPreview.isSetFocus){
				appClass.mPreview.isSetFocus = false;
				focusAim.setImageResource(R.drawable.aim1);
				focusAim.setVisibility(View.INVISIBLE);
				captureButton.setBackgroundResource(R.drawable.shutter_normal);
				ImageView ivCameraShutter = (ImageView)appClass.getActivity().findViewById(R.id.img_camera_shutter_icon);
				ivCameraShutter.setImageResource(R.drawable.shutter);
			}else{
				if(appClass.ListFocus == null ||
						params.getFocusMode().equals(Parameters.FOCUS_MODE_FIXED)){}else{
//					focusAim.setImageResource(R.drawable.aim2);
					focusAim.setVisibility(View.VISIBLE);
					appClass.mPreview.mCamera.autoFocus(appClass.mPreview.mFocusCallback);
				}
			}
			break;
		}
	}

	public AlertDialog InfoFreeMemDialog(){
//		LinearLayout ll = (LinearLayout)appClass.getActivity().findViewById(R.id.alertFrame);
		View layout = appClass.getActivity().getLayoutInflater().inflate(R.layout.alertdialog_meminfo,
				(ViewGroup)appClass.getActivity().findViewById(R.id.layout_root));
//		ll.setBackgroundResource(R.color.alert_bg);
		AlertDialog.Builder builder = new AlertDialog.Builder(appClass.getActivity());
		builder.setView(layout);		
		
		TextView txtFreeMem = (TextView) layout.findViewById(R.id.tv_mem_alert_freemem);
		TextView txtResolusion = (TextView) layout.findViewById(R.id.tv_mem_alert_resolusion);
		TextView txtBattery = (TextView) layout.findViewById(R.id.tv_mem_alert_battery);
		
		FrameLayout memLineBg = (FrameLayout)layout.findViewById(R.id.mem_alert_freemem_line_bg);
		LinearLayout memLine = (LinearLayout)layout.findViewById(R.id.mem_alert_freemem_line);
		String freememStr = ccCamUtil.refreshMemory(appClass);
		String[] arr = freememStr.split(",");
		float freemem = Float.parseFloat(arr[0]);
		float maxmem = Float.parseFloat(arr[1]);
		if(freemem < maxmem/20)
			memLine.setBackgroundColor(appClass.getResources().getColor(R.color.color_red));
		android.view.ViewGroup.LayoutParams params1 = memLine.getLayoutParams();
		params1.width = (int) (memLineBg.getLayoutParams().width*freemem/maxmem);
		memLine.setLayoutParams(params1);
		txtFreeMem.setText(freemem+"GB/"+maxmem+"GB");
		
		DisplayMetrics displayMetrics = new DisplayMetrics();
		appClass.getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		txtResolusion.setText(displayMetrics.widthPixels+"x"+displayMetrics.heightPixels + ","+
				displayMetrics.densityDpi+"dp");
		
		Button btn = (Button)appClass.getActivity().findViewById(R.id.btn_camera_bettery);
		String batteryStr = (String) btn.getText();
		FrameLayout batteryLineBg = (FrameLayout)layout.findViewById(R.id.mem_alert_battery_line_bg);
		LinearLayout batteryLine = (LinearLayout)layout.findViewById(R.id.mem_alert_battery_line);
		int value = Integer.parseInt(batteryStr.replace("%", ""));
		android.view.ViewGroup.LayoutParams params = batteryLine.getLayoutParams();
		if(value < 20)
			batteryLine.setBackgroundColor(appClass.getResources().getColor(R.color.color_red));
		params.width = batteryLineBg.getLayoutParams().width*value/100;
		batteryLine.setLayoutParams(params);
		txtBattery.setText(batteryStr+"("+btn.getTag()+")");
		
//		builder.setPositiveButton(layout.getResources().getString(R.string.button_ok), new DialogInterface.OnClickListener() {
//
//			@Override
//			public void onClick(DialogInterface dialog, int which) {
//				dialog.dismiss();
//			}
//		});
		final AlertDialog alertDialog = builder.create();
		
		Button btnNo = (Button) layout.findViewById(R.id.btn_mem_alert_ok);
		btnNo.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				alertDialog.dismiss();
			}
		});
		return alertDialog;
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		Parameters params = appClass.mParameter.getParameters();
		switch(seekBar.getId()){
		case R.id.seekbar_expo:
			int expos = progress - params.getMaxExposureCompensation();
			params.setExposureCompensation(expos);
			break;
		case R.id.seekbar_zoom:
//			int zoom = progress - params.getMaxExposureCompensation();
			params.setZoom(progress);
			break;
		}
		
		appClass.mParameter.setParameters(params);
		ccCamUtil.refreshCameraInfo(appClass);
	}
	    
	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		switch(seekBar.getId()){
		case R.id.seekbar_expo:
//			Drawable thumb = appClass.getActivity().getResources().getDrawable( R.drawable.expo_seekbtn_on);
//			SeekBar mSeekBar = (SeekBar)appClass.getActivity().findViewById(R.id.seekbar_expo);
//			mSeekBar.setThumb(thumb);
			break;
		case R.id.seekbar_zoom:

			break;
		}
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		switch(seekBar.getId()){
		case R.id.seekbar_expo:
//			Drawable thumb = appClass.getActivity().getResources().getDrawable( R.drawable.expo_seekbtn);
//			SeekBar mSeekBar = (SeekBar)appClass.getActivity().findViewById(R.id.seekbar_expo);
//			mSeekBar.setThumb(thumb);
			break;
		case R.id.seekbar_zoom:

			break;
		}
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		
		if(group == flash_rg){
			switch(checkedId){
			case R.id.flash_radio0:
				//flash auto
				cameraFlashSet(Parameters.FLASH_MODE_AUTO);
				break;
			case R.id.flash_radio1:
				//flash on
				cameraFlashSet(Parameters.FLASH_MODE_ON);
				break;
			case R.id.flash_radio2:
				//flash off
				cameraFlashSet(Parameters.FLASH_MODE_OFF);
				break;
			case R.id.flash_radio3:
				//torch
				cameraFlashSet(Parameters.FLASH_MODE_RED_EYE);
				break;
			case R.id.flash_radio4:
				//torch
				cameraFlashSet(Parameters.FLASH_MODE_TORCH);
				break;
			}			
		}
		else
		if(group == focus_rg){
			switch(checkedId){
			case R.id.focus_radio0:
				cameraFocusSet(Parameters.FOCUS_MODE_AUTO);
				break;
			case R.id.focus_radio1:
				cameraFocusSet(Parameters.FOCUS_MODE_MACRO);
				break;
			case R.id.focus_radio2:
				cameraFocusSet(Parameters.FOCUS_MODE_INFINITY);
				break;
			case R.id.focus_radio3:
				cameraFocusSet(Parameters.FOCUS_MODE_FIXED);
				break;
			}
		}
		else
		if(group == timer_rg){
//			ImageView timerBtn = (ImageView)appClass.getActivity().findViewById(R.id.icon_camera_timer);
			ImageView timerBtn = (ImageView)appClass.getActivity().findViewById(R.id.btn_camera_timer);
			switch(checkedId){
			case R.id.timer_radio0:
				timerCount = 0;
				timerBtn.setImageResource(R.drawable.timer);
//				timerBtn.setVisibility(View.INVISIBLE);
				break;
			case R.id.timer_radio1:
				timerCount = 3;
				timerBtn.setImageResource(R.drawable.timer_3);
//				timerBtn.setVisibility(View.VISIBLE);
				break;
			case R.id.timer_radio2:
				timerCount = 5;
				timerBtn.setImageResource(R.drawable.timer_5);
//				timerBtn.setVisibility(View.VISIBLE);
				break;
			case R.id.timer_radio3:
				timerCount = 10;
				timerBtn.setImageResource(R.drawable.timer_10);
//				timerBtn.setVisibility(View.VISIBLE);
				break;
			case R.id.timer_radio4:
				timerCount = 30;
				timerBtn.setImageResource(R.drawable.timer_30);
//				timerBtn.setVisibility(View.VISIBLE);
				break;
			}			
		}
		else
		if(group == filter_rg){
			switch(checkedId){
			case R.id.filter_radio0:
				appClass.isCherryFilter = 0;				
				break;
			case R.id.filter_radio1:
				appClass.isCherryFilter = 1;
				break;
			case R.id.filter_radio2:
				appClass.isCherryFilter = 2;
				break;
			case R.id.filter_radio3:
				appClass.isCherryFilter = 3;
				break;
			}
			cameraFilter();
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if(isChecked){
			appClass.isTimerBeep = true;
		}else{
			appClass.isTimerBeep = false;
		}
	}
	
	class MyCountDownTimer {
	    private long countDownInterval;
	    private TextView TimetTextView;
	    private int Count;
	    
	    public MyCountDownTimer(long pCountDownInterval) {
//	            this.millisInFuture = pMillisInFuture;
            this.countDownInterval = pCountDownInterval;
        }	    
	    public void setTextView(TextView tv){
	    	TimetTextView = tv;
	    }
	    public void setCouncNum(int num){
	    	Count = num;
	    }
	    public void Stop(){
	    	timerCountMil = -1;
	    }
	    public void Start() 
	    {
	        final Handler handler = new Handler();
	        Log.v("status", "starting");
	        final Runnable counter = new Runnable(){

	            public void run(){	            	
	            	if(timerCountMil == -1){	            		
	            		TimetTextView.setVisibility(View.INVISIBLE);
	            	}else
	                if(timerCountMil <= 0) {
						TimetTextView.setVisibility(View.INVISIBLE);
						if(appClass.isTimerBeep)
							ccCamUtil.playSound(appClass.getApplicationContext(),IN.VOL_MED,2);
						
						appClass.mPreview.shootOn();
	                } else {
	                	if(appClass.isTimerBeep)
							ccCamUtil.playSound(appClass.getApplicationContext(),IN.VOL_MED,0);
						if(timerCountMil > 0){
												
							TimetTextView.setText(Integer.toString(timerCountMil/1000));
							TimetTextView.setVisibility(View.VISIBLE);
						}
						timerCountMil -= countDownInterval;
	                    handler.postDelayed(this, countDownInterval);
	                }
	            }
	        };

	        handler.postDelayed(counter, countDownInterval);
	    }
	}
}
