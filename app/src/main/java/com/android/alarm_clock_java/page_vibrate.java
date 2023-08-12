package com.android.alarm_clock_java;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class page_vibrate extends AppCompatActivity {

    private DatabaseHelper mDBHelper;
    private SQLiteDatabase mDb;
    Vibrator vibrator;
    String selection;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getSupportActionBar().hide();
        setContentView(R.layout.page_vibrate);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

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

        mDBHelper.openDataBase();

        int vibrations_count = mDBHelper.get_vibrations_count();

        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radioGroup_page_vibrate);
        //выбрать первую радиокнопку и задать ей стайлинг
        RadioButton[] radioButtons = new RadioButton[vibrations_count];
        //пройтись по массиву радиокнопок и задать им стайлинг с текстом
        for (int i = 0; i < vibrations_count; i++) {
            radioButtons[i] = new RadioButton(page_vibrate.this);
            radioButtons[i].setButtonTintList(ContextCompat.getColorStateList(this, R.color.main_style));
            radioButtons[i].setPadding(30, 50, 0, 50);
            radioButtons[i].setId(i);
            radioButtons[i].setText(mDBHelper.get_name(i+1));
            radioGroup.addView(radioButtons[i]);
        }


        @SuppressLint("UseSwitchCompatOrMaterialCode") Switch aSwitch = (Switch) findViewById(R.id.switch_page_vibrate);
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                TextView textView_style=findViewById(R.id.textView_static_page_vibrate_on_off);
                if(isChecked)
                {
                    textView_style.setTextColor(ContextCompat.getColor(page_vibrate.this, R.color.main_style));
                }
                else
                {
                    textView_style.setTextColor(ContextCompat.getColor(page_vibrate.this, R.color.grey));
                }

                if(isChecked && radioGroup.getCheckedRadioButtonId() != -1)  {
                    if (vibrator != null && vibrator.hasVibrator()) {
                        vibrator.cancel();
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        @SuppressLint("ResourceType") String vibration_timings = mDBHelper.get_vibration_timings(radioButtons[radioGroup.getCheckedRadioButtonId()].getId() + 1);
                        @SuppressLint("ResourceType") String vibration_amplitudes = mDBHelper.get_vibration_amplitudes(radioButtons[radioGroup.getCheckedRadioButtonId()].getId() + 1);
                        String[] array_vibration_timings = vibration_timings.split(",");
                        String[] array_vibration_amplitudes = vibration_amplitudes.split(",");

                        int[] int_array_vibration_amplitudes = Arrays.asList(array_vibration_amplitudes).stream().mapToInt(Integer::parseInt).toArray();
                        long[] long_array_vibration_timings = Arrays.asList(array_vibration_timings).stream().mapToLong(Long::parseLong).toArray();
                        VibrationEffect effect = VibrationEffect.createWaveform(long_array_vibration_timings, int_array_vibration_amplitudes, 1);
                        if (vibrator.hasVibrator())
                            vibrator.vibrate(effect);
                    } else {
                        vibrator.vibrate(200);
                    }
                }
                else
                {
                    if (vibrator!=null && vibrator.hasVibrator())
                    {
                        vibrator.cancel();
                    }
                }
            }
        });

        radioGroup.clearCheck();

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(aSwitch.isChecked()) {

                    if (vibrator != null && vibrator.hasVibrator()) {
                        vibrator.cancel();
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        String vibration_timings = mDBHelper.get_vibration_timings(checkedId + 1);
                        String vibration_amplitudes = mDBHelper.get_vibration_amplitudes(checkedId + 1);
                        String[] array_vibration_timings = vibration_timings.split(",");
                        String[] array_vibration_amplitudes = vibration_amplitudes.split(",");

                        int[] int_array_vibration_amplitudes = Arrays.asList(array_vibration_amplitudes).stream().mapToInt(Integer::parseInt).toArray();
                        long[] long_array_vibration_timings = Arrays.asList(array_vibration_timings).stream().mapToLong(Long::parseLong).toArray();
                        VibrationEffect effect = VibrationEffect.createWaveform(long_array_vibration_timings, int_array_vibration_amplitudes, 1);
                        if (vibrator.hasVibrator())
                            vibrator.vibrate(effect);
                    } else {
                        vibrator.vibrate(200);
                    }
                }
                else
                {
                    if (vibrator!=null && vibrator.hasVibrator())
                    {
                        vibrator.cancel();
                    }
                }


                selection = (String) radioButtons[checkedId].getText();
                SharedPreferences preferences = getSharedPreferences("vibrator_name", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("vibrator_radio_value_selection", selection);
                editor.commit();
            }

        });

        //получить данные по выбранной вибрации
        //получение значений, переданных в активити
        Bundle bundle = getIntent().getExtras();
        String vibrator_name=bundle.getString("vibrator_name");
        //Adapter_alarm adapter_alarm = new Adapter_alarm(context,alarm_list);
        if(vibrator_name!=null) {
            for (int i = 0; i < vibrations_count; i++) {
                if (radioButtons[i].getText().equals(vibrator_name)) {
                    radioButtons[i].setChecked(true);
                    break;
                }
            }
        }
        ConstraintLayout constraintLayout=findViewById(R.id.constraint_layout_check_vibration_page_vibrate);
        constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView textView = findViewById(R.id.textView_static_page_vibrate_on_off);
                if(!aSwitch.isChecked())
                {
                    aSwitch.setChecked(true);
                    textView.setText("Включено");
                }
                else
                {
                    aSwitch.setChecked(false);
                    textView.setText("Выключено");
                }
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        ScrollView scrollView = findViewById(R.id.scrollView_page_vibrate);
        GradientDrawable shape = new GradientDrawable();
        shape.setCornerRadius(70);
        shape.setColor(Color.WHITE);
        scrollView.setBackground(shape);

        ConstraintLayout constraintLayout = findViewById(R.id.constraint_layout_check_vibration_page_vibrate);
        GradientDrawable shape_constraintLayout = new GradientDrawable();
        shape_constraintLayout.setCornerRadius(70);
        shape_constraintLayout.setColor(Color.WHITE);
        constraintLayout.setBackground(shape_constraintLayout);

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDBHelper.close();
        if (vibrator!=null)
        {
            vibrator.cancel();
        }
    }

    public void finish_page_vibrate(View view){
        finish();
    }

}
