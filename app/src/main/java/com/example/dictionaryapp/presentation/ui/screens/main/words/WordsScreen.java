package com.example.dictionaryapp.presentation.ui.screens.main.words;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.dictionaryapp.R;
import com.example.dictionaryapp.app.App;
import com.example.dictionaryapp.data.sources.local.room.enteties.Word;
import com.example.dictionaryapp.databinding.ScreenWordsBinding;
import com.example.dictionaryapp.presentation.ui.adapters.WordsAdapter;
import com.example.dictionaryapp.presentation.ui.dialogs.main.DialogInfo;
import com.example.dictionaryapp.presentation.ui.screens.main.MainScreenDirections;
import com.example.dictionaryapp.utils.SystemHelpers;

import java.util.List;
import java.util.concurrent.Executors;

public class WordsScreen extends Fragment {

    private ScreenWordsBinding binding;
    private final WordsAdapter adapter = new WordsAdapter();
    private NavController navController;
    private String querySt = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = ScreenWordsBinding.inflate(inflater);
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

        initSearchView();

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

    private void initSearchView() {
        Handler handler = new Handler();
        binding.search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query != null) {
                    querySt = query;
                    loadData();
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                handler.removeCallbacksAndMessages(null);
                handler.postDelayed(() -> {
                    if (newText != null && !newText.trim().equals(querySt)) {
                        querySt = newText.trim();
                        loadData();
                    }
                }, 1000);
                return true;
            }
        });

        ((ImageView) binding.search.findViewById(R.id.search_close_btn)).setOnClickListener(v -> {
            binding.search.setQuery(null, false);
            binding.search.clearFocus();
            SystemHelpers.hideKeyboard(requireActivity());
        });
    }

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
            List<Word> ls = !App.storage.getBoolean("isUz", false) ? App.appDatabase.getWordDao().searchByWord(querySt) :
                    App.appDatabase.getWordDao().searchByTranslation(querySt);
            requireActivity().runOnUiThread(() -> loadToAdapter(ls));
        });
    }

    private void editDb(Word word) {
        Executors.newSingleThreadExecutor().execute(() -> App.appDatabase.getWordDao().update(word));
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

    public WordsScreen() {
        super((R.layout.screen_words));
    }
}
