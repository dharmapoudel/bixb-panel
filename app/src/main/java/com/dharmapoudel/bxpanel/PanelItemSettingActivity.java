package com.dharmapoudel.bxpanel;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PanelItemSettingActivity extends AppCompatActivity implements  BillingProcessor.IBillingHandler {

    private BillingProcessor bp;
    private SharedPreferences prefs;

    private Map<String, String> applicationMap;

    private Intent intent;

    private static final String TAG = PanelItemSettingActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_itemsettings);

        //remove the shadow
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setTitle(R.string.app_name);


        //init the preference
        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        //retrieve which item was clicked
        intent = getIntent();

    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        //get all installed apps
        getInstalledAppList();

    }


    @Override
    protected void onResume() {
        super.onResume();

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!bp.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onProductPurchased(@NonNull String productId, @Nullable TransactionDetails details) {
        Toast.makeText(getApplicationContext(), getResources().getString(R.string.thank_you), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPurchaseHistoryRestored() {
        //Toast.makeText(getApplicationContext(), "History Restored!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBillingError(int errorCode, @Nullable Throwable error) {
        Toast.makeText(getApplicationContext(), getResources().getString(R.string.next_time), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBillingInitialized() {
        //Toast.makeText(getApplicationContext(), "Billing initialized!", Toast.LENGTH_SHORT).show();
    }


    public void showAppSelectDialog(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext(), R.style.BxPanelDialogTheme);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.app_dialog, null);
        builder.setView(dialogView);
        builder.setTitle(view.getContext().getResources().getString(R.string.select_apps));


        Log.e(TAG, "setting action for panel item: " + intent.getStringExtra(Constants.CURRENT_ITEM));

        String viewIndex = intent.getStringExtra(Constants.CURRENT_ITEM);
        String currentApp = prefs.getString(Constants.LIST_ITEM+"_"+viewIndex+"_action", "");
        CharSequence[] installedApps = applicationMap.keySet().toArray(new CharSequence[applicationMap.size()]);

        builder.setSingleChoiceItems(installedApps, getValueIndex(currentApp, installedApps), (DialogInterface dialog, int which) -> {
            //get the clicked item
            String appName = (applicationMap.keySet().toArray()[which]).toString();
            String packageName = applicationMap.get(appName);
            //update the preferences
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(Constants.LIST_ITEM+"_"+viewIndex+"_type", Constants.ACTION_OPEN_APP);
            editor.putString(Constants.LIST_ITEM+"_"+viewIndex+"_action", appName);
            editor.putString(Constants.LIST_ITEM+"_"+viewIndex+"_package", packageName);
            editor.apply();
            //close the dialog
            dialog.dismiss();
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();


    }

    private int getValueIndex(String currentApp, CharSequence[] installedApps) {
        if (currentApp != null && installedApps != null) {
            for (int i = installedApps.length - 1; i >= 0; i--) {
                if (installedApps[i].equals(currentApp)) {
                    return i;
                }
            }
        }
        return -1;
    }


    private void getInstalledAppList(){

        final PackageManager packageManager = getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> resInfoList = packageManager.queryIntentActivities(intent, 0);


        Map<String, String> appInfoMap = new HashMap<>();

        for(ResolveInfo resolveInfo : resInfoList) {
            try {
                String packageName = resolveInfo.activityInfo.packageName;
                ApplicationInfo appInfo = packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA);
                appInfoMap.put(packageManager.getApplicationLabel(appInfo).toString(), packageName);
            } catch (PackageManager.NameNotFoundException e) {
                //Do Nothing
            }
        }
        //applicationMap = appInfoMap;

        applicationMap = appInfoMap.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    }

}
