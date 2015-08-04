package com.pasta.ddvegan.sync;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
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

import com.pasta.ddvegan.R;
import com.pasta.ddvegan.activities.SplashActivity;
import com.pasta.ddvegan.activities.MainActivity;
import com.pasta.ddvegan.models.DataRepo;
import com.pasta.ddvegan.utils.NetworkUtil;


public class NewsAndSpotUpdater extends AsyncTask<Integer, Integer, Integer> {
    SplashActivity context;

    public NewsAndSpotUpdater(SplashActivity context) {
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

        if (NetworkUtil.isConnected(context)) {
            result = requestNews(dbMan.getMaxNewsId());
            SQLiteDatabase db = dbMan.getWritableDatabase();
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
                //ret = Utils.serverError;
                e.printStackTrace();
            }
            db.close();
            DataRepo.veganNews.clear();
            dbMan.getVeganNewsFromDatabase();
            Collections.reverse(DataRepo.veganNews);
        } else {
            //ret = Utils.noInternet;
        }

        return ret;
    }

    protected void onPostExecute(Integer result) {
        super.onPostExecute(result);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                Animation x = new AlphaAnimation(1,0);
                x.setFillAfter(true);
                x.setDuration(200);
                x.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {}

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        Intent intent = new Intent(context, MainActivity.class);
                        context.startActivity(intent);
                        context.overridePendingTransition(R.anim.anim_slide_in, R.anim.anim_slide_out);
                        context.finish();
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {}
                });
                context.icon.startAnimation(x);

            }
        }, 2000);
    }

    public String requestNews(int maxId) {

        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost("http://ddvegan.pastayouth.org/getVeganNews.php");

        try {
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);
            nameValuePairs.add(new BasicNameValuePair("maxId", "" + maxId));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
        } catch (IOException e) {
        }

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
