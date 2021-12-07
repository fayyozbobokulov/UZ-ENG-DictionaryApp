package com.example.dictionaryapp.presentation.ui.screens.main.saved;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.dictionaryapp.R;
import com.example.dictionaryapp.app.App;
import com.example.dictionaryapp.data.sources.local.room.enteties.Word;
import com.example.dictionaryapp.databinding.ScreenSavedWordsBinding;
import com.example.dictionaryapp.presentation.ui.adapters.WordsAdapter;
import com.example.dictionaryapp.presentation.ui.dialogs.main.DialogInfo;
import com.example.dictionaryapp.presentation.ui.screens.main.MainScreenDirections;

import java.util.List;
import java.util.concurrent.Executors;

public class SavedWordsScreen extends Fragment {

    private ScreenSavedWordsBinding binding;
    private final WordsAdapter adapter = new WordsAdapter();
    private NavController navController;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = ScreenSavedWordsBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        navController = Navigation.findNavController(requireActivity(), R.id.app_nav_host);
        loadViews();
    }

    private void loadViews() {
        binding.list.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        binding.list.setAdapter(adapter);
        binding.list.setItemAnimator(null);
        adapter.setOnItemClickListener(word -> {
            DialogInfo dialog = new DialogInfo();
            dialog.word = word;
            dialog.setOnItemEditClickListener(() -> navController.navigate((NavDirections) MainScreenDirections.actionMainScreen3ToEditWordScreen(word)));
            dialog.setOnItemDeleteClickListener(() -> deleteWord(word));
            dialog.show(getChildFragmentManager(), "dialogInfo");
        });

        adapter.setOnItemSaveListener(this::editDb);
        loadData();

        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(receiver, new IntentFilter("refresh"));
    }

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void onReceive(Context context, Intent intent) {
            loadData();
            adapter.notifyDataSetChanged();
        }
    };

    private void deleteWord(Word word) {
        Executors.newSingleThreadExecutor().execute(() -> {
            App.appDatabase.getWordDao().delete(word);
            requireActivity().runOnUiThread(() -> {
                Toast.makeText(requireContext(), "Word successfully deleted!", Toast.LENGTH_SHORT).show();
                loadData();
            });
        });
    }

    private void loadData() {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<Word> ls = App.appDatabase.getWordDao().getAllSaved();
            requireActivity().runOnUiThread(() -> loadToAdapter(ls));
        });
    }

    private void editDb(Word word) {
        Executors.newSingleThreadExecutor().execute(() -> {
            App.appDatabase.getWordDao().update(word);
            loadData();
        });
    }

    private void loadToAdapter(List<Word> list) {
        if (list.isEmpty()) {
            binding.list.setVisibility(View.GONE);
            binding.textEmpty.setVisibility(View.VISIBLE);
        } else {
            binding.textEmpty.setVisibility(View.GONE);
            binding.list.setVisibility(View.VISIBLE);
        }
        adapter.submitList(list);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding.list.setAdapter(null);
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(receiver);
        binding = null;
    }

    public SavedWordsScreen() {
        super((R.layout.screen_saved_words));
    }
}
