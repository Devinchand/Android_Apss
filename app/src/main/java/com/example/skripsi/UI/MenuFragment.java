package com.example.skripsi.UI;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.skripsi.Login;
import com.example.skripsi.Menu.BuburA;
import com.example.skripsi.Menu.BuburB2;
import com.example.skripsi.Menu.BuburB2Kecil;
import com.example.skripsi.Menu.BuburI;
import com.example.skripsi.Menu.Kulit;
import com.example.skripsi.Menu.Shabu;
import com.example.skripsi.Menu.Songsui;
import com.example.skripsi.Menu.SongsuiKecil;
import com.example.skripsi.databinding.FragmentMenuBinding;
import com.google.firebase.auth.FirebaseAuth;

public class MenuFragment extends Fragment {

    private FragmentMenuBinding binding;
    ImageButton btn1,btn2,btn3,btn4,btn5,btn6,btn7,btn8;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMenuBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        btn1 = binding.ibtn1;
        btn2 = binding.ibtn2;
        btn3 = binding.ibtn3;
        btn4 = binding.ibtn4;
        btn5 = binding.ibtn5;
        btn6 = binding.ibtn6;
        btn7 = binding.ibnt7;
        btn8 = binding.ibtn8;

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), Songsui.class);
                startActivity(i);
            }
        });
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), BuburB2.class);
                startActivity(i);
            }
        });
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), BuburI.class);
                startActivity(i);
            }
        });
        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), BuburA.class);
                startActivity(i);
            }
        });
        btn5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), Shabu.class);
                startActivity(i);
            }
        });
        btn6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), SongsuiKecil.class);
                startActivity(i);
            }
        });
        btn7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), BuburB2Kecil.class);
                startActivity(i);
            }
        });

        btn8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), Kulit.class);
                startActivity(i);
            }
        });
        return root;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Tambahkan callback untuk menangani tombol Back
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(),
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        showLogoutConfirmationDialog();
                    }
                });
    }

    private void showLogoutConfirmationDialog() {
        new AlertDialog.Builder(requireContext())
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
                        dialog.dismiss();
                    }
                })
                .create()
                .show();
    }

    private void logoutAndNavigateToLogin() {
        // Logout dari Firebase Authentication
        FirebaseAuth.getInstance().signOut();

        // Navigasi ke Login activity
        Intent intent = new Intent(requireActivity(), Login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

        // Tutup activity saat ini
        requireActivity().finish();
    }
}