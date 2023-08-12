package com.android.alarm_clock_java.models;

public class Alarm {
    int id, music_state, vibration_state, switch_state;
    String time, music_title, vibration_title;

    public Alarm(int id, String time, int music_state, int vibration_state, int switch_state, String music_title, String vibration_title) {
        this.id = id;
        this.music_state = music_state;
        this.vibration_state = vibration_state;
        this.switch_state = switch_state;
        this.time = time;
        this.music_title = music_title;
        this.vibration_title = vibration_title;
    }

    public String getMusic_title() {
        return music_title;
    }

    public void setMusic_title(String music_title) {
        this.music_title = music_title;
    }

    public String getVibration_title() {
        return vibration_title;
    }

    public void setVibration_title(String vibration_title) {
        this.vibration_title = vibration_title;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMusic_state() {
        return music_state;
    }

    public void setMusic_state(int music_state) {
        this.music_state = music_state;
    }

    public int getVibration_state() {
        return vibration_state;
    }

    public void setVibration_state(int vibration_state) {
        this.vibration_state = vibration_state;
    }

    public int getSwitch_state() {
        return switch_state;
    }

    public void setSwitch_state(int switch_state) {
        this.switch_state = switch_state;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
