package mindtrack.muslimorganizer.ui.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.memetix.mst.language.Language;
import com.memetix.mst.translate.Translate;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import mindtrack.muslimorganizer.R;
import mindtrack.muslimorganizer.database.ConfigPreferences;
import mindtrack.muslimorganizer.database.Database;
import mindtrack.muslimorganizer.model.LocationInfo;
import mindtrack.muslimorganizer.model.Place;
import mindtrack.muslimorganizer.service.PlacesService;

public class MosquesActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener, GoogleMap.OnMapClickListener, GoogleMap.OnMyLocationButtonClickListener {
    private GoogleMap googleMap;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private double fusedLatitude = 0.0;
    private double fusedLongitude = 0.0;
    private String searchPlace = "mosque";
    private boolean halalFlag = false;
    private boolean isHala = false;
    private int radius = 2;
    private int zoom = 15;
    ProgressDialog progressDialog;


    private String keyWords = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mosques);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.action_mosque);
        init();
        startFusedLocation();
        registerRequestUpdate(this);


    }

    private void init() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        new MenuInflater(this).inflate(R.menu.menu_around, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
        } else if (id == R.id.halal) {
            if (halalFlag == false) {
                halalFlag = true;
                isHala = true;
                item.setIcon(getResources().getDrawable(R.drawable.ic_mosque_n));
                googleMap.clear();
                searchPlace = "restaurant";
                Marker marker = googleMap.addMarker(new MarkerOptions()
                        .position(getPosition())
                        .title("My Location").icon(BitmapDescriptorFactory.fromResource(R.drawable.placeholder)));
                new Places().execute(getPosition());
            } else {
                halalFlag = false;
                isHala = false;
                item.setIcon(getResources().getDrawable(R.drawable.ic_halal));
                googleMap.clear();
                searchPlace = "mosque";
                Marker marker = googleMap.addMarker(new MarkerOptions()
                        .position(new LatLng(getFusedLatitude(), getFusedLongitude()))
                        .title("My Location").icon(BitmapDescriptorFactory.fromResource(R.drawable.placeholder)));
                new Places().execute(getPosition());
            }

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        this.googleMap.setOnMapClickListener(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        this.googleMap.setMyLocationEnabled(true);
        this.googleMap.setOnMyLocationButtonClickListener(this);

    }


    /**
     * Function to check google play services
     *
     * @return Found or not
     */
    private boolean checkPlayServices() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(this);
        if (result != ConnectionResult.SUCCESS) {
            if (googleAPI.isUserResolvableError(result)) {
                googleAPI.getErrorDialog(this, result,
                        1).show();
            }

            return false;
        }

        return true;
    }

    /**
     * Start to attach google location services
     */
    public void startFusedLocation() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this).addApi(LocationServices.API)
                    .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                        @Override
                        public void onConnectionSuspended(int cause) {
                        }

                        @Override
                        public void onConnected(Bundle connectionHint) {
                        }
                    }).addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {

                        @Override
                        public void onConnectionFailed(ConnectionResult result) {

                        }
                    }).build();
            mGoogleApiClient.connect();
        } else {
            mGoogleApiClient.connect();
        }
    }

    /**
     * Stop google play services
     */
    public void stopFusedLocation() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
    }

    /**
     * Start to listen and get new loactions
     *
     * @param listener
     */
    public void registerRequestUpdate(final com.google.android.gms.location.LocationListener listener) {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(1000); // every second

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, listener);
                } catch (SecurityException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                    if (!isGoogleApiClientConnected()) {
                        mGoogleApiClient.connect();
                    }
                    registerRequestUpdate(listener);
                }
            }
        }, 1000);
    }

    /**
     * Check google client is connected
     *
     * @return
     */
    public boolean isGoogleApiClientConnected() {
        return mGoogleApiClient != null && mGoogleApiClient.isConnected();
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location.getLatitude() != 0 || location.getLongitude() != 0) {
            setFusedLatitude(location.getLatitude());
            setFusedLongitude(location.getLongitude());
            stopFusedLocation();
            CameraPosition oldPos = googleMap.getCameraPosition();
            CameraPosition pos = CameraPosition.builder(oldPos)
                    .target(getPosition())
                    .zoom(zoom)
                    .build();
            googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(pos));

            Marker marker = googleMap.addMarker(new MarkerOptions()
                    .position(getPosition())
                    .title("My Location").icon(BitmapDescriptorFactory.fromResource(R.drawable.placeholder)));


            AsyncTask placesAsync = new Places().execute(getPosition());

        }

    }

    private LatLng getPosition() {
//        LatLng position = new LatLng(52.011138, 4.357177);

        LatLng position = new LatLng(getFusedLatitude(), getFusedLongitude());

        return position;
    }

    public void setFusedLatitude(double lat) {
        fusedLatitude = lat;
    }

    public void setFusedLongitude(double lon) {
        fusedLongitude = lon;
    }

    public double getFusedLatitude() {
        return fusedLatitude;
    }

    public double getFusedLongitude() {
        return fusedLongitude;
    }

    @Override
    public void onMapClick(LatLng latLng) {
        googleMap.clear();
        Marker mark = googleMap.addMarker(new MarkerOptions().position(latLng).draggable(true).icon(BitmapDescriptorFactory.fromResource(R.drawable.placeholder)));
        Location location = googleMap.getMyLocation();
        location.setLatitude(latLng.latitude);
        location.setLongitude(latLng.longitude);
        onLocationChanged(location);
    }

    public void showDialog(){
        //start progress dialog
        if (progressDialog != null && progressDialog.isShowing()){
            return;
        }else if (progressDialog != null){
            progressDialog.show();
            return;
        }
        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.show();

    }

    @Override
    public boolean onMyLocationButtonClick() {
        googleMap.clear();
        onLocationChanged(googleMap.getMyLocation());
        return true;
    }

    class Places extends AsyncTask<LatLng, Void, List<Place>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showDialog();
        }

        @Override
        protected List<Place> doInBackground(LatLng... latLngs) {
            LocationInfo locationInfo = ConfigPreferences.getLocationConfig(MosquesActivity.this);
            PlacesService service = new PlacesService("AIzaSyCzJfJZDE4KUR6BUvi3oyAbx2ECooQFQCI");




            LatLng latLng = latLngs[0];

            String lang = getLanguageString(latLng.latitude, latLng.longitude);
            Log.i("KeyLang1" , lang);


            keyWords = "";

            try {
                keyWords = translate(searchPlace , lang);
                Log.i("KeyOnTryTrans" , keyWords);
            } catch (Exception e) {
                e.printStackTrace();
                Log.i("KeyNotTrans" , keyWords);
            }

            if (isHala){
                keyWords = keyWords.concat(",restaurant");
                keyWords = "halal,حلال";
            }else{
                keyWords = keyWords.concat(",mosque");
            }
            Log.i("URL_STRING" , keyWords);


            ArrayList<Place> findPlaces = service.findPlaces(latLng.latitude, latLng.longitude, searchPlace, keyWords ,lang, radius*1000);

            return findPlaces;
        }

        @SuppressLint("LongLogTag")
        private String getLanguageString(double LATITUDE, double LONGITUDE) {
            String lang = "Null";
            Geocoder geocoder = new Geocoder(MosquesActivity.this, Locale.getDefault());
            try {
                List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
                if (addresses != null) {
                    Address returnedAddress = addresses.get(0);


                    lang = returnedAddress.getSubLocality();

                    Locale[] locales = Locale.getAvailableLocales();
                    for (Locale localeIn : locales) {
                        if (returnedAddress.getCountryCode().equalsIgnoreCase(localeIn.getCountry())) {
                            lang = localeIn.getLanguage();
                            break;
                        }
                    }
                    Log.i("My Current language", "" + lang);
                } else {
                    Log.w("My Current loction address", "No Address returned!");
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.w("My Current loction address", "Canont get Address!");
            }
            Log.i("KeyLang" , lang);
            return lang;
        }



        public String translate(String text , String language) throws Exception{

            String baseUrl = "https://translate.yandex.net/api/v1.5/tr.json/translate?";
            String key = "trnsl.1.1.20170510T140213Z.6a44cccf7f780263.80721577190dab03a39f2197335c5c5f8cc0fbc9";

            String content = getUrlContents(baseUrl.concat("key=").concat(key)
                    .concat("&lang=").concat(language)
                    .concat("&text=").concat(text));


            JSONObject object = new JSONObject(content);
            Log.i("URL_STRING" , "return object : "+object.toString());

            StringBuilder builder = new StringBuilder();
            if (object.has("text")){
                JSONArray array = object.getJSONArray("text");
                for (int k = 0 ; k < array.length() ; k ++){
                    if (!builder.toString().isEmpty()){
                        builder.append(",");
                    }
                    builder.append(array.optString(k));
                }
            }

            Log.i("URL_STRING" , "final string : "+builder.toString());
//            // Set the Client ID / Client Secret once per JVM. It is set statically and applies to all services
//            Translate.setClientId("tuiytuyhussein");
//            Translate.setClientSecret("ecXuhVXIKxq2+bZJRfhSv1oD50cIx+udAX563nMQTkw=");
//
//            String translatedText = "";
//
//            // English AUTO_DETECT -> gERMAN Change this if u wanna other languages
//            translatedText = Translate.execute(text, Language.fromString(language));
            return builder.toString();
        }

        private String getUrlContents(String theUrl) {
            StringBuilder content = new StringBuilder();
            try {
                URL url = new URL(theUrl);
                URLConnection urlConnection = url.openConnection();
                BufferedReader bufferedReader = new BufferedReader(
                        new InputStreamReader(urlConnection.getInputStream()), 8);
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    content.append(line + "\n");
                }
                bufferedReader.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return content.toString();
        }

        @Override
        protected void onPostExecute(List<Place> places) {
            super.onPostExecute(places);

            if (places.size() != 0 ) {

                if (progressDialog != null) {
                    progressDialog.dismiss();
                }

                for (int i = 0; i < places.size(); i++) {

                    URL url_value = null;
                    Place place = places.get(i);
                    if (!isHala) {
                        Marker marker = googleMap.addMarker(new MarkerOptions()
                                .position(new LatLng(place.getLatitude(), place.getLongitude()))
                                .title(place.getName()).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_mosque_g)));
                    }else {
//                        LocationInfo locationInfo = ConfigPreferences.getLocationConfig(MosquesActivity.this);
//                        if (place.getName().toLowerCase().equals("hala") || place.getName().toLowerCase().equals("حلال") || new Database().isIslamicCountry(locationInfo.continentCode)) {
                            Marker marker = googleMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(place.getLatitude(), place.getLongitude()))
                                    .title(place.getName()).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_hala_g)));
//                        }
                    }

                    radius = 2;

                }

            }else if (radius < 257){
                radius *= radius;
                if (zoom > 9) {
                    zoom -= 1;
                    CameraPosition oldPos = googleMap.getCameraPosition();
                    CameraPosition pos = CameraPosition.builder(oldPos)
                            .target(getPosition())
                            .zoom(zoom)
                            .build();
                    googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(pos));
                }
                new Places().execute(getPosition());

            }else{
                radius = 2;
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
            }
        }
    }


}
