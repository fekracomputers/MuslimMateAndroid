package mindtrack.muslimorganizer.service;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by TuiyTuy on 12/14/2016.
 */

public abstract interface DetectLocationListener {
    public abstract void onDetectLocationListener(LatLng latLng);
}
