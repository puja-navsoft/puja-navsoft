package com.afieat.ini;

import android.content.Intent;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.afieat.ini.adapters.ReviewImageAdapter;
import com.afieat.ini.utils.Apis;
import com.afieat.ini.utils.AppInstance;
import com.afieat.ini.utils.AppUtils;
import com.afieat.ini.utils.K_ApiCall;
import com.afieat.ini.utils.NetworkRequest;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.vlk.multimager.activities.GalleryActivity;
import com.vlk.multimager.utils.Constants;
import com.vlk.multimager.utils.Image;
import com.vlk.multimager.utils.Params;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class
CustomerReviewActivity extends AppCompatActivity {

    private  RatingBar Rating;
    private RecyclerView recyclerView;
    private TextView BTN_Submit;
    private EditText description;
    private String RESID;
    private ReviewImageAdapter.OnImageAction onImageAction;
    private ProgressBar progressBar;
    private ReviewImageAdapter reviewImageAdapter;
    private ArrayList<Image> imagesList;
    public static int MODE;
    public static String REVIEWID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         Toolbar mToolbar;
        setContentView(R.layout.activity_customer_review);
        mToolbar = (Toolbar) findViewById(R.id.appbar);
        Rating = (RatingBar) findViewById(R.id.ratingAvg);
        BTN_Submit = (TextView) findViewById(R.id.BTN_Submit);
        description = (EditText) findViewById(R.id.description);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        mToolbar.setTitle(getIntent().getStringExtra("resname"));
        mToolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        mToolbar.setTitleTextColor(ContextCompat.getColor(getApplicationContext(), R.color.greyXLight));
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
        Rating.setNumStars(5);
        imagesList = new ArrayList<>();
        assert mToolbar != null;

        onImageAction = new ReviewImageAdapter.OnImageAction() {
            @Override
            public void OnImageDelete(String ImageName, final int Position) {
                AppUtils.showViews(progressBar);
                Map<String, String> params = new HashMap<>();
                params.put("review_id", REVIEWID);
                params.put("review_image", ImageName);
                Log.d("@@ URL- ", Apis.DELETE_REVIEWIMAGE);
                Log.d("@@ IMAGENAME- ", ImageName);
                NetworkRequest request = new NetworkRequest(Request.Method.POST, Apis.DELETE_REVIEWIMAGE, params, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("@@ k_DeleteImage- ", response + "");
                        AppUtils.hideViews(progressBar);
                        try {
                            if (response.getInt("code") == 1) {
                                imagesList.remove(Position);
                                reviewImageAdapter.notifyDataSetChanged();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        AppUtils.hideViews(progressBar);
                    }
                });
                AppInstance.getInstance(getApplicationContext()).addToRequestQueue(request);
            }
        };

        RESID = getIntent().getStringExtra("resid");
        MODE = getIntent().getIntExtra("purpose", 1);
        if (MODE == 2) {

            Rating.setRating(Float.parseFloat(getIntent().getStringExtra("rev_rating")));
            description.setText(getIntent().getStringExtra("revewtext"));
            String Images = getIntent().getStringExtra("rev_images");
            if (!"".equalsIgnoreCase(Images)) {
                REVIEWID = getIntent().getStringExtra("rev_id");
                String[] images = Images.split(",");
                for (String url : images) {
                    Image image = new Image(0, null,
                            url, true);
                    imagesList.add(image);
                }
                reviewImageAdapter = new ReviewImageAdapter(imagesList, CustomerReviewActivity.this, onImageAction);
                recyclerView.setAdapter(reviewImageAdapter);
            }

        } else {
        }


        BTN_Submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppUtils.showViews(progressBar);

                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("user_id", AppInstance.getInstance(getApplicationContext()).getFromSharedPref(AppUtils.PREF_USER_ID));
                    jsonObject.put("resid", RESID);
                    jsonObject.put("ratecount", (int) Rating.getRating() + "");
                    jsonObject.put("review", description.getText());
                } catch (JSONException e) {
                    e.printStackTrace();
                    AppUtils.hideViews(progressBar);
                }


                try {
                    new K_ApiCall().PhotoUplaodAPICALL(Apis.POST_REVIEW, jsonObject, imagesList, new K_ApiCall.APICall() {
                        @Override
                        public void Onsuccess() {
                            AppUtils.hideViews(progressBar);
                            onBackPressed();
                        }

                        @Override
                        public void OnError(String ex) {
                            AppUtils.hideViews(progressBar);
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                    AppUtils.hideViews(progressBar);
                } finally {
                }

            }
        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_addreview, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.menu_addImage:
                initiateMultiPicker();
                break;
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case Constants.TYPE_MULTI_CAPTURE:
                handleResponseIntent(intent);
                break;
            case Constants.TYPE_MULTI_PICKER:
                handleResponseIntent(intent);
                break;
        }
    }

    private void initiateMultiPicker() {
        Intent intent = new Intent(this, GalleryActivity.class);
        Params params = new Params();
        params.setCaptureLimit(6);
        params.setPickerLimit(6);
        params.setToolbarColor(R.color.colorPrimary);
        params.setActionButtonColor(R.color.colorPrimary);
        params.setButtonTextColor(R.color.colorPrimary);
        intent.putExtra(Constants.KEY_PARAMS, params);
        startActivityForResult(intent, Constants.TYPE_MULTI_PICKER);
    }

    private void handleResponseIntent(Intent intent) {
        ArrayList<Image> imagesList_tem = intent.getParcelableArrayListExtra(Constants.KEY_BUNDLE_LIST);
        imagesList.addAll(imagesList_tem);
        reviewImageAdapter = new ReviewImageAdapter(imagesList, CustomerReviewActivity.this, onImageAction);
        recyclerView.setAdapter(reviewImageAdapter);
    }
}
