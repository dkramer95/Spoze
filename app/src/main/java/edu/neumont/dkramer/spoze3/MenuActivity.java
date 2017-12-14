package edu.neumont.dkramer.spoze3;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import edu.neumont.dkramer.spoze3.util.Preferences;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

/**
 * Created by dkramer on 10/24/17.
 */

public class MenuActivity extends AppCompatActivity {
	private static final int PERMISSION_ALL = 5532;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menu);
		Preferences.init(this);
		checkPermissions();
		checkSharedImage();
	}

	protected void checkPermissions() {
	    String[] permissions = { CAMERA, WRITE_EXTERNAL_STORAGE };
	    if (!hasPermissions(this, permissions)) {
			ActivityCompat.requestPermissions(this, permissions, PERMISSION_ALL);
		}
	}

	protected void checkSharedImage() {
		Intent intent = getIntent();
		String action = intent.getAction();
		String type = intent.getType();

		if (action != null && action.equals(Intent.ACTION_SEND) && type != null) {
			if (type.startsWith("image/")) {
				// load image
				Uri imageURI = intent.getParcelableExtra(Intent.EXTRA_STREAM);

				// create new intent to pass to visualization
				Intent visualIntent = new Intent(this, VisualizationActivity.class);
				visualIntent.setAction(action);
				visualIntent.setType(type);
				visualIntent.putExtra("imageURI", imageURI);
				startActivity(visualIntent);
			}
		}
	}


	protected static boolean hasPermissions(Context ctx, String... permissions) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ctx != null) {
			for (String p : permissions) {
				if (ActivityCompat.checkSelfPermission(ctx, p) != PackageManager.PERMISSION_GRANTED) {
					return false;
				}
			}
		}
		return true;
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
}
