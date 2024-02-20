package com.example.srsapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class OnDutyAdapter extends RecyclerView.Adapter<OnDutyAdapter.ViewHolder>{
    private List<UserOnDuty> userList;
    private OnItemClickListener itemClickListener;
    public interface OnItemClickListener {
        void onItemClick(UserOnDuty user);
    }

    public OnDutyAdapter(List<UserOnDuty> userList, OnItemClickListener itemClickListener){
        this.userList = userList;
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.on_duty_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UserOnDuty user = userList.get(position);
        holder.fullNameTextView.setText(user.getName());
        holder.contactTextView.setText(user.getContact());
        holder.locationTextView.setText(user.getLocation());
        holder.dateTextView.setText(user.getDate());
        holder.shiftTextView.setText(user.getShift());
        if(user.getRemark()){
            holder.remarkTextView.setText("leader");
        } else
            holder.remarkTextView.setText("");

        holder.cancelDutyButton.setOnClickListener(v -> {
            BottomSheetDialogCancelDuty bottomSheet = BottomSheetDialogCancelDuty.newInstance(user);
            bottomSheet.show(((AppCompatActivity) v.getContext()).getSupportFragmentManager(), bottomSheet.getTag());
            holder.additionalInfoLayout.setVisibility(View.GONE);
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView dateTextView, fullNameTextView, contactTextView, locationTextView, shiftTextView, remarkTextView;
        LinearLayout additionalInfoLayout;
        Button cancelDutyButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card_view);
            dateTextView = itemView.findViewById(R.id.text_date);
            fullNameTextView = itemView.findViewById(R.id.text_full_name);
            contactTextView = itemView.findViewById(R.id.text_contact);
            locationTextView = itemView.findViewById(R.id.text_location);
            shiftTextView = itemView.findViewById(R.id.text_shift);
            remarkTextView = itemView.findViewById(R.id.remark);
            additionalInfoLayout = itemView.findViewById(R.id.additional_layout);
            cancelDutyButton = itemView.findViewById(R.id.btnCancelDuty);
        }
    }
}
