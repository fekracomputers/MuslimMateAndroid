package mindtrack.muslimorganizer.ui.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.widget.RemoteViews;

import java.util.Locale;

import mindtrack.muslimorganizer.R;
import mindtrack.muslimorganizer.calculator.calendar.HGDate;
import mindtrack.muslimorganizer.service.CalenderRemoteViewsService;
import mindtrack.muslimorganizer.ui.activity.MainActivity;
import mindtrack.muslimorganizer.utility.Dates;
import mindtrack.muslimorganizer.utility.NumbersLocal;

/**
 * Widget of the calender view
 */
public class CalenderWidget extends AppWidgetProvider {
    private static String PRAYER_CHANGE = "prayer.language.change";
    private RemoteViews remoteViews;
    private Context context;
    private Intent svcIntent;

    /**
     * Function fire on system update widget
     *
     * @param context          Context of application
     * @param appWidgetManager App widget manger
     * @param appWidgetIds     Widget system ids
     */
    @Override
    public void onUpdate(Context context, final AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        final int count = appWidgetIds.length;

        //set widget language with the system language
        Locale locale = new Locale(Resources.getSystem().getConfiguration().locale.getLanguage());
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
        this.context = context;

        //loop in all widgets
        for (int i = 0; i < count; i++) {
            svcIntent = new Intent(context, CalenderRemoteViewsService.class);
            final int widgetId = appWidgetIds[i];
            remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_calender);
            remoteViews.setRemoteAdapter(R.id.calendar_pager, svcIntent);
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds[i], R.id.calendar_pager);
            HGDate hgDate = new HGDate();
            hgDate.toHigri();
            remoteViews.setTextViewText(R.id.textView8, Dates.getCurrentWeekDay());
            remoteViews.setTextViewText(R.id.textView7, NumbersLocal.convertToNumberTypeSystem(context, hgDate.getDay() + ""));
            remoteViews.setTextViewText(R.id.textView24, NumbersLocal.convertToNumberTypeSystem(context, hgDate.getYear() + ""));
            remoteViews.setTextViewText(R.id.curr_month_txt, Dates.islamicMonthName(context, hgDate.getMonth() - 1));
            remoteViews.setTextViewText(R.id.curr_month_txt_other, showOtherMonth(hgDate));

            PendingIntent configPendingIntent = PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class), 0);
            remoteViews.setOnClickPendingIntent(R.id.relativeLayout, configPendingIntent);
            appWidgetManager.updateAppWidget(widgetId, remoteViews);

        }
    }

    /**
     * Function to show months in calender
     *
     * @param dtIslamic Islamic DateTime
     * @return months to view
     */
    public String showOtherMonth(HGDate dtIslamic) {
        String month = "";
        HGDate hgDate = new HGDate();
        hgDate.setHigri(dtIslamic.getYear(), dtIslamic.getMonth(), 1);
        hgDate.toGregorian();
        month = Dates.gregorianMonthName(context, hgDate.getMonth() - 1);
        hgDate.setHigri(dtIslamic.getYear(), dtIslamic.getMonth(), 29);
        hgDate.toGregorian();
        month += (month.equals(Dates.gregorianMonthName(context, hgDate.getMonth() - 1))) ? "" :
                " - " + Dates.gregorianMonthName(context, hgDate.getMonth() - 1);
        return month;

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
            int[] ids = gm.getAppWidgetIds(new ComponentName(context, CalenderWidget.class));
            this.onUpdate(context, gm, ids);
        } else {
            super.onReceive(context, intent);
        }
    }


}
