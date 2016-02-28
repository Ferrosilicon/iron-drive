package com.example.android.celebratemydrive;

import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.Locale;
import java.util.Random;

final class TimerRunnable implements Runnable, TextToSpeech.OnInitListener {

    /**
     * The delay between voice commands.
     */
    private static final int VOICE_COUNTDOWN_DELAY = 20;

    private static final String[] VOICE_COMMANDS = new String[]{"Keep two eyes on the road",
            "Keep two hands on the wheel", "Obey the speed limit",
            "Make sure you're wearing your seat belt"};

    private static final Random RANDOM = new Random();

    private final MainActivity mainActivity;

    private TextToSpeech tts;

    private int voiceCountdown = VOICE_COUNTDOWN_DELAY;

    TimerRunnable(final MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        if (mainActivity.notificationSwitch.isChecked())
            tts = new TextToSpeech(mainActivity.getApplicationContext(), this);
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            final int result = tts.setLanguage(Locale.US);
            if (result != TextToSpeech.LANG_MISSING_DATA
                    && result != TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.i("TimerRunnable", "Initiated TPS successfully");
            }
        }
    }

    @Override
    public void run() {
        if (voiceCountdown == 0 && mainActivity.notificationSwitch.isChecked()) {
            tts.speak(VOICE_COMMANDS[RANDOM.nextInt(VOICE_COMMANDS.length)],
                    TextToSpeech.QUEUE_ADD, null, "RANDOM");
            voiceCountdown = VOICE_COUNTDOWN_DELAY;
        }
        voiceCountdown--;
    }
}
