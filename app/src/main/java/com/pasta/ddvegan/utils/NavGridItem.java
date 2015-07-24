package com.pasta.ddvegan.utils;

import android.graphics.drawable.Drawable;

/**
 * Created by julian on 28.03.15.
 */
public class NavGridItem {

    int type;
    Drawable tileImage;
    boolean selected;

    public NavGridItem(int type, Drawable image) {
        this.type = type;
        tileImage = image;
    }

    public Drawable getTileImage() {
        return tileImage;
    }

    public int getType (){
        return type;
    }

    public boolean isSelected(){
        return selected;
    }

    public void setSelected(boolean b){
        this.selected = b;
    }
}
