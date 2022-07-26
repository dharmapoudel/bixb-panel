
package com.dharmapoudel.bxpanel;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.dharmapoudel.bxpanel.actions.ActionFactory;

/**
 * An Activity which allows selecting the animator duration scale from a full list, accessed by
 * long pressing the quick action tile.
 */
public class PanelActivity extends Activity {

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private int currentItem, lastItem, count;

    private boolean waitOnFirstItem;
    private ViewGroup panel;
    private Handler panelHideHandler, itemClickHandler;

    private String TAG = PanelActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();
        if(extras == null || !extras.containsKey("INDEX")){
            startActivity(new Intent(this, MainActivity.class));
            finish();
        } else {

            //init the preference
            prefs = PreferenceManager.getDefaultSharedPreferences(this);
            count = prefs.getInt(Constants.NO_OF_ITEMS, 5);
            int startItem = prefs.getInt(Constants.ITEM_TO_START, 2); //0,1,2,3,4

            //Remove title bar
            requestWindowFeature(Window.FEATURE_NO_TITLE);

            //Remove notification bar
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

            //set the content layout
            setContentView(R.layout.activity_bxpanel);

            //populate all the items on the panel
            attachPanelItems();


            //left or right edge
            boolean showOnRight = prefs.getBoolean(Constants.SHOW_ON_RIGHT, false);
            int panelSide = (showOnRight) ? Gravity.END : Gravity.START;
            getWindow().setGravity(Gravity.CENTER | panelSide);


            //override the transitions
            if (savedInstanceState == null) {
                if (showOnRight)
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                else
                    overridePendingTransition(R.anim.slide_right, R.anim.slide_out_left);
            }


            int index = extras.getInt("INDEX");
            currentItem = (startItem + index) % count;

            //remember last position
            boolean rememberLastPosition = prefs.getBoolean(Constants.REMEMBER_LAST_POSITION, true);
            if (rememberLastPosition) {
                currentItem = ((prefs.getInt(Constants.CURRENT_ITEM, startItem)) % count) - 1;
            }

            //should we not auto trigger on first item
            waitOnFirstItem = prefs.getBoolean(Constants.WAIT_ON_FIRST, false);

        }

    }

    private void attachPanelItems() {
        panel = findViewById(R.id.panel);
        for(int i = 0 ; i < count; i++ ){
            View panelItem = getLayoutInflater().inflate(R.layout.bxpanel_item, null);
            panelItem.setTag(i);
            ImageView imageView = (ImageView)((ViewGroup) panelItem).getChildAt(0);
            imageView.setBackground(null);


            String type = prefs.getString(Constants.LIST_ITEM+"_"+i+"_type", "");
            String action = prefs.getString(Constants.LIST_ITEM+"_"+i+"_action", "");
            String extra = prefs.getString(Constants.LIST_ITEM+"_"+i+"_extra", "");

            Drawable albumArtPath = IconFactory.getIconFromTag(type, action, extra, getApplicationContext());
            Glide.with(getApplicationContext()).load(albumArtPath).into(imageView);

            panel.addView(panelItem);
        }
    }


    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();

        //handlers
        itemClickHandler = (itemClickHandler != null) ? itemClickHandler : new Handler();
        panelHideHandler = (panelHideHandler != null) ? panelHideHandler : new Handler();

        //get current and last item
        currentItem = (currentItem + 1 ) % count;
        lastItem = ((currentItem-1) < 0) ? (count-1) : (currentItem -1);


        //reset last item
        View lastView = panel.getChildAt(lastItem);
        lastView.setBackground(null);
        ((ViewGroup) lastView).getChildAt(0).startAnimation(AnimationUtils.loadAnimation(this, R.anim.fab_close_15_to_1));


        //animate icon and change bg of current item
        View currentView = panel.getChildAt(currentItem);
        currentView.setBackgroundColor(getResources().getColor(R.color.shadow));
        ((ViewGroup) currentView).getChildAt(0).startAnimation(AnimationUtils.loadAnimation(this, R.anim.fab_open_1_to_15));


        //save the current item number - whether you click it or not
        editor = prefs.edit();
        editor.putInt(Constants.CURRENT_ITEM, currentItem);
        editor.apply();

        //automatically click item after specified seconds
        boolean autoTrigger = prefs.getBoolean(Constants.AUTO_TRIGGER, true);
        int autoTriggerInterval = prefs.getInt(Constants.AUTO_TRIGGER_AFTER, 2000);
        if(autoTrigger && !waitOnFirstItem) {
            itemClickHandler.removeCallbacksAndMessages(null);
            itemClickHandler.postDelayed(() -> currentView.performClick(), autoTriggerInterval);
        }

        //skipFirstItem
        waitOnFirstItem = false;

        //automatically close after specified seconds
        boolean autoClosePanel = prefs.getBoolean(Constants.AUTO_CLOSE, false);
        int autoCloseInterval = prefs.getInt(Constants.AUTO_CLOSE_AFTER, 3000);
        if(autoClosePanel) {
            panelHideHandler.removeCallbacksAndMessages(null);
            panelHideHandler.postDelayed(() -> {
                itemClickHandler.removeCallbacksAndMessages(null);
                finish();
            }, autoCloseInterval);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        itemClickHandler.removeCallbacksAndMessages(null);
        panelHideHandler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    protected  void onDestroy(){
        super.onDestroy();
    }



    public void clickView(View v){

        //remove background from last item
        panel.getChildAt(currentItem).setBackground(null);

        currentItem = Integer.valueOf(v.getTag().toString());
        lastItem = ((currentItem-1) < 0) ? (count-1) : (currentItem -1);

        //save the current item number
        editor = prefs.edit();
        editor.putInt(Constants.CURRENT_ITEM, currentItem);
        editor.apply();

        //remove any existing click callbacks
        itemClickHandler.removeCallbacksAndMessages(null);

        //vibrate for given interval
        boolean vibrate = prefs.getBoolean(Constants.TRIGGER_VIBRATION, true);
        int vibrationDuration = prefs.getInt(Constants.TRIGGER_DURATION, 2);
        if(vibrate) {
            ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(vibrationDuration);
        }


        //set background on current item/ animate current item icon
        v.setBackgroundColor(getResources().getColor(R.color.shadow, null));
        Animation closeAnimation = AnimationUtils.loadAnimation(this, R.anim.fab_close_15_to_1_slow);
        ((ViewGroup) v).getChildAt(0).startAnimation(closeAnimation);

        closeAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) { }
            @Override
            public void onAnimationEnd(Animation animation) {
                //perform action
                String actionType = prefs.getString(Constants.LIST_ITEM + "_" + v.getTag().toString() + "_type", "");
                assert actionType != null;
                ActionFactory.getActionInstance(actionType).performAction(v, prefs);

                //close panel
                boolean closePanelOnClick = prefs.getBoolean(Constants.CLOSE_PANEL_ON_ITEM_CLICK, true);
                if(closePanelOnClick){
                    finish();
                }

             }
            @Override
            public void onAnimationRepeat(Animation animation) { }
        });

    }

}
