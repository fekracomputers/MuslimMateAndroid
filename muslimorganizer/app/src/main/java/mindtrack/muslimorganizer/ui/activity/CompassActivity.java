package mindtrack.muslimorganizer.ui.activity;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import mindtrack.muslimorganizer.R;
import mindtrack.muslimorganizer.database.ConfigPreferences;
import mindtrack.muslimorganizer.model.LocationInfo;
import mindtrack.muslimorganizer.utility.Validations;

/**
 * Activity of compass and pray direction (Quibla)
 */
public class CompassActivity extends AppCompatActivity implements SensorEventListener, OnMapReadyCallback, com.google.android.gms.location.LocationListener {
    private TextView countryName, Quibladegree;
    private RelativeLayout compass, compassMapContainer, pointerMap, compassMain, innerPosition;
    private ImageView indicator, redCircle, compassLevel, pointerIndicator, smallCircleLevel, errorImage, pointerMapQuibla, pointerIndicatorInner;
    private float currentDegree = 0f;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer, mMagnetometer;
    private float[] mLastAccelerometer = new float[3];
    private float[] mLastMagnetometer = new float[3];
    private float[] mR = new float[9];
    private float[] mOrientation = new float[3];
    private boolean mLastAccelerometerSet = false, switchView = false,
            pointerPosition = true, mLastMagnetometerSet = false, start = false, mapReady = false;
    private double previousAzimuthInDegrees = 0f;
    private long SensorSendTime;
    private float pointerFirstPositionX, pointerFirstPositionY, smallCircleRadius, newX, newY;
    private double lastRoll, lastPitch, lastTime;
    private GoogleMap googleMap;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private double fusedLatitude = 0.0;
    private double fusedLongitude = 0.0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_compass);
        getSupportActionBar().setTitle(getString(R.string.quibla_compass));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        init();
    }

    @Override
    public void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);
        mSensorManager.registerListener(this, mMagnetometer, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    public void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this, mAccelerometer);
        mSensorManager.unregisterListener(this, mMagnetometer);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
        } else if (id == R.id.view) {

            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
            if (switchView == false) {
                if (Validations.gpsEnabled(this)) {
                    if (Validations.isNetworkAvailable(this)) {
                        if (checkPlayServices()) {
                            startFusedLocation();
                            registerRequestUpdate(this);
                            compass.setVisibility(View.GONE);
                            compassMapContainer.setVisibility(View.VISIBLE);
                            innerPosition.setVisibility(View.VISIBLE);
                            redCircle.setVisibility(View.VISIBLE);
                            switchView = true;
                        }
                    }
                }

            } else {
                redCircle.setVisibility(View.INVISIBLE);
                stopFusedLocation();
                compass.setVisibility(View.VISIBLE);
                compassMapContainer.setVisibility(View.GONE);
                innerPosition.setVisibility(View.GONE);
                switchView = false;
            }

        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_compass, menu);
        return true;
    }

    /**
     * Function to init compass activity
     */
    private void init() {
        countryName = (TextView) findViewById(R.id.textView11);
        if (MainActivity.locationInfo != null)
            countryName.setText(getResources().getConfiguration()
                    .locale.getDisplayLanguage().equals("العربية")
                    ? ConfigPreferences.getLocationConfig(this).name_english
                    : ConfigPreferences.getLocationConfig(this).name);

        //init compass activity views
        Quibladegree = (TextView) findViewById(R.id.textView12);
        Quibladegree.setText(getString(R.string.qibla_direction) + " " + ConfigPreferences.getQuibla(this));
        indicator = (ImageView) findViewById(R.id.imageView2);
        compass = (RelativeLayout) findViewById(R.id.compassContainer);
        compassMapContainer = (RelativeLayout) findViewById(R.id.compassMapContainer);
        compassMain = (RelativeLayout) findViewById(R.id.compassMain);
        smallCircleLevel = (ImageView) findViewById(R.id.smallCircle);
        innerPosition = (RelativeLayout) findViewById(R.id.innerplace);
        pointerIndicatorInner = (ImageView) findViewById(R.id.poinerInner);
        redCircle = (ImageView) findViewById(R.id.red_circle);
        errorImage = (ImageView) findViewById(R.id.error);

        //init sensor services
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        compassLevel = (ImageView) findViewById(R.id.compassLevel);

        //animate compass pointer
        RotateAnimation ra = new RotateAnimation(currentDegree, MainActivity.quiblaDegree,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        ra.setDuration(400);
        ra.setFillAfter(true);
        indicator.startAnimation(ra);
        pointerIndicatorInner.startAnimation(ra);
    }

    /**
     * Function to make compass indicator stable
     *
     * @param input      Input
     * @param lastOutput Lst output
     * @param dt         Last time
     * @return New output
     */
    public double lowPass(double input, double lastOutput, double dt) {
        double elapsedTime = dt - SensorSendTime;
        Log.d("TIMESEND", elapsedTime + "");
        SensorSendTime = (long) dt;
        elapsedTime = elapsedTime / 1000;
        final double lagConstant = 1;
        double alpha = elapsedTime / (lagConstant + elapsedTime);
        return alpha * input + (1 - alpha) * lastOutput;
    }

    /**
     * Function to make compass level indicator stable
     *
     * @param input      Input
     * @param lastOutput Lst output
     * @param dt         Last time
     * @return New output
     */
    public double lowPassPointerLevel(double input, double lastOutput, double dt) {
        final double lagConstant = 0.25;
        double alpha = dt / (lagConstant + dt);
        return alpha * input + (1 - alpha) * lastOutput;
    }

    /**
     * override function return every change
     *
     * @param event Sensor changes
     */
    @Override
    public void onSensorChanged(SensorEvent event) {

        double startTime = System.currentTimeMillis();

        if (event.sensor == mAccelerometer) {
            mLastAccelerometer = event.values;
            mLastAccelerometerSet = true;
        } else if (event.sensor == mMagnetometer) {
            mLastMagnetometer = event.values;
            mLastMagnetometerSet = true;
        }
        if (mLastAccelerometerSet && mLastMagnetometerSet) {
            boolean success = SensorManager.getRotationMatrix(mR, null, mLastAccelerometer, mLastMagnetometer);
            SensorManager.getOrientation(mR, mOrientation);
            float azimuthInRadians = mOrientation[0];
            double azimuthInDegress = -(float) (Math.toDegrees(azimuthInRadians) + 360) % 360;

            if (Math.abs(azimuthInDegress - previousAzimuthInDegrees) > 300) {
                previousAzimuthInDegrees = azimuthInDegress;
            }

            azimuthInDegress = lowPass(azimuthInDegress, previousAzimuthInDegrees, startTime);

            if (mapReady) updateCamera((float) azimuthInDegress);

            RotateAnimation ra = new RotateAnimation(
                    (float) previousAzimuthInDegrees,
                    (float) azimuthInDegress,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF,
                    0.5f);

            ra.setDuration(500);
            ra.setFillAfter(true);
            compass.startAnimation(ra);
            innerPosition.startAnimation(ra);

            previousAzimuthInDegrees = azimuthInDegress;


            if (pointerPosition == true) {
                pointerFirstPositionX = compassLevel.getX();
                pointerFirstPositionY = compassLevel.getY();
                smallCircleRadius = smallCircleLevel.getX();
                pointerPosition = false;
            }

            if (success) {
                float orientation[] = new float[3];
                SensorManager.getOrientation(mR, orientation);
                double yaw = orientation[0] * 57.2957795f;
                double pitch = orientation[1] * 57.2957795f;
                double roll = orientation[2] * 57.2957795f;
                if (pitch > 90) pitch -= 180;
                if (pitch < -90) pitch += 180;
                if (roll > 90) roll -= 180;
                if (roll < -90) roll += 180;

                double time = System.currentTimeMillis();

                if (!start) {
                    lastTime = time;
                    lastRoll = roll;
                    lastPitch = pitch;
                }
                start = true;


                double dt = (time - lastTime) / 1000.0;
                roll = lowPassPointerLevel(roll, lastRoll, dt);
                pitch = lowPassPointerLevel(pitch, lastPitch, dt);
                lastTime = time;
                lastRoll = roll;
                lastPitch = pitch;

                newX = (float) (pointerFirstPositionX + pointerFirstPositionX * roll / 90.0);
                newY = (float) (pointerFirstPositionY + pointerFirstPositionY * pitch / 90.0);

                compassLevel.setX(newX);
                compassLevel.setY(newY);

                if (smallCircleRadius / 3 < Math.sqrt((roll * roll) + (pitch * pitch))) {
                    compassLevel.setImageResource(R.drawable.ic_error_pointer);
                } else {
                    compassLevel.setImageResource(R.drawable.ic_level_pointer);
                }
            }


        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    /**
     * Listener of map ready
     *
     * @param googleMap google map object
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        this.googleMap = googleMap;
        mapReady = true;
    }


    /**
     * Function to rotate map
     *
     * @param bearing number of rotation
     */
    private void updateCamera(float bearing) {

        LocationInfo location = ConfigPreferences.getLocationConfig(this);
        CameraPosition oldPos = googleMap.getCameraPosition();

        CameraPosition pos = CameraPosition.builder(oldPos)
                .target(new LatLng(getFusedLatitude(), getFusedLongitude()))
                .zoom(17)
                .bearing(360-bearing)
                .build();
        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(pos));
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
        setFusedLatitude(location.getLatitude());
        setFusedLongitude(location.getLongitude());
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
}

