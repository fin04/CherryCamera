package com.epriest.cherryCamera.gallery;

import java.util.ArrayList;

import com.epriest.cherryCamera.R;
import com.epriest.cherryCamera.gallery.ccPhotoInfo.PhotoExif;
import com.epriest.cherryCamera.gallery.ccPhotoInfo.PhotoItem;
import com.epriest.cherryCamera.util.ccCamUtil;
import com.epriest.cherryCamera.util.ccPicUtil;
import com.epriest.cherryCamera.util.ccPicUtil.BitmapInfo;
import com.epriest.cherryCamera.util.ccUtil;
import com.epriest.cherryCamera.util.logline;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ccGalleryPicviewAct extends Activity 
implements OnClickListener, OnCheckedChangeListener{
    private ViewPager mPager;    
    int currentPhotoPos;
    boolean isErase;
    private boolean isFullImage = false;
    private ArrayList<PhotoItem> PhotoData = new ArrayList<PhotoItem>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery_picturelayout);

		MobileAds.initialize(getApplicationContext(), getString(R.string.banner_ad_unit_id));
		AdView mAdView = (AdView) findViewById(R.id.adView);
		AdRequest adRequest = new AdRequest.Builder()
				.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)        // All emulators
//				.addTestDevice("F51F2A6C4A6290BDB0C711BAE2697457")
//				.addTestDevice("7763B915C09B3BF4C69010EE31F744D6")
				.build();
		mAdView.loadAd(adRequest);

        Intent i = getIntent();
		if(i != null){
			currentPhotoPos = i.getIntExtra("pos", 0);
//			Toast.makeText(this, ""+currentPhotoPos, Toast.LENGTH_SHORT).show();
			getData(currentPhotoPos);
			setText(currentPhotoPos);//first 
		}
		
		pictureViewpageInit();
    }

    private ArrayList<PhotoItem> getData(int position) {
		PhotoData = ccPicUtil.getContentList(this);
		return PhotoData;
	}

	private void pictureViewpageInit(){
    	 mPager = (ViewPager)findViewById(R.id.gallery_picture_view_pager);
         mPager.setAdapter(new PagerAdapterClass(this));
         mPager.setCurrentItem(currentPhotoPos);
         mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int position) {
				currentPhotoPos = position;
				setText(currentPhotoPos);
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {}
		});
         
         CheckBox cbFullimage = (CheckBox)findViewById(R.id.gallery_btn_fullimage);
         cbFullimage.setOnCheckedChangeListener(this);
         findViewById(R.id.gallery_exif_info).setOnClickListener(this);
         findViewById(R.id.gallery_btn_erase).setOnClickListener(this);
         findViewById(R.id.gallery_btn_share).setOnClickListener(this);		
         findViewById(R.id.gallery_btn_help).setOnClickListener(this);
         findViewById(R.id.gallery_helpimage).setOnClickListener(this);
//       findViewById(R.id.gallery_btn_etc).setOnClickListener(this);
//       findViewById(R.id.imageinfobox).setOnClickListener(this);         
	}
	
/*	private void pDialogSet(String message, boolean isCancel){
		if(pd.isShowing())
			pd.dismiss();
		pd = new ProgressDialog(this);
		pd.setMessage(message);
		pd.setCancelable(false);
		if(isCancel){
			pd.setButton(DialogInterface.BUTTON_NEGATIVE, 
					getResources().getString(R.string.button_cancel), 
					new DialogInterface.OnClickListener() {
			    @Override
			    public void onClick(DialogInterface dialog, int which) {
			        dialog.dismiss();
			    }
			});
		}		
		pd.show();
	}*/
    
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.gallery_exif_info:			
			LinearLayout tvInfo = (LinearLayout)findViewById(R.id.gallery_exif_window);
			if(tvInfo.getVisibility() == View.VISIBLE)
				tvInfo.setVisibility(View.GONE);
			else
				tvInfo.setVisibility(View.VISIBLE);			
			break;
		case R.id.gallery_btn_erase:
			AlertDialog.Builder alert = new AlertDialog.Builder(this);
			alert.setTitle(getResources().getString(R.string.alerttitle));
			alert.setMessage(getResources().getString(R.string.eraseMessage));
			alert.setPositiveButton(getResources().getString(R.string.msg_yes), 
					new DialogInterface.OnClickListener() {
			    public void onClick( DialogInterface dialog, int which) {
			        dialog.dismiss();
			        if(!isErase){
			        	isErase = true;//
			        	logline.d(" e currentPhotoPos : "+currentPhotoPos);		
			        	int pos = currentPhotoPos;
			        	currentPhotoPos = ccPicUtil.eraseContent(ccGalleryPicviewAct.this
			        			,PhotoData.get(currentPhotoPos).PhotoId , PhotoData.size(), currentPhotoPos);
			        	PhotoData.remove(pos);
			        	if(currentPhotoPos == -1){
			        		galleryFinish();
			        		return;
			        	}
			        	getData(currentPhotoPos);
			        	changePager(currentPhotoPos);
			        	viewExifInfo(currentPhotoPos);
			        	Toast toast = Toast.makeText(ccGalleryPicviewAct.this, 
			        			ccGalleryPicviewAct.this.getText(R.string.file_delete_success),
			        			Toast.LENGTH_SHORT);
			        	toast.setGravity(Gravity.CENTER, 0, 150);
			        	toast.show();
			        	setText(currentPhotoPos);
			        	isErase = false;
			        }
			    }
			});
			alert.setNegativeButton(getResources().getString(R.string.msg_no), 
					new DialogInterface.OnClickListener() {
			    public void onClick( DialogInterface dialog, int which) {
			        dialog.dismiss();
			    }
			});
			alert.show();
			break;
		case R.id.gallery_btn_share:
			Intent share = new Intent(Intent.ACTION_SEND);
			share.setType("image/jpeg");
//			logline.d("============"+PhotoData.get(currentPhotoPos));PhotoData.get(position).PhotoName;
			share.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + PhotoData.get(currentPhotoPos).PhotoData));
			startActivity(Intent.createChooser(share, "Share image"));
			break;
		case R.id.gallery_btn_help:
			findViewById(R.id.gallery_helpimage).setVisibility(View.VISIBLE);
			break;
		case R.id.gallery_helpimage:
			findViewById(R.id.gallery_helpimage).setVisibility(View.INVISIBLE);
			break;
		case R.id.gallery_btn_etc:
			LinearLayout etcLL = (LinearLayout)findViewById(R.id.buttonLayout_etc);
			if(etcLL.isShown())
				etcLL.setVisibility(View.GONE);
			else
				etcLL.setVisibility(View.VISIBLE);
			break;
		}
	}
	
	@Override
	public void onCheckedChanged(CompoundButton v, boolean isChecked) {
		switch(v.getId()){
		case R.id.gallery_btn_fullimage:
			if(isChecked){
				isFullImage = true;
			}else{
				isFullImage = false;
			}
			changePager(currentPhotoPos);
			break;
		}
	}
	
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK){
			// help가 떴을경우..
			if(findViewById(R.id.gallery_helpimage).isShown()){
				findViewById(R.id.gallery_helpimage).setVisibility(View.INVISIBLE);
				return true;
			}else{
				galleryFinish();
			}
			
		}
		return true;
	}
	
	private void galleryFinish(){
		Intent intent = getIntent();
		logline.d("---"+PhotoData.size());
		if(PhotoData.isEmpty()){
			intent.putExtra("pic_data", "null");
		}else{
			intent.putExtra("pic_data",PhotoData.get(currentPhotoPos).PhotoData);
		}
		intent.putExtra("pic_num", currentPhotoPos);
		setResult(RESULT_OK,intent);
		finish();
	}
	
	private void changePager(int currentPhotoPos){
		mPager.getAdapter().notifyDataSetChanged();
	}
	
	private void viewExifInfo(int currentPhotoPos) {
		PhotoExif exif = ccCamUtil.getExif(PhotoData.get(currentPhotoPos).PhotoData);
		
		TextView tvMake = (TextView)findViewById(R.id.textExif_make);
		if(exif.MAKE == null)
			tvMake.setText("");
		else
			tvMake.setText(exif.MAKE);

		TextView tvModel = (TextView)findViewById(R.id.textExif_model);tvMake.setText(exif.MAKE);
		if(exif.Model == null)
			tvModel.setText("");
		else
			tvModel.setText(exif.Model);	
		
		TextView tvTime = (TextView)findViewById(R.id.textExif_time);
		if(exif.DateTime == null)
			tvTime.setText("");
		else			
			tvTime.setText(exif.DateTime);
		
		TextView tvOrientation = (TextView)findViewById(R.id.textExif_Orientation);
		int ori = ccCamUtil.setOrientationToDegree(Integer.parseInt(exif.Orientation));
		if(exif.Orientation == null)
			tvOrientation.setText("");
		else
			tvOrientation.setText(""+ori+(char) 0x00B0);
		
		TextView tvFocalLength = (TextView)findViewById(R.id.textExif_FocalLength);
		if(exif.FocalLength == null)
			tvFocalLength.setText("");
		else
			tvFocalLength.setText(exif.FocalLength);
		
		TextView tvExpoTime = (TextView)findViewById(R.id.textExif_ExposureTime);
		if(exif.EXPOTIME == null)
			tvExpoTime.setText("");
		else
			tvExpoTime.setText(exif.EXPOTIME);
		
		TextView tvWB = (TextView)findViewById(R.id.textExif_WhiteBalence);
		if(exif.WhiteBalence == null)
			tvWB.setText("");
		else{			
			if(Integer.parseInt(exif.WhiteBalence) == ExifInterface.WHITEBALANCE_AUTO)
				tvWB.setText(R.string.camera_auto);
			else
				tvWB.setText(R.string.camera_manual);
		}
		
		TextView tvFnum = (TextView)findViewById(R.id.textExif_fnumber);
		if(exif.APERTURE == null)
			tvFnum.setText("");
		else
			tvFnum.setText("F"+exif.APERTURE);
		
		TextView tvIso = (TextView)findViewById(R.id.textExif_iso);
		if(exif.ISO == null)
			tvIso.setText("");
		else
			tvIso.setText(exif.ISO);
	}
	
	private boolean setText(int position){
		//==============
		// photo number
		//==============
		TextView tvNum = (TextView)findViewById(R.id.gallery_text_number);
		int size = PhotoData.size();
		String pNum = "<"+(size-position)+"/"+size+">";
		
		tvNum.setText(pNum);
		
		//==============
		// photo date
		//==============
		TextView tvDate = (TextView)findViewById(R.id.gallery_text_filename);
//		String pDate = ccCamUtil.changeDateFormat(PhotoData.get(position).PhotoDate
//				, "yyyy-MM-dd a HH:mm:ss");
		String pDate = PhotoData.get(position).PhotoName;
		tvDate.setText(pDate);
		
		//==============
		// photo file size
		//==============
		TextView tvSize = (TextView)findViewById(R.id.gallery_text_size);
		double size1 = Double.parseDouble(PhotoData.get(currentPhotoPos).PhotoDataSize)/1024*0.001;
		String pSize = "File Size :"+ (int)(size1*10.0+0.5)/10.0+"MB";
		tvSize.setText(pSize);
		
		//==============
		// photo resolution
		//==============
		TextView tvResolution = (TextView)findViewById(R.id.gallery_text_resolution);
		BitmapInfo bitinfo = ccPicUtil.getBitmapSize(PhotoData.get(position).PhotoData);
		String pResolution = "Size :"+bitinfo.width
				+"x"+bitinfo.height;			
		tvResolution.setText(pResolution);
		
		viewExifInfo(position);
		return true;
	}
	
	private Bitmap setPicture(int photoNum){
		if(currentPhotoPos == -1){
			galleryFinish();
			return null;
		}
		if(ccPicUtil.wrongBitmap(PhotoData.get(photoNum).PhotoData))
    		return BitmapFactory.decodeResource(ccGalleryPicviewAct.this.getResources()
    				, R.drawable.wrong_file);
		else if(ccUtil.nullFile(PhotoData.get(photoNum).PhotoData))
    		return BitmapFactory.decodeResource(ccGalleryPicviewAct.this.getResources()
    				, R.drawable.wrong_file);

    	return setPreviewImageView(photoNum);
	}
    
    private Bitmap setPreviewImageView(int photoNum){
    	Bitmap bitmap;
    	try{
    		bitmap = ccPicUtil.previewCooking(PhotoData.get(photoNum).PhotoData,0
        			,ccUtil.getScreenSize(ccGalleryPicviewAct.this).widthPixels
        			,ccUtil.getScreenSize(ccGalleryPicviewAct.this).heightPixels);
            int pW = bitmap.getWidth();
            int pH = bitmap.getHeight();
            Matrix matrix = new Matrix();
            int degree = ccCamUtil.getPathOrientationToDegree(PhotoData.get(photoNum).PhotoData);
            matrix.setRotate(degree, (float) pW/2, (float) pH/2);
            if(isFullImage){
    	        if(pW > pH){
    	        	matrix.setRotate(90, (float) pW/2, (float) pH/2);
    	        }
            }
    		bitmap = Bitmap.createBitmap(bitmap, 0, 0, 
            		pW, pH, matrix, true);	            
        }catch(Exception e){
        	logline.d("previewBitmap Resize Exception : "+e);
        	return BitmapFactory.decodeResource(ccGalleryPicviewAct.this.getResources()
    				, R.drawable.wrong_file);
        }

		return bitmap;
	}
	
	private class PagerAdapterClass extends PagerAdapter{

		private LayoutInflater mInflater;
		
		public PagerAdapterClass(Context context) {
			super();
			mInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return PhotoData.size();
		}

		@Override
        public Object instantiateItem(View pager, int position) {
			View v = mInflater.inflate(R.layout.gallery_picture_pageritem, null);
//			setText(mPager.getCurrentItem());
			TouchImageView iv = (TouchImageView)v.findViewById(R.id.gallery_picture_image);
			iv.setImageBitmap(setPicture(position));			
            ((ViewPager) pager).addView(v, 0);
            return v; 
        }
 
        @Override
        public void destroyItem(View pager, int position, Object view) {    
            ((ViewPager)pager).removeView((View)view);
        }
        
        @Override
        public boolean isViewFromObject(View pager, Object obj) {
            return pager == obj; 
        }
        
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }
 
        @Override public void restoreState(Parcelable arg0, ClassLoader arg1) {}
        @Override public Parcelable saveState() { return null; }
        @Override public void startUpdate(View arg0) {}
        @Override public void finishUpdate(View arg0) {}
		
	}

	
}
