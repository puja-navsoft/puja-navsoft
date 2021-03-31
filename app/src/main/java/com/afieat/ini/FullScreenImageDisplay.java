package com.afieat.ini;

import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.afieat.ini.adapters.ImageViewPagerAdapter;
import com.afieat.ini.utils.Apis;
import com.afieat.ini.utils.AppUtils;

import java.util.ArrayList;

public class FullScreenImageDisplay extends AppCompatActivity {
    private Toolbar mToolbar;
    ArrayList<String> ImagesList;
    ViewPager ViewPAGE;
    String mResName;
    TextView TXTCount;
    ImageViewPagerAdapter PagerAdapter;
    String[] Images;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image_display);
        mToolbar = (Toolbar) findViewById(R.id.appbar);
        mToolbar.setTitle("");
        mToolbar.setTitleTextColor(ContextCompat.getColor(getApplicationContext(), R.color.greyXLight));
        mToolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        ViewPAGE = (ViewPager) findViewById(R.id.ViewPAGE);
//        TXTCount = (TextView) findViewById(R.id.TXTCount);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        ImagesList = new ArrayList<>();

        switch (getIntent().getStringExtra("FromPage")) {
            case "MYREVIEW":
                Images = getIntent().getStringArrayExtra("Images");
                for (String url : Images) {
                    ImagesList.add("https://d22vvrqrexhw5s.cloudfront.net/upload/review/thumb_300_300/" + url);
                }
                SetPageViewer();
                break;

            case "FOODBUSKET": {
                Images = getIntent().getStringArrayExtra("Images");
                for (String url : Images) {
                    ImagesList.add(Apis.IMG_PATH + "items/gallery/" + url);
                }
                SetPageViewer();
                break;
            }
            case "REST_INFO":

                Images = getIntent().getStringArrayExtra("Images");
                for (String url : Images) {
                    ImagesList.add(Apis.IMG_PATH + "restaurants/gallery/" + url);
                }
                SetPageViewer();
                break;

            default:
                break;}


    }

    private void SetPageViewer() {
        PagerAdapter = new ImageViewPagerAdapter(this, ImagesList);
        ViewPAGE.setAdapter(PagerAdapter);
        ViewPAGE.setCurrentItem(getIntent().getIntExtra("SELECTITEM", 0));
        // mToolbar.setTitle((getIntent().getIntExtra("SELECTITEM", 0)+1)+"/"+ImagesList.size());
        mToolbar.setTitle(AppUtils.CURRENT_RESTAURANT_NAME + " Photos");
        ViewPAGE.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                //   mToolbar.setTitle((position+1)+"/"+ImagesList.size());
                mToolbar.setTitle(AppUtils.CURRENT_RESTAURANT_NAME + " Photos");
            }

            //
            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return false;
    }


}
