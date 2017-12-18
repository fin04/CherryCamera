package com.epriest.cherryCamera.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.location.LocationManager;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ccUtil {
	
	static public String getSDPath() {
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

	static public boolean nullFile(String path) {
		logline.d(" === "+path);
		File file = new File(path);
		long file_size = file.length();
		logline.d("file_size : "+file_size);
		if(file_size == 0)
			return true;
//		try{
//			
//		}catch(Exception e){
//			
//		}
		return false;
	}
	
	static public DisplayMetrics getScreenSize(Activity act){
		DisplayMetrics displaymetrics = new DisplayMetrics();
		act.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		return displaymetrics; 	
	}
	
	static public int gerRandom(int max, int offset){
		 int Result = new Random().nextInt(max) + offset;
         return Result;
	}
	
	static public int getResId(Resources resources, String resStr, String type, String packageName){
		try{
			int resId = resources.getIdentifier(resStr, type, packageName);
			return resId;
		}catch(Exception e){
			return -1;
		}
	}
	
	public static ArrayList<View> getAllChildren(View v) {

	    if (!(v instanceof ViewGroup)) {
	        ArrayList<View> viewArrayList = new ArrayList<View>();
	        viewArrayList.add(v);
	        return viewArrayList;
	    }

	    ArrayList<View> result = new ArrayList<View>();

	    ViewGroup viewGroup = (ViewGroup) v;
	    for (int i = 0; i < viewGroup.getChildCount(); i++) {

	        View child = viewGroup.getChildAt(i);

	        ArrayList<View> viewArrayList = new ArrayList<View>();
	        viewArrayList.add(child);
	        viewArrayList.addAll(getAllChildren(child));

	        result.addAll(viewArrayList);
	    }
	    return result;
	}
	
	public static void setExternalFontOnView(View view){
		ArrayList<View> arr = getAllChildren(view);
		for(View chView : arr){
			if(chView instanceof TextView || chView instanceof Button || chView instanceof EditText){
//				ULog.d("----"+chView.getTag());
				setFontTextview(view.getContext(), chView);
			}
		}
	}
	
	public static void setFontTextview(Context context, View tv){
		String fontPath = "fonts/";
		String tag = (String) tv.getTag();
		if(tag == null){
			return;
		}
		if(tag.contains("Roboto"))
			fontPath+="Roboto/";
		else if(tag.contains("NotoSansKR"))
			fontPath+="NotoSansKR/";
		else
			return;
		
		if(tag.contains(".ttf"))
			fontPath+=tag;
		else
			fontPath+=tag+".ttf";
		try{
			Typeface typeface = Typeface.createFromAsset(context.getAssets(), fontPath);
			if(tv instanceof TextView)
				((TextView)tv).setTypeface(typeface);
			else if(tv instanceof EditText)
				((EditText)tv).setTypeface(typeface);
			else 
				((Button)tv).setTypeface(typeface);
		}catch(Exception e){
//			e.printStackTrace();
		}
	}
	
	public static Paint setFontToPaint(Context con, Paint paint, int font){
		String pathToFont = "fonts/NotoSansKR/NotoSansKR-Medium-Hestia.ttf";
		switch(font){
		case 0:
			break;
		case 1:
			pathToFont = "fonts/Roboto/Roboto-Medium.ttf";
			break;
		}
		Typeface plain = Typeface.createFromAsset(con.getAssets(), pathToFont);
		Typeface bold = Typeface.create(plain, Typeface.NORMAL);
		paint.setTypeface(bold);		
		return paint;
	}
		
	public static int getCardColor(String hexColor){
		int color;
		try{
			color = Color.parseColor(hexColor);
		}catch(Exception e){
			color = Color.parseColor("#0bb6d8");
		}
		
		return color;
	}
	
	public static int getCardBackImage(Resources resources, String imageName, String packName){
		if(imageName == null)
			return 0;
		String resName = "@drawable/"+imageName;
//		ULog.d("---"+resName);
		int resId;
		try{
			resId = resources.getIdentifier(resName, "drawable", packName);
		}catch(Exception e){
			resId = 0;
		}
		
		return resId;
	}		
	
	public static int getRandomInt(int minNum, int maxNum){
		int iRand = new Random().nextInt(maxNum-minNum);
		return iRand+minNum;
	}
	
	public static Bitmap blur(Context context, Bitmap sentBitmap, int radius) {

		if (VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
			Bitmap bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);

			final RenderScript rs = RenderScript.create(context);
			final Allocation input = Allocation.createFromBitmap(rs, sentBitmap, Allocation.MipmapControl.MIPMAP_NONE,
					Allocation.USAGE_SCRIPT);
			final Allocation output = Allocation.createTyped(rs, input.getType());
			final ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
			script.setRadius(radius); //0.0f ~ 25.0f
			script.setInput(input);
			script.forEach(output);
			output.copyTo(bitmap);
			return bitmap;
		}
		return sentBitmap;
		
	}
	
	public static ArrayList<ContactInfo> getBirthdayToContact(Context con){
		ArrayList<ContactInfo> birthdayArrayList = new ArrayList<ContactInfo>();
		try{    
            String[] projection = new String[]{ContactsContract.Data.RAW_CONTACT_ID,ContactsContract.Data.DATA1};  
            String selection = "mimetype='vnd.android.cursor.item/contact_event' and data2='3'";  
            Cursor cur = con.getContentResolver().query(ContactsContract.Data.CONTENT_URI, projection, selection, null, null);
            if(cur!=null){
                while(cur.moveToNext()){
                    if(cur.getString(cur.getColumnIndex(ContactsContract.Data.DATA1))!=null){
                        String birthday = cur.getString(cur.getColumnIndex(ContactsContract.Data.DATA1));
                        String  row_id = cur.getString(cur.getColumnIndex(ContactsContract.Data.RAW_CONTACT_ID));
                        birthdayArrayList.add(AddContactList(con, birthday, row_id)); 
                    }  
                }  
            }
            cur.close();
            return birthdayArrayList;
        } catch (Exception e) {      
            e.printStackTrace();
            return null;
        }
	}
	
	private static ContactInfo AddContactList(Context con, String birthday,String row_id) {  
        ContactInfo info = new ContactInfo();
        info.setBirthday(birthday);  
        String[] projection= {Phone.DISPLAY_NAME, Phone.NUMBER, Phone.PHOTO_URI};  
        Cursor cur = con.getContentResolver().query(Phone.CONTENT_URI, 
        		projection,Phone.RAW_CONTACT_ID+"=?", new String[]{row_id}, null);  
        if(cur!=null){  
            cur.moveToFirst();  
            String number = cur.getString(cur.getColumnIndex(Phone.NUMBER));  
            String name = cur.getString(cur.getColumnIndex(Phone.DISPLAY_NAME));  
            String photo_uri = cur.getString(cur.getColumnIndex(Phone.PHOTO_URI));
            info.setNumber(number);
            info.setName(name);
            info.setUri(photo_uri);
        }   
        cur.close();
        return info;
    }
	
	public static class ContactInfo {  
	    private String number;
	    private String birthday;
	    private String name;
	    private String uri;

	    public String getNumber() {  
	        return number;  
	    }  
	    public void setNumber(String number) {  
	        this.number = number;  
	    }  
	    public String getBirthday() {  
	        return birthday;  
	    }  
	    public void setBirthday(String birthday) {  
	        this.birthday = birthday;  
	    }
	    public String getName() {  
	        return name;  
	    }  
	    public void setName(String name) {  
	        this.name = name;  
	    }
	    public String getUri() {  
	        return uri;  
	    }  
	    public void setUri(String uri) {  
	        this.uri = uri;  
	    } 
	}

	
	public static boolean getGpsState(Context context, int gpsState) {
		LocationManager lm = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
		switch(gpsState){
		case IN.GPS_ONOFF:
//			if(lm.isProviderEnabled(LocationManager.GPS_PROVIDER)){
//				final Intent poke = new Intent();
//		        poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
//		        poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
//		        poke.setData(Uri.parse("3")); 
//		        mActivity.sendBroadcast(poke);
//			}else{
			Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent);
//			}
				
			break;
		case IN.GPS_CHECK:			
			if(lm.isProviderEnabled(LocationManager.GPS_PROVIDER))
				return true;
			break;
		}
		return false;
	}

	public static boolean isSupported(String value, List<String> supported) {
		return supported == null ? false : supported.indexOf(value) >= 0;
	}
}
