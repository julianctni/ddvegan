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
    PopupMenu sortMenu;


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
                title = getString(R.string.category_food);
                break;
            case (DataRepo.SHOPPING):
                spots = DataRepo.shoppingSpots;
                title = getString(R.string.category_shopping);
                break;
            case (DataRepo.BAKERY):
                spots = DataRepo.bakerySpots;
                title = getString(R.string.category_bakery);
                break;
            case (DataRepo.ICECREAM):
                spots = DataRepo.icecreamSpots;
                title = getString(R.string.category_icecream);
                break;
            case (DataRepo.VOKUE):
                spots = DataRepo.vokueSpots;
                title = getString(R.string.category_vokue);
                break;
            case (DataRepo.CAFE):
                spots = DataRepo.cafeSpots;
                title = getString(R.string.category_cafe);
                break;
            case (DataRepo.FAVORITES):
                spots = DataRepo.favoriteSpots;
                break;
        }
        DataRepo.sortByName(spots);
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
                        Toast.makeText(getActivity(), getString(R.string.spotlist_toast_gps_success), Toast.LENGTH_SHORT).show();
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
                if (sortMenu == null) {
                    sortMenu = new PopupMenu(this.getActivity(), menuItemView);
                    sortMenu.inflate(R.menu.menu_spotlist_sort);
                    sortMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            switch (menuItem.getItemId()) {
                                case R.id.menu_sort_alphab:
                                    menuItem.setChecked(!menuItem.isChecked());
                                    DataRepo.sortByName(spots);
                                    spotListAdapter.notifyDataSetChanged();
                                    return true;

                                case R.id.menu_sort_distance:
                                    if (DataRepo.hasDistance) {
                                        DataRepo.sortByDistance(spots);
                                        spotListAdapter.notifyDataSetChanged();
                                    } else if (calculateDistances())
                                        menuItem.setChecked(!menuItem.isChecked());
                                    return true;

                                case R.id.menu_sort_hours:
                                    menuItem.setChecked(!menuItem.isChecked());
                                    menuItem.setChecked(true);
                                    DataRepo.sortByHours(spots);
                                    spotListAdapter.notifyDataSetChanged();
                                    return true;
                            }
                            return false;
                        }
                    });
                }
                sortMenu.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /*
     * Get the current location and calculate the distances.
     */
    public boolean calculateDistances() {
        gps.updateLocation();
        if (!gps.isOn()) {
            AlertNoGPS();
            return false;
        } else {
            if (!gps.newLocation) {
                dialog = new ProgressDialog(getActivity());
                dialog.setMessage(getString(R.string.spotlist_dialog_gps_calc));
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.setCancelable(false);
                dialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.dialog_cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        gps.stop();
                    }
                });
                dialog.show();
            }
        }
        return true;
    }

    private void AlertNoGPS() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(getString(R.string.dialog_msg_nogps)).setCancelable(false)
                .setPositiveButton(getString(R.string.dialog_continue), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent callGPSSettingIntent = new Intent(
                                android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(callGPSSettingIntent);
                    }
                });
        builder.setNegativeButton(getString(R.string.dialog_cancel), new DialogInterface.OnClickListener() {
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
        public void onFragmentInteraction(int id);
    }

}
