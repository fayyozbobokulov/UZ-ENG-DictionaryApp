package com.example.dictionaryapp.presentation.ui.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dictionaryapp.app.App;
import com.example.dictionaryapp.data.sources.local.room.enteties.Word;
import com.example.dictionaryapp.databinding.ItemWordBinding;

public class WordsAdapter extends ListAdapter<Word, WordsAdapter.WordViewHolder> {

    private OnItemClickListener onItemClickListener;
    private OnItemSaveListener onItemSaveListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnItemSaveListener(OnItemSaveListener onItemSaveListener) {
        this.onItemSaveListener = onItemSaveListener;
    }

    public WordsAdapter() {
        super(COMPARATOR);
    }

    @NonNull
    @Override
    public WordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new WordViewHolder(ItemWordBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull WordViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    class WordViewHolder extends RecyclerView.ViewHolder {
        private final ItemWordBinding binding;

        public WordViewHolder(@NonNull ItemWordBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.layoutRoot.setOnClickListener(v -> onItemClickListener.onClick(getItem(getBindingAdapterPosition())));
            binding.btnSave.setOnClickListener(v -> {
                Word data = getItem(getBindingAdapterPosition());
                data.isSaved = !data.isSaved;
                onItemSaveListener.onClick(data);
            });
        }

        public void bind(Word data) {
            binding.btnSave.setChecked(data.isSaved);
            if (!App.storage.getBoolean("isUz", false)) {
                binding.textWord.setText(data.word);
                binding.textTranslate.setText(data.translate);
            } else {
                binding.textWord.setText(data.translate);
                binding.textTranslate.setText(data.word);
            }
            if (data.date != null) {
                binding.textDate.setVisibility(View.VISIBLE);
                binding.textDate.setText(data.date);
            } else {
                binding.textDate.setVisibility(View.GONE);
            }
        }
    }

    static DiffUtil.ItemCallback<Word> COMPARATOR = new DiffUtil.ItemCallback<Word>() {
        @Override
        public boolean areItemsTheSame(@NonNull Word oldItem, @NonNull Word newItem) {
            return oldItem.id == newItem.id;
        }

        @SuppressLint("DiffUtilEquals")
        @Override
        public boolean areContentsTheSame(@NonNull Word oldItem, @NonNull Word newItem) {
            return oldItem.id == newItem.id && oldItem.date.equals(newItem.date) && oldItem.isSaved == newItem.isSaved && oldItem.word.equals(newItem.word) && oldItem.translate.equals(newItem.translate);
        }
    };

    @FunctionalInterface
    public interface OnItemClickListener {
        void onClick(Word word);
    }

    @FunctionalInterface
    public interface OnItemSaveListener {
        void onClick(Word word);
    }
}
