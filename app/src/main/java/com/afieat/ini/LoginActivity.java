package com.afieat.ini;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;

import com.algolia.search.saas.RequestOptions;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.text.method.PasswordTransformationMethod;
import android.text.method.TransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afieat.ini.database.DBHelper;
import com.afieat.ini.utils.Apis;
import com.afieat.ini.utils.AppInstance;
import com.afieat.ini.utils.AppUtils;
import com.afieat.ini.utils.NetworkRequest;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public class LoginActivity extends AppCompatActivity {

    private Map<String, String> params;
    private boolean isPasswordVisible;

    private RelativeLayout page;
    private TextInputEditText tetEmail, tetPassword;
    private Button btnForgotPassword;
    private String id = "";
    private ImageView googleBtn,facebook;

    public static boolean FromSpalshPage = false;

    //   private String android_id;
    private Class<?> c;
    private CallbackManager callbackManager;
    private String loginStatus;


    public LoginActivity() {
        params = new HashMap<>();
        isPasswordVisible = false;
    }

    private AppInstance appInstance;
    private String mUserId;
    private final int REQUEST_CHECKIN = 101;
    private static final int RC_SIGN_IN = 1;


    ImageView openDrawer;
    ImageView fakeViewDrawer;

    DrawerLayout drawerLayout;
    private ListView lvNav;
    private NavigationView mNavigationView;

    private String[] temp_navitem;
    private int[] temp_navicon = new int[0];
    private GoogleSignInClient googleSignInClient;

    private final String[] navItems = {"Start Order", "English/عربى", "Facebook", "Help Center", "About Afieat"};
    private final String[] navItems_ar = {"بدء الطلب", "English/عربى", "فيس بوك", "مركز المساعدة", "حول Afieat"};
    private final int[] navIcons = {
            R.drawable.home_24,
            R.drawable.nav_language,
            R.drawable.fb_24,
            R.drawable.nav_help,
            R.drawable.about_24,

            /*R.drawable.nav_settings,*/
            /*R.drawable.nav_contact,*/

            /*R.drawable.nav_signout*/
    };


    //---------------
    private Dialog afieatGifLoaderDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        android_id = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
//        SharedPreferences.Editor editor = getSharedPreferences(AppUtils.SHARED_PREF_DEVICE_ID, MODE_PRIVATE).edit();
//        editor.putString("deviceID", android_id);
//        editor.commit();
//        Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/font name.ttf");
//        tx.setTypeface(custom_font);
        overridePendingTransition(R.anim.entry_in, R.anim.entry_out);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.new_drawer_layout_login_page);

       /* getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
*/
        appInstance = AppInstance.getInstance(getApplicationContext());

        AppUtils.APPTOKEN = appInstance.getAuthkey();

        mUserId = appInstance.getFromSharedPref(AppUtils.PREF_USER_ID);

        page = (RelativeLayout) findViewById(R.id.page);
        //  mToolbar = (Toolbar) findViewById(R.id.appbar);
      /*  mToolbar.setTitle(getString(R.string.title_check_in));
        mToolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        mToolbar.setTitleTextColor(ContextCompat.getColor(getApplicationContext(), R.color.greyXLight));
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
*/
      /*  Intent intent = getIntent();
        // String action = intent.getAction();
        Uri data = intent.getData();

        System.out.println("Firebase : LoginActivity : data : " + data);*/

        tetEmail = (TextInputEditText) findViewById(R.id.tetEmail);
        tetPassword = (TextInputEditText) findViewById(R.id.tetPassword);
        googleBtn = findViewById(R.id.googleBtnLogin);
        facebook = findViewById(R.id.fbLoginButton);
        //       btnForgotPassword = (Button) findViewById(R.id.btnForgotPassword);

        tetPassword.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if ("en".equals(AppInstance.getInstance(getApplicationContext()).getFromSharedPref(AppUtils.PREF_USER_LANG))) {
                        if (event.getRawX() >= (tetPassword.getRight() - tetPassword.getCompoundDrawables()[2].getBounds().width() - 60)) {
                            int drawable = isPasswordVisible ? R.drawable.ic_visibility_black_18dp : R.drawable.ic_visibility_off_black_18dp;
                            TransformationMethod method = isPasswordVisible ? new PasswordTransformationMethod() : null;
                            isPasswordVisible = !isPasswordVisible;
                            tetPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, drawable, 0);
                            tetPassword.setTransformationMethod(method);
                            return true;
                        }
                    } else {
                        if (event.getRawX() <= (tetPassword.getLeft() - tetPassword.getCompoundDrawables()[0].getBounds().width() - 60)) {
                            int drawable = isPasswordVisible ? R.drawable.ic_visibility_black_18dp : R.drawable.ic_visibility_off_black_18dp;
                            TransformationMethod method = isPasswordVisible ? new PasswordTransformationMethod() : null;
                            isPasswordVisible = !isPasswordVisible;
                            tetPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, drawable, 0);
                            tetPassword.setTransformationMethod(method);
                            return true;
                        }
                    }
                }
                return false;
            }
        });

        // ********************* Google SIGN IN ***********************

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                // .requestIdToken()
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        // facebook

        callbackManager = CallbackManager.Factory.create();

        facebookLogin();

//        btnForgotPassword.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
//                startActivity(intent);
//            }
//        });
        if ("true".equals(AppInstance.getInstance(getApplicationContext()).getFromSharedPref(AppUtils.PREF_LOGGED_IN))) {
            setResult(RESULT_OK);
            finish();
        }

        openDrawer = findViewById(R.id.openDrawer);
        fakeViewDrawer = findViewById(R.id.fakeViewDrawer);


        lvNav = findViewById(R.id.lvNav);
        mNavigationView = findViewById(R.id.nav_view);
        drawerLayout = findViewById(R.id.drawerLayout);

        lvNav.setAdapter(new NavListAdapter());
        lvNav.setOnItemClickListener(new NavMenuItemClickListener());


        openDrawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                drawerLayout.openDrawer(mNavigationView);
            }
        });


        fakeViewDrawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                drawerLayout.openDrawer(mNavigationView);
            }
        });


        googleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent signInIntent = googleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);

            }
        });

        facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList("public_profile", "email"));
            }
        });


        checkForLoggedInStatus();

    }

    private void facebookLogin() {
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {


                        GraphRequest request = new GraphRequest(
                                AccessToken.getCurrentAccessToken(),
                                "/" + loginResult.getAccessToken().getUserId(),
                                null,
                                HttpMethod.GET,
                                new GraphRequest.Callback() {
                                    public void onCompleted(GraphResponse response) {
                                        // Log.d("@@ FB-" , response.getJSONObject());
                                        // LoginWithFacebook(response);
                                        /* handle the result */

                                        // JSONObject Params = new JSONObject();
                                        JSONObject object = response.getJSONObject();
                                        Log.d("TAG", object.toString());
                                        String first_name="", id = "";
                                        String email="", phone="", image_url="";
                                        try {
                                            first_name = object.getString("name");
                                        }
                                        catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                            // String last_name = object.getString("last_name");
                                        try {
                                            email = object.getString("email");
                                        } catch (JSONException ex) {
                                            ex.printStackTrace();
                                        }
                                        try {
                                            id = object.getString("id");
                                        } catch (JSONException ex) {
                                            ex.printStackTrace();
                                        }
                                        try {
                                            phone = object.getString("phone");
                                        } catch (JSONException ex) {
                                            ex.printStackTrace();
                                        }
                                        image_url = "https://graph.facebook.com/"+id+ "/picture?type=normal";



                                            //update in server
//                                            if (first_name!=null)
//                                                updateFacebookUI(first_name,email,id);
//                                            else
//                                                updateFacebookUI("",email,id);

                                        facebookLoginData(first_name,email,id,phone);
                                    }
                                }
                        );

                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id,name,email,gender, birthday,cover,picture.type(large)");
                        request.setParameters(parameters);
                        request.executeAsync();

                    }

                    @Override
                    public void onCancel() {
                        // App code
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                        Log.d("@@ FB-" , exception.getMessage());
                    }
                });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data); // For facebook  login

        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {

        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            updateUI(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            e.printStackTrace();
//            updateUI(null);
        }
    }

    private void updateUI(GoogleSignInAccount account) {

        //  Utils.showToastLong(account.getGivenName(),this);
        googleLogin(account);

    }


    private void facebookLoginData(String firstName, String emailId, String id, String phone) {
        params.clear();
        String firstname = "";
        String lastname = "";
        String name = firstName;
        firstname = name;
        final String devToken = AppInstance.getInstance(getApplicationContext()).getFromSharedPref(AppUtils.SHARED_PREF_DEV_FIRE_BASE_TOKEN);

        if (devToken.length() == 0) {
            Toast.makeText(getApplicationContext(), getString(R.string.msg_no_token), Toast.LENGTH_LONG).show();
            return;
        }

        try {
            firstname = name.split(" ")[0];
            lastname = name.split(firstname)[1].trim();
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }

        final String email = emailId;

        params.put("firstname", firstname);
        //       params.put("lastname", lastname);

        params.put("email", email);
        if (!phone.isEmpty())
            params.put("phone",phone);
        params.put("father_name", "");
        //            params.put("grand_father_name", granpaName);
        params.put("user_device_token", devToken);
        params.put("city", "");
        params.put("referal_code", "");
        params.put("social_id", id);

        Log.d("check_sign_up", "  test " + params);
//                    Snackbar.make(page, getString(R.string.msg_account_created), Snackbar.LENGTH_LONG).show();

        System.out.println("SignUpActivity : Param : " + params);
        if (AppUtils.isNetworkAvailable(getApplicationContext())) {

            final String finalFirstname = firstname;
            //               final String finalLastname = lastname;

            final ProgressDialog dialog = AppUtils.showProgress(LoginActivity.this, getString(R.string.msg_please_wait), getString(R.string.msg_please_wait));
            NetworkRequest request = new NetworkRequest(Request.Method.POST, Apis.SOCIAL_LOGIN, params,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            AppUtils.log(response.toString());
                            AppUtils.hideProgress(dialog);
                            String msg = response.optString("msg");
                            String code = response.optString("code");
                            if ("0".equals(code)) {
//                                            tetEmail.setError(msg);
//                                            tetEmail.requestFocus();
                                Snackbar.make(page, msg, Snackbar.LENGTH_LONG).show();
                            } else {


                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        setResult(RESULT_OK);
                                        finish();
                                    }
                                }, 2000);

                                JSONObject userdataObject = response.optJSONObject("userdata");
                                final String userID = (String) response.opt("id");
                                final String emailID = (String) response.opt("email");
                                String appToken = (String) response.opt("appToken");
                                AppInstance.getInstance(getApplicationContext()).addToSharedPref(AppUtils.PREF_USER_FNAME, finalFirstname);
                                //                    AppInstance.getInstance(getApplicationContext()).addToSharedPref(AppUtils.PREF_USER_LNAME, finalLastname);
                                AppInstance.getInstance(getApplicationContext()).addToSharedPref(AppUtils.PREF_USER_FATHER, "");
                                //                    AppInstance.getInstance(getApplicationContext()).addToSharedPref(AppUtils.PREF_USER_FAMILY, granpaName);
                                //                    AppInstance.getInstance(getApplicationContext()).addToSharedPref(AppUtils.PREF_USER_PHONE, mobile);

                                AppInstance.getInstance(getApplicationContext()).addToSharedPref(AppUtils.PREF_USER_EMAIL, emailID);
                                AppInstance.getInstance(getApplicationContext()).addToSharedPref(AppUtils.PREF_USER_PHONE, response.optString("phone"));
                                DBHelper db = new DBHelper(LoginActivity.this);
                                db.updateLoggedInUser(userID);

                                if (response.has("appToken"))
                                    AppInstance.getInstance(getApplicationContext()).setAuthkey(response.optString("appToken"));

                                AppUtils.log(msg);
                                if (response.optString("phone").isEmpty() || response.optString("email").isEmpty()) {
                                    Snackbar.make(page, getString(R.string.msg_account_created), Snackbar.LENGTH_LONG).show();

                                    final Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            Intent intent = new Intent(LoginActivity.this, PhoneRegistration.class);
                                            intent.putExtra("id", userID);
                                            intent.putExtra("from","social");
                                            intent.putExtra("devToken",devToken);
                                            intent.putExtra("email",email);
                                            startActivity(intent);
                                        }
                                    }, 200);
                                } else {
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {

                                            AppInstance.getInstance(getApplicationContext()).addToSharedPref(AppUtils.PREF_USER_ID, userID);
//                                            AppInstance.getInstance(getApplicationContext()).addToSharedPref(AppUtils.PREF_USER_EMAIL, emailID);
                                            AppInstance.getInstance(getApplicationContext()).addToSharedPref(AppUtils.PREF_LOGGED_IN, String.valueOf(true));
                                            DBHelper db = new DBHelper(LoginActivity.this);
                                            db.updateLoggedInUser(userID);
                                            setResult(RESULT_OK);
                                            if (AppUtils.Voucher_Data.length() > 0) {

                                                Intent intent = new Intent(LoginActivity.this, VoucherActivity.class);
                                                startActivity(intent);
                                                finish();
                                            } else {
                                                if (AppUtils.IS_FROM_PROCEED_TO_PAY) {
                                                    Intent intent = new Intent(LoginActivity.this, BasketActivity2.class);
                                                    System.out.println("IS_FROM_PROCEED_TO_PAY : " + "");
                                                    intent.putExtra("MinPrice", AppUtils.MINIMUM_PRICE);
                                                    startActivity(intent);
                                                    finish();
                                                } else if (AppUtils.IS_NOTIFICATION_CLICK) {
                                                    startActivity(new Intent(LoginActivity.this, CategoryListActivity.class));
                                                    finish();

                                                } else {
                                                    Intent intent = new Intent(LoginActivity.this, LocationActivity.class);
                                                    startActivity(intent);
                                                    finish();
                                                }

                                            }
                                        }
                                    }, 1000);
                                    Snackbar.make(page, getString(R.string.msg_login_success), Snackbar.LENGTH_LONG).show();
                                    if (FromSpalshPage) {

                                        if (AppUtils.IS_FROM_PROCEED_TO_PAY) {
                                            Intent intent = new Intent(LoginActivity.this, BasketActivity2.class);
                                            System.out.println("IS_FROM_PROCEED_TO_PAY : " + "");
                                            intent.putExtra("MinPrice", AppUtils.MINIMUM_PRICE);
                                            startActivity(intent);
                                            finish();
                                        } else if (AppUtils.IS_NOTIFICATION_CLICK) {
                                            startActivity(new Intent(LoginActivity.this, CategoryListActivity.class));
                                            finish();

                                        } /*else {
                                                Intent intent = new Intent(LoginActivity.this, LocationActivity.class);
                                                startActivity(intent);
                                                finish();
                                                FromSpalshPage = false;
                                            }*/

                                    }
                                }

                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.printStackTrace();
                            Snackbar snackbar = Snackbar.make(page, getString(R.string.msg_operation_not_completed), Snackbar.LENGTH_INDEFINITE);
                            snackbar.show();
                            AppUtils.hideProgress(dialog);
                        }
                    }
            );
            AppInstance.getInstance(getApplicationContext()).addToRequestQueue(request);

        } else {
            Snackbar snackbar = Snackbar.make(page, getString(R.string.msg_no_internet), Snackbar.LENGTH_INDEFINITE);
            snackbar.setActionTextColor(Color.RED);
            snackbar.show();
        }
    }


    private void googleLogin(GoogleSignInAccount account){

        params.clear();
            String firstname = "";
            String lastname = "";
            String name = account.getDisplayName();
            firstname = name;
            final String devToken = AppInstance.getInstance(getApplicationContext()).getFromSharedPref(AppUtils.SHARED_PREF_DEV_FIRE_BASE_TOKEN);

            if (devToken.length() == 0) {
                Toast.makeText(getApplicationContext(), getString(R.string.msg_no_token), Toast.LENGTH_LONG).show();
                return;
            }

            try {
                firstname = name.split(" ")[0];
                lastname = name.split(firstname)[1].trim();
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            final String email = account.getEmail().trim();
            final String password = tetPassword.getText().toString().trim();

            params.put("firstname", firstname);
            //       params.put("lastname", lastname);
            params.put("email", email);
            params.put("father_name", "");
            //            params.put("grand_father_name", granpaName);
            params.put("user_device_token", devToken);
            params.put("city", "");
            params.put("referal_code", "");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            params.put("social_id", Objects.requireNonNull(account.getId()));
        }

        Log.d("check_sign_up", "  test " + params);
//                    Snackbar.make(page, getString(R.string.msg_account_created), Snackbar.LENGTH_LONG).show();

            System.out.println("SignUpActivity : Param : " + params);
            if (AppUtils.isNetworkAvailable(getApplicationContext())) {

                final String finalFirstname = firstname;
                //               final String finalLastname = lastname;

                final ProgressDialog dialog = AppUtils.showProgress(LoginActivity.this, getString(R.string.msg_please_wait), getString(R.string.msg_please_wait));
                NetworkRequest request = new NetworkRequest(Request.Method.POST, Apis.SOCIAL_LOGIN, params,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                AppUtils.log(response.toString());
                                AppUtils.hideProgress(dialog);
                                String msg = response.optString("msg");
                                String code = response.optString("code");
                                if ("0".equals(code)) {
//                                            tetEmail.setError(msg);
//                                            tetEmail.requestFocus();
                                    Snackbar.make(page, msg, Snackbar.LENGTH_LONG).show();
                                } else {


                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            setResult(RESULT_OK);
                                            finish();
                                        }
                                    }, 2000);

                                    final String userID = (String) response.opt("id");
                                    final String emailID = (String) response.opt("email");
                                    AppInstance.getInstance(getApplicationContext()).addToSharedPref(AppUtils.PREF_USER_FNAME, finalFirstname);
                                    //                    AppInstance.getInstance(getApplicationContext()).addToSharedPref(AppUtils.PREF_USER_LNAME, finalLastname);
                                    AppInstance.getInstance(getApplicationContext()).addToSharedPref(AppUtils.PREF_USER_FATHER, "");
                                    //                    AppInstance.getInstance(getApplicationContext()).addToSharedPref(AppUtils.PREF_USER_FAMILY, granpaName);
                                    //                    AppInstance.getInstance(getApplicationContext()).addToSharedPref(AppUtils.PREF_USER_PHONE, mobile);

                                    AppInstance.getInstance(getApplicationContext()).addToSharedPref(AppUtils.PREF_USER_EMAIL, emailID);
                                    AppInstance.getInstance(getApplicationContext()).addToSharedPref(AppUtils.PREF_USER_PHONE, response.optString("phone"));
                                    DBHelper db = new DBHelper(LoginActivity.this);
                                    db.updateLoggedInUser(userID);
                                    if (response.has("appToken"))
                                        AppInstance.getInstance(getApplicationContext()).setAuthkey(response.optString("appToken"));


                                    AppUtils.log(msg);
                                    if (response.optString("phone").isEmpty()) {
                                        Snackbar.make(page, getString(R.string.msg_account_created), Snackbar.LENGTH_LONG).show();

                                        final Handler handler = new Handler();
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                Intent intent = new Intent(LoginActivity.this, PhoneRegistration.class);
                                                intent.putExtra("id", userID);
                                                intent.putExtra("from","social");
                                                intent.putExtra("devToken",devToken);
                                                intent.putExtra("email",email);
                                                startActivity(intent);
                                            }
                                        }, 200);
                                    }
                                    else{
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {

                                                AppInstance.getInstance(getApplicationContext()).addToSharedPref(AppUtils.PREF_USER_ID, userID);
//                                                AppInstance.getInstance(getApplicationContext()).addToSharedPref(AppUtils.PREF_USER_EMAIL, emailID);
                                                AppInstance.getInstance(getApplicationContext()).addToSharedPref(AppUtils.PREF_LOGGED_IN, String.valueOf(true));
                                                DBHelper db = new DBHelper(LoginActivity.this);
                                                db.updateLoggedInUser(userID);
                                                setResult(RESULT_OK);
                                                if (AppUtils.Voucher_Data.length() > 0) {

                                                    Intent intent = new Intent(LoginActivity.this, VoucherActivity.class);
                                                    startActivity(intent);
                                                    finish();
                                                } else {
                                                    if (AppUtils.IS_FROM_PROCEED_TO_PAY) {
                                                        Intent intent = new Intent(LoginActivity.this, BasketActivity2.class);
                                                        System.out.println("IS_FROM_PROCEED_TO_PAY : " + "");
                                                        intent.putExtra("MinPrice", AppUtils.MINIMUM_PRICE);
                                                        startActivity(intent);
                                                        finish();
                                                    } else if (AppUtils.IS_NOTIFICATION_CLICK) {
                                                        startActivity(new Intent(LoginActivity.this, CategoryListActivity.class));
                                                        finish();

                                                    } else {
                                                        Intent intent = new Intent(LoginActivity.this, LocationActivity.class);
                                                        startActivity(intent);
                                                        finish();
                                                    }

                                                }
                                            }
                                        }, 1000);
                                        Snackbar.make(page, getString(R.string.msg_login_success), Snackbar.LENGTH_LONG).show();
                                        if (FromSpalshPage) {

                                            if (AppUtils.IS_FROM_PROCEED_TO_PAY) {
                                                Intent intent = new Intent(LoginActivity.this, BasketActivity2.class);
                                                System.out.println("IS_FROM_PROCEED_TO_PAY : " + "");
                                                intent.putExtra("MinPrice", AppUtils.MINIMUM_PRICE);
                                                startActivity(intent);
                                                finish();
                                            } else if (AppUtils.IS_NOTIFICATION_CLICK) {
                                                startActivity(new Intent(LoginActivity.this, CategoryListActivity.class));
                                                finish();

                                            } /*else {
                                                Intent intent = new Intent(LoginActivity.this, LocationActivity.class);
                                                startActivity(intent);
                                                finish();
                                                FromSpalshPage = false;
                                            }*/

                                        }
                                    }

                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                error.printStackTrace();
                                Snackbar snackbar = Snackbar.make(page, getString(R.string.msg_operation_not_completed), Snackbar.LENGTH_INDEFINITE);
                                snackbar.show();
                                AppUtils.hideProgress(dialog);
                            }
                        }
                );
                AppInstance.getInstance(getApplicationContext()).addToRequestQueue(request);

            } else {
                Snackbar snackbar = Snackbar.make(page, getString(R.string.msg_no_internet), Snackbar.LENGTH_INDEFINITE);
                snackbar.setActionTextColor(Color.RED);
                snackbar.show();
            }


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return false;
    }

    public void onLoginButtonClicked(View view) {
        params.clear();
        if (isValidFields()) {
            String email = "";
            String password = "";

            email = tetEmail.getText().toString().trim();
            password = tetPassword.getText().toString().trim();
            String devToken = AppInstance.getInstance(getApplicationContext()).getFromSharedPref(AppUtils.SHARED_PREF_DEV_FIRE_BASE_TOKEN);


            Log.d("DeviceToken_afieat ", " test " + devToken);

            if (devToken.length() == 0) {
                Toast.makeText(getApplicationContext(), getString(R.string.msg_no_token), Toast.LENGTH_LONG).show();
                return;
            }
            params.put("username_login", email);
            params.put("password", password);
            params.put("user_device_token", devToken);
            params.put("registered_from", "2");


            Log.d("Request parameter ", " test " + params);

            if (AppUtils.isNetworkAvailable(getApplicationContext())) {
                // final ProgressDialog dialog = AppUtils.showProgress(LoginActivity.this, getString(R.string.msg_logging_in), getString(R.string.msg_please_wait));
                afieatGifLoaderDialog();

                final String finalEmail = email;
                final String finalPassword = password;
                try {
                    NetworkRequest request = new NetworkRequest(Request.Method.POST, Apis.LOG_IN, params,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(final JSONObject response) {
                                    //  AppUtils.hideProgress(dialog);
                                    afieatGifLoaderDialog.dismiss();
                                    AppUtils.log(response + "");
                                    System.out.println("Rahul : loginResponse : " + response);
                                    if ("0".equals(response.optString("status"))) {
                                        Snackbar.make(page, getString(R.string.msg_login_fail), Snackbar.LENGTH_LONG).show();
                                    } else if ("1".equals(response.optString("status"))) {
                                        id = response.optString("id");
                                        String name = response.optString("name");
                                        String phone = response.optString("phone");

                                        AppInstance.getInstance(getApplicationContext()).addToSharedPref(AppUtils.PREF_USER_ID, id);
                                        AppInstance.getInstance(getApplicationContext()).addToSharedPref(AppUtils.PREF_USER_FNAME, name);
                                        AppInstance.getInstance(getApplicationContext()).addToSharedPref(AppUtils.PREF_USER_PHONE, phone);
                                        AppInstance.getInstance(getApplicationContext()).addToSharedPref(AppUtils.MY_POINTS, response.optString("point_value"));
                                        AppInstance.getInstance(getApplicationContext()).addToSharedPref(AppUtils.MY_POINTS_VALUE, response.optString("point"));


                                        if (response.has("appToken"))
                                            AppInstance.getInstance(getApplicationContext()).setAuthkey(response.optString("appToken"));

                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {

                                                AppInstance.getInstance(getApplicationContext()).addToSharedPref(AppUtils.SHARED_PREF_USER_PASSWORD, finalPassword);
                                                AppInstance.getInstance(getApplicationContext()).addToSharedPref(AppUtils.PREF_USER_EMAIL, finalEmail);
                                                AppInstance.getInstance(getApplicationContext()).addToSharedPref(AppUtils.PREF_LOGGED_IN, String.valueOf(true));
                                                DBHelper db = new DBHelper(LoginActivity.this);
                                                db.updateLoggedInUser(id);
                                                setResult(RESULT_OK);
                                                if (AppUtils.Voucher_Data.length() > 0) {

                                                    Intent intent = new Intent(LoginActivity.this, VoucherActivity.class);
                                                    startActivity(intent);
                                                    finish();
                                                } else {
                                                    if (AppUtils.IS_FROM_PROCEED_TO_PAY) {
                                                        Intent intent = new Intent(LoginActivity.this, BasketActivity2.class);
                                                        System.out.println("IS_FROM_PROCEED_TO_PAY : " + "");
                                                        intent.putExtra("MinPrice", AppUtils.MINIMUM_PRICE);
                                                        startActivity(intent);
                                                        finish();
                                                    } else if (AppUtils.IS_NOTIFICATION_CLICK) {
                                                        startActivity(new Intent(LoginActivity.this, CategoryListActivity.class));
                                                        finish();

                                                    } else {
                                                        Intent intent = new Intent(LoginActivity.this, LocationActivity.class);
                                                        startActivity(intent);
                                                        finish();
                                                    }

                                                }
                                            }
                                        }, 1000);
                                        Snackbar.make(page, getString(R.string.msg_login_success), Snackbar.LENGTH_LONG).show();
                                        if (FromSpalshPage) {

                                            if (AppUtils.IS_FROM_PROCEED_TO_PAY) {
                                                Intent intent = new Intent(LoginActivity.this, BasketActivity2.class);
                                                System.out.println("IS_FROM_PROCEED_TO_PAY : " + "");
                                                intent.putExtra("MinPrice", AppUtils.MINIMUM_PRICE);
                                                startActivity(intent);
                                                finish();
                                            } else if (AppUtils.IS_NOTIFICATION_CLICK) {
                                                startActivity(new Intent(LoginActivity.this, CategoryListActivity.class));
                                                finish();

                                            } /*else {
                                                Intent intent = new Intent(LoginActivity.this, LocationActivity.class);
                                                startActivity(intent);
                                                finish();
                                                FromSpalshPage = false;
                                            }*/

                                        }

                                    }

                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    //AppUtils.hideProgress(dialog);
                                    afieatGifLoaderDialog.dismiss();
                                    error.printStackTrace();
                                }
                            }
                    );
                    AppInstance.getInstance(getApplicationContext()).addToRequestQueue(request);

                } catch (Exception e) {
                    e.printStackTrace();
                    //   AppUtils.hideProgress(dialog);
                    afieatGifLoaderDialog.dismiss();
                }

            }
        }
    }

    private boolean isValidFields() {
        boolean flag = true;
        if (!(isNumeric(tetEmail.getText().toString().trim()))) {
            if (!AppUtils.isValidEmail(tetEmail.getText().toString().trim())) {
                tetEmail.setError(getString(R.string.msg_email_invalid));
                flag = false;
            }
        }

        if (tetPassword.getText().toString().trim().length() == 0) {
            tetPassword.setError(getString(R.string.msg_field_mandatory));
            flag = false;
        }
        return flag;
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.exit_in, R.anim.exit_out);
    }

    public static boolean isNumeric(String email) {
        return email.matches("-?\\d+(\\.\\d+)?");
    }

    //===================================Register Extra


    public void onSignUpButtonClicked(View view) {
        Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
        startActivity(intent);
    }

    public void onClickForgetPassword(View view) {

        Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
        startActivity(intent);
    }

    public void onClickSkipLogin(View view) {
        FromSpalshPage = false;
        Intent intent = new Intent(LoginActivity.this, LocationActivity.class);
        startActivity(intent);

    }


    private void checkForLoggedInStatus() {


        String lang = appInstance.getFromSharedPref(AppUtils.PREF_USER_LANG);
        if ("ar".equals(lang)) {
            temp_navitem = navItems_ar;
        } else {
            temp_navitem = navItems;
        }

        List<String> navList = new ArrayList<>(Arrays.asList(temp_navitem));
        List<Integer> navIconsList = new ArrayList<>();
        for (int navIcon : navIcons) {
            navIconsList.add(navIcon);
        }
        mUserId = appInstance.getFromSharedPref(AppUtils.PREF_USER_ID);
        loginStatus = appInstance.getFromSharedPref(AppUtils.PREF_LOGGED_IN);
       /* View userPanel = findViewById(R.id.navHeader);
        assert userPanel != null;
        TextView tvUsername = (TextView) userPanel.findViewById(R.id.tvUsername);
        TextView tvUserEmail = (TextView) userPanel.findViewById(R.id.tvUserEmail);
        AppUtils.log("UserId: " + mUserId);
*/
        if (loginStatus.equalsIgnoreCase("true")) {
            // logged in

            // Sunit 27-01-2017


            if (!navList.contains("Sign Out") && !navList.contains("خروج")) {
                AppUtils.log("test11");

                if ("ar".equals(appInstance.getFromSharedPref(AppUtils.PREF_USER_LANG))) {
                    // navList.add("خروج");

                } else {

                  /*  navList.add("Sign Out");
                    navList.remove(navList.indexOf("Login"));
                    navIconsList.remove(navIconsList.indexOf(R.drawable.login_24));
                    navList.remove(navList.indexOf("Login"));
                    navIconsList.remove(navIconsList.indexOf(R.drawable.login_24));


                    AppUtils.log("test11-33");*/
                }

                navIconsList.add(R.drawable.nav_signout);


               /* AppUtils.showViews(userPanel);
                ((TextView) userPanel.findViewById(R.id.LogInBtn)).setVisibility(View.GONE);
                tvUsername.setText(appInstance.getFromSharedPref(AppUtils.PREF_USER_FNAME));
                AppUtils.log("@@ CALL- Callll" + appInstance.getFromSharedPref(AppUtils.PREF_USER_FNAME));
                tvUserEmail.setText(appInstance.getFromSharedPref(AppUtils.PREF_USER_EMAIL));
                tvUsername.setVisibility(View.VISIBLE);
                tvUserEmail.setVisibility(View.VISIBLE);*/
            }
        } else {

            // not loggeed in
            //     AppUtils.hideViews(userPanel);

            if (navList.contains("Sign Out") || navList.contains("خروج")) {
                AppUtils.log("test22");
                if ("ar".equals(appInstance.getFromSharedPref(AppUtils.PREF_USER_LANG))) {
                    navList.remove(navList.indexOf("خروج"));
                    navList.remove(navList.indexOf("تسجيل الدخول"));
                    //  navList.remove(navList.indexOf("حسابي"));
                    AppUtils.log("test11-44");
                } else {
                    navList.remove(navList.indexOf("Sign Out"));
                    navList.remove(navList.indexOf("Login"));
                    //   navList.remove(navList.indexOf("My Deals"));

                    AppUtils.log("test11-55");
                }
                navIconsList.remove(navIconsList.indexOf(R.drawable.login));
                //navIconsList.remove(navIconsList.indexOf(R.drawable.nav_deal));

               /* AppUtils.showViews(userPanel);
                ((TextView) userPanel.findViewById(R.id.LogInBtn)).setVisibility(View.VISIBLE);
                tvUsername.setVisibility(View.GONE);
                tvUserEmail.setVisibility(View.GONE);*/
            } else {

                if (navList.contains("Login") || navList.contains("تسجيل الدخول")) {
                    if ("ar".equals(appInstance.getFromSharedPref(AppUtils.PREF_USER_LANG))) {

                        navList.remove(navList.indexOf("تسجيل الدخول"));
//                        navList.remove(navList.indexOf("حسابي"));
                    } else {
                        navList.remove(navList.indexOf("Login"));
                        // navList.remove(navList.indexOf("My Deals"));
                    }

                    navIconsList.remove(navIconsList.indexOf(R.drawable.login));
                    //  navIconsList.remove(navIconsList.indexOf(R.drawable.nav_deal));
                }


            }
         /*   AppUtils.showViews(userPanel);
            ((TextView) userPanel.findViewById(R.id.LogInBtn)).setVisibility(View.VISIBLE);
            tvUsername.setVisibility(View.GONE);
            tvUserEmail.setVisibility(View.GONE);*/
        }

        temp_navitem = new String[navList.size()];
        temp_navicon = new int[navIconsList.size()];
        for (int i = 0; i < navList.size(); i++) {
            temp_navitem[i] = navList.get(i);
            temp_navicon[i] = navIconsList.get(i);
        }
        AppUtils.log("Changing menu");
        ((NavListAdapter) lvNav.getAdapter()).notifyDataSetChanged();
    }


    class NavListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return temp_navicon.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            String item = temp_navitem[position];
            int drawable = temp_navicon[position];
/*
         //   View view= LayoutInflater.from(LoginActivity.this).inflate(R.layout.layout_nav_list_item_new, null, false);
            TextView view = (TextView) LayoutInflater.from(LoginActivity.this).inflate(R.layout.layout_nav_list_item, null, false);
            view.setText(item);
            //ImageView nav_img=(ImageView) LayoutInflater.from(LoginActivity.this).inflate(R.layout.layout_nav_list_item, null, false);
            //  if (position == 1)
            //  view.setTextColor(Color.parseColor("#C63A2B"));
           // TextView txtview=view.findViewById(R.id.tvMenuTitle);
            //txtview.setText(item);
            //ImageView nav_img= view.findViewById(R.id.nav_img);
            //nav_img.setBackgroundResource(drawable);
            view.setCompoundDrawablesWithIntrinsicBounds(drawable, 0, 0, 0);
            view.setCompoundDrawablePadding(55);*/
            View viewNavList = LayoutInflater.from(LoginActivity.this).inflate(R.layout.layout_nav_list_item_new, null, false);
            //TextView view = (TextView) LayoutInflater.from(LocationActivity.this).inflate(R.layout.layout_nav_list_item, null, false);

            TextView tvMenuTitle = viewNavList.findViewById(R.id.tvMenuTitle);
            tvMenuTitle.setText(item);
            if ("ar".equals(AppInstance.getInstance(getApplicationContext()).getFromSharedPref(AppUtils.PREF_USER_LANG))) {
                tvMenuTitle.setCompoundDrawablesWithIntrinsicBounds(0, 0, drawable, 0);
            } else {
                tvMenuTitle.setCompoundDrawablesWithIntrinsicBounds(drawable, 0, 0, 0);

            }
            tvMenuTitle.setCompoundDrawablePadding(55);
            //ImageView img=viewNavList.findViewById(R.id.img);


            // img.setBackgroundResource(drawable);

            return viewNavList;
        }

    }

    private class NavMenuItemClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(final AdapterView<?> parent, View view, final int position, long id) {
            drawerLayout.closeDrawer(mNavigationView);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    System.out.println("Rahul  : NavMenuItemClickListener :  " + position);
                    Intent intent;
                    switch (position) {
                        case 0:
                             /*   //TODO: Change preferred address
                                intent = new Intent(LoginActivity.this, LoginActivity.class);
                                startActivity(intent);*/
                            FromSpalshPage = false;
                            Intent intente = new Intent(LoginActivity.this, LocationActivity.class);
                            startActivity(intente);


                            break;
                        case 1:
                            startActivity(new Intent(LoginActivity.this, LanguageSelectionActivity.class));

                            break;
                        case 2:
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/afieat"));
                            startActivity(browserIntent);
                            break;
                        case 3:
                            if (loginStatus.equalsIgnoreCase("true")) {
                                intent = new Intent(LoginActivity.this, DealsActivity.class);
                            } else {
                                intent = new Intent(LoginActivity.this, HelpCenterActivity.class);
                            }
                            startActivity(intent);

                            break;
                        case 4:
                          /*  if (mUserId.length() > 0) {
                                intent = new Intent(LocationActivity.this, DealsActivity.class);
                                startActivity(intent);
                            } else {
                                intent = new Intent(LocationActivity.this, HelpCenterActivity.class);
                                startActivity(intent);
                            }*/
                            intent = new Intent(LoginActivity.this, AboutusActivity.class);
                            startActivity(intent);
                            break;
                        case 6:
                         /*   if (mUserId.length() > 0) {
                                intent = new Intent(LocationActivity.this, DealsActivity.class);
                                startActivity(intent);
                            } else {
                                intent = new Intent(LocationActivity.this, HelpCenterActivity.class);
                                startActivity(intent);
                            }
*/
                            //TODO: Sign Out

                            appInstance.setAuthkey("");
                            AppUtils.APPTOKEN = "";
                            AppInstance.getInstance(getApplicationContext()).addToSharedPref(AppUtils.PREF_USER_ID, "");
                            AppInstance.getInstance(getApplicationContext()).addToSharedPref(AppUtils.PREF_LOGGED_IN, String.valueOf(false));
                            checkForLoggedInStatus();
                            Snackbar.make(findViewById(R.id.page), getString(R.string.msg_logout_success), Snackbar.LENGTH_SHORT).show();
                            break;
                        case 7:
                            if (loginStatus.equalsIgnoreCase("true")) {
                                startActivityForResult(new Intent(LoginActivity.this, LanguageSelectionActivity.class), AppUtils.REQ_CHANGE_LANG);
                            } else {
                                startActivityForResult(new Intent(LoginActivity.this, HelpCenterActivity.class), AppUtils.REQ_CHANGE_LANG);
                            }

                            break;
                        case 8:
                            startActivityForResult(new Intent(LoginActivity.this, HelpCenterActivity.class), AppUtils.REQ_CHANGE_LANG);
                            break;
                        case 9:
                            //TODO: Sign Out

                            appInstance.setAuthkey("");
                            AppUtils.APPTOKEN = "";
                            AppInstance.getInstance(getApplicationContext()).addToSharedPref(AppUtils.PREF_USER_ID, "");
                            AppInstance.getInstance(getApplicationContext()).addToSharedPref(AppUtils.PREF_LOGGED_IN, String.valueOf(false));
                            checkForLoggedInStatus();
                            Snackbar.make(findViewById(R.id.page), getString(R.string.msg_logout_success), Snackbar.LENGTH_SHORT).show();
                            break;
                        default:
                            break;  }
                }
            }, 250);
        }
    }

    private void afieatGifLoaderDialog() {
        afieatGifLoaderDialog = new Dialog(this);
        afieatGifLoaderDialog.setContentView(R.layout.afieat_gif_loader_dialog);
        afieatGifLoaderDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        afieatGifLoaderDialog.show();
    }

}
