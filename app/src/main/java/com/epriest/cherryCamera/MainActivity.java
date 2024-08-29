package com.epriest.cherryCamera;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.epriest.cherryCamera.main.cCameraMenuSet;
import com.epriest.cherryCamera.main.cCameraParameters;
import com.epriest.cherryCamera.main.fragment.FragmentBottom;
import com.epriest.cherryCamera.main.fragment.FragmentLeft;
import com.epriest.cherryCamera.main.fragment.FragmentMain;
import com.epriest.cherryCamera.main.fragment.FragmentUpper;
import com.epriest.cherryCamera.util.IN;
import com.epriest.cherryCamera.util.ccCamUtil;
import com.epriest.cherryCamera.util.logline;

public class MainActivity extends FragmentActivity {

    private String TAG = this.getClass().getSimpleName();
    public ApplicationClass appClass;

    OrientationEventListener myOrientationEventListener;

    private float oldTouchValue;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        logline.d(TAG, "MainActivity onCreate");
        appClass = (ApplicationClass)getApplication();
        appClass.setActivity(this);

        setContentView(R.layout.ex_main_camera);

//        setFragment();

        appClass.fragmentMain = (FragmentMain)getSupportFragmentManager().findFragmentById(R.id.main_fragment);
        appClass.fragmentBottom = (FragmentBottom) getSupportFragmentManager().findFragmentById(R.id.bottom_fragment);
        appClass.fragmentLeft = (FragmentLeft) getSupportFragmentManager().findFragmentById(R.id.left_fragment);
        appClass.fragmentUpper = (FragmentUpper) getSupportFragmentManager().findFragmentById(R.id.upper_fragment);

        appClass.mMenuset = new cCameraMenuSet(appClass);
        appClass.mParameter = new cCameraParameters(appClass);
        appClass.cameraFacingMode = ccCamUtil.checkCameraFacingMode();

        myOrientationEventListener
                = new OrientationEventListener(getBaseContext()){
            @Override
            public void onOrientationChanged(int orientation) {
//				logline.d("Orientation: " + String.valueOf(orientation));

               appClass.fragmentMain.drawPitch(orientation);
                int prevDegree = appClass.orientation;
                appClass.orientation =  ccCamUtil.setDegreeToExifOrientation(orientation);
//		        logline.d(""+appClass.orientation);
                if(prevDegree != appClass.orientation)
                    appClass.fragmentMain.setChangeOrientation();
            }
        };
        myOrientationEventListener.enable();

    }

    private void setFragment(){
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.add(R.id.main_fragment, new FragmentMain());
        fragmentTransaction.commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        logline.d(TAG, "onActivityResult resultCode : "+resultCode );
        if(resultCode == RESULT_OK){
            if(requestCode == IN.galleryRequestCode){
                String picData = data.getExtras().getString("pic_data");
                if(picData.equals("null")){
                    appClass.mPreview.previewImageChange(null);
                }else{
                    appClass.mPreview.previewImageChange(picData);
                }
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent touchevent) {
//		final ImageView iv = (ImageView)findViewById(R.id.helpImage);

        switch (touchevent.getAction()){
            case MotionEvent.ACTION_DOWN:
                oldTouchValue = touchevent.getX();
                break;
            case MotionEvent.ACTION_UP:
//       	if(app.helpFlag){
//   			iv.setVisibility(View.INVISIBLE);
//   			app.helpFlag = false;
//   		}

                //if(this.searchOk==false) return false;
                float currentX = touchevent.getX();
//           if (!app.openOption && oldTouchValue < currentX){
//        	   app.openOption = true;
//        	   app.vf.setInAnimation(ccUtil.inFromLeftAnimation());
//        	   app.vf.setOutAnimation(ccUtil.outToRightAnimation());
//        	   app.vf.showNext();
//           }
                if (appClass.openOption != IN.MODE_OPENMENU_CLOSE && oldTouchValue > currentX){
                    appClass.mMenuset.openOptionClose();
                }else{
                    return true;
			   /*ImageView focusArea = (ImageView)findViewById(R.id.camera_preview_focusarea);
			   appClass.mPreview.takeAutoFocusArea(focusArea, touchevent.getX(), touchevent.getY(), new Point(appClass.lcdWidth, appClass.lcdHeight));
			   if (appClass.mPreview.mFocusArea == null) {
				   appClass.mPreview.mFocusArea = new ArrayList<Area>();
				   appClass.mPreview.mFocusArea.add(new Area(new Rect(), 1));
			   }

			   appClass.mPreview.mCamera.autoFocus(appClass.mPreview.mFocusCallback);*/
                }
                break;
        }
        return true;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        logline.d(TAG, "key = "+keyCode);
        switch(keyCode){
            case KeyEvent.KEYCODE_VOLUME_DOWN:
            case KeyEvent.KEYCODE_VOLUME_UP:
            case KeyEvent.KEYCODE_ENTER:
            case KeyEvent.KEYCODE_CAMERA:
                if(appClass.mPreview.iShutter)
                    return true;
                if(appClass.timerCount > 0){
                    appClass.timerCountMil = appClass.timerCount*1000;
                    TextView tv = (TextView)appClass.getActivity().findViewById(R.id.camera_timer_text);
                    appClass.fragmentMain.timer.setTextView(tv);
                    appClass.fragmentMain.timer.Start();
                }else{
                    appClass.mPreview.shootOn();
                }
                break;
            case KeyEvent.KEYCODE_BACK:
                if(appClass.timerCountMil > 0){
                    appClass.fragmentMain.timer.Stop();
                }else
                if (appClass.openOption != IN.MODE_OPENMENU_CLOSE){
                    appClass.mMenuset.openOptionClose();
                }
//			else if(app.helpFlag){
//				final ImageView iv = (ImageView)findViewById(R.id.helpImage);
//				iv.setVisibility(ImageView.INVISIBLE);
//				app.helpFlag = false;
//			}
                else{
//				app.cameraFinish(intentGetAction, mPreview.pictureFile);
                    finish();
                }
                break;
        }
        return true;
    }

    //볼륨키 작동 금지
    /*@Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if(event.getAction() == KeyEvent.ACTION_UP){
            logline.d(TAG,"===ACTION_UP===");
        }else{
            logline.d(TAG,"===ACTION_DN===");
            switch(event.getKeyCode()){
                case KeyEvent.KEYCODE_VOLUME_UP:
                    super.onKeyDown(KeyEvent.KEYCODE_DPAD_UP, new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DPAD_UP));
                    return true;
                case KeyEvent.KEYCODE_VOLUME_DOWN:
                    super.onKeyDown(KeyEvent.KEYCODE_DPAD_DOWN, new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DPAD_UP));
                    return true;
                case KeyEvent.KEYCODE_CAMERA:
                case KeyEvent.KEYCODE_ENTER:
                    return true;
            }
        }
        return super.dispatchKeyEvent(event);
    }*/

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

}
