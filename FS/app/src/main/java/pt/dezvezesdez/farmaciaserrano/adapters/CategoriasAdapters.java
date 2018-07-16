package pt.dezvezesdez.farmaciaserrano.adapters;

import android.app.Activity;
import android.graphics.Color;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;
import java.util.Random;

import pt.dezvezesdez.farmaciaserrano.R;
import pt.dezvezesdez.farmaciaserrano.modelos.Categoria;
import pt.dezvezesdez.farmaciaserrano.util.Helper;

/**
 * Created by dezvezesdez on 28/04/2017.
 */

public class CategoriasAdapters extends RecyclerView.Adapter {

    private Activity a;
    private ArrayList<Categoria> categorias;
    private ImageLoader imageLoader;


    public CategoriasAdapters(Activity a, ArrayList<Categoria> categorias) {
        this.a = a;
        this.categorias = categorias;
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(a));

    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_categoria, parent, false);
        vh = new CategoriaViewHolder(v);
        return vh;
    }

/*    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        imageLoader.destroy();
    }*/

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof CategoriaViewHolder) {

            Categoria cat = categorias.get(position);

            // ((CategoriaViewHolder) holder).title.setText(cat.getTitulo());

            String color = cat.getColor();
             int c = Color.parseColor(color);
            //int c = Integer.parseInt(color.replaceFirst("^#",""), 16);
            ((CategoriaViewHolder) holder).image.setBackgroundColor(c);

            DisplayMetrics displaymetrics = new DisplayMetrics();
            a.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
            int rect_ = displaymetrics.widthPixels / 2 - 2;


            ((CategoriaViewHolder) holder).image.setLayoutParams(new LinearLayout.LayoutParams(rect_, rect_));
            Helper.DoorImageLoader(cat.getImage(), ((CategoriaViewHolder) holder).image, imageLoader);

        }

    }

    @Override
    public int getItemCount() {
        return categorias.size();
    }


    public static class CategoriaViewHolder extends RecyclerView.ViewHolder {

        public ImageView image;
        // public TextView title;

        public CategoriaViewHolder(View view) {
            super(view);
            image = (ImageView) view.findViewById(R.id.imageC);
            // title = (TextView) view.findViewById(R.id.titleC);
        }



    }
}
