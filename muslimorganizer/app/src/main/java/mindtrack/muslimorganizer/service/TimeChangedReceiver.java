package mindtrack.muslimorganizer.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import mindtrack.muslimorganizer.utility.MindtrackLog;


public class TimeChangedReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        MindtrackLog.add("Time Change");
        context.startService(new Intent(context, PrayingDayCalculateHandler.class));
    }

}