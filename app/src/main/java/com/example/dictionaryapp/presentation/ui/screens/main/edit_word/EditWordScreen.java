package com.example.dictionaryapp.presentation.ui.screens.main.edit_word;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.dictionaryapp.R;
import com.example.dictionaryapp.app.App;
import com.example.dictionaryapp.data.sources.local.room.enteties.Word;
import com.example.dictionaryapp.databinding.ScreenEditWordBinding;
import com.example.dictionaryapp.utils.SystemHelpers;
import com.example.dictionaryapp.utils.helpers.DatePickerDialogHelper;

import java.util.Objects;
import java.util.concurrent.Executors;

public class EditWordScreen extends Fragment {

    private ScreenEditWordBinding binding;
    private Word word;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = ScreenEditWordBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        word = EditWordScreenArgs.fromBundle(getArguments()).getData();
        loadViews();
    }

    private void loadViews() {
        binding.toolbar.setNavigationOnClickListener(v -> Navigation.findNavController(requireActivity(), R.id.app_nav_host).navigateUp());
        binding.toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.delete) {
                deleteWord(word);
            }
            return true;
        });

        if (!App.storage.getBoolean("isUz", false)) {
            binding.editTextWord.setText(word.word);
            binding.editTextTranslation.setText(word.translate);
        } else {
            binding.editTextWord.setText(word.translate);
            binding.editTextTranslation.setText(word.word);
        }
        binding.textNotify.setText(word.date);
        binding.switchFavourite.setChecked(word.isSaved);

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
                word.id = this.word.id;
                editDb(word);
            }
        });
    }

    private void deleteWord(Word word) {
        Executors.newSingleThreadExecutor().execute(() -> {
            App.appDatabase.getWordDao().delete(word);
            requireActivity().runOnUiThread(() -> {
                Toast.makeText(requireContext(), "Word successfully deleted!", Toast.LENGTH_SHORT).show();
                SystemHelpers.hideKeyboard(requireActivity());
                Navigation.findNavController(requireActivity(), R.id.app_nav_host).navigateUp();
            });
        });
    }

    private void editDb(Word word) {
        Executors.newSingleThreadExecutor().execute(() -> {
            App.appDatabase.getWordDao().update(word);
            requireActivity().runOnUiThread(() -> {
                Toast.makeText(requireContext(), "Word successfully edited!", Toast.LENGTH_SHORT).show();
                SystemHelpers.hideKeyboard(requireActivity());
            });
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public EditWordScreen() {
        super((R.layout.screen_add_word));
    }
}
