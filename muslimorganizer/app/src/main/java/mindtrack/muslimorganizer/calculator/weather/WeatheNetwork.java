package mindtrack.muslimorganizer.calculator.weather;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import mindtrack.muslimorganizer.adapter.WeatherAdapter;
import mindtrack.muslimorganizer.database.ConfigPreferences;
import mindtrack.muslimorganizer.model.LocationInfo;
import mindtrack.muslimorganizer.model.Weather;
import mindtrack.muslimorganizer.utility.WeatherIcon;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Class to get weather
 */
public class WeatheNetwork extends AsyncTask<Void, Void, List<Weather>> {
    private final String url = "http://api.openweathermap.org/data/2.5/forecast?";
    private final String ApiID = "&appid=ac6f2688dbfdc24772be777529947e27";
    private Context context;
    private WeatherAdapter adapter;
    private TextView today , min , max  , desc;
    private ImageView todayImage;
    private List<Weather> weatherList, weathers;

    public WeatheNetwork(Context context, List<Weather> weatherList, WeatherAdapter adapter, TextView today, ImageView todayImage , TextView mini , TextView max , TextView desc) {
        this.context = context;
        this.adapter = adapter;
        this.weatherList = weatherList;
        this.today = today;
        this.todayImage = todayImage;
        this.min = mini ;
        this.max = max ;
        this.desc = desc ;
    }

    @Override
    protected List<Weather> doInBackground(Void... voids) {
        try {
            weathers = new ArrayList<>();
            LocationInfo locationInfo = ConfigPreferences.getLocationConfig(context);
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(url + "lat=" + locationInfo.latitude + "&lon=" + locationInfo.longitude + ApiID).build();

            Response response = client.newCall(request).execute();
            String jsonData = response.body().string();
            if (jsonData != null) {
                JSONObject Jobject = new JSONObject(jsonData);
                JSONArray Jarray = Jobject.getJSONArray("list");
                for (int i = 0; i < Jarray.length(); i++) {
                    JSONObject object = Jarray.getJSONObject(i);
                    JSONObject main = object.getJSONObject("main");
                    JSONArray weather = object.getJSONArray("weather");
                    String desc = weather.getJSONObject(0).getString("description");
                    String icon = weather.getJSONObject(0).getString("icon");
                    String date = object.getString("dt_txt");
                    String temp = main.getString("temp");
                    String temp_min = main.getString("temp_min");
                    String temp_max = main.getString("temp_max");
                    String humidty = main.getString("humidity");
                    JSONObject wind = object.getJSONObject("wind");
                    String windSpeed = wind.getString("speed");
                    weathers.add(new Weather(date,Math.round(Float.valueOf(temp) - 272.15f)+"" , Math.round(Float.valueOf(temp_min) - 272.15f)+"" , Math.round(Float.valueOf(temp_max) - 272.15f)+"", icon , desc , humidty , windSpeed));
                }


            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        ConfigPreferences.setWeather(context , weathers);
        return weathers;
    }

    @Override
    protected void onPostExecute(List<Weather> s) {
        super.onPostExecute(s);
        if (s != null && s.size() != 0) {
            Weather weather = s.get(0);
            min.setText(weather.tempMini);
            max.setText(weather.tempMax);
            desc.setText(weather.desc);
            today.setText(weather.temp +"°");
            todayImage.setImageResource(WeatherIcon.get_icon_id(weather.image));
            weatherList.addAll(s);
            adapter.notifyDataSetChanged();

        }
    }
}
