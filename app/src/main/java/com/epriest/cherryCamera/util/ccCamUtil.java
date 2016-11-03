package com.epriest.cherryCamera.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.media.AudioManager;
import android.media.ExifInterface;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.provider.MediaStore;
import android.view.Surface;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.epriest.cherryCamera.ApplicationClass;
import com.epriest.cherryCamera.R;
import com.epriest.cherryCamera.gallery.ccPhotoInfo.PhotoExif;

/**
 * @author Camera Utility Method
 * (2015.7.4)
 */
public class ccCamUtil{

	public ccCamUtil(){

	}
	
	public static int getSdkVer(){
		return Build.VERSION.SDK_INT;
	}
	
//	public void createPhoto(Canvas c, byte[] mRawData, camView mCamView){
//		in [] pickuppedColorData = new int[mRawData.length];
//		decodeYUV(mRawData, pickuppedColorData,mCamView.lcdW, mCamView.lcdH);
//        setColorDatas(mCamView, pickuppedColorData, mCamView.lcdW, mCamView.lcdH);
//	}
	
	public static void googleplayUpdate(Activity mActivity) {
		String appPackageName = mActivity.getPackageName(); // getPackageName() from Context or Activity object
		try {
			mActivity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
		} catch (android.content.ActivityNotFoundException anfe) {
			mActivity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName)));
		}
	}
	
	public String getSDPath() {
		String SDPath;
		String ext = Environment.getExternalStorageState();
        if (ext.equals(Environment.MEDIA_MOUNTED)) {
        	SDPath = Environment.getExternalStorageDirectory().toString();
        } else {
        	SDPath = Environment.MEDIA_UNMOUNTED;
        }
		logline.d("SD path : "+SDPath);
		return SDPath;
	}
	
	public String getPath(Uri uri, ApplicationClass appClass) {
	    String[] projection = { MediaStore.Images.Media.DATA };
	    Cursor cursor = appClass.getActivity().managedQuery(uri, projection, null, null, null);
	    if(cursor!=null){
	        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
	        cursor.moveToFirst();
	        return cursor.getString(column_index);
	    }
	    else return null;
	}
	
	public Uri pathToContentUri(String targetDir){
		String ext = Environment.getExternalStorageState();
		Uri targetUri;
		if (ext.equals(Environment.MEDIA_MOUNTED)) {
        	targetUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        }else{
        	targetUri = MediaStore.Images.Media.INTERNAL_CONTENT_URI;
        }
		targetUri = targetUri.buildUpon().appendQueryParameter("bucketId", String.valueOf(targetDir.toLowerCase().hashCode())).build();
		return targetUri;
	}
	
//	public void previewCooking(String picName, camView mCamView){
//		if(mCamView.previewBitmap != null){
//			mCamView.previewBitmap.recycle();
//			mCamView.previewBitmap = null;
//			System.gc();
//		}
//		String path = getSDPath()+IN.cPath;
//		mCamView.previewBitmap = BitmapFactory.decodeFile(path+"/"+picName);
//		logline.d(path+"/"+picName);
//	}
	
	public void createpath(String dir) {
		File path = new File(dir);
		if(!path.isDirectory()){
			path.mkdirs();
		}
	}
	
	public void removepath(String dir){
		File path = new File(dir);
		if (path.isDirectory()) {
	        String[] children = path.list();
	        for (int i = 0; i < children.length; i++)
	            new File(path, children[i]).delete();
	    }
	}

	
//	void createExternalStoragePublicPicture() {
//	    // Create a path where we will place our picture in the user's
//	    // public pictures directory.  Note that you should be careful about
//	    // what you place here, since the user often manages these files.  For
//	    // pictures and other media owned by the application, consider
//	    // Context.getExternalMediaDir().
//	    File path = Environment.getExternalStoragePublicDirectory(
//	            Environment.DIRECTORY_PICTURES);
//	    File file = new File(path, "DemoPicture.jpg");
//
//	    try {
//	        // Make sure the Pictures directory exists.
//	        path.mkdirs();
//
//	        // Very simple code to copy a picture from the application's
//	        // resource into the external file.  Note that this code does
//	        // no error checking, and assumes the picture is small (does not
//	        // try to copy it in chunks).  Note that if external storage is
//	        // not currently mounted this will silently fail.
//	        InputStream is = getResources().openRawResource(R.drawable.balloons);
//	        OutputStream os = new FileOutputStream(file);
//	        byte[] data = new byte[is.available()];
//	        is.read(data);
//	        os.write(data);
//	        is.close();
//	        os.close();
//
//	        // Tell the media scanner about the new file so that it is
//	        // immediately available to the user.
//	        MediaScannerConnection.scanFile(this,
//	                new String[] { file.toString() }, null,
//	                new MediaScannerConnection.OnScanCompletedListener() {
//	            public void onScanCompleted(String path, Uri uri) {
//	                Log.i("ExternalStorage", "Scanned " + path + ":");
//	                Log.i("ExternalStorage", "-> uri=" + uri);
//	            }
//	        });
//	    } catch (IOException e) {
//	        // Unable to create file, likely because external storage is
//	        // not currently mounted.
//	        Log.w("ExternalStorage", "Error writing " + file, e);
//	    }
//	}
//
//	void deleteExternalStoragePublicPicture() {
//	    // Create a path where we will place our picture in the user's
//	    // public pictures directory and delete the file.  If external
//	    // storage is not currently mounted this will fail.
//	    File path = Environment.getExternalStoragePublicDirectory(
//	            Environment.DIRECTORY_PICTURES);
//	    File file = new File(path, "DemoPicture.jpg");
//	    file.delete();
//	}
//
//	boolean hasExternalStoragePublicPicture() {
//	    // Create a path where we will place our picture in the user's
//	    // public pictures directory and check if the file exists.  If
//	    // external storage is not currently mounted this will think the
//	    // picture doesn't exist.
//	    File path = Environment.getExternalStoragePublicDirectory(
//	            Environment.DIRECTORY_PICTURES);
//	    File file = new File(path, "DemoPicture.jpg");
//	    return file.exists();
//	}

	/** Create a File for saving an image or video */
	public static File getOutputMediaFile(int type){		
//	    File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
//	              Environment.DIRECTORY_DCIM), "cherrycamera");
	    File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), Environment.DIRECTORY_DCIM+IN.cPath);
	    // This location works best if you want the created images to be shared
	    // between applications and persist after your app has been uninstalled.

	    // Create the storage directory if it does not exist
	    if (! mediaStorageDir.exists()){
	        if (! mediaStorageDir.mkdirs()){
	            logline.d("MyCameraApp", "failed to create directory"+mediaStorageDir.getAbsolutePath());
	            return null;
	        }
	    }

	    // Create a media file name
	    String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
	    File mediaFile;
	    if (type == IN.MEDIA_TYPE_IMAGE){
	        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
	        "cherry-"+ timeStamp + ".jpg");
	    } else if(type == IN.MEDIA_TYPE_VIDEO) {
	        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
	        "cherry-"+ timeStamp + ".mp4");
	    } else {
	        return null;
	    }

	    return mediaFile;
	}
	
	/*public void addMediaScanner(final Context con, final File file){
		MediaScannerConnection.scanFile(con,
	            new String[] { file.toString() }, null,
	            new MediaScannerConnection.OnScanCompletedListener() {
	        public void onScanCompleted(String path, Uri uri) {
	            Log.i("ExternalStorage", "Scanned " + path + ":");
	            Log.i("ExternalStorage", "-> uri=" + uri);
	        }
	    });		
//		
//		new MediaScannerConnectionClient(){
//			private MediaScannerConnection msc = null;
//			{
//				msc = new MediaScannerConnection(con, this);
//				msc.connect();
//			}
//			public void onMediaScannerConnected(){
//				msc.scanFile(file.getAbsolutePath(), null);
//			}
//			public void onScanCompleted(String path, Uri uri){
//				mUri = uri;
////				contentListCooking();
//				logline.d("-addMediaScanner--path = "+path+", uri = "+uri);
//				if(path.equals(file.getAbsolutePath()))
//					msc.disconnect();	
//			}
//		};
	}*/
	
	public boolean nullFile(String path) {
		logline.d(" === "+path);
		File file = new File(path);
		long file_size = file.length();
		if(file_size == 0)
			return true;
//		try{
//			
//		}catch(Exception e){
//			
//		}
		return false;
	}
	
	public void setColorDatas(int[] pickuppedColorData ,int width,int height){
		Bitmap pickuppedBitmap = null;
	    pickuppedBitmap = Bitmap.createBitmap(width,height,Bitmap.Config.ARGB_8888);
	    pickuppedBitmap.setPixels(pickuppedColorData, 0, width, 0, 0, width, height);	     
//	    Bitmap bitmap = null;
//	    bitmap = Bitmap.createBitmap(lcdW,lcdH,Bitmap.Config.ARGB_8888);
//	    Canvas c1 = new Canvas(bitmap);
//	    c1.drawBitmap(pickuppedBitmap, 0, 0, paint);
//	    drawCherry(c1);

	    SimpleDateFormat formatter = new SimpleDateFormat ("yyyyMMddHHmmss");
	    
		Date currentTime = new Date ();
		String dTime = formatter.format (currentTime);
		String fileName = "cherry-"+dTime;
		logline.d("picture Taken "+fileName);
		
	    FileOutputStream outStream = null;
			
	    
	    File file = null;//	      
//	    OutputStream outStream1 = null;
	    
	    
//	     
//	    try {
//			outStream1 = new FileOutputStream(file);
//		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//	     
//	    bgImage.compress(Bitmap.CompressFormat.JPEG, 50, outStream);
//	     
//	    outStream.flush();
//	    outStream.close();

	    Bitmap Bit_Picture = Bitmap.createBitmap(width,height,Bitmap.Config.ARGB_8888);
		try{
			file = new File(IN.cPath+fileName+".jpg");			
			outStream = new FileOutputStream(String.format(file.toString()));
			if (pickuppedBitmap.compress(CompressFormat.JPEG, 100, outStream) ){
			}
			if(Bit_Picture != null){
				Bit_Picture.recycle();				
			}
			Bit_Picture = Bitmap.createScaledBitmap(pickuppedBitmap, pickuppedBitmap.getWidth()/5, pickuppedBitmap.getHeight()/5, true);
			//Bit_Picture.createBitmap(bitmap);
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				outStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}finally{
				//Toast.makeText(context, "사진이 저장되었습니다.", Toast.LENGTH_LONG).show();
			}
		}		
	}
	
	public static long getTotalMemory() {
	    String str1 = "/proc/meminfo";
	    String str2;        
	    String[] arrayOfString;
	    long initial_memory = 0;
	    try {	    
	    	FileReader localFileReader = new FileReader(str1);	    
	    	BufferedReader localBufferedReader = new BufferedReader(    localFileReader, 8192);
	    	str2 = localBufferedReader.readLine();//meminfo	    
	    	arrayOfString = str2.split("\\s+");	    
//	    	for (String num : arrayOfString) {
	    		//	    Log.i(str2, num + "\t");	    
//	    	}	    
	    	//total Memory	   
	    	initial_memory = Integer.valueOf(arrayOfString[1]).intValue(); 	    
	    	localBufferedReader.close();	    	
	    	return initial_memory;	   
	    } catch (IOException e) {	        
	    	return -1;
	    }	  
	}	
	
	public static void setCameraDisplayOrientation(int cameraId, Camera camera, ApplicationClass appClass) {
		if(getSdkVer() == 8){
			setCameraDisplayOrientationAPI8(camera, appClass);
			return;
		}			
	    Camera.CameraInfo info =
	             new Camera.CameraInfo();
	    Camera.getCameraInfo(cameraId, info);
	     int rotation = appClass.getActivity().getWindowManager().getDefaultDisplay()
	             .getRotation();
	     int degrees = 0;
	     switch (rotation) {
	         case Surface.ROTATION_0: degrees = 0; break;
	         case Surface.ROTATION_90: degrees = 90; break;
	         case Surface.ROTATION_180: degrees = 180; break;
	         case Surface.ROTATION_270: degrees = 270; break;
	     }

	     int result;
	     if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
	         result = (info.orientation + degrees) % 360;
	         result = (360 - result) % 360;  // compensate the mirror
	     } else {  // back-facing
	         result = (info.orientation - degrees + 360) % 360;
	     }
	     logline.d("orientation result : "+result);
	     camera.setDisplayOrientation(result);
//	     Parameters param = camera.getParameters();
//	     param.setRotation(result);
//	     camera.setParameters(param);
	 }
	
	private static void setCameraDisplayOrientationAPI8(Camera camera, ApplicationClass appClass){
        //Sets the camera right Orientation.
        //Special void for API 8 build.
        //This void should be called before calling camera.setParameters(cameraParameters).
        if (appClass.getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
        	logline.d("orientation = ORIENTATION_PORTRAIT");
            camera.setDisplayOrientation(90);
        }
        if (appClass.getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
        	logline.d("orientation = ORIENTATION_LANDSCAPE");
            camera.setDisplayOrientation(0);
        }
    }
	
	static public String changeDateTimeFormat(String inputDate, String inputFormat, String outputFormat){		
		Date parsed = null;
	    String outputDate = "";

	    SimpleDateFormat df_input = new SimpleDateFormat(inputFormat, java.util.Locale.getDefault());
	    SimpleDateFormat df_output = new SimpleDateFormat(outputFormat, java.util.Locale.getDefault());
	    try {
	        parsed = df_input.parse(inputDate);
	        outputDate = df_output.format(parsed);
	    } catch (ParseException e) { 
	    }
	    return outputDate;
	}
	
	static public void setFilenameToExifDate(String name, String path){
		ExifInterface exif = null;
		try {
			exif = new ExifInterface(path);
		} catch (IOException e) {
			e.printStackTrace();
		}
		String exifDate = exif.getAttribute(ExifInterface.TAG_DATETIME);
//		logline.d("---="+exifDate);
		if(exifDate != null)
			return;
		String dateStr = name.replace("cherry-", "");
		dateStr = dateStr.replace(".jpg", "");
		dateStr = changeDateTimeFormat(dateStr, "yyyyMMddHHmmss", "yyyy:MM:dd HH:mm:ss");
//		logline.d(dateStr);
		setDateExif(path, dateStr);
	}
	
	/*public void onOrientationChanged(int orientation) {
		if (orientation == ORIENTATION_UNKNOWN) return;
		   android.hardware.Camera.CameraInfo info =
		        new android.hardware.Camera.CameraInfo();
		   android.hardware.Camera.getCameraInfo(cameraId, info);
		   orientation = (orientation + 45) / 90 * 90;
		   int rotation = 0;
		   if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
		     rotation = (info.orientation - orientation + 360) % 360;
		   } else {  // back-facing camera
		     rotation = (info.orientation + orientation) % 360;
		   }
		   mParameters.setRotation(rotation);
	}*/
	
	public static int setDegreeToExifOrientation(int pitchAngle){
		
        if(pitchAngle < 45 || pitchAngle >= 315){
        	return ExifInterface.ORIENTATION_ROTATE_90;
        }
        else if(pitchAngle >= 45 && pitchAngle < 135){
        	return ExifInterface.ORIENTATION_ROTATE_180;
        }
        else if(pitchAngle >= 135 && pitchAngle < 225){
        	return ExifInterface.ORIENTATION_ROTATE_270;
        }
        else if(pitchAngle >= 225 && pitchAngle < 315){
        	return ExifInterface.ORIENTATION_NORMAL;
        }
		return ExifInterface.ORIENTATION_NORMAL;
	}
	
	/**
	 * 이미지 EXIF에 orientation 입력 
	 * @param path : image path
	 * @param ori : image orientation (0,90,180,270)
	 * @return
	 */
	public static boolean setOriExif(String path, int ori){
		try{
			ExifInterface exif = new ExifInterface(path);
			int oriExif = ExifInterface.ORIENTATION_NORMAL;
			switch(ori){
			case 0:
				break;
			case 90:
				oriExif = ExifInterface.ORIENTATION_ROTATE_90;
				break;
			case 180:
				oriExif = ExifInterface.ORIENTATION_ROTATE_180;
				break;
			case 270:
				oriExif = ExifInterface.ORIENTATION_ROTATE_270;
				break;
			}
			exif.setAttribute(ExifInterface.TAG_ORIENTATION, ""+oriExif);
			exif.saveAttributes();
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public static void setDateExif(String path, String date){		
		try{
			ExifInterface exif = new ExifInterface(path);			
			if(date != null)
				exif.setAttribute(ExifInterface.TAG_DATETIME, date);
			exif.saveAttributes();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	/**
	 * 이미지에 EXIF 세팅
	 * @param path - image path
	 * @param flash - flash on/off
	 * @param model - 모델
	 * @param make - 제조업체
	 * @param focalLength - 초점거리
	 * @param wb - 화이트밸런스
	 * @param fNum - 조리개
	 * @param expoTime - 노출시간
	 * @param iso
	 */
	public static void setExif(String path, String flash, String model, String make, 
			String focalLength, String wb, String fNum, String expoTime, String iso,
			String date){		
		try{
			ExifInterface exif = new ExifInterface(path);
			if(flash != null)
				exif.setAttribute(ExifInterface.TAG_FLASH, flash);
			if(model != null)
				exif.setAttribute(ExifInterface.TAG_MODEL, model);
			if(make != null)
				exif.setAttribute(ExifInterface.TAG_MAKE, make);
			if(focalLength != null)
				exif.setAttribute(ExifInterface.TAG_FOCAL_LENGTH, focalLength);
			if(wb != null)
				exif.setAttribute(ExifInterface.TAG_WHITE_BALANCE, wb);
			if(Build.VERSION.SDK_INT < 11){
				exif.saveAttributes();
				return;
			}
			if(fNum != null)
				exif.setAttribute(ExifInterface.TAG_APERTURE, fNum);
			if(expoTime != null)
				exif.setAttribute(ExifInterface.TAG_EXPOSURE_TIME, expoTime);
			if(iso != null)
				exif.setAttribute(ExifInterface.TAG_ISO, iso);
			if(date != null)
				exif.setAttribute(ExifInterface.TAG_DATETIME, date);
			exif.saveAttributes();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void moveOldExifToFile(ExifInterface oldexif, String newPath){		
        ExifInterface newexif;
        int build = Build.VERSION.SDK_INT;
		try {	
			 newexif = new ExifInterface(newPath);
			// From API 11
	        if (build >= 11) {
	            if (oldexif.getAttribute("FNumber") != null) {
	                newexif.setAttribute("FNumber",
	                        oldexif.getAttribute("FNumber"));
	            }
	            if (oldexif.getAttribute("ExposureTime") != null) {
	                newexif.setAttribute("ExposureTime",
	                        oldexif.getAttribute("ExposureTime"));
	            }
	            if (oldexif.getAttribute("ISOSpeedRatings") != null) {
	                newexif.setAttribute("ISOSpeedRatings",
	                        oldexif.getAttribute("ISOSpeedRatings"));
	            }
	        }
	        // From API 9
	        if (build >= 9) {
	            if (oldexif.getAttribute("GPSAltitude") != null) {
	                newexif.setAttribute("GPSAltitude",
	                        oldexif.getAttribute("GPSAltitude"));
	            }
	            if (oldexif.getAttribute("GPSAltitudeRef") != null) {
	                newexif.setAttribute("GPSAltitudeRef",
	                        oldexif.getAttribute("GPSAltitudeRef"));
	            }
	        }
	        // From API 8
	        if (build >= 8) {
	            if (oldexif.getAttribute("FocalLength") != null) {
	                newexif.setAttribute("FocalLength",
	                        oldexif.getAttribute("FocalLength"));
	            }
	            if (oldexif.getAttribute("GPSDateStamp") != null) {
	                newexif.setAttribute("GPSDateStamp",
	                        oldexif.getAttribute("GPSDateStamp"));
	            }
	            if (oldexif.getAttribute("GPSProcessingMethod") != null) {
	                newexif.setAttribute(
	                        "GPSProcessingMethod",
	                        oldexif.getAttribute("GPSProcessingMethod"));
	            }
	            if (oldexif.getAttribute("GPSTimeStamp") != null) {
	                newexif.setAttribute("GPSTimeStamp", ""
	                        + oldexif.getAttribute("GPSTimeStamp"));
	            }
	        }
	        if (oldexif.getAttribute("DateTime") != null) {
	            newexif.setAttribute("DateTime",
	                    oldexif.getAttribute("DateTime"));
	        }
	        if (oldexif.getAttribute("Flash") != null) {
	            newexif.setAttribute("Flash",
	                    oldexif.getAttribute("Flash"));
	        }
	        if (oldexif.getAttribute("GPSLatitude") != null) {
	            newexif.setAttribute("GPSLatitude",
	                    oldexif.getAttribute("GPSLatitude"));
	        }
	        if (oldexif.getAttribute("GPSLatitudeRef") != null) {
	            newexif.setAttribute("GPSLatitudeRef",
	                    oldexif.getAttribute("GPSLatitudeRef"));
	        }
	        if (oldexif.getAttribute("GPSLongitude") != null) {
	            newexif.setAttribute("GPSLongitude",
	                    oldexif.getAttribute("GPSLongitude"));
	        }
	        if (oldexif.getAttribute("GPSLatitudeRef") != null) {
	            newexif.setAttribute("GPSLongitudeRef",
	                    oldexif.getAttribute("GPSLongitudeRef"));
	        }
	        //Need to update it, with your new height width
	        newexif.setAttribute("ImageLength",
	                "200");
	        newexif.setAttribute("ImageWidth",
	                "200");

	        if (oldexif.getAttribute("Make") != null) {
	            newexif.setAttribute("Make",
	                    oldexif.getAttribute("Make"));
	        }
	        if (oldexif.getAttribute("Model") != null) {
	            newexif.setAttribute("Model",
	                    oldexif.getAttribute("Model"));
	        }
	        if (oldexif.getAttribute("Orientation") != null) {
	            newexif.setAttribute("Orientation",
	                    oldexif.getAttribute("Orientation"));
	        }
	        if (oldexif.getAttribute("WhiteBalance") != null) {
	            newexif.setAttribute("WhiteBalance",
	                    oldexif.getAttribute("WhiteBalance"));
	        }
	        newexif.saveAttributes();
		} catch (IOException e) {
			e.printStackTrace();
		}
        
	}
	
	public static boolean setGPSExif(String path, String latStr, String lonStr){
		logline.d("setExif latStr = "+latStr);
		if(latStr == null || lonStr == null)
			return false;
		logline.d("setExif path = "+path);
		double latitude = Double.parseDouble(latStr);
		double longitude = Double.parseDouble(lonStr);
		try{
			ExifInterface exif = new ExifInterface(path);
			logline.d("exif = "+exif.getAttribute(ExifInterface.TAG_DATETIME));
			int num1Lat = (int)Math.floor(latitude);
		    int num2Lat = (int)Math.floor((latitude - num1Lat) * 60);
		    double num3Lat = (latitude - ((double)num1Lat+((double)num2Lat/60))) * 3600000;

		    int num1Lon = (int)Math.floor(longitude);
		    int num2Lon = (int)Math.floor((longitude - num1Lon) * 60);
		    double num3Lon = (longitude - ((double)num1Lon+((double)num2Lon/60))) * 3600000;

		    exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE, num1Lat+"/1,"+num2Lat+"/1,"+num3Lat+"/1000");
		    exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, num1Lon+"/1,"+num2Lon+"/1,"+num3Lon+"/1000");
//		    long now = System.currentTimeMillis();
//			Date date = new Date(now);
//			SimpleDateFormat sdf = new SimpleDateFormat("yyyy:MM:dd HH:mm"); 
//			String str = sdf.format(date);
//		    exif.setAttribute(ExifInterface.TAG_DATETIME, str);

		    if (latitude > 0) {
		        exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF, "N"); 
		    } else {
		        exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF, "S");
		    }

		    if (longitude > 0) {
		        exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF, "E");    
		    } else {
		    exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF, "W");
		    }

		    exif.saveAttributes();
//			Log.d("setExif",""+exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE)
//					+","+exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE));
		}catch(IOException e){
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public static boolean setGPSExif(ExifInterface exif, String latStr, String lonStr) {
		if(latStr == null || lonStr == null)
			return false;
		double latitude = Double.parseDouble(latStr);
		double longitude = Double.parseDouble(lonStr);
		try{
			int num1Lat = (int)Math.floor(latitude);
		    int num2Lat = (int)Math.floor((latitude - num1Lat) * 60);
		    double num3Lat = (latitude - ((double)num1Lat+((double)num2Lat/60))) * 3600000;

		    int num1Lon = (int)Math.floor(longitude);
		    int num2Lon = (int)Math.floor((longitude - num1Lon) * 60);
		    double num3Lon = (longitude - ((double)num1Lon+((double)num2Lon/60))) * 3600000;

		    exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE, num1Lat+"/1,"+num2Lat+"/1,"+num3Lat+"/1000");
		    exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, num1Lon+"/1,"+num2Lon+"/1,"+num3Lon+"/1000");
//		    long now = System.currentTimeMillis();
//			Date date = new Date(now);
//			SimpleDateFormat sdf = new SimpleDateFormat("yyyy:MM:dd HH:mm"); 
//			String str = sdf.format(date);
//		    exif.setAttribute(ExifInterface.TAG_DATETIME, str);

		    if (latitude > 0) {
		        exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF, "N"); 
		    } else {
		        exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF, "S");
		    }

		    if (longitude > 0) {
		        exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF, "E");    
		    } else {
		    exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF, "W");
		    }

		    exif.saveAttributes();
//			Log.d("setExif",""+exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE)
//					+","+exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE));
		}catch(IOException e){
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	static public PhotoExif getExif(String path){
		PhotoExif info = new PhotoExif();
		ExifInterface exif = null;
		try {
			exif = new ExifInterface(path);
		} catch (IOException e) {
			e.printStackTrace();
		}
		info.DateTime = exif.getAttribute(ExifInterface.TAG_DATETIME);
		info.Flash = exif.getAttribute(ExifInterface.TAG_FLASH);
		info.FocalLength = exif.getAttribute(ExifInterface.TAG_FOCAL_LENGTH);
		info.ISO = exif.getAttribute(ExifInterface.TAG_ISO);
		info.WhiteBalence = exif.getAttribute(ExifInterface.TAG_WHITE_BALANCE);
		info.Model = exif.getAttribute(ExifInterface.TAG_MODEL);
		info.Orientation = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
		info.APERTURE = exif.getAttribute(ExifInterface.TAG_APERTURE);
		info.EXPOTIME = exif.getAttribute(ExifInterface.TAG_EXPOSURE_TIME);
		info.MAKE = exif.getAttribute(ExifInterface.TAG_MAKE);
		return info;
	}
	
	static public int setOrientationToDegree(int pictureOrientation){
		switch(pictureOrientation){
		case ExifInterface.ORIENTATION_NORMAL:
			return 0;
		case ExifInterface.ORIENTATION_ROTATE_90:
			return 90;
		case ExifInterface.ORIENTATION_ROTATE_180:
			return 180;
		case ExifInterface.ORIENTATION_ROTATE_270:
			return 270;		
		}
		return 0;
	}
	static public int getPathOrientationToDegree(String path){
		int degree;
		try {
			ExifInterface exif = new ExifInterface(path);
			exif.getAttribute(ExifInterface.TAG_ORIENTATION);
			degree = setOrientationToDegree(Integer.parseInt(exif.getAttribute(ExifInterface.TAG_ORIENTATION)));
		} catch (IOException e) {
			e.printStackTrace();
			return 0;
		}
		return degree;
	}
	
//	public void onOrientationChanged(int orientation) {
//	     if (orientation == ORIENTATION_UNKNOWN) return;
//	     android.hardware.Camera.CameraInfo info =
//	            new android.hardware.Camera.CameraInfo();
//	     android.hardware.Camera.getCameraInfo(cameraId, info);
//	     orientation = (orientation + 45) / 90 * 90;
//	     int rotation = 0;
//	     if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
//	         rotation = (info.orientation - orientation + 360) % 360;
//	     } else {  // back-facing camera
//	         rotation = (info.orientation + orientation) % 360;
//	     }
//	     mParameters.setRotation(rotation);
//	 }


	// save camera status 
//	public void statusSave(Context con, int status, int value, String str) {
//		SharedPreferences pref = con.getSharedPreferences("saveStatus", Activity.MODE_PRIVATE);
//		SharedPreferences.Editor edit = pref.edit();
//		
//		switch(status){
//		case IN.savePictureSize:
//			edit.putInt("pictureSize",value);
//			break;
//		case IN.saveFocusMode:
//			edit.putString("focusMode", str);
//			break;
//		case IN.saveFlashMode:
//			edit.putString("flashMode", str);
//		}
//		edit.commit();		
//	}
	
	public static void statusSave(Context con, int status, int value, String str) {
		SharedPreferences pref = con.getSharedPreferences("saveStatus", Activity.MODE_PRIVATE);
		SharedPreferences.Editor edit = pref.edit();
		
		switch(status){
		case IN.saveBackPictureSize:
			edit.putInt("pictureSize_b",value);
			break;
		case IN.saveFrontPictureSize:
			edit.putInt("pictureSize_f",value);
		case IN.saveFocusMode:
			edit.putString("focusMode", str);
			break;
		case IN.saveFlashMode:
			edit.putString("flashMode", str);
		}
		edit.commit();
	}
	
	// load camera Picture size  
	public static int loadStatusInt(int defTag, int status, ApplicationClass appClass) {
		SharedPreferences pref = appClass.getActivity().getSharedPreferences("saveStatus", Activity.MODE_PRIVATE);
		
		switch(status){
		case IN.saveBackPictureSize:
			return pref.getInt("pictureSize_b",defTag);
		case IN.saveFrontPictureSize:
			return pref.getInt("pictureSize_f",defTag);
		}
		return 0;
	}

	public static String loadStatus(int status, ApplicationClass appClass) {
		SharedPreferences pref = appClass.getActivity().getSharedPreferences("saveStatus", Activity.MODE_PRIVATE);
		
		switch(status){
		case IN.saveFocusMode:
			return pref.getString("focusMode", Parameters.FOCUS_MODE_AUTO);
		case IN.saveFlashMode:
			return pref.getString("flashMode", Parameters.FLASH_MODE_AUTO);
		}		
		return "0";
	}
	
	
	public String deviceModel(){
		return android.os.Build.MODEL;
	}
	
	/*public void menuBgimageLoad() {
		ccGalleryUtil cgUtil;
		cgUtil = new ccGalleryUtil(appClass.getActivity());
		cgUtil.lcdWidth = lcdW;
		cgUtil.lcdHeight = lcdH;
		//마지막 이미지 불러오기
		cgUtil.imageFileLoad();		
		
		if(cgUtil.allPhotoNum != 0){
//			if(cgUtil.previewBitmap != null){
//				try{
//					cgUtil.previewBitmap.recycle();
//					cgUtil.previewBitmap = null;
//				}catch(Exception e){
//					logline.d("bitmap Null Exception : "+e);
//				}
//			}
			Bitmap bitmap = ccPicUtil.previewCooking(
					getSDPath()+IN.cPath+"/"+cgUtil.PhotoName.get(cgUtil.PhotoName.size()-1)
					,1
					,ccPicUtil.getScreenSize(appClass.getActivity()).widthPixels
        			,ccPicUtil.getScreenSize(appClass.getActivity()).heightPixels);
			
//			Bitmap galleryBitmap;
//	        Matrix matrix = new Matrix(); 
//	        if(cgUtil.picWidth > cgUtil.lcdWidth){
//	        	try{
//		        	prevBitmap = Bitmap.createScaledBitmap(cgUtil.previewBitmap, (int)tempW, cgUtil.lcdHeight, true);
//		        }catch(Exception e){
//		        	logline.d("previewBitmap Scaled Exception : "+e);
//		        	return false;
//		        }
//		        matrix.setRotate(90, (float) cgUtil.previewBitmap.getWidth(), (float) cgUtil.previewBitmap.getHeight());
//	        }else{
//	        	prevBitmap = galleryUtil.previewBitmap;
////	        	matrix.setRotate(90, (float) galleryUtil.picWidth, (float) galleryUtil.picHeight);
//	        }
////
//        	try{
//	            galleryBitmap = Bitmap.createBitmap(cgUtil.previewBitmap, 0, 0, 
//	            		cgUtil.previewBitmap.getWidth(), cgUtil.previewBitmap.getHeight(), matrix, true);	            
//	        }catch(Exception e){
//	        	logline.d("previewBitmap Resize Exception : "+e);
//	        	return false;
//	        }
//	        if(cgUtil.previewBitmap != null){
//	        	cgUtil.previewBitmap.recycle();
//	        	cgUtil.previewBitmap = null;
//            }
              
			//==============
			// preview photo change
			//==============
	        BitmapDrawable d = new BitmapDrawable(appClass.getActivity().getResources(),bitmap);
			RelativeLayout ivGalleryImage = (RelativeLayout)appClass.getActivity().findViewById(R.id.menu_layout_bg);
			ivGalleryImage.setBackgroundDrawable(d);
			recycleBitmap(bitmap);
		}
	}*/
	
	public static boolean recycleBitmap(Bitmap bitmap){
		if(bitmap != null){
			try{
				bitmap.recycle();
				bitmap = null;
			}catch(Exception e){
				logline.d("previewBitmap Null Exception : "+e);
				return false;
			}
		}
		return true;
	}
	
	
	
	/*public void recycleBitmap(Bitmap bitmap){
		if(bitmap != null){
			try{
				bitmap.recycle();
				bitmap = null;
			}catch(Exception e){
				logline.d("bitmap Null Exception : "+e);
			}
		}
	}*/
	
	public static String refreshMemory(ApplicationClass appClass){
		logline.d("refreshMemory");
		StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
		float bytesAvailable  =(float) stat.getBlockCount()* (float)stat.getBlockSize()/(1073741824);
		bytesAvailable = Math.round(bytesAvailable*100);
		bytesAvailable = bytesAvailable/100;
		float bytesFree  =(float) stat.getFreeBlocks()* (float)stat.getBlockSize()/(1073741824);
		bytesFree = Math.round(bytesFree*100);
		bytesFree = bytesFree/100;
		Button memButton = (Button)appClass.getActivity().findViewById(R.id.btn_camera_freememory);
		memButton.setText(""+bytesFree);

        return bytesFree+","+bytesAvailable;
	}

	public static void refreshCameraInfo(ApplicationClass appClass){
//		ActivityManager activityManager = (ActivityManager)appClass.getActivity().getSystemService(appClass.getActivity().ACTIVITY_SERVICE);
//		MemoryInfo mi = new MemoryInfo();
//		activityManager.getMemoryInfo(mi);
//		logline.d("memory free"+":" + mi.availMem);
//
//		int availProc = Runtime.getRuntime().availableProcessors();
//		long total = Runtime.getRuntime().totalMemory()/(1024);
//		long free = Runtime.getRuntime().freeMemory()/(1024);
//		long max = Runtime.getRuntime().maxMemory()/(1024);
//		Toast.makeText(appClass.getActivity().getApplicationContext(),availProc + "\nTM " + Long.toString(total) + "\nFM " + Long.toString(free) + "\nMM" + Long.toString(max),Toast.LENGTH_SHORT).show();
//		
		// resolution info
		String pictureSize = null;
		if(appClass.isCherryFilter == 0){
			pictureSize = appClass.mParameter.getParameters().getPictureSize().width
					+"x"+appClass.mParameter.getParameters().getPictureSize().height;
		}else{
			pictureSize = appClass.mPreview.previewW+"x"+appClass.mPreview.previewH;
		}
		TextView tv = (TextView)appClass.getActivity().findViewById(R.id.info_resolution);
//		Button pButton = (Button)appClass.getActivity().findViewById(R.id.btn_camera_resolution);
//		if(frontCameraAccess)
//			pButton.setVisibility(View.INVISIBLE);
//		else
		
		tv.setText(pictureSize);		
		logline.d("Picture Size : "+ pictureSize);
		
		// scene info
//		tv = (TextView)appClass.getActivity().findViewById(R.id.text_camera_scene);
		ImageButton icon_scene = (ImageButton)appClass.getActivity().findViewById(R.id.btn_camera_scene);
		if(appClass.ListScene == null){
			icon_scene.setImageResource(R.drawable.scene_default);
//			tv.setVisibility(View.INVISIBLE);
		}else{
//			tv.setText(params.getSceneMode());
			
			if(appClass.mParameter.getParameters().getSceneMode().contains(Parameters.SCENE_MODE_ACTION)){
				icon_scene.setImageResource(R.drawable.scene_action);
			}else if(appClass.mParameter.getParameters().getSceneMode().contains(Parameters.SCENE_MODE_BARCODE)){
				icon_scene.setImageResource(R.drawable.scene_barcode);
			}else if(appClass.mParameter.getParameters().getSceneMode().contains(Parameters.SCENE_MODE_BEACH)){
				icon_scene.setImageResource(R.drawable.scene_beach);
			}else if(appClass.mParameter.getParameters().getSceneMode().contains(Parameters.SCENE_MODE_CANDLELIGHT)){
				icon_scene.setImageResource(R.drawable.scene_candle);
			}else if(appClass.mParameter.getParameters().getSceneMode().contains(Parameters.SCENE_MODE_FIREWORKS)){
				icon_scene.setImageResource(R.drawable.scene_fireworks);
			}else if(appClass.mParameter.getParameters().getSceneMode().contains(Parameters.SCENE_MODE_LANDSCAPE)){
				icon_scene.setImageResource(R.drawable.scene_landscape);
			}else if(appClass.mParameter.getParameters().getSceneMode().contains(Parameters.SCENE_MODE_NIGHT)){
				icon_scene.setImageResource(R.drawable.scene_night);
			}else if(appClass.mParameter.getParameters().getSceneMode().contains(Parameters.SCENE_MODE_NIGHT_PORTRAIT)){
				icon_scene.setImageResource(R.drawable.scene_nightportrait);
			}else if(appClass.mParameter.getParameters().getSceneMode().contains(Parameters.SCENE_MODE_PARTY)){
				icon_scene.setImageResource(R.drawable.scene_party);
			}else if(appClass.mParameter.getParameters().getSceneMode().contains(Parameters.SCENE_MODE_PORTRAIT)){
				icon_scene.setImageResource(R.drawable.scene_portrait);
			}else if(appClass.mParameter.getParameters().getSceneMode().contains(Parameters.SCENE_MODE_SNOW)){
				icon_scene.setImageResource(R.drawable.scene_snow);
			}else if(appClass.mParameter.getParameters().getSceneMode().contains(Parameters.SCENE_MODE_SPORTS)){
				icon_scene.setImageResource(R.drawable.scene_action);
			}else if(appClass.mParameter.getParameters().getSceneMode().contains(Parameters.SCENE_MODE_STEADYPHOTO)){
				icon_scene.setImageResource(R.drawable.scene_steady);
			}else if(appClass.mParameter.getParameters().getSceneMode().contains(Parameters.SCENE_MODE_SUNSET)){
				icon_scene.setImageResource(R.drawable.scene_sunset);
			}else if(appClass.mParameter.getParameters().getSceneMode().contains(Parameters.SCENE_MODE_THEATRE)){
				icon_scene.setImageResource(R.drawable.scene_theatre);
			}else if(appClass.mParameter.getParameters().getSceneMode().contains(IN.SCENE_FLOWER)){
				icon_scene.setImageResource(R.drawable.scene_flower);
			}else{
				icon_scene.setImageResource(R.drawable.scene_auto);
			}
		}
		
		logline.d("Scene mode : "+appClass.mParameter.getParameters().getSceneMode());
		
		
		// flash info
//		ImageButton flashButton = (ImageButton)appClass.getActivity().findViewById(R.id.btn_camera_flash);
		ImageView flashButton = (ImageView)appClass.getActivity().findViewById(R.id.icon_camera_flash);

		if(appClass.ListFlashMode == null || appClass.ListFlashMode.size() == 1){
			flashButton.setVisibility(View.INVISIBLE);
		}else {			
			if(appClass.mParameter.getParameters().getFlashMode().equals(Parameters.FLASH_MODE_AUTO)){
				flashButton.setImageResource(R.drawable.flash_auto);
			}else if(appClass.mParameter.getParameters().getFlashMode().equals(Parameters.FLASH_MODE_ON)){
				flashButton.setImageResource(R.drawable.flash_on);
			}else if(appClass.mParameter.getParameters().getFlashMode().equals(Parameters.FLASH_MODE_OFF)){
				flashButton.setImageResource(R.drawable.flash_off);
			}else if(appClass.mParameter.getParameters().getFlashMode().equals(Parameters.FLASH_MODE_TORCH)){
				flashButton.setImageResource(R.drawable.flash_torch);
			}else if(appClass.mParameter.getParameters().getFlashMode().equals(Parameters.FLASH_MODE_RED_EYE)){
				flashButton.setImageResource(R.drawable.flash_redeye);
			}
//			tv.setText(params.getFlashMode());			
			logline.d("Flash mode : "+appClass.mParameter.getParameters().getFlashMode());
		}		
			
		// focus info
//		ImageButton focusButton = (ImageButton)appClass.getActivity().findViewById(R.id.btn_camera_focus);
		ImageView focusButton = (ImageView)appClass.getActivity().findViewById(R.id.icon_camera_focus);
		if(appClass.ListFocus == null || appClass.ListFocus.size() == 1){
			focusButton.setVisibility(View.INVISIBLE);
		}else{			
			if(appClass.mParameter.getParameters().getFocusMode().equals(Parameters.FOCUS_MODE_AUTO)){
				focusButton.setImageResource(R.drawable.focus_auto);			
			}else if(appClass.mParameter.getParameters().getFocusMode().equals(Parameters.FOCUS_MODE_INFINITY)){
				focusButton.setImageResource(R.drawable.focus_infinity);			
			}else if(appClass.mParameter.getParameters().getFocusMode().equals(Parameters.FOCUS_MODE_MACRO)){
				focusButton.setImageResource(R.drawable.focus_macro);
			}else if(appClass.mParameter.getParameters().getFocusMode().equals(Parameters.FOCUS_MODE_FIXED)){
				focusButton.setImageResource(R.drawable.focus_fix);
			}
//			tv.setText(params.getFocusMode());
			logline.d("Focus mode : "+appClass.mParameter.getParameters().getFocusMode());
		}	
		
		
		// effect info
//		Button effectButton = (Button)appClass.getActivity().findViewById(R.id.btn_camera_effect);
//		if(ListColorEffect == null){
//			effectButton.setVisibility(View.INVISIBLE);
//		}else {
//			if(params.getColorEffect().equals(IN.EFFECT_NONE))
//				effectButton.setText("effect-"+ params.getColorEffect());
//			else
//				effectButton.setText(params.getColorEffect());
//			logline.d("Effect mode : "+params.getColorEffect());
//		}
				
		// exposure mode
		tv = (TextView)appClass.getActivity().findViewById(R.id.text_camera_exposure);
		if(appClass.expoMaxSize != 0){			
			tv.setText("Expo["+appClass.mParameter.getParameters().getExposureCompensation()+"]");
			logline.d("Expo step : "+appClass.mParameter.getParameters().getExposureCompensation());
		}else{
			tv.setText("");
		}
	}
	
	public void cameraFinish(String intentGetAction, File file, ApplicationClass appClass){

		if(intentGetAction.contains("IMAGE_CAPTURE")){
//			Intent intent = appClass.getActivity().getIntent();
//			if(ccUtil.currentPhotoNum == 0){
//				intent.putExtra("pic_data", "null");
//			}else{
//				intent.putExtra("pic_data", ccUtil.PhotoData.get(ccUtil.currentPhotoNum-1));
//			}
//			intent.putExtra("pic_num", ccUtil.currentPhotoNum);
//			appClass.getActivity().setResult(Activity.RESULT_OK,intent);
			
			
//			Uri saveUri = (Uri) appClass.getActivity().getIntent().getExtras().getParcelable(MediaStore.EXTRA_OUTPUT);
//
//			if (saveUri != null)
//			{
//			    // Save the bitmap to the specified URI (use a try/catch block)
//			    outputStream = getContentResolver().openOutputStream(saveUri);
//			    outputStream.write(data); // write your bitmap here
//			    outputStream.close();
//			    appClass.getActivity().setResult(Activity.RESULT_OK);
//			}
//			else
//			{
//			    // If the intent doesn't contain an URI, send the bitmap as a Parcelable
//			    // (it is a good idea to reduce its size to ~50k pixels before)
//				appClass.getActivity().setResult(Activity.RESULT_OK
//						, new Intent("inline-data").putExtra("data", bitmap));
//			}
			
			Bitmap bitmap = BitmapFactory.decodeFile(file.getPath());
			appClass.getActivity().setResult(Activity.RESULT_OK
					, new Intent("inline-data").putExtra("data", bitmap));
		}
	}
	
	public static boolean checkCameraHardware(Context context) {
	    if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
	        // this device has a camera
	        return true;
	    } else if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT)){
	        // this device has a camera
	        return true;
	    } else {
	        // no camera on this device
	        return false;
	    }
	}
	
	public static int checkCameraFacingMode(){
		logline.d("*****Build.VERSION.SDK_INT**** "+ccCamUtil.getSdkVer());
		if(ccCamUtil.getSdkVer() > 8){
			Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
		    int cameraCount = Camera.getNumberOfCameras();
		    if(cameraCount == 0)
		    	return 0;
		    Camera.getCameraInfo( 0, cameraInfo);
//		    logline.d("CAMERA_FACING : "+ cameraInfo.facing);
		    
		    if(cameraCount == 1){
		    	if(cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK)
		    		return IN.CAMERA_ID_BACK;
		    	else if(cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT)
		    		return IN.CAMERA_ID_FRONT;
		    	
		    }else if(cameraCount == 2){
//		    	if(cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK)
		    		return IN.CAMERA_ID_BACK_FRONT;
//		    	else if(cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT)
//		    		return IN.CAMERA_ID_FRONT_BACK;
		    }
		}
	    return 0;
	}
	
	public static Camera getCameraInstance(int cameraFacingMode, boolean frontCameraAccess){
		Camera c = null;	  
	    if(Build.VERSION.SDK_INT < 9){
	    	try {
//	    		releaseCameraAndPreview(c);
	    		c = Camera.open(); // attempt to get a Camera instance
		    }
		    catch (Exception e){
		    	logline.d("Camera failed to open: " + e.getLocalizedMessage());
		    	return null;
		        // Camera is not available (in use or does not exist)
		    }
	    }else{
//	    	logline.d("frontCameraAccess = "+frontCameraAccess);
	    	
//	    	try {
//     			releaseCameraAndPreview();
//     			c = Camera.open(0);
//            } catch (RuntimeException e) {
//            	logline.d("0Camera failed to open: " + e.getLocalizedMessage());
//            }
	    	logline.d("camera = "+Camera.getNumberOfCameras());
	    	if(frontCameraAccess){	    		
	         	if(cameraFacingMode == IN.CAMERA_ID_FRONT){
	         		try {
	         			c = Camera.open(0);
	                } catch (RuntimeException e) {
	                	logline.d("0Camera failed to open: " + e.getLocalizedMessage());
	                	return null;
	                }
	         	}else{
	         		try {
	         			c = Camera.open(1);	                    
	                } catch (RuntimeException e) {
	                	logline.d("1Camera failed to open: " + e.getLocalizedMessage());
	                	return null;
	                }
	         	}
	    	}else{
	    		 try {
	    			 c = Camera.open(0);	                 
	             } catch (RuntimeException e) {
	             	logline.d("0Camera failed to open: " + e.getLocalizedMessage());
	             	return null;
	             }
	    	}
	    }
	    return c; // returns null if camera is unavailable
	}

//	private boolean safeCameraOpen(int id) {
//	    boolean qOpened = false;
//	  
//	    try {
//	        releaseCameraAndPreview();
//	        mCamera = Camera.open(id);
//	        qOpened = (mCamera != null);
//	    } catch (Exception e) {
//	        Log.e(getString(R.string.app_name), "failed to open Camera");
//	        e.printStackTrace();
//	    }
//
//	    return qOpened;    
//	}

	
//	public void surfaceChanged(SurfaceHolder holder,int format, int width,int height) 
//    {
//        camera.setPreviewCallback(new PreviewCallback() {
//
//            public void onPreviewFrame(byte[] data, Camera camera) {
//
//                Camera.Parameters parameters = camera.getParameters();
//
//                int width = parameters.getPreviewSize().width;
//                int height = parameters.getPreviewSize().height;
//
//                ByteArrayOutputStream outstr = new ByteArrayOutputStream();
//                Rect rect = new Rect(0, 0, width, height); 
//                YuvImage yuvimage=new YuvImage(data,ImageFormat.NV21,width,height,null);
//                yuvimage.compressToJpeg(rect, 100, outstr);
//                Bitmap bmp = BitmapFactory.decodeByteArray(outstr.toByteArray(), 0, outstr.size());
//            }
//}
//}
	
    
	/**
	 * @param mContext  the application context
	 * @param volume (range = 0.0 to 1.0)
	 * @param loop A loop value of -1 means loop forever, a value of 0 means don't loop, 
	 * other values indicate the number of repeats
	 * @param isTimerBeep
	 * 
	 * @return non-zero streamID if successful, zero if failed
	 */
	public static int playSound(Context mContext, float volume, int loop){
		SoundPool sound_pool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
		int sound_beep = sound_pool.load(mContext, R.raw.beep, 1);
		int streamId = 0;
		streamId = sound_pool.play(sound_beep, volume, volume, 0, loop, 1f);
		return streamId;
	}

	public float[] setRamMemory(Activity mActivity){
		MemoryInfo mi = new MemoryInfo();
		ActivityManager activityManager = (ActivityManager)mActivity.getSystemService(Context.ACTIVITY_SERVICE);
		activityManager.getMemoryInfo(mi);
		float availableMemory = (float)mi.availMem/1048576f;
		float totalMemory = (float)getTotalMemory()/1024f;			
		totalMemory = Math.round(totalMemory*10);
		totalMemory = totalMemory/10;		
		availableMemory = Math.round(availableMemory*10);
		availableMemory = availableMemory/10;
//		logline.d("RamMemory : "+availableMemory+"/"+totalMemory);
		float[] i = new float[3];
		i[0] = totalMemory;
		i[1] = availableMemory;
		i[2] = availableMemory/totalMemory*100;
        return i;
	}
	
	public static long prevClickTime = 0;
	public static boolean isDoubleClick(){
		long nextClickTime = System.currentTimeMillis();
		if(prevClickTime+1000 > nextClickTime)
			return true;
		else
			prevClickTime = nextClickTime;
		return false;
	}

	public static void saveData(Context mContext, String key, String value){
		SharedPreferences prefs = mContext.getSharedPreferences("cherrycamera", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(key, value);
        editor.commit();
	}
	
	public static String loadData(Context mContext, String key, String defaultValue){
		SharedPreferences prefs = mContext.getSharedPreferences("cherrycamera", Context.MODE_PRIVATE);
		return prefs.getString(key, defaultValue);
	}
	
	public static String changeDateFormat(String date, String format){
//		String pattern = "yyyy-MM-dd a HH:mm:ss";
		SimpleDateFormat formatter = new SimpleDateFormat(format);	
		return (String)formatter.format(new Timestamp(Long.parseLong(date)));
//		PhotoDate.add();	
	}
	
	public static boolean checkIsFile(String path){
		File f = new File(path);
		if (f.exists())
			return true;
		else
			return false;
	}
	
	/** 
	 * 0 off,
	 * 1 on,
	 * -1 null
	 */
	public static int checkBluetooth(){
		BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (mBluetoothAdapter == null) {
			return -1;
		} else {
		    if (mBluetoothAdapter.isEnabled()) {
		        return 1;
		    }else{
		    	return 0;
		    }
		}
	}
	
}
