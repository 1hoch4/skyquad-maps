/*
 * SkyQuad Maps - Plan routes for your quadrotor
 *
 * Licensed under the GNU General Public License v3
 * (c) 2011, Jörg Thalheim <jthalheim@gmail.com>
 */
package com.skyquad.maps;

import android.util.Log;

import com.google.android.maps.GeoPoint;

public class ExtGeoPoint extends GeoPoint {
	public ExtGeoPoint(double latitude, double longitude) {
		super((int) (latitude * 1000000), (int) (longitude * 1000000));
		Log.v("ExtGeoPoint", (latitude * 1000000) + ":" + (longitude * 1000000));
		Log.v("ExtGeoPoint",getLatitudeAsStr() + ":" + getLongitudeAsStr());
	}
	
	public ExtGeoPoint(int latitudeE6, int longitudeE6) {
		super((int) (latitudeE6), (int) (longitudeE6));
	}

	@Override
	public int getLatitudeE6() {
		return super.getLatitudeE6();
	}

	@Override
	public int getLongitudeE6() {
		return super.getLongitudeE6();
	}

	public double getLatitude() {
		return (double) getLatitudeE6() / 1E6;
	}

	public String getLatitudeAsStr() {
		return String.valueOf(getLatitude());
	}

	public double getLongitude() {
		return (double) getLongitudeE6() / 1E6;
	}

	public String getLongitudeAsStr() {
		return String.valueOf(getLongitude());
	}
}