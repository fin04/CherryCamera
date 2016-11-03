package com.epriest.cherryCamera.old;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.epriest.cherryCamera.ApplicationClass;
import com.epriest.cherryCamera.R;
import com.epriest.cherryCamera.gallery.ImageItem;
import com.epriest.cherryCamera.util.IN;
import com.epriest.cherryCamera.util.ccPicUtil;
import com.epriest.cherryCamera.util.ccCamUtil;

public class GridViewAdapter extends BaseAdapter {
	public ArrayList<String> PhotoData = new ArrayList<String>();
	private Context context;
	private int itemResourceId;
	public ArrayList<ImageItem> data = new ArrayList<ImageItem>();
	BitmapFactory.Options bmOptions;
	ApplicationClass appc;
//	private ccGalleryUtil galleryUtil;
	
	public GridViewAdapter(Context context, int itemResourceId,
			ArrayList<ImageItem> data) {
		this.itemResourceId = itemResourceId;
		this.context = context;
//		this.galleryUtil = galleryUtil; 
		this.appc = appc;
		this.data = data;
		bmOptions = new BitmapFactory.Options();
		bmOptions.inDither = true;
//		bmOptions.inJustDecodeBounds = false;
		bmOptions.inSampleSize = 2;
	}	
	
	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		Toast.makeText(context, ""+position, Toast.LENGTH_SHORT).show();
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
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
		
		ImageItem item = (ImageItem) data.get(position);

//		Bitmap bitmap = MediaStore.Images.Thumbnails.getThumbnail(
//                context.getContentResolver(), item.getImageID(),
//                MediaStore.Images.Thumbnails.MINI_KIND, bmOptions);
		String thumbPath = Environment.getExternalStorageDirectory()+IN.cPath+IN.cThumPath;
		String str = PhotoData.get(position);
		String[] fileName = str.split(File.separator);
		String name = thumbPath+File.separator+"thum_"+fileName[fileName.length-1];
		Bitmap bitmap;
		if(ccCamUtil.checkIsFile(name)){
			bitmap = ccPicUtil.previewCooking(name, 0
					,appc.lcdWidth
	    			,appc.lcdWidth);
			
		}else{
			bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.gallery_board_mid);
			
		}
		holder.image.setImageBitmap(bitmap);
//		Log.d("","thume size = "+bitmap.getWidth()+","+bitmap.getHeight());
		holder.imageNumber.setText(position);
		holder.imageTitle.setText(ccCamUtil.changeDateFormat(item.getDate(), "yyyy-MM-dd"));
//				+"\n("+item.getPicsize()+")");
		/*if(isCheckBox)
			holder.cBox.setVisibility(View.VISIBLE);
		else
			holder.cBox.setVisibility(View.INVISIBLE);
		
		final int checkPos = position;
		holder.cBox.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if(isChecked)
					checkList.set(checkPos, true);
				else
					checkList.set(checkPos, false);
			}
		});
		
		if(checkList.get(checkPos))
			holder.cBox.setChecked(true);
		else
			holder.cBox.setChecked(false);*/
		
		return row;
	}
	
	
 
	static class ViewHolder {
		TextView imageNumber;
		TextView imageTitle;
		ImageView image;
		CheckBox cBox;
	}

}
