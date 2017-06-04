package mindtrack.muslimorganizer.database;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import java.util.List;

import mindtrack.muslimorganizer.model.LocationInfo;
import mindtrack.muslimorganizer.model.Weather;
import mindtrack.muslimorganizer.model.WeatherSave;

/**
 * Class to save application configurations
 */
public class ConfigPreferences {
    private static final String MAIN_CONFIG = "application_settings";
    public static final String LOCATION_INFO = "location_information",
            QUIBLA_DEGREE = "quibla_degree", ALARM = "alarm",
            NEXT_PRAY = "next_pray", WEATHER_INFO = "Weather",
            TODAY_WETHER = "today_weather", WEEK_WETHER = "week_weather",
            APP_LANGUAGE = "app_language", PRAY_NOTIFY = "pray_notify",
            ZEKER_NOTIFY = "zeker_notifiy", ZEKER_NOTIFICATION = "zeker_notification",
            SILENT_MOOD = "silent_mood", LED_MOOD = "led_mood",
            WIDGET_MONTH = "widget_month", VIBRATION = "vibration_mood",
            TWENTYFOUR = "twenty_four", AZKAR_MOOD = "azkar_mood",
            COUNTRY_POPUP = "country_popup", APP_FIRST_OPEN = "application_first_open";

    /**
     * Function to save location information
     *
     * @param locationConfig
     */
    public static void setLocationConfig(Context context, LocationInfo locationConfig) {
        SharedPreferences.Editor editor = context.getSharedPreferences
                (MAIN_CONFIG, Context.MODE_PRIVATE).edit();
        Gson gson = new Gson();
        String json = gson.toJson(locationConfig);
        editor.putString(LOCATION_INFO, json);
        editor.commit();
    }

    /**
     * Function to get location information
     *
     * @return LocationInfo object
     */
    public static LocationInfo getLocationConfig(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences
                (MAIN_CONFIG, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString(LOCATION_INFO, "");
        LocationInfo locationInfo = gson.fromJson(json, LocationInfo.class);
        return locationInfo;
    }

    /**
     * Function to save weather
     *
     * @param weather Weather list
     */
    public static void setWeather(Context context, List<Weather> weather) {
        SharedPreferences.Editor editor = context.getSharedPreferences
                (MAIN_CONFIG, Context.MODE_PRIVATE).edit();
        Gson gson = new Gson();
        String json = gson.toJson(new WeatherSave(weather));
        editor.putString(WEATHER_INFO, json);
        editor.commit();
    }

    /**
     * Function to get saved weather
     *
     * @return
     */
    public static WeatherSave getWeather(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences
                (MAIN_CONFIG, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString(WEATHER_INFO, "");
        WeatherSave weathers = gson.fromJson(json, WeatherSave.class);
        return weathers;
    }

    /**
     * Function to save today weather list
     *
     * @param weather Weather list of day
     */
    public static void setTodayListWeather(Context context, List<Weather> weather) {
        SharedPreferences.Editor editor = context.getSharedPreferences
                (MAIN_CONFIG, Context.MODE_PRIVATE).edit();
        Gson gson = new Gson();
        String json = gson.toJson(new WeatherSave(weather));
        editor.putString(TODAY_WETHER, json);
        editor.commit();
    }

    /**
     * Function to get today weather list
     *
     * @return Today weather list
     */
    public static WeatherSave getTodayListWeather(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences
                (MAIN_CONFIG, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString(TODAY_WETHER, "");
        WeatherSave weathers = gson.fromJson(json, WeatherSave.class);
        return weathers;
    }

    /**
     * Function to save weather of week
     *
     * @param weather Weather list of week
     */
    public static void setWeekListWeather(Context context, List<Weather> weather) {
        SharedPreferences.Editor editor = context.getSharedPreferences
                (MAIN_CONFIG, Context.MODE_PRIVATE).edit();
        Gson gson = new Gson();
        String json = gson.toJson(new WeatherSave(weather));
        editor.putString(WEEK_WETHER, json);
        editor.commit();
    }

    /**
     * Function to get weather of the week
     *
     * @return Weather list of week
     */
    public static WeatherSave getWeekListWeather(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences
                (MAIN_CONFIG, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString(WEEK_WETHER, "");
        WeatherSave weathers = gson.fromJson(json, WeatherSave.class);
        return weathers;
    }

    /**
     * Function to save Quibla degree
     *
     * @param degree degree from north
     */
    public static void setQuibla(Context context, int degree) {
        SharedPreferences.Editor editor = context.getSharedPreferences
                (MAIN_CONFIG, Context.MODE_PRIVATE).edit();
        editor.putInt(QUIBLA_DEGREE, degree);
        editor.commit();
    }


    /**
     * Function to get saved degree
     *
     * @return Quibla degree from north
     */
    public static int getQuibla(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences
                (MAIN_CONFIG, Context.MODE_PRIVATE);
        int degree = sharedPreferences.getInt(QUIBLA_DEGREE, -1);
        return degree;
    }

    /**
     * function to set alarm
     *
     * @param alarm alarm
     */
    public static void setAlarm(Context context, boolean alarm) {
        SharedPreferences.Editor editor = context.getSharedPreferences
                (MAIN_CONFIG, Context.MODE_PRIVATE).edit();
        editor.putBoolean(ALARM, alarm);
        editor.commit();
    }


    /**
     * Function to get alarm state
     *
     * @return Alarm state
     */
    public static boolean getAlarm(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences
                (MAIN_CONFIG, Context.MODE_PRIVATE);
        boolean alarm = sharedPreferences.getBoolean(ALARM, false);
        return alarm;
    }


    /**
     * Function to set next pray alarm time
     *
     * @param time
     */
    public static void setNextPrayAlarm(Context context, String time) {
        SharedPreferences.Editor editor = context.getSharedPreferences
                (MAIN_CONFIG, Context.MODE_PRIVATE).edit();
        editor.putString(NEXT_PRAY, time);
        editor.commit();
    }


    /**
     * Function to get next pray alarm time
     */
    public static String getNextPrayAlarm(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences
                (MAIN_CONFIG, Context.MODE_PRIVATE);
        String alarm = sharedPreferences.getString(NEXT_PRAY, null);
        return alarm;
    }


    /**
     * Function to set application language
     *
     * @param language Application language
     */
    public static void setApplicationLanguage(Context context, String language) {
        SharedPreferences.Editor editor = context.getSharedPreferences
                (MAIN_CONFIG, Context.MODE_PRIVATE).edit();
        editor.putString(APP_LANGUAGE, language);
        editor.commit();
    }


    /**
     * Function to get application language
     *
     * @return Application language
     */
    public static String getApplicationLanguage(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences
                (MAIN_CONFIG, Context.MODE_PRIVATE);
        String language = sharedPreferences.getString(APP_LANGUAGE, "en");
        return language;
    }


    /**
     * Function to set notification for praying
     *
     * @param notification Flag to notify or not
     */
    public static void setPrayingNotification(Context context, boolean notification) {
        SharedPreferences.Editor editor = context.getSharedPreferences
                (MAIN_CONFIG, Context.MODE_PRIVATE).edit();
        editor.putBoolean(PRAY_NOTIFY, notification);
        editor.commit();
    }


    /**
     * Function to get notification of pray or not
     *
     * @return
     */
    public static boolean getPrayingNotification(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences
                (MAIN_CONFIG, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(PRAY_NOTIFY, false);
    }

    /**
     * Function to set silent mood
     *
     * @param silent Flag of silent mood
     */
    public static void setSilentMood(Context context, boolean silent) {
        SharedPreferences.Editor editor = context.getSharedPreferences
                (MAIN_CONFIG, Context.MODE_PRIVATE).edit();
        editor.putBoolean(SILENT_MOOD, silent);
        editor.commit();
    }


    /**
     * Function to get silent mood
     *
     * @return Flag of silent mood
     */
    public static boolean getSilentMood(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences
                (MAIN_CONFIG, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(SILENT_MOOD, true);
    }


    /**
     * Function to set led mood
     *
     * @param led Flag of led mood
     */
    public static void setLedNotification(Context context, boolean led) {
        SharedPreferences.Editor editor = context.getSharedPreferences
                (MAIN_CONFIG, Context.MODE_PRIVATE).edit();
        editor.putBoolean(LED_MOOD, led);
        editor.commit();
    }

    /**
     * Function to get led mood
     *
     * @return Flag of led mood
     */
    public static boolean getLedNotification(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences
                (MAIN_CONFIG, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(LED_MOOD, true);
    }


    /**
     * Function to set current widget month show
     *
     * @param context Application context
     * @param month   Current month
     */
    public static void setCurrentWidgetMonth(Context context, int month) {
        SharedPreferences.Editor editor = context.getSharedPreferences
                (MAIN_CONFIG, Context.MODE_PRIVATE).edit();
        editor.putInt(WIDGET_MONTH, month);
        editor.commit();
    }


    /**
     * Function to get current widget month show
     *
     * @param context Application context
     * @return Current month
     */
    public static int getCurrentWidgetMonth(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences
                (MAIN_CONFIG, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(WIDGET_MONTH, 1);
    }


    /**
     * Function to set vibration mode
     *
     * @param context       Application context
     * @param vibrationFlag Vibration mode on / off
     */
    public static void setVibrationMode(Context context, boolean vibrationFlag) {
        SharedPreferences.Editor editor = context.getSharedPreferences
                (MAIN_CONFIG, Context.MODE_PRIVATE).edit();
        editor.putBoolean(VIBRATION, vibrationFlag);
        editor.commit();
    }


    /**
     * Function to get vibration mode
     *
     * @param context Application context
     * @return Current vibration mode
     */
    public static boolean getVibrationMode(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences
                (MAIN_CONFIG, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(VIBRATION, true);
    }


    /**
     * Function to set twenty four hour mode show
     *
     * @param context        Application context
     * @param twentyFourFlag on / off
     */
    public static void setTwentyFourMode(Context context, boolean twentyFourFlag) {

        SharedPreferences.Editor editor = context.getSharedPreferences
                (MAIN_CONFIG, Context.MODE_PRIVATE).edit();
        editor.putBoolean(TWENTYFOUR, twentyFourFlag);
        editor.commit();
    }


    /**
     * Function to get twenty four hour mode
     *
     * @param context Application context
     * @return on / off
     */
    public static boolean getTwentyFourMode(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences
                (MAIN_CONFIG, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(TWENTYFOUR, false);
    }


    /**
     * Function to set azkar mood
     *
     * @param context   Application context
     * @param azkarMood Azkar flag on / off
     */
    public static void setAzkarMood(Context context, boolean azkarMood) {
        SharedPreferences.Editor editor = context.getSharedPreferences
                (MAIN_CONFIG, Context.MODE_PRIVATE).edit();
        editor.putBoolean(AZKAR_MOOD, azkarMood);
        editor.commit();
    }

    /**
     * Function to get azkar mood
     *
     * @param context Application context
     * @return Azkar flag on / off
     */
    public static boolean getAzkarMood(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences
                (MAIN_CONFIG, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(AZKAR_MOOD, true);

    }

    /**
     * Function to set world prayer selected country
     *
     * @param context      Application context
     * @param locationInfo Location Information of selected country
     */
    public static void setWorldPrayerCountry(Context context, LocationInfo locationInfo) {
        SharedPreferences.Editor editor = context.getSharedPreferences
                (MAIN_CONFIG, Context.MODE_PRIVATE).edit();
        Gson gson = new Gson();
        String json = gson.toJson(locationInfo);
        editor.putString(COUNTRY_POPUP, json);
        editor.commit();

    }

    /**
     * Function to get world prayer selected country
     *
     * @param context Application context
     * @return LocationInfo of selected country
     */
    public static LocationInfo getWorldPrayerCountry(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences
                (MAIN_CONFIG, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString(COUNTRY_POPUP, "");
        LocationInfo locationInfo = gson.fromJson(json, LocationInfo.class);
        return locationInfo;
    }

    /**
     * Function to set application first open done
     *
     * @param context Application context
     */
    public static void setApplicationFirstOpenDone(Context context) {
        SharedPreferences.Editor editor = context.getSharedPreferences
                (MAIN_CONFIG, Context.MODE_PRIVATE).edit();
        editor.putBoolean(APP_FIRST_OPEN, false);
        editor.commit();
    }

    /**
     * Function to check if app 1st open or not
     *
     * @param context Application context
     * @return Flag of 1st open or not
     */
    public static boolean IsApplicationFirstOpen(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences
                (MAIN_CONFIG, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(APP_FIRST_OPEN, true);
    }


}
