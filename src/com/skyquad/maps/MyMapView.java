/*                                                                                                                              
 * SkyQuad Maps - Plan routes for your quadrotor                                                                                
 *                                                                                                                              
 * Licensed under the GNU General Public License v3                                                                             
 * (c) 2011, Jörg Thalheim <jthalheim@gmail.com>                                                                                
 */
package com.skyquad.maps;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;
import com.google.android.maps.Projection;

public class MyMapView extends MapActivity {

	private TextView mLongitude, mLatitude;
	private CheckBox mSatellite;

	private MapView mMapView;
	private MapController mMapController;

	@Override
	protected void onCreate(Bundle icicle) {
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
			double latitude = bundle.getDouble("Latitude",
					R.string.latitiudeDefault);
			double longitude = bundle.getDouble("Longitude",
					R.string.longitudeDefault);
			Log.v("MyMapView", "Latitude " + String.valueOf(latitude));
			Log.v("MyMapView", "Longitude " + String.valueOf(longitude));
			ExtGeoPoint initGeoPoint = new ExtGeoPoint(latitude, longitude);
			CenterLocation(initGeoPoint);
		}
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	private void SetSatellite() {
		mMapView.setSatellite(mSatellite.isChecked());
	};

	private void placeMarker(ExtGeoPoint point) {
		Drawable marker = getResources().getDrawable(
				android.R.drawable.ic_menu_myplaces);
		marker.setBounds(0, 0, marker.getIntrinsicWidth(),
				marker.getIntrinsicHeight());
		mMapView.getOverlays().add(new InterestingLocations(marker, point));
	}

	private void CenterLocation(ExtGeoPoint centerGeoPoint) {
		mMapController.animateTo(centerGeoPoint);
		mLongitude.setText("Longitude: " + centerGeoPoint.getLongitudeAsStr());
		mLatitude.setText("Latitude: " + centerGeoPoint.getLatitudeAsStr());
		placeMarker(centerGeoPoint);
	};

	private CheckBox.OnClickListener mySatelliteOnClickListener = new CheckBox.OnClickListener() {
		public void onClick(View v) {
			SetSatellite();
		}
	};



	class InterestingLocations extends ItemizedOverlay<OverlayItem> {

		private List<OverlayItem> locations = new ArrayList<OverlayItem>();
		private Drawable marker;
		private OverlayItem mOverlayItem;

		boolean MoveMap;

		public InterestingLocations(Drawable defaultMarker, ExtGeoPoint point) {
			super(defaultMarker);
			// TODO Auto-generated constructor stub
			this.marker = defaultMarker;
			// create locations of interest
			mOverlayItem = new OverlayItem(point, "My Place", "My Place");
			locations.add(mOverlayItem);

			populate();
		}

		@Override
		protected OverlayItem createItem(int i) {
			// TODO Auto-generated method stub
			return locations.get(i);
		}

		@Override
		public int size() {
			// TODO Auto-generated method stub
			return locations.size();
		}

		@Override
		public void draw(Canvas canvas, MapView mapView, boolean shadow) {
			// TODO Auto-generated method stub
			super.draw(canvas, mapView, shadow);

			boundCenterBottom(marker);
		}

		@Override
		public boolean onTouchEvent(MotionEvent arg0, MapView arg1) {
			// TODO Auto-generated method stub
			// super.onTouchEvent(arg0, arg1);

			int Action = arg0.getAction();
			if (Action == MotionEvent.ACTION_UP) {

				if (!MoveMap) {
					Projection proj = mMapView.getProjection();
					ExtGeoPoint loc = new ExtGeoPoint(proj.fromPixels(
							(int) arg0.getX(), (int) arg0.getY()));

					// remove the last marker
					//mMapView.getOverlays().remove(0);

					CenterLocation(loc);
				}

			} else if (Action == MotionEvent.ACTION_DOWN) {

				MoveMap = false;

			} else if (Action == MotionEvent.ACTION_MOVE) {
				MoveMap = true;
			}

			return super.onTouchEvent(arg0, arg1);
		}
	}
}
