package com.android.alarm_clock_java;
import android.annotation.SuppressLint;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
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
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.android.alarm_clock_java.models.Alarm;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class alarm_dialog extends AppCompatActivity  {
    Ringtone ringtone;
    Vibrator vibrator;
    private DatabaseHelper mDBHelper;
    private SQLiteDatabase mDb;
    private final java.util.Calendar calendar = Calendar.getInstance();
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.alarm_dialog);

        if(isDeviceLocked(alarm_dialog.this))
        {
            Bundle bundle=getIntent().getExtras();
            Intent intent = new Intent(alarm_dialog.this, lock_screen.class);
            intent.putExtra("id", bundle.getInt("id", 0));
            intent.putExtra("vibration_name",bundle.getString("vibration_name"));
            startActivity(intent);
            this.finish();
        }
        else
        {
            //задать в диалоге текущее время
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
            String currentDateTimeString = sdf.format(calendar.getTime());
            TextView textView = findViewById(R.id.current_time);
            textView.setText(currentDateTimeString);


            SharedPreferences preferences_switch_state_music_vibration = getSharedPreferences("switch_state_music_vibration",MODE_PRIVATE);
            boolean switch_state_music = preferences_switch_state_music_vibration.getBoolean("switch_state_music",false);
            if(switch_state_music)
            {
                SharedPreferences preferences=getSharedPreferences("music_name",MODE_PRIVATE);
                String radio_value_selection = preferences.getString("radio_value_selection","");

                String fileName="";

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
                if(file.exists())
                {
                    Uri uri = Uri.fromFile(file);
                    ringtone = RingtoneManager.getRingtone(alarm_dialog.this, uri);

                }
                else
                {
                    String completePath_download = Environment.getExternalStorageDirectory() + "/Download/" + fileName;
                    File file_download = new File(completePath_download);
                    Uri myUri1_download = Uri.fromFile(file_download);
                    ringtone = RingtoneManager.getRingtone(alarm_dialog.this, myUri1_download);
                }

                if (ringtone == null) {
                    Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
                    ringtone = RingtoneManager.getRingtone(alarm_dialog.this, uri);
                }
                if (ringtone != null) {
                    ringtone.play();
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
            }
        }


    }

    @SuppressLint("ObsoleteSdkInt")
    public static boolean isDeviceLocked(Context context) {
        boolean isLocked;

        KeyguardManager keyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        boolean inKeyguardRestrictedInputMode = keyguardManager.isKeyguardLocked();

        if (inKeyguardRestrictedInputMode) {
            isLocked = true;

        } else {
            PowerManager powerManager = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
                isLocked = !powerManager.isInteractive();
            } else {
                isLocked = !powerManager.isScreenOn();
            }
        }

        return isLocked;
    }

    @Override
    protected void onStart() {
        super.onStart();

        //привязка диалога к верху и создание прочразчного фона
        Window window = this.getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.setGravity(Gravity.TOP);

        //сделать края диалога закруглёнными
        ConstraintLayout constraintLayout = findViewById(R.id.constraint_layout_alarm_dialog);
        GradientDrawable shape = new GradientDrawable();
        shape.setCornerRadius(70);
        shape.setColor(Color.WHITE);
        constraintLayout.setBackground(shape);
    }

    //выключение будильника
    public void cancel_alert(View view){
        finish();
    }

    //при закрытии диалога будильник останавливается
    @Override
    public void onDestroy() {
        if(ringtone!=null && ringtone.isPlaying())
        {
            ringtone.stop();
        }

        if (vibrator!=null)
        {
            vibrator.cancel();
        }
        super.onDestroy();
    }
}
