package com.example.uidesign.adapter;


import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uidesign.R;
import com.example.uidesign.VisitorDetailActivity;
import com.example.uidesign.network.models.AllVisitors;
import java.util.List;

public class VisitorAdapter extends RecyclerView.Adapter<VisitorAdapter.ViewHolder> {
    private List<AllVisitors.Visitor> visitors;
    private Context context;

    public VisitorAdapter(Context context, List<AllVisitors.Visitor> visitors) {
        this.context = context;
        this.visitors = visitors;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_visitor_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AllVisitors.Visitor v = visitors.get(position);
        holder.tvId.setText(String.valueOf(v.getVisitor_id()));
        holder.tvName.setText(v.getFull_name());
        holder.tvStatus.setText(v.getLast_status());

        int color;

        switch (v.getLast_status()) {
            case "Valid":
                color = 0xFF4CAF50;
                break;
            case "Expired":
                color = 0xFFFF5722;
                break;
            case "Pending":
                color = 0xFFFF9800;
                break;
            default:
                color = 0xFF9E9E9E;
                break;

        }

        holder.tvStatus.setTextColor(color);

        holder.card.setOnClickListener(view -> {
            Intent intent = new Intent(context, VisitorDetailActivity.class);
            intent.putExtra("visitor", v);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return visitors.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvId, tvName, tvStatus;
        CardView card;

        public ViewHolder(View itemView) {
            super(itemView);
            card = itemView.findViewById(R.id.card_visitor);
            tvId = itemView.findViewById(R.id.tv_id);
            tvName = itemView.findViewById(R.id.tv_name);
            tvStatus = itemView.findViewById(R.id.tv_status);
        }
    }
}