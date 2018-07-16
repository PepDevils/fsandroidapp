package pt.dezvezesdez.farmaciaserrano.modelos;

import com.google.gson.annotations.SerializedName;

/**
 * Created by dezvezesdez on 03/05/2017.
 */

public class Produto extends Object  {

    @SerializedName("id")
    private String id;

    @SerializedName("ref")
    private String ref;

    @SerializedName("title")
    private String name;

    @SerializedName("regular_price")
    private String regular_price;

    @SerializedName("_price")
    private String _price;

    @SerializedName("sale_price")
    private String sales_price;

    @SerializedName("thumbnail_id")
    private String image;

    @SerializedName("description")
    private String descricao;

    @SerializedName("weight")
    private String weight;

    @SerializedName("stock")
    private int stock;

    @SerializedName("stock_status")
    private String stock_status;

    @SerializedName("manage_stock")
    private String manage_stock;

    @SerializedName("back_orders")
    private String back_orders;

    private String subcategoria;

    private String categoria;

    private int quantidade_compra;

    private String infoAdicional;

    public Produto(){

    }

    public Produto(String id, String ref, String name, String regular_price, String _price, String sales_price, String image, String descricao, String weight, int stock, String stock_status, String manage_stock , String back_orders) {
        this.id = id;
        this.ref = ref;
        this.name = name;
        this.regular_price = regular_price;
        this._price = _price;
        this.sales_price = sales_price;
        this.image = image;
        this.descricao = descricao;
        this.weight = weight;
        this.stock = stock;
        this.stock_status = stock_status;
        this.manage_stock = manage_stock;
        this.back_orders = back_orders;
    }

    //GETTERS AND SETTERS
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRegular_price() {
        return regular_price;
    }

    public void setRegular_price(String regular_price) {
        this.regular_price = regular_price;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getSubcategoria() {
        return subcategoria;
    }

    public void setSubcategoria(String subcategoria) {
        this.subcategoria = subcategoria;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public String getInfoAdicional() {
        return infoAdicional;
    }

    public void setInfoAdicional(String infoAdicional) {
        this.infoAdicional = infoAdicional;
    }

    public int getQuantidade_compra() {
        return quantidade_compra;
    }

    public void setQuantidade_compra(int quantidade_compra) {
        this.quantidade_compra = quantidade_compra;
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public String getSales_price() {
        return sales_price;
    }

    public void setSales_price(String sales_price) {
        this.sales_price = sales_price;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getStock_status() {
        return stock_status;
    }

    public void setStock_status(String stock_status) {
        this.stock_status = stock_status;
    }

    public String getManage_stock() {
        return manage_stock;
    }

    public void setManage_stock(String manage_stock) {
        this.manage_stock = manage_stock;
    }

    public String getBack_orders() {
        return back_orders;
    }

    public void setBack_orders(String back_orders) {
        this.back_orders = back_orders;
    }

    public String get_price() {
        return _price;
    }

    public void set_price(String _price) {
        this._price = _price;
    }
}
