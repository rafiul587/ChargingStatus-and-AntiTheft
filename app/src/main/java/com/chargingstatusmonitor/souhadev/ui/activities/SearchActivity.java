package com.chargingstatusmonitor.souhadev.ui.activities;

import static com.chargingstatusmonitor.souhadev.utils.PermissionUtils.READ_PERMISSION_CODE;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.chargingstatusmonitor.souhadev.R;
import com.chargingstatusmonitor.souhadev.databinding.ActivityMainBinding;
import com.chargingstatusmonitor.souhadev.databinding.ActivitySearchBinding;
import com.chargingstatusmonitor.souhadev.utils.MyService;

public class SearchActivity extends AppCompatActivity {

    NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivitySearchBinding binding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_search);

        navController.addOnDestinationChangedListener((navController, navDestination, bundle) -> {
            if(navDestination.getId() == R.id.navigation_unlock){
                binding.toolbar.setTitle(getString(R.string.unlock_screen_title));
            }else {
                binding.toolbar.setTitle(getString(R.string.search_screen_title));
            }
        });
        binding.toolbar.setNavigationOnClickListener(v -> {
            NavDestination currentDestination = navController.getCurrentDestination();
            if (currentDestination != null) {
                if (currentDestination.getId() == R.id.navigation_search) {
                    finish();
                } else {
                    navController.navigateUp();
                }
            }
        });
    }
}