package mindtrack.muslimorganizer.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import mindtrack.muslimorganizer.R;
import mindtrack.muslimorganizer.adapter.CalenderAdapter;
import mindtrack.muslimorganizer.calculator.calendar.HGDate;
import mindtrack.muslimorganizer.model.CalendarCell;
import mindtrack.muslimorganizer.ui.activity.PrayShowActivity;
import mindtrack.muslimorganizer.utility.Dates;
import mindtrack.muslimorganizer.utility.NumbersLocal;


/**
 * Calender fragment to show calender
 */
public class CalendarFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener {
    private TextView month, otherMonth, dayMonth, dayWeekName, calendarType, calendarYear;
    private ImageView left, right, calendarBack;
    private List<CalendarCell> monthList;
    private GridView calender;
    private CalenderAdapter adapter;
    private boolean spaceFlag = true;
    public static int space;
    public int mainMonth, mainYear;
    private String otherMonth_a, otherMonth_b;
    private boolean flagCalendar = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_calendar, container, false);

        calendarBack = (ImageView) rootView.findViewById(R.id.calendar_image);
        month = (TextView) rootView.findViewById(R.id.curr_month_txt);
        otherMonth = (TextView) rootView.findViewById(R.id.curr_month_txt_other);
        left = (ImageView) rootView.findViewById(R.id.curr_month_l);
        right = (ImageView) rootView.findViewById(R.id.curr_month_r);
        dayMonth = (TextView) rootView.findViewById(R.id.textView7);
        dayWeekName = (TextView) rootView.findViewById(R.id.textView8);
        calendarType = (TextView) rootView.findViewById(R.id.textView23);
        calendarYear = (TextView) rootView.findViewById(R.id.textView24);
        right.setOnClickListener(this);
        left.setOnClickListener(this);


        monthList = new ArrayList<>();
        adapter = new CalenderAdapter(getContext());
        calender = (GridView) rootView.findViewById(R.id.calendar_pager);
        calender.setOnItemClickListener(this);
        calender.setAdapter(adapter);


        final HGDate georgianDate = new HGDate();
        final HGDate islamicDate = new HGDate(georgianDate);
        islamicDate.toHigri();

        loadIslamicCalendar(georgianDate, islamicDate);
        rootView.findViewById(R.id.mainDates).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (flagCalendar) {
                    loadGregorianCalendar(georgianDate, islamicDate);
                    calendarBack.setImageResource(R.drawable.mos);
                    flagCalendar = false;
                } else {
                    loadIslamicCalendar(georgianDate, islamicDate);
                    calendarBack.setImageResource(R.drawable.sliderbackground);
                    flagCalendar = true;
                }
            }
        });

        return rootView;
    }

    /**
     * Function to forward or backward in calendar
     *
     * @param view left or right arrow
     */
    @Override
    public void onClick(View view) {

        if (view == right) {
            mainMonth++;
            if (mainMonth >= 13) {
                mainMonth = 1;
                mainYear++;
                calendarYear.setText(NumbersLocal.convertNumberType(getContext(), mainYear + ""));
                if (flagCalendar) {
                    loadMonthsDaysIslamic(mainMonth, mainYear);
                } else {
                    loadMonthsDayGregorian(mainMonth, mainYear);
                }

            } else {
                if (flagCalendar) {
                    loadMonthsDaysIslamic(mainMonth, mainYear);
                } else {
                    loadMonthsDayGregorian(mainMonth, mainYear);
                }
            }
        } else if (view == left) {
            mainMonth--;
            if (mainMonth <= 1) {
                mainMonth = 12;
                mainYear--;
                calendarYear.setText(NumbersLocal.convertNumberType(getContext(), mainYear + ""));
                if (flagCalendar) {
                    loadMonthsDaysIslamic(mainMonth, mainYear);
                } else {
                    loadMonthsDayGregorian(mainMonth, mainYear);
                }
            } else {
                if (flagCalendar) {
                    loadMonthsDaysIslamic(mainMonth, mainYear);
                } else {
                    loadMonthsDayGregorian(mainMonth, mainYear);
                }
            }
        }

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        CalendarCell cell = adapter.getItem(position);
        if (cell.day != -1) {
            if (flagCalendar) {
                startActivity(new Intent(getContext(), PrayShowActivity.class)
                        .putExtra("date", cell.day + "-" + cell.hijriMonth + "-" + mainYear));
            } else {
                HGDate hgDate = new HGDate();
                hgDate.setGregorian(mainYear, 1, 1);
                hgDate.toHigri();
                startActivity(new Intent(getContext(), PrayShowActivity.class)
                        .putExtra("date", cell.dayOther + "-" + cell.hijriMonth + "-" + hgDate.getYear()));
            }
        }

    }

    /**
     * Function to load islamic month days
     *
     * @param month Month to load days
     * @param year  Year to load
     */
    public void loadMonthsDaysIslamic(int month, int year) {
        spaceFlag = true;
        adapter.clear();
        monthList.clear();
        this.month.setText(Dates.islamicMonthName(getContext(), month - 1));
        HGDate hgDate = new HGDate();
        hgDate.setHigri(year, month, 1);
        adapter.enableGregorian(false);
        while (month == hgDate.getMonth()) {
            HGDate od = new HGDate(hgDate);
            od.toGregorian();
            //get first spacing of the month
            if (spaceFlag == true) {
                space = /*getCalenderSpacing(*/hgDate.weekDay() + 1;/*+ 2);*/
                spaceFlag = false;
            }
            //get georgian months for same islamic month
            if (hgDate.getDay() == 1)
                otherMonth_a = Dates.gregorianMonthName(getContext(), od.getMonth() - 1);
            if (hgDate.getDay() == 29)
                otherMonth_b = Dates.gregorianMonthName(getContext(), od.getMonth() - 1);

            //add day
            monthList.add(new CalendarCell(hgDate.getDay(), od.getDay()
                    , month
                    , od.getMonth()
                    , hgDate.weekDay() + 1));

            hgDate.nextDay();
        }

        //show months
        String htmlString = "<u>" + otherMonth_a + " - " + otherMonth_b + "</u>";
        otherMonth.setText(Html.fromHtml(htmlString));

        //add spacing to list
        for (int j = 0; j < space; j++) {
            monthList.add(0, new CalendarCell(-1, -1, -1, -1, -1));
        }
        adapter.addAll(monthList);
        adapter.notifyDataSetChanged();
    }

    /**
     * Function to load gregorian month days
     *
     * @param month Month to load days
     * @param year  Year to load
     */
    public void loadMonthsDayGregorian(int month, int year) {
        spaceFlag = true;
        adapter.clear();
        monthList.clear();
        this.month.setText(Dates.gregorianMonthName(getContext(), month - 1));
        adapter.enableGregorian(true);
        HGDate hgDate = new HGDate();
        hgDate.setGregorian(year, month, 1);
        while (month == hgDate.getMonth()) {
            HGDate od = new HGDate(hgDate);
            od.toHigri();

            //add day of month information
            monthList.add(new CalendarCell(hgDate.getDay(),
                    od.getDay(),
                    od.getMonth(),
                    month,
                    od.weekDay() + 1));

            //get first spacing of the month
            if (spaceFlag == true) {
                space = /*getCalenderSpacing(*/hgDate.weekDay() + 1;/*+ 2);*/
                spaceFlag = false;
            }

            //get islamic months for same georgian month
            if (hgDate.getDay() == 1)
                otherMonth_a = Dates.islamicMonthName(getContext(), od.getMonth() - 1);
            if (hgDate.getDay() == 29)
                otherMonth_b = Dates.islamicMonthName(getContext(), od.getMonth() - 1);

            hgDate.nextDay();

        }

        //show months
        String htmlString = "<u>" + otherMonth_a + " - " + otherMonth_b + "</u>";
        otherMonth.setText(Html.fromHtml(htmlString));

        //add spacing to list
        for (int j = 0; j < space; j++) {
            monthList.add(0, new CalendarCell(-1, -1, -1, -1, -1));
        }
        adapter.addAll(monthList);
        adapter.notifyDataSetChanged();
    }

    /**
     * Function to get first day space in week
     *
     * @param dayOfTheWeek First day of the month
     * @return space in week
     */
    public int getCalenderSpacing(int dayOfTheWeek) {
        switch (dayOfTheWeek) {
            case 1:
                return 5;
            case 2:
                return 6;
            case 3:
                return 0;
            case 4:
                return 1;
            case 5:
                return 2;
            case 6:
                return 3;
            default:
                return 4;
        }
    }

    /**
     * Function to load islamic calendar
     *
     * @param dtISO     Current day time gregorian
     * @param dtIslamic Current day time islamic
     */
    public void loadIslamicCalendar(HGDate dtISO, HGDate dtIslamic) {
        month.setText(Dates.islamicMonthName(getContext(), dtIslamic.getMonth() - 1));
        otherMonth.setText(Dates.gregorianMonthName(getContext(), dtISO.getMonth()));
        mainMonth = dtIslamic.getMonth();
        mainYear = dtIslamic.getYear();
        calendarYear.setText(NumbersLocal.convertNumberType(getContext(), mainYear + ""));
        loadMonthsDaysIslamic(mainMonth, mainYear);
        calendarType.setText(getString(R.string.hijri));
        dayMonth.setText(NumbersLocal.convertNumberType(getContext(), dtIslamic.getDay() + ""));
        Log.d("Week_Day", dtIslamic.weekDay() + "");
        dayWeekName.setText(Dates.getCurrentWeekDay());
    }

    /**
     * Function to load gregorian calendar
     *
     * @param dtISO     Current day time gregorian
     * @param dtIslamic Current day time islamic
     */
    public void loadGregorianCalendar(HGDate dtISO, HGDate dtIslamic) {
        month.setText(Dates.gregorianMonthName(getContext(), dtISO.getMonth() - 1));
        otherMonth.setText(Dates.gregorianMonthName(getContext(), dtISO.getMonth()));
        mainMonth = dtISO.getMonth();
        mainYear = dtISO.getYear();
        calendarYear.setText(NumbersLocal.convertNumberType(getContext(), dtISO.getYear() + ""));
        loadMonthsDayGregorian(dtISO.getMonth(), dtISO.getYear());
        calendarType.setText(getString(R.string.gregorian));
        HGDate hgDate = new HGDate();
        dayMonth.setText(NumbersLocal.convertNumberType(getContext(), hgDate.getDay() + ""));
        dayWeekName.setText(Dates.getCurrentWeekDay());
    }

}
