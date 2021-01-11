package com.dharmapoudel.bxpanel;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.bumptech.glide.Glide;

public class MainActivity extends AppCompatActivity implements  BillingProcessor.IBillingHandler {

    private BillingProcessor bp;
    private SharedPreferences prefs;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //remove the shadow
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setTitle(R.string.app_name);


        //init the preference
        prefs = PreferenceManager.getDefaultSharedPreferences(this);


        //initialize billing
        bp = new BillingProcessor(this, Constants.LICENSE_KEY, this);
        bp.initialize();

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

    }

    @Override
    protected void onResume() {
        super.onResume();

        //update the panel items shown
        updatePanelItems();

    }



    private void updatePanelItems() {
        LinearLayout panelItemsLayout = findViewById(R.id.panelItems);
        int count = prefs.getInt(Constants.NO_OF_ITEMS, 5);
        //show these items
        for(int i = 0 ; i < count; i++ ){
            String type = prefs.getString(Constants.LIST_ITEM+"_"+i+"_type", "");
            String action = prefs.getString(Constants.LIST_ITEM+"_"+i+"_action", "");
            String extra = prefs.getString(Constants.LIST_ITEM+"_"+i+"_extra", "");
            View linearLayout =  panelItemsLayout.getChildAt(i);

            updatePanelItemText(linearLayout, type, action, extra);
            updatePanelItemIcon(linearLayout, type, action, extra);

            panelItemsLayout.getChildAt(i).setVisibility(View.VISIBLE);
        }

        //hide the rest
        SharedPreferences.Editor editor = prefs.edit();
        for(int i = count; i < 10; i++ ){
            editor.remove(Constants.LIST_ITEM+"_"+i+"_type");
            editor.remove(Constants.LIST_ITEM+"_"+i+"_action");
            editor.remove(Constants.LIST_ITEM+"_"+i+"_extra");
            editor.apply();
            panelItemsLayout.getChildAt(i).setVisibility(View.GONE);
        }
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


    public void openAppSettings(View v){
        startActivity(new Intent(this, PreferenceActivity.class));
    }

    public void openPanelItemSettings(View v){
        Intent intent = new Intent(this, PanelItemSettingActivity.class);
        intent.putExtra(Constants.CURRENT_ITEM, v.getTag().toString());
        startActivity(intent);
    }

    private void updatePanelItemText(View view, String actionType, String action, String extra){
        ViewGroup vg = (ViewGroup) ((ViewGroup) view).getChildAt(1);

        String itemText =  getActionText(actionType); //Constants.ACTION_OPEN_APP.equalsIgnoreCase(actionType) ? getString(R.string.open_app): getString(R.string.panel_item);
        TextView textViewTitle =   (TextView) vg.getChildAt(0);
        textViewTitle.setText(itemText);

        TextView textViewDescription =   (TextView) vg.getChildAt(1);
        textViewDescription.setText((action == "") ? getString(R.string.panel_item_description) : action);

    }


    private void updatePanelItemIcon(View view, String actionType, String action, String extra){
        ImageView imageView = (ImageView)((ViewGroup) view).getChildAt(0);
        imageView.setBackground(null);
        Drawable iconDrawable = IconFactory.getIconFromTag(actionType, action, extra, view.getContext());
        Glide.with(getApplicationContext()).load(iconDrawable).into(imageView);
    }

    private String getActionText( String actionType) {

        int actionText;
        switch(actionType){
            case Constants.ACTION_OPEN_APP:
                actionText = R.string.open_app;
                break;

            case Constants.ACTION_NAVIGATE:
                actionText =  R.string.navigation;
                break;

            case Constants.ACTION_MEDIA:
                actionText =  R.string.media;
                break;

            case Constants.ACTION_UTILS:
                actionText = R.string.utils;
                break;

            default:
                actionText = R.string.panel_item;
        }

        return getResources().getString(actionText);
    }


}