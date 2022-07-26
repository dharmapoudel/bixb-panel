package com.dharmapoudel.bxpanel.actions;

import android.accessibilityservice.AccessibilityService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import com.dharmapoudel.bxpanel.core.BxPanelAccessibilityService;
import com.dharmapoudel.bxpanel.Constants;

import static android.accessibilityservice.AccessibilityService.GLOBAL_ACTION_BACK;

public class NavigateAction implements Action{
    private static final String TAG = NavigateAction.class.getSimpleName();


    @Override
    public void performAction(View v, SharedPreferences prefs)  {
        String viewTag = v.getTag().toString();
        String type = prefs.getString(Constants.LIST_ITEM+"_"+viewTag+"_type", "");
        String action = prefs.getString(Constants.LIST_ITEM+"_"+viewTag+"_action", "");
        String extra = prefs.getString(Constants.LIST_ITEM+"_"+viewTag+"_extra", "");

        Log.i(TAG, "performing action navigate " + action);

        switch (action) {
            case Constants.HOME:
                navigateToHome(v.getContext());
                break;
            case Constants.RECENTS:
                naviagateToRecents(v.getContext());
                break;

            case Constants.BACK:
            default:
                naviagateBack(v);
                break;
        }

    }


    private  void navigateToHome(Context context){
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(startMain);
    }

    private void naviagateToRecents(Context context) {
        //new BxPanelAccessibilityService().performGlobalAction(GLOBAL_ACTION_RECENTS);
        new BxPanelAccessibilityService().performGlobalAction(AccessibilityService.GLOBAL_ACTION_RECENTS);

    }

    private void naviagateBack(View v) {
        new BxPanelAccessibilityService().performGlobalAction(GLOBAL_ACTION_BACK);
        v.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK));
        v.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BACK));
    }


}
