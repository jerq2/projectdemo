package com.example.myapplication.ui.products;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Product;
import com.example.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ProductsFragment extends Fragment {

    private RecyclerView recyclerView;
    private ProductsAdapter adapter;
    private FirebaseFirestore db;
    private List<Product> productList;
    private boolean isAdmin = false;
    private FirebaseAuth auth;
    private Button btnBackToHome;

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_products, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        btnBackToHome = view.findViewById(R.id.btnBackToHome);
        btnBackToHome.setOnClickListener(view1 -> Navigation.findNavController(view).navigateUp());

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        productList = new ArrayList<>();

        // Initialize the adapter
        adapter = new ProductsAdapter(productList, isAdmin);
        recyclerView.setAdapter(adapter);

        checkAdminStatus(); // Check if user is admin before loading products

        return view;
    }

    private void checkAdminStatus() {
        String userId = auth.getCurrentUser().getUid();

        // Fetch user document to check if they are an admin
        db.collection("users").document(userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists() && document.getBoolean("isAdmin")) {
                    // User is an admin
                    isAdmin = true;
                }
                // Reload adapter after determining admin status
                adapter = new ProductsAdapter(productList, isAdmin);
                recyclerView.setAdapter(adapter);

                // Load products after determining the admin status
                loadProducts();
            } else {
                Toast.makeText(getActivity(), "Error checking admin status", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadProducts() {
        productList.clear(); // Clear the list before reloading
        db.collection("products").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String name = document.getString("name");
                    double price = document.getDouble("price");
                    String documentId = document.getId(); // Get Firestore document ID

                    // Create a Product object with the name, price, and documentId
                    Product product = new Product(name, price, documentId);
                    productList.add(product);
                }

                // Notify the adapter that the data has changed
                adapter.notifyDataSetChanged();
            } else {
                Toast.makeText(getActivity(), "Error loading products", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
