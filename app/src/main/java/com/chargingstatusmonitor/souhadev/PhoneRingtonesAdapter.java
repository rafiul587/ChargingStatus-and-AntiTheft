package com.chargingstatusmonitor.souhadev;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chargingstatusmonitor.souhadev.data.FileEntity;
import com.chargingstatusmonitor.souhadev.databinding.ItemPhoneRingtoneBinding;

import java.util.List;

public class PhoneRingtonesAdapter extends RecyclerView.Adapter<PhoneRingtonesAdapter.ViewHolder> {
    private final List<FileEntity> fileModels;
    OnItemClickAction listener;

    public interface OnItemClickAction {
        void onSetRingtoneClick(int position);
        void onItemClick(int position);
    }

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView setRingtoneIcon, lockIcon, pauseIcon;

        public ViewHolder(ItemPhoneRingtoneBinding binding) {
            super(binding.getRoot());

            setRingtoneIcon = binding.setRingtoneIcon;
            lockIcon = binding.lockIcon;
            pauseIcon = binding.pauseIcon;
            itemView.setOnClickListener(v -> {
                listener.onItemClick(getAdapterPosition());
            });
            setRingtoneIcon.setOnClickListener(v -> {
                listener.onSetRingtoneClick(getAdapterPosition());
            });
        }
    }

    public PhoneRingtonesAdapter(List<FileEntity> fileModels, OnItemClickAction listener) {
        this.fileModels = fileModels;
        this.listener = listener;
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        ItemPhoneRingtoneBinding binding = ItemPhoneRingtoneBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false);
        return new ViewHolder(binding);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int position) {
        FileEntity file = fileModels.get(position);
        if(file.isLocked()){
            viewHolder.setRingtoneIcon.setVisibility(View.GONE);
            viewHolder.lockIcon.setVisibility(View.VISIBLE);
        }else {
            viewHolder.setRingtoneIcon.setVisibility(View.VISIBLE);
            viewHolder.lockIcon.setVisibility(View.GONE);
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return fileModels.size();
    }
}

