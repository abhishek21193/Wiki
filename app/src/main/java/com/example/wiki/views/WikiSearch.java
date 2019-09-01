package com.example.wiki.views;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.wiki.network.DataCallBack;
import com.example.wiki.R;
import com.example.wiki.Utility;
import com.example.wiki.models.WikiResponse;
import com.example.wiki.viewmodels.WikiSearchViewModel;
import com.example.wiki.models.Page;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class WikiSearch extends Fragment {

    private final static String TAG = WikiSearch.class.getSimpleName() + "_fatal";

    private final static long SEARCH_SUGGESTION_DELAY = 500;

    private WikiAdapter wikiAdapter;
    private List<Page> queryList;
    private RecyclerView recyclerView;
    private WikiSearchViewModel mWikiSearchViewModel;
    private SearchView mSearchView;
    private long mStartTime;
    private String mSearchQuery;
    private Handler mSearchHandler;
    private ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        queryList = new ArrayList<>();
        mStartTime = 0;
        mSearchQuery = "";
        mSearchHandler = new Handler();

        recyclerView = view.findViewById(R.id.recyclerViewId);
        mSearchView = view.findViewById(R.id.search);
        progressBar = view.findViewById(R.id.progress_bar);

        progressBar.setVisibility(View.INVISIBLE);

        wikiAdapter = new WikiAdapter(getContext(), queryList, mClickInterface);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(wikiAdapter);

        mWikiSearchViewModel = ViewModelProviders.of(this).get(WikiSearchViewModel.class);

        if(getContext() != null) {
            mWikiSearchViewModel.init(getContext().getApplicationContext());
            initObservables();
            initsearch();
        }
    }

    private void initObservables() {
        mWikiSearchViewModel.getSearchResultsObservable().observe(this, mWikiSearchObserver);
    }

    private void removeObservables() {
        mWikiSearchViewModel.getSearchResultsObservable().removeObserver(mWikiSearchObserver);
    }


    private Observer<DataCallBack<WikiResponse>> mWikiSearchObserver = new Observer<DataCallBack<WikiResponse>>() {
        @Override
        public void onChanged(DataCallBack<WikiResponse> wikiResponseDataCallBack) {
            progressBar.setVisibility(View.INVISIBLE);
            if (Utility.isValidStr(mSearchQuery)) {
                if (wikiResponseDataCallBack != null) {
                    if (wikiResponseDataCallBack.getStatus() == DataCallBack.SUCCESS) {
                        if (wikiResponseDataCallBack.getData() != null && wikiResponseDataCallBack.getData().getQuery() != null) {
                            queryList.clear();
                            queryList.addAll(wikiResponseDataCallBack.getData().getQuery().getPages());
                            Log.d(TAG, queryList.toString());
                            wikiAdapter.setQuery(mSearchQuery);
                            wikiAdapter.notifyDataSetChanged();
                        }

                    } else {
                        Toast.makeText(getContext(), wikiResponseDataCallBack.getError(), Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(getContext(), "Something went wrong!", Toast.LENGTH_SHORT).show();
                }
            } else {
                queryList.clear();
                wikiAdapter.notifyDataSetChanged();
            }

        }
    };

    private void initsearch() {

        mSearchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSearchView.setIconified(false);
            }
        });

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mSearchQuery = newText;
                if (Utility.isValidStr(mSearchQuery)) {
                    mSearchHandler.removeCallbacks(mRunSearchSuggestions);
                    mStartTime = System.currentTimeMillis();
                    mSearchHandler.postDelayed(mRunSearchSuggestions, 500);
                } else if (queryList.size() > 0) {
                    queryList.clear();
                    wikiAdapter.notifyDataSetChanged();
                }

                return false;
            }
        });

        mSearchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {

                queryList.clear();
                wikiAdapter.notifyDataSetChanged();

                return false;
            }
        });
    }

    private Runnable mRunSearchSuggestions = new Runnable() {
        @Override
        public void run() {
            if (System.currentTimeMillis() - mStartTime > SEARCH_SUGGESTION_DELAY) {
                progressBar.setVisibility(View.VISIBLE);
                mWikiSearchViewModel.getSearchResults(mSearchQuery, 5);
            } else {
                mSearchHandler.postDelayed(this, 100);
            }
        }
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        removeObservables();
    }

    private WikiAdapter.ClickInterface mClickInterface = new WikiAdapter.ClickInterface() {
        @Override
        public void handleClick(Page queryDetail) {
            if (getActivity() != null) {
                hideKeyboard();
                Bundle bundle = new Bundle();
                bundle.putString("wiki_url", queryDetail.getFullurl());
                ((WikiLauncher) getActivity()).switchFunctionality(WikiLauncher.SHOW_PAGE, bundle);
            }
        }
    };

    private void hideKeyboard() {
        if (getActivity() != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
            View view = getActivity().getCurrentFocus();
            if (view == null) {
                view = new View(getActivity());
            }
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

    }
}
