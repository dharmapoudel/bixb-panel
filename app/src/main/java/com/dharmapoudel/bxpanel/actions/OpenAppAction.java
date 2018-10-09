package com.dharmapoudel.bxpanel.actions;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.view.View;

import com.dharmapoudel.bxpanel.Constants;

public class OpenAppAction implements Action{
    private static final String TAG = OpenAppAction.class.getSimpleName();


    @Override
    public void performAction(View v, SharedPreferences prefs) {
        String viewTag = v.getTag().toString();
        String packageName = prefs.getString(Constants.LIST_ITEM + "_" + viewTag + "_package", "");

        Log.i(TAG, "opening app: " + prefs.getString(Constants.LIST_ITEM + "_" + viewTag + "_action", ""));
        startNewActivity(v.getContext(), packageName);

    }


    public static void startNewActivity(Context context, String packageName) {
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
        if (intent == null) {
            // Bring user to the market or let them choose an app
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("market://details?id=" + packageName));
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }


}
