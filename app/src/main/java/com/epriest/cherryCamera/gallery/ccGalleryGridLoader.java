package com.epriest.cherryCamera.gallery;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.epriest.cherryCamera.R;
import com.epriest.cherryCamera.gallery.ccPhotoInfo.PhotoItem;
import com.epriest.cherryCamera.util.ccPicUtil;
import com.epriest.cherryCamera.util.ccCamUtil;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

public class ccGalleryGridLoader extends Activity {
	
	private GridView gridView;
	private ImageAdapter customGridAdapter;
	private int lcdWidth, lcdHeight;
	private ArrayList<PhotoItem> PhotoData = new ArrayList<PhotoItem>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gallery_thumblayout);

		MobileAds.initialize(getApplicationContext(), getString(R.string.banner_ad_unit_id));
		AdView mAdView = (AdView) findViewById(R.id.adView);
		AdRequest adRequest = new AdRequest.Builder()
				.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)        // All emulators
//				.addTestDevice("F51F2A6C4A6290BDB0C711BAE2697457")
//				.addTestDevice("7763B915C09B3BF4C69010EE31F744D6")
				.build();
		mAdView.loadAd(adRequest);

		DisplayMetrics displayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		lcdWidth = displayMetrics.widthPixels;
		lcdHeight = displayMetrics.heightPixels;
		
		getData();		
		imageLoder_init();
		gridviewInit();
		
//		for(PhotoItem item : PhotoData){
//			ccCamUtil.setFilenameToExifDate(item.PhotoName,item.PhotoData);
//		}
	}
	
	private void imageLoder_init(){
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
        .threadPriority(Thread.NORM_PRIORITY - 2)
        .memoryCacheExtraOptions(lcdWidth/4, lcdHeight/5) // default = device screen dimensions
        .denyCacheImageMultipleSizesInMemory()
        .discCacheFileNameGenerator(new Md5FileNameGenerator())
        .tasksProcessingOrder(QueueProcessingType.LIFO)
        .writeDebugLogs() // Remove for release app
        .build();
		ImageLoader.getInstance().init(config);
	}
	
	private void gridviewInit(){
		gridView = (GridView) findViewById(R.id.gallery_gridView);
		customGridAdapter = new ImageAdapter(this, R.layout.gallery_gridview_item, PhotoData);
		gridView.setAdapter(customGridAdapter);
		gridView.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {			
				Intent i = new Intent(ccGalleryGridLoader.this, ccGalleryPicviewAct.class);
				i.putExtra("pos", position);
				ccGalleryGridLoader.this.startActivityForResult(i, 100);
			}           
        });
	}
	
	private ArrayList<PhotoItem> getData() {
		PhotoData = ccPicUtil.getContentList(this);
		return PhotoData;
	}
	
//	private ArrayList<ImageItem> getData() {		
//		final ArrayList<ImageItem> imageItems = new ArrayList<ImageItem>();		
//		for (int i = 0; i < info.photoCount; i++) {
//			Long id = Long.parseLong(PhotoId.get(i));
//			String date = PhotoDate.get(i);
////			String picsize = appc.PhotoWidth.get(i)+"x"+appc.PhotoHeight.get(i);
//			String picsize = "";
//			String picname = PhotoName.get(i);
//			int num = i;
//			imageItems.add(new ImageItem(num, id, date, picsize, picname));
//		} 
//		return imageItems; 
//	}
	
	private void changeGridView(int currentPhotoPos){
		customGridAdapter.data.clear();
		customGridAdapter.data = getData();
		int position = currentPhotoPos;
		gridView.setSelection(position > 0 ? position - 1 : 0);
		customGridAdapter.notifyDataSetInvalidated();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch(requestCode){
		case 100:
			if(resultCode == RESULT_OK){
				int num = data.getIntExtra("pic_num", 0);
				if(num == -1)
					finish();
				else
					changeGridView(data.getIntExtra("pic_num", 0));
			}
		}
	}



	private static class ImageAdapter extends BaseAdapter {

		private Context context;
		private int itemResourceId;
		public ArrayList<PhotoItem> data = new ArrayList<PhotoItem>();
//		private ccGalleryUtil galleryUtil;
		private LayoutInflater inflater;		
		private DisplayImageOptions options;
		
		public ImageAdapter(Context context, int itemResourceId,
				ArrayList<PhotoItem> data) {
			this.itemResourceId = itemResourceId;
			this.context = context;
//			this.galleryUtil = galleryUtil; 
			this.data = data;
			inflater = LayoutInflater.from(context);
			
			options = new DisplayImageOptions.Builder()
			.showImageOnLoading(R.drawable.thumb_row)
			.showImageForEmptyUri(R.drawable.thumb_row)
			.showImageOnFail(R.drawable.wrong_file)
			.cacheInMemory(true)
			.cacheOnDisk(true)
			.considerExifParams(true)
			.bitmapConfig(Bitmap.Config.RGB_565)
			.build();
		}
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return data.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {			
			View row = convertView;
			ViewHolder holder = null;
	 
			if (row == null) {
				LayoutInflater inflater = ((Activity) context).getLayoutInflater();
				row = inflater.inflate(itemResourceId, parent, false);
				holder = new ViewHolder();
				holder.imageNumber = (TextView) row.findViewById(R.id.tv_gallery_thumnumber);
				holder.imageTitle = (TextView) row.findViewById(R.id.tv_gallery_thumtext);
				holder.image = (ImageView) row.findViewById(R.id.gallery_thumimage);
				row.setTag(holder);
			} else {
				holder = (ViewHolder) row.getTag();
			}
			
			PhotoItem item = (PhotoItem) data.get(position);
			holder.imageNumber.setText(Integer.toString(data.size()-position));
//			holder.imageTitle.setText(item.getPicname());
			holder.imageTitle.setText(ccCamUtil.changeDateFormat(item.PhotoDate, "yyyy-MM-dd"));
			
//			final ViewHolder holder_ = holder;
			String uri = "file://"+item.PhotoData;
			ImageLoader.getInstance()
					.displayImage(uri, holder.image, options, new SimpleImageLoadingListener() {
						@Override
						public void onLoadingStarted(String imageUri, View view) {
//							holder_.progressBar.setProgress(0);
//							holder_.progressBar.setVisibility(View.VISIBLE);
						}

						@Override
						public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
//							holder_.progressBar.setVisibility(View.GONE);
						}

						@Override
						public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
//							holder_.progressBar.setVisibility(View.GONE);
						}
					}, new ImageLoadingProgressListener() {
						@Override
						public void onProgressUpdate(String imageUri, View view, int current, int total) {
//							holder_.progressBar.setProgress(Math.round(100.0f * current / total));
						}
					});
			return row;
		}
		
		static class ViewHolder {
			TextView imageNumber;
			TextView imageTitle;
			ImageView image;
			CheckBox cBox;
			ProgressBar progressBar;
		}		
	}

}
