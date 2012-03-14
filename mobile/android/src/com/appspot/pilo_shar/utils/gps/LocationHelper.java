package com.appspot.pilo_shar.utils.gps;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public class LocationHelper {
	private Timer timer;
	private LocationManager locMan;

	private LocationResult locationResult; // Location Result Callback

	private boolean gps_enabled = false;
	private boolean network_enabled = false;
	private int TIME_OUT_INTERVAL = 20000; // After (x)millisecond, do not find
											// any location

	public boolean getLocation(Context context, LocationResult result) {
		locationResult = result;// Callback when a location found
		// Set timer for request location after that get LastKnownGoodLocation
		timer = new Timer();
		timer.schedule(new GetLastLocation(), TIME_OUT_INTERVAL);
		
		if (locMan == null)
			locMan = (LocationManager) context
					.getSystemService(Context.LOCATION_SERVICE);

		// Check if gps, network is on
		try {
			gps_enabled = locMan
					.isProviderEnabled(LocationManager.GPS_PROVIDER);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		try {
			network_enabled = locMan
					.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		// don't start listeners if no provider is enabled
		if (!gps_enabled && !network_enabled)
			return false;

		// Request GPS
		if (gps_enabled)
			locMan.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,
					locationListenerGps);
		// request network location
		if (network_enabled)
			locMan.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0,
					0, locationListenerNetwork);


		return true;
	}

	// Listener when GPS location is found
	private LocationListener locationListenerGps = new LocationListener() {
		public void onLocationChanged(Location location) {
			timer.cancel();
			locationResult.gotLocation(location);

			// Stop request update
			locMan.removeUpdates(this);
			locMan.removeUpdates(locationListenerNetwork);
		}

		public void onProviderDisabled(String provider) {
		}

		public void onProviderEnabled(String provider) {
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	};
	// Listener when network location is found
	private LocationListener locationListenerNetwork = new LocationListener() {
		public void onLocationChanged(Location location) {
			timer.cancel();
			locationResult.gotLocation(location);

			// Stop request update
			locMan.removeUpdates(this);
			locMan.removeUpdates(locationListenerGps);
		}

		public void onProviderDisabled(String provider) {
		}

		public void onProviderEnabled(String provider) {
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	};

	// Timeout, cant get anything from gps, network, trying to get
	// LastKnownGoodLocation
	private class GetLastLocation extends TimerTask {
		@Override
		public void run() {
			locMan.removeUpdates(locationListenerGps);
			locMan.removeUpdates(locationListenerNetwork);

			Location net_loc = null, gps_loc = null;
			if (gps_enabled)
				gps_loc = locMan
						.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			if (network_enabled)
				net_loc = locMan
						.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

			// if there are both values use the latest one
			if (gps_loc != null && net_loc != null) {

				if (gps_loc.getTime() > net_loc.getTime()) {
					locationResult.gotLocation(gps_loc);
				} else {
					locationResult.gotLocation(net_loc);
					return;
				}
			}

			if (gps_loc != null) {
				locationResult.gotLocation(gps_loc);
				return;
			}
			locationResult.gotLocation(net_loc);

		}
	}

}
