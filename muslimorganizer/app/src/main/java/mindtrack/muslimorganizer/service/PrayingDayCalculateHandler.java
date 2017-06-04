package mindtrack.muslimorganizer.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import mindtrack.muslimorganizer.calculator.prayer.PrayerTimeCalculator;
import mindtrack.muslimorganizer.database.ConfigPreferences;
import mindtrack.muslimorganizer.model.LocationInfo;
import mindtrack.muslimorganizer.utility.Alarms;
import mindtrack.muslimorganizer.utility.Calculators;
import mindtrack.muslimorganizer.utility.NumbersLocal;

/**
 * Service to set alarm of all prayers
 */
public class PrayingDayCalculateHandler extends IntentService {
    private static final int PRAYER_SIG = 110, AZKAR_SIG = 895;

    public PrayingDayCalculateHandler() {
        super(PrayingDayCalculateHandler.class.getSimpleName());
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        SimpleDateFormat nsdf = new SimpleDateFormat("yyyy-MM-dd");
        String[] normaldate = nsdf.format(new Date().getTime()).split("-");
        LocationInfo locationInfo = ConfigPreferences.getLocationConfig(getApplicationContext());
        if (locationInfo == null) return;
        double[] prayers = new PrayerTimeCalculator(
                Integer.parseInt(NumbersLocal.convertNumberType(getApplicationContext(), normaldate[2].trim()))
                , Integer.parseInt(NumbersLocal.convertNumberType(getApplicationContext(), normaldate[1].trim()))
                , Integer.parseInt(NumbersLocal.convertNumberType(getApplicationContext(), normaldate[0].trim()))
                , locationInfo.latitude, locationInfo.longitude
                , locationInfo.timeZone, locationInfo.mazhab
                , locationInfo.way, locationInfo.dls
                , getApplicationContext()).calculateDailyPrayers_withSunset();

        Calendar c = Calendar.getInstance();
        int hourNow = c.get(Calendar.HOUR_OF_DAY);
        int minsNow = c.get(Calendar.MINUTE);

        int counter = 0;
        for (double pray : prayers) {
            counter++;
            if (hourNow < Calculators.extractHour(pray)) {
                break;
            } else {
                if (hourNow == Calculators.extractHour(pray)) {
                    if (minsNow < Calculators.extractMinutes(pray)) {
                        break;
                    }
                }
            }
        }

        for (int i = (counter - 1); i < prayers.length; i++) {
            //alarm for every prayer
            Alarms.setNotificationAlarm(getApplicationContext(), Calculators.extractHour(prayers[i])
                    , Calculators.extractMinutes(prayers[i]), PRAYER_SIG + i, i + "");

            Log.d("String_date" , Calculators.extractHour(prayers[i])+" "+Calculators.extractMinutes(prayers[i]));

            if (ConfigPreferences.getAzkarMood(this) == true) {
                //alarm for morning Azkar
                if (i == 0)
                    Alarms.setAlarmForAzkar(getApplicationContext(), Calculators.extractHour(prayers[i])
                            , Calculators.extractMinutes(prayers[i]) + 30, AZKAR_SIG + i , "1");
                //alarm for night Azkar
                if (i == 3)
                    Alarms.setAlarmForAzkar(getApplicationContext(), Calculators.extractHour(prayers[i])
                            , Calculators.extractMinutes(prayers[i])+35, AZKAR_SIG + i , "2");
            }

        }

        //reset widget for new changes
        sendBroadcast(new Intent().setAction("prayer.information.change"));

        stopSelf();
        PrayingDayCalculateAlarm.completeWakefulIntent(intent);
    }


}