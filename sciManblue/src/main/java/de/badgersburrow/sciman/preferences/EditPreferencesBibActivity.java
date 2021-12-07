package de.badgersburrow.sciman.preferences;

//import org.xmlpull.v1.XmlPullParser;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.widget.Toast;

import de.badgersburrow.sciman.R;


public class EditPreferencesBibActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener
{
	//OnSharedPreferenceChangeListener listener;
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
    	/*boolean isAutotextsizeEnabled = sp.getBoolean("isAutotextsizeEnabled", true);
        if (isAutotextsizeEnabled)
        {*/
        	addPreferencesFromResource(R.xml.preferences_bib);
        /*}
        else{
        	addPreferencesFromResource(R.xml.preferences_sb);
        }*/
        

        try{
    		String prefTextSize = String.valueOf(sp.getInt("prefTextSize", 10));
        	SlidebarPreference prefTextSizePref = (SlidebarPreference)findPreference("prefTextSize");
        	prefTextSizePref.setSummary(getString(R.string.pref_textsizecurrent) + prefTextSize);
    	}catch(RuntimeException e){
    		
    	}    	
    	getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);        
    }
    
    
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        /*if (key.equals("isAutotextsizeEnabled")){
        	
        	boolean isAutotextsizeEnabled = sharedPreferences.getBoolean("isAutotextsizeEnabled", true);
        	if (isAutotextsizeEnabled)
            {
        		
        		PreferenceScreen prefScreen = getPreferenceScreen();
        		prefScreen.removeAll();

            	addPreferencesFromResource(R.xml.preferences);
            }
            else{
            	PreferenceScreen prefScreen = getPreferenceScreen();
        		prefScreen.removeAll();
            	addPreferencesFromResource(R.xml.preferences_sb);
            }
        }
        
        if (key.equals("pref_language")){
        	Toast.makeText(this, getString(R.string.pref_language_warn), Toast.LENGTH_LONG).show();
        }
        */
        if (key.equals(PREFERENCE_KEY)){
        	try{
        		String prefTextSize = String.valueOf(sharedPreferences.getInt("prefTextSize", 10));
            	SlidebarPreference prefTextSizePref = (SlidebarPreference)findPreference("prefTextSize");
            	prefTextSizePref.setSummary(getString(R.string.pref_textsizecurrent) + prefTextSize);
        	}catch(RuntimeException e){
        		
        	}        	
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