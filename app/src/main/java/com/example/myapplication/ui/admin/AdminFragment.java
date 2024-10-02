package com.example.myapplication.ui.admin;

import android.os.Bundle;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Product;
import com.example.myapplication.R;
import com.example.myapplication.ui.products.ProductsAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class AdminFragment extends Fragment {

    private EditText etProductName, etProductPrice;
    private Button btnAddProduct, btnLogout;
    private RecyclerView recyclerView;
    private ProductsAdapter adapter;
    private FirebaseFirestore db;
    private List<Product> productList;
    private FirebaseAuth auth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin, container, false);

        // Initialize UI elements
        etProductName = view.findViewById(R.id.etProductName);
        etProductPrice = view.findViewById(R.id.etProductPrice);
        btnAddProduct = view.findViewById(R.id.btnAddProduct);
        btnLogout = view.findViewById(R.id.btnLogout);
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Initialize Firebase Firestore and FirebaseAuth
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        productList = new ArrayList<>();

        // Initialize adapter with isAdmin = true (admin users can delete)
        adapter = new ProductsAdapter(productList, true); // Pass true for admins
        recyclerView.setAdapter(adapter);

        // Load products into RecyclerView
        loadProducts();

        // Add product logic
        btnAddProduct.setOnClickListener(v -> addProduct());

        // Logout logic
        btnLogout.setOnClickListener(v -> {
            // Sign out the user
            auth.signOut();
            Toast.makeText(getActivity(), "Logged out successfully", Toast.LENGTH_SHORT).show();
            // Navigate back to the LoginFragment (assume you have this logic)
            Navigation.findNavController(view).navigate(R.id.action_adminFragment_to_loginFragment);
        });

        return view;
    }

    // Method to add product to Firestore
    private void addProduct() {
        String productName = etProductName.getText().toString();
        String productPrice = etProductPrice.getText().toString();

        if (!productName.isEmpty() && !productPrice.isEmpty()) {
            try {
                double price = Double.parseDouble(productPrice);

                // Check if a product with the same name already exists
                db.collection("products")
                        .whereEqualTo("name", productName)
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                if (task.getResult().isEmpty()) {
                                    // No product with the same name exists, proceed with adding the product
                                    Product product = new Product(productName, price);
                                    db.collection("products").add(product)
                                            .addOnSuccessListener(documentReference -> {
                                                Toast.makeText(getActivity(), "Product Added", Toast.LENGTH_SHORT).show();
                                                etProductName.setText("");
                                                etProductPrice.setText("");
                                                loadProducts(); // Reload products after adding the product
                                            })
                                            .addOnFailureListener(e -> {
                                                Toast.makeText(getActivity(), "Error adding product: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            });
                                } else {
                                    // Product with the same name exists, show an error
                                    Toast.makeText(getActivity(), "Product with this name already exists!", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(getActivity(), "Error checking for duplicate product", Toast.LENGTH_SHORT).show();
                            }
                        });
            } catch (NumberFormatException e) {
                Toast.makeText(getActivity(), "Invalid price format", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getActivity(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
        }
    }


    // Method to load products from Firestore
    private void loadProducts() {
        productList.clear(); // Clear the list before reloading
        db.collection("products").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String name = document.getString("name");
                    double price = document.getDouble("price");
                    String documentId = document.getId();

                    // Create a Product object with the Firestore document ID
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
