package pt.dezvezesdez.farmaciaserrano.modelos;

import com.google.gson.annotations.SerializedName;

/**
 * Created by dezvezesdez on 09/06/2017.
 */

public class Banner {

    @SerializedName("guid")
    private String image_link;

    private String pesquisa_nome;
    private String pesquisa_categoria;
    private String pesquisa_ordenacao;
    private String pesquisa_min;
    private String pesquisa_max;
    private int pesquisa_index;
    private int pesquisa_quant;


    //todo: mudar o construtor para ter todos os atributos
    public Banner(String image_link) {
        this.image_link = image_link;
    }

    public String getImage_link() {
        return image_link;
    }

    public void setImage_link(String image_link) {
        this.image_link = image_link;
    }

    public String getPesquisa_nome() {
        return pesquisa_nome;
    }

    public void setPesquisa_nome(String pesquisa_nome) {
        this.pesquisa_nome = pesquisa_nome;
    }

    public String getPesquisa_categoria() {
        return pesquisa_categoria;
    }

    public void setPesquisa_categoria(String pesquisa_categoria) {
        this.pesquisa_categoria = pesquisa_categoria;
    }

    public String getPesquisa_ordenacao() {
        return pesquisa_ordenacao;
    }

    public void setPesquisa_ordenacao(String pesquisa_ordenacao) {
        this.pesquisa_ordenacao = pesquisa_ordenacao;
    }

    public String getPesquisa_min() {
        return pesquisa_min;
    }

    public void setPesquisa_min(String pesquisa_min) {
        this.pesquisa_min = pesquisa_min;
    }

    public String getPesquisa_max() {
        return pesquisa_max;
    }

    public void setPesquisa_max(String pesquisa_max) {
        this.pesquisa_max = pesquisa_max;
    }

    public int getPesquisa_index() {
        return pesquisa_index;
    }

    public void setPesquisa_index(int pesquisa_index) {
        this.pesquisa_index = pesquisa_index;
    }

    public int getPesquisa_quant() {
        return pesquisa_quant;
    }

    public void setPesquisa_quant(int pesquisa_quant) {
        this.pesquisa_quant = pesquisa_quant;
    }
}
