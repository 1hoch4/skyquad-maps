/*
 * SkyQuad Maps - Plan routes for your quadrotor
 *
 * Licensed under the GNU General Public License v3
 * (c) 2011, Jörg Thalheim <jthalheim@gmail.com>
 */
package com.skyquad.maps;

import android.util.Log;

import com.google.android.maps.GeoPoint;

/**
 * This class handles the conversion between microdegrees and degreese
 * and provide serialisation.
 */
public class ExtGeoPoint extends GeoPoint {
	// Debugging
	private static final String TAG = "ExtGeoPoint";
	private static final boolean D = true;

	public ExtGeoPoint(double latitude, double longitude) {
		super((int) (latitude * 1E6), (int) (longitude * 1E6));		
		if (D) Log.d(TAG, toString());
	}
	
	public ExtGeoPoint(int latitudeE6, int longitudeE6) {
		super(latitudeE6, longitudeE6);
	}
	
	public ExtGeoPoint(GeoPoint p) {
		super(p.getLatitudeE6(), p.getLongitudeE6());
	}

	public double getLatitude() {
		return getLatitudeE6() / 1E6;
	}

	// Damn Java, doesn't support function signatures,
	// based of return types
	public String getLatitudeAsStr() {
		return String.valueOf(getLatitude());
	}

	public double getLongitude() {
		return getLongitudeE6() / 1E6;
	}

	public String getLongitudeAsStr() {
		return String.valueOf(getLongitude());
	}
	
	public String toString() {
		return getLatitudeAsStr() + ":" + getLongitudeAsStr();
	}
}