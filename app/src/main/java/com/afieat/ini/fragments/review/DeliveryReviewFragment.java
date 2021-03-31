package com.afieat.ini.fragments.review;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afieat.ini.R;

@SuppressLint("ValidFragment")
public class DeliveryReviewFragment extends Fragment {

    private TextView btDpPrevious;
    private ViewPager viewPager;

    @SuppressLint("ValidFragment")
    public  DeliveryReviewFragment (ViewPager viewPager){
        this.viewPager=viewPager;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view= inflater.inflate(R.layout.fragment_delivery_review, container, false);

        btDpPrevious=view.findViewById(R.id.bt_dpPrevious);
        btDpPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(1);
            }
        });

        return view;
    }
}
