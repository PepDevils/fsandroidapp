package pt.dezvezesdez.farmaciaserrano.activities;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.daimajia.slider.library.Indicators.PagerIndicator;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;

import java.util.ArrayList;

import pt.dezvezesdez.farmaciaserrano.R;
import pt.dezvezesdez.farmaciaserrano.fragments.AjudaFragment;
import pt.dezvezesdez.farmaciaserrano.fragments.CategoriasFragment;
import pt.dezvezesdez.farmaciaserrano.modelos.Banner;
import pt.dezvezesdez.farmaciaserrano.modelos.Categoria;
import pt.dezvezesdez.farmaciaserrano.util.Helper;
import rx.Subscription;

public class HomeActivity extends AppCompatActivity {


    public static ArrayList<Categoria> Categorias = new ArrayList<>();
    public static ArrayList<Banner> Banners = new ArrayList<>();

    private FragmentTransaction fragmentTransaction;
    private Fragment newFragmentCategorias;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Helper.TranslucideSystemDef(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        CollectData();
        CategoriasFragmentInsert();
        SubirOScrollAOIniciar();
        ColocarOsBannersInAction();


/*        ImageButton ibt_menu = (ImageButton) findViewById(R.id.ibt_menu);
        ibt_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                        .add(R.id.lll, AjudaFragment.newInstance())
                        .commit();
            }
        });*/


        //em falta:

        /*

            d) na animação inicial fazer a transição com o logo da farmacia serrano.


        */




    }


    private void SubirOScrollAOIniciar() {
        final ScrollView sv = (ScrollView) findViewById(R.id.scrollview_home);
        //tentaviva inicial
        sv.scrollTo(0, sv.getTop());
        //tentaviva noutra thread
        sv.post(new Runnable() {
            public void run() {
                sv.fullScroll(sv.FOCUS_UP);
            }
        });

    }

    private void CollectData() {
        Categorias = Helper.getCategoriasInSharedPref(this, "cat");
        Banners = Helper.getBannersInSharedPref(this, "ban");

    }


    private void CategoriasFragmentInsert() {
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        newFragmentCategorias = CategoriasFragment.newInstance();
        fragmentTransaction.add(R.id.fragment_container, newFragmentCategorias);
        fragmentTransaction.commit();
    }

    private void ColocarOsBannersInAction() {
        SliderLayout sl = (SliderLayout) findViewById(R.id.slider);

        for (int i = 0; i < Banners.size(); i++) {
            DefaultSliderView textSliderView = new DefaultSliderView(this);
            final Banner ban = Banners.get(i);
            // initialize a SliderLayout
            textSliderView
                    .image(ban.getImage_link())
                    .setScaleType(BaseSliderView.ScaleType.Fit);
            //add your extra information
            textSliderView.bundle(new Bundle());
            textSliderView.getBundle()
                    .putString("extra", ban.getImage_link());


            textSliderView.setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
                @Override
                public void onSliderClick(BaseSliderView slider) {
                    //todo: ir a api e quando enviar o banner tb enviar os parametros de pesquisa
                    Toast.makeText(HomeActivity.this, "ver comentario todo", Toast.LENGTH_SHORT).show();
                    //PesquisaAvancada(ban.getPesquisa_nome(), ban.getPesquisa_categoria(), ban.getPesquisa_ordenacao(), ban.getPesquisa_min(), ban.getPesquisa_max(), ban.getPesquisa_index(), ban.getPesquisa_quant())
                }
            });

            sl.addSlider(textSliderView);
        }
        sl.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        sl.setDuration(4000);
        sl.startAutoCycle();

        sl.setIndicatorVisibility(PagerIndicator.IndicatorVisibility.Invisible);
        sl.getPagerIndicator().setEnabled(true);




        //actualizar a altura do banner com novo ratio
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int screenWidth = displaymetrics.widthPixels;
        double ration = 16 / 6.5;
        sl.setLayoutParams(new LinearLayout.LayoutParams(screenWidth, (int) (screenWidth / ration)));

        if (Banners.size() == 1) {
            sl.stopAutoCycle();
            sl.setPresetTransformer(SliderLayout.Transformer.Fade);
        }

    }

    private void PesquisaAvancada(String name_prod, String cat_prod, String ordenacao_id, String min, String max, int index, int quant) {
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

}
