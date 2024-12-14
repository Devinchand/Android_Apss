package com.example.skripsi.Data;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.skripsi.R;

import java.util.ArrayList;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    private final List<CartItem> cartItemList;
    private final OnDeleteItemListener deleteItemListener;

    public CartAdapter(List<CartItem> cartItemList, OnDeleteItemListener deleteItemListener) {
        this.cartItemList = cartItemList != null ? cartItemList : new ArrayList<>();
        this.deleteItemListener = deleteItemListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CartItem item = cartItemList.get(position);
        holder.orderDetails.setText(item.getOrderDetails());
        holder.catatan.setText(item.getCatatan());
        holder.btnDelete.setOnClickListener(v -> deleteItemListener.onDeleteItem(position));
    }

    @Override
    public int getItemCount() {
        return cartItemList.size();
    }

    public List<String> getOrders() {
        List<String> orders = new ArrayList<>();
        for (CartItem item : cartItemList) {
            orders.add(item.getOrderDetails());
        }
        return orders;
    }
    public interface OnDeleteItemListener {
        void onDeleteItem(int position);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView orderDetails, catatan;
        Button btnDelete;

        ViewHolder(View itemView) {
            super(itemView);
            orderDetails = itemView.findViewById(R.id.orderDetailsTextView);
            catatan = itemView.findViewById(R.id.catatanTextView);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
