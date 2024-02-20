package com.example.srsapplication;

import android.content.Intent;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TableAdapter extends RecyclerView.Adapter<TableAdapter.ViewHolder> {
    private List<User> userList;
    private OnItemClickListener itemClickListener;
    public interface OnItemClickListener {
        void onItemClick(User user);
    }

    public TableAdapter(List<User> userList, OnItemClickListener itemClickListener){
        this.userList = userList;
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_schedule_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = userList.get(position);
        holder.fullNameTextView.setText(user.getFullName());
        holder.emailTextView.setText(user.getEmail());
        holder.contactTextView.setText(user.getContact());

        holder.assignDutyButton.setOnClickListener(v -> {
            MyBottomSheetDialogFragment bottomSheet = MyBottomSheetDialogFragment.newInstance(user);
            bottomSheet.show(((AppCompatActivity) v.getContext()).getSupportFragmentManager(), bottomSheet.getTag());
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView fullNameTextView;
        TextView emailTextView;
        TextView contactTextView;
        LinearLayout additionalInfoLayout;
        Button assignDutyButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card_view);
            fullNameTextView = itemView.findViewById(R.id.text_full_name);
            emailTextView = itemView.findViewById(R.id.text_email);
            contactTextView = itemView.findViewById(R.id.text_contact);
            additionalInfoLayout = itemView.findViewById(R.id.additional_layout);
            assignDutyButton = itemView.findViewById(R.id.btnAssignDuty);
        }
    }
}
