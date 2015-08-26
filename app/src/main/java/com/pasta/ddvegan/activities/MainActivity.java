package com.pasta.ddvegan.activities;

import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.pasta.ddvegan.R;
import com.pasta.ddvegan.adapters.NavigationGridAdapter;
import com.pasta.ddvegan.fragments.AboutFragment;
import com.pasta.ddvegan.fragments.MapFragment;
import com.pasta.ddvegan.fragments.NewsFragment;
import com.pasta.ddvegan.fragments.SpotDetailFragment;
import com.pasta.ddvegan.fragments.SpotListFragment;
import com.pasta.ddvegan.fragments.StartPageFragment;
import com.pasta.ddvegan.models.DataRepo;
import com.pasta.ddvegan.utils.NavGridItem;

import java.util.ArrayList;


public class MainActivity extends ActionBarActivity
        implements SpotListFragment.OnFragmentInteractionListener,
        StartPageFragment.OnFragmentInteractionListener,
        MapFragment.OnFragmentInteractionListener {

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private Toolbar toolbar;
    private ImageView homeButton;
    private Menu menu;
    private ListView navList;
    private FragmentManager fragmentManager;
    private NavigationGridAdapter navAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fragmentManager = getSupportFragmentManager();
        initView();
        setDrawerToggle();
        if (fragmentManager.findFragmentById(R.id.content_frame) == null)
            this.setUpStartPage();
        homeButton = (ImageView)findViewById(R.id.home_button);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment current = fragmentManager.findFragmentById(R.id.content_frame);
                if (!(current instanceof StartPageFragment))
                    setUpStartPage();
            }
        });
    }

    private void initView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle(null);
        }
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navList = (ListView) findViewById(R.id.navigationGrid);
        if (DataRepo.navGridItems.isEmpty())
            this.setUpGridItems();
        navAdapter = new NavigationGridAdapter(this, DataRepo.navGridItems);

        navList.setAdapter(navAdapter);
        navList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                selectItem((NavGridItem) navList.getItemAtPosition(position));
            }
        });
    }

    public void setUpGridItems() {
        DataRepo.navGridItems.add(new NavGridItem(DataRepo.SHOPPING, getResources().getDrawable(R.drawable.button_shopping),"Einkaufsmöglichkeiten"));
        DataRepo.navGridItems.add(new NavGridItem(DataRepo.FOOD, getResources().getDrawable(R.drawable.button_food),"Essen und Trinken"));
        DataRepo.navGridItems.add(new NavGridItem(DataRepo.BAKERY, getResources().getDrawable(R.drawable.button_bakery),"Backwaren"));
        DataRepo.navGridItems.add(new NavGridItem(DataRepo.CAFE, getResources().getDrawable(R.drawable.button_cafe),"Kaffee und Kuchen"));
        DataRepo.navGridItems.add(new NavGridItem(DataRepo.ICECREAM, getResources().getDrawable(R.drawable.button_icecream),"Eiscreme"));
        DataRepo.navGridItems.add(new NavGridItem(DataRepo.VOKUE, getResources().getDrawable(R.drawable.button_vokue),"Volxküchen"));
        DataRepo.navGridItems.add(new NavGridItem(DataRepo.MAP, getResources().getDrawable(R.drawable.button_map),"Kartenansicht"));
        DataRepo.navGridItems.add(new NavGridItem(DataRepo.ABOUT, getResources().getDrawable(R.drawable.button_info),"Impressum"));
    }

    private void selectItem(NavGridItem item) {
        if (item.isSelected()) {
            Toast.makeText(this, "Du befindest dich schon auf dieser Seite.", Toast.LENGTH_SHORT).show();
            return;
        }
        Fragment fragment;
        for (NavGridItem n : DataRepo.navGridItems)
            n.setSelected(false);
        item.setSelected(true);
        navAdapter.notifyDataSetChanged();
        String fragTag = "";
        if (item.getType() == DataRepo.MAP) {
            fragment = new MapFragment();
            fragTag = "MAP";
        } else if (item.getType() == DataRepo.ABOUT) {
            fragment = new AboutFragment();
            fragTag = "ABOUT";
        } else {
            fragment = SpotListFragment.create(item.getType());
            fragTag = "SPOTLIST"+item.getType();
        }
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, fragment, fragTag)
                .commit();

        drawerLayout.closeDrawers();
    }

    private void setUpStartPage() {
        for (NavGridItem n : DataRepo.navGridItems)
            n.setSelected(false);
        navAdapter.notifyDataSetChanged();
        Fragment fragment = new StartPageFragment();
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, fragment, "STARTPAGE")
                .commit();
        if (NewsFragment.newsAdapter != null)
            NewsFragment.newsAdapter.notifyDataSetChanged();
    }

    private void setDrawerToggle() {
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_closed) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
        drawerLayout.setDrawerListener(drawerToggle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_spotlist, menu);
        menu.findItem(R.id.menu_sort).setVisible(false);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    public void onBackPressed() {
        Fragment current = fragmentManager.findFragmentById(R.id.content_frame);
        if (current instanceof SpotDetailFragment)
            fragmentManager.popBackStack();
        else if (!(current instanceof StartPageFragment)) {
            this.setUpStartPage();
        } else {
            StartPageFragment frag = (StartPageFragment) current;
            if (frag.mViewPager.getCurrentItem() > 0)
                frag.mViewPager.setCurrentItem(0);
            else
                super.onBackPressed();
        }
    }

    @Override
    public void onFragmentInteraction(int venueId) {}

    @Override
    public void onFragmentInteraction(Uri uri) {}
}
