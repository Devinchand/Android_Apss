package com.example.skripsi.Data;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.skripsi.R;

import java.util.List;

public class AdminAdapter extends RecyclerView.Adapter<AdminAdapter.ViewHolder> {

    private List<Order> queueList;

    public AdminAdapter(List<Order> queueList) {
        this.queueList = queueList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Order order = queueList.get(position);
        holder.nomorUrut.setText("Nomor Urut: " + order.getNomorUrut());
        holder.name.setText("Nama: " + order.getName());
        holder.details.setText("Pesanan: " + order.getOrder());
        holder.estimatedTime.setText("Estimasi Waktu: " + order.getEstimatedTime() + " menit");
    }

    @Override
    public int getItemCount() {
        return queueList.size();
    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nomorUrut, name, details, estimatedTime;

        ViewHolder(View itemView) {
            super(itemView);
            nomorUrut = itemView.findViewById(R.id.nomorUrut);
            name = itemView.findViewById(R.id.orderName);
            details = itemView.findViewById(R.id.orderDetails);
            estimatedTime = itemView.findViewById(R.id.orderEstimatedTime);
        }
    }
}
