package mindtrack.muslimorganizer.ui.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import mindtrack.muslimorganizer.R;
import mindtrack.muslimorganizer.calculator.calendar.HGDate;
import mindtrack.muslimorganizer.calculator.location.LocationReader;
import mindtrack.muslimorganizer.calculator.prayer.PrayerTimeCalculator;
import mindtrack.muslimorganizer.calculator.prayer.PrayerTimes;
import mindtrack.muslimorganizer.database.ConfigPreferences;
import mindtrack.muslimorganizer.model.Event;
import mindtrack.muslimorganizer.model.LocationInfo;
import mindtrack.muslimorganizer.ui.popup.CountryPrayerPopup;
import mindtrack.muslimorganizer.utility.Calculators;
import mindtrack.muslimorganizer.utility.Dates;
import mindtrack.muslimorganizer.utility.NumbersLocal;

/**
 * Activity to show praying in any time
 */
public class PrayShowActivity extends AppCompatActivity {
    private TextView monthDay, monthView, weekDay,
            HmonthDay, HmonthView, fajr,
            sunrise, zuhr, asr, magrib, isha, salahNow,
            remain, location;
    private List<Event> eventList;
    private LocationInfo locationInfo;
    private LocationReader lr;
    private SimpleDateFormat format;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle(getString(R.string.praying_time));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.fragment_praying);

        //islamic events
        eventList = new ArrayList<>();
        eventList.add(new Event(getString(R.string.ramdanstart), "1-9-1437"));
        eventList.add(new Event(getString(R.string.laylt_kader), "27-9-1437"));
        eventList.add(new Event(getString(R.string.eid_el_feter), "1-10-1437"));
        eventList.add(new Event(getString(R.string.wafet_el_arafa), "9-12-1437"));
        eventList.add(new Event(getString(R.string.el_adha), "10-12-1437"));
        eventList.add(new Event(getString(R.string.islamic_year), "1-1-1438"));
        eventList.add(new Event(getString(R.string.milad_al_naby), "1-3-1438"));
        init();
    }

    /**
     * Function to check if certain day is event or not
     *
     * @param day   Day for comparison
     * @param month Month of comparison
     * @return True or false
     */
    public String compareDates(int day, int month) {
        String eventName = "";
        for (int i = 0; i < eventList.size(); i++) {
            String[] date = eventList.get(i).hejriDate.split("-");
            if (Integer.parseInt(date[0]) == day && Integer.parseInt(date[1]) == month) {
                eventName = eventList.get(i).eventName;
                return eventName;
            }
        }
        return eventName;
    }

    /**
     * Function to init Prayer activity views
     */
    private void init() {

        //check application language
        final boolean arabic = getResources().getConfiguration().
                locale.getDisplayLanguage().equals("العربية") ? true : false;

        //get intent to show prayer data
        String dataSt = getIntent().getStringExtra("date");
        String[] date ;

        if (dataSt.contains("-")){
            date = dataSt.split("-");
        }else{
            date = dataSt.split("/");
        }

        final int year = Integer.parseInt(date[2].trim());
        final int month = Integer.parseInt(date[1].trim());
        final int day = Integer.parseInt(date[0].trim());

        //convert to Georgian date
        HGDate hgDate = new HGDate();
        hgDate.setHigri(year, month, day);
        Log.d("HGD" , year+"-"+month+"-"+day);
        hgDate.toGregorian();
        Log.d("HGD" , hgDate.getYear()+"-"+hgDate.getMonth()+"-"+hgDate.getDay());

        //init prayer show views
        final String eventName = compareDates(day, month);
        monthDay = (TextView) findViewById(R.id.textView3);
        monthView = (TextView) findViewById(R.id.textView4);
        weekDay = (TextView) findViewById(R.id.textView);
        HmonthDay = (TextView) findViewById(R.id.textView5);
        HmonthView = (TextView) findViewById(R.id.textView6);
        location = (TextView) findViewById(R.id.textView2);
        salahNow = (TextView) findViewById(R.id.textView7);
        remain = (TextView) findViewById(R.id.textView8);


        //prayer view animations
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.bottom_top);
        fajr = (TextView) findViewById(R.id.fajrTime);
        fajr.setAnimation(animation);
        ((CardView) fajr.getParent().getParent()).setAnimation(animation);
        sunrise = (TextView) findViewById(R.id.sunriseTime);
        sunrise.setAnimation(animation);
        ((CardView) sunrise.getParent().getParent()).setAnimation(animation);
        zuhr = (TextView) findViewById(R.id.zuhrTime);
        zuhr.setAnimation(animation);
        ((CardView) zuhr.getParent().getParent()).setAnimation(animation);
        asr = (TextView) findViewById(R.id.asrTime);
        asr.setAnimation(animation);
        ((CardView) asr.getParent().getParent()).setAnimation(animation);
        magrib = (TextView) findViewById(R.id.magribTime);
        magrib.setAnimation(animation);
        ((CardView) magrib.getParent().getParent()).setAnimation(animation);
        isha = (TextView) findViewById(R.id.ishaTime);
        isha.setAnimation(animation);
        ((CardView) isha.getParent().getParent()).setAnimation(animation);

        //check open from
        try {
            String type = date[3];
            locationInfo = ConfigPreferences.getWorldPrayerCountry(this);
            salahNow.setText(CountryPrayerPopup.locationInformation);
        } catch (Exception e) {
            locationInfo = ConfigPreferences.getLocationConfig(PrayShowActivity.this);
            salahNow.setText(eventName.equals("") ? getString(R.string.show_only) : eventName);
        }

        //Prayer information
        location.setText(arabic ? locationInfo.name_english : locationInfo.name);
        monthView.setText(Dates.gregorianMonthName(PrayShowActivity.this, hgDate.getMonth() - 1));
        HmonthDay.setText(NumbersLocal.convertNumberType(PrayShowActivity.this, day + ""));
        HmonthView.setText(Dates.islamicMonthName(PrayShowActivity.this, month - 1));
        remain.setText(year + "");
        monthDay.setText(NumbersLocal.convertNumberType(PrayShowActivity.this, hgDate.getDay() + ""));
        weekDay.setText(Dates.weekDayName(this, hgDate.weekDay()+1));

        //calculate prayer times

        format = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        lr = new LocationReader(this);
        lr.read(locationInfo.latitude,locationInfo.longitude);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH , hgDate.getDay());
        calendar.set(Calendar.MONTH , hgDate.getMonth() - 1);
        calendar.set(Calendar.YEAR , hgDate.getYear());
        int yearCal = calendar.get(Calendar.YEAR);
        int monthCal = calendar.get(Calendar.MONTH);
        int dayCal = calendar.get(Calendar.DAY_OF_MONTH);
        int timeZone = calendar.getTimeZone().getRawOffset()/(1000*60*60);
        int dst = calendar.getTimeZone().getDSTSavings();
        Date [] prayers = new PrayerTimes(dayCal, monthCal +1, yearCal, lr.getLatitude(), lr.getLongitude(), timeZone, !(dst>0), PrayerTimes.getDefaultMazhab(lr.getCountryCode()), PrayerTimes.getDefaultWay(lr.getCountryCode())).get();


        //set prayer times
        fajr.setText(format.format(prayers[0]));
        sunrise.setText(format.format(prayers[1]));
        zuhr.setText(format.format(prayers[2]));
        asr.setText(format.format(prayers[3]));
        magrib.setText(format.format(prayers[4]));
        isha.setText(format.format(prayers[5]));



        if (locationInfo != null) {
            ((TextView) findViewById(R.id.textView32)).setText(getString(R.string.near) + " " + (getResources().getConfiguration()
                    .locale.getDisplayLanguage()
                    .equals("العربية") ? locationInfo.city_ar : locationInfo.city));

        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }


}
