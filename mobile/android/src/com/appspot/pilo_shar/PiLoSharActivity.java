package com.appspot.pilo_shar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class PiLoSharActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		initListener();
	}

	private void initListener() {
		// Find button resources
		Button btnShowMap = (Button) findViewById(R.id.btn_showmap);
		Button btnShowImagesList = (Button) findViewById(R.id.btn_show_images);

		// Register listener
		btnShowMap.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// Create an intent(a plan)
				Intent showMapIntent = new Intent(getApplicationContext(),
						MapActivity.class);
				// Start it
				startActivity(showMapIntent);
			}
		});

		btnShowImagesList.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// Create an intent(a plan)
				Intent showListIntent = new Intent(getApplicationContext(),ImageActivity.class);
				// Start it
				startActivity(showListIntent);
			}
		});

	}
}