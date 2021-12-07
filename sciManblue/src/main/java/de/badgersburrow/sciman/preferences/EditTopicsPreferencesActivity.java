package de.badgersburrow.sciman.preferences;

import java.io.File;
import java.util.List;

import de.badgersburrow.sciman.R;
import de.badgersburrow.sciman.main.MainActivity;
import de.badgersburrow.sciman.objects.Topics;
import de.badgersburrow.sciman.utilities.VariousMethods;
import de.badgersburrow.sciman.utilities.PictureLink;

//import de.badgersburrow.sciman.NewBibActivity.LoadViewTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.TableRow.LayoutParams;



import android.app.ProgressDialog;
import android.os.AsyncTask;

//a custom preference screen for the conferences to set the savepath: folder + extern
public class EditTopicsPreferencesActivity extends Activity {	
	Topics allTopics = MainActivity.allTopics;
	String theme_color;
	private LinearLayout ll_topics;
	private int numberOfAddedTopics=0;
	private int numberOfDeletedTopics=0;
	
	
	public void deleteTopic(int id){
    	
    	View topic_v = findViewById(id);
    	ll_topics.removeView(topic_v);
    	allTopics.deleteTopic(numberOfAddedTopics-numberOfDeletedTopics-1);
    	numberOfDeletedTopics++;
    	
    }
	
	 /** Called when the activity is first created. */
	
    @Override
    public void onCreate(Bundle savedInstanceState) {  
    	
    	SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        theme_color = sp.getString("pref_color_theme_entries","Black");
        if (theme_color.equalsIgnoreCase("Black")){
        	setTheme(android.R.style.Theme_Black_NoTitleBar);
        } else if (theme_color.equalsIgnoreCase("White")){
        	setTheme(android.R.style.Theme_Light_NoTitleBar);
        }
        // Obtain the sharedPreference, default to true if not available
        super.onCreate(savedInstanceState); 
        setContentView(R.layout.topicspreference);

        
        LayoutInflater li_header = getLayoutInflater();
        View v;
		v = li_header.inflate(android.R.layout.preference_category, null);
		TextView tv_header = (TextView)v.findViewById(android.R.id.title);  
		tv_header.setText(getResources().getString(R.string.pref_screen_topics));
		v.setLayoutParams(new LayoutParams(
    			LayoutParams.MATCH_PARENT,
    			LayoutParams.MATCH_PARENT));
		tv_header.setPadding(6, 3, 0, 3);
		
		LinearLayout ll_header = (LinearLayout)findViewById(R.id.ll_header);
		ll_header.addView(v);
		
		final EditText ed_topics = (EditText)findViewById(R.id.ed_topics);
        ll_topics = (LinearLayout)findViewById(R.id.ll_topics);
        
        ImageView iv_addtopic = (ImageView)findViewById(R.id.iv_addtopic);
        
        for (int i=0; i<allTopics.getNumberOfTopics();i++){
    	  	View topic_v;
			LayoutInflater li = getLayoutInflater();
			topic_v = li.inflate(R.layout.topic, null);

			topic_v.setId(numberOfAddedTopics);
			TextView tv_topic = (TextView)topic_v.findViewById(R.id.tv_topic);
			tv_topic.setText(allTopics.getTopic(i ));
			PictureLink iv_delete = (PictureLink)topic_v.findViewById(R.id.iv_delete);
			iv_delete.setRownumber(numberOfAddedTopics);
			iv_delete.setOnClickListener(new View.OnClickListener() {
	        	public void onClick(View delete_v) {
	        		deleteTopic(((PictureLink) delete_v).getRownumber());
	        	}
			});        			
			numberOfAddedTopics++;
			ll_topics.addView(topic_v);
        }
        
        iv_addtopic.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View v) {
        		String newTopic = ed_topics.getText().toString();
        		if (newTopic!=null && !newTopic.equalsIgnoreCase("") && !allTopics.isItem(newTopic))
        		//and not already added to Conftopics
        		{    
        			View topic_v;
        			LayoutInflater li = getLayoutInflater();
        			topic_v = li.inflate(R.layout.topic, null);

        			topic_v.setId(numberOfAddedTopics);
        			TextView tv_topic = (TextView)topic_v.findViewById(R.id.tv_topic);
        			tv_topic.setText(ed_topics.getText().toString());
        			PictureLink iv_delete = (PictureLink)topic_v.findViewById(R.id.iv_delete);
        			iv_delete.setRownumber(numberOfAddedTopics);
        			iv_delete.setOnClickListener(new View.OnClickListener() {
        	        	public void onClick(View delete_v) {
        	        		deleteTopic(((PictureLink) delete_v).getRownumber());
        	        	}
        			});        			
        			numberOfAddedTopics++;
        			allTopics.addTopic(newTopic);    			
        			ll_topics.addView(topic_v);
        			ed_topics.setText("");        			
				} else{
					
				}
        	}
        });
        
        //final TextView testtext = (TextView)findViewById(R.id.testtext);
        Button bt_ok = (Button)findViewById(R.id.bt_ok);
        bt_ok.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				VariousMethods.saveTopics(allTopics, EditTopicsPreferencesActivity.this);
				MainActivity.allTopics = allTopics;
				finish(); 						   			
			}			
		});      
    }
}
