package com.dharmapoudel.bxpanel;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.util.Log;

public class Util {
    private static final String TAG = Util.class.getSimpleName();


    public static Drawable getIconFromTag(int tag, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        PackageManager packageManager = context.getPackageManager();

        Drawable iconDrawable;
        String type = prefs.getString(Constants.LIST_ITEM + "_" + tag + "_type", "");
        switch (type){
            case Constants.ACTION_OPEN_APP:
                iconDrawable = getAppIcon(tag, prefs, packageManager);
                break;
            default:
                iconDrawable = context.getResources().getDrawable(R.drawable.add_icon, null);
        }
        return iconDrawable;
    }


    public static Drawable getAppIcon(int i, SharedPreferences prefs, PackageManager packageManager) {
        //String action = prefs.getString(Constants.LIST_ITEM + "_" + i + "_action", "");
        String packageName = prefs.getString(Constants.LIST_ITEM + "_" + i + "_package", "");

        Drawable iconDrawable = null;
        try {
            iconDrawable = packageManager.getApplicationIcon(packageName);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, e.getMessage());
        }
        return iconDrawable;
    }

}
