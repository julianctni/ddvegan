package com.pasta.ddvegan.activities;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.pasta.ddvegan.R;
import com.pasta.ddvegan.sync.DatabaseUpdater;

public class LoadingActivity extends ActionBarActivity {

    public Animation transUp;
    public Animation transDown;
    public Animation rotate;
    public AnimationSet as;
    public ImageView icon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        DatabaseUpdater updater = new DatabaseUpdater(this);
        updater.execute();


        icon = (ImageView)findViewById(R.id.loading_icon);
        icon.setVisibility(View.VISIBLE);
        TextView tv = (TextView)findViewById(R.id.loading_text);
        tv.setVisibility(View.VISIBLE);

        transUp = new TranslateAnimation(icon.getLeft(), icon.getLeft(), icon.getTop(), icon.getTop()-300);
        transUp.setDuration(600);
        //transUp.setFillAfter(true);

        transDown = new TranslateAnimation(icon.getLeft(), icon.getLeft(), icon.getTop()-300, icon.getTop());
        transDown.setDuration(600);
        //transDown.setFillAfter(true);

        rotate = new RotateAnimation(0,360, Animation.RELATIVE_TO_SELF,0.5f , Animation.RELATIVE_TO_SELF,0.5f);
        rotate.setDuration(400);

        as = new AnimationSet(true);
        as.addAnimation(rotate);
        as.addAnimation(transDown);

        transUp.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                icon.startAnimation(as);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        transDown.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                icon.startAnimation(transUp);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });


        icon.startAnimation(transUp);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_loading, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
