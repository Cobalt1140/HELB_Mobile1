package com.example.helb_mobile1.main.dailyWord;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class DailyWordViewModelFactory implements ViewModelProvider.Factory {
    private final Context context;

    public DailyWordViewModelFactory(Context context) {
        this.context = context.getApplicationContext();
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(DailyWordViewModel.class)) {
            return (T) new DailyWordViewModel(context);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}