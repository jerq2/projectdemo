package com.example.myapplication.ui.login;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class LoginViewModel extends ViewModel {

    // LiveData to observe the email and password fields
    private final MutableLiveData<String> email = new MutableLiveData<>();
    private final MutableLiveData<String> password = new MutableLiveData<>();

    // LiveData for observing the login result (success or error)
    private final MutableLiveData<Boolean> loginResult = new MutableLiveData<>();

    public LiveData<String> getEmail() {
        return email;
    }

    public LiveData<String> getPassword() {
        return password;
    }

    public LiveData<Boolean> getLoginResult() {
        return loginResult;
    }

    // Method to handle login logic (e.g., Firebase Auth or local validation)
    public void login(String enteredEmail, String enteredPassword) {
        // Simple validation (you could replace this with Firebase Authentication logic)
        if (enteredEmail != null && enteredPassword != null && enteredEmail.contains("@") && enteredPassword.length() > 5) {
            // Assuming the login succeeds
            loginResult.setValue(true);
        } else {
            // If validation fails
            loginResult.setValue(false);
        }
    }

    // Set email and password from user input
    public void setEmail(String newEmail) {
        email.setValue(newEmail);
    }

    public void setPassword(String newPassword) {
        password.setValue(newPassword);
    }
}
