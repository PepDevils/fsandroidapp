package pt.dezvezesdez.farmaciaserrano.activities;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.UserManager;
import android.support.annotation.LayoutRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.ListPopupWindow;
import android.support.v7.widget.MenuPopupWindow;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewStub;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.appyvet.rangebar.RangeBar;
import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import pt.dezvezesdez.farmaciaserrano.R;
import pt.dezvezesdez.farmaciaserrano.adapters.MyProdutosRecyclerViewAdapter;
import pt.dezvezesdez.farmaciaserrano.fragments.AjudaFragment;
import pt.dezvezesdez.farmaciaserrano.fragments.ProdFragment;
import pt.dezvezesdez.farmaciaserrano.fragments.ProdutosFragment;
import pt.dezvezesdez.farmaciaserrano.interfaces.Interface_ReclycerViewOnTounchHack;
import pt.dezvezesdez.farmaciaserrano.interfaces.MyApiEndpointInterface;
import pt.dezvezesdez.farmaciaserrano.modelos.Banner;
import pt.dezvezesdez.farmaciaserrano.modelos.Categoria;
import pt.dezvezesdez.farmaciaserrano.modelos.Ordenacao;
import pt.dezvezesdez.farmaciaserrano.modelos.Produto;
import pt.dezvezesdez.farmaciaserrano.util.CachingControlInterceptor;
import pt.dezvezesdez.farmaciaserrano.util.Helper;
import pt.dezvezesdez.farmaciaserrano.view.CustomSpinner;
import pt.dezvezesdez.farmaciaserrano.view.RecyclerViewTounchListener;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class PesquisaAvancadaActivity extends AppCompatActivity {

    private Toolbar myToolbar;
    private SearchView sv_prod_name;
    private CustomSpinner sp_ordenacao, sp_categorias;
    private TextView tx_price_seek;
    private Button bt_pesquisar;

    private int min, max;
    private ArrayList<Categoria> categorias;

    private String order_prod = "todos";
    private String name_prod = "todos";
    private String cat_prod = "todos";

    private ArrayList<String> ordenacoes = new ArrayList<>();
    private ArrayList<Integer> ordenacoes_index = new ArrayList<>();
    public static ArrayList<Ordenacao> ordenacaos = new ArrayList<>();

    private RangeBar rangebar;

    private Retrofit retrofit;
    private final String url = "http://farmaciaserrano.pt/app/";
    private MyApiEndpointInterface apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pesquisa_avancada);
        TopBarConfigure();

        tx_price_seek = (TextView) findViewById(R.id.tx_price_seek);
        bt_pesquisar = (Button) findViewById(R.id.bt_pesquisar);
        sv_prod_name = (SearchView) findViewById(R.id.sv_prod_name);

        PrepareApiCalls();
        GetOrdenaçao();


        rangebar = (RangeBar) findViewById(R.id.rangebar);
        rangebar.setBarColor(getResources().getColor(R.color.fs_rosa_dark));
        rangebar.setPinColor(getResources().getColor(R.color.fs_rosa_dark));
        rangebar.setSelectorColor(getResources().getColor(R.color.fs_rosa_dark));
        rangebar.setTickColor(getResources().getColor(android.R.color.transparent));
        rangebar.setConnectingLineColor(getResources().getColor(android.R.color.darker_gray));

        rangebar.setOnRangeBarChangeListener(new RangeBar.OnRangeBarChangeListener() {
            @Override
            public void onRangeChangeListener(RangeBar rangeBar, int leftPinIndex, int rightPinIndex, String leftPinValue, String rightPinValue) {
                String aux = "PREÇO: €" + leftPinValue + " - €" + rightPinValue;
                tx_price_seek.setText(aux);

                max = Integer.valueOf(rightPinValue);
                min = Integer.valueOf(leftPinIndex);
            }
        });

        //todo:mandar vir o preço maximo e minimo
        rangebar.setTickStart(0);
        rangebar.setTickEnd(1000);
        GetMax();


        sp_ordenacao = (CustomSpinner) findViewById(R.id.sp_ordenacao);
        sp_categorias = (CustomSpinner) findViewById(R.id.sp_categorias);

        categorias = Helper.getCategoriasInSharedPref(this, "cat");
        ArrayList<String> cats_names = new ArrayList<>();
        for (int i = 0; i < categorias.size(); i++) {
            cats_names.add(categorias.get(i).getTitulo());
        }

        SpinnerRoll(sp_categorias, cats_names);


        bt_pesquisar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String ordenacao = (String) sp_ordenacao.getSelectedItem();
                    String categoria = (String) sp_categorias.getSelectedItem();
                    String nome = sv_prod_name.getQuery().toString();

                    if (ordenacao == null) {
                        order_prod = "todos";
                    }else{
                        order_prod = ordenacao;
                    }

                    if (categoria == null) {
                        cat_prod = "todos";
                    }else{
                        cat_prod = categoria;
                    }


                    if (nome.equalsIgnoreCase("")) {
                        name_prod = "todos";
                    } else {
                        name_prod = nome;
                    }

                    order_prod = ConversorOrdenacao(order_prod, false);
                    cat_prod = ConversorCategoria(cat_prod);

                    ToResultados(name_prod, cat_prod, order_prod, "" + min, "" + max, 0, 20);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_pesquisa:
                MainActivity.pesquia_aberta = true;
                Intent ii = new Intent(this, MainActivity.class);
                ii.putExtra("from","action_pesquisa");
                startActivity(ii);
                return false;

            case R.id.action_pesquisa_avançada:
                return true;

            case R.id.action_compra:
                Toast.makeText(this, "Não tem produtos no carrinho", Toast.LENGTH_LONG).show();

                /*Intent i = new Intent(this, CarrinhoActivity.class);
                startActivity(i);*/

                return true;

            case R.id.action_settings:

                Intent iii = new Intent(this, MainActivity.class);
                iii.putExtra("from","action_settings");
                startActivity(iii);

      /*          getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                        .replace(R.id.fragment_for_scroll, AjudaFragment.newInstance())
                        .commit();*/

                return false;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_bar_menu2, menu);
        return super.onCreateOptionsMenu(menu);
    }



    private void ToResultados(String name_prod, String cat_prod, String ordenacao_id, String min, String max, int index, int quant) {
        Intent itent = new Intent(this, ResultadoActivity.class);
        itent.putExtra("name", name_prod);
        itent.putExtra("categoria", cat_prod);
        itent.putExtra("ordenacao", ordenacao_id);
        itent.putExtra("min", min);
        itent.putExtra("max", max);
        itent.putExtra("index", index);
        itent.putExtra("quant", quant);
        startActivity(itent);
    }

    private void TopBarConfigure() {
        myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        myToolbar.setLogo(R.drawable.logo);//todo:mudar a puta da imagem
        ((TextView) findViewById(R.id.title_sub_bar)).setText("Pesquisa Avançada");
    }

    private void SpinnerRoll(final Spinner sp, ArrayList<String> arr) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.my_simple_spinner_item_2, arr);
        adapter.setDropDownViewResource(R.layout.my_simple_spinner_item);
        sp.setAdapter(adapter);

        sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //quando se escolhe um item o teclado fecha
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void PrepareApiCalls() {

        long SIZE_OF_CACHE = 10 * 1024 * 1024; // 10 MiB
        Cache cache = new Cache(new File(PesquisaAvancadaActivity.this.getCacheDir(), "http"), SIZE_OF_CACHE);

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

    private void GetOrdenaçao() {

        bt_pesquisar.setEnabled(false);
        //todo:colocar um progressbar

        Observable<List<Ordenacao>> obs_ords = apiService.getOrdObs();
        obs_ords
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Ordenacao>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(PesquisaAvancadaActivity.this, "e: " + e, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNext(List<Ordenacao> ordenacaos_) {

                        ordenacaos = (ArrayList<Ordenacao>) ordenacaos_;
                        ordenacoes = new ArrayList<>();
                        ordenacoes_index = new ArrayList<>();

                        for (int i = 0; i < ordenacaos.size(); i++) {
                            Ordenacao ord = ordenacaos.get(i);
                            String name = ord.getName();
                            String s = ord.getSwitch_();
                            ordenacoes.add(name);
                            ordenacoes_index.add(Integer.valueOf(s));

                        }

                        SpinnerRoll(sp_ordenacao, ordenacoes);

                        bt_pesquisar.setEnabled(true);
                        //todo:retirar um progressbar


                    }


                });

    }

    private void GetMax() {

        Call<ResponseBody> callback = apiService.getMax();
        callback.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 200) {
                    String jsonData = null;
                    try {
                        jsonData = response.body().string();
                        JSONObject Jobject = new JSONObject(jsonData);
                        String max_ = Jobject.getString("max");

                        int xxx = Integer.parseInt(max_);
                        rangebar.setTickEnd(xxx);

                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });


    }

    private String ConversorOrdenacao(String compare, boolean toName) {

        if (!toName) {
            //return o switch
            for (int i = 0; i < ordenacaos.size(); i++) {
                Ordenacao ord = ordenacaos.get(i);
                if (ord.getName().equalsIgnoreCase(compare)) {
                    return ord.getSwitch_();
                }
            }
            return "todos";
        } else {
            //return o name
            for (int i = 0; i < ordenacaos.size(); i++) {
                Ordenacao ord = ordenacaos.get(i);
                if (ord.getName().equalsIgnoreCase(compare)) {
                    return ord.getSwitch_();
                }
            }
            return "todos";
        }
    }
    private String ConversorCategoria(String cat) {
        for (int i = 0; i < categorias.size(); i++) {
            Categoria c = categorias.get(i);
            if(c.getTitulo().equalsIgnoreCase(cat)){
                return c.getId();
            }
        }
        return "todos";
    }

    @Override
    protected void onResume() {
        super.onResume();

        //vai buscar a view que está em foco, e fecha o keyboard associado a mesma
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if ((keyCode == KeyEvent.KEYCODE_BACK))
        {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

/*        Intent intt = new Intent(this, HomeActivity.class);
        intt.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intt);*/

/*        try {
            ActivityInfo[] list = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_ACTIVITIES).activities;
            for(int i = 0;i< list.length;i++)
            {
                if(list[i].name.equalsIgnoreCase(MainActivity.class.getName())){
                    Intent intt = new Intent(this, MainActivity.class);
                    intt.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(intt);
                    break;

                }
            }
        }

        catch (Exception e1) {
            e1.printStackTrace();
        }*/

    }
}
