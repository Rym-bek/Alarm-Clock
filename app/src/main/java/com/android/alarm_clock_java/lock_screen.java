package com.android.alarm_clock_java;

import static androidx.core.content.PackageManagerCompat.LOG_TAG;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.annotation.SuppressLint;
import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

public class lock_screen extends AppCompatActivity  {

    Ringtone ringtone_lock_screen;

    private final java.util.Calendar calendar= Calendar.getInstance();

    Vibrator vibrator;
    private DatabaseHelper mDBHelper;
    private SQLiteDatabase mDb;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lock_screen);

        IntentFilter filter = new IntentFilter();
        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(2000);
                startVibrate();
            }
        };
        registerReceiver(receiver, filter);
        //установка флагов
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        Objects.requireNonNull(this.getSupportActionBar()).hide();

        //вывод текущего времени
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String currentDateTimeString = sdf.format(calendar.getTime());
        TextView textView = findViewById(R.id.current_time_lock_screen);
        textView.setText(currentDateTimeString);

        SimpleDateFormat sdf_date = new SimpleDateFormat("EEE, d MMM.", Locale.getDefault());
        String currentDateTimeString_date = sdf_date.format(calendar.getTime());
        TextView textView_current_date = findViewById(R.id.current_date);
        textView_current_date.setText(currentDateTimeString_date);

        SharedPreferences preferences_switch_state_music_vibration = getSharedPreferences("switch_state_music_vibration",MODE_PRIVATE);
        boolean switch_state_music = preferences_switch_state_music_vibration.getBoolean("switch_state_music",false);
        if(switch_state_music) {
            SharedPreferences preferences = getSharedPreferences("music_name", MODE_PRIVATE);
            String radio_value_selection = preferences.getString("radio_value_selection", "");

            String fileName = "";

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
            Bundle bundle=getIntent().getExtras();
            int id = bundle.getInt("id", 0);
            mDBHelper.openDataBase();
            fileName=mDBHelper.get_music_title(id+1);
            mDBHelper.close();
            if(fileName.equals(""))
            {
                if(!radio_value_selection.equals(""))
                {
                    fileName = radio_value_selection;
                }
            }

            String completePath = Environment.getExternalStorageDirectory() + "/Ringtones/" + fileName;

            File file = new File(completePath);
            if (file.exists()) {
                Uri uri = Uri.fromFile(file);
                Log.d("bla", String.valueOf(uri));
                ringtone_lock_screen = RingtoneManager.getRingtone(lock_screen.this, uri);
            } else {
                String completePath_download = Environment.getExternalStorageDirectory() + "/Download/" + fileName;
                File file_download = new File(completePath_download);
                Uri myUri1_download = Uri.fromFile(file_download);
                ringtone_lock_screen = RingtoneManager.getRingtone(lock_screen.this, myUri1_download);
            }


            if (ringtone_lock_screen == null) {
                Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
                ringtone_lock_screen = RingtoneManager.getRingtone(lock_screen.this, uri);
            }
            if (ringtone_lock_screen != null) {
                ringtone_lock_screen.play();
            }
        }


        boolean switch_state_vibration = preferences_switch_state_music_vibration.getBoolean("switch_state_vibration",false);
        if(switch_state_vibration){
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

            vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                String radio_value_selection_vibration="";
                Bundle bundle=getIntent().getExtras();
                radio_value_selection_vibration=bundle.getString("vibration_name");
                if(radio_value_selection_vibration.equals(""))
                {
                    SharedPreferences preferences_vibration = getSharedPreferences("vibrator_name", MODE_PRIVATE);
                    radio_value_selection_vibration = preferences_vibration.getString("vibrator_radio_value_selection", "");
                }
                String vibration_timings = mDBHelper.get_vibration_timings_with_name(radio_value_selection_vibration);
                String vibration_amplitudes = mDBHelper.get_vibration_amplitudes_with_name(radio_value_selection_vibration);
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
            mDBHelper.close();
        }

    }

    private void startVibrate() {
        SharedPreferences preferences_switch_state_music_vibration = getSharedPreferences("switch_state_music_vibration",MODE_PRIVATE);
        boolean switch_state_vibration = preferences_switch_state_music_vibration.getBoolean("switch_state_vibration",false);
        if(switch_state_vibration){
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

            vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                String radio_value_selection_vibration="";
                Bundle bundle=getIntent().getExtras();
                radio_value_selection_vibration=bundle.getString("vibration_name");
                if(radio_value_selection_vibration.equals(""))
                {
                    SharedPreferences preferences_vibration = getSharedPreferences("vibrator_name", MODE_PRIVATE);
                    radio_value_selection_vibration = preferences_vibration.getString("vibrator_radio_value_selection", "");
                }
                String vibration_timings = mDBHelper.get_vibration_timings_with_name(radio_value_selection_vibration);
                String vibration_amplitudes = mDBHelper.get_vibration_amplitudes_with_name(radio_value_selection_vibration);
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
            mDBHelper.close();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();


        ImageButton iv = (ImageButton) findViewById(R.id.lock_screen_cancel_button);
        ObjectAnimator scaleDown = ObjectAnimator.ofPropertyValuesHolder(
                iv,
                PropertyValuesHolder.ofFloat("scaleX", 1.2f),
                PropertyValuesHolder.ofFloat("scaleY", 1.2f));
        scaleDown.setDuration(600);

        scaleDown.setRepeatCount(ObjectAnimator.INFINITE);
        scaleDown.setRepeatMode(ObjectAnimator.REVERSE);
        scaleDown.start();

        final Animation animAlpha = AnimationUtils.loadAnimation(lock_screen.this, R.anim.alpha);
        iv.startAnimation(animAlpha);

    }

    public void cancel_alert_lock_screen(View view){
        finish();
    }

    //при закрытии диалога будильник останавливается
    @Override
    public void onDestroy() {
        if(ringtone_lock_screen!=null && ringtone_lock_screen.isPlaying())
        {
            ringtone_lock_screen.stop();
        }

        if (vibrator!=null)
        {
            vibrator.cancel();
        }
        super.onDestroy();
    }
}
