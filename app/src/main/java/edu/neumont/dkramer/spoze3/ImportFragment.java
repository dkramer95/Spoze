package edu.neumont.dkramer.spoze3;

import android.app.DialogFragment;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

/**
 * Created by dkramer on 12/5/17.
 */

public class ImportFragment extends DialogFragment {
    protected Button mJustImportButton;
    protected Button mImportAndSaveButton;
    protected GalleryItemView mImportPreview;
    protected Uri mImageUri;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.import_fragment_layout, container, false);
        mJustImportButton = view.findViewById(R.id.justImportButton);
        mImportAndSaveButton = view.findViewById(R.id.importAndSaveButton);
        mImportPreview = view.findViewById(R.id.importPreview);

        if (mImageUri != null) {
            loadImage();
        }
        return view;
    }

    public void onResume() {
        super.onResume();
        if (mImageUri != null) {
            loadImage();
        }
    }

    protected void loadImage() {
        Glide.with(this)
                .load(mImageUri)
                .placeholder(R.drawable.ic_loading)
                .fitCenter()
                .into(mImportPreview);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    public ImageView getImportPreview() {
        return mImportPreview;
    }

    public void setResource(Uri imageURI) {
        mImageUri = imageURI;
    }
}
