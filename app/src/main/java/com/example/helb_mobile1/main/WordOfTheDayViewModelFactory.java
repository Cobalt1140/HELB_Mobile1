package com.example.helb_mobile1.main;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class WordOfTheDayViewModelFactory implements ViewModelProvider.Factory {
    private final PreferencesManager prefs;

    public WordOfTheDayViewModelFactory(PreferencesManager prefs) {
        this.prefs = prefs;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(WordOfTheDayViewModel.class)) {
            return (T) new WordOfTheDayViewModel(prefs);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}