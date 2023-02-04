package com.raytech.gerenciadortimes;

import static android.content.ContentValues.TAG;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private WebView webview;
    private WebView webviewResultado;
    private MainActivity activity;
    public String url = "";
    private String lista = "";
    private Date ultimaAtualizacao = new Date();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    ViewPager viewPager;
    TabLayout tabLayout;
    TabAccessAdapter tabAccessAdapter;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        activity = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        viewPager = (ViewPager) findViewById(R.id.view_pager);
        tabAccessAdapter = new TabAccessAdapter(getSupportFragmentManager(), this);
        viewPager.setAdapter(tabAccessAdapter);

        tabLayout = (TabLayout) findViewById(R.id.main_tabs);
        tabLayout.setupWithViewPager(viewPager);
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

    public void carregarLista(){
        carregarLista(lista);
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
                                String resultado = document.get("resultados") != null ? document.get("resultados").toString() : "[]";
                                Timestamp timestamp = (Timestamp) document.get("data_atualizacao");
                                String data = timestamp.toDate().toString();

                                webview.loadUrl("javascript:carregarLista("+json+", "+resultado+", '"+data+"')");
                                webviewResultado.loadUrl("javascript:carregarResultado("+resultado+")");
                                break;
                            }
                        } else {
                            webview.loadUrl("javascript:carregarLista('', '')");
                        }
                    }
                }
            });
    }

    public void salvar(final String lista, String id, String json, String resultados, String dataAtualizacao){
        Map<String, Object> registro = new HashMap<>();
        registro.put("lista", lista);
        registro.put("json", json);
        registro.put("resultados", resultados);
        registro.put("data_atualizacao", new Date());
        webviewResultado.post(new Runnable() {
            @Override
            public void run() {
                webviewResultado.loadUrl("javascript:carregarResultado("+resultados+")");
            }
        });

        //if(id == null) {
            db.collection("listas").whereEqualTo("lista", lista).get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                if (task.getResult() != null && !task.getResult().isEmpty()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        Timestamp timestamp = (Timestamp) document.get("data_atualizacao");
                                        String data = timestamp.toDate().toString();

                                        if(data.equals(dataAtualizacao)){
                                            update(document.getId(), registro);
                                        }else {
                                            String json = document.get("json").toString();
                                            String resultado = document.get("resultados") != null ? document.get("resultados").toString() : "[]";
                                            webview.loadUrl("javascript:carregarLista("+json+", "+resultado+", '"+data+"')");
                                            webviewResultado.loadUrl("javascript:carregarResultado("+resultado+")");
                                        }
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
        //}else{
            //update(id, registro);
        //}
    }

    private void insert(Map<String, Object> registro){
        db.collection("listas").add(registro)
            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    Log.d(TAG, "Executado com sucesso: " + documentReference.getId());
                    String data = registro.get("data_atualizacao").toString();
                    webview.loadUrl("javascript:salvarId('"+documentReference.getId()+"','"+data+"')");
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
                    String data = registro.get("data_atualizacao").toString();
                    webview.loadUrl("javascript:salvarId('"+id+"','"+data+"')");
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w(TAG, "Ocorreu um erro", e);
                }
            });
    }

    public void setWebview(WebView webview) {
        this.webview = webview;
    }

    public void setWebviewResultado(WebView webviewResultado) {
        this.webviewResultado = webviewResultado;
    }

    public String getLista() {
        return lista;
    }

    public Date getUltimaAtualizacao() {
        return ultimaAtualizacao;
    }

    public void setUltimaAtualizacao(Date ultimaAtualizacao) {
        this.ultimaAtualizacao = ultimaAtualizacao;
    }
}


