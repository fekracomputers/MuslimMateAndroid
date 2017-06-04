package mindtrack.muslimorganizer.ui.popup;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import mindtrack.muslimorganizer.R;
import mindtrack.muslimorganizer.calculator.calendar.HGDate;
import mindtrack.muslimorganizer.calculator.quibla.QuiblaCalculator;
import mindtrack.muslimorganizer.database.ConfigPreferences;
import mindtrack.muslimorganizer.database.Database;
import mindtrack.muslimorganizer.model.City;
import mindtrack.muslimorganizer.model.Country;
import mindtrack.muslimorganizer.model.LocationInfo;
import mindtrack.muslimorganizer.ui.activity.MainActivity;
import mindtrack.muslimorganizer.ui.activity.PrayShowActivity;
import mindtrack.muslimorganizer.ui.activity.SelectLocationTabsActivity;
import mindtrack.muslimorganizer.ui.activity.SelectPositionActivity;

/**
 * Class for pop-up to see prayer countries
 */
public class CountryPrayerPopup {
    private Context context;
    private String[] countries, cities;
    private Spinner country, city;
    private Button showPrayer;
    public static String locationInformation;
    public static float lat, log;
    private List<City> cityList;
    private TextView title;
    private boolean manualLocationMood = false;
//    private boolean fromLocationBtn = false;

//    public CountryPrayerPopup(Context context) {
//        this.context = context;
////        init();
//
//
//        context.startActivity(new Intent(context , SelectLocationTabsActivity.class));
//    }

    public CountryPrayerPopup(Context context, boolean manualLocationMood , boolean fromLocationBtn) {
        this.context = context;
        this.manualLocationMood = manualLocationMood;
//        this.fromLocationBtn = fromLocationBtn;
//        init();
        Log.i("IsFromLocationBtn" , "pop   "+fromLocationBtn);
        Intent intent = new Intent(context , SelectLocationTabsActivity.class);
        intent.putExtra("IsFromLocationBtn" , fromLocationBtn);
        context.startActivity(intent);
    }

    /**
     * Function to init countries pop-up views
     */
    private void init() {
        //init popup views
        final Dialog otherCountriesPrayerPopup = new Dialog(context);
        otherCountriesPrayerPopup.requestWindowFeature(Window.FEATURE_NO_TITLE);
        otherCountriesPrayerPopup.setContentView(R.layout.popup_other_country);
        country = (Spinner) otherCountriesPrayerPopup.findViewById(R.id.spinner1);
        city = (Spinner) otherCountriesPrayerPopup.findViewById(R.id.spinner2);
        showPrayer = (Button) otherCountriesPrayerPopup.findViewById(R.id.button);
        title = (TextView) otherCountriesPrayerPopup.findViewById(R.id.textView22);

        //extract countries names and short cuts
        final List<Country> countriesList = new Database().getAllCountries();
        List<String> countryNamesArray = new ArrayList<>();
        List<String> countryArabicNamesArray = new ArrayList<>();
        final List<String> countriesID = new ArrayList<>();
        for (Country countryItem : countriesList) {
            countryNamesArray.add(countryItem.countryName);
            countryArabicNamesArray.add(countryItem.countryArabicName);
            countriesID.add(countryItem.countryShortCut);
        }

        //show arabic and english names of languages
        if (ConfigPreferences.getApplicationLanguage(context).equals("ar")) {
            countries = countryArabicNamesArray.toArray(new String[countryArabicNamesArray.size()]);
        } else {
            countries = countryNamesArray.toArray(new String[countryNamesArray.size()]);
        }


        //change popup mood
        if (manualLocationMood == true) {
            showPrayer.setText(context.getString(R.string.manual_selection));
            title.setText(context.getString(R.string.manual_selection_title));
            //otherCountriesPrayerPopup.setCancelable(false);
        }

        //spinner adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
                R.layout.spinner_view, countries);
        country.setAdapter(adapter);

        //on change new item from spinner
        country.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String code = countriesID.get(position);
                cityList = new Database().getAllCities(code);
                List<String> cityNames = new ArrayList<String>();
                List<String> cityArabicNames = new ArrayList<String>();
                for (City city : cityList) {
                    cityNames.add(city.Name);
                    cityArabicNames.add(city.arabicName == null ? city.Name : city.arabicName);
                }
                if (ConfigPreferences.getApplicationLanguage(context).equals("ar")) {
                    cities = cityArabicNames.toArray(new String[cityArabicNames.size()]);
                } else {
                    cities = cityNames.toArray(new String[cityNames.size()]);
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
                        R.layout.spinner_view, cities);
                city.setAdapter(adapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }

        });

        //on click to show prayer of city
        showPrayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //enable progress bar
                showPrayer.setVisibility(View.GONE);
                otherCountriesPrayerPopup.findViewById(R.id.progressBar2).setVisibility(View.VISIBLE);

                //select location manually
                locationInformation = city.getSelectedItem().toString();
                for (City cityItem : cityList) {
                    if (cityItem.Name.equals(city.getSelectedItem().toString())
                            || cityItem.arabicName.equals(city.getSelectedItem().toString())) {
                        lat = cityItem.Lat;
                        log = cityItem.lon;
                        break;
                    }
                }

                //check popup flavor
                if (manualLocationMood == true) {

                    LocationInfo locationInfo = new Database().getLocationInfo((float) lat, (float) log);
                    ConfigPreferences.setLocationConfig(context, locationInfo);
                    ConfigPreferences.setQuibla(context, (int) QuiblaCalculator.doCalculate((float) lat, (float) log));
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("mazhab", locationInfo.mazhab + ""); // value to store
                    editor.putString("calculations", locationInfo.way + "");
                    editor.commit();
                    Toast.makeText(context, "Your Location is : " + locationInfo.city, Toast.LENGTH_LONG).show();
                    otherCountriesPrayerPopup.dismiss();
                    ((MainActivity) context).finish();
                    context.startActivity(new Intent(context, MainActivity.class));
                } else {
                    //thread to calculate country prayers
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            HGDate hgDate = new HGDate();
                            hgDate.toHigri();
                            ConfigPreferences.setWorldPrayerCountry(context, new Database().getLocationInfo(lat, log));
                            context.startActivity(new Intent(context, PrayShowActivity.class).putExtra("date", hgDate.getDay() + "-" + hgDate.getMonth() + "-" + hgDate.getYear() + "- 0"));
                            otherCountriesPrayerPopup.dismiss();
                        }
                    }).start();
                }


            }
        });

        //on cancel return to gps enable popup
        otherCountriesPrayerPopup.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                if (manualLocationMood == true && ConfigPreferences.getLocationConfig(context) == null) {
                    ((Activity) context).finish();
                    Toast.makeText(context,
                            "Application can't run without select or detect your location",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        otherCountriesPrayerPopup.show();

    }

}
