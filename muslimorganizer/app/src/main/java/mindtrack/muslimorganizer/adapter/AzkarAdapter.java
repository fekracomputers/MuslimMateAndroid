package mindtrack.muslimorganizer.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import mindtrack.muslimorganizer.R;
import mindtrack.muslimorganizer.model.ZekerType;
import mindtrack.muslimorganizer.ui.activity.AzkarActivity;
import mindtrack.muslimorganizer.utility.NumbersLocal;

/**
 * Adapter for azkar class
 */
public class AzkarAdapter extends RecyclerView.Adapter<AzkarAdapter.ViewHolder> {
    private List<ZekerType> zekerTypeList;
    private Context context;

    public AzkarAdapter(List<ZekerType> zekerTypeList, Context context) {
        this.zekerTypeList = zekerTypeList;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_azkar, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final ZekerType zekerType = zekerTypeList.get(position);
        Animation animation = AnimationUtils.loadAnimation(context , R.anim.bottom_top);
        holder.title.setText(zekerType.zekrTitle);
        holder.ID.setText(String.valueOf(zekerType.zekrID));
        holder.counter.setText(NumbersLocal.convertNumberType(context, String.valueOf(zekerType.zekrCounter)));
        ((RelativeLayout) holder.title.getParent()).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(context, AzkarActivity.class).putExtra("zekr_type"
                        , zekerType.zekrID).putExtra("title", zekerType.zekrTitle));
            }
        });

        if (!zekerType.isAnimated()) {
            holder.counter.setAnimation(animation);
            zekerType.setAnimated(true);
            zekerTypeList.set(position , zekerType);
        }

    }

    @Override
    public int getItemCount() {
        return zekerTypeList.size();
    }

    /**
     * Class of view holder in the adapter
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView title, ID, counter;

        public ViewHolder(View itemView) {
            super(itemView);
            Typeface tf = Typeface.createFromAsset(context.getAssets(), "fonts/simple.otf");
            title = (TextView) itemView.findViewById(R.id.textView14);
            title.setTypeface(tf);
            ID = (TextView) itemView.findViewById(R.id.hidden);
            counter = (TextView) itemView.findViewById(R.id.textView16);
        }
    }

}
