package com.example.skripsi;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.skripsi.UI.AboutFragment;
import com.example.skripsi.UI.CartFragment;
import com.example.skripsi.UI.MenuFragment;
import com.example.skripsi.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        replaceFragment(new MenuFragment());

        binding.navView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.menu) {
                replaceFragment(new MenuFragment());
                return true;
            } else if (itemId == R.id.cart) {
                replaceFragment(new CartFragment());
                return true;
            } else if (itemId == R.id.about) {
                replaceFragment(new AboutFragment());
                return true;
            }
            return false;
        });

    }

    private void replaceFragment(Fragment Fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, Fragment);
        fragmentTransaction.commit();

    }

}
