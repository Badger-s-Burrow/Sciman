package de.badgersburrow.sciman.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import de.badgersburrow.sciman.FragmentUpdate;
import de.badgersburrow.sciman.MyFragment;
import de.badgersburrow.sciman.R;
import de.badgersburrow.sciman.bibtab.BibFragment;
import de.badgersburrow.sciman.conftab.ConfFragment;

/**
 * Created by reim on 23.06.15.
 */
public class MainTabFragment extends Fragment implements FragmentUpdate {


    public static SectionPagerAdapter mPagerAdapter;
    static FragmentActivity mainActivity;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.main_tab_fragment,container,false);
        mainActivity = getActivity();
        TabLayout tabLayout  = (TabLayout) rootView.findViewById(R.id.tabs);
        ViewPager mViewPager = (ViewPager) rootView.findViewById(R.id.pager);

        mPagerAdapter = new SectionPagerAdapter(mainActivity.getSupportFragmentManager());
        mViewPager.setAdapter(mPagerAdapter);
        tabLayout.setupWithViewPager(mViewPager);
        return rootView;
    }



    public class SectionPagerAdapter extends FragmentPagerAdapter {

        public SectionPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new BibFragment();
                case 1:
                    return new ConfFragment();
                case 2:
                default:
                    return new MyFragment();
            }
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getResources().getString(R.string.tab_bib);
                case 1:
                    return getResources().getString(R.string.tab_conf);
                case 2:
                default:
                    return getResources().getString(R.string.tab_my);
            }
        }
    }
    @Override
    public void update() {
        // TODO Auto-generated method stub

    }
}

