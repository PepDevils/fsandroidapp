package pt.dezvezesdez.farmaciaserrano.adapters;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;

import pt.dezvezesdez.farmaciaserrano.R;
import pt.dezvezesdez.farmaciaserrano.activities.MainActivity;
import pt.dezvezesdez.farmaciaserrano.activities.ProdutoActivity;
import pt.dezvezesdez.farmaciaserrano.fragments.ProdutosFragment;
import pt.dezvezesdez.farmaciaserrano.modelos.Produto;
import pt.dezvezesdez.farmaciaserrano.util.Helper;


public class MyProdutosRecyclerViewAdapter extends RecyclerView.Adapter<MyProdutosRecyclerViewAdapter.ViewHolder> implements View.OnClickListener {

    //variavies
    private ArrayList<Produto> Produtos__ = new ArrayList<>();
    private AppCompatActivity a;
    private ImageLoader imageLoader;

    //construtor
    public MyProdutosRecyclerViewAdapter(AppCompatActivity a, ArrayList<Produto> Produtos) {
        this.Produtos__ = Produtos;
        this.a = a;

        //setHasStableIds(true);

        imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(a));

    }

    private View view;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_produtos, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        holder.setIsRecyclable(false);

        try {
            if (holder instanceof RecyclerView.ViewHolder) {

                final Produto prod = Produtos__.get(position);

                Helper.DoorImageLoader(prod.getImage(), holder.prod_image, imageLoader);
                holder.tx_nome.setTag(position);
                holder.tx_price_promotion.setTag(position);
                holder.tx_price.setTag(position);
                holder.prod_image.setTag(position);

                holder.tx_nome.setText(prod.getName());

                String preco = prod.getRegular_price();
                String preco_sales = prod.getSales_price();

                if (preco_sales != null && preco_sales.equalsIgnoreCase("") && preco != null && preco.equalsIgnoreCase("")) {
                    holder.tx_price.setText("");
                    holder.tx_price_promotion.setText("");
                } else if (preco_sales != null && preco_sales.equalsIgnoreCase("")) {
                    preco = Helper.FiltrarEDitTextToMoney(preco);
                    preco = preco + " €";
                    holder.tx_price.setText(preco);
                    holder.tx_price_promotion.setText("");
                } else if (preco != null && !preco.equalsIgnoreCase("")) {
                    preco = Helper.FiltrarEDitTextToMoney(preco);
                    preco = preco + " €";
                    holder.tx_price.setText(preco);
                    holder.tx_price.setPaintFlags(holder.tx_price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    preco_sales = preco_sales + " €";
                    holder.tx_price_promotion.setText(preco_sales);
                    SetColorInTextV(holder.tx_price, R.color.fs_rosa_dark);
                    SetColorInTextV(holder.tx_price_promotion, R.color.fs_verde);
                }


                String stock = prod.getStock_status();
                if (stock != null && stock.equalsIgnoreCase("outofstock")) {
                    String aux = "ESGOTADO";
                    holder.img_prod_compra.setText(aux);
                    holder.img_prod_compra.setEnabled(false);
                    Drawable dra = a.getResources().getDrawable(R.drawable.selector_esgotado_button, null);
                    holder.img_prod_compra.setBackground(dra);
                } else {
                    String aux = MainActivity.isProdutoNoCarrinho(a, prod) ? "REMOVER" : "ADICIONAR";
                    holder.img_prod_compra.setText(aux);
                    Drawable dra = MainActivity.isProdutoNoCarrinho(a, prod) ? a.getResources().getDrawable(R.drawable.selector_remove_button, null) : a.getResources().getDrawable(R.drawable.selector_add_button, null);
                    holder.img_prod_compra.setBackground(dra);
                }


                holder.img_prod_compra.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (holder.img_prod_compra.getText().equals("ADICIONAR")) {

                            MainActivity.AdicionarProduto(a, MainActivity.geralMenu, prod, 1);
                            holder.img_prod_compra.setText("REMOVER");
                            // holder.img_prod_compra.setTextColor(Color.RED);
                            holder.img_prod_compra.setBackground(a.getResources().getDrawable(R.drawable.selector_remove_button, null));

                        } else {

                            MainActivity.RemoverProduto(a, MainActivity.geralMenu, prod);
                            holder.img_prod_compra.setText("ADICIONAR");
                            //holder.img_prod_compra.setTextColor(Color.WHITE);
                            holder.img_prod_compra.setBackground(a.getResources().getDrawable(R.drawable.selector_add_button, null));

                        }
                    }
                });

            }
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
    }

    private void SetColorInTextV(TextView tv, int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            tv.setTextColor(a.getResources().getColor(color, null));
        } else {
            tv.setTextColor(a.getResources().getColor(color));
        }
    }


    public void clear() {
        Produtos__.removeAll(Produtos__);
       // Produtos__.clear();
    }


    public void insert(Produto produto, int index) {
        Produtos__.add(index, produto);
    }

    @Override
    public int getItemCount() {
        return Produtos__.size();
    }

    @Override
    public void onClick(View v) {

        if (v.getRootView().findViewById(R.id.fragment_for_scroll) != null) {
            //fragment
            int position = (int) v.getTag();
            Produto prod = Produtos__.get(position);
            ProdutosFragment.AbrirFragProdutoEanimation(a, prod);

        } else {

            //todo: criar ProdutoActivity semelhante ao fragment.

            int position = (int) v.getTag();
            Produto prod = Produtos__.get(position);

            Intent inte = new Intent(a, ProdutoActivity.class);
            inte.putExtra("prod_id", prod.getId());
            inte.putExtra("prod_ref", prod.getRef());
            inte.putExtra("prod_name", prod.getName());
            inte.putExtra("prod_regular_price", prod.getRegular_price());
            inte.putExtra("prod_price", prod.get_price());
            inte.putExtra("prod_sales_price", prod.getSales_price());
            inte.putExtra("prod_image", prod.getImage());
            inte.putExtra("prod_desc", prod.getDescricao());
            inte.putExtra("prod_weight", prod.getWeight());
            inte.putExtra("prod_stock", prod.getStock());
            inte.putExtra("prod_stock_status", prod.getStock_status());
            inte.putExtra("prod_manage_stock", prod.getManage_stock());
            inte.putExtra("prod_back_orders", prod.getBack_orders());

            a.startActivity(inte);

        }


    }

    protected class ViewHolder extends RecyclerView.ViewHolder {

        final TextView tx_nome;
        final TextView tx_price;
        final TextView tx_price_promotion;
        final ImageView prod_image;
        final Button img_prod_compra;

        private ViewHolder(View view) {
            super(view);
            tx_nome = (TextView) view.findViewById(R.id.tx_nome);
            tx_price = (TextView) view.findViewById(R.id.tx_price);
            tx_price_promotion = (TextView) view.findViewById(R.id.tx_price_promotion);
            img_prod_compra = (Button) view.findViewById(R.id.img_prod_compra);
            prod_image = (ImageView) view.findViewById(R.id.prod_image);

            tx_nome.setOnClickListener(MyProdutosRecyclerViewAdapter.this);
            tx_price.setOnClickListener(MyProdutosRecyclerViewAdapter.this);
            tx_price_promotion.setOnClickListener(MyProdutosRecyclerViewAdapter.this);
            prod_image.setOnClickListener(MyProdutosRecyclerViewAdapter.this);
        }

    }
}
