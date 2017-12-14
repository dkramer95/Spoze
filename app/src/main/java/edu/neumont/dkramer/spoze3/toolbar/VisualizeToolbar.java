package edu.neumont.dkramer.spoze3.toolbar;

import edu.neumont.dkramer.spoze3.R;

/**
 * Created by dkramer on 12/13/17.
 */

public enum VisualizeToolbar implements IToolbar {
    NORMAL(R.layout.normal_toolbar),
    OBJECT(R.layout.sign_toolbar);

    private int mId;

    VisualizeToolbar(int id) {
        mId = id;
    }

    @Override
    public int getLayoutId() {
        return mId;
    }
}
