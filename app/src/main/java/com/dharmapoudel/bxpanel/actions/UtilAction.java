package com.dharmapoudel.bxpanel.actions;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import com.dharmapoudel.bxpanel.Constants;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;

public class UtilAction implements Action{

    private static final String TAG = UtilAction.class.getSimpleName();

    private CameraManager camManager;


    @Override
    public void performAction(View v, SharedPreferences prefs) {
        String viewTag = v.getTag().toString();
        String type = prefs.getString(Constants.LIST_ITEM+"_"+viewTag+"_type", "");
        String action = prefs.getString(Constants.LIST_ITEM+"_"+viewTag+"_action", "");
        String extra = prefs.getString(Constants.LIST_ITEM+"_"+viewTag+"_extra", Constants.FLASHLIGHT_OFF);

        Log.i(TAG, "performing action actiontype " + action);

        switch (action) {
            case Constants.WIFI:
                restartWifi(v.getContext(), viewTag, prefs);
                break;

            case Constants.FLASHLIGHT:
            default:
                if(Constants.FLASHLIGHT_OFF.equalsIgnoreCase(extra)){
                    flashLightOn(v.getContext(), viewTag, prefs);
                } else{
                    flashLightOff(v.getContext(), viewTag, prefs);
                }
                break;
        }
    }

    private void restartWifi(Context context, String viewTag, SharedPreferences prefs) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if( wifiManager.isWifiEnabled()){
            wifiManager.setWifiEnabled(false);
            wifiManager.setWifiEnabled(true);
        }

    }

    private void flashLightOn(Context context,  String viewTag, SharedPreferences prefs) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Constants.LIST_ITEM+"_"+viewTag+"_extra", Constants.FLASHLIGHT_ON);
        editor.apply();

        try {
            camManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
            String cameraId;
            if (camManager != null) {
                cameraId = camManager.getCameraIdList()[0];
                camManager.setTorchMode(cameraId, true);
            }
        } catch (CameraAccessException e) {
            Log.e(TAG, e.toString());
        }
    }

    private void flashLightOff(Context context,  String viewTag, SharedPreferences prefs) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Constants.LIST_ITEM+"_"+viewTag+"_extra", Constants.FLASHLIGHT_OFF);
        editor.apply();

        try {
            String cameraId;
            camManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
            if (camManager != null) {
                cameraId = camManager.getCameraIdList()[0];
                camManager.setTorchMode(cameraId, false);
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }


    private void takeScreenshot(View v, Context context) {
        Date now = new Date();
        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);

        try {
            // image naming and path  to include sd card  appending name you choose for file
            String mPath = Environment.getExternalStorageDirectory().toString() + "/" + now + ".jpg";

            // create bitmap screen capture
            //View v1 = //v.getDisplay().getWindow().getDecorView().getRootView();
            v.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(v.getDrawingCache());
            v.setDrawingCacheEnabled(false);

            File imageFile = new File(mPath);

            FileOutputStream outputStream = new FileOutputStream(imageFile);
            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (Throwable e) {
            // Several error may come out with file handling or DOM
            e.printStackTrace();
        }
    }


}
