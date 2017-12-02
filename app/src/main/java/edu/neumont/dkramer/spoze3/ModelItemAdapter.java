package edu.neumont.dkramer.spoze3;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import edu.neumont.dkramer.spoze3.gl.GLModel;

/**
 * Created by dkramer on 11/15/17.
 */

public class ModelItemAdapter extends BaseAdapter {
	protected List<GLModel> mData;
	protected Context mContext;
	protected LayoutInflater mInflater;
	protected TextView mModelTextView;
	protected Button mDeleteModelButton;


	public ModelItemAdapter(Context context, List<GLModel> data) {
		mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mContext = context;
		mData = data;
	}

	@Override
	public int getCount() {
		return mData.size();
	}

	@Override
	public Object getItem(int i) {
		return mData.get(i);
	}

	@Override
	public long getItemId(int i) {
		return i;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup viewGroup) {
		View view = convertView;
		if (view == null) {
			view = mInflater.inflate(R.layout.model_list_item, null);
		}
		mModelTextView = view.findViewById(R.id.modelTextView);
		mModelTextView.setText("Model : " + mData.get(position).getId());
		mDeleteModelButton = view.findViewById(R.id.deleteModelButton);
		return view;
	}
}
