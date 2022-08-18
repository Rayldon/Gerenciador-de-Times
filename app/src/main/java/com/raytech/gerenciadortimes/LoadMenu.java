package com.raytech.gerenciadortimes;

import android.webkit.JavascriptInterface;

public class LoadMenu {
  private MainActivity main;

  LoadMenu(MainActivity main) {
    this.main = main;
  }

  @JavascriptInterface
  public void loadMenu(final String url){
    main.runOnUiThread(new Runnable() {
      @Override
      public void run() {
        main.url = url;
        main.invalidateOptionsMenu();
      }
    });
  }

  @JavascriptInterface
  public void salvar(String lista, String id, String json){
    main.salvar(lista, id, json);
  }

  @JavascriptInterface
  public void buscar(String lista){
    main.carregarLista(lista);
  }

  @JavascriptInterface
  public void criarLista(String lista){
    main.criarLista(lista);
  }
}
