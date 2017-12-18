package com.epriest.cherryCamera.main;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.PreviewCallback;
import android.media.ExifInterface;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.epriest.cherryCamera.ApplicationClass;
import com.epriest.cherryCamera.R;
import com.epriest.cherryCamera.util.IN;
import com.epriest.cherryCamera.util.ccCamUtil;
import com.epriest.cherryCamera.util.ccPicUtil;
import com.epriest.cherryCamera.util.ccUtil2;
import com.epriest.cherryCamera.util.logline;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.Policy;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Camera set Preview on SurfaceView
 */
public class cCameraPreview extends SurfaceView implements SurfaceHolder.Callback {
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

    public DrawView drawView;

    ApplicationClass appClass;

    public int previewW, previewH;


    public cCameraPreview(Context context, String sceneName, ApplicationClass appClass) {
        super(context);
        logline.d(TAG, "cCameraPreview()");
        this.appClass = appClass;
        this.sceneName = sceneName;

        pd = new ProgressDialog(appClass.getActivity());
//		LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE); 
//		cameraMainView = inflater.inflate(R.layout.main_camera, null);
        mHolder = getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        drawView = new DrawView(context);
    }

    public void setPreviewW(int previewW) {
        this.previewW = previewW;
    }

    public int getPreviewW() {
        return previewW;
    }

    public void setPreviewH(int previewH) {
        this.previewH = previewH;
    }

    public int getPreviewH() {
        return previewH;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        logline.d(TAG, "Camera Surface Created");
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
        logline.d(TAG, "Camera Surface Changed");

        if (mHolder.getSurface() == null) {
            // preview surface does not exist
            return;
        }

        // stop preview before making changes
//        try {
//            mCamera.stopPreview();
//        } catch (Exception e){
        // ignore: tried to stop a non-existent preview
//        	Log.d(TAG, "Error stop camera preview: " + e.getMessage());
//        }

        // set preview size and make any resize, rotate or
        // reformatting changes here 
//        ccCamUtil.setCameraDisplayOrientation(camera_id, mCamera,appClass);
        setParam();
    }

    public List<Camera.Area> mFocusArea;

    public void takeAutoFocusArea(View view, float posX, float posY, Point point) {
        setFocusImage(view, posX - view.getWidth() / 2, posY - view.getHeight() / 2);
        setAutoFocusArea(mCamera, (int) posX, (int) posY, 120, point);
        drawView.invalidate();
//        setAutoFocusArea(mCamera, (int) posX, (int) posY, 120, point);
    }

    public void setFocusImage(View view, float posX, float posY) {
        view.setVisibility(VISIBLE);
        view.setX(posX);
        view.setY(posY);
    }

    private void setAutoFocusArea(Camera camera, int posX, int posY,
                                  int focusRange, Point point) {
        //FocusArea는 ICS에서 지원
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH)
            return;

        if (posX < 0 || posY < 0) {
            setArea(camera, null);
            return;
        }

        int touchPointX = point.x >> 1;
        int touchPointY = point.y >> 1;

        if(posX < touchPointX){
            posX = (posX*100) / touchPointX * -1000;
        }else{
            posX = ((posX-touchPointX)*100) / touchPointX * 1000;
        }

        if(posY < touchPointY){
            posY = (posY*100) / touchPointY * -1000;
        }else{
            posY = ((posY-touchPointY)*100) / touchPointY * 1000;
        }

        posX /= 100;
        posY /= 100;


        int top = posY - focusRange;
        int bottom = posY + focusRange;
        int left = posX - focusRange;
        int right = posX + focusRange;

        if (left < -1000)
            left = -1000;
        if (top < -1000)
            top = -1000;
        if (right > 1000)
            right = 1000;
        if (bottom > 1000)
            bottom = 1000;

        Rect rect = new Rect(left, top, right, bottom);
        ArrayList<Camera.Area> arraylist = new ArrayList<Camera.Area>();
        arraylist.add(new Camera.Area(rect, 1000)); // 지정된 영역을 100%의 가중치를 두겠다는 의미입니다.

        setArea(camera, arraylist);
    }

    private void setAutoFocusArea(Camera camera, int posX, int posY,
                                  int focusRange, boolean flag, Point point) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            /** 영역을 지정해서 포커싱을 맞추는 기능은 ICS 이상 버전에서만 지원됩니다.  **/
            return;
        }

        if (posX < 0 || posY < 0) {
            setArea(camera, null);
            return;
        }

        int touchPointX;
        int touchPointY;
        int endFocusY;
        int startFocusY;

        if (!flag) {
            /** Camera.setDisplayOrientation()을 이용해서 영상을 세로로 보고 있는 경우. **/
            touchPointX = point.y >> 1;
            touchPointY = point.x >> 1;

            startFocusY = posX;
            endFocusY   = posY;
        } else {
            /** Camera.setDisplayOrientation()을 이용해서 영상을 가로로 보고 있는 경우. **/
            touchPointX = point.x >> 1;
            touchPointY = point.y >> 1;

            startFocusY = posY;
            endFocusY = point.x - posX;
        }

        float startFocusX   = 1000F / (float) touchPointY;
        float endFocusX     = 1000F / (float) touchPointX;

        /** 터치된 위치를 기준으로 focusing 영역으로 사용될 영역을 구한다. **/
        startFocusX = (int) (startFocusX * (float) (startFocusY - touchPointY)) - focusRange;
        startFocusY = (int) (endFocusX * (float) (endFocusY - touchPointX)) - focusRange;
        endFocusX = startFocusX + focusRange;
        endFocusY = startFocusY + focusRange;

        if (startFocusX < -1000)
            startFocusX = -1000;

        if (startFocusY < -1000)
            startFocusY = -1000;

        if (endFocusX > 1000) {
            endFocusX = 1000;
        }

        if (endFocusY > 1000) {
            endFocusY = 1000;
        }

    /*
     * 주의 : Android Developer 예제 소스 처럼 ArrayList에 Camera.Area를 2개 이상 넣게 되면
     *          에러가 발생되는 것을 경험할 수 있을 것입니다.
     **/
        Rect rect = new Rect((int) startFocusX, (int) startFocusY, (int) endFocusX, (int) endFocusY);
        ArrayList<Camera.Area> arraylist = new ArrayList<Camera.Area>();
        arraylist.add(new Camera.Area(rect, 1000)); // 지정된 영역을 100%의 가중치를 두겠다는 의미입니다.

        setArea(camera, arraylist);
    }

    private void setArea(Camera camera, List<Camera.Area> list) {
        boolean enableFocusModeMacro = true;

        Camera.Parameters parameters;
        parameters = camera.getParameters();

        int maxNumFocusAreas = parameters.getMaxNumFocusAreas();
        int maxNumMeteringAreas = parameters.getMaxNumMeteringAreas();

        if (maxNumFocusAreas > 0) {
            parameters.setFocusAreas(list);
        }

        if (maxNumMeteringAreas > 0) {
            parameters.setMeteringAreas(list);
        }

        if (list == null || maxNumFocusAreas < 1 || maxNumMeteringAreas < 1) {
            enableFocusModeMacro = false;
        }

        if (enableFocusModeMacro == true) {
        /*
         * FOCUS_MODE_MACRO을 사용하여 근접 촬영이 가능하도록 해야
         * 지정된 Focus 영역으로 초점이 좀더 선명하게 잡히는 것을 볼 수 있습니다.
         */
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_MACRO);
            logline.d(TAG, "focus mode macro");
        } else {
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
            logline.d(TAG, "focus mode auto");
        }
        camera.setParameters(parameters);
    }

    public void setParam() {
        Parameters params = null;
        try {
            params = mCamera.getParameters();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (params == null) {
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

        appClass.mParameter.previewSize(params, appClass.lcdWidth, appClass.lcdHeight);
        appClass.mParameter.whiteBalance(params);
        appClass.mParameter.supportedAntibanding(params);
        appClass.mParameter.colorEffect(params);
        appClass.mParameter.scene(params, sceneName);
        appClass.mParameter.supportedFocusModes(params);
        appClass.mParameter.supportedFlashModes(params);
        appClass.mParameter.pictureSize(params);
        appClass.expoMaxSize = params.getMaxExposureCompensation();
//        appClass.mParameter.areaFocus(params);
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
        } catch (Exception e) {
            Log.d(TAG, "Error starting camera preview: " + e.getMessage());
        }
        ccCamUtil.refreshCameraInfo(appClass);
        // Camera Button Setting      
        if (!isButtonSetting) {
            appClass.mMenuset.optionSet();
            appClass.mMenuset.btnSet();
            isButtonSetting = true;
        }
        ccCamUtil.refreshMemory(appClass);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // empty. Take care of releasing the Camera preview in your activity.
        Log.d(TAG, "Camera Surface Destroyed");
        releaseCamera();
    }

    public void connectCamera() {
        logline.d(TAG, "*connectCamera");
        if (mCamera == null) {
            mCamera = ccCamUtil.getCameraInstance(appClass.cameraFacingMode, appClass.mMenuset.frontCameraAccess);
        }
        try {
            mCamera.setPreviewDisplay(mHolder);
//            mCamera.startPreview();
        } catch (Exception e) {
            Log.d(TAG, "Error starting camera preview: " + e.getMessage());
        }
    }

    public void releaseCamera() {
        logline.d(TAG, "*releaseCamera");
        try {
            mCamera.stopPreview();
        } catch (Exception e) {
            // ignore: tried to stop a non-existent preview
            Log.d(TAG, "Error stop camera preview: " + e.getMessage());
        }
        if (mCamera != null) {
            mCamera.release();        // release the camera for other applications
            mCamera = null;
        }
    }

    AutoFocusCallback mAutoFocusCallback = new AutoFocusCallback() {
        @Override
        public void onAutoFocus(boolean arg0, Camera arg1) {
            logline.d(TAG, "autofocus");
//            final ImageView focusAim = (ImageView) appClass.getActivity().findViewById(R.id.camera_preview_focus);
//            focusAim.setImageResource(R.drawable.aim3);
            capture();
        }

    };

    AutoFocusCallback mFocusCallback = new AutoFocusCallback() {
        @Override
        public void onAutoFocus(boolean arg0, Camera arg1) {
            isSetFocus = true;
            final Button captureButton = (Button) appClass.getActivity().findViewById(R.id.btn_camera_shutter);
            final ImageView focusAim = (ImageView) appClass.getActivity().findViewById(R.id.camera_preview_focus);
            ImageView ivCameraShutter = (ImageView) appClass.getActivity().findViewById(R.id.img_camera_shutter_icon);
            ivCameraShutter.setImageResource(R.drawable.shutter_);
            captureButton.setBackgroundResource(R.drawable.shutter_focus);

            /*focusAim.setImageResource(R.drawable.focusarea);
            List<Camera.Area> arealist = arg1.getParameters().getFocusAreas();
            if (arealist.size() > 0) {
                int l = arealist.get(0).rect.left;
                int t = arealist.get(0).rect.top;
                int r = arealist.get(0).rect.right;
                int b = arealist.get(0).rect.bottom;
                int left	= (l+1000) * appClass.lcdWidth/2000;
                int top		= (t+1000) * appClass.lcdHeight/2000;
                int right	= (r+1000) * appClass.lcdWidth/2000;
                int bottom	= (b+1000) * appClass.lcdHeight/2000;
//                int weight = arealist.get(0).weight;
//                int lcdDivideW = (appClass.lcdWidth * 1000) / (weight * 2);
//                int lcdDivideH = (appClass.lcdHeight * 1000) / (weight * 2);
//                int top = (lcdDivideH * (arealist.get(0).rect.top + weight)) / 1000;
//                int bottom = (lcdDivideH * (arealist.get(0).rect.bottom + weight)) / 1000;
//                int right = (lcdDivideW * (arealist.get(0).rect.right + weight)) / 1000;
//                int left = (lcdDivideW * (arealist.get(0).rect.left + weight)) / 1000;
////                int x = (lcdDivideW * (arealist.get(0).rect.centerX()+weight))/100;
////                int y = (lcdDivideH * (arealist.get(0).rect.centerY()+weight))/100;
//                ViewGroup.LayoutParams params = (ViewGroup.LayoutParams) focusAim.getLayoutParams();
//                params.width = right - left;
//                params.height = bottom - top;
//                focusAim.setLayoutParams(params);
                logline.d("" + arealist.get(0).rect.centerX() + "," + arealist.get(0).rect.centerY());
                int x = ((appClass.lcdWidth * 1000) / 2000 * (arealist.get(0).rect.centerX() + 1000)) / 1000;
                int y = ((appClass.lcdHeight * 1000) / 2000 * (arealist.get(0).rect.centerY() + 1000)) / 1000;
                drawView.setArea(left, top, right, bottom);
                drawView.setHaveArea(true);
            }else{
                drawView.setHaveArea(false);
            }
            drawView.invalidate();*/
        }
    };

    private class DrawView extends View{
        boolean haveArea;
        boolean haveTouch;
        Paint drawingPaint;
        int l;
        int t;
        int r;
        int b;

        public DrawView(Context context) {
            super(context);
            haveArea = false;
            haveTouch = false;
            drawingPaint = new Paint();
            drawingPaint.setColor(Color.GREEN);
            drawingPaint.setStyle(Paint.Style.STROKE);
            drawingPaint.setStrokeWidth(2);
        }

        public void setHaveArea(boolean h){
            haveArea = h;
        }

        public void setHaveTouch(boolean t, Rect tArea){
            haveTouch = t;
//            touchArea = tArea;
        }

        public void setArea(int left, int top, int right, int bottom){
            this.l = left;
            this.t = top;
            this.r = right;
            this.b = bottom;
        }

        @Override
        protected void onDraw(Canvas canvas) {
            if(haveArea){

                // Camera driver coordinates range from (-1000, -1000) to (1000, 1000).
                // UI coordinates range from (0, 0) to (width, height).

                int vWidth = getWidth();
                int vHeight = getHeight();

                /*for(int i=0; i<detectedFaces.length; i++){

                    if(i == 0){
                        drawingPaint.setColor(Color.GREEN);
                    }else{
                        drawingPaint.setColor(Color.RED);
                    }*/

                    int left	= (l+1000) * vWidth/2000;
                    int top		= (t+1000) * vHeight/2000;
                    int right	= (r+1000) * vWidth/2000;
                    int bottom	= (b+1000) * vHeight/2000;
                    canvas.drawRect(
                            left, top, right, bottom,
                            drawingPaint);
//                }
            }else{
                canvas.drawColor(Color.TRANSPARENT);
            }

//            if(haveTouch){
//                drawingPaint.setColor(Color.BLUE);
//                canvas.drawRect(
//                        touchArea.left, touchArea.top, touchArea.right, touchArea.bottom,
//                        drawingPaint);
//            }
        }


    }

    private void pDialogSet(String message, boolean isCancel) {
        if (pd.isShowing())
            pd.dismiss();
        pd = new ProgressDialog(appClass.getActivity());
        pd.setMessage(message);
        pd.setCancelable(false);
        if (isCancel) {
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

    public void shootOn() {
        pDialogSet(appClass.getResources().getString(R.string.upload_message), true);
        appClass.mPreview.iShutter = true;
        if (isSetFocus) {
            capture();
            isSetFocus = false;
        } else {
            logline.d(TAG, "AutoFocus , " + mCamera.getParameters().getFocusMode());
            if (appClass.ListFocus == null)
                capture();
            else if (mCamera.getParameters().getFocusMode().equals(Parameters.FOCUS_MODE_FIXED))
                capture();
            else {
                ImageView focusAim = (ImageView) appClass.getActivity().findViewById(R.id.camera_preview_focus);
                focusAim.setImageResource(R.drawable.aim2);
                mCamera.autoFocus(mAutoFocusCallback);
            }
        }
//		mPreview.iShutter = true;
    }

    public boolean capture() {
        if (mCamera != null && !isTakingPic) {
            isTakingPic = true;
            if (appClass.isCherryFilter != 0) {
//				mCamera.setPreviewCallback(mPreview);
                mCamera.setOneShotPreviewCallback(mPreview);
            } else {
                mCamera.takePicture(null, null, mPicture);
            }

            return true;
        }
        return false;
    }

    public PreviewCallback mPreview = new PreviewCallback() {
        @Override
        public void onPreviewFrame(final byte[] data, Camera camera) {
            final int pictureOrientation = appClass.orientation;
            ;
            pDialogSet(appClass.getResources().getString(R.string.upload_save_message), true);

//			new Thread(new Runnable(){
//				@Override
//				public void run() {
            camera.setPreviewCallback(null);
            camera.stopPreview();
            int[] pickuppedColorData = new int[data.length];
            int width = camera.getParameters().getPreviewSize().width;
            int height = camera.getParameters().getPreviewSize().height;
            ccPicUtil.decodeYUV(data, pickuppedColorData, width, height);

            Bitmap pickuppedBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            pickuppedBitmap.setPixels(pickuppedColorData, 0, width, 0, 0, width, height);

            Matrix m = new Matrix();
            if (appClass.mMenuset.frontCameraAccess) {
                float[] mirrorY = {-1, 0, 0, 0, 1, 0, 0, 0, 1};
                Matrix matrixMirrorY = new Matrix();
                matrixMirrorY.setValues(mirrorY);
                m.postConcat(matrixMirrorY);
            }

            int px = appClass.mPreview.previewW - appClass.lcdWidth;
            if (px < 0)
                px = 0;
            int py = appClass.mPreview.previewH - appClass.lcdHeight;
            if (py < 0)
                py = 0;
            Bitmap bitmap = Bitmap.createBitmap(pickuppedBitmap,
                    px / 2, py / 2, appClass.mPreview.previewW - px, appClass.mPreview.previewH - py, m, true);
            if (pickuppedBitmap != null) {
                try {
                    pickuppedBitmap.recycle();
                } catch (Exception e) {
                    logline.d(TAG, "bitmap Null Exception : " + e);
                }
            }
            if (previewBitmap != null) {
                try {
                    previewBitmap.recycle();
                    previewBitmap = null;
                } catch (Exception e) {
                    logline.d(TAG, "bitmap Null Exception : " + e);
                }
            }

            ImageView iv = (ImageView) appClass.getActivity().findViewById(R.id.camera_filter_save);
            iv.setVisibility(View.VISIBLE);
            iv.setBackgroundDrawable(new BitmapDrawable(getResources(), bitmap));
            iv.setDrawingCacheEnabled(true);
            logline.d(TAG, "" + iv.getWidth() + "," + iv.getHeight() + "..." + bitmap.getWidth() + "," + bitmap.getHeight());
            previewBitmap = Bitmap.createBitmap(iv.getDrawingCache(true));
            iv.setDrawingCacheEnabled(false);
            iv.setBackgroundDrawable(null);
            iv.setVisibility(View.INVISIBLE);

            float degrees = ccCamUtil.setOrientationToDegree(pictureOrientation);

            m.reset();
            m.setRotate(degrees,
                    (float) width / 2, (float) height / 2);

            bitmap = Bitmap.createBitmap(previewBitmap,
                    0, 0, previewBitmap.getWidth(), previewBitmap.getHeight(), m, true);
            if (previewBitmap != null) {
                try {
                    previewBitmap.recycle();
                    previewBitmap = null;
                } catch (Exception e) {
                    logline.d(TAG, "bitmap Null Exception : " + e);
                }
            }
            File pictureFile = ccCamUtil.getOutputMediaFile(IN.MEDIA_TYPE_IMAGE);
            if (pictureFile != null) {
                try {
                    FileOutputStream fos = null;
                    fos = new FileOutputStream(pictureFile);
                    bitmap.compress(CompressFormat.JPEG, 90, fos);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } finally {
                    if (bitmap != null) {
                        bitmap.recycle();
                    }
                }
            }

//					 if(pictureFile != null){
            addMediaScanner(pictureFile);
            Parameters params = appClass.mParameter.getParameters();
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

            new Thread(new Runnable() {
                @Override
                public void run() {
                    File pictureFile = ccCamUtil.getOutputMediaFile(IN.MEDIA_TYPE_IMAGE);

                    if (pictureFile == null) {
                        logline.d(TAG, "Error creating media file, check storage permissions: ");
                        return;
                    }

                    if (previewBitmap != null) {
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
    private void addMediaScanner(final File file) {
        if (file == null) {
            setPreviewPicture(null);
            mHandler.sendEmptyMessage(20);
            return;
        }
        MediaScannerConnection.scanFile(appClass,
                new String[]{file.getAbsolutePath()}, null,
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

    private File setSaveFile(byte[] data, File file) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            if (!file.exists()) {
                file.createNewFile();
            }
            fos.write(data);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
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
        try {
            BitmapFactory.Options opt = new BitmapFactory.Options();
            opt.inSampleSize = 0;
//			opt.inDither = true;
//			opt.inPreferredConfig = Bitmap.Config.RGB_565;
            bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
        } catch (Exception e) {
//			Log.d(TAG,"previewBitmap decode Exception : "+e);
            return null;
        }
        if (bitmap == null) return null;
        Matrix m = new Matrix();
//		m.setRotate(degrees,
//				(float) bitmap.getWidth()/2, (float) bitmap.getHeight()/2);
        if (cameraFacingMode) {
            if (pictureOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
//				m.setRotate(270,
//						(float) bitmap.getWidth()/2, (float) bitmap.getHeight()/2);
            }
            float[] mirrorY = {-1, 0, 0, 0, 1, 0, 0, 0, 1};
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
            } finally {
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
            } finally {
                if (bitmap != null) {
                    bitmap.recycle();
                    bitmap = null;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
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

    private void setPreviewPicture(String path) {
        if (previewBitmap != null) {
            previewBitmap.recycle();
            previewBitmap = null;
        }
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 8;
            previewBitmap = BitmapFactory.decodeFile(path, options);
        } catch (Exception e) {
            logline.d(TAG, "previewBitmap decode Exception : " + e);
        }
    }

    public void previewImageChange(String gallerySelectPath) {
        logline.d(TAG, "previewImageChange pictureData =" + gallerySelectPath);
        ImageView previewImg = (ImageView) appClass.getActivity().findViewById(R.id.img_photo_gallery);

        if (gallerySelectPath != null) {
            Bitmap bm = getDrawabletoPath(gallerySelectPath);
            previewImg.setImageBitmap(bm);
        }

        if (previewBitmap != null) {
            try {
                previewBitmap.recycle();
                previewBitmap = null;
            } catch (Exception e) {
                logline.d(TAG, "previewBitmap Null Exception : " + e);
            }
        }

        if (pd.isShowing()) {
            pd.dismiss();
        }

        isTakingPic = false;
        ccCamUtil.refreshMemory(appClass);
        iShutter = false;
        final Button captureButton = (Button) appClass.getActivity().findViewById(R.id.btn_camera_shutter);
        captureButton.setBackgroundResource(R.drawable.shutter_normal);
        final ImageView focusAim = (ImageView) appClass.getActivity().findViewById(R.id.camera_preview_focus);
        focusAim.setImageResource(R.drawable.aim1);
        ImageView ivCameraShutter = (ImageView) appClass.getActivity().findViewById(R.id.img_camera_shutter_icon);
        ivCameraShutter.setImageResource(R.drawable.shutter);

        if (appClass.isOutOfMem) {
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

    public Bitmap getDrawabletoPath(String fileName) {
        int thumViewSize = 150;
        Bitmap bitmapPicture = null;
        Bitmap srcBitmap = null;

        ccPicUtil.BitmapInfo bitinfo = ccPicUtil.getBitmapSize(fileName);
        float sizeValue = bitinfo.width / bitinfo.height;

        try {
            int scale;
            if (sizeValue >= 1)
                scale = bitinfo.width / thumViewSize;
            else
                scale = bitinfo.height / thumViewSize;

            BitmapFactory.Options options = new BitmapFactory.Options();
            if (scale >= 8) {
                options.inSampleSize = 8;
            } else if (scale >= 4) {
                options.inSampleSize = 4;
            } else if (scale >= 2) {
                options.inSampleSize = 2;
            } else {
                options.inSampleSize = 1;
            }

            bitmapPicture = BitmapFactory.decodeFile(fileName, options);
            srcBitmap = Bitmap.createScaledBitmap(bitmapPicture, thumViewSize, thumViewSize, true);
            return srcBitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                default:
                    logline.d("=====mHandler 5=====");
                    try {
                        mCamera.startPreview();
                    } catch (Exception e) {
                    }
                    previewImageChange((String) msg.obj);
                    break;
                case 10:
                    logline.d("=====mHandler 10=====");
                    if (pd.isShowing()) {
                        pd.dismiss();
                    }
                    appClass.getActivity().finish();
                    break;
                case 20:
                    logline.d("=====mHandler default=====");
                    if (pd.isShowing()) {
                        pd.dismiss();
                    }
                    break;
            }
            super.handleMessage(msg);
        }
    };


}
