package mindtrack.muslimorganizer.service;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

/**
 * Alarm to azkar notification time
 */
public class AzkarAlarm extends WakefulBroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle b = intent.getExtras();
        Log.d("Azkar" , b.getString("Azkar")+"") ;
        Intent serviceNotification = new Intent(context, AzkarNotification.class).putExtra("Azkar" , b.getString("Azkar"));
        startWakefulService(context, serviceNotification);
    }
}
