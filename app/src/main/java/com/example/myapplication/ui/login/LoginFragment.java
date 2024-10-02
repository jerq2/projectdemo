package com.example.myapplication.ui.login;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginFragment extends Fragment {

    private EditText etEmail, etPassword;
    private Button btnLogin, btnSignUp;
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private static final String TAG = "LoginFragment";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Initialize the view
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        // Initialize Firebase instances
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialize UI elements
        etEmail = view.findViewById(R.id.etEmail);
        etPassword = view.findViewById(R.id.etPassword);
        btnLogin = view.findViewById(R.id.btnLogin);
        btnSignUp = view.findViewById(R.id.btnSignUp);

        // Set click listeners
        btnLogin.setOnClickListener(view1 -> loginUser());
        btnSignUp.setOnClickListener(view1 -> navigateToSignUp());

        return view;
    }

    private void loginUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Validate email and password
        if (email.isEmpty()) {
            etEmail.setError("Email is required");
            etEmail.requestFocus();
            return;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Enter a valid email");
            etEmail.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            etPassword.setError("Password is required");
            etPassword.requestFocus();
            return;
        }
        if (password.length() < 6) {
            etPassword.setError("Password should be at least 6 characters");
            etPassword.requestFocus();
            return;
        }

        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = auth.getCurrentUser();
                if (user != null) {
                    fetchUserData(user);
                } else {
                    Toast.makeText(getActivity(), "User is null after login", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "User is null after login");
                }
            } else {
                Exception exception = task.getException();
                if (exception instanceof FirebaseAuthInvalidUserException) {
                    // Email doesn't exist
                    Toast.makeText(getActivity(), "Login Failed: The Email Doesn't Exist", Toast.LENGTH_SHORT).show();
                } else if (exception instanceof FirebaseAuthInvalidCredentialsException) {
                    // Check if the issue is with the password
                    if (exception.getMessage().contains("password")) {
                        Toast.makeText(getActivity(), "Login Failed: Wrong Password", Toast.LENGTH_SHORT).show();
                    } else {
                        // Handle invalid email or other credential issues
                        Toast.makeText(getActivity(), "Login Failed: Email or Password is wrong", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // General error
                    Toast.makeText(getActivity(), "Login Failed: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                }
                Log.e("LoginError", "Login failed: " + exception.getMessage());
            }
        });
    }


    private void fetchUserData(FirebaseUser user) {
        db.collection("users").document(user.getUid()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    // Check the isAdmin field
                    Boolean isAdmin = document.getBoolean("isAdmin");
                    if (isAdmin != null && isAdmin) {
                        navigateToAdmin();
                    } else {
                        navigateToHome();
                    }
                } else {
                    Toast.makeText(getActivity(), "User document not found", Toast.LENGTH_SHORT).show();
                    Log.e("LoginError", "User document not found in Firestore");
                }
            } else {
                Toast.makeText(getActivity(), "Error fetching user data: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("LoginError", "Error fetching user data: " + task.getException().getMessage());
            }
        });
    }

    private void navigateToAdmin() {
        if (getView() != null) {
            Navigation.findNavController(getView()).navigate(R.id.action_loginFragment_to_adminFragment);
        } else {
            Log.e(TAG, "getView() returned null when navigating to AdminFragment");
        }
    }

    private void navigateToHome() {
        if (getView() != null) {
            Navigation.findNavController(getView()).navigate(R.id.action_loginFragment_to_homeFragment);
        } else {
            Log.e(TAG, "getView() returned null when navigating to HomeFragment");
        }
    }

    private void navigateToSignUp() {
        if (getView() != null) {
            Navigation.findNavController(getView()).navigate(R.id.action_loginFragment_to_signupFragment);
        } else {
            Log.e(TAG, "getView() returned null in navigateToSignUp()");
        }
    }
}
