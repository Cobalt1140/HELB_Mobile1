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

    private TextView usernameTxt;
    private TextView pointTxt;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_account, container, false);


        usernameTxt = view.findViewById(R.id.Account_Username_Text);
        pointTxt = view.findViewById(R.id.Account_Point_Total_Text);
        Button logOutButton = view.findViewById(R.id.Account_LogOut_Button);

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
        PreferencesManager prefs = PreferencesManager.getInstance(requireContext());
        usernameTxt.setText(prefs.getCachedUsername());
        pointTxt.setText("Points: "+String.valueOf(prefs.getCachedPointTotal()));
    }



    @Override
    public void onFragmentVisible() {
        setUsernameAndPoints();
    }
}