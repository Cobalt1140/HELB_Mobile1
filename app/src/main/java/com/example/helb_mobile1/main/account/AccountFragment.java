package com.example.helb_mobile1.main.account;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.helb_mobile1.managers.AuthManager;
import com.example.helb_mobile1.managers.PreferencesManager;
import com.example.helb_mobile1.R;
import com.example.helb_mobile1.auth.AuthActivity;
import com.example.helb_mobile1.main.IOnFragmentVisibleListener;


public class AccountFragment extends Fragment implements IOnFragmentVisibleListener {
    /*
    One of the 4 main fragments in MainActivity, handles logging out and account info display
     */

    private static final String EXPLANATION_TEXT = "Bienvenue à 'En Plaine Vue'!\nVotre objectif est " +
            "de capturer une photo qui, selon vous, est en rapport avec le mot quotidien.\n " +
            "Vous recevrez des points par rapport à la distance aux autres captures des autres utilisateurs." +
            "\n Au plus il y a des captures qui sont condensées au même endroit que la votre, au plus de points vous recevrez." +
            "\n En effet, l'objectif du jeu est de deviner ce que les autres utilisateurs pensent quands ils entendent le mot quotidien." +
            "\n Par exemple, avec le mot 'arbre', vous pouvez bien prendre une photo de n'importe quel arbre du campus de la Plaine." +
            "\n Cependant, pensez à quel arbre serait le plus iconique du campus selon la majorité des gens." +
            "\n Un nouveau mot apparaît chaque jour à 8h et le temps de soumission se finit à 18h.";
    //String put in code because putting it in xml values doesn't work

    private TextView usernameTxt;
    private TextView pointTxt;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        /*
        Handles views, and updates Account info
         */
        View view = inflater.inflate(R.layout.fragment_account, container, false);


        usernameTxt = view.findViewById(R.id.Account_Username_Text);
        pointTxt = view.findViewById(R.id.Account_Point_Total_Text);
        Button logOutButton = view.findViewById(R.id.Account_LogOut_Button);

        TextView explanation = view.findViewById(R.id.Account_Explanation);
        explanation.setText(EXPLANATION_TEXT);

        setUsernameAndPoints();

        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PreferencesManager.getInstance(requireContext()).resetPersonalMarkerInCache();
                AuthManager.getInstance().signOutUser();
                Intent intent = new Intent(requireActivity(), AuthActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });


        return view;
    }


    private void setUsernameAndPoints(){
        /*
        Gets info from sharedPreferences, like username and point totals and displays them
         */
        PreferencesManager prefs = PreferencesManager.getInstance(requireContext());
        usernameTxt.setText(prefs.getCachedUsername());
        pointTxt.setText("Points: "+String.valueOf(prefs.getCachedPointTotal()));
    }



    @Override
    public void onFragmentVisible() {
        setUsernameAndPoints();
    }
}