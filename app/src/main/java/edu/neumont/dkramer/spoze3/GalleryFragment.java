package edu.neumont.dkramer.spoze3;

import android.app.DialogFragment;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import static android.content.res.Configuration.ORIENTATION_LANDSCAPE;
import static android.content.res.Configuration.ORIENTATION_PORTRAIT;
import static android.widget.ImageView.ScaleType.FIT_CENTER;

/**
 * Created by dkramer on 11/11/17.
 */

public class GalleryFragment extends DialogFragment {
    protected RecyclerView mRecyclerView;
    protected ButtonClickHandler mButtonClickHandler;
    protected Button mLoadSelectedButton;
    protected Button mDeleteSelectedButton;

    protected List<GalleryItemView> mNormalSelected;
    protected List<GalleryItemView> mDeleteSelected;


    static int[] imageIds =
    {
        R.drawable.banner_texture,
        R.drawable.decal_texture,
        R.drawable.logo_texture,
        R.drawable.texture7,
        R.drawable.texture_3,
        R.drawable.texture_4,
        R.drawable.vim_texture,
        R.drawable.texture10,
    };


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.gallery_fragment_layout, container, false);
        mRecyclerView = view.findViewById(R.id.imagegallery);
        updateLayoutManager(getActivity().getResources().getConfiguration().orientation);

        // Button stuff
        mLoadSelectedButton = view.findViewById(R.id.loadSelectedButton);
        mDeleteSelectedButton = view.findViewById(R.id.deleteSelectedButton);
        mButtonClickHandler = new ButtonClickHandler();

        mNormalSelected = new ArrayList<>();
        mDeleteSelected = new ArrayList<>();
        refreshButtons();

        ArrayList<Integer> createLists = prepareData();
        MyAdapter adapter = new MyAdapter(getActivity(), createLists);
        mRecyclerView.setAdapter(adapter);
        return view;
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

    private ArrayList<Integer> prepareData() {
        ArrayList<Integer> imageList = new ArrayList<>();
        for (int j = 0; j < imageIds.length; ++j) {
            imageList.add(imageIds[j]);
        }
        return imageList;
    }



    private class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
        private Context mContext;
        private ArrayList<Integer> mGalleryList;

        public MyAdapter(Context ctx, ArrayList<Integer> galleryList) {
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
            holder.mImgView.setScaleType(FIT_CENTER);
            holder.mImgView.setImageResource(mGalleryList.get(index));
            holder.mImgView.setOnClickListener(mButtonClickHandler);
            holder.mImgView.setOnLongClickListener(mButtonClickHandler);
        }

        @Override
        public int getItemCount() {
            return mGalleryList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private ImageView mImgView;

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
