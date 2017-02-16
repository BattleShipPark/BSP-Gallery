package com.battleshippark.bsp_gallery.presentation.folders;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 */
class FoldersItemDecoration extends RecyclerView.ItemDecoration {
    private Paint paint;

    FoldersItemDecoration() {
        paint = new Paint();
        paint.setColor(Color.rgb(0x80, 0x80, 0x80));
        paint.setStrokeWidth(1);
    }


    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);

        for (int i = 0; i < parent.getChildCount() - 1; i++) {
            final View child = parent.getChildAt(i);

            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            final int left = child.getPaddingLeft() + params.leftMargin;
            final int right = child.getWidth() - params.rightMargin - child.getPaddingRight();
            final int top = child.getBottom();

            c.drawLine(left, top, right, top, paint);
        }

    }
}
