package com.afieat.ini.fragments;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import android.widget.TimePicker;

import com.afieat.ini.interfaces.OnTimePickListener;

import java.util.Calendar;

/**
 * Created by amartya on 24/05/16 with love.
 */
public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    private OnTimePickListener listener;

    public TimePickerFragment() {
        this.listener = null;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar calendar = Calendar.getInstance();
        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        return new TimePickerDialog(getActivity(), this, hourOfDay, minute, true);
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        listener.onTimePicked(hourOfDay, minute);
    }

    public void setOnTimePickedListener(OnTimePickListener listener) {
        this.listener = listener;
    }

}
