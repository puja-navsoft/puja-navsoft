package com.afieat.ini.fragments;


import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.afieat.ini.R;
import com.afieat.ini.interfaces.PhotoSlideListener;
import com.afieat.ini.utils.AppUtils;
import com.bumptech.glide.Glide;

/**
 * A simple {@link Fragment} subclass.
 */
public class PhotoSlideFragment extends Fragment {

    private ImageView ivImage;
    private ImageView ivLeft, ivRight;


    private Fragment parentFrag;
    private String imageUri;
    private int index, size;

    public PhotoSlideFragment() {
        // Required empty public constructor
    }

    public static PhotoSlideFragment newInstance(String imageUri, Fragment parentFrag, int index, int size) {
        PhotoSlideFragment fragment = new PhotoSlideFragment();
        fragment.imageUri = imageUri;
        fragment.parentFrag = parentFrag;
        fragment.index = index + 1;
        fragment.size = size;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_photo_slide, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ivImage = (ImageView) view.findViewById(R.id.ivImage);
        ivLeft = (ImageView) view.findViewById(R.id.ivLeft);
        ivRight = (ImageView) view.findViewById(R.id.ivRight);

        TextView tvIndex;
        tvIndex = (TextView) view.findViewById(R.id.tvIndex);
        loadImageFromUri();
    }

    private void loadImageFromUri() {
        AppUtils.log("-> " + imageUri);
        if (imageUri.contains("88_88")) imageUri = imageUri.replace("88_88", "300_300");
        if (imageUri.contains("68_68")) imageUri = imageUri.replace("68_68", "300_300");
        //     ivImage.setImageURI(Uri.parse(imageUri));

        Glide
                .with(getContext())
                .load(Uri.parse(imageUri))
                .placeholder(R.drawable.placeholder_land)
                .into(ivImage);
        ivLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((PhotoSlideListener) parentFrag).onSlideLeft();
            }
        });
        ivRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((PhotoSlideListener) parentFrag).onSlideRight();
            }
        });
        //    tvIndex.setText(index + " / " + size);
    }
}
