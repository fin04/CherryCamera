package com.epriest.cherryCamera.main;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;

public class SaveImageAsync extends AsyncTask<Void, String, Void> {
	private Context mContext;
    private int imageResourceID;

    private ProgressDialog mProgressDialog;

    public SaveImageAsync(Context context, int image) {
        mContext = context;
        imageResourceID = image;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.setMessage("Saving Image to SD Card");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }

    @Override
    protected Void doInBackground(Void... filePath) {
        try {
            Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), imageResourceID);

            ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream(); 
            bitmap.compress(CompressFormat.JPEG, 100, byteOutputStream); 
            byte[] mbitmapdata = byteOutputStream.toByteArray();
            ByteArrayInputStream inputStream = new ByteArrayInputStream(mbitmapdata);

            String baseDir = Environment.getExternalStorageDirectory().getAbsolutePath();
            String fileName = "mySavedImage.jpg";

            OutputStream outputStream = new FileOutputStream(baseDir + File.separator + fileName);
            byteOutputStream.writeTo(outputStream);

            byte[] buffer = new byte[128]; //Use 1024 for better performance
            int lenghtOfFile = mbitmapdata.length;
            int totalWritten = 0;
            int bufferedBytes = 0;

            while ((bufferedBytes = inputStream.read(buffer)) > 0) {
                totalWritten += bufferedBytes;
                publishProgress(Integer.toString((int) ((totalWritten * 100) / lenghtOfFile)));
                outputStream.write(buffer, 0, bufferedBytes);
            }

        } catch (IOException e) { e.printStackTrace(); }
        return null;

    }

    protected void onProgressUpdate(String... progress) {
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setProgress(Integer.parseInt(progress[0]));
    }

    @Override
    protected void onPostExecute(Void filename) {
        mProgressDialog.dismiss();
        mProgressDialog = null;
    }

}
