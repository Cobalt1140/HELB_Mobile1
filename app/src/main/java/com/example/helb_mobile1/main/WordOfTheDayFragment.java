package com.example.helb_mobile1.main;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.helb_mobile1.R;
import com.example.helb_mobile1.auth.AuthViewModel;

import org.json.JSONException;


public class WordOfTheDayFragment extends Fragment {

    WordOfTheDayViewModel wotdViewModel;
    TextView wordDisplay;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_word_of_the_day, container, false);

        PreferencesManager prefs = new PreferencesManager(requireActivity());
        WordOfTheDayViewModelFactory factory = new WordOfTheDayViewModelFactory(prefs);
        wotdViewModel = new ViewModelProvider(this, factory).get(WordOfTheDayViewModel.class);

        wordDisplay = view.findViewById(R.id.WOTD_Word_Text);

        observeViewModel();
        return view;
    }

    private void observeViewModel(){
        wotdViewModel.fetchWordIfNeeded(requireActivity());
        wotdViewModel.getWordLiveData().observe(getViewLifecycleOwner(), word -> {
            if (word != null && wordDisplay != null){
                wordDisplay.setText(word.substring(0,1).toUpperCase()+word.substring(1));
            }
        });
    }
}