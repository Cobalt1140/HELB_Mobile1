package com.example.helb_mobile1.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.helb_mobile1.R;
import com.example.helb_mobile1.auth.AuthManager;


public class AccountFragment extends Fragment {



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        Button logOutBtn = view.findViewById(R.id.temp_logOut);
        logOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AuthManager.getInstance().signOutUser();
                Toast.makeText(requireActivity(), "Signed Out!", Toast.LENGTH_SHORT).show();

            }
        });

        return view;
    }
}