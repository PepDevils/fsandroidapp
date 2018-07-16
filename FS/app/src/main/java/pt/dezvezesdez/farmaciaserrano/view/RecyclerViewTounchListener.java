package pt.dezvezesdez.farmaciaserrano.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import pt.dezvezesdez.farmaciaserrano.interfaces.Interface_ReclycerViewOnTounchHack;

/**
 * Created by dezvezesdez on 03/05/2017.
 */

public class RecyclerViewTounchListener implements RecyclerView.OnItemTouchListener {


    private Context mc;
    private GestureDetector mgd;
    private Interface_ReclycerViewOnTounchHack mirvoth;

    public RecyclerViewTounchListener(Context mc, final RecyclerView rv, final Interface_ReclycerViewOnTounchHack mirvoth) {
        this.mc = mc;
        this.mirvoth = mirvoth;

        mgd = new GestureDetector(mc, new GestureDetector.SimpleOnGestureListener() {

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                Log.d("RecyclerView", "Item Clicked onSingleTapUp");


                View cv = rv.findChildViewUnder(e.getX(), e.getY());

                if (cv != null && mirvoth != null) {

                    mirvoth.onItemAdapterClickListener(cv, rv.getChildAdapterPosition(cv));

                }


                return true;

            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        mgd.onTouchEvent(e);
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }
}
