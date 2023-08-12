package com.android.alarm_clock_java;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.app.AlarmManager;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.alarm_clock_java.interfaces.UpdateTextCallback;
import com.android.alarm_clock_java.ui.home.AlertFragment;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;


public class page_setalert extends AppCompatActivity
{

    private final java.util.Calendar calendar=Calendar.getInstance();
    UpdateTextCallback updateTextCallback;
    private DatabaseHelper mDBHelper;
    private SQLiteDatabase mDb;
    Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        context=getBaseContext();

        SharedPreferences preferences_calendar_clear=getSharedPreferences("calendar_day_data",MODE_PRIVATE);
        preferences_calendar_clear.edit().clear().apply();

        Objects.requireNonNull(getSupportActionBar()).hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page_setalert);

        //объявляю таймпикер
        TimePicker timePicker = (TimePicker) this.findViewById(R.id.timePicker);
        timePicker.setIs24HourView(true);


        //textview
        TextView textView_current_date = findViewById(R.id.textView_page_setalert_date);

        //музыка и вибрация
        TextView textView_vibration = findViewById(R.id.name_of_music_vibration_setalert);
        TextView textView = findViewById(R.id.name_of_music_page_setalert);

        //форматирование главной панели даты
        SimpleDateFormat sdf_date = new SimpleDateFormat("EEE, d MMM", Locale.getDefault());


        //получаю Switch
        @SuppressLint("UseSwitchCompatOrMaterialCode") Switch switch_music_pagesetalert_SharedPreferences = (Switch) findViewById(R.id.switch_music_pagesetalert);
        @SuppressLint("UseSwitchCompatOrMaterialCode") Switch switch_vibration_pagesetalert_SharedPreferences = (Switch) findViewById(R.id.switch_vibration_pagesetalert);


        //получение значений, переданных в активити
        Bundle bundle = getIntent().getExtras();
        String getTime=bundle.getString("getTime");
        //Adapter_alarm adapter_alarm = new Adapter_alarm(context,alarm_list);
        if(getTime!=null)
        {
            int getMusic_state=bundle.getInt("getMusic_state");
            int getVibration_state=bundle.getInt("getVibration_state");
            String getMusic_title=bundle.getString("getMusic_title");
            String getVibration_title=bundle.getString("getVibration_title");

            if(getMusic_state==1)
            {
                switch_music_pagesetalert_SharedPreferences.setChecked(true);

            }
            else
            {
                switch_music_pagesetalert_SharedPreferences.setChecked(false);
            }

            if(getVibration_state==1)
            {
                switch_vibration_pagesetalert_SharedPreferences.setChecked(true);
            }
            else
            {
                switch_vibration_pagesetalert_SharedPreferences.setChecked(false);
            }


            calendar.setTimeInMillis(Long.valueOf(getTime));

            //начальное присвоение даты
            String currentDateTimeString_date = sdf_date.format(calendar.getTime());
            textView_current_date.setText(currentDateTimeString_date);

            timePicker.setHour(calendar.get(Calendar.HOUR_OF_DAY));
            timePicker.setMinute(calendar.get(Calendar.MINUTE));

            textView.setText(getMusic_title);
            textView_vibration.setText(getVibration_title);
        }
        else
        {

            //задаю Switch значения которые в прошлый раз выбрал пользователь
            SharedPreferences preferences_switch_state_music_vibration_get = getSharedPreferences("switch_state_music_vibration",MODE_PRIVATE);
            boolean switch_state_music = preferences_switch_state_music_vibration_get.getBoolean("switch_state_music",false);
            if(switch_state_music)
            {
                switch_music_pagesetalert_SharedPreferences.setChecked(true);
            }
            else
            {
                switch_music_pagesetalert_SharedPreferences.setChecked(false);
            }

            boolean switch_state_vibration = preferences_switch_state_music_vibration_get.getBoolean("switch_state_vibration",false);
            if(switch_state_vibration)
            {
                switch_vibration_pagesetalert_SharedPreferences.setChecked(true);
            }
            else
            {
                switch_vibration_pagesetalert_SharedPreferences.setChecked(false);
            }

            calendar.add(Calendar.DAY_OF_MONTH, 1);
            String currentDateTimeString_date = sdf_date.format(calendar.getTime());
            textView_current_date.setText(currentDateTimeString_date);

            timePicker.setHour(6);
            timePicker.setMinute(0);

            //начальное присвоение названия музыки
            SharedPreferences preferences = getSharedPreferences("music_name", MODE_PRIVATE);
            String radio_value_selection = preferences.getString("radio_value_selection", "");

            textView.setText(radio_value_selection);

            //начальное присвоение названия вибрации
            SharedPreferences preferences_vibration = getSharedPreferences("vibrator_name", MODE_PRIVATE);
            String radio_value_selection_vibration = preferences_vibration.getString("vibrator_radio_value_selection", "");

            textView_vibration.setText(radio_value_selection_vibration);
        }

        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                Date date = new Date();
                @SuppressWarnings("deprecation") int hour = date.getHours();
                @SuppressWarnings("deprecation") int min = date.getMinutes();
                SharedPreferences preferences_date=getSharedPreferences("calendar_day_data",MODE_PRIVATE);
                int dayOfMonth = preferences_date.getInt("dayOfMonth",0);
                if(dayOfMonth==0 || dayOfMonth==date.getDate())
                {
                    if(hourOfDay<hour)
                    {
                        //noinspection deprecation
                        calendar.set(Calendar.DAY_OF_MONTH, date.getDate()+1);
                    }
                    else if(hourOfDay==hour && minute<=min)
                    {
                        //noinspection deprecation
                        calendar.set(Calendar.DAY_OF_MONTH, date.getDate()+1);
                    }
                    else
                    {
                        //noinspection deprecation
                        calendar.set(Calendar.DAY_OF_MONTH, date.getDate());
                    }
                }
                SimpleDateFormat sdf_date = new SimpleDateFormat("EEE, d MMM", Locale.getDefault());
                String currentDateTimeString_date = sdf_date.format(calendar.getTime());
                textView_current_date.setText(currentDateTimeString_date);
            }
        });



        //кнопка: отменить создание будильника
        ImageButton button_cancel =(ImageButton) this.findViewById(R.id.button_cancel);
        button_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());


        //База данных
        mDBHelper = new DatabaseHelper(this);

        try {
            mDBHelper.updateDataBase();
        } catch (IOException mIOException) {
            throw new Error("UnableToUpdateDatabase");
        }

        try {
            mDb = mDBHelper.getWritableDatabase();
        } catch (SQLException mSQLException) {
            throw mSQLException;
        }

        //кнопка: сохранить будильник
        ImageButton button_save =(ImageButton) this.findViewById(R.id.button_save);
        button_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //SharedPreferences для получения данных из calendar_dialog
                SharedPreferences preferences=getSharedPreferences("calendar_day_data",MODE_PRIVATE);
                int year = preferences.getInt("year",0);
                if(year!=0)
                {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, preferences.getInt("month",0));
                    calendar.set(Calendar.DAY_OF_MONTH, preferences.getInt("dayOfMonth",0));
                }
                calendar.set(Calendar.SECOND,0);
                calendar.set(Calendar.MILLISECOND,0);
                calendar.set(Calendar.MINUTE,timePicker.getMinute());
                calendar.set(Calendar.HOUR_OF_DAY,timePicker.getHour());

                Date date = new Date();
                if(calendar.get(Calendar.DAY_OF_MONTH)<=date.getDate() && calendar.get(Calendar.HOUR_OF_DAY)<=date.getHours() && calendar.get(Calendar.MINUTE)<=date.getMinutes())
                {
                    Toast.makeText(page_setalert.this, "Нельзя установить время раньше текущего", Toast.LENGTH_SHORT).show();
                }
                else
                {

                    //добавление будильника в систему
                    AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                    AlarmManager.AlarmClockInfo alarmClockInfo = new AlarmManager.AlarmClockInfo(calendar.getTimeInMillis(),getAlarmInfoPendingIntent());
                    alarmManager.setAlarmClock(alarmClockInfo, getAlarmActionPendingIntent());

                    Toast.makeText(page_setalert.this, "Установлен "+sdf.format(calendar.getTimeInMillis()), Toast.LENGTH_SHORT).show();

                    @SuppressLint("UseSwitchCompatOrMaterialCode") Switch switch_music_pagesetalert = (Switch) findViewById(R.id.switch_music_pagesetalert);
                    @SuppressLint("UseSwitchCompatOrMaterialCode") Switch switch_vibration_pagesetalert = (Switch) findViewById(R.id.switch_vibration_pagesetalert);
                    SharedPreferences preferences_switch_state_music_vibration = getSharedPreferences("switch_state_music_vibration", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences_switch_state_music_vibration.edit();
                    editor.putBoolean("switch_state_music", switch_music_pagesetalert.isChecked());
                    editor.putBoolean("switch_state_vibration", switch_vibration_pagesetalert.isChecked());
                    editor.commit();

                    //добавление будильника в базу данных
                    mDBHelper.openDataBase();
                    if(getTime!=null)
                    {
                        mDBHelper.update_alarm_info(bundle.getInt("getId"),calendar.getTimeInMillis(),switch_music_pagesetalert.isChecked(),switch_vibration_pagesetalert.isChecked(), (String) textView.getText(), (String) textView_vibration.getText());
                    }
                    else
                    {
                        mDBHelper.add_alarm_info(calendar.getTimeInMillis(),switch_music_pagesetalert.isChecked(),switch_vibration_pagesetalert.isChecked(), (String) textView.getText(), (String) textView_vibration.getText());
                    }
                    mDBHelper.close();

                    /*AlertFragment alertFragment = new AlertFragment();
                    alertFragment.updateText(String.valueOf(calendar.getTimeInMillis()));*/

                    /*Bundle bundle = new Bundle();
                    bundle.putString("new_calendar_time", String.valueOf(calendar.getTimeInMillis()));
                    AlertFragment alertFragment = new AlertFragment();
                    alertFragment.setArguments(bundle);
                    getSupportFragmentManager().beginTransaction().replace(R.id.navigation_alert, alertFragment).commit();
                    */

                    /*Bundle bundle_AlertFragment = new Bundle();
                    bundle_AlertFragment.putString("new_calendar_time", String.valueOf(calendar.getTimeInMillis()));
                    AlertFragment alertFragment = new AlertFragment();
                    alertFragment.setArguments(bundle_AlertFragment);*/
                    finish();
                }
            }
        });

        //кнопка: открыть календарь
        ImageButton button_calendar_dialog =(ImageButton) this.findViewById(R.id.imageButton_calendar);
        button_calendar_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(page_setalert.this, calendar_dialog.class);
                intent.putExtra("TimeInMillis",calendar.getTimeInMillis());
                startActivity(intent);
            }

        });


        //кнопка-лэйаут: открыть страницу создания музыки
        ConstraintLayout button_constraintLayout_set_music =(ConstraintLayout) this.findViewById(R.id.constraint_layout_music_select);
        button_constraintLayout_set_music.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView textView_name_of_music_page_setalert=findViewById(R.id.name_of_music_page_setalert);
                Intent intent = new Intent(page_setalert.this, page_set_music.class);
                intent.putExtra("music_name",textView_name_of_music_page_setalert.getText());
                startActivity(intent);
            }
        });

        //кнопка-лэйаут: открыть страницу создания вибрации
        ConstraintLayout button_constraintLayout_set_vibration =(ConstraintLayout) this.findViewById(R.id.constraint_layout_vibration_select);
        button_constraintLayout_set_vibration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView textView_name_of_music_vibration_setalert=findViewById(R.id.name_of_music_vibration_setalert);
                Intent intent = new Intent(page_setalert.this, page_vibrate.class);
                intent.putExtra("vibrator_name",textView_name_of_music_vibration_setalert.getText());
                startActivity(intent);
            }
        });
        @SuppressLint("UseSwitchCompatOrMaterialCode") Switch switch_music_pagesetalert = (Switch) findViewById(R.id.switch_music_pagesetalert);
        @SuppressLint("UseSwitchCompatOrMaterialCode") Switch switch_vibration_pagesetalert = (Switch) findViewById(R.id.switch_vibration_pagesetalert);
            switch_music_pagesetalert.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @SuppressLint("ResourceAsColor")
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(!switch_music_pagesetalert.isChecked() && !switch_vibration_pagesetalert.isChecked())
                    {
                        switch_vibration_pagesetalert.setChecked(true);
                    }
                }
            });


            switch_vibration_pagesetalert.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @SuppressLint("ResourceAsColor")
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(!switch_music_pagesetalert.isChecked() && !switch_vibration_pagesetalert.isChecked())
                    {
                        switch_music_pagesetalert.setChecked(true);
                    }
                }
            });


    }


    @Override
    protected void onResume() {
        super.onResume();
        TextView textView_current_date = findViewById(R.id.textView_page_setalert_date);
        SharedPreferences preferences_date=getSharedPreferences("calendar_day_data",MODE_PRIVATE);
        int year = preferences_date.getInt("year",0);
        if(year!=0)
        {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, preferences_date.getInt("month",0));
            calendar.set(Calendar.DAY_OF_MONTH, preferences_date.getInt("dayOfMonth",0));
        }
        SimpleDateFormat sdf_date = new SimpleDateFormat("EEE, d MMM", Locale.getDefault());
        String currentDateTimeString_date = sdf_date.format(calendar.getTime());
        textView_current_date.setText(currentDateTimeString_date);
    }

    @Override
    public void onRestart(){
        super.onRestart();
        //обновление присвоения данных
        SharedPreferences preferences = getSharedPreferences("music_name", MODE_PRIVATE);
        String radio_value_selection = preferences.getString("radio_value_selection", "");
        TextView textView = findViewById(R.id.name_of_music_page_setalert);
        textView.setText(radio_value_selection);

        SharedPreferences preferences_vibration = getSharedPreferences("vibrator_name", MODE_PRIVATE);
        String radio_value_selection_vibration = preferences_vibration.getString("vibrator_radio_value_selection", "");
        TextView textView_vibration = findViewById(R.id.name_of_music_vibration_setalert);
        textView_vibration.setText(radio_value_selection_vibration);

    }

    //Будильник
    @SuppressLint("UnspecifiedImmutableFlag")
    private PendingIntent getAlarmInfoPendingIntent(){
        Bundle bundle = getIntent().getExtras();
        int item_count=bundle.getInt("item_count");
        Intent alarmInfoIntent = new Intent(this, AlertFragment.class);
        alarmInfoIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        return PendingIntent.getActivity(this, item_count, alarmInfoIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }
    @SuppressLint("UnspecifiedImmutableFlag")
    private PendingIntent getAlarmActionPendingIntent() {
        Bundle bundle = getIntent().getExtras();
        //Adapter_alarm adapter_alarm = new Adapter_alarm(context,alarm_list);
        int item_count=bundle.getInt("item_count");
        //int item_count=adapter_alarm.getItemCount()-3;
        Intent intent = new Intent(this, alarm_dialog.class);
        intent.putExtra("id",item_count);
        TextView textView=findViewById(R.id.name_of_music_vibration_setalert);
        intent.putExtra("vibration_name",textView.getText());
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        return PendingIntent.getActivity(this, item_count, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

}