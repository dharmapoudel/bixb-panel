package com.dharmapoudel.bxpanel.actions;

import com.dharmapoudel.bxpanel.Constants;

public class ActionFactory {

    public static Action getActionInstance(String actionType){

        Action action;

        switch(actionType){
            case Constants.ACTION_OPEN_APP:
                action = new OpenAppAction();
                break;


            default:
                action = new OpenAppAction();
        }
        return action;

    }

}
