package pt.dezvezesdez.farmaciaserrano.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import pt.dezvezesdez.farmaciaserrano.R;
import pt.dezvezesdez.farmaciaserrano.interfaces.MyApiEndpointInterface;
import pt.dezvezesdez.farmaciaserrano.modelos.Banner;
import pt.dezvezesdez.farmaciaserrano.modelos.Categoria;
import pt.dezvezesdez.farmaciaserrano.modelos.SubCategoria;
import pt.dezvezesdez.farmaciaserrano.util.Helper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.Observer;
import rx.Scheduler;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class FullscreenActivity extends AppCompatActivity {

    private final String url = "http://farmaciaserrano.pt/app/";
    private Retrofit retrofit;
    private MyApiEndpointInterface apiService;

    private boolean call_one = false;
    private boolean call_two = false;

    private Subscription sub_banners,sub_destaques;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);

        setContentView(R.layout.activity_fullscreen);


        PrepareApiCalls();
        GetDestaques();
        GetBanners();

    }

    private void PrepareApiCalls() {

        retrofit = new Retrofit.Builder()
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(url)
                .build();

        apiService = retrofit.create(MyApiEndpointInterface.class);
    }

    private void GetBanners() {

        Observable<List<Banner>> obs_banners = apiService.getBannersObs();

        sub_banners = obs_banners
                .subscribeOn(Schedulers.newThread())
                .observeOn(/*AndroidSchedulers.mainThread()*/ Schedulers.io())
                .subscribe(new Observer<List<Banner>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(List<Banner> banners) {
                        int count = banners.size();
                        for (int i = 0; i < count; i++) {
                            Banner ban = banners.get(i);
                            if (ban != null) {
                                Helper.saveObjectInSharedPref(getApplicationContext(), ban, "ban" + i, count);
                            }
                        }
                        call_one = true;
                        if (call_one && call_two) {
                            AfterGetData();
                        }

                    }
                });
    }


    private void GetDestaques() {

        Observable<List<Categoria>> obs_banners = apiService.getCategoriasObs();
        sub_destaques = obs_banners
                .subscribeOn(Schedulers.newThread())
                .observeOn(/*AndroidSchedulers.mainThread() */ Schedulers.io()) //      AndroidSchedulers.mainThread()
                .subscribe(new Observer<List<Categoria>>() {

                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("onError", "onFailure: " + e);
                        onBackPressed();

                    }

                    @Override
                    public void onNext(List<Categoria> categorias) {
                        int count = categorias.size();
                        for (int i = 0; i < count; i++) {
                            final Categoria cat = categorias.get(i);
                            if (cat != null) {
                                //guardar as categorias
                                Helper.saveObjectInSharedPref(getApplicationContext(), cat, "cat" + i, count);
                                //chamar cada cada subcategoria por cada categoria

                                Observable<List<SubCategoria>> calli = apiService.getSubcategoriasObs(cat.getId());
                                calli
                                        .subscribeOn(Schedulers.newThread())
                                        .observeOn(Schedulers.io())
                                        .subscribe(new Observer<List<SubCategoria>>() {
                                            @Override
                                            public void onCompleted() {

                                            }

                                            @Override
                                            public void onError(Throwable e) {

                                            }

                                            @Override
                                            public void onNext(List<SubCategoria> subCategorias) {
                                                int count = subCategorias.size();
                                                for (int i = 0; i < count; i++) {
                                                    SubCategoria sub = subCategorias.get(i);
                                                    if (sub != null) {
                                                        Helper.saveObjectInSharedPref(getApplicationContext(), sub, cat.getId() + "sub" + i, count);
                                                    }

                                                }
                                            }
                                        });


                            }

                        }

                        call_two = true;
                        if (call_one && call_two) {
                            AfterGetData();
                        }

                    }
                });
/*

        Call<List<Categoria>> call = apiService.getCategorias();
        call.enqueue(new Callback<List<Categoria>>() {
            @Override
            public void onResponse(Call<List<Categoria>> call, Response<List<Categoria>> response) {
                int statusCode = response.code();
                // ArrayList<Categoria> cats = new ArrayList<>();
                if (statusCode == 200) {
                    List<Categoria> categorias = response.body();
                    int count = categorias.size();
                    for (int i = 0; i < count; i++) {
                        final Categoria cat = categorias.get(i);
                        if (cat != null) {
                            //guardar as categorias
                            Helper.saveObjectInSharedPref(getApplicationContext(), cat, "cat" + i, count);
                            //chamar cada cada subcategoria por cada categoria
                            Call<List<SubCategoria>> calli = apiService.getSubcategorias(cat.getId());
                            calli.enqueue(new Callback<List<SubCategoria>>() {
                                @Override
                                public void onResponse(Call<List<SubCategoria>> call, Response<List<SubCategoria>> response) {
                                    if (response.code() == 200) {
                                        List<SubCategoria> subcategorias = response.body();
                                        int count = subcategorias.size();
                                        for (int i = 0; i < count; i++) {
                                            SubCategoria sub = subcategorias.get(i);
                                            if (sub != null) {
                                                Helper.saveObjectInSharedPref(getApplicationContext(), sub, cat.getId() + "sub" + i, count);
                                                Log.e("FullPepe", "onResponse: " + "Cap " + cat.getId() + " - " + sub.getTitle() + " SAVED ");
                                            }

                                        }
                                    }

                                }

                                @Override
                                public void onFailure(Call<List<SubCategoria>> call, Throwable t) {

                                }
                            });
                        }

                    }


                    call_two = true;
                    if (call_one && call_two) {
                        AfterGetData();
                    }

                }

            }

            @Override
            public void onFailure(Call<List<Categoria>> call, Throwable t) {
                Log.e("onFailure", "onFailure: " + t);

                //fechar aplicação
                onBackPressed();
            }
        });*/

    }


    private void AfterGetData() {
        StartNextActivity();
    }


    private void StartNextActivity() {
        Intent i = new Intent(FullscreenActivity.this, HomeActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Unsubscriber(sub_banners);
        Unsubscriber(sub_destaques);

    }

    private void Unsubscriber(Subscription s){
        if(s != null && s.isUnsubscribed()){
            s.unsubscribe();
        }
    }
}
