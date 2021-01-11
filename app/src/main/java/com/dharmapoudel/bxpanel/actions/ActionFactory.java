package com.dharmapoudel.bxpanel.actions;

import com.dharmapoudel.bxpanel.Constants;

public class ActionFactory {

    public static Action getActionInstance(String actionType){

        Action action;

        switch(actionType){

            case Constants.ACTION_NAVIGATE:
                action = new NavigateAction();
                break;

            case Constants.ACTION_MEDIA:
                action = new MediaAction();
                break;

            case Constants.ACTION_UTILS:
                action = new UtilAction();
                break;

            case Constants.ACTION_OPEN_APP:
            default:
                action = new OpenAppAction();
                break;

        }
        return action;

    }

}
