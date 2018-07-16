package pt.dezvezesdez.farmaciaserrano.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Observable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pt.dezvezesdez.farmaciaserrano.R;
import pt.dezvezesdez.farmaciaserrano.modelos.Banner;
import pt.dezvezesdez.farmaciaserrano.modelos.Categoria;
import pt.dezvezesdez.farmaciaserrano.modelos.SubCategoria;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by dezvezesdez on 28/04/2017.
 */

public class Helper {

    public static final String PLACEHOLDER_IMAGE = "https://farmaciaserrano.pt/wp-content/uploads/2017/05/logo-farmacia-serrano-1.png";

    public static void DoorImageLoader(String url, final ImageView image, final ImageLoader imageLoader) {

        final DisplayImageOptions options = new DisplayImageOptions
                .Builder()
                .imageScaleType(ImageScaleType.EXACTLY)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .resetViewBeforeLoading(true) //as imagens não trocam quando faz o load em scroll
                .build();

        imageLoader.displayImage(url, image, options);
    }

    public static void ImageLoaderWithHeight(String url, final ImageView image, final ImageLoader imageLoader, int height) {

        final DisplayImageOptions options = new DisplayImageOptions
                .Builder()
                .imageScaleType(ImageScaleType.EXACTLY)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .resetViewBeforeLoading(true) //as imagens não trocam quando faz o load em scroll
                .build();

        imageLoader.displayImage(url, image, options);


    }

    public static void TranslucideSystemDef(AppCompatActivity a) {
        //colocar este metodo antes da "setContentView(R.layout.activity_main);" na Activity
        a.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        a.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        a.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        a.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
            a.getWindow().setStatusBarColor(a.getResources().getColor(R.color.niente));
            a.getWindow().setNavigationBarColor(a.getResources().getColor(R.color.niente));
        } else {
            a.getWindow().setStatusBarColor(a.getResources().getColor(R.color.niente, a.getTheme()));
            a.getWindow().setNavigationBarColor(a.getResources().getColor(R.color.niente, a.getTheme()));
        }
    }

    public static void saveObjectInSharedPref(Context c, Object not, String tag, int total) {
        try {
            //SharedPreferences appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(c);
            SharedPreferences appSharedPrefs = c.getSharedPreferences("MY_PREFERENCES", Activity.MODE_PRIVATE);
            SharedPreferences.Editor prefsEditor = appSharedPrefs.edit();
            Gson gson = new Gson();
            String json = gson.toJson(not);
            String t = "total" + tag;
            prefsEditor.putInt(t, total);
            prefsEditor.putString(tag, json);
            prefsEditor.apply();
        } catch (Exception e) {
            Log.d("CacheDatePepeError:", "saveObjectInFileCache: " + e.getMessage());
        }
    }

    public static ArrayList<Categoria> getCategoriasInSharedPref(Context c, String tag) {
        ArrayList<Categoria> notic = new ArrayList<>();
        try {
            //SharedPreferences appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(c);
            SharedPreferences appSharedPrefs = c.getSharedPreferences("MY_PREFERENCES", Activity.MODE_PRIVATE);
            Gson gson = new Gson();
            String t = "total" + tag + 0;
            int total = appSharedPrefs.getInt(t, 0);
            if (total > 0) {
                for (int i = 0; i < total; i++) {
                    String json = appSharedPrefs.getString(tag + i, "");
                    Categoria n = gson.fromJson(json, Categoria.class);
                    notic.add(n);
                }
            }
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
        return notic;
    }

    public static ArrayList<SubCategoria> getSubCategoriasInSharedPref(Context c, String categoria_tag, String tag) {
        ArrayList<SubCategoria> notic = new ArrayList<>();
        try {
            //SharedPreferences appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(c);
            SharedPreferences appSharedPrefs = c.getSharedPreferences("MY_PREFERENCES", Activity.MODE_PRIVATE);
            Gson gson = new Gson();
            String t = "total" + categoria_tag + tag + 0;
            int total = appSharedPrefs.getInt(t, 0);
            if (total > 0) {
                for (int i = 0; i < total; i++) {
                    String json = appSharedPrefs.getString(categoria_tag + tag + i, "");
                    SubCategoria n = gson.fromJson(json, SubCategoria.class);
                    notic.add(n);
                }
            }
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
        return notic;
    }

    public static ArrayList<Banner> getBannersInSharedPref(Context c, String tag) {
        ArrayList<Banner> notic = new ArrayList<>();
        try {
            //SharedPreferences appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(c);
            SharedPreferences appSharedPrefs = c.getSharedPreferences("MY_PREFERENCES", Activity.MODE_PRIVATE);
            Gson gson = new Gson();
            int total = appSharedPrefs.getInt("total" + tag + 0, 0);
            if (total > 0) {
                for (int i = 0; i < total; i++) {
                    String json = appSharedPrefs.getString(tag + i, "");
                    Banner n = gson.fromJson(json, Banner.class);
                    notic.add(n);
                }
            }
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
        return notic;
    }

    public static boolean containsIgnoreCase(String str, String searchStr) {
        if (str == null || searchStr == null) return false;

        final int length = searchStr.length();
        if (length == 0)
            return true;

        for (int i = str.length() - length; i >= 0; i--) {
            if (str.regionMatches(true, i, searchStr, 0, length))
                return true;
        }
        return false;
    }


    public static String FiltrarEDitTextToMoney(String string) {
        try {
            if (string.equalsIgnoreCase("")) {
                return string;
            } else {
                String[] strings = string.split("\\.");
                if (strings.length == 2) {
                    String decimal = strings[1];
                    if (decimal.length() == 2) {

                    } else if (decimal.length() == 1) {
                        string = strings[0] + "." + decimal + "0";

                    } else if (decimal.length() == 0) {
                        string = strings[0] + ".00";

                    } else if (decimal.length() > 2) {
                        String aux = decimal.substring(0, 1);
                        int auxint = Integer.parseInt(aux);
                        char t = decimal.charAt(2);
                        int f = (int) t;
                        if (f <= 5) {
                            string = strings[0] + "." + aux;
                        } else if (f > 5) {
                            auxint = auxint + 1;
                            string = strings[0] + "." + auxint;
                        }
                    } else {

                    }
                }

            }

        } catch (NumberFormatException e) {
            e.printStackTrace();
            return string;
        }

        return string;
    }

    public static boolean isEmailValid(String email) {
        String regExpn =
                "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                        + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                        + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                        + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$";

        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(regExpn, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);

        if (matcher.matches())
            return true;
        else
            return false;
    }


    public static boolean ObjectParamsVer(Object obj){
        for (Field f : obj.getClass().getFields()) {
            f.setAccessible(true);
            try {
                if (f.get(obj) == null) {
                    return false;
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

}
