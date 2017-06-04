package mindtrack.muslimorganizer.service;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by TuiyTuy on 12/14/2016.
 */

public abstract interface DetectLocationManualListener {
    public abstract void onDetectLocationManualListener(LatLng latLng);
}
