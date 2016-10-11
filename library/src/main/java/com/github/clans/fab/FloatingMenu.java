package com.github.clans.fab;

/**
 * WRITE DESCRIPTION PLS
 *
 * @author Josef Hruška (josef@stepuplabs.io)
 */

public interface FloatingMenu {
    void open(boolean animate);

    void close(boolean animate);

    boolean isMenuHidden();

    boolean isMenuButtonHidden();

    void showMenuButton(boolean animate);

    void hideMenuButton(boolean animate);

    void showMenu(boolean animate);

    void hideMenu(boolean animate);
}
