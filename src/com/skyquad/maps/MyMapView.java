/*                                                                                                                              
 * SkyQuad Maps - Plan routes for your quadrotor                                                                                
 *                                                                                                                              
 * Licensed under the GNU General Public License v3                                                                             
 * (c) 2011, Jörg Thalheim <jthalheim@gmail.com>                                                                                
 */
package com.skyquad.maps;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;

public class MyMapView extends MapActivity {

	private TextView mLongitude, mLatitude;
	private CheckBox mSatellite;

	private MapView mMapView;
	private MapController mMapController;

	@Override
	protected void onCreate(Bundle icicle){
		super.onCreate(icicle);
		setContentView(R.layout.mymapview);

		Bundle bundle = this.getIntent().getExtras();
		int Mode = bundle.getInt("Mode");

		mMapView = (MapView) findViewById(R.id.mapview);
		mMapController = mMapView.getController();
		mMapView.setBuiltInZoomControls(true);

		mLongitude = (TextView) findViewById(R.id.longitude);
		mLatitude = (TextView) findViewById(R.id.latitude);
		mSatellite = (CheckBox) findViewById(R.id.satellite);
		mSatellite.setOnClickListener(mySatelliteOnClickListener);

		SetSatellite();

		Log.v("MyMapView", "Mode: " + String.valueOf(bundle.getInt("Mode")));

		if (Mode == 1) {
			double latitude = bundle.getDouble("Latitude", R.string.latitiudeDefault);
			double longitude = bundle.getDouble("Longitude", R.string.longitudeDefault);
			Log.v("MyMapView", "Latitude " + String.valueOf(latitude));
			Log.v("MyMapView", "Longitude " + String.valueOf(longitude));
			ExtGeoPoint initGeoPoint = new ExtGeoPoint(latitude,longitude);
			CenterLocation(initGeoPoint);
		}		
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

	private void SetSatellite() {
		mMapView.setSatellite(mSatellite.isChecked());
	};

	private void CenterLocation(ExtGeoPoint centerGeoPoint) {
		mMapController.animateTo(centerGeoPoint);
		mLongitude.setText("Longitude: " + centerGeoPoint.getLongitudeAsStr());
		mLatitude.setText("Latitude: " + centerGeoPoint.getLatitudeAsStr());
	};

	private CheckBox.OnClickListener mySatelliteOnClickListener = new CheckBox.OnClickListener() {
		public void onClick(View v) {
			// TODO Auto-generated method stub
			SetSatellite();
		}
	};
}