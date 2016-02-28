package com.example.android.celebratemydrive;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

class ArriveLocationListener implements LocationListener {

    private final MainActivity mainActivity;
    private final Location targetLocation;
    private final float targetLocationThreshold;

    ArriveLocationListener(final MainActivity mainActivity, final Location targetLocation,
                           final float targetLocationThreshold) {
        this.mainActivity = mainActivity;
        this.targetLocation = targetLocation;
        this.targetLocationThreshold = targetLocationThreshold;
    }

    @Override
    public void onLocationChanged(final Location location) {
        if (targetLocation.distanceTo(location) <= targetLocationThreshold) {
            // TODO: Arrived at location
            mainActivity.onTrackingStop();
        }
    }

    @Override
    public void onStatusChanged(final String provider, final int status, final Bundle extras) {
    }

    @Override
    public void onProviderEnabled(final String provider) {
    }

    @Override
    public void onProviderDisabled(final String provider) {
    }
}
