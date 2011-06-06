/*
 * SkyQuad Maps - Plan routes for your quadrotor
 *
 * Licensed under the GNU General Public License v3
 * (c) 2011, Jörg Thalheim <jthalheim@gmail.com>
 */
package com.skyquad.maps;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

public class LocationChooser extends Activity {
	private LocationManager mLocationManager;
	private LocationListener mLocationListener;
	private CheckBox mUseGps;
	private Button mOpenMapButton;
	private ExtGeoPoint mLocation;
	private ExtEditText mLatitude, mLongitude;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		loadMenu();
		validLocation();
		mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		mLocationListener = new MyLocationListener();

		mLocationManager
				.requestLocationUpdates(
						LocationManager.GPS_PROVIDER,
						Long.parseLong(getString(R.string.timeBetweenLocationUpdates)),
						Float.parseFloat(getString(R.string.minDistanceBeforeLocationUpdate)),
						mLocationListener);
	}

	private void loadMenu() {
		// Init UI
		setContentView(R.layout.locationchooser);
		// Get reference to UI elements
		mUseGps = (CheckBox) findViewById(R.id.useGps);
		// mLatitude = new MyEditText(findViewById(R.id.latitude).getContext());
		// mLongitude = new
		// MyEditText(findViewById(R.id.longitude).getContext());
		mLatitude = (ExtEditText) findViewById(R.id.latitude);
		mLongitude = (ExtEditText) findViewById(R.id.longitude);
		mOpenMapButton = (Button) findViewById(R.id.openMap);

		// Toggle use GPS
		mUseGps.setOnClickListener(new CheckBox.OnClickListener() {
			public void onClick(View v) {
				boolean enableEditViews = true;
				if (mUseGps.isChecked()) {
					Location location = updateGps();
					if (location == null) {
						missingGps();
						mUseGps.setChecked(false);
					} else {
						enableEditViews = false;
					}
				}

				mLatitude.setEditable(enableEditViews);
				mLongitude.setEditable(enableEditViews);
			}
		});

		// Handle user defined location
		EditText.OnKeyListener locationOnKeyListener = new EditText.OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				validLocation();
				return false;
			}
		};

		mLatitude.setOnKeyListener(locationOnKeyListener);
		mLongitude.setOnKeyListener(locationOnKeyListener);

		mOpenMapButton.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View arg0) {
				openMyMapsView(mLocation);
			}
		});
	}

	private void validLocation() {
		String latitude = mLatitude.getText().toString();
		String longitude = mLongitude.getText().toString();
		if (!(latitude.equals("") || longitude.equals("") ||
				latitude.equals("-") || longitude.equals("-"))) {
			Log.v("LocationChooser", latitude + ":" + longitude);
			double locationLatitude = Float.parseFloat(longitude);
			double locationLongitude = Float.parseFloat(latitude);		

			mLocation = new ExtGeoPoint(locationLatitude, locationLongitude);
		}
	}

	private Location updateGps() {
		// Get the current location from GPS
		mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		Location initLocation = mLocationManager
				.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		return initLocation;
	}

	private void openMyMapsView(ExtGeoPoint startLocation) {
		Intent intent = new Intent();
		intent.setClass(LocationChooser.this, MyMapView.class);
		Bundle bundle = new Bundle();
		if (startLocation == null) {
			// Mode 0 means no location is given
			bundle.putInt("Mode", 0);
		} else {
			// Mode 1: we have a starting point.
			bundle.putInt("Mode", 1);

			bundle.putDouble("Latitude", startLocation.getLatitude());
			bundle.putDouble("Longitude", startLocation.getLongitude());
			bundle.putString("Boston", "Tea");
		}

		intent.putExtras(bundle);
		startActivity(intent);
	}

	private void missingGps() {
		AlertDialog alertDialog;
		alertDialog = new AlertDialog.Builder(this).create();
		alertDialog.setTitle(R.string.missingGpsTitle);
		alertDialog.setMessage(this.getText(R.string.missingGpsMsg));
		alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				return;
			}
		});
		alertDialog.show();
	}

	private class MyLocationListener implements LocationListener {
		public void onLocationChanged(Location location) {
			if (mUseGps.isChecked()) {
				mLocation = new ExtGeoPoint(location.getLatitude(),
						location.getLongitude());
				mLatitude.setText(mLocation.getLatitudeAsStr());
				mLongitude.setText(mLocation.getLongitudeAsStr());
			}
		}

		public void onProviderDisabled(String arg0) {
		}

		public void onProviderEnabled(String arg0) {
		}

		public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		}
	}
}