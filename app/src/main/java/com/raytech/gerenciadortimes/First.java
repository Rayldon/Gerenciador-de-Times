package com.raytech.gerenciadortimes;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.fragment.app.Fragment;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class First extends Fragment {
    private WebView webview;
    private MainActivity activity;

    public First(){
    }

    public First(MainActivity activity){
        this.activity = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_first, container, false);
        webview = (WebView) v.findViewById(R.id.webView);
        this.activity.setWebview(webview);

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

        webview.loadUrl("file:///android_asset/html/index.html");

        webview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                long diff = new Date().getTime() - activity.getUltimaAtualizacao().getTime();
                if(!activity.getLista().equals("") && TimeUnit.MILLISECONDS.toSeconds(diff) > 20) {
                    activity.carregarLista(activity.getLista());
                    activity.setUltimaAtualizacao(new Date());
                }
                return false;
            }
        });

        return v;
    }
}