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
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;

import com.pasta.ddvegan.adapters.NavigationGridAdapter;
import com.pasta.ddvegan.R;
import com.pasta.ddvegan.fragments.AboutFragment;
import com.pasta.ddvegan.fragments.MapFragment;
import com.pasta.ddvegan.fragments.NewsFragment;
import com.pasta.ddvegan.fragments.SpotDetailFragment;
import com.pasta.ddvegan.fragments.SpotListFragment;
import com.pasta.ddvegan.fragments.StartPageFragment;
import com.pasta.ddvegan.models.DataRepo;
import com.pasta.ddvegan.models.VeganSpot;
import com.pasta.ddvegan.sync.DatabaseUpdater;
import com.pasta.ddvegan.utils.NavGridItem;


public class MainActivity extends ActionBarActivity
        implements SpotListFragment.OnFragmentInteractionListener,
        StartPageFragment.OnFragmentInteractionListener,
        MapFragment.OnFragmentInteractionListener {

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private Toolbar toolbar;
    private ImageView homeButton;
    private Menu menu;
    private GridView navigationGrid;
    private FragmentManager fragmentManager;
    private ArrayList<NavGridItem> navGridItems = new ArrayList<NavGridItem>();
    private NavigationGridAdapter navAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragmentManager = getSupportFragmentManager();
        initView();
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle(null);
        }
        initDrawer();
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
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationGrid = (GridView) findViewById(R.id.navigationGrid);
        if (navGridItems.isEmpty())
            this.setUpGridItems();
        navAdapter = new NavigationGridAdapter(this, navGridItems);
        navigationGrid.setAdapter(navAdapter);
        navigationGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                selectItem((NavGridItem) navigationGrid.getItemAtPosition(position));
            }
        });
    }


    public void setUpGridItems() {
        navGridItems.add(new NavGridItem(DataRepo.SHOPPING, getResources().getDrawable(R.drawable.button_shopping)));
        navGridItems.add(new NavGridItem(DataRepo.FOOD, getResources().getDrawable(R.drawable.button_food)));
        navGridItems.add(new NavGridItem(DataRepo.BAKERY, getResources().getDrawable(R.drawable.button_bakery)));
        navGridItems.add(new NavGridItem(DataRepo.CAFE, getResources().getDrawable(R.drawable.button_cafe)));
        navGridItems.add(new NavGridItem(DataRepo.ICECREAM, getResources().getDrawable(R.drawable.button_icecream)));
        navGridItems.add(new NavGridItem(DataRepo.VOKUE, getResources().getDrawable(R.drawable.button_vokue)));
        navGridItems.add(new NavGridItem(DataRepo.MAP, getResources().getDrawable(R.drawable.button_map)));
        navGridItems.add(new NavGridItem(DataRepo.ABOUT, getResources().getDrawable(R.drawable.button_info)));
    }

    private void selectItem(NavGridItem item) {
        if (item.isSelected()) {
            Toast.makeText(this, "Du befindest dich schon auf dieser Seite.", Toast.LENGTH_SHORT).show();
            return;
        }
        Fragment fragment;
        for (NavGridItem n : navGridItems)
            n.setSelected(false);
        item.setSelected(true);
        navAdapter.notifyDataSetChanged();

        if (item.getType() == DataRepo.MAP) {
            fragment = new MapFragment();
        } else if (item.getType() == DataRepo.ABOUT) {
            fragment = new AboutFragment();
        } else
            fragment = SpotListFragment.create(item.getType());
        Fragment current = fragmentManager.findFragmentById(R.id.content_frame);

        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, fragment, "SPOTLIST")
                .commit();

        drawerLayout.closeDrawer(Gravity.START);
    }

    private void setUpStartPage() {
        for (NavGridItem n : navGridItems)
            n.setSelected(false);
        navAdapter.notifyDataSetChanged();
        Fragment fragment = new StartPageFragment();
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, fragment, "STARTPAGE")
                .commit();
        if (NewsFragment.newsAdapter != null)
            NewsFragment.newsAdapter.notifyDataSetChanged();
    }



    private void initDrawer() {
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
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(int venueId) {
        /*Fragment fragment = new SpotDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("venueId", venueId);
        fragment.setArguments(bundle);
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, fragment)
                .addToBackStack(null)
                .commit();*/
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
    public void onFragmentInteraction(Uri uri) {

    }
}
