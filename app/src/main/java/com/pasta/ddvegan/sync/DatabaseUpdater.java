package com.pasta.ddvegan.sync;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

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
        dbMan.getVeganSpotsFromDatabase();

        if (!dbMan.dbEmpty()) {
            Log.i("DatabaseUpdater", "Database has already been downloaded.");
            dbMan.close();
            return ret;
        }

        if (NetworkUtil.isConnected(context)) {
            Log.i("DatabaseUpdater", "Downloading complete database.");
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
                    String imgKey = jsonSpot.getString("spotImgKey");
                    String hours = jsonSpot.getString("spotHours");
                    int catFood = jsonSpot.getInt("catFood");
                    int catBakery = jsonSpot.getInt("catBakery");
                    int catShopping = jsonSpot.getInt("catShopping");
                    int catVokue = jsonSpot.getInt("catVokue");
                    int catCafe = jsonSpot.getInt("catCafe");
                    int catIcecream = jsonSpot.getInt("catIcecream");
                    double gpsLat = Float.parseFloat(jsonSpot.getString("spotLocLat"));
                    double gpsLong = Float.parseFloat(jsonSpot.getString("spotLocLong"));
                    Log.i("DatabaseUpdater", "Inserting vegan spot: " + name);
                    ContentValues values = new ContentValues();
                    values.put("spotId", id);
                    values.put("spotName", name);
                    values.put("spotAddress", address);
                    values.put("spotPhone", phone);
                    values.put("spotMail", mail);
                    values.put("spotUrl", url);
                    values.put("spotInfo", info);
                    values.put("spotImgKey", imgKey);
                    values.put("spotHours", hours.replace("\n", "").replace("\r", ""));
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
                dbMan.getVeganSpotsFromDatabase();
            } catch (JSONException e) {
                e.printStackTrace();
                ret = NetworkUtil.serverError;
            }
            db.close();
        } else {
            ret = NetworkUtil.connectionError;
        }

        return -1;
    }

    protected void onPostExecute(Integer result) {
        super.onPostExecute(result);
        if (result <= 0) {
            NewsAndSpotUpdater updater = new NewsAndSpotUpdater(context, result);
            updater.execute();
        } else if (result == NetworkUtil.connectionError){
            Toast.makeText(context, "Um die Appdaten zu initialisieren, benötigst du eine Internetverbindung.", Toast.LENGTH_SHORT).show();
            context.finish();
        } else if (result == NetworkUtil.serverError){
            Toast.makeText(context, "Aus technischen Gründen können die Appdaten nicht initialisiert werden. Versuche es später erneut.", Toast.LENGTH_SHORT).show();
            context.finish();
        }
    }

    public String requestVeganSpots() {

        HttpClient httpclient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(DataRepo.apiVeganSpots);

        InputStream inputStream = null;
        String result = "";
        try {
            HttpResponse response = httpclient.execute(httpGet);
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
