package com.example.helb_mobile1.main.dailyWord;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.helb_mobile1.main.AppViewModelFactory;
import com.example.helb_mobile1.R;
import com.example.helb_mobile1.main.IOnFragmentVisibleListener;


public class DailyWordFragment extends Fragment implements IOnFragmentVisibleListener {

    private DailyWordViewModel dailyWordViewModel;
    private TextView wordDisplay;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_word_of_the_day, container, false);


        AppViewModelFactory factory = new AppViewModelFactory(requireContext());
        dailyWordViewModel = new ViewModelProvider(this, factory).get(DailyWordViewModel.class);

        wordDisplay = view.findViewById(R.id.WOTD_Word_Text);

        observeViewModel();
        return view;
    }

    private void observeViewModel(){
        dailyWordViewModel.fetchWordIfNeeded(requireActivity());
        dailyWordViewModel.getWordLiveData().observe(getViewLifecycleOwner(), word -> {
            if (word != null && wordDisplay != null){
                wordDisplay.setText(word.substring(0,1).toUpperCase()+word.substring(1));
            }
        });

        dailyWordViewModel.getErrorLiveData().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {

                Toast.makeText(getContext(),  error, Toast.LENGTH_SHORT).show();


            }
        });
    }

    @Override
    public void onFragmentVisible(){
        dailyWordViewModel.fetchWordIfNeeded(requireActivity());
    }
}