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
public class RestuarantReviewFragment extends Fragment {

    private TextView btRestReviewSubmit,btRestReviewPrevious;
    private ViewPager viewPager;

    @SuppressLint("ValidFragment")
    public  RestuarantReviewFragment (ViewPager viewPager){
        this.viewPager=viewPager;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_restdelivery_review, container, false);



        btRestReviewSubmit=view.findViewById(R.id.bt_restReviewSubmit);
        //viewPager=view.findViewById(R.id.tab_review);

        btRestReviewSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(2);
            }
        });

        btRestReviewPrevious=view.findViewById(R.id.bt_restReviewPrevious);
        btRestReviewPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(0);
            }
        });

        return view;
    }
}
