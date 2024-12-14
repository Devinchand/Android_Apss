package com.example.skripsi.Menu;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.skripsi.R;
import com.example.skripsi.UI.CartFragment;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Songsui extends AppCompatActivity {

    private CheckBox dagingCheckBox, hatiCheckBox, ususCheckBox, jantungCheckBox, darahCheckBox, telorCheckBox, sosisCheckBox;
    private EditText quantityEditText, catatanEditText;
    private Button confirmButton;
    private FirebaseFirestore db;

    private static final int BASE_PRICE = 35000;
    private static final int TELOR_PRICE = 6000;
    private static final int SOSIS_PRICE = 12000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_songsui);

        // Initialize UI components
        dagingCheckBox = findViewById(R.id.daging);
        hatiCheckBox = findViewById(R.id.hati);
        ususCheckBox = findViewById(R.id.usus);
        jantungCheckBox = findViewById(R.id.jantung);
        darahCheckBox = findViewById(R.id.darah);
        telorCheckBox = findViewById(R.id.telor);
        sosisCheckBox = findViewById(R.id.sosis);
        quantityEditText = findViewById(R.id.quantityInput);
        catatanEditText = findViewById(R.id.Catatan1);
        confirmButton = findViewById(R.id.btnok);

        // Initialize Firebase Firestore
        db = FirebaseFirestore.getInstance();

        // Set click listener for confirm button
        confirmButton.setOnClickListener(view -> processOrder());
    }

    private void processOrder() {
        // Check network connection
        if (!isNetworkConnected()) {
            Toast.makeText(this, "Koneksi internet tidak tersedia", Toast.LENGTH_SHORT).show();
            return;
        }

        StringBuilder isianBuilder = new StringBuilder();
        if (dagingCheckBox.isChecked()) isianBuilder.append("Daging, ");
        if (hatiCheckBox.isChecked()) isianBuilder.append("Hati, ");
        if (ususCheckBox.isChecked()) isianBuilder.append("Usus, ");
        if (jantungCheckBox.isChecked()) isianBuilder.append("Jantung, ");
        if (darahCheckBox.isChecked()) isianBuilder.append("Darah, ");

        String isian = isianBuilder.length() > 0 ? isianBuilder.substring(0, isianBuilder.length() - 2) : "Tidak ada isian";

        StringBuilder topingBuilder = new StringBuilder();
        int toppingPrice = 0;
        if (telorCheckBox.isChecked()) {
            topingBuilder.append("Telor, ");
            toppingPrice += TELOR_PRICE;
        }
        if (sosisCheckBox.isChecked()) {
            topingBuilder.append("Sosis, ");
            toppingPrice += SOSIS_PRICE;
        }

        String toping = topingBuilder.length() > 0 ? topingBuilder.substring(0, topingBuilder.length() - 2) : "Tidak ada toping";

        int quantity;
        try {
            quantity = Integer.parseInt(quantityEditText.getText().toString());
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Kuantitas tidak valid", Toast.LENGTH_SHORT).show();
            return;
        }

        int totalPrice = (BASE_PRICE + toppingPrice) * quantity;

        String catatan = catatanEditText.getText().toString();
        if (catatan.isEmpty()) {
            catatan = "Tidak ada catatan";
        }

        String orderDetails = quantity + " -- Songsui Besar -- \n" +
                "Isi: " + isian + "\n" +
                "Topping: " + toping + "\n" +
                "Total Harga: Rp. " + totalPrice;
        sendToCart(orderDetails, catatan);
    }

    private void sendToCart(String orderDetails, String catatan) {
        Map<String, Object> cartItem = new HashMap<>();
        cartItem.put("orderDetails", orderDetails);
        cartItem.put("catatan", catatan);

        db.collection("cartItems")
                .add(cartItem)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(Songsui.this, "Pesanan berhasil ditambahkan ke keranjang", Toast.LENGTH_SHORT).show();
                    navigateToCartFragment();
                })
                .addOnFailureListener(e -> {
                    Log.e("FirestoreError", "Gagal menambahkan ke keranjang", e);
                    Toast.makeText(Songsui.this, "Gagal menambahkan ke keranjang", Toast.LENGTH_SHORT).show();
                });
    }

    private void navigateToCartFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.ordersRecyclerView, new CartFragment())
                .addToBackStack(null)
                .commit();
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }
}
