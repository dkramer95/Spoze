package edu.neumont.dkramer.spoze3;

import android.Manifest;
import android.app.DialogFragment;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLSurfaceView;
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
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import edu.neumont.dkramer.spoze3.gl.GLActivity;
import edu.neumont.dkramer.spoze3.gl.GLContext;
import edu.neumont.dkramer.spoze3.gl.GLWorld;
import edu.neumont.dkramer.spoze3.models.SignModel2;

import static android.content.res.Configuration.ORIENTATION_LANDSCAPE;
import static android.content.res.Configuration.ORIENTATION_PORTRAIT;
import static android.view.View.GONE;

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

    protected List<GalleryItemView> mNormalSelected;
//    protected List<GalleryItemView> mDeleteSelected;


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

    protected boolean ensureCanWriteToExternalStorage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!hasGrantedWriteExternalStoragePermission()) {
                Toast.makeText(getContext(), "Please grant permission to write to external storage", Toast.LENGTH_LONG).show();
                requestWriteExternalStoragePermission();
                return false;
            }
        }

        if (isExternalStorageWritable()) {
            File dir = getGalleryDir();
            dir.mkdirs();
            if (!dir.exists()) {
                Toast.makeText(getActivity(), "Spoze Gallery Missing!", Toast.LENGTH_LONG).show();
            }
        }
        return true;
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

        if (ensureCanWriteToExternalStorage()) {
            initButtons(view);
            initGalleryView();
        }

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
//        mLoadSelectedButton.setOnClickListener((v) -> loadSelectedItems());

        mCloseButton = view.findViewById(R.id.closeButton);
//        mCloseButton.setOnClickListener((v) -> hide());

        mButtonClickHandler = new ButtonClickHandler();

        mNormalSelected = new ArrayList<>();
//        mDeleteSelected = new ArrayList<>();
        refreshButtons();
    }

//    protected void loadSelectedItems() {
//    	final GLContext ctx = ((GLActivity)getActivity()).getGLContext();
//    	final GLWorld world = ctx.getGLView().getScene().getWorld();
//        ctx.getGLView().setVisibility(View.VISIBLE);
//        ctx.getGLView().setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
//
//    	for (GalleryItemView item : mNormalSelected) {
//            Bitmap bmp = BitmapFactory.decodeFile(item.getResourceString());
//            ctx.queueEvent(() -> {
//                world.addModel(SignModel2.fromBitmap(ctx, bmp, world.getWidth(), world.getHeight()));
//            });
//        }
//        hide();
//    }

    public void hide() {
        getFragmentManager()
			.beginTransaction()
			.setCustomAnimations(R.animator.fade_in, R.animator.fade_out)
			.hide(this)
			.commit();
    }

    @Override
    public void onStart() {
        super.onStart();
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

    public void delete(View view) {
        RelativeLayout layout = (RelativeLayout)view.getParent();
        MyAdapter adapter = (MyAdapter)mRecyclerView.getAdapter();
        GalleryItemView item = (GalleryItemView) layout.getChildAt(0);

        layout.animate().alpha(0).setDuration(500).withEndAction(() -> {
            adapter.remove(item.getResourceString());
//            adapter.notifyDataSetChanged();
            adapter.notifyChange();
            view.setVisibility(GONE);
        }).start();
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

        public void notifyChange() {
            notifyDataSetChanged();

            if (getItemCount() == 0) {
                Toast.makeText(getActivity(), "You deleted everything", Toast.LENGTH_LONG).show();
            }
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
                mImgView.mDeleteButton = view.findViewById(R.id.deleteItemButton);
                mImgView.mDeleteButton.setVisibility(View.INVISIBLE);
            }
        }
    }

    protected void refreshButtons() {
        mLoadSelectedButton.setEnabled(!mNormalSelected.isEmpty());
//        mDeleteSelectedButton.setEnabled(!mDeleteSelected.isEmpty());
    }



    private class ButtonClickHandler implements View.OnClickListener, View.OnLongClickListener {

        @Override
        public void onClick(View view) {
            GalleryItemView itemView = (GalleryItemView)view;
            itemView.onClick(view);
            if (itemView.isNormalSelected()) {
                mNormalSelected.add(itemView);
//                mDeleteSelected.remove(itemView);
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
//                mDeleteSelected.add(itemView);
                // enable delete button if not already
            } else {
//                mDeleteSelected.remove(itemView);
            }
            refreshButtons();
            return true;
        }
    }
}
