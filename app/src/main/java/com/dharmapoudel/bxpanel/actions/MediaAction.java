package com.dharmapoudel.bxpanel.actions;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import com.dharmapoudel.bxpanel.Constants;

public class MediaAction implements Action {

    private static final String TAG = MediaAction.class.getSimpleName();


    @Override
    public void performAction(View v, SharedPreferences prefs) {
        String viewTag = v.getTag().toString();
        String type = prefs.getString(Constants.LIST_ITEM + "_" + viewTag + "_type", "");
        String action = prefs.getString(Constants.LIST_ITEM + "_" + viewTag + "_action", "");
        String extra = prefs.getString(Constants.LIST_ITEM + "_" + viewTag + "_extra", Constants.MEDIA_PAUSE);

        Log.i(TAG, "performing action actiontype " + action);

        switch (action) {
            case Constants.MEDIA_PREVIOUS:
                mediaPrevious(v.getContext());
                break;

            case Constants.MEDIA_NEXT:
                mediaNext(v.getContext());
                break;

            case Constants.MEDIA_PLAYPAUSE:
            default:
                toggleMedia(v.getContext());
                break;
        }
    }

    private void mediaPrevious(Context context) {
        try {
            AudioManager am = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
            KeyEvent downEvent = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PREVIOUS);
            am.dispatchMediaKeyEvent(downEvent);

            KeyEvent upEvent = new KeyEvent(KeyEvent.ACTION_UP,KeyEvent.KEYCODE_MEDIA_PREVIOUS);
            am.dispatchMediaKeyEvent(upEvent);

            am.dispatchMediaKeyEvent(downEvent);
            am.dispatchMediaKeyEvent(upEvent);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }


    private void mediaNext(Context context) {
        try {
            AudioManager am = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
            KeyEvent downEvent = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_NEXT);
            am.dispatchMediaKeyEvent(downEvent);

            KeyEvent upEvent = new KeyEvent(KeyEvent.ACTION_UP,KeyEvent.KEYCODE_MEDIA_NEXT);
            am.dispatchMediaKeyEvent(upEvent);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private void toggleMedia(Context context) {
        try {
            AudioManager am = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
            KeyEvent downEvent = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE);
            am.dispatchMediaKeyEvent(downEvent);

            KeyEvent upEvent = new KeyEvent(KeyEvent.ACTION_UP,KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE);
            am.dispatchMediaKeyEvent(upEvent);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

}
