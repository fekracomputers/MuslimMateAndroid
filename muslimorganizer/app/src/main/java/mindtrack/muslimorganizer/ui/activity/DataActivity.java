package mindtrack.muslimorganizer.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;

import mindtrack.muslimorganizer.R;
import mindtrack.muslimorganizer.service.CopyDatabase;
import mindtrack.muslimorganizer.utility.Alarms;

/**
 * Activity to copy database
 */
public class DataActivity extends AppCompatActivity {
    private ProgressBar copyingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);
        copyingBar = (ProgressBar) findViewById(R.id.progressBar);
        int color = 0xFF00FF00;
        copyingBar.getIndeterminateDrawable().setColorFilter(color, PorterDuff.Mode.SRC_IN);
        copyingBar.getProgressDrawable().setColorFilter(color, PorterDuff.Mode.SRC_IN);
        if(!Alarms.isMyServiceRunning(this , CopyDatabase.class))
            startService(new Intent(this , CopyDatabase.class));
    }


    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(copyingBoBroadcastReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(
                copyingBoBroadcastReceiver, new IntentFilter("coping_main_database"));
    }


    /**
     * Broadcast to check copying process
     */
    private BroadcastReceiver copyingBoBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            copyingBar.setMax(intent.getIntExtra("file_size", 0));
            copyingBar.setProgress(intent.getIntExtra("coping_size", 0));
            if (intent.getIntExtra("finish", 0) == 1) {
                copyingBar.setVisibility(View.GONE);
                Intent main = new Intent(DataActivity.this, MainActivity.class);
                startActivity(main);
                finish();
            }
        }
    };
}
