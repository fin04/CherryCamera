package com.epriest.cherryCamera.main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.PreviewCallback;
import android.media.ExifInterface;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.epriest.cherryCamera.ApplicationClass;
import com.epriest.cherryCamera.R;
import com.epriest.cherryCamera.util.IN;
import com.epriest.cherryCamera.util.ccCamUtil;
import com.epriest.cherryCamera.util.ccPicUtil;
import com.epriest.cherryCamera.util.logline;

/**
 * @author Camera set Preview on SurfaceView 
 *
 */
public class cCameraPreview extends SurfaceView implements SurfaceHolder.Callback{
	private String TAG = this.getClass().getSimpleName();
	
//	private final int MODE_FRONTCAMERA = 10;
	private SurfaceHolder mHolder = null;
    public Camera mCamera = null;
    public boolean isCameraPause = false;

	public int camera_id;
	public String sceneName;
	private boolean isButtonSetting;
	private boolean isTakingPic;	

	public boolean iShutter = false;
	boolean isSetFocus;
	public ProgressDialog pd;
	public Bitmap previewBitmap;

	ApplicationClass appClass;
	
	public int previewW, previewH;

	
	public cCameraPreview(Context context, String sceneName, ApplicationClass appClass) {
		super(context);
		logline.d(TAG,"cCameraPreview()");
		this.appClass = appClass;		
		this.sceneName = sceneName;
		
		pd = new ProgressDialog(appClass.getActivity());		
//		LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE); 
//		cameraMainView = inflater.inflate(R.layout.main_camera, null);
		mHolder = getHolder();
		mHolder.addCallback(this);		
		mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}
	
	public void setPreviewW(int previewW){
		this.previewW = previewW;
	}
	
	public int getPreviewW(){
		return previewW;
	}
	
	public void setPreviewH(int previewH){
		this.previewH = previewH;
	}
	
	public int getPreviewH(){
		return previewH;
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		logline.d(TAG,"Camera Surface Created");
		connectCamera();
//		mCamera = app.getCameraInstance();
		/*try {
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
        } catch (Exception e){
        	logline.d("Error setPreviewDisplay(holder): " + e.getMessage());
        }*/
	}
	
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.
		logline.d(TAG,"Camera Surface Changed");
		
        if (mHolder.getSurface() == null){
          // preview surface does not exist
          return;
        }
        
        // stop preview before making changes
        try {
//            mCamera.stopPreview();
        } catch (Exception e){
          // ignore: tried to stop a non-existent preview
        	Log.d(TAG, "Error stop camera preview: " + e.getMessage());
        }

        // set preview size and make any resize, rotate or
        // reformatting changes here 
//        ccCamUtil.setCameraDisplayOrientation(camera_id, mCamera,appClass);
        setParam();
	}
	
	public void setParam(){
		Parameters params = null;
        try{
        	params = mCamera.getParameters();
        }catch(Exception e){
        	e.printStackTrace();        	
        }
        if(params == null){
        	AlertDialog.Builder alert = new AlertDialog.Builder(appClass.getBaseContext());
			alert.setTitle(getResources().getString(R.string.alerttitle));
			alert.setMessage(getResources().getString(R.string.cannot_camera));
			alert.setPositiveButton(getResources().getString(R.string.button_ok), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					releaseCamera();
					appClass.getActivity().finish();
				}			   
			});			
			alert.show();
//        	Toast toast = Toast.makeText(mContext, mContext.getText(R.string.cannot_camera), Toast.LENGTH_SHORT);
//			toast.setGravity(Gravity.CENTER, 0, 0);
//			toast.show();
        	
        	return;
        }
        

        appClass.mParameter.previewSize(params,appClass.lcdWidth,appClass.lcdHeight);
        appClass.mParameter.whiteBalance(params);
        appClass.mParameter.supportedAntibanding(params);
        appClass.mParameter.colorEffect(params);
        appClass.mParameter.scene(params, sceneName);
        appClass.mParameter.supportedFocusModes(params);
        appClass.mParameter.supportedFlashModes(params);
        appClass. mParameter.pictureSize(params);
        appClass.expoMaxSize = params.getMaxExposureCompensation();
//        params.setRotation(0);
        mCamera.setParameters(params);
//        params = mCamera.getParameters();
		/*logline.d("==========\n " +
				"preview : "+params.getPreviewSize().width+","+params.getPreviewSize().height
				+"\n picture : "+params.getPictureSize().width+","+params.getPictureSize().height
				+"\n scene : "+params.getSceneMode()
				+"\n focus : "+params.getFocusMode()
				+"\n flash : "+params.getFlashMode()
				+"\n effect : "+params.getColorEffect()
				+"\n antibanding : "+params.getAntibanding()
				+"\n max exposure : "+params.getMaxExposureCompensation()
				+"\n max zoom : "+params.getMaxZoom()
				+"\n framerate : "+params.getPreviewFrameRate());*/

        // start preview with new settings
        try {
//            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();
        } catch (Exception e){
        	Log.d(TAG,"Error starting camera preview: " + e.getMessage());
        }
        ccCamUtil.refreshCameraInfo(appClass);
        // Camera Button Setting      
        if(!isButtonSetting){        	
        	appClass.mMenuset.optionSet();
        	appClass.mMenuset.btnSet();
        	isButtonSetting = true;
        }
        ccCamUtil.refreshMemory(appClass);
	}
	
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// empty. Take care of releasing the Camera preview in your activity.
		Log.d(TAG,"Camera Surface Destroyed");
		releaseCamera();
	}
	
	public void connectCamera(){
		logline.d(TAG, "*connectCamera");
		if(mCamera == null){
			mCamera = ccCamUtil.getCameraInstance(appClass.cameraFacingMode, appClass.mMenuset.frontCameraAccess);	
		}
		try {
			mCamera.setPreviewDisplay(mHolder);
//            mCamera.startPreview();
        } catch (Exception e){
        	Log.d(TAG,"Error starting camera preview: " + e.getMessage());
        }
	}
		
	public void releaseCamera(){
		logline.d(TAG, "*releaseCamera");
		try {
			mCamera.stopPreview();
		} catch (Exception e){
		    // ignore: tried to stop a non-existent preview
		  	Log.d(TAG, "Error stop camera preview: " + e.getMessage());
		}
        if (mCamera != null){
            mCamera.release();        // release the camera for other applications
            mCamera = null;
        }
    }
	
	AutoFocusCallback mAutoFocusCallback = new AutoFocusCallback(){		
		@Override
		public void onAutoFocus(boolean arg0, Camera arg1) {
			logline.d(TAG, "autofocus");
			final ImageView focusAim = (ImageView)appClass.getActivity().findViewById(R.id.camera_preview_focus);
			focusAim.setImageResource(R.drawable.aim3);
			capture();			
		}
		
	};
	
	AutoFocusCallback mFocusCallback = new AutoFocusCallback(){		
		@Override
		public void onAutoFocus(boolean arg0, Camera arg1) {
			isSetFocus = true;
			final Button captureButton = (Button)appClass.getActivity().findViewById(R.id.btn_camera_shutter);
			final ImageView focusAim = (ImageView)appClass.getActivity().findViewById(R.id.camera_preview_focus);
			ImageView ivCameraShutter = (ImageView)appClass.getActivity().findViewById(R.id.img_camera_shutter_icon);
			ivCameraShutter.setImageResource(R.drawable.shutter_);
			captureButton.setBackgroundResource(R.drawable.shutter_focus);
			focusAim.setImageResource(R.drawable.aim3);			
		}
	};
	
	private void pDialogSet(String message, boolean isCancel){
		if(pd.isShowing())
			pd.dismiss();
		pd = new ProgressDialog(appClass.getActivity());
		pd.setMessage(message);
		pd.setCancelable(false);
		if(isCancel){
			pd.setButton(DialogInterface.BUTTON_NEGATIVE, 
					appClass.getResources().getString(R.string.button_cancel), 
					new DialogInterface.OnClickListener() {
			    @Override
			    public void onClick(DialogInterface dialog, int which) {
			        dialog.dismiss();
			        mHandler.sendEmptyMessage(10);
			    }
			});
		}		
		pd.show();
	}
	
	public void shootOn(){		
		pDialogSet(appClass.getResources().getString(R.string.upload_message), true);
		appClass.mPreview.iShutter = true;
		if(isSetFocus){
			capture();
			isSetFocus = false;
		}else{
			logline.d(TAG, "AutoFocus , "+mCamera.getParameters().getFocusMode());
			if(appClass.ListFocus == null)				
				capture();
			else if(mCamera.getParameters().getFocusMode().equals(Parameters.FOCUS_MODE_FIXED))
				capture();
			else{
				ImageView focusAim = (ImageView)appClass.getActivity().findViewById(R.id.camera_preview_focus);
				focusAim.setImageResource(R.drawable.aim2);
				mCamera.autoFocus(mAutoFocusCallback);
			}
		}
//		mPreview.iShutter = true;
	}

	public boolean capture(){
		if(mCamera != null && !isTakingPic){
			isTakingPic = true;
			if(appClass.isCherryFilter != 0){
//				mCamera.setPreviewCallback(mPreview);
				mCamera.setOneShotPreviewCallback(mPreview);
			}else{
				mCamera.takePicture(null, null, mPicture);
			}
			
			return true;
		}
		return false;
	}

	public PreviewCallback mPreview = new PreviewCallback(){
		@Override
		public void onPreviewFrame(final byte[] data, Camera camera) {
			final int pictureOrientation = appClass.orientation;; 
			pDialogSet(appClass.getResources().getString(R.string.upload_save_message), true);
			
//			new Thread(new Runnable(){
//				@Override
//				public void run() {
					camera.setPreviewCallback(null);
				    camera.stopPreview();
					int [] pickuppedColorData = new int[data.length];
					int width = camera.getParameters().getPreviewSize().width;
					int height = camera.getParameters().getPreviewSize().height;
					ccPicUtil.decodeYUV(data, pickuppedColorData,width, height);
				    
				    Bitmap pickuppedBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
				    pickuppedBitmap.setPixels(pickuppedColorData, 0, width, 0, 0, width, height);
				    
					 Matrix m = new Matrix();					
					 if(appClass.mMenuset.frontCameraAccess){						 
						 float[] mirrorY = { -1, 0, 0, 0, 1, 0, 0, 0, 1};
						 Matrix matrixMirrorY = new Matrix();
						 matrixMirrorY.setValues(mirrorY);   
						 m.postConcat(matrixMirrorY);
					 }
					 
					 int px = appClass.mPreview.previewW-appClass.lcdWidth;
					 if(px < 0)
						 px = 0;
					 int py = appClass.mPreview.previewH-appClass.lcdHeight;
					 if(py < 0)
						 py = 0;
					 Bitmap bitmap = Bitmap.createBitmap(pickuppedBitmap, 
							 px/2, py/2, appClass.mPreview.previewW-px, appClass.mPreview.previewH-py, m, true);
					 if(pickuppedBitmap != null){
							try{
								pickuppedBitmap.recycle();
							}catch(Exception e){
								logline.d(TAG,"bitmap Null Exception : "+e);
							}
						}
				    if(previewBitmap != null){
						try{
							previewBitmap.recycle();
							previewBitmap = null;
						}catch(Exception e){
							logline.d(TAG,"bitmap Null Exception : "+e);
						}
					}

				    ImageView iv = (ImageView)appClass.getActivity().findViewById(R.id.camera_filter_save);
				    iv.setVisibility(View.VISIBLE);
				    iv.setBackgroundDrawable(new BitmapDrawable(getResources(), bitmap));
				    iv.setDrawingCacheEnabled(true);
				    logline.d(TAG,""+iv.getWidth()+","+iv.getHeight()+"..."+bitmap.getWidth()+","+bitmap.getHeight());
				    previewBitmap = Bitmap.createBitmap(iv.getDrawingCache(true));
				    iv.setDrawingCacheEnabled(false);
				    iv.setBackgroundDrawable(null);
				    iv.setVisibility(View.INVISIBLE);
				    
				    float degrees = ccCamUtil.setOrientationToDegree(pictureOrientation);
				    
					 m.reset();
					 m.setRotate(degrees,
			                 (float) width/2, (float) height/2);
					
					 bitmap = Bitmap.createBitmap(previewBitmap, 
							 0, 0, previewBitmap.getWidth(), previewBitmap.getHeight(), m, true);
					 if(previewBitmap != null){
							try{
								previewBitmap.recycle();
								previewBitmap = null;
							}catch(Exception e){
								logline.d(TAG,"bitmap Null Exception : "+e);
							}
						}	 
				    File pictureFile = ccCamUtil.getOutputMediaFile(IN.MEDIA_TYPE_IMAGE);
				    if(pictureFile != null){
				    	try {
				    		FileOutputStream fos = null;
				    		fos = new FileOutputStream(pictureFile);
				    		bitmap.compress(CompressFormat.JPEG, 90, fos);
				    	} catch (FileNotFoundException e) { 
				    		e.printStackTrace();
			 			}finally{
			 				if (bitmap != null) {
			 					bitmap.recycle();	 	             
						    }
			 			}
				    }
				    
//					 if(pictureFile != null){
				        	addMediaScanner(pictureFile);
				        	
//				        }
//						mHandler.sendEmptyMessage(10);
					
//				}
//			}).start();
			
		}
		
	};
	
	public PictureCallback mPicture = new PictureCallback() {
	    @Override
	    public void onPictureTaken(final byte[] data, Camera camera) {
	    	final int pictureOrientation = appClass.orientation;
	    	pDialogSet(appClass.getResources().getString(R.string.upload_save_message), true);

	    	new Thread(new Runnable(){
				@Override
				public void run() {
					File pictureFile = ccCamUtil.getOutputMediaFile(IN.MEDIA_TYPE_IMAGE);
				        
					if (pictureFile == null){
			            logline.d(TAG, "Error creating media file, check storage permissions: ");    
			            return;
					}

					if(previewBitmap != null){
						previewBitmap.recycle();
						previewBitmap = null;
					}
			        pictureFile = setSaveRotateFile(data, pictureFile, pictureOrientation, appClass.mMenuset.frontCameraAccess);
			        addMediaScanner(pictureFile);			        
				}
	    	}).start();
	    }
	};
	//693319
	private void addMediaScanner(final File file){
		if(file == null){
			setPreviewPicture(null);
            mHandler.sendEmptyMessage(20);
            return;
		}
		MediaScannerConnection.scanFile(appClass,
	            new String[] { file.getAbsolutePath() }, null,
	            new MediaScannerConnection.OnScanCompletedListener() {
	        public void onScanCompleted(String path, Uri uri) {
	            logline.d("ExternalStorage", "Scanned " + path + ":");
	            logline.d("ExternalStorage", "-> uri=" + uri);
//	            setPreviewPicture(path);
//	            previewImageChange(path);
//	            mHandler.sendEmptyMessage(10);	            
	            Message msg = Message.obtain();
				msg.obj = path;
				mHandler.sendMessage(msg);
	        }
	   });
	}	
	
	private File setSaveFile(byte[] data, File file){
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(file);
			if (!file.exists()) {
				file.createNewFile();
			}
			fos.write(data);
			fos.flush();
			fos.close();
		}catch (IOException e){
			e.printStackTrace();
		}finally{
			try {
				if (fos != null)
					fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return file;
	}
	
	private File setSaveRotateFile(byte[] data, File file, int pictureOrientation, boolean cameraFacingMode) {
		File photoFile = setSaveFile(data, file);		
		ExifInterface oldExif = null;
		try {
			oldExif = new ExifInterface(photoFile.getPath());
			oldExif.setAttribute(ExifInterface.TAG_ORIENTATION, Integer.toString(pictureOrientation));
		} catch (IOException e) {
			e.printStackTrace();
		}

//		float degrees = ccCamUtil.setOrientationToDegree(pictureOrientation);
		
		Bitmap bitmap = null;
		try{
			BitmapFactory.Options opt = new BitmapFactory.Options();
			opt.inSampleSize = 0;
//			opt.inDither = true;
//			opt.inPreferredConfig = Bitmap.Config.RGB_565;
			bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
		}catch(Exception e){
//			Log.d(TAG,"previewBitmap decode Exception : "+e);
			return null;
		} 
		if(bitmap == null) return null;
		Matrix m = new Matrix();
//		m.setRotate(degrees,
//				(float) bitmap.getWidth()/2, (float) bitmap.getHeight()/2);
		if(cameraFacingMode){
			if(pictureOrientation == ExifInterface.ORIENTATION_ROTATE_90){
//				m.setRotate(270,
//						(float) bitmap.getWidth()/2, (float) bitmap.getHeight()/2);
			}
			float[] mirrorY = { -1, 0, 0, 0, 1, 0, 0, 0, 1};
			Matrix matrixMirrorY = new Matrix();
			matrixMirrorY.setValues(mirrorY);  
			m.postConcat(matrixMirrorY);
		}
         try {
             Bitmap b2 = Bitmap.createBitmap(
            		 bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
             FileOutputStream fos = null;
			try {
				fos = new FileOutputStream(file);
				b2.compress(CompressFormat.JPEG, 95, fos);
				fos.flush();
	            fos.close();
			} catch (IOException e) {
//				Log.d(TAG,"==IOException==");
				e.printStackTrace();
			}finally{
				if (b2 != null) {
	            	 b2.recycle();
	            	 b2 = null;
	             }
			}
         } catch (OutOfMemoryError ex) {
             // We have no memory to rotate. Return the original bitmap.
//        	 Log.d(TAG,"==OutOfMemoryError==");
        	 FileOutputStream fos = null;
        	 try {        		
        		 fos = new FileOutputStream(file);
        		 bitmap.compress(CompressFormat.JPEG, 90, fos);
        		 try {
					fos.flush();
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}                 
 			} catch (Exception e) { 				
 				e.printStackTrace();
 				return null;
 			}finally{
 				if (bitmap != null) {
 					bitmap.recycle();
 					bitmap = null;
 	             }
 			}
         } catch (Exception e){
        	 e.printStackTrace();				
        	 return null;
         }finally{
        	 if (bitmap != null) {
				bitmap.recycle();
				bitmap = null;
             }
         }
         //위치정보 저장
//         if(appClass.isGPS){
//        	 String latStr = getLatitude();
//        	 String lonStr = getLongitude();
//        	 ccCamUtil.setGPSExif(oldExif, latStr, lonStr);
//         }
         
         ccCamUtil.moveOldExifToFile(oldExif, file.getAbsolutePath());
		return file;
	}

	private void setPreviewPicture(String path){
		if (previewBitmap != null) {
			previewBitmap.recycle();
			previewBitmap = null;
         }
		try{
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inSampleSize = 8;
			previewBitmap = BitmapFactory.decodeFile(path, options);
		}catch(Exception e){
			logline.d(TAG, "previewBitmap decode Exception : "+e);
		}
	}
	
	public void previewImageChange(String gallerySelectPath){		
		logline.d(TAG,"previewImageChange pictureData =" + gallerySelectPath);
		ImageView previewImg = (ImageView)appClass.getActivity().findViewById(R.id.img_photo_gallery);
		
		if(gallerySelectPath != null){
			Bitmap bm = getDrawabletoPath(gallerySelectPath);	
			previewImg.setImageBitmap(bm);
		}

		if(previewBitmap != null){
			try{
				previewBitmap.recycle();
				previewBitmap = null;
			}catch(Exception e){
				logline.d(TAG,"previewBitmap Null Exception : "+e);
			}
		}
		
        if(pd.isShowing()){
    		pd.dismiss();
    	}
        
        isTakingPic = false;			
        ccCamUtil.refreshMemory(appClass);
        iShutter = false;
        final Button captureButton = (Button)appClass.getActivity().findViewById(R.id.btn_camera_shutter);
        captureButton.setBackgroundResource(R.drawable.shutter_normal);
        final ImageView focusAim = (ImageView)appClass.getActivity().findViewById(R.id.camera_preview_focus);
		focusAim.setImageResource(R.drawable.aim1);
		ImageView ivCameraShutter = (ImageView)appClass.getActivity().findViewById(R.id.img_camera_shutter_icon);
		ivCameraShutter.setImageResource(R.drawable.shutter);
		
		if(appClass.isOutOfMem){
			appClass.isOutOfMem = false;
			new AlertDialog.Builder(appClass)
			.setTitle(R.string.alerttitle)
			.setMessage(R.string.big_resolution)
			.setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {					
				}				
			})
			.setNeutralButton(R.string.change_resolution, new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					Parameters params = mCamera.getParameters();
					appClass.mMenuset.cameraResolutionList(params);
				}				
			})
			.show();			
		}
	}
		
	public Bitmap getDrawabletoPath(String fileName){
		int thumViewSize = 150;
		Bitmap bitmapPicture = null;
		Bitmap srcBitmap = null;
		
		ccPicUtil.BitmapInfo bitinfo = ccPicUtil.getBitmapSize(fileName);
		float sizeValue = bitinfo.width/bitinfo.height;
				
		try{
			int scale;		 
			if(sizeValue >= 1)
				 scale = bitinfo.width/thumViewSize;
			else
				scale = bitinfo.height/thumViewSize;

			BitmapFactory.Options options = new BitmapFactory.Options();
			 if(scale >= 8) {
				 options.inSampleSize = 8;
			 } else if(scale >= 4) {
				 options.inSampleSize = 4;			    
			 } else if(scale >= 2) {
				 options.inSampleSize = 2;		   
			 } else {
				 options.inSampleSize = 1;			    
			 }

			bitmapPicture = BitmapFactory.decodeFile(fileName, options);
			srcBitmap = Bitmap.createScaledBitmap(bitmapPicture, thumViewSize, thumViewSize, true);
			return srcBitmap;
		}catch(Exception e){
			e.getMessage();
			return null;
		}
	}
	
	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			
			switch(msg.what){
			default:
				logline.d("=====mHandler 5=====");
				try{			    		
		    		mCamera.startPreview();
		    	}catch(Exception e){        		
		    	}
				previewImageChange((String) msg.obj);
				break;
			case 10:
				logline.d("=====mHandler 10=====");
				if(pd.isShowing()){
		    		pd.dismiss();
		    	}
				appClass.getActivity().finish();
				break;
			case 20:
				logline.d("=====mHandler default=====");
				if(pd.isShowing()){
		    		pd.dismiss();
		    	}
				break;
			}
			super.handleMessage(msg);
		}		
	};
	
	
}
