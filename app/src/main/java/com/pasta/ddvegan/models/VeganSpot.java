package com.pasta.ddvegan.models;

import android.util.Log;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by julian on 28.03.15.
 */
public class VeganSpot implements Comparable<VeganSpot> {
    private String name;
    private String adresse;
    private String URL;
    private String mail;
    private String phone;
    private String imgKey;
    private int id;
    private double GPS_lat;
    private double GPS_long;
    private String info;
    public boolean hasHours = false;
    private boolean isFavorite = false;
    private float distance = -1f;
    public HashMap<Integer, ArrayList<Integer>> timeMap = new HashMap<Integer, ArrayList<Integer>>();
    Calendar cal = Calendar.getInstance();

    public VeganSpot(String name, String adresse, String URL, String imgkey, double GPS_lat,
                     double GPS_long, String mail, String info, int id, String phone, String imgKey) {
        this.name = name;
        this.adresse = adresse;
        this.URL = URL;
        this.mail = mail;
        this.phone = phone;
        this.imgKey = imgkey;
        this.GPS_lat = GPS_lat;
        this.GPS_long = GPS_long;
        this.info = info;
        this.id = id;
        this.imgKey = imgKey;

        for (int i = 1; i <= 7; i++) {
            timeMap.put(i, new ArrayList<Integer>());
        }
    }

    // getter
    public String getName() {
        return name;
    }

    public String getPhone() {
        if (this.phone == null)
            return "";
        return phone;
    }

    public String getMail() {
        if (this.mail == null)
            return "";
        return mail;
    }

    public float getFloatDistance() {
        return distance;
    }

    public int getID() {
        return id;
    }

    public String getImgKey() {
        return imgKey;
    }

    public String getAdresse() {
        return adresse;
    }

    public String getURL() {
        if (this.URL == null)
            return "";
        return URL;
    }


    public double getGPS_lat() {
        return GPS_lat;
    }

    public double getGPS_long() {
        return GPS_long;
    }

    public String getInfo() {
        return info;
    }


    public String getDistance() {
        if (distance == -1f)
            return "";
        else
            return Math.round(distance * 100.0) / 100.0 + " km";
    }

    public void setDistance(float disttance) {
        this.distance = disttance;

    }

    public boolean isFavorite(){
        return isFavorite;
    }

    public void setFavorite(boolean b) {
        isFavorite = b;
    }

    // Hours
    public void addHours(int day, String timeString) {
        if (timeString.equals("null"))
            return;
        timeMap.put(day, new ArrayList<Integer>());
        timeMap.get(day).add(Integer.parseInt(timeString.substring(0, 4)));
        timeMap.get(day).add(Integer.parseInt(timeString.substring(5, 9)));
        if (!hasHours)
            hasHours = true;
        if (timeString.length() == 19) {
            timeMap.get(day).add(Integer.parseInt(timeString.substring(10, 14)));
            timeMap.get(day).add(Integer.parseInt(timeString.substring(15, 19)));
        }
    }

    public String getHoursForDay(int day){
        if (timeMap.get(day).get(0) == 0)
            return "geschlossen";
        String hours = "";
        String h1 = ""+timeMap.get(day).get(0);
        if (h1.length() < 4)
            h1 = "0"+h1;
        String h2 = ""+timeMap.get(day).get(1);
        if (h2.length() < 4)
            h2 = "0"+h2;
        hours += (h1.substring(0,2)+":"+h1.substring(2,4)+" - "+h2.substring(0,2)+":"+h2.substring(2,4));
        if (timeMap.get(day).size()>2) {
            h1 = "" + timeMap.get(day).get(2);
            h2 = "" + timeMap.get(day).get(3);
            hours += (", " + h1.substring(0, 2) + ":" + h1.substring(2, 4) + " - " + h2.substring(0, 2) + ":" + h2.substring(2, 4));
        }
        return hours;
    }

    public boolean checkIfOpen() {
        if (!hasHours)
            return false;
        int day = cal.get(Calendar.DAY_OF_WEEK);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        if (hour < 4) {
            hour = hour + 24;
            if (day == 1)
                day = 7;
            else
                day--;
        }
        int time = (hour * 100) + (cal.get(Calendar.MINUTE));
        ArrayList<Integer> timeList = timeMap.get(day);
        if (timeList.get(0) <= time && time <= timeList.get(1))
            return true;
        else {
            if (timeList.size() == 2)
                return false;
            else {
                if (timeList.get(2) <= time && time <= timeList.get(3))
                    return true;
                else
                    return false;
            }
        }
    }

    public HashMap<Integer, ArrayList<Integer>> getTimeMap() {
        return timeMap;
    }

    // verschiedenes
    public int compareTo(VeganSpot spot) {
        Collator collator = Collator.getInstance(Locale.GERMAN);
        collator.setStrength(Collator.SECONDARY);
        return collator.compare(getName(), spot.getName());
    }

    public String toString() {
        return name + "\n" + adresse;

    }
}
