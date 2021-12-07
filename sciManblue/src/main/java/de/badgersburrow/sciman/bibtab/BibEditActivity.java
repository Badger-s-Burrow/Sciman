package de.badgersburrow.sciman.bibtab;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import de.badgersburrow.sciman.objects.Bib;
import de.badgersburrow.sciman.R;
import de.badgersburrow.sciman.utilities.VariousMethods;

import java.io.File;
import java.util.List;
import java.util.Map;

//import de.badgersburrow.sciman.BibNewActivity.ImageAdapter;
//import test.androidbib.BibNewActivity.LoadViewTask;

public class BibEditActivity extends AppCompatActivity {
	
	
	private static final int SELECT_PICTURE = 1;
	
	private boolean chose_custom = false;
	
	private boolean avaible = true;

	private String customPicturePath = "None";
	
	private Bitmap customPic;
	
	private Bitmap resizedBitmap;
	private int newHeight = 200;
	
	private String selectedImagePath;
	
	private String filemanagerstring;
	private String[] sources = null;
	private String[] sourcesCode = new String[] {"local", "DropBox","manualApp"};
	private String selectedSource = "None";
	private String manualApp = "None";
	private String manualAppName = "None";
	private boolean hideSysApps=true;
	
	private String[] installedAppArray;
	private String[] installedAppNameArray;
	private Bib Bibitem;
	
	String KEY_TEXTPSS = "TEXTPSS";
	 static final int CUSTOM_DIALOG_ID = 0;
	  
	 ListView dialog_ListView;
	 
	 int[] myImageIds;
	 String[] imageKeys;
	Map<String, Integer> topicsDict;
		
    /** Called when the activity is first created. */
	TextView mySelection;
	Gallery myGallery;
	String iconKey="None";
	
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
				progressDialog = ProgressDialog.show(BibEditActivity.this,getString(R.string.nb_loadingtit),
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
						//Initialize an integer (that will act as a counter) to zero
						/*int counter = 0;
						//While the counter is smaller than four
						while(counter <= 4)
						{
							//Wait 850 milliseconds
							this.wait(850);
							//Increment the counter
							counter++;
							//Set the current progress.
							//This value is going to be passed to the onProgressUpdate() method.
							publishProgress(counter*25);
						}*/
					}
				/*}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}*/
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
				//setContentView(R.layout.bib_new_activity);
				showDialog(CUSTOM_DIALOG_ID);
			}
	    }

	
	
    @Override
    public void onCreate(Bundle savedInstanceState) { 
    	
    	
    	SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);


        // Obtain the sharedPreference, default to true if not available
        hideSysApps = sp.getBoolean("isHideSystemappsEnabled", true);
        sources = getResources().getStringArray(R.array.sources);
        super.onCreate(savedInstanceState); 
       	setContentView(R.layout.bib_edit_activity);


		Toolbar toolbar = (Toolbar) findViewById(de.badgersburrow.sciman.R.id.toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowHomeEnabled(true);

        Intent intentFromMenu = super.getIntent();//
        
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        switch(metrics.densityDpi){
             case DisplayMetrics.DENSITY_LOW:
            	 		newHeight = 120;
                        break;
             case DisplayMetrics.DENSITY_MEDIUM:
            	 		newHeight = 160;
                         break;
             case DisplayMetrics.DENSITY_HIGH:
            	 		newHeight = 200;
                         break;
             case 320:
        	 			newHeight = 240;
        	 			break;
        }
        
        
        //Bundle extras = intentFromMenu.getExtras();
        final Bib selectedBib = (Bib)intentFromMenu.getSerializableExtra("Bib");
        String bibName = selectedBib.getBibname();
        String comment = selectedBib.getComment();
        boolean extern = selectedBib.getExtern();
        String fileName = selectedBib.getFile();
        String pdfDirectory = selectedBib.getPdfdirectory();
        iconKey = selectedBib.getIconKey();
        selectedSource = selectedBib.getSource();
        manualApp = selectedBib.getManualApp();
        manualAppName = getAppName(manualApp);
        try{
    		customPic.recycle();
    	}catch(RuntimeException e) {
    		System.out.println("customPic not recyclable");
    	}
        
        final EditText ed_name = (EditText)findViewById(R.id.ed_name);
        Spinner sp_source = (Spinner)findViewById(R.id.sp_source);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, sources);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_source.setAdapter(adapter);
        
        sp_source.setOnItemSelectedListener(new OnItemSelectedListener() {
            //@Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
            	selectedSource=sourcesCode[position];// your code here
                
                //maybe a chosen on creation boolean has to be introduced to catch the startup selection
                
                changeTextforSource(selectedSource);
            }

            //@Override
            public void onNothingSelected(AdapterView<?> parentView) {
            	selectedSource="None";
                // does not change anything, or do not allow click ok
            }

        });
        final EditText ed_comment = (EditText)findViewById(R.id.ed_comment);
        final CheckBox cb_extern = (CheckBox)findViewById(R.id.cb_extern);
        final EditText ed_file = (EditText)findViewById(R.id.ed_file);
        
        final EditText ed_pdfdirectory = (EditText)findViewById(R.id.ed_pdfdirectory);
        
        TextView tv_manualapp = (TextView)findViewById(R.id.tv_manualapp);
        tv_manualapp.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				new LoadViewTask().execute();
				//chooseApp();
			}
		}); 
        
        if (Bibitem instanceof Bib){
        	bibName = Bibitem.getBibname();
        	comment = Bibitem.getComment();
        	extern = Bibitem.getExtern();
            fileName = Bibitem.getFile();
            pdfDirectory = Bibitem.getPdfdirectory();
            
            iconKey = Bibitem.getIconKey();
            selectedSource = Bibitem.getSource();
            manualApp = Bibitem.getManualApp();
            manualAppName = getAppName(manualApp);
            changeTextforSource(selectedSource);
            ed_name.setText(bibName);
            ed_comment.setText(comment);
            cb_extern.setChecked(extern);
            ed_file.setText(fileName);            
            ed_pdfdirectory.setText(pdfDirectory);
            int indexOfSource = java.util.Arrays.asList(sources).indexOf(selectedSource);
            sp_source.setSelection(indexOfSource, true);
            try{
        		customPic.recycle();
        	}catch(RuntimeException e) {
        		System.out.println("customPic not recyclable");
        	}
        }
        changeTextforSource(selectedSource);
        
        /*
        //Bitmap testbit = null;
        if (chose_custom){
        	File file = new File(selectedBib.getCustompath());

       	 	if (file.exists()) {
       	 		BitmapFactory.Options options = new BitmapFactory.Options();

                try{
    	        	if (file.length()>1500000){
    	        		options.inSampleSize = 8;
    	        	}else if (file.length()>800000){
    	        		options.inSampleSize = 4;
	    	        }else if (file.length()>400000){
    	        		options.inSampleSize = 2;
    	        	}else {
    	        		options.inSampleSize = 1;
    	        	}

    	        	customPic = BitmapFactory.decodeFile(customPicturePath,options);
            	} catch(OutOfMemoryError e){
            		System.out.println("Out of memory");
            		options.inSampleSize = 8;
            		customPic = BitmapFactory.decodeFile(customPicturePath,options);
            	}

                int width = customPic.getWidth();
                int height = customPic.getHeight();
                int newWidth = (int) (newHeight/(height*1.0f)*width);
                // calculate the scale
                float scaleWidth = ((float) newWidth) / width;
                float scaleHeight = ((float) newHeight) / height;

                // create a matrix for the manipulation
                Matrix matrix = new Matrix();
                // resize the bit map
                matrix.postScale(scaleWidth, scaleHeight);
                // rotate the Bitmap
                //matrix.postRotate(45);

                // recreate the new Bitmap
                resizedBitmap = Bitmap.createBitmap(customPic, 0, 0,
                                  width, height, matrix, true);
                  //Toast.makeText(this, getString(R.string.eb_deselect_pic), Toast.LENGTH_LONG).show();
                //custom_btn.setImageBitmap(resizedBitmap);
                //chose_custom = true;

       	 	} else{
       	 		Toast.makeText(this, getString(R.string.eb_backp_warn1) + selectedBib.getBibname() + getString(R.string.eb_backp_warn2), Toast.LENGTH_LONG).show();
       	 		avaible=false;
       	 	}

		}*/
        //Intent resultIntent = new Intent();

        //Intent  in = super.getIntent();
        //Bundle b = in.getExtras();

        //item ide = b.getString("itemID");
        //Intent in = super.getIntent();
        //item selectedItem = (item)in.getSerializableExtra("item");
        
        
        ed_name.setText(bibName);
        ed_comment.setText(comment);
        cb_extern.setChecked(extern);
        ed_file.setText(fileName);
        ed_pdfdirectory.setText(pdfDirectory);
        int indexOfSource = java.util.Arrays.asList(sourcesCode).indexOf(selectedSource);
        sp_source.setSelection(indexOfSource, true);
      

      //select correct startposition for spinner
        
        
        final TextView testtext = (TextView)findViewById(R.id.testtext);
        Button bt_ok = (Button)findViewById(R.id.bt_ok);
        bt_ok.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				String name =getString(R.string.none);
				String file =getString(R.string.none);
				String comment =getString(R.string.none);
				String pdfdirectory =getString(R.string.none);
				if (ed_name.getText().toString()!=null){
					name=ed_name.getText().toString();
				}
				if (ed_comment.getText().toString()!=null){
					comment=ed_comment.getText().toString();
				}
				boolean extern = cb_extern.isChecked();
				if (ed_file.getText().toString()!=null){
					file=ed_file.getText().toString();
				}
				
				if (ed_pdfdirectory.getText().toString()!=null){
					pdfdirectory=ed_pdfdirectory.getText().toString();
				}
				Bundle bibAttributes = new Bundle();
				Bitmap iconBitmap = ((BitmapDrawable) getResources().getDrawable(topicsDict.get(iconKey))).getBitmap();
				String iconBytes = VariousMethods.encodeTobase64(iconBitmap);
				bibAttributes.putSerializable("bib", new Bib(name,selectedSource,manualApp,manualAppName,comment,extern,file,pdfdirectory,iconKey,"",0));

				Intent mIntent = new Intent();
				//mIntent.putExtra("background", backgroundselection);
	            mIntent.putExtras(bibAttributes);

				setResult(3,mIntent);//Activity.RESULT_OK,  mIntent);
				
				finish();
			}
		});
        
        topicsDict = VariousMethods.getTopicDict(this);
		imageKeys = getResources().getStringArray(R.array.img_id_arr_topics_keys);
		
		int len = imageKeys.length;
		 
		myImageIds = new int[len];
		 
		int backgroundPos = 0;
		for (int i = 0; i < len; i++){
			myImageIds[i] = topicsDict.get(imageKeys[i]);
			if (imageKeys[i].equals(iconKey)){
				backgroundPos = i;
			}
		}
		
		// Bind the gallery defined in the main.xml
		// Apply a new (customized) ImageAdapter to it.

		myGallery = (Gallery) findViewById(R.id.ed_backgroundGallery);

		myGallery.setAdapter(new ImageAdapter(this));
		myGallery.setSpacing(5);
		myGallery.setSelection(backgroundPos);
		myGallery.setOnItemSelectedListener(new OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> parent, View v,
					int position, long id) {
				//mySelection.setText(" selected option: " + position );
				iconKey=imageKeys[position];
			}

			public void onNothingSelected(AdapterView<?> parent) {
				//mySelection.setText("Nothing selected");
				iconKey=imageKeys[0];
			}
		});

    } 
    
    public void changeTextforSource(String source){
    	if (source.equalsIgnoreCase("local")){
    		TextView tv_manualapp = (TextView)findViewById(R.id.tv_manualapp);
    		TextView tv_name = (TextView)findViewById(R.id.tv_name);
        	TextView tv_file = (TextView)findViewById(R.id.tv_file);
        	TextView ed_file = (TextView)findViewById(R.id.ed_file);
        	ImageView iv_arrow = (ImageView)findViewById(R.id.iv_arrow);
        	TextView tv_pdfdirectory = (TextView)findViewById(R.id.tv_pdfdirectory);
        	tv_manualapp.setText("");
        	//tv_manualapp.setHeight(0);
        	iv_arrow.setImageResource(R.drawable.no);
        	//tv_manualapp.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, 0));
        	tv_manualapp.setClickable(false);
        	tv_name.setText(getString(R.string.nb_name));
        	tv_file.setText(getString(R.string.nb_file));
        	ed_file.setHint(getString(R.string.nb_file_hint));
        	tv_pdfdirectory.setText(getString(R.string.nb_pdfdirectory));
        	
        } else if (source.equalsIgnoreCase("dropbox")){
        	TextView tv_manualapp = (TextView)findViewById(R.id.tv_manualapp);
        	ImageView iv_arrow = (ImageView)findViewById(R.id.iv_arrow);
        	TextView tv_name = (TextView)findViewById(R.id.tv_name);
        	TextView tv_file = (TextView)findViewById(R.id.tv_file);
        	TextView ed_file = (TextView)findViewById(R.id.ed_file);        	
        	TextView tv_pdfdirectory = (TextView)findViewById(R.id.tv_pdfdirectory);
        	tv_manualapp.setText("");
        	//tv_manualapp.setHeight(0);
        	iv_arrow.setImageResource(R.drawable.no);
        	//iv_arrow.setMaxHeight(0);
        	//tv_manualapp.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, 0));
        	tv_manualapp.setClickable(false);
        	tv_name.setText(getString(R.string.nb_name_dropb));
        	tv_file.setText(getString(R.string.nb_file_dropb));
        	ed_file.setHint(getString(R.string.nb_file_hint_dropb));
        	tv_pdfdirectory.setText(getString(R.string.nb_pdfdirectory_dropb));
        	
        	
        } else if (source.equalsIgnoreCase("manualApp")){
        	TextView tv_manualapp = (TextView)findViewById(R.id.tv_manualapp);
        	TextView tv_name = (TextView)findViewById(R.id.tv_name);
        	TextView tv_file = (TextView)findViewById(R.id.tv_file);
        	TextView ed_file = (TextView)findViewById(R.id.ed_file);
        	ImageView iv_arrow = (ImageView)findViewById(R.id.iv_arrow);
        	TextView tv_pdfdirectory = (TextView)findViewById(R.id.tv_pdfdirectory);
        	if (manualApp.equalsIgnoreCase("None")){
        		tv_manualapp.setText(getString(R.string.nb_sourcemanualApp) + getString(R.string.nb_sourcemanualApp_None));
        	} else {
        		tv_manualapp.setText(getString(R.string.nb_sourcemanualApp) + manualAppName);
        	}
        	iv_arrow.setImageResource(R.drawable.arrow_to_app);
        	//tv_manualapp.setHeight(40);
        	//tv_manualapp.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        	tv_manualapp.setClickable(true);
        	tv_name.setText(getString(R.string.nb_name));
        	tv_file.setText(getString(R.string.nb_file));
        	ed_file.setHint(getString(R.string.nb_file_hint));
        	tv_pdfdirectory.setText(getString(R.string.nb_pdfdirectory));
        	
        	
        	
        }
    }
    
    public class ImageAdapter extends BaseAdapter {
		/** The parent context */
		private Context myContext;
		// Put some images to project-folder: /res/drawable/
		// format: jpg, gif, png, bmp, ...
		private int[] privImageIds = myImageIds;
		
		/** Simple Constructor saving the 'parent' context. */
		public ImageAdapter(Context c) {
			this.myContext = c;
		}

		// inherited abstract methods - must be implemented
		// Returns count of images, and individual IDs
		public int getCount() {
			return this.privImageIds.length;
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}
		// Returns a new ImageView to be displayed,
		public View getView(int position, View convertView, 
				ViewGroup parent) {

			if (position==getCount()-1){
				ImageView iv = new ImageView(this.myContext);
				iv.setId(9999);
				if (resizedBitmap!=null){
					iv.setImageBitmap(resizedBitmap);
				} else {
					iv.setImageResource(R.drawable.custom);
				}
				
				//iv.setBackgroundDrawable(getResources().getDrawable(android.R.drawable.list_selector_background));
				iv.setLayoutParams(new Gallery.LayoutParams(newHeight, newHeight));
				iv.setScaleType(ImageView.ScaleType.CENTER_CROP);	
				
		        //tr.drawable.list_selector_background);
		       

				iv.setOnClickListener(new View.OnClickListener() {
		           
		               //@Override
		               public void onClick(View v) {
		            	   Intent intent = new Intent();
		                   intent.setType("image/*");
		                   intent.setAction(Intent.ACTION_GET_CONTENT);
		                   startActivityForResult(Intent.createChooser(intent,
		                		   getString(R.string.eb_select_pic)), SELECT_PICTURE);

		                  
		               }
		            });
				return iv;
			} else{
				// Get a View to display image data 					
				ImageView iv = new ImageView(this.myContext);
				iv.setImageResource(this.privImageIds[position]);
				// Image should be scaled somehow
				//iv.setScaleType(ImageView.ScaleType.CENTER);
				//iv.setScaleType(ImageView.ScaleType.CENTER_CROP);			
				//iv.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
				//iv.setScaleType(ImageView.ScaleType.FIT_CENTER);
				//iv.setScaleType(ImageView.ScaleType.FIT_XY);
				iv.setScaleType(ImageView.ScaleType.FIT_START);
				iv.setLayoutParams(new Gallery.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
				
				return iv;
			}
		}
	}// ImageAdapter
    //UPDATED
      public void onActivityResult(int requestCode, int resultCode, Intent data) {
          if (resultCode == RESULT_OK) {
              if (requestCode == SELECT_PICTURE) {
                  Uri selectedImageUri = data.getData();

                  //OI FILE Manager
                  filemanagerstring = selectedImageUri.getPath();

                  //MEDIA GALLERY
                  selectedImagePath = getPath(selectedImageUri);

                  //DEBUG PURPOSE - you can delete this if you want
                  if(selectedImagePath!=null)
                      System.out.println(selectedImagePath);
                  else System.out.println("selectedImagePath is null");
                  if(filemanagerstring!=null)
                      System.out.println(filemanagerstring);
                  else System.out.println("filemanagerstring is null");

                  //NOW WE HAVE OUR WANTED STRING
                  ImageView custom_btn = (ImageView) findViewById(9999);
                  //custom_btn.setLayoutParams(new LayoutParams(pictureWidth,    pictureHeight          ));
                  
                  try{
                	  customPic.recycle();
                  }catch(RuntimeException e){
                	  
                  }
                  
                  BitmapFactory.Options options = new BitmapFactory.Options();
                  options.inSampleSize = 8;
                  if(selectedImagePath!=null){
                    
    					
    					customPicturePath=selectedImagePath;
                  }else{
                    	
  					
    					customPicturePath=filemanagerstring;
    					
                       
                  }
                  File file = new File(customPicturePath);
                  try{
      	        	if (file.length()>1500000){
      	        		options.inSampleSize = 8;
      	        	}else if (file.length()>800000){
      	        		options.inSampleSize = 4;
  	    	        }else if (file.length()>400000){
      	        		options.inSampleSize = 2;
      	        	}else {
      	        		options.inSampleSize = 1;
      	        	}
      	        	
      	        		customPic = BitmapFactory.decodeFile(customPicturePath,options);
	              	} catch(OutOfMemoryError e){
	              		System.out.println("Out of memory");
	              		options.inSampleSize = 8;
	              		customPic = BitmapFactory.decodeFile(customPicturePath,options);
	              	}
                  
                  int width = customPic.getWidth();
                  int height = customPic.getHeight();
                  int newWidth = (int) (newHeight/(height*1.0f)*width);
                  // calculate the scale
                  float scaleWidth = ((float) newWidth) / width;
                  float scaleHeight = ((float) newHeight) / height;
                 
                  // create a matrix for the manipulation
                  Matrix matrix = new Matrix();
                  // resize the bit map
                  matrix.postScale(scaleWidth, scaleHeight);
                  // rotate the Bitmap
                  //matrix.postRotate(45);
                  try{
                	  resizedBitmap.recycle();
                  }catch(RuntimeException e){
                	  
                  }
                  // recreate the new Bitmap
                  resizedBitmap = Bitmap.createBitmap(customPic, 0, 0,
                                    width, height, matrix, true);
                    //Toast.makeText(this, getString(R.string.eb_deselect_pic), Toast.LENGTH_LONG).show();
                  custom_btn.setImageBitmap(resizedBitmap);
                  chose_custom = true;
                  
              }
          }
      }

      //UPDATED!
      public String getPath(Uri uri) {
          String[] projection = { MediaStore.Images.Media.DATA };
          Cursor cursor = managedQuery(uri, projection, null, null, null);
          if(cursor!=null)
          {
              //HERE YOU WILL GET A NULLPOINTER IF CURSOR IS NULL
              //THIS CAN BE, IF YOU USED OI FILE MANAGER FOR PICKING THE MEDIA
              int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
              cursor.moveToFirst();
              return cursor.getString(column_index);
          }
          else return null;
      }
      @Override
      public void onSaveInstanceState(Bundle savedInstanceState)
      {
        // EditTexts text
  		final EditText ed_name = (EditText)findViewById(R.id.ed_name);
  		final EditText ed_comment = (EditText)findViewById(R.id.ed_comment);
  		final CheckBox cb_extern = (CheckBox)findViewById(R.id.cb_extern);
  		final EditText ed_file = (EditText)findViewById(R.id.ed_file);
  	    final EditText ed_pdfdirectory = (EditText)findViewById(R.id.ed_pdfdirectory);
  	    String name =getString(R.string.none);
  	    String comment =getString(R.string.none);
  	    boolean extern = cb_extern.isChecked();
  		String file =getString(R.string.none);
  		String pdfdirectory =getString(R.string.none);
  		if (ed_name.getText().toString()!=null){
  			name=ed_name.getText().toString();
  		}
  		if (ed_comment.getText().toString()!=null){
  			comment=ed_comment.getText().toString();
  		}
  		
  		if (ed_file.getText().toString()!=null){
  			file=ed_file.getText().toString();
  		}
  		
  		if (ed_pdfdirectory.getText().toString()!=null){
  			pdfdirectory=ed_pdfdirectory.getText().toString();
  		}
  		
  		Bibitem=new Bib(name,selectedSource,manualApp,manualAppName,comment,extern,file,pdfdirectory,iconKey,"",0);
        savedInstanceState.putSerializable("tempBib", Bibitem);
        
        super.onSaveInstanceState(savedInstanceState);
      }

      @Override
      public void onRestoreInstanceState(Bundle savedInstanceState)
      {
        super.onRestoreInstanceState(savedInstanceState);
        // EditTexts text
        Bibitem=(Bib)savedInstanceState.getSerializable("tempBib");
        
      }


      @Override
      protected Dialog onCreateDialog(int id) {
      
       Dialog dialog = null;
        
       switch(id) {
          case CUSTOM_DIALOG_ID:
           dialog = new Dialog(BibEditActivity.this);
           dialog.setContentView(R.layout.installedapps_dialog);
           dialog.setTitle("Choose app for syncing");
                   
           dialog.setCancelable(true);
           dialog.setCanceledOnTouchOutside(true);
            
           dialog.setOnCancelListener(new OnCancelListener(){
      
         //@Override
         public void onCancel(DialogInterface dialog) {
          // TODO Auto-generated method stub
          //Toast.makeText(BibEditActivity.this,"OnCancelListener",Toast.LENGTH_LONG).show();
         }});
            
           dialog.setOnDismissListener(new OnDismissListener(){
      
         //@Override
         public void onDismiss(DialogInterface dialog) {
          // TODO Auto-generated method stub
          //Toast.makeText(BibEditActivity.this,"OnDismissListener",Toast.LENGTH_LONG).show();
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
          //Toast.makeText(BibEditActivity.this,parent.getItemAtPosition(position).toString() + " clicked",Toast.LENGTH_LONG).show();
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
      
      /**
       * Return whether the given PackgeInfo represents a system package or not.
       * User-installed packages (Market or otherwise) should not be denoted as
       * system packages.
       * 
       * @param pkgInfo
       * @return
       */
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
		//showDialog(CUSTOM_DIALOG_ID);
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