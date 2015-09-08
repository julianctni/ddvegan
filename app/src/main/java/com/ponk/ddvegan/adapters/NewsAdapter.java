package com.ponk.ddvegan.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ponk.ddvegan.R;
import com.ponk.ddvegan.models.VeganNews;

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
            holder.type = (TextView) convertView.findViewById(R.id.news_item_type);
            holder.newsIcon = (ImageView) convertView.findViewById(R.id.news_icon);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.header.setText(news.getNewsContent());
        holder.type.setText(news.getNewsType());
        holder.time.setText(news.formatNewsTime());

        if (news.getNewsTypeInt() < 7) {
            holder.header.setTextColor(Color.parseColor("#DD666666"));
            holder.time.setTextColor(Color.parseColor("#DD888888"));
            holder.type.setTextColor(Color.parseColor("#DD333333"));
            holder.header.setTextSize(18f);
            switch (news.getNewsTypeInt()) {
                case (1):
                    holder.newsIcon.setImageResource(R.drawable.icon_news_contact);
                    break;

                case (2):
                    holder.newsIcon.setImageResource(R.drawable.icon_news_address);
                    break;

                case (3):
                    holder.newsIcon.setImageResource(R.drawable.icon_news_hours);
                    break;

                case (4):
                    holder.newsIcon.setImageResource(R.drawable.icon_news_info);
                    break;
                case (5):
                    holder.newsIcon.setImageResource(R.drawable.icon_news_new);
                    break;
                case (6):
                    holder.newsIcon.setImageResource(R.drawable.icon_news_del);
                    break;
            }
        }else {
            holder.header.setTextColor(Color.parseColor("#FF444444"));
            holder.time.setTextColor(Color.parseColor("#FF888888"));
            holder.type.setTextColor(Color.parseColor("#FF333333"));
            holder.header.setTextSize(20f);
            holder.newsIcon.setImageResource(R.drawable.icon_news);
        }


        return convertView;
    }

    static class ViewHolder {
        TextView type;
        TextView header;
        TextView time;
        ImageView newsIcon;
    }

}
