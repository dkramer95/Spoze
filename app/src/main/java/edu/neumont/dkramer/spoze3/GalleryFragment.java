package edu.neumont.dkramer.spoze3;

import android.Manifest;
import android.app.DialogFragment;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.content.res.Configuration.ORIENTATION_LANDSCAPE;
import static android.content.res.Configuration.ORIENTATION_PORTRAIT;
import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

/**
 * Created by dkramer on 11/11/17.
 */

public class GalleryFragment extends DialogFragment {
    private static final int PERMISSION_CODE = 5813;

    private static final String TAG = "Gallery Fragment";
    private static final String SPOZE_DIRECTORY = "SpozeGallery";

    protected RecyclerView mRecyclerView;
    protected ButtonClickHandler mButtonClickHandler;
    protected Button mLoadSelectedButton;
    protected Button mCloseButton;
    protected Spinner mDirectorySpinner;

    protected List<GalleryItemView> mSelectedItems;
    protected List<String> mDirectories;
    protected String mSelectedDirectory;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ensureCanWriteToExternalStorage();
    }

    protected boolean ensureCanWriteToExternalStorage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!hasGrantedWriteExternalStoragePermission()) {
                Toast.makeText(getContext(), "Please grant permission to write to external storage", Toast.LENGTH_LONG).show();
                requestWriteExternalStoragePermission();
                return false;
            }
        }

        if (isExternalStorageWritable()) {
            mDirectories = getGalleryDirectories();

//            File dir = getGalleryDir();
//            dir.mkdirs();
//            if (!dir.exists()) {
//                Toast.makeText(getActivity(), "Spoze Gallery Missing!", Toast.LENGTH_LONG).show();
//            }
        }
        return true;
    }

    protected String getPictureDir() {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
    }

    protected List<String> getGalleryDirectories() {
//        File dirs = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES));
        File dirs = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        return Arrays.asList(dirs.list());
    }

//    protected File getGalleryDir() {
//        return new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
//                + File.separator + SPOZE_DIRECTORY);
//    }

    protected File getGalleryDir() {
        if (mSelectedDirectory != null) {
            return new File(mSelectedDirectory);
        } else {
            return new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                    + File.separator + SPOZE_DIRECTORY);
        }
    }

    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return state.equals(Environment.MEDIA_MOUNTED);
    }

    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return state.equals(Environment.MEDIA_MOUNTED)
               || state.equals(Environment.MEDIA_MOUNTED_READ_ONLY);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.gallery_fragment_layout, container, false);
        mRecyclerView = view.findViewById(R.id.imagegallery);
        updateLayoutManager(getActivity().getResources().getConfiguration().orientation);

        if (ensureCanWriteToExternalStorage()) {
            initButtons(view);
            initGalleryView();
        }

        mDirectorySpinner = view.findViewById(R.id.directorySpinner);

        DirectoryItemAdapter adapter = new DirectoryItemAdapter(getActivity(), getGalleryDirectories());
        mDirectorySpinner.setAdapter(new DirectoryItemAdapter(getActivity(), getGalleryDirectories()));
        mDirectorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String dir = getPictureDir() + File.separator + adapter.getItem(i);

                // we selected something new
                if (!dir.equals(mSelectedDirectory)) {
                    mSelectedDirectory = dir;
                    Toast.makeText(getActivity(), "Selected: " + dir, Toast.LENGTH_SHORT).show();
                    initGalleryView();
                    // update our recycler view
                }
//                mDirectorySpinner.setVisibility(INVISIBLE);
            }

            // unused
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Toast.makeText(getActivity(), "Nothing Selected", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    protected void initGalleryView() {
        List<GalleryItem> createLists = prepareData();
        MyAdapter adapter = new MyAdapter(getActivity(), createLists);
        mRecyclerView.setAdapter(adapter);
    }

    protected void initButtons(View view) {
        // Button stuff
        mLoadSelectedButton = view.findViewById(R.id.loadSelectedButton);
        mCloseButton = view.findViewById(R.id.closeButton);

        mButtonClickHandler = new ButtonClickHandler();
        mSelectedItems = new ArrayList<>();
        refreshButtons();
    }

    public void hide() {
        getFragmentManager()
			.beginTransaction()
			.setCustomAnimations(R.animator.fade_in, R.animator.fade_out)
			.hide(this)
			.commit();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        updateLayoutManager(newConfig.orientation);
    }

    protected void updateLayoutManager(int orientation) {
        if (orientation == ORIENTATION_PORTRAIT) {
            mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        } else if (orientation == ORIENTATION_LANDSCAPE) {
            mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 4));
        }
    }

    protected boolean hasGrantedWriteExternalStoragePermission() {
        return ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    protected void requestWriteExternalStoragePermission() {
        ActivityCompat.requestPermissions(getActivity(),
                new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE }, PERMISSION_CODE);
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getActivity(), "Write External Storage Permission Granted!", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private List<GalleryItem> prepareData() {
        List<GalleryItem> imageList = new ArrayList<>();
        File[] files = getGalleryDir().listFiles();
        for (File f : files) {
            GalleryItem item = new GalleryItem(f.getAbsolutePath());
            imageList.add(item);
            Log.i(TAG, "Added File => " + item.getResourceString());
        }
        return imageList;
    }

    public List<GalleryItemView> getSelectedItems() {
        return mSelectedItems;
    }

    public void clearSelectedItems() {
        for (GalleryItemView item : mSelectedItems) {
            item.clear();
        }
        mSelectedItems.clear();
    }

    public void delete(View view) {
        RelativeLayout layout = (RelativeLayout)view.getParent();
        GalleryItemView itemView = (GalleryItemView)layout.getChildAt(0);
        MyAdapter adapter = (MyAdapter)mRecyclerView.getAdapter();
        String resourceStr = itemView.getResourceString();

        File f = new File(resourceStr);
        if (!f.delete()) {
            Log.i(TAG, "Failed to delete file!");
        }

        layout.animate().alpha(0).setDuration(500).withEndAction(() -> {
            itemView.clear();
            itemView.setVisibility(GONE);
            view.setVisibility(GONE);
            adapter.notifyRemoved(itemView.getItem());
        }).start();
    }

    public void showDirectorySpinner() {
        mDirectorySpinner.setVisibility(VISIBLE);
        mDirectorySpinner.performClick();
    }


    private class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
        private Context mContext;
        private List<GalleryItem> mGalleryList;
//        private List<String> mGalleryList;

        public MyAdapter(Context ctx, List<GalleryItem> galleryList) {
            mContext = ctx;
            mGalleryList = galleryList;
        }

        @Override
        public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_layout, parent, false);
            return new ViewHolder(view);
        }

        public void notifyRemoved(GalleryItem item) {
            int index = mGalleryList.indexOf(item);
            super.notifyItemRemoved(index);
            mGalleryList.remove(index);

        	if (getItemCount() == 0) {
                Toast.makeText(getActivity(), "You deleted everything", Toast.LENGTH_LONG).show();
            }
        }

        public void notifyChange(String item) {
            mGalleryList.remove(item);
            notifyDataSetChanged();

            if (getItemCount() == 0) {
                Toast.makeText(getActivity(), "You deleted everything", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onBindViewHolder(MyAdapter.ViewHolder holder, int index) {
            GalleryItem galleryItem = mGalleryList.get(index);
            GalleryItemView itemView = holder.mImgView;
            Log.i(TAG, galleryItem.toString());

            // load image
            Glide.with(getActivity())
                    .load(galleryItem.getResourceString())
                    .placeholder(R.drawable.ic_loading)
                    .fitCenter()
                    .into(itemView);

            itemView.setGalleryItem(galleryItem);
            itemView.setOnClickListener(mButtonClickHandler);
            itemView.setOnLongClickListener(mButtonClickHandler);
            itemView.refreshView();
            // i don't know if i need this anymore
            itemView.setResourceString(galleryItem.getResourceString());
        }

        public boolean remove(String item) {
            return mGalleryList.remove(item);
        }

        @Override
        public int getItemCount() {
            return mGalleryList.size();
        }

        public int indexOf(String resourceString) {
        	return mGalleryList.indexOf(resourceString);
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private GalleryItemView mImgView;

            public ViewHolder(View view) {
                super(view);
                mImgView = view.findViewById(R.id.img);
                mImgView.mDeleteButton = view.findViewById(R.id.deleteItemButton);
                mImgView.mDeleteButton.setVisibility(INVISIBLE);
            }
        }
    }

    protected void refreshButtons() {
        mLoadSelectedButton.setEnabled(!mSelectedItems.isEmpty());
    }


    private class DirectoryItemAdapter extends BaseAdapter {
        private List<String> mData;
        private TextView mDirectoryTextView;
        private LayoutInflater mInflater;


        public DirectoryItemAdapter(Context context, List<String> data) {
            mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
            View view = convertView != null ? convertView : mInflater.inflate(R.layout.gallery_list_item, null);
            mDirectoryTextView = view.findViewById(R.id.directoryTextView);
            mDirectoryTextView.setText(mData.get(position));
            mDirectoryTextView.setTextColor(Color.WHITE);
            return view;
        }


        public View getDropDownView(int position, View convertView, ViewGroup viewGroup) {
            View view = getView(position, convertView, viewGroup);
            mDirectoryTextView.setTextColor(Color.BLACK);
            return view;
        }
    }


    private class ButtonClickHandler implements View.OnClickListener, View.OnLongClickListener {

        @Override
        public void onClick(View view) {
            GalleryItemView itemView = (GalleryItemView)view;
            itemView.onClick(view);
            if (itemView.isNormalSelected()) {
                mSelectedItems.add(itemView);
                // enable load button if not already
            } else {
                mSelectedItems.remove(itemView);
            }
            refreshButtons();
        }

        @Override
        public boolean onLongClick(View view) {
            GalleryItemView itemView = (GalleryItemView)view;
            itemView.onLongClick(view);
            if (itemView.isDeleteSelected()) {
                mSelectedItems.remove(itemView);
                // enable delete button if not already
            }
            refreshButtons();
            return true;
        }
    }
}
