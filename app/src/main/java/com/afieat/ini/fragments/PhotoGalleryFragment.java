package com.afieat.ini.fragments;


import android.os.Bundle;
import android.os.Handler;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.afieat.ini.R;
import com.afieat.ini.adapters.PhotoGalleryAdapter;
import com.afieat.ini.interfaces.PhotoSlideListener;
import com.afieat.ini.utils.AppUtils;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class PhotoGalleryFragment extends Fragment implements PhotoSlideListener {

    private List<String> photos;
    private PhotoSlideFragment fragment;
    private FragmentManager mFragmentManager;
    private int containerId;

    private int mIndex = 0;

   /* public PhotoGalleryFragment() {
        // Required empty public constructor
    }*/

    public static PhotoGalleryFragment newInstance(List<String> photos, int containerId) {
        PhotoGalleryFragment fragment = new PhotoGalleryFragment();
        fragment.photos = photos;
        fragment.containerId = containerId;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFragmentManager = getActivity().getSupportFragmentManager();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_photo_gallery, container, false);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                GridView gridPhotos = (GridView) view.findViewById(R.id.gridPhotos);
                gridPhotos.setAdapter(new PhotoGalleryAdapter(photos));
                gridPhotos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        mIndex = position;
                        String imageUri = photos.get(position);
                        AppUtils.log("IMG: " + imageUri);
                        loadPhotoSlideFragment(imageUri);
                    }
                });
            }
        }, 500);
    }

    private void loadPhotoSlideFragment(String imageUri) {
        fragment = PhotoSlideFragment.newInstance(imageUri, PhotoGalleryFragment.this, mIndex, photos.size());
        mFragmentManager
                .beginTransaction()
                .add(containerId, fragment, null)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onSlideLeft() {
        if (mIndex > 0) {
            getActivity().onBackPressed();
            loadPhotoSlideFragment(photos.get(--mIndex));
        }
    }

    @Override
    public void onSlideRight() {
        if (mIndex < (photos.size() - 1)) {
            getActivity().onBackPressed();
            loadPhotoSlideFragment(photos.get(++mIndex));
        }
    }
}
