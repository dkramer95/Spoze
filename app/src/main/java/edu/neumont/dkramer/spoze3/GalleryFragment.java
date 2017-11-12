package edu.neumont.dkramer.spoze3;

import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;

import static android.widget.ImageView.ScaleType.FIT_CENTER;

/**
 * Created by dkramer on 11/11/17.
 */

public class GalleryFragment extends DialogFragment {
    protected RecyclerView mRecyclerView;

    static int[] imageIds =
    {
        R.drawable.banner_texture,
        R.drawable.decal_texture,
        R.drawable.logo_texture,
        R.drawable.texture7,
        R.drawable.texture_3,
        R.drawable.texture_4,
        R.drawable.vim_texture,
    };


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.gallery_layout, container, false);
        mRecyclerView = view.findViewById(R.id.imagegallery);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);
        mRecyclerView.setLayoutManager(layoutManager);
        ArrayList<Integer> createLists = prepareData();
        MyAdapter adapter = new MyAdapter(getActivity(), createLists);
        mRecyclerView.setAdapter(adapter);
        return view;
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
}
