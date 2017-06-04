package mindtrack.muslimorganizer.utility;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import mindtrack.muslimorganizer.service.AzkarAlarm;
import mindtrack.muslimorganizer.service.PrayerAlarm;
import mindtrack.muslimorganizer.service.PrayingDayCalculateAlarm;
import mindtrack.muslimorganizer.service.RingingAlarm;
import mindtrack.muslimorganizer.service.SilentMoodAlarm;


/**
 * Class to do some settings
 */
public class Alarms {

    /**
     * Function to check service running of not
     *
     * @param context      Application context
     * @param serviceClass Service class
     * @return Service running or not
     */
    public static boolean isMyServiceRunning(Context context, Class<?> serviceClass) {

        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager
                .getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


    /**
     * Function to start alarm and set notification.
     *
     * @param context Application context
     * @param hour    Hour of alarm
     * @param min     Min of alarm
     * @param id      ID of alarm
     */
    public static void setNotificationAlarm(Context context, int hour, int min, int id, String extra) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, min);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Bundle details = new Bundle();
        details.putString("prayName", extra);
        Intent alarmReceiver = new Intent(context, PrayerAlarm.class);
        alarmReceiver.putExtras(details);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, id, alarmReceiver, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }


    /**
     * Function to set alarm notification every day
     *
     * @param context Application context
     * @param hour    Hour of alarm
     * @param min     Min of alarm
     * @param id      ID of alarm
     */
    public static void setAlarmForAzkar(Context context, int hour, int min, int id , String type) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, min);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Bundle details = new Bundle();
        details.putString("Azkar", type);
        Intent alarmReceiver = new Intent(context, AzkarAlarm.class);
        alarmReceiver.putExtras(details);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, id, alarmReceiver, PendingIntent.FLAG_UPDATE_CURRENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // kitkat...
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        } else {
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(), 1000 * 60 * 60 * 24, pendingIntent);
        }
    }


    /**
     * Function to start praying calculator broadcast
     *
     * @param context Context
     */
    public static void startCalculatePrayingBroadcast(Context context) {
        context.sendBroadcast(new Intent("com.mindtrack.muslimorganizer.calculatepraying"));
    }


    /**
     * Function to set daily prayer calculate broadcast
     *
     * @param context Context
     */
    public static void setNotificationAlarmMainPrayer(Context context) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 1);
        calendar.set(Calendar.MINUTE, 0);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent alarmReceiver = new Intent(context, PrayingDayCalculateAlarm.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 1111, alarmReceiver, PendingIntent.FLAG_UPDATE_CURRENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // kitkat...
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        } else {
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(), 1000 * 60 * 60 * 24, pendingIntent);
        }
    }


    /**
     * Function to set alarm to make mobile not silent
     *
     * @param context Application context
     */
    public static void NormalAudio(Context context) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent alarmReceiver = new Intent(context, RingingAlarm.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 11022, alarmReceiver, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis() + 900000, pendingIntent);
    }


    /**
     * Function to switch application to silent mood
     *
     * @param minutes Time to switch
     * @param context Application context
     */
    public static void switchToSilent(int minutes, Context context) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent alarmReceiver = new Intent(context, SilentMoodAlarm.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 1159, alarmReceiver, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis() + TimeUnit.MINUTES.toMillis(minutes), pendingIntent);
    }


}
