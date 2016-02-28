package com.example.android.celebratemydrive;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

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
    private static final String[] INITIAL_PERMS = {
            Manifest.permission.ACCESS_FINE_LOCATION
    };
    Switch notificationSwitch;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private Spinner locationSpinner;
    private ImageView button;
    private boolean red = true;
    private ScheduledThreadPoolExecutor executor;
    private ScheduledFuture scheduledFuture;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setTitle("Iron Drive");
            setSupportActionBar(toolbar);
        }
        getPermission();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        executor = new ScheduledThreadPoolExecutor(1);

        EditText timerET = (EditText) findViewById(R.id.timer);
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
        notificationSwitch = (Switch) findViewById(R.id.voice_switch);

        button = (ImageView) findViewById(R.id.button);
        final MainActivity instance = this;
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                button.setImageResource(red ? R.drawable.green_car : R.drawable.red_car);
                if (!(red = !red)) {
                    scheduledFuture = executor.scheduleAtFixedRate(new TimerRunnable(instance),
                            0, 1000, TimeUnit.MILLISECONDS);
                    if (locationSpinner.getSelectedItem() != null
                            && locationSpinner.getSelectedItemId() > 0) {
                        Log.i("LocationSpinner", locationSpinner.getSelectedItem().toString());
                        onTrackingStart(null); // TODO: get Location instance of spinner value
                    }
                } else {
                    scheduledFuture.cancel(false);
                    if (locationSpinner.getSelectedItem() != null
                            && locationSpinner.getSelectedItemId() > 0)
                        onTrackingStop();
                }
            }
        });
    }

    @TargetApi(23)
    private void getPermission() {
        if (Integer.parseInt(Build.VERSION.RELEASE.split("\\.")[0]) >= 6)
            requestPermissions(INITIAL_PERMS, 1337);
    }

    /**
     * Called when the start button is pressed and GPS tracking is enabled (not timer).
     */
    private void onTrackingStart(final Location targetLocation) {
        try {
            locationListener = new ArriveLocationListener(this, targetLocation,
                    TARGET_LOCATION_THRESHOLD);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                    MINIMUM_LOCATION_UPDATE_TIME, MINIMUM_LOCATION_UPDATE_DISTANCE,
                    locationListener);
        } catch (final SecurityException e) {
            Log.e("MainActivity", "Permission fuck-up while activating", e);
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
            Log.e("MainActivity", "Permission fuck-up while deactivating", e);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        final int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static final class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        @NonNull
        public Dialog onCreateDialog(final Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            final int hour = c.get(Calendar.HOUR_OF_DAY);
            final int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(final TimePicker view, final int hourOfDay, final int minute) {
            String minStr = Integer.toString(minute);
            if (minute < 10) {
                minStr = "0" + minStr;
            }
        }
    }

    private final class LockingTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(final Void... voids) {
            return null;
        }
    }
}
