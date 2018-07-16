package pt.dezvezesdez.farmaciaserrano.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import pt.dezvezesdez.farmaciaserrano.R;


public class DetalhesContaFragment extends Fragment {


    public DetalhesContaFragment() {
        // Required empty public constructor
    }


    public static DetalhesContaFragment newInstance() {
        DetalhesContaFragment fragment = new DetalhesContaFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_detalhes_conta, container, false);




        return v;
    }

}
