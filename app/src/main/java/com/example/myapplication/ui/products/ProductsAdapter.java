package com.example.myapplication.ui.products;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Product;
import com.example.myapplication.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.ProductViewHolder> {

    private List<Product> productList;
    private boolean isAdmin; // Flag to check if the user is an admin

    // Constructor to initialize the product list and the isAdmin flag
    public ProductsAdapter(List<Product> productList, boolean isAdmin) {
        this.productList = productList;
        this.isAdmin = isAdmin; // Initialize the isAdmin flag
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the item_product.xml layout for each item
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        // Get the current product from the list
        Product product = productList.get(position);

        // Set the product name and price in the views
        holder.productName.setText(product.getName());
        holder.productPrice.setText(String.valueOf(product.getPrice()));

        // Show or hide the delete button based on isAdmin flag
        if (isAdmin) {
            holder.btnDeleteProduct.setVisibility(View.VISIBLE);
            // Handle delete button click
            holder.btnDeleteProduct.setOnClickListener(v -> {
                // Delete the product from Firestore
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("products").document(product.getDocumentId()) // Use product's document ID
                        .delete()
                        .addOnSuccessListener(aVoid -> {
                            // Remove the product from the list and notify the adapter
                            productList.remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, productList.size());
                            Toast.makeText(holder.itemView.getContext(), "Product deleted", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(holder.itemView.getContext(), "Error deleting product", Toast.LENGTH_SHORT).show();
                        });
            });
        } else {
            // Hide the delete button for non-admin users
            holder.btnDeleteProduct.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    // ViewHolder class to hold the views for each product item
    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView productName, productPrice;
        Button btnDeleteProduct;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            // Initialize the views
            productName = itemView.findViewById(R.id.tvProductName);
            productPrice = itemView.findViewById(R.id.tvProductPrice);
            btnDeleteProduct = itemView.findViewById(R.id.btnDeleteProduct);
        }
    }
}
