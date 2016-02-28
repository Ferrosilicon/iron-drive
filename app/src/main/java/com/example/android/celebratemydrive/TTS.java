package com.example.android.celebratemydrive;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.Locale;
import java.util.Random;

// TODO: deprecated
final class TTS extends Service implements TextToSpeech.OnInitListener,
        TextToSpeech.OnUtteranceCompletedListener {

    private static final String[] VOICE_COMMANDS = new String[]{"Keep two eyes on the road",
            "Keep two hands on the wheel", "Obey the speed limit",
            "Make sure you're wearing your seat belt"};
    private final Random random = new Random();
    private TextToSpeech mTts;

    @Override
    public void onCreate() {
        Log.e("Tag", "Service in onCreate");
        mTts = new TextToSpeech(this, this);
        // This is a good place to set spokenText
    }

    @Override
    public void onInit(final int status) {
        Log.e("Tag", "Service started");
        if (status == TextToSpeech.SUCCESS) {
            final int result = mTts.setLanguage(Locale.US);
            if (result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED) {
                mTts.speak(VOICE_COMMANDS[random.nextInt(VOICE_COMMANDS.length)], TextToSpeech.QUEUE_FLUSH, null, "a");
            }
        }
    }

    @Override
    public void onUtteranceCompleted(final String uttId) {
        stopSelf();
    }

    @Override
    public void onDestroy() {
        if (mTts != null) {
            mTts.stop();
            mTts.shutdown();
        }
        super.onDestroy();
    }

    @Override
    public IBinder onBind(final Intent arg0) {
        return null;
    }
}
