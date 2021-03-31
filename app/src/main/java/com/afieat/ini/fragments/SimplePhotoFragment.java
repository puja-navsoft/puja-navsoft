package com.afieat.ini.fragments;


import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.afieat.ini.R;
import com.bumptech.glide.Glide;

/**
 * A simple {@link Fragment} subclass.
 */
public class SimplePhotoFragment extends Fragment {

    private ImageView ivImage;
    private Uri path;

    public SimplePhotoFragment() {
        // Required empty public constructor
    }

    public static Fragment newInstance(Uri logoPath) {
        SimplePhotoFragment fragment = new SimplePhotoFragment();
        fragment.path = logoPath;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_simple_photo, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ivImage = (ImageView) view.findViewById(R.id.ivImage);
        //      ivImage.setImageURI(path);

        Glide
                .with(getContext())
                .load(path)
                .placeholder(R.drawable.placeholder_land)
                .into(ivImage);
    }
}
