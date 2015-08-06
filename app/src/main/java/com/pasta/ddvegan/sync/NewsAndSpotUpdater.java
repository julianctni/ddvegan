package com.pasta.ddvegan.sync;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Toast;

import com.pasta.ddvegan.R;
import com.pasta.ddvegan.activities.SplashActivity;
import com.pasta.ddvegan.activities.MainActivity;
import com.pasta.ddvegan.models.DataRepo;
import com.pasta.ddvegan.utils.NetworkUtil;


public class NewsAndSpotUpdater extends AsyncTask<Integer, Integer, Integer> {
    SplashActivity context;
    DatabaseManager dbMan;
    SQLiteDatabase db;
    int ret = 0;
    boolean freshDB = false;

    boolean updateSpots = false;

    public NewsAndSpotUpdater(SplashActivity context, int result) {
        this.context = context;
        freshDB = (result == -1);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Integer doInBackground(Integer... ints) {
        dbMan = new DatabaseManager(context);
        db = dbMan.getWritableDatabase();

        if (NetworkUtil.isConnected(context)) {
            HashSet<Integer> spots = updateNews();
            if (!freshDB)
                updateVeganSpots(spots);
        } else {
            ret = NetworkUtil.connectionError;
        }

        return ret;
    }

    protected void onPostExecute(Integer result) {
        super.onPostExecute(result);
        switch (result) {
            case NetworkUtil.connectionError:
                Toast.makeText(context, "Zum Aktualisieren der Daten ist eine Internetverbindung notwendig.", Toast.LENGTH_SHORT).show();
                break;
            case NetworkUtil.serverError:
                Toast.makeText(context, "Aus technischen Gründen konnten keine Daten aktualisiert werden.", Toast.LENGTH_SHORT).show();
                break;
        }
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                Animation x = new AlphaAnimation(1, 0);
                x.setFillAfter(true);
                x.setDuration(200);
                x.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        Intent intent = new Intent(context, MainActivity.class);
                        context.startActivity(intent);
                        context.overridePendingTransition(R.anim.anim_slide_in, R.anim.anim_slide_out);
                        context.finish();
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                });
                context.icon.startAnimation(x);

            }
        }, 2000);
    }


    public void updateVeganSpots(HashSet<Integer> spots) {
        if (spots.isEmpty())
            return;
        JSONObject spotObj = new JSONObject();
        JSONArray idArray = new JSONArray();
        for (int i : spots) {
            idArray.put(i + "");
        }
        try {
            spotObj.put("spotIds", idArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.i("NewsAndSpotUpdater", spotObj.toString());

        String result = requestSpotUpdates(spotObj.toString());
        JSONArray jArray = null;
        try {
            jArray = new JSONArray(result);
            for (int i = 0; i < jArray.length(); i++) {
                JSONObject jsonSpot = jArray.getJSONObject(i);
                int id = jsonSpot.getInt("spotId");
                String name = jsonSpot.getString("spotName");
                String address = jsonSpot.getString("spotAddress");
                String phone = jsonSpot.getString("spotPhone");
                String mail = jsonSpot.getString("spotMail");
                String url = jsonSpot.getString("spotUrl");
                String info = jsonSpot.getString("spotInfo");
                String hours = jsonSpot.getString("spotHours");
                String imgKey = jsonSpot.getString("spotImgKey");
                String hoursMon = jsonSpot.getString("hoursMon");
                String hoursTue = jsonSpot.getString("hoursTue");
                String hoursWed = jsonSpot.getString("hoursWed");
                String hoursThu = jsonSpot.getString("hoursThu");
                String hoursFri = jsonSpot.getString("hoursFri");
                String hoursSat = jsonSpot.getString("hoursSat");
                String hoursSun = jsonSpot.getString("hoursSun");
                int catFood = jsonSpot.getInt("catFood");
                int catBakery = jsonSpot.getInt("catBakery");
                int catShopping = jsonSpot.getInt("catShopping");
                int catVokue = jsonSpot.getInt("catVokue");
                int catCafe = jsonSpot.getInt("catCafe");
                int catIcecream = jsonSpot.getInt("catIcecream");
                double gpsLat = Float.parseFloat(jsonSpot.getString("spotLocLat"));
                double gpsLong = Float.parseFloat(jsonSpot.getString("spotLocLong"));

                Log.i("SQLITE", "updating veganSpot" + id);
                ContentValues values = new ContentValues();
                values.put("spotId", id);
                values.put("spotName", name);
                values.put("spotAddress", address);
                values.put("spotPhone", phone);
                values.put("spotMail", mail);
                values.put("spotUrl", url);
                values.put("spotInfo", info);
                values.put("spotHours", hours);
                values.put("spotImgKey", imgKey);
                values.put("hoursMon", hoursMon);
                values.put("hoursTue", hoursTue);
                values.put("hoursWed", hoursWed);
                values.put("hoursThu", hoursThu);
                values.put("hoursFri", hoursFri);
                values.put("hoursSat", hoursSat);
                values.put("hoursSun", hoursSun);
                values.put("catFood", catFood);
                values.put("catShopping", catShopping);
                values.put("catCafe", catCafe);
                values.put("catIcecream", catIcecream);
                values.put("catVokue", catVokue);
                values.put("catBakery", catBakery);
                values.put("spotLocLong", gpsLong);
                values.put("spotLocLat", gpsLat);
                db.insertWithOnConflict("veganSpots", null, values, SQLiteDatabase.CONFLICT_REPLACE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            ret = NetworkUtil.serverError;
        }
        dbMan.getVeganSpotsFromDatabase();

    }


    public HashSet<Integer> updateNews() {
        HashSet<Integer> updateTheseSpots = new HashSet<Integer>();
        String result = requestNews(dbMan.getMaxNewsId());
        JSONArray jArray = null;
        try {
            jArray = new JSONArray(result);
            for (int i = 0; i < jArray.length(); i++) {
                JSONObject jsonNews = jArray.getJSONObject(i);
                int newsId = jsonNews.getInt("newsId");
                int newsType = jsonNews.getInt("newsType");
                int spotId = jsonNews.getInt("spotId");
                String newsContent = jsonNews.getString("newsContent");
                String newsTime = jsonNews.getString("newsTime");
                if (newsType <= 6) {
                    updateSpots = true;
                    if (newsType == 6)
                        dbMan.deleteVeganSpotFromDb(spotId);
                    else {
                        updateTheseSpots.add(spotId);
                    }
                }
                Log.i("SQLITE", "inserting news" + newsId);
                ContentValues values = new ContentValues();
                values.put("newsId", newsId);
                values.put("newsType", newsType);
                values.put("spotId", spotId);
                values.put("newsContent", newsContent);
                values.put("newsTime", newsTime);
                db.insert("veganNews", null, values);
            }
        } catch (JSONException e) {
            ret = NetworkUtil.serverError;
            //e.printStackTrace();
        }

        DataRepo.veganNews.clear();
        dbMan.getVeganNewsFromDatabase();
        Collections.reverse(DataRepo.veganNews);

        return updateTheseSpots;
    }

    public String requestNews(int maxId) {

        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost("http://ddvegan.pastayouth.org/getVeganNews.php");

        try {
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
            nameValuePairs.add(new BasicNameValuePair("maxId", "" + maxId));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
        } catch (IOException e) {
        }

        InputStream inputStream = null;
        String result = "";
        try {
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();

            inputStream = entity.getContent();
            // json is UTF-8 by default
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    inputStream, "UTF-8"), 8);
            StringBuilder sb = new StringBuilder();

            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            result = sb.toString();
        } catch (Exception e) {
        } finally {
            try {
                if (inputStream != null)
                    inputStream.close();
            } catch (Exception squish) {
            }
        }
        return result;
    }

    public String requestSpotUpdates(String spotIds) {

        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost("http://ddvegan.pastayouth.org/updateVeganSpot.php");

        try {
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
            nameValuePairs.add(new BasicNameValuePair("spotIds", "" + spotIds));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
        } catch (IOException e) {
        }

        InputStream inputStream = null;
        String result = "";
        try {
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();

            inputStream = entity.getContent();
            // json is UTF-8 by default
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    inputStream, "UTF-8"), 8);
            StringBuilder sb = new StringBuilder();

            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            result = sb.toString();
        } catch (Exception e) {
        } finally {
            try {
                if (inputStream != null)
                    inputStream.close();
            } catch (Exception squish) {
            }
        }
        return result;
    }
}
