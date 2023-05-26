package com.chargingstatusmonitor.souhadev.ui.charginganimation;

import static com.chargingstatusmonitor.souhadev.ui.sounds.MyRecordsFragment.find;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.chargingstatusmonitor.souhadev.MyApplication;
import com.chargingstatusmonitor.souhadev.R;
import com.chargingstatusmonitor.souhadev.data.local.AppDataStore;
import com.chargingstatusmonitor.souhadev.data.remote.DownloadFileState;
import com.chargingstatusmonitor.souhadev.data.remote.RetrofitClient;
import com.chargingstatusmonitor.souhadev.databinding.FragmentChargingAnimationBinding;
import com.chargingstatusmonitor.souhadev.model.AnimationModel;
import com.chargingstatusmonitor.souhadev.model.ApiResponse;
import com.chargingstatusmonitor.souhadev.utils.AppExecutors;
import com.chargingstatusmonitor.souhadev.utils.Constants;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AnimationListFragment extends Fragment implements AnimationListAdapter.OnAnimationClickListener {

    private FragmentChargingAnimationBinding binding;
    CompositeDisposable disposable;

    AnimationListAdapter adapter;
    List<AnimationModel> animationList;

    List<StorageReference> animations;
    Call<ApiResponse> call;
    AppDataStore dataStore;

    public AnimationListFragment() {
    }

    String selectedAnimation = "";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentChargingAnimationBinding.inflate(inflater, container, false);
        dataStore = ((MyApplication) requireContext().getApplicationContext()).getDataStore();
        disposable = new CompositeDisposable();
        animationList = new ArrayList<>();
        animations = new ArrayList<>();
        setAnimationRecyclerView();
        binding.progressBar.setVisibility(View.VISIBLE);
        selectedAnimation = dataStore.getStringValue(AppDataStore.SELECTED_ANIMATION_NAME).blockingFirst();
        if (selectedAnimation.isEmpty() || selectedAnimation.equals(Constants.DEFAULT_ANIMATION)) {
            adapter.setDefaultSelected(true);
        }
        showAnimationList();
        return binding.getRoot();
    }

    private void setAnimationRecyclerView() {
        adapter = new AnimationListAdapter(animationList, this);
        binding.recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 3));
        binding.recyclerView.setAdapter(adapter);
    }

    private void showAnimationList() {
        Observable<List<String>> listObservable = Observable.create(this::getAllAnimations);

        Observable<List<String>> listObservable2 = Observable.create(emitter -> {
            getDownloadedAnimation(requireContext(), emitter);
        });

        disposable.add(Observable.zip(listObservable, listObservable2, (animationList, downloadedAnimationList) -> {
            // combine the two lists here and return the result
            List<AnimationModel> result = new ArrayList<>();
            for (String animation : animationList) {
                if (downloadedAnimationList.contains(animation)) {
                    if (!adapter.isDefaultSelected()) {
                        result.add(new AnimationModel(animation, selectedAnimation.equals(animation), 100));
                    } else result.add(new AnimationModel(animation, false, 100));
                } else result.add(new AnimationModel(animation, false, -1));
            }
            return result;
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(result -> {
            animationList.clear();
            animationList.addAll(result);
            binding.progressBar.setVisibility(View.GONE);
            adapter.notifyDataSetChanged();
        }, error -> binding.progressBar.setVisibility(View.GONE)));
    }

    public void getAllAnimations(@NonNull ObservableEmitter<List<String>> emitter) {
        call = RetrofitClient.getInstance().getArchiveApi().getArchiveDetails(Constants.IDENTIFIER);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse> call, @NonNull Response<ApiResponse> response) {
                if (response.isSuccessful()) {
                    try {
                        List<String> animations = response.body().mapToFiles();

                        emitter.onNext(animations);
                        emitter.onComplete();
                    } catch (Exception e) {
                        if (binding != null) binding.progressBar.setVisibility(View.GONE);
                        //emitter.onError(e);
                        Log.d("TAG", "onResponse: " + e.getLocalizedMessage());
                    }
                } else {
                    if (binding != null) binding.progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse> call, Throwable t) {
                // Handle error
                // ...
                if (binding != null) binding.progressBar.setVisibility(View.GONE);
                //emitter.onError(t);
            }
        });
    }

    public void downloadAnimation(int position) {

        AnimationModel animation = animationList.get(position);
        notifyItemChange(animation.getName(), position, 0);
        File destinationDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), requireContext().getString(R.string.download_folder_name));

        if (!destinationDir.exists()) {
            destinationDir.mkdirs();
        }
        notifyItemChange(animation.getName(), position, 0);
        disposable.add(RetrofitClient.getInstance().downloadFile(Constants.IDENTIFIER, destinationDir, animation.getName()).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(state -> {
            if (state instanceof DownloadFileState.Progress) {
                notifyItemChange(animation.getName(), position, ((DownloadFileState.Progress) state).getPercent());
            } else if (state instanceof DownloadFileState.Finished) {
                notifyItemChange(animation.getName(), position, 100);
            }

        }, throwable -> notifyItemChange(animation.getName(), position, -1)));
    }

    public void notifyItemChange(String name, int position, int progress) {
        animationList.set(position, new AnimationModel(name, false, progress));
        adapter.notifyItemChanged(position + 1);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (call != null) call.cancel();
        disposable.clear();
        binding = null;
    }

    @Override
    public void onItemClick(int position) {
        if (position == 0) {
            Bundle bundle = new Bundle();
            bundle.putString(Constants.KEY_NAME, Constants.DEFAULT_ANIMATION);
            NavHostFragment.findNavController(this).navigate(R.id.action_navigation_animation_list_to_navigation_apply_animation, bundle);
        } else {
            AnimationModel animation = animationList.get(position - 1);
            if (animation.getProgress() == 100) {
                Bundle bundle = new Bundle();
                bundle.putString(Constants.KEY_NAME, animation.getName());
                NavHostFragment.findNavController(this).navigate(R.id.action_navigation_animation_list_to_navigation_apply_animation, bundle);
            } else {
                downloadAnimation(position - 1);
            }
        }
    }

    public void getDownloadedAnimation(Context context, @NonNull ObservableEmitter<List<String>> emitter) {
        List<String> recordingList = new ArrayList<>();
        AppExecutors.getInstance().networkIO().execute(() -> {
            Uri collection = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q ? MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL) : MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            String[] projection = {MediaStore.Images.Media.DISPLAY_NAME, MediaStore.Images.Media.DATA, MediaStore.Images.Media.MIME_TYPE};
            String selection = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q ? MediaStore.Images.Media.RELATIVE_PATH + " LIKE ? " : MediaStore.Images.Media.DATA + " LIKE ? ";
            String selection2 = "AND " + MediaStore.Images.Media.MIME_TYPE + " = ?";
            String[] selectionArgs = {"%" + context.getString(R.string.download_folder_name) + "%", "image/gif"};
            String sortOrder = MediaStore.Images.Media.DISPLAY_NAME + " DESC ";
            try (Cursor cursor = context.getContentResolver().query(collection, projection, selection + selection2, selectionArgs, sortOrder)) {
                if (cursor != null) {
                    int nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);
                    while (cursor.moveToNext()) {
                        String name = cursor.getString(nameColumn);
                        recordingList.add(name);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
                String folderName = context.getString(R.string.download_folder_name);
                File folder = new File(Environment.getExternalStorageDirectory(), folderName);
                if (folder.exists() && folder.isDirectory()) {
                    File[] files = folder.listFiles((dir, name) -> name.toLowerCase(Locale.getDefault()).endsWith(".gif"));
                    if (files != null) {
                        for (File file : files) {
                            String name = file.getName();
                            if (find(recordingList, n -> n.equals(name)) == null) {
                                recordingList.add(name);
                            }
                        }
                    }
                }
            }
            emitter.onNext(recordingList);
            emitter.onComplete();
        });

    }
}