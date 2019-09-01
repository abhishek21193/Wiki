package com.example.wiki.viewmodels;

import android.content.Context;

import com.example.wiki.network.DataCallBack;
import com.example.wiki.repos.WikiRepo;
import com.example.wiki.models.WikiResponse;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class WikiSearchViewModel extends ViewModel {
    private MutableLiveData<DataCallBack<WikiResponse>> mWikiResponseLiveData;
    private WikiRepo mWikiRepo;

    public void init(Context context){
        if (mWikiResponseLiveData != null){
            return;
        }
        mWikiRepo = WikiRepo.getInstance(context);
        mWikiResponseLiveData = new MutableLiveData<>();

    }

    public LiveData<DataCallBack<WikiResponse>> getSearchResultsObservable() {
        return mWikiResponseLiveData;
    }

    public void getSearchResults(String query, int limit) {
        mWikiRepo.getWikiSearchResults(query, limit, mWikiResponseLiveData);
    }
}
