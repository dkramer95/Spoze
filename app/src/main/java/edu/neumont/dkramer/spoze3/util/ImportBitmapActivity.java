package edu.neumont.dkramer.spoze3.util;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import edu.neumont.dkramer.spoze3.VisualizationActivity;

/**
 * Created by dkramer on 10/24/17.
 */

public class ImportBitmapActivity extends AppCompatActivity {
	protected static final int OPEN_IMAGE = 1191;

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
			case OPEN_IMAGE:
				openImage(resultCode, data);
				break;
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		openGallery();
	}

	protected void openImage(int resultCode, Intent data) {
		if (resultCode == AppCompatActivity.RESULT_OK) {
			Intent intent = new Intent(this, VisualizationActivity.class);
			// pass URI of image to the activity
			intent.setData(data.getData());
			startActivity(intent);
		}
		finish();
	}

	protected void openGallery() {
		Intent intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		startActivityForResult(Intent.createChooser(intent, "Open Image"), OPEN_IMAGE);
	}
}
