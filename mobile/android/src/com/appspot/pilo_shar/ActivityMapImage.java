package com.appspot.pilo_shar;

import android.os.Bundle;

import com.google.android.maps.MapActivity;

public class ActivityMapImage extends MapActivity {
	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.activity_map);
		
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}


}
