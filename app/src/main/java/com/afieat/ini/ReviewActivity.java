package com.afieat.ini;

import com.google.android.material.tabs.TabLayout;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import com.afieat.ini.adapters.ViewPagerReviewAdapter;
import com.afieat.ini.fragments.review.DeliveryReviewFragment;
import com.afieat.ini.fragments.review.FoodReviewFragment;
import com.afieat.ini.fragments.review.RestuarantReviewFragment;

public class ReviewActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private TabLayout tabReview;
    private ViewPager viewpagerReview;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_review);
        //Toolbar for Delivery Process review
        mToolbar = (Toolbar) findViewById(R.id.appbar);
        //Only in English , Arabic content not added and also not added in String.xml
        mToolbar.setTitle("Please give us Review");
        mToolbar.setTitleTextColor(ContextCompat.getColor(getApplicationContext(), R.color.greyXLight));
        mToolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        viewpagerReview = (ViewPager) findViewById(R.id.viewpager_review);
        viewpagerReview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        setupViewPager(viewpagerReview);



        tabReview = (TabLayout) findViewById(R.id.tab_review);
        tabReview.setupWithViewPager(viewpagerReview);
        //  tabReview.clearOnTabSelectedListeners();

        LinearLayout tabStrip = ((LinearLayout)tabReview.getChildAt(0));
        for(int i = 0; i < tabStrip.getChildCount(); i++) {
            tabStrip.getChildAt(i).setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return true;
                }
            });
        }



    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerReviewAdapter adapter = new ViewPagerReviewAdapter(getSupportFragmentManager(), viewPager);
        adapter.addFragment(new FoodReviewFragment(viewPager), "Food");
        adapter.addFragment(new RestuarantReviewFragment(viewPager), "Restaurant");
        adapter.addFragment(new DeliveryReviewFragment(viewPager), "Delivery");
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);

        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            onBackPressed();
        return super.onOptionsItemSelected(item);
    }

}
