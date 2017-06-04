package mindtrack.muslimorganizer.ui.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;
import java.util.Locale;

import mindtrack.muslimorganizer.R;
import mindtrack.muslimorganizer.calculator.calendar.HGDate;
import mindtrack.muslimorganizer.calculator.location.LocationTracker;
import mindtrack.muslimorganizer.database.ConfigPreferences;
import mindtrack.muslimorganizer.database.Database;
import mindtrack.muslimorganizer.service.DetectLocationListener;
import mindtrack.muslimorganizer.ui.activity.PrayShowActivity;

/**
 * Created by TuiyTuy on 12/13/2016.
 */

public class MapFragment extends SupportMapFragment implements OnMapReadyCallback,
        GoogleMap.OnInfoWindowClickListener, GoogleMap.OnMapClickListener, GoogleMap.OnMarkerDragListener, GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMapLoadedCallback {


    private Context context;
    private GoogleMap mapView;
    private int INTENT_CODE = -1;
    private LocationTracker tracker;


    private GoogleMap.OnCameraChangeListener cameraChangeListener;
    private Marker myMarker;
    private AlertDialog dialog;
    private ProgressDialog progressDialog;
    AlertDialog.Builder dialogBuilder;

    DetectLocationListener listener;

    public void addListener(DetectLocationListener listener){
        this.listener = listener;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mapView = googleMap;
        setUpMap();
    }


    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        context = getActivity();
        tracker = new LocationTracker(context);
        tracker.updateLocation();
    }
    View v;
    @Override
    public View onCreateView(LayoutInflater mInflater, ViewGroup parent,
                             Bundle bundle) {

        return super.onCreateView(mInflater, parent, bundle);
    }
    @Override
    public void onInflate(Activity activity, AttributeSet attr, Bundle bundle) {
        super.onInflate(activity, attr, bundle);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }


    @Override
    public void onStart() {
        super.onStart();


        setupGoogleService();


    }

    private void setupGoogleService() {
        int statusCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity().getApplicationContext());
        if (statusCode == ConnectionResult.SUCCESS) {

            getMapAsync(this);

        } else {
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(statusCode, (Activity) context, INTENT_CODE);
            dialog.show();
        }
    }


    /* setup Map in this fragment*/
    private void setUpMap() {

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED){

        }else {
            mapView.setMyLocationEnabled(true);
        }
//        mapView.getUiSettings().setMapToolbarEnabled(true);
        mapView.getUiSettings().setIndoorLevelPickerEnabled(true);
        mapView.getUiSettings().setAllGesturesEnabled(true);
        if (cameraChangeListener != null){
            mapView.setOnCameraChangeListener(cameraChangeListener);
        }
        setMyLocation();
        mapView.setOnMapClickListener(this);
        mapView.setOnMarkerDragListener(this);
        mapView.setOnMyLocationButtonClickListener(this);
        mapView.setOnMapLoadedCallback(this);

    }


    private void setMyLocation() {
        Location location = mapView.getMyLocation();

        LatLng pos = null;
        if (location != null && location.getAccuracy() < 100) {
            pos = new LatLng(location.getLatitude() , location.getLongitude());

        }else if (tracker != null && tracker.hasLocation() && tracker.getLocation() != null){
            pos = new LatLng(tracker.getLocationLatitude() , tracker.getLocationLongitude());
        }

        if (pos != null) {
            CameraPosition localCameraPosition = CameraPosition.builder().target(pos).zoom(14.0F).bearing(0.0F).build();
            mapView.animateCamera(CameraUpdateFactory.newCameraPosition(localCameraPosition), 1000, null);
            mapView.addMarker(new MarkerOptions().position(pos).draggable(true).icon(BitmapDescriptorFactory.fromResource(R.drawable.placeholder)));
            if (listener != null){
                listener.onDetectLocationListener(pos);
        }

        }else{
            LatLng lng = new LatLng(ConfigPreferences.getLocationConfig(context).latitude
                    , ConfigPreferences.getLocationConfig(context).longitude);
        if (listener != null) {
            listener.onDetectLocationListener(lng);
        }
        }

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == INTENT_CODE) {
            setupGoogleService();
        }
    }


    @Override
    public void onMapClick(LatLng latLng) {
        mapView.clear();
        Marker mark = mapView.addMarker(new MarkerOptions().position(latLng).draggable(true).icon(BitmapDescriptorFactory.fromResource(R.drawable.placeholder)));
        onMarkerDragEnd(mark);
    }




    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        mapView.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));

        if (listener != null) {
            listener.onDetectLocationListener(marker.getPosition());
        }

    }



    private LatLng getMyLocationtLatLng(){
        if (mapView.isMyLocationEnabled() && mapView.getMyLocation() != null) {
            return new LatLng(mapView.getMyLocation().getLatitude(), mapView.getMyLocation().getLongitude());
        }
        return new LatLng(ConfigPreferences.getLocationConfig(context).latitude , ConfigPreferences.getLocationConfig(context).latitude);
    }

    @Override
    public boolean onMyLocationButtonClick() {
        mapView.clear();
        Marker mark = mapView.addMarker(new MarkerOptions().position(getMyLocationtLatLng()).draggable(true).icon(BitmapDescriptorFactory.fromResource(R.drawable.placeholder)));
        onMarkerDragEnd(mark);
        return true;
    }

    @Override
    public void onMapLoaded() {
     onMyLocationButtonClick();
    }
}
