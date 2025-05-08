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


public class LoginFragment extends Fragment {

    private EditText emailInput, passwordInput;
    private AuthViewModel authViewModel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        //On Screen buttons and inputs
        emailInput = view.findViewById(R.id.Login_Email_Input);
        passwordInput = view.findViewById(R.id.Login_Password_Input);
        Button loginButton = view.findViewById(R.id.Login_Button);
        Button signupRedirectButton = view.findViewById(R.id.Switch_to_Register_Button);
        CheckBox visiblePasswordBox = view.findViewById(R.id.Login_Visible_Password);

        //ViewModel
        authViewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);


        observeViewModel();



        loginButton.setOnClickListener(v -> {
            if (emailInput.getText().length()!=0 && passwordInput.getText().length()!=0){
                String email = emailInput.getText().toString().strip();
                String password = passwordInput.getText().toString().strip();
                authViewModel.login(email, password);
            }


        });

        signupRedirectButton.setOnClickListener(v -> {
            // Switch to SignupFragment
            if (getActivity() instanceof AuthActivity) {
                AuthActivity authActivity = (AuthActivity) getActivity();
                authActivity.loadFragment(new RegisterFragment());
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



    private void observeViewModel() { //Deal with info changes in ViewModel, like loading spinner
        authViewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading) { //TODO add loading spinner animation


            }
        });

        authViewModel.getAuthError().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {

                Toast.makeText(getContext(), "Login Failed: " + error, Toast.LENGTH_SHORT).show();


            }
        });

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
    }
}