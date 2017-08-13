package com.example.prodigy.litevervelife.HelpClasses.Timer;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Prodigy on 30.07.2017.
 */

public class GlobalTimerForGatt extends Timer {
    private static GlobalTimerForGatt globalTimerForGatt;

    GlobalTimerForGatt(){}

    public static GlobalTimerForGatt createNewGattTaskSchedule(final TimerTaskGlobal.ITimerActions iTimerActions, long delay, long period){
        globalTimerForGatt = new GlobalTimerForGatt();
        globalTimerForGatt.schedule(new TimerTask(){
            @Override
            public void run() {
                iTimerActions.runActions();
            }
        }, delay, period);
        return globalTimerForGatt;
    }

    public static GlobalTimerForGatt createNewGattTaskSchedule(final TimerTaskGlobal.ITimerActions iTimerActions, long delay){
        globalTimerForGatt = new GlobalTimerForGatt();
        globalTimerForGatt.schedule(new TimerTask(){
            @Override
            public void run() {
                iTimerActions.runActions();
            }
        }, delay);
        return globalTimerForGatt;
    }

}
