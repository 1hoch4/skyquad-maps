package com.skyquad.maps;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Main extends Activity {
	private Button newMapButton, loadMapButton;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Init UI
		setContentView(R.layout.main);
		newMapButton = (Button) findViewById(R.id.newMap);
		loadMapButton = (Button) findViewById(R.id.loadMap);
		
		newMapButton.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View arg0) {
				openLocationChooser();
			}
		});
		loadMapButton.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
			}
		});
	}
	
	private void openLocationChooser() {
		Intent intent = new Intent();
		intent.setClass(Main.this, LocationChooser.class);
		startActivity(intent);
	};
}