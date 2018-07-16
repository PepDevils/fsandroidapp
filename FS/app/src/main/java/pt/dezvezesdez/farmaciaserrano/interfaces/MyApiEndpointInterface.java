package pt.dezvezesdez.farmaciaserrano.interfaces;


import org.json.JSONObject;

import java.util.List;

import okhttp3.ResponseBody;
import pt.dezvezesdez.farmaciaserrano.modelos.Banner;
import pt.dezvezesdez.farmaciaserrano.modelos.Categoria;
import pt.dezvezesdez.farmaciaserrano.modelos.Ordenacao;
import pt.dezvezesdez.farmaciaserrano.modelos.Produto;
import pt.dezvezesdez.farmaciaserrano.modelos.SubCategoria;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by dezvezesdez on 08/06/2017.
 */

public interface MyApiEndpointInterface {

    @GET("mobile_api.php?func=get_categorias")
    Call<List<Categoria>> getCategorias();

    @GET("mobile_api.php?func=get_categorias")
    Observable<List<Categoria>> getCategoriasObs();

    @GET("mobile_api.php?func=get_banners")
    Call<List<Banner>> getBanners();

    @GET("mobile_api.php?func=get_banners")
    Observable<List<Banner>> getBannersObs();

    @GET("mobile_api.php?func=get_subcat")
    Call<List<SubCategoria>> getSubcategorias(@Query("cat_id") String id);
    // igual a ter por exemplo :                http://farmaciaserrano.pt/app/mobile_api.php?func=get_subcat&cat_id=100

    @GET("mobile_api.php?func=get_subcat")
    Observable<List<SubCategoria>> getSubcategoriasObs(@Query("cat_id") String id);

    @GET("mobile_api.php?func=ord")
    Observable<List<Ordenacao>> getOrdObs();

    @GET("mobile_api.php?func=maxp")
    Call<ResponseBody> getMax();

    @GET("mobile_api.php?func=pesquisar")
    Observable<List<Produto>> getProdutosPesquisa(@Query("nome") String nome,
                                                  @Query("cat") String categoria,
                                                  @Query("ord") String ordem,
                                                  @Query("min") String min,
                                                  @Query("max") String max,
                                                  @Query("fpi") int index_produto,
                                                  @Query("quant") int quantidade_paginas
                                                  );

}
