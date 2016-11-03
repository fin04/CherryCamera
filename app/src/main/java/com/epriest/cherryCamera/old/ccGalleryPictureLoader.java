package com.epriest.cherryCamera.old;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
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
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.epriest.cherryCamera.ApplicationClass;
import com.epriest.cherryCamera.R;
import com.epriest.cherryCamera.util.ccPicUtil;
import com.epriest.cherryCamera.util.ccCamUtil;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

public class ccGalleryPictureLoader extends Activity implements OnClickListener{
	
	public ArrayList<String> PhotoId = new ArrayList<String>();
	public ArrayList<String> PhotoName = new ArrayList<String>();
	public ArrayList<String> PhotoDate = new ArrayList<String>();
	public ArrayList<String> PhotoDataSize = new ArrayList<String>();
	public ArrayList<String> PhotoData = new ArrayList<String>();
	
	private ViewPager mPager;
	
	private int currentPhotoPos;
	boolean isErase;
    private boolean isFullImage = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gallery_picturelayout);
		uil_init();
		Intent i = getIntent();
		if(i != null){
			currentPhotoPos = i.getIntExtra("pos", 0);
		}
		Toast.makeText(this, "pos : "+currentPhotoPos, Toast.LENGTH_SHORT).show();
			
		pictureViewpageInit();
	}
	
	private void pictureViewpageInit(){
		mPager = (ViewPager) findViewById(R.id.gallery_picture_view_pager);
        mPager.setAdapter(new ViewPagerAdapter(this));
        mPager.setCurrentItem(currentPhotoPos);
        
        findViewById(R.id.gallery_btn_erase).setOnClickListener(this);
		findViewById(R.id.gallery_btn_share).setOnClickListener(this);		
		findViewById(R.id.imageinfobox).setOnClickListener(this);
		findViewById(R.id.gallery_btn_help).setOnClickListener(this);
		findViewById(R.id.gallery_helpimage).setOnClickListener(this);
		findViewById(R.id.gallery_btn_etc).setOnClickListener(this);
		findViewById(R.id.gallery_btn_beauty).setOnClickListener(this);
	}
	
	private void uil_init(){		
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
        .threadPriority(Thread.NORM_PRIORITY - 2)
        .denyCacheImageMultipleSizesInMemory()
        .discCacheFileNameGenerator(new Md5FileNameGenerator())
        .tasksProcessingOrder(QueueProcessingType.LIFO)
        .writeDebugLogs() // Remove for release app
        .build();
		ImageLoader.getInstance().init(config);
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.gallery_btn_erase:
			AlertDialog.Builder alert = new AlertDialog.Builder(this);
			alert.setTitle(getResources().getString(R.string.alerttitle));
			alert.setMessage(getResources().getString(R.string.eraseMessage));
			alert.setPositiveButton(getResources().getString(R.string.msg_yes), new DialogInterface.OnClickListener() {
			    public void onClick( DialogInterface dialog, int which) {
			        dialog.dismiss();
			        if(!isErase){			        	
			        	isErase = true;//			        	
			        	int pos = ccPicUtil.eraseContent(ccGalleryPictureLoader.this
			        			,PhotoId.get(currentPhotoPos) , PhotoId.size(), currentPhotoPos);
			        	changePager(pos);
			        	currentPhotoPos = pos;
			        	Toast toast = Toast.makeText(ccGalleryPictureLoader.this, 
			        			ccGalleryPictureLoader.this.getText(R.string.file_delete_success),
			        			Toast.LENGTH_SHORT);
			        	toast.setGravity(Gravity.CENTER, 0, 150);
			        	toast.show();			        	
			        }
			    }
			});
			alert.setNegativeButton(getResources().getString(R.string.msg_no), new DialogInterface.OnClickListener() {
			    public void onClick( DialogInterface dialog, int which) {
			        dialog.dismiss();
			    }
			});
			alert.show();
			break;
		case R.id.gallery_btn_share:
			Intent share = new Intent(Intent.ACTION_SEND);
			share.setType("image/jpeg");
//			logline.d("============"+appc.PhotoData.get(currentPhotoPos));
			share.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + PhotoData.get(currentPhotoPos)));
			startActivity(Intent.createChooser(share, "Share Image"));
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
		if(PhotoData.isEmpty()){
			intent.putExtra("pic_data", "null");
		}else{
			intent.putExtra("pic_data",PhotoData.get(currentPhotoPos));
		}
		intent.putExtra("pic_num", currentPhotoPos);
		setResult(RESULT_OK,intent);
		finish();
	}
	
	private void changePager(int currentPhotoPos){
		mPager.getAdapter().notifyDataSetChanged();
	}
	
	
	public class ViewPagerAdapter extends PagerAdapter {

		private LayoutInflater inflater;
		DisplayImageOptions options;
		
		public ViewPagerAdapter(Context context) {
			inflater = LayoutInflater.from(context);	
			
			options = new DisplayImageOptions.Builder()
			.showImageForEmptyUri(R.drawable.thumb_row)
			.showImageOnFail(R.drawable.wrong_file)
			.resetViewBeforeLoading(true)
			.cacheOnDisk(true)
			.imageScaleType(ImageScaleType.EXACTLY)
			.bitmapConfig(Bitmap.Config.RGB_565)
			.displayer(new FadeInBitmapDisplayer(300))
			.build();
		}		

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
//			super.destroyItem(container, position, object);
			container.removeView((View) object);
		}
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return PhotoData.size();
		}

		@Override
		public Object instantiateItem(ViewGroup view, final int position) {			
			View imageLayout = inflater.inflate(R.layout.gallery_picture_pageritem, view, false);
			assert imageLayout != null;
			currentPhotoPos = position;
			setText(position);
			ImageView imageView = (ImageView) imageLayout.findViewById(R.id.gallery_picture_image);
			final ProgressBar spinner = (ProgressBar) imageLayout.findViewById(R.id.item_gallery_picture_loading);
			ImageLoader.getInstance().displayImage("file://"+PhotoData.get(position), imageView, options, new SimpleImageLoadingListener() {
				@Override
				public void onLoadingStarted(String imageUri, View view) {
					spinner.setVisibility(View.VISIBLE);					
				}

				@Override
				public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
					String message = null;
					switch (failReason.getType()) {
						case IO_ERROR:
							message = "Input/Output error";
							break;
						case DECODING_ERROR:
							message = "Image can't be decoded";
							break;
						case NETWORK_DENIED:
							message = "Downloads are denied";
							break;
						case OUT_OF_MEMORY:
							message = "Out Of Memory error";
							break;
						case UNKNOWN:
							message = "Unknown error";
							break;
					}
					Toast.makeText(view.getContext(), message, Toast.LENGTH_SHORT).show();

					spinner.setVisibility(View.GONE);
				}

				@Override
				public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
					spinner.setVisibility(View.GONE);					
				}
			});

			view.addView(imageLayout, 0);
			return imageLayout;
		}


		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view.equals(object);
		}

		@Override
		public void restoreState(Parcelable state, ClassLoader loader) {
			// TODO Auto-generated method stub
//			super.restoreState(state, loader);
		}



		@Override
		public Parcelable saveState() {
			// TODO Auto-generated method stub
			return null;
		}
		
		private boolean setText(int position){
			//==============
			// photo number
			//==============
			TextView tvNum = (TextView)findViewById(R.id.gallery_text_number);
			String pNum = "<"+(position+1)+"/"+PhotoData.size()+">";
			tvNum.setText(pNum);
			
			//==============
			// photo date
			//==============
//			TextView tvDate = (TextView)findViewById(R.id.gallery_text_date);
//			String pDate = ccCamUtil.changeDateFormat(PhotoDate.get(position)
//					, "yyyy-MM-dd a HH:mm:ss");
//			tvDate.setText(pDate);
			
			//==============
			// photo file size
			//==============
//			TextView tvSize = (TextView)findViewById(R.id.gallery_text_size);
//			double size1 = Double.parseDouble(PhotoDataSize.get(currentPhotoPos))/1024*0.001;
//			String pSize = "File Size :"+ (int)(size1*10.0+0.5)/10.0+"MB";
//			tvSize.setText(pSize);
			
			//==============
			// photo resolution
			//==============
//			TextView tvResolution = (TextView)findViewById(R.id.gallery_text_resolution);	
//			String pResolution = "picture size :"+appc.PhotoWidth.get(position)
//					+"x"+appc.PhotoHeight.get(position);			
//			tvResolution.setText(pResolution);
			
			return true;
		}		
	}
}
