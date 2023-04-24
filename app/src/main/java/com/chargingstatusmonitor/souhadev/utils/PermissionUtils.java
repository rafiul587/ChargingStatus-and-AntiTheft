package com.chargingstatusmonitor.souhadev.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

public class PermissionUtils {
    public static final int WRITE_PERMISSION_CODE = 101;
    public static final int READ_PERMISSION_CODE = 102;
    public static final int RECORD_PERMISSION_REQUEST_CODE = 201;
    public static String[] READ_PERMISSION = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE};
    public static String[] WRITE_PERMISSION = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
    public static boolean hasPermission(@NonNull Context context, @NonNull String permission) {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    }
    
    public static boolean hasPermissions(@NonNull Context context, @NonNull String[] permissions) {
        for (String permission : permissions) {
            if (!hasPermission(context, permission)) {
                return false;
            }
        }
        return true;
    }
     
    public static void requestPermission(@NonNull Fragment fragment, @NonNull String permission, int requestCode) {
        fragment.requestPermissions(new String[]{permission}, requestCode);
    }
    
    public static void requestPermissions(@NonNull Fragment fragment, @NonNull String[] permissions, int requestCode) {
       fragment.requestPermissions(permissions, requestCode);
    }
    
    public static void shouldShowPermissionRationale(Fragment fragment, String permission, int requestCode, String rationaleMsg) {
            if (fragment.shouldShowRequestPermissionRationale(permission)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(fragment.requireContext())
                        .setTitle("Permission Required")
                        .setMessage(rationaleMsg)
                        .setPositiveButton("Ok", (dialog, which) -> {
                            dialog.dismiss();
                            fragment.requestPermissions(new String[] {permission}, requestCode);
                        })
                        .setNegativeButton("Cancel", (dialog, which) -> {
                            Toast.makeText(fragment.requireContext(), "Permission denied.", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        });
                builder.create().show();
            }
        }

    public static void requestWritePermission(Fragment fragment) {
        if(fragment.shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            PermissionUtils.shouldShowPermissionRationale(fragment, Manifest.permission.WRITE_EXTERNAL_STORAGE, WRITE_PERMISSION_CODE, "We need write permission for saving anything to the storage!");
        }else {
            fragment.requestPermissions(WRITE_PERMISSION, WRITE_PERMISSION_CODE);
        }
    }

    public static void requestReadPermission(Fragment fragment) {
        if(fragment.shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)){
            PermissionUtils.shouldShowPermissionRationale(fragment, Manifest.permission.READ_EXTERNAL_STORAGE, READ_PERMISSION_CODE, "We need read permission for getting files from the storage!");
        }else {
            fragment.requestPermissions(READ_PERMISSION, READ_PERMISSION_CODE);
        }
    }

}
