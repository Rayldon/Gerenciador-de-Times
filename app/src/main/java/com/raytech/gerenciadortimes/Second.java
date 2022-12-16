package com.raytech.gerenciadortimes;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Second extends Fragment {
    private WebView webview;
    private SwipeRefreshLayout swipeRefreshLayout;
    private MainActivity activity;

    public Second(){
    }

    public Second(MainActivity activity){
        this.activity = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_second, container, false);
        webview = v.findViewById(R.id.webViewResultado);
        this.activity.setWebviewResultado(webview);

        CookieManager.getInstance().setAcceptCookie(true);
        CookieManager.getInstance().setAcceptThirdPartyCookies(webview, true);
        webview.setVerticalScrollBarEnabled(false);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.setLayerType(webview.LAYER_TYPE_SOFTWARE, null);
        webview.getSettings().setPluginState(WebSettings.PluginState.ON);
        webview.getSettings().setAllowFileAccess(true);
        webview.getSettings().setAllowFileAccessFromFileURLs(true);
        webview.getSettings().setAllowUniversalAccessFromFileURLs(true);
        webview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webview.getSettings().setLoadWithOverviewMode(true);
        webview.getSettings().setUseWideViewPort(true);
        webview.getSettings().setDomStorageEnabled(true);
        webview.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        webview.clearCache(true);
        webview.clearHistory();
        webview.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        webview.setScrollbarFadingEnabled(false);
        webview.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webview.addJavascriptInterface(new LoadMenu(activity), "android");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            webview.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else {
            webview.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        webview.loadUrl("file:///android_asset/html/resultado.html");

        swipeRefreshLayout = v.findViewById(R.id.swipeResultado);
        swipeRefreshLayout.setProgressViewOffset(false, 0,180);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                        activity.carregarLista();
                    }
                },  2000);
            }
        });

        swipeRefreshLayout.setColorSchemeColors(
                getResources().getColor(android.R.color.holo_blue_dark),
                getResources().getColor(android.R.color.holo_green_dark)
        );

        return v;
    }
}