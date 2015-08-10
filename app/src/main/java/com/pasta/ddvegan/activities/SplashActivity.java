package com.pasta.ddvegan.activities;

import android.content.pm.PackageManager;
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
import com.pasta.ddvegan.models.DataRepo;
import com.pasta.ddvegan.sync.DatabaseUpdater;

public class SplashActivity extends ActionBarActivity {

    public Animation transUp;
    public Animation transDown;
    public Animation rotate;
    public AnimationSet as;
    public ImageView icon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        try {
            DataRepo.appVersion = getPackageManager()
                    .getPackageInfo(this.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        DatabaseUpdater updater = new DatabaseUpdater(this);
        updater.execute();


        icon = (ImageView)findViewById(R.id.loading_icon);
        icon.setVisibility(View.VISIBLE);
        TextView tv = (TextView)findViewById(R.id.loading_text);
        tv.setVisibility(View.VISIBLE);

        transUp = new TranslateAnimation(icon.getLeft(), icon.getLeft(), icon.getTop(), icon.getTop()-300);
        transUp.setDuration(600);

        transDown = new TranslateAnimation(icon.getLeft(), icon.getLeft(), icon.getTop()-300, icon.getTop());
        transDown.setDuration(600);

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
}
