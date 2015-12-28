package com.example.android.celebratemydrive;

import android.app.IntentService;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.Locale;
import java.util.Random;

/**
 * Created by Shubhang on 12/27/2015.
 */
public class AlarmReceiver extends BroadcastReceiver {
    TextToSpeech tps;


    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("Tag", "in broadcastreceiver");

        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if (sharedPreferences.getBoolean("voice", true)) {
            Log.e("", "Voice is true");
            Intent myIntent = new Intent(context, TTS.class);
            context.startService(myIntent);
        }
    }
}
