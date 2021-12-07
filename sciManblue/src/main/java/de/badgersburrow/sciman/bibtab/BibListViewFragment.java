package de.badgersburrow.sciman.bibtab;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import de.badgersburrow.sciman.main.MainActivity;
import de.badgersburrow.sciman.R;
//import de.badgersburrow.sciman.MainActivity_new.DrawerItemClickListener;
import de.badgersburrow.sciman.objects.Bib;
import de.badgersburrow.sciman.objects.item;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class BibListViewFragment extends Fragment {

    public RecyclerView mRecyclerView;
    public BibListViewRecyclerAdapter mRecyclerAdapter;

    String itemListString = "itemListString";

	//private int numberOfItemsAfterSearch;
	
	private static Activity bibViewActivity = null;
	private View rootView = null;

	String fileName 		= BibViewActivity.fileName;
    String pdfDirectory 	= BibViewActivity.pdfDirectory;
    int bibID 				= BibViewActivity.bibID;
    ArrayList<Bib> Bibitems	= MainActivity.Bibitems;
    ArrayList<item> items 	= BibViewActivity.items;
    int AnzahlItemsGesamt 	= BibViewActivity.AnzahlItemsGesamt;
    int sortAfterColumn     = BibViewActivity.sortAfterColumn;
    int sortedBeforeColumn  = BibViewActivity.sortedBeforeColumn;
    String searchText		= BibViewActivity.searchText;
    boolean search_active   = BibViewActivity.search_active;
    
    static item selectedItem;
    private ArrayList<item> itemsSorted  = new ArrayList<>();

	//private int AnzahlItems; //number of items currently viewed
    //private int prefTextSize;
    //private int prefHeaderTextSize;
    
    private static boolean mLandscapeView;
    
    static BibDetailedViewFragment bibDetailedViewFragment;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	bibViewActivity = getActivity();
    	SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(bibViewActivity);
    	mLandscapeView = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
        super.onCreate(savedInstanceState);  

        rootView = inflater.inflate(R.layout.bib_listview_fragment,container,false);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        // 2. set layoutManger
        mRecyclerView.setLayoutManager(new LinearLayoutManager(bibViewActivity));

        if (savedInstanceState != null) {
            // Restore value of members from saved state
            items = (ArrayList<item>) savedInstanceState.getSerializable(itemListString);
        } else {
            items = BibViewActivity.refineItems(searchText, sortAfterColumn, sortedBeforeColumn);
        }
        mRecyclerAdapter = new BibListViewRecyclerAdapter(items);
        // registerForContextMenu(mRecyclerView);
        mRecyclerView.setAdapter(mRecyclerAdapter);
        // 5. set item animator to DefaultAnimator
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        //createRows(searchText, sortAfterColumn, sortedBeforeColumn);

        
		return rootView;
    } 





    public static void showBibitem(item Bibitem){
        BibViewActivity.selectedItem = Bibitem;
        boolean isTablet = bibViewActivity.getResources().getBoolean(R.bool.isTablet);
        if (mLandscapeView && isTablet){
            FragmentManager fragmentManager = bibViewActivity.getFragmentManager();
            //FrameLayout rightContainer = (FrameLayout)rootView.findViewById(R.id.right_container);
            BibDetailedViewFragment bibdetailedviewfrag= (BibDetailedViewFragment) bibViewActivity.getFragmentManager().findFragmentByTag("bibdetailedview");
            if (bibdetailedviewfrag!=null){
                fragmentManager.beginTransaction().detach(bibdetailedviewfrag).commit();
                fragmentManager.beginTransaction().remove(bibdetailedviewfrag).commit();
            }
            bibDetailedViewFragment = new BibDetailedViewFragment();

            fragmentManager.beginTransaction().add(R.id.right_container, bibDetailedViewFragment,"bibdetailedview").commit();
            //rightContainer.setLayoutParams(new LayoutParams(100, LayoutParams.MATCH_PARENT, 2));
            //BibDetailedViewFragment bibdetailedfrag= (BibDetailedViewFragment) getFragmentManager().findFragmentByTag("bibdetailedview");
            //BibViewActivity.setDetailedViewContent(items[entryId-10000]);

        } else {
            Intent in = new Intent(bibViewActivity, BibDetailedViewActivity.class);
            in.putExtra("item",Bibitem);

            bibViewActivity.startActivity(in);
            bibViewActivity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            //Toast.makeText(bibViewActivity, "portrait orientation", Toast.LENGTH_LONG).show();
        }
    }
	
	public void clearList(){
		try{
			View entryView = rootView.findViewById(10001);
            ((ViewManager)entryView.getParent()).removeView(entryView);
		} catch (RuntimeException e){
			
		}
		for(item singleItem : items) {
			try{
				View entryView = rootView.findViewById(10000+singleItem.getId());
				((ViewManager)entryView.getParent()).removeView(entryView);
				
			} catch (RuntimeException e){
				Log.d("clearList", "Could not destroy entryView");
			}
		
        	
		}
	}


	
	 public void saveBibItems(Bib[] Bibitems){
	    	//final Bib[] Bibitemsdefault = new Bib[100];
	    	
	    	FileOutputStream fos = null;
	    	ObjectOutputStream out = null;
	    	try {
		    	fos = bibViewActivity.openFileOutput("androidbib.cfg", Context.MODE_PRIVATE);
		    	out = new ObjectOutputStream(fos);
		    	out.writeObject(Bibitems);
		    	out.close();
		    	System.out.println("Object Persisted");
	    	} catch (IOException ex) {
	    		ex.printStackTrace();
	    	}
	    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState)
    {
        savedInstanceState.putSerializable(itemListString, items);
        super.onSaveInstanceState(savedInstanceState);
    }

  
}