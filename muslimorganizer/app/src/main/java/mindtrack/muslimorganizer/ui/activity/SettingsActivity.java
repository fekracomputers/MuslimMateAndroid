package mindtrack.muslimorganizer.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.Locale;

import mindtrack.muslimorganizer.R;
import mindtrack.muslimorganizer.database.ConfigPreferences;
import mindtrack.muslimorganizer.model.LocationInfo;
import mindtrack.muslimorganizer.utility.Alarms;

/**
 * Activity for settings
 */
public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String languageToLoad = ConfigPreferences.getApplicationLanguage(this);
        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.title_activity_settings));
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MainSettings()).commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }

    /**
     * Fragment of the main settings
     */
    public static class MainSettings extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
        private SharedPreferences sp;
        private ListPreference listPreference;
        LocationInfo locationInfo;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings);
            sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
            locationInfo = ConfigPreferences.getLocationConfig(getActivity());
            //disable and enable praying notification related setting
            if (ConfigPreferences.getPrayingNotification(getActivity()) == false) {
                getPreferenceScreen().findPreference("silent").setEnabled(false);
                getPreferenceScreen().findPreference("vibration").setEnabled(false);
                getPreferenceScreen().findPreference("led").setEnabled(false);
            }

            //disable or enable silent related settings
            if(ConfigPreferences.getSilentMood(getActivity()) == false){
                getPreferenceScreen().findPreference("vibration").setEnabled(false);
            }

            if (locationInfo != null) {
                Log.i("DATA_SETTING" ,"locationInfo.dls : "+(locationInfo.dls > 0));
                CheckBoxPreference checked = (CheckBoxPreference) getPreferenceScreen().findPreference("day_light");
                checked.setChecked(locationInfo.dls > 0);
                ListPreference wayPref = (ListPreference) getPreferenceScreen().findPreference("calculations");
                Log.i("DATA_SETTING" ,"locationInfo.way : "+locationInfo.way);
                wayPref.setValueIndex(locationInfo.way);
                ListPreference mazhapPref = (ListPreference) getPreferenceScreen().findPreference("mazhab");
                mazhapPref.setValueIndex(locationInfo.mazhab);
                Log.i("DATA_SETTING" ,"locationInfo.mazhab : "+locationInfo.mazhab);
            }

            listPreference = (ListPreference) findPreference("language");
            String lang = ConfigPreferences.getApplicationLanguage(getActivity()).equalsIgnoreCase("en") ? "English" : "العربية";
            listPreference.setSummary(getString(R.string.language_summary)
                    + "  (" + lang + ") ");

        }

        @Override
        public void onResume() {
            super.onResume();
            getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onPause() {
            super.onPause();
            getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

            switch (key) {
                case "language":
                    changeLanguage(sharedPreferences.getString(key, null));
                    break;
                case "notification_azkar":
                    ConfigPreferences.setAzkarMood(getActivity(), sharedPreferences.getBoolean(key, true));
                    break;
                case "notification_islamic":
                    sharedPreferences.getBoolean(key, false);
                    break;
                case "notification_prayer":
                    ConfigPreferences.setPrayingNotification(getActivity(), sharedPreferences.getBoolean(key, true));
                    if (sharedPreferences.getBoolean(key, true) == false) {
                        ConfigPreferences.setVibrationMode(getActivity(), false);
                        ConfigPreferences.setSilentMood(getActivity(), false);
                        ConfigPreferences.setLedNotification(getActivity(), false);
                        getPreferenceScreen().findPreference("silent").setEnabled(false);
                        getPreferenceScreen().findPreference("vibration").setEnabled(false);
                        getPreferenceScreen().findPreference("led").setEnabled(false);
                    } else {
                        ConfigPreferences.setVibrationMode(getActivity(), true);
                        ConfigPreferences.setSilentMood(getActivity(), true);
                        ConfigPreferences.setLedNotification(getActivity(), true);
                        getPreferenceScreen().findPreference("silent").setEnabled(true);
                        getPreferenceScreen().findPreference("vibration").setEnabled(true);
                        getPreferenceScreen().findPreference("led").setEnabled(true);
                    }

                    break;
                case "day_light":
                    praying(key, sharedPreferences.getBoolean(key, false));
                    break;
                case "calculations":
                    Log.i("DATA_SETTING" , "way : "+sharedPreferences.getString(key, null));
                    praying(key, sharedPreferences.getString(key, null));
                    break;
                case "mazhab":
                    Log.i("DATA_SETTING" , "mazhab : "+sharedPreferences.getString(key, null));
                    praying(key, sharedPreferences.getString(key, null));
                    break;
                case "silent":
                    ConfigPreferences.setSilentMood(getActivity(), sharedPreferences.getBoolean(key, true));
                    if (sharedPreferences.getBoolean(key, true) == false) {
                        ConfigPreferences.setVibrationMode(getActivity(), false);
                        getPreferenceScreen().findPreference("vibration").setEnabled(false);
                    } else {
                        ConfigPreferences.setVibrationMode(getActivity(), true);
                        getPreferenceScreen().findPreference("vibration").setEnabled(true);
                    }
                    break;
                case "led":
                    ConfigPreferences.setLedNotification(getActivity(), sharedPreferences.getBoolean(key, true));
                    break;
                case "vibration":
                    ConfigPreferences.setVibrationMode(getActivity(), sharedPreferences.getBoolean(key, true));
                    break;
                case "hour":
                    ConfigPreferences.setTwentyFourMode(getActivity(), sharedPreferences.getBoolean(key, true));
                    break;
            }

        }

        /**
         * Function  to change language
         *
         * @param language
         */
        public void changeLanguage(String language) {
            if (language.equalsIgnoreCase("1")) {
                ConfigPreferences.setApplicationLanguage(getActivity(), "en");
            } else {
                ConfigPreferences.setApplicationLanguage(getActivity(), "ar");
            }

            if (listPreference != null) {
                String lang = ConfigPreferences.getApplicationLanguage(getActivity()).equalsIgnoreCase("en") ? "English" : "العربية";
                listPreference.setSummary(getString(R.string.language_summary)
                        + "  (" + lang + ") ");
            }


            getActivity().sendBroadcast(new Intent().setAction("prayer.information.change"));
            getActivity().sendBroadcast(new Intent().setAction("prayer.language.change"));
            startActivity(new Intent(getActivity(), MainActivity.class)
                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));

        }

        /**
         * Function to settings in pray
         *
         * @param key   Key of shared prefrence
         * @param value Object value
         */
        public void praying(String key, Object value) {
            switch (key) {
                case "calculations":
                    ConfigPreferences.setLocationConfig(getActivity(), new LocationInfo(locationInfo.latitude
                            , locationInfo.longitude, locationInfo.name, locationInfo.name_english
                            , locationInfo.iso, locationInfo.city, locationInfo.continentCode
                            , locationInfo.number, locationInfo.mazhab, Integer.valueOf((String) value)
                            , locationInfo.dls, locationInfo.timeZone, locationInfo.city_ar));
                    break;

                case "mazhab":
                    ConfigPreferences.setLocationConfig(getActivity(), new LocationInfo(locationInfo.latitude
                            , locationInfo.longitude, locationInfo.name, locationInfo.name_english
                            , locationInfo.iso, locationInfo.city, locationInfo.continentCode
                            , locationInfo.number, Integer.valueOf((String) value), locationInfo.way
                            , locationInfo.dls, locationInfo.timeZone, locationInfo.city_ar));
                    break;

                case "day_light":
                    ConfigPreferences.setLocationConfig(getActivity(), new LocationInfo(locationInfo.latitude
                            , locationInfo.longitude, locationInfo.name, locationInfo.name_english
                            , locationInfo.iso, locationInfo.city, locationInfo.continentCode
                            , locationInfo.number, locationInfo.mazhab, locationInfo.way
                            , (boolean) value == true ? 1 : 0, locationInfo.timeZone, locationInfo.city_ar));
                    break;


            }
//            Toast.makeText(getActivity(), getString(R.string.change_method), Toast.LENGTH_LONG).show();
            getActivity().sendBroadcast(new Intent().setAction("prayer.information.change"));
            Alarms.startCalculatePrayingBroadcast(getActivity());
            getActivity().sendBroadcast(new Intent().setAction("prayer.information.change.in.settings"));

        }

    }

}
