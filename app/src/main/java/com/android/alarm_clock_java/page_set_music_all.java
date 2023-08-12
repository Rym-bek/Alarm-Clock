package com.android.alarm_clock_java;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.SearchView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class page_set_music_all extends AppCompatActivity {
    String selected_song="";
    private static final String TAG = "suka";
    Ringtone ringtone_page_set_music_all;
    List<String> items_all = new ArrayList<>();
    private DatabaseHelper mDBHelper;
    private SQLiteDatabase mDb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getSupportActionBar().hide();
        setContentView(R.layout.page_set_music_all);

        GradientDrawable shape = new GradientDrawable();
        shape.setCornerRadius(70);
        shape.setColor(Color.WHITE);

        String completePath = Environment.getExternalStorageDirectory()+ "/Download/";
        File directory = new File(completePath);
        File[] files = directory.listFiles();
        if(files!=null)
        {
            for (int i = 0; i < files.length; i++)
            {
                if(files[i].getName().toLowerCase().endsWith(".mp3"))
                {
                    items_all.add(files[i].getName());
                }
            }
        }
        TextView textView = (TextView) findViewById(R.id.textView_songs_count_page_set_music_all);
        textView.append(" "+String.valueOf(items_all.size()));

        SearchView searchView = findViewById(R.id.searchView_page_set_music_all);

        if(!items_all.isEmpty())
        {
            RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radioGroup_page_set_music_all);
            RadioButton[] radioButtons = new RadioButton[items_all.size()];

                //выбрать первую радиокнопку и задать ей стайлинг
                //пройтись по массиву радиокнопок и задать им стайлинг с текстом
                for (int i = 0; i < items_all.size(); i++) {
                    radioButtons[i] = new RadioButton(page_set_music_all.this);
                    radioButtons[i].setButtonTintList(ContextCompat.getColorStateList(this, R.color.main_style));
                    radioButtons[i].setPadding(30, 50, 0, 50);
                    radioButtons[i].setText(items_all.get(i));
                    radioButtons[i].setId(i);
                    radioGroup.addView(radioButtons[i]);
                }
                radioGroup.setBackground(shape);


            @SuppressLint("UseSwitchCompatOrMaterialCode") Switch aSwitch = (Switch) findViewById(R.id.switch_music_page_set_music_all);
            aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @SuppressLint("ResourceAsColor")
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    TextView textView_style=findViewById(R.id.textView_static_page_set_music_sound);
                    if(isChecked){

                        textView_style.setTextColor(ContextCompat.getColor(page_set_music_all.this, R.color.main_style));
                    }
                    else
                    {
                        textView_style.setTextColor(ContextCompat.getColor(page_set_music_all.this, R.color.grey));
                    }

                    if(isChecked && radioGroup.getCheckedRadioButtonId() != -1)  {
                        if (ringtone_page_set_music_all != null && ringtone_page_set_music_all.isPlaying()) {
                            ringtone_page_set_music_all.stop();
                        }
                        File file = new File(completePath+(String) radioButtons[radioGroup.getCheckedRadioButtonId()].getText());
                        Uri uri_custom = Uri.fromFile(file);
                        ringtone_page_set_music_all = RingtoneManager.getRingtone(page_set_music_all.this, uri_custom);
                        if (ringtone_page_set_music_all == null) {
                            Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
                            ringtone_page_set_music_all = RingtoneManager.getRingtone(page_set_music_all.this, uri);
                        }
                        if (ringtone_page_set_music_all != null) {
                            ringtone_page_set_music_all.play();
                        }
                    }
                    else
                    {
                        if(ringtone_page_set_music_all !=null && ringtone_page_set_music_all.isPlaying())
                        {
                            ringtone_page_set_music_all.stop();
                        }
                    }
                }
            });

            radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {

                    selected_song= (String) radioButtons[radioGroup.getCheckedRadioButtonId()].getText();
                    Log.e(TAG, String.valueOf(selected_song));

                    if(aSwitch.isChecked())
                    {
                        if (ringtone_page_set_music_all != null && ringtone_page_set_music_all.isPlaying()) {
                            ringtone_page_set_music_all.stop();
                        }

                        File file = new File(completePath+(String) radioButtons[radioGroup.getCheckedRadioButtonId()].getText());
                        Uri uri_custom = Uri.fromFile(file);
                        ringtone_page_set_music_all = RingtoneManager.getRingtone(page_set_music_all.this, uri_custom);
                        if (ringtone_page_set_music_all == null) {
                            Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
                            ringtone_page_set_music_all = RingtoneManager.getRingtone(page_set_music_all.this, uri);
                        }
                        if (ringtone_page_set_music_all != null) {
                            ringtone_page_set_music_all.play();
                        }
                    }
                    else
                    {
                        if(ringtone_page_set_music_all !=null && ringtone_page_set_music_all.isPlaying())
                        {
                            ringtone_page_set_music_all.stop();
                        }
                    }

                }
            });

            ConstraintLayout constraintLayout_check=findViewById(R.id.constraint_layout_check_music_page_set_music_all);
            constraintLayout_check.setOnClickListener(new View.OnClickListener() {
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
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String s) {
                    searchView.clearFocus();
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String s) {
                    for (int i = 0;i < items_all.size();i++)
                    {
                        if (radioButtons[i].getText().toString().toLowerCase().contains(searchView.getQuery().toString().toLowerCase()))
                        {
                            radioButtons[i].setVisibility(View.VISIBLE);
                        } else
                        {
                            radioButtons[i].setVisibility(View.GONE);
                        }
                    }
                    return false;
                }
            });
            searchView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    searchView.setIconified(false);
                }
            });
        }
        else
        {
            searchView.setVisibility(View.GONE);
        }

    }

    public void finish_page_set_music_all(View view)
    {
        finish();
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void copy_song_page_set_music_all(View view) throws IOException {
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

        Intent intent = new Intent(page_set_music_all.this, page_set_music.class);
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radioGroup_page_set_music_all);
        if(radioGroup.getCheckedRadioButtonId() != -1)
        {
            mDBHelper.openDataBase();
            mDBHelper.add_song_name(selected_song);
            mDBHelper.close();
            intent.putExtra("music_name",selected_song);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //стайлинг searchView
        SearchView searchView = findViewById(R.id.searchView_page_set_music_all);
        GradientDrawable shape = new GradientDrawable();
        shape.setCornerRadius(70);
        shape.setColor(Color.WHITE);
        searchView.setBackground(shape);
        searchView.setQueryHint("Введите название");
        searchView.onActionViewExpanded();
        searchView.setIconified(false);
        searchView.clearFocus();


        //стайлинг constraintLayout
        GradientDrawable shape_constraintLayout = new GradientDrawable();
        shape_constraintLayout.setCornerRadius(70);
        shape_constraintLayout.setColor(Color.WHITE);
        ConstraintLayout constraintLayout = findViewById(R.id.constraint_layout_check_music_page_set_music_all);
        constraintLayout.setBackground(shape_constraintLayout);


        //стайлинг scrollView
        GradientDrawable shape_scrollView = new GradientDrawable();
        shape_scrollView.setCornerRadius(70);
        shape_scrollView.setColor(Color.WHITE);
        ScrollView scrollView = findViewById(R.id.scrollView_page_set_music_all);
        scrollView.setBackground(shape_scrollView);


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(ringtone_page_set_music_all !=null && ringtone_page_set_music_all.isPlaying())
        {
            ringtone_page_set_music_all.stop();
        }
    }
}
