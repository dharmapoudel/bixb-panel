package com.dharmapoudel.bxpanel;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.PurchaseInfo;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
        Objects.requireNonNull(getSupportActionBar()).setElevation(0);
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
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onProductPurchased(@NonNull String productId, @Nullable PurchaseInfo details) {
        //Toast.makeText(getApplicationContext(), getResources().getString(R.string.thank_you), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPurchaseHistoryRestored() {
        //Toast.makeText(getApplicationContext(), "History Restored!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBillingError(int errorCode, @Nullable Throwable error) {
        //Toast.makeText(getApplicationContext(), getResources().getString(R.string.next_time), Toast.LENGTH_SHORT).show();
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

        String viewIndex = intent.getStringExtra(Constants.CURRENT_ITEM);
        String currentApp = prefs.getString(Constants.LIST_ITEM+"_"+viewIndex+"_action", "");
        CharSequence[] installedApps = applicationMap.keySet().toArray(new CharSequence[0]);

        builder.setSingleChoiceItems(installedApps, getValueIndex(currentApp, installedApps), (DialogInterface dialog, int which) -> {
            //get the clicked item
            String appName = (applicationMap.keySet().toArray()[which]).toString();
            String packageName = applicationMap.get(appName);
            //update the preferences
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(Constants.LIST_ITEM+"_"+viewIndex+"_type", Constants.ACTION_OPEN_APP);
            editor.putString(Constants.LIST_ITEM+"_"+viewIndex+"_action", appName);
            editor.putString(Constants.LIST_ITEM+"_"+viewIndex+"_extra", packageName);
            editor.apply();
            //close the dialog
            dialog.dismiss();

            Toast.makeText(view.getContext(), "Action Set to open "+ appName, Toast.LENGTH_SHORT).show();
            finish();
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

    public void selectNavigationAction(View view) {

        String viewIndex = intent.getStringExtra(Constants.CURRENT_ITEM);
        int viewId =  Integer.parseInt(view.getTag().toString());
        String action = (viewId > 1) ? ( viewId > 2 ? Constants.BACK : Constants.RECENTS) : Constants.HOME;

        //update the preferences
        updateItemPreferences(viewIndex, Constants.ACTION_NAVIGATE, action, "");


        //show toast mesage briefly
        Toast.makeText(view.getContext(), "Action Set to navigate "+ action, Toast.LENGTH_SHORT).show();
        finish();

    }

    public void selectUtilityAction(View view) {

        String viewIndex = intent.getStringExtra(Constants.CURRENT_ITEM);
        String action = getActionFromViewId(Integer.parseInt(view.getTag().toString()));

        //update the preferences
        updateItemPreferences(viewIndex, Constants.ACTION_UTILS, action, "");

        //show toast mesage briefly
        Toast.makeText(view.getContext(), "Action Set to utility  "+ action, Toast.LENGTH_SHORT).show();
        finish();

    }

    public void selectMediaAction(View view) {

        String viewIndex = intent.getStringExtra(Constants.CURRENT_ITEM);
        String action = getActionFromViewId(Integer.parseInt(view.getTag().toString()));

        //update the preferences
        updateItemPreferences(viewIndex, Constants.ACTION_MEDIA, action, "");

        //show toast mesage briefly
        Toast.makeText(view.getContext(), "Action Set to media  "+ action, Toast.LENGTH_SHORT).show();
        finish();
    }

    private void updateItemPreferences(String viewIndex, String actionType, String action, String extra){
        //update the preferences
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Constants.LIST_ITEM+"_"+viewIndex+"_type", actionType);
        editor.putString(Constants.LIST_ITEM+"_"+viewIndex+"_action", action);
        editor.putString(Constants.LIST_ITEM+"_"+viewIndex+"_extra", extra);
        editor.apply();
    }

    private String getActionFromViewId(int viewId) {
        String action;
        switch(viewId){
            case 4:
                action =  Constants.MEDIA_PLAYPAUSE;
                break;
            case 5:
                action =  Constants.MEDIA_PREVIOUS;
                break;
            case 6:
                action =  Constants.MEDIA_NEXT;
                break;
            case 7:
                action =  Constants.FLASHLIGHT;
                break;
            case 8:
            default:
                action =  Constants.WIFI;
                break;
        }
        return action;
    }


}
