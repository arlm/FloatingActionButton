package com.github.clans.fab;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.shapes.RectShape;

/**
 * Custom round-rectangle shape for ripple effect.
 *
 * @author Josef Hruška (josef@stepuplabs.io)
 */

public class CustomShape extends RectShape {
    private Context mContext;
    private float mRadius = 75f;
    private float mExtraTopShadow = -7f;
    private float mExtraLeftShadow = -5f;
    private float mButtonPadding = 64f;

    CustomShape(Context ctx) {
        super();
        mContext = ctx;
    }

    @Override
    public void draw(Canvas canvas, Paint paint) { //
            canvas.drawRoundRect(new RectF(Util.dpToPx(mContext, mExtraLeftShadow), Util.dpToPx(mContext, mExtraTopShadow), Util.getScreenWidth(mContext) - Util.dpToPx(mContext, mButtonPadding), Util.dpToPx(mContext, 50f)),mRadius,mRadius, paint);
    }
    //TODO(pepa): It seems it has actually no effect if we use it or not - might be useful?.
//   @Override
//
//    public void getOutline(Outline outline) {
//        final RectF rect = new RectF(Util.dpToPx(mContext, -4f), Util.dpToPx(mContext, -10f), Util.getScreenWidth(mContext) - Util.dpToPx(mContext, 5f), Util.dpToPx(mContext, 60f));
//        outline.setRoundRect((int) rect.left, (int) Math.ceil(rect.top),
//                (int) Math.floor(rect.right), (int) Math.floor(rect.bottom),70f);
//    }
}
