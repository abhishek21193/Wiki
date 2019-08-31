package com.example.wiki;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WikiRepo {

    private static final String TAG = WikiRepo.class.getSimpleName() + "_fatal";
    private static WikiRepo newsRepository;

    public static WikiRepo getInstance(Context context) {
        if (newsRepository == null) {
            newsRepository = new WikiRepo(context);
        }
        return newsRepository;
    }

    private ApiInterface apiInterface;

    public WikiRepo(Context context) {
        apiInterface = RetroClient.getApiInterface(context);
    }

    public void getWikiSearchResults(String query, int limit, final MutableLiveData<DataCallBack<WikiResponse>> wikiSearchData) {
        apiInterface.getSearchResults(query, limit).enqueue(new Callback<WikiResponse>() {
            @Override
            public void onResponse(Call<WikiResponse> call, Response<WikiResponse> response) {
                findResponseSource(response);
                if (response.isSuccessful()) {
                    wikiSearchData.setValue(new DataCallBack<>(DataCallBack.SUCCESS, "success", response.body()));
                } else {
                    wikiSearchData.setValue(new DataCallBack<>(DataCallBack.FAILURE, response.message(), response.body()));
                }
            }

            @Override
            public void onFailure(Call<WikiResponse> call, Throwable t) {
                wikiSearchData.setValue(new DataCallBack<WikiResponse>(DataCallBack.FAILURE, "Something went wrong!", null));
            }
        });
    }

    private void findResponseSource(Response<WikiResponse> response) {
        if (response.raw().cacheResponse() != null) {
            Log.d(TAG, "response came from cache");
        }

        if (response.raw().networkResponse() != null) {
            Log.d(TAG, "response came from server");
        }
    }

}
