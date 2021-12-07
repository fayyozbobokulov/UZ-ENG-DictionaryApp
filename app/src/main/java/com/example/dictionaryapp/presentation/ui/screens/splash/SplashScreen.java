package com.example.dictionaryapp.presentation.ui.screens.splash;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.dictionaryapp.R;
import com.example.dictionaryapp.databinding.ScreenSplashBinding;

import java.util.concurrent.Executors;

/**
 * Created by B.Kozimov on 06.12.2021 17:47.
 */
@SuppressLint("CustomSplashScreen")
public class SplashScreen extends Fragment {

    private ScreenSplashBinding binding = null;
    private NavController navController;

    public SplashScreen() {
        super(R.layout.screen_splash);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = ScreenSplashBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        navController = Navigation.findNavController(view);
        loadViews();
    }

    private void loadViews() {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                Thread.sleep(1000L);
                requireActivity().runOnUiThread(() -> navController.navigate(R.id.action_splashScreen_to_mainScreen3));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
