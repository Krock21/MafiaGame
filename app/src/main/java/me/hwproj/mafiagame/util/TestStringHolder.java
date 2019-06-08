package me.hwproj.mafiagame.util;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/**
 * A test class which is not used in actual game
 */
public class TestStringHolder extends ViewModel {

    private final MutableLiveData<String> data;
    private String s = "";
    private final Object sync = new Object();

    public TestStringHolder() {
        data = new MutableLiveData<>();
        synchronized (sync) {
            modifyData();
        }
    }

    public void append(char c) {
        synchronized (sync) {
            s = s + c;
            modifyData();
        }
    }

    public void append(String cc) {
        synchronized (sync) {
            s = s + cc;
            modifyData();
        }
    }

    public String get() {
        synchronized (sync) {
            return s;
        }
    }

    public void setText(String s) {
        synchronized (sync) {
            this.s = s;
            modifyData();
        }
    }

    public LiveData<String> getData() {
        return data;
    }

    private void modifyData() {
        data.postValue(s);
    }
}
