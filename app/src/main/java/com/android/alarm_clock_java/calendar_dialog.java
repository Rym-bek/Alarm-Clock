package com.android.alarm_clock_java;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class calendar_dialog extends AppCompatActivity {
    int temp_year;
    int temp_month;
    int temp_dayOfMonth;
    Calendar calendar=Calendar.getInstance();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_calendar);

        Window window = this.getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.setGravity(Gravity.BOTTOM);


        ConstraintLayout constraintLayout = findViewById(R.id.constraint_layout_calendar_dialog);

        GradientDrawable shape = new GradientDrawable();
        shape.setCornerRadius(70);
        shape.setColor(Color.WHITE);
        constraintLayout.setBackground(shape);

        CalendarView calendarView = findViewById(R.id.CalendarView_dialog);
        calendarView.setMinDate(System.currentTimeMillis());
        calendarView.setMaxDate(System.currentTimeMillis()+63072000000L);

        Bundle bundle=getIntent().getExtras();
        calendar.setTimeInMillis(bundle.getLong("TimeInMillis"));
        calendarView.setDate(calendar.getTimeInMillis());



        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {

            @Override
            public void onSelectedDayChange(CalendarView view, int year,
                                            int month, int dayOfMonth) {
                temp_year=year;
                temp_month=month;
                temp_dayOfMonth=dayOfMonth;
            }

        });
    }

    public void cancel_calendar_dialog(View view){
        finish();
    }

    public void apply_calendar_dialog(View view){
        //SharedPreferences для передачи данных в page_setalert
        SharedPreferences preferences=getSharedPreferences("calendar_day_data",MODE_PRIVATE);
        SharedPreferences.Editor editor=preferences.edit();
        editor.putInt("year",temp_year);
        editor.putInt("month",temp_month);
        editor.putInt("dayOfMonth",temp_dayOfMonth);
        editor.commit();

        finish();
    }
}
