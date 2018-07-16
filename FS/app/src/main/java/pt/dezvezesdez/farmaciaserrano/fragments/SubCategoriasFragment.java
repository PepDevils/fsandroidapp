package pt.dezvezesdez.farmaciaserrano.fragments;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;

import pt.dezvezesdez.farmaciaserrano.R;
import pt.dezvezesdez.farmaciaserrano.activities.MainActivity;
import pt.dezvezesdez.farmaciaserrano.adapters.SubCategoriasAdapters;
import pt.dezvezesdez.farmaciaserrano.interfaces.Interface_ReclycerViewOnTounchHack;
import pt.dezvezesdez.farmaciaserrano.modelos.SubCategoria;
import pt.dezvezesdez.farmaciaserrano.view.RecyclerViewTounchListener;

/**
 * Created by dezvezesdez on 03/05/2017.
 */

public class SubCategoriasFragment extends Fragment implements Interface_ReclycerViewOnTounchHack {

    private ArrayList<SubCategoria> mysubcategorias = new ArrayList<>();
    private Activity a;

    OnSubcategoriaSelectedListener mCallback;

    // Container Activity must implement this interface
    public interface OnSubcategoriaSelectedListener {
         void onSubcategoriaSelected(int position);
    }


    public static SubCategoriasFragment newInstance() {
        return new SubCategoriasFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        a = getActivity();
        mysubcategorias = MainActivity.SubCategorias;

    }


    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_categorias, container, false);

        LinearLayoutManager layoutManager = new LinearLayoutManager(a, LinearLayoutManager.HORIZONTAL, false);
        RecyclerView rv = (RecyclerView) v.findViewById(R.id.my_recycler_view);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(layoutManager);

        SubCategoriasAdapters scadapter = new SubCategoriasAdapters(a,mysubcategorias);

        rv.setAdapter(scadapter);
        rv.addOnItemTouchListener(new RecyclerViewTounchListener(a, rv, this));

        return v;
    }

    @Override
    public void onItemAdapterClickListener(View view, int position) {
       // Toast.makeText(a, "Change another frag " + position, Toast.LENGTH_SHORT).show();

        // Send the event to the host activity
        mCallback.onSubcategoriaSelected(position);

    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnSubcategoriaSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " deverá implementar OnSubcategoriaSelectedListener");
        }
    }
}
