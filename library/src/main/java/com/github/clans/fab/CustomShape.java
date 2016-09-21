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
    private float mButtonPadding;

    CustomShape(Context ctx) {
        super();
        mContext = ctx;
        mButtonPadding = ctx.getResources().getDimensionPixelSize(R.dimen.custom_shape_buttonPadding);
    }

    @Override
    public void draw(Canvas canvas, Paint paint) { //
            canvas.drawRoundRect(new RectF(Util.dpToPx(mContext, mExtraLeftShadow), Util.dpToPx(mContext, mExtraTopShadow), Util.getScreenWidth(mContext) - mButtonPadding, Util.dpToPx(mContext, 50f)),mRadius,mRadius, paint);
    }
}
