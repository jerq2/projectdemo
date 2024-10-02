package com.example.myapplication;

public class Product {
    private String name;
    private double price;
    private String documentId; // Firestore document ID

    // Constructor with all parameters
    public Product(String name, double price, String documentId) {
        this.name = name;
        this.price = price;
        this.documentId = documentId;
    }

    // Overloaded constructor with only name and price
    public Product(String name, double price) {
        this.name = name;
        this.price = price;
    }

    // Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }
}
