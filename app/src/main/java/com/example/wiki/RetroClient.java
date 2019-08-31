package com.example.wiki;

import android.content.Context;
import android.net.ConnectivityManager;
import android.util.Log;

import java.io.IOException;

import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetroClient {

    private final static String TAG = RetroClient.class.getSimpleName() + "_fatal";
    private static int cacheSize = 10 * 1024 * 1024; // 10 MB

    private static Retrofit getRetrofitInstance(final Context context) {

        Cache cache = new Cache(context.getCacheDir(), cacheSize);

        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
        clientBuilder.addInterceptor(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                Log.d(TAG, "request: " + request.toString());
                if (!isNetworkConnected(context)) {
                    int maxStale = 60 * 60 * 24; // Offline cache available for 1 day
                    request = request.newBuilder()
                            .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                            .removeHeader("Pragma")
                            .build();
                }
                return chain.proceed(request);
            }
        });

        clientBuilder.addNetworkInterceptor(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                Log.d(TAG, "request: " + request.toString());
                okhttp3.Response response = chain.proceed(request);
                int maxAge = 60; // read from cache for 60 seconds even if there is internet connection
                return response.newBuilder()
                        .header("Cache-Control", "public, max-age=" + maxAge)
                        .removeHeader("Pragma")
                        .build();
            }
        });

        clientBuilder.cache(cache);

        return new Retrofit.Builder()
                .baseUrl(Constant.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(clientBuilder.build())
                .build();
    }


    public static ApiInterface getApiInterface(Context context) {
        return getRetrofitInstance(context).create(ApiInterface.class);
    }

    private static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }


}
