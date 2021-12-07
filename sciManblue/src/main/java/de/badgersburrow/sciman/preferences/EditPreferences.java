package de.badgersburrow.sciman.preferences;

//import org.xmlpull.v1.XmlPullParser;

import java.util.Locale;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.res.Configuration;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.widget.Toast;

import de.badgersburrow.sciman.R;


public class EditPreferences extends PreferenceActivity implements OnSharedPreferenceChangeListener
{
	OnSharedPreferenceChangeListener listener;
	private Locale locale = null;
	private static final String PREFERENCE_KEY = "prefTextSize";
    @Override
    public void onCreate(Bundle savedInstanceState)
    {	
    	SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        String theme_color = sp.getString("pref_color_theme_entries","Black");
        if (theme_color.equalsIgnoreCase("White")){
        	setTheme(android.R.style.Theme_Light_NoTitleBar);
        }
    	super.onCreate(savedInstanceState);
    	addPreferencesFromResource(R.xml.preferences);
        
        
       PreferenceScreen folderscreen = (PreferenceScreen)findPreference("prefscreen_folder");
       Intent folderprefs = new Intent(this,EditFolderPreferencesActivity.class);
       folderscreen.setIntent(folderprefs);
       
       PreferenceScreen topicsscreen = (PreferenceScreen)findPreference("prefscreen_topics");
       Intent topicsprefs = new Intent(this,EditTopicsPreferencesActivity.class);
       topicsscreen.setIntent(topicsprefs);
       
       PreferenceScreen ps_bibscreen = (PreferenceScreen)findPreference("prefscreen_bib");
       Intent i_bibscreen = new Intent(this,EditPreferencesBibActivity.class);
       ps_bibscreen.setIntent(i_bibscreen);
       
       PreferenceScreen ps_confscreen = (PreferenceScreen)findPreference("prefscreen_conf");
       Intent i_confscreen = new Intent(this,EditPreferencesConfActivity.class);
       ps_confscreen.setIntent(i_confscreen);
       
        	
    	getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);        
    }
    
    
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
       
       if (key.equals("pref_color_theme_entries")){
       	
    	    finish();
       		//PreferenceScreen prefScreen = getPreferenceScreen();
       		//prefScreen.removeAll();
       		String theme_color = sharedPreferences.getString("pref_color_theme_entries","Black");
       		if (theme_color.equalsIgnoreCase("White")){
            	setTheme(android.R.style.Theme_Light_NoTitleBar);
            } else if (theme_color.equalsIgnoreCase("Black")){
            	setTheme(android.R.style.Theme_Black_NoTitleBar);
            }
       		
       		android.os.SystemClock.sleep(300);
       		startActivity(new Intent(this, this.getClass()));
           	addPreferencesFromResource(R.xml.preferences);
           
       }
       
       if (key.equals("pref_language")){
    	   finish();
    	   Configuration config = getBaseContext().getResources().getConfiguration();

           String lang = sharedPreferences.getString("pref_language", "");
           if (! "".equals(lang) && ! config.locale.getLanguage().equals(lang))
           {
               locale = new Locale(lang);
               Locale.setDefault(locale);
               config.locale = locale;
               getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
               
           }
           android.os.SystemClock.sleep(300);
      		startActivity(new Intent(this, this.getClass()));
       	//Toast.makeText(this, getString(R.string.pref_language_warn), Toast.LENGTH_LONG).show();
       }
       
              
    }
  
    
    @Override
    protected void onResume()
    {
        super.onResume();              
    }
    
    @Override
	public void onBackPressed() {    	
	   onDestroy();
	   super.onBackPressed();//finish();
	}
    
    @Override
    protected void onDestroy() {
    // Unregister from changes
      getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
      super.onDestroy();
    }
}