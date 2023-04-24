package com.chargingstatusmonitor.souhadev;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chargingstatusmonitor.souhadev.data.FileEntity;
import com.chargingstatusmonitor.souhadev.databinding.ItemMyRecordBinding;
import com.chargingstatusmonitor.souhadev.databinding.ItemPhoneRingtoneBinding;
import com.chargingstatusmonitor.souhadev.model.FileModel;

import java.util.List;

public class MyRecordsAdapter extends RecyclerView.Adapter<MyRecordsAdapter.ViewHolder> {
    private final List<FileModel> fileModels;
    OnItemClickAction listener;

    int selectedItem = -1;

    public interface OnItemClickAction {
        void onItemClick(int position);

        void onSetRingtoneClick(int position);

    }

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView pauseIcon, setRingtoneIcon;
        TextView name, duration;

        public ViewHolder(ItemMyRecordBinding binding) {
            super(binding.getRoot());
            pauseIcon = binding.pauseIcon;
            setRingtoneIcon = binding.setRingtoneIcon;
            name = binding.name;
            duration = binding.duration;
            itemView.setOnClickListener(v -> {
                listener.onItemClick(getAdapterPosition());
            });
            setRingtoneIcon.setOnClickListener(v -> {
                listener.onSetRingtoneClick(getAdapterPosition());
            });
        }
    }

    public MyRecordsAdapter(List<FileModel> fileModels, OnItemClickAction listener) {
        this.fileModels = fileModels;
        this.listener = listener;
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        ItemMyRecordBinding binding = ItemMyRecordBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false);
        return new ViewHolder(binding);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        FileModel file = fileModels.get(position);
        if (selectedItem == position) {
            holder.pauseIcon.setVisibility(View.VISIBLE);
        } else {
            holder.pauseIcon.setVisibility(View.GONE);
        }
        holder.name.setText(file.getName());
        holder.duration.setText(file.getDuration());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return fileModels.size();
    }

    public void setSelectedItem(int selectedItem) {
        if (selectedItem == this.selectedItem) {
            return;
        }
        int prevSelectedItem = this.selectedItem;
        this.selectedItem = selectedItem;
        if (selectedItem == -1) {
            notifyItemChanged(prevSelectedItem);
        } else {
            if (prevSelectedItem >= 0) notifyItemChanged(prevSelectedItem);
            notifyItemChanged(selectedItem);
        }

    }

    public int getSelectedItem() {
        return selectedItem;
    }
}

