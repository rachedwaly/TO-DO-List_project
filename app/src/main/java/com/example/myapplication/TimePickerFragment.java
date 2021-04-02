package com.example.myapplication;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.fragment.app.DialogFragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class TimePickerFragment extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {

    private TextView timeView;

    private SimpleDateFormat timeFormatter;

    public TimePickerFragment(TextView _timeView) {
        this.timeView = _timeView;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        timeFormatter = new SimpleDateFormat("HH:mm");

        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute,
                true);
    }

    public void onTimeSet(TimePicker view, int hour, int min) {
        // Do something with the time chosen by the user
        Calendar newTime = Calendar.getInstance();
        newTime.set(Calendar.HOUR_OF_DAY, hour);
        newTime.set(Calendar.MINUTE, min);
        timeView.setText(timeFormatter.format(newTime.getTime()));
    }
}
