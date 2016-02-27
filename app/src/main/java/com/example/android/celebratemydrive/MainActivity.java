package com.example.android.celebratemydrive;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
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
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TimePicker;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    /**
     * The minimum amount of time between location updates in milliseconds.
     */
    private static final long MINIMUM_LOCATION_UPDATE_TIME = 2500;

    /**
     * The minimum amount of distance between location updates in meters.
     */
    private static final float MINIMUM_LOCATION_UPDATE_DISTANCE = 10;

    /**
     * The target location threshold in meters.
     */
    private static final float TARGET_LOCATION_THRESHOLD = 50;
    EditText timerET;
    ImageView button;
    boolean red = true;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private Spinner locationSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setTitle("Iron Drive");
            setSupportActionBar(toolbar);
        }

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

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
        locationSpinner = (Spinner) findViewById(R.id.locationSpinner);

        button = (ImageView) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                button.setImageResource(red ? R.drawable.green_car : R.drawable.red_car);
                final View voiceSwitch = findViewById(R.id.voice_switch);
                if (!(red = !red)) {
                    Calendar c = Calendar.getInstance();
                    PendingIntent mAlarmSender = PendingIntent.getBroadcast(getApplicationContext(), 0,
                            new Intent(getApplicationContext(), AlarmReceiver.class),
                            PendingIntent.FLAG_ONE_SHOT);
                    long firstTime = c.getTimeInMillis();
                    AlarmManager am = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
                    SharedPreferences sharedPreferences = PreferenceManager
                            .getDefaultSharedPreferences(getApplicationContext());
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("voice", voiceSwitch.isEnabled());
                    editor.apply();
                    Boolean bool = sharedPreferences.getBoolean("voice", true);
                    String s = bool.toString();
                    Log.e("", s);

                    if (locationSpinner.getSelectedItem() != null) {
                        onTrackingStart(null); // TODO: get Location instance of spinner value
                    }
                    am.set(AlarmManager.RTC_WAKEUP, firstTime, mAlarmSender);
                } else {
                    if (locationSpinner.getSelectedItem() != null)
                        onTrackingStop();
                }
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
                editor.apply();
            }
        });
    }

    /**
     * Called when the start button is pressed and GPS tracking is enabled (not timer).
     */
    void onTrackingStart(final Location targetLocation) {
        try {
            locationListener = new ArriveLocationListener(this, targetLocation, TARGET_LOCATION_THRESHOLD);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                    MINIMUM_LOCATION_UPDATE_TIME, MINIMUM_LOCATION_UPDATE_DISTANCE, locationListener);
        } catch (final SecurityException e) {
            Log.e("MainActivity", "Permission fuck-up while activating");
        }
    }

    /**
     * Called when the app is stopped when tracking is enabled or the user has arrived at their
     * location.
     */
    void onTrackingStop() {
        try {
            locationManager.removeUpdates(locationListener);
            locationListener = null;
        } catch (final SecurityException e) {
            Log.e("MainActivity", "Permission fuck-up while deactivating");
        }
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
        @NonNull
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
