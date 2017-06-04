package mindtrack.muslimorganizer.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import mindtrack.muslimorganizer.R;
import mindtrack.muslimorganizer.model.Weather;
import mindtrack.muslimorganizer.utility.NumbersLocal;
import mindtrack.muslimorganizer.utility.WeatherIcon;

/**
 * Adapter for weather list
 */
public class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.ViewHolder> {
    private List<Weather> weatherList;
    private Context context;

    public WeatherAdapter(List<Weather> weatherList, Context context) {
        this.weatherList = weatherList;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_weather, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Weather weather = weatherList.get(position);
        String[] time = weather.dayName.split(" ");
        String[] weatherTime = time[1].split(":");
        String[] date = time[0].split("-");
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
        Date d = new Date();
        d.setYear(Integer.parseInt(date[0]));
        d.setMonth(Integer.parseInt(date[1]) - 1);
        d.setDate(Integer.parseInt(date[2]) - 1);
        String dayOfTheWeek = sdf.format(d);
        Log.d("DAY", weather.dayName + " : " + dayOfTheWeek);
        holder.dayName.setText(NumbersLocal.convertNumberType(context, weatherTime[0] + ":" + weatherTime[1] + ""));
        holder.weather.setText(NumbersLocal.convertNumberType(context, weather.tempMini + "°"));
        holder.image.setImageResource(WeatherIcon.get_icon_id_white(weather.image));
    }

    @Override
    public int getItemCount() {
        return weatherList.size();
    }

    /**
     * Class of view holder in the adapter
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView weather, dayName;
        private ImageView image;

        public ViewHolder(View itemView) {
            super(itemView);
            dayName = (TextView) itemView.findViewById(R.id.textView17);
            weather = (TextView) itemView.findViewById(R.id.textView18);
            image = (ImageView) itemView.findViewById(R.id.imageView3);
        }
    }

}
