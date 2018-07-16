package pt.dezvezesdez.farmaciaserrano.comparators;

import java.util.Comparator;

import pt.dezvezesdez.farmaciaserrano.modelos.Produto;

/**
 * Created by dezvezesdez on 28/06/2017.
 */

public class ComparatorNameCrescente implements Comparator<Produto> {

    @Override
    public int compare(Produto p1, Produto p2) {
        return p1.getName().compareTo(p2.getName());
    }
}
