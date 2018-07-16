package pt.dezvezesdez.farmaciaserrano.adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import pt.dezvezesdez.farmaciaserrano.R;
import pt.dezvezesdez.farmaciaserrano.modelos.SubCategoria;
import pt.dezvezesdez.farmaciaserrano.util.Helper;

/**
 * Created by dezvezesdez on 03/05/2017.
 */

public class SubCategoriasAdapters extends RecyclerView.Adapter {

    private Activity a;
    private ArrayList<SubCategoria> subCategorias;

    public SubCategoriasAdapters(Activity a, ArrayList<SubCategoria> subCategorias) {
        this.a = a;
        this.subCategorias = subCategorias;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sub_categoria, parent, false);
        vh = new SubCategoriasAdapters.SubCategoriaViewHolder(v);
        return vh;

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof SubCategoriaViewHolder) {
            SubCategoria subcat = subCategorias.get(position);
            ((SubCategoriaViewHolder) holder).title.setText(subcat.getTitle());

        } else {

        }

    }

    @Override
    public int getItemCount() {
        return subCategorias.size();
    }


    public static class SubCategoriaViewHolder extends RecyclerView.ViewHolder {

        public TextView title;

        public SubCategoriaViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.titleSubC);
        }

    }

}
