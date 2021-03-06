package com.vlk.multimager.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.snackbar.Snackbar;
import com.vlk.multimager.R;

/**
 * Created by vansikrishna on 08/06/2016.
 */
public class Utils {

    public static void loge(String className, String message) {
        Log.e(className, message);
    }

    public static void showShortSnack(View parent, String message){
        Snackbar snackbar = Snackbar.make(parent, message, Snackbar.LENGTH_SHORT);
        TextView textView = (TextView) snackbar.getView().findViewById(R.id.snackbar_text);
        textView.setMaxLines(10);
        snackbar.show();
    }

    public static void showLongSnack(View parent, String message){
        Snackbar snackbar = Snackbar.make(parent, message, Snackbar.LENGTH_LONG);
        TextView textView = (TextView) snackbar.getView().findViewById(R.id.snackbar_text);
        textView.setMaxLines(10);
        snackbar.show();
    }

    public static void initToolBar(AppCompatActivity activity, Toolbar toolbar, boolean homeUpIndicator){
        activity.setSupportActionBar(toolbar);
        final ActionBar ab = activity.getSupportActionBar();
        ab.setDisplayShowHomeEnabled(false);
        ab.setDisplayHomeAsUpEnabled(homeUpIndicator);
        ab.setDisplayShowCustomEnabled(true);
        ab.setDisplayShowTitleEnabled(false);
    }

    public static boolean hasCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
        } else {
            return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
        }
    }

    public static boolean hasCameraFlashHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA_FLASH)) {
            return true;
        } else {
            return false;
        }
    }

    public static void setViewBackgroundColor(Activity activity, View view, int color){
        if(color == 0)
            return;
        if(view instanceof ImageButton){
            AppCompatImageButton imageButton = (AppCompatImageButton) view;
            GradientDrawable drawable = (GradientDrawable) imageButton.getBackground();
            drawable.setColor(color);
            if(Build.VERSION.SDK_INT >= 16)
                imageButton.setBackground(drawable);
            else
                imageButton.setBackgroundDrawable(drawable);
        }
        else if(view instanceof ImageView){
            ImageView imageView = (ImageView) view;
            GradientDrawable drawable = (GradientDrawable) imageView.getBackground();
            drawable.setColor(color);
            if(Build.VERSION.SDK_INT >= 16)
                imageView.setBackground(drawable);
            else
                imageView.setBackgroundDrawable(drawable);
        }
        else if(view instanceof Toolbar){
            Toolbar toolbar = (Toolbar) view;
            toolbar.setBackgroundColor(color);
            if(Build.VERSION.SDK_INT >= 21) {
                Window window = activity.getWindow();
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(getDarkColor(color));
            }
        }
    }

    public static void setButtonTextColor(View view, int color) {
        if(color == 0)
            return;
        if (view instanceof Button) {
            ((Button)view).setTextColor(color);
        }
    }

    public static void setViewsColorStateList(View view, int normalColor, int darkenedColor) {
        int[][] states = new int[][] {new int[] { android.R.attr.state_enabled}, new int[] { android.R.attr.state_pressed}};
        int[] colors = new int[]{normalColor, darkenedColor};
        ColorStateList colorStateList = new ColorStateList(states, colors);
        if(view instanceof AppCompatButton){
            ((AppCompatButton)view).setSupportBackgroundTintList(colorStateList);
        }
    }

    public static void setViewsColorStateList(int normalColor, int darkenedColor, View... views) {
        int[][] states = new int[][] {new int[0] , new int[] { android.R.attr.state_pressed}};
        int[] colors = new int[]{normalColor, darkenedColor};
        ColorStateList colorStateList = new ColorStateList(states, colors);
        for (View view : views) {
            if(view instanceof AppCompatButton){
                if(Build.VERSION.SDK_INT >= 21)
                    ((AppCompatButton)view).setBackgroundTintList(colorStateList);
                else
                    ((AppCompatButton)view).setSupportBackgroundTintList(colorStateList);
            }
            if(view instanceof AppCompatImageView){
                ((AppCompatImageView)view).setColorFilter(normalColor);
            }
            if(view instanceof AppCompatTextView){
                ((AppCompatTextView)view).setTextColor(normalColor);
            }
        }
    }

    public static int getDarkColor(int color){
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        return Color.rgb((int)(red * 0.8), (int)(green * 0.8), (int)(blue * 0.8));
    }

    public static int getLightColor(int color){
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        return Color.argb((int)(255*0.5), red, green, blue);
    }
}
