package com.epriest.cherryCamera.main.fragment;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.hardware.Camera;
import android.media.ExifInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.epriest.cherryCamera.ApplicationClass;
import com.epriest.cherryCamera.R;
import com.epriest.cherryCamera.main.cCameraPreview;
import com.epriest.cherryCamera.util.IN;
import com.epriest.cherryCamera.util.ccCamUtil;
import com.epriest.cherryCamera.util.ccPicUtil;
import com.epriest.cherryCamera.util.logline;

public class FragmentMain extends Fragment implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    private String TAG = this.getClass().getSimpleName();
    private ApplicationClass appClass;

    PorterDuffColorFilter mColorFilter;
    ImageView ivPitch;
    ImageView ivPitchOn;

    public MyCountDownTimer timer;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        logline.d(TAG, "FragmentMain onCreate");
        appClass = (ApplicationClass)getActivity().getApplication();
        appClass.setActivity(getActivity());
        return inflater.inflate(R.layout.camera_main, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Create our Preview view and set it as the content of our activity.
        FrameLayout preview = getActivity().findViewById(R.id.camera_preview);
        appClass.mPreview = new cCameraPreview(getContext(), appClass);
        preview.addView(appClass.mPreview);

        mColorFilter = new PorterDuffColorFilter(Color.GREEN, PorterDuff.Mode.SRC_ATOP);
        ivPitch = getActivity().findViewById(R.id.camera_preview_pitch);
        ivPitchOn = getActivity().findViewById(R.id.camera_preview_pitch_on);

        timer = new MyCountDownTimer(1000);

        appClass.getActivity().findViewById(R.id.btn_camera_shutter).setOnClickListener(this);
        appClass.getActivity().findViewById(R.id.camera_preview_pitch_on).setOnClickListener(this);
    }

    public void drawPitch(float angle) {
        int horizonAng = (int) Math.abs(angle % 90);
//		logline.d("angle = "+angle);
        if (horizonAng < 3 || horizonAng > 87)
            ivPitchOn.setColorFilter(mColorFilter);
        else
            ivPitchOn.setColorFilter(null);

        Matrix matrix = new Matrix();
        ivPitch.setScaleType(ImageView.ScaleType.MATRIX);   //required
        matrix.postRotate(270 - angle, ivPitch.getWidth() / 2, ivPitch.getHeight() / 2);
        ivPitch.setImageMatrix(matrix);
    }

    public void setChangeOrientation() {
//        ImageView ivGallery = (ImageView)findViewById(R.id.img_photo_gallery);
//        Button ivCameraMemory = (Button)findViewById(R.id.btn_camera_freememory);
//        Button ivCameraBattery = (Button)findViewById(R.id.btn_camera_bettery);
//        ImageButton ivCameraScene = (ImageButton)findViewById(R.id.btn_camera_scene);
//		ImageView ivCameraFlash = (ImageView)findViewById(R.id.icon_camera_flash);
        ImageView ivCameraShutter = getActivity().findViewById(R.id.img_camera_shutter_icon);
        ImageView ivCameraFocus = getActivity().findViewById(R.id.icon_camera_focus);
        TextView ivCameraExpo = getActivity().findViewById(R.id.text_camera_exposure);
        TextView ivCountTimer = getActivity().findViewById(R.id.camera_timer_text);

        switch (appClass.orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
            case ExifInterface.ORIENTATION_ROTATE_270:
                ivCountTimer.startAnimation(ccPicUtil.rotateAnimation(getContext(), 0, -90, 0, 0));
                ivCameraShutter.startAnimation(ccPicUtil.rotateAnimation(getContext(), 0, -90, 0, 0));
                ivCameraFocus.startAnimation(ccPicUtil.rotateAnimation(getContext(), 0, -90, 0, 0));
                ivCameraExpo.startAnimation(ccPicUtil.rotateAnimation(getContext(), 0, -90, 0, 0));

//                ivGallery.startAnimation(ccPicUtil.rotateAnimation(getContext(), 0, -90, 0, 0));
//                ivCameraScene.startAnimation(ccPicUtil.rotateAnimation(getContext(), 0, -90, 0, 0));
//                ivCameraMemory.startAnimation(ccPicUtil.rotateAnimation(getContext(), 0, -90, 0, 0));
//                ivCameraBattery.startAnimation(ccPicUtil.rotateAnimation(getContext(), 0, -90, 0, 0));
//			ivCameraFlash.startAnimation(ccPicUtil.rotateAnimation(this, 0, -90, 0, 0));

                break;
            case ExifInterface.ORIENTATION_NORMAL:
            case ExifInterface.ORIENTATION_ROTATE_180:
                ivCountTimer.startAnimation(ccPicUtil.rotateAnimation(getContext(), -90, 0, 0, 0));
                ivCameraFocus.startAnimation(ccPicUtil.rotateAnimation(getContext(), -90, 0, 0, 0));
                ivCameraExpo.startAnimation(ccPicUtil.rotateAnimation(getContext(), -90, 0, 0, 0));
                ivCameraShutter.startAnimation(ccPicUtil.rotateAnimation(getContext(), -90, 0, 0, 0));
//                ivGallery.startAnimation(ccPicUtil.rotateAnimation(getContext(), -90, 0, 0, 0));
//                ivCameraScene.startAnimation(ccPicUtil.rotateAnimation(getContext(), -90, 0, 0, 0));
//                ivCameraMemory.startAnimation(ccPicUtil.rotateAnimation(getContext(), -90, 0, 0, 0));
//                ivCameraBattery.startAnimation(ccPicUtil.rotateAnimation(getContext(), -90, 0, 0, 0));
//			ivCameraFlash.startAnimation(ccPicUtil.rotateAnimation(this, -90, 0, 0, 0));

                break;
        }
    }

    public class MyCountDownTimer {
        private long countDownInterval;
        private TextView TimetTextView;
        private int Count;

        public MyCountDownTimer(long pCountDownInterval) {
//	            this.millisInFuture = pMillisInFuture;
            this.countDownInterval = pCountDownInterval;
        }

        public void setTextView(TextView tv) {
            TimetTextView = tv;
        }

        public void setCouncNum(int num) {
            Count = num;
        }

        public void Stop() {
            appClass.timerCountMil = -1;
        }

        public void Start() {
            final Handler handler = new Handler();
            Log.v("status", "starting");
            final Runnable counter = new Runnable() {

                public void run() {
                    if (appClass.timerCountMil == -1) {
                        TimetTextView.setVisibility(View.INVISIBLE);
                    } else if (appClass.timerCountMil <= 0) {
                        TimetTextView.setVisibility(View.INVISIBLE);
                        if (appClass.isTimerBeep)
                            ccCamUtil.playSound(appClass.getApplicationContext(), IN.VOL_MED, 2);

                        appClass.mPreview.shootOn();
                    } else {
                        if (appClass.isTimerBeep)
                            ccCamUtil.playSound(appClass.getApplicationContext(), IN.VOL_MED, 0);
                        if (appClass.timerCountMil > 0) {

                            TimetTextView.setText(Integer.toString(appClass.timerCountMil / 1000));
                            TimetTextView.setVisibility(View.VISIBLE);
                        }
                        appClass.timerCountMil -= countDownInterval;
                        handler.postDelayed(this, countDownInterval);
                    }
                }
            };

            handler.postDelayed(counter, countDownInterval);
        }
    }

    @Override
    public void onClick(View view) {
        Camera.Parameters params = appClass.mParameter.getParameters();

        ImageView focusAim = getActivity().findViewById(R.id.camera_preview_focus);
        Button captureButton = getActivity().findViewById(R.id.btn_camera_shutter);
        if (appClass.mPreview.iShutter)
            return;
        final Vibrator vibe = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
        vibe.vibrate(IN.ViberateTime_Easy);
        switch (view.getId()) {
            case R.id.btn_camera_shutter:
                if (appClass.timerCount > 0) {
                    appClass.timerCountMil = appClass.timerCount * 1000;

                    TextView tv = getActivity().findViewById(R.id.camera_timer_text);
                    timer.setTextView(tv);
                    timer.Start();
                } else {
                    appClass.mPreview.shootOn();
                }
                break;
            case R.id.camera_preview_pitch_on:
                if (appClass.mPreview.isSetFocus) {
                    appClass.mPreview.isSetFocus = false;
                    focusAim.setImageResource(R.drawable.aim1);
                    focusAim.setVisibility(View.INVISIBLE);
                    captureButton.setBackgroundResource(R.drawable.shutter_normal);
                    ImageView ivCameraShutter = getActivity().findViewById(R.id.img_camera_shutter_icon);
                    ivCameraShutter.setImageResource(R.drawable.shutter);
                } else {
                    if (appClass.ListFocus == null ||
                            params.getFocusMode().equals(Camera.Parameters.FOCUS_MODE_FIXED)) {
                    } else {
//					focusAim.setImageResource(R.drawable.aim2);
                        focusAim.setVisibility(View.VISIBLE);
                        appClass.mPreview.mCamera.autoFocus(appClass.mPreview.mFocusCallback);
                    }
                }
                break;

        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
        Camera.Parameters params = appClass.mParameter.getParameters();
        switch (seekBar.getId()) {
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
        switch (seekBar.getId()) {
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
        switch (seekBar.getId()) {
            case R.id.seekbar_expo:
//			Drawable thumb = appClass.getActivity().getResources().getDrawable( R.drawable.expo_seekbtn);
//			SeekBar mSeekBar = (SeekBar)appClass.getActivity().findViewById(R.id.seekbar_expo);
//			mSeekBar.setThumb(thumb);
                break;
            case R.id.seekbar_zoom:

                break;
        }
    }
}
