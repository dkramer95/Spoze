package edu.neumont.dkramer.spoze3;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import edu.neumont.dkramer.spoze3.gesture.DeviceShake;
import edu.neumont.dkramer.spoze3.gl.GLActivity;
import edu.neumont.dkramer.spoze3.util.Preferences;

import static edu.neumont.dkramer.spoze3.gl.deviceinfo.GLDeviceInfo.Type.ACCELEROMETER;
import static edu.neumont.dkramer.spoze3.util.Preferences.Key.SHAKE_ACTION;

/**
 * Created by dkramer on 11/14/17.
 */

public class HelpFragment extends DialogFragment implements DeviceShake.OnShakeListener {
	private Button mGotItButton;
	private Button mStopShowButton;
	private DeviceShake mDeviceShake;




	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.help_layout, container, false);
		init(view);
		return view;
	}

	private void init(View view) {
//		DebugVisualizationActivity activity = (DebugVisualizationActivity)getActivity();
//
//		if (Preferences.getBoolean(SHAKE_ACTION)) {
//			mDeviceShake = new DeviceShake(this);
//			activity.getGLContext().getDeviceInfo(ACCELEROMETER).addOnUpdateListener(mDeviceShake);
//
//			mGotItButton = view.findViewById(R.id.gotItButton);
//			mGotItButton.setOnClickListener((v) -> {
//				hide();
//			});
//
//			mStopShowButton = view.findViewById(R.id.stopShowButton);
//			mStopShowButton.setOnClickListener((v) -> {
//				Preferences.putBoolean(SHAKE_ACTION, false).save();
//				activity.getGLContext().getDeviceInfo(ACCELEROMETER).removeOnUpdateListener(mDeviceShake);
//				hide();
//				Toast.makeText(activity, "Shake for help disabled", Toast.LENGTH_LONG).show();
//			});
//		}

		VisualizationActivity2 activity = (VisualizationActivity2)getActivity();

		mGotItButton = view.findViewById(R.id.gotItButton);
		mGotItButton.setOnClickListener((v) -> {
			hide();
		});

		mStopShowButton = view.findViewById(R.id.stopShowButton);
		mStopShowButton.setOnClickListener((v) -> {
			Preferences.putString(SHAKE_ACTION, "None").save();
			activity.initShakeAction();
			hide();
			Toast.makeText(activity, "Shake for help disabled", Toast.LENGTH_LONG).show();
		});
	}

	public void hide() {
		getFragmentManager()
			.beginTransaction()
			.setCustomAnimations(R.animator.fade_in, R.animator.fade_out)
			.hide(this)
			.commit();
	}

	public void show() {
		getFragmentManager()
			.beginTransaction()
			.setCustomAnimations(R.animator.fade_in, R.animator.fade_out)
			.show(this)
			.commit();
	}

	@Override
	public void onShake() {
		show();
	}
}
