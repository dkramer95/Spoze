package edu.neumont.dkramer.spoze3;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;

import edu.neumont.dkramer.spoze3.util.Preferences;

import static edu.neumont.dkramer.spoze3.util.Preferences.Key.SHAKE_FOR_HELP;

/**
 * Created by dkramer on 11/13/17.
 */

public class SettingsActivity extends AppCompatActivity {
	private SwitchCompat mShakeHelpSwitch;


	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings_layout);
		init();
	}

	@Override
	protected void onPause() {
		super.onPause();
		savePreferences();
	}

	protected void init() {
		mShakeHelpSwitch = findViewById(R.id.shakeHelpSwitch);
		mShakeHelpSwitch.setChecked(Preferences.getBoolean(SHAKE_FOR_HELP));
	}

	protected void savePreferences() {
		Preferences.putBoolean(SHAKE_FOR_HELP, mShakeHelpSwitch.isChecked()).save();
	}
}
