package com.chargingstatusmonitor.souhadev.ui.fragments.charginganimation;

import static com.chargingstatusmonitor.souhadev.ui.fragments.MyRecordsFragment.find;
import static com.chargingstatusmonitor.souhadev.utils.FileUtils.getDuration;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.chargingstatusmonitor.souhadev.AppExecutors;
import com.chargingstatusmonitor.souhadev.R;
import com.chargingstatusmonitor.souhadev.databinding.FragmentChargingAnimationBinding;
import com.chargingstatusmonitor.souhadev.model.AnimationModel;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.OnProgressListener;
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

public class AnimationListFragment extends Fragment implements AnimationListAdapter.OnAnimationClickListener {

    private FragmentChargingAnimationBinding binding;
    CompositeDisposable disposable;

    StorageReference listRef;

    AnimationListAdapter adapter;
    List<AnimationModel> animationList;

    List<StorageReference> animations;

    public AnimationListFragment() {

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentChargingAnimationBinding.inflate(inflater, container, false);
        disposable = new CompositeDisposable();
        animationList = new ArrayList<>();
        animations = new ArrayList<>();
        listRef = FirebaseStorage.getInstance().getReference().child("animations");
        adapter = new AnimationListAdapter(animationList, this);
        binding.recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 3));
        binding.recyclerView.setAdapter(adapter);

        Observable<List<StorageReference>> listObservable = Observable.create(emitter -> {
            listAllPaginated(null, emitter);
        });

        Observable<List<String>> listObservable2 = Observable.create(emitter -> {
            getDownloadedAnimation(requireContext(), emitter);
        });
        binding.progressBar.setVisibility(View.VISIBLE);
        disposable.add(Observable.zip(listObservable, listObservable2, (animationList, downloadedAnimationList) -> {
                    // combine the two lists here and return the result
                    List<AnimationModel> result = new ArrayList<>();
                    for(StorageReference animation: animationList){
                        if(downloadedAnimationList.contains(animation.getName())) {
                            result.add(new AnimationModel(animation, 100));
                        }else result.add(new AnimationModel(animation, -1));
                    }
                    return result;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    animationList.clear();
                    animationList.addAll(result);
                    binding.progressBar.setVisibility(View.GONE);
                    Log.d("TAG", "onCreateView: " +result.toString());
                    adapter.notifyDataSetChanged();
                }, error -> {
                    binding.progressBar.setVisibility(View.GONE);
                }));
        return binding.getRoot();
    }

    public void listAllPaginated(@Nullable String pageToken, @NonNull ObservableEmitter<List<StorageReference>> emitter) {

        Task<ListResult> listPageTask = pageToken != null
                ? listRef.list(100, pageToken)
                : listRef.list(100);

        listPageTask
                .addOnSuccessListener(listResult -> {
                    List<StorageReference> items = listResult.getItems();
                    animations.addAll(items);
                    // Recurse onto next page
                    if (listResult.getPageToken() != null) {
                        listAllPaginated(listResult.getPageToken(), emitter);
                    }
                    emitter.onNext(animations);
                    emitter.onComplete();
                }).addOnFailureListener(e -> {
                    // Uh-oh, an error occurred.
                });
    }

    public void downloadAnimation(int position) {

        StorageReference storageRef = animationList.get(position).getAnimation();

        File storagePath = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), requireContext().getString(R.string.download_folder_name));

        if (!storagePath.exists()) {
            storagePath.mkdirs();
        }

        final File myFile = new File(storagePath, storageRef.getName());
        animationList.set(position, new AnimationModel(storageRef, 0));
        adapter.notifyItemChanged(position+1);
        storageRef.getFile(myFile).addOnSuccessListener(taskSnapshot -> {
            animationList.set(position, new AnimationModel(storageRef, 100));
            adapter.notifyItemChanged(position+1);
        }).addOnFailureListener(exception -> {
            animationList.set(position, new AnimationModel(storageRef, -1));
            adapter.notifyItemChanged(position+1);
        }).addOnProgressListener(taskSnapshot -> {
            //calculating progress percentage
            float progress = (100f * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
            animationList.set(position, new AnimationModel(storageRef, (int) progress));
            adapter.notifyItemChanged(position+1);
        });;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        disposable.dispose();
        binding = null;
    }

    @Override
    public void onItemClick(int position) {
        if(position == 0){
            Bundle bundle = new Bundle();
            bundle.putString("name", "default.gif");
            NavHostFragment.findNavController(this).navigate(R.id.action_navigation_animation_list_to_navigation_apply_animation, bundle);
        }else {
            AnimationModel animation = animationList.get(position-1);
            if (animation.getProgress() == 100) {
                Bundle bundle = new Bundle();
                bundle.putString("name", animation.getAnimation().getName());
                NavHostFragment.findNavController(this).navigate(R.id.action_navigation_animation_list_to_navigation_apply_animation, bundle);
            } else {
                downloadAnimation(position - 1);
            }
        }

    }

    public void getDownloadedAnimation(Context context, @NonNull ObservableEmitter<List<String>> emitter) {
        List<String> recordingList = new ArrayList<>();
        AppExecutors.getInstance().networkIO().execute(() -> {
            Uri collection = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q ?
                    MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL) :
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            String[] projection = {
                    MediaStore.Images.Media.DISPLAY_NAME,
                    MediaStore.Images.Media.DATA,
                    MediaStore.Images.Media.MIME_TYPE
            };
            String selection = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q ?
                    MediaStore.Images.Media.RELATIVE_PATH + " LIKE ? " :
                    MediaStore.Images.Media.DATA + " LIKE ? ";
            String selection2 = "AND " + MediaStore.Images.Media.MIME_TYPE + " = ?";
            String[] selectionArgs = {
                    "%" + context.getString(R.string.download_folder_name) + "%", "image/gif"};
            String sortOrder = MediaStore.Images.Media.DISPLAY_NAME + " DESC ";
            try (Cursor cursor = context.getContentResolver().query(
                    collection,
                    projection,
                    selection + selection2,
                    selectionArgs,
                    sortOrder
            )) {
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
                    File[] files = folder.listFiles((dir, name) ->
                            name.toLowerCase(Locale.getDefault()).endsWith(".gif")
                    );
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
            Log.d("TAG", "getMyRecords: " + +recordingList.size());
            AppExecutors.getInstance().mainThread().execute(() -> {
                Log.d("TAG", "getDownloadedAnimation: " + recordingList);
            });
        });

    }
}