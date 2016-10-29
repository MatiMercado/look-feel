package ar.edu.itba.hci.q2.g4.androidapp;


import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;

import ar.edu.itba.hci.q2.g4.androidapp.R;

/*
* Notes: The menu is not used, it can be deleted
* */

public class Home extends NavigationDrawerActivity {

    private SectionsPagerAdapter2 mSectionsPagerAdapter2;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter2 = new SectionsPagerAdapter2(getFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter2);

    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter2 extends FragmentPagerAdapter {

        public SectionsPagerAdapter2(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment2 (defined as a static inner class below).
            return PlaceholderFragment2.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "SECTION 1";
                case 1:
                    return "SECTION 2";
                case 2:
                    return "SECTION 3";
            }
            return null;
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment2 extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment2 newInstance(int sectionNumber) {
            PlaceholderFragment2 fragment = new PlaceholderFragment2();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);

            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment2() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_home, container, false);
            ImageView image = (ImageView) rootView.findViewById(R.id.wallpaper_image);
            Integer section = getArguments().getInt(ARG_SECTION_NUMBER);

            switch (section){
                case 1:
                    image.setImageResource(R.drawable.slide1);
                    break;
                case 2:
                    image.setImageResource(R.drawable.slide2);
                    break;
                case 3:
                    image.setImageResource(R.drawable.slide3);
                    break;
                default:
                return null;
            }

            return rootView;
        }
    }
}
