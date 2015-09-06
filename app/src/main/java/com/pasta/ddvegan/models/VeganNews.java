package com.pasta.ddvegan.models;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class VeganNews {

    int newsId;
    int newsType;
    int spotId;
    String newsTime;
    String newsContent;
    boolean isNew;

    public VeganNews (int newsId, int newsType, int spotId, String newsContent, String newsTime, boolean isNew){
        this.newsId = newsId;
        this.newsType = newsType;
        this.spotId = spotId;
        this.newsContent = createNewsContent (newsType, newsContent);
        this.newsTime = newsTime;
        this.isNew = isNew;
    }


    /**
     1 -> contact
     2 -> address
     3 -> hours
     4 -> info
     5 -> new spot
     6 -> spot deleted
     7 -> individual news
     */
    public String createNewsContent (int type, String content) {
        switch (type){
            case 1:
                return "Kontaktdaten bei "+content+" geändert.";
            case 2:
                return "Adressdaten bei "+content+" geändert.";
            case 3:
                return "Öffnungszeiten bei "+content+" geändert.";
            case 4:
                return "Infoangaben bei "+content+" geändert.";
            case 5:
                return content+" wurde zur Datenbank hinzugefügt.";
            case 6:
                return content+" wurde aus der Datenbank entfernt.";
            case 7:
                return content;
            case 8:
                return content;
        }
        return "";
    }
    public String getNewsType() {
        String type = "";
        if (isNew)
            type = "#update ";
        if (newsType < 7)
            return type + "#info";
        else if (newsType == 7)
            return "#news";
        else
            return "#welcome";
    }

    public int getNewsTypeInt(){
        return newsType;
    }

    public String formatNewsTime() {
        Calendar currentTime = Calendar.getInstance();
        Calendar messageTime = Calendar.getInstance();

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.GERMAN);
        SimpleDateFormat dfShort = new SimpleDateFormat("HH:mm", Locale.GERMAN);
        SimpleDateFormat dfDate = new SimpleDateFormat("dd. MMM, HH:mm", Locale.GERMAN);

        try {
            messageTime.setTime(df.parse(newsTime));
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }

        if (currentTime.get(Calendar.DAY_OF_YEAR) == (messageTime
                .get(Calendar.DAY_OF_YEAR) + 1))
            return ("gestern, " + dfShort.format(messageTime.getTime())+" Uhr");
        else if (currentTime.get(Calendar.DAY_OF_YEAR) == messageTime
                .get(Calendar.DAY_OF_YEAR))
            return ("heute, " + dfShort.format(messageTime.getTime())+" Uhr");
        else
            return (dfDate.format(messageTime.getTime())+" Uhr");
    }

    public int getSpotId(){
        return spotId;
    }
    public String getNewsContent() {
        return newsContent;
    }

    public boolean isNew(){
        return isNew;
    }
}
