package com.example.dictionaryapp.presentation.ui.dialogs.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.dictionaryapp.R;
import com.example.dictionaryapp.app.App;
import com.example.dictionaryapp.data.sources.local.room.enteties.Word;
import com.example.dictionaryapp.databinding.DialogInfoBinding;

public class DialogInfo extends DialogFragment {

    private DialogInfoBinding binding;
    public Word word;

    private OnItemEditClickListener onItemEditClickListener;
    private OnItemDeleteClickListener onItemDeleteClickListener;

    public void setOnItemEditClickListener(OnItemEditClickListener onItemEditClickListener) {
        this.onItemEditClickListener = onItemEditClickListener;
    }

    public void setOnItemDeleteClickListener(OnItemDeleteClickListener onItemDeleteClickListener) {
        this.onItemDeleteClickListener = onItemDeleteClickListener;
    }

    public DialogInfo() {
        super(R.layout.dialog_info);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, R.style.DialogStyle);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DialogInfoBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        loadViews();
    }

    private void loadViews() {
        if (!App.storage.getBoolean("isUz", false)) {
            binding.textWord.setText(word.word);
            binding.textTranslation.setText(word.translate);
        } else {
            binding.textWord.setText(word.translate);
            binding.textTranslation.setText(word.word);
        }
        binding.textNotify.setText(word.date);
        binding.switchFavourite.setChecked(word.isSaved);

        binding.btnDelete.setOnClickListener(v -> {
            onItemDeleteClickListener.onClick();
            dismiss();
        });

        binding.btnEdit.setOnClickListener(v -> {
            onItemEditClickListener.onClick();
            dismiss();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @FunctionalInterface
    public interface OnItemEditClickListener {
        void onClick();
    }

    @FunctionalInterface
    public interface OnItemDeleteClickListener {
        void onClick();
    }
}
