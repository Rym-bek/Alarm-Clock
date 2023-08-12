package com.android.alarm_clock_java.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityOptions;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.android.alarm_clock_java.DatabaseHelper;
import com.android.alarm_clock_java.R;
import com.android.alarm_clock_java.alarm_dialog;
import com.android.alarm_clock_java.interfaces.UpdateTextCallback;
import com.android.alarm_clock_java.models.Alarm;
import com.android.alarm_clock_java.page_setalert;
import com.android.alarm_clock_java.ui.home.AlertFragment;
import com.android.alarm_clock_java.ui.home.AlertViewModel;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class Adapter_alarm extends RecyclerView.Adapter<Adapter_alarm.AlarmViewHolder>{
    Context context;
    List<Alarm> alarms;
    int position_temp;
    private DatabaseHelper mDBHelper;
    private SQLiteDatabase mDb;

    private UpdateTextCallback updateTextCallback;

    public Adapter_alarm(Context context, List<Alarm> alarms, UpdateTextCallback updateTextCallback) {
        this.context = context;
        this.alarms = alarms;
        this.updateTextCallback = updateTextCallback;
    }


    @NonNull
    @Override
    public AlarmViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View alarmItems = LayoutInflater.from(context).inflate(R.layout.item_alarm, parent, false);
        return new AlarmViewHolder(alarmItems);
    }

    @Override
    public void onViewRecycled(@NonNull AlarmViewHolder holder) {
        super.onViewRecycled(holder);
    }


    @Override
    public void onBindViewHolder(@NonNull Adapter_alarm.AlarmViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.AlertFragment_switch.setOnCheckedChangeListener(null);
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
        mDBHelper.openDataBase();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        SimpleDateFormat sdf_date = new SimpleDateFormat("EEE, d MMM", Locale.getDefault());
        long times = Long.valueOf(alarms.get(position).getTime());

        holder.AlertFragment_alarm_time.setText(String.valueOf(sdf.format(times)));

        holder.AlertFragment_alarm_date.setText(String.valueOf(sdf_date.format(times)));

        if(alarms.get(position).getSwitch_state()==1)
        {
            holder.AlertFragment_switch.setChecked(true);
            //изменяю цвет textview
            holder.AlertFragment_alarm_time.setTextColor(ContextCompat.getColor(context, R.color.black));
            holder.AlertFragment_alarm_date.setTextColor(ContextCompat.getColor(context, R.color.black));
        }
        else
        {
            holder.AlertFragment_switch.setChecked(false);
            //изменяю цвет textview
            holder.AlertFragment_alarm_time.setTextColor(ContextCompat.getColor(context, R.color.grey));
            holder.AlertFragment_alarm_date.setTextColor(ContextCompat.getColor(context, R.color.grey));
        }

        holder.AlertFragment_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @SuppressLint({"ObsoleteSdkInt", "ResourceAsColor"})
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                context = compoundButton.getContext();
                position_temp=position;


                AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                if(isChecked)
                {

                    mDBHelper.update_alarm_switch_state(alarms.get(position_temp).getId(),1);

                    //очень важно! обновление оставшегося времени по изменении состояния Switch
                    updateTextCallback.updateText(alarms.get(position).getTime());
                    //обновляю модель
                    alarms.get(position_temp).setSwitch_state(1);

                    //изменяю цвет textview
                    holder.AlertFragment_alarm_time.setTextColor(ContextCompat.getColor(context, R.color.black));
                    holder.AlertFragment_alarm_date.setTextColor(ContextCompat.getColor(context, R.color.black));

                    //устанавливаю будильник
                    AlarmManager.AlarmClockInfo alarmClockInfo = new AlarmManager.AlarmClockInfo(Long.valueOf(alarms.get(position_temp).getTime()),getAlarmInfoPendingIntent(position_temp));
                    alarmManager.setAlarmClock(alarmClockInfo, getAlarmActionPendingIntent(position_temp));
                    SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
                    Toast.makeText(context, "Установлен "+sdf2.format(times), Toast.LENGTH_SHORT).show();
                }
                else
                {


                    //изменяю цвет textview
                    holder.AlertFragment_alarm_time.setTextColor(ContextCompat.getColor(context, R.color.grey));
                    holder.AlertFragment_alarm_date.setTextColor(ContextCompat.getColor(context, R.color.grey));

                    Log.d("jojo_jojo_switch",String.valueOf(position_temp));
                    mDBHelper.update_alarm_switch_state(alarms.get(position_temp).getId(),0);

                    //очень важно! обновление оставшегося времени по изменении состояния Switch
                    updateTextCallback.updateText(alarms.get(position).getTime());

                    //обновляю модель
                    alarms.get(position_temp).setSwitch_state(0);
                    alarmManager.cancel(getAlarmActionPendingIntent(position_temp));
                }
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context, page_setalert.class);
                intent.putExtra("getId", alarms.get(position).getId());
                intent.putExtra("getTime", alarms.get(position).getTime());
                intent.putExtra("getMusic_state", alarms.get(position).getMusic_state());
                intent.putExtra("getVibration_state", alarms.get(position).getVibration_state());
                intent.putExtra("getMusic_title", alarms.get(position).getMusic_title());
                intent.putExtra("getVibration_title", alarms.get(position).getVibration_title());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return alarms.size();
    }

    public static final class AlarmViewHolder extends RecyclerView.ViewHolder{
        private final TextView AlertFragment_alarm_time;
        private final TextView AlertFragment_alarm_date;
        @SuppressLint("UseSwitchCompatOrMaterialCode")
        private final Switch AlertFragment_switch;

        public AlarmViewHolder(@NonNull View itemView) {

            super(itemView);
            AlertFragment_alarm_time = (TextView) itemView.findViewById(R.id.AlertFragment_alarm_time);
            AlertFragment_alarm_date = (TextView) itemView.findViewById(R.id.AlertFragment_alarm_date);
            AlertFragment_switch = (Switch) itemView.findViewById(R.id.AlertFragment_switch);

        }
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private PendingIntent getAlarmInfoPendingIntent(int position_temp){
        Intent alarmInfoIntent = new Intent(context, AlertFragment.class);
        alarmInfoIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        return PendingIntent.getActivity(context, alarms.get(position_temp).getId(), alarmInfoIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }
    @SuppressLint("UnspecifiedImmutableFlag")
    private PendingIntent getAlarmActionPendingIntent(int position_temp) {
        Log.d("jojo_6",String.valueOf(alarms.get(position_temp).getId()));
        Intent intent = new Intent(context, alarm_dialog.class);
        intent.putExtra("id",alarms.get(position_temp).getId());
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        return PendingIntent.getActivity(context, alarms.get(position_temp).getId(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
