package de.badgersburrow.sciman.conftab;

import android.content.Context;
import android.os.Bundle;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;

import com.google.gson.Gson;
import de.badgersburrow.sciman.FragmentUpdate;
import de.badgersburrow.sciman.R;
import de.badgersburrow.sciman.objects.News;
import de.badgersburrow.sciman.objects.WebsocketRequest;
import de.badgersburrow.sciman.objects.Conference;
import de.badgersburrow.sciman.preferences.EditFolderPreferencesActivity;
import de.badgersburrow.sciman.main.MainActivity;
import de.badgersburrow.sciman.main.MainTabFragment;
import de.badgersburrow.sciman.objects.Topics;
import de.badgersburrow.sciman.utilities.VariousMethods;
import de.badgersburrow.sciman.objects.WebsocketReturn;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.res.Configuration;
import android.preference.PreferenceManager;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ImageButton;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketException;
import de.tavendo.autobahn.WebSocketHandler;


   

public class ConfFragment extends Fragment implements FragmentUpdate{
	View rootView = null;
	
	private Conference recentConf;    
    private String theme_color;   
	int requestCode;
	int i=0;
	int picturewidth = 48;
	int Confid=0;
	Bundle[] Conflist = new Bundle[20];
	static ArrayList<Conference> Confitems = MainActivity.Confitems;
    static Map<String,Conference> ConfitemsDict = MainActivity.ConfitemsDict;

	public RecyclerView mRecyclerView;
	public static ConfRecyclerAdapter mRecyclerAdapter;

    ImageButton FAB;

    public static Conference selectedConf;
	Topics allTopics = MainActivity.allTopics;
    static Activity mainActivity = null;



	OnSharedPreferenceChangeListener listener;		
	private Locale locale = null;



    private final static WebSocketConnection mConnection = new WebSocketConnection();


				 
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

    public void OpenConfigureConf()
	{	  
       Intent in = new Intent(mainActivity, EditFolderPreferencesActivity.class);
        startActivity(in);
	}
    
	public final static void OpenConf(Conference selectedConf, Context context)
	{
	   //SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
	   //final boolean isSavesortsearchEnabled = sp.getBoolean("isSavesortsearchEnabled", false);
       Intent in = new Intent(mainActivity, ConfViewActivity.class);
       Bundle extras = new Bundle();
       extras.putSerializable("conf", selectedConf);
       in.putExtras(extras);
        try{
            context.startActivity(in);
        }
        catch (Exception e) {
            Log.e("MyActivity::MyMethod", e.getMessage());
        }
        mainActivity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
			
	}
	
	public void OpenPoster(Conference selectedConf) 
	{
	   //SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
	   //final boolean isSavesortsearchEnabled = sp.getBoolean("isSavesortsearchEnabled", false);
       Intent in = new Intent(mainActivity, PosterViewActivity.class);  
       Bundle extras = new Bundle();
       extras.putSerializable("conf", selectedConf);
       in.putExtras(extras);
        startActivity(in);
	}
	
	public void OpenContacts(Conference selectedConf) 
	{
	   //SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
	   //final boolean isSavesortsearchEnabled = sp.getBoolean("isSavesortsearchEnabled", false);
       Intent in = new Intent(mainActivity, ContactViewActivity.class);
       Bundle extras = new Bundle();
       extras.putSerializable("conf", selectedConf);
       in.putExtras(extras);
        startActivity(in);
	}
	

	
	
	@Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		AdapterView.AdapterContextMenuInfo info =  (AdapterView.AdapterContextMenuInfo) menuInfo;
		//selectedConf = Confitems[Confid-1000];
        menu.setHeaderTitle(selectedConf.getConfname());
		MenuInflater inflater = mainActivity.getMenuInflater();
        inflater.inflate(R.menu.main_cm_conf, menu);
    }
	
	public void editConf(Conference selectedConf) {
		Intent editConfIn = new Intent(mainActivity, ConfEditActivity.class);
	   	requestCode = 3;//Activity.RESULT_OK;
	   	editConfIn.putExtra("Conf", selectedConf);
        editConfIn.putExtra("Topics", allTopics);
        startActivityForResult(editConfIn, requestCode);
	}
	
	public void removeConf(Conference selectedConf) {
        mRecyclerAdapter.remove(selectedConf);
        //Confitems.set(selectedConf.getID(),null);
        VariousMethods.deleteConference(mainActivity, selectedConf);
        MainTabFragment.mPagerAdapter.notifyDataSetChanged();
        //saveConfItems(mRecyclerAdapter.getItems());
    }

	
	public Conference[] sortConferences(Conference[] Conferences){
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mainActivity);
		String sortAfter = sp.getString("pref_sortconfs", "new");
		Conference[] sortedConferences = new Conference[100];
		for(int j = 0; j < i; j++) {
			String currentDate = Conferences[j].getDate();
			String currentName = Conferences[j].getConfname();
    		boolean inserted=false;
			for(int k = 0; k < j; k++) {
				if (sortAfter.equalsIgnoreCase("new")){
					if (currentDate.compareTo(sortedConferences[k].getDate())>0){
						for(int l = j; l > k; l--) {
							sortedConferences[l]=sortedConferences[l-1];
						}
						sortedConferences[k]=Conferences[j];
						//insertedItems++;
						inserted=true;
						break;
					} else if (currentDate.compareTo(sortedConferences[k].getDate())>0 && currentName.compareTo(sortedConferences[k].getConfname())<0){
						for(int l = j; l > k; l--) {
							sortedConferences[l]=sortedConferences[l-1];
						}
						sortedConferences[k]=Conferences[j];
						//insertedItems++;
						inserted=true;
						break;
					}
					//insertLine = k;
				} else if (sortAfter.equalsIgnoreCase("old")){
					if (currentDate.compareTo(sortedConferences[k].getDate())<0){
						for(int l = j; l > k; l--) {
							sortedConferences[l]=sortedConferences[l-1];
						}
						sortedConferences[k]=Conferences[j];
						//insertedItems++;
						inserted=true;
						break;
					} else if (currentDate.compareTo(sortedConferences[k].getDate())<0 && currentName.compareTo(sortedConferences[k].getConfname())<0){
						for(int l = j; l > k; l--) {
							sortedConferences[l]=sortedConferences[l-1];
						}
						sortedConferences[k]=Conferences[j];
						//insertedItems++;
						inserted=true;
						break;
					}
				} else if (sortAfter.equalsIgnoreCase("alpha")){
					if (currentName.compareTo(sortedConferences[k].getConfname())<0){
						for(int l = j; l > k; l--) {
							sortedConferences[l]=sortedConferences[l-1];
						}
						sortedConferences[k]=Conferences[j];
						//insertedItems++;
						inserted=true;
						break;
					}
				}
				
			}
			if (!inserted){
				sortedConferences[j]=Conferences[j];
				//insertedItems++;
			}
			
			
		}
		
		return sortedConferences;
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        int id = item.getItemId();
        if (id == R.id.ConfEditItem) {
            editConf(selectedConf);
            return true;
        } else if (id == R.id.ConfDeleteItem) {
            removeConf(selectedConf);
            return true;
        } else {
            return super.onContextItemSelected(item);
        }
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
        rootView = inflater.inflate(R.layout.conf_main, container, false);




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
          
        listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        	  @Override
			public void onSharedPreferenceChanged(SharedPreferences sp, String key) {
        	
        		if(key.equalsIgnoreCase("isPictsEnabled")){
        	  		final boolean isPictsEnabledIn = sp.getBoolean("isPictsEnabled", true);
	        		//showConfs(isPictsEnabledIn);
	        	  	}
        	  	}
        	};

        sp.registerOnSharedPreferenceChangeListener(listener);

        //Bibitems = readBibItems();
        readConfItems();
        // 3. create an adapter
        mRecyclerAdapter = new ConfRecyclerAdapter(MainActivity.Confitems);
        registerForContextMenu(mRecyclerView);
        mRecyclerView.setAdapter(mRecyclerAdapter);
        // 5. set item animator to DefaultAnimator
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        //mRecyclerView.setOnClickListener(null);

        /*FAB = (ImageButton) rootView.findViewById(R.id.fab_image_button);
        FAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newConf();
            }
        });
        */


        /*
        RelativeLayout sync_button = (RelativeLayout)rootView.findViewById(R.id.sync_button_rl);
        ImageView sync_button_iv = (ImageView)rootView.findViewById(R.id.sync_button_iv);
        
        	sync_button_iv.setImageResource(R.drawable.no);
        	sync_button.setClickable(false);

        //Confitems = sortConferences(Confitems);
        //showConfs(true);
		ImageView menu_button_iv = (ImageView)rootView.findViewById(R.id.menu_button_iv);
		RelativeLayout menu_button = (RelativeLayout)rootView.findViewById(R.id.menu_button_rl);	
    	menu_button_iv.setImageResource(R.drawable.no);  
    	menu_button.setClickable(false);
        */


        final FloatingActionsMenu fab_menu = (FloatingActionsMenu) rootView.findViewById(R.id.fab_menu);

        FloatingActionButton fab_custom = (FloatingActionButton) rootView.findViewById(R.id.fab_custom);

        fab_custom.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                fab_menu.collapse();
                newConf();
            }
        });

        FloatingActionButton fab_online = (FloatingActionButton) rootView.findViewById(R.id.fab_online);
		fab_online.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
                fab_menu.collapse();
                String condition = VariousMethods.randomString(5);
                pollServer(MainActivity.requestIdGetConfList, condition);
                /*Intent chooseConfIn = new Intent(mainActivity, ConfSelectActivity.class);
                mainActivity.startActivity(chooseConfIn);
                mainActivity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);*/

			}
		});

        FloatingActionButton fab_online_rand = (FloatingActionButton) rootView.findViewById(R.id.fab_online_rand);
        fab_online_rand.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                fab_menu.collapse();
                String confIdentifier = "0";
                pollServer(MainActivity.requestIdGetConf,confIdentifier);

            }
        });



		return rootView;
	}

    
    public void newConf()
	{
        if (VariousMethods.checkPrefs(mainActivity)){

            Intent newConfIn = new Intent(mainActivity, ConfNewActivity.class);
            requestCode = 2;//Activity.RESULT_OK;

            startActivityForResult(newConfIn, requestCode);
        } else {
            VariousMethods.prefDialog(mainActivity);
        }

    }

    public void readConfItems(){
        if (MainActivity.Confitems.isEmpty()) {
            MainActivity.Confitems = VariousMethods.readConferences(mainActivity);
            MainActivity.ConfitemsDict = VariousMethods.getConfDict(MainActivity.Confitems);
        }
    }

    public static void saveConfItems(ArrayList<Conference> currentConfitems){
    	//Utils.saveConference(this, Conf)
    	VariousMethods.saveConferences(currentConfitems, mainActivity);
		MainActivity.Confitems = currentConfitems;
    }

    /*
    public void replaceConfItem(Conference Confitem, int rownumber){

    	SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mainActivity);
        // Obtain the sharedPreference, default to true if not available
        final boolean isPictsEnabled = sp.getBoolean("isPictsEnabled", true);
    	
        
        TableRow tr_conf = (TableRow)rootView.findViewById(1000+rownumber);
        View v = tr_conf.findViewById(R.id.rl_conference);
        TextView tv_conf = (TextView)v.findViewById(R.id.conference_name);
    	   	
    	//setBackgroundPic( iv_conf, isPictsEnabled,Bibitem);
    	tv_conf.setText("  " + Confitem.getConfname());
    	
    }
    */
    	    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode) {
            case 2: {//Activity.RESULT_OK: {
                if (resultCode == 2) {//Activity.RESULT_OK) {
                    Bundle extrasConfItem = data.getExtras();
                    Conference tempConf = (Conference)extrasConfItem.getSerializable("conf");
                    tempConf.setID(mRecyclerAdapter.getItemCount());
                    mRecyclerAdapter.add(tempConf);
                    MainTabFragment.mPagerAdapter.notifyDataSetChanged();
                    saveConfItems(mRecyclerAdapter.getItems());

                    MainActivity.Newsitems.add(0, new News(tempConf, "added"));
                    VariousMethods.saveNews(MainActivity.Newsitems, mainActivity);
                    if (MainActivity.newsFragment!=null){
                        MainActivity.newsFragment.update();
                    }
                }
                break;
            }
            case 3: {
                if (resultCode == 3) {
                    Bundle extrasConfItem = data.getExtras();
                    Conference tempConf = (Conference)extrasConfItem.getSerializable("conf");
                    int Confid = selectedConf.getID();
                    tempConf.setID(Confid);
                    mRecyclerAdapter.replace(Confid, tempConf);//BibitemReplace(tempBib);
                    MainTabFragment.mPagerAdapter.notifyDataSetChanged();
                    saveConfItems(mRecyclerAdapter.getItems());

                    MainActivity.Newsitems.add(0, new News(tempConf, "updated"));
                    VariousMethods.saveNews(MainActivity.Newsitems, mainActivity);
                    if (MainActivity.newsFragment!=null){
                        MainActivity.newsFragment.update();
                    }



                }
                break;
            }
            default: {
                saveConfItems(mRecyclerAdapter.getItems());
                break;
            }
        }
    }

    // maybe even switch to ArrayList<String> instead of single argument
    public final static void pollServer(final int requestId, final String argument) {

        try {
            mConnection.connect(MainActivity.wsuri, new WebSocketHandler() {

                @Override
                public void onOpen() {
                    Log.d(MainActivity.TAG, "Status: Connected to " + MainActivity.wsuri);
                    ArrayList<String> arguments = new ArrayList<String>();
                    switch (requestId) {
                        case MainActivity.requestIdGetConf: {
                            // argument is confIdentifier
                            String reason = "getConf";

                            arguments.add(argument);
                            WebsocketRequest request = new WebsocketRequest(MainActivity.appId, MainActivity.userId, MainActivity.deviceId, reason, arguments);

                            mConnection.sendTextMessage(request.getGson());
                            break;
                        }
                        case MainActivity.requestIdGetConfHeader: {
                            // argument is confIdentifier
                            String reason = "getConfHeader";

                            arguments.add(argument);
                            WebsocketRequest request = new WebsocketRequest(MainActivity.appId, MainActivity.userId, MainActivity.deviceId, reason, arguments);

                            mConnection.sendTextMessage(request.getGson());
                            break;
                        }
                        case MainActivity.requestIdGetConfList: {
                            // argument is condition of listed confs
                            String reason = "getConfList";

                            arguments.add(argument);
                            WebsocketRequest request = new WebsocketRequest(MainActivity.appId, MainActivity.userId, MainActivity.deviceId, reason, arguments);

                            mConnection.sendTextMessage(request.getGson());
                            break;
                        }
                        default: {

                            break;
                        }
                    }


                }

                @Override
                public void onTextMessage(String payload) {
                    Log.d(MainActivity.TAG, "Got echo: " + payload);
                    Gson gson = new Gson();
                    WebsocketReturn websocketReturn = gson.fromJson(payload, WebsocketReturn.class);
                    switch (websocketReturn.getReturnType()) {
                        case MainActivity.requestIdGetConf: {
                            Log.d(MainActivity.TAG, "reason: getConf");
                            Conference Conf = websocketReturn.getConference();
                            Conf.setStandard(websocketReturn.getIdentifier());
                            VariousMethods.createConfFolder(mainActivity, Conf);
                            //Conf.setID(mRecyclerAdapter.getItemCount());
                            mRecyclerAdapter.add(Conf);

                            MainActivity.Newsitems.add(0, new News(Conf, "added"));
                            VariousMethods.saveNews(MainActivity.Newsitems, mainActivity);
                            if (MainActivity.newsFragment!=null){
                                MainActivity.newsFragment.update();
                            }


                            MainTabFragment.mPagerAdapter.notifyDataSetChanged();
                            //if (Conf.getHeaderOnServer()){
                            // also check if pictures should be loaded, while on mobile
                            String reason = "getConfHeader";
                            ArrayList<String> arguments = new ArrayList<String>();
                            arguments.add(Conf.getIdentifier());
                            WebsocketRequest request = new WebsocketRequest(MainActivity.appId, MainActivity.userId, MainActivity.deviceId, reason, arguments);

                            mConnection.sendTextMessage(request.getGson());

                            //}
                            saveConfItems(mRecyclerAdapter.getItems());

                            break;
                        }
                        case MainActivity.requestIdGetConfHeader: {
                            Log.d(MainActivity.TAG, "reason: getConfHeader");
                            Conference Confitem = MainActivity.ConfitemsDict.get(websocketReturn.getIdentifier());

                            if (websocketReturn.getReturnString() != null){
                                Confitem.setHeader(websocketReturn.getReturnString(), mainActivity);
                            }
                            saveConfItems(mRecyclerAdapter.getItems());

                            break;
                        }

                        case MainActivity.requestIdGetConfList: {
                            Log.d(MainActivity.TAG, "reason: getConfList");

                            ArrayList<String> confListIdentifier = websocketReturn.getConferenceList();
                            Intent chooseConfIn = new Intent(mainActivity, ConfSelectActivity.class);
                            chooseConfIn.putExtra("confList", confListIdentifier);
                            mainActivity.startActivity(chooseConfIn);
                            mainActivity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

                            break;
                        }
                        default: {
                            Log.d(MainActivity.TAG, "Invalid reason!");
                            break;
                        }


                    }

                    mConnection.disconnect();
                }

                @Override
                public void onClose(int code, String reason) {
                    Log.d(MainActivity.TAG, "Connection lost.");
                }
            });
        } catch (WebSocketException e) {

            Log.d(MainActivity.TAG, e.toString());
        }
    }




    public void onResume() {
        super.onResume();
        allTopics = MainActivity.allTopics;
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mConnection.isConnected()) {
            mConnection.disconnect();
        }
    }
}
