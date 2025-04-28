package com.example.helb_mobile1.auth;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AuthViewModel extends ViewModel {

    private final AuthManager authManager;
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> authError = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoggedIn = new MutableLiveData<>();

    public AuthViewModel() {
        authManager = AuthManager.getInstance();
        checkLoginStatus();
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getAuthError() {
        return authError;
    }

    public LiveData<Boolean> getIsLoggedIn() {
        return isLoggedIn;
    }

    private void checkLoginStatus() {
        isLoggedIn.setValue(authManager.isLoggedIn());
    }

    public void login(String email, String password) {
        isLoading.setValue(true);
        authManager.signInUser(email, password)
                .addOnCompleteListener(task -> {
                    isLoading.setValue(false);
                    if (task.isSuccessful()) {
                        isLoggedIn.setValue(true);
                    } else {
                        authError.setValue(task.getException().getMessage());
                    }
                });
    }

    public void register(String email, String password) {
        isLoading.setValue(true);
        authManager.registerUser(email, password)
                .addOnCompleteListener(task -> {
                    isLoading.setValue(false);
                    if (task.isSuccessful()) {
                        isLoggedIn.setValue(true);
                    } else {
                        authError.setValue(task.getException().getMessage());
                    }
                });
    }
}
