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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.overlay.Icon;
import com.mapbox.mapboxsdk.overlay.ItemizedIconOverlay;
import com.mapbox.mapboxsdk.overlay.Marker;
import com.mapbox.mapboxsdk.tileprovider.tilesource.MapboxTileLayer;
import com.mapbox.mapboxsdk.views.MapView;
import com.pasta.ddvegan.R;
import com.pasta.ddvegan.models.DataRepo;
import com.pasta.ddvegan.models.VeganSpot;
import com.pasta.ddvegan.utils.GpsUtil;

import java.util.ArrayList;
import java.util.List;


public class MapFragment extends Fragment {


    private OnFragmentInteractionListener mListener;
    protected MapView mapView;/*
    protected ResourceProxy mResourceProxy;
    ArrayList<OverlayItem> bakeryOverlayList = new ArrayList<OverlayItem>();
    ArrayList<OverlayItem> cafeOverlayList = new ArrayList<OverlayItem>();
    ArrayList<OverlayItem> foodOverlayList = new ArrayList<OverlayItem>();
    ArrayList<OverlayItem> icecreamOverlayList = new ArrayList<OverlayItem>();
    ArrayList<OverlayItem> shoppingOverlayList = new ArrayList<OverlayItem>();
    ArrayList<OverlayItem> vokueOverlayList = new ArrayList<OverlayItem>();
    ArrayList<OverlayItem> favOverlayList = new ArrayList<OverlayItem>();
    SpotOverlay bakeryOverlay;
    SpotOverlay cafeOverlay;
    SpotOverlay foodOverlay;
    SpotOverlay icecreamOverlay;
    SpotOverlay shoppingOverlay;
    SpotOverlay vokueOverlay;
    SpotOverlay favOverlay;*/
    /*ItemizedIconOverlay<OverlayItem> singleSpotOverlay;
    ItemizedIconOverlay<OverlayItem> locationMarkerOverlay;*/
    ProgressDialog dialog;
    public static Handler mapHandler;
    private GpsUtil gps = new GpsUtil(this);
    PopupMenu popupMenu;
    boolean singleSpot;
    int singleSpotId;

    SpotOverlay bakeryOverlay;
    SpotOverlay cafeOverlay;
    SpotOverlay foodOverlay;
    SpotOverlay icecreamOverlay;
    SpotOverlay shoppingOverlay;
    SpotOverlay vokueOverlay;
    SpotOverlay favOverlay;

    ArrayList<Marker> bakeryMarkers = new ArrayList<Marker>();
    ArrayList<Marker> cafeMarkers = new ArrayList<Marker>();
    ArrayList<Marker> foodMarkers = new ArrayList<Marker>();
    ArrayList<Marker> icecreamMarkers = new ArrayList<Marker>();
    ArrayList<Marker> shoppingMarkers = new ArrayList<Marker>();
    ArrayList<Marker> vokueMarkers = new ArrayList<Marker>();
    ArrayList<Marker> favMarkers = new ArrayList<Marker>();

    Marker singleSpotMarker;
    Marker currentPositionMarker;


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
        setRetainInstance(true);
        if (getArguments() != null) {
            singleSpot = getArguments().getBoolean("showSingleSpot");
            singleSpotId = getArguments().getInt("spotId");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //mResourceProxy = new ResourceProxyImpl(inflater.getContext().getApplicationContext());
        //mapView = new MapView(inflater.getContext(), 256, mResourceProxy);
        mapView = new MapView(getActivity());
        mapView.setAccessToken("pk.eyJ1IjoicGFzdGFzb2Z0d2FyZSIsImEiOiJhZjJkYjBhNzMyMTNiMzI4ZmY5NDM0MDU1YjJmNTlmZCJ9.-nkTpeqduWxnSeizwuyV2Q");
        mapView.setTileSource(new MapboxTileLayer("mapbox.streets"));
        mapView.setCenter(new LatLng(51.056553, 13.742202));
        mapView.setMaxZoomLevel(20);
        mapView.setZoom(14);
        setHasOptionsMenu(true);
        return mapView;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (!singleSpot) {
            loadOverlays();
            this.reloadMapMind();
        } else {
            Drawable spotMarker = this.getResources().getDrawable(R.drawable.marker_single);
            spotMarker.setBounds(0, 0, 20, 20);
            VeganSpot spot = DataRepo.veganSpots.get(singleSpotId);
            singleSpotMarker = new Marker("" + spot.getID(), spot.getName(), new LatLng(
                    spot.getGPS_lat(), spot.getGPS_long()));
            singleSpotMarker.setIcon(new Icon(this.getResources().getDrawable(R.drawable.marker_single)));
            singleSpotMarker.setHotspot(Marker.HotspotPlace.BOTTOM_CENTER);
            mapView.addMarker(singleSpotMarker);
            mapView.postInvalidate();
        }

        mapHandler = new Handler() {
            public void handleMessage(Message msg) {
                dialog.dismiss();
                Toast.makeText(getActivity(), "Hier bist du!", Toast.LENGTH_LONG)
                        .show();
                mapView.setZoom(14);
                mapView.setCenter(new LatLng(gps.getLatitude(), gps.getLongitude()));
                currentPositionMarker = new Marker("locationMarker", "Marks the location",
                        new LatLng(gps.getLatitude(), gps.getLongitude()));
                currentPositionMarker.setIcon(new Icon(getActivity().getResources().getDrawable(
                        R.drawable.fadenkreuz)));
                currentPositionMarker.setHotspot(Marker.HotspotPlace.CENTER);
                mapView.addMarker(currentPositionMarker);
                mapView.postInvalidate();
                gps.stop();

                super.handleMessage(msg);
            }
        };
    }

    public Handler getHandler(){
        return mapHandler;
    }

    public void loadOverlays() {
        Icon bakeryMarker = new Icon(this.getResources().getDrawable(R.drawable.marker_bakery));
        Icon cafeMarker = new Icon(this.getResources().getDrawable(R.drawable.marker_cafe));
        Icon foodMarker = new Icon(this.getResources().getDrawable(R.drawable.marker_food));
        Icon icecreamMarker = new Icon(this.getResources().getDrawable(R.drawable.marker_icecream));
        Icon shoppingMarker = new Icon(this.getResources().getDrawable(R.drawable.marker_shopping));
        Icon vokueMarker = new Icon(this.getResources().getDrawable(R.drawable.marker_vokue));
        Icon favMarker = new Icon(this.getResources().getDrawable(R.drawable.marker_fav));

        if (foodMarkers.isEmpty()) {
            Log.i("test", foodMarkers.size()+"");
            for (VeganSpot v : DataRepo.foodSpots) {
                Marker m = new Marker(v.getID()+"",v.getName(),new LatLng(v.getGPS_lat(),v.getGPS_long()));
                m.setIcon(foodMarker);
                m.setHotspot(Marker.HotspotPlace.BOTTOM_CENTER);
                foodMarkers.add(m);
                Log.i("test", foodMarkers.size()+"");
            }
            foodOverlay = new SpotOverlay(getActivity(), foodMarkers, null);
        }

        if (shoppingMarkers.isEmpty()) {
            for (VeganSpot v : DataRepo.shoppingSpots) {
                Marker m = new Marker(v.getID()+"",v.getName(),new LatLng(v.getGPS_lat(),v.getGPS_long()));
                m.setIcon(shoppingMarker);
                m.setHotspot(Marker.HotspotPlace.BOTTOM_CENTER);
                shoppingMarkers.add(m);
            }
            shoppingOverlay = new SpotOverlay(getActivity(), shoppingMarkers, null);
        }

        if (bakeryMarkers.isEmpty()) {
            for (VeganSpot v : DataRepo.bakerySpots) {
                Marker m = new Marker(v.getID()+"",v.getName(),new LatLng(v.getGPS_lat(),v.getGPS_long()));
                m.setIcon(bakeryMarker);
                m.setHotspot(Marker.HotspotPlace.BOTTOM_CENTER);
                bakeryMarkers.add(m);
            }
            bakeryOverlay = new SpotOverlay(getActivity(), bakeryMarkers, null);
        }

        if (cafeMarkers.isEmpty()) {
            for (VeganSpot v : DataRepo.cafeSpots) {
                Marker m = new Marker(v.getID()+"",v.getName(),new LatLng(v.getGPS_lat(),v.getGPS_long()));
                m.setIcon(cafeMarker);
                m.setHotspot(Marker.HotspotPlace.BOTTOM_CENTER);
                cafeMarkers.add(m);
            }
            cafeOverlay = new SpotOverlay(getActivity(), cafeMarkers, null);
        }

        if (icecreamMarkers.isEmpty()) {
            for (VeganSpot v : DataRepo.icecreamSpots) {
                Marker m = new Marker(v.getID()+"",v.getName(),new LatLng(v.getGPS_lat(),v.getGPS_long()));
                m.setIcon(icecreamMarker);
                m.setHotspot(Marker.HotspotPlace.BOTTOM_CENTER);
                icecreamMarkers.add(m);
            }
            icecreamOverlay = new SpotOverlay(getActivity(), icecreamMarkers, null);
        }

        if (vokueMarkers.isEmpty()) {
            for (VeganSpot v : DataRepo.vokueSpots) {
                Marker m = new Marker(v.getID()+"",v.getName(),new LatLng(v.getGPS_lat(),v.getGPS_long()));
                m.setIcon(vokueMarker);
                m.setHotspot(Marker.HotspotPlace.BOTTOM_CENTER);
                vokueMarkers.add(m);
            }
            vokueOverlay = new SpotOverlay(getActivity(), vokueMarkers, null);
        }

        if (favMarkers.isEmpty()) {
            for (VeganSpot v : DataRepo.favoriteSpots) {
                Marker m = new Marker(v.getID()+"",v.getName(),new LatLng(v.getGPS_lat(),v.getGPS_long()));
                m.setIcon(favMarker);
                m.setHotspot(Marker.HotspotPlace.BOTTOM_CENTER);
                favMarkers.add(m);
            }
            favOverlay = new SpotOverlay(getActivity(), favMarkers, null);
        }

    }


    public void reloadMapMind() {
        if (!DataRepo.mapMind.isEmpty()) {
            if (DataRepo.mapMind.contains(DataRepo.BAKERY)) {
                mapView.addItemizedOverlay(bakeryOverlay);
                mapView.postInvalidate();
            }
            if (DataRepo.mapMind.contains(DataRepo.CAFE)) {
                mapView.addItemizedOverlay(cafeOverlay);
                mapView.postInvalidate();
            }
            if (DataRepo.mapMind.contains(DataRepo.ICECREAM)) {
                mapView.addItemizedOverlay(icecreamOverlay);
                mapView.postInvalidate();
            }
            if (DataRepo.mapMind.contains(DataRepo.FOOD)) {
                mapView.addItemizedOverlay(foodOverlay);
                mapView.postInvalidate();
            }
            if (DataRepo.mapMind.contains(DataRepo.SHOPPING)) {
                mapView.addItemizedOverlay(shoppingOverlay);
                mapView.postInvalidate();
            }
            if (DataRepo.mapMind.contains(DataRepo.VOKUE)) {
                mapView.addItemizedOverlay(vokueOverlay);
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


    public void handleMenuClick(MenuItem menuItem, SpotOverlay overlay, int mapMindKey) {
        if (!menuItem.isChecked()) {
            menuItem.setChecked(true);
            mapView.addItemizedOverlay(overlay);
            Log.i("Overlay Size", "" + overlay.size());
            Log.i("Overlays", "" + mapView.getItemizedOverlays().size());
            mapView.postInvalidate();
            DataRepo.mapMind.add(mapMindKey);
        } else {
            menuItem.setChecked(false);
            mapView.removeOverlay(overlay);
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


    public class SpotOverlay extends ItemizedIconOverlay {

        public SpotOverlay(Context pContext, List<Marker> pList, OnItemGestureListener<Marker> pOnItemGestureListener) {
            super(pContext, pList, new OnItemGestureListener<Marker>() {
                @Override
                public boolean onItemSingleTapUp(int i, Marker m) {
                    VeganSpot spot = DataRepo.veganSpots.get(Integer.parseInt(m.getTitle()));
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
                public boolean onItemLongPress(int i, Marker m) {
                    return false;
                }
            });
        }


    }

    /*
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
    }*/
}
