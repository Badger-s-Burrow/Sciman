package de.badgersburrow.sciman.conftab;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.graphics.drawable.Drawable;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.ActionMenuItemView;
import androidx.appcompat.widget.ActionMenuView;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import de.badgersburrow.sciman.PrefsActivity;
import de.badgersburrow.sciman.objects.Conference;
import de.badgersburrow.sciman.objects.Contact;
import de.badgersburrow.sciman.objects.Poster;
import de.badgersburrow.sciman.R;
import de.badgersburrow.sciman.objects.Topics;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;


public class ConfViewActivity extends AppCompatActivity {
	GridView gridView;
	
	// Keep all Images in array
    public Integer[] posterImagesIds = new Integer[100];
    public String[] posterImagesFiles = new String[100];
    private int requestCode =0;
    public String[] posterTexts = new String[100];
    
    public int numberOfPoster =0;
    public int numberOfContacts =0;
    public int Posterid =0;
	private Poster[] posterItems = new Poster[100];
	private String theme_color;
	private Contact[] contactList;
	private Conference Conf;
	private String filePath;
	private int PictureWidth = 100;
	private int PictureHeight = 140;

    DrawerLayout mDrawerLayout;
    NavigationView navigationView;
	
	
	
	public void OpenPoster(Conference selectedConf) 
	{
	   //SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
	   //final boolean isSavesortsearchEnabled = sp.getBoolean("isSavesortsearchEnabled", false);
       Intent in = new Intent(this, PosterViewActivity.class);
       Bundle extras = new Bundle();
       extras.putSerializable("conf", selectedConf);
       in.putExtras(extras);       
       startActivity(in);  			
	}
	
	public void OpenContacts(Conference selectedConf) 
	{
	   //SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
	   //final boolean isSavesortsearchEnabled = sp.getBoolean("isSavesortsearchEnabled", false);
       Intent in = new Intent(this, ContactViewActivity.class);
       Bundle extras = new Bundle();
       extras.putSerializable("conf", selectedConf);
       in.putExtras(extras);       
       startActivity(in);    		
	}
	
	
	/** Called when the activity is first created. */
	
    @Override
    public void onCreate(Bundle savedInstanceState) {  
    	SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        theme_color = sp.getString("pref_color_theme_entries","Black");

        super.onCreate(savedInstanceState);  
        //ScrollView sv = new ScrollView(this);
       	setContentView(R.layout.conf_view_activity);

        Intent intentFromMenu = super.getIntent();//
        Bundle extras = intentFromMenu.getExtras();
        Conf = (Conference)extras.getSerializable("conf");
        int colorPrimary = Color.parseColor(Conf.getColorPrimary());
        int colorPrimaryDark = Color.parseColor(Conf.getColorPrimaryDark());
        int colorSecondary = Color.parseColor(Conf.getColorSecondary());
        int colorSecondaryLight = Color.parseColor(Conf.getColorSecondaryLight());
        int colorAccent = Color.parseColor(Conf.getColorAccent());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle(Conf.getConfname());
        toolbar.setTitleTextColor(colorSecondary);
        toolbar.setSubtitleTextColor(colorSecondary);

        toolbar.setBackgroundColor(colorPrimary);
        Drawable navigationIcon = toolbar.getNavigationIcon();
        colorizeToolbar(toolbar,colorPrimaryDark,this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setStatusBarColor(colorPrimaryDark);
            //window.setNavigationBarColor(Color.parseColor(Conf.getColorPrimaryDark()));
            //menutem.setIcon(android.R.drawable.ic_action_something_tinted);
            if(navigationIcon != null) {
                navigationIcon.setTint(colorPrimaryDark);
            }
        } else {
            if(navigationIcon != null){
                navigationIcon.setColorFilter(colorPrimaryDark, PorterDuff.Mode.MULTIPLY);
            }
        }


        mDrawerLayout = (DrawerLayout) findViewById(R.id.DrawerLayout);
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(
                this,  mDrawerLayout, toolbar,
                R.string.drawer_open, R.string.drawer_close
        );
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        getSupportActionBar().setHomeButtonEnabled(true);
        mDrawerToggle.syncState();



        //Initializing NavigationView
        navigationView = (NavigationView) findViewById(R.id.navigation_view);

        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {


                //Checking if the item is in checked state or not, if not make it in checked state
                if (menuItem.isChecked()) menuItem.setChecked(false);
                else menuItem.setChecked(true);

                //Closing drawer on item click
                mDrawerLayout.closeDrawers();

                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()) {

                    case R.id.cf_nav_sciman:
                        finish();
                        return true;
                    case R.id.cf_nav_news:
                        Toast.makeText(getApplicationContext(), "Send Selected", Toast.LENGTH_SHORT).show();
                        return true;

                    //Replacing the main content with ContentFragment Which is our Inbox View;
                    case R.id.cf_nav_program:
                        Toast.makeText(getApplicationContext(), "Profile Selected", Toast.LENGTH_SHORT).show();
                        //ContentFragment fragment = new ContentFragment();
                        //android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        //fragmentTransaction.replace(R.id.frame,fragment);
                        //fragmentTransaction.commit();
                        return true;

                    // For rest of the options we just show a toast on click

                    case R.id.cf_nav_contrib:
                        Toast.makeText(getApplicationContext(), "All Mail Selected", Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.cf_nav_map:
                        Toast.makeText(getApplicationContext(), "All Mail Selected", Toast.LENGTH_SHORT).show();
                        //startActivity(i);
                        return true;
                    case R.id.cf_nav_schedule:
                        Toast.makeText(getApplicationContext(), "All Mail Selected", Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.cf_nav_stored_contrib:
                        Toast.makeText(getApplicationContext(), "All Mail Selected", Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.cf_nav_stored_contacts:
                        Toast.makeText(getApplicationContext(), "All Mail Selected", Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.cf_nav_impressum:
                        Toast.makeText(getApplicationContext(), "All Mail Selected", Toast.LENGTH_SHORT).show();
                        //OpenAbout();
                        return true;
                    default:
                        Toast.makeText(getApplicationContext(), "Somethings Wrong", Toast.LENGTH_SHORT).show();
                        return true;

                }
            }
        });

        RelativeLayout header_rl_background = (RelativeLayout) navigationView.findViewById(R.id.nav_header_background);
        if (Conf.getHeaderLocal()){

            File imgFile = new  File(Conf.getHeaderBitmapFile());

            if(imgFile.exists()){

                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

                ImageView myImage = (ImageView) navigationView.findViewById(R.id.nav_header_imageview);

                myImage.setImageBitmap(myBitmap);

            } else {
                header_rl_background.setBackgroundColor(colorPrimary);
            }
        } else {
            header_rl_background.setBackgroundColor(colorPrimary);
        }
        TextView header_tv_name = (TextView) navigationView.findViewById(R.id.nav_header_name);
        header_tv_name.setText(Conf.getConfname());
        header_tv_name.setTextColor(colorSecondary);
        TextView header_tv_year = (TextView) navigationView.findViewById(R.id.nav_header_year);
        header_tv_year.setText(Integer.toString(Conf.getYear()));
        header_tv_year.setTextColor(colorSecondary);
        TextView header_tv_location = (TextView) navigationView.findViewById(R.id.nav_header_location);
        header_tv_location.setText(Conf.getLocation());
        header_tv_location.setTextColor(colorSecondary);

                        //Name
                        //TextView tv_header = (TextView)findViewById(R.id.tv_header);
                        //tv_header.setText(Conf.getConfname());
                        //Comments
        TextView tv_comment = (TextView) findViewById(R.id.tv_comment);
        tv_comment.setText(Conf.getComment());
        //Date
        TextView tv_date = (TextView)findViewById(R.id.tv_date);
        tv_date.setText(Conf.getDate());
        //best: as text fe. 2nd July 2012, and language specific, is there a function for that?
        
        //Location
        TextView tv_location = (TextView)findViewById(R.id.tv_location);
        tv_location.setText(Conf.getLocation());
        //Topics
        LinearLayout ll_topics = (LinearLayout)findViewById(R.id.ll_topics);
        Topics Conftopics = Conf.getTopics();
        for (int i=0; i<Conftopics.getNumberOfTopics();i++){
  			View topic_v;
			LayoutInflater li = getLayoutInflater();
			topic_v = li.inflate(R.layout.topic_confdv, null);

			TextView tv_topic = (TextView)topic_v.findViewById(R.id.tv_topic);
			tv_topic.setText(Conftopics.getTopic(i));

			ll_topics.addView(topic_v);
        }	
        //savepath
        TextView tv_savepath = (TextView)findViewById(R.id.tv_savepath);
        tv_savepath.setText(Conf.getConfFolder());
        
        //Poster, number of items and link
        readPosters(Conf.getConfFolder());        
        TextView tv_poster_count = (TextView)findViewById(R.id.tv_poster_count);
        for (int i=0;i<posterItems.length;i++) {
        	if (posterItems[i] instanceof Poster){
        		numberOfPoster++;
        	}
        }
        tv_poster_count.setText(numberOfPoster + getString(R.string.cfv_items));
        ImageView iv_poster_pic = (ImageView)findViewById(R.id.poster_pic);    	  	
    	
    	iv_poster_pic.setOnClickListener(new View.OnClickListener() {            
            //@Override
            public void onClick(View v) {
            	//tr.setBackgroundColor(Color.GREEN);
               //function to be called when the row is clicked.  
               OpenPoster(Conf);
            }
         });
        
        	//maybe preview of first 3 poster
        
        //Contacts, number of items and link
    	TextView tv_contacts_count = (TextView)findViewById(R.id.tv_contacts_count);
        tv_contacts_count.setText(getString(R.string.cfv_items));
        ImageView iv_contacts_pic = (ImageView)findViewById(R.id.contacts_pic);
    	iv_contacts_pic.setOnClickListener(new View.OnClickListener() {            
            //@Override
            public void onClick(View v) {
               int rowid;
               OpenContacts(Conf);
            }
         });
    }
   
        
        /*
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        switch(metrics.densityDpi){
        	//set column width
             case DisplayMetrics.DENSITY_LOW:
            	 gridView.setColumnWidth(58);
            	 PictureWidth = 58;
            	 PictureHeight = 80;
                        break;
             case DisplayMetrics.DENSITY_MEDIUM:
            	 gridView.setColumnWidth(79);
            	 PictureWidth = 79;
            	 PictureHeight = 110;
                         break;
             case DisplayMetrics.DENSITY_HIGH://changed other resolutions have to be adapted too
            	 gridView.setColumnWidth(140);
            	 PictureWidth = 140;
            	 PictureHeight = 210;
                         break;
             case 320:
            	 gridView.setColumnWidth(129);
            	 PictureWidth = 129;
            	 PictureHeight = 180;
        	 			break;
        }*/

        //addButton(0);
        /*filePath = Utils.getMainConfFolder(this)+Conf.getConfFolder();
        if (readPosters(filePath)){
        	for (int i=0;i<99;i++){
        		if (posterItems[i] instanceof Poster){
        			addItem(posterItems[i],i);
        		}
        		
        	}
        }
        
        
        
        registerForContextMenu(gridView);
        // Instance of ImageAdapter Class
        gridView.setAdapter(mAdapter);   
        /**
         * On Click event for Single Gridview Item
         * */
        /*gridView.setOnItemClickListener(new OnItemClickListener() {
            //@Override
            public void onItemClick(AdapterView<?> parent, View v,
                    int position, long id) {
        		Intent in = new Intent(getApplicationContext(), PosterDetailedViewActivity.class);
        		Bundle extras = new Bundle();
       	     	extras.putSerializable("poster", posterItems[position]);
       	     	extras.putSerializable("conf", Conf);	
       	     	in.putExtras(extras);
        		startActivity(in);
        		//start poster_detailedview_activity
        		/*Intent in = new Intent(getApplicationContext(), PosterDetailedViewActivity.class);
                in.putExtra("poster", posterItems[position]);
                startActivity(in);*/
        /*    }
        });
    } 
    
    /*public void addButton(int id){
    	if (theme_color.equalsIgnoreCase("White")){
    		posterImagesIds[0] = R.drawable.posteradd_white;
        } else {
        	posterImagesIds[0] = R.drawable.posteradd;
        }
    	//android.R.drawable.ic_input_add;
    	posterTexts[0] = "add";
    	numberOfItems++;
    }*/
    
    public boolean readPosters(String fileFolder){
    	File posterFile = new File(fileFolder + "poster.cfg");
    	if (posterFile.exists()){
    		try{    	
    			File postercfg = new File(fileFolder,"poster.cfg");
        		FileInputStream fis = new FileInputStream(postercfg);
    	    	ObjectInputStream in = null;
    	    	//fis = act.openFileInput(conferencePath + "/conference.cfg");
    	    	in = new ObjectInputStream(fis);	    			    	
    	    	posterItems = (Poster[]) in.readObject();
    	    	System.out.println("Object Read");
    	    	in.close();
    	    	return true;
        	} catch (IOException ex) {    		
        		ex.printStackTrace();
        		return false;
        	} catch (ClassNotFoundException e) {
        		e.printStackTrace();
        		return false;
    		}    		
    	} else{
    		return false;
    	}   	
    }
    public View getToolbarNavigationIcon(Toolbar toolbar){
        //check if contentDescription previously was set
        boolean hadContentDescription = TextUtils.isEmpty(toolbar.getNavigationContentDescription());
        String descrip = (toolbar.getNavigationContentDescription()).toString();
        String contentDescription = !hadContentDescription ? descrip : "navigationIcon";
        toolbar.setNavigationContentDescription(contentDescription);
        ArrayList<View> potentialViews = new ArrayList<View>();
        //find the view based on it's content description, set programatically or with android:contentDescription
        toolbar.findViewsWithText(potentialViews,contentDescription, View.FIND_VIEWS_WITH_CONTENT_DESCRIPTION);
        //Nav icon is always instantiated at this point because calling setNavigationContentDescription ensures its existence
        View navIcon = null;
        if(potentialViews.size() > 0){
            navIcon = potentialViews.get(0); //navigation icon is ImageButton
        }
        //Clear content description if not previously present
        if(hadContentDescription)
            toolbar.setNavigationContentDescription(null);
        return navIcon;
    }

    public static void colorizeToolbar(Toolbar toolbarView, int toolbarIconsColor, Activity activity) {
        final PorterDuffColorFilter colorFilter
                = new PorterDuffColorFilter(toolbarIconsColor, PorterDuff.Mode.MULTIPLY);

        for(int i = 0; i < toolbarView.getChildCount(); i++) {
            final View v = toolbarView.getChildAt(i);

            //Step 1 : Changing the color of back button (or open drawer button).
            if(v instanceof ImageButton) {
                //Action Bar back button
                ((ImageButton)v).getDrawable().setColorFilter(colorFilter);
            }

            if(v instanceof ActionMenuView) {
                for(int j = 0; j < ((ActionMenuView)v).getChildCount(); j++) {

                    //Step 2: Changing the color of any ActionMenuViews - icons that
                    //are not back button, nor text, nor overflow menu icon.
                    final View innerView = ((ActionMenuView)v).getChildAt(j);

                    if(innerView instanceof ActionMenuItemView) {
                        int drawablesCount = ((ActionMenuItemView)innerView).getCompoundDrawables().length;
                        for(int k = 0; k < drawablesCount; k++) {
                            if(((ActionMenuItemView)innerView).getCompoundDrawables()[k] != null) {
                                final int finalK = k;

                                //Important to set the color filter in seperate thread,
                                //by adding it to the message queue
                                //Won't work otherwise.
                                innerView.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        ((ActionMenuItemView) innerView).getCompoundDrawables()[finalK].setColorFilter(colorFilter);
                                    }
                                });
                            }
                        }
                    }
                }
            }
            /*
            //Step 3: Changing the color of title and subtitle.
            toolbarView.setTitleTextColor(toolbarIconsColor);
            toolbarView.setSubtitleTextColor(toolbarIconsColor);

            //Step 4: Changing the color of the Overflow Menu icon.
            setOverflowButtonColor(activity, colorFilter);*/
        }
    }

}