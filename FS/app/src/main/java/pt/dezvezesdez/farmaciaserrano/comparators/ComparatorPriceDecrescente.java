package pt.dezvezesdez.farmaciaserrano.comparators;

import java.util.Comparator;

import pt.dezvezesdez.farmaciaserrano.modelos.Produto;

/**
 * Created by dezvezesdez on 28/06/2017.
 */

public class ComparatorPriceDecrescente implements Comparator<Produto> {

    @Override
    public int compare(Produto p1, Produto p2) {
        float price1 = Float.valueOf(p1.get_price());
        float price2 = Float.valueOf(p2.get_price());

        if (price1 > price2) {
            return -1;
        } else if (price1 < price2) {
            return 1;
        } else {
            return 0;
        }
    }
}
