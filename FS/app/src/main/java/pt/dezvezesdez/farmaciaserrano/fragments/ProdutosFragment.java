package pt.dezvezesdez.farmaciaserrano.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

import java.util.ArrayList;

import pt.dezvezesdez.farmaciaserrano.R;
import pt.dezvezesdez.farmaciaserrano.activities.MainActivity;
import pt.dezvezesdez.farmaciaserrano.adapters.MyProdutosRecyclerViewAdapter;
import pt.dezvezesdez.farmaciaserrano.interfaces.Interface_ReclycerViewOnTounchHack;
import pt.dezvezesdez.farmaciaserrano.modelos.Produto;
import pt.dezvezesdez.farmaciaserrano.modelos.SubCategoria;
import pt.dezvezesdez.farmaciaserrano.view.RecyclerViewTounchListener;

public class ProdutosFragment extends Fragment implements Interface_ReclycerViewOnTounchHack {

    public static MyProdutosRecyclerViewAdapter myProdutosRecyclerViewAdapter;
    private RecyclerView recyclerView;
    private LinearLayoutManager llm;

    ArrayList<Produto> Produtos_ = MainActivity.Produtos;

    public ProdutosFragment() {
    }

    public static ProdutosFragment newInstance() {
        return new ProdutosFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_produtos_list, container, false);

        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            recyclerView = (RecyclerView) view;
            llm = new LinearLayoutManager(context);
            recyclerView.setLayoutManager(llm);
            recyclerView.setItemAnimator(new DefaultItemAnimator());

            Produtos_ = MainActivity.Produtos;

            if (!MainActivity.isTodos) {
                Produtos_ = FilterProdutosBySubCategoria(Produtos_);
            } else {
                Produtos_ = FilterCategoria(Produtos_);
            }


            myProdutosRecyclerViewAdapter = new MyProdutosRecyclerViewAdapter((AppCompatActivity) getActivity(), Produtos_);
            recyclerView.setAdapter(myProdutosRecyclerViewAdapter);
            recyclerView.addOnItemTouchListener(new RecyclerViewTounchListener(getActivity(), recyclerView, this));

            //recyclerView.addOnItemTouchListener(new RecyclerViewTounchListener(getActivity(), recyclerView, this));

            //GESTURE PARA DRAG AND DISMISS (CALLBACK INTERFACE)
       /*     ItemTouchHelper.Callback callback = new MyFastItemAdapter((AppCompatActivity) getActivity(),myProdutosRecyclerViewAdapter);
            ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
            touchHelper.attachToRecyclerView(recyclerView);*/

        }
        return view;
    }

    private ArrayList<Produto> FilterCategoria(ArrayList<Produto> produtos_) {
        ArrayList<Produto> ProdutosFiltrados = new ArrayList<>();
        for (Produto prod : produtos_) {
            ProdutosFiltrados.add(prod);
        }
        return ProdutosFiltrados;
    }

    private ArrayList<Produto> FilterProdutosBySubCategoria(ArrayList<Produto> produtos) {
        ArrayList<Produto> ProdutosFiltrados = new ArrayList<>();
        for (Produto prod : produtos) {
            String aux = prod.getSubcategoria();
            if (MainActivity.SubCategoriaSelector.equalsIgnoreCase(aux)) {
                ProdutosFiltrados.add(prod);
            }

        }
        return ProdutosFiltrados;
    }

    public static void AbrirFragProdutoEanimation(AppCompatActivity a, Produto prod_) {
        if (MainActivity.sv != null) {
            if (MainActivity.sv.getVisibility() == View.VISIBLE) {
                if (MainActivity.pesquia_aberta) {
                    MainActivity.setItemsVisibility(MainActivity.geralMenu);
                    a.invalidateOptionsMenu();
                    MainActivity.pesquia_aberta = false;
                    MainActivity.ToggleFragmentAndSpinner(a, MainActivity.geralMenu);
                }

                AbrirProduto(a, prod_);
            } else {
                AbrirProduto(a, prod_);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (MainActivity.pesquia_aberta) {
            getActivity().findViewById(R.id.subimage).setVisibility(View.GONE);
            getActivity().findViewById(R.id.spinner_container).setVisibility(View.GONE);
           /* getActivity().findViewById(R.id.spinner_container).setEnabled(false);
            getActivity().findViewById(R.id.subimage).setEnabled(false);*/
        } else {
            getActivity().findViewById(R.id.subimage).setVisibility(View.VISIBLE);
            getActivity().findViewById(R.id.spinner_container).setVisibility(View.VISIBLE);
        /*    getActivity().findViewById(R.id.spinner_container).setEnabled(true);
            getActivity().findViewById(R.id.subimage).setEnabled(true);*/
        }


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    ScrollView sv = (ScrollView) getActivity().findViewById(R.id.scrollView);
                    if (sv != null) {
                        sv.smoothScrollTo(0, 0);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 200);


    }

    public static void AbrirProduto(AppCompatActivity a, Produto prod_) {
        MainActivity.spinner_subcat.setEnabled(false);
        MainActivity.subimage.setVisibility(View.GONE);
        a.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_for_scroll, ProdFragment.newInstance(prod_)).commit();

    }

    @Override
    public void onItemAdapterClickListener(View view, int position) {
        //TODO: verificar esta função e eliminala se for necessario

    }

}
