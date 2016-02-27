package com.example.android.celebratemydrive;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("AlarmReceiver", "Received");

        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        // SharedPreferences.Editor editor = sharedPreferences.edit();

        if (sharedPreferences.getBoolean("voice", true)) {
            Log.e("", "Voice is true");
            Intent myIntent = new Intent(context, TTS.class);
            context.startService(myIntent);
        }
    }
}
