package de.badgersburrow.sciman.bibtab;

import de.badgersburrow.sciman.R;
import de.badgersburrow.sciman.SuggestionsDatabase;
import de.badgersburrow.sciman.objects.item;

import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.widget.SearchView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


public class BibDetailedViewActivity extends AppCompatActivity {
    
    private String theme_color;
    private boolean isSearchtimestampEnabled;
    
    private CharSequence mTitle;
    
    BibDetailedViewFragment bibDetailedViewFragment;
    
    private SearchView mSearchView;
    private SuggestionsDatabase database;
    
    private boolean mTableView;
    private boolean mLandscapeView;
    
    public item selectedItem;
    
   
	
    
	
	/** Called when the activity is first created. */
	
    
	@Override
    public void onCreate(Bundle savedInstanceState) {  
    	SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        theme_color = sp.getString("pref_color_theme_entries","Black");
        if (theme_color.equalsIgnoreCase("White")){
        	setTheme(android.R.style.Theme_Light_NoTitleBar);
        }
        super.onCreate(savedInstanceState); 
        //ScrollView sv = new ScrollView(this);
       	setContentView(R.layout.bib_detailedview_activity);

        
        //setContentView(R.layout.staticscrollview);
        Intent in = super.getIntent();
        selectedItem = (item)in.getSerializableExtra("item");
            
        mLandscapeView = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(selectedItem.getTitle());
        
              
        FragmentManager fragmentManager = getFragmentManager();
    	//FrameLayout Container = (FrameLayout)findViewById(R.id.container);
        
        BibDetailedViewFragment bibdetailedviewfrag= (BibDetailedViewFragment) getFragmentManager().findFragmentByTag("bibdetailedview");
		if (bibdetailedviewfrag!=null){
			fragmentManager.beginTransaction().detach(bibdetailedviewfrag).commit();
		}
        bibDetailedViewFragment = new BibDetailedViewFragment();				
        fragmentManager.beginTransaction().add(R.id.container, bibDetailedViewFragment,"bibdetailedview").commit();
    } 

    
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			/*NavUtils.navigateUpTo(this,
					new Intent(this, BibViewActivity.class));
			overridePendingTransition(R.anim.fade_in, R.anim.fade_out);*/
			super.onBackPressed();
	        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
			
			return true;
		
		default:
            return super.onOptionsItemSelected(item);
        
		
		}
		
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



    
	
    /*public boolean onClose() {
        return false;
    }
 
    protected boolean isAlwaysExpanded() {
        return false;
    }*/
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState)
    {
    	super.onSaveInstanceState(savedInstanceState);
       
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState)
    {
    	
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