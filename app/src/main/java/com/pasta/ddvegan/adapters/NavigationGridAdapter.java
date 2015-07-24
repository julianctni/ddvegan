package com.pasta.ddvegan.adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.ArrayList;

import com.pasta.ddvegan.R;
import com.pasta.ddvegan.utils.NavGridItem;

public class NavigationGridAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater mInflater;
    private ArrayList<NavGridItem> items;

    public NavigationGridAdapter(Context c, ArrayList<NavGridItem> items) {
        mContext = c;
        mInflater = (LayoutInflater)c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.items = items;
    }

    public int getCount() {
        return items.size();
    }

    public NavGridItem getItem(int position) {
        return items.get(position);
    }


    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.nav_grid_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.navIcon = (ImageView) convertView.findViewById(R.id.navigationImage);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        NavGridItem item = items.get(position);
        viewHolder.navIcon.setImageDrawable(item.getTileImage());
        if (!item.isSelected())
            viewHolder.navIcon.setBackgroundColor(mContext.getResources().getColor(R.color.primary_bright));
        else
            viewHolder.navIcon.setBackgroundColor(mContext.getResources().getColor(R.color.primary));


        return convertView;
    }

    private static class ViewHolder {
        ImageView navIcon;
    }
}