package pt.dezvezesdez.farmaciaserrano.activities;

import android.graphics.Paint;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.uncopt.android.widget.text.justify.JustifiedTextView;

import java.util.ArrayList;

import pt.dezvezesdez.farmaciaserrano.R;
import pt.dezvezesdez.farmaciaserrano.modelos.Categoria;
import pt.dezvezesdez.farmaciaserrano.modelos.Produto;
import pt.dezvezesdez.farmaciaserrano.modelos.SubCategoria;
import pt.dezvezesdez.farmaciaserrano.util.Helper;

public class ProdutoActivity extends AppCompatActivity {

    private final String ind = "Indisponível";
    private int stock;


    private Toolbar myToolbar;
    private ImageView iv_produto;
    private Spinner spinner_subcat;
    private TextView tv_titulo_produto, label_client, tv_price_produto, tv_price_sales_produto, tv_ref_produto, tv_stock_produto, desc_title, info_add, tx_categoria;
    private EditText quant;
    private Button add;
    private JustifiedTextView desc_produto, info_produto;
    private ImageLoader imageLoader;
    private FrameLayout spinner_container;
    private ScrollView sv;

    private Categoria cat;
    private SubCategoria subcat;
    private Produto prod;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_produto);
        TopBarConfigure();
        UiConfigure();
        GetDataFromPreviosActivity();
        InsertData();


    }

    @Override
    public void onResume() {
        super.onResume();
        //colorcar o scroll Para o topo
        sv.scrollTo(0, sv.getBottom());
        Runnable r = new Runnable() {
            @Override
            public void run() {
                sv.fullScroll(ScrollView.FOCUS_UP);
                sv.scrollTo(0, sv.getBottom());
            }
        };
        r.run();

        //fechar o keyborad
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);


    }

    private void InsertData() {

        boolean v_cat = Helper.ObjectParamsVer(cat);
        boolean v_subcat = Helper.ObjectParamsVer(subcat);


        //CATEGORIA
        GradientDrawable drawable = (GradientDrawable) spinner_container.getBackground();
        if (cat != null && v_cat) {
            //Helper.ImageLoaderWithHeight(cat.getImageCor(), subimage, imageLoader, 200);
            tx_categoria.setBackgroundColor(Integer.valueOf(cat.getColor()));
            drawable.setStroke((int) getResources().getDimension(R.dimen.stroke_with), Integer.valueOf(cat.getColor()));
            tx_categoria.setText(cat.getTitulo());
            desc_title.setTextColor(Integer.valueOf(cat.getColor()));
            info_add.setTextColor(Integer.valueOf(cat.getColor()));
        } else {
            //Helper.ImageLoaderWithHeight(Helper.PLACEHOLDER_IMAGE, subimage, imageLoader, 200);
            tx_categoria.setText("Outros");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                drawable.setStroke((int) getResources().getDimension(R.dimen.stroke_with), getResources().getColor(android.R.color.black, null));
                tx_categoria.setBackgroundColor(getResources().getColor(android.R.color.black, null));
                desc_title.setTextColor(getResources().getColor(android.R.color.black, null));
                info_add.setTextColor(getResources().getColor(android.R.color.black, null));
            } else {
                drawable.setStroke((int) getResources().getDimension(R.dimen.stroke_with), getResources().getColor(android.R.color.black));
                tx_categoria.setBackgroundColor(getResources().getColor(android.R.color.black));
                desc_title.setTextColor(getResources().getColor(android.R.color.black));
                info_add.setTextColor(getResources().getColor(android.R.color.black));
            }



        }


        //SUBTITULO
        ArrayList<String> items = new ArrayList<>();
        if (subcat != null && v_subcat)
            items.add(subcat.getTitle());
        else
            items.add("Sem Subcategoria");
        ArrayAdapter adapter = new ArrayAdapter<>(this, R.layout.my_simple_spinner_item, items);
        spinner_subcat.setAdapter(adapter);
        spinner_subcat.setEnabled(false);

        //PRODUTO
        Helper.ImageLoaderWithHeight(prod.getImage(), iv_produto, imageLoader, 200);

        if (prod.getStock() == 0 && prod.getStock_status().equalsIgnoreCase("outofstock")) {
            tv_stock_produto.setText("ESGOTADO");
            quant.setEnabled(false);
        } else {
            tv_stock_produto.setText("EM STOCK");
            quant.setEnabled(true);
        }

        stock = prod.getStock();

        if (prod.getManage_stock().equalsIgnoreCase("yes")) {
            if (prod.getBack_orders().equalsIgnoreCase("yes")) {
                //desconta-se stock mas não se informa o cliente
                label_client.setText("");
                stock = 99;
            } else if (prod.getBack_orders().equalsIgnoreCase("notify")) {
                //avisar o cliente
                if (stock > 0) {
                    label_client.setText("Em stock (pode ser encomendado sem stock)");

                } else {
                    label_client.setText("Disponível por encomenda a fornecedor");
                }
                stock = 99;

            } else if (prod.getBack_orders().equalsIgnoreCase("no")) {
                label_client.setText("");
                //só pode comprar com o stock existente

            }
        } else {
            if (stock >= 0) {
                //não diminui stock... ou seja quantidade 99
                stock = 99;
                label_client.setText("");
                tv_stock_produto.setText("EM STOCK");
                quant.setEnabled(true);
                //https://stackoverflow.com/questions/14212518/is-there-a-way-to-define-a-min-and-max-value-for-edittext-in-android
                //quant.setFilters(new InputFilter[]{new InputFilterMinMax(0, stock)});
            } else {
                label_client.setText("");
                tv_stock_produto.setText("ESGOTADO");
                quant.setEnabled(false);
            }
        }


        //adicionar texto do produto na view
        String name = prod.getName();
        if (name.equalsIgnoreCase("")) {
            tv_titulo_produto.setText(ind);
        } else {
            tv_titulo_produto.setText(name);
        }

        String ref = prod.getRef();
        if (ref.equalsIgnoreCase("")) {
            tv_ref_produto.setText(ind);
        } else {
            ref = "REF: " + prod.getRef();
            tv_ref_produto.setText(ref);
        }

        if (prod.getDescricao().equals("")) {
            desc_produto.setText(ind);
        } else {
            Spanned desc = CookHTML(prod.getDescricao());
            desc_produto.setText(desc);
        }

        if (prod.getWeight().equals("")) {
            info_produto.setText(ind);
        } else {
            Spanned info = CookHTML(prod.getWeight());
            info_produto.setText(info + " Kg");
        }

        String preco = prod.getRegular_price();
        String preco_sales = prod.getSales_price();

        if (preco_sales.equalsIgnoreCase("") && preco.equalsIgnoreCase("")) {
            tv_price_produto.setText("");
            tv_price_sales_produto.setText("");
        } else if (preco_sales.equalsIgnoreCase("")) {
            preco = Helper.FiltrarEDitTextToMoney(preco);
            preco = preco + " €";
            tv_price_produto.setText(preco);
            tv_price_sales_produto.setText("");
        } else if (!preco.equalsIgnoreCase("")) {
            preco = Helper.FiltrarEDitTextToMoney(preco);
            preco = preco + " €";
            tv_price_produto.setText(preco);
            tv_price_produto.setPaintFlags(tv_price_produto.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            preco_sales = preco_sales + " €";
            tv_price_sales_produto.setText(preco_sales);
        }

        //adicionar o produto ao carrinho com a quantidade selecionada
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int edit_quant = 0;
                try {
                    edit_quant = (Integer.parseInt(quant.getText().toString()));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }

                if (edit_quant > 0 && edit_quant <= stock) {

                    if (MainActivity.isProdutoNoCarrinho(ProdutoActivity.this, prod)) {
                        //actualizar
                        MainActivity.RemoverProduto(ProdutoActivity.this, MainActivity.geralMenu, prod);
                        MainActivity.AdicionarProduto(ProdutoActivity.this, MainActivity.geralMenu, prod, edit_quant);
                        Toast.makeText(ProdutoActivity.this, "Produto Actualizado", Toast.LENGTH_SHORT).show();

                    } else {
                        //adicionar
                        MainActivity.AdicionarProduto(ProdutoActivity.this, MainActivity.geralMenu, prod, edit_quant);
                        Toast.makeText(ProdutoActivity.this, "Produto Adicionado", Toast.LENGTH_SHORT).show();
                    }

                } else if (edit_quant == 0) {
                    //remover
                    if (MainActivity.isProdutoNoCarrinho(ProdutoActivity.this, prod)) {
                        MainActivity.RemoverProduto(ProdutoActivity.this, MainActivity.geralMenu, prod);
                        Toast.makeText(ProdutoActivity.this, "Produto removido", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    //não adicionar
                    Toast.makeText(ProdutoActivity.this, "Produto não adicionado ao carrinho\nVerifique a quantidade", Toast.LENGTH_SHORT).show();

                }

            }
        });

        VerificarOCarrinhoPelasQuantidadesDoProduto();


    }

    private void UiConfigure() {

        imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(this));

        iv_produto = (ImageView) findViewById(R.id.iv_produto);

        spinner_subcat = (Spinner) findViewById(R.id.spinner_subcat);

        spinner_container = (FrameLayout) findViewById(R.id.spinner_container);

        tv_titulo_produto = (TextView) findViewById(R.id.tv_titulo_produto);
        tv_price_produto = (TextView) findViewById(R.id.tv_price_produto);
        tv_price_sales_produto = (TextView) findViewById(R.id.tv_price_sales_produto);
        tv_ref_produto = (TextView) findViewById(R.id.tv_ref_produto);
        tv_stock_produto = (TextView) findViewById(R.id.tv_stock_produto);
        label_client = (TextView) findViewById(R.id.label_client);
        tx_categoria = (TextView) findViewById(R.id.tx_categoria);

        quant = (EditText) findViewById(R.id.quant);
        add = (Button) findViewById(R.id.add);

        desc_produto = (JustifiedTextView) findViewById(R.id.desc_produto);
        info_produto = (JustifiedTextView) findViewById(R.id.info_produto);

        desc_title = (TextView) findViewById(R.id.desc_title);
        info_add = (TextView) findViewById(R.id.info_add);

        sv = (ScrollView) findViewById(R.id.sv);


    }

    private void TopBarConfigure() {
        myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        myToolbar.setLogo(R.drawable.logo);//mudar a puta da imagem
    }

    private void GetDataFromPreviosActivity() {
        if (getIntent().getExtras() != null) {
            Bundle b = getIntent().getExtras();
            try {
                String name_cat = b.getString("name_cat");
                String image_cat = b.getString("image_cat");
                String imagecor_cat = b.getString("imagecor_cat");
                String color_cat = b.getString("color_cat");
                String id_cat = b.getString("id_cat");
                cat = new Categoria(name_cat, image_cat, imagecor_cat, color_cat, id_cat);

                String name_subcat = b.getString("name_subcat");
                subcat = new SubCategoria(name_subcat);

                String prod_id = b.getString("prod_id");
                String prod_ref = b.getString("prod_ref");
                String prod_name = b.getString("prod_name");
                String prod_regular_price = b.getString("prod_regular_price");
                String prod_price = b.getString("prod_price");
                String prod_sales_price = b.getString("prod_sales_price");
                String prod_image = b.getString("prod_image");
                String prod_desc = b.getString("prod_desc");
                String prod_weight = b.getString("prod_weight");
                int prod_stock = b.getInt("prod_stock");
                String prod_stock_status = b.getString("prod_stock_status");
                String prod_manage_stock = b.getString("prod_manage_stock");
                String prod_back_orders = b.getString("prod_back_orders");
                prod = new Produto(prod_id, prod_ref, prod_name, prod_regular_price, prod_price, prod_sales_price, prod_image, prod_desc, prod_weight, prod_stock, prod_stock_status, prod_manage_stock, prod_back_orders);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    private Spanned CookHTML(String aux) {
        Spanned res;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            res = Html.fromHtml(aux, Html.FROM_HTML_MODE_COMPACT);
        } else {
            res = Html.fromHtml(aux);
        }
        return res;
    }

    private void VerificarOCarrinhoPelasQuantidadesDoProduto() {

        boolean isProducto = MainActivity.isProdutoNoCarrinho(ProdutoActivity.this, prod);
        if (isProducto) {
            //então quanta quantidade
            Produto prod_ = MainActivity.getProduto(ProdutoActivity.this, prod);
            if (prod_ != null) {
                int quantidade = prod_.getQuantidade_compra();
                if (quantidade != 0) {
                    quant.setText("" + quantidade);
                    add.setText("ACTUALIZAR");
                }

            }

        }

    }

}
