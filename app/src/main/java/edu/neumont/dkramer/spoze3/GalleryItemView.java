package edu.neumont.dkramer.spoze3;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

/**
 * Created by dkramer on 11/11/17.
 */

public class GalleryItemView extends AppCompatImageView implements View.OnClickListener, View.OnLongClickListener {
    private static final String TAG = "GalleryItemView";

    protected String mResourceString;
    protected Button mDeleteButton;
    protected GalleryItem mItem;



    public GalleryItemView(Context context) {
        super(context);
    }

    public GalleryItemView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public GalleryItemView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setGalleryItem(GalleryItem item) {
        mItem = item;
    }

    public GalleryItem getItem() {
        return mItem;
    }

    /* These click methods will be called from our fragment... */

    @Override
    public boolean onLongClick(View view) {
        if (mItem.isDeleteSelected()) {
            getBackground().setColorFilter(null);
            mItem.setDeleteSelected(false);
            mDeleteButton.animate().alpha(0).setDuration(250).withEndAction(() -> mDeleteButton.setVisibility(INVISIBLE));
        } else {
            getBackground().setColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY);
            mItem.setDeleteSelected(true);
            mDeleteButton.setVisibility(VISIBLE);
            mDeleteButton.animate().alpha(0).start();
            mDeleteButton.animate().alpha(1).setDuration(250).start();
            mItem.setNormalSelected(false);
        }
        return true;
    }

    public void refreshView() {
    	if (mItem.isDeleteSelected()) {
            getBackground().setColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY);
            mDeleteButton.setVisibility(VISIBLE);
        } else if (mItem.isNormalSelected()) {
    	    getBackground().setColorFilter(Color.GREEN, PorterDuff.Mode.MULTIPLY);
        } else {
    	    getBackground().setColorFilter(null);
            mDeleteButton.setVisibility(INVISIBLE);
        }
    }

    @Override
    public void onClick(View view) {
        if (!mItem.isDeleteSelected()) {
            if (mItem.isNormalSelected()) {
                getBackground().setColorFilter(null);
                mItem.setNormalSelected(false);
            } else {
                getBackground().setColorFilter(Color.GREEN, PorterDuff.Mode.MULTIPLY);
                mItem.setNormalSelected(true);
            }
        } else {
            // clear delete
            getBackground().setColorFilter(null);
            mItem.setDeleteSelected(false);
            mDeleteButton.animate().alpha(0).setDuration(250).withEndAction(() -> mDeleteButton.setVisibility(INVISIBLE));
        }
    }

    public void clear() {
        getBackground().setColorFilter(null);
        mItem.setNormalSelected(false);
        mItem.setDeleteSelected(false);
    }

    public void onDelete() {
        clearColorFilter();
        animate().alpha(0).setDuration(5).start();
    }

    public void setResourceString(String str) {
        mResourceString = str;
    }

    public String getResourceString() {
        return mResourceString;
    }

    public boolean isNormalSelected() {
    	return mItem.isNormalSelected();
    }

    public boolean isDeleteSelected() {
    	return mItem.isDeleteSelected();
    }

    public Button getDeleteButton() {
        return mDeleteButton;
    }

    public void setDeleteButton(Button button) {
        mDeleteButton = button;
    }
}
