package com.vlk.multimager.views;

import android.app.Dialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.vlk.multimager.R;

/**
 * Created by vansikrishna on 08/06/2016.
 */
public class CustomProgressDialog extends Dialog {

    private TextView messageTextView;
    private ProgressBar progressBar;
    private String message;

    public CustomProgressDialog(Context context) {
        super(context);
    }

    public void setMessage(String msg) {
        message = msg;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(0));
        setContentView(R.layout.custom_progress_dialog);
        messageTextView = (TextView) findViewById(R.id.messageTextView);
        progressBar = (ProgressBar) findViewById(R.id.waitProgressBar);
        progressBar.getIndeterminateDrawable()
                .setColorFilter(fetchAccentColor(), PorterDuff.Mode.SRC_IN);
        getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    }

    private int fetchAccentColor() {
        TypedValue typedValue = new TypedValue();
        TypedArray a = getContext().obtainStyledAttributes(typedValue.data, new int[]{R.attr.colorAccent});
        int color = a.getColor(0, 0);
        a.recycle();
        return color;
    }

    @Override
    public void show() {
        super.show();
        messageTextView.setText(message);
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }
}
