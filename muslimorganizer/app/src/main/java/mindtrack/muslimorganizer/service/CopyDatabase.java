package mindtrack.muslimorganizer.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import mindtrack.muslimorganizer.database.DatabaseCopy;

/**
 * Service to copy Database
 */
public class CopyDatabase extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new DatabaseCopy(this).execute("/data/data/" + getPackageName() + "/"+"muslim_organizer.sqlite.png");
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
