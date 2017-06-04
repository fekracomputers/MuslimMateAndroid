package mindtrack.muslimorganizer.ui.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import mindtrack.muslimorganizer.R;
import mindtrack.muslimorganizer.calculator.calendar.HGDate;
import mindtrack.muslimorganizer.calculator.location.LocationReader;
import mindtrack.muslimorganizer.calculator.location.Utility;
import mindtrack.muslimorganizer.calculator.prayer.PrayerTimeCalculator;
import mindtrack.muslimorganizer.calculator.prayer.PrayerTimes;
import mindtrack.muslimorganizer.database.ConfigPreferences;
import mindtrack.muslimorganizer.model.LocationInfo;
import mindtrack.muslimorganizer.utility.Calculators;
import mindtrack.muslimorganizer.utility.Dates;
import mindtrack.muslimorganizer.utility.NumbersLocal;

/**
 * Created by Dev. M. Hussein on 5/9/2017.
 */

public class PrayingFragment extends Fragment {
    private TextView monthDay, monthView, weekDay,
            HmonthDay, HmonthView, country, city, fajr,
            sunrise, zuhr, asr, magrib, isha, salahNow, remain;
    private RelativeLayout pray1, pray2, pray3, pray4, pray5, pray6;
    private Context context;
    private LocationReader lr;
    private PrayerTimes prayerTimes;
    private SimpleDateFormat format;
    private Timer timer;
    private LocationInfo locationInfo;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_praying, container, false);
        context = getActivity();
        format = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        return rootView;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
    }


    /**
     * Function to init fragment view
     *
     * @param rootView Main view of fragment
     */
    private void init(View rootView) {

        pray1 = (RelativeLayout) rootView.findViewById(R.id.p1);
        pray2 = (RelativeLayout) rootView.findViewById(R.id.p2);
        pray3 = (RelativeLayout) rootView.findViewById(R.id.p3);
        pray4 = (RelativeLayout) rootView.findViewById(R.id.p4);
        pray5 = (RelativeLayout) rootView.findViewById(R.id.p5);
        pray6 = (RelativeLayout) rootView.findViewById(R.id.p6);

        final HGDate hgDate = new HGDate();
        monthDay = (TextView) rootView.findViewById(R.id.textView3);
        monthDay.setText(NumbersLocal.convertNumberType(getContext(),hgDate.getDay()+"") );
        monthView = (TextView) rootView.findViewById(R.id.textView4);
        monthView.setText(Dates.gregorianMonthName(getContext(), hgDate.getMonth()-1)+"");
        weekDay = (TextView) rootView.findViewById(R.id.textView);
        weekDay.setText(Dates.weekDayName(getContext(), hgDate.weekDay() + 1));

        hgDate.toHigri();

        HmonthDay = (TextView) rootView.findViewById(R.id.textView5);
        HmonthDay.setText(NumbersLocal.convertNumberType(getContext(), String.valueOf(hgDate.getDay()).trim()));
        HmonthView = (TextView) rootView.findViewById(R.id.textView6);
        HmonthView.setText(Dates.islamicMonthName(getContext(), Integer.valueOf(hgDate.getMonth()) - 1).trim());

        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.bottom_top);
        fajr = (TextView) rootView.findViewById(R.id.fajrTime);
        fajr.setAnimation(animation);
        sunrise = (TextView) rootView.findViewById(R.id.sunriseTime);
        sunrise.setAnimation(animation);
        zuhr = (TextView) rootView.findViewById(R.id.zuhrTime);
        zuhr.setAnimation(animation);
        asr = (TextView) rootView.findViewById(R.id.asrTime);
        asr.setAnimation(animation);
        magrib = (TextView) rootView.findViewById(R.id.magribTime);
        magrib.setAnimation(animation);
        isha = (TextView) rootView.findViewById(R.id.ishaTime);
        isha.setAnimation(animation);
        salahNow = (TextView) rootView.findViewById(R.id.textView7);
        remain = (TextView) rootView.findViewById(R.id.textView8);

        locationInfo = ConfigPreferences.getLocationConfig(getContext());
        if (locationInfo != null) {
            lr = new LocationReader(context);
            lr.read(locationInfo.latitude,locationInfo.longitude);
            country = (TextView) rootView.findViewById(R.id.textView2);
            country.setText((getResources().getConfiguration()
                    .locale.getDisplayLanguage()
                    .equals("العربية") ? locationInfo.name_english : locationInfo.name));

            city = (TextView) rootView.findViewById(R.id.textView32);
            city.setText(getString(R.string.near) + " " + (getResources().getConfiguration()
                    .locale.getDisplayLanguage()
                    .equals("العربية") ? locationInfo.city_ar : locationInfo.city));


            if (lr.isAvailable()){

                getPrayer();
            }


        }


    }

    private void getPrayer() {
        if (lr == null || !lr.isAvailable()) return ;
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int timeZone = calendar.getTimeZone().getRawOffset()/(1000*60*60);
        int dst = calendar.getTimeZone().getDSTSavings();

        if (locationInfo.dls == 1){
            dst = 1;
        }
        PrayerTimes.Mazhab mazhab =PrayerTimes.Mazhab.PTC_MAZHAB_SHAFEI;
        PrayerTimes.Way way = PrayerTimes.Way.PTC_WAY_EGYPT;
        switch (locationInfo.way){
            case 0:
                way = PrayerTimes.Way.PTC_WAY_EGYPT;
                break;
            case 1:
                way = PrayerTimes.Way.PTC_WAY_KARACHI;
                break;
            case 2:
                way = PrayerTimes.Way.PTC_WAY_ISNA;
                break;
            case 3:
                way = PrayerTimes.Way.PTC_WAY_UMQURA;
                break;
            case 4:
                way = PrayerTimes.Way.PTC_WAY_MWL;
                break;
        }

        switch (locationInfo.mazhab){
            case 0:
                mazhab = PrayerTimes.Mazhab.PTC_MAZHAB_SHAFEI;
                break;
            case 1:
                mazhab = PrayerTimes.Mazhab.PTC_MAZHAB_HANAFI;
                break;
        }

        prayerTimes =  new PrayerTimes(day, month+1, year, lr.getLatitude(), lr.getLongitude(), timeZone, (locationInfo.dls>0), mazhab, way);
        updateViews();

    }

    Date fajrDate , sunriseDate , duhrDate , asrDate , maghrebDate , ishaDate , midNightDate;
    private void updateViews() {

        Calendar mid = Calendar.getInstance();
        mid.set(Calendar.HOUR_OF_DAY , 0);
        mid.set(Calendar.MINUTE , 0);
        mid.set(Calendar.SECOND , 0);
        midNightDate = mid.getTime();


        Date [] dates = prayerTimes.get();

        fajrDate = dates[0];
        fajr.setText(format.format(fajrDate));

        sunriseDate = dates[1];
        sunrise.setText(format.format(sunriseDate));

        duhrDate = dates[2];
        zuhr.setText(format.format(duhrDate));

        asrDate = dates[3];
        asr.setText(format.format(asrDate));

        maghrebDate = dates[4];
        magrib.setText(format.format(maghrebDate));

        ishaDate = dates[5];
        isha.setText(format.format(ishaDate));

        checkActiveView();

    }

    String nextPray = "";
    Date nextDate , lastDate;
    private void checkActiveView() {
        if (fajrDate == null || sunriseDate == null || duhrDate == null || asrDate == null || maghrebDate == null || ishaDate == null) return;
        removeActiveViews();
        Date current = Calendar.getInstance().getTime();



        if (current.after(fajrDate) && current.before(sunriseDate)){
            pray2.setBackgroundColor(Color.argb(255, 73, 138, 127));
            nextPray = getString(R.string.sunrise);
            lastDate = fajrDate;
            nextDate = sunriseDate;
        }else if (current.after(sunriseDate) && current.before(duhrDate)){
            pray3.setBackgroundColor(Color.argb(255, 73, 138, 127));
            nextPray = getString(R.string.zuhr);
            lastDate = sunriseDate;
            nextDate = duhrDate;
        }else if (current.after(duhrDate) && current.before(asrDate)){
            pray4.setBackgroundColor(Color.argb(255, 73, 138, 127));
            nextPray = getString(R.string.asr);
            lastDate = duhrDate;
            nextDate = asrDate;
        }else if (current.after(asrDate) && current.before(maghrebDate)){
            pray5.setBackgroundColor(Color.argb(255, 73, 138, 127));
            nextPray = getString(R.string.magrib);
            lastDate = asrDate;
            nextDate = maghrebDate;
        }else if (current.after(maghrebDate) && current.before(ishaDate)){
            pray6.setBackgroundColor(Color.argb(255, 73, 138, 127));
            nextPray = getString(R.string.isha);
            lastDate = maghrebDate;
            nextDate = ishaDate;
        }else {

            if (current.after(midNightDate) && current.before(fajrDate)){
                lastDate = getPrayerforPreviousDay().get()[5];
                nextDate = fajrDate;
            }else {

                lastDate = ishaDate;
                nextDate = getPrayerforNextDay().get()[0];
            }
            pray1.setBackgroundColor(Color.argb(255, 73, 138, 127));
            nextPray = getString(R.string.fajr);


        }

        salahNow.setText(NumbersLocal.convertNumberType(getContext(), nextPray + " " + format.format(nextDate)));


        Log.i("DATE_TAg" ,"last : "+format.format(lastDate));
        Log.i("DATE_TAg" ,"current : "+format.format(current));
        Log.i("DATE_TAg" ,"end : "+format.format(nextDate));



        updateTimer(current);

    }
    private void removeActiveViews(){
        pray1.setBackgroundColor(Color.TRANSPARENT);
        pray2.setBackgroundColor(Color.TRANSPARENT);
        pray3.setBackgroundColor(Color.TRANSPARENT);
        pray4.setBackgroundColor(Color.TRANSPARENT);
        pray5.setBackgroundColor(Color.TRANSPARENT);
        pray6.setBackgroundColor(Color.TRANSPARENT);
    }


    @Override
    public void onStop() {
        super.onStop();
        if (timer != null){
            timer.cancel();
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        context.unregisterReceiver(settingsChangeReceiver);
    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onStart() {
        super.onStart();
        context.registerReceiver(settingsChangeReceiver , new IntentFilter("prayer.information.change.in.settings"));
    }

    @Override
    public void onResume() {
        super.onResume();
        checkActiveView();
    }

    int i =0;
    private BroadcastReceiver settingsChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (lr != null && lr.isAvailable()){
                i ++;
                Log.i("settingsChanges" , "item #"+i);
                Toast.makeText(context , "Prayer settings Changed" , Toast.LENGTH_SHORT).show();
                locationInfo = ConfigPreferences.getLocationConfig(getContext());
                lr.read(locationInfo.latitude,locationInfo.longitude);
                getPrayer();
            }
        }
    };
    Calendar endCal = Calendar.getInstance() ,startCal = Calendar.getInstance() , currCal = Calendar.getInstance();
    private void updateTimer(Date current) {


        endCal.setTime(nextDate);
        startCal.setTime(lastDate);
        currCal.setTime(current);
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                final long timeRemaining = endCal.getTimeInMillis() - Calendar.getInstance().getTimeInMillis();

                int seconds = (int) (timeRemaining / 1000) % 60;
                int minutes = (int) ((timeRemaining / (1000 * 60)) % 60);
                int hours = (int) ((timeRemaining / (1000 * 60 * 60)) % 24);
                int days = (int) (timeRemaining / (1000 * 60 * 60 * 24));
                boolean hasDays = days > 0;
                final String timeNow = String.format("%1$02d%4$s%2$02d%5$s%3$02d%6$s",
                        hasDays ? days : hours,
                        hasDays ? hours : minutes,
                        hasDays ? minutes : seconds,
                        hasDays ? ":" : ":",
                        hasDays ? ":" : ":",
                        hasDays ? "m" : "s");

                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (timeRemaining <= 0) {
                                checkActiveView();
                                return;
                            }
                            remain.setText(timeNow);
                        }
                    });
                }

            }
        } , 1000 , 1000);


    }

    private PrayerTimes getPrayerforPreviousDay() {

        if (lr == null || !lr.isAvailable()) return null;
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH , calendar.get(Calendar.DAY_OF_MONTH) - 1);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int timeZone = calendar.getTimeZone().getRawOffset()/(1000*60*60);
        int dst = calendar.getTimeZone().getDSTSavings();
        if (locationInfo.dls == 1){
            dst = 1;
        }
        return new PrayerTimes(day, month+1, year, lr.getLatitude(), lr.getLongitude(), timeZone, !(dst>0), PrayerTimes.getDefaultMazhab(lr.getCountryCode()), PrayerTimes.getDefaultWay(lr.getCountryCode()));
    }


    private PrayerTimes getPrayerforNextDay() {
        if (lr == null || !lr.isAvailable()) return null;
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH , calendar.get(Calendar.DAY_OF_MONTH) + 1);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int timeZone = calendar.getTimeZone().getRawOffset()/(1000*60*60);
        int dst = calendar.getTimeZone().getDSTSavings();
        if (locationInfo.dls == 1){
            dst = 1;
        }
        return new PrayerTimes(day, month+1, year, lr.getLatitude(), lr.getLongitude(), timeZone, !(dst>0), PrayerTimes.getDefaultMazhab(lr.getCountryCode()), PrayerTimes.getDefaultWay(lr.getCountryCode()));
    }



}
