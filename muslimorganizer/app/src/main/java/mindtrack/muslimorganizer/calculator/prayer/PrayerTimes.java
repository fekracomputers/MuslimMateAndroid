/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mindtrack.muslimorganizer.calculator.prayer;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * 
 * @author Usama
 */
public class PrayerTimes {

    private double fajr;
    private double sunrise;
    private double thuhr;
    private double asr;
    private double sunset;
    private double maghreb;
    private double isha;
    
    private final double latitude;
    private final double longitude;
    private final double timeZone;
    private final boolean dayLightSaving;
    
    public enum Way {PTC_WAY_EGYPT, PTC_WAY_KARACHI, PTC_WAY_ISNA, PTC_WAY_UMQURA, PTC_WAY_MWL};
    public enum Mazhab {PTC_MAZHAB_SHAFEI, PTC_MAZHAB_HANAFI};

    private final Mazhab mazhab;
    private final Way way;
    
    private final int day;
    private final int month;
    private final int year;
    private final double julianDate;
    
    private double[] timePortions = null;

    public PrayerTimes(int day, int month, int year, double latitude, double longitude, double timeZone, boolean dayLightSaving, Mazhab mazhab, Way way) {
        
        this.timePortions = new double[] { 5 / 24.0, 6 / 24.0, 12 / 24.0, 13 / 24.0, 18 / 24.0, 18 / 24.0, 18 / 24.0 };

        this.day = day;
        this.month = month;
        this.year = year;
        this.julianDate = calculateJulianDate(this.day, this.month, this.year);
        
        this.latitude = latitude;
        this.longitude = longitude;
        this.timeZone = timeZone;
        this.dayLightSaving = dayLightSaving;

        this.mazhab = mazhab;
        this.way = way;
    }

    private static double fixHours(double a) {

        a = a - 24.0 * (Math.floor(a / 24.0));
        a = a < 0 ? a + 24.0 : a;

        return a;
    }

    private static double calculateJulianDate(int day, int month, int year) {

        if (month <= 2) {
            year -= 1;
            month += 12;
        }

        int a = year / 100;
        int b = a / 4;
        int c = 2 - a + b;
        int d = day;
        int e = (int) (365.25 * (year + 4716));
        int f = (int) (30.7 * (month + 1));
        double julianDate = c + d + e + f - 1524.5 - 1;

        return julianDate;
    }

    private static double calculateEquationOfTime(double julianDate, double timePortion) {

        double d = julianDate - 2451545.0 + timePortion;
        double g = PrayerTimesMath.fixAngle(357.529 + 0.98560028 * d);
        double q = PrayerTimesMath.fixAngle(280.459 + 0.98564736 * d);
        double l = q + 1.915 * (PrayerTimesMath.dSin(g)) + 0.020 * (PrayerTimesMath.dSin(2 * g));
        double e = 23.439 - 0.00000036 * d;
        double ra = PrayerTimesMath.dATan2((PrayerTimesMath.dCos(e)) * (PrayerTimesMath.dSin(l)), (PrayerTimesMath.dCos(l))) / 15;
        ra = fixHours(ra);
        double equationOfTime = q / 15 - ra;

        return equationOfTime;
    }

    private static double calculateSunDeclination(double julianDate, double timePortion) {

        double d = julianDate - 2451545.0 + timePortion;
        double g = 357.529 + 0.98560028 * d;
        double q = 280.459 + 0.98564736 * d;
        double l = q + 1.915 * (PrayerTimesMath.dSin(g)) + 0.020 * (PrayerTimesMath.dSin(2 * g));
        double e = 23.439 - 0.00000036 * d;
        double sunDeclination = PrayerTimesMath.dASin((PrayerTimesMath.dSin(e)) * (PrayerTimesMath.dSin(l)));

        return sunDeclination;
    }

    private static double calculateMidDay(double julianDate, double timePortion) {

        double equationOfTime = calculateEquationOfTime(julianDate, timePortion);
        double midDay = fixHours(12 - equationOfTime);

        return midDay;
    }
    
    // Sunrise and Sunset
    private static double calculateSunDuration(double a, double latitude, double julianDate, double timePortion) {

        // time period between mid-day and the time at which sun reaches an angle below the horizon
        double sunDeclination = calculateSunDeclination(julianDate, timePortion);
        a = PrayerTimesMath.dSin(a);
        double b = (PrayerTimesMath.dSin(latitude)) * (PrayerTimesMath.dSin(sunDeclination));
        double c = (PrayerTimesMath.dCos(latitude)) * (PrayerTimesMath.dCos(sunDeclination));
        double d = (PrayerTimesMath.dACos((-a - b) / c));
        double sunDuration = 2 * d / 15;

        return sunDuration;
    }

    private void calculateThuhr() {

        double equationOfTime = calculateEquationOfTime(this.julianDate, this.timePortions[2]);
        this.thuhr = fixHours(12 - equationOfTime);
    }

    // Sunrise
    private void calculateSunrise() {

        this.sunrise = calculateMidDay(this.julianDate, this.timePortions[1]) - calculateSunDuration(0.8333, this.latitude, this.julianDate, this.timePortions[1])/2;
    }

    // Sunset & Maghreb prayer
    private void calculateSunset() {

        this.sunset = calculateMidDay(this.julianDate, this.timePortions[5]) + calculateSunDuration(0.8333, this.latitude, this.julianDate, this.timePortions[4])/2;
    }

    // Asr Prayer
    private void calculateAsr() {
        
        int m = 0;
        switch(this.mazhab){
            case PTC_MAZHAB_SHAFEI: m  = 0; break;
            case PTC_MAZHAB_HANAFI: m  = 1; break;
        }

        double d = calculateSunDeclination(this.julianDate, this.timePortions[3]);
        double g = -PrayerTimesMath.dACot(m + 1 + PrayerTimesMath.dTan(Math.abs(this.latitude - d)));
        double s = calculateSunDeclination(this.julianDate, this.timePortions[3]);
        double z = calculateMidDay(this.julianDate, this.timePortions[3]);
        double v = ((double) (1 / 15.0)) * PrayerTimesMath.dACos((-PrayerTimesMath.dSin(g) - PrayerTimesMath.dSin(s) * PrayerTimesMath.dSin(this.latitude)) / (PrayerTimesMath.dCos(s) * PrayerTimesMath.dCos(this.latitude)));

        this.asr = z + (g > 90 ? -v : v);
    }

    // Fajr and Isha
    private void calculateFajrAndIsha() {

        if (this.latitude <= 50) {
            
            switch (this.way) {
                case PTC_WAY_EGYPT: // Egyptian General Authority of Survey
                    this.fajr = calculateMidDay(this.julianDate, this.timePortions[0]) - calculateSunDuration(19.5, this.latitude, this.julianDate, this.timePortions[0])/2;
                    this.isha = calculateMidDay(this.julianDate, this.timePortions[6]) + calculateSunDuration(17.5, this.latitude, this.julianDate, this.timePortions[6])/2;
                    break;
                case PTC_WAY_KARACHI: // Karachi
                    this.fajr = calculateMidDay(this.julianDate, this.timePortions[0]) - calculateSunDuration(18, this.latitude, this.julianDate, this.timePortions[0])/2;
                    this.isha = calculateMidDay(this.julianDate, this.timePortions[6]) + calculateSunDuration(18, this.latitude, this.julianDate, this.timePortions[6])/2;
                    break;
                case PTC_WAY_ISNA: // Islamic Society of North America (ISNA)
                    this.fajr = calculateMidDay(this.julianDate, this.timePortions[0]) - calculateSunDuration(15, this.latitude, this.julianDate, this.timePortions[0])/2;
                    this.isha = calculateMidDay(this.julianDate, this.timePortions[6]) + calculateSunDuration(15, this.latitude, this.julianDate, this.timePortions[6])/2;
                    break;
                case PTC_WAY_UMQURA: // Umm al-Qura, Makkah
                    this.fajr = calculateMidDay(this.julianDate, this.timePortions[0]) - calculateSunDuration(18.5, this.latitude, this.julianDate, this.timePortions[0])/2;
                    this.isha = maghreb + 1.5;
                    break;
                case PTC_WAY_MWL: // Muslim World League(MWL)
                    this.fajr = calculateMidDay(this.julianDate, this.timePortions[0]) - calculateSunDuration(18, this.latitude, this.julianDate, this.timePortions[0])/2;
                    this.isha = calculateMidDay(this.julianDate, this.timePortions[6]) + calculateSunDuration(17, this.latitude, this.julianDate, this.timePortions[6])/2;
                    break;
            }
            
        } else {// high latitude. One-Seventh of the Night method is used

            // period between sunset-sunrise
            double p = 24 - (this.sunset - this.sunrise);
            this.fajr = this.sunrise - 2 * p / 7.0 - 1 / 6.0;
            this.isha = this.sunset + 2 * p / 7.0 - 1 / 15.0;

        }        
    }

    private double[] calculate() {
        
        calculateSunrise();
        calculateSunset();
        calculateThuhr();
        calculateAsr();
        this.maghreb = this.sunset;
        calculateFajrAndIsha();
        
        double[] prayers = new double[6];
        prayers[0] = this.fajr;
        prayers[1] = this.sunrise;
        prayers[2] = this.thuhr;
        prayers[3] = this.asr;
        prayers[4] = this.maghreb;
        prayers[5] = this.isha;
        
        return prayers;
    }
    
    private void updateTimePortions(double[] prayers) {
        
        for (int i = 0; i < prayers.length; i++){
            this.timePortions[i] = prayers[i] / 24.0;
        }
        
        this.timePortions[6] = 1.0;
    }
    
    private double[] adjust(double[] prayers) {
        
        for (int i = 0; i < prayers.length; i++) {
            prayers[i] = prayers[i] + this.timeZone - this.longitude / 15.0;
            prayers[i] = (Math.ceil(prayers[i] * 60)) / 60.0;
            if (this.dayLightSaving)
                prayers[i]++;
        }
        
        return prayers;
    }


    public Date getDateNow() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR , year);
        calendar.set(Calendar.MONTH , month);
        calendar.set(Calendar.DATE , day);
        return calendar.getTime();
    }

    public Date[] get() {
        
        double[] prayers = calculate();
        updateTimePortions(prayers);
        
        prayers = calculate();
        prayers = adjust(prayers);
        
        Date[] prayersTimes;
        prayersTimes = new Date[prayers.length];
        
        Calendar calendar = Calendar.getInstance();
        
        for (int i = 0; i < prayers.length; i++) {
            calendar.set(this.year, this.month -1, this.day, (int)prayers[i], (int)(60.0 * (prayers[i]-(int)prayers[i])), 0);
            prayersTimes[i] = calendar.getTime();
        }

        return prayersTimes;
    }

    public static Mazhab getDefaultMazhab(String countryCode) {
        countryCode = countryCode.toUpperCase();

        if("AF, AL, EG".indexOf(countryCode)>=0) return Mazhab.PTC_MAZHAB_SHAFEI;

        return Mazhab.PTC_MAZHAB_HANAFI;
    }

    public static Way getDefaultWay(String countryCode) {
        countryCode = countryCode.toUpperCase();

        if("CM, CF, CD, CG, CI, EG, GH, IQ, KE, LY, MY, ML, SN, SO, SD, TN".indexOf(countryCode)>=0) return Way.PTC_WAY_EGYPT;

        if("AF, AL, BD, IN, PK, WF".indexOf(countryCode)>=0) return Way.PTC_WAY_KARACHI;

        if("CA, US".indexOf(countryCode)>=0) return Way.PTC_WAY_ISNA;

        if("BH, KW, OM, QA, SA, AE, YE".indexOf(countryCode)>=0) return Way.PTC_WAY_UMQURA;

        return Way.PTC_WAY_MWL;
    }
}
