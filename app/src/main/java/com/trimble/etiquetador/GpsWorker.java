package com.trimble.etiquetador;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.trimble.etiquetador.model.Position;

public class GpsWorker implements LocationListener {

    private boolean isGPSEnabled=false;
    private boolean isNetworkEnabled=false;

    protected LocationManager locationManager;
    private Location location;
    private Context ctx;

    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES=0;
    private static final long MIN_TIME_BW_UPDATES=1000;

    public GpsWorker(Context ctx){
        locationManager = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);
    }

    public void init(){
        isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        try{
            if(isGPSEnabled){
                //Register for location updates using the named provider
                locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    public Position getCurrentPosition(){
        Position pos = null;
        isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        try {
            if (isGPSEnabled) {
                if (locationManager != null) {
                    location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);//Returns a Location indicating the data from the last known location fix obtained from the given provider.
                    if (location != null) {
                        pos = new Position(location.getLongitude(),location.getLatitude());
                        return pos;
                    }
                }
            }
        } catch(Exception ex){
            ex.printStackTrace();
        }
        return pos;
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}

