package com.pasta.ddvegan.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.pasta.ddvegan.R;
import com.pasta.ddvegan.adapters.NewsAdapter;
import com.pasta.ddvegan.models.DataRepo;
import com.pasta.ddvegan.models.VeganNews;


public class NewsFragment extends ListFragment {


    public static NewsAdapter newsAdapter;

    public NewsFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        newsAdapter = new NewsAdapter(getActivity(), DataRepo.veganNews);
        this.setListAdapter(newsAdapter);
    }


    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        VeganNews news = (VeganNews)l.getItemAtPosition(position);
        SpotDetailFragment detailFragment = SpotDetailFragment.create(news.getSpotId());
        this.getParentFragment().getFragmentManager().beginTransaction()
                .replace(R.id.content_frame, detailFragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_news, container, false);
    }


}
