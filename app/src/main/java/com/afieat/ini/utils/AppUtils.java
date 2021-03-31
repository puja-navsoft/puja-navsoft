package com.afieat.ini.utils;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.afieat.ini.models.DemoSearchModel;
import com.afieat.ini.models.Food;
import com.afieat.ini.models.FoodCopy;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by amartya on 30/03/16 with love.
 */
public final class AppUtils {

    public static final String SHARED_PREF_FILENAME = "afieat_sharedPref";
    public static final String SHARED_PREF_DEVICE_ID = "afieat_sharedPref_device_id";
    public static final String SHARED_PREF_USER_PASSWORD = "afieat_sharedPref_user_password";
    public static final String PREF_USER_ID = "pref_user_id";
    public static final String PREF_LOGGED_IN = "pref_logged_in";
    public static final String PREF_CITY = "pref_city";
    public static final String PREF_CITY_ID = "pref_city_id";
    public static final String PREF_REGION = "pref_region";
    public static final String PREF_REGION_ID = "pref_region_id";
    public static final String PREF_USER_FNAME = "pref_user_fname";
    public static final String PREF_USER_LNAME = "pref_user_lname";
    public static final String PREF_USER_FATHER = "pref_user_father_name";
    public static final String PREF_USER_FAMILY = "pref_user_family_name";
    public static final String PREF_USER_EMAIL = "pref_user_email";
    public static final String PREF_USER_PHONE = "pref_user_phone";
    public static final String PREF_USER_LANG = "pref_user_lang";
    public static final String PREF_ORDER_NO = "pref_order_no";
    public static final String PREF_CURRENCY_CONV = "pref_currency_conv";
    public static final String MY_POINTS = "my_points";
    public static final String MY_POINTS_VALUE = "my_points_value";

    public static  int selectedSize=-1;
    public static int LAST_SELECTED_LISTVIEW_ITEM=0;
    public static int LAST_SELECTED_TAP_POSITION=0;
    public static String PLATE_PRICE="";
    public static String PLATE_ID="";
    public static String PLATE_TYPE="";
    public static JSONObject SEARCH_JSON_=null;
    public static ArrayList<List<Food>> MFOOD;
    public static ArrayList<DemoSearchModel> DEMO_SEARCH_MODEL_LIST;
    public static ArrayList<FoodCopy> DEMO_FOOD_COPY;
    public static ArrayList<String> HINT_TEXT;
    public static ArrayList<String> HINT_TEXT_AR;

    public static final String RESTAURANT_INDEX = "restaurants_dev";
  //  public static final String RESTAURANT_INDEX = "restaurants";
   public static final String ITEMS_INDEX = "items_dev";
  //  public static final String ITEMS_INDEX = "items";
    public static final String CUISINE_INDEX = "cuisine";

    public static String Voucher_Data = "";
    public static boolean IS_OPEN = false;
    public static boolean IS_NOTIFICATION_CLICK = false;
    public static String CURRENT_RESTAURANT_NAME = "";
    public static String CURRENT_RESTAURANT_NAME_AR = "";
    public static String CURRENT_RESTAURANT_NAME_FINAL = "";
    public static String CURRENT_RESTAURANT_NAME_FINAL_AR = "";

    public static boolean IS_FROM_PROCEED_TO_PAY = false;
    public static String MINIMUM_PRICE = "";
    public static String GROUP_ID = "";
    // used in MainActivity
    public static final String EXTRA_REGION_ID = "extra_region_id";
    public static final String EXTRA_CITY_ID = "extra_city_id";
    public static final String EXTRA_CITY_NAME = "extra_city_name";
    public static final String EXTRA_REGION_NAME = "extra_region_name";
    public static final String EXTRA_REQ_LOCATION = "extra_request_loc";


    // Used in CategoryList Activity
    public static final String EXTRA_GROUP_ID = "gorup_id";

    // used in RestaurantDetailsActivity
    public static final String EXTRA_RESTAURANT_ID = "extra_restaurant_id";
    public static final String EXTRA_ITEM_ADDED_BASKET = "extra_item_added_basket";

    //used in RestaurantOrderItemsFragment
    public static final String EXTRA_FOOD_ID = "extra_food_id";
    public static final String EXTRA_FOOD_NAME = "extra_food_name";

    public static boolean IS_SUPER_MARKET=false;

    //used in PaymentActivity
    public static final String EXTRA_MERCHANT_ID = "extra_merchant_id";
    public static final String EXTRA_RESTAURANT_DISCOUNT = "extra_rest_discount";
    public static final String EXTRA_PROMO_DISCOUNT = "extra_promo_discount";
    public static final String EXTRA_TOTAL_COST = "extra_total_cost";
    public static final String EXTRA_TOTAL_COST_USD = "extra_total_cost_usd";
    public static final String EXTRA_REQ_ADDRESS = "extra_req_address";
    public static final String EXTRA_DEL_TIME = "extra_del_time";
    public static final String EXTRA_DEL_DATE = "extra_del_date";
    public static final String EXTRA_PROMO_VOUCHER_ID = "promo_voucher_id";
    public static final String EXTRA_PROMO_VOUCHER_CODE = "promo_voucher_code";
    public static final String EXTRA_DEL_ADDRESS = "extra_del_address";
    public static final String EXTRA_PAYMENT_URL = "extra_payment_url";

    //used in OrdersActivity
    public static final String EXTRA_ORDER_ID = "extra_order_id";
    public static final String EXTRA_ORDER_NO = "extra_order_no";

    //used in AddressSelectionActivity
    public static final String EXTRA_ADDRESS_ID = "extra_address_id";

    //used in AddressAddActivity
    public static final String EXTRA_IN_EDIT_MODE = "extra_in_edit_mode";
    public static final String EXTRA_AD_ID = "extra_ad_id";

    public static final double EXCHANGE_RATE = 0.00090;

    public static final int REQ_PLACE_ORDER = 666;
    public static final int REQ_CHANGE_LANG = 667;
    public static final int REQ_LOGIN = 668;

    //used in MyFirebaseInstanceIDService
    public static final String SHARED_PREF_DEV_FIRE_BASE_TOKEN = "prefDevFireBaseToken";

    //used in MyFirebaseMessagingService
    public static final String SHARED_PREF_USER_ID = "prefUserId";

    //INTENT FILTER
    public static final String ACTION_ITEM_ADDED_BASKET = "item_added_basket";
    public static final String FROM_PUSH = "from_push";
    public static final String EXTRA_FROM_SEARCH = "search";
    public static final String EXTRA_ITEM_NAME = "item name";
    public static final String EXTRA_CUISINE_ID = "search cuisine";


    public static String APPTOKEN = "";
    public static String AUTHRIZATIONKEY = "";

    public static String REGION_ID = "";
    public static String CURRENT_RESTAURANT_IMAGE = "";

    public static String IS_CART_VISIBLE = "";
    public static String timeOfDelivery = "";

    /**
     * checks whether there is network connectivity
     *
     * @param context AppContext
     * @return true or false
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager conMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netWInfo = conMgr.getActiveNetworkInfo();
        return netWInfo != null && netWInfo.isAvailable() && netWInfo.isConnected();
    }

    /**
     * checks whether the string is in valid email address format
     * WARNING: Does not check for empty string
     *
     * @param email EmailId
     * @return true or false
     */
    public static boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static void log(String msg) {
        Log.d("@@ ", "-> " + msg);
    }

    public static void nwLog(String msg) {
        Log.v("MSG", msg);
    }

    public static void dbLog(String msg) {
        Log.v("MSG", "QUERY-> " + msg);
    }

    public static void log(JSONObject msg) {
        try {
            Log.d("@@ ", msg.toString(2));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * show an indeterminate progress dialog
     *
     * @param context Activity context
     * @param title   Title/header of the progressdialog
     * @param message Message/body of the progressdialog
     * @return ProgressDialog
     */
    public static ProgressDialog showProgress(Context context, String title, String message) {
        ProgressDialog dialog = new ProgressDialog(context);
        dialog.setMessage(message);
        if (title.trim().length() > 0) dialog.setTitle(title);
        dialog.show();
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

    /**
     * hides the progress dialog
     *
     * @param dialog ProgressDialog instance
     */
    public static void hideProgress(ProgressDialog dialog) {
        dialog.hide();
        dialog.dismiss();
    }

    /**
     * shows 'n' number of views previously hidden
     *
     * @param views any number of View objects
     */
    public static void showViews(View... views) {
        for (View view : views) {
            view.setVisibility(View.VISIBLE);
        }
    }

    /**
     * hides 'n' number of views previously visible
     *
     * @param views any number of View objects
     */
    public static void hideViews(View... views) {
        for (View view : views) {
            view.setVisibility(View.GONE);
        }
    }

    /**
     * format monetary values to 2-decimal places
     *
     * @param value String value of amount
     * @return value Formatted value of amount
     */
    public static String monetize(String value) {
        /*double d = Double.valueOf(value);
        return String.format("%.2f", d);*/
        return value;
    }

    /**
     * This method converts dp unit to equivalent pixels, depending on device density.
     *
     * @param dp      A value in dp (density independent pixels) unit. Which we need to convert into pixels
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent px equivalent to dp depending on device density
     */
    public static int convertDpToPixel(int dp, Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        DisplayMetrics displaymetrics = new DisplayMetrics();
        display.getMetrics(displaymetrics);

        return (int) (dp * displaymetrics.density + 0.5f);
    }

    /**
     * This method converts device specific pixels to density independent pixels.
     *
     * @param px      A value in px (pixels) unit. Which we need to convert into dp
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent dp equivalent to px value
     */
    public static int convertPixelToDp(int px, Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics displaymetrics = new DisplayMetrics();
        display.getMetrics(displaymetrics);
        return (int) (px / displaymetrics.density + 0.5f);
    }

    /**
     * disables 'n' number of buttons previously enabled
     *
     * @param buttons Any number of buttons
     */
    public static void disableButtons(Button... buttons) {
        for (Button button : buttons) {
            button.setEnabled(false);
            button.setAlpha(0.3f);
        }
    }

    /**
     * enables 'n' number of buttons previously disabled
     *
     * @param buttons Any number of buttons
     */
    public static void enableButtons(Button... buttons) {
        for (Button button : buttons) {
            button.setEnabled(true);
            button.setAlpha(1f);
        }
    }

    public static String formatDate(String date) {
        String inputPattern = "yyyy-MM-dd HH:mm:ss";
        String outputPattern = "dd MMM, yyyy";

        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        try {
            Date inputDate = inputFormat.parse(date);
            String outputDate = outputFormat.format(inputDate);
            return outputDate;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String changeToEnglish(String str) {
        return (((((((((((str.replaceAll("١", "1")).replaceAll("٢", "2")).replaceAll("٣", "3")).replaceAll("٤", "4"))
                .replaceAll("٥", "5")).replaceAll("٦", "6")).replaceAll("٧", "7")).replaceAll("٨", "8")).replaceAll("٩", "9")).replaceAll("٠", "0")).replaceAll("٫", "."));
    }

    public static String changeToArabic(String str, Context context, boolean... keepDecimal) {
        /*if (str.contains(".")) {
            if (keepDecimal == null) {
                str = str.substring(0, str.indexOf("."));
            }
        }
        if (str.contains("to")) {
            str = str.replace("to", "إلى");
        }
        if (AppInstance.getInstance(context).getFromSharedPref(AppUtils.PREF_USER_LANG).equals("ar")) {

        } else {
            return str;
        }*/

        String result=str;

        /*if ("en".equals(AppInstance.getInstance(context).getFromSharedPref(AppUtils.PREF_USER_LANG))) {//ENG
            result=str;
        }
        else {//Arabic
            if (str.contains(".")) {
                // Double totalAmount = Double.parseDouble(str.substring(0, str.indexOf(".") + 2));
                Double totalAmount = Double.parseDouble(str);
                str = String.valueOf(new DecimalFormat("0.00").format(totalAmount));
            }
            if (str.contains("to")) {
                str = str.replace("to", "إلى");
            }
             result =(((((((((((str.replaceAll("1", "١")).replaceAll("2", "٢")).replaceAll("3", "٣")).replaceAll("4", "٤"))
                    .replaceAll("5", "٥")).replaceAll("6", "٦")).replaceAll("7", "٧")).replaceAll("8", "٨")).replaceAll("9", "٩")).replaceAll("0", "٠")).replaceAll("\\.", "٫"));

        }*/

        /*if (str.contains(".")) {
            // Double totalAmount = Double.parseDouble(str.substring(0, str.indexOf(".") + 2));
            Double totalAmount = Double.parseDouble(str);
            str = String.valueOf(new DecimalFormat("0.00").format(totalAmount));
        }
        if (str.contains("to")) {
            str = str.replace("to", "إلى");
        }
         return (((((((((((str.replaceAll("1", "١")).replaceAll("2", "٢")).replaceAll("3", "٣")).replaceAll("4", "٤"))
                .replaceAll("5", "٥")).replaceAll("6", "٦")).replaceAll("7", "٧")).replaceAll("8", "٨")).replaceAll("9", "٩")).replaceAll("0", "٠")).replaceAll("\\.", "٫"));*/
       return  result;
    }


    public static boolean hasSoftKeys(Activity mActivity) {
        boolean hasSoftwareKeys;


        if (mActivity == null) {
            return true;
        }

        WindowManager window = mActivity.getWindowManager();

        if (window != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            Display d = mActivity.getWindowManager().getDefaultDisplay();

            DisplayMetrics realDisplayMetrics = new DisplayMetrics();
            d.getRealMetrics(realDisplayMetrics);

            int realHeight = realDisplayMetrics.heightPixels;
            int realWidth = realDisplayMetrics.widthPixels;

            DisplayMetrics displayMetrics = new DisplayMetrics();
            d.getMetrics(displayMetrics);

            int displayHeight = displayMetrics.heightPixels;
            int displayWidth = displayMetrics.widthPixels;

            hasSoftwareKeys = (realWidth - displayWidth) > 0 || (realHeight - displayHeight) > 0;
        } else {
            boolean hasMenuKey = ViewConfiguration.get(mActivity.getApplicationContext()).hasPermanentMenuKey();
            boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);

            hasSoftwareKeys = !hasMenuKey && !hasBackKey;
        }

        return hasSoftwareKeys;
    }


  public static  HashMap<String, String> testHashMap = new HashMap<String, String>();

    public static boolean indexExists(final List list, final int index) {
        return index >= 0 && index < list.size();
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static Dialog setMargins(Dialog dialog, int marginLeft, int marginTop, int marginRight, int marginBottom )
    {
        Window window = dialog.getWindow();
        if ( window == null )
        {
            // dialog window is not available, cannot apply margins
            return dialog;
        }
        Context context = dialog.getContext();

        // set dialog to fullscreen
        RelativeLayout root = new RelativeLayout( context );
        root.setLayoutParams( new ViewGroup.LayoutParams( ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT ) );
        dialog.requestWindowFeature( Window.FEATURE_NO_TITLE );
        dialog.setContentView( root );
        // set background to get rid of additional margins
        window.setBackgroundDrawable( new ColorDrawable( Color.WHITE ) );

        // apply left and top margin directly
        window.setGravity( Gravity.LEFT | Gravity.TOP );
        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.x = marginLeft;
        attributes.y = marginTop;
        window.setAttributes( attributes );

        // set right and bottom margin implicitly by calculating width and height of dialog
        Point displaySize = getDisplayDimensions( context );
        int width = displaySize.x - marginLeft - marginRight;
        int height = displaySize.y - marginTop - marginBottom;
        window.setLayout( width, height );

        return dialog;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @NonNull
    public static Point getDisplayDimensions( Context context )
    {
        WindowManager wm = ( WindowManager ) context.getSystemService( Context.WINDOW_SERVICE );
        Display display = wm.getDefaultDisplay();

        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics( metrics );
        int screenWidth = metrics.widthPixels;
        int screenHeight = metrics.heightPixels;

        // find out if status bar has already been subtracted from screenHeight
        display.getRealMetrics( metrics );
        int physicalHeight = metrics.heightPixels;
        int statusBarHeight = getStatusBarHeight( context );
        int navigationBarHeight = getNavigationBarHeight( context );
        int heightDelta = physicalHeight - screenHeight;
        if ( heightDelta == 0 || heightDelta == navigationBarHeight )
        {
            screenHeight -= statusBarHeight;
        }

        return new Point( screenWidth, screenHeight );
    }

    public static int getStatusBarHeight( Context context )
    {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier( "status_bar_height", "dimen", "android" );
        return ( resourceId > 0 ) ? resources.getDimensionPixelSize( resourceId ) : 0;
    }

    public static int getNavigationBarHeight( Context context )
    {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier( "navigation_bar_height", "dimen", "android" );
        return ( resourceId > 0 ) ? resources.getDimensionPixelSize( resourceId ) : 0;
    }


}


