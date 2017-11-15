package edu.neumont.dkramer.spoze3;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ListView;

import java.util.List;

import edu.neumont.dkramer.spoze3.gl.GLModel;

/**
 * Created by dkramer on 11/15/17.
 */

public class ModelFragment extends DialogFragment {
	protected ListView mModelListView;
	protected ModelItemAdapter mModelItemAdapter;
	protected Button mCloseListButton;


	public void setModelData(List<GLModel> modelData) {
		mModelItemAdapter = new ModelItemAdapter(getActivity(), modelData);
		mModelListView.setAdapter(mModelItemAdapter);
//		mModelItemAdapter.notifyDataSetChanged();
	}


	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.model_list_layout, container, false);
		mModelListView = view.findViewById(R.id.modelListView);
		mCloseListButton = view.findViewById(R.id.closeListButton);
		mCloseListButton.setOnClickListener((v) -> hide());
		mModelListView.setAdapter(mModelItemAdapter);
		return view;
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

}
