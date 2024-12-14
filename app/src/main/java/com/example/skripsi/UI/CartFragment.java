package com.example.skripsi.UI;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.skripsi.Data.CartAdapter;
import com.example.skripsi.Data.CartItem;
import com.example.skripsi.QueueActivity;
import com.example.skripsi.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartFragment extends Fragment {

    private RecyclerView recyclerView;
    private CartAdapter adapter;
    private FirebaseFirestore db;
    private List<CartItem> cartItemList;
    private EditText namaInput;
    private Button btnConfirmOrder;
    private TextView totalHargaTextView;
    private ProgressBar progressBar;

    private DatabaseReference queueRef;
    private DatabaseReference lastOrderNumberRef;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);

        // Initialize views
        namaInput = view.findViewById(R.id.Nama);
        btnConfirmOrder = view.findViewById(R.id.btnConfirmOrder);
        recyclerView = view.findViewById(R.id.ordersRecyclerView);
        totalHargaTextView = view.findViewById(R.id.totalPrice);
        progressBar = view.findViewById(R.id.pbar);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Firebase references
        queueRef = FirebaseDatabase.getInstance().getReference("queue");
        lastOrderNumberRef = FirebaseDatabase.getInstance().getReference("lastOrderNumber");

        // Initialize cart item list and adapter
        cartItemList = new ArrayList<>();
        adapter = new CartAdapter(cartItemList, this::onDeleteItem);
        recyclerView.setAdapter(adapter);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();
        loadCartItems();

        // Button to confirm order
        btnConfirmOrder.setOnClickListener(v -> confirmOrder());

        return view;
    }

    private void confirmOrder() {
        String name = namaInput.getText().toString().trim();

        // Validate name input
        if (name.isEmpty()) {
            Toast.makeText(getContext(), "Masukkan nama terlebih dahulu!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate cart items
        List<String> orders = adapter.getOrders();
        if (orders.isEmpty()) {
            Toast.makeText(getContext(), "Keranjang kosong!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show progress bar
        progressBar.setVisibility(View.VISIBLE);

        // Get last order number and add new order
        lastOrderNumberRef.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                Integer currentNumber = currentData.getValue(Integer.class);
                if (currentNumber == null) {
                    currentData.setValue(1);
                } else {
                    currentData.setValue(currentNumber + 1);
                }
                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                if (committed && currentData != null) {
                    int nomorUrut = currentData.getValue(Integer.class);
                    calculateWaitTimeAndAddOrder(nomorUrut, name, orders);
                } else {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Gagal mendapatkan nomor antrian", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void calculateWaitTimeAndAddOrder(int nomorUrut, String name, List<String> orders) {
        queueRef.get().addOnSuccessListener(snapshot -> {
            int totalWaitTime = 0;
            for (DataSnapshot child : snapshot.getChildren()) {
                Integer estimatedTime = child.child("estimatedTime").getValue(Integer.class);
                if (estimatedTime != null) {
                    totalWaitTime += estimatedTime;
                }
            }

            int currentOrderTime = (orders.size() * 2) + 8; // Estimasi waktu pesanan saat ini
            int finalEstimatedTime = totalWaitTime + currentOrderTime;

            // Add order to queue
            addOrderToQueue(nomorUrut, name, orders, finalEstimatedTime, currentOrderTime);
        }).addOnFailureListener(e -> {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(getContext(), "Gagal menghitung waktu tunggu", Toast.LENGTH_SHORT).show();
        });
    }

    private void addOrderToQueue(int nomorUrut, String name, List<String> orders, int finalEstimatedTime, int currentOrderTime) {
        int totalHarga = calculateTotalPrice();

        Map<String, Object> orderData = new HashMap<>();
        orderData.put("nomorUrut", nomorUrut);
        orderData.put("name", name);
        orderData.put("order", String.join(", ", orders));
        orderData.put("estimatedTime", currentOrderTime);
        orderData.put("totalWaitTime", finalEstimatedTime);
        orderData.put("totalHarga", totalHarga);
        orderData.put("timestamp", System.currentTimeMillis());

        queueRef.push().setValue(orderData).addOnSuccessListener(unused -> {
            progressBar.setVisibility(View.GONE);
            Intent intent = new Intent(getActivity(), QueueActivity.class);
            intent.putExtra("name", name);
            intent.putExtra("nomorUrut", nomorUrut);
            intent.putExtra("totalWaitTime", finalEstimatedTime);
            startActivity(intent);
        }).addOnFailureListener(e -> {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(getContext(), "Gagal menambahkan antrian!", Toast.LENGTH_SHORT).show();
        });
    }

    private void onDeleteItem(int position) {
        CartItem itemToDelete = cartItemList.get(position);

        // Hapus dari database
        db.collection("cartItems").document(itemToDelete.getId())
                .delete()
                .addOnSuccessListener(unused -> {
                    // Hapus dari list lokal
                    cartItemList.remove(position);
                    adapter.notifyItemRemoved(position);
                    adapter.notifyItemRangeChanged(position, cartItemList.size());
                    totalHargaTextView.setText("Total: " + calculateTotalPrice());
                    Toast.makeText(getContext(), "Item dihapus", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Gagal menghapus item", Toast.LENGTH_SHORT).show());
    }

    private void loadCartItems() {
        db.collection("cartItems").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                cartItemList.clear();
                for (DocumentSnapshot doc : task.getResult().getDocuments()) {
                    CartItem item = doc.toObject(CartItem.class);
                    if (item != null) {
                        cartItemList.add(item);
                    }
                }
                adapter.notifyDataSetChanged();
            }
        }).addOnFailureListener(e -> Toast.makeText(getContext(), "Gagal memuat keranjang", Toast.LENGTH_SHORT).show());
    }

    private int calculateTotalPrice() {
        int total = 0;
        for (CartItem item : cartItemList) {
            total += item.getPrice() * item.getQuantity();
        }
        return total;
    }
}
