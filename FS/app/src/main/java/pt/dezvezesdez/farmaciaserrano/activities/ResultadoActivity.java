package pt.dezvezesdez.farmaciaserrano.activities;

import android.os.Handler;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.EOFException;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import pt.dezvezesdez.farmaciaserrano.R;
import pt.dezvezesdez.farmaciaserrano.adapters.MyProdutosRecyclerViewAdapter;
import pt.dezvezesdez.farmaciaserrano.comparators.ComparatorNameCrescente;
import pt.dezvezesdez.farmaciaserrano.comparators.ComparatorNameDecrescente;
import pt.dezvezesdez.farmaciaserrano.comparators.ComparatorPriceCrescente;
import pt.dezvezesdez.farmaciaserrano.comparators.ComparatorPriceDecrescente;
import pt.dezvezesdez.farmaciaserrano.interfaces.Interface_ReclycerViewOnTounchHack;
import pt.dezvezesdez.farmaciaserrano.interfaces.MyApiEndpointInterface;
import pt.dezvezesdez.farmaciaserrano.modelos.Ordenacao;
import pt.dezvezesdez.farmaciaserrano.modelos.Produto;
import pt.dezvezesdez.farmaciaserrano.util.CachingControlInterceptor;
import pt.dezvezesdez.farmaciaserrano.view.RecyclerViewTounchListener;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ResultadoActivity extends AppCompatActivity implements Interface_ReclycerViewOnTounchHack {

    private Retrofit retrofit;
    private final String url = "http://farmaciaserrano.pt/app/";
    private MyApiEndpointInterface apiService;

    private RecyclerView rv;
    private MyProdutosRecyclerViewAdapter myProdutosRecyclerViewAdapter;

    private Toolbar myToolbar;
    private ProgressBar progress_bar;
    private TextView tv_noproduct;

    private ArrayList<Produto> todos_os_produtos = new ArrayList<>();
    private boolean isFirstDownLoaded = true;
    private Subscription subscrition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resultado);
        TopBarConfigure();
        PrepareApiCalls();


        progress_bar = (ProgressBar) findViewById(R.id.progress_bar);

        tv_noproduct = (TextView) findViewById(R.id.tv_noproduct);

        rv = (RecyclerView) findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(this));

        if (getIntent().getExtras() != null) {
            String name = getIntent().getExtras().getString("name");
            String categoria = getIntent().getExtras().getString("categoria");
            String ordenacao = getIntent().getExtras().getString("ordenacao");
            String min = getIntent().getExtras().getString("min");
            String max = getIntent().getExtras().getString("max");
            int index = getIntent().getExtras().getInt("index");
            int quant = getIntent().getExtras().getInt("quant");
            PesquisaAvancada(name, categoria, ordenacao, min, max, index, quant);

        }


    }

    private void TriggerMessageNoProducts() {
        if (todos_os_produtos.size() == 0) {
            tv_noproduct.setVisibility(View.VISIBLE);
            TimeToGoBack();
        } else {
            tv_noproduct.setVisibility(View.GONE);
        }
    }

    private void PesquisaAvancada(final String nome, final String categoria, final String ordenacao, final String min, final String max, final int index, final int quant) {
        Observable<List<Produto>> obs_pesquisa = apiService.getProdutosPesquisa(nome, categoria, ordenacao, min, max, index, quant);
        subscrition = obs_pesquisa
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Produto>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("onError", "onError: " + e);
                        if (e.fillInStackTrace() instanceof EOFException) {
                            progress_bar.setVisibility(View.GONE);
                            TriggerMessageNoProducts();
                        }
                    }

                    @Override
                    public void onNext(List<Produto> produtos) {
                        if (produtos.size() != 0) {
                            progress_bar.setVisibility(View.GONE);
                            if (isFirstDownLoaded) {
                                int incr_index = index + quant;
                                PesquisaAvancada(nome, categoria, ordenacao, min, max, incr_index, quant);

                                for (int i = 0; i < produtos.size(); i++) {
                                    todos_os_produtos.add(produtos.get(i));
                                }

                                myProdutosRecyclerViewAdapter = new MyProdutosRecyclerViewAdapter(ResultadoActivity.this, todos_os_produtos);
                                myProdutosRecyclerViewAdapter.setHasStableIds(false);
                                rv.setAdapter(myProdutosRecyclerViewAdapter);
                                rv.addOnItemTouchListener(new RecyclerViewTounchListener(ResultadoActivity.this, rv, ResultadoActivity.this));
                                isFirstDownLoaded = false;
                            } else {

                                int incr_index = index + quant;
                                PesquisaAvancada(nome, categoria, ordenacao, min, max, incr_index, quant);

                                for (int i = 0; i < produtos.size(); i++) {
                                    todos_os_produtos.add(produtos.get(i));
                                    myProdutosRecyclerViewAdapter.notifyDataSetChanged();
                                }
                            }

                            //ORDENAÇÃO DA LISTA DE PRODUTOS
                            if (!ordenacao.equalsIgnoreCase("todos")) {
                                Ordenacao o = null;
                                for (Ordenacao ord : PesquisaAvancadaActivity.ordenacaos) {
                                    if (Integer.valueOf(ord.getSwitch_()) == Integer.valueOf(ordenacao)) {
                                        o = ord;
                                        break;
                                    }
                                }

                                if (o.getName().equalsIgnoreCase("Preço Crescente")) {
                                    Collections.sort(todos_os_produtos, new ComparatorPriceCrescente());
                                } else if (o.getName().equalsIgnoreCase("Preço Decrescente")) {
                                    Collections.sort(todos_os_produtos, new ComparatorPriceDecrescente());
                                } else {
                                     /* Collections.sort(todos_os_produtos, new ComparatorNameCrescente());
                                    Collections.sort(todos_os_produtos, new ComparatorNameDecrescente());*/
                                }
                            }


                        } else {
                            int incr_index = index + quant;
                            PesquisaAvancada(nome, categoria, ordenacao, min, max, incr_index, quant);
                            if (myProdutosRecyclerViewAdapter != null) {
                                myProdutosRecyclerViewAdapter.notifyDataSetChanged();
                            }
                        }

                        //TriggerMessageNoProducts();
                    }
                });


    }

    private void TimeToGoBack() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                try {
                    onBackPressed();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }, 3000);
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        //super.onSaveInstanceState(outState, outPersistentState);
    }

    private void PrepareApiCalls() {

        long SIZE_OF_CACHE = 10 * 1024 * 1024; // 10 MiB
        Cache cache = new Cache(new File(ResultadoActivity.this.getCacheDir(), "http"), SIZE_OF_CACHE);

        OkHttpClient.Builder b = new OkHttpClient.Builder();
        b.readTimeout(5, TimeUnit.MINUTES);
        b.writeTimeout(5, TimeUnit.MINUTES);

        OkHttpClient client = b
                .cache(cache)
                .addNetworkInterceptor(new CachingControlInterceptor())
                .build();


        retrofit = new Retrofit.Builder()
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .baseUrl(url)
                .build();

        apiService = retrofit.create(MyApiEndpointInterface.class);
    }

    private void TopBarConfigure() {
        myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        myToolbar.setLogo(R.drawable.logo);//todo:mudar a puta da imagem
        ((TextView) findViewById(R.id.title_sub_bar)).setText("Resultado");
    }

    @Override
    public void onItemAdapterClickListener(View view, int position) {
        //todo:  colocar aqui o click no produto
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (subscrition != null && !subscrition.isUnsubscribed()) {
            subscrition.unsubscribe();
            subscrition = null;
        }

    }
}
