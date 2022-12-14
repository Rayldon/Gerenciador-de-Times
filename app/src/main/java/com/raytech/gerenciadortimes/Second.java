package com.raytech.gerenciadortimes;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

public class Second extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.activity_main, container, false);
        //WebView webview = (WebView) v.findViewById(R.id.webView);
        //webview.loadUrl("file:///android_asset/html/historico.html");
        return inflater.inflate(R.layout.fragment_second, container, false);
    }
}