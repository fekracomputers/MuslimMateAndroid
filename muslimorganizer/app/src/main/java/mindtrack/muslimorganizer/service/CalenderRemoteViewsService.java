package mindtrack.muslimorganizer.service;

import android.content.Intent;
import android.widget.RemoteViewsService;

import java.util.ArrayList;
import java.util.List;

import mindtrack.muslimorganizer.adapter.CalenderWidgetAdapter;
import mindtrack.muslimorganizer.calculator.calendar.HGDate;
import mindtrack.muslimorganizer.model.CalendarCell;

/**
 * Service for remote view calender widget
 */
public class CalenderRemoteViewsService extends RemoteViewsService {
    private boolean spaceFlag = true;
    private List<CalendarCell> monthList;
    public int space;


    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {

        monthList = new ArrayList<>();
        HGDate hgDate = new HGDate();
        hgDate.toHigri();
        loadMonthsDaysIslamic(hgDate.getMonth(), hgDate.getYear());
        return (new CalenderWidgetAdapter(this.getApplicationContext(),
                intent, monthList));
    }


    /**
     * Function to load islamic month days
     *
     * @param month Month to load days
     * @param year  Year to load
     */
    public void loadMonthsDaysIslamic(int month, int year) {
        spaceFlag = true;
        monthList.clear();
        HGDate hgDate = new HGDate();
        hgDate.setHigri(year, month, 1);
        while (month == hgDate.getMonth()) {

            HGDate od = new HGDate(hgDate);
            od.toGregorian();

            if (spaceFlag == true) {
                space = hgDate.weekDay() + 1;
                spaceFlag = false;
            }

            monthList.add(new CalendarCell(hgDate.getDay(), od.getDay()
                    , month
                    , hgDate.getMonth()
                    , hgDate.weekDay() + 1));

            hgDate.nextDay();
        }

        for (int j = 0; j < space; j++) {
            monthList.add(0, new CalendarCell(-1, -1, -1, -1, -1));
        }
    }



}