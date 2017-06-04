package mindtrack.muslimorganizer.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import mindtrack.muslimorganizer.R;
import mindtrack.muslimorganizer.calculator.calendar.HGDate;
import mindtrack.muslimorganizer.database.ConfigPreferences;
import mindtrack.muslimorganizer.ui.activity.MainActivity;
import mindtrack.muslimorganizer.ui.activity.PrayerImageActivity;
import mindtrack.muslimorganizer.utility.Alarms;
import mindtrack.muslimorganizer.utility.MindtrackLog;

public class PrayerNotification extends Service {
    private String prayingName , prayerType;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        prayingName = intent.getStringExtra("prayName");
        //not mid night
        Log.i("ACTIVITY_SRAT" , "PrayerNotification is working well");
        if (ConfigPreferences.getPrayingNotification(this) && !prayingName.equals("6"))
            showNotification();
        if (ConfigPreferences.getSilentMood(this)) changeMobileToSilentMood();
        sendBroadcast(new Intent().setAction("prayer.information.change"));
        stopSelf();
        PrayerAlarm.completeWakefulIntent(intent);
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * Function to check to make mobile silent in prayer
     */
    private void changeMobileToSilentMood() {
        AudioManager mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int ringerMode = mAudioManager.getRingerMode();
        if (ringerMode != AudioManager.RINGER_MODE_SILENT) {
            Alarms.switchToSilent(10, this);
        }
    }

    /**
     * Function to show prayer notification
     */
    public void showNotification() {

        switch (prayingName) {
            case "0":
                prayingName = this.getString(R.string.fajr_prayer);
                prayerType = PrayerImageActivity.MOSQUE_NIGHT;
                MindtrackLog.add(prayingName);
                break;
            case "1":
                prayingName = this.getString(R.string.sunrize_prayer);
                prayerType = PrayerImageActivity.MOSQUE_DAY;
                MindtrackLog.add(prayingName);
                break;
            case "2":
                HGDate hgDate = new HGDate();
                prayingName = hgDate.weekDay() != 5 ? this.getString(R.string.zuhr_prayer) : this.getString(R.string.jomma_prayer);

                prayerType = PrayerImageActivity.MOSQUE_DAY;
                MindtrackLog.add(prayingName);
                break;
            case "3":
                prayingName = this.getString(R.string.asr_prayer);
                prayerType = PrayerImageActivity.MOSQUE_DAY;
                MindtrackLog.add(prayingName);
                break;
            case "4":
                prayingName = this.getString(R.string.magreb_prayer);
                prayerType = PrayerImageActivity.MOSQUE_NIGHT;
                MindtrackLog.add(prayingName);
                break;
            case "5":
                prayingName = this.getString(R.string.asha_prayer);
                prayerType = PrayerImageActivity.MOSQUE_NIGHT;
                MindtrackLog.add(prayingName);
                break;
            case "6":
                prayingName = this.getString(R.string.mid_night);
                MindtrackLog.add(prayingName);
                break;

        }


        NotificationCompat.Builder builder;
        boolean aboveLollipopFlag = android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
        PendingIntent intent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), 0);
        if (ConfigPreferences.getLedNotification(this)) {
            builder = new NotificationCompat.Builder(this).
                    setSmallIcon(aboveLollipopFlag ? R.drawable.notification_white : R.drawable.ic_launcher_prayer_name)
                    .setPriority(Notification.PRIORITY_MAX)
                    .setContentText(prayingName)
                    .setContentTitle(getString(R.string.remember))
                    .setDefaults(Notification.DEFAULT_SOUND)
                    .setLights(0xFF00ff00, 1000, 1000)
                    .setAutoCancel(true)
                    .setColor(Color.parseColor("#2a5f54"))
                    .setContentIntent(intent);
        } else {
            builder = new NotificationCompat.Builder(this).
                    setSmallIcon(aboveLollipopFlag ? R.drawable.notification_white : R.drawable.ic_launcher_prayer_name)
                    .setPriority(Notification.PRIORITY_MAX)
                    .setContentText(prayingName)
                    .setContentTitle(getString(R.string.remember))
                    .setDefaults(Notification.DEFAULT_SOUND)
                    .setAutoCancel(true)
                    .setColor(Color.parseColor("#2a5f54"))
                    .setContentIntent(intent);
        }

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(0, builder.build());

/*For start activity when prayer come*/

//        Intent intent1 = new Intent(this , PrayerImageActivity.class);
//        Log.i("ACTIVITY_SRAT" , "Start Activity is working well");
//        intent1.putExtra(PrayerImageActivity.MOSQUE_TYPE , prayerType);
//        intent1.putExtra(PrayerImageActivity.PRAY_TYPE , prayingName);
//        startActivity(intent1);


    }

    boolean isShow = true;
    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }
}
