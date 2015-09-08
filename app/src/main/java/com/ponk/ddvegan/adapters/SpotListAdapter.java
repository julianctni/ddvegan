package com.ponk.ddvegan.adapters;

import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ponk.ddvegan.R;
import com.ponk.ddvegan.fragments.SpotDetailFragment;
import com.ponk.ddvegan.fragments.SpotListFragment;
import com.ponk.ddvegan.fragments.StartPageFragment;
import com.ponk.ddvegan.models.VeganSpot;

import java.util.ArrayList;


public class SpotListAdapter extends RecyclerView.Adapter<SpotListAdapter.ViewHolder> {

    public ArrayList<VeganSpot> items;
    public Fragment fragment;

    public SpotListAdapter(ArrayList<VeganSpot> items, SpotListFragment fragment) {
        this.items = items;
        this.fragment = fragment;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.spot_list_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        VeganSpot item = items.get(position);
        holder.name.setText(item.getName());
        holder.address.setText(item.getAdresse());
        if (item.getFloatDistance() > 1000.00)
            holder.distance.setText("");
        else
            holder.distance.setText(item.getDistance());
        if (!item.hasHours) {
            holder.openSign.setImageResource(android.R.color.transparent);
            return;
        }
        if (item.checkIfOpen())
            holder.openSign.setImageResource(R.drawable.sign_open);
        else
            holder.openSign.setImageResource(R.drawable.sign_closed);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView name;
        public TextView address;
        public TextView distance;
        public ImageView openSign;
        public CardView card;


        @Override
        public void onClick(View v) {
            if (fragment.getParentFragment() instanceof StartPageFragment)
                fragment = fragment.getParentFragment();
            Fragment detailFragment = SpotDetailFragment.create(items.get(getPosition()).getID());
            fragment.getFragmentManager().beginTransaction()
                    .addToBackStack(null)
                    .replace(R.id.content_frame, detailFragment, "SPOTDETAIL")
                    .commit();
        }

        public ViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.spotName);
            address = (TextView) itemView.findViewById(R.id.spotAddress);
            distance = (TextView) itemView.findViewById(R.id.spotDistance);
            openSign = (ImageView) itemView.findViewById(R.id.spotOpen);
            card = (CardView) itemView.findViewById(R.id.spotCardView);
            itemView.setOnClickListener(this);
        }
    }
}