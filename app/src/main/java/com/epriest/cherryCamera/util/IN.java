package com.epriest.cherryCamera.util;

import com.epriest.cherryCamera.R;

public interface IN {
	
	static final public String TAG = "Cherry Camera!";
	static final public String AD_NUM = "a14ecb67bd1c630";
	
	static final public int STATE_MENU = 19;
	
	static final public int CAMERA_ID_BACK_FRONT = 1;
	static final public int CAMERA_ID_FRONT_BACK = 2;
	static final public int CAMERA_ID_BACK = 3;
	static final public int CAMERA_ID_FRONT = 4;
	
	static final public int MENU_CAM = 11;
	static final public int MENU_SCENE = 12;
	
	public static final int MEDIA_TYPE_IMAGE = 1;
	public static final int MEDIA_TYPE_VIDEO = 2;
	
	static final int CAMERA_MENU_RESOLUTION = 1;
	static final int CAMERA_MENU_HELP = 2;	
	static final int CAMERA_MENU_EXIT = 3;
	static final int CAMERA_MENU_FRONTCAMERA = 4;
	static final int CAMERA_MENU_BACKCAMERA = 5;
	
	static final int CAMERA_MENU_SCENE_ID = 9;
	
	static final int MODE_NORMAL_CAMERA = 0;
	static final int MODE_SILENT_CAMERA = 1;
	static final int MODE_FILTER_CAMERA = 2;
	static final public int MODE_FACING_CAMERA = 99;
	
	static final public int MODE_OPENMENU_CLOSE = 0;
	static final public int MODE_OPENMENU_OPTION = 1;
	static final public int MODE_OPENMENU_FOCUS = 2;
	static final public int MODE_OPENMENU_FLASH = 3;
	static final public int MODE_OPENMENU_FILTER = 4;
	static final public int MODE_OPENMENU_ZOOM = 5;
	static final public int MODE_OPENMENU_TIMER = 6;
	
	static final int MODE_RESOLUTION = 2;
	static final int MODE_LENGTH = 3;
	static final int MODE_BRIGHT = 4;
	static final int MODE_FILTER = 5;
	
	static final public int GALLERY_PREVIEW_PREV = 0;
	static final public int GALLERY_PREVIEW_CURRENT = 1;
	static final public int GALLERY_PREVIEW_NEXT = 2;
	
	static final int saveBackPictureSize = 6;
	static final int saveFocusMode = 7;
	static final int saveFlashMode = 8;
	static final int saveFrontPictureSize = 9;
	
	static final int galleryRequestCode = 1;
	
	static final String cPath = "/cherrycamera";
	static final String cThumPath = "/.thumb";

	static final float VOL_LOW = 0.2f;
	static final float VOL_MED = 0.5f;
	static final float VOL_HIGH = 0.8f;
	static final long ViberateTime = 100;
	static final long ShootViberateTime = 30;
	static final int menuframeTitleY = 7;

	static final int TOUCH_NONE = 100;
	static final int TOUCH_DNARROW = 101;
	static final int TOUCH_UPARROW = 102;
	static final int TOUCH_MOVE = 103;
	static final int TOUCH_EXITPREVIEW = 104;
	static final int TOUCH_ERASE = 105;
	static final int TOUCH_SHARE = 106;
	static final int TOUCH_SAVE = 107;

	public static final int LAUNCHED_CAM = 1123;

	public static final long LOW_RAM = 3500000;
	
	static final public String CAMERA_FACING = "facing";
	static final public String SCENE_DEFAULT = "default";
	static final public String SCENE_AUTO = "auto";
	static final public String SCENE_ACTION = "action";
	static final public String SCENE_BARCODE = "barcode";
	static final public String SCENE_BEACH = "beach";
	static final public String SCENE_CANDLELIGHT = "candlelight";
	static final public String SCENE_FIREWORKS = "fireworks";
	static final public String SCENE_HDR = "hdr";
	static final public String SCENE_LANDSCAPE = "landscape";
	static final public String SCENE_NIGHT = "night";
	static final public String SCENE_NIGHTPORTRAIT = "night-portrait";
	static final public String SCENE_PARTY = "party";
	static final public String SCENE_PORTRAIT = "portrait";
	static final public String SCENE_SNOW = "snow";
	static final public String SCENE_SPORTS = "sports";
	static final public String SCENE_STEADYPHOTO = "steadyphoto";
	static final public String SCENE_SUNSET = "sunset";	
	static final public String SCENE_THEATRE = "theatre";
	static final public String SCENE_FLOWER = "flower";
		
	static final public String EFFECT_AQUA = "aqua";
	static final public String EFFECT_BLACKBOARD = "blackboard";
	static final public String EFFECT_MONO = "mono";
	static final public String EFFECT_NEGATIVE = "negative";
	static final public String EFFECT_NONE = "none";
	static final public String EFFECT_POSTERIZE = "posterize";
	static final public String EFFECT_SEPIA = "sepia";
	static final public String EFFECT_SOLARIZE = "solarize";
	static final public String EFFECT_WHITEBOARD = "whiteboard";
	
	public static final String[] menuScnString = {
		SCENE_AUTO, CAMERA_FACING,  SCENE_LANDSCAPE, SCENE_BARCODE,
		SCENE_PORTRAIT, SCENE_NIGHT, SCENE_PARTY, SCENE_SPORTS, 
		SCENE_SUNSET, SCENE_FLOWER, SCENE_BEACH, SCENE_HDR,	
    };
	
	public static final int[] iconImageId = {
		R.drawable.icon_auto, R.drawable.icon_facing, R.drawable.icon_landscape, R.drawable.icon_barcode,
		R.drawable.icon_portrait, R.drawable.icon_night, R.drawable.icon_party, R.drawable.icon_sports, 
		R.drawable.icon_sunset, R.drawable.icon_flowers, R.drawable.icon_beach, R.drawable.icon_hdr, 
	};
	
	public static final int[] menuScnStringID = {
    	R.string.scene_auto, R.string.front_facing, R.string.scene_landscape, R.string.scene_barcode,
    	R.string.scene_portrait, R.string.scene_night, R.string.scene_party, R.string.scene_sports, 
    	R.string.scene_sunset,  R.string.scene_flowers, R.string.scene_beach, R.string.scene_hdr, 
    };
	
	public static int GPS_ONOFF = 1;
	public static int GPS_CHECK = 2;
}
