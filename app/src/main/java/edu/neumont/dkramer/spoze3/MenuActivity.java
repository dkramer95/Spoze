package edu.neumont.dkramer.spoze3;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import edu.neumont.dkramer.spoze3.util.Preferences;

/**
 * Created by dkramer on 10/24/17.
 */

public class MenuActivity extends AppCompatActivity {
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menu);
		Preferences.init(this);
	}

	protected void onDestroy() {
		super.onDestroy();
		Preferences.finish();
	}

	public void visualizeButtonClicked(View view) {
		Intent intent = new Intent(this, VisualizationActivity.class);
		startActivity(intent);
	}

	public void settingsButtonClicked(View view) {
		Intent intent = new Intent(this, SettingsActivity.class);
		startActivity(intent);
	}

	public void debugButtonClicked(View view) {
		Intent intent = new Intent(this, DebugVisualizationActivity.class);
		startActivity(intent);
	}
}
