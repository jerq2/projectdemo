package com.example.myapplication.ui.signup;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignupFragment extends Fragment {

    private EditText etEmail, etPassword, etUsername, etAdminCode;
    private Button btnSignUp;
    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signup, container, false);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        etEmail = view.findViewById(com.example.myapplication.R.id.etEmail);
        etPassword = view.findViewById(R.id.etPassword);
        etUsername = view.findViewById(R.id.etUsername);
        etAdminCode = view.findViewById(R.id.etAdminCode);
        btnSignUp = view.findViewById(R.id.btnSignUp);


        btnSignUp.setOnClickListener(view1 -> signUpUser());

        return view;
    }

    private void signUpUser() {
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();
        String username = etUsername.getText().toString();
        String adminCode = etAdminCode.getText().toString();
        boolean isAdmin;

        if (adminCode.equals("Admin123")) isAdmin = true;
        else {
            isAdmin = false;
        }


        if (!email.isEmpty() && !password.isEmpty() && !username.isEmpty()) {
            if (password.length() < 6) {
                Toast.makeText(getActivity(), "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                return;
            }

            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    FirebaseUser user = auth.getCurrentUser();
                    if (user != null) {
                        // Save user info in Firestore
                        Map<String, Object> userInfo = new HashMap<>();
                        userInfo.put("username", username);
                        userInfo.put("email", email);
                        userInfo.put("isAdmin", isAdmin);

                        db.collection("users").document(user.getUid()).set(userInfo)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(getActivity(), "Sign Up Successful", Toast.LENGTH_SHORT).show();
                                    // Navigate back to LoginFragment after successful signup
                                    Navigation.findNavController(getView()).navigate(R.id.action_signupFragment_to_loginFragment);
                                })
                                .addOnFailureListener(e -> Toast.makeText(getActivity(), "Error saving user info", Toast.LENGTH_SHORT).show());

                    }
                } else {
                    Toast.makeText(getActivity(), "Sign Up Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(getActivity(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
        }
    }
}
