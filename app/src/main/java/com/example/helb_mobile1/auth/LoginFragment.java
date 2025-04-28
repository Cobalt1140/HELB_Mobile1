package com.example.helb_mobile1.auth;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.helb_mobile1.R;


public class LoginFragment extends Fragment {

    private EditText emailInput, passwordInput;
    private AuthViewModel authViewModel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        emailInput = view.findViewById(R.id.Login_Email_Input);
        passwordInput = view.findViewById(R.id.Login_Password_Input);
        Button loginButton = view.findViewById(R.id.Login_Button);
        Button signupRedirectButton = view.findViewById(R.id.Switch_to_Register_Button);

        authViewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);

        observeViewModel();

        loginButton.setOnClickListener(v -> {
            String email = emailInput.getText().toString().strip();
            String password = passwordInput.getText().toString().strip();
            authViewModel.login(email, password);
        });

        signupRedirectButton.setOnClickListener(v -> {
            // Switch to SignupFragment
            if (getActivity() instanceof AuthActivity) {
                AuthActivity authActivity = (AuthActivity) getActivity();
                authActivity.loadFragment(new RegisterFragment());
            }
        });

        return view;
    }

    private void observeViewModel() {
        authViewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading) {
                /*
                startActivity(new Intent(getActivity(), MainActivity.class));
                requireActivity().finish();

                 */
            }
        });

        authViewModel.getAuthError().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {

                Toast.makeText(getContext(), "Login Failed: " + error, Toast.LENGTH_SHORT).show();


            }
        });
    }
}