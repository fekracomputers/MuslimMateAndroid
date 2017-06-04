package mindtrack.muslimorganizer.calculator.weather;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import mindtrack.muslimorganizer.model.WeatherItem;


public class JsonParser {

	private static JSONObject getObject(String tagName, JSONObject jObj)
			throws JSONException {
		JSONObject subObj = jObj.getJSONObject(tagName);
		return subObj;
	}

	private static String getString(String tagName, JSONObject jObj)
			throws JSONException {
		return jObj.getString(tagName);
	}

	private static float getFloat(String tagName, JSONObject jObj)
			throws JSONException {
		return (float) jObj.getDouble(tagName);
	}

	public int parse_curr_temp(String response) throws JSONException {

		JSONObject jObj = new JSONObject(response);

		JSONObject jTempObj = getObject("main", jObj);

		return Math.round(getFloat("temp", jTempObj)-272.15f);
	}

	public String parse_curr_icon(String response) throws JSONException {

		// We create out JSONObject from the data
		JSONObject jObj = new JSONObject(response);

		JSONArray jArr = jObj.getJSONArray("weather"); // Here we have the
														// forecast for every
														// day
		JSONObject jDayForecast = jArr.getJSONObject(0);

		return getString("icon", jDayForecast);
	}

	public ArrayList<WeatherItem> parse_forecast(String response)
			throws JSONException {

		ArrayList<WeatherItem> forecastItems = new ArrayList<WeatherItem>();

		// We create out JSONObject from the data
		JSONObject jObj = new JSONObject(response);

		JSONArray jArr = jObj.getJSONArray("list"); // Here we have the forecast
													// for every day

		// We traverse all the array and parse the data
		for (int i = 0; i < jArr.length(); i++) {
			WeatherItem forecast = new WeatherItem();

			JSONObject jDayForecast = jArr.getJSONObject(i);
			
			forecast.setId(i);
			
			forecast.setTimestamp(jDayForecast.getLong("dt")*1000);

			JSONObject jTempObj = getObject("temp", jDayForecast);
			forecast.setMin(Math.round(getFloat("min", jTempObj)-272.15f));
			forecast.setMax(Math.round(getFloat("max", jTempObj)-272.15f));

			JSONArray jWeatherArr = jDayForecast.getJSONArray("weather");
			JSONObject jWeatherObj = jWeatherArr.getJSONObject(0);
			forecast.setIcon(getString("icon", jWeatherObj));

			
			forecastItems.add(forecast);
		}

		return forecastItems;
	}
}
