package mindtrack.muslimorganizer.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import mindtrack.muslimorganizer.utility.MindtrackLog;

/**
 * Broadcast to set praying alarm after reboot mobile
 */
public class StartUpBootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            MindtrackLog.add("Boot Complete");
            context.startService(new Intent(context, PrayingDayCalculateHandler.class));
        }
    }
}
