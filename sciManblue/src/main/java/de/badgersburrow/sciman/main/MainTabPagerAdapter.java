package de.badgersburrow.sciman.main;

import android.app.Fragment;
import android.app.FragmentManager;
import android.util.SparseArray;
import android.view.ViewGroup;

public class MainTabPagerAdapter extends FragmentStatePagerAdapter {

         public MainTabPagerAdapter(FragmentManager fm) {
		 super(fm);
		 	// TODO Auto-generated constructor stub
         }

		SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();      
 

     @Override
     public Object instantiateItem(ViewGroup container, int position) {
         Fragment fragment = (Fragment) super.instantiateItem(container,
             position);
         registeredFragments.put(position, fragment);
         return fragment;
     }
 
     @Override
     public void destroyItem(ViewGroup container, int position, Object object) {
         super.destroyItem(container, position, object);
         registeredFragments.remove(position);
     }
 
     public Fragment getRegisteredFragment(int position) {
         return registeredFragments.get(position);
     }

	@Override
	public Fragment getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return registeredFragments.size();
	}

	public void addFragment(Fragment fragment) {
		registeredFragments.append(getCount(),fragment);
		
	}
 
//         ...
 
}
