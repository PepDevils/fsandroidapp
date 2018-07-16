package pt.dezvezesdez.farmaciaserrano.modelos;

import com.google.gson.annotations.SerializedName;


/**
 * Created by dezvezesdez on 28/04/2017.
 */

public class Categoria {

    @SerializedName("name")
    private String Titulo;

    @SerializedName("image_link")
    private String Image;

    @SerializedName("image_link_cor")
    private String ImageCor;

    @SerializedName("color")
    private String Color;

    @SerializedName("id")
    private String id;

    public Categoria(String titulo, String image, String imageCor, String color, String id) {
        Titulo = titulo;
        Image = image;
        ImageCor = imageCor;
        Color = color;
        this.id = id;
    }

    public String getTitulo() {
        return Titulo;
    }

    public void setTitulo(String titulo) {
        Titulo = titulo;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getImageCor() {
        return ImageCor;
    }

    public void setImageCor(String imageCor) {
        ImageCor = imageCor;
    }

    public String getColor() {
        return Color;
    }

    public void setColor(String color) {
        Color = color;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
