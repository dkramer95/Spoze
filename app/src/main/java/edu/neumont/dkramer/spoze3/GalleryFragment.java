package edu.neumont.dkramer.spoze3;

import android.app.DialogFragment;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static android.content.res.Configuration.ORIENTATION_LANDSCAPE;
import static android.content.res.Configuration.ORIENTATION_PORTRAIT;

/**
 * Created by dkramer on 11/11/17.
 */

public class GalleryFragment extends DialogFragment {
    private static final String TAG = "Gallery Fragment";
    private static final String SPOZE_DIRECTORY = "SpozeGallery";

    protected RecyclerView mRecyclerView;
    protected ButtonClickHandler mButtonClickHandler;
    protected Button mLoadSelectedButton;
    protected Button mDeleteSelectedButton;

    protected List<GalleryItemView> mNormalSelected;
    protected List<GalleryItemView> mDeleteSelected;


//    static int[] imageIds =
//    {
//        R.drawable.banner_texture,
//        R.drawable.decal_texture,
//        R.drawable.logo_texture,
//        R.drawable.texture7,
//        R.drawable.texture_3,
//        R.drawable.texture_4,
//        R.drawable.vim_texture,
//        R.drawable.texture10,
//    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ensureCanWriteToExternalStorage();
    }

    protected void ensureCanWriteToExternalStorage() {
        if (isExternalStorageWritable()) {
            File dir = getGalleryDir();
            dir.mkdirs();
            if (!dir.exists()) {
                Toast.makeText(getActivity(), "Spoze Gallery Missing!", Toast.LENGTH_LONG).show();
            }
        }
    }

    protected File getGalleryDir() {
        return new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                + File.separator + SPOZE_DIRECTORY);
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

        initButtons(view);
        initGalleryView();

        return view;
    }

    protected void initGalleryView() {
        List<String> createLists = prepareData();
        MyAdapter adapter = new MyAdapter(getActivity(), createLists);
        mRecyclerView.setAdapter(adapter);
    }

    protected void initButtons(View view) {
        // Button stuff
        mLoadSelectedButton = view.findViewById(R.id.loadSelectedButton);
        mDeleteSelectedButton = view.findViewById(R.id.deleteSelectedButton);
        mButtonClickHandler = new ButtonClickHandler();

        mNormalSelected = new ArrayList<>();
        mDeleteSelected = new ArrayList<>();
        refreshButtons();
    }

    @Override
    public void onStart() {
        super.onStart();
        // stop progress bar
//        getActivity().findViewById(R.id.galleryLoadProgressBar).setVisibility(View.GONE);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        updateLayoutManager(newConfig.orientation);
    }

    public void deleteSelected() {
        MyAdapter adapter = (MyAdapter)mRecyclerView.getAdapter();
        for (GalleryItemView g : mDeleteSelected) {
            g.setOnClickListener(null);
        }

        Iterator<GalleryItemView> itemIterator = mDeleteSelected.iterator();
        while (itemIterator.hasNext()) {
            GalleryItemView g = itemIterator.next();
            String resStr = g.getResourceString();

            File f = new File(resStr);
            if (f.exists()) {
                f.delete();
            }
            g.onDelete();
            adapter.remove(resStr);
            g.animate().alpha(0).setDuration(500).withEndAction(() -> {
                adapter.notifyDataSetChanged();
            });
            itemIterator.remove();
        }

        mDeleteSelectedButton.setEnabled(false);
    }

    protected void updateLayoutManager(int orientation) {
        if (orientation == ORIENTATION_PORTRAIT) {
            mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        } else if (orientation == ORIENTATION_LANDSCAPE) {
            mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 4));
        }
    }

    private List<String> prepareData() {
        List<String> imageList = new ArrayList<>();
        File[] files = getGalleryDir().listFiles();
        for (File f : files) {
            imageList.add(f.getAbsolutePath());
            Log.i(TAG, "Added File => " + f);
        }
        return imageList;
    }

    public List<GalleryItemView> getSelectedItems() {
        return mNormalSelected;
    }

    public void clearSelectedItems() {
        for (GalleryItemView item : mNormalSelected) {
            item.clear();
        }
        mNormalSelected.clear();
    }


    private class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
        private Context mContext;
        private List<String> mGalleryList;

        public MyAdapter(Context ctx, List<String> galleryList) {
            mContext = ctx;
            mGalleryList = galleryList;
        }

        @Override
        public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_layout, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(MyAdapter.ViewHolder holder, int index) {
            String filePath = mGalleryList.get(index);
            GalleryItemView galleryItem = holder.mImgView;

            Glide.with(getActivity())
                    .load(filePath)
                    .placeholder(R.drawable.ic_launcher_background)
                    .fitCenter()
                    .into(galleryItem);

            galleryItem.setOnClickListener(mButtonClickHandler);
            galleryItem.setOnLongClickListener(mButtonClickHandler);
            galleryItem.setResourceString(filePath);
        }

        public boolean remove(String item) {
            return mGalleryList.remove(item);
        }

        @Override
        public int getItemCount() {
            return mGalleryList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private GalleryItemView mImgView;

            public ViewHolder(View view) {
                super(view);
                mImgView = view.findViewById(R.id.img);
            }
        }
    }

    protected void refreshButtons() {
        mLoadSelectedButton.setEnabled(!mNormalSelected.isEmpty());
        mDeleteSelectedButton.setEnabled(!mDeleteSelected.isEmpty());
    }



    private class ButtonClickHandler implements View.OnClickListener, View.OnLongClickListener {

        @Override
        public void onClick(View view) {
            GalleryItemView itemView = (GalleryItemView)view;
            itemView.onClick(view);
            if (itemView.isNormalSelected()) {
                mNormalSelected.add(itemView);
                mDeleteSelected.remove(itemView);
                // enable load button if not already
            } else {
                mNormalSelected.remove(itemView);
            }
            refreshButtons();
        }

        @Override
        public boolean onLongClick(View view) {
            GalleryItemView itemView = (GalleryItemView)view;
            itemView.onLongClick(view);
            if (itemView.isDeleteSelected()) {
                mNormalSelected.remove(itemView);
                mDeleteSelected.add(itemView);
                // enable delete button if not already
            } else {
                mDeleteSelected.remove(itemView);
            }
            refreshButtons();
            return true;
        }
    }
}
