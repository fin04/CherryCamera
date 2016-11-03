package com.epriest.cherryCamera.old;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.epriest.cherryCamera.ApplicationClass;
import com.epriest.cherryCamera.R;
import com.epriest.cherryCamera.gallery.ImageItem;
import com.epriest.cherryCamera.util.IN;
import com.epriest.cherryCamera.util.ccPicUtil;
import com.epriest.cherryCamera.util.ccUtil;
import com.epriest.cherryCamera.util.ccCamUtil;
import com.epriest.cherryCamera.util.logline;


public class ccGalleryThumbViewActivity extends Activity implements OnClickListener, OnCheckedChangeListener{//, OnItemClickListener{

	public ArrayList<String> PhotoId = new ArrayList<String>();
	public ArrayList<String> PhotoName = new ArrayList<String>();
	public ArrayList<String> PhotoDate = new ArrayList<String>();
	public ArrayList<String> PhotoDataSize = new ArrayList<String>();
	public ArrayList<String> PhotoData = new ArrayList<String>();
	
	int lcdWidth, lcdHeight;
//	private ccGalleryUtil galleryUtil;
	private Context mContext;
//	private AdView adView;	
	
	private GridView gridView;
	private GridViewAdapter customGridAdapter;
	private boolean isFullImage = false;
//	SensorManager mSensor;
//	float pitchAngle;
//	int orientation_degree;
	ImageView[] ivGalleryImage = new ImageView[3];
	Bitmap[] previewBitmap = new Bitmap[3];
	ViewSwitcher viewSwitcher;
	int currentPhotoPos;
//	Animation slide_in_left, slide_out_right;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gallery_thumblayout);
		mContext = this;
		
		galleryInit();
//		adViewInit();
	}
	
	/*private void adViewInit() {
		adView = (AdView)this.findViewById(R.id.adGalleryView);
	    AdRequest adRequest = new AdRequest.Builder()
//	    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
//	    .addTestDevice("TEST_DEVICE_ID")
	    .build();
	    adView.loadAd(adRequest);
	    
        try{
	        adView = new AdView(this, AdSize.BANNER, IN.AD_NUM);
	        LinearLayout layout = (LinearLayout)findViewById(R.id.adlayout);
	        layout.addView(adView);
	        adView.loadAd(new AdRequest());
//	        adView.setVisibility(adView.INVISIBLE);
        }catch(Exception e){
        	logline.d("-----AD View Exception  : "+e);
        }
	}*/
	
	private void galleryInit(){
//		galleryUtil =  new ccGalleryUtil(this);
//		viewSwitcher = (ViewSwitcher) findViewById(R.id.viewSwitcher_gallery);
//		ccPicUtil.imageFileLoad(this);
//		galleryUtil.makeThumbFolder();
		gridviewInit();
//		pictureViewpageInit();
	}

	private void gridviewInit(){		
		gridView = (GridView) findViewById(R.id.gallery_gridView);
		customGridAdapter = new GridViewAdapter(this, R.layout.gallery_gridview_item, getData());
		gridView.setAdapter(customGridAdapter);
		gridView.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				Intent i = new Intent(ccGalleryThumbViewActivity.this, ccGalleryPictureLoader.class);
				i.putStringArrayListExtra("data", PhotoData);
//				i.putStringArrayListExtra(name, galleryUtil.PhotoDataSize);
				i.putStringArrayListExtra("date", PhotoDate);
				i.putStringArrayListExtra("id", PhotoId);
//				i.putStringArrayListExtra("w", appc.PhotoWidth);
//				i.putStringArrayListExtra("h", appc.PhotoHeight);
//				i.putExtra("pos", position);
				i.putExtra("allPhotoNum", PhotoData.size());
				ccGalleryThumbViewActivity.this.startActivity(i);

//				Toast.makeText(ccGalleryThumbViewActivity.this, ""+position, Toast.LENGTH_SHORT).show();
//					galleryUtil.currentPhotoPos = position;
//					setGallery(galleryUtil.currentPhotoPos);
//					viewSwitcher.showPrevious();			
			}           
        });
	}
		
	private ArrayList<ImageItem> getData() {		
		final ArrayList<ImageItem> imageItems = new ArrayList<ImageItem>();
		for (int i = 0; i < PhotoData.size(); i++) {
			Long id = Long.parseLong(PhotoId.get(i));
			String date = PhotoDate.get(i);
//			String picsize = appc.PhotoWidth.get(i)+"x"+appc.PhotoHeight.get(i);
			String picsize = "";
			String picname = PhotoName.get(i);
			int num = i;
			imageItems.add(new ImageItem(num, id, date, picsize, picname));
		} 
		return imageItems; 
	}
	
	private void changeGridView(){
		customGridAdapter.data.clear();
		customGridAdapter.data = getData();
		int position = currentPhotoPos;
		gridView.setSelection(position > 0 ? position - 1 : 0);
		customGridAdapter.notifyDataSetInvalidated();
		viewSwitcher.showNext();
	}
	
	private ProgressDialog progressDialog;
	private void setGallery(final int position){		
		progressDialog = ProgressDialog.show(this, "", "Loading..");
		new Thread() {
			public void run() {
				galleryHandler.sendEmptyMessage(position);
			}
		}.start();
	}
	
	private Handler galleryHandler = new Handler() {
        public void handleMessage(Message msg) {
        	super.handleMessage(msg);
        	setPreviewBitmap(msg.what);    
        	progressDialog.dismiss();
        	ivGalleryImage[IN.GALLERY_PREVIEW_CURRENT].setX(0);
        	ivGalleryImage[IN.GALLERY_PREVIEW_PREV].setVisibility(View.GONE);
			ivGalleryImage[IN.GALLERY_PREVIEW_NEXT].setVisibility(View.GONE);			
        }
    };
	
	private void setPreviewBitmap(int position){
		int[] photoNum = new int[3];
		for(int i=0 ; i>photoNum.length; i++)
			photoNum[i] = 0;		
		
		int pSize = PhotoData.size();
		switch(pSize){
		case 0:
			currentPhotoPos = -1;
			break;
		case 1:
			currentPhotoPos = 0;
			break;
		case 2:
			if(position > pSize){
				currentPhotoPos = 0;
				photoNum[IN.GALLERY_PREVIEW_PREV] = 1;
				photoNum[IN.GALLERY_PREVIEW_NEXT] = 1;
			}else if(position < 0){
				currentPhotoPos = 1;
				photoNum[IN.GALLERY_PREVIEW_PREV] = 0;
				photoNum[IN.GALLERY_PREVIEW_NEXT] = 0;
			}
			break;
		default:
			if(position >= pSize || position == 0){
				photoNum[IN.GALLERY_PREVIEW_PREV] = pSize-1;
				currentPhotoPos = 0;
				photoNum[IN.GALLERY_PREVIEW_NEXT] = 1;
			}else if(position < 0 || position == pSize-1){
				photoNum[IN.GALLERY_PREVIEW_PREV] = pSize-2;
				currentPhotoPos =pSize-1;
				photoNum[IN.GALLERY_PREVIEW_NEXT] = 0;	
			}else{
				photoNum[IN.GALLERY_PREVIEW_PREV] = position-1;
				currentPhotoPos = position;
				photoNum[IN.GALLERY_PREVIEW_NEXT] = position+1;
			}
			break;			
		}
		photoNum[IN.GALLERY_PREVIEW_CURRENT] = currentPhotoPos;
		
		logline.d("allPhotoNum : "+pSize
				+", currentPhotoNum" + photoNum[IN.GALLERY_PREVIEW_CURRENT]
				+ ", prevPhotoNum"+ photoNum[IN.GALLERY_PREVIEW_PREV] 
				+", nextPhotoNum" + photoNum[IN.GALLERY_PREVIEW_NEXT]);
		
		if(currentPhotoPos == -1){ //gallery photo number = 0
			galleryFinish();			
			finish();
			return;
		}else{
			setPicture(photoNum);
			/*for(int i=0; i<ivGalleryImage.length; i++ ){
				logline.d(i+":"+photoNum[i]);
				if(!setPicture(photoNum)){
					// failed photo view
					ivGalleryImage[1].setImageResource(R.drawable.wrong_file);
				}
			}*/
				
		}
			
		setText();
	}
	
	private void setPicture(int[] photoNum){
		for(int i=0; i<previewBitmap.length; i++)
			ccCamUtil.recycleBitmap(previewBitmap[i]);

		if(ccUtil.nullFile(PhotoData.get(photoNum[IN.GALLERY_PREVIEW_PREV])))//!2장이하일때 오류
			ivGalleryImage[1].setImageResource(R.drawable.wrong_file);
//		Bitmap previewBitmap0 = MediaStore.Images.Thumbnails.getThumbnail(
//                getContentResolver(), Long.parseLong(galleryUtil.PhotoId.get(photoNum[IN.GALLERY_PREVIEW_PREV]-1)),
//                MediaStore.Images.Thumbnails.MINI_KIND, new BitmapFactory.Options());
		previewBitmap[0] = ccPicUtil.previewCooking(PhotoData.get(photoNum[IN.GALLERY_PREVIEW_PREV]), 10
				,ccUtil.getScreenSize(ccGalleryThumbViewActivity.this).widthPixels
    			,ccUtil.getScreenSize(ccGalleryThumbViewActivity.this).heightPixels);
		
		ivGalleryImage[IN.GALLERY_PREVIEW_PREV].setImageBitmap(previewBitmap[0]);
		


		if(ccUtil.nullFile(PhotoData.get(photoNum[IN.GALLERY_PREVIEW_CURRENT])))
			ivGalleryImage[1].setImageResource(R.drawable.wrong_file);
		previewBitmap[1] = ccPicUtil.previewCooking(PhotoData.get(photoNum[IN.GALLERY_PREVIEW_CURRENT]), 2
				,ccUtil.getScreenSize(ccGalleryThumbViewActivity.this).widthPixels
    			,ccUtil.getScreenSize(ccGalleryThumbViewActivity.this).heightPixels);
		if(previewBitmap[1] == null)
			ivGalleryImage[1].setImageResource(R.drawable.wrong_file);
		setPreviewImageView(previewBitmap[1], IN.GALLERY_PREVIEW_CURRENT);
		

		if(ccUtil.nullFile(PhotoData.get(photoNum[IN.GALLERY_PREVIEW_NEXT])))
			ivGalleryImage[1].setImageResource(R.drawable.wrong_file);
//		Bitmap previewBitmap1 = MediaStore.Images.Thumbnails.getThumbnail(
//                getContentResolver(), Long.parseLong(galleryUtil.PhotoId.get(photoNum[IN.GALLERY_PREVIEW_NEXT]-1)),
//                MediaStore.Images.Thumbnails.MINI_KIND, new BitmapFactory.Options());
		previewBitmap[2] = ccPicUtil.previewCooking(PhotoData.get(photoNum[IN.GALLERY_PREVIEW_NEXT]), 10
				,ccUtil.getScreenSize(ccGalleryThumbViewActivity.this).widthPixels
    			,ccUtil.getScreenSize(ccGalleryThumbViewActivity.this).heightPixels);
		ivGalleryImage[IN.GALLERY_PREVIEW_NEXT].setImageBitmap(previewBitmap[2]);
	}
	
	private boolean setPreviewImageView(Bitmap bitmap, int i){
		//==============
		// preview photo resize
		//==============
        logline.d("lcdW,lcdH : "+lcdWidth+","+lcdHeight);
//        logline.d("picWidth,picHeight : "+galleryUtil.picWidth+","+galleryUtil.picHeight);
        
        int pW = bitmap.getWidth();
        int pH = bitmap.getHeight();
        Matrix matrix = new Matrix();
        if(isFullImage){
	        if(pW > pH){
	        	matrix.setRotate(90, (float) pW/2, (float) pH/2);
	        }
        }

    	try{
    		bitmap = Bitmap.createBitmap(bitmap, 0, 0, 
            		pW, pH, matrix, true);	            
        }catch(Exception e){
        	logline.d("previewBitmap Resize Exception : "+e);
        	return false;
        }

		// preview photo change
    	ivGalleryImage[i].setImageBitmap(bitmap);
    	
		return true;
	}
	
	/*private boolean setPicture_(int i, int photoNum){
		if(galleryUtil.previewBitmap != null){
			try{
				galleryUtil.previewBitmap.recycle();
				galleryUtil.previewBitmap = null;
			}catch(Exception e){
				logline.d("previewBitmap Null Exception : "+e);
				return false;
			}
		}

		if(galleryUtil.nullFile(galleryUtil.PhotoData.get(photoNum-1)))
			return false;
		switch(i){
		case IN.GALLERY_PREVIEW_PREV:
		case IN.GALLERY_PREVIEW_NEXT:
			galleryUtil.previewBitmap = MediaStore.Images.Thumbnails.getThumbnail(
	                getContentResolver(), Long.parseLong(galleryUtil.PhotoId.get(photoNum-1)),
	                MediaStore.Images.Thumbnails.MINI_KIND, new BitmapFactory.Options());
			break;
		case IN.GALLERY_PREVIEW_CURRENT:
			if(!ccGalleryUtil.previewCooking(ccGalleryUtil.previewBitmap, galleryUtil.PhotoData.get(photoNum-1), 1))
				return false;
			break;
		}
		
		//==============
		// preview photo resize
		//==============
        logline.d("lcdW,lcdH : "+galleryUtil.lcdWidth+","+galleryUtil.lcdHeight);
        logline.d("picWidth,picHeight : "+galleryUtil.picWidth+","+galleryUtil.picHeight);
        
        int pW = galleryUtil.previewBitmap.getWidth();
        int pH = galleryUtil.previewBitmap.getHeight();
        Matrix matrix = new Matrix();
        if(isFullImage){
	        if(pW > pH){
	        	matrix.setRotate(90, (float) pW/2, (float) pH/2);
	        }
        }

    	try{
    		galleryUtil.previewBitmap = Bitmap.createBitmap(galleryUtil.previewBitmap, 0, 0, 
            		pW, pH, matrix, true);	            
        }catch(Exception e){
        	logline.d("previewBitmap Resize Exception : "+e);
        	return false;
        }

		// preview photo change
    	ivGalleryImage[i].setImageBitmap(galleryUtil.previewBitmap);
    	
		return true;
	}	*/
	
	private boolean setText(){
		//==============
		// photo number
		//==============
		TextView tvNum = (TextView)findViewById(R.id.gallery_text_number);
		String pNum = "<"+(currentPhotoPos+1)+"/"+PhotoDate.size()+">";
		tvNum.setText(pNum);
		
		//==============
		// photo date
		//==============
//		TextView tvDate = (TextView)findViewById(R.id.gallery_text_date);
//		String pDate = ccCamUtil.changeDateFormat(PhotoDate.get(currentPhotoPos)
//				, "yyyy-MM-dd a HH:mm:ss");
//		tvDate.setText(pDate);
		
		//==============
		// photo file size
		//==============
		TextView tvSize = (TextView)findViewById(R.id.gallery_text_size);
		double size1 = Double.parseDouble(PhotoDataSize.get(currentPhotoPos))/1024*0.001;
		String pSize = "File Size :"+ (int)(size1*10.0+0.5)/10.0+"MB";
		tvSize.setText(pSize);
		
		//==============
		// photo resolution
		//==============
//		TextView tvResolution = (TextView)findViewById(R.id.gallery_text_resolution);	
//		String pResolution = "picture size :"+appc.PhotoWidth.get(currentPhotoPos)
//				+"x"+appc.PhotoWidth.get(currentPhotoPos);			
//		tvResolution.setText(pResolution);
		
		return true;
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.gallery_btn_erase:
//			AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
//			alert.setTitle(getResources().getString(R.string.alerttitle));
//			alert.setMessage(getResources().getString(R.string.eraseMessage));
//			alert.setPositiveButton(getResources().getString(R.string.msg_yes), new DialogInterface.OnClickListener() {
//			    public void onClick( DialogInterface dialog, int which) {
//			        dialog.dismiss();
//			        if(!isErase){			        	
//			        	isErase = true;
////			        	int cNum = galleryUtil.currentPhotoNum;			        	
//			    		if(ccGalleryUtil.deleteThumbFile(new File(appc.PhotoData.get(currentPhotoPos))))
//			    			Toast.makeText(ccGalleryThumbViewActivity.this, "thumbnail delete success", Toast.LENGTH_SHORT).show();
//			    		else
//			    			Toast.makeText(ccGalleryThumbViewActivity.this, "thumbnail delete failed", Toast.LENGTH_SHORT).show();
//			        	currentPhotoPos = ccPicUtil.erasePictureFromCR(currentPhotoPos);
//			        	Toast toast = Toast.makeText(ccGalleryThumbViewActivity.this, 
//			        			ccGalleryThumbViewActivity.this.getText(R.string.file_delete_success),
//			        			Toast.LENGTH_SHORT);
//			        	toast.setGravity(Gravity.CENTER, 0, 150);
//			        	toast.show();
////				        customGridAdapter.checkList.remove(cNum);
//				        setGallery(currentPhotoPos);
//			        }
//			    }
//			});
//			alert.setNegativeButton(getResources().getString(R.string.msg_no), new DialogInterface.OnClickListener() {
//			    public void onClick( DialogInterface dialog, int which) {
//			        dialog.dismiss();
//			    }
//			});
//			alert.show();
			break;
		case R.id.gallery_btn_share:
			Intent share = new Intent(Intent.ACTION_SEND);
			share.setType("image/jpeg");
			logline.d("============"+PhotoData.get(currentPhotoPos));
			share.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + PhotoData.get(currentPhotoPos)));
			startActivity(Intent.createChooser(share, "Share Image"));
			break;
//		case R.id.gallery_btn_thumb:
		case R.id.imageinfobox:
			changeGridView();
			break;
//		case R.id.btn_return_picture:
//			viewSwitcher.showPrevious();
//			break;
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
		case R.id.gallery_btn_beauty:
			break;
//		case R.id.gallery_preview_image:
//			if(galleryUtil.currentPhotoNum == 0)
//				return;
//			Intent intent = new Intent();
//			intent.setAction(Intent.ACTION_VIEW);
//			logline.d(galleryUtil.PhotoData.get(galleryUtil.currentPhotoNum-1));
//			intent.setDataAndType(Uri.parse("file://"+galleryUtil.PhotoData.get(galleryUtil.currentPhotoNum-1)), "image/*");
//			startActivity(intent);
//			break;
		/*case R.id.btn_thumb_selecton:
			if(customGridAdapter.isCheckBox){
				customGridAdapter.isCheckBox = false;
				customGridAdapter.checkList.clear();
			}else{
				customGridAdapter.isCheckBox = true;
			}
			customGridAdapter.notifyDataSetChanged();
			break;*/
		/*case R.id.btn_thumb_delete:
//			progressDialog = ProgressDialog.show(this, "",
//				        "Please wait for few seconds...", true);
			 new Thread(new Runnable() {
			        public void run() {
			        	File path = new File(Environment.getExternalStorageDirectory()
			        	        .getAbsoluteFile() + IN.cPath);
			        	if(path.exists()){
			        		File[] files = path.listFiles();
			        		int count = files.length-1;
				        	for (int i = 0; i < files.length; i++) {
				        		if(customGridAdapter.checkList.get(i)){
					        		String name = files[count-i].getName();
					        		logline.d(count-i+"/"+count+"-"+name+" : ");
				        		}
				        	}
			        	}

			        	handler.sendEmptyMessage(0);
			        }
			 }).start();
			break;*/
		}
	}
	
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK){
			
			// help가 떴을경우..
			if(findViewById(R.id.gallery_helpimage).isShown()){
				findViewById(R.id.gallery_helpimage).setVisibility(View.INVISIBLE);
				return true;
			}
			
			if(viewSwitcher.getDisplayedChild() == 0){
				galleryFinish();
				finish();
			}else{
				changeGridView();
			}
			
		}
		return true;
	}
	
//	boolean isLayerDrag = false;
	float posStartX, posStartY;
//	float posX, posY;
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float moveX = 0;
		if(viewSwitcher.getDisplayedChild() == 0)
			return true;
		
		switch(event.getAction()){
		case MotionEvent.ACTION_DOWN:
//			isLayerDrag = true;
			posStartX = event.getX();
			posStartY = event.getY();
//			ivGalleryImage[IN.GALLERY_PREVIEW_NEXT].setVisibility(View.VISIBLE);
//			ivGalleryImage[IN.GALLERY_PREVIEW_PREV].setVisibility(View.VISIBLE);
//			logline.d("/====="+event.getX()+","+event.getY());
			break;
		case MotionEvent.ACTION_MOVE:
//			logline.d("======"+event.getX()+","+event.getY()+","+event.getActionMasked());
//			posX = event.getX();
//			posY = event.getY();
			moveX = event.getX()-posStartX;
			ivGalleryImage[IN.GALLERY_PREVIEW_CURRENT].setX(moveX);
			if(moveX < 0){				
				ivGalleryImage[IN.GALLERY_PREVIEW_NEXT].setVisibility(View.VISIBLE);
				ivGalleryImage[IN.GALLERY_PREVIEW_PREV].setVisibility(View.GONE);
			}else if (moveX > 0){				
				ivGalleryImage[IN.GALLERY_PREVIEW_PREV].setVisibility(View.VISIBLE);
				ivGalleryImage[IN.GALLERY_PREVIEW_NEXT].setVisibility(View.GONE);
			}
			break;
		case MotionEvent.ACTION_UP:
//			isLayerDrag = false;
			moveX = ivGalleryImage[IN.GALLERY_PREVIEW_CURRENT].getX();
			logline.d("=== moveX : "+moveX);
			if(moveX > lcdWidth/3){
				//prev image			
				logline.d("=1==  : "+lcdWidth/3);
				TranslateAnimation animate = new TranslateAnimation(0,lcdWidth-moveX,0,0);
				animate.setDuration(100);
				animate.setFillAfter(false);
				ivGalleryImage[IN.GALLERY_PREVIEW_CURRENT].startAnimation(animate);
				animate.setAnimationListener(new AnimationListener(){

					@Override
					public void onAnimationEnd(Animation animation) {
						setGallery(currentPhotoPos-1);
					}

					@Override
					public void onAnimationRepeat(Animation animation) {}

					@Override
					public void onAnimationStart(Animation animation) {}
					
				});				
				
//				galleryUtil.slideToRight(ivGalleryImage[IN.GALLERY_PREVIEW_CURRENT]);
//				setGallery(galleryUtil.currentPhotoNum-1);
				
			}else if(moveX < -(lcdWidth/3)){
				//next image	
				logline.d("=2==  : "+lcdWidth/3);
				TranslateAnimation animate = new TranslateAnimation(0,-lcdWidth+moveX,0,0);
				animate.setDuration(100);
				animate.setFillAfter(false);
				ivGalleryImage[IN.GALLERY_PREVIEW_CURRENT].startAnimation(animate);
				animate.setAnimationListener(new AnimationListener(){

					@Override
					public void onAnimationEnd(Animation animation) {
						setGallery(currentPhotoPos+1);
					}

					@Override
					public void onAnimationRepeat(Animation animation) {}

					@Override
					public void onAnimationStart(Animation animation) {}
					
				});				
				
//				galleryUtil.slideToLeft(ivGalleryImage[IN.GALLERY_PREVIEW_CURRENT]);
//				setGallery(galleryUtil.currentPhotoNum+1);
			}else{
				logline.d("=3==  : "+lcdWidth/3);
				ivGalleryImage[IN.GALLERY_PREVIEW_CURRENT].setX(0);
//				ivGalleryImage[IN.GALLERY_PREVIEW_CURRENT].setY(0);
				ivGalleryImage[IN.GALLERY_PREVIEW_PREV].setVisibility(View.GONE);
				ivGalleryImage[IN.GALLERY_PREVIEW_NEXT].setVisibility(View.GONE);
			}
			
			break;
		}
		return super.onTouchEvent(event);
	}

	private void galleryFinish(){
		Intent intent = getIntent();
		if(PhotoDate.size() == 0){
			intent.putExtra("pic_data", "null");
		}else{
			intent.putExtra("pic_data", PhotoData.get(currentPhotoPos));
		}
//		intent.putExtra("pic_num", galleryUtil.currentPhotoNum);
		setResult(RESULT_OK,intent);
	}
	
	

	@Override
	protected void onResume() {
//		if (adView != null) {
//			adView.resume();
//		}
		super.onResume();
	}

	@Override
	protected void onPause() {
//		if (adView != null) {
//			adView.pause();
//		}
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		logline.d("ccGallery Activity onDestroy");
		for(int i=0; i<previewBitmap.length; i++)
			ccCamUtil.recycleBitmap(previewBitmap[i]);
//		if (adView != null)
//			adView.destroy();
//		if(galleryUtil.previewBitmap != null){
//			try{
//				galleryUtil.previewBitmap.recycle();
//				galleryUtil.previewBitmap = null;
//				System.gc();
//			}catch(Exception e){
//				logline.d("previewBitmap Null Exception : "+e);
//			}
//		}		
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
			setGallery(currentPhotoPos);
			break;
		
		}
	}

	/*@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Log.d("","======="+position);
		setGallery(position);
		viewSwitcher.showPrevious();
		Toast.makeText(this, position + "#Selected",
				Toast.LENGTH_SHORT).show();
	}*/

//	@Override
//	public void onAccuracyChanged(Sensor arg0, int arg1) {
//		
//	}
//
//	@Override
//	public void onSensorChanged(SensorEvent arg0) {
//		switch(arg0.sensor.getType()){
//    	case SensorManager.SENSOR_ORIENTATION:
//    		pitchAngle = arg0.values[1]*10; 
//    		int prevDegree = orientation_degree;
//			if(Math.abs(pitchAngle) >= 80){
//				orientation_degree = IN.PITCH_PORTRAIT;
//			}else if(Math.abs(pitchAngle) <= 10){
//				orientation_degree = IN.PITCH_LANDSCAPE;
//			}
//			if(prevDegree != orientation_degree)
//				setChangeOrientation();
//    		break;
//    	}
//	}
	
	/*private void setChangeOrientation() {
		float lcdValue = galleryUtil.lcdHeight*100 / galleryUtil.picHeight;
        final int tempW = (int)(galleryUtil.picWidth*lcdValue)/100;
        final int tempH = (int)(galleryUtil.picHeight*lcdValue)/100;
		final ImageView ivGalleryImage = (ImageView)findViewById(R.id.gallery_preview_image);
		Animation aniRotate = AnimationUtils.loadAnimation(mContext,R.anim.gallery_rotate90);
		aniRotate.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationEnd(Animation arg0) {
		        Matrix matrix = new Matrix();
//		        matrix.setRotate(0, (float) galleryUtil.previewBitmap.getWidth()/2, (float) galleryUtil.previewBitmap.getHeight()/2);
		        matrix.setScale(tempW/2, tempH/2);
//		        logline.d("w = "+ivGalleryImage.getWidth());
//		        ivGalleryImage.setRotation(0);
		        
				ivGalleryImage.setImageMatrix(matrix);
				ivGalleryImage.setScaleType(ScaleType.MATRIX);
			}

			@Override
			public void onAnimationRepeat(Animation arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onAnimationStart(Animation arg0) {
				// TODO Auto-generated method stub
				
			}
			
		});
		Animation aniRotate_back = AnimationUtils.loadAnimation(mContext,R.anim.gallery_rotate90_back);
		aniRotate_back.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationEnd(Animation arg0) {
//				Matrix matrix = new Matrix();
//				matrix.setScale(galleryUtil.previewBitmap.getWidth(), galleryUtil.previewBitmap.getHeight());
//		        matrix.setRotate(90, (float) galleryUtil.previewBitmap.getWidth()/2, (float) galleryUtil.previewBitmap.getHeight()/2);
//		        ivGalleryImage.setRotation(90);
//		        ivGalleryImage.setScaleType(ScaleType.FIT_CENTER);
//				logline.d("w = "+ivGalleryImage.getWidth());
			}

			@Override
			public void onAnimationRepeat(Animation arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onAnimationStart(Animation arg0) {
				// TODO Auto-generated method stub
				
			}
			
		});
		switch(orientation_degree){
		case IN.PITCH_PORTRAIT:
			ivGalleryImage.startAnimation(aniRotate);
			ivGalleryImage.invalidate();
			break;
		case IN.PITCH_LANDSCAPE:
			ivGalleryImage.startAnimation(aniRotate_back);
			ivGalleryImage.invalidate();
			break;
		}
	}*/

}
