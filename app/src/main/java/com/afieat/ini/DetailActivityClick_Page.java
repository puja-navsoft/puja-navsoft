package com.afieat.ini;

import android.app.Dialog;
import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.afieat.ini.fragments.RestaurantGalleryFragment;
import com.afieat.ini.fragments.RestaurantOrderItemsFragment;
import com.afieat.ini.fragments.RestaurantReviewFragment;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.Objects;

public class DetailActivityClick_Page extends AppCompatActivity {


    private Fragment targetFragment;
    private FragmentManager fragmentManager;
    private String minPrice;
    private int sortType = 0;
    public String mRestaurantName;
    private ShimmerFrameLayout mShimmerViewContainer;
    private View view;
    private ImageView sort;
    private String res_id;
    private String page_to_call;
    private Toolbar mToolbar;
    private TextView titleTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_click__page);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        sort = findViewById(R.id.sortBtn);
        titleTxt = findViewById(R.id.titleTxt);
//        mToolbar.setTitle(getString(R.string.title_menus));
        mToolbar.setTitle(" ");
        mToolbar.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
        mToolbar.setTitleTextColor(ContextCompat.getColor(getApplicationContext(), R.color.greyXLight));
        // GroupID = getIntent().getStringExtra(AppUtils.EXTRA_GROUP_ID) == null ? "" : getIntent().getStringExtra(AppUtils.EXTRA_GROUP_ID);
        minPrice = getIntent().getStringExtra("MinPrice");
        //ImageTransitionEffect();

//        mToolbar.getBackground().setAlpha(0);
        setSupportActionBar(mToolbar);

        mShimmerViewContainer = findViewById(R.id.shimmer_view_container);
        mShimmerViewContainer.startShimmerAnimation();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        res_id = getIntent().getExtras().getString("res_id");
        page_to_call = getIntent().getExtras().getString("page_to_call");
        mRestaurantName = getIntent().getExtras().getString("mRestaurantName");

        typeToView(false);

//        sort.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                showSortMenu();
//            }
//        });


        mShimmerViewContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                RestaurantOrderItemsFragment mRestaurantOrderItemsFragment = (RestaurantOrderItemsFragment) targetFragment;
                mRestaurantOrderItemsFragment.showSearchDialog();
            }
        });
        /*final int[] backGround = {R.drawable.bg_restaurant,R.drawable.green_bg,R.drawable.rect_bg_green};

        new CountDownTimer(30000, 1000) {

            public void onTick(long millisUntilFinished) {
              //  mTextField.setText("seconds remaining: " + millisUntilFinished / 1000);
                int randomStr = backGround[new Random().nextInt(backGround.length)];
                mShimmerViewContainer.setBackgroundResource(randomStr);
            }

            public void onFinish() {
                //mTextField.setText("done!");
            }
        }.start();*/

    }

    private void typeToView(Boolean fromAlert){

        if ("1".equalsIgnoreCase(page_to_call)) {
            fragmentManager = getSupportFragmentManager();
            targetFragment = RestaurantGalleryFragment.newInstance(res_id, mRestaurantName);
            mToolbar.setTitle(mRestaurantName);
            titleTxt.setText(mRestaurantName);
            if (!fromAlert)
                emigrateTo(targetFragment);
            else
                replaceTo(targetFragment);
            mShimmerViewContainer.setVisibility(View.GONE);
            findViewById(R.id.linear1).setVisibility(View.GONE);
            findViewById(R.id.linear2).setVisibility(View.GONE);

        } else if ("2".equalsIgnoreCase(page_to_call)) {
            fragmentManager = getSupportFragmentManager();
            targetFragment = RestaurantReviewFragment.newInstance(res_id);
            mToolbar.setTitle(mRestaurantName);
            titleTxt.setText(mRestaurantName);
            if (!fromAlert)
                emigrateTo(targetFragment);
            else
                replaceTo(targetFragment);
            mShimmerViewContainer.setVisibility(View.GONE);
            findViewById(R.id.linear1).setVisibility(View.GONE);
            findViewById(R.id.linear2).setVisibility(View.GONE);
        } else if ("3".equalsIgnoreCase(page_to_call)) {
            fragmentManager = getSupportFragmentManager();
            targetFragment = RestaurantOrderItemsFragment.newInstance(res_id, minPrice);
            mToolbar.setTitle(mRestaurantName);
            titleTxt.setText(mRestaurantName);
            if (!fromAlert)
                emigrateTo(targetFragment);
            else
                replaceTo(targetFragment);
        }

    }

    private void showSortMenu() {

        // custom dialog
//        final Dialog dialog = new Dialog(this);

//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        view = inflater.inflate(R.layout.dialog_sort, null, false);
//
//        ((DetailActivityClick_Page) getApplicationContext()).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
//        dialog.setContentView(view);
//        final Window window = dialog.getWindow();
//        window.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
//        window.setBackgroundDrawableResource(R.color.white);
//        window.setGravity(Gravity.CENTER_HORIZONTAL);

        //before inflating the custom alert dialog layout, we will get the current activity viewgroup
        ViewGroup viewGroup = findViewById(android.R.id.content);

        //then we will inflate the custom alert dialog xml that we created
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_sort, viewGroup, false);


        //Now we need an AlertDialog.Builder object
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        //setting the view of the builder to our custom view that we already inflated
        builder.setView(dialogView);
        final AlertDialog alertDialog = builder.create();

        // dialog.setContentView(R.layout.filter_viewtype_dialog);
        // dialog.setTitle("Title...");

        // set the custom dialog components - text, image and button

        final RadioButton image =  dialogView.findViewById(R.id.imageRb);
        Button apply = dialogView.findViewById(R.id.applyBtn);

        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (image.isChecked()) {
                    Toast.makeText(DetailActivityClick_Page.this, "Sorted as image", Toast.LENGTH_SHORT).show();
                    sortType = 1;
                    typeToView(true);
                    alertDialog.dismiss();
                }

            }
        });


        alertDialog.show();
    }


    private void emigrateTo(Fragment targetFragment) {


        fragmentManager
                .beginTransaction()
                .setCustomAnimations(R.anim.entry_in, R.anim.entry_out, R.anim.exit_in, R.anim.exit_out)
                .add(R.id.frameLayout, targetFragment)
                .addToBackStack(null)
                .commit();

        /*findViewById(R.id.homeContentRel).setVisibility(View.GONE);
        findViewById(R.id.flContainer).setVisibility(View.VISIBLE);*/

    }

    private void replaceTo(Fragment targetFragment) {


        Fragment fragment = getSupportFragmentManager().findFragmentByTag("RestaurantOrderItemsFragment");
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction().remove(fragment).commit();
        }
        if (targetFragment.isAdded()) { // if the fragment is already in container
            FragmentTransaction ft = fragmentManager.beginTransaction().setCustomAnimations(R.anim.entry_in, R.anim.entry_out, R.anim.exit_in, R.anim.exit_out);
            ft.replace(R.id.frameLayout, targetFragment).addToBackStack(null);
            ft.commit();
            ft.show(targetFragment);
        } else { // fragment needs to be added to frame container
            FragmentTransaction ft = fragmentManager.beginTransaction().setCustomAnimations(R.anim.entry_in, R.anim.entry_out, R.anim.exit_in, R.anim.exit_out);
            ft.add(R.id.frameLayout, targetFragment).addToBackStack(null);
            ft.commit();
            ft.show(targetFragment);
        }

//        fragmentManager
//                .beginTransaction()
//                .setCustomAnimations(R.anim.entry_in, R.anim.entry_out, R.anim.exit_in, R.anim.exit_out)
//                .replace(R.id.frameLayout, targetFragment)
//                .addToBackStack(null)
//                .commit();

        /*findViewById(R.id.homeContentRel).setVisibility(View.GONE);
        findViewById(R.id.flContainer).setVisibility(View.VISIBLE);*/

    }

    @Override
    public void onBackPressed() {


        if (fragmentManager.getBackStackEntryCount() > 1) {
            fragmentManager.popBackStack();
        } else {

            supportFinishAfterTransition();
            overridePendingTransition(R.anim.exit_in, R.anim.exit_out);
            //  finishAfterTransition();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // getMenuInflater().inflate(R.menu.view_online_search_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.search:
                RestaurantOrderItemsFragment mRestaurantOrderItemsFragment = (RestaurantOrderItemsFragment) targetFragment;
                mRestaurantOrderItemsFragment.showSearchDialog();
                // startActivity(new Intent(DetailActivityClick_Page.this,ViewOnlineSearchActivity.class));
                // mRestaurantOrderItemsFragment.tabStrip.setVisibility(View.GONE);

                break;
            case android.R.id.home:
                onBackPressed();

                return true;
            default:
                break;   }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();

        System.out.println("nfknsknfnfsnd : DetailActivityClick_Page : onResume");
    }
}
