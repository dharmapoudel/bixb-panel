package com.dharmapoudel.bxpanel;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.util.Log;

public class IconFactory {
    private static final String TAG = IconFactory.class.getSimpleName();


    public static Drawable getIconFromTag(String actionType, String action, String extra, Context context) {
        PackageManager packageManager = context.getPackageManager();

        Drawable iconDrawable;
        switch (actionType){
            case Constants.ACTION_OPEN_APP:
                iconDrawable = getAppIcon(extra, packageManager);
                break;

            case Constants.ACTION_NAVIGATE: {
                Drawable navDrawable;
                switch (action) {
                    case Constants.HOME:
                        navDrawable = context.getResources().getDrawable(R.drawable.navigation_home, null);
                        break;
                    case Constants.RECENTS:
                        navDrawable = context.getResources().getDrawable(R.drawable.navigation_recents, null);
                        break;

                    case Constants.BACK:
                    default:
                        navDrawable = context.getResources().getDrawable(R.drawable.navigation_back, null);
                        break;
                }
                iconDrawable = navDrawable;
                break;
            }

            case Constants.ACTION_MEDIA: {
                Drawable navDrawable;
                switch (action) {
                    case Constants.MEDIA_PREVIOUS:
                        navDrawable = context.getResources().getDrawable(R.drawable.media_previous, null);
                        break;

                    case Constants.MEDIA_NEXT:
                        navDrawable = context.getResources().getDrawable(R.drawable.media_next, null);
                        break;

                    case Constants.MEDIA_PLAYPAUSE:
                    default:
                        navDrawable = context.getResources().getDrawable(R.drawable.media_playpause, null);
                        break;
                }
                iconDrawable = navDrawable;
                break;
            }

            case Constants.ACTION_UTILS:{
                Drawable drawable;
                switch (action) {
                    case Constants.WIFI:
                        drawable = context.getResources().getDrawable(R.drawable.util_wifi, null);
                        break;

                    case Constants.FLASHLIGHT:
                    default:
                        drawable = context.getResources().getDrawable(R.drawable.util_flashlight, null);
                        break;
                }
                iconDrawable = drawable;
                break;
            }
            default:
                iconDrawable = context.getResources().getDrawable(R.drawable.add_icon, null);
        }
        return iconDrawable;
    }


    private static Drawable getAppIcon(String extra, PackageManager packageManager) {
        Drawable iconDrawable = null;
        try {
            iconDrawable = packageManager.getApplicationIcon(extra);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, e.getMessage());
        }
        return iconDrawable;
    }

}
