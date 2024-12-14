package com.example.skripsi.Admin;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.skripsi.Data.AdminAdapter;
import com.example.skripsi.Data.Order;
import com.example.skripsi.Login;
import com.example.skripsi.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class Admin extends AppCompatActivity {

    private RecyclerView adminRecyclerView;
    private DatabaseReference queueRef;
    private AdminAdapter adminAdapter;
    private List<Order> queueList;
    private Button resetButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        resetButton = findViewById(R.id.btnReset);

        // Inisialisasi RecyclerView
        adminRecyclerView = findViewById(R.id.adminRecyclerView);
        adminRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Inisialisasi daftar antrian dan adapter
        queueList = new ArrayList<>();
        adminAdapter = new AdminAdapter(queueList);
        adminRecyclerView.setAdapter(adminAdapter);

        // Referensi ke Firebase
        queueRef = FirebaseDatabase.getInstance().getReference("queue");

        // Muat data real-time
        loadQueueDataRealtime();

        // Tombol reset
        resetButton.setOnClickListener(v -> resetAllOrders());

        // Atur padding untuk sistem insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Tambahkan callback untuk tombol Back
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                showLogoutConfirmationDialog();
            }
        });
    }

    private void resetAllOrders() {
        DatabaseReference lastOrderNumberRef = FirebaseDatabase.getInstance().getReference("lastOrderNumber");

        new AlertDialog.Builder(this)
                .setTitle("Konfirmasi Reset")
                .setMessage("Apakah Anda yakin ingin mereset semua pesanan dan nomor antrian?")
                .setPositiveButton("Ya", (dialog, which) -> {
                    // Reset queue dan nomor urut
                    queueRef.removeValue().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Reset nomor urut terakhir
                            lastOrderNumberRef.setValue(0).addOnCompleteListener(resetTask -> {
                                if (resetTask.isSuccessful()) {
                                    queueList.clear();
                                    adminAdapter.notifyDataSetChanged();
                                    Toast.makeText(this, "Pesanan dan nomor antrian berhasil direset", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(this, "Gagal mereset nomor antrian", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            Toast.makeText(this, "Gagal mereset pesanan", Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Tidak", (dialog, which) -> dialog.dismiss())
                .show();
    }


    private void loadQueueDataRealtime() {
        queueRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                int nomorUrut = 0;
                try {
                    Object nomorUrutObj = snapshot.child("nomorUrut").getValue();
                    if (nomorUrutObj instanceof Long) {
                        nomorUrut = ((Long) nomorUrutObj).intValue();
                    } else if (nomorUrutObj instanceof String) {
                        nomorUrut = Integer.parseInt((String) nomorUrutObj);
                    }
                } catch (NumberFormatException e) {
                    nomorUrut = 0; // Default if parsing fails
                }
                // Data baru ditambahkan
                String name = snapshot.child("name").getValue(String.class);
                String order = snapshot.child("order").getValue(String.class);
                Integer estimatedTime = snapshot.child("estimatedTime").getValue(Integer.class);

                if (name != null && order != null && estimatedTime != null) {
                    queueList.add(new Order(nomorUrut, name, order, estimatedTime));
                    adminAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                // Implementasikan logika jika data berubah (opsional)
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                int nomorUrut = 0;
                try {
                    Object nomorUrutObj = snapshot.child("nomorUrut").getValue();
                    if (nomorUrutObj instanceof Long) {
                        nomorUrut = ((Long) nomorUrutObj).intValue();
                    } else if (nomorUrutObj instanceof String) {
                        nomorUrut = Integer.parseInt((String) nomorUrutObj);
                    }
                } catch (NumberFormatException e) {
                    nomorUrut = 0;
                }
                // Data dihapus
                String name = snapshot.child("name").getValue(String.class);
                String order = snapshot.child("order").getValue(String.class);
                Integer estimatedTime = snapshot.child("estimatedTime").getValue(Integer.class);

                if ( name != null && order != null && estimatedTime != null) {
                    Order removedOrder = new Order(nomorUrut, name, order, estimatedTime);
                    queueList.remove(removedOrder);
                    adminAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                // Implementasikan logika jika data dipindahkan (opsional)
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Tangani error
            }
        });
    }

    private void showLogoutConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Konfirmasi Logout")
                .setMessage("Apakah Anda yakin ingin keluar?")
                .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        logoutAndNavigateToLogin();
                    }
                })
                .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss(); // Tutup dialog tanpa tindakan
                    }
                })
                .create()
                .show();
    }

    private void logoutAndNavigateToLogin() {
        // Logout dari Firebase Authentication
        FirebaseAuth.getInstance().signOut();

        // Navigasi ke Login activity
        Intent intent = new Intent(Admin.this, Login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

        // Tutup activity saat ini
        finish();
    }
}
