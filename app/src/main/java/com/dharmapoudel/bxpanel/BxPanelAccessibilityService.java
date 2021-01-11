package com.dharmapoudel.bxpanel;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

public class BxPanelAccessibilityService extends AccessibilityService implements SensorEventListener {

    private static final String TAG = BxPanelAccessibilityService.class.getSimpleName();

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        Log.i(TAG, accessibilityEvent.toString());
        // get the source node of the event
        //AccessibilityNodeInfo nodeInfo = accessibilityEvent.getSource();

        // Use the event and node information to determine
        // what action to take

        // take action on behalf of the user
       // nodeInfo.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);

        // recycle the nodeInfo object
        //nodeInfo.recycle();
    }

    @Override
    public void onInterrupt() {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
