package mindtrack.muslimorganizer.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import mindtrack.muslimorganizer.R;
import mindtrack.muslimorganizer.database.ConfigPreferences;
import mindtrack.muslimorganizer.ui.activity.AzkarActivity;
import mindtrack.muslimorganizer.utility.MindtrackLog;

/**
 * Service to show notification of azkar
 */
public class AzkarNotification extends Service {
    private String Azkar;


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        try {
            Azkar = intent.getStringExtra("Azkar");
            Log.d("Azkar", Azkar + "");
            MindtrackLog.add(Azkar.equals("1") ? getString(R.string.sabah) : getString(R.string.massa));
            if (ConfigPreferences.getAzkarMood(this)) showNotification();
            AzkarAlarm.completeWakefulIntent(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * Function to notify azkar time
     */
    public void showNotification() {
        try {
            Bitmap bigIcon = BitmapFactory.decodeResource(getResources(),
                    R.drawable.ic_launcher_prayer_name);

            boolean aboveLollipopFlag = android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;

            //pending intent to open azkar
            PendingIntent intent = PendingIntent.getActivity(this, 0,
                    new Intent(this, AzkarActivity.class)
                            .putExtra("zekr_type", Azkar.equals("1") ? 2 : 3)
                            .putExtra("title", Azkar.equals("1") ? getString(R.string.sabah) : getString(R.string.massa)), 0);

            //azkar notification builder
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this).
                    setSmallIcon(R.drawable.ic_launcher_prayer_name)
                    .setPriority(Notification.PRIORITY_MAX)
                    .setContentText(Azkar.equals("1") ? getString(R.string.sabah) : getString(R.string.massa))
                    .setContentTitle(getString(R.string.remember))
                    .setAutoCancel(true)
                    .setSmallIcon(aboveLollipopFlag ? R.drawable.notification_white : R.drawable.ic_launcher_prayer_name)
                    .setDefaults(Notification.DEFAULT_SOUND)
                    .setColor(Color.parseColor("#2a5f54"))
                    .setContentIntent(intent);

            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.notify(1001001, builder.build());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
