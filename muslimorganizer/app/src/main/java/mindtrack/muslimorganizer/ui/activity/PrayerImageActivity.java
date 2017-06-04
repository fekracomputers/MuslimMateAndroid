package mindtrack.muslimorganizer.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.PowerManager;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.cast.MediaStatus;
import com.koushikdutta.ion.Ion;

import cn.carbs.android.autozoominimageview.library.AutoZoomInImageView;
import mindtrack.muslimorganizer.R;

public class PrayerImageActivity extends AppCompatActivity {


    AutoZoomInImageView img;
    TextView praysTxt;
    private Context context;
    public static String MOSQUE_TYPE = "type" , PRAY_TYPE = "pray_type";
    public static String MOSQUE_DAY = "day_mosque";
    public static String MOSQUE_NIGHT = "night_mosque";

    String mosque_type = MOSQUE_DAY , pray;


    @RequiresApi(api = Build.VERSION_CODES.KITKAT_WATCH)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new FullScreenActivity().invoke();
        setContentView(R.layout.activity_prayer_image);
        getSupportActionBar().hide();
        context = this;


        if (getIntent().hasExtra(MOSQUE_TYPE)) {
            mosque_type = getIntent().getExtras().getString(MOSQUE_TYPE);
            Log.i("ACTIVITY_SRAT" , "activity is working well"+" Type : "+mosque_type);

        }

        if(getIntent().hasExtra(PRAY_TYPE)){
            pray = getIntent().getExtras().getString(PRAY_TYPE);
        }


        img = (AutoZoomInImageView) findViewById(R.id.img_pray);
        praysTxt = (TextView) findViewById(R.id.txt_pray);


        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        boolean isScreenOn = pm.isInteractive();

        if (isScreenOn){
            playAnimation();
        }



        praysTxt.setText(getString(R.string.now_coming_prays)
                .concat(" ").concat(pray));


            if (mosque_type.equalsIgnoreCase(MOSQUE_DAY)) {
                Log.i("ACTIVITY_SRAT" , "Day ");
                        showImage(R.drawable.mosque_day);
            }else if (mosque_type.equalsIgnoreCase(MOSQUE_NIGHT)){
                Log.i("ACTIVITY_SRAT" , "night ");
                showImage(R.drawable.mosque_night);
            }


    }



    void playAnimation(){

        img.post(new Runnable() {

            @Override
            public void run() {
//                auto_zoomin_image_view.init()
//                  .startZoomInByScaleDeltaAndDuration(0.3f, 1000, 1000);

                img.init()
                        .setScaleDelta(0.2f)
                        .setDurationMillis(30500)
                        .setOnZoomListener(new AutoZoomInImageView.OnZoomListener(){
                            @Override
                            public void onStart(View view) {

                            }
                            @Override
                            public void onUpdate(View view, float progress) {
                            }
                            @Override
                            public void onEnd(View view) {
                                playAnimation();
                            }
                        })
                        .start(1000);
            }
        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        playAnimation();


    }

    class FullScreenActivity {
        public void invoke() {
            // remove title
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }
    public enum Types {
        day_mosque, night_mosque ;
    }

    public void showImage( int placeHolder ) {



        img.setImageResource(placeHolder);

//        Ion.with(context).load("")
//                .withBitmap()
//                .placeholder(placeHolder)
//                .error(placeHolder)
//                .centerInside()
//                .intoImageView(img);


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
