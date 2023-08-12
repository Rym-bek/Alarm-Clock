package com.android.alarm_clock_java;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

import com.android.alarm_clock_java.models.Alarm;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "vibration_info.db";
    private static String DB_PATH = "";
    private static final int DB_VERSION = 52;

    private SQLiteDatabase mDataBase;
    private final Context mContext;
    private boolean mNeedUpdate = false;

    Calendar calendar_static=Calendar.getInstance();;

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);

        if (android.os.Build.VERSION.SDK_INT >= 17)
            DB_PATH = context.getApplicationInfo().dataDir + "/databases/";
        else
            DB_PATH = "/data/data/" + context.getPackageName() + "/databases/";

        //DB_PATH = Environment.getExternalStorageDirectory() + "/Ringtones/";
        this.mContext = context;

        copyDataBase();

        this.getReadableDatabase();
    }

    public void updateDataBase() throws IOException {
        if (mNeedUpdate) {
            File dbFile = new File(DB_PATH + DB_NAME);
            if (dbFile.exists())
                dbFile.delete();

            copyDataBase();

            mNeedUpdate = false;
        }
    }

    private boolean checkDataBase() {
        File dbFile = new File(DB_PATH + DB_NAME);
        return dbFile.exists();
    }

    private void copyDataBase() {
        if (!checkDataBase()) {
            this.getReadableDatabase();
            this.close();
            try {
                copyDBFile();
            } catch (IOException mIOException) {
                throw new Error("ErrorCopyingDataBase");
            }
        }
    }

    private void copyDBFile() throws IOException {
        InputStream mInput = mContext.getAssets().open(DB_NAME);
        OutputStream mOutput = new FileOutputStream(DB_PATH + DB_NAME);
        byte[] mBuffer = new byte[1024];
        int mLength;
        while ((mLength = mInput.read(mBuffer)) > 0)
            mOutput.write(mBuffer, 0, mLength);
        mOutput.flush();
        mOutput.close();
        mInput.close();
    }

    public boolean openDataBase() throws SQLException {
        mDataBase = SQLiteDatabase.openDatabase(DB_PATH + DB_NAME, null, SQLiteDatabase.CREATE_IF_NECESSARY);
        return mDataBase != null;
    }

    @Override
    public synchronized void close() {
        if (mDataBase != null)
            mDataBase.close();
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion)
            mNeedUpdate = true;
    }

    public String get_music_title(int id_get){
        String query = "SELECT music_title From alarm where id = ?";
        Cursor cursor = mDataBase.rawQuery(query, new String[] {String.valueOf(id_get)});
        String title="";
        if (cursor.moveToFirst())
        {
            title = cursor.getString(0);
            Log.d("ZAVTRA",title);
        }
        cursor.close();

        return title;
    }

    public long return_next_alarm(){
        Cursor cursor = mDataBase.rawQuery("SELECT time FROM alarm WHERE switch_state = 1", null);
        cursor.moveToFirst();
        long min_value=Long.MAX_VALUE;
        while (!cursor.isAfterLast()) {
            long temp = Long.valueOf(cursor.getString(0));
            if(temp<min_value)
            {
                min_value=temp;
            }
            cursor.moveToNext();
        }
        cursor.close();

        return min_value;
    }

    public int booleanToInt(boolean switch_state) {
        int temp = 0;
        if (switch_state) {
            temp = 1;
        }
        return temp;
    }

    public void update_alarm_switch_state(int id_get, int switch_state_get){
        String query = "UPDATE alarm SET switch_state = " +switch_state_get+ " WHERE id = "+ id_get;
        mDataBase.execSQL(query);
    }
    public List get_alarm_info(){
        List<Alarm> alarm_list = new ArrayList<>();
        String query ="SELECT * FROM alarm";
        Cursor cursor = mDataBase.rawQuery(query, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            int id = cursor.getInt(0);
            String time = cursor.getString(1);


            int music_state = cursor.getInt(2);
            int vibration_state = cursor.getInt(3);
            int switch_state = cursor.getInt(4);
            String music_title=cursor.getString(5);
            String vibration_title=cursor.getString(6);
            int switch_state_temp=switch_state;
            //прибавить один день если будильник уже прошёл
            long time_long=Long.valueOf(time);
            if(Long.valueOf(time)<=calendar_static.getTimeInMillis())
            {
                time_long+=86400000;
                String query_add_day = "UPDATE alarm SET time = " +time_long+ ", switch_state = 0 WHERE id = "+ id;
                switch_state_temp=0;
                mDataBase.execSQL(query_add_day);
            }
            //передать новое значение на экран и в базу данных
            time=String.valueOf(time_long);
            alarm_list.add(new Alarm(id,time,music_state,vibration_state,switch_state_temp, music_title, vibration_title));
            cursor.moveToNext();
        }
        cursor.close();

        return alarm_list;
    }

    public void update_alarm_info(int id, long calendar, boolean switch_music_pagesetalert, boolean switch_vibration_pagesetalert, String radio_value_selection, String radio_value_selection_vibration){
        ContentValues values = new ContentValues();
        values.put("time", calendar);
        values.put("music_state", booleanToInt(switch_music_pagesetalert));
        values.put("vibration_state", booleanToInt(switch_vibration_pagesetalert));
        values.put("switch_state", 1);
        values.put("music_title", radio_value_selection);
        values.put("vibration_title", radio_value_selection_vibration);
        mDataBase.update("alarm", values, "id = ?", new String[]{String.valueOf(id)});
    }

    public void add_alarm_info(long calendar, boolean switch_music_pagesetalert, boolean switch_vibration_pagesetalert, String radio_value_selection, String radio_value_selection_vibration){
        ContentValues values = new ContentValues();
        values.put("time", calendar);
        values.put("music_state", booleanToInt(switch_music_pagesetalert));
        values.put("vibration_state", booleanToInt(switch_vibration_pagesetalert));
        values.put("switch_state", 1);
        values.put("music_title", radio_value_selection);
        values.put("vibration_title", radio_value_selection_vibration);
        mDataBase.insert("alarm", null, values);
    }

    public String get_vibration_amplitudes_with_name(String button_name){
        String query = "SELECT * From sounds where title = ?";
        Cursor cursor = mDataBase.rawQuery(query, new String[] {String.valueOf(button_name)});
        String title="";
        if (cursor.moveToFirst())
        {
            title = cursor.getString(3);
        }
        cursor.close();

        return title;
    }

    public String get_vibration_timings_with_name(String button_name){
        String query = "SELECT * From sounds where title = ?";
        Cursor cursor = mDataBase.rawQuery(query, new String[] {String.valueOf(button_name)});
        String title="";
        if (cursor.moveToFirst())
        {
            title = cursor.getString(2);
        }
        cursor.close();

        return title;
    }

    public ArrayList get_all_songs(){
        ArrayList list_get_all_songs = new ArrayList<String>();
        Cursor cursor = mDataBase.rawQuery("SELECT * FROM songs", null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            list_get_all_songs.add(cursor.getString(1));
            cursor.moveToNext();
        }
        cursor.close();

        return list_get_all_songs;
    }

    public void add_song_name(String title_value){
        ContentValues values = new ContentValues();
        values.put("title", title_value);
        mDataBase.insert("songs", null, values);
    }


    public String get_vibration_amplitudes(int button_id){
        String query = "SELECT * From sounds where id = ?";
        Cursor cursor = mDataBase.rawQuery(query, new String[] {String.valueOf(button_id)});
        String title="";
        if (cursor.moveToFirst())
        {
            title = cursor.getString(3);
        }
        cursor.close();

        return title;
    }

    public String get_vibration_timings(int button_id){
        String query = "SELECT * From sounds where id = ?";
        Cursor cursor = mDataBase.rawQuery(query, new String[] {String.valueOf(button_id)});
        String title="";
        if (cursor.moveToFirst())
        {
            title = cursor.getString(2);
        }
        cursor.close();

        return title;
    }

    public String get_name(int button_id){
        String query = "SELECT * From sounds where id = ?";
        Cursor cursor = mDataBase.rawQuery(query, new String[] {String.valueOf(button_id)});
        String title="";
        if (cursor.moveToFirst())
        {
            title = cursor.getString(1);
        }
        cursor.close();

        return title;
    }

    public int get_vibrations_count(){
        String query = "SELECT COUNT (*)  From sounds";
        Cursor cursor = mDataBase.rawQuery(query, null);
        cursor.moveToFirst();
        int count= cursor.getInt(0);
        cursor.close();
        return count;
    }
}