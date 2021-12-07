package de.badgersburrow.sciman.bibtab;

import java.util.ArrayList;

import de.badgersburrow.sciman.main.MainActivity;
import de.badgersburrow.sciman.R;
import de.badgersburrow.sciman.SuggestionSimpleCursorAdapter;
import de.badgersburrow.sciman.SuggestionsDatabase;
//import de.badgersburrow.sciman.MainActivity_new.DrawerItemClickListener;
import de.badgersburrow.sciman.objects.Bib;
import de.badgersburrow.sciman.objects.item;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TableRow.LayoutParams;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NavUtils;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;


public class BibViewActivity extends AppCompatActivity {

    static Bib bib;
	
	static String fileName;
    static String pdfDirectory ;
    static int bibID ;
    static ArrayList<item> items = new ArrayList<item>(); //all items in the original ordering
    static int AnzahlItemsGesamt = 0;
    
    static item selectedItem;
    
    BibTableViewFragment bibTableViewFragment;
    BibListViewFragment bibListViewFragment;
    BibDetailedViewFragment bibDetailedViewFragment;
	
	//private int numberOfItemsAfterSearch;
		
    static int sortedBeforeColumn = 0;
    static int sortAfterColumn = 4;
    static String searchText = "";
    static boolean search_active=false;
    //private int AnzahlItems; //number of items currently viewed
    private int prefTextSize;
    private int prefHeaderTextSize;


    private boolean isSearchtimestampEnabled;
    

    private DrawerLayout mDrawerLayout;
    private LinearLayout mDrawerLinearLayout;
    private ActionBarDrawerToggle mDrawerToggle;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private String[] mSortFields;
    
    private SearchView mSearchView;
    private SuggestionsDatabase database;
    
    private boolean mTableView;
    private boolean mLandscapeView;
    
    
    public enum sortOptions
    {
        Author0(R.id.rB_author0),
        Author1(R.id.rB_author1),
        Title0(R.id.rB_title0),
        Title1(R.id.rB_title1),
        Year0(R.id.rB_year0),
        Year1(R.id.rB_year1),
        Journal0(R.id.rB_journal0),
        Journal1(R.id.rB_journal1),
        Timestamp0(R.id.rB_timestamp0),
        Timestamp1(R.id.rB_timestamp1),
        Bibtexkey0(R.id.rB_bibtexkey0),
        Bibtexkey1(R.id.rB_bibtexkey1);
        
        int idInLayout;
        
        sortOptions(int idInLayout)
        {
        		this.idInLayout = idInLayout;
        }
     

           //accessor for id

        public int getIdInLayout()
        {
        		return idInLayout;
        }
        
        

    }
    
    private void exclusivelySetOption(sortOptions selectedOption)
    {
        //Cycle through all options
        for (sortOptions option : sortOptions.values())
        {
        	 RadioButton radio = (RadioButton) 

             findViewById(option.getIdInLayout());
		     if (radio != null)
		     {
                //Check or uncheck as needed
                radio.setChecked(option.ordinal() == selectedOption.ordinal());
		     }
        }
    }
    
	

    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.bibviewmenu, menu);

	    MenuItem searchItem = menu.findItem(R.id.action_search);
	    mSearchView = (SearchView) searchItem.getActionView();
	    setupSearchView(searchItem);
		return true;
	}
	
	/** Called when the activity is first created. */
	
    
	@Override
    public void onCreate(Bundle savedInstanceState) {  
    	SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        super.onCreate(savedInstanceState); 
        //ScrollView sv = new ScrollView(this);
      	setContentView(R.layout.bib_mainview_activity);
        //setContentView(R.layout.staticscrollview);
        Intent intentFromMenu = super.getIntent();//
        Bundle extras = intentFromMenu.getExtras();

        bib = (Bib) extras.getSerializable("bib");
        mTableView = extras.getBoolean("tableView");
        items = bib.getItems();

        mLandscapeView = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
        
        if (savedInstanceState!=null){
        	items = (ArrayList<item>) savedInstanceState.getSerializable("items");
        	AnzahlItemsGesamt = savedInstanceState.getInt("anzahl");
            mTableView = savedInstanceState.getBoolean("tableView");
        	searchText = savedInstanceState.getString("searchText");
        	sortedBeforeColumn = savedInstanceState.getInt("sortedBeforeColumn");
            sortAfterColumn = savedInstanceState.getInt("sortAfterColumn");
        }

        FrameLayout rightContainer = (FrameLayout)findViewById(R.id.right_container);
    	rightContainer.setLayoutParams(new LayoutParams(0, 0, 0));

		Toolbar toolbar = (Toolbar) findViewById(R.id.bib_toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(MainActivity.Bibitems.get(bibID).getBibname());
        
        mTitle = mDrawerTitle = getTitle();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLinearLayout = (LinearLayout) findViewById(R.id.right_drawer);

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLinearLayout.setOnClickListener(null);
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        
        
        // set up the drawer's list view with items and click listener
        
        //radiobuttons of drawer
        for (sortOptions option : sortOptions.values())
        {
        	RadioButton radio = (RadioButton)
            findViewById(option.getIdInLayout());
        	
        	RadioButton.OnClickListener optionOnClickListener = 

       		     new RadioButton.OnClickListener()
       		{
       		 @Override
			public void onClick(View v)
       		 {
       		  sortOptions option = (sortOptions) v.getTag();
       		  //React to your option selection here
       		  exclusivelySetOption(option);
       		  mDrawerLayout.closeDrawer(mDrawerLinearLayout);
       		  //Toast.makeText(BibViewActivity.this, option.name(), Toast.LENGTH_LONG).show();
       		  if (option.name().equalsIgnoreCase("author0")){
       			sortAfterColumn = 0;
       		  }else if (option.name().equalsIgnoreCase("author1")){
       			sortAfterColumn = 10;
       		  }else if (option.name().equalsIgnoreCase("title0")){
       			sortAfterColumn = 1;
       		  }else if (option.name().equalsIgnoreCase("title1")){
       		    sortAfterColumn = 11;
       		  }else if (option.name().equalsIgnoreCase("year0")){
       			  sortAfterColumn = 2;
       		  }else if (option.name().equalsIgnoreCase("year1")){
       			  sortAfterColumn = 12;
       		  }else if (option.name().equalsIgnoreCase("journal0")){
       			  sortAfterColumn = 3;
       		  }else if (option.name().equalsIgnoreCase("journal1")){
       			sortAfterColumn = 13;
       		  }else if (option.name().equalsIgnoreCase("bibtexkey0")){
       			sortAfterColumn = 4;
       		  }else if (option.name().equalsIgnoreCase("bibtexkey1")){
       			sortAfterColumn = 14;
       		  }else if (option.name().equalsIgnoreCase("timestamp0")){
       			sortAfterColumn = 5;
       		  }else if (option.name().equalsIgnoreCase("timestamp1")){
       			sortAfterColumn = 15;
       		  }
       		  if (sortAfterColumn >=10){
       			sortAfterColumn = sortAfterColumn-10;
       			sortedBeforeColumn = sortAfterColumn;
       		  } else{
       			sortedBeforeColumn = sortAfterColumn+1;
       		  }
       		
       		  updateFragment(mTableView);
       		  
       		  

       		 }
       		};
        	if (radio != null)
        	{
        		radio.setOnClickListener(optionOnClickListener);
        		radio.setTag(option);
        	}
        	
        }
        
        

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
                ) {
            @Override
			public void onDrawerClosed(View view) {
                //getActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            @Override
			public void onDrawerOpened(View drawerView) {
                //getActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerToggle.setDrawerIndicatorEnabled(false);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        //mDrawerLayout.setDrawerLockMode(0);
        
        database = new SuggestionsDatabase(this);

        if (bib.getItems().size()==0){
            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle(getString(R.string.alert));
            alertDialog.setMessage(getString(R.string.fnf));
            alertDialog.setButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    //here you can add functions

                }
            });
            alertDialog.setIcon(R.drawable.alerticon);
            alertDialog.show();
        }

        if (mTableView){
        	showBibView("bibtableview");
        } else {
        	showBibView("biblistview");
        }
        //updateFragment(mTableView);

    } 


	
    

    
    /* The click listener for ListView in the navigation drawer */
    /*private class DrawerClickListener implements LinearLayout.OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			
		}
    }*/
    
    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerLinearLayout);
        //boolean drawerOpenSort = mDrawerLayout.isDrawerOpen(mDrawerSort);
        menu.findItem(R.id.action_search).setVisible(!drawerOpen);
        menu.findItem(R.id.action_sort).setVisible(!drawerOpen);
        menu.findItem(R.id.action_switchview).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }
    
    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
    	if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
    	}
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpTo(this,
					new Intent(this, MainActivity.class));
			overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
			
			return true;
		/*case R.id.action_search:
			mDrawerList.setAdapter(new ArrayAdapter<String>(this,
	                R.layout.drawer_list_item, mSortFields));
			mDrawerLayout.openDrawer(GravityCompat.END);
			return true;*/
			
		case R.id.action_sort:
			//mDrawerList.setAdapter(new ArrayAdapter<String>(this,
	         //       R.layout.drawer_list_item, mSortFields));
			mDrawerLayout.openDrawer(GravityCompat.END);
			return true;
			
		case R.id.action_switchview:
			//mDrawerList.setAdapter(new ArrayAdapter<String>(this,
	         //       R.layout.drawer_list_item, mSortFields));
			if (mTableView){
				showBibView("biblistview");
			} else {
				showBibView("bibtableview");
			}
			MainActivity.Bibitems.get(bibID).setTableView(mTableView);
			BibFragment.saveBibItems(MainActivity.Bibitems);
			
			return true;	
		default:
            return super.onOptionsItemSelected(item);
        
		
		}
		
	}

    public static ArrayList<item> refineItems(String searchText, int sortAfterColumn, int sortedBeforeColumn){
        //Searching

        boolean lightBack = false;
        ArrayList<item> itemsSearched = new ArrayList<>();
        ArrayList<item> itemsSorted = new ArrayList<>();

        for(int j = 0; j < items.size(); j++) {
            boolean add = true;
            if (searchText.length()!=0){
                if ((items.get(j).getAuthor().indexOf(searchText)==-1) &&
                        (items.get(j).getTitle().indexOf(searchText)==-1) &&
                        (items.get(j).getYear().indexOf(searchText)==-1) &&
                        (items.get(j).getJournal().indexOf(searchText)==-1) &&
                        (items.get(j).getBibtexkey().indexOf(searchText)==-1)){
                    add = false;
                }
            }
            if (add){
                itemsSearched.add(items.get(j));
            }

        }
        int currentAnzahlItems = itemsSearched.size();
        //Sorting
        switch(sortAfterColumn){
            case 0:{//Author
                for(int j = 0; j < currentAnzahlItems; j++) {
                    String currentAuthor = itemsSearched.get(j).getAuthor();
                    boolean inserted=false;
                    for(int k = 0; k < j; k++) {
                        if (((currentAuthor.compareTo(itemsSorted.get(k).getAuthor())<0) && (sortedBeforeColumn!=0)) ||
                                ((currentAuthor.compareTo(itemsSorted.get(k).getAuthor())>0) && (sortedBeforeColumn==0))){
                            //for(int l = j; l > k; l--) {
                            //	itemsSorted.set(l,itemsSorted.get(l-1));
                            //}
                            itemsSorted.add(k,itemsSearched.get(j));
                            inserted=true;
                            break;
                        }
                    }
                    if (!inserted){
                        itemsSorted.add(itemsSearched.get(j));
                    }

                }
                break;
            }
            case 1:{//Title
                for(int j = 0; j < currentAnzahlItems; j++) {
                    String currentTitle = itemsSearched.get(j).getTitle();
                    boolean inserted=false;
                    for(int k = 0; k < j; k++) {
                        if (((currentTitle.compareTo(itemsSorted.get(k).getTitle())<0) && (sortedBeforeColumn!=1)) ||
                                ((currentTitle.compareTo(itemsSorted.get(k).getTitle())>0) && (sortedBeforeColumn==1))){
                            //for(int l = j; l > k; l--) {
                            //	itemsSorted.set(l,itemsSorted.get(l-1));
                            //}
                            itemsSorted.add(k, itemsSearched.get(j));
                            inserted=true;
                            break;
                        }
                    }
                    if (!inserted){
                        itemsSorted.add(itemsSearched.get(j));
                    }
                }
                break;
            }
            case 2:{//Year
                for(int j = 0; j < currentAnzahlItems; j++) {
                    String currentYear = itemsSearched.get(j).getYear();
                    boolean inserted=false;
                    for(int k = 0; k < j; k++) {
                        if (((currentYear.compareTo(itemsSorted.get(k).getYear())<0) && (sortedBeforeColumn!=2)) ||
                                ((currentYear.compareTo(itemsSorted.get(k).getYear())>0) && (sortedBeforeColumn==2))){
                            //for(int l = j; l > k; l--) {
                            //	itemsSorted.set(l,itemsSorted.get(l-1));
                            //}
                            itemsSorted.add(k, itemsSearched.get(j));
                            inserted=true;
                            break;
                        }
                    }
                    if (!inserted){
                        itemsSorted.add(itemsSearched.get(j));
                    }
                }
                break;
            }
            case 3:{//Journal
                for(int j = 0; j < currentAnzahlItems; j++) {
                    String currentJournal = itemsSearched.get(j).getJournal();
                    boolean inserted=false;
                    for(int k = 0; k < j; k++) {
                        if (((currentJournal.compareTo(itemsSorted.get(k).getJournal())<0) && (sortedBeforeColumn!=3)) ||
                                ((currentJournal.compareTo(itemsSorted.get(k).getJournal())>0) && (sortedBeforeColumn==3))){
                            //for(int l = j; l > k; l--) {
                            //	itemsSorted.set(l,itemsSorted.get(l-1));
                            //}
                            itemsSorted.add(k, itemsSearched.get(j));
                            inserted=true;
                            break;
                        }
                    }
                    if (!inserted){
                        itemsSorted.add(itemsSearched.get(j));
                    }
                }
                break;
            }
            case 4:{//Bibtexkey
                for(int j = 0; j < currentAnzahlItems; j++) {
                    String currentBibtexkey = itemsSearched.get(j).getBibtexkey();
                    boolean inserted=false;
                    for(int k = 0; k < j; k++) {
                        if (((currentBibtexkey.compareTo(itemsSorted.get(k).getBibtexkey())<0) && (sortedBeforeColumn!=4)) ||
                                ((currentBibtexkey.compareTo(itemsSorted.get(k).getBibtexkey())>0) && (sortedBeforeColumn==4))){
                            //for(int l = j; l > k; l--) {
                            //	itemsSorted.set(l,itemsSorted.get(l-1));
                            //}
                            itemsSorted.add(k,itemsSearched.get(j));
                            inserted=true;
                            break;
                        }
                    }
                    if (!inserted){
                        itemsSorted.add(itemsSearched.get(j));
                    }
                }
                break;
            }
            case 5:{//timestamp
                for(int j = 0; j < currentAnzahlItems; j++) {
                    String currentTimestamp = itemsSearched.get(j).getTimestamp();
                    boolean inserted=false;
                    for(int k = 0; k < j; k++) {
                        if (((currentTimestamp.compareTo(itemsSorted.get(k).getTimestamp())<0) && (sortedBeforeColumn!=5)) ||
                                ((currentTimestamp.compareTo(itemsSorted.get(k).getTimestamp())>0) && (sortedBeforeColumn==5))){
                            //for(int l = j; l > k; l--) {
                            //	itemsSorted.set(l,itemsSorted.get(l-1));
                            //}
                            itemsSorted.add(k, itemsSearched.get(j));
                            inserted=true;
                            break;
                        }
                    }
                    if (!inserted){
                        itemsSorted.add(itemsSearched.get(j));
                    }
                }
                break;
            }
            default: {
                itemsSorted=itemsSearched;
                break;
            }
        }

        return itemsSorted;


    }
    
    public void updateFragment(boolean tableView){
    	if (tableView){
			  BibTableViewFragment bibtableviewfrag= (BibTableViewFragment) getFragmentManager().findFragmentByTag("bibtableview");
			  bibtableviewfrag.clearTable();
              items = refineItems(searchText, sortAfterColumn, sortedBeforeColumn);
			  bibtableviewfrag.createRows(items);
		  } else {
			  BibListViewFragment biblistviewfrag= (BibListViewFragment) getFragmentManager().findFragmentByTag("biblistview");
			  items = refineItems(searchText, sortAfterColumn, sortedBeforeColumn);
              biblistviewfrag.mRecyclerAdapter.setItems(items);
		  }
    }
    
    
    private void showBibView(String fragmentName) {
    	
        
    	FragmentManager fragmentManager = getFragmentManager();
    	FrameLayout rightContainer = (FrameLayout)findViewById(R.id.right_container);
    	rightContainer.setLayoutParams(new LayoutParams(0, 0, 0));


		if (fragmentName.equalsIgnoreCase("biblistview")){
			BibTableViewFragment bibtableviewfrag= (BibTableViewFragment) getFragmentManager().findFragmentByTag("bibtableview");
			if (bibtableviewfrag!=null){
				fragmentManager.beginTransaction().detach(bibtableviewfrag).commit();
			}	
			BibListViewFragment biblistviewfrag= (BibListViewFragment) getFragmentManager().findFragmentByTag("biblistview");
			if (biblistviewfrag!=null){
				fragmentManager.beginTransaction().remove(biblistviewfrag).commit();
			}
			bibListViewFragment = new BibListViewFragment();			
	        fragmentManager.beginTransaction().add(R.id.left_container, bibListViewFragment,"biblistview").commit();


			boolean isTablet = getResources().getBoolean(R.bool.isTablet);
			if (mLandscapeView && isTablet){
            	       	
                rightContainer.setLayoutParams(new LayoutParams(100,android.view.ViewGroup.LayoutParams.MATCH_PARENT, 2));
            }
            
            mTableView = false;
            MainActivity.Bibitems.get(bibID).setTableView(mTableView);
	        BibFragment.saveBibItems(MainActivity.Bibitems);
	        
            
		} else if (fragmentName.equalsIgnoreCase("bibtableview")){
			BibListViewFragment biblistviewfrag= (BibListViewFragment) getFragmentManager().findFragmentByTag("biblistview");
			if (biblistviewfrag!=null){
				fragmentManager.beginTransaction().detach(biblistviewfrag).commit();
			}
			BibDetailedViewFragment bibdetailedviewfrag= (BibDetailedViewFragment) getFragmentManager().findFragmentByTag("bibdetailedview");
			if (bibdetailedviewfrag!=null){
				fragmentManager.beginTransaction().detach(bibdetailedviewfrag).commit();
			}
			BibTableViewFragment bibtableviewfrag= (BibTableViewFragment) getFragmentManager().findFragmentByTag("bibtableview");
			if (bibtableviewfrag!=null){
				fragmentManager.beginTransaction().remove(bibtableviewfrag).commit();
			}
			bibTableViewFragment = new BibTableViewFragment();				
	        fragmentManager.beginTransaction().add(R.id.left_container, bibTableViewFragment,"bibtableview").commit();
	            	            
			
			rightContainer.setLayoutParams(new LayoutParams(0, 0, 0));
            mTableView = true;
		}
		MainActivity.Bibitems.get(bibID).setTableView(mTableView);
        BibFragment.saveBibItems(MainActivity.Bibitems);
		
	}




	@Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
    
    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    private void setupSearchView(MenuItem searchItem) {
    	
    	if (!searchText.equalsIgnoreCase("")){
    		mSearchView.setQuery(searchText, false);
    		mSearchView.setIconified(false);
    		mSearchView.clearFocus();
    	}
        
 
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
	    		@Override
	    		public boolean onQueryTextChange(String newText) {
	    			Cursor cursor = database.getSuggestions(newText);
	    			if (searchText != "" && newText.equalsIgnoreCase("")){
	    				searchText = "";
		    			MainActivity.Bibitems.get(bibID).setSearchText("");
		    	        BibFragment.saveBibItems(MainActivity.Bibitems);
		    	        search_active=false;
		    	        updateFragment(mTableView);
		         		  
	    			}
	    			
	    	        if(cursor.getCount() != 0)
	    	        {
	    	            String[] columns = new String[] {SuggestionsDatabase.FIELD_SUGGESTION };
	    	            int[] columnTextId = new int[] { android.R.id.text1};
	    	            for (int i=0;i<columnTextId.length;i++){
	    	            	int currentID = columnTextId[i];
	    	            	String currentString = columns[i];
	    	    			for(int j = 0; j < columnTextId.length; j++) {
	    	    				if (currentID>columnTextId[j]){
	    	    					columnTextId[i] = columnTextId[j];
	    	    					columnTextId[j] = currentID;
	    	    					columns[i] = columns[j];
	    	    					columns[j] = currentString;
	    	    					
    	    						break;
	    	    				}
	    	    				//insertLine = k;
	    	    			}
	    	    			
	    	            }
	    	            
	    	            SuggestionSimpleCursorAdapter simple = new SuggestionSimpleCursorAdapter(getBaseContext(),
	    	                    android.R.layout.simple_list_item_1, cursor,
	    	                    columns , columnTextId, 0);

	    	            //CursorAdapter simple = new CursorAdapter(getBaseContext(), cursor, false);
	    	            mSearchView.setSuggestionsAdapter(simple);
	    	            
	    	            //mSearchView.setSuggestionsAdapter(simple);
	    	            return true;
	    	        }
	    	        else
	    	        {
	    	            return false;
	    	        }
	    			
	    		}
        
	    		@Override
	    		public boolean onQueryTextSubmit(String query) {
	    			mSearchView.clearFocus();
	    			searchText = query;
	    			MainActivity.Bibitems.get(bibID).setSearchText(query);
	    	        BibFragment.saveBibItems(MainActivity.Bibitems);
	    	        search_active=true;
	    	        updateFragment(mTableView);
	         		  
	            	long result = database.insertSuggestion(query);
	                return result != -1;
	    		}
        });
        
        
   
        
             
        
        mSearchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
		

        	    @Override
        	    public boolean onSuggestionSelect(int position) {

        	        return false;
        	    }

        	    @Override
        	    public boolean onSuggestionClick(int position) {

        	        SQLiteCursor cursor = (SQLiteCursor) mSearchView.getSuggestionsAdapter().getItem(position);
        	        int indexColumnSuggestion = cursor.getColumnIndex( SuggestionsDatabase.FIELD_SUGGESTION);

        	        mSearchView.setQuery(cursor.getString(indexColumnSuggestion), true); //true submits, flase just inserts

        	        return true;
        	    }
        	    
        });
        
        
        
	        
    }
    
    

    
	
    /*public boolean onClose() {
        return false;
    }
 
    protected boolean isAlwaysExpanded() {
        return false;
    }*/
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState)
    {
    	savedInstanceState.putSerializable("items", items);
    	savedInstanceState.putInt("anzahl", AnzahlItemsGesamt);
    	savedInstanceState.putBoolean("tableView", mTableView);	
    	savedInstanceState.putString("searchText", searchText);
   		savedInstanceState.putInt("sortAfterColumn", sortAfterColumn);
   		savedInstanceState.putInt("sortedBeforeColumn", sortedBeforeColumn);
       super.onSaveInstanceState(savedInstanceState);
       
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState)
    {
    	items = (ArrayList<item>) savedInstanceState.getSerializable("items");
    	AnzahlItemsGesamt = savedInstanceState.getInt("anzahl");
    	mTableView = savedInstanceState.getBoolean("tableView");	
    	searchText = savedInstanceState.getString("searchText");
    	sortedBeforeColumn = savedInstanceState.getInt("sortedBeforeColumn");
    	sortAfterColumn = savedInstanceState.getInt("sortAfterColumn");

      super.onRestoreInstanceState(savedInstanceState);
      	        
    }
    
    @Override
    public void onResume()
    {
        super.onResume();
        //sortOptions currentOption = sortOptions(R.id.rB_bibtexkey0);//get what our option should be
        //exclusivelySetOption(currentOption);
    }
    
}