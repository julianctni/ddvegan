package com.pasta.ddvegan.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.pasta.ddvegan.R;
import com.pasta.ddvegan.models.VeganNews;

import java.util.ArrayList;


public class NewsAdapter extends BaseAdapter {

    private ArrayList<VeganNews> news;
    protected LayoutInflater mInflater;

    public NewsAdapter(Context context, ArrayList<VeganNews> news) {
        this.news = news;
        mInflater = LayoutInflater.from(context);
    }

    public int getCount() {
        if (news == null)
            return 0;
        else
            return news.size();
    }

    public Object getItem(int position) {
        return news.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
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

        holder.header.setText(news.get(position).getNewsContent());
        holder.time.setText(news.get(position).formatNewsTime());
        return convertView;
    }

    static class ViewHolder {
        TextView header;
        TextView time;
    }

}
