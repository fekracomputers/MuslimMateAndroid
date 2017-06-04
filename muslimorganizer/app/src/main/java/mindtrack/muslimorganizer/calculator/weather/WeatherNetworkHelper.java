package mindtrack.muslimorganizer.calculator.weather;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WeatherNetworkHelper {

	private Context context;
	private String temp;

	private static String url = "http://api.openweathermap.org/data/2.5/";
	private static String app_id = "&APPID=ac6f2688dbfdc24772be777529947e27";

	JsonParser parser;

	Handler handler;

	public WeatherNetworkHelper(Context context) {
		this.context = context;
		parser = new JsonParser();
		handler = new Handler();
		temp = "";
	}

	public String getJsonResponse(String append) {

		try {

			HttpURLConnection con = null;
			InputStream is = null;

			try {
				con = (HttpURLConnection) (new URL(url + append + app_id))
						.openConnection();
				con.setRequestMethod("GET");
				con.setDoInput(true);
				con.setDoOutput(true);
				con.connect();

				// getting the response
				StringBuffer buffer = new StringBuffer();
				is = con.getInputStream();
				BufferedReader br = new BufferedReader(
						new InputStreamReader(is));
				String line = null;
				while ((line = br.readLine()) != null)
					buffer.append(line + "\r\n");

				is.close();
				con.disconnect();

				temp = buffer.toString();

			} catch (Throwable t) {
				t.printStackTrace();
				handler.post(new Runnable() {
					public void run() {
						Toast.makeText(context,
								"Problem in updating the weather data",
								Toast.LENGTH_SHORT).show();
					}
				});
				return null;
			} finally {
				try {
					is.close();
				} catch (Throwable t) {

				}
				try {

					con.disconnect();
				} catch (Throwable t) {
				}
			}

		} catch (Exception e) {

			e.printStackTrace();
			Toast.makeText(context, "Problem in updating the weather data",
					Toast.LENGTH_SHORT).show();

			return null;
		}

		return temp;

	}

	public boolean isNetworkAvailable() {

		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (cm != null && cm.getActiveNetworkInfo() != null
				&& cm.getActiveNetworkInfo().isConnected()) {
			Log.e("Network Testing", "***Available***");
			return true;
		}
		Log.e("Network Testing", "***Not Available***");
		return false;
	}
}
