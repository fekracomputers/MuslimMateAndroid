package mindtrack.muslimorganizer.service;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

/**
 * Alarm for prayer to give notification
 */
public class PrayerAlarm extends WakefulBroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle b = intent.getExtras();
        startWakefulService(context , new Intent(context ,
                PrayerNotification.class).putExtra("prayName" ,
                b.getString("prayName")));

        Log.i("ACTIVITY_SRAT" , "PrayerAlarm is working well");
    }
}
