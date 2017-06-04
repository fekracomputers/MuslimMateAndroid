package mindtrack.muslimorganizer.database;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import mindtrack.muslimorganizer.service.CopyDatabase;

/**
 * Class to copying data in background
 */
public class DatabaseCopy extends AsyncTask<String, Void, Boolean> {
    private Context context;

    public DatabaseCopy(Context context) {
        this.context = context;
    }

    @Override
    protected Boolean doInBackground(String... params) {
        InputStream in = null;
        int fileSize = 0, copyedSize = 0;
        try {
            in = context.getAssets().open("muslim_organizer.sqlite.png");
            OutputStream out = new FileOutputStream(params[0]);
            fileSize = in.available();
            byte[] buf = new byte[1024 * 3];
            int len;
            while ((len = in.read(buf)) > 0) {
                copyedSize += len;
                out.write(buf, 0, len);
                Intent copying = new Intent("coping_main_database");
                copying.putExtra("file_size", fileSize);
                copying.putExtra("coping_size", copyedSize);
                LocalBroadcastManager.getInstance(context).sendBroadcast(copying);
            }
            in.close();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileSize == copyedSize;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        if (aBoolean == true) {
            Intent copying = new Intent("coping_main_database");
            copying.putExtra("finish", 1);
            LocalBroadcastManager.getInstance(context).sendBroadcast(copying);
            context.stopService(new Intent(context , CopyDatabase.class));
        }
    }
}
