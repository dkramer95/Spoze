package edu.neumont.dkramer.spoze3;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import edu.neumont.dkramer.spoze3.util.Conf;
import edu.neumont.dkramer.spoze3.util.Preferences;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * Created by dkramer on 10/24/17.
 */

public class MenuActivity extends AppCompatActivity {
	private static final int PERMISSION_ALL = 5532;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menu);
		initAd();
		initButtons();
		Preferences.init(this);
		checkPermissions();
		checkSharedImage();
	}

	private String getAdAppId() {
		return Conf.IS_DEBUG ? "ca-app-pub-3940256099942544~3347511713"
				: "ca-app-pub-9570473815266143~2427211859";
	}

	private String getAdUnitId() {
		return Conf.IS_DEBUG ? "ca-app-pub-3940256099942544/6300978111"
            	: "ca-app-pub-9570473815266143/2386138816";
	}

	private void initAd() {
	    MobileAds.initialize(this, getAdAppId());

		AdView adView = new AdView(this);
		adView.setAdSize(AdSize.MEDIUM_RECTANGLE);
		adView.setAdUnitId(getAdUnitId());

		AdRequest.Builder builder = new AdRequest.Builder();
		if (Conf.IS_DEBUG) {
            builder.addTestDevice("373DA34294B332AA8A82AE2251D3A7AB");
		}
		AdRequest request = builder.build();

		if (adView.getAdSize() != null && adView.getAdUnitId() != null) {
			adView.loadAd(request);
		}
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(WRAP_CONTENT, MATCH_PARENT);
		params.gravity = Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL;
		adView.setLayoutParams(params);
		((LinearLayout)findViewById(R.id.adView)).addView(adView);

		if (request.isTestDevice(this)) {
			Toast.makeText(this, "NOTICE: Ads are test only!" +
					" Change Conf.DEBUG for production!", Toast.LENGTH_LONG).show();
		}
	}

	protected void initButtons() {
		Typeface font = Typeface.createFromAsset(getAssets(), "roboto_light.ttf");
		int[] buttonIds = { R.id.visualizeButton, R.id.settingsButton };
		for (int id : buttonIds) {
			Button button = findViewById(id);
			button.setTypeface(font);
		}
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
