package edu.neumont.dkramer.spoze3;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import edu.neumont.dkramer.spoze3.util.Preferences;

import static edu.neumont.dkramer.spoze3.util.Preferences.Key.SCREENSHOT_FORMAT;
import static edu.neumont.dkramer.spoze3.util.Preferences.Key.SCREENSHOT_QUALITY;
import static edu.neumont.dkramer.spoze3.util.Preferences.Key.SHAKE_ACTION;
import static edu.neumont.dkramer.spoze3.util.Preferences.Key.SHAKE_SENSITIVITY;
import static edu.neumont.dkramer.spoze3.util.Preferences.Key.SWIPE_ACTION;

/**
 * Created by dkramer on 11/13/17.
 */

public class SettingsActivity extends AppCompatActivity {
	private Spinner mShakeActionSpinner;
	private Spinner mShakeSensitivitySpinner;
	private Spinner mSwipeSpinner;
	private Spinner mFormatSpinner;
	private SeekBar mQualitySlider;
	private TextView mQualityTextView;



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
		loadViews();
		loadSlider();
		createDropdowns();
		populate();
		populateDropdowns();
	}

	protected void loadSlider() {
		mQualitySlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
				mQualityTextView.setText("" + progress);
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) { }

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) { }
		});
		mQualitySlider.setProgress(Preferences.getInt(SCREENSHOT_QUALITY, 100));
	}

	protected void loadViews() {
		mShakeActionSpinner = findViewById(R.id.shakeGesturesSpinner);
		mShakeSensitivitySpinner = findViewById(R.id.shakeSensitivitySpinner);
		mSwipeSpinner = findViewById(R.id.swipeGesturesSpinner);
		mFormatSpinner = findViewById(R.id.formatSpinner);
		mQualitySlider = findViewById(R.id.qualitySlider);
		mQualityTextView = findViewById(R.id.qualityTextView);
	}

	protected void createDropdowns() {
		createDropdown(R.array.shake_actions_array, mShakeActionSpinner);
		createDropdown(R.array.shake_sensitivity_array, mShakeSensitivitySpinner);
		createDropdown(R.array.swipe_actions_array, mSwipeSpinner);
		createDropdown(R.array.screenshot_formats_array, mFormatSpinner);
	}

	protected void populate() {
		populateDropdowns();
	}

	protected void populateDropdowns() {
		populateSpinnerFromPreference(mShakeActionSpinner, Preferences.getString(SHAKE_ACTION));
		populateSpinnerFromPreference(mShakeSensitivitySpinner, Preferences.getString(SHAKE_SENSITIVITY));
		populateSpinnerFromPreference(mSwipeSpinner, Preferences.getString(SWIPE_ACTION));
		populateSpinnerFromPreference(mFormatSpinner, Preferences.getString(SCREENSHOT_FORMAT));
	}

	protected void populateSpinnerFromPreference(Spinner spinner, String value) {
		int index = spinnerStringIndex(spinner, value);
		// default to first if value isn't found
		index = (index == -1) ? 0 : index;
		spinner.setSelection(index);
	}

	protected int spinnerStringIndex(Spinner spinner, String str) {
		return ((ArrayAdapter<CharSequence>)spinner.getAdapter()).getPosition(str);
	}

	private void createDropdown(int resourceArrayId, Spinner spinner) {
		ArrayAdapter<CharSequence> sensitivityAdapter =
				ArrayAdapter.createFromResource(this, resourceArrayId, android.R.layout.simple_spinner_item);
		sensitivityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(sensitivityAdapter);
	}

	protected void savePreferences() {
		Preferences.putString(SHAKE_ACTION, mShakeActionSpinner.getSelectedItem().toString());
		Preferences.putString(SHAKE_SENSITIVITY, mShakeSensitivitySpinner.getSelectedItem().toString());
		Preferences.putString(SWIPE_ACTION, mSwipeSpinner.getSelectedItem().toString());
		Preferences.putString(SCREENSHOT_FORMAT, mFormatSpinner.getSelectedItem().toString());
		Preferences.putInt(SCREENSHOT_QUALITY, mQualitySlider.getProgress());
		Preferences.save();
	}
}
