package com.example.wiki;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiInterface {

    @GET(Constant.END_POINT_URL)
    Call<WikiResponse> getSearchResults(@Query("gpssearch") String gpssearch, @Query("gpslimit") int gpslimit);

  //  https://en.wikipedia.org//w/api.php?action=query&format=json&prop=pageimages%7Cpageterms&generator=prefixsearch&redirects=1&formatversion=2&piprop=thumbnail&pithumbsize=50&pilimit=10&wbptterms=description&gpssearch=Albert+E&gpslimit=10

}
