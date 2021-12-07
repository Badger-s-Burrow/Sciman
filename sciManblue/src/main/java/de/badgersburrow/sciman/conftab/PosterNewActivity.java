package de.badgersburrow.sciman.conftab;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.text.format.Time;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import de.badgersburrow.sciman.objects.Conference;
import de.badgersburrow.sciman.objects.Poster;
import de.badgersburrow.sciman.R;
import de.badgersburrow.sciman.utilities.VariousMethods;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


public class PosterNewActivity extends Activity {
		
	private Poster Posteritem;
	private Conference Conf;
	ListView dialog_ListView;
	private static final int SELECT_PICTURE = 1;
	private static final int CAMERA_PIC_REQUEST=6;
	private boolean setPicture = false;
	private Bitmap thumbnail;
	private String thumbnailPath = null;
	private File thumbnailFile;
	private int rotation = 0;
	private Bitmap resizedBitmap;
	private int newHeight = 300;
	private String filemanagerstring;
	String selectedImagePath;
	//String PicturePath;
	int TAKE_PICTURE=0;	
	//Camera camera;
	

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
        if (theme_color.equalsIgnoreCase("White")){
        	setTheme(android.R.style.Theme_Light_NoTitleBar);
        }
        // Obtain the sharedPreference, default to true if not available
        super.onCreate(savedInstanceState); 
        if (theme_color.equalsIgnoreCase("White")){
        	setContentView(R.layout.newposter);
        	//TODO: bib_white);
        } else {
        	setContentView(R.layout.newposter);
        }
        Intent intentFromMenu = super.getIntent();//
        Bundle extras = intentFromMenu.getExtras();
        Conf = (Conference)extras.getSerializable("conf");
        //Intent resultIntent = new Intent();
        
        /*private String mainauthorfirstname;
    	private String mainauthorlastname;
    	private String coauthor;
    	private String title;
    	private String abstracte;	
    	private String[] references;*/
        final EditText ed_name = (EditText)findViewById(R.id.ed_name);
        final EditText ed_coauthor = (EditText)findViewById(R.id.ed_coauthor);
        final EditText ed_title = (EditText)findViewById(R.id.ed_title);
        final EditText ed_abstract = (EditText)findViewById(R.id.ed_abstract);
        final EditText ed_references = (EditText)findViewById(R.id.ed_references);
        
        			        
        //set text, from before
        //Bibitem = readBibItem();
        if (Posteritem instanceof Poster){
        	String firstname = Posteritem.getMainauthorfirstname();
        	String lastname = Posteritem.getMainauthorlastname();
        	String coauthor = Posteritem.getCoauthor();
        	String title = Posteritem.getTitle();
        	String abstracte = Posteritem.getAbstract();
            String[] references = Posteritem.getReferences();
            
            ed_name.setText(firstname + " " + lastname);
            ed_coauthor.setText(coauthor);
            ed_title.setText(title);
            ed_abstract.setText(abstracte);
            ed_references.setText(references[0]);
        }
        if (thumbnailPath != null){
        	setThumbnail();
        }
        
        
        //final TextView testtext = (TextView)findViewById(R.id.testtext);
        Button bt_ok = (Button)findViewById(R.id.bt_ok);
        bt_ok.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				String firstname = getString(R.string.none);
				String lastname = getString(R.string.none);
				String coauthor = getString(R.string.none);
				String title = getString(R.string.none);
				String abstracte = getString(R.string.none);
				String[] references = new String[1];
				references[0] = getString(R.string.none);
				
				if (ed_name.getText().toString()!=null){
					String name=ed_name.getText().toString();
					String[] names = name.split(" ");
					if (names.length==1){
						lastname=names[0];	
					} else if (names.length==2){
						lastname=names[1];	
						firstname=names[0];
					}else if (names.length>2){
						lastname=names[names.length-1];	
						firstname=names[0];
					}					
				}				
				if (ed_coauthor.getText().toString()!=null){
					coauthor=ed_coauthor.getText().toString();
				}				
				if (ed_references.getText().toString()!=null){
					references[0]=ed_references.getText().toString();
				}
				if (ed_title.getText().toString()!=null){
					title=ed_title.getText().toString();
				}
				if (ed_abstract.getText().toString()!=null){
					abstracte=ed_abstract.getText().toString();
				}
				if (thumbnailPath != null){
					thumbnailFile = new File(thumbnailPath);
					Time now = new Time();
					now.setToNow();
					String imagePath = VariousMethods.getMainFolder(PosterNewActivity.this)+Conf.getConfFolder()+"/" + lastname + "_" + now.format2445() + ".jpg";
					File posterFile = new File(imagePath);
					try{
						VariousMethods.copy(thumbnailFile,posterFile);
						Posteritem=new Poster(firstname,lastname,coauthor,title,abstracte,references,lastname + "_" + now.format2445() + ".jpg",rotation,0);
					} catch (IOException e){
						Posteritem=new Poster(firstname,lastname,coauthor,title,abstracte,references,null,rotation,0);
						Toast.makeText(PosterNewActivity.this, "copying the picture to the Conference folder failed", Toast.LENGTH_LONG).show();
					}
					
				}else{
					Posteritem=new Poster(firstname,lastname,coauthor,title,abstracte,references,null,rotation,0);
				}
				Bundle createdPoster = new Bundle();				
				createdPoster.putSerializable("poster", Posteritem);					
				Intent mIntent = new Intent();				
	            mIntent.putExtras(createdPoster);	            
				setResult(2,mIntent);//Activity.RESULT_OK,  mIntent);				
				finish();    			
			}			
		}); 
        Button bt_camera = (Button)findViewById(R.id.bt_camera);
        bt_camera.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
					thumbnailPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/tempposterpic.jpg" ;
					thumbnailFile = new File(thumbnailPath);
		 			Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		 			Uri outputFileUri = Uri.fromFile(thumbnailFile);
		 			cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
		 			startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST);		 			
			}			
		});
        Button bt_file = (Button)findViewById(R.id.bt_file);
        bt_file.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				Intent fileIntent = new Intent();
				fileIntent.setType("image/*");
				fileIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(fileIntent,
             		   getString(R.string.eb_select_pic)), SELECT_PICTURE);
			}			
		});
    }      
   
    public void setThumbnail(){
        ImageView image =(ImageView)findViewById(R.id.PhotoCaptured);

        try{
      	  thumbnail.recycle();
        }catch(RuntimeException e){
      	  
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 8;
        
        rotation = VariousMethods.getBitmapRotation(thumbnailPath);
        File file = new File(thumbnailPath);
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
        	
        	thumbnail = BitmapFactory.decodeFile(thumbnailPath,options);
    	} catch(OutOfMemoryError e){
    		System.out.println("Out of memory");
    		options.inSampleSize = 8;
    		thumbnail = BitmapFactory.decodeFile(thumbnailPath,options);
    	}
         
        int width = thumbnail.getWidth();
        int height = thumbnail.getHeight();
        int newWidth = (int) (newHeight/(height*1.0f)*width);
        // calculate the scale
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
       
        // create a matrix for the manipulation
        Matrix matrix = new Matrix();
        // resize the bit map
        matrix.postScale(scaleWidth, scaleHeight);
        // rotate the Bitmap
        matrix.postRotate(rotation);
        try{
      	  resizedBitmap.recycle();
        }catch(RuntimeException e){
      	  
        }
        // recreate the new Bitmap
        resizedBitmap = Bitmap.createBitmap(thumbnail, 0, 0,
                          width, height, matrix, true);
        image.setImageBitmap(resizedBitmap);

    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
      super.onActivityResult(requestCode, resultCode, data);
      
      //Toast.makeText(this, requestCode, Toast.LENGTH_LONG).show();
      switch(requestCode) {
        case CAMERA_PIC_REQUEST: {//Activity.RESULT_OK: {
        	if (resultCode==Activity.RESULT_OK){
        		thumbnailPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/tempposterpic.jpg";
        		setThumbnail();
        	} else {
        		Toast.makeText(PosterNewActivity.this, "Picture not taken", Toast.LENGTH_LONG).show();
        	}
        	
            
          break;
        }
        case SELECT_PICTURE: {
        	if (resultCode==Activity.RESULT_OK){
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
	            if(selectedImagePath!=null){
	            	thumbnailPath=selectedImagePath;
	            }else{
	            	thumbnailPath=filemanagerstring;
	            }
	            setThumbnail();
        	} else {
        		Toast.makeText(PosterNewActivity.this, "No picture selected", Toast.LENGTH_LONG).show();
        	}
        	break;
        }
        default: {
        	Toast.makeText(PosterNewActivity.this, "Picture not taken", Toast.LENGTH_LONG).show();
        	break;
        }
      }
    }
    
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
        final EditText ed_coauthor = (EditText)findViewById(R.id.ed_coauthor);
        final EditText ed_title = (EditText)findViewById(R.id.ed_title);
        final EditText ed_abstract = (EditText)findViewById(R.id.ed_abstract);
        final EditText ed_references = (EditText)findViewById(R.id.ed_references);
    	String firstname = getString(R.string.none);
		String lastname = getString(R.string.none);
		String coauthor = getString(R.string.none);
		String title = getString(R.string.none);
		String abstracte = getString(R.string.none);
		String[] references = new String[1];
		references[0] = getString(R.string.none);
		
		if (ed_name.getText().toString()!=null){
			String name=ed_name.getText().toString();
			String[] names = name.split(" ");
			if (names.length==1){
				lastname=names[0];	
			} else if (names.length==2){
				lastname=names[1];	
				firstname=names[0];
			}else if (names.length>2){
				lastname=names[names.length-1];	
				firstname=names[0];
			}
			
							
		}				
		if (ed_coauthor.getText().toString()!=null){
			coauthor=ed_coauthor.getText().toString();
		}				
		if (ed_references.getText().toString()!=null){
			references[0]=ed_references.getText().toString();
		}
		if (ed_title.getText().toString()!=null){
			title=ed_title.getText().toString();
		}
		if (ed_abstract.getText().toString()!=null){
			abstracte=ed_abstract.getText().toString();
		}

		//return Confitem
		//change from single strings to conference 
		Posteritem=new Poster(firstname,lastname,coauthor,title,abstracte,references,null,0,0);
		savedInstanceState.putString("tempposterpic", thumbnailPath);
		savedInstanceState.putSerializable("tempposter", Posteritem);      
		super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState)
    {
      super.onRestoreInstanceState(savedInstanceState);
      // EditTexts text
      Posteritem=(Poster)savedInstanceState.getSerializable("tempposter");  
      thumbnailPath = savedInstanceState.getString("tempposterpic");
      if (thumbnailPath!=null){
    	  setThumbnail();
      }
      
    }
  
    public boolean hasImageCaptureBug() {

        // list of known devices that have the bug
        ArrayList<String> devices = new ArrayList<String>();
        devices.add("android-devphone1/dream_devphone/dream");
        devices.add("generic/sdk/generic");
        devices.add("vodafone/vfpioneer/sapphire");
        devices.add("tmobile/kila/dream");
        devices.add("verizon/voles/sholes");
        devices.add("google_ion/google_ion/sapphire");

        return devices.contains(android.os.Build.BRAND + "/" + android.os.Build.PRODUCT + "/"
                + android.os.Build.DEVICE);

    }

}
