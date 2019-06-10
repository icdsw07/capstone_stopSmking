package com.example.stopsmoking.subFragment;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.stopsmoking.subClass.AlarmReceiver;

import java.util.Calendar;
import java.util.Locale;

import static android.content.Context.ALARM_SERVICE;

public class TimePickerFragment2 extends DialogFragment implements TimePickerDialog.OnTimeSetListener{

    AlarmManager alarmManager;
    Intent recevierIntent;
    PendingIntent pendingIntent;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        final Calendar calendar = Calendar.getInstance(Locale.KOREA);
        int hour_now = calendar.get(Calendar.HOUR_OF_DAY);
        int minute_now = calendar.get(Calendar.MINUTE);
        alarmManager = (AlarmManager)getActivity().getSystemService(ALARM_SERVICE);
        recevierIntent = new Intent(getActivity(), AlarmReceiver.class);

        //getView().findViewById(R.id.home_setting)
        TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), AlertDialog.THEME_DEVICE_DEFAULT_DARK,
                this, hour_now, minute_now, false);
        TextView titleText = new TextView(getActivity());
        titleText.setText("측정 시간 설정2");
        titleText.setBackgroundColor(Color.parseColor("#ffEEE8AA"));
        titleText.setPadding(5, 3, 5, 3);
        titleText.setGravity(Gravity.CENTER_HORIZONTAL);
        timePickerDialog.setCustomTitle(titleText);

        return timePickerDialog;
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
        Toast.makeText(getActivity(), "측정 예정 시간 : " + hourOfDay + "시 " + minute + "분", Toast.LENGTH_SHORT).show();

        Calendar cal = Calendar.getInstance(Locale.KOREA);
        cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
        cal.set(Calendar.MINUTE, minute);

        pendingIntent = PendingIntent.getBroadcast(getActivity(), 1, recevierIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
    }
}
