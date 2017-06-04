package mindtrack.muslimorganizer.model;

/**
 * Model class for calender cell
 */
public class CalendarCell {

    public int day, dayOther, hijriMonth , georgianMonth, week;
    public String tag;

    public CalendarCell(int day, int dayOther, int hijriMonth , int georgianMonth, int week) {
        this.day = day;
        this.dayOther = dayOther;
        this.hijriMonth = hijriMonth;
        this.georgianMonth = georgianMonth ;
        this.week = week;
    }

}