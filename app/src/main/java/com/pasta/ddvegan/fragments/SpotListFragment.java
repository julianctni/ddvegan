package com.pasta.ddvegan.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.pasta.ddvegan.R;
import com.pasta.ddvegan.adapters.SpotListAdapter;
import com.pasta.ddvegan.models.DataRepo;
import com.pasta.ddvegan.models.VeganSpot;
import com.pasta.ddvegan.utils.GpsUtil;
import com.pasta.ddvegan.utils.HideTopBar;

import java.util.ArrayList;


public class SpotListFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    int type;
    String title = "";
    ArrayList<VeganSpot> spots;
    RecyclerView rv;
    LinearLayoutManager layoutParams;
    public static SpotListAdapter spotListAdapter;
    LinearLayout spotListHeader;
    public static Handler listHandler;
    GpsUtil gps;
    ProgressDialog dialog;


    public static SpotListFragment create(int type) {
        SpotListFragment fragment = new SpotListFragment();
        Bundle args = new Bundle();
        args.putInt("spotType", type);
        fragment.setArguments(args);
        return fragment;
    }

    public SpotListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            type = getArguments().getInt("spotType");
        }
        if (type != DataRepo.FAVORITES)
            setRetainInstance(true);
        spots = new ArrayList<VeganSpot>();
        switch (type) {
            case (DataRepo.FOOD):
                spots = DataRepo.foodSpots;
                title = "Restaurants & co.";
                break;
            case (DataRepo.SHOPPING):
                spots = DataRepo.shoppingSpots;
                title = "Einkaufsmöglichkeiten";
                break;
            case (DataRepo.BAKERY):
                spots = DataRepo.bakerySpots;
                title = "Backwaren";
                break;
            case (DataRepo.ICECREAM):
                spots = DataRepo.icecreamSpots;
                title = "Eiscreme";
                break;
            case (DataRepo.VOKUE):
                spots = DataRepo.vokueSpots;
                title = "Volxküchen";
                break;
            case (DataRepo.CAFE):
                spots = DataRepo.cafeSpots;
                title = "Café & Kuchen";
                break;
            case (DataRepo.FAVORITES):
                spots = DataRepo.favoriteSpots;
                break;
        }
        gps = new GpsUtil(this);
        listHandler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 0:
                        for (VeganSpot current : DataRepo.veganSpots.values()) {
                            if (current.getGPS_long() != 0) ;
                            current.setDistance(gps.calculateDistance(current.getGPS_lat(), current.getGPS_long()));
                        }
                        dialog.dismiss();
                        Toast.makeText(getActivity(), "Entfernungen wurden ermittelt!", Toast.LENGTH_SHORT).show();
                        DataRepo.hasDistance = true;
                        DataRepo.sortByDistance(spots);
                        spotListAdapter.notifyDataSetChanged();
                        gps.stop();

                }
                super.handleMessage(msg);
            }
        };
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = (View) inflater.inflate(R.layout.fragment_spot_list,
                container, false);
        TextView tv = (TextView) rootView.findViewById(R.id.venue_list_header);
        RelativeLayout noFavLayout = (RelativeLayout) rootView.findViewById(R.id.noFavLayout);
        if (type == DataRepo.FAVORITES) {
            tv.setVisibility(View.GONE);
            if (spots.isEmpty())
                noFavLayout.setVisibility(View.VISIBLE);
        } else
            tv.setText(title);
        setHasOptionsMenu(true);
        this.setUpRecyclerView(rootView);
        return rootView;
    }


    private void setUpRecyclerView(View rootView) {
        layoutParams = new LinearLayoutManager(getActivity());
        spotListAdapter = new SpotListAdapter(spots,this);
        rv = (RecyclerView) rootView.findViewById(R.id.spotList);
        rv.setAdapter(spotListAdapter);
        rv.setLayoutManager(layoutParams);
        spotListHeader = (LinearLayout) rootView.findViewById(R.id.venue_list_header_layout);
        spotListHeader.measure(0, 0);
        rv.addItemDecoration(new HideTopBar(spotListHeader
                .getMeasuredHeight()));
        rv.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView,
                                             int newState) {
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                if (dy > 5) {
                    if (spotListHeader.getVisibility() == View.VISIBLE) {
                        spotListHeader.startAnimation(AnimationUtils.loadAnimation(getActivity(),
                                R.anim.abc_slide_out_top));
                        spotListHeader.setVisibility(View.GONE);
                    }
                } else if (dy < -5) {
                    if (spotListHeader.getVisibility() == View.GONE)
                        spotListHeader.startAnimation(AnimationUtils.loadAnimation(getActivity(),
                                R.anim.abc_slide_in_top));
                    spotListHeader.setVisibility(View.VISIBLE);

                }
            }
        });
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_spotlist, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_sort:

                View menuItemView = this.getActivity().findViewById(R.id.menu_sort);
                PopupMenu popupMenu = new PopupMenu(this.getActivity(), menuItemView);
                popupMenu.inflate(R.menu.menu_spotlist_sort);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.menu_sort_alphab:
                                DataRepo.sortByName(spots);
                                spotListAdapter.notifyDataSetChanged();
                                return true;

                            case R.id.menu_sort_distance:
                                if (DataRepo.hasDistance) {
                                    DataRepo.sortByDistance(spots);
                                    spotListAdapter.notifyDataSetChanged();
                                } else
                                    calculateDistances();
                                return true;

                            case R.id.menu_sort_hours:
                                DataRepo.sortByHours(spots);
                                spotListAdapter.notifyDataSetChanged();
                                return true;
                        }
                        return false;
                    }
                });
                popupMenu.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /*
     * Get the current location and calculate the distances.
     */
    public void calculateDistances() {
        gps.updateLocation();
        if (!gps.isOn())
            AlertNoGPS();
        else {
            if (!gps.newLocation) {
                dialog = new ProgressDialog(getActivity());
                dialog.setMessage("Calculating distances");
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.setCancelable(false);
                dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        gps.stop();
                    }
                });
                dialog.show();
            }
        }
    }

    /*
 * Alert, if Location is switched off.
 */
    private void AlertNoGPS() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("no gps").setCancelable(false)
                .setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent callGPSSettingIntent = new Intent(
                                android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(callGPSSettingIntent);
                    }
                });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public Handler getHandler(){
        return listHandler;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(int id);
    }

}
