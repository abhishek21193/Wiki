package com.example.wiki.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.example.wiki.R;

public class WikiLauncher extends AppCompatActivity {

    private final static String TAG = WikiLauncher.class.getSimpleName() + "_fatal";

    public final static int SEARCH = 201;
    public final static int SHOW_PAGE = 202;

    FragmentManager mFragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mFragmentManager = getSupportFragmentManager();

        switchFunctionality(SEARCH, null);
    }


    public void switchFunctionality(int func, Bundle bundle) {
        switch (func) {
            case SEARCH:
                WikiSearch wikiSearch = new WikiSearch();
                FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
                fragmentTransaction.add(R.id.container, wikiSearch);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                fragmentTransaction.commit();

                break;

            case SHOW_PAGE:
                WikiWebView wikiWebView = new WikiWebView();

                if(bundle != null) {
                    wikiWebView.setArguments(bundle);
                }

                FragmentTransaction fragmentTransactionWeb = mFragmentManager.beginTransaction();
                fragmentTransactionWeb.add(R.id.container, wikiWebView);
                fragmentTransactionWeb.addToBackStack(null);
                fragmentTransactionWeb.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                fragmentTransactionWeb.commit();

                break;
        }
    }

    @Override
    public void onBackPressed() {

        int backStackEntryCount = mFragmentManager.getBackStackEntryCount();
        if (backStackEntryCount == 1) {
            finish();
        } else {
            super.onBackPressed();
        }
    }

}
