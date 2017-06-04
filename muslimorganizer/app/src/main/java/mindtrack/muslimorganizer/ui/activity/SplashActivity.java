package mindtrack.muslimorganizer.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.io.File;
import java.util.Locale;

import mindtrack.muslimorganizer.R;
import mindtrack.muslimorganizer.database.ConfigPreferences;

/**
 * Splash screen activity and check database
 */
public class SplashActivity extends AppCompatActivity {
    private final String APP_PATH = "/mindtrack-muslimOrganizer", DATABASE_NAME = "muslim_organizer.sqlite.png";
    private static final int REQUEST_WRITE_STORAGE = 112;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //open application in first open with system language
        if(ConfigPreferences.IsApplicationFirstOpen(this) == true){
            String systemLanguage = Resources.getSystem().getConfiguration().locale.getLanguage();
            ConfigPreferences.setApplicationFirstOpenDone(this);
            ConfigPreferences.setApplicationLanguage(this , systemLanguage == "ar" ? systemLanguage : "en");
            //set default language for settings
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("language", systemLanguage.equals("ar") ? "0" : "1"); // value to store
            editor.commit();
        }

        //open application with saved language
        String languageToLoad = ConfigPreferences.getApplicationLanguage(this);
        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());

        setContentView(R.layout.activity_splash);

        //check permission of read write
        boolean hasPermission = (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
        if (!hasPermission) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_WRITE_STORAGE);
        } else {
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    checkAndCopy.start();
                }
            }, 1500);

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_WRITE_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkAndCopy.start();
                } else {
                    Toast.makeText(this, "The application can't start without this permission", Toast.LENGTH_LONG).show();
                    SplashActivity.this.finish();
                }
            }
        }
    }


    //thread to check and validate application resources then copy application data
    Thread checkAndCopy = new Thread(new Runnable() {
        @Override
        public void run() {
            File mainFile = new File("/data/data/com.fekracomputers.muslimmate/muslim_organizer.sqlite.png");
            if (mainFile.exists()) {
                Intent main = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(main);
                finish();
                return;
            }
            Intent data = new Intent(SplashActivity.this, DataActivity.class);
            startActivity(data);
            finish();
        }
    });


}
