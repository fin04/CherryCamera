package com.epriest.cherryCamera.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import com.epriest.cherryCamera.ApplicationClass;
import com.epriest.cherryCamera.gallery.ccPhotoInfo.PhotoItem;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Rect;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;

public class ccPicUtil {

	public ccPicUtil() {
		
	}
			
	static public boolean wrongBitmap(String path) {
		BitmapInfo info = getBitmapSize(path);
		if(info.mime == null)
			return true;
		else
			return false;
		
	}
	
	static public void decodeYUV(byte[] inputYUV420SP, int [] result, int width, int height) {
		final int frameSize = width * height;
		for (int j = 0, yp = 0; j < height; j++) {
		   int uvp = frameSize + (j >> 1) * width, u = 0, v = 0;
		   for (int i = 0; i < width; i++, yp++) {
		     int y = (0xff & ((int) inputYUV420SP[yp])) - 16;
		     if (y < 0) y = 0;
		     if ((i & 1) == 0) {
		         v = (0xff & inputYUV420SP[uvp++]) - 128;
		         u = (0xff & inputYUV420SP[uvp++]) - 128;
		     }
		     int y1192 = 1192 * y;
		     int r = (y1192 + 1634 * v);
		     int g = (y1192 - 833 * v - 400 * u);
		     int b = (y1192 + 2066 * u);
		     if (r < 0) r = 0; else if (r > 262143) r = 262143;
		     if (g < 0) g = 0; else if (g > 262143) g = 262143;
		     if (b < 0) b = 0; else if (b > 262143) b = 262143;
		     result[yp] = 0xff000000 | ((r << 6) & 0xff0000) | ((g >> 2) & 0xff00) | ((b >> 10) & 0xff);
		   }
		}
	}
	
	/**
	 * @param picName file path
	 * @return 0-width, 1-height, 2-mime type
	 */
	static public BitmapInfo getBitmapSize(String picName){
		BitmapInfo bitinfo = new BitmapInfo();
		try {
	        BitmapFactory.Options options = new BitmapFactory.Options();
	        options.inJustDecodeBounds = true;
	        BitmapFactory.decodeFile(picName, options);
	        bitinfo.width = options.outWidth;
	        bitinfo.height = options.outHeight;
	        bitinfo.mime = options.outMimeType;
	    } catch(Exception e) {	   
             
	    }
		return bitinfo;
	}
	
	static public Bitmap previewCooking(String picName, int optSize, int lcdWidth, int lcdHeight){		
		try{
			return decodeBitmap(picName, optSize, lcdWidth, lcdHeight);
		}catch(Exception e){
			logline.d("previewBitmap Make Exception : "+e);
			return null;
		}
	}
	
	static public Bitmap decodeBitmap(String picName, int optSize, int lcdWidth, int lcdHeight){
		BitmapInfo bitinfo = getBitmapSize(picName);
		int ratioW = bitinfo.width / lcdWidth;
		int ratioH = bitinfo.height / lcdHeight;

		int bitmapRatio = 1;
		if(ratioW > ratioH)
			bitmapRatio = ratioW;			
		else
			bitmapRatio = ratioH;
		
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = false;
		if(optSize == 0)
			options.inSampleSize = bitmapRatio;
		else
			options.inSampleSize = optSize;
		options.inPurgeable = true;
		options.inDither = true;
 		options.inPreferredConfig = Bitmap.Config.RGB_565;
		Bitmap bitmap = BitmapFactory.decodeFile(picName, options);
//		logline.d("bitmapRatio:  "+optSize+",size : "+previewBitmap.getWidth()+","+previewBitmap.getHeight());
		return bitmap;
	}
	
	static public int getContentCount(Activity act){
		Uri imageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
		String[] projection = new String[]{MediaStore.Images.Media.BUCKET_DISPLAY_NAME};
		String where = MediaStore.Images.Media.BUCKET_DISPLAY_NAME+"='cherrycamera'";
		ContentResolver cr = act.getApplicationContext().getContentResolver();
		Cursor cur = cr.query(imageUri,
                projection, where, null, null);
		return cur.getCount();		
	}
	
	static public PhotoItem getContent(Activity act, Uri imageUri){
		PhotoItem item = new PhotoItem();
		String[] projection = new String[]{
	            MediaStore.Images.Media._ID,
	            MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
	            MediaStore.Images.Media.DATE_TAKEN,
	            MediaStore.Images.Media.DISPLAY_NAME,
	            MediaStore.Images.Media.SIZE,
	            MediaStore.Images.Media.DATA,
	    };
		ContentResolver cr = act.getApplicationContext().getContentResolver();
		Cursor cur = cr.query(imageUri,
                projection, null, null, null);
		if(cur != null){
			try {
                if (cur.moveToFirst()) {
                	item.PhotoId = cur.getString(cur.getColumnIndex(MediaStore.Images.Media._ID));
     	            item.PhotoDate = cur.getString(cur.getColumnIndex(MediaStore.Images.Media.DATE_TAKEN));
//     	            item.PhotoBucket = cur.getString(cur.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME));
     	            item.PhotoName = cur.getString(cur.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
     	            item.PhotoDataSize = cur.getString(cur.getColumnIndex(MediaStore.Images.Media.SIZE));
     	            item.PhotoData = cur.getString(cur.getColumnIndex(MediaStore.Images.Media.DATA));             	
                }                	
			} finally {
            	cur.close();
            }
		}
		return item;
	}
	
	static public boolean addContent(Activity act, Uri uri){
		
		return  true;
	}
	
	static public int eraseContent(Activity act, String PhotoId, int photoTotal, int erasePhotoPos){
		long picID = Long.parseLong(PhotoId);		
		Uri imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, picID);
		logline.d("imageUri - " + imageUri.toString());
		act.getContentResolver().delete(imageUri, null, null);
//		int pNum = currentPhotoNum;
//		contentListCooking(act);
		photoTotal = getContentCount(act);
		if(photoTotal <= 0){
//			currentPhotoNum = 0;
			return -1;
		}else{
//			currentPhotoNum = pNum;
			if(erasePhotoPos >= photoTotal){
//				currentPhotoNum = allPhotoNum;
				return photoTotal-1;
			}
			return erasePhotoPos;
		}
	}
	
	public String getRealImagePath (Uri uriPath,ApplicationClass appClass){	
		String []proj = {MediaStore.Images.Media.DATA};
		Cursor cursor = appClass.getActivity().managedQuery (uriPath, proj, null, null, null);
		int index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		String path = cursor.getString(index);
		path = path.substring(5);
		return path;
	}

	static public ArrayList<PhotoItem> getContentList(Activity act){
		ArrayList<PhotoItem> PhotoData = new ArrayList<PhotoItem>();		
		String[] projection = new String[]{
	            MediaStore.Images.Media._ID,
	            MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
	            MediaStore.Images.Media.DATE_TAKEN,
	            MediaStore.Images.Media.DISPLAY_NAME,
	            MediaStore.Images.Media.SIZE,
	            MediaStore.Images.Media.DATA,
	    };
		ContentResolver cr = act.getApplicationContext().getContentResolver();
		Uri imageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
		String where = "bucket_display_name='cherrycamera'";
		Cursor cur = cr.query(imageUri,
                projection, where, null, null);
		try {
			cur.moveToLast();
			while (!cur.isBeforeFirst()) {
            	PhotoItem item = new PhotoItem();        	
 	            String bucket = cur.getString(cur.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME));
            	if(bucket.equals("cherrycamera")){
            		item.PhotoId = cur.getString(cur.getColumnIndex(MediaStore.Images.Media._ID));
     	            item.PhotoDate = cur.getString(cur.getColumnIndex(MediaStore.Images.Media.DATE_TAKEN));
     	            item.PhotoName = cur.getString(cur.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
    	            item.PhotoDataSize = cur.getString(cur.getColumnIndex(MediaStore.Images.Media.SIZE));
    	            item.PhotoData = cur.getString(cur.getColumnIndex(MediaStore.Images.Media.DATA));     
            		PhotoData.add(item);
            	}
            	cur.moveToPrevious();
            }
		} finally {
        	cur.close();
        }
		return PhotoData;
	}
	
	/*static public int contentListCooking(Activity act){
		ApplicationClass appc = (ApplicationClass)act.getApplicationContext();
		if(appc.PhotoId != null){
			appc.PhotoId.clear();
			appc.PhotoName.clear();
			appc.PhotoDate.clear();
			appc.PhotoDataSize.clear();
			appc.PhotoData.clear();
		}
//		PhotoId = new ArrayList<String>();
//		PhotoName = new ArrayList<String>();
//		PhotoDate = new ArrayList<String>();
//		PhotoSize = new ArrayList<String>();
//		PhotoData = new ArrayList<String>();
		
		// which image properties are we querying
		String[] projection = new String[]{
	            MediaStore.Images.Media._ID,
	            MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
	            MediaStore.Images.Media.DATE_TAKEN,
	            MediaStore.Images.Media.DISPLAY_NAME,
	            MediaStore.Images.Media.SIZE,
	            MediaStore.Images.Media.DATA,
	    };
		// Get the base URI for the People table in the Contacts content provider.
		Uri images = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
		// Make the query.

	    Cursor cur = act.managedQuery(
	    		images,
	            projection, // Which columns to return
	            "",         // Which rows to return (all rows)
	            null,       // Selection arguments (none)
	            ""          // Ordering
	            );

		if (cur.moveToLast()) {
			String id, date, name, bucket, size, data;

	        int idColumn = cur.getColumnIndex(
	            MediaStore.Images.Media._ID);

	        int dateColumn = cur.getColumnIndex(
	            MediaStore.Images.Media.DATE_TAKEN);
	        
	        int nameColumn = cur.getColumnIndex(
		            MediaStore.Images.Media.DISPLAY_NAME);
	        
	        int bucketColumn = cur.getColumnIndex(
		            MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
	        
	        int sizeColumn = cur.getColumnIndex(
		            MediaStore.Images.Media.SIZE);
	        
	        int dataColumn = cur.getColumnIndex(
		            MediaStore.Images.Media.DATA);
	        
	        	        do {
	            // Get the field values
	            id = cur.getString(idColumn);
	            date = cur.getString(dateColumn);
	            bucket = cur.getString(bucketColumn);
	            name = cur.getString(nameColumn);
	            size = cur.getString(sizeColumn);
	            data = cur.getString(dataColumn);
	            
	            if(bucket.equals("cherrycamera")){
	            	appc.PhotoId.add(id);
	            	appc.PhotoName.add(name);
	            	appc.PhotoDate.add(date);
	            	
//					String pattern = "yyyy-MM-dd a HH:mm:ss";
//					SimpleDateFormat formatter = new SimpleDateFormat(pattern);			
//					PhotoDate.add((String)formatter.format(new Timestamp(Long.parseLong(date))));					
	            	appc.PhotoDataSize.add(size);
	            	appc.PhotoData.add(data);
//	            	Uri imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, Long.parseLong(id));
	            	
//	            	logline.d(id+"-NAME=" + name
//	 	                   +",date_taken=" + PhotoDate.get(PhotoDate.size()-1)
//	 	                   +",size=" + size
//	 	                   +",path=" + path);
	            }	            
	            
	        } while (cur.moveToPrevious());	        
	    }
		
		int allPhotoNum = appc.PhotoId.size();
        logline.d("photoNum : "+allPhotoNum);
        return allPhotoNum;
	}*/
	
	public static class BitmapInfo{
		public int width;
		public int height;
		public String mime;
	}
	
	public Rect rectNull(Bitmap bitmap){
		int x = 0;
		int y = 0;
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();
		
		Rect src = new Rect(x,y,w,h);
		
		return src;
	}
	
	public Bitmap loadAssetsBitmap(Context context, String path, Bitmap.Config format) {
		Bitmap bit = null;
		try{
			InputStream is = context.getAssets().open(path);
			BitmapFactory.Options opt = new BitmapFactory.Options();
			opt.inPreferredConfig = format;
			bit = BitmapFactory.decodeStream(is, null, opt);
			if(is != null)
				try{
					is.close();
				}catch(IOException e){}
		}catch(IOException e){
			Log.d("IOException..", ""+e);
		}
		return bit;
	}
	
	public static Bitmap getViewDrawingCache(View view){
		if(view.isDrawingCacheEnabled())
			view.setDrawingCacheEnabled(false);
		view.setDrawingCacheEnabled(true);
		Bitmap bitmap = view.getDrawingCache();
		return bitmap;
	}
	
	public static Animation inFromRightAnimation() {
        Animation inFromRight = new TranslateAnimation(
        Animation.RELATIVE_TO_PARENT,  +1.0f, Animation.RELATIVE_TO_PARENT,  0.0f,
        Animation.RELATIVE_TO_PARENT,  0.0f, Animation.RELATIVE_TO_PARENT,   0.0f
        );
        inFromRight.setDuration(350);
        inFromRight.setInterpolator(new AccelerateInterpolator());
        return inFromRight;        
	}
	
    public static Animation outToLeftAnimation() {
        Animation outtoLeft = new TranslateAnimation(
         Animation.RELATIVE_TO_PARENT,  0.0f, Animation.RELATIVE_TO_PARENT,  -1.0f,
         Animation.RELATIVE_TO_PARENT,  0.0f, Animation.RELATIVE_TO_PARENT,   0.0f
        );
        outtoLeft.setDuration(350);
        outtoLeft.setInterpolator(new AccelerateInterpolator());
        return outtoLeft;
    }  
    
    // for the next movement
    public static Animation inFromLeftAnimation() {
        Animation inFromLeft = new TranslateAnimation(
        Animation.RELATIVE_TO_PARENT,  -1.0f, Animation.RELATIVE_TO_PARENT,  0.0f,
        Animation.RELATIVE_TO_PARENT,  0.0f, Animation.RELATIVE_TO_PARENT,   0.0f
        );
        inFromLeft.setDuration(350);
        inFromLeft.setInterpolator(new AccelerateInterpolator());
        return inFromLeft;        
    }
    
    public static Animation outToRightAnimation() {
        Animation outtoRight = new TranslateAnimation(
         Animation.RELATIVE_TO_PARENT,  0.0f, Animation.RELATIVE_TO_PARENT,  +1.0f,
         Animation.RELATIVE_TO_PARENT,  0.0f, Animation.RELATIVE_TO_PARENT,   0.0f
        );
        outtoRight.setDuration(350);
        outtoRight.setInterpolator(new AccelerateInterpolator());
        return outtoRight;        
    }
    
    public static RotateAnimation rotateAnimation(Context context, 
    		float startAngle, float endAngle, int duration, int repeat){    	
    	if(duration <= 0)
    		duration = 500;
    	RotateAnimation rotateAnim = new RotateAnimation(startAngle, endAngle,
    	          Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
    	rotateAnim.setStartOffset(0);
    	rotateAnim.setDuration(duration);
    	rotateAnim.setInterpolator(context, android.R.anim.decelerate_interpolator);
    	rotateAnim.setFillAfter(true);
    	rotateAnim.setRepeatCount(repeat);
		return rotateAnim;    	
    }
    
    static public ColorFilter adjustHue( float value ){
	    ColorMatrix cm = new ColorMatrix();

	    adjustHue(cm, value);

	    return new ColorMatrixColorFilter(cm);
	}

    static public void adjustHue(ColorMatrix cm, float value){
	    value = cleanValue(value, 180f) / 180f * (float) Math.PI;
	    if (value == 0)
	    {
	        return;
	    }
	    float cosVal = (float) Math.cos(value);
	    float sinVal = (float) Math.sin(value);
	    float lumR = 0.213f;
	    float lumG = 0.715f;
	    float lumB = 0.072f;
	    float[] mat = new float[]
	    { 
	            lumR + cosVal * (1 - lumR) + sinVal * (-lumR), lumG + cosVal * (-lumG) + sinVal * (-lumG), lumB + cosVal * (-lumB) + sinVal * (1 - lumB), 0, 0, 
	            lumR + cosVal * (-lumR) + sinVal * (0.143f), lumG + cosVal * (1 - lumG) + sinVal * (0.140f), lumB + cosVal * (-lumB) + sinVal * (-0.283f), 0, 0,
	            lumR + cosVal * (-lumR) + sinVal * (-(1 - lumR)), lumG + cosVal * (-lumG) + sinVal * (lumG), lumB + cosVal * (1 - lumB) + sinVal * (lumB), 0, 0, 
	            0f, 0f, 0f, 1f, 0f, 
	            0f, 0f, 0f, 0f, 1f };
	    cm.postConcat(new ColorMatrix(mat));
	}
	
    static protected float cleanValue(float p_val, float p_limit){
	    return Math.min(p_limit, Math.max(-p_limit, p_val));
	}
}
