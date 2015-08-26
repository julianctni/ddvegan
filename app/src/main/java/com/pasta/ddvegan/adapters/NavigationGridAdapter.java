package com.pasta.ddvegan.adapters;


import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import com.pasta.ddvegan.R;
import com.pasta.ddvegan.models.DataRepo;
import com.pasta.ddvegan.utils.NavGridItem;

import org.w3c.dom.Text;

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

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.nav_grid_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.navIcon = (ImageView) convertView.findViewById(R.id.navigationImage);
            viewHolder.navText = (TextView) convertView.findViewById(R.id.navigationText);
            viewHolder.seperator = convertView.findViewById(R.id.navListSeperator);
            viewHolder.navItemLayout = (RelativeLayout) convertView.findViewById(R.id.navItemLayout);
            viewHolder.itemSelected = convertView.findViewById(R.id.itemSelected);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        NavGridItem item = items.get(position);


        viewHolder.navText.setText(item.getName());
        if (item.getType() == DataRepo.MAP)
            viewHolder.seperator.setVisibility(View.VISIBLE);
        else
            viewHolder.seperator.setVisibility(View.GONE);

        viewHolder.navIcon.setImageDrawable(item.getTileImage());
        if (!item.isSelected()) {
            viewHolder.itemSelected.setVisibility(View.GONE);
            viewHolder.navItemLayout.setBackgroundColor(Color.parseColor("#EFFFFFFF"));
        } else {
            viewHolder.itemSelected.setVisibility(View.VISIBLE);
            viewHolder.navItemLayout.setBackgroundColor(Color.parseColor("#EFEFEFEF"));
        }


        return convertView;
    }

    private static class ViewHolder {
        View seperator;
        ImageView navIcon;
        TextView navText;
        View itemSelected;
        RelativeLayout navItemLayout;
    }
}