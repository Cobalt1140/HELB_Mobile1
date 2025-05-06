package com.example.helb_mobile1.auth;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.helb_mobile1.AuthManager;
import com.example.helb_mobile1.DatabaseManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class AuthViewModel extends ViewModel {

    private final AuthManager authManager;
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> authError = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoggedIn = new MutableLiveData<>();

    public AuthViewModel() {
        authManager = AuthManager.getInstance();
        isLoggedIn.setValue(authManager.isLoggedIn());
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

    public void register(String email, String password, String username) {
        isLoading.setValue(true);

        DatabaseManager.getInstance().handleIsUsernameTaken(username, new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()){
                    authError.setValue("Username is already taken!");
                    isLoading.setValue(false);
                } else {
                    authManager.registerUser(email, password)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    DatabaseManager.getInstance().createUserProfile(AuthManager.getInstance().getCurrentUid(),username);
                                    isLoading.setValue(false);
                                    isLoggedIn.setValue(true);
                                } else {
                                    authError.setValue(task.getException().getMessage());
                                    isLoading.setValue(false);
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                authError.setValue("Error checking username with database: "+error.getMessage());
                isLoading.setValue(false);
            }
        });


    }


}
