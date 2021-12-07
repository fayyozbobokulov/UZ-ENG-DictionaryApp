package com.example.dictionaryapp.utils.helpers;

import android.app.DatePickerDialog;
import android.content.Context;

import java.util.Calendar;

public class DatePickerDialogHelper {

    private static DatePickerDialogHelper INSTANCE = null;

    private DatePickerDialogHelper() {

    }

    public static DatePickerDialogHelper getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new DatePickerDialogHelper();
        }
        return INSTANCE;
    }

    public void showDatePickerDialog(Context fragmentManager, DatePickerHelperInterface result) {
        Calendar now = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(fragmentManager, (view, year, month, dayOfMonth) -> {
            String selectedTime = "";
            if (dayOfMonth <= 9) {
                selectedTime += "0";
            }
            selectedTime += String.valueOf(dayOfMonth);
            selectedTime += ".";
            if (month <= 9) {
                selectedTime += "0";
            }
            selectedTime += String.valueOf(month + 1);
            selectedTime += ".";
            selectedTime += String.valueOf(year);
            result.dateSelected(selectedTime);
        },
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        dialog.show();
    }

    @FunctionalInterface
    public interface DatePickerHelperInterface {
        void dateSelected(String selectedTime);
    }
}