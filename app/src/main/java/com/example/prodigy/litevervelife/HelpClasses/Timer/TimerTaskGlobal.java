package com.example.prodigy.litevervelife.HelpClasses.Timer;

import android.app.Activity;

import java.util.TimerTask;

/**
 * Created by Prodigy on 24.06.2017.
 */

public class TimerTaskGlobal extends TimerTask {
    private Activity activity;
    private ITimerActions iTimerActions;

    public TimerTaskGlobal(Activity activity, ITimerActions iTimerActions) {
        this.activity = activity;
        this.iTimerActions = iTimerActions;
    }

    @Override
    public void run() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                iTimerActions.runActions();
            }
        });
    };


    public static interface ITimerActions {
        void runActions();
    }
}