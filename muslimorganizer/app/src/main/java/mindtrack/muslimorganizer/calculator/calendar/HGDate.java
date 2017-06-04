/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mindtrack.muslimorganizer.calculator.calendar;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author fekracomputers
 */
public class HGDate {
    private double julianDay;
    private int day;
    private int month;
    private int year;

    public enum DT {NONE, HIGRI, GREGORIAN};

    DT type;

    public HGDate() {
        type = DT.NONE;
        julianDay = 0;
        day = 0;
        month = 0;
        year = 0;
        DateNow();
    }

    public HGDate(HGDate d) {
        type = d.type;
        julianDay = d.julianDay;
        day = d.day;
        month = d.month;
        year = d.year;
    }


    public Date DateNow() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        String[] formattedDate = df.format(c.getTime()).split("-");
        setGregorian(Integer.parseInt(formattedDate[2].trim()),
                Integer.parseInt(formattedDate[1].trim()),
                Integer.parseInt(formattedDate[0].trim()));


        return c.getTime();
    }

    private final double HIGRI_EPOCH = 1948438.5; //1948439.5

    public double higri_to_jd(int year, int month, int day) {
        return day +
                Math.ceil(29.5 * (month - 1)) +
                (year - 1) * 354 +
                Math.floor((3 + (11 * year)) / 30) +
                HIGRI_EPOCH - 1;
    }

    private final double GREGORIAN_EPOCH = 1721425.5;

    private boolean leap_gregorian(int year) {
        return ((year % 4) == 0) && (!(((year % 100) == 0) && ((year % 400) != 0)));
    }

    private long mod(long a, long b) {
        return (long) (a - (b * Math.floor(a / b)));
    }

    private double gregorian_to_jd(int year, int month, int day) {
        return (double) ((GREGORIAN_EPOCH - 1) +
                (365 * (year - 1)) +
                Math.floor((year - 1) / 4) +
                (-Math.floor((year - 1) / 100)) +
                Math.floor((year - 1) / 400) +
                Math.floor((((367 * month) - 362) / 12) +
                        ((month <= 2) ? 0 : (leap_gregorian(year) ? -1 : -2)) +
                        day));
    }

    public boolean setHigri(int year, int month, int day) {
        if (year < 1 || month < 1 || day < 1) return false;

        julianDay = higri_to_jd(year, month, day);
        toHigri();
        if (year != this.year || month != this.month || day != this.day) {
            this.type = DT.NONE;
            return false;
        }

        return true;
    }

    public boolean setGregorian(int year, int month, int day) {
        if (year < 622 || month < 1 || day < 1) return false;
        if (year == 622 && month < 7) return false;
        if (year == 622 && month == 7 && day < 18) return false;

        julianDay = gregorian_to_jd(year, month, day);
        toGregorian();
        if (year != this.year || month != this.month || day != this.day) {
            this.type = DT.NONE;
            return false;
        }

        return true;
    }

    public boolean toHigri() {
        double jd = Math.floor(julianDay) + 0.5;

        year = (int) Math.floor(((30 * (jd - HIGRI_EPOCH)) + 10646) / 10631);
        month = (int) Math.min(12, Math.ceil((jd - (29 + higri_to_jd(year, 1, 1))) / 29.5) + 1);
        day = (int) (jd - higri_to_jd(year, month, 1) + 1);

        type = DT.HIGRI;

        return true;
    }

    public boolean toGregorian() {
        double jd, depoch, quadricent, dqc, cent, dcent, quad, dquad, yindex, dyindex, yearday, leapadj;

        jd = Math.floor(julianDay - 0.5) + 0.5;
        depoch = jd - GREGORIAN_EPOCH;
        quadricent = Math.floor(depoch / 146097);
        dqc = mod((long) depoch, 146097);
        cent = Math.floor(dqc / 36524);
        dcent = mod((long) dqc, 36524);
        quad = Math.floor(dcent / 1461);
        dquad = mod((long) dcent, 1461);
        yindex = Math.floor(dquad / 365);
        year = (int) ((quadricent * 400) + (cent * 100) + (quad * 4) + yindex);
        if (!((cent == 4) || (yindex == 4))) {
            year++;
        }
        yearday = (long) (jd - gregorian_to_jd((int) year, 1, 1));
        leapadj = ((jd < gregorian_to_jd((int) year, 3, 1)) ? 0 : (leap_gregorian((int) year) ? 1 : 2));
        month = (int) Math.floor((((yearday + leapadj) * 12) + 373) / 367);
        day = (int) (jd - gregorian_to_jd((int) year, month, 1) + 1);

        type = DT.GREGORIAN;

        return true;
    }

    public int getDay() {
        return day;
    }

    public int getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }

    public DT getType() {
        return type;
    }

    public int weekDay() {
        return (int) mod((long) Math.floor((julianDay + 1.5)), 7);
    }

    public void nextDay() {
        julianDay = julianDay + 1;
        if (type == DT.HIGRI) toHigri();
        else if (type == DT.GREGORIAN) toGregorian();
    }

    public void previousDay() {
        julianDay = julianDay - 1;
        if (type == DT.HIGRI) toHigri();
        else if (type == DT.GREGORIAN) toGregorian();
    }

    @Override
    public String toString() {
        return "" + day + "/" + month + "/" + year;
    }
}

