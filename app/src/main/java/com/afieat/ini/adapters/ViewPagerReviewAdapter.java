package com.afieat.ini.adapters;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.afieat.ini.fragments.review.DeliveryReviewFragment;
import com.afieat.ini.fragments.review.FoodReviewFragment;
import com.afieat.ini.fragments.review.RestuarantReviewFragment;

import java.util.ArrayList;
import java.util.List;

public class ViewPagerReviewAdapter extends FragmentPagerAdapter {

    private List<Fragment> reviewFragList=new ArrayList<>();
    private List<String> reviewTitleList=new ArrayList<>();
    private ViewPager viewPager;

    public ViewPagerReviewAdapter(FragmentManager fm, ViewPager viewPager) {

        super(fm);
        this.viewPager=viewPager;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                // Top Rated fragment activity
                return new FoodReviewFragment(viewPager);
            case 1:
                // Games fragment activity
                return new RestuarantReviewFragment(viewPager);
            case 2:
                // Movies fragment activity
                return new DeliveryReviewFragment(viewPager);
        }
        return reviewFragList.get(position);
    }

    @Override
    public int getCount() {
        return 3;
    }

    public void addFragment(Fragment fragment, String title) {
        reviewFragList.add(fragment);
        reviewTitleList.add(title);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return reviewTitleList.get(position);
    }
}
