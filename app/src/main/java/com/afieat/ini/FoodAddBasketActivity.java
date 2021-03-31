package com.afieat.ini;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import androidx.fragment.app.FragmentManager;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.afieat.ini.database.DBHelper;
import com.afieat.ini.models.Food;
import com.afieat.ini.utils.Apis;
import com.afieat.ini.utils.AppInstance;
import com.afieat.ini.utils.AppUtils;
import com.afieat.ini.utils.NetworkRequest;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FoodAddBasketActivity extends AppCompatActivity {


    private ScrollView svPage;


    private TextView tvFoodTitle;
    private TextView tvFoodInfo;
    private TextView tvShowMore;

    private RelativeLayout rlGallery;

    private TextView tvPhotos;


    private LinearLayout llPlateSizes;
    private TextView tvSize;

    private LinearLayout llQty;

    private TextView tvQty;

    private TextInputEditText tetComment;

    private LinearLayout llAddOns, llIngredients;


    private TextView tvTotal;

    private View lineGallery, lineSize, lineAddOns, lineQty, lineIngredients;


    private Food mFood;
    private double mBasePrice;
    private double mAddOnPrice;
    private double mIngredientPrice;
    private double mDiscount;
    private int mQty;

    private FragmentManager fragmentManager;
    private List<Ingredient> ingredients;
    private List<Addon> addons;
    private List<Plate> plates;

    public FoodAddBasketActivity() {
        mFood = new Food();
        mBasePrice = mAddOnPrice = mIngredientPrice = mDiscount = 0;
        mQty = 1;
        ingredients = new ArrayList<>();
        addons = new ArrayList<>();
        plates = new ArrayList<>();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        overridePendingTransition(R.anim.entry_in, R.anim.entry_out);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_add_basket);
        Toolbar mToolbar;
        fragmentManager = getSupportFragmentManager();

        svPage = (ScrollView) findViewById(R.id.svPage);

        lineGallery = findViewById(R.id.lineGallery);
        lineAddOns = findViewById(R.id.lineAddOns);
        lineSize = findViewById(R.id.lineSize);
        lineQty = findViewById(R.id.lineQty);
        lineIngredients = findViewById(R.id.lineIngredients);
        tetComment = (TextInputEditText) findViewById(R.id.tetComment);

        AppUtils.hideViews(svPage, lineGallery, lineAddOns, lineSize, lineQty, lineIngredients);

        String mUserId;
        mUserId = AppInstance.getInstance(getApplicationContext()).getFromSharedPref(AppUtils.PREF_USER_ID);
        String foodId;
        String foodName;
        if (((foodId = getIntent().getStringExtra(AppUtils.EXTRA_FOOD_ID)) != null)) {
            mFood.setId(foodId);
            //      mFood.setName(foodName);
        }

        mToolbar = (Toolbar) findViewById(R.id.appbar);
        assert mToolbar != null;
        //  mToolbar.setTitle(mFood.getName().length() > 25 ? mFood.getName().substring(0, 22) + "..." : mFood.getName());
        mToolbar.setTitleTextColor(ContextCompat.getColor(getApplicationContext(), R.color.greyXLight));
        mToolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        setSupportActionBar(mToolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        loadFoodInfoFromNW();
    }

    private void loadFoodInfoFromNW() {
        if (AppUtils.isNetworkAvailable(getApplicationContext())) {
            final ProgressDialog dialog = AppUtils.showProgress(this, "", getString(R.string.msg_please_wait));
            Map<String, String> params = new HashMap<>();
            params.put("item_id", mFood.getId());
            params.put("lang", AppInstance.getInstance(getApplicationContext()).getFromSharedPref(AppUtils.PREF_USER_LANG));
            NetworkRequest request = new NetworkRequest(Request.Method.POST, Apis.FOOD_ITEM_DETAILS, params,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            System.out.println("Rahul : FoodAddBasketActivity : response : " + response);
                            AppUtils.log("@@ K Data-" + response);
                            AppUtils.hideProgress(dialog);
                            JSONObject itemObject = response.optJSONObject("item");
                            JSONArray photosArray = itemObject.optJSONArray("gallery_photo");
                            JSONArray sizeArray = itemObject.optJSONArray("size");
                            JSONArray sizePriceArray = itemObject.optJSONArray("size_price");

                            JSONArray discountedSizePriceArray = itemObject.optJSONArray("discounted_size_price");

                            System.out.println("sizePriceArray : " + sizePriceArray);
                            JSONArray addOnCatArray = itemObject.optJSONArray("Addon_category");
                            JSONArray addOnItemsArray = itemObject.optJSONArray("addon_items");
                            JSONArray ingredientsArray = itemObject.optJSONArray("ingredients_details");
                            if ("ar".equals(AppInstance.getInstance(getApplicationContext()).getFromSharedPref(AppUtils.PREF_USER_LANG))) {
                                mFood.setName(itemObject.optString("item_name_ar"));
                            } else {
                                mFood.setName(itemObject.optString("item_name"));
                            }

                            showFoodBasicInfo(itemObject);
                            if (photosArray != null) {
                                if (photosArray.length() > 0) showFoodImageGallery(photosArray);
                            }
                            if (sizeArray != null) {
                                showPlateSizes(sizeArray, sizePriceArray, discountedSizePriceArray);
                            }
                            showQuantity();
                            if (addOnCatArray != null && addOnItemsArray != null) {
                                showAddOnsAvailable(addOnCatArray, addOnItemsArray);
                            }
                            showTotalCost();
                            if (ingredientsArray != null && ingredientsArray.length() > 0) {
                                showIngredients(ingredientsArray);
                            }

                            AppUtils.showViews(svPage);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            AppUtils.hideProgress(dialog);
                            error.printStackTrace();
                            Snackbar snackbar = Snackbar.make(findViewById(R.id.page), getString(R.string.msg_operation_not_completed), Snackbar.LENGTH_INDEFINITE);
                            snackbar.setAction(getString(R.string.snack_retry), new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    loadFoodInfoFromNW();
                                }
                            });
                            snackbar.setActionTextColor(Color.RED);
                            snackbar.show();
                        }
                    }
            );
            AppInstance.getInstance(getApplicationContext()).addToRequestQueue(request);
        } else {
            Snackbar snackbar = Snackbar.make(findViewById(R.id.page), getString(R.string.msg_no_internet), Snackbar.LENGTH_INDEFINITE);
            snackbar.setAction(getString(R.string.snack_retry), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadFoodInfoFromNW();
                }
            });
            snackbar.setActionTextColor(Color.RED);
            snackbar.show();
        }
    }

    private void showFoodBasicInfo(JSONObject itemObject) {
        mFood.setMerchantId(itemObject.optString("merchant_id"));

        String itemDesc = itemObject.optString("item_description");
        if ("ar".equals(AppInstance.getInstance(getApplicationContext()).getFromSharedPref(AppUtils.PREF_USER_LANG))) {
            itemDesc = itemObject.optString("item_description_ar");
        }
        itemDesc = Html.fromHtml(itemDesc).toString().trim();

        if (itemDesc.length() > 140) itemDesc = itemDesc.substring(0, 132);
        String itemLogo = itemObject.optString("photo");
        String itemDiscount = itemObject.optString("discount");
        if ("".equals(itemDiscount)) itemDiscount = "0";
        mDiscount = Double.parseDouble(itemDiscount);

        ImageView ivFoodLogo;
        ivFoodLogo = findViewById(R.id.ivFoodLogo);
        //       ivFoodLogo.setImageURI(Uri.parse(Apis.IMG_PATH + "items/image/thumb_81_81/" + itemLogo));

        Glide
                .with(getApplicationContext())
                .load(Uri.parse(Apis.IMG_PATH + "items/image/thumb_81_81/" + itemLogo))
                .placeholder(R.drawable.placeholder_land)
                .into(ivFoodLogo);

        tvFoodTitle = (TextView) findViewById(R.id.tvFoodTitle);
        tvFoodTitle.setText(mFood.getName());

        tvFoodInfo = (TextView) findViewById(R.id.tvFoodInfo);
        tvFoodInfo.setText(itemDesc);
    }

    private void showFoodImageGallery(final JSONArray photosArray) {
        final int MAX_PICS = 4;
        rlGallery = (RelativeLayout) findViewById(R.id.rlGallery);
        AppUtils.showViews(rlGallery, lineGallery);

        LinearLayout llGallery;
        llGallery = (LinearLayout) findViewById(R.id.llGallery);
        tvPhotos = (TextView) findViewById(R.id.tvPhotos);
        tvPhotos.setText(tvPhotos.getText() + " (" + photosArray.length() + ")");
        final List<String> photoUris = new ArrayList<>();
        int i = 0;
        while (i < photosArray.length()) {
            View view = null;
            String url = Apis.IMG_PATH + "items/gallery/thumb_68_68/" + photosArray.optString(i);
            photoUris.add(url);
            if (i >= MAX_PICS) {
                i++;
                continue;
            }
            if (i < MAX_PICS - 1 || (i + 1) == photosArray.length()) {
                view = LayoutInflater.from(FoodAddBasketActivity.this).inflate(R.layout.layout_simple_thumb, null);
            } else {
                view = LayoutInflater.from(FoodAddBasketActivity.this).inflate(R.layout.layout_simple_thumb_plus, null);
                ((TextView) view.findViewById(R.id.tvMore)).setText(String.valueOf(photosArray.length() - (i + 1)));
            }
            ImageView photoView = (ImageView) view.findViewById(R.id.ivFoodImage);
            //    photoView.setImageURI(Uri.parse(url));

            Glide
                    .with(getApplicationContext())
                    .load(Uri.parse(url))
                    .placeholder(R.drawable.placeholder_land)
                    .into(photoView);
            view.setTag(i);
            llGallery.addView(view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Fragment fragment = PhotoGalleryFragment.newInstance(photoUris, R.id.flContainer);
//                    fragmentManager
//                            .beginTransaction()
//                            .setCustomAnimations(R.anim.entry_in, R.anim.entry_out, R.anim.exit_in, R.anim.exit_out)
//                            .add(R.id.flContainer, fragment)
//                            .addToBackStack(null)
//                            .commit();
                    String[] images = new String[photosArray.length()];
                    for (int i = 0; i < photosArray.length(); i++) {
                        images[i] = photosArray.optString(i);
                    }

                    startActivity(new Intent(FoodAddBasketActivity.this, FullScreenImageDisplay.class)
                            .putExtra("Images", images).putExtra("FromPage", "FOODBUSKET")
                            .putExtra("SELECTITEM", Integer.parseInt("" + v.getTag())));
                }
            });
            i++;
        }
    }

    private void showPlateSizes(JSONArray sizeArray, JSONArray sizePriceArray, JSONArray discountedPriceSizeArray) {

        System.out.println("FoodAddBasketActivity : sizeArray : " + sizeArray);
        System.out.println("FoodAddBasketActivity : sizePriceArray : " + sizePriceArray);
        System.out.println("FoodAddBasketActivity : discountedPriceSizeArray : " + discountedPriceSizeArray);

        LinearLayout llSize;
        llSize = (LinearLayout) findViewById(R.id.llSize);
        AppUtils.showViews(llSize, lineSize);
        llPlateSizes = (LinearLayout) findViewById(R.id.llPlateSizes);
        int i = 0;
        AppUtils.log("@@K Size-" + sizeArray);
        while (i < sizeArray.length()) {
            String size = sizeArray.optJSONObject(i).optString("name");
            String id = sizeArray.optJSONObject(i).optString("id");
            String price = String.valueOf(Double.parseDouble(discountedPriceSizeArray.optString(i)));
            String discountedPrice = String.valueOf(Double.parseDouble(sizePriceArray.optString(i)));
            View plateLayout = LayoutInflater.from(FoodAddBasketActivity.this).inflate(R.layout.layout_plate_size, null);
            LinearLayout llPlate = (LinearLayout) plateLayout.findViewById(R.id.llPlate);
            TextView tvPrice = (TextView) plateLayout.findViewById(R.id.tvPrice);
            TextView tvPlate = (TextView) plateLayout.findViewById(R.id.tvPlate);
            TextView tvActualPrice = (TextView) plateLayout.findViewById(R.id.tvActualPrice);
            View viewCutPrice = (View) plateLayout.findViewById(R.id.viewCutPrice);
            if (price.equalsIgnoreCase(discountedPrice)) {
                plateLayout.findViewById(R.id.relPrice).setVisibility(View.GONE);
            }

            tvPrice.setText(getString(R.string.currency) + AppUtils.changeToArabic(AppUtils.monetize(price), getApplicationContext()));
            tvActualPrice.setText(getString(R.string.currency) + AppUtils.changeToArabic(AppUtils.monetize(discountedPrice), getApplicationContext()));
            tvPlate.setText(size);
            final Plate plate = new Plate();
            plate.id = id;
            plate.index = i;
            plate.size = size;
            plate.price = price;
            plate.discountedPrice = discountedPrice;
            plate.llPlate = llPlate;
            plate.tvPrice = tvPrice;
            plate.tvDiscountedPrice = tvActualPrice;
            plate.tvPlate = tvPlate;
            plate.viewCutPrice = viewCutPrice;
            if (i == 0) {
                plate.selectPlate();
                mBasePrice = Double.parseDouble(plate.price) * mQty;
            }
            plateLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int clickIndex = plate.index;
                    for (Plate plateItem : plates) {
                        if (plateItem.selected) {
                            if (plateItem.index != clickIndex) {
                                plateItem.deSelectPlate();
                            }
                        } else {
                            if (plateItem.index == clickIndex) {
                                plateItem.selectPlate();
                                mBasePrice = Double.parseDouble(plateItem.price) * mQty;
                                onTotalPriceChanged();
                            }
                        }
                    }
                }
            });
            plates.add(plate);
            llPlateSizes.addView(plateLayout);
            i++;
        }
    }

    private void showQuantity() {
        llQty = (LinearLayout) findViewById(R.id.llQty);
        AppUtils.showViews(llQty, lineQty);
        tvQty = (TextView) findViewById(R.id.tvQty);

        ImageView ivAdd,
                ivSubtract;
        ;

        ivAdd = (ImageView) findViewById(R.id.ivAdd);
        ivSubtract = (ImageView) findViewById(R.id.ivSubtract);
        tvQty.setText(AppUtils.changeToArabic(String.valueOf(mQty), getApplicationContext()));
        ivAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                mQty = Integer.parseInt(tvQty.getText().toString());
                double unitPrice = mBasePrice / mQty;
                double unitAddOnPrice = mAddOnPrice / mQty;
                double unitIngrPrice = mIngredientPrice / mQty;
                tvQty.setText(AppUtils.changeToArabic(String.valueOf(++mQty), getApplicationContext()));
                mBasePrice = unitPrice * mQty;
                mIngredientPrice = unitIngrPrice * mQty;
                mAddOnPrice = unitAddOnPrice * mQty;

                AppUtils.testHashMap.put(mFood.getId(), String.valueOf(mQty));

                onTotalPriceChanged();
            }
        });
        ivSubtract.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                mQty = Integer.parseInt(tvQty.getText().toString());
                if (mQty > 1) {
                    double unitPrice = mBasePrice / mQty;
                    double unitAddOnPrice = mAddOnPrice / mQty;
                    double unitIngrPrice = mIngredientPrice / mQty;
                    tvQty.setText(AppUtils.changeToArabic(String.valueOf(--mQty), getApplicationContext()));
                    mBasePrice = unitPrice * mQty;
                    mAddOnPrice = unitAddOnPrice * mQty;
                    mIngredientPrice = unitIngrPrice * mQty;

                    AppUtils.testHashMap.put(mFood.getId(), String.valueOf(mQty));

                    onTotalPriceChanged();
                }
            }
        });
    }

    private void showAddOnsAvailable(JSONArray addOnCatArray, JSONArray addOnItemsArray) {
        llAddOns = (LinearLayout) findViewById(R.id.llAddOns);
        AppUtils.showViews(llAddOns, lineAddOns);
        int i = 0;
        while (i < addOnCatArray.length()) {
            try {
                String addOnTitle = addOnCatArray.optString(i);
                LinearLayout addOnLayout = (LinearLayout) LayoutInflater.from(FoodAddBasketActivity.this).inflate(R.layout.layout_food_addon, null);
                TextView tvAddonTitle = (TextView) addOnLayout.findViewById(R.id.tvAddonTitle);
                tvAddonTitle.setText(addOnTitle);
                JSONArray addOnsArray = addOnItemsArray.optJSONArray(i);
                int j = 0;
                while (j < addOnsArray.length()) {
                    JSONObject addOnObject = addOnsArray.optJSONObject(j);
                    View checkboxLayout = LayoutInflater.from(FoodAddBasketActivity.this).inflate(R.layout.layout_checkbox_w_price, null);
                    CheckBox cbAddOn = (CheckBox) checkboxLayout.findViewById(R.id.cbAddOn);
                    TextView tvPrice = (TextView) checkboxLayout.findViewById(R.id.tvPrice);
                    Addon addon = new Addon();
                    addon.checkBox = cbAddOn;
                    addon.id = addOnObject.optString("sub_item_id");
                    addon.name = addOnObject.optString("sub_item_name");
                    if ("ar".equals(AppInstance.getInstance(getApplicationContext()).getFromSharedPref(AppUtils.PREF_USER_LANG))) {
                        addon.name = addOnObject.optString("sub_item_name_ar");
                    }
                    addon.price = addOnObject.optString("price");
                    addons.add(addon);
                    addon.checkBox.setText(addon.name);
                    addon.checkBox.setOnCheckedChangeListener(addon.listener);
                    tvPrice.setText(getString(R.string.currency) + AppUtils.monetize(addon.price));
                    addOnLayout.addView(checkboxLayout);
                    j++;
                }
                llAddOns.addView(addOnLayout);
                i++;
                if (i < addOnCatArray.length()) {
                    View line = LayoutInflater.from(FoodAddBasketActivity.this).inflate(R.layout.layout_simple_line, null);
                    line.setPadding(25, 5, 0, 5);
                    llAddOns.addView(line);
                }
            } catch (NullPointerException e) {
                break;
            }
        }
    }

    private void showIngredients(JSONArray ingredientsArray) {
        llIngredients = (LinearLayout) findViewById(R.id.llIngredients);
        AppUtils.showViews(llIngredients, lineIngredients);
        int i = 0;
        while (i < ingredientsArray.length()) {
            try {
                JSONObject ingredientObject = ingredientsArray.optJSONObject(i);
                String ingredientName = ingredientObject.optString("ingredients_name");
                if ("ar".equals(AppInstance.getInstance(getApplicationContext()).getFromSharedPref(AppUtils.PREF_USER_LANG))) {
                    ingredientName = ingredientObject.optString("ingredients_name_ar");
                }
                String ingredientId = ingredientObject.optString("ingredients_id");
                String ingredientPrice = ingredientObject.optString("price");
                Ingredient ingredient = new Ingredient();
                ingredient.id = ingredientId;
                ingredient.name = ingredientName;
                ingredient.price = ingredientPrice;
                View checkboxLayout = LayoutInflater.from(FoodAddBasketActivity.this).inflate(R.layout.layout_checkbox_w_price, null);
                CheckBox cbIngredient = (CheckBox) checkboxLayout.findViewById(R.id.cbAddOn);
                TextView tvPrice = (TextView) checkboxLayout.findViewById(R.id.tvPrice);
                if (Double.parseDouble(ingredient.price) > 0)
                    tvPrice.setText(getString(R.string.currency) + AppUtils.monetize(ingredient.price));
                else
                    tvPrice.setText(getString(R.string.free));
                ingredient.checkBox = cbIngredient;
                ingredient.checkBox.setText(ingredientName);
                ingredient.checkBox.setOnCheckedChangeListener(ingredient.listener);
                llIngredients.addView(checkboxLayout);
                ingredients.add(ingredient);
                i++;
            } catch (NullPointerException e) {
                e.printStackTrace();
                break;
            }
        }
    }

    private void showTotalCost() {
        RelativeLayout rlTotal;
        rlTotal = (RelativeLayout) findViewById(R.id.rlTotal);
        tvTotal = (TextView) findViewById(R.id.tvTotal);
        AppUtils.showViews(rlTotal);

        tvTotal.setText(getString(R.string.currency) + AppUtils.changeToArabic(AppUtils.monetize(String.valueOf(mBasePrice + mAddOnPrice + mIngredientPrice)), getApplicationContext()));
    }

    private void onTotalPriceChanged() {
        mFood.setUnitPrice(String.valueOf(mBasePrice / mQty));
        tvTotal.setText(getString(R.string.currency) + AppUtils.changeToArabic(String.valueOf(mBasePrice + mAddOnPrice + mIngredientPrice), getApplicationContext()));
    }

    public void onAddToBasketClicked(View view) {

     /*   AppUtils.CURRENT_RESTAURANT_NAME_FINAL=AppUtils.CURRENT_RESTAURANT_NAME;
        AppUtils.CURRENT_RESTAURANT_NAME_FINAL_AR=AppUtils.CURRENT_RESTAURANT_NAME_AR;*/

        SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor mEditor = sharedpreferences.edit();

        mEditor.putString("CURRENT_RESTAURANT_NAME_FINAL", AppUtils.CURRENT_RESTAURANT_NAME);
        mEditor.putString("CURRENT_RESTAURANT_NAME_FINAL_AR", AppUtils.CURRENT_RESTAURANT_NAME_AR);
        mEditor.apply();

        mFood.setPriceBasket(AppUtils.monetize(String.valueOf(mBasePrice + mAddOnPrice + mIngredientPrice)));
        mFood.setBasketCount(String.valueOf(mQty));
        for (Plate plate : plates) {
            if (plate.selected) {
                mFood.setSizeBasket(plate.size);
                mFood.setSizeBasketId(plate.id);
            }
        }
        String strAddOns = "";
        String strAddOnIds = "";
        String strAddOnPrices = "";
        for (Addon addon : addons) {
            if (addon.checkBox.isChecked()) {
                if (strAddOns.length() > 0) {
                    strAddOns += ";;";
                    strAddOnIds += ";;";
                    strAddOnPrices += ";;";
                }
                strAddOns += addon.name;
                strAddOnIds += addon.id;
                strAddOnPrices += addon.price;
            }
        }
        mFood.setAddOns(strAddOns);
        mFood.setAddOnIds(strAddOnIds);
        mFood.setAddOnPrices(strAddOnPrices);

        String strIngredients = "";
        String strIngredientIds = "";
        String strIngredientPrices = "";
        for (Ingredient ingredient : ingredients) {
            if (ingredient.checkBox.isChecked()) {
                if (strIngredients.length() > 0) {
                    strIngredients += ";;";
                    strIngredientIds += ";;";
                    strIngredientPrices += ";;";
                }
                strIngredients += ingredient.name;
                strIngredientIds += ingredient.id;
                strIngredientPrices += ingredient.price;
            }
        }
        mFood.setIngredients(strIngredients);
        mFood.setIngredientIds(strIngredientIds);
        mFood.setIngredientPrices(strIngredientPrices);
        mFood.setComment(tetComment.getText().toString().trim());
        onTotalPriceChanged();
        final DBHelper db = new DBHelper(this);
        System.out.println("hjvjvj : db.getBasketMerchantId() : " + db.getBasketMerchantId());
        System.out.println("hjvjvj : mFood.getMerchantId() : " + mFood.getMerchantId());
        if (db.getBasketMerchantId().equals(mFood.getMerchantId()) || db.getBasketMerchantId().length() == 0) {

            db.addFoodBasket(AppInstance.getInstance(getApplicationContext()).getFromSharedPref(AppUtils.PREF_USER_ID), mFood);
            AppUtils.testHashMap.put(mFood.getId(), String.valueOf(mQty));

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    onBackPressed();
                }
            }, 1500);
            Snackbar.make(findViewById(R.id.page), getString(R.string.msg_food_added_basket), Snackbar.LENGTH_SHORT).show();
            AppInstance.getInstance(getApplicationContext()).addToSharedPref("min_price", AppUtils.MINIMUM_PRICE);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(FoodAddBasketActivity.this);
            builder.setMessage(getString(R.string.msg_pending_order))
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            db.deleteAll();
                            db.addFoodBasket(AppInstance.getInstance(getApplicationContext()).getFromSharedPref(AppUtils.PREF_USER_ID), mFood);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    onBackPressed();
                                }
                            }, 1500);
                            Snackbar.make(findViewById(R.id.page), getString(R.string.msg_food_added_basket), Snackbar.LENGTH_SHORT).show();
                            AppInstance.getInstance(getApplicationContext()).addToSharedPref("min_price", AppUtils.MINIMUM_PRICE);
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                        }
                    });
            AlertDialog dialog = builder.create();
            dialog.show();
        }

    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.restaurant_food, menu);
        return true;
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                break;
            /*case R.id.menu_info:
                AppUtils.log("Info");
                return true;*/
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        if (fragmentManager.getBackStackEntryCount() >= 1) {
            fragmentManager.popBackStack();
        } else {
            finish();
            overridePendingTransition(R.anim.exit_in, R.anim.exit_out);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    class Addon {
        private String id,
                name,
                price;
        private CheckBox checkBox;

        CompoundButton.OnCheckedChangeListener listener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mAddOnPrice += Double.parseDouble(price) * mQty;
                } else {
                    mAddOnPrice -= Double.parseDouble(price) * mQty;
                }
                onTotalPriceChanged();
            }
        };
    }

    class Ingredient {
        private String id, name, price;
        private CheckBox checkBox;

        CompoundButton.OnCheckedChangeListener listener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mIngredientPrice += Double.parseDouble(price) * mQty;
                } else {
                    mIngredientPrice -= Double.parseDouble(price) * mQty;
                }
                onTotalPriceChanged();
            }
        };
    }

    class Plate {
        private int index;
        private String id;
        private String size, price, discountedPrice;
        private LinearLayout llPlate;
        private TextView tvPlate, tvPrice, tvDiscountedPrice;
        private boolean selected;
        private View viewCutPrice;

        public void selectPlate() {
            selected = true;
            llPlate.setBackgroundResource(R.drawable.rect_bg_primarycolor_solid);
            tvPlate.setTextColor(ContextCompat.getColor(FoodAddBasketActivity.this, R.color.white));
            tvPrice.setTextColor(ContextCompat.getColor(FoodAddBasketActivity.this, R.color.white));
            tvDiscountedPrice.setTextColor(ContextCompat.getColor(FoodAddBasketActivity.this, R.color.white));
            viewCutPrice.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

        }

        public void deSelectPlate() {
            selected = false;
            llPlate.setBackgroundResource(R.drawable.rect_bg_primarycolor_deep);
            tvPlate.setTextColor(ContextCompat.getColor(FoodAddBasketActivity.this, R.color.colorPrimary));
            tvPrice.setTextColor(ContextCompat.getColor(FoodAddBasketActivity.this, R.color.colorPrimary));
            tvDiscountedPrice.setTextColor(ContextCompat.getColor(FoodAddBasketActivity.this, R.color.colorPrimary));
            viewCutPrice.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        }
    }
}
