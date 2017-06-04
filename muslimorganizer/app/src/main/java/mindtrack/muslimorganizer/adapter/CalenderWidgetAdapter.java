package mindtrack.muslimorganizer.adapter;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.ArrayList;
import java.util.List;

import mindtrack.muslimorganizer.R;
import mindtrack.muslimorganizer.calculator.calendar.HGDate;
import mindtrack.muslimorganizer.model.CalendarCell;
import mindtrack.muslimorganizer.model.Event;
import mindtrack.muslimorganizer.utility.NumbersLocal;

/**
 * Adapter for the widget calender
 */
public class CalenderWidgetAdapter implements RemoteViewsService.RemoteViewsFactory {
    private List<CalendarCell> daysList;
    private Context context;
    private int appWidgetId;
    private List<Event> eventList;


    public CalenderWidgetAdapter(Context context, Intent intent, List<CalendarCell> daysList) {
        this.context = context;
        this.daysList = daysList;
        appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);

        //Islamic events
        eventList = new ArrayList<>();
        eventList.add(new Event(this.context.getString(R.string.ramdanstart), "1-9-1437"));
        eventList.add(new Event(this.context.getString(R.string.laylt_kader), "27-9-1437"));
        eventList.add(new Event(this.context.getString(R.string.eid_el_feter), "1-10-1437"));
        eventList.add(new Event(this.context.getString(R.string.wafet_el_arafa), "9-12-1437"));
        eventList.add(new Event(this.context.getString(R.string.el_adha), "10-12-1437"));
        eventList.add(new Event(this.context.getString(R.string.islamic_year), "1-1-1438"));
        eventList.add(new Event(this.context.getString(R.string.milad_al_naby), "1-3-1438"));
    }

    @Override
    public void onCreate() {
    }

    @Override
    public void onDataSetChanged() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return (daysList.size());
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews row = new RemoteViews(context.getPackageName(),
                R.layout.calender_cell);

        CalendarCell cell = daysList.get(position);

        Intent islamicIntent = new Intent();
        Bundle islamicIntentExtras = new Bundle();
        islamicIntentExtras.putString("islamic_day",
                NumbersLocal.convertNumberType(context, cell.day + ""));
        islamicIntent.putExtras(islamicIntentExtras);

        Intent gregorianIntent = new Intent();
        Bundle gregorianIntentExtras = new Bundle();
        gregorianIntentExtras.putString("gregorian_day",
                NumbersLocal.convertNumberType(context, cell.dayOther + ""));
        gregorianIntent.putExtras(gregorianIntentExtras);

        //not empty date
        if (cell.day != -1) {

            //set dates in calender
            row.setViewVisibility(R.id.textView31, View.VISIBLE);
            row.setViewVisibility(R.id.textView30, View.VISIBLE);
            row.setTextViewText(R.id.textView31, NumbersLocal.convertToNumberTypeSystem(context, cell.dayOther + ""));
            row.setTextViewText(R.id.textView30, NumbersLocal.convertToNumberTypeSystem(context, cell.day + ""));

            //get current day
            HGDate hgDate = new HGDate();
            hgDate.toHigri();

            //check if that is the current day or not
            if (cell.day == hgDate.getDay()) {
                row.setTextColor(R.id.textView31, Color.WHITE);
                row.setTextColor(R.id.textView30, Color.WHITE);
                row.setInt(R.id.textView31, "setBackgroundColor", Color.argb(255, 73, 138, 127));
                row.setInt(R.id.textView30, "setBackgroundColor", Color.argb(255, 73, 138, 127));
            } else {
                row.setTextColor(R.id.textView31, Color.GRAY);
                row.setTextColor(R.id.textView30, Color.BLACK);
                row.setInt(R.id.textView31, "setBackgroundColor", Color.WHITE);
                row.setInt(R.id.textView30, "setBackgroundColor", Color.WHITE);
            }


            //check if the day one of the islamic events
            for (Event event : eventList) {
                String[] date = event.hejriDate.split("-");
                if (cell.day == Integer.parseInt(date[0]) && cell.hijriMonth == Integer.parseInt(date[1])) {
                    row.setInt(R.id.textView31, "setBackgroundColor", Color.YELLOW);
                    row.setInt(R.id.textView30, "setBackgroundColor", Color.YELLOW);
                }
            }


        } else {
            //hide empty cellls
            row.setViewVisibility(R.id.textView31, View.GONE);
            row.setViewVisibility(R.id.textView30, View.GONE);
        }

        row.setOnClickFillInIntent(R.id.textView31, gregorianIntent);
        row.setOnClickFillInIntent(R.id.textView30, islamicIntent);

        return (row);
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return (1);
    }

    @Override
    public long getItemId(int i) {
        return (i);
    }

    @Override
    public boolean hasStableIds() {
        return (true);
    }


}
