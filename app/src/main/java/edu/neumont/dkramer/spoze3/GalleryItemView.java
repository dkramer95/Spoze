package edu.neumont.dkramer.spoze3;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by dkramer on 11/11/17.
 */

public class GalleryItemView extends AppCompatImageView implements View.OnClickListener, View.OnLongClickListener {
    private static final String TAG = "GalleryItemView";

    protected boolean normalSelectFlag;
    protected boolean deleteSelectFlag;
    protected String mResourceString;



    public GalleryItemView(Context context) {
        super(context);
    }

    public GalleryItemView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public GalleryItemView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /* These click methods will be called from our fragment... */

    @Override
    public boolean onLongClick(View view) {
        if (deleteSelectFlag) {
            getBackground().setColorFilter(null);
            deleteSelectFlag = false;
        } else {
            getBackground().setColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY);
            deleteSelectFlag = true;
            normalSelectFlag = false;
        }
        return true;
    }

    @Override
    public void onClick(View view) {
        if (!deleteSelectFlag) {
            if (normalSelectFlag) {
                getBackground().setColorFilter(null);
                normalSelectFlag = false;
            } else {
                getBackground().setColorFilter(Color.GREEN, PorterDuff.Mode.MULTIPLY);
                normalSelectFlag = true;
            }
        }
    }

    public void clear() {
        getBackground().setColorFilter(null);
        normalSelectFlag = false;
        deleteSelectFlag = false;
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
        return normalSelectFlag;
    }

    public boolean isDeleteSelected() {
        return deleteSelectFlag;
    }
}
