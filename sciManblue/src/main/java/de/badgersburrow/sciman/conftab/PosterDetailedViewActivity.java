package de.badgersburrow.sciman.conftab;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import de.badgersburrow.sciman.objects.Conference;
import de.badgersburrow.sciman.objects.Poster;
import de.badgersburrow.sciman.R;
import de.badgersburrow.sciman.utilities.VariousMethods;

import java.io.File;


public class PosterDetailedViewActivity extends Activity {
	private String theme_color;
	private int newWidth;
	private String thumbnailPath;
	
	/** Called when the activity is first created. */
	
    @Override
    public void onCreate(Bundle savedInstanceState) {  
    	SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        theme_color = sp.getString("pref_color_theme_entries","Black");
        if (theme_color.equalsIgnoreCase("White")){
        	setTheme(android.R.style.Theme_Light_NoTitleBar);
        }
        super.onCreate(savedInstanceState);  
        //ScrollView sv = new ScrollView(this);
        setContentView(R.layout.posterdetailedview);

        Intent intentFromMenu = super.getIntent();//
        Display display = getWindowManager().getDefaultDisplay(); 
    	int displaywidth = display.getWidth();
    	//int displayheight = display.getHeight();
    	if (displaywidth>1000){
    		newWidth=900;
    	} else {
    		newWidth=(int)(0.9*displaywidth);
    	}
        Bundle extras = intentFromMenu.getExtras();
        Poster poster = (Poster)extras.getSerializable("poster");
        Conference conf = (Conference)extras.getSerializable("conf");
        //setContentView(R.layout.staticscrollview);
        TextView tv_title = (TextView)findViewById(R.id.poster_dv_title);
        TextView tv_author = (TextView)findViewById(R.id.poster_dv_author);
        ImageView iv_thumbnail = (ImageView)findViewById(R.id.poster_dv_thumbnail);
        TextView tv_abstract = (TextView)findViewById(R.id.poster_dv_abstract);
        TextView tv_references = (TextView)findViewById(R.id.poster_dv_references);
        tv_title.setText(poster.getTitle());
        tv_author.setText(poster.getMainauthorfirstname() + " " + poster.getMainauthorlastname() + " and " + poster.getCoauthor());
        
        int rotation= poster.getRotation();
        thumbnailPath = VariousMethods.getMainFolder(this)+ conf.getConfFolder() + "/" + poster.getImagefile();
        Bitmap thumbnail;
	      BitmapFactory.Options options = new BitmapFactory.Options();
	      options.inSampleSize = 8;
	      
	      rotation = poster.getRotation();
	      File file = new File(thumbnailPath);
	      try{
	      	if (file.length()>1500000){
	      		options.inSampleSize = 2;
	      	}else if (file.length()>800000){
	      		options.inSampleSize = 1;
	        }else if (file.length()>400000){
	      		options.inSampleSize = 1;
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
	      if (poster.getRotation() == 90 ||  poster.getRotation() == 270){
	    	  
	      } else {
	    	  
		      
	      }
	      
	      int newHeight = (int) (newWidth/(width*1.0f)*height);
	      // calculate the scale
	      float scaleWidth = ((float) newWidth) / (width*1.0f);
	      float scaleHeight = ((float) newHeight) / (height*1.0f);
	     
	      // create a matrix for the manipulation
	      Matrix matrix = new Matrix();
	      // resize the bit map
	      matrix.postScale(scaleWidth, scaleHeight);
	      // rotate the Bitmap
	      matrix.postRotate(rotation);
	      
	      // recreate the new Bitmap
	      Bitmap resizedBitmap = Bitmap.createBitmap(thumbnail, 0, 0,
	                        width, height, matrix, true);
	      iv_thumbnail.setImageBitmap(resizedBitmap);
	   
	    iv_thumbnail.setClickable(true);
	    iv_thumbnail.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setAction(Intent.ACTION_VIEW);
				intent.setDataAndType(Uri.parse("file:/" + thumbnailPath), "image/*");
				startActivity(intent);	 			
			}			
		});
        tv_abstract.setText(poster.getAbstract());
        tv_references.setText(poster.getReferences()[0]);
    } 
    
   
 
  
}
