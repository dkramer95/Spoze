package edu.neumont.dkramer.spoze3.fragments;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import edu.neumont.dkramer.spoze3.GalleryItemView;
import edu.neumont.dkramer.spoze3.R;
import edu.neumont.dkramer.spoze3.VisualizationActivity;

/**
 * Created by dkramer on 12/5/17.
 */

public class ImportFragment extends OverlayFragment {
    protected Button mJustImportButton;
    protected Button mImportAndSaveButton;
    protected ImageButton mCloseButton;
    protected GalleryItemView mImportPreview;
    protected Uri mImageUri;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.import_fragment_layout, container, false);
        mJustImportButton = view.findViewById(R.id.justImportButton);
        mImportAndSaveButton = view.findViewById(R.id.importAndSaveButton);
        mImportPreview = view.findViewById(R.id.importPreview);
        mCloseButton = view.findViewById(R.id.closeImportButton);
        initButtons();

        if (mImageUri != null) {
            loadImage();
        }
        return view;
    }

    protected void initButtons() {
        mJustImportButton.setOnClickListener((v) -> {
            importIntoWorld(false);
            hide();
        });

        mImportAndSaveButton.setOnClickListener((v) -> {
            importIntoWorld(true);
            hide();
        });

        mCloseButton.setOnClickListener((v) -> {
            hide();
            Toast.makeText(getActivity(), "Import canceled", Toast.LENGTH_SHORT).show();
        });
    }

    protected void importIntoWorld(boolean saveImage) {
        VisualizationActivity activity = (VisualizationActivity)getActivity();

        Glide.with(this).load(mImageUri).asBitmap().into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap bmp, GlideAnimation animation) {
                activity.importBitmap(bmp);
                if (saveImage) {
                    activity.saveBitmap(bmp, activity.getSavedImportsDir(), "png", 100);
                    Toast.makeText(getActivity(), "Saved Image!", Toast.LENGTH_SHORT).show();
                }
            }
        });
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
