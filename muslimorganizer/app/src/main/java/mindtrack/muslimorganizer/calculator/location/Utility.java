package mindtrack.muslimorganizer.calculator.location;

/**
 * Created by sobh on 4/8/2017.
 */

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.widget.EditText;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import mindtrack.muslimorganizer.R;

public class Utility {

    public static String getStringPrefs (Context context, String prefsKey , String defaultValue){
        return PreferenceManager.getDefaultSharedPreferences(context).getString(prefsKey, defaultValue);
    }

    public static void saveStringPrefs (Context context, String prefsKey, String prefsValue){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        preferences.edit().putString(prefsKey, prefsValue).apply();
    }

    public static Date getCurrentTime (){
        return Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTime();
    }

    public static Date parseDate (String sDate){

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date = null;
        try {
            date = sdf.parse(sDate);
        } catch (ParseException e) {}

        return date;
    }

    public static String formatDate (Date date){

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        return sdf.format(date);
    }

    public static String getAndroidID(Context context)
    {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public static void getInput(Context context, String title, DialogInterface.OnClickListener onOkListener)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);

        final EditText input = new EditText(context);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setId(R.id.text1);
        builder.setView(input);

        builder.setPositiveButton("OK", onOkListener);

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    public static String getInputText(DialogInterface dialog)
    {
        return ((EditText) ((AlertDialog) dialog).findViewById(R.id.text1)).getText().toString();
    }

    public static double getGeoDistance(double latitude1, double  longitude1, double  latitude2, double longitude2)
    {
        final double r = 6373; // Radius of the earth in km
        double dLatitude = Math.toRadians(latitude2-latitude1);  // deg2rad below
        double dLongitude = Math.toRadians(longitude2-longitude1);

        double a = Math.sin(dLatitude/2) * Math.sin(dLatitude/2) + Math.cos(Math.toRadians(latitude1)) * Math.cos(Math.toRadians(latitude2)) *  Math.sin(dLongitude/2) * Math.sin(dLongitude/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double d = r * c; // Distance in km

        return 1000 * Math.abs(d);
    }
}

