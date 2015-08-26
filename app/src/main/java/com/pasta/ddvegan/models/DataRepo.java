package com.pasta.ddvegan.models;


import com.pasta.ddvegan.utils.NavGridItem;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;

public class DataRepo {

    public final static int FOOD = 1,
            SHOPPING = 2,
            CAFE = 3,
            BAKERY = 4,
            VOKUE = 5,
            ICECREAM = 6,
            FAVORITES = 7,
            MAP = 8,
            ABOUT = 9;

    public static String appVersion = "";

    public static String apiUrl = "http://www.pastayouth.org/ddvegan/api/v1/";
    public static String apiVeganNews = apiUrl + "get/veganNews";
    public static String apiVeganSpots = apiUrl + "get/veganSpots";
    public static String apiVeganSpotUpdates = apiUrl + "post/veganSpotUpdates";
    public static String apiFeedback = apiUrl + "post/feedback";
    public static String apiReport = apiUrl + "post/report";
    public static String apiImage = apiUrl + "get/image/";

    public static HashMap<Integer, VeganSpot> veganSpots = new HashMap<Integer, VeganSpot>();
    public static HashSet<Integer> chosenMapItems = new HashSet<Integer>();
    public static ArrayList<VeganSpot> foodSpots = new ArrayList<VeganSpot>();
    public static ArrayList<VeganSpot> shoppingSpots = new ArrayList<VeganSpot>();
    public static ArrayList<VeganSpot> cafeSpots = new ArrayList<VeganSpot>();
    public static ArrayList<VeganSpot> bakerySpots = new ArrayList<VeganSpot>();
    public static ArrayList<VeganSpot> vokueSpots = new ArrayList<VeganSpot>();
    public static ArrayList<VeganSpot> icecreamSpots = new ArrayList<VeganSpot>();
    public static HashMap<Integer,VeganSpot> favoriteMap = new HashMap<Integer,VeganSpot>();
    public static ArrayList<VeganSpot> favoriteSpots = new ArrayList<VeganSpot>();


    public static ArrayList<NavGridItem> navGridItems = new ArrayList<NavGridItem>();


    public static void updateFavorites(){
        favoriteSpots.clear();
        favoriteSpots.addAll(favoriteMap.values());
    }

    public static ArrayList<VeganNews> veganNews = new ArrayList<VeganNews>();

    public static boolean hasDistance = false;

    public static void clearSpotLists() {
        veganSpots.clear();
        foodSpots.clear();
        shoppingSpots.clear();
        cafeSpots.clear();
        bakerySpots.clear();
        vokueSpots.clear();
        icecreamSpots.clear();
        favoriteMap.clear();
        favoriteSpots.clear();
    }




    public static void sortByName(ArrayList<VeganSpot> list) {
        VeganSpotNameComparator venueNameComparator = new VeganSpotNameComparator();
        Collections.sort(list, venueNameComparator);
    }

    public static void sortByDistance(ArrayList<VeganSpot> list) {
        VeganSpotDistanceComparator venueDistanceComparator = new VeganSpotDistanceComparator();
        Collections.sort(list, venueDistanceComparator);
    }

    public static void sortByHours(ArrayList<VeganSpot> list) {
        VeganSpotHoursComparator spotHoursComparator = new VeganSpotHoursComparator();
        Collections.sort(list, spotHoursComparator);
    }

    public static class VeganSpotNameComparator implements Comparator<VeganSpot> {

        @Override
        public int compare(VeganSpot v1, VeganSpot v2) {
            Collator collator = Collator.getInstance(Locale.GERMAN);
            collator.setStrength(Collator.SECONDARY);
            if (v1.getName() == null && v2.getName() == null) {
                return 0;
            }
            if (v1.getName() == null) {
                return 1;
            }
            if (v2.getName() == null) {
                return -1;
            }
            return collator.compare(v1.getName(), v2.getName());
        }
    }

    public static class VeganSpotDistanceComparator implements Comparator<VeganSpot> {

        @Override
        public int compare(VeganSpot v1, VeganSpot v2) {
            if (v1.getFloatDistance() > v2.getFloatDistance())
                return 1;
            if (v1.getFloatDistance() < v2.getFloatDistance())
                return -1;
            else
                return 1;
        }
    }

    public static class VeganSpotHoursComparator implements Comparator<VeganSpot> {

        @Override
        public int compare(VeganSpot v1, VeganSpot v2) {
            if (!v1.checkIfOpen() && v2.checkIfOpen())
                return 1;
            if (v1.checkIfOpen() && !v2.checkIfOpen())
                return -1;
            else
                return 1;
        }
    }


}
