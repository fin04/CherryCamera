package com.epriest.cherryCamera;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Process;

import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.epriest.cherryCamera.util.ccCamUtil;
import com.epriest.cherryCamera.util.ccFirebase;
import com.epriest.cherryCamera.util.logline;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

/**
 * @author Cherry Camera Main
 */
public class ccActivity extends Activity{

    //	public static final Collection<String> PRODUCT_CODE_TYPES = list("UPC_A", "UPC_E", "EAN_8", "EAN_13", "RSS_14");
//	public final Collection<String> ONE_D_CODE_TYPES =
//			list("UPC_A", "UPC_E", "EAN_8", "EAN_13", "CODE_39", "CODE_93", "CODE_128",
//					"ITF", "RSS_14", "RSS_EXPANDED");
//	public static final Collection<String> QR_CODE_TYPES = Collections.singleton("QR_CODE");
//	public static final Collection<String> DATA_MATRIX_TYPES = Collections.singleton("DATA_MATRIX");
//
//	public static final Collection<String> ALL_CODE_TYPES = null;
    private String TAG = this.getClass().getSimpleName();
    private String intentGetAction;

    private ccMenuset mSet;

    private final int REQUEST_ACCESS_CAMERA_PERMISSION = 122;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        logline.d(TAG, "==onCreate==");

        // permission set
        if (isGrantedPermission(Manifest.permission.CAMERA) &&
                isGrantedPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            setCreate();
        } else {
//			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_ACCESS_CAMERA_PERMISSION);
//			}
        }
    }

    public boolean isGrantedPermission(String permission) {
        return PackageManager.PERMISSION_GRANTED ==
                ContextCompat.checkSelfPermission(getApplicationContext(), permission);
    }

    private void setCreate() {
        // ===========
        // check camera
        // ===========
        if (!ccCamUtil.checkCameraHardware(this)) {
            Toast.makeText(this, "this device have not Camera", Toast.LENGTH_SHORT).show();
            finish();
        }

        // ===========
        // menuset
        // ===========
        mSet = new ccMenuset(this, intentGetAction);

        // ===========
        // check storage mount
        // ===========
        String ext = Environment.getExternalStorageState();
        if (!ext.equals(Environment.MEDIA_MOUNTED)) {
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setMessage(getResources().getString(R.string.unmount_sdcard));
            alert.setPositiveButton(getResources().getString(R.string.msg_yes), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    finish();
                }
            });
            alert.show();
            return;
        }

        // privacy info
        TextView privacy = (TextView) findViewById(R.id.tv_appinfo);
        privacy.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "http://epriest.tistory.com/3";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
            }
        });

        ccFirebase.setAdMob(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        switch (requestCode) {
            case REQUEST_ACCESS_CAMERA_PERMISSION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    setCreate();
                } else {
                    new AlertDialog.Builder(this)
                            .setCancelable(false)
                            .setMessage(getResources().getString(R.string.permission_denied))
                            .setPositiveButton(getResources().getString(R.string.button_ok),
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish();
                                        }
                                    })
                            .show();
                }
            }
            break;
        }
    }

    @Override
    protected void onResume() {
        logline.d(TAG, "==onResume==");
        super.onResume();
    }

    @Override
    protected void onPause() {
        logline.d(TAG, "==onPause==");
        super.onPause();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // QR reader
        final IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
//		final String resultQrUri = result.getContents();
        String resultFormat = null;
        if (result != null)
            resultFormat = result.getFormatName();

        if (resultFormat == null) {
        } else if (resultFormat.equals("QR_CODE")) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.msg_sbc_results + " [" + result.getFormatName() + "]")
                    .setMessage(result.getContents() + " \n")
                    .setPositiveButton(R.string.button_open_browser, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Uri uri = Uri.parse(result.getContents());
                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .show();
        } else if (resultFormat.equals("UPC_A") || resultFormat.equals("UPC_E") ||
                resultFormat.equals("EAN_8") || resultFormat.equals("EAN_13") ||
                resultFormat.equals("CODE_39") || resultFormat.equals("CODE_93") ||
                resultFormat.equals("CODE_128") || resultFormat.equals("ITF") || resultFormat.equals("DATA_MATRIX") ||
                resultFormat.equals("RSS_14") || resultFormat.equals("RSS_EXPANDED")) {
            new AlertDialog.Builder(this)
                    .setTitle(" [" + result.getFormatName() + "]")
                    .setMessage(result.getContents() + " \n")
                    .setPositiveButton(R.string.button_web_search, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_WEB_SEARCH);
                            intent.putExtra(SearchManager.QUERY, result.getContents());
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .show();
        } else {
        }
    }

//	private static Collection<String> list(String... values) {
//	    return Collections.unmodifiableCollection(Arrays.asList(values));
//	  }

    public void exitCamera() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(getResources().getString(R.string.alerttitle));
        alert.setMessage(getResources().getString(R.string.finishmessage));
        alert.setPositiveButton(getResources().getString(R.string.msg_yes), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });
        alert.setNegativeButton(getResources().getString(R.string.msg_no), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alert.show();
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        }
        return true;
    }

    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {

        if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_CAMERA) {
            return true;
        }
        return super.onKeyLongPress(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        logline.d(TAG, "==onDestroy==");
        System.gc();
        super.onDestroy();
        Process.killProcess(Process.myPid());
    }

}
