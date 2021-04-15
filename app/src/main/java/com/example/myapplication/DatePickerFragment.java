package com.example.myapplication;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TableRow;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import androidx.fragment.app.DialogFragment;

public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    private TextView dateView;
    private TableRow repeaterRow;

    private SimpleDateFormat dateFormatter;

    public DatePickerFragment(TextView _dateView, TableRow _repetearRow) {
        this.dateView = _dateView;
        this.repeaterRow = _repetearRow;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);

        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        Log.d("Day", "day set");
        Calendar newDate = Calendar.getInstance();
        newDate.set(year, monthOfYear, dayOfMonth);
        dateView.setText(dateFormatter.format(newDate.getTime()));
        repeaterRow.setVisibility(View.VISIBLE);
    }
}
