package com.example.skripsi.Data;

public class CartItem {
    private String id;
    private String orderDetails;
    private String catatan;
    private int price;
    private int quantity;

    public CartItem() {
        // Required empty constructor for Firestore
    }

    public CartItem(String orderDetails, String catatan, int price, int quantity) {
        this.orderDetails = orderDetails;
        this.catatan = catatan;
        this.price = price;
        this.quantity = quantity;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrderDetails() {
        return orderDetails;
    }

    public void setOrderDetails(String orderDetails) {
        this.orderDetails = orderDetails;
    }

    public String getCatatan() {
        return catatan;
    }

    public void setCatatan(String catatan) {
        this.catatan = catatan;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
