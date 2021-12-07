package com.example.dictionaryapp.presentation.ui.screens.main.add_word;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.dictionaryapp.R;
import com.example.dictionaryapp.app.App;
import com.example.dictionaryapp.data.sources.local.room.enteties.Word;
import com.example.dictionaryapp.databinding.ScreenAddWordBinding;
import com.example.dictionaryapp.utils.SystemHelpers;
import com.example.dictionaryapp.utils.helpers.DatePickerDialogHelper;

import java.util.Objects;
import java.util.concurrent.Executors;

public class AddWordScreen extends Fragment {

    private ScreenAddWordBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = ScreenAddWordBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        loadViews();
    }

    private void loadViews() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.langs));
        binding.langSpinner.setAdapter(adapter);

        if (!App.storage.getBoolean("isUz", false))
            binding.langSpinner.setSelection(0);
        else
            binding.langSpinner.setSelection(1);

        binding.layoutSpinner.setOnClickListener(v -> binding.langSpinner.performClick());

        binding.notifyAt.setOnClickListener(v -> DatePickerDialogHelper.getInstance().showDatePickerDialog(requireContext(), result -> binding.textNotify.setText(result)));

        binding.btnAdd.setOnClickListener(v -> {
            Word word;
            String text = Objects.requireNonNull(binding.editTextWord.getText()).toString();
            String translate = Objects.requireNonNull(binding.editTextTranslation.getText()).toString();
            boolean isFav = binding.switchFavourite.isChecked();
            String notify = binding.textNotify.getText().toString();
            boolean isEnglish = binding.langSpinner.getSelectedItemPosition() == 0;

            boolean isActive = true;
            if (text.trim().isEmpty()) {
                isActive = false;
                binding.editTextWord.setError("Fill the field!");
            }
            if (translate.trim().isEmpty()) {
                isActive = false;
                binding.editTextTranslation.setError("Fill the field!");
            }
            if (isActive) {
                if (!isEnglish) {
                    String temp = translate;
                    translate = text;
                    text = temp;
                }
                word = new Word(text, translate, notify, isFav);
                addToDb(word);
            }
        });

        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(receiver, new IntentFilter("refresh"));
    }

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void onReceive(Context context, Intent intent) {
            if (!App.storage.getBoolean("isUz", false))
                binding.langSpinner.setSelection(0);
            else
                binding.langSpinner.setSelection(1);
        }
    };

    private void addToDb(Word word) {
        Executors.newSingleThreadExecutor().execute(() -> {
            App.appDatabase.getWordDao().insert(word);
            requireActivity().runOnUiThread(() -> {
                Toast.makeText(requireContext(), "Word added successfully!", Toast.LENGTH_SHORT).show();
                binding.editTextWord.setText("");
                binding.editTextTranslation.setText("");
                binding.switchFavourite.setChecked(false);
                binding.textNotify.setText("");
                SystemHelpers.hideKeyboard(requireActivity());
            });
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(receiver);
        binding = null;
    }

    public AddWordScreen() {
        super((R.layout.screen_add_word));
    }
}
