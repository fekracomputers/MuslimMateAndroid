package mindtrack.muslimorganizer.ui.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import java.util.ArrayList;
import java.util.List;

import mindtrack.muslimorganizer.R;
import mindtrack.muslimorganizer.calculator.calendar.HGDate;
import mindtrack.muslimorganizer.database.ConfigPreferences;
import mindtrack.muslimorganizer.database.Database;
import mindtrack.muslimorganizer.model.City;
import mindtrack.muslimorganizer.model.Country;
import mindtrack.muslimorganizer.service.DetectLocationListener;
import mindtrack.muslimorganizer.service.DetectLocationManualListener;
import mindtrack.muslimorganizer.ui.activity.PrayShowActivity;

/**
 * Created by TuiyTuy on 12/14/2016.
 */

public class ManualLocationFragment extends Fragment {

    View v;
    Context context;
    SearchableSpinner countrySp , citySp;
    Button okBtn , cancelBtn;
    private String[] countries, cities;
    private List<City> cityList;
    private ProgressDialog progressDialog;
    DetectLocationManualListener listener;

    private LatLng latLng;
    public void addListener(DetectLocationManualListener listener){
        this.listener = listener;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_select_location_manual , container , false);
        context = getActivity();
        latLng = new LatLng(0 , 0);

        setupViews();

        return v;
    }

    private void setupViews() {



        cancelBtn = (Button) v.findViewById(R.id.btn_current_cancel);
        okBtn = (Button) v.findViewById(R.id.btn_current_ok);
        countrySp = (SearchableSpinner) v.findViewById(R.id.sp_country);
        citySp = (SearchableSpinner) v.findViewById(R.id.sp_city);

        countrySp.setTitle(getString(R.string.select_country));
        citySp.setTitle(getString(R.string.select_city));

        countrySp.setPositiveButton(getString(R.string.close));
        citySp.setPositiveButton(getString(R.string.close));

        addItemToSpinner();



    }


    public void addItemToSpinner(){
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


        //spinner adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
                R.layout.spinner_view, countries);
        countrySp.setAdapter(adapter);

        //on change new item from spinner
        countrySp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
                citySp.setAdapter(adapter);
                addLATLNG();
            }



            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }

        });

        citySp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                addLATLNG();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                final float lat= cityList.get(citySp.getSelectedItemPosition()).Lat;
                final float lon = cityList.get(citySp.getSelectedItemPosition()).lon;
                showDialog();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        HGDate hgDate = new HGDate();
                        hgDate.toHigri();
                        ConfigPreferences.setWorldPrayerCountry(context, new Database().getLocationInfo(lat, lon));
                        context.startActivity(new Intent(context, PrayShowActivity.class).putExtra("date", hgDate.getDay() + "-" + hgDate.getMonth() + "-" + hgDate.getYear() + "- 0"));
                        ((Activity)context).finish();
                        if (progressDialog != null){
                            progressDialog.dismiss();
                        }
                    }
                }).start();
            }
        });
    }

    private void addLATLNG() {
        double lat= cityList.get(citySp.getSelectedItemPosition()).Lat;
        double lon = cityList.get(citySp.getSelectedItemPosition()).lon;
        if (listener != null) {
            listener.onDetectLocationManualListener(new LatLng(lat, lon));
        }
    }


    public void showDialog(){
        //start progress dialog
        progressDialog = new ProgressDialog(context);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.show();
    }
}
