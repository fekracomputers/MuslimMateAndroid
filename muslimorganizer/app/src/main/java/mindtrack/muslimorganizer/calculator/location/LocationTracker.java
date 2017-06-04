package mindtrack.muslimorganizer.calculator.location;

import android.app.Service;
import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.CellLocation;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.ResultTransform;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Date;


public class LocationTracker  implements LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public static final int GPSTRACKER_PERMISSIONS_REQUEST = 99;
    public static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
    public static final long MIN_TIME_BW_UPDATES = 1000 * 10;

    private static Context context;
    private static LocationListener locationListener;

    private LocationManager locationManager;
    private WifiManager wifiManager;

    private TelephonyManager telephonyManager;
    private GsmCellLocation gsmCellLocation;
    private List<ScanResult> wifiDevicesInfos;
    private Location gsmLocation;
    private Location location;
    private Date networkCellUpdateTime;
    private Date wifiDevicesUpdateTime;

    private PhoneStateListener phoneStateListener;
    private BroadcastReceiver wifiManagerBroadcastReceiver;
    private GoogleApiClient googleApiClient;

    private static LocationRequest locationRequest = LocationRequest.create()
            .setFastestInterval(MIN_TIME_BW_UPDATES)
            .setInterval(MIN_TIME_BW_UPDATES)
            .setSmallestDisplacement(MIN_DISTANCE_CHANGE_FOR_UPDATES)
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    public LocationTracker(Context context) {
        this.context = context;
    }
    private static GoogleApiClient apiClient;
    private  static void enableGPS(final Activity activity) {

        apiClient  = new GoogleApiClient.Builder(activity)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {
                        requestGps(activity  ,apiClient);

                    }

                    @Override
                    public void onConnectionSuspended(int i) {

                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                    }
                })
                .addApi(LocationServices.API)
                .build();

        apiClient.connect();
    }

    private static void requestGps(final Activity activity , GoogleApiClient apiClient) {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(apiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:


                        Log.i("TAG_LOCATION_REQUEST", "All location settings are satisfied.");
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        Log.i("TAG_LOCATION_REQUEST", "Location settings are not satisfied. Show the user a dialog to upgrade location settings ");

                        try {
                            // Show the dialog by calling startResolutionForResult(), and check the result
                            // in onActivityResult().
                            status.startResolutionForResult(activity, 100);
                        } catch (IntentSender.SendIntentException e) {
                            Log.i("TAG_LOCATION_REQUEST", "PendingIntent unable to execute request.");
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.i("TAG_LOCATION_REQUEST", "Location settings are inadequate, and cannot be fixed here. Dialog not created.");
                        break;
                }
            }
        });
    }

    public static boolean needPermission(Context context)
    {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission( context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission( context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED;
    }


    public static boolean initialize(Context context) {

        boolean needPermission =  needPermission(context);

        if (!(context instanceof Activity)) {
            return !needPermission;
        }

        if(needPermission) {
            String[] permissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.READ_PHONE_STATE};
            ActivityCompat.requestPermissions( (Activity) context, permissions, GPSTRACKER_PERMISSIONS_REQUEST);
            return false;
        }

        LocationManager locationManager = (LocationManager) context.getSystemService(Service.LOCATION_SERVICE);

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && !locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            enableGPS((Activity) context);
        }

        return true;
    }

    private void writeNetworkCellData(OutputStream out, int cid, int lac, int mmc, int mnc) throws IOException {
        DataOutputStream dataOutputStream = new DataOutputStream(out);
        dataOutputStream.writeShort(21);
        dataOutputStream.writeLong(0);
        dataOutputStream.writeUTF("en");
        dataOutputStream.writeUTF("Android");
        dataOutputStream.writeUTF("1.0");
        dataOutputStream.writeUTF("Web");
        dataOutputStream.writeByte(27);
        dataOutputStream.writeInt(0);
        dataOutputStream.writeInt(0);
        dataOutputStream.writeInt(3);
        dataOutputStream.writeUTF("");

        dataOutputStream.writeInt(cid);
        dataOutputStream.writeInt(lac);

        dataOutputStream.writeInt(0);
        dataOutputStream.writeInt(0);
        dataOutputStream.writeInt(0);
        dataOutputStream.writeInt(0);
        dataOutputStream.flush();
    }

    public boolean updateNetworkCellLocation()
    {
        try {
            String urlString = "http://www.google.com/glm/mmap";

            //---open a connection to Google Maps API---
            URL url = new URL(urlString);
            URLConnection conn = url.openConnection();
            HttpURLConnection httpConn = (HttpURLConnection) conn;
            httpConn.setRequestMethod("POST");
            httpConn.setDoOutput(true);
            httpConn.setDoInput(true);
            httpConn.connect();

            //---write some custom data to Google Maps API---
            OutputStream outputStream = httpConn.getOutputStream();
            writeNetworkCellData(outputStream, getNetworkCellCid(), getNetworkCellLac(), getNetworkOperatorMMC(), getNetworkOperatorMNC());

            //---get the response---
            InputStream inputStream = httpConn.getInputStream();
            DataInputStream dataInputStream = new DataInputStream(inputStream);

            //---interpret the response obtained---
            dataInputStream.readShort();
            dataInputStream.readByte();
            int code = dataInputStream.readInt();
            if (code == 0) {
                double lat = (double) dataInputStream.readInt() / 1000000D;
                double lng = (double) dataInputStream.readInt() / 1000000D;
                dataInputStream.readInt();
                dataInputStream.readInt();
                dataInputStream.readUTF();

                gsmLocation = new Location("gsm");
                gsmLocation.setLatitude(lat);
                gsmLocation.setLongitude(lng);

                return true;
            }

        } catch (Exception e) {
        }

        return false;
    }

    @SuppressWarnings("MissingPermission")
    @Override
    public void onConnected(@Nullable Bundle bundle) {

        if (googleApiClient != null ) {
            if (context instanceof  Activity) {
                enableGPS((Activity) context);
            }


            LocationServices.FusedLocationApi.requestLocationUpdates(
                    googleApiClient,
                    locationRequest,
                    new com.google.android.gms.location.LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {
                            if (location != null) {
                                changeLocation(location);
                            }
                        }
                    });

        }
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    @SuppressWarnings("MissingPermission")
    public boolean startTracking(LocationListener locationListener,boolean withLocationManager , boolean trackLocation, boolean trackNetworkCells, boolean trackWifiDevices) {

        if(locationManager!=null || phoneStateListener!=null || wifiManagerBroadcastReceiver!=null || googleApiClient != null)
            return true;

        this.locationListener = locationListener;

        double latitude = Double.parseDouble(Utility.getStringPrefs(this.context, "latitude", "99999"));
        double longitude = Double.parseDouble(Utility.getStringPrefs(this.context, "longitude", "99999"));
        if(latitude!=99999 && longitude!=99999)
        {
            location = new Location("saved");
            location.setLatitude(latitude);
            location.setLongitude(longitude);
        }

        if (trackLocation && !needPermission(context)){

            googleApiClient = new GoogleApiClient.Builder(context)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();

            googleApiClient.connect();

            if (withLocationManager){
            locationManager = (LocationManager) this.context.getSystemService(Service.LOCATION_SERVICE);

//            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
//            }

//            if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
//            }

//            if (locationManager.isProviderEnabled(LocationManager.PASSIVE_PROVIDER)) {
                locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
//            }
            }
        }

        if(trackNetworkCells && !needPermission(context)) {
            //Network Cell Location
            telephonyManager = (TelephonyManager) this.context.getSystemService(Service.TELEPHONY_SERVICE);
            telephonyManager.listen(phoneStateListener = new PhoneStateListener() {

                @Override
                public void onCellLocationChanged(CellLocation location) {
                    gsmCellLocation = (GsmCellLocation) telephonyManager.getCellLocation();
                    networkCellUpdateTime = Utility.getCurrentTime();
                    //updateNetworkCellLocation();
                }
            }, PhoneStateListener.LISTEN_CELL_LOCATION);
        }

        if(trackWifiDevices ) {
            wifiManager = (WifiManager) this.context.getSystemService(this.context.WIFI_SERVICE);

            //WIFI Location
            this.context.registerReceiver(wifiManagerBroadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context c, Intent intent) {
                    wifiDevicesInfos = wifiManager.getScanResults();
                    wifiDevicesUpdateTime = Utility.getCurrentTime();
                }
            }, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        }

        return (locationManager!=null || phoneStateListener!=null || wifiManagerBroadcastReceiver!=null);
    }

    @SuppressWarnings("MissingPermission")
    public boolean updateLocation()
    {
        if (googleApiClient != null && googleApiClient.isConnected()) {
            location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        }

        if (location==null && locationManager != null && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }

        if (location==null && locationManager != null && locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }

        if (location==null && locationManager != null && locationManager.isProviderEnabled(LocationManager.PASSIVE_PROVIDER)) {
            location = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
        }

        if (location != null) {
            saveLocation(location);
            return true;
        }

        Log.i("TRACKER_LOG" , "gps is ON : and location is null");

        return false;
    }

    public boolean hasLocation()
    {
        return (location!=null);
    }

    public Location getLocation(){
        if (location != null) {
            Log.i("REQUEST_LOCATION", "loc : (" + location.getLatitude() + " : " + location.getLongitude()+")");
        }else{
            Log.i("REQUEST_LOCATION", "loc : null");
        }
        return location;
    }

    public void resetFresh()
    {
        if(location!=null)location.setProvider("saved");
    }

    public boolean isFresh()
    {
        if(location!=null)return !location.getProvider().equals("saved");
        return false;
    }

    public  double getLocationAccuracy() {
        if(location==null)return 0;
        return location.getAccuracy();
    }

    public  Date getLocationUpdateTime() {
        if(location==null)return null;
        return new Date(location.getTime());
    }

    public  double getLocationLatitude() {
        if(location==null)return 0;
        return location.getLatitude();
    }

    public  double getLocationLongitude() {
        if(location==null)return 0;
        return location.getLongitude();
    }

    public  int getLocationProviderType() {
        if(location==null)return 0;

        String provider = location.getProvider().toLowerCase();
        if(provider.equals("gps"))return 1;
        if(provider.equals("network"))return 2;
        if(provider.equals("passive"))return 3;
        if(provider.equals("saved"))return 4;
        if(provider.equals("fused"))return 5;

        return 0;
    }

    public  String getLocationProvider() {
        if(location==null)return "";
        return location.getProvider();
    }

    public boolean hasNetworkCellInfo()
    {
        return (gsmCellLocation!=null);
    }

    public  int getNetworkCellLac() {
        if(gsmCellLocation==null)return 0;
        return gsmCellLocation.getLac();
    }

    public  int getNetworkCellCid() {
        if(gsmCellLocation==null)return 0;
        return gsmCellLocation.getCid();
    }

    public  String getNetworkOperatorCountryCode() {
        if(telephonyManager==null)return "";
        return telephonyManager.getNetworkCountryIso();
    }

    public  int getNetworkOperatorMMC() {
        if(telephonyManager==null)return 0;
        if (telephonyManager.getNetworkOperator().isEmpty()) return 0;
        return Integer.parseInt(telephonyManager.getNetworkOperator().substring(0, 2));
    }

    public  int getNetworkOperatorMNC() {
        if(telephonyManager==null)return 0;
        if (telephonyManager.getNetworkOperator().isEmpty()) return 0;
        return Integer.parseInt(telephonyManager.getNetworkOperator().substring(3));
    }

    public  int getNetworkOperatorID() {
        if(telephonyManager==null)return 0;
        return Integer.parseInt(telephonyManager.getNetworkOperator().isEmpty() ? "0" : telephonyManager.getNetworkOperator());
    }

    public  String getNetworkOperatorName() {
        if(telephonyManager==null)return "";
        return telephonyManager.getNetworkOperatorName();
    }

    public  double getNetworkCellLatitude() {
        if(gsmLocation==null)return 0;
        return gsmLocation.getLatitude();
    }

    public  double getNetworkCellLongitude() {
        if(gsmLocation==null)return 0;
        return gsmLocation.getLongitude();
    }

    public  int getNetworkCellAccuracy() {
        return 0;
    }

    public  Date getNetworkCellUpdateTime() {
        return networkCellUpdateTime;
    }

    public boolean hasWifiDevicesInfo()
    {
        return (wifiDevicesInfos!=null);
    }


    public  int getWifiDevicesCount() {
        if(wifiDevicesInfos==null)return 0;
        return wifiDevicesInfos.size();
    }

    public  String getWifiDeviceSSID(int i) {
        if(wifiDevicesInfos==null)return "";
        return wifiDevicesInfos.get(i).SSID;
    }

    public  String getWifiDeviceBSSID(int i) {
        if(wifiDevicesInfos==null)return "";
        return wifiDevicesInfos.get(i).BSSID;
    }

    public  double getWifiDeviceAccuracy(int i) {
        if(wifiDevicesInfos==null)return -1000;
        return wifiDevicesInfos.get(i).level;
    }

    public  Date getWifiDevicesUpdateTime() {
        return wifiDevicesUpdateTime;
    }

    private void saveLocation(Location location) {
        Utility.saveStringPrefs(this.context, "latitude", ""+location.getLatitude());
        Utility.saveStringPrefs(this.context, "longitude", ""+location.getLongitude());
    }

    private void changeLocation(Location location)
    {
        this.location = location;
        saveLocation(location);

        if (locationListener!=null){
            locationListener.onLocationChanged(location);
        }
    }



    @Override
    public void onLocationChanged(Location location)
    {
        if (location != null) {
            changeLocation(location);
        }
    }

    @Override
    public void onProviderDisabled(String provider)
    {
        if (locationListener!=null){
            locationListener.onProviderDisabled(provider);
        }
    }

    @Override
    public void onProviderEnabled(String provider)
    {
        if (locationListener!=null){
            locationListener.onProviderEnabled(provider);
        }

        updateLocation();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras)
    {
        if (locationListener!=null){
            locationListener.onStatusChanged(provider , status , extras);
        }
    }

    public boolean isStarted()
    {
        if(locationManager!=null||phoneStateListener!=null||wifiManagerBroadcastReceiver!=null)
            return true;

        return false;
    }

    public void stopTracking()
    {
        Toast.makeText(context , "Stop Tracking" , Toast.LENGTH_LONG).show();
        if (googleApiClient != null){
            googleApiClient.disconnect();
            googleApiClient = null;
        }

        if(locationManager != null)
        {
            locationManager.removeUpdates(LocationTracker.this);
            locationManager = null;
        }

        if(phoneStateListener!=null)
        {
            telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);
            phoneStateListener = null;
        }

        if(wifiManagerBroadcastReceiver!=null)
        {
            if (this.context != null) {
                this.context.unregisterReceiver(wifiManagerBroadcastReceiver);
                wifiManagerBroadcastReceiver = null;
            }
        }
    }

    public static String getDeviceID(Context context)
    {
        TelephonyManager  telephonyManager = (TelephonyManager) context.getSystemService(Service.TELEPHONY_SERVICE);
        return telephonyManager.getDeviceId();
    }
}