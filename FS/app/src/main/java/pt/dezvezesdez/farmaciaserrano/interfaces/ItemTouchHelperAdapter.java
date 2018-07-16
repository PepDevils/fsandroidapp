package pt.dezvezesdez.farmaciaserrano.interfaces;

import android.graphics.Canvas;
import android.support.v7.widget.RecyclerView;

/**
 * Created by dezvezesdez on 09/05/2017.
 */

public interface ItemTouchHelperAdapter {
    boolean onItemMove(int fromPosition, int toPosition);
    void onItemDismiss(int position);
    void OnChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive);
}
