package pt.dezvezesdez.farmaciaserrano.adapters;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.widget.ArrayAdapter;

import java.util.List;
import java.util.Objects;

/**
 * Created by dezvezesdez on 05/06/2017.
 */

public class HintSpinnerAdapter extends ArrayAdapter<String> {

    public HintSpinnerAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<String> objects) {
        super(context, resource, objects);
    }



}
