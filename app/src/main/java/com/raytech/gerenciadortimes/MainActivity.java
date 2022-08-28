package com.raytech.gerenciadortimes;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    final Context context = this;
    private WebView webview;
    private MainActivity activity;
    public String url = "";
    private String lista = "";
    private Date ultimaAtualizacao = new Date();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        activity = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //MobileAds.initialize(this,"ca-app-pub-2085155322175264/8059261149");

        //mAdView = findViewById(R.id.adView);
        //AdRequest adRequest = new AdRequest.Builder().build();
        //mAdView.loadAd(adRequest);

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        webview = (WebView) findViewById(R.id.webView);
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
                long diff = new Date().getTime() - ultimaAtualizacao.getTime();
                if(!lista.equals("") && TimeUnit.MILLISECONDS.toSeconds(diff) > 20) {
                    carregarLista(lista);
                    ultimaAtualizacao = new Date();
                }
                return false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        menu.add(0, 1, Menu.NONE, "Abrir lista");
        menu.add(0, 1, Menu.NONE, "Nova lista");
        menu.add(0, 1, Menu.NONE, "Limpar lista");
        //menu.add(0, 1, Menu.NONE, "Desfazer");
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

    public void criarLista(String lista){
        final String novaLista = lista.trim().toUpperCase();
        db.collection("listas").whereEqualTo("lista", novaLista).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult() != null && !task.getResult().isEmpty()) {
                                webview.loadUrl("javascript:criarLista('')");
                            } else {
                                webview.loadUrl("javascript:criarLista('"+novaLista+"')");
                            }
                        }
                    }
                });
    }

    public void carregarLista(String lista){
        lista = lista.trim().toUpperCase();
        this.lista = lista;
        db.collection("listas").whereEqualTo("lista", lista).get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        if (task.getResult() != null && !task.getResult().isEmpty()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String json = document.get("json").toString();
                                webview.loadUrl("javascript:carregarLista("+json+")");
                                break;
                            }
                        } else {
                            webview.loadUrl("javascript:carregarLista('')");
                        }
                    }
                }
            });
    }

    public void salvar(String lista, String id, String json){
        lista = lista.trim().toUpperCase();
        Map<String, Object> registro = new HashMap<>();
        registro.put("lista", lista);
        registro.put("json", json);

        if(id == null) {
            db.collection("listas").whereEqualTo("lista", lista).get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                if (task.getResult() != null && !task.getResult().isEmpty()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        update(document.getId(), registro);
                                        break;
                                    }
                                } else {
                                    insert(registro);
                                }
                            } else {
                                Log.w(TAG, "Erro ao obter lista.", task.getException());
                            }
                        }
                    });
        }else{
            update(id, registro);
        }
    }

    private void insert(Map<String, Object> registro){
        db.collection("listas").add(registro)
            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    Log.d(TAG, "Executado com sucesso: " + documentReference.getId());
                    webview.loadUrl("javascript:salvarId('"+documentReference.getId()+"')");
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w(TAG, "Ocorreu um erro", e);
                }
            });
    }

    private void update(String id, Map<String, Object> registro){
        db.collection("listas").document(id).update(registro)
            .addOnSuccessListener(new OnSuccessListener<Object>() {
                @Override
                public void onSuccess(Object o) {
                    Log.d(TAG, "Executado com sucesso");
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w(TAG, "Ocorreu um erro", e);
                }
            });
    }
}