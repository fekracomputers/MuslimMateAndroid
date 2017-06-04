package mindtrack.muslimorganizer.service;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import mindtrack.muslimorganizer.model.Place;

/**
 * Class to access google places api
 */
public class PlacesService {

    private String API_KEY;

    public PlacesService(String apikey) {
        this.API_KEY = apikey;
    }

    public void setApiKey(String apikey) {
        this.API_KEY = apikey;
    }

    /**
     * Function to parse Json response and save in list
     * @param latitude Your location latitude
     * @param longitude Your location longitude
     * @param placeSpacification Places name you request
     * @return List of places
     */
    public ArrayList<Place> findPlaces(double latitude, double longitude,
                                       String placeSpacification , String keyWords , String lang,int radius) {

        String urlString = makeUrl(latitude, longitude, placeSpacification ,keyWords , lang , radius);
        Log.i("URL_STRING" , urlString);

        try {
            String json = getJSON(urlString);

            if (json == null || json.isEmpty()){
                return new ArrayList<>();
            }
            System.out.println(json);
            JSONObject object = new JSONObject(json);
            JSONArray array = object.getJSONArray("results");

            ArrayList<Place> arrayList = new ArrayList<Place>();
            for (int i = 0; i < array.length(); i++) {
                try {
                    Place place = Place.jsonToPontoReferencia((JSONObject) array.get(i));
                    Log.v("Places Services ", "" + place);
                    arrayList.add(place);
                } catch (Exception e) {
                }
            }
            return arrayList;
        } catch (JSONException ex) {
            Logger.getLogger(PlacesService.class.getName()).log(Level.SEVERE,
                    null, ex);
        }
        return null;
    }

    /**
     * Function to create the url
     *
     * @param latitude  Your location Latitude
     * @param longitude Your Location Longitude
     * @param place     Places name you request
     * @return String Url
     */
    private String makeUrl(double latitude, double longitude, String place,String keyWords ,String lang, int radius) {
        StringBuilder urlString = new StringBuilder(
                "https://maps.googleapis.com/maps/api/place/search/json?");

        if (place.equals("")) {
            urlString.append("&location=");
            urlString.append(Double.toString(latitude));
            urlString.append(",");
            urlString.append(Double.toString(longitude));
            urlString.append("&radius="+radius);
            urlString.append("&query="+keyWords);
            urlString.append("&language="+lang);
            urlString.append("&sensor=false&key=" + API_KEY);
        } else {
            urlString.append("&location=");
            urlString.append(Double.toString(latitude));
            urlString.append(",");
            urlString.append(Double.toString(longitude));
            urlString.append("&radius="+radius);
            urlString.append("&types="+place);
            urlString.append("&query="+ keyWords);
            urlString.append("&language="+lang);
            urlString.append("&sensor=false&key=" + API_KEY);
        }
        return urlString.toString();
    }

    /**
     * Function to request
     *
     * @param url Request url
     * @return String response
     */
    protected String getJSON(String url) {
        return getUrlContents(url);
    }

    /**
     * Function to send request to google places api
     *
     * @param theUrl Request url
     * @return Json response
     */
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
}