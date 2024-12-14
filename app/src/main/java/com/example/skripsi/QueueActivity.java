package com.example.skripsi;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class QueueActivity extends AppCompatActivity {

    private TextView queueNumberTextView, estimatedTimeTextView, userNameTextView;
    private DatabaseReference queueRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_queue);

        // Initialize views
        queueNumberTextView = findViewById(R.id.queueNumber);
        estimatedTimeTextView = findViewById(R.id.estimatedTime);
        userNameTextView = findViewById(R.id.userName);

        // Initialize Firebase reference
        queueRef = FirebaseDatabase.getInstance().getReference("queue");

        // Get extras from intent
        String name = getIntent().getStringExtra("name");
        String order = getIntent().getStringExtra("order");
        int estimatedTime = getIntent().getIntExtra("estimatedTime", 0);

        // Set user name and estimated time
        userNameTextView.setText("Nama: " + name);
        estimatedTimeTextView.setText("Estimasi Waktu: " + estimatedTime + " menit");


        calculateQueueNumberAndWaitTime();
    }

    private void calculateQueueNumberAndWaitTime() {
        DatabaseReference lastOrderNumberRef = FirebaseDatabase.getInstance().getReference("lastOrderNumber");

        lastOrderNumberRef.get().addOnSuccessListener(snapshot -> {
            int queueNumber = snapshot.exists() ? snapshot.getValue(Integer.class) : 0;

            queueRef.get().addOnSuccessListener(queueSnapshot -> {
                int totalWaitTime = 0;
                for (DataSnapshot child : queueSnapshot.getChildren()) {
                    Integer estimatedTime = child.child("estimatedTime").getValue(Integer.class);
                    if (estimatedTime != null) {
                        totalWaitTime += estimatedTime;
                    }
                }

                queueNumberTextView.setText("Nomor Antrian: " + (queueNumber));
                estimatedTimeTextView.setText("Estimasi Waktu Tunggu: " + totalWaitTime + " menit");
            });
        }).addOnFailureListener(e -> {
            queueNumberTextView.setText("Gagal memuat nomor antrian");
            estimatedTimeTextView.setText("Gagal memuat estimasi waktu");
        });
    }


}
