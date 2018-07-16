package pt.dezvezesdez.farmaciaserrano.fragments;

import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.uncopt.android.widget.text.justify.JustifiedTextView;

import pt.dezvezesdez.farmaciaserrano.R;
import pt.dezvezesdez.farmaciaserrano.activities.MainActivity;
import pt.dezvezesdez.farmaciaserrano.modelos.Produto;
import pt.dezvezesdez.farmaciaserrano.util.Helper;

public class ProdFragment extends Fragment {

    private View v;
    private static Produto produto;
    private int stock;
    private String stock_status, manage_sctoks, back_orders;

    private final String ind = "Indisponível";

    private ImageView iv_produto;
    private TextView tv_titulo_produto, label_client, tv_price_produto, tv_price_sales_produto, tv_ref_produto, tv_stock_produto, desc_title, info_add;
    private EditText quant;
    private Button add;
    private JustifiedTextView desc_produto, info_produto;
    private ImageLoader imageLoader;
    private boolean pode_descontar_sctock = false;

    public ProdFragment() {
        // Required empty public constructor

    }


    public static ProdFragment newInstance(Produto prod) {
        ProdFragment fragment = new ProdFragment();
        produto = prod;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((AppCompatActivity) getActivity()).supportInvalidateOptionsMenu();

        imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(getActivity()));

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_prod, container, false);

        iv_produto = (ImageView) v.findViewById(R.id.iv_produto);

        tv_titulo_produto = (TextView) v.findViewById(R.id.tv_titulo_produto);
        tv_price_produto = (TextView) v.findViewById(R.id.tv_price_produto);
        tv_price_sales_produto = (TextView) v.findViewById(R.id.tv_price_sales_produto);
        tv_ref_produto = (TextView) v.findViewById(R.id.tv_ref_produto);
        tv_stock_produto = (TextView) v.findViewById(R.id.tv_stock_produto);
        label_client = (TextView) v.findViewById(R.id.label_client);

        quant = (EditText) v.findViewById(R.id.quant);
        add = (Button) v.findViewById(R.id.add);

        desc_produto = (JustifiedTextView) v.findViewById(R.id.desc_produto);
        info_produto = (JustifiedTextView) v.findViewById(R.id.info_produto);

        desc_title = (TextView) v.findViewById(R.id.desc_title);
        info_add = (TextView) v.findViewById(R.id.info_add);

        //imagem
        Helper.ImageLoaderWithHeight(produto.getImage(), iv_produto, imageLoader, 0);

        //mudar a cor do texto igual á categoria
        desc_title.setTextColor(getActivity().getIntent().getExtras().getInt("categoria_color"));
        info_add.setTextColor(getActivity().getIntent().getExtras().getInt("categoria_color"));

        //verificar a quantidade de stock que o produto tem e colocar esta como limite de quantidade e tb avisar se tem "em stock/indisponivel"
        stock = produto.getStock();
        stock_status = produto.getStock_status();
        manage_sctoks = produto.getManage_stock();
        back_orders = produto.getBack_orders();

        //todo:pode_descontar_sctockpode_descontar_sctockpode_descontar_sctockpode_descontar_sctock pode_descontar_sctock

        if (stock == 0 && produto.getStock_status().equalsIgnoreCase("outofstock")) {
            tv_stock_produto.setText("ESGOTADO");
            quant.setEnabled(false);
        } else {
            //tv_stock_produto.setText("EM STOCK " + "(" + stock + ")");
            tv_stock_produto.setText("EM STOCK");
            quant.setEnabled(true);
            //https://stackoverflow.com/questions/14212518/is-there-a-way-to-define-a-min-and-max-value-for-edittext-in-android
            //quant.setFilters(new InputFilter[]{new InputFilterMinMax(0, stock)});
        }

        if (manage_sctoks.equalsIgnoreCase("yes")) {
            if (back_orders.equalsIgnoreCase("yes")) {
                //desconta-se stock mas não se informa o cliente
                label_client.setText("");
                stock = 99;
            } else if (back_orders.equalsIgnoreCase("notify")) {
                //avisar o cliente
                if (stock > 0) {
                    label_client.setText("Em stock (pode ser encomendado sem stock)");

                } else {
                    label_client.setText("Disponível por encomenda a fornecedor");
                }
                stock = 99;

            } else if (back_orders.equalsIgnoreCase("no")) {
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
        String name = produto.getName();
        if (name.equalsIgnoreCase("")) {
            tv_titulo_produto.setText(ind);
        } else {
            tv_titulo_produto.setText(name);
        }

        String ref = produto.getRef();
        if (ref.equalsIgnoreCase("")) {
            tv_ref_produto.setText(ind);
        } else {
            ref = "REF: " + produto.getRef();
            tv_ref_produto.setText(ref);
        }

        if (produto.getDescricao().equals("")) {
            desc_produto.setText(ind);
        } else {
            Spanned desc = CookHTML(produto.getDescricao());
            desc_produto.setText(desc);
        }

        if (produto.getWeight().equals("")) {
            info_produto.setText(ind);
        } else {
            Spanned info = CookHTML(produto.getWeight());
            info_produto.setText(info + " Kg");
        }

        String preco = produto.getRegular_price();
        String preco_sales = produto.getSales_price();

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

                    if (MainActivity.isProdutoNoCarrinho((AppCompatActivity) getActivity(), produto)) {
                        //actualizar
                        MainActivity.RemoverProduto((AppCompatActivity) getActivity(), MainActivity.geralMenu, produto);
                        MainActivity.AdicionarProduto((AppCompatActivity) getActivity(), MainActivity.geralMenu, produto, edit_quant);
                        Toast.makeText(getActivity(), "Produto Actualizado", Toast.LENGTH_SHORT).show();

                    } else {
                        //adicionar
                        MainActivity.AdicionarProduto((AppCompatActivity) getActivity(), MainActivity.geralMenu, produto, edit_quant);
                        Toast.makeText(getActivity(), "Produto Adicionado", Toast.LENGTH_SHORT).show();
                    }

                } else if (edit_quant == 0) {
                    //remover
                    if (MainActivity.isProdutoNoCarrinho((AppCompatActivity) getActivity(), produto)) {
                        MainActivity.RemoverProduto((AppCompatActivity) getActivity(), MainActivity.geralMenu, produto);
                        Toast.makeText(getActivity(), "Produto removido", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    //não adicionar
                    Toast.makeText(getActivity(), "Produto não adicionado ao carrinho\nVerifique a quantidade", Toast.LENGTH_SHORT).show();

                }

            }
        });


        VerificarOCarrinhoPelasQuantidadesDoProduto();

        return v;
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

        boolean isProducto = MainActivity.isProdutoNoCarrinho((AppCompatActivity) getActivity(), produto);
        if (isProducto) {
            //então quanta quantidade
            Produto prod = MainActivity.getProduto((AppCompatActivity) getActivity(), produto);
            if (prod != null) {
                int quantidade = prod.getQuantidade_compra();
                if (quantidade != 0) {
                    quant.setText("" + quantidade);
                    add.setText("ACTUALIZAR");
                }

            }

        }

    }

    @Override
    public void onResume() {
        super.onResume();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    ScrollView sv = (ScrollView) getActivity().findViewById(R.id.scrollView);
                    if(sv != null){
                        sv.smoothScrollTo(0,0);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 200);
    }

    @Override
    public void onPause() {
        super.onPause();
        ProdutosFragment.myProdutosRecyclerViewAdapter.notifyDataSetChanged();

    }
}
