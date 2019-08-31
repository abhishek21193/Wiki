package com.example.wiki;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class WikiWebView extends Fragment {

    private WebView mWikiView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_web, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mWikiView = view.findViewById(R.id.wiki_page_view);
        mWikiView.setWebViewClient(new WebViewClient());

        if(getArguments() != null && Utility.isValidStr(getArguments().getString("wiki_url"))) {
            mWikiView.loadUrl(getArguments().getString("wiki_url"));
        }
    }
}
