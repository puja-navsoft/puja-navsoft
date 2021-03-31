package com.afieat.ini;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;

import com.afieat.ini.Security.WithOutLoginSecurityPermission;
import com.afieat.ini.utils.AppInstance;
import com.afieat.ini.utils.AppUtils;
import com.afieat.ini.utils.NetworkRequest;
import com.amazonaws.mobileconnectors.pinpoint.PinpointManager;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import pl.droidsonroids.gif.AnimationListener;

public class SplashActivity extends AppCompatActivity {

    public static PinpointManager pinpointManager;
    private AppInstance appInstance = null;
    private String mUserId = "";
    public static final String LOG_TAG = SplashActivity.class.getSimpleName();
    public boolean aminDone, networkDone = false;
    private String video_name = "";
    private VideoView mVideoView;
    private ImageView ivSplash;
    private static final String baseLiveUrl = "https://afieat.com/webs/getlaunchingvideo";
    private static final String baseDevUrl = "http://34.205.164.176/webs/getlaunchingvideo";

    @Override
    protected void onStart() {
        super.onStart();

        Intent intent = getIntent();
        // String action = intent.getAction();
        Uri data = intent.getData();

        System.out.println("Firebase : data : " + data);
        // System.out.println("Firebase : data : AppInv "+   AppInviteReferral.getDeepLink(intent));

     /*   FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                        // Get deep link from result (may be null if no link is found)
                        Uri deepLink = null;
                        if (pendingDynamicLinkData != null) {
                            deepLink = pendingDynamicLinkData.getLink();
                            System.out.println("Firebase : "+pendingDynamicLinkData.zzrw());
                            System.out.println("Firebase deepLink : "+deepLink);


                        }
                        // Handle the deep link. For example, open the linked
                        // content, or apply promotional credit to the user's
                        // account.
                        // ...

                        // ...
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println("Firebase DL : "+e.getMessage());
                    }
                });*/
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //AWSMobileClient.getInstance().initialize(this).execute();
        setContentView(R.layout.activity_splash);
        mVideoView = findViewById(R.id.vvSplash);
        ivSplash = findViewById(R.id.ivSplash);
        Intent intent = getIntent();
        // String action = intent.getAction();
        Uri data = intent.getData();

        requestLoadVideoSplash();

        System.out.println("Firebase : data : " + data);

        AnimationListener animationListener = new AnimationListener() {
            @Override
            public void onAnimationCompleted(int loopNumber) {
                System.out.println("onAnimationCompleted : ");

                aminDone = true;
                if (aminDone && networkDone) {
                   /* startActivity(new Intent(SplashActivity.this, LocationActivity.class));
                    finish();*/
                }

            }
        };

       /*  mHandler=new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {

            //    if()
                return false;
            }
        });
*/


/*


        System.out.println("Rahul : SplashActivity : getNavigationBarHeight :"+getNavigationBarHeight());
        System.out.println("Rahul : SplashActivity : hasSoftKeys :"+AppUtils.hasSoftKeys(this));

*/

        //sourav(17.01.2018)
        /*if (pinpointManager == null) {
            PinpointConfiguration pinpointConfig = new PinpointConfiguration(
                    getApplicationContext(),
                    AWSMobileClient.getInstance().getCredentialsProvider(),
                    AWSMobileClient.getInstance().getConfiguration());

            pinpointManager = new PinpointManager(pinpointConfig);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        String deviceToken =
                                InstanceID.getInstance(SplashActivity.this).getToken(
                                        "243112180094",
                                        GoogleCloudMessaging.INSTANCE_ID_SCOPE);
                        Log.e("NotError", deviceToken);
                        pinpointManager.getNotificationClient()
                                .registerGCMDeviceToken(deviceToken);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }*/

       /* Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setClassName("in.amazon.mShop.android.shopping", "in.amazon.mShop.android.shopping.LandingActivity");
        startActivity(intent);*/


/*
        Intent launchIntent = getPackageManager().getLaunchIntentForPackage("in.amazon.mShop.android.shopping");
        startActivity(launchIntent);*/
        //p---------------------

        /*GifImageView mGigImageView = (GifImageView) findViewById(R.id.bikeLoader);

        GifDrawable gifDrawable = null;

        try {
            gifDrawable = new GifDrawable(getResources(), R.drawable.new_gif_loader);
            gifDrawable.addAnimationListener(animationListener);
            gifDrawable.setLoopCount(1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mGigImageView.setImageDrawable(gifDrawable);*/


        String lang = AppInstance.getInstance(getApplicationContext()).getFromSharedPref(AppUtils.PREF_USER_LANG);
        if ("".equals(lang)) {
            lang = "ar";
            AppInstance.getInstance(getApplicationContext()).addToSharedPref(AppUtils.PREF_USER_LANG, lang);
        }
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());

        appInstance = AppInstance.getInstance(getApplicationContext());
        mUserId = appInstance.getFromSharedPref(AppUtils.PREF_USER_ID);


        String deviceId = Settings.Secure.getString(this.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        AppUtils.log("@@ Online- " + AppUtils.isNetworkAvailable(getApplicationContext()));
        if (AppUtils.isNetworkAvailable(getApplicationContext()))
            new WithOutLoginSecurityPermission(new WithOutLoginSecurityPermission.OnSecurityResult() {
                @Override
                public void OnAllowPermission(String AuthToken) {
                    AppUtils.AUTHRIZATIONKEY = AuthToken;
                    Calendar calendar = Calendar.getInstance();
                    SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
                    String DateCreate = format.format(calendar.getTime());
                    AppInstance.getInstance(SplashActivity.this).setAuthkeyforall(AuthToken, DateCreate);
                    if (mUserId.length() > 0) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                AppUtils.AUTHRIZATIONKEY = AppInstance.getInstance(getApplicationContext()).getAuthkeyforall();
                             /*   startActivity(new Intent(SplashActivity.this, LocationActivity.class));
                                finish();*/
                                networkDone = true;
                                if (aminDone && networkDone) {
                                    startActivity(new Intent(SplashActivity.this, LocationActivity.class));
                                    finish();
                                }
                            }
                        }, 2000);

                    } else {
                        LoginActivity.FromSpalshPage = true;
                        networkDone = true;
                        if (aminDone && networkDone) {
                            startActivity(new Intent(SplashActivity.this, LocationActivity.class));
                            finish();
                        }
                     /*   startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                        finish();*/
                    }
                    AppUtils.log("@@ APPTOKEN-" + AuthToken);
                }

                @Override
                public void OnRejectPermission() {
                    Log.e("Auth", "AUTHRIZATIONKEY NOT FOUND");
                }
            }).execute(deviceId);
        else
            Toast.makeText(getApplicationContext(), getString(R.string.msg_no_internet), Toast.LENGTH_LONG).show();


    }

    private void requestLoadVideoSplash() {

        NetworkRequest request = new NetworkRequest(Request.Method.GET, baseLiveUrl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println("Rahul : SplashActivity : requestLoadVideoSplash : response : " + response);

                        try {
                            video_name = response.getString("video_name");
                            String[] fileExt = video_name.split("\\.(?=[^\\.]+$)");
                            findViewById(R.id.bikeLoader).setVisibility(View.GONE);

                            if(fileExt[1].equalsIgnoreCase("png") ||
                                    fileExt[1].equalsIgnoreCase("jpeg") ||
                                    fileExt[1].equalsIgnoreCase("jpg")){

                                mVideoView.setVisibility(View.GONE);
                                ivSplash.setVisibility(View.VISIBLE);
                                imageSplash(video_name);
                            }
                            else{
                                mVideoView.setVisibility(View.VISIBLE);
                                ivSplash.setVisibility(View.GONE);
                                videoSplash(video_name);
                            }
                            //mVideoView.setMediaController(mMediaController);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();

                    }
                }
        );
        AppInstance.getInstance(getApplicationContext()).addToRequestQueue(request);
    }

    private void videoSplash(String argVideoName) {

        mVideoView.setVideoURI(Uri.parse("https://d22vvrqrexhw5s.cloudfront.net/upload/" + argVideoName));
        mVideoView.requestFocus();

        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {

                startActivity(new Intent(SplashActivity.this, LocationActivity.class));
                finish();
            }
        });

        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mVideoView.start();
                mediaPlayer.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
                    @Override
                    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
                        /*MediaController mediaController = new MediaController(SplashActivity.this);
                        mVideoView.setMediaController(mediaController);
                        mediaController.setAnchorView(mVideoView);*/
                    }
                });
            }
        });

        mVideoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                return false;
            }
        });
    }

    private void imageSplash(String imageName){
        Glide.with(getApplicationContext())
                .load("https://d22vvrqrexhw5s.cloudfront.net/upload/"+ imageName)
                .error(R.drawable.splash_bg_image)
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        ivSplash.setImageDrawable(resource);
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                startActivity(new Intent(SplashActivity.this, LocationActivity.class));
                                finish();
                            }
                        }, 3000);
                        return false;
                    }
                })
                .into(ivSplash);
    }



/*    @Override
    protected void onPause() {
        super.onPause();

        // unregister notification receiver
        LocalBroadcastManager.getInstance(this).unregisterReceiver(notificationReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // register notification receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(notificationReceiver,
                new IntentFilter(PushListenerService.ACTION_PUSH_NOTIFICATION));
    }

    private final BroadcastReceiver notificationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(LOG_TAG, "Received notification from local broadcast. Display it in a dialog.");

            Bundle data = intent.getBundleExtra(PushListenerService.INTENT_SNS_NOTIFICATION_DATA);
            String message = PushListenerService.getMessage(data);

            new AlertDialog.Builder(SplashActivity.this)
                    .setTitle("Push notification")
                    .setMessage(message)
                    .setPositiveButton(android.R.string.ok, null)
                    .show();
        }
    };*/

    public boolean isNavigationBarAvailable() {

        boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
        boolean hasHomeKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_HOME);

        return (!(hasBackKey && hasHomeKey));
    }

    private int getNavigationBarHeight() {
        Resources resources = getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return resources.getDimensionPixelSize(resourceId);
        }
        return 0;
    }


}
