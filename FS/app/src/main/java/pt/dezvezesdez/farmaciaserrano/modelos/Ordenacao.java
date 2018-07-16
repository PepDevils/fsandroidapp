package pt.dezvezesdez.farmaciaserrano.modelos;

import com.google.gson.annotations.SerializedName;

/**
 * Created by dezvezesdez on 27/06/2017.
 */

public class Ordenacao {

    @SerializedName("name")
    private String name;

    @SerializedName("switch")
    private String switch_;

    public Ordenacao(String name, String switch_) {
        this.name = name;
        this.switch_ = switch_;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSwitch_() {
        return switch_;
    }

    public void setSwitch_(String switch_) {
        this.switch_ = switch_;
    }
}
