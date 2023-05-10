package com.chargingstatusmonitor.souhadev;

import static com.chargingstatusmonitor.souhadev.utils.PermissionUtils.READ_PERMISSION_CODE;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

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
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_search, R.id.navigation_unlock)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_search);
        NavigationUI.setupWithNavController(binding.toolbar, navController, appBarConfiguration);
    }
}