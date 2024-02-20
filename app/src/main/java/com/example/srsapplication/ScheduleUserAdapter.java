package com.example.srsapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ScheduleUserAdapter extends RecyclerView.Adapter<ScheduleUserAdapter.ViewHolder>{
    private List<UserOnDuty> userList;

    public ScheduleUserAdapter(List<UserOnDuty> userList){
        this.userList = userList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.on_duty_item_user, parent, false);
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
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView dateTextView, fullNameTextView, contactTextView, locationTextView, shiftTextView, remarkTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card_view);
            dateTextView = itemView.findViewById(R.id.text_date);
            fullNameTextView = itemView.findViewById(R.id.text_full_name);
            contactTextView = itemView.findViewById(R.id.text_contact);
            locationTextView = itemView.findViewById(R.id.text_location);
            shiftTextView = itemView.findViewById(R.id.text_shift);
            remarkTextView = itemView.findViewById(R.id.remark);
        }
    }
}
