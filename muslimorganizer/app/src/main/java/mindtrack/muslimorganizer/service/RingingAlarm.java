package mindtrack.muslimorganizer.service;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.support.v4.content.WakefulBroadcastReceiver;

import mindtrack.muslimorganizer.utility.MindtrackLog;

/**
 * Broadcast receiver to enable mobile normal mood
 */
public class RingingAlarm extends WakefulBroadcastReceiver {
    private AudioManager mAudioManager;
    @Override
    public void onReceive(Context context, Intent intent) {
        MindtrackLog.add("Normal Mood");
        mAudioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        mAudioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
    }
}
