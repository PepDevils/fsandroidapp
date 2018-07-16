package pt.dezvezesdez.farmaciaserrano.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.graphics.drawable.GradientDrawable;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;

import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.mikepenz.actionitembadge.library.ActionItemBadge;
import com.mikepenz.actionitembadge.library.ActionItemBadgeAdder;
import com.mikepenz.actionitembadge.library.utils.BadgeStyle;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;


import pt.dezvezesdez.farmaciaserrano.R;
import pt.dezvezesdez.farmaciaserrano.fragments.AjudaFragment;
import pt.dezvezesdez.farmaciaserrano.fragments.CategoriasFragment;
import pt.dezvezesdez.farmaciaserrano.fragments.DetalhesContaFragment;
import pt.dezvezesdez.farmaciaserrano.fragments.ProdFragment;
import pt.dezvezesdez.farmaciaserrano.fragments.ProdutosFragment;
import pt.dezvezesdez.farmaciaserrano.fragments.SubCategoriasFragment;

import pt.dezvezesdez.farmaciaserrano.modelos.Categoria;
import pt.dezvezesdez.farmaciaserrano.modelos.Produto;
import pt.dezvezesdez.farmaciaserrano.modelos.SubCategoria;
import pt.dezvezesdez.farmaciaserrano.util.Helper;

import static pt.dezvezesdez.farmaciaserrano.fragments.ProdutosFragment.myProdutosRecyclerViewAdapter;


// https://android-arsenal.com/details/1/4836

public class MainActivity extends AppCompatActivity implements SubCategoriasFragment.OnSubcategoriaSelectedListener {

    public static boolean isTodos;
    private ArrayList<String> items;

    public static SearchView sv;

    public static ArrayList<SubCategoria> SubCategorias = new ArrayList<>();
    //public static ArrayList<Produto> Produtos = new ArrayList<>();
    public static ArrayList<Produto> Produtos;
    public static Spinner spinner_subcat;
    public static ImageView subimage;
    public static Menu geralMenu;
    public static String SubCategoriaSelector;
    public static int badgeComprasCount = 0;
    public static boolean pesquia_aberta = false;

    private Toolbar myToolbar;
    private FragmentTransaction fragmentTransaction;
    private Fragment newFragmentCategorias;
    private MenuItem searchitem;
    private static String categoria;
    private static ScrollView scrollView;
    private static FrameLayout spinner_container;

    public static boolean isLogIn;
    private ArrayAdapter<String> adapter;

    private ImageLoader imageLoader;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TopBarConfigure();

        scrollView = (ScrollView) findViewById(R.id.scrollView);
        subimage = (ImageView) findViewById(R.id.subimage);
        spinner_container = (FrameLayout) findViewById(R.id.spinner_container);

        Categoria cat;
        if (getIntent().getExtras() != null) {
            categoria = getIntent().getExtras().getString("categoria_nome");
            cat = HomeActivity.Categorias.get(getIntent().getExtras().getInt("idCategoria"));
            subimage.setBackgroundColor(getIntent().getExtras().getInt("categoria_color"));
            //  https://stackoverflow.com/questions/4772537/i-need-to-change-the-stroke-color-to-a-user-defined-color-nothing-to-do-with-th
            GradientDrawable drawable = (GradientDrawable) spinner_container.getBackground();
            drawable.setStroke((int) getResources().getDimension(R.dimen.stroke_with), getIntent().getExtras().getInt("categoria_color"));

        } else {
            categoria = HomeActivity.Categorias.get(0).getTitulo();
            cat = HomeActivity.Categorias.get(0);
        }

        imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(this));
        Helper.ImageLoaderWithHeight(cat.getImageCor(), subimage, imageLoader, 200);

        SubCategorias = new ArrayList<>();
        ArrayList<SubCategoria> s = Helper.getSubCategoriasInSharedPref(this.getApplicationContext(), cat.getId(), "sub");
        for (int i = 0; i < s.size(); i++) {
            SubCategorias.add(s.get(i));
        }

        Produtos = new ArrayList<>();
        for (int i = 0; i < SubCategorias.size(); i++) {
            SubCategoria sub = SubCategorias.get(i);
            if (sub.getProdutos() != null) {
                ArrayList<Produto> prods_sub = sub.getProdutos();
                for (int j = 0; j < prods_sub.size(); j++) {
                    Produto prod = prods_sub.get(j);
                    prod.setCategoria(categoria);
                    prod.setSubcategoria(sub.getTitle());
                    Produtos.add(prod);
                }

            }
        }

        badgeComprasCount = PreferenceManager.getDefaultSharedPreferences(this).getInt("badgeComprasCount", 0);


        SpinnerSubtitle();
        ProdutosFragmentInsert();
    }


    @Override
    protected void onResume() {
        super.onResume();

        ActualizarProdutosPorSubcategoria(this, spinner_subcat.getSelectedItem().toString());
        spinner_subcat.setEnabled(true);
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);

        if (getIntent().getExtras() != null) {
            Bundle b = getIntent().getExtras();
            String from = b.getString("from");
            if (from != null) {
                if (from.equalsIgnoreCase("action_pesquisa")) {
                    //para pesquisa
                    MenuItem searchMenuItem = geralMenu.findItem(R.id.action_pesquisa);
                    SearchView searchView = (SearchView) searchMenuItem.getActionView();
                    searchMenuItem.expandActionView();
                    MenuItemCompat.expandActionView(searchMenuItem);
                    searchView.onActionViewExpanded();
                    sv.setIconified(true);

                    getSupportFragmentManager().beginTransaction()
                            .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                            .add(R.id.fragment_for_scroll, ProdutosFragment.newInstance())
                            .commit();


                } else if (from.equalsIgnoreCase("action_settings")) {

                }
            }
        }


    }

    private void SpinnerSubtitle() {
        spinner_subcat = (Spinner) findViewById(R.id.spinner_subcat);
        items = new ArrayList<>();
        items.removeAll(items);
        items.clear();

        isTodos = true;

        for (int i = 0; i < SubCategorias.size(); i++) {
            SubCategoria subcat = SubCategorias.get(i);
            if (i == 0 && !subcat.getTitle().equalsIgnoreCase(categoria)) {
                items.add(categoria);
            }
            items.add(subcat.getTitle());
        }


        adapter = new ArrayAdapter<String>(this, R.layout.my_simple_spinner_item, items) {

            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                ((TextView) v).setGravity(Gravity.CENTER);
                return v;
            }

            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View v = super.getDropDownView(position, convertView, parent);
                ((TextView) v).setGravity(Gravity.CENTER);
                return v;

            }
        };
        adapter.setDropDownViewResource(R.layout.my_simple_spinner_item);
        spinner_subcat.setAdapter(adapter);

        spinner_subcat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i != 0) {
                    isTodos = false;
                    //updatedData(Produtos);
                } else {
                    isTodos = true;
                }

                adapter.notifyDataSetChanged();
                ActualizarProdutosPorSubcategoria(MainActivity.this, spinner_subcat.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_pesquisa:
                pesquia_aberta = true;
                getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                        .replace(R.id.fragment_for_scroll, ProdutosFragment.newInstance())
                        .commit();
                //updatedData(Produtos);
                spinner_container.setVisibility(View.GONE);
                subimage.setVisibility(View.GONE);
                return true;

            case R.id.action_pesquisa_avançada:
                Intent i = new Intent(this, PesquisaAvancadaActivity.class);
                startActivity(i);
                return true;

            case R.id.action_compra:
                Toast.makeText(this, "Não tem produtos no carrinho", Toast.LENGTH_LONG).show();
                /*Intent i = new Intent(this, CarrinhoActivity.class);
                startActivity(i);*/
                return true;

            case R.id.action_settings:
                getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                        .replace(R.id.fragment_for_scroll, AjudaFragment.newInstance())
                        .commit();
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }


    @Override
    public void onSubcategoriaSelected(int position) {
        ActualizarProdutosPorSubcategoria(this, spinner_subcat.getSelectedItem().toString());
    }

    @Override
    protected void onNewIntent(Intent intent) {

        setIntent(intent);
        super.onNewIntent(intent);

/*        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            sv.setQuery(query, false);

            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
        }*/
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.action_bar_menu, menu);
        //https://www.youtube.com/watch?v=9OWmnYPX1uc
        searchitem = menu.findItem(R.id.action_pesquisa);
        sv = (SearchView) MenuItemCompat.getActionView(searchitem);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        sv.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        sv.setQueryHint(getResources().getString(R.string.search_hint));
        MenuItemCompat.setOnActionExpandListener(searchitem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                pesquia_aberta = true;
                ToggleFragmentAndSpinner(MainActivity.this, menu);
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                pesquia_aberta = false;
                invalidateOptionsMenu();
                ToggleFragmentAndSpinner(MainActivity.this, menu);
                return true;
            }
        });


        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                spinner_subcat.setEnabled(true);

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);

                ArrayList<Produto> arrayListProd = new ArrayList<>();
                for (Produto prod : Produtos) {
                    if (!query.equalsIgnoreCase("")) {
                        if (Helper.containsIgnoreCase(prod.getName(), query)) {
                            arrayListProd.add(prod);
                        }
                    }
                }

                updatedData(arrayListProd);

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                ArrayList<Produto> arrayListProd = new ArrayList<>();
                for (Produto prod : Produtos) {
                    if (!newText.equalsIgnoreCase("")) {
                        if (Helper.containsIgnoreCase(prod.getName(), newText)) {
                            arrayListProd.add(prod);
                        }
                    }
                }

                updatedData(arrayListProd);

                return true;
            }
        });


        ActualizaGravaCompras(this, menu);

        geralMenu = menu;

        return super.onCreateOptionsMenu(menu);

    }


    //TODO: verificar o problema de acrescentar no menu em vez de substituir
    public static void ActualizaGravaCompras(final AppCompatActivity a, final Menu menu) {
        //you can add some logic (hide it if the count == 0)
        //actualizar o icon
        BadgeStyle bigStyle = ActionItemBadge.BadgeStyles.RED.getStyle();
        if (badgeComprasCount > 0) {
            new ActionItemBadgeAdder()
                    .act(a)
                    .menu(menu)
                    .title("Compras")
                    .itemDetails(0, R.id.action_compra, 1)
                    .showAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
                    .add(a.getBaseContext().getResources().getDrawable(android.R.drawable.ic_input_get), bigStyle, badgeComprasCount, new ActionItemBadge.ActionItemBadgeListener() {
                        @Override
                        public boolean onOptionsItemSelected(MenuItem menuitem) {
                            //TODO:Entrar para a actividade carrinho
                            //RemoverTodosOsProduto(a, menu);

                            Intent i = new Intent(a, CarrinhoActivity.class);
                            a.startActivity(i);


                            return false;
                        }
                    });


            //shared
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(a);
            SharedPreferences.Editor editor = sp.edit();

            //gravar numero nas sharedpreferences
            editor.putInt("badgeComprasCount", badgeComprasCount);
            editor.apply();

        } else {
            //ActionItemBadge.hide(menu.findItem(R.id.action_compra));

            menu.findItem(R.id.action_compra).setVisible(true);

            //limpar shared preferences
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(a);
            SharedPreferences.Editor editor = sp.edit();
            editor.clear().apply();
        }


    }


    //PRODUTOS // PRODUTOS    //PRODUTOS // PRODUTOS    //PRODUTOS // PRODUTOS    //PRODUTOS // PRODUTOS //PRODUTOS // PRODUTOS    //PRODUTOS // PRODUTOS    //PRODUTOS // PRODUTOS    //PRODUTOS // PRODUTOS
    public static Produto getProduto(AppCompatActivity a, Produto prod) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(a);
        Gson gson = new Gson();
        String json = sp.getString("produto" + prod.getId(), "");
        Produto re_produt = gson.fromJson(json, Produto.class);
        return re_produt;
    }

    public static void RemoverProduto(AppCompatActivity a, Menu menu, Produto velho_produto) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(a);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove("produto" + velho_produto.getId());
        editor.apply();
        ActualizaGravaCompras(a, menu);
        a.supportInvalidateOptionsMenu();
        MainActivity.badgeComprasCount--;
        if (MainActivity.badgeComprasCount < 0) {
            MainActivity.badgeComprasCount = 0;
        }
    }

    public static void RemoverTodosOsProduto(AppCompatActivity a, Menu menu) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(a);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        badgeComprasCount = 0;
        editor.putInt("badgeComprasCount", badgeComprasCount);
        editor.apply();
        ActualizaGravaCompras(a, menu);
        a.supportInvalidateOptionsMenu();
        //actualizar o adapter
        ProdutosFragment.myProdutosRecyclerViewAdapter.notifyDataSetChanged();

    }

    public static boolean isProdutoNoCarrinho(AppCompatActivity a, Produto novo_produto) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(a);
        String str_produto = sp.getString("produto" + novo_produto.getId(), null);
        if (str_produto == null)
            return false;
        return true;
    }

    public static void AdicionarProduto(AppCompatActivity a, Menu menu, Produto novo_produto, int quantidade) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(a);
        SharedPreferences.Editor editor = sp.edit();
        ActualizaGravaCompras(a, menu);
        //adicionar os produtos nas sharedespreferences
        Gson gson = new Gson();
        //String str_produtos = sp.getString("produto" + novo_produto.getId(), "");
        novo_produto.setQuantidade_compra(quantidade);
        String json = gson.toJson(novo_produto);
        editor.putString("produto" + novo_produto.getId(), json);
        editor.apply();
        a.supportInvalidateOptionsMenu();
        MainActivity.badgeComprasCount++;
    }
    //PRODUTOS // PRODUTOS    //PRODUTOS // PRODUTOS    //PRODUTOS // PRODUTOS    //PRODUTOS // PRODUTOS //PRODUTOS // PRODUTOS    //PRODUTOS // PRODUTOS    //PRODUTOS // PRODUTOS    //PRODUTOS // PRODUTOS


    public static void ToggleFragmentAndSpinner(AppCompatActivity a, Menu menu) {

        setItemsVisibility(menu);

        if (pesquia_aberta) {

            if (a.getSupportFragmentManager().findFragmentById(R.id.fragment_for_scroll) != null) {
                Fragment frag = a.getSupportFragmentManager().findFragmentById(R.id.fragment_for_scroll);
                if (frag instanceof ProdFragment) {

                       /* a.getSupportFragmentManager().beginTransaction()
                                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                                .remove(frag)
                                .commit();*/

                }
            }


            RelativeLayout.LayoutParams lpp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            lpp.addRule(RelativeLayout.BELOW, R.id.my_toolbar);
            scrollView.setLayoutParams(lpp);

            //esconder spinner
            spinner_container.setVisibility(View.GONE);
            a.findViewById(R.id.subimage).setVisibility(View.GONE);


        } else {

            a.getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                    .add(R.id.fragment_for_scroll, ProdutosFragment.newInstance())
                    .commit();

            RelativeLayout.LayoutParams lpp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            lpp.addRule(RelativeLayout.BELOW, R.id.spinner_container);
            scrollView.setLayoutParams(lpp);

            //mostrar spinner
            spinner_container.setVisibility(View.VISIBLE);
            a.findViewById(R.id.subimage).setVisibility(View.VISIBLE);


            //a.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_for_scroll, ProdutosFragment.newInstance()).commit();
            ActualizarProdutosPorSubcategoria(a, spinner_subcat.getSelectedItem().toString());

        }


    }

    public static void setItemsVisibility(final Menu menu) {
        try {
            for (int i = 0; i < menu.size(); ++i) {
                MenuItem item = menu.getItem(i);
                if (item.isVisible()) {
                    item.setVisible(false);
                } else {
                    item.setVisible(true);
                    //http://stackoverflow.com/questions/11422441/initially-hidden-menuitem-not-shown-when-setvisibletrue-is-called
                    //http://stackoverflow.com/questions/11422441/initially-hidden-menuitem-not-shown-when-setvisibletrue-is-called
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void ProdutosFragmentInsert() {
        if (spinner_subcat != null) {
            spinner_subcat.setEnabled(true);
        }
        subimage.setVisibility(View.VISIBLE);
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_for_scroll, ProdutosFragment.newInstance()).commit();
    }

    private void CloseProductFragament() {
        spinner_subcat.setEnabled(true);
        subimage.setVisibility(View.VISIBLE);
        getSupportFragmentManager().beginTransaction()
                // .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(R.id.fragment_for_scroll, ProdutosFragment.newInstance())
                .commit();
    }

    private static void ActualizarProdutosPorSubcategoria(AppCompatActivity a, String sub) {
        SubCategoriaSelector = sub;
        a.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_for_scroll, ProdutosFragment.newInstance()).commit();
    }


    public static void updatedData(ArrayList<Produto> itemsArrayList) {
        myProdutosRecyclerViewAdapter.clear();
        if (itemsArrayList != null) {
            for (Produto object : itemsArrayList) {
                myProdutosRecyclerViewAdapter.insert(object, myProdutosRecyclerViewAdapter.getItemCount());
            }
        }
        myProdutosRecyclerViewAdapter.notifyDataSetChanged();
    }


    private void TopBarConfigure() {
        myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        myToolbar.setLogo(R.drawable.logo);//mudar a puta da imagem
    }

    @Override
    public void onBackPressed() {

        Fragment f = getSupportFragmentManager().findFragmentById(R.id.fragment_for_scroll);

        if (f instanceof DetalhesContaFragment) {
            getSupportFragmentManager().beginTransaction()
                    // .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                    .remove(DetalhesContaFragment.newInstance())
                    .replace(R.id.fragment_for_scroll, AjudaFragment.newInstance())
                    .commit();
        } else if (f instanceof AjudaFragment) {
            if (findViewById(R.id.edt_email_criar) != null && findViewById(R.id.edt_email_criar).getVisibility() == View.VISIBLE) {
                AjudaFragment.AposCriarConta();
            } else {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_for_scroll, ProdutosFragment.newInstance())
                        .commit();
                spinner_subcat.setEnabled(true);
                subimage.setVisibility(View.VISIBLE);
            }

        } else if (f instanceof ProdFragment) {
            CloseProductFragament();
        } else if (f instanceof ProdutosFragment) {
            super.onBackPressed();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        imageLoader.destroy();
    }
}
