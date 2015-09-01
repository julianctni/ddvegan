package com.pasta.ddvegan.utils;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.pasta.ddvegan.fragments.MapFragment;
import com.pasta.ddvegan.fragments.SpotListFragment;

public class GpsUtil extends Handler implements LocationListener {

    SpotListFragment listContext;
    MapFragment mapContext;

    protected static final int UPDATE_LOCATION = 0;

    private LocationManager locationManager;
    private static final long MIN_TIME = 5 * 60 * 1000; // 5 Minuten

    private double latitude, longitude;
    public boolean newLocation = false;

    public GpsUtil(SpotListFragment myContext) {
        this.listContext = myContext;
    }

    public GpsUtil(MapFragment myContext) {
        this.mapContext = myContext;
    }

    public void updateLocation() {
        if (listContext != null)
            locationManager = (LocationManager) listContext.getActivity().getSystemService(Context.LOCATION_SERVICE);

        if (mapContext != null)
            locationManager = (LocationManager) mapContext.getActivity().getSystemService(Context.LOCATION_SERVICE);

        if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, 5000, this);
        } else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, 5000, this);
        }
    }

    public double getLatitude() {
        return this.latitude;
    }

    public double getLongitude() {
        return this.longitude;
    }

    public void stop() {
        locationManager.removeUpdates(this);
        locationManager = null;
    }

    public float calculateDistance(double dist_lat, double dist_lng) {
        Location standort = new Location("current");
        standort.setLatitude(this.latitude);
        standort.setLongitude(this.longitude);
        Location ziel = new Location("target");
        ziel.setLatitude(dist_lat);
        ziel.setLongitude(dist_lng);
        return standort.distanceTo(ziel) / 1000;
    }

    public boolean isOn() {
        return (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager
                .isProviderEnabled(LocationManager.NETWORK_PROVIDER));
    }

    @Override
    public void onLocationChanged(Location location) {
        this.latitude = location.getLatitude();
        this.longitude = location.getLongitude();
        Log.i("gps", location.getLatitude() + "");
        Log.i("gps", location.getLongitude() + "");
        newLocation = true;
        Message msg = Message.obtain();
        msg.what = UPDATE_LOCATION;
        if (listContext != null)
            this.listContext.getHandler().sendMessage(msg);
        if (mapContext != null)
            this.mapContext.getHandler().sendMessage(msg);
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }
}
