package pt.dezvezesdez.farmaciaserrano.modelos;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by dezvezesdez on 02/05/2017.
 */

public class SubCategoria {

    @SerializedName("sub_categoria")
    private String title;

    @SerializedName("produto")
    private ArrayList<Produto> produtos;

    public SubCategoria(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ArrayList<Produto> getProdutos() {
        return produtos;
    }

    public void setProdutos(ArrayList<Produto> produtos) {
        this.produtos = produtos;
    }
}
