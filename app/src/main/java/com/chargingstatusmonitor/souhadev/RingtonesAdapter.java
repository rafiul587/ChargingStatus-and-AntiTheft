package com.chargingstatusmonitor.souhadev;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chargingstatusmonitor.souhadev.data.FileEntity;
import com.chargingstatusmonitor.souhadev.databinding.ItemDownloadRingtoneBinding;
import com.chargingstatusmonitor.souhadev.databinding.ItemPhoneRingtoneBinding;
import com.chargingstatusmonitor.souhadev.utils.FileType;

import java.util.List;

public class RingtonesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final List<FileEntity> fileModels;
    OnItemClickAction listener;

    int selectedItem = -1;

    final int VIEW_TYPE_PHONE_RINGTONE = 1;
    final int VIEW_TYPE_DOWNLOAD_RINGTONE = 2;


    public interface OnItemClickAction {
        void onSetRingtoneClick(int position);

        void onDownloadClick(int position);

        void onItemClick(int position);
    }

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    public class DownloadViewHolder extends RecyclerView.ViewHolder {
        ImageView setRingtoneIcon, lockIcon, pauseIcon, downloadIcon;
        TextView name, duration;

        public DownloadViewHolder(ItemDownloadRingtoneBinding binding) {
            super(binding.getRoot());

            setRingtoneIcon = binding.setRingtoneIcon;
            lockIcon = binding.lockIcon;
            pauseIcon = binding.pauseIcon;
            downloadIcon = binding.download;
            name = binding.name;
            duration = binding.duration;
            itemView.setOnClickListener(v -> {
                listener.onItemClick(getAdapterPosition());
            });
            downloadIcon.setOnClickListener(v -> {
                listener.onDownloadClick(getAdapterPosition());
            });
            setRingtoneIcon.setOnClickListener(v -> {
                listener.onSetRingtoneClick(getAdapterPosition());
            });
        }
    }

    public class PhoneViewHolder extends RecyclerView.ViewHolder {
        ImageView setRingtoneIcon, lockIcon, pauseIcon;
        TextView name, duration;

        public PhoneViewHolder(ItemPhoneRingtoneBinding binding) {
            super(binding.getRoot());

            setRingtoneIcon = binding.setRingtoneIcon;
            lockIcon = binding.lockIcon;
            pauseIcon = binding.pauseIcon;
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

    public RingtonesAdapter(List<FileEntity> fileModels, OnItemClickAction listener) {
        this.fileModels = fileModels;
        this.listener = listener;
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        if (viewType == VIEW_TYPE_PHONE_RINGTONE) {
            ItemPhoneRingtoneBinding binding = ItemPhoneRingtoneBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false);
            return new PhoneViewHolder(binding);
        } else {
            ItemDownloadRingtoneBinding binding = ItemDownloadRingtoneBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false);
            return new DownloadViewHolder(binding);
        }

    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int position) {
        FileEntity file = fileModels.get(position);

        if (getItemViewType(position) == VIEW_TYPE_PHONE_RINGTONE) {

            PhoneViewHolder holder = (PhoneViewHolder) viewHolder;

            if (selectedItem == position) {
                holder.pauseIcon.setVisibility(View.VISIBLE);
            } else {
                holder.pauseIcon.setVisibility(View.GONE);
            }
            holder.name.setText(file.getName());
            holder.duration.setText(file.getDuration());
            if (file.isLocked()) {
                holder.setRingtoneIcon.setVisibility(View.GONE);
                holder.lockIcon.setVisibility(View.VISIBLE);
            } else {
                holder.setRingtoneIcon.setVisibility(View.VISIBLE);
                holder.lockIcon.setVisibility(View.GONE);
            }
        } else {
            DownloadViewHolder holder = (DownloadViewHolder) viewHolder;
            if (selectedItem == position) {
                holder.pauseIcon.setVisibility(View.VISIBLE);
            } else {
                holder.pauseIcon.setVisibility(View.GONE);
            }
            holder.name.setText(file.getName());
            holder.duration.setText(file.getDuration());
            if (file.isLocked()) {
                holder.setRingtoneIcon.setVisibility(View.GONE);
                holder.downloadIcon.setVisibility(View.GONE);
                holder.lockIcon.setVisibility(View.VISIBLE);
            } else {
                holder.setRingtoneIcon.setVisibility(View.VISIBLE);
                holder.downloadIcon.setVisibility(View.VISIBLE);
                holder.lockIcon.setVisibility(View.GONE);
            }
        }
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

    public int getSelectedItem(){
        return selectedItem;
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return fileModels.size();
    }


    @Override
    public int getItemViewType(int position) {
        return fileModels.get(position).getType().equals(FileType.ASSET) ? VIEW_TYPE_DOWNLOAD_RINGTONE : VIEW_TYPE_PHONE_RINGTONE;
    }
}

