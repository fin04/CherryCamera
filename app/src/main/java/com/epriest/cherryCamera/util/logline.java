package com.epriest.cherryCamera.util;

//import com.epriest.cherryCamera.BuildConfig;

import android.content.Context;
import android.util.Log;

public class logline {
	static boolean isLog = false;//BuildConfig.DEBUG;
	public static void d(String str){		
		if (isLog)
			Log.d(IN.TAG, str);
	}
	
	public static void d(String tag, String str){
		if (isLog)
			Log.d(tag, str);
	}
	
	public static void d(Context con, String str){
		if (isLog)
			Log.d(con.getClass().getSimpleName(), str);
	}
}
