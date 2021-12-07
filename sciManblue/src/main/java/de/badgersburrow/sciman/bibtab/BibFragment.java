package de.badgersburrow.sciman.bibtab;

import java.util.ArrayList;
import java.util.Locale;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ImageButton;
import android.widget.Toast;

//import com.dropbox.android.sample.DBRoulette;
//import com.dropbox.android.sample.R;
//import com.dropbox.android.sample.DBRoulette;
//import com.dropbox.android.sample.DownloadRandomPicture;
import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.android.AuthActivity;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.Session.AccessType;
import com.dropbox.client2.session.TokenPair;

import de.badgersburrow.sciman.FragmentUpdate;
import de.badgersburrow.sciman.main.MainTabFragment;
import de.badgersburrow.sciman.R;
import de.badgersburrow.sciman.objects.Bib;
import de.badgersburrow.sciman.main.MainActivity;
import de.badgersburrow.sciman.objects.News;
//import de.badgersburrow.sciman.library.RecentBibViewActivity;
import de.badgersburrow.sciman.utilities.SyncBibLibrary;
import de.badgersburrow.sciman.utilities.SyncFullLibrary;
import de.badgersburrow.sciman.objects.Topics;
import de.badgersburrow.sciman.utilities.VariousMethods;


import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;


public class BibFragment extends Fragment implements FragmentUpdate
{
	private static final String TAG = "AndroidBib";

    ///////////////////////////////////////////////////////////////////////////
    //                      Your app-specific settings.                      //
    ///////////////////////////////////////////////////////////////////////////

    // Replace this with your app key and secret assigned by Dropbox.
    // Note that this is a really insecure way to do this, and you shouldn't
    // ship code which contains your key & secret in such an obvious way.
    // Obfuscation is good.
    final static private String APP_KEY = "uf597mx4a2swh8s";
    final static private String APP_SECRET = "0u99n4u2qg7i0ts";

    // If you'd like to change the access type to the full Dropbox instead of
    // an app folder, change this value.
    final static private AccessType ACCESS_TYPE = AccessType.APP_FOLDER;

    ///////////////////////////////////////////////////////////////////////////
    //                      End app-specific settings.                       //
    ///////////////////////////////////////////////////////////////////////////

    // You don't need to change these, leave them alone.
    final static private String ACCOUNT_PREFS_NAME = "prefs";
    final static private String ACCESS_KEY_NAME = "ACCESS_KEY";
    final static private String ACCESS_SECRET_NAME = "ACCESS_SECRET";
    
    DropboxAPI<AndroidAuthSession> mApi;
 
    private boolean mLoggedIn;
    private Bib recentBib;
    
    private String theme_color;
    Topics allTopics = MainActivity.allTopics;
    
    static Activity mainActivity = null;
    View rootView = null;
	int requestCode;
	int i=0;
	//static int Bibid = 0;
	Bundle[] Biblist = new Bundle[20];
	static ArrayList<Bib> Bibitems = MainActivity.Bibitems;//new ArrayList<Bib>();
	OnSharedPreferenceChangeListener listener;
	
	public RecyclerView mRecyclerView;
	public BibRecyclerAdapter mRecyclerAdapter;
	public static Bib selectedBib;
	
	private Bitmap customPic;
	ImageButton FAB;
	
	//SharedPreferences myPrefs = this.getSharedPreferences("myPrefs", MODE_WORLD_READABLE);
	private Locale locale = null;
	
	@Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        
        if (locale != null)
        {
        	
            newConfig.locale = locale;
            Locale.setDefault(locale);
            mainActivity.getBaseContext().getResources().updateConfiguration(newConfig, mainActivity.getBaseContext().getResources().getDisplayMetrics());
            
        }
    }

	public final static void OpenBib(Bib selectedBib, Context context) 
	{
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mainActivity);
	    final boolean isSavesortsearchEnabled = sp.getBoolean("isSavesortsearchEnabled", false);
	    

        String line = null;
        String BibtexPath;
        boolean successfulRead = false;

        if (selectedBib.getItems().size()==0 || selectedBib.isItemsOlder()){
            successfulRead = selectedBib.readBibFile(mainActivity);

        }

        if (selectedBib.getItems().size()>0 || successfulRead){
            Intent in = new Intent(mainActivity, BibViewActivity.class);
            if (!isSavesortsearchEnabled){
                selectedBib.setSearchText("");
                selectedBib.setSortColumn(4);

            }
            in.putExtra("bib",selectedBib);
            in.putExtra("tableView", selectedBib.getTableView());


            try{
                context.startActivity(in);
            }
            catch (Exception e) {
                Log.e("MyActivity::MyMethod", e.getMessage());
            }
            mainActivity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
	}
		


	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		AdapterView.AdapterContextMenuInfo info =  (AdapterView.AdapterContextMenuInfo) menuInfo;

		menu.setHeaderTitle(selectedBib.getBibname());

		MenuInflater inflater=mainActivity.getMenuInflater();
		if (selectedBib.getSource().equalsIgnoreCase("local")){
			inflater.inflate(R.menu.main_cm_local, menu);
		}else if(selectedBib.getSource().equalsIgnoreCase("dropbox")){
			inflater.inflate(R.menu.main_cm_dropbox, menu);
		}else if(selectedBib.getSource().equalsIgnoreCase("manualApp")){

		inflater.inflate(R.menu.main_cm_manualapp, menu);
		MenuItem syncItem = menu.findItem(R.id.SyncManualApp);
		syncItem.setTitle(getString(R.string.mcm_syncmanualapp) + " " + selectedBib.getManualAppName());

		}else{
			inflater.inflate(R.menu.main_cm_local, menu);
		}


	}
		
	public void editBib(Bib selectedBib) {
		Intent editBibIn = new Intent(mainActivity, BibEditActivity.class);
		requestCode = 3;//Activity.RESULT_OK;
		//editBibIn.putExtra("file",selectedBib.getFile());
		//editBibIn.putExtra("pdfDirectory",selectedBib.getPdfdirectory());
		editBibIn.putExtra("Bib", selectedBib);
		startActivityForResult(editBibIn, requestCode);
	}

	public void removeBib(Bib selectedBib) {
		mRecyclerAdapter.remove(selectedBib);
		MainTabFragment.mPagerAdapter.notifyDataSetChanged();
		saveBibItems(mRecyclerAdapter.getItems());
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		int id = item.getItemId();
		if (id == R.id.EditItem) {
			editBib(selectedBib);
			return true;
		} else if (id == R.id.DeleteItem) {
			removeBib(selectedBib);
			return true;
		} else if (id == R.id.SyncBibtex) {
			syncBibtex(selectedBib);
			return true;
		} else if (id == R.id.SyncFull) {
			syncFull(selectedBib);
			return true;
		} else if (id == R.id.Unlink) {
			logOut(selectedBib);
			return true;
		} else if (id == R.id.SyncManualApp) {
			syncManualApp(selectedBib);
			return true;
		} else if (id == R.id.ClearRecent) {
			//clearRecentBib();
			return true;
		} else {
			return super.onContextItemSelected(item);
		}
	}


	private void syncFull(Bib Bibitem) {
		if (!mLoggedIn) {

			// Start the remote authentication
			mApi.getSession().startAuthentication(mainActivity);
			setLoggedIn(mApi.getSession().isLinked());

		}
		String directory = Environment.getExternalStorageDirectory().getAbsolutePath();
		String pdfDirectory= Bibitem.getPdfDirectory();
		SyncFullLibrary download = new SyncFullLibrary(mainActivity, mApi, Bibitem.getBibname(), pdfDirectory);
		download.execute();


	}



	private void syncBibtex(Bib Bibitem) {
		if (!mLoggedIn) {

			// Start the remote authentication
			mApi.getSession().startAuthentication(mainActivity);


			setLoggedIn(mApi.getSession().isLinked());

		}
		String directory = Environment.getExternalStorageDirectory().getAbsolutePath();
		String pdfDirectory= Bibitem.getPdfDirectory();
		SyncBibLibrary download = new SyncBibLibrary(mainActivity, mApi, Bibitem.getBibname(), pdfDirectory, Bibitem.getFile());
		download.execute();
	}
	private void logOut(Bib Bibitem) {
		// Remove credentials from the session
		if (mLoggedIn) {
			mApi.getSession().unlink();

			// Clear our stored keys
			clearKeys();
			// Change UI state to display logged out version

			setLoggedIn(mApi.getSession().isLinked());
		} else {
			showToast(getString(R.string.dropb_notlinked));
		}
	}

	private void syncManualApp(Bib Bibitem){
		launchApp(Bibitem.getManualApp());

	}

	protected void launchApp(String packageName) {
		Intent mIntent = mainActivity.getPackageManager().getLaunchIntentForPackage(packageName);
		if (mIntent != null) {
			try {
				startActivity(mIntent);
			} catch (ActivityNotFoundException err) {
				Toast t = Toast.makeText(mainActivity.getApplicationContext(),
						R.string.app_not_found, Toast.LENGTH_SHORT);
				t.show();
			}
		}
	}

	private void setLoggedIn(boolean loggedIn) {
		mLoggedIn = loggedIn;
		//could set text below name, if linked or not:: status: ...

		 /*
		  * Commented as it currently crashes the app
		if (loggedIn) {
			for(Bib Bibitem : Bibitems) {
				if (Bibitem.getSource().equalsIgnoreCase("dropbox")){
					TextView tv_bib_status = (TextView)rootView.findViewById(2500+Bibitem.getID());

					tv_bib_status.setText(getString(R.string.dropb_status_linked));
				}
			}
		} else {
			for(Bib Bibitem : Bibitems) {
				if (Bibitem.getSource().equalsIgnoreCase("dropbox")){
					TextView tv_bib_status = (TextView)rootView.findViewById(2500+Bibitem.getID());
					tv_bib_status.setText(getString(R.string.dropb_status_unlinked));
				}
			}
		}*/
	}

	private String[] getKeys() {
		SharedPreferences prefs = mainActivity.getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
		String key = prefs.getString(ACCESS_KEY_NAME, null);
		String secret = prefs.getString(ACCESS_SECRET_NAME, null);
		if (key != null && secret != null) {
			String[] ret = new String[2];
			ret[0] = key;
			ret[1] = secret;
			return ret;
		} else {
			return null;
		}
	}

	/**
	 * Shows keeping the access keys returned from Trusted Authenticator in a local
	 * store, rather than storing user name & password, and re-authenticating each
	 * time (which is not to be done, ever).
	 */
	private void storeKeys(String key, String secret) {
		// Save the access key for later
		SharedPreferences prefs = mainActivity.getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
		Editor edit = prefs.edit();
		edit.putString(ACCESS_KEY_NAME, key);
		edit.putString(ACCESS_SECRET_NAME, secret);
		edit.commit();
	}

	private void clearKeys() {
		SharedPreferences prefs = mainActivity.getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
		Editor edit = prefs.edit();
		edit.clear();
		edit.commit();
	}

	private AndroidAuthSession buildSession() {
		AppKeyPair appKeyPair = new AppKeyPair(APP_KEY, APP_SECRET);
		AndroidAuthSession session;

		String[] stored = getKeys();
		if (stored != null) {
			AccessTokenPair accessToken = new AccessTokenPair(stored[0], stored[1]);
			session = new AndroidAuthSession(appKeyPair, ACCESS_TYPE, accessToken);
		} else {
			session = new AndroidAuthSession(appKeyPair, ACCESS_TYPE);
		}

		return session;
	}

	private void checkAppKeySetup() {
		// Check to make sure that we have a valid app key
		if (APP_KEY.startsWith("CHANGE") ||
				APP_SECRET.startsWith("CHANGE")) {
			showToast(getString(R.string.dropb_error_appkey));
			mainActivity.finish();
			return;
		}

		// Check if the app has set up its manifest properly.
		Intent testIntent = new Intent(Intent.ACTION_VIEW);
		String scheme = "db-" + APP_KEY;
		String uri = scheme + "://" + AuthActivity.AUTH_VERSION + "/test";
		testIntent.setData(Uri.parse(uri));
		PackageManager pm = mainActivity.getPackageManager();
		if (0 == pm.queryIntentActivities(testIntent, 0).size()) {
			showToast("URL scheme in your app's " +
					"manifest is not set up correctly. You should have a " +
					"com.dropbox.client2.android.AuthActivity with the " +
					"scheme: " + scheme);
			mainActivity.finish();
		}
	}

	public static void showToast(String msg) {
		Toast error = Toast.makeText(mainActivity, msg, Toast.LENGTH_SHORT);
		error.show();
	}
		
		
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}
        
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) { 
    	mainActivity = getActivity();
    	SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mainActivity);
    	theme_color = sp.getString("pref_color_theme_entries","Black");

    	rootView = inflater.inflate(R.layout.bib_main_activity,container,false);
    	
    	// 1. get a reference to recyclerView
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        // 2. set layoutManger
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mainActivity));
        //RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(mainActivity, LinearLayoutManager.VERTICAL);
        //mRecyclerView.addItemDecoration(itemDecoration);
        
        super.onCreate(savedInstanceState);
        Configuration config = mainActivity.getBaseContext().getResources().getConfiguration();

        String lang = sp.getString("pref_language", "");
        if (! "".equals(lang) && ! config.locale.getLanguage().equals(lang))
        {
            locale = new Locale(lang);
            Locale.setDefault(locale);
            config.locale = locale;
            mainActivity.getBaseContext().getResources().updateConfiguration(config, mainActivity.getBaseContext().getResources().getDisplayMetrics());
        }

    
        
        AndroidAuthSession session = buildSession();
        mApi = new DropboxAPI<AndroidAuthSession>(session);
        checkAppKeySetup();
        
        listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        	  @Override
			public void onSharedPreferenceChanged(SharedPreferences sp, String key) {
        		  
        		if(key.equalsIgnoreCase("isPictsEnabled")){
        	  		final boolean isPictsEnabledIn = sp.getBoolean("isPictsEnabled", true);
	        		//showBibs(isPictsEnabledIn);
	        	  	}
        	  	}        	  
        	};

        sp.registerOnSharedPreferenceChangeListener(listener);
        
         
        
        //Bibitems = readBibItems();
        readBibItems();
        // 3. create an adapter
        mRecyclerAdapter = new BibRecyclerAdapter(MainActivity.Bibitems);
        registerForContextMenu(mRecyclerView);
        mRecyclerView.setAdapter(mRecyclerAdapter);
        // 5. set item animator to DefaultAnimator
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        //mRecyclerView.setOnClickListener(null);
        
        FAB = (ImageButton) rootView.findViewById(R.id.fab_image_button);
        FAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            	newBib();
            }
        });

		setLoggedIn(mApi.getSession().isLinked());	
		//saveBibItems(Bibitems);
 	
		
		return rootView;
    }
 
    /* I need a method for the menu on each card, but this is not it
     * 
     * 
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mainmenu, menu);
        return true;
    }*/
    

    public void newBib() 
	{
    	if (VariousMethods.checkPrefs(mainActivity)){

    	   Intent newBibIn = new Intent(mainActivity, BibNewActivity.class);
    	   requestCode = 2;//Activity.RESULT_OK;
	       
	       startActivityForResult(newBibIn,requestCode);
  	    } else {
			VariousMethods.prefDialog(mainActivity);
  	    }  	 
	} 
    
    public void deleteAll() 
	{
    	
    	AlertDialog.Builder alertDialog = new AlertDialog.Builder(mainActivity);

        alertDialog.setTitle(getString(R.string.main_delete));
        alertDialog.setMessage(getString(R.string.main_deletemessg));
        alertDialog.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {

        	  @Override
			public void onClick(DialogInterface arg0, int arg1) {
        		  mRecyclerAdapter.removeAll();
        		  }
        	  });
        alertDialog.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
        	       
        	  @Override
			public void onClick(DialogInterface arg0, int arg1) {
        	  // do something when the Cancel button is clicked
        	  }});
        alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
        alertDialog.show();
        
	} 
    
    
    public void readBibItems(){
    	if (MainActivity.Bibitems.isEmpty()) {
    		MainActivity.Bibitems = VariousMethods.readBibliographies(mainActivity);
    	}     	
    }
    
    public static void saveBibItems(ArrayList<Bib> currentBibitems){
		VariousMethods.saveBibliographies(currentBibitems,mainActivity);
    	MainActivity.Bibitems = currentBibitems;
    }
    
  

    
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
      super.onActivityResult(requestCode, resultCode, data);
      
      switch(requestCode) {
        case 2: {//Activity.RESULT_OK: {
          if (resultCode == 2) {//Activity.RESULT_OK) {
        	  Bundle extrasBibItem = data.getExtras();
        	  Bib tempBib = (Bib)extrasBibItem.getSerializable("bib");
        	  tempBib.setID(mRecyclerAdapter.getItemCount());
        	  mRecyclerAdapter.add(tempBib);
        	  MainTabFragment.mPagerAdapter.notifyDataSetChanged();
        	  saveBibItems(mRecyclerAdapter.getItems());

              MainActivity.Newsitems.add(0, new News(tempBib, "added"));
			  VariousMethods.saveNews(MainActivity.Newsitems, mainActivity);
              if (MainActivity.newsFragment!=null){
                  MainActivity.newsFragment.update();
              }
          }
          break;
        }
        case 3: {
        	if (resultCode == 3) {
        		Bundle extrasBibItem = data.getExtras();
        		Bib tempBib = (Bib)extrasBibItem.getSerializable("bib");
        		int Bibid = selectedBib.getID();
        		tempBib.setID(Bibid);
        		mRecyclerAdapter.replace(Bibid, tempBib);//BibitemReplace(tempBib);
        		MainTabFragment.mPagerAdapter.notifyDataSetChanged();
        		saveBibItems(mRecyclerAdapter.getItems());

                MainActivity.Newsitems.add(0, new News(tempBib, "updated"));
				VariousMethods.saveNews(MainActivity.Newsitems, mainActivity);
                if (MainActivity.newsFragment!=null){
                    MainActivity.newsFragment.update();
                }
        	}
        	break;
        }
        default: {
        	saveBibItems(mRecyclerAdapter.getItems());
        	break;
        }
      }
    }
    
    
    
    @Override
	public void onResume() {
        super.onResume();
        allTopics = MainActivity.allTopics;
        AndroidAuthSession session = mApi.getSession();

        // The next part must be inserted in the onResume() method of the
        // activity from which session.startAuthentication() was called, so
        // that Dropbox authentication completes properly.
        if (session.authenticationSuccessful()) {
            try {
                // Mandatory call to complete the auth
                session.finishAuthentication();

                // Store it locally in our app for later use
                TokenPair tokens = session.getAccessTokenPair();
                storeKeys(tokens.key, tokens.secret);
                setLoggedIn(mApi.getSession().isLinked());
            } catch (IllegalStateException e) {
                showToast(getString(R.string.dropb_error_authen) + e.getLocalizedMessage());
                Log.i(TAG, "Error authenticating", e);
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState)
    {
	     super.onSaveInstanceState(savedInstanceState);
    }

	@Override
	public void onPause() { 
         MainActivity.allTopics = allTopics;
         super.onPause();
    }

	@Override
	public void update() {
		mRecyclerView.setAdapter(mRecyclerAdapter);
	}
	 
 
}

