package com.pasta.ddvegan.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.pasta.ddvegan.R;
import com.pasta.ddvegan.models.DataRepo;
import com.pasta.ddvegan.models.VeganSpot;
import com.pasta.ddvegan.utils.GpsUtil;

import org.osmdroid.ResourceProxy;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.util.ResourceProxyImpl;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.OverlayItem;

import java.util.ArrayList;
import java.util.List;


public class MapFragment extends Fragment {


    private OnFragmentInteractionListener mListener;
    protected MapView mapView;
    protected ResourceProxy mResourceProxy;
    ArrayList<OverlayItem> bakeryOverlayList = new ArrayList<OverlayItem>();
    ArrayList<OverlayItem> cafeOverlayList = new ArrayList<OverlayItem>();
    ArrayList<OverlayItem> foodOverlayList = new ArrayList<OverlayItem>();
    ArrayList<OverlayItem> icecreamOverlayList = new ArrayList<OverlayItem>();
    ArrayList<OverlayItem> shoppingOverlayList = new ArrayList<OverlayItem>();
    ArrayList<OverlayItem> vokueOverlayList = new ArrayList<OverlayItem>();
    ArrayList<OverlayItem> favOverlayList = new ArrayList<OverlayItem>();
    CustomItemizedOverlay bakeryOverlay;
    CustomItemizedOverlay cafeOverlay;
    CustomItemizedOverlay foodOverlay;
    CustomItemizedOverlay icecreamOverlay;
    CustomItemizedOverlay shoppingOverlay;
    CustomItemizedOverlay vokueOverlay;
    CustomItemizedOverlay favOverlay;
    ItemizedIconOverlay<OverlayItem> singleSpotOverlay;
    ItemizedIconOverlay<OverlayItem> locationMarkerOverlay;
    ProgressDialog dialog;
    public static Handler mapHandler;
    private GpsUtil gps = new GpsUtil(this);
    PopupMenu popupMenu;
    boolean singleSpot;
    int singleSpotId;


    public MapFragment() {
    }


    public static MapFragment create(boolean showSingleSpot, int id) {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        args.putBoolean("showSingleSpot", showSingleSpot);
        args.putInt("spotId", id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            singleSpot = getArguments().getBoolean("showSingleSpot");
            singleSpotId = getArguments().getInt("spotId");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mResourceProxy = new ResourceProxyImpl(inflater.getContext().getApplicationContext());
        mapView = new MapView(inflater.getContext(), 256, mResourceProxy);
        setHasOptionsMenu(true);
        return mapView;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mapView.setMultiTouchControls(true);
        mapView.getController().setZoom(13);
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        GeoPoint center = new GeoPoint(51.054503, 13.742888);
        mapView.getController().setCenter(center);

        mapHandler = new Handler() {
            public void handleMessage(Message msg) {
                dialog.dismiss();
                Toast.makeText(getActivity(), "Hier bist du!", Toast.LENGTH_LONG)
                        .show();
                mapView.getController().setZoom(14);
                mapView.getController().setCenter(new GeoPoint(gps.getLatitude(), gps.getLongitude()));
                if (locationMarkerOverlay != null)
                    mapView.getOverlays().remove(locationMarkerOverlay);
                OverlayItem LocationMarkerItem = new OverlayItem("locationMarker", "Marks the location",
                        new GeoPoint(gps.getLatitude(), gps.getLongitude()));
                LocationMarkerItem.setMarker(getActivity().getResources().getDrawable(
                        R.drawable.fadenkreuz));
                LocationMarkerItem.setMarkerHotspot(OverlayItem.HotspotPlace.CENTER);
                ArrayList<OverlayItem> tempList = new ArrayList<OverlayItem>();
                tempList.add(LocationMarkerItem);
                locationMarkerOverlay = new ItemizedIconOverlay<OverlayItem>(getActivity(), tempList,
                        null);

                mapView.getOverlays().add(locationMarkerOverlay);
                mapView.postInvalidate();
                gps.stop();

                super.handleMessage(msg);
            }
        };
        if (!singleSpot) {
            loadOverlays();
            this.reloadMapMind();
        } else {
            Drawable spotMarker = this.getResources().getDrawable(R.drawable.marker_single);
            spotMarker.setBounds(0, 0, 20, 20);
            VeganSpot spot = DataRepo.veganSpots.get(singleSpotId);
            OverlayItem singleSpotItem = new OverlayItem("" + spot.getID(), spot.getName(), new GeoPoint(
                    spot.getGPS_lat(), spot.getGPS_long()));
            singleSpotItem.setMarker(this.getResources().getDrawable(R.drawable.marker_single));
            singleSpotItem.setMarkerHotspot(OverlayItem.HotspotPlace.BOTTOM_CENTER);
            ArrayList<OverlayItem> tempList = new ArrayList<OverlayItem>();
            tempList.add(singleSpotItem);
            singleSpotOverlay = new ItemizedIconOverlay<OverlayItem>(getActivity(), tempList, null);
            mapView.getOverlays().add(singleSpotOverlay);
            mapView.postInvalidate();
        }
    }

    public Handler getHandler(){
        return mapHandler;
    }

    public void loadOverlays() {
        Drawable bakeryMarker = this.getResources().getDrawable(R.drawable.marker_bakery);
        Drawable cafeMarker = this.getResources().getDrawable(R.drawable.marker_cafe);
        Drawable foodMarker = this.getResources().getDrawable(R.drawable.marker_food);
        Drawable icecreamMarker = this.getResources().getDrawable(R.drawable.marker_icecream);
        Drawable shoppingMarker = this.getResources().getDrawable(R.drawable.marker_shopping);
        Drawable vokueMarker = this.getResources().getDrawable(R.drawable.marker_vokue);
        Drawable favMarker = this.getResources().getDrawable(R.drawable.marker_fav);

        icecreamMarker.setBounds(0, 0, 20, 20);
        foodMarker.setBounds(0, 0, 20, 20);
        cafeMarker.setBounds(0, 0, 20, 20);
        bakeryMarker.setBounds(0, 0, 20, 20);
        shoppingMarker.setBounds(0, 0, 20, 20);
        vokueMarker.setBounds(0, 0, 20, 20);
        favMarker.setBounds(0, 0, 20, 20);

        if (foodOverlayList.isEmpty()) {
            for (VeganSpot current : DataRepo.foodSpots) {
                OverlayItem currentItem = new OverlayItem("" + current.getID(), current.getName(),
                        new GeoPoint(current.getGPS_lat(), current.getGPS_long()));
                currentItem.setMarker(foodMarker);
                currentItem.setMarkerHotspot(OverlayItem.HotspotPlace.BOTTOM_CENTER);
                foodOverlayList.add(currentItem);
            }
            foodOverlay = new CustomItemizedOverlay(getActivity(), foodOverlayList);
        }

        if (shoppingOverlayList.isEmpty()) {
            for (VeganSpot current : DataRepo.shoppingSpots) {
                OverlayItem currentItem = new OverlayItem("" + current.getID(), current.getName(),
                        new GeoPoint(current.getGPS_lat(), current.getGPS_long()));
                currentItem.setMarker(shoppingMarker);
                currentItem.setMarkerHotspot(OverlayItem.HotspotPlace.BOTTOM_CENTER);
                shoppingOverlayList.add(currentItem);
            }
            shoppingOverlay = new CustomItemizedOverlay(getActivity(), shoppingOverlayList);
        }

        if (vokueOverlayList.isEmpty()) {
            for (VeganSpot current : DataRepo.vokueSpots) {
                OverlayItem currentItem = new OverlayItem("" + current.getID(), current.getName(),
                        new GeoPoint(current.getGPS_lat(), current.getGPS_long()));
                currentItem.setMarker(vokueMarker);
                currentItem.setMarkerHotspot(OverlayItem.HotspotPlace.BOTTOM_CENTER);
                vokueOverlayList.add(currentItem);
            }
            vokueOverlay = new CustomItemizedOverlay(getActivity(), vokueOverlayList);
        }

        if (bakeryOverlayList.isEmpty()) {
            for (VeganSpot current : DataRepo.bakerySpots) {
                OverlayItem currentItem = new OverlayItem("" + current.getID(), current.getName(),
                        new GeoPoint(current.getGPS_lat(), current.getGPS_long()));
                currentItem.setMarker(bakeryMarker);
                currentItem.setMarkerHotspot(OverlayItem.HotspotPlace.BOTTOM_CENTER);
                bakeryOverlayList.add(currentItem);
            }
            bakeryOverlay = new CustomItemizedOverlay(getActivity(), bakeryOverlayList);
        }

        if (icecreamOverlayList.isEmpty()) {
            for (VeganSpot current : DataRepo.icecreamSpots) {
                OverlayItem currentItem = new OverlayItem("" + current.getID(), current.getName(),
                        new GeoPoint(current.getGPS_lat(), current.getGPS_long()));
                currentItem.setMarker(icecreamMarker);
                currentItem.setMarkerHotspot(OverlayItem.HotspotPlace.BOTTOM_CENTER);
                icecreamOverlayList.add(currentItem);
            }
            icecreamOverlay = new CustomItemizedOverlay(getActivity(), icecreamOverlayList);
        }

        if (cafeOverlayList.isEmpty()) {
            for (VeganSpot current : DataRepo.cafeSpots) {
                OverlayItem currentItem = new OverlayItem("" + current.getID(), current.getName(),
                        new GeoPoint(current.getGPS_lat(), current.getGPS_long()));
                currentItem.setMarker(cafeMarker);
                currentItem.setMarkerHotspot(OverlayItem.HotspotPlace.BOTTOM_CENTER);
                cafeOverlayList.add(currentItem);
            }
            cafeOverlay = new CustomItemizedOverlay(getActivity(), cafeOverlayList);
        }

        if (favOverlayList.isEmpty()) {
            for (VeganSpot current : DataRepo.favoriteSpots) {
                OverlayItem currentItem = new OverlayItem("" + current.getID(), current.getName(),
                        new GeoPoint(current.getGPS_lat(), current.getGPS_long()));
                currentItem.setMarker(favMarker);
                currentItem.setMarkerHotspot(OverlayItem.HotspotPlace.BOTTOM_CENTER);
                favOverlayList.add(currentItem);
            }
            favOverlay = new CustomItemizedOverlay(getActivity(), favOverlayList);
        }

    }

    /*
     * Reload the last map status during runtime.
     */
    public void reloadMapMind() {
        if (!DataRepo.mapMind.isEmpty() /*&& showIt == null*/) {
            if (DataRepo.mapMind.contains(DataRepo.BAKERY)) {
                mapView.getOverlays().add(bakeryOverlay);
                mapView.postInvalidate();
            }
            if (DataRepo.mapMind.contains(DataRepo.CAFE)) {
                mapView.getOverlays().add(cafeOverlay);
                mapView.postInvalidate();
            }
            if (DataRepo.mapMind.contains(DataRepo.ICECREAM)) {
                mapView.getOverlays().add(icecreamOverlay);
                mapView.postInvalidate();
            }
            if (DataRepo.mapMind.contains(DataRepo.FOOD)) {
                mapView.getOverlays().add(foodOverlay);
                mapView.postInvalidate();
            }
            if (DataRepo.mapMind.contains(DataRepo.SHOPPING)) {
                mapView.getOverlays().add(shoppingOverlay);
                mapView.postInvalidate();
            }
            if (DataRepo.mapMind.contains(DataRepo.VOKUE)) {
                mapView.getOverlays().add(vokueOverlay);
                mapView.postInvalidate();
            }
            if (DataRepo.mapMind.contains(DataRepo.FAVORITES)) {
                mapView.getOverlays().add(favOverlay);
                mapView.postInvalidate();
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_map, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }


    public void handleMenuClick(MenuItem menuItem, CustomItemizedOverlay overlay, int mapMindKey) {
        if (!menuItem.isChecked()) {
            menuItem.setChecked(true);
            mapView.getOverlays().add(overlay);
            mapView.postInvalidate();
            DataRepo.mapMind.add(mapMindKey);
        } else {
            menuItem.setChecked(false);
            mapView.getOverlays().remove(overlay);
            mapView.postInvalidate();
            DataRepo.mapMind.remove(mapMindKey);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_show_layers:
                View menuItemView = this.getActivity().findViewById(R.id.menu_show_layers);
                if (popupMenu == null) {
                    popupMenu = new PopupMenu(this.getActivity(), menuItemView);
                    popupMenu.inflate(R.menu.menu_map_layers);
                    if (!singleSpot) {
                        popupMenu.getMenu().findItem(R.id.back_items_map).setChecked(DataRepo.mapMind.contains(DataRepo.BAKERY));
                        popupMenu.getMenu().findItem(R.id.food_items_map).setChecked(DataRepo.mapMind.contains(DataRepo.FOOD));
                        popupMenu.getMenu().findItem(R.id.cafe_items_map).setChecked(DataRepo.mapMind.contains(DataRepo.CAFE));
                        popupMenu.getMenu().findItem(R.id.vokue_items_map).setChecked(DataRepo.mapMind.contains(DataRepo.VOKUE));
                        popupMenu.getMenu().findItem(R.id.shopping_items_map).setChecked(DataRepo.mapMind.contains(DataRepo.SHOPPING));
                        popupMenu.getMenu().findItem(R.id.ice_items_map).setChecked(DataRepo.mapMind.contains(DataRepo.ICECREAM));
                    }
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            switch (menuItem.getItemId()) {
                                case R.id.food_items_map:
                                    handleMenuClick(menuItem, foodOverlay, DataRepo.FOOD);
                                    //showIt = null;
                                    return true;

                                case R.id.shopping_items_map:
                                    handleMenuClick(menuItem, shoppingOverlay, DataRepo.SHOPPING);
                                    //showIt = null;
                                    return true;

                                case R.id.back_items_map:
                                    handleMenuClick(menuItem, bakeryOverlay, DataRepo.BAKERY);
                                    //showIt = null;
                                    return true;

                                case R.id.cafe_items_map:
                                    handleMenuClick(menuItem, cafeOverlay, DataRepo.CAFE);
                                    //showIt = null;
                                    return true;

                                case R.id.ice_items_map:
                                    handleMenuClick(menuItem, icecreamOverlay, DataRepo.ICECREAM);
                                    //showIt = null;
                                    return true;

                                case R.id.vokue_items_map:
                                    handleMenuClick(menuItem, vokueOverlay, DataRepo.VOKUE);
                                    //showIt = null;
                                    return true;
                                case R.id.fav_items_map:
                                    if (!menuItem.isChecked()) {
                                        if (DataRepo.favoriteSpots.isEmpty()) {
                                            Toast.makeText(getActivity(), "Keine Favoriten vorhanden.", Toast.LENGTH_LONG).show();
                                            return true;
                                        }
                                        menuItem.setChecked(true);
                                        mapView.getOverlays().add(favOverlay);
                                        mapView.postInvalidate();
                                        DataRepo.mapMind.add(DataRepo.FAVORITES);
                                    } else {
                                        menuItem.setChecked(false);
                                        mapView.getOverlays().remove(favOverlay);
                                        mapView.postInvalidate();
                                        DataRepo.mapMind.remove(DataRepo.FAVORITES);
                                    }
                                    //showIt = null;
                                    return true;
                                case R.id.showAll_items_map:
                                    DataRepo.mapMind.add(DataRepo.VOKUE);
                                    DataRepo.mapMind.add(DataRepo.SHOPPING);
                                    DataRepo.mapMind.add(DataRepo.ICECREAM);
                                    DataRepo.mapMind.add(DataRepo.BAKERY);
                                    DataRepo.mapMind.add(DataRepo.CAFE);
                                    DataRepo.mapMind.add(DataRepo.FOOD);
                                    //showIt = null;
                                    reloadMapMind();
                                    return true;

                                case R.id.hideAll_items_map:
                                    DataRepo.mapMind.clear();
                                    mapView.getOverlays().clear();
                                    mapView.postInvalidate();
                                    //showIt = null;
                                    return true;
                            }
                            return false;
                        }
                    });
                }
                popupMenu.show();
                return true;

            case R.id.menu_show_location:
                gps.updateLocation();
                if (!gps.isOn())
                    Toast.makeText(getActivity(), "Sie müssen erst die Ortung einschalten!", Toast.LENGTH_SHORT).show();
                else {
                    if (!gps.newLocation) {
                        dialog = new ProgressDialog(getActivity());
                        dialog.setMessage("Searching location...");
                        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        dialog.setCancelable(false);
                        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Abbrechen",
                                new DialogInterface.OnClickListener() {
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
        return super.onOptionsItemSelected(item);
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
    public void onStart() {
        super.onStart();
        DataRepo.chosenMapItems.clear();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }


    public class CustomItemizedOverlay extends ItemizedIconOverlay<OverlayItem> {

        public CustomItemizedOverlay(final Context context, final List<OverlayItem> aList) {
            super(context, aList, new OnItemGestureListener<OverlayItem>() {
                @Override
                public boolean onItemSingleTapUp(final int index, final OverlayItem item) {
                    VeganSpot spot = DataRepo.veganSpots.get(Integer.parseInt(item.getTitle()));
                    if (!DataRepo.chosenMapItems.contains(spot.getID())) {
                        SpotDetailFragment fragment = SpotDetailFragment.create(spot.getID());
                        getFragmentManager().beginTransaction()
                                .replace(R.id.content_frame, fragment)
                                .addToBackStack(null)
                                .commit();
                        DataRepo.chosenMapItems.add(spot.getID());
                    }
                    return false;
                }

                @Override
                public boolean onItemLongPress(final int index, final OverlayItem item) {
                    return false;
                }
            });
        }
    }
}
