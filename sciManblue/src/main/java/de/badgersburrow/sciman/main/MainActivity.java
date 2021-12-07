package de.badgersburrow.sciman.main;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;

import de.badgersburrow.sciman.AboutActivity;
import de.badgersburrow.sciman.PrefsActivity;
import de.badgersburrow.sciman.R;
import de.badgersburrow.sciman.objects.Bib;
import de.badgersburrow.sciman.objects.Conference;
import de.badgersburrow.sciman.preferences.EditPreferences;
import de.badgersburrow.sciman.objects.News;
import de.badgersburrow.sciman.utilities.VariousMethods;
import de.badgersburrow.sciman.objects.Topics;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;


import android.provider.Settings;
import android.support.design.widget.NavigationView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    public static ArrayList<Bib> Bibitems = new ArrayList<Bib>();
    public static ArrayList<Conference> Confitems = new ArrayList<Conference>();
    public static ArrayList<News> Newsitems = new ArrayList<>();
    public static Map<String,Conference> ConfitemsDict;

    public static Map<String, Integer> topicsDict;
    public static Map<String, Integer> bibitemtypesDict;

    public static Topics allTopics = new Topics();
    private Locale locale = null;
    SharedPreferences sp;

    static int currentTab = 0;
    static AppCompatActivity mainActivity;


    private NavigationView navigationView;                           // Declaring the Toolbar Object
    Toolbar mToolbar;
    float elevationToolbar;

    DrawerLayout mDrawerLayout;
    MainTabFragment tabFragment;
    public static MainNewsFragment newsFragment;
    MainProfileFragment profileFragment;
    MainContactsFragment contactsFragment;

    static final String tabFragString = "tabFrag";
    static final String newsFragString = "newsFrag";
    static final String profileFragString = "profileFrag";
    static final String contactsFragString = "contactsFrag";
    static final String fragActiveString = "fragActive";
    int fragActivePos;


    public static final String TAG = "de.tavendo.test1";
    public final static String wsuri = "ws://dajoe.duckdns.org:9020";//"ws://192.168.2.112:9020";//
    public final static String passwdSecret       = "78kf#ds2a3";
    public static String appId;
    public static String deviceId;
    public static int userId;

    final static public int requestIdGetConf            = 1;
    final static public int requestIdGetConfHeader      = 2;
    final static public int requestIdGetConfList        = 3;
    final static public int requestIdUserLogin          = 4;
    final static public int requestIdUserSignup         = 5;

    final static public int failureIdUserSignupKeyerror = -5;
    final static public int failureIdUserSignupEmail    = -4;
    final static public int failureIdUserSignupInput    = -3;

    final static public int failureIdUserLoginKeyerror      = -5;
    final static public int failureIdUserLoginCredentials   = -4;
    final static public int failureIdUserLoginInput         = -3;

    final static public String sharedPrefUserDetails                = "userDetails";
    final static public String sharedPrefUserDetailsEmail           = "email";
    final static public String sharedPrefUserDetailsPasswordHash    = "passwordHash";
    final static public String sharedPrefUserDetailsName            = "name";
    final static public String defaultPasswordHash                  = "None";
    SharedPreferences.OnSharedPreferenceChangeListener mOnSharedPreferenceChangeListener;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);










        topicsDict = VariousMethods.getTopicDict(this);
        bibitemtypesDict = VariousMethods.getBibitemTypeDict(this);
        Newsitems = VariousMethods.readNews(this);
        mainActivity = this;
        appId = getPackageName();
        deviceId = Settings.Secure.getString(mainActivity.getContentResolver(), Settings.Secure.ANDROID_ID);
        userId = 5;



        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        elevationToolbar = getResources().getDimension(R.dimen.elevation_toolbar);

        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
        }
        mDrawerLayout = (DrawerLayout) findViewById(R.id.DrawerLayout);
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, mToolbar,
                R.string.drawer_open, R.string.drawer_close
        );
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        getSupportActionBar().setHomeButtonEnabled(true);
        mDrawerToggle.syncState();

        if (savedInstanceState != null) {
            // Restore value of members from saved state
            fragActivePos = savedInstanceState.getInt(fragActiveString);
            tabFragment      = (MainTabFragment) getSupportFragmentManager().findFragmentByTag(tabFragString);
            newsFragment     = (MainNewsFragment) getSupportFragmentManager().findFragmentByTag(newsFragString);
            contactsFragment = (MainContactsFragment) getSupportFragmentManager().findFragmentByTag(contactsFragString);
            profileFragment  = (MainProfileFragment) getSupportFragmentManager().findFragmentByTag(profileFragString);
            selectItem(fragActivePos);
        } else {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            tabFragment = new MainTabFragment();
            fragActivePos = R.id.nav_sciman;
            fragmentTransaction.add(R.id.frame, tabFragment,"tabFrag");
            fragmentTransaction.commit();
            getSupportActionBar().setElevation(0);
            getSupportActionBar().setTitle(R.string.app_name);
        }

        View headerView = LayoutInflater.from(this).inflate(R.layout.main_nav_header, null);
        final TextView tv_name = (TextView) headerView.findViewById(R.id.nav_header_name);
        final TextView tv_email = (TextView) headerView.findViewById(R.id.nav_header_email);

        //Initializing NavigationView
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.addHeaderView(headerView);

        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                int id = menuItem.getItemId();
                if ((id == R.id.nav_sciman) || (id == R.id.nav_news) || (id == R.id.nav_profile) || (id == R.id.nav_contacts)) {
                    fragActivePos = menuItem.getItemId();
                }


                //Checking if the item is in checked state or not, if not make it in checked state
                if (menuItem.isChecked()) menuItem.setChecked(false);
                else menuItem.setChecked(true);

                //Closing drawer on item click
                mDrawerLayout.closeDrawers();


                //Check to see which item was being clicked and perform appropriate action
                return selectItem(menuItem.getItemId());


            }
        });
        SharedPreferences userDetails = getSharedPreferences(sharedPrefUserDetails, MODE_PRIVATE);
        String userEmail = userDetails.getString(sharedPrefUserDetailsEmail, null);
        String userPasswordHash = userDetails.getString(sharedPrefUserDetailsPasswordHash, null);
        String userName = userDetails.getString(sharedPrefUserDetailsName, null);

        if (userEmail==null || userPasswordHash==null || userName==null){
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        } else {
            tv_name.setText(userName);
            tv_email.setText(userEmail);
        }


        mOnSharedPreferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                if (key.equals(sharedPrefUserDetailsName)){
                    String userName = sharedPreferences.getString(sharedPrefUserDetailsName, null);
                    tv_name.setText(userName);
                }
                if (key.equals(sharedPrefUserDetailsEmail)){
                    String userEmail = sharedPreferences.getString(sharedPrefUserDetailsEmail, null);
                    tv_email.setText(userEmail);
                }
            }
        };
        userDetails.registerOnSharedPreferenceChangeListener(mOnSharedPreferenceChangeListener);

    }

    public boolean selectItem(int id) {

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        switch (id) {
            case R.id.nav_sciman:
                if (newsFragment != null) fragmentTransaction.hide(newsFragment);
                if (profileFragment != null) fragmentTransaction.hide(profileFragment);
                if (contactsFragment != null) fragmentTransaction.hide(contactsFragment);

                if (tabFragment == null) {
                    tabFragment = new MainTabFragment();
                    fragmentTransaction.add(R.id.frame, tabFragment, tabFragString);
                } else {
                    fragmentTransaction.show(tabFragment);
                }
                fragmentTransaction.commit();

                getSupportActionBar().setElevation(0);
                getSupportActionBar().setTitle(R.string.app_name);
                return true;
            case R.id.nav_news:
                if (tabFragment != null) fragmentTransaction.hide(tabFragment);
                if (profileFragment != null) fragmentTransaction.hide(profileFragment);
                if (contactsFragment != null) fragmentTransaction.hide(contactsFragment);


                if (newsFragment == null) {
                    newsFragment = new MainNewsFragment();
                    fragmentTransaction.add(R.id.frame, newsFragment, newsFragString);
                } else {
                    fragmentTransaction.show(newsFragment);
                }
                fragmentTransaction.commit();

                getSupportActionBar().setElevation(elevationToolbar);
                getSupportActionBar().setTitle(R.string.main_nav_news);
                return true;

            //Replacing the main content with ContentFragment Which is our Inbox View;
            case R.id.nav_profile:
                if (tabFragment != null) fragmentTransaction.hide(tabFragment);
                if (newsFragment != null) fragmentTransaction.hide(newsFragment);
                if (contactsFragment != null) fragmentTransaction.hide(contactsFragment);

                if (profileFragment == null) {
                    profileFragment = new MainProfileFragment();
                    fragmentTransaction.add(R.id.frame, profileFragment, profileFragString);
                } else {
                    fragmentTransaction.show(profileFragment);
                }

                fragmentTransaction.commit();
                getSupportActionBar().setElevation(elevationToolbar);
                getSupportActionBar().setTitle(R.string.main_nav_profile);
                return true;

            // For rest of the options we just show a toast on click

            case R.id.nav_contacts:
                if (tabFragment != null) fragmentTransaction.hide(tabFragment);
                if (newsFragment != null) fragmentTransaction.hide(newsFragment);
                if (profileFragment != null) fragmentTransaction.hide(profileFragment);

                if (contactsFragment == null) {
                    contactsFragment = new MainContactsFragment();
                    fragmentTransaction.add(R.id.frame, contactsFragment,contactsFragString);
                } else {
                    fragmentTransaction.show(contactsFragment);
                }
                fragmentTransaction.commit();
                getSupportActionBar().setElevation(elevationToolbar);
                getSupportActionBar().setTitle(R.string.main_nav_contacts);
                return true;
            case R.id.nav_settings:
                Intent i = new Intent(mainActivity, PrefsActivity.class);
                startActivity(i);
                return true;
            case R.id.nav_info:
                OpenAbout();
                return true;
            default:
                Toast.makeText(getApplicationContext(), "Somethings Wrong", Toast.LENGTH_SHORT).show();
                return true;
        }
    }



    
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_menu, menu);
		return true;
	}
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	
        int itemId = item.getItemId();
		if (itemId == R.id.newItem) {
			newItem();
			return true;
		} else if (itemId == R.id.deleteAllItems) {
			//deleteAll();
		} else if (itemId == R.id.preferences) {
			startActivity(new Intent(this, EditPreferences.class));
			return true;
		} else if (itemId == R.id.about) {
			OpenAbout();  //Toast.makeText(this, "About Bib", Toast.LENGTH_LONG).show();
		} else {
			return super.onOptionsItemSelected(item);
		}
        return true;
    }
    
    public void newItem(){
        /*
    	if (currentTab==0){
    		BibFragment BibFragmentInstance = (BibFragment)this.getFragmentManager().findFragmentByTag("BibFragment");
    		BibFragmentInstance.newBib();
    	} else if (currentTab==1){
    		ConfFragment ConfFragmentInstance = (ConfFragment)this.getFragmentManager().findFragmentByTag("ConfFragment");
    		ConfFragmentInstance.newConf();
    	} else if (currentTab==2){
    		MyFragment MyFragmentInstance = (MyFragment)this.getFragmentManager().findFragmentByTag("MyFragment");
    		//MyFragmentInstance.newMy();
    	}*/
    }


    
    public void OpenAbout() 
	{
		Intent aboutIntent = new Intent(this, AboutActivity.class);
		startActivity(aboutIntent);
	}


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putInt(fragActiveString, fragActivePos);
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    
}