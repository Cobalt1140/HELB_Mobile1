package com.example.helb_mobile1.auth;


import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.helb_mobile1.managers.AuthManager;
import com.example.helb_mobile1.managers.DatabaseManager;
import com.example.helb_mobile1.managers.db_callbacks.IUserDataCallback;
import com.example.helb_mobile1.managers.PreferencesManager;
import com.example.helb_mobile1.R;
import com.example.helb_mobile1.main.MainActivity;


public class RegisterFragment extends Fragment {
    /*
    fragment for the register screen
     */


    private AuthViewModel authViewModel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /*
        handles views, sets up ViewModel
         */
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        EditText emailInput = view.findViewById(R.id.Register_Email_Input);
        EditText passwordInput = view.findViewById(R.id.Register_Password_Input);
        EditText usernameInput = view.findViewById(R.id.Register_Username_Input);
        Button registerButton = view.findViewById(R.id.Register_Button);
        Button loginRedirectButton = view.findViewById(R.id.Switch_to_Login_Button);
        CheckBox visiblePasswordBox = view.findViewById(R.id.Register_Visible_Password);

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        observeViewModel();

        registerButton.setOnClickListener(v -> {
            if (emailInput.getText().length()!=0 && passwordInput.getText().length()!=0 &&
                    usernameInput.getText().length() != 0){
                String email = emailInput.getText().toString().strip();
                String password = passwordInput.getText().toString().strip();
                String username = usernameInput.getText().toString().strip();
                authViewModel.register(email, password, username);
            }

        });

        loginRedirectButton.setOnClickListener(v -> {
            if (getActivity() instanceof AuthActivity) {
                AuthActivity authActivity = (AuthActivity) getActivity();
                authActivity.loadFragment(new LoginFragment());
            }
        });


        visiblePasswordBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            //code from https://droidbyme.medium.com/show-hide-password-in-edittext-in-android-c4c3db44f734
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    passwordInput.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    passwordInput.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });

        return view;
    }

    private void observeViewModel() {
        authViewModel.getIsLoggedIn().observe(getViewLifecycleOwner(), isLoggedIn -> {
            if (isLoggedIn){
                DatabaseManager.getInstance().fetchAndHandleAccountData(AuthManager.getInstance().getCurrentUid(), new IUserDataCallback() {
                    @Override
                    public void onUserDataReceived(String username, long points) {
                        PreferencesManager prefs = PreferencesManager.getInstance(requireContext());
                        prefs.saveUsernameInCache(username);
                        prefs.savePointTotalInCache(points);
                        Intent intent = new Intent(requireActivity(), MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }

                    @Override
                    public void onError(String error) {
                        Toast.makeText(getContext(), "Error acquiring username and points: "+error, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(requireActivity(), MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                });
            }
        });
        authViewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading) { //TODO add loading spinner animation

            } else {

            }
        });

        authViewModel.getAuthError().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {

                Toast.makeText(getContext(), "Signup Failed: " + error, Toast.LENGTH_SHORT).show();


            }
        });


    }
}
