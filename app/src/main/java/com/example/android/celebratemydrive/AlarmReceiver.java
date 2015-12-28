package com.example.android.celebratemydrive;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.Random;

/**
 * Created by Shubhang on 12/27/2015.
 */
public class AlarmReceiver extends BroadcastReceiver {
    TextToSpeech tps;
    private static final String[] VOICE_COMMANDS = new String[] { "Keep two eyes on the road",
            "Keep two hands on the wheel", "Obey the speed limit", "Make sure you're wearing your seat belt" };
    Random random = new Random();

    @Override
    public void onReceive(Context context, Intent intent) {
        tps = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {}
        });

        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if (sharedPreferences.getBoolean("voice", true)) {
            try {
                tps.speak(VOICE_COMMANDS[random.nextInt(VOICE_COMMANDS.length)], TextToSpeech.QUEUE_FLUSH, null, "a");
                                    Thread.sleep(300000);
             } catch (final InterruptedException e) {
                Log.e("Void thread exception", e.getMessage());
             }
        }
    }
}
