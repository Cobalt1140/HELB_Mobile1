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


public class RegisterFragment extends Fragment {

    private EditText emailInput, passwordInput;
    private AuthViewModel authViewModel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        emailInput = view.findViewById(R.id.Register_Email_Input);
        passwordInput = view.findViewById(R.id.Register_Password_Input);
        Button registerButton = view.findViewById(R.id.Register_Button);
        Button loginRedirectButton = view.findViewById(R.id.Switch_to_Login_Button);

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        observeViewModel();

        registerButton.setOnClickListener(v -> {
            String email = emailInput.getText().toString();
            String password = passwordInput.getText().toString();
            authViewModel.register(email, password);
        });

        loginRedirectButton.setOnClickListener(v -> {
            if (getActivity() instanceof AuthActivity) {
                AuthActivity authActivity = (AuthActivity) getActivity();
                authActivity.loadFragment(new LoginFragment());
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
            } else {

            }
        });

        authViewModel.getAuthError().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {

                Toast.makeText(getContext(), "Signup Failed: " + error, Toast.LENGTH_SHORT).show();


            }
        });

        authViewModel.getIsLoggedIn().observe(getViewLifecycleOwner(), isLoggedIn -> {
            if (isLoggedIn){

            }
        });
    }
}
