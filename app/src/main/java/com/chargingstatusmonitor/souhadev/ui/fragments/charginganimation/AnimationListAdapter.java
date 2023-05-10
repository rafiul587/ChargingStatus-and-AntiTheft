package com.chargingstatusmonitor.souhadev.ui.fragments.charginganimation;


import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.chargingstatusmonitor.souhadev.R;
import com.chargingstatusmonitor.souhadev.databinding.ItemAnimationBinding;
import com.chargingstatusmonitor.souhadev.model.AnimationModel;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class AnimationListAdapter extends RecyclerView.Adapter<AnimationListAdapter.AnimationListViewHolder> {

    private List<AnimationModel> animationList;

    interface OnAnimationClickListener {
        void onItemClick(int position);
    }

    public OnAnimationClickListener listener;

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    public class AnimationListViewHolder extends RecyclerView.ViewHolder {
        final ImageView animationImageView, download;
        CircularProgressIndicator downloadProgress;

        public AnimationListViewHolder(ItemAnimationBinding binding) {
            super(binding.getRoot());
            // Define click listener for the ViewHolder's View

            animationImageView = binding.gifImageView;
            download = binding.download;
            downloadProgress = binding.downloadProgress;
            itemView.setOnClickListener(v -> {
                listener.onItemClick(getAdapterPosition());
            });
        }
    }

    public AnimationListAdapter(List<AnimationModel> dataSet, OnAnimationClickListener listener) {
        animationList = dataSet;
        this.listener = listener;
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public AnimationListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        ItemAnimationBinding binding = ItemAnimationBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false);

        return new AnimationListViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AnimationListViewHolder viewHolder, final int position) {
        if (position == 0) {
            Glide.with(viewHolder.itemView.getContext())
                    .asGif()
                    .load("file:///android_asset/animations/default.gif")
                    .into(viewHolder.animationImageView);
            viewHolder.download.setVisibility(View.GONE);
            return;
        }
        AnimationModel animation = animationList.get(position - 1);
        if(animation.getProgress() == -1 || animation.getProgress() == 100){
            viewHolder.downloadProgress.hide();
        }else if(animation.getProgress()>=0){
            viewHolder.downloadProgress.show();
            viewHolder.downloadProgress.setProgress(animation.getProgress());
        }
        viewHolder.downloadProgress.setProgress(animation.getProgress());
        if (animation.getProgress() == 100f) {
            viewHolder.download.setVisibility(View.GONE);
        } else {
            viewHolder.download.setVisibility(View.VISIBLE);
        }

        Glide.with(viewHolder.itemView.getContext())
                .load(animation.getAnimation())
                .placeholder(R.drawable.ic_loading)
                .into(viewHolder.animationImageView);
    }

    @Override
    public int getItemCount() {
        return animationList.size() + 1;
    }
}

