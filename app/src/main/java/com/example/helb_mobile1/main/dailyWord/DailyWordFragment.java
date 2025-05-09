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
    /*
    One of the Main 4 fragments contained in Main Activity
    Handles displaying the daily word and countdown for submission time and new word time
     */

    private DailyWordViewModel dailyWordViewModel;
    private TextView wordDisplay;
    private TextView countdownText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        /*
        Handles views, sets off initial functions in ViewModel, sets up connection to viewModel
         */

        View view = inflater.inflate(R.layout.fragment_word_of_the_day, container, false);

        countdownText = view.findViewById(R.id.WOTD_Countdown_Text);



        AppViewModelFactory factory = new AppViewModelFactory(requireContext()); //Gives App Context to ViewModel for SharedPreferences
        dailyWordViewModel = new ViewModelProvider(this, factory).get(DailyWordViewModel.class);

        wordDisplay = view.findViewById(R.id.WOTD_Word_Text);

        dailyWordViewModel.fetchWordIfNeeded();

        dailyWordViewModel.setCountdownAdapted();

        observeViewModel();
        return view;
    }

    private void observeViewModel(){
        /*
        Deals with data from DailyWordViewModel
         */
        dailyWordViewModel.getWordLiveData().observe(getViewLifecycleOwner(), word -> {
            if (word != null && wordDisplay != null){
                wordDisplay.setText("Mot Quotidien:\n"+word.substring(0,1).toUpperCase()+word.substring(1));
            }
        });

        dailyWordViewModel.getErrorLiveData().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(getContext(),  error, Toast.LENGTH_SHORT).show();
            }
        });

        dailyWordViewModel.getCountdownLiveData().observe(getViewLifecycleOwner(), time -> {
            if (time != null) {
                countdownText.setText(time);
            }
        });
    }



    @Override
    public void onFragmentVisible(){
        /*
        implementing IOnFragmentVisibleListener lets the fragment trigger code each time the fragment
        is selected in the BottomNavigationMenu
         */
        dailyWordViewModel.fetchWordIfNeeded();
        dailyWordViewModel.setCountdownAdapted();
    }
}