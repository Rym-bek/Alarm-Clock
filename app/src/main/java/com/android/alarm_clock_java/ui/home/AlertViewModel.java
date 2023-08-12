package com.android.alarm_clock_java.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AlertViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public AlertViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Все будильники \nотключены");
    }

    public LiveData<String> getText() {
        return mText;
    }

    public void setText(String text_get)
    {
        mText = new MutableLiveData<>();
        mText.postValue(text_get);
    }

}