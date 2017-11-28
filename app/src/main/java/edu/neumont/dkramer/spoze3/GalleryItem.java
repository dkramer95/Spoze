package edu.neumont.dkramer.spoze3;

/**
 * Created by dkramer on 11/28/17.
 */

public class GalleryItem {
	private String mResourceString;
	private boolean mNormalSelectFlag;
	private boolean mDeleteSelectFlag;

	public GalleryItem(String resourceString) {
		mResourceString = resourceString;
	}

	public boolean isNormalSelected() {
		return mNormalSelectFlag;
	}

	public boolean isDeleteSelected() {
		return mDeleteSelectFlag;
	}

	public void setNormalSelected(boolean value) {
		mNormalSelectFlag = value;
	}

	public void setDeleteSelected(boolean value) {
		mDeleteSelectFlag = value;
	}

	public String getResourceString() {
		return mResourceString;
	}

	@Override
	public String toString() {
		return String.format("[Del: %b][Normal: %b][Res: %s]\n",
				isDeleteSelected(), isNormalSelected(), getResourceString());
	}
}
