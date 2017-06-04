package mindtrack.muslimorganizer.ui.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.widget.RemoteViews;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import mindtrack.muslimorganizer.R;
import mindtrack.muslimorganizer.calculator.calendar.HGDate;
import mindtrack.muslimorganizer.calculator.prayer.PrayerTimeCalculator;
import mindtrack.muslimorganizer.database.ConfigPreferences;
import mindtrack.muslimorganizer.model.LocationInfo;
import mindtrack.muslimorganizer.ui.activity.MainActivity;
import mindtrack.muslimorganizer.utility.Calculators;
import mindtrack.muslimorganizer.utility.Dates;
import mindtrack.muslimorganizer.utility.NumbersLocal;

/**
 * Widget for praying times
 */
public class PrayerWidget extends AppWidgetProvider {
    private static String PRAYER_CHANGE = "prayer.information.change";
    private double[] prayers, nextDayPrayers;

    /**
     * Function when system update widget
     *
     * @param context          Application context
     * @param appWidgetManager App widget manger
     * @param appWidgetIds     Widget system ids
     */
    @Override
    public void onUpdate(Context context, final AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        String languageToLoad = ConfigPreferences.getApplicationLanguage(context);

        //set widget language
        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        context.getResources().updateConfiguration(config,
                context.getResources().getDisplayMetrics());


        final int count = appWidgetIds.length;
        for (int i = 0; i < count; i++) {
            final int widgetId = appWidgetIds[i];
            final RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                    R.layout.widget_prayer);

            //get current islamic and georgian dates
            HGDate georgianDate = new HGDate();
            HGDate islamicDate = new HGDate(georgianDate);
            islamicDate.toHigri();
            String hDay = georgianDate.getDay()+"";
            String hMonth = georgianDate.getMonth()-1+"";
            SimpleDateFormat sdf = new SimpleDateFormat("EEEE, MMMM , dd");
            String[] myformat = sdf.format(new Date().getTime()).split(",");
            String week = myformat[0];

            //set current dates
            remoteViews.setTextViewText(R.id.textView5, NumbersLocal.convertNumberType(context , islamicDate.getDay() + "") );
            remoteViews.setTextViewText(R.id.textView6, Dates.islamicMonthName(context, islamicDate.getMonth()-1));
            remoteViews.setTextViewText(R.id.textView3, NumbersLocal.convertNumberType(context , (hDay + "").trim()) );
            remoteViews.setTextViewText(R.id.textView4, Dates.gregorianMonthName(context, Integer.parseInt(hMonth.trim())).trim());
            remoteViews.setTextViewText(R.id.textView, week);

            //get saved location information
            LocationInfo locationInfo = ConfigPreferences.getLocationConfig(context);
            if (locationInfo != null) {
                remoteViews.setTextViewText(R.id.textView2, (context.getResources().getConfiguration()
                        .locale.getDisplayLanguage().equals("العربية")
                        ? locationInfo.name_english : locationInfo.name));

                remoteViews.setTextViewText(R.id.textView32, context.getString(R.string.near)+" "+ (context.getResources().getConfiguration()
                                .locale.getDisplayLanguage().equals("العربية")
                                ? locationInfo.city_ar : locationInfo.city));

                SimpleDateFormat nsdf = new SimpleDateFormat("yyyy-MM-dd");
                String[] normaldate = nsdf.format(new Date().getTime()).split("-");

                prayers = new PrayerTimeCalculator(
                        Integer.parseInt(NumbersLocal.convertNumberType(context, normaldate[2].trim()))
                        , Integer.parseInt(NumbersLocal.convertNumberType(context, normaldate[1].trim()))
                        , Integer.parseInt(NumbersLocal.convertNumberType(context, normaldate[0].trim()))
                        , locationInfo.latitude, locationInfo.longitude
                        , locationInfo.timeZone, locationInfo.mazhab
                        , locationInfo.way, locationInfo.dls
                        , context).calculateDailyPrayers_withSunset();

                HGDate nextDay = new HGDate();
                nextDay.nextDay();
                nextDayPrayers = new PrayerTimeCalculator(
                        Integer.parseInt(NumbersLocal.convertNumberType(context, nextDay.getDay()+""))
                        , Integer.parseInt(NumbersLocal.convertNumberType(context, nextDay.getMonth()+""))
                        , Integer.parseInt(NumbersLocal.convertNumberType(context, nextDay.getYear()+""))
                        , locationInfo.latitude, locationInfo.longitude
                        , locationInfo.timeZone, locationInfo.mazhab
                        , locationInfo.way, locationInfo.dls
                        , context).calculateDailyPrayers_withSunset();

                Calendar c = Calendar.getInstance();
                int houreNow = c.get(Calendar.HOUR_OF_DAY);
                int minsNow = c.get(Calendar.MINUTE);
                int counter = 0;

                for (double pray : prayers) {
                    counter++;
                    if (houreNow < Calculators.extractHour(pray)) {
                        break;
                    } else {
                        if (houreNow == Calculators.extractHour(pray)) {
                            if (minsNow < Calculators.extractMinutes(pray)) {
                                break;
                            }
                        }
                    }
                }

                //switch to check the next prayer
                switch (counter) {
                    case 1:
                        remoteViews.setTextViewText(R.id.textView7, NumbersLocal.convertNumberType(context
                                , context.getString(R.string.fajr_prayer) + " " + Calculators.extractPrayTime(context, prayers[0])));
                        break;
                    case 2:
                        remoteViews.setTextViewText(R.id.textView7, NumbersLocal.convertNumberType(context
                                , context.getString(R.string.sunrize_prayer) + " " + Calculators.extractPrayTime(context, prayers[1])));
                        break;
                    case 3:
                        remoteViews.setTextViewText(R.id.textView7, NumbersLocal.convertNumberType(context
                                , (georgianDate.weekDay() != 5 ? context.getString(R.string.zuhr_prayer) : context.getString(R.string.jomma_prayer))  + " " + Calculators.extractPrayTime(context, prayers[2])));
                        break;
                    case 4:
                        remoteViews.setTextViewText(R.id.textView7, NumbersLocal.convertNumberType(context
                                , context.getString(R.string.asr_prayer) + " " + Calculators.extractPrayTime(context, prayers[3])));
                        break;
                    case 5:
                        remoteViews.setTextViewText(R.id.textView7, NumbersLocal.convertNumberType(context
                                , context.getString(R.string.magreb_prayer) + " " + Calculators.extractPrayTime(context, prayers[4])));
                        break;
                    case 6:
                        remoteViews.setTextViewText(R.id.textView7, NumbersLocal.convertNumberType(context
                                , context.getString(R.string.asha_prayer) + " " + Calculators.extractPrayTime(context, prayers[5])));
                        break;
                    case 7:
                        remoteViews.setTextViewText(R.id.textView7, NumbersLocal.convertNumberType(context
                                , context.getString(R.string.fajr_prayer) + " " + Calculators.extractPrayTime(context, nextDayPrayers[0])));
                        break;
                }

                //Intent open application when press in widget
                PendingIntent configPendingIntent = PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class), 0);
                remoteViews.setOnClickPendingIntent(R.id.relativeLayout, configPendingIntent);

                appWidgetManager.updateAppWidget(widgetId, remoteViews);
            }
        }


    }

    /**
     * Receive broadcast for refresh
     *
     * @param context Application context
     * @param intent  Intent to do something
     */
    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();
        if (action.equals(PRAYER_CHANGE) || action.equals(Intent.ACTION_DATE_CHANGED)) {
            AppWidgetManager gm = AppWidgetManager.getInstance(context);
            int[] ids = gm.getAppWidgetIds(new ComponentName(context, PrayerWidget.class));
            this.onUpdate(context, gm, ids);
        } else {
            super.onReceive(context, intent);
        }
    }

}