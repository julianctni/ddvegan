package com.pasta.ddvegan.utils;

import android.graphics.drawable.Drawable;


public class NavItem {

    int type;
    String name;
    Drawable tileImage;
    boolean selected;

    public NavItem(int type, Drawable image, String name) {
        this.type = type;
        tileImage = image;
        this.name = name;
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
    public String getName(){
        return name;
    }

    public void setSelected(boolean b){
        this.selected = b;
    }
}
