package com.pasta.ddvegan.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pasta.ddvegan.R;
import com.pasta.ddvegan.models.DataRepo;
import com.pasta.ddvegan.utils.SlidingTabLayout;


public class StartPageFragment extends Fragment {

    SlidingTabLayout mSlidingTabLayout;
    public ViewPager mViewPager;


    public StartPageFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_start_page, container, false);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mViewPager = (ViewPager) view.findViewById(R.id.viewpager);
        mViewPager.setAdapter(new SamplePagerAdapter(this.getChildFragmentManager()));
        mSlidingTabLayout = (SlidingTabLayout) view.findViewById(R.id.sliding_tabs);
        mSlidingTabLayout.setBackgroundColor(getResources().getColor(R.color.primary));
        mSlidingTabLayout.setDistributeEvenly(true);
        mSlidingTabLayout.setSelectedIndicatorColors(getResources().getColor(R.color.primary_bright));
        mSlidingTabLayout.setViewPager(mViewPager);

    }


    class SamplePagerAdapter extends FragmentPagerAdapter {
        public SamplePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0)
                return new NewsFragment();
            else
                return SpotListFragment.create(DataRepo.FAVORITES);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (position == 0)
                return getString(R.string.startpage_tab_dashboard);
            else
                return getString(R.string.startpage_tab_fav);
        }

    }

}

