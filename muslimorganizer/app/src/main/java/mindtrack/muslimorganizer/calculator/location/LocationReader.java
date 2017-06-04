package mindtrack.muslimorganizer.calculator.location;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by sobh on 4/8/2017.
 */

public class LocationReader {
    private static final String LOCATION_PREF_TAG = "location_pref_tag";
    private final Context context;
    private final String sURL = "http://ip-api.com/json/";
    boolean freshData;

    public Map<String, String> locationInfo = new HashMap<String, String>();

    public LocationReader(Context context) {
        this.context = context;
        this.freshData = false;

        try {
            String sLocationInfo = Utility.getStringPrefs(context ,LOCATION_PREF_TAG, "");
            JSONObject obj = new JSONObject(sLocationInfo);
            Iterator<String> iterator = obj.keys();
            while (iterator.hasNext()) {
                String key = iterator.next();
                try {
                    String value = obj.getString(key);
                    locationInfo.put(key, value);
                } catch (JSONException e) {
                }
            }

        } catch(Exception e) {
        }
    }

    public boolean isFresh() {
        return freshData;
    }

    public boolean isAvailable() {
        return (locationInfo.size()>0);
    }

    public boolean read() {

        if(!ipToAddress())
            return false;

        JSONObject obj = new JSONObject(locationInfo);
        String sLocationInfo = obj.toString();
        Utility.saveStringPrefs(context , LOCATION_PREF_TAG , sLocationInfo);
        this.freshData = (locationInfo.size()>0);
        return true;
    }

    public boolean read(double latitude, double longitude) {

        if(!locationToAddress(latitude, longitude))
            return false;

        JSONObject obj = new JSONObject(locationInfo);
        String sLocationInfo = obj.toString();
        Utility.saveStringPrefs(context , LOCATION_PREF_TAG , sLocationInfo);
        this.freshData = (locationInfo.size()>0);
        return true;
    }

    public boolean ipToAddress() {

        try {

            URL url = new URL(this.sURL);

            String fullString = "";
            BufferedReader reader;
            reader = new BufferedReader(new InputStreamReader(url.openStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                fullString += line;
            }
            reader.close();

            JSONObject jsonObject = new JSONObject(fullString);

            locationInfo.clear();

            locationInfo.put("city", jsonObject.getString("city"));
            locationInfo.put("country", jsonObject.getString("country"));
            locationInfo.put("countrycode", jsonObject.getString("countryCode").toLowerCase());
            locationInfo.put("region", jsonObject.getString("region"));
            locationInfo.put("regionname", jsonObject.getString("regionName"));
            locationInfo.put("latitude", jsonObject.getString("lat"));
            locationInfo.put("longitude", jsonObject.getString("lon"));

            return (locationInfo.size()>0);

        } catch(Exception e) {
        }

        return false;
    }

    public boolean locationToAddress(double latitude, double longitude) {

        Geocoder geocoder = new Geocoder(this.context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                locationInfo.clear();

                String city = returnedAddress.getAdminArea();
                if(city==null) city = returnedAddress.getLocality();

                locationInfo.put("country", returnedAddress.getCountryName());
                locationInfo.put("countrycode", returnedAddress.getCountryCode().toLowerCase());
                locationInfo.put("latitude", ""+returnedAddress.getLatitude());
                locationInfo.put("longitude", ""+returnedAddress.getLongitude());
                if(city!=null)locationInfo.put("city", ""+city);

                String address = "";
                for (int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++) {
                    address = address + returnedAddress.getAddressLine(i) + "\n";
                }
                locationInfo.put("address", address);

                return true;

            } else {
            }
        } catch (Exception e) {
        }

        return false;
    }

    public double getLatitude() {
        return Double.parseDouble(locationInfo.get("latitude"));
    }

    public double getLongitude() {
        return Double.parseDouble(locationInfo.get("longitude"));
    }

    public String getCountryCode() {
        return locationInfo.get("countrycode");
    }

    public String getCountry() {
        return locationInfo.get("country");
    }

    public String getCity() {
        return locationInfo.get("city");
    }

    public String getAddress() {
        return locationInfo.get("address");
    }
}
