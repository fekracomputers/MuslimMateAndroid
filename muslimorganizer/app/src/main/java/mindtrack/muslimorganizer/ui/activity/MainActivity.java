package mindtrack.muslimorganizer.ui.activity;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import mindtrack.muslimorganizer.R;
import mindtrack.muslimorganizer.calculator.location.LocationReader;
import mindtrack.muslimorganizer.calculator.prayer.PrayerTimes;
import mindtrack.muslimorganizer.calculator.quibla.QuiblaCalculator;
import mindtrack.muslimorganizer.database.ConfigPreferences;
import mindtrack.muslimorganizer.database.Database;
import mindtrack.muslimorganizer.model.LocationInfo;
import mindtrack.muslimorganizer.model.Prayer;
import mindtrack.muslimorganizer.model.ZekerType;
import mindtrack.muslimorganizer.service.FusedLocationService;
import mindtrack.muslimorganizer.service.PrayerAlarm;
import mindtrack.muslimorganizer.service.PrayerNotification;
import mindtrack.muslimorganizer.ui.fragments.AzkarFragment;
import mindtrack.muslimorganizer.ui.fragments.CalendarFragment;
import mindtrack.muslimorganizer.ui.fragments.IslamicEventsFragment;
import mindtrack.muslimorganizer.ui.fragments.PrayingFragment;
import mindtrack.muslimorganizer.ui.fragments.WeatherFragment;
import mindtrack.muslimorganizer.ui.popup.CountryPrayerPopup;
import mindtrack.muslimorganizer.ui.popup.DataConvertPopup;
import mindtrack.muslimorganizer.utility.Alarms;
import mindtrack.muslimorganizer.utility.Validations;

/**
 * Activity for the main window
 */
public class MainActivity extends AppCompatActivity implements com.google.android.gms.location.LocationListener {
    private static final int REQUEST_GPS_LOCATION = 113;
    public static LocationInfo locationInfo;
    public static int quiblaDegree;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private ProgressDialog detectLocation;
    FusedLocationService gps;
    ProgressDialog progressDialog;
    public static List<ZekerType> zekerTypeList;
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //load application language
        String languageToLoad = ConfigPreferences.getApplicationLanguage(this);
        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());

        /*addHelper = new Adds();*/
        setContentView(R.layout.activity_main);

        locationInfo = ConfigPreferences.getLocationConfig(this);
        quiblaDegree = ConfigPreferences.getQuibla(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        //always set alarm for the prays in the application open
        if (ConfigPreferences.getPrayingNotification(this))
            Alarms.setNotificationAlarmMainPrayer(this);

        //clickable application title
        TextView applicationTitle = (TextView) findViewById(R.id.title);
        applicationTitle.setText(getString(R.string.main));
        applicationTitle.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mViewPager.setCurrentItem(0);
            }
        });


//        /*for testing prayer notification*/
//        sendBroadcast(new Intent(this, PrayerAlarm.class).putExtra("prayName" ,
//                "4"));

        //view pager to disable or enable landscape
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
//                if (position == 4 || position == 1 || position == 3)
//                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//                else
//                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });




        //check if user detect location before or not
        if (ConfigPreferences.getLocationConfig(this) == null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_GPS_LOCATION);
            } else {
                //start to detect user loaction
                getLocation();
            }

        }

        //load azkar in the main activity
        new AzkarTypes().execute();


    }




    public void showDialog(){
        //start progress dialog
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setCancelable(true);
        dialogBuilder.setMessage(getString(R.string.location_dialog_message));
        dialogBuilder.setTitle(android.R.string.dialog_alert_title);
        dialogBuilder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        dialogBuilder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                getLocation();
            }
        });

        dialog = dialogBuilder.show();



    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            //start compass activity
            startActivity(new Intent(this, CompassActivity.class));
            return true;
        } else if (id == R.id.action_location) {
            //check permission for marshmallow
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_GPS_LOCATION);
            } else {
                //start getting location
                showDialog();
                return true;
            }

        } else if (id == R.id.action_convert_date) {
            //start date convert pop-up
            new DataConvertPopup(this);
        } else if (id == R.id.settings) {
            //settings activity
            startActivityForResult(new Intent(this, SettingsActivity.class) , 16);
        } else if (id == R.id.mosques) {
            //check gps enable or not
            if (Validations.gpsEnabled(this)) {
                if (Validations.isNetworkAvailable(this)) {
                    startActivity(new Intent(this, MosquesActivity.class));
                }
            }

        } else if (id == R.id.worldpraye) {
            //start country prayer pop-up
            new CountryPrayerPopup(this , true , false);
        } else if (id == R.id.action_rate_app) {
            //market url of the application
            String url = "https://play.google.com/store/apps/details?id=com.fekracomputers.muslimmate";
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        } else if (id == R.id.action_about_app) {
            //start about activity
            startActivity(new Intent(this, AboutActivity.class));
        } else if (id == R.id.action_share) {
            //share intent for the application
            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, "#" + getString(R.string.app_name) + "\n https://play.google.com/store/apps/details?id=com.fekracomputers.muslimmate");
            startActivity(Intent.createChooser(sharingIntent, "Share using"));
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            //check if user allow permission to app or not <= 21
            case REQUEST_GPS_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLocation();
                } else {
                    finish();
                    Toast.makeText(this, "The application can't start without this permission", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Validations.REQUEST_CODE && resultCode == 0) {
            getLocation();
        }else if (requestCode == 16){
//            sendBroadcast(new Intent().setAction("prayer.information.change"));
        }

    }

    /**
     * Function to get and save location in shared preference
     */
    public void getLocation() {
        if (Validations.gpsEnabledInLocation(this , true , true)) {
            //start progress dialog
            progressDialog = new ProgressDialog(this);
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.setMessage(getString(R.string.detecting_location));
            progressDialog.show();
            gps = new FusedLocationService(this, this);
        }
    }

    Location currLocation = null;
    @Override
    public void onLocationChanged(Location location) {
        //get location from fused location api
        if (location != null && currLocation == null) {
            currLocation = location;
            gps.setFusedLatitude(location.getLatitude());
            gps.setFusedLongitude(location.getLongitude());
            if (gps.getFusedLatitude() != 0 && gps.getFusedLongitude() != 0) {
                LocationInfo locationInfo = new Database().getLocationInfo((float) gps.getFusedLatitude(), (float) gps.getFusedLongitude());
                Calendar calendar = Calendar.getInstance();
                LocationReader lr = new LocationReader(this);
                lr.read(gps.getFusedLatitude(),gps.getFusedLongitude());
                int dst = calendar.getTimeZone().getDSTSavings();
                locationInfo.dls = dst;
                switch (PrayerTimes.getDefaultMazhab(lr.getCountryCode())){
                    case PTC_MAZHAB_HANAFI:
                        locationInfo.mazhab = 1;
                        break;
                    case  PTC_MAZHAB_SHAFEI:
                        locationInfo.mazhab = 0;
                        break;
                }
                switch (PrayerTimes.getDefaultWay(lr.getCountryCode())){
                    case PTC_WAY_EGYPT:
                        locationInfo.way = 0;
                        break;
                    case  PTC_WAY_UMQURA:
                        locationInfo.way = 3;
                        break;

                    case  PTC_WAY_MWL:
                        locationInfo.way = 4;
                        break;

                    case  PTC_WAY_KARACHI:
                        locationInfo.way = 1;
                        break;

                    case  PTC_WAY_ISNA:
                        locationInfo.way = 2;
                        break;
                }
                ConfigPreferences.setLocationConfig(this, locationInfo);
                ConfigPreferences.setQuibla(this, (int) QuiblaCalculator.doCalculate((float) location.getLatitude(), (float) location.getLongitude()));
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("mazhab", locationInfo.mazhab + ""); // value to store
                editor.putString("calculations", locationInfo.way + "");
                editor.commit();
                Toast.makeText(getApplicationContext(), "Your Location is : " + locationInfo.city, Toast.LENGTH_LONG).show();
                progressDialog.cancel();
                gps.stopFusedLocation();
                Intent intent = getIntent();
                sendBroadcast(new Intent().setAction("prayer.information.change"));
                finish();
                startActivity(intent);
                ConfigPreferences.setPrayingNotification(this, true);
                Alarms.startCalculatePrayingBroadcast(this);

            }
        }else{
            new CountryPrayerPopup(this , true , true);
        }
    }

    /**
     * Adapter for application tabs and switch
     */
    public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 0:
                    return new PrayingFragment();
                case 1:
                    return new CalendarFragment();
                case 2:
                    return new AzkarFragment();
                case 3:
                    return new IslamicEventsFragment();
                default:
                    return new WeatherFragment();

            }

        }

        @Override
        public int getCount() {
            return 5;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.praying_tab);
                case 1:
                    return getString(R.string.calender_tab);
                case 2:
                    return getString(R.string.azkar_tab);
                case 3:
                    return getString(R.string.islamic_tab);
                case 4:
                    return getString(R.string.weather_tab);

            }
            return null;
        }
    }


    /**
     * Async task to show azkar and count of every Zeker
     */
    private class AzkarTypes extends AsyncTask<Void, Void, List<ZekerType>> {

        @Override
        protected List<ZekerType> doInBackground(Void... voids) {
            zekerTypeList = new ArrayList<>();
            return new Database().getAllAzkarTypes();
        }

        @Override
        protected void onPostExecute(List<ZekerType> zekerTypes) {
            super.onPostExecute(zekerTypes);
            zekerTypeList.addAll(zekerTypes);
        }
    }


}
