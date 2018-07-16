package pt.dezvezesdez.farmaciaserrano.fragments;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import java.util.ArrayList;

import pt.dezvezesdez.farmaciaserrano.R;
import pt.dezvezesdez.farmaciaserrano.activities.MainActivity;
import pt.dezvezesdez.farmaciaserrano.util.Helper;

public class AjudaFragment extends Fragment {

    private View v;
    private static Button bt_encomenda, bt_moradas, bt_detalhes, bt_ajuda, bt_terminar, bt_init_sessao, bt_criar_conta, criar;
    private static EditText edt_email, edt_pass;
    private static LinearLayout ll_content;


    public AjudaFragment() {
        // Required empty public constructor
    }

    public static AjudaFragment newInstance() {
        AjudaFragment fragment = new AjudaFragment();
        return fragment;
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.fragment_ajuda, container, false);

        ll_content = (LinearLayout) v.findViewById(R.id.ll_content);

        edt_email = (EditText) v.findViewById(R.id.edt_email);
        edt_pass = (EditText) v.findViewById(R.id.edt_pass);
        bt_init_sessao = (Button) v.findViewById(R.id.bt_init_sessao);
        bt_criar_conta = (Button) v.findViewById(R.id.bt_criar_conta);

        bt_init_sessao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //todo: verificar email e pass na API e colocar neste if
                if (Helper.isEmailValid(edt_email.getText().toString()) && !edt_pass.getText().toString().equalsIgnoreCase("") /*&& connection to pass */) {
                    AposIniciarSessao();
                    //gravar
                    SharedPreferences prefs = getActivity().getSharedPreferences("MY_PREFERENCES", Activity.MODE_PRIVATE);
                    prefs.edit().putBoolean("isLogIn", true).apply();
                    MainActivity.isLogIn = true;
                    //fechar keyboard
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getActivity().getWindow().getDecorView().getWindowToken(), 0);

                } else {
                    Toast.makeText(getActivity(), "Verifique os campos", Toast.LENGTH_SHORT).show();
                }
            }
        });

        bt_criar_conta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //   https://stackoverflow.com/questions/22454839/android-adding-simple-animations-while-setvisibilityview-gone

                FadeOutView(edt_email);
                FadeOutView(edt_pass);
                FadeOutView(bt_init_sessao);
                FadeOutView(bt_criar_conta);

                final View layout_criar_conta = inflater.inflate(R.layout.layout_criar_conta, null, false);
                ll_content.addView(layout_criar_conta);

                final EditText edt_email_criar = (EditText) ll_content.findViewById(R.id.edt_email_criar);
                final EditText edt_pass_criar = (EditText) ll_content.findViewById(R.id.edt_pass_criar);
                final EditText edt_pass_criar_rep = (EditText) ll_content.findViewById(R.id.edt_pass_criar_rep);

                criar = (Button) ll_content.findViewById(R.id.criar);
                criar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {


                        if (Helper.isEmailValid(edt_email_criar.getText().toString()) && edt_pass_criar.getText().toString().equals(edt_pass_criar_rep.getText().toString())) {
                            edt_email.setText(edt_email_criar.getText().toString());
                            edt_pass.setText(edt_pass_criar.getText().toString());
                            AposCriarConta();


                        } else {
                            Toast.makeText(getActivity(), "Verifique os campos", Toast.LENGTH_SHORT).show();
                        }


                        //escrever as credenciais nos edit texts
                        //escrever as credenciais nos edit texts
                        //escrever as credenciais nos edit texts
                        //escrever as credenciais nos edit texts
                        //escrever as credenciais nos edit texts
                        //para fazer logo o log in
                        //gravar este nos sharedpreferences


                    }
                });
            }
        });

        bt_encomenda = (Button) v.findViewById(R.id.bt_encomenda);
        bt_moradas = (Button) v.findViewById(R.id.bt_moradas);
        bt_detalhes = (Button) v.findViewById(R.id.bt_detalhes);
        bt_ajuda = (Button) v.findViewById(R.id.bt_ajuda);
        bt_terminar = (Button) v.findViewById(R.id.bt_terminar);


        bt_encomenda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        bt_moradas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        bt_detalhes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // DetalhesContaFragment
                ((AppCompatActivity) getActivity()).getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                        .add(R.id.fragment_for_scroll, DetalhesContaFragment.newInstance())
                        .commit();
            }
        });

        bt_ajuda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        bt_terminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AposFecharSessaoOuInicio();
                SharedPreferences prefs = getActivity().getSharedPreferences("MY_PREFERENCES", Activity.MODE_PRIVATE);
                prefs.edit().putBoolean("isLogIn", false).apply();
            }
        });

        return v;
    }


    private void AposIniciarSessao() {
        bt_encomenda.setVisibility(View.VISIBLE);
        bt_moradas.setVisibility(View.VISIBLE);
        bt_detalhes.setVisibility(View.VISIBLE);
        bt_ajuda.setVisibility(View.VISIBLE);
        bt_terminar.setVisibility(View.VISIBLE);

        edt_email.setVisibility(View.GONE);
        edt_pass.setVisibility(View.GONE);
        bt_init_sessao.setVisibility(View.GONE);
        bt_criar_conta.setVisibility(View.GONE);
    }


    private void AposFecharSessaoOuInicio() {
        bt_encomenda.setVisibility(View.GONE);
        bt_moradas.setVisibility(View.GONE);
        bt_detalhes.setVisibility(View.GONE);
        bt_ajuda.setVisibility(View.GONE);
        bt_terminar.setVisibility(View.GONE);

        edt_email.setVisibility(View.VISIBLE);
        edt_pass.setVisibility(View.VISIBLE);
        bt_init_sessao.setVisibility(View.VISIBLE);
        bt_criar_conta.setVisibility(View.VISIBLE);
    }


    public static void AposCriarConta() {
/*        View vv = v.findViewById(R.id.llcriarconta);
        ll_content.removeView(vv);*/

        // fecha a zona de criar conta e aparece novamente o login
        FadeItBackOn(edt_email);
        FadeItBackOn(edt_pass);
        FadeItBackOn(bt_init_sessao);
        FadeItBackOn(bt_criar_conta);

        for (int i = 0; i < ll_content.getChildCount(); i++) {
            View v = ll_content.getChildAt(i);
            if (v instanceof LinearLayout) {
                ll_content.removeView(v);
            }
        }
    }


    @Override
    public void onResume() {
        super.onResume();

        //preparar Main Activity na entrada

        //no caso de vir apartire do fragment Produtos
        if (getActivity().findViewById(R.id.subimage) != null && getActivity().findViewById(R.id.spinner_container) != null) {
            getActivity().findViewById(R.id.subimage).setVisibility(View.GONE);
            getActivity().findViewById(R.id.spinner_container).setVisibility(View.GONE);

            ScrollView sv = (ScrollView) getActivity().findViewById(R.id.scrollView);
            RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            rlp.addRule(RelativeLayout.BELOW, getActivity().findViewById(R.id.my_toolbar).getId());
            sv.setLayoutParams(rlp);
        }


        SharedPreferences prefs = getActivity().getSharedPreferences("MY_PREFERENCES", Activity.MODE_PRIVATE);
        if (prefs.getBoolean("isLogIn", false)) {
            AposIniciarSessao();
        } else {
            AposFecharSessaoOuInicio();
        }


    }

    @Override
    public void onPause() {
        super.onPause();

        if (getActivity().findViewById(R.id.scrollView) != null) {
            ScrollView sv = (ScrollView) getActivity().findViewById(R.id.scrollView);
            RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            rlp.addRule(RelativeLayout.BELOW, getActivity().findViewById(R.id.spinner_container).getId());
            sv.setLayoutParams(rlp);
        }

    }

    private void FadeOutView(final View v) {
        //  https://stackoverflow.com/questions/22454839/android-adding-simple-animations-while-setvisibilityview-gone
        //This fades out a view
        v.setEnabled(false);
        v.animate().alpha(1.0f).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                v.setVisibility(View.GONE);
            }
        });
    }

    private static void FadeItBackOn(final View v) {
        v.setEnabled(true);
        v.setVisibility(View.VISIBLE);

    }
}
