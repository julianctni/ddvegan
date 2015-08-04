package com.pasta.ddvegan.sync;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import com.pasta.ddvegan.activities.SplashActivity;
import com.pasta.ddvegan.models.DataRepo;
import com.pasta.ddvegan.utils.NetworkUtil;


public class DatabaseUpdater extends AsyncTask<Integer, Integer, Integer> {
    SplashActivity context;

    public DatabaseUpdater(SplashActivity context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Integer doInBackground(Integer... ints) {
        int ret = 0;
        String result = "";
        DatabaseManager dbMan = new DatabaseManager(context);
        DataRepo.clearSpotLists();
        dbMan.getVeganSpotsFromDatabase();
        if (!DataRepo.veganSpots.isEmpty()) {
            dbMan.close();
            return 0;
        }
        if (NetworkUtil.isConnected(context)) {
            result = requestVeganSpots();
            SQLiteDatabase db = dbMan.getWritableDatabase();
            db.execSQL ("DELETE FROM veganSpots");
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

                    Log.i("SQLITE", "inserting veganSpot" + id);
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
                    db.insert("veganSpots", null, values);
                }
            } catch (JSONException e) {
                //ret = Utils.serverError;
                e.printStackTrace();
            }
            db.close();
        } else {
            //ret = Utils.noInternet;
        }

        return ret;
    }

    protected void onPostExecute(Integer result) {
        super.onPostExecute(result);
        NewsAndSpotUpdater updater = new NewsAndSpotUpdater(context);
        updater.execute();
    }

    public String requestVeganSpots() {

        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost("http://ddvegan.pastayouth.org/json/veganSpots.json");

        InputStream inputStream = null;
        String result = null;
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
