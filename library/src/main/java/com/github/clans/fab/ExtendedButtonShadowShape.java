package com.github.clans.fab;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.shapes.RectShape;

/**
 * Shape for extended action menu.
 *
 * @author Josef Hruška (josef@stepuplabs.io)
 */

public class ExtendedButtonShadowShape extends RectShape {
    private Context mContext;
    private float mRadius = 75f;
    private float mExtraTopShadow = -0f;
    private float mExtraLeftShadow = -5f;
    private float mButtonPaddingPortrait;
    private float mButtonPaddingLandscape;

    ExtendedButtonShadowShape(Context ctx) {
        super();
        mContext = ctx;
        mButtonPaddingPortrait = ctx.getResources().getDimensionPixelSize(R.dimen.extended_button_shadow_padding_portrait);
        mButtonPaddingLandscape = ctx.getResources().getDimensionPixelSize(R.dimen.extended_button_shadow_padding_landscape);
    }

    @Override
    public void draw(Canvas canvas, Paint paint) { //TODO(pepa): need to change
        if (mContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            canvas.drawRoundRect(new RectF(Util.dpToPx(mContext, mExtraLeftShadow), Util.dpToPx(mContext, mExtraTopShadow), Util.getScreenWidth(mContext) - mButtonPaddingPortrait, Util.dpToPx(mContext, 56f)), mRadius, mRadius, paint);
        } else {
            canvas.drawRoundRect(new RectF(Util.dpToPx(mContext, mExtraLeftShadow), Util.dpToPx(mContext, mExtraTopShadow), Util.getScreenWidth(mContext) - mButtonPaddingLandscape, Util.dpToPx(mContext, 56f)), mRadius, mRadius, paint);
        }
    }
}
