package com.android.alarm_clock_java;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class page_set_music extends AppCompatActivity
{
    Ringtone ringtone;
    List<String> items = new ArrayList<>();
    String selection;
    int flag_global=0;
    float progress_volume=1f;

    private DatabaseHelper mDBHelper;
    private SQLiteDatabase mDb;
    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getSupportActionBar().hide();
        setContentView(R.layout.set_alarm_music);

        String completePath = Environment.getExternalStorageDirectory()+ "/Ringtones/"; // /storage/emulated/0/Ringtones
        String completePath_download = Environment.getExternalStorageDirectory()+ "/Download/";

        File directory = new File(completePath);
        File[] files = directory.listFiles();
        if(files!=null)
        {
            for (int i = 0; i < files.length; i++)
            {
                if(files[i].getName().toLowerCase().endsWith(".mp3"))
                {
                    items.add(files[i].getName());
                }
            }
        }

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

        items.addAll(mDBHelper.get_all_songs());


        if(!items.isEmpty()) {
            RadioButton[] radioButtons = new RadioButton[items.size()];
            RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radioGroup_page_set_music);
            //выбрать первую радиокнопку и задать ей стайлинг
            //пройтись по массиву радиокнопок и задать им стайлинг с текстом
            for (int i = 0; i < items.size(); i++) {
                    radioButtons[i] = new RadioButton(page_set_music.this);
                    radioButtons[i].setButtonTintList(ContextCompat.getColorStateList(this, R.color.main_style));
                    radioButtons[i].setPadding(30, 50, 0, 50);
                    radioButtons[i].setText(items.get(i));
                    radioButtons[i].setId(i);
                    radioGroup.addView(radioButtons[i]);
            }

            @SuppressLint("UseSwitchCompatOrMaterialCode") Switch aSwitch = findViewById(R.id.switch_music_page_set_music);
            aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    TextView textView_style=findViewById(R.id.textView_static_page_set_music_sound);
                    if(isChecked){

                        textView_style.setTextColor(ContextCompat.getColor(page_set_music.this, R.color.main_style));
                    }
                    else
                    {
                        textView_style.setTextColor(ContextCompat.getColor(page_set_music.this, R.color.grey));
                    }

                    if(isChecked && radioGroup.getCheckedRadioButtonId() != -1)  {
                        if (ringtone != null && ringtone.isPlaying()) {
                            ringtone.stop();
                        }
                        File file = new File(completePath+(String) radioButtons[radioGroup.getCheckedRadioButtonId()].getText());
                        if(file.exists())
                        {
                            Uri uri = Uri.fromFile(file);
                            Log.d("bla",String.valueOf(uri));
                            ringtone = RingtoneManager.getRingtone(page_set_music.this, uri);
                        }
                        else
                        {
                            File file_download = new File(completePath_download + (String) radioButtons[radioGroup.getCheckedRadioButtonId()].getText());
                            Uri myUri1_download = Uri.fromFile(file_download);
                            ringtone = RingtoneManager.getRingtone(page_set_music.this, myUri1_download);
                        }
                        if (ringtone == null) {
                            Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
                            ringtone = RingtoneManager.getRingtone(page_set_music.this, uri);
                        }
                        if (ringtone != null) {
                            ringtone.setVolume(progress_volume);
                            ringtone.play();
                        }
                    }
                    else
                    {
                        if(ringtone !=null && ringtone.isPlaying())
                        {
                            ringtone.stop();
                        }
                    }
                }
            });

            radioGroup.clearCheck();

            radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    if(flag_global<2){
                        flag_global=flag_global+1;
                    }
                    if (ringtone != null && ringtone.isPlaying()) {
                        ringtone.stop();
                    }
                    File file = new File(completePath + (String) radioButtons[checkedId].getText());
                    if(file.exists())
                    {
                        Uri uri = Uri.fromFile(file);
                        ringtone = RingtoneManager.getRingtone(page_set_music.this, uri);
                    }
                    else
                    {
                        File file_download = new File(completePath_download + (String) radioButtons[radioGroup.getCheckedRadioButtonId()].getText());
                        Uri myUri1_download = Uri.fromFile(file_download);
                        ringtone = RingtoneManager.getRingtone(page_set_music.this, myUri1_download);
                    }
                    if (ringtone == null) {
                        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
                        ringtone = RingtoneManager.getRingtone(page_set_music.this, uri);
                    }
                    if (ringtone != null && flag_global == 2 && aSwitch.isChecked()) {
                        ringtone.setVolume(progress_volume);
                        ringtone.play();
                    }
                    else
                    {
                        if(ringtone !=null && ringtone.isPlaying())
                        {
                            ringtone.stop();
                        }
                    }

                    selection = (String) radioButtons[checkedId].getText();
                    if(!items.isEmpty()) {
                        SharedPreferences preferences = getSharedPreferences("music_name", MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("radio_value_selection", selection);
                        editor.commit();
                    }
                }

            });

            Bundle bundle = getIntent().getExtras();
            if(bundle!=null)
            {
                for (int i = 0; i < items.size(); i++) {
                    if (radioButtons[i].getText().equals(bundle.getString("music_name"))) {
                        radioButtons[i].setChecked(true);
                        break;
                    }
                }
            }

            SeekBar volumeSeekbar = findViewById(R.id.seekBar_page_set_music);
            if(ringtone!=null)
            {
                volumeSeekbar.setMax((int) ringtone.getVolume()*100);
                volumeSeekbar.setProgress((int) ringtone.getVolume()*100);
            }
            volumeSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    progress_volume=progress*0.01f;
                    if(ringtone!=null && ringtone.isPlaying())
                    {
                        ringtone.setVolume(progress_volume);
                    }
                }
                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }
                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });

            ImageButton imageButton=findViewById(R.id.button_sound_volume);
            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    volumeSeekbar.setProgress(10);
                    if(ringtone!=null && ringtone.isPlaying())
                    {
                        ringtone.setVolume(10*0.01f);
                    }
                }
            });

            ConstraintLayout constraintLayout=findViewById(R.id.constraint_layout_check_music_page_set_music);
            constraintLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!aSwitch.isChecked())
                    {
                        aSwitch.setChecked(true);
                    }
                    else
                    {
                        aSwitch.setChecked(false);
                    }
                }
            });

        }
    }


    public void finish_page_set_music(View view)
    {
        if(ringtone!=null && ringtone.isPlaying())
        {
            ringtone.stop();
        }
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        @SuppressLint("UseSwitchCompatOrMaterialCode") Switch aSwitch = findViewById(R.id.switch_music_page_set_music);
        aSwitch.setChecked(false);
    }

    @Override
    protected void onStart() {
        super.onStart();

        //сделать края закруглёнными
        ConstraintLayout constraintLayout = findViewById(R.id.constraint_layout_check_music_page_set_music);
        GradientDrawable shape_constraintLayout = new GradientDrawable();
        shape_constraintLayout.setCornerRadius(70);
        shape_constraintLayout.setColor(Color.WHITE);
        constraintLayout.setBackground(shape_constraintLayout);

        //сделать края закруглёнными
        ScrollView scrollView = findViewById(R.id.scrollView_page_set_music);
        GradientDrawable shape = new GradientDrawable();
        shape.setCornerRadius(70);
        shape.setColor(Color.WHITE);
        scrollView.setBackground(shape);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mDBHelper.close();
        if(ringtone!=null && ringtone.isPlaying())
        {
            ringtone.stop();
        }
    }

    public void button_go_to_page_set_music_all(View view)
    {
        Intent intent = new Intent(page_set_music.this, page_set_music_all.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        if(ringtone!=null && ringtone.isPlaying())
        {
            ringtone.stop();
        }

    }
}
