package edu.neumont.dkramer.spoze3;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import edu.neumont.dkramer.spoze3.R;

/**
 * Created by dkramer on 11/14/17.
 */

public class HelpFragment extends DialogFragment {
	private Button mGotItButton;
	private Button mStopShowButton;

	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.help_layout, container, false);
		mGotItButton = view.findViewById(R.id.gotItButton);
		mStopShowButton = view.findViewById(R.id.stopShowButton);

		return view;
	}
}
