package mindtrack.muslimorganizer.utility;

import android.content.Context;

import mindtrack.muslimorganizer.R;
import mindtrack.muslimorganizer.database.ConfigPreferences;

/**
 * Class contains functions to calculate times and dates
 */
public class Calculators {

    /**
     * Function to extract minutes only from double time
     *
     * @param time Double time
     * @return Integer minutes
     */
    public static int extractMinutes(double time) {
        /*Log.d("String_date_m" , time+"");
        String preparedNumber = (time + "").substring(0, 3);
        double timeDouble = new Double(time);
        int houre = (int) timeDouble;
        double minsDouble = (60 * (timeDouble - houre)) / 100;
        preparedNumber = (minsDouble + "").substring(0, (minsDouble + "").length() < 4 ? 3 : 4);
        double mins = new Double(preparedNumber);
        String minsFinal = String.valueOf(mins).replace("0.", "");
        Log.d("String_date_m" , minsFinal+"");
        return Integer.parseInt(minsFinal.trim());*/

        String preparedNumber = (time + "").substring(0, 3);
        double timeDouble = new Double(time);
        int hour = (int) timeDouble;
        double minsDouble = (60 * (timeDouble - hour)) / 100;

        preparedNumber = (minsDouble + "").substring(0, (minsDouble + "").length() < 4 ? 3 : 4);
        double mins = new Double(preparedNumber);
        String minsFinal = String.valueOf(mins).replace("0.", "");

        return Integer.parseInt((minsFinal.length() == 1 ? minsFinal + "0" : minsFinal).trim()) ;

    }

    /**
     * Function to extract Hours only from double time
     *
     * @param time Double time
     * @return Integer hours
     */
    public static int extractHour(double time) {
        String preparedNumber = (time + "").substring(0, 3);
        double timeDouble = Double.parseDouble(preparedNumber);
        return (int) timeDouble;
    }


    /**
     * Function to extract praying time from double numbers
     *
     * @param time Double time
     * @return String Time of pray
     */
    public static String extractPrayTime(Context context, double time) {

        boolean pmFlag = false;
        String preparedNumber = (time + "").substring(0, 3);
        double timeDouble = new Double(time);
        int hour = (int) timeDouble;
        double minsDouble = (60 * (timeDouble - hour)) / 100;

        preparedNumber = (minsDouble + "").substring(0, (minsDouble + "").length() < 4 ? 3 : 4);
        double mins = new Double(preparedNumber);
        String minsFinal = String.valueOf(mins).replace("0.", "");
        if (ConfigPreferences.getTwentyFourMode(context) != true) {
            if (hour > 12) {
                hour -= 12;
                pmFlag = true;
            }
        }
        return NumbersLocal.convertNumberType(context, hour + ":" +
                (minsFinal.length() == 1 ? minsFinal + "0" : minsFinal) + " " +
                ((hour >= 12 || pmFlag) ? context.getString(R.string.pm) : context.getString(R.string.am)));
    }


}
