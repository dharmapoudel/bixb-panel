package com.dharmapoudel.bxpanel.actions;

import android.content.SharedPreferences;
import android.view.View;

public interface Action {

    void performAction(View v, SharedPreferences prefs);

}
