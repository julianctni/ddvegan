package com.ponk.ddvegan.activities;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.ponk.ddvegan.R;
import com.ponk.ddvegan.adapters.NavigationAdapter;
import com.ponk.ddvegan.fragments.AboutFragment;
import com.ponk.ddvegan.fragments.MapFragment;
import com.ponk.ddvegan.fragments.NewsFragment;
import com.ponk.ddvegan.fragments.SpotDetailFragment;
import com.ponk.ddvegan.fragments.SpotListFragment;
import com.ponk.ddvegan.fragments.StartPageFragment;
import com.ponk.ddvegan.models.DataRepo;
import com.ponk.ddvegan.utils.NavItem;


public class MainActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle drawerToggle;
    Toolbar toolbar;
    ImageView homeButton;
    Menu menu;
    ListView navList;
    FragmentManager fragmentManager;
    NavigationAdapter navAdapter;

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
        if (DataRepo.navItems.isEmpty())
            this.setUpGridItems();
        navAdapter = new NavigationAdapter(this, DataRepo.navItems);

        navList.setAdapter(navAdapter);
        navList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                selectItem((NavItem) navList.getItemAtPosition(position));
            }
        });
    }

    public void setUpGridItems() {
        DataRepo.navItems.add(new NavItem(DataRepo.SHOPPING, getResources().getDrawable(R.drawable.button_shopping), getString(R.string.category_shopping)));
        DataRepo.navItems.add(new NavItem(DataRepo.FOOD, getResources().getDrawable(R.drawable.button_food), getString(R.string.category_food)));
        DataRepo.navItems.add(new NavItem(DataRepo.BAKERY, getResources().getDrawable(R.drawable.button_bakery), getString(R.string.category_bakery)));
        DataRepo.navItems.add(new NavItem(DataRepo.CAFE, getResources().getDrawable(R.drawable.button_cafe), getString(R.string.category_cafe)));
        DataRepo.navItems.add(new NavItem(DataRepo.ICECREAM, getResources().getDrawable(R.drawable.button_icecream), getString(R.string.category_icecream)));
        DataRepo.navItems.add(new NavItem(DataRepo.VOKUE, getResources().getDrawable(R.drawable.button_vokue), getString(R.string.category_vokue)));
        DataRepo.navItems.add(new NavItem(DataRepo.MAP, getResources().getDrawable(R.drawable.button_map), getString(R.string.category_map)));
        DataRepo.navItems.add(new NavItem(DataRepo.ABOUT, getResources().getDrawable(R.drawable.button_info), getString(R.string.category_about)));
    }

    private void selectItem(NavItem item) {
        if (item.isSelected()) {
            Toast.makeText(this, getString(R.string.nav_toast_twice), Toast.LENGTH_SHORT).show();
            return;
        }
        Fragment fragment;
        for (NavItem n : DataRepo.navItems)
            n.setSelected(false);
        item.setSelected(true);
        navAdapter.notifyDataSetChanged();
        String fragTag;
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
        for (NavItem n : DataRepo.navItems)
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
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, 0, 0) {
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
}
