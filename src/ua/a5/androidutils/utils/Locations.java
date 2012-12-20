package ua.a5.androidutils.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import ua.a5.androidutils.R;

/**
 * @author Sergey Khokhlov
 */
public class Locations implements LocationListener {
    private LocationManager locationManager;
    private static Location lastLocation;
    private Context context;
    private String locationProvider;

    public Locations(Context context) {
        this.context = context;
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (lastLocation == null) {
            lastLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }
        boolean enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!enabled) {
            locationProvider = LocationManager.NETWORK_PROVIDER;
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(R.string.gps_title).setMessage(R.string.gps_msg);
            builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    Locations.this.context.startActivity(intent);
                }
            });
            builder.setNegativeButton(android.R.string.cancel, null);
            builder.create().show();
        } else {
            locationProvider = LocationManager.GPS_PROVIDER;
        }
    }

    public void onActivityResume() {
        boolean enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!enabled) {
            locationProvider = LocationManager.NETWORK_PROVIDER;
        } else {
            locationProvider = LocationManager.GPS_PROVIDER;
        }
        locationManager.requestLocationUpdates(locationProvider, 400, 1, this);
    }

    public void onActivityPause() {
        locationManager.removeUpdates(this);
    }

    public Location getLocation() {
        return lastLocation;
    }

    @Override
    public void onLocationChanged(Location location) {
        lastLocation = location;
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
    }

    @Override
    public void onProviderEnabled(String s) {
    }

    @Override
    public void onProviderDisabled(String s) {
    }
}
