package de.badgersburrow.sciman.preferences;

import java.util.List;

import de.badgersburrow.sciman.R;
import de.badgersburrow.sciman.utilities.VariousMethods;

//import de.badgersburrow.sciman.NewBibActivity.LoadViewTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
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
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.TableRow.LayoutParams;



import android.app.ProgressDialog;
import android.os.AsyncTask;

//a custom preference screen for the conferences to set the savepath: folder + extern
public class EditFolderPreferencesActivity extends Activity {	
	ListView dialog_ListView;
	
	private String savePath = null;
	private boolean extern = false;
	private String[] sources = null;
	private String[] sources_values = null;
	private String[] sourcesCode = new String[] {"None", "Dropbox","manualApp"};
	private String selectedSource = "None";
	private String manualApp = "None";
	private String manualAppName = "None";
	private String[] installedAppArray;
	private String[] installedAppNameArray;
	private boolean hideSysApps=true;
	
	String KEY_TEXTPSS = "TEXTPSS";
	static final int CUSTOM_DIALOG_ID = 0;
	
	private static String Custom_pref = "custompref";
	private static String Custom_pref_path = "SavePath";
	private static String Custom_pref_extern = "SavePathExtern";
	private static String Custom_pref_syncmethod = "Syncmethod";
	private static String Custom_pref_manualapp = "manualApp";
	private static String Custom_pref_manualappname = "manualAppName";

	//A ProgressDialog object
		private ProgressDialog progressDialog;
	   

	    //To use the AsyncTask, it must be subclassed
	    private class LoadViewTask extends AsyncTask<Void, Integer, Void>
	    {
	    	//Before running code in separate thread
			@Override
			protected void onPreExecute()
			{
				//Create a new progress dialog
				progressDialog = ProgressDialog.show(EditFolderPreferencesActivity.this,getString(R.string.nb_loadingtit),  
						getString(R.string.nb_loadinginfo), false, false);
			}

			//The code to be executed in a background thread.
			@Override
			protected Void doInBackground(Void... params)
			{
				/* This is just a code that delays the thread execution 4 times,
				 * during 850 milliseconds and updates the current progress. This
				 * is where the code that is going to be executed on a background
				 * thread must be placed.
				 */
				/*try
				{*/
					//Get the current thread's token
					synchronized (this)
					{
						chooseApp();						
					}

				return null;
			}

			//Update the progress
			@Override
			protected void onProgressUpdate(Integer... values)
			{
				//set the current progress of the progress dialog
				progressDialog.setProgress(values[0]);
			}

			//after executing the code in the thread
			@Override
			protected void onPostExecute(Void result)
			{
				//close the progress dialog
				progressDialog.dismiss();
				//initialize the View
				//setContentView(R.layout.newbib);
				showDialog(CUSTOM_DIALOG_ID);
			}
	    }
	    
    @Override
	public void onBackPressed() {    	
    	// do something on back.
    	Intent mIntent = new Intent();
        //mIntent.putExtras(bibAttributes);
		setResult(0,mIntent);//Activity.RESULT_OK,  mIntent);		
		super.onBackPressed();//finish();
	}

	
	 /** Called when the activity is first created. */
	
    @Override
    public void onCreate(Bundle savedInstanceState) {  
    	
    	SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        String theme_color = sp.getString("pref_color_theme_entries","Black");
        if (theme_color.equalsIgnoreCase("Black")){
        	setTheme(android.R.style.Theme_Black_NoTitleBar);
        } else if (theme_color.equalsIgnoreCase("White")){
        	setTheme(android.R.style.Theme_Light_NoTitleBar);
        }
        // Obtain the sharedPreference, default to true if not available
        super.onCreate(savedInstanceState); 
        setContentView(R.layout.custompreference);

        LayoutInflater li = getLayoutInflater();
        View v;
		v = li.inflate(android.R.layout.preference_category, null);
		TextView tv_header = (TextView)v.findViewById(android.R.id.title);  
		tv_header.setText(getResources().getString(R.string.custompref_header));
		v.setLayoutParams(new LayoutParams(
    			LayoutParams.MATCH_PARENT,
    			LayoutParams.MATCH_PARENT));
		tv_header.setPadding(6, 3, 0, 3);
		
		LinearLayout ll_header = (LinearLayout)findViewById(R.id.ll_header);
		ll_header.addView(v);
        
        final EditText ed_savepath = (EditText)findViewById(R.id.ed_savepath);        
        final CheckBox cb_extern = (CheckBox)findViewById(R.id.cb_confextern);
        sources = getResources().getStringArray(R.array.sources_wn);
        sources_values = getResources().getStringArray(R.array.sources_wn_values);
        			        
        //set text, from before
        //Bibitem = readBibItem();
       SharedPreferences confpref = getSharedPreferences(Custom_pref, 0);
       if (savePath != null){
            ed_savepath.setText(savePath);           
            cb_extern.setChecked(extern);
    	} else {
    		
    		savePath = confpref.getString(Custom_pref_path,null);
    		extern = confpref.getBoolean(Custom_pref_extern,false);
    		if (savePath != null){
                ed_savepath.setText(savePath);           
                cb_extern.setChecked(extern);
        	} 
    	}
        selectedSource = confpref.getString(Custom_pref_syncmethod,"None");
        manualApp = confpref.getString(Custom_pref_manualapp,"None");//getResources().getString(R.string.none));
        manualAppName = confpref.getString(Custom_pref_manualappname,"None");//getResources().getString(R.string.none));
        changeTextforSource(selectedSource);
       
       	if (!extern && !VariousMethods.isExternalWritable()){
       		cb_extern.setEnabled(false);            
       	}
        Button bt_ok = (Button)findViewById(R.id.bt_ok);
        bt_ok.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {				
				//check if folder was changed, because the content has to be moved too 
				//check if name is can be used as a folder name
				Intent mIntent = new Intent();				
				SharedPreferences confpref = getSharedPreferences(Custom_pref, 0);
				Editor edit = confpref.edit();
				savePath = confpref.getString(Custom_pref_path,null);
	    		extern = confpref.getBoolean(Custom_pref_extern,false);
	    		String newSavePath = ed_savepath.getText().toString();
	    		boolean newExtern = cb_extern.isChecked();
	    		if (newSavePath.startsWith("/")){
	    			newSavePath=newSavePath.substring(1,newSavePath.length());
	    		}
	    		if (newSavePath.endsWith("/")){
	    			newSavePath=newSavePath.substring(0,newSavePath.length()-1);
	    		}
	    		boolean newSavePathIsOkay =true;
	    		if (newSavePath.contains("/")){
	    			String[] checkNewSavePathList=newSavePath.split("/");
	    			for(int i=0; i<checkNewSavePathList.length;i++){
	    				if (!VariousMethods.checkDirectory(checkNewSavePathList[i])){
	    					newSavePathIsOkay =false;
	    					break;
	    				}
	    			}
	    		}
	    		else {
	    			newSavePathIsOkay= VariousMethods.checkDirectory(newSavePath);
	    		}
	    		if (!newSavePathIsOkay){
	    			AlertDialog alertDialog = new AlertDialog .Builder(EditFolderPreferencesActivity.this).create();
		            alertDialog.setTitle(getString(R.string.alert));
		            alertDialog.setMessage(getString(R.string.incorrectfolder));
		            alertDialog.setButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
		                public void onClick(DialogInterface dialog, int which) {
		                           //here you can add functions		           
		              } }); 
		            alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
		        	alertDialog.show();
	    		}
	    		else {	
	    			if (savePath==null){
	    				edit.putString(Custom_pref_path, newSavePath);
						//check if sdcard is available
						edit.putBoolean(Custom_pref_extern, newExtern);
						edit.putString(Custom_pref_syncmethod,selectedSource);
						edit.putString(Custom_pref_manualapp,manualApp);//getResources().getString(R.string.none));
						edit.putString(Custom_pref_manualappname,manualAppName);//getResources().getString(R.string.none));
						edit.commit();
						VariousMethods.createMainFolder(EditFolderPreferencesActivity.this);
		    			
						setResult(4,mIntent);//Activity.RESULT_OK,  mIntent);				
						finish();
	    			} else {//if (savePath.compareTo(newSavePath)!=0 || extern!=newExtern ){	    				
						VariousMethods.changeMainFolder(EditFolderPreferencesActivity.this, newSavePath, newExtern);
	    				edit.putString(Custom_pref_path, newSavePath);
						//check if sdcard is available
						edit.putBoolean(Custom_pref_extern, newExtern);
						edit.putString(Custom_pref_syncmethod,selectedSource);
						edit.putString(Custom_pref_manualapp,manualApp);//getResources().getString(R.string.none));
						edit.putString(Custom_pref_manualappname,manualAppName);//getResources().getString(R.string.none));
						edit.commit();
						
						setResult(4,mIntent);//Activity.RESULT_OK,  mIntent);				
						finish(); 
		    			
		    			
	    			}	    			
	    		}				   			
			}			
		}); 
        
        Spinner sp_source = (Spinner)findViewById(R.id.source_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, sources);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_source.setAdapter(adapter);
        sp_source.setOnItemSelectedListener(new OnItemSelectedListener() {
            //@Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                selectedSource=sourcesCode[position];// your code here
                changeTextforSource(selectedSource);
            }

            //@Override
            public void onNothingSelected(AdapterView<?> parentView) {
            	selectedSource="None";
            }
        });
        
        TextView tv_manualapp = (TextView)findViewById(R.id.tv_manualapp);
        tv_manualapp.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				new LoadViewTask().execute();
				//chooseApp();
			}
		}); 
    }      
   
    
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState)
    {
      
		savedInstanceState.putString("savePath", savePath);
		savedInstanceState.putBoolean("extern", extern);
		//savedInstanceState.putBoolean("source", extern);
		super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState)
    {
      super.onRestoreInstanceState(savedInstanceState);
      // EditTexts tex
      savePath= savedInstanceState.getString("savePath");
      extern= savedInstanceState.getBoolean("extern");
    }
  
    public void changeTextforSource(String source){
    	if (source.equalsIgnoreCase("None")){
    		TextView tv_manualapp = (TextView)findViewById(R.id.tv_manualapp);
    		ImageView iv_arrow = (ImageView)findViewById(R.id.iv_arrow);
        	tv_manualapp.setText("");
        	//tv_manualapp.setHeight(0);
        	iv_arrow.setImageResource(R.drawable.no);
        	//tv_manualapp.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, 0));
        	tv_manualapp.setClickable(false);
        	
        } else if (source.equalsIgnoreCase("dropbox")){
        	TextView tv_manualapp = (TextView)findViewById(R.id.tv_manualapp);
        	ImageView iv_arrow = (ImageView)findViewById(R.id.iv_arrow);
        	tv_manualapp.setText("");
        	//tv_manualapp.setHeight(0);
        	iv_arrow.setImageResource(R.drawable.no);
        	//tv_manualapp.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, 0));
        	tv_manualapp.setClickable(false);
        	
        } else if (source.equalsIgnoreCase("manualApp")){
        	TextView tv_manualapp = (TextView)findViewById(R.id.tv_manualapp);
        	ImageView iv_arrow = (ImageView)findViewById(R.id.iv_arrow);
        	
        	if (manualApp.equalsIgnoreCase("None")){
        		tv_manualapp.setText(getString(R.string.nb_sourcemanualApp) + getString(R.string.nb_sourcemanualApp_None));
        	} else {
        		tv_manualapp.setText(getString(R.string.nb_sourcemanualApp) + manualAppName);
        	}
        	//iv_arrow.setMaxHeight(40);
        	//tv_manualapp.setHeight(40);
        	iv_arrow.setImageResource(R.drawable.arrow_to_app);
        	//tv_manualapp.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        	tv_manualapp.setClickable(true);        	
        }
    }
    
    @Override
    protected Dialog onCreateDialog(int id) {
    
     Dialog dialog = null;
      
     switch(id) {
        case CUSTOM_DIALOG_ID:
         dialog = new Dialog(EditFolderPreferencesActivity.this);
         dialog.setContentView(R.layout.installedapps_dialog);
         dialog.setTitle("Choose app for syncing");
          
         dialog.setCancelable(true);
         dialog.setCanceledOnTouchOutside(true);
          
         dialog.setOnCancelListener(new OnCancelListener(){
    
	       //@Override
	       public void onCancel(DialogInterface dialog) {
	        // TODO Auto-generated method stub
	        //Toast.makeText(NewBibActivity.this,"OnCancelListener",Toast.LENGTH_LONG).show();
	       }});
          
         dialog.setOnDismissListener(new OnDismissListener(){
    
       //@Override
       public void onDismiss(DialogInterface dialog) {
        // TODO Auto-generated method stub
        //Toast.makeText(NewBibActivity.this,"OnDismissListener",Toast.LENGTH_LONG).show();
       }});
    
         //Prepare ListView in dialog
         dialog_ListView = (ListView)dialog.findViewById(R.id.dialoglist);
         ArrayAdapter<String> dialogAdapter
          = new ArrayAdapter<String>(this,
        		  R.layout.installedapps_list, installedAppNameArray);
         dialog_ListView.setAdapter(dialogAdapter);
         dialog_ListView.setOnItemClickListener(new OnItemClickListener(){
    
       //@Override
       public void onItemClick(AdapterView<?> parent, View view,
         int position, long id) {
        // TODO Auto-generated method stub
        //Toast.makeText(NewBibActivity.this,parent.getItemAtPosition(position).toString() + " clicked",Toast.LENGTH_LONG).show();
        manualApp = installedAppArray[position];
        manualAppName= installedAppNameArray[position];
        changeTextforSource(selectedSource);
        dismissDialog(CUSTOM_DIALOG_ID);
       }});
          
            break;
        }
    
     return dialog;
    }
    
    @Override
    protected void onPrepareDialog(int id, Dialog dialog, Bundle bundle) {
     // TODO Auto-generated method stub
     super.onPrepareDialog(id, dialog, bundle);
    
     switch(id) {
        case CUSTOM_DIALOG_ID:
         //
            break;
        }
      
    }
    
    private boolean isSystemPackage(ApplicationInfo pkgInfo) {
    	  if (!hideSysApps) return false;
    	  else{
          return ((pkgInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0)  ? true
                  : false;
    	  }
      }
      
      public void chooseApp(){
  		final PackageManager pm = getPackageManager();

  		List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
  		
  		
  		String[] installedApps = new String[packages.size()];
  		String[] installedAppNames = new String[packages.size()];
  		int count=0;
  		for (ApplicationInfo packageInfo : packages) {
  			if (!isSystemPackage(packageInfo)){
  				installedApps[count]=packageInfo.packageName;
  				installedAppNames[count]=getAppName(packageInfo.packageName);
      			count+=1;
  			}  			
  		}
  		String[] resizedInstalledApps=new String[count];
  		String[] resizedInstalledAppNames=new String[count];
  		for (int i=0; i<count; i++){
  			resizedInstalledApps[i]=installedApps[i];
  			resizedInstalledAppNames[i]=installedAppNames[i];
  		}
  		installedAppArray=resizedInstalledApps;
  		installedAppNameArray=resizedInstalledAppNames;  		
  	}
      
      public String getAppName(String PackageName){
      	final PackageManager pm = getApplicationContext().getPackageManager();
      	ApplicationInfo ai;
      	try {
      		ai = pm.getApplicationInfo( PackageName, 0);
      	} catch (final NameNotFoundException e) {
      		ai = null;
      	}
      	final String applicationName = (String) (ai != null ? pm.getApplicationLabel(ai) : "(unknown)");
      	return applicationName;
       }  


}
