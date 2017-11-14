package edu.neumont.dkramer.spoze3;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.widget.Switch;

import edu.neumont.dkramer.spoze3.util.PreferenceKeys;

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
		openPreferences();
	}

	protected void openPreferences() {
		SharedPreferences prefs = getPreferences(Context.MODE_PRIVATE);
		boolean shakeToHelp = prefs.getBoolean(PreferenceKeys.SHAKE_FOR_HELP, true);
		mShakeHelpSwitch.setChecked(shakeToHelp);
	}

	protected void savePreferences() {
		SharedPreferences prefs = getPreferences(Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putBoolean(PreferenceKeys.SHAKE_FOR_HELP, mShakeHelpSwitch.isChecked());
		editor.commit();
	}

}
