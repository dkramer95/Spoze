package edu.neumont.dkramer.spoze3;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.Log;

/**
 * Created by dkramer on 11/11/17.
 */

public class GalleryItemView extends AppCompatImageView {
    private static final String TAG = "GalleryItemView";
    protected boolean flag = false;




    public GalleryItemView(Context context) {
        super(context);
        init();
    }

    public GalleryItemView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GalleryItemView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    protected void init() {
        setOnLongClickListener((v) -> {
            Log.i(TAG, "Long click detected");
            if (!flag) {
                getBackground().setColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY);
                flag = true;
            } else {
                getBackground().setColorFilter(null);
                flag = false;
            }
//            setBackgroundColor(Color.parseColor("#88AA0000"));
            return true;
        });
    }
}
