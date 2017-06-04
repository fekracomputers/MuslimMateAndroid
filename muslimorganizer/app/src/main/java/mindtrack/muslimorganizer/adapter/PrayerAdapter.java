package mindtrack.muslimorganizer.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import mindtrack.muslimorganizer.R;
import mindtrack.muslimorganizer.model.Prayer;

/**
 * Adapter for Prayer Times
 */
public class PrayerAdapter extends RecyclerView.Adapter<PrayerAdapter.MyViewHolder> {
    private List<Prayer> prayers;

    public PrayerAdapter(List<Prayer> prayers) {
        this.prayers = prayers;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_pray, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Prayer prayer = prayers.get(position);
        holder.prayerName.setText(prayer.prayerName);
        holder.time.setText(prayer.time);

    }

    @Override
    public int getItemCount() {
        return prayers.size();
    }

    /**
     * Class of view holder in the adapter
     */
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView prayerName, time;

        public MyViewHolder(View view) {
            super(view);
            prayerName = (TextView) view.findViewById(R.id.textView9);
            time = (TextView) view.findViewById(R.id.textView10);
        }
    }

}
