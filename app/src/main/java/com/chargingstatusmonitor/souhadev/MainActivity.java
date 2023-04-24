package com.chargingstatusmonitor.souhadev;

import static com.chargingstatusmonitor.souhadev.utils.FileUtils.startDownload;
import static com.chargingstatusmonitor.souhadev.utils.PermissionUtils.READ_PERMISSION;
import static com.chargingstatusmonitor.souhadev.utils.PermissionUtils.READ_PERMISSION_CODE;
import static com.chargingstatusmonitor.souhadev.utils.PermissionUtils.RECORD_PERMISSION_REQUEST_CODE;
import static com.chargingstatusmonitor.souhadev.utils.PermissionUtils.WRITE_PERMISSION_CODE;
import static com.chargingstatusmonitor.souhadev.utils.PermissionUtils.hasPermission;
import static com.chargingstatusmonitor.souhadev.utils.PermissionUtils.shouldShowPermissionRationale;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.chargingstatusmonitor.souhadev.databinding.ActivityMainBinding;
import com.chargingstatusmonitor.souhadev.utils.MyService;
import com.chargingstatusmonitor.souhadev.utils.PermissionUtils;

import java.security.Permission;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    Intent serviceIntent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){

            } else if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)){
                shouldShowPermissionRationale();
            }else ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, READ_PERMISSION_CODE);
        }
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_ringtone, R.id.navigation_search, R.id.navigation_settings)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupWithNavController(binding.toolbar, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
        if (serviceIntent == null) {
            // start service
            serviceIntent = new Intent(this, MyService.class);
            startService(serviceIntent);
        }

    }

    public void shouldShowPermissionRationale() {
            AlertDialog.Builder builder = new AlertDialog.Builder(this)
                    .setTitle("Permission Required")
                    .setMessage("We need storage permission to read and write to the storage!")
                    .setPositiveButton("Ok", (dialog, which) -> {
                        dialog.dismiss();
                        ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, READ_PERMISSION_CODE);
                    })
                    .setNegativeButton("Cancel", (dialog, which) -> {
                        Toast.makeText(this, "Permission denied.", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    });
            builder.create().show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == READ_PERMISSION_CODE) {
            if (grantResults.length > 0) {
                // check whether storage permission granted or not.
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // do what you want;
                } else {

                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(serviceIntent);
    }
}