package com.raytech.gerenciadortimes;

import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private WebView webview;
    private MainActivity activity;
    public String url = "";
    private String lista = "";
    private Date ultimaAtualizacao = new Date();
    //FirebaseFirestore db = FirebaseFirestore.getInstance();

    @RequiresApi(api = Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        activity = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webview = (WebView) findViewById(R.id.webView);
        CookieManager.getInstance().setAcceptCookie(true);
        //CookieManager.getInstance().setAcceptThirdPartyCookies(webview, true);
        webview.setVerticalScrollBarEnabled(false);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.setLayerType(webview.LAYER_TYPE_SOFTWARE, null);
        webview.getSettings().setPluginState(WebSettings.PluginState.ON);
        webview.getSettings().setAllowFileAccess(true);
        //webview.getSettings().setAllowFileAccessFromFileURLs(true);
        //webview.getSettings().setAllowUniversalAccessFromFileURLs(true);
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

        webview.loadUrl("file:///android_asset/html/teste.html");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        menu.add(0, 1, Menu.NONE, "Abrir lista");
        menu.add(0, 1, Menu.NONE, "Nova lista");
        menu.add(0, 1, Menu.NONE, "Limpar lista");
        menu.add(0, 1, Menu.NONE, "Desfazer");
        //menu.add(0, 1, Menu.NONE, "Refazer");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getTitle() != null && item.getTitle().equals("Limpar lista")) {
            webview.loadUrl("javascript:limpar()");
        }
        if(item.getTitle() != null && item.getTitle().equals("Desfazer")) {
            webview.loadUrl("javascript:desfazer()");
        }
        if(item.getTitle() != null && item.getTitle().equals("Refazer")) {
            webview.loadUrl("javascript:refazer()");
        }
        if(item.getTitle() != null && item.getTitle().equals("Abrir lista")) {
            webview.loadUrl("javascript:abrirLista()");
        }
        if(item.getTitle() != null && item.getTitle().equals("Nova lista")) {
            webview.loadUrl("javascript:menuNovaLista()");
        }
        activity.invalidateOptionsMenu();
        return super.onOptionsItemSelected(item);
    }
}


