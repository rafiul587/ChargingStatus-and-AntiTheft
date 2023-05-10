package com.chargingstatusmonitor.souhadev;

import static com.chargingstatusmonitor.souhadev.utils.PermissionUtils.READ_PERMISSION_CODE;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.chargingstatusmonitor.souhadev.databinding.ActivityMainBinding;
import com.chargingstatusmonitor.souhadev.ui.fragments.HomeFragment;
import com.chargingstatusmonitor.souhadev.utils.MyService;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_OVERLAY_PERMISSION = 1000;
    private ActivityMainBinding binding;
    Intent serviceIntent;
    NavController navController;
    private static final int REQUEST_CODE_PERMISSIONS = 100;

    String[] permissions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            permissions = new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.RECORD_AUDIO
            };
        }else {
            permissions = new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.RECORD_AUDIO
            };
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(permissions, REQUEST_CODE_PERMISSIONS);
            }
        }
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_battery_info, R.id.navigation_animation_graph, R.id.navigation_ringtone_graph, R.id.navigation_anti_theft_graph)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupWithNavController(binding.toolbar, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
        if (serviceIntent == null) {
            // start service
            serviceIntent = new Intent(this, MyService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(serviceIntent);
            } else startService(serviceIntent);
            setServiceSwitchInitialState();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            boolean allPermissionsGranted = true;
            List<String> deniedPermissions = new ArrayList<>();

            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false;
                    deniedPermissions.add(permissions[i]);
                }
            }
            Log.d("TAG" ,"onRequestPermissionsResult: " + deniedPermissions);
            if (!allPermissionsGranted) {
                for (String permission : deniedPermissions) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(permission)) {
                            showPermissionRationaleDialog(permission);
                        }
                    }
                }
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
                    showDrawOverlayPermissionDialog();
                }
            }
        }
    }

    private void showDrawOverlayPermissionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permission required");
        builder.setMessage("This app needs to draw over other apps in order to function properly.");
        builder.setPositiveButton("OK", (dialog, which) -> requestDrawOverlayPermission());
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void requestDrawOverlayPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, REQUEST_CODE_OVERLAY_PERMISSION);
        }
    }

    private void showPermissionRationaleDialog(final String permission) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permission required!");
        builder.setMessage(getPermissionName(permission));
        builder.setPositiveButton("OK", (dialog, which) -> requestPermission(permission));
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void requestPermission(String permission) {
        ActivityCompat.requestPermissions(this, new String[]{permission}, REQUEST_CODE_PERMISSIONS);
    }

    private String getPermissionName(String permission) {
        switch (permission) {
            case Manifest.permission.READ_EXTERNAL_STORAGE:
            case Manifest.permission.WRITE_EXTERNAL_STORAGE:
                return "We need storage permission to read and write to the storage!";
            case Manifest.permission.RECORD_AUDIO:
                return "This app needs access to your microphone to record your voice.";
            default:
                return "permission";
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_OVERLAY_PERMISSION) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Settings.canDrawOverlays(this)) {
                if (serviceIntent == null) {
                    // start service
                    serviceIntent = new Intent(this, MyService.class);
                    startService(serviceIntent);
                    setServiceSwitchInitialState();
                }
            } else {
                // The user did not grant the permission, handle this case accordingly
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        } else if (item.getItemId() == R.id.action_search) {
            startActivity(new Intent(this, SearchActivity.class));
            return true;
        } else return super.onOptionsItemSelected(item);
    }

    public void setServiceSwitchInitialState() {

        Fragment navHostFragment = getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_main);

        if (navHostFragment != null) {
            Fragment fragment = navHostFragment.getChildFragmentManager().getFragments().get(0);
            if (fragment instanceof HomeFragment) {
                Log.d("TAG", "setServiceSwitchInitialState: ");
                ((HomeFragment) fragment).setServiceSwitchInitialState();
            }
        }
    }

    public void goToAntiTheftSection(){
        binding.navView.setSelectedItemId(R.id.navigation_anti_theft_graph);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}