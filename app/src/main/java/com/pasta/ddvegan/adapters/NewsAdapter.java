package com.pasta.ddvegan.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.pasta.ddvegan.R;
import com.pasta.ddvegan.models.VeganNews;

import java.util.ArrayList;


public class NewsAdapter extends BaseAdapter {

    private ArrayList<VeganNews> newsList;
    protected LayoutInflater mInflater;

    public NewsAdapter(Context context, ArrayList<VeganNews> newsList) {
        this.newsList = newsList;
        mInflater = LayoutInflater.from(context);
    }

    public int getCount() {
        if (newsList == null)
            return 0;
        else
            return newsList.size();
    }

    public Object getItem(int position) {
        return newsList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        VeganNews news = newsList.get(position);
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.news_list_item, null);
            holder = new ViewHolder();
            holder.header = (TextView) convertView.findViewById(R.id.news_item_header);
            holder.time = (TextView) convertView.findViewById(R.id.news_item_time);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (news.isNew())
            holder.header.setTypeface(null, Typeface.BOLD);
        else
            holder.header.setTypeface(null, Typeface.NORMAL);
        holder.header.setText(news.getNewsContent());
        holder.time.setText(news.formatNewsTime());
        return convertView;
    }

    static class ViewHolder {
        TextView header;
        TextView time;
    }

}
