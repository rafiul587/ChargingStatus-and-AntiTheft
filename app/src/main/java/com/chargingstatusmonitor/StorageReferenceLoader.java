package com.chargingstatusmonitor;

import android.net.Uri;

import androidx.annotation.NonNull;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.signature.ObjectKey;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class StorageReferenceLoader<Data> implements ModelLoader<StorageReference, Data> {

    private final FirebaseStorage storage;

    public StorageReferenceLoader(FirebaseStorage storage) {
        this.storage = storage;
    }

    @Override
    public ModelLoader.LoadData<Data> buildLoadData(StorageReference reference, int width, int height, Options options) {
        return new LoadData<>(new ObjectKey(reference.getPath()), new FirebaseStorageFetcher<>(storage, reference));
    }

    @Override
    public boolean handles(@NonNull StorageReference reference) {
        return true;
    }

    private static class FirebaseStorageFetcher<Data> implements DataFetcher<Data> {

        private final FirebaseStorage storage;
        private final StorageReference reference;

        FirebaseStorageFetcher(FirebaseStorage storage, StorageReference reference) {
            this.storage = storage;
            this.reference = reference;
        }

        @Override
        public void loadData(Priority priority, DataCallback<? super Data> callback) {
            reference.getDownloadUrl().addOnSuccessListener(uri -> {
                callback.onDataReady((Data) uri);
            }).addOnFailureListener(callback::onLoadFailed);
        }

        @Override
        public void cleanup() {}

        @Override
        public void cancel() {}

        @Override
        public Class<Data> getDataClass() {
            return (Class<Data>) Uri.class;
        }

        @Override
        public DataSource getDataSource() {
            return DataSource.REMOTE;
        }
    }
}

