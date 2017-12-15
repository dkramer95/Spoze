package edu.neumont.dkramer.spoze3;

import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;

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
		Intent intent = new Intent(this, VisualizationActivity.class);

		if (resultCode == AppCompatActivity.RESULT_OK) {
			intent.putExtra("imageURI", data.getData());
		}
		startActivity(intent);
		finish();
	}

	protected void openGallery() {
		Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		intent.setType("image/*");
		startActivityForResult(Intent.createChooser(intent, "Open Image"), OPEN_IMAGE);
	}
}
