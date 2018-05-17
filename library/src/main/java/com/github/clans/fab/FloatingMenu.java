package com.github.clans.fab;

import android.animation.AnimatorSet;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;

/**
 * Interface for FloatingActionMenu and ExtendedFloatingActionMenu
 *
 * @author Josef Hruška (josef@stepuplabs.io)
 */

public interface FloatingMenu {

    void open(boolean animate);

    void close(boolean animate);

    void setClosedOnTouchOutside(boolean close);

    void setOnMenuToggleListener(FloatingMenuToggleListener listener);

    void setOnMenuButtonClickListener(View.OnClickListener clickListener);

    boolean isOpened();

    ImageView getMenuIconView();

    AnimatorSet getIconToggleAnimatorSet();

    void setIconToggleAnimatorSet(AnimatorSet animatorSet);

    float getMenuX();

    float getMenuY();

    void setMenuButtonShowAnimation(Animation showAnimation);

    void setMenuButtonHideAnimation(Animation hideAnimation);

    void setCorrectPivot();
}
