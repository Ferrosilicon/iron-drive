package com.example.android.celebratemydrive;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Random;


public class MainActivity extends AppCompatActivity {

    private static final String[] VOICE_COMMANDS = new String[] { "Keep two eyes on the road",
            "Keep two hands on the wheel", "Obey the speed limit", "Make sure you're wearing your seat belt" };

    Random random = new Random();
    EditText timerET;
    ImageView button;
    boolean red = true;
    Thread thread;
    TextToSpeech tps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setTitle("Iron Drive");
            setSupportActionBar(toolbar);
        }

        timerET = (EditText) findViewById(R.id.timer);
        timerET.setClickable(true);
        timerET.setFocusable(false);
        timerET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new TimePickerFragment();
                newFragment.show(getSupportFragmentManager(), "timePicker");
            }
        });
//        tps = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
//            @Override
//            public void onInit(int status) {
//
//            }
//        });
        button = (ImageView) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                button.setImageResource(red ? R.drawable.green_car : R.drawable.red_car);
                final View voiceSwitch = findViewById(R.id.voice_switch);
                red = !red;
                if (!red) {
                    Calendar c = Calendar.getInstance();
                    PendingIntent mAlarmSender = PendingIntent.getBroadcast(getApplicationContext(), 0,
                            new Intent(getApplicationContext(), AlarmReceiver.class),
                            PendingIntent.FLAG_ONE_SHOT);
                    long firstTime = c.getTimeInMillis();
                    AlarmManager am = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
                    am.set(AlarmManager.RTC_WAKEUP, firstTime, mAlarmSender);
                }
//                if (!(red = !red) && voiceSwitch != null && voiceSwitch.isEnabled())
//                    new Thread(new Runnable() {
//                        @Override
//                        public void run() {
//                            try {
//                                while (!red) {
//                                    tps.speak(VOICE_COMMANDS[random.nextInt(VOICE_COMMANDS.length)], TextToSpeech.QUEUE_FLUSH, null, "a");
//                                    Thread.sleep(300000);
//                                }
//                            } catch (final InterruptedException e) {
//                                Log.e("Void thread exception", e.getMessage());
//                            }
//                        }
//                    }).start();
//                if (!red) {
//                    new LockingTask().execute();
//                }
            }
        });
        final Switch notificationSwitch = (Switch) findViewById(R.id.voice_switch);
        notificationSwitch.setChecked(PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                .getBoolean("voice", true));
        notificationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences sharedPreferences = PreferenceManager
                        .getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("voice", isChecked);
                editor.commit();

                if (isChecked) {
                    Calendar c = Calendar.getInstance();
                    PendingIntent mAlarmSender = PendingIntent.getBroadcast(getApplicationContext(), 0,
                            new Intent(getApplicationContext(), AlarmReceiver.class),
                            PendingIntent.FLAG_ONE_SHOT);
                    long firstTime = c.getTimeInMillis();
                    AlarmManager am = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
                    if (!sharedPreferences.getBoolean("running", false)) {
                        am.set(AlarmManager.RTC_WAKEUP, firstTime, mAlarmSender);
                    }
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            String minStr = Integer.toString(minute);
            if (minute < 10) {
                minStr = "0" + minStr;
            }
        }
    }

    private class LockingTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            return null;
        }
    }
}
