package com.android.alarm_clock_java.ui.home;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.alarm_clock_java.DatabaseHelper;
import com.android.alarm_clock_java.R;
import com.android.alarm_clock_java.adapter.Adapter_alarm;
import com.android.alarm_clock_java.databinding.FragmentAlertBinding;
import com.android.alarm_clock_java.interfaces.UpdateTextCallback;
import com.android.alarm_clock_java.lock_screen;
import com.android.alarm_clock_java.models.Alarm;
import com.android.alarm_clock_java.page_setalert;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AlertFragment extends Fragment implements UpdateTextCallback {
    private DatabaseHelper mDBHelper;
    private SQLiteDatabase mDb;
    private FragmentAlertBinding binding;
    Context context;
    List<Alarm> alarm_list = new ArrayList<>();

    RecyclerView alarmRecycler;
    Adapter_alarm adapter_alarm;
    long min_value=Long.MAX_VALUE;
    String[] days = {"день", "дня", "дней"};


    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onResume() {
        //База данных
        mDBHelper = new DatabaseHelper(context);
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
        Log.d("resume_mode","RESUMED");
        super.onResume();
        alarm_list.clear();
        mDBHelper.openDataBase();
        alarm_list.addAll(mDBHelper.get_alarm_info());
        mDBHelper.close();
        setAlarmRecycler(alarm_list);
        Bundle bundle = this.getArguments();
        if(bundle!=null)
        {
            String new_calendar_time = bundle.getString("new_calendar_time","");
            updateText(new_calendar_time);
        }
    }



    @Override
    public void updateText(String text) {
        //объявляю Textview
        TextView textView_text_end_of_alert = getView().findViewById(R.id.text_end_of_alert);
        TextView textView_AlertFragment_next_date = getView().findViewById(R.id.AlertFragment_next_date);


        //нахождение ближайшего будильника

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat sdf = new SimpleDateFormat("HH ч. mm мин.", Locale.getDefault());


        if(Long.valueOf(text)==min_value)
        {
            mDBHelper.openDataBase();

            min_value=mDBHelper.return_next_alarm();

            mDBHelper.close();
        }
        else if(Long.valueOf(text)<min_value)
        {
            min_value=Long.valueOf(text);
        }

        SimpleDateFormat sdf_date = new SimpleDateFormat("EEE, d MMM, HH:mm", Locale.getDefault());

        if(min_value!=Long.MAX_VALUE)
        {
            long help_temp=min_value-calendar.getTimeInMillis();
            long cases = help_temp/86400000;
            if(cases==0)
            {
                textView_text_end_of_alert.setText("Будильник через\n"+sdf.format(help_temp+64860000));

                textView_AlertFragment_next_date.setText(sdf_date.format(min_value));
            }
            else if(cases!=0)
            {
                int last = (int) (cases-((cases/10)*10));
                if(cases <= 14) {
                    if(cases == 1)
                        textView_text_end_of_alert.setText("Будильник \nчерез "+cases+ " " + days[0]);
                    if(cases >= 2 && cases <=4)
                        textView_text_end_of_alert.setText("Будильник \nчерез "+cases+ " " + days[1]);
                    if(cases >= 5 && cases <=14)
                        textView_text_end_of_alert.setText("Будильник \nчерез "+cases+ " " + days[2]);
                    textView_AlertFragment_next_date.setText(sdf_date.format(min_value));
                }
                else
                {
                    if(last == 1)
                        textView_text_end_of_alert.setText("Будильник \nчерез "+cases+ " " + days[0]);
                    if(last >= 2 && last <=4)
                        textView_text_end_of_alert.setText("Будильник \nчерез "+cases+ " " + days[1]);
                    if((last >= 5 && last <=9) || last == 0)
                        textView_text_end_of_alert.setText("Будильник \nчерез "+cases+ " " + days[2]);
                    SimpleDateFormat sdf_date_year = new SimpleDateFormat("EEE, d MMM. yyyy г., HH:mm", Locale.getDefault());
                    textView_AlertFragment_next_date.setText(sdf_date_year.format(min_value));
                }
            }
        }
        else
        {
            textView_text_end_of_alert.setText("Все будильники \nотключены");
            textView_AlertFragment_next_date.setText(null);
        }

    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        Log.d("resume_mode","Created");
        assert container != null;
        context = container.getContext();

        AlertViewModel alertViewModel =
                new ViewModelProvider(this).get(AlertViewModel.class);
        binding = FragmentAlertBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        //База данных
        mDBHelper = new DatabaseHelper(context);
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

        //открытие базы данных
        mDBHelper.openDataBase();

        //нахождение ближайшего будильника

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat sdf = new SimpleDateFormat("HH ч. mm мин.", Locale.getDefault());

        //объявляю Textview
        TextView textView_text_end_of_alert = root.findViewById(R.id.text_end_of_alert);
        TextView textView_AlertFragment_next_date = root.findViewById(R.id.AlertFragment_next_date);

        min_value=mDBHelper.return_next_alarm();
        SimpleDateFormat sdf_date = new SimpleDateFormat("EEE, d MMM, HH:mm", Locale.getDefault());
        if(min_value!=Long.MAX_VALUE)
        {
            long help_temp=min_value-calendar.getTimeInMillis();
            long cases = help_temp/86400000;
            if(cases==0)
            {
                textView_text_end_of_alert.setText("Будильник через\n"+sdf.format(help_temp+64860000));

                textView_AlertFragment_next_date.setText(sdf_date.format(min_value));
            }
            else if(cases!=0)
            {
                int last = (int) (cases-((cases/10)*10));
                if(cases <= 14) {
                    if(cases == 1)
                        textView_text_end_of_alert.setText("Будильник \nчерез "+cases+ " " + days[0]);
                    if(cases >= 2 && cases <=4)
                        textView_text_end_of_alert.setText("Будильник \nчерез "+cases+ " " + days[1]);
                    if(cases >= 5 && cases <=14)
                        textView_text_end_of_alert.setText("Будильник \nчерез "+cases+ " " + days[2]);
                    textView_AlertFragment_next_date.setText(sdf_date.format(min_value));
                }
                else
                {
                    if(last == 1)
                        textView_text_end_of_alert.setText("Будильник \nчерез "+cases+ " " + days[0]);
                    if(last >= 2 && last <=4)
                        textView_text_end_of_alert.setText("Будильник \nчерез "+cases+ " " + days[1]);
                    if((last >= 5 && last <=9) || last == 0)
                        textView_text_end_of_alert.setText("Будильник \nчерез "+cases+ " " + days[2]);
                    SimpleDateFormat sdf_date_year = new SimpleDateFormat("EEE, d MMM. yyyy г., HH:mm", Locale.getDefault());
                    textView_AlertFragment_next_date.setText(sdf_date_year.format(min_value));
                }
            }
        }
        else
        {
            textView_text_end_of_alert.setText("Все будильники \nотключены");
            textView_AlertFragment_next_date.setText(null);
        }

        //Добавление данных из базы данных в список
        alarm_list.addAll(mDBHelper.get_alarm_info());

        //вызов ресайклера
        alarmRecycler = root.findViewById(R.id.alarmRecycler);
        setAlarmRecycler(alarm_list);

        //кнопка создания будильника
        ImageButton add_alert = (ImageButton)root.findViewById(R.id.add_alert);
        add_alert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, page_setalert.class);
                intent.putExtra("item_count",alarm_list.size());
                startActivity(intent);
            }
        });

        //закрытие базы данных
        mDBHelper.close();

        return root;
    }

    private void setAlarmRecycler(List<Alarm> alarm_list) {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);

        alarmRecycler.setLayoutManager(layoutManager);

        adapter_alarm = new Adapter_alarm(context, alarm_list, this);
        alarmRecycler.setAdapter(adapter_alarm);
    }

    @Override
    public void onDestroyView() {
        alarm_list.clear();
        super.onDestroyView();
        binding = null;
    }

}