package mindtrack.muslimorganizer.service;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.support.v4.content.WakefulBroadcastReceiver;

import mindtrack.muslimorganizer.database.ConfigPreferences;
import mindtrack.muslimorganizer.utility.Alarms;
import mindtrack.muslimorganizer.utility.MindtrackLog;

/**
 * Broadcast to switch mobile to silent
 */
public class SilentMoodAlarm extends WakefulBroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        MindtrackLog.add("Silent Mood");
        AudioManager mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        mAudioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
        if (ConfigPreferences.getVibrationMode(context))
            mAudioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
        Alarms.NormalAudio(context);
    }
}
