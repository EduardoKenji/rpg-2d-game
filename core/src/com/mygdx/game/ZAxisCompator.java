package com.mygdx.game;

import java.util.Comparator;

/**
 * Created by Mario on 23/06/2017.
 */
class ZAxisComparator implements Comparator<ZOrderableSprite> {

    @Override
    public int compare(ZOrderableSprite o1, ZOrderableSprite o2) {
        if(o1.getY() > o2.getY()) return -1;
        if(o1.getY() < o2.getY()) return 1;
        return 0;
    }
}
