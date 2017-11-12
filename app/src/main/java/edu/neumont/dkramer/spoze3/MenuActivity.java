package edu.neumont.dkramer.spoze3;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * Created by dkramer on 10/24/17.
 */

public class MenuActivity extends AppCompatActivity {
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menu);
	}

	public void visualizeButtonClicked(View view) {
		Intent intent = new Intent(this, ImportBitmapActivity.class);
		startActivity(intent);

//		// TESTING --> TODO make button for this action
//		Intent intent = new Intent(this, GalleryFragment.class);
//		startActivity(intent);
	}

	public void debugButtonClicked(View view) {
		Intent intent = new Intent(this, DebugVisualizationActivity.class);
		startActivity(intent);
	}
}
