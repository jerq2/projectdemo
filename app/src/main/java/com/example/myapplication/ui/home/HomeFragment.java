package com.example.myapplication.ui.home;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;

public class HomeFragment extends Fragment {
    private Button btnLogout, btnGoToProducts;
    private FirebaseAuth auth;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout and assign it to a variable
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance();

        // Initialize the Logout button
        btnLogout = view.findViewById(R.id.btnLogout);
        btnGoToProducts = view.findViewById(R.id.btnGoToProducts);

        // Set click listener for logout
        btnLogout.setOnClickListener(v -> {
            // Sign out from Firebase
            auth.signOut();

            // Navigate back to the LoginFragment
            Navigation.findNavController(view).navigate(R.id.action_homeFragment_to_loginFragment);
        });

        btnGoToProducts.setOnClickListener(view1 -> {

            Navigation.findNavController(view).navigate(R.id.action_homeFragment_to_productsFragment);
        });

        // Return the inflated view
        return view;
    }
}
