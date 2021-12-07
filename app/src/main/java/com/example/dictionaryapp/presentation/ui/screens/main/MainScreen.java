package com.example.dictionaryapp.presentation.ui.screens.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.dictionaryapp.R;
import com.example.dictionaryapp.app.App;
import com.example.dictionaryapp.databinding.ScreenMainBinding;

public class MainScreen extends Fragment {

    private ScreenMainBinding binding;
    private NavController navScreenController;
    private AppBarConfiguration appBarConfiguration;

    public MainScreen() {
        super(R.layout.screen_main);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = ScreenMainBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        setUpNavigation();
        loadViews();
    }

    private void loadViews() {
        if (App.storage.getBoolean("isUz", false)) {
            binding.en.setChecked(true);
        } else
            binding.uz.setChecked(true);
        binding.toggle.setOnCheckedChangeListener((group, checkedId) -> {
            App.storage.edit().putBoolean("isUz", checkedId == binding.en.getId()).apply();

            LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(new Intent("refresh"));
        });
    }

    private void setUpNavigation() {
        NavHostFragment navHostFragment = (NavHostFragment) getChildFragmentManager().findFragmentById(R.id.main_screen_nav_host);
        if (navHostFragment != null) {
            navScreenController = navHostFragment.getNavController();
        }

        appBarConfiguration = new AppBarConfiguration.Builder(navScreenController.getGraph())
                .setOpenableLayout(binding.drawerLayout)
                .setFallbackOnNavigateUpListener(() -> false)
                .build();
        NavigationUI.setupWithNavController(binding.navView, navScreenController);
        NavigationUI.setupWithNavController(binding.toolbar, navScreenController, appBarConfiguration);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        appBarConfiguration = null;
    }
}
