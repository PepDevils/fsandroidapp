package pt.dezvezesdez.farmaciaserrano.fragments;

import android.animation.Animator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.OkHttpClient.Builder;


import pt.dezvezesdez.farmaciaserrano.R;
import pt.dezvezesdez.farmaciaserrano.activities.FullscreenActivity;
import pt.dezvezesdez.farmaciaserrano.activities.HomeActivity;
import pt.dezvezesdez.farmaciaserrano.activities.MainActivity;
import pt.dezvezesdez.farmaciaserrano.adapters.CategoriasAdapters;
import pt.dezvezesdez.farmaciaserrano.interfaces.Interface_ReclycerViewOnTounchHack;
import pt.dezvezesdez.farmaciaserrano.interfaces.MyApiEndpointInterface;
import pt.dezvezesdez.farmaciaserrano.modelos.Categoria;
import pt.dezvezesdez.farmaciaserrano.modelos.SubCategoria;
import pt.dezvezesdez.farmaciaserrano.util.CachingControlInterceptor;
import pt.dezvezesdez.farmaciaserrano.util.Helper;
import pt.dezvezesdez.farmaciaserrano.view.RecyclerViewTounchListener;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CategoriasFragment extends Fragment implements Interface_ReclycerViewOnTounchHack {

    private ArrayList<Categoria> mycategorias = new ArrayList<>();
    private Activity a;
    private final String url = "http://farmaciaserrano.pt/app/";
    private RecyclerView rv;
    private CategoriasAdapters cadapetr;

    private ImageLoader imageLoader;

    public CategoriasFragment() {
    }

    public static CategoriasFragment newInstance() {
        return new CategoriasFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        a = getActivity();
        mycategorias = HomeActivity.Categorias;

    }

    private View v;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.fragment_categorias, container, false);

        imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(a));


        // LinearLayoutManager layoutManager = new LinearLayoutManager(a, LinearLayoutManager.HORIZONTAL, false);
        GridLayoutManager layoutManager = new GridLayoutManager(a, 2);

        rv = (RecyclerView) v.findViewById(R.id.my_recycler_view);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(layoutManager);

        if (mycategorias == null) {
            mycategorias = Helper.getCategoriasInSharedPref(getActivity(), "cat");
        }

        // criar subcategoria adapter
        cadapetr = new CategoriasAdapters(a, mycategorias);

        rv.setAdapter(cadapetr);
        rv.addOnItemTouchListener(new RecyclerViewTounchListener(a, rv, this));


        return v;
    }

    @Override
    public void onItemAdapterClickListener(View view, int position) {
        //Toast.makeText(a, "onItemAdapterClickListener " + position, Toast.LENGTH_SHORT).show();
        //abrir a main activity com os detais da categoria

        imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(a));

        AlphaViewTounchSkill(view, position);


    }


    private void AlphaViewTounchSkill(final View view, final int position) {
        view.animate().setDuration(250).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

                getActivity().findViewById(R.id.shades).setVisibility(View.VISIBLE);

                Helper.DoorImageLoader(mycategorias.get(position).getImageCor(),(ImageView) (view.findViewById(R.id.imageC)),imageLoader);
                // Helper.ImageLoaderWithHeight(cat.getImageCor(),subimage,imageLoader, 200);
                mycategorias.get(position).getImageCor();
                v.setClickable(false);

                //todo: mudar o codigo para verificar se a subcategoria ja foi carregada em caso de sucesso entrar directamente sem carregar

                ArrayList<SubCategoria> asub = Helper.getSubCategoriasInSharedPref(getActivity().getApplicationContext(), mycategorias.get(position).getId(),"sub");
                if(asub != null && asub.size() > 0 ){
                    Intent i = new Intent(a, MainActivity.class);
                    i.putExtra("idCategoria", position);
                    i.putExtra("categoria_nome", mycategorias.get(position).getTitulo());
                    i.putExtra("categoria_color", Color.parseColor(mycategorias.get(position).getColor()));
                    i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(i);
                    Helper.DoorImageLoader(mycategorias.get(position).getImage(),(ImageView) (view.findViewById(R.id.imageC)),imageLoader);
                    getActivity().findViewById(R.id.shades).setVisibility(View.GONE);
                }else{
                    GetSubcategoriasFromChoosenCategorie(mycategorias.get(position), position, view);
                }

            }

            @Override
            public void onAnimationEnd(Animator animator) {


            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        }).start();
    }

    private void GetSubcategoriasFromChoosenCategorie(final Categoria categoria, final int position, final View view) {

        //https://stackoverflow.com/questions/34311058/http-caching-with-retrofit-2-0-x

        long SIZE_OF_CACHE = 10 * 1024 * 1024; // 10 MiB
        Cache cache = new Cache(new File(getActivity().getCacheDir(), "http"), SIZE_OF_CACHE);

        Builder b = new Builder();
        b.readTimeout(5, TimeUnit.MINUTES);
        b.writeTimeout(5, TimeUnit.MINUTES);

        OkHttpClient client = b
                .cache(cache)
                .addNetworkInterceptor(new CachingControlInterceptor())
                .build();


        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(url)
                .client(client)
                .build();

        MyApiEndpointInterface apiService = retrofit.create(MyApiEndpointInterface.class);
        Call<List<SubCategoria>> call = apiService.getSubcategorias(categoria.getId());
        call.enqueue(new Callback<List<SubCategoria>>() {
            @Override
            public void onResponse(Call<List<SubCategoria>> call, Response<List<SubCategoria>> response) {
                int statusCode = response.code();


                //todo: mudar o codigo para verificar se a subcategoria ja foi carregada em caso de sucesso entrar directamente sem carregar

                if (statusCode == 200) {
                    List<SubCategoria> subcategorias = response.body();
                    int count = subcategorias.size();
                    for (int i = 0; i < count; i++) {
                        SubCategoria sub = subcategorias.get(i);
                        if (sub != null) {
                            Helper.saveObjectInSharedPref(getActivity().getApplicationContext(), sub, categoria.getId() + "sub" + i, count);
                        }

                    }

                    getActivity().findViewById(R.id.shades).setVisibility(View.GONE);

                    Intent i = new Intent(a, MainActivity.class);
                    i.putExtra("idCategoria", position);
                    i.putExtra("categoria_nome", mycategorias.get(position).getTitulo());
                    i.putExtra("categoria_color", Color.parseColor(mycategorias.get(position).getColor()));
                    i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(i);
                    Helper.DoorImageLoader(mycategorias.get(position).getImage(),(ImageView) (view.findViewById(R.id.imageC)),imageLoader);


                }
            }

            @Override
            public void onFailure(Call<List<SubCategoria>> call, Throwable t) {
                Log.e("onFailure", "onFailure: " + t);
                Toast.makeText(getActivity(), "Ocorreu um erro", Toast.LENGTH_SHORT).show();
                Helper.DoorImageLoader(mycategorias.get(position).getImage(),(ImageView) (view.findViewById(R.id.imageC)),imageLoader);
                getActivity().findViewById(R.id.shades).setVisibility(View.GONE);
            }
        });


    }


}
