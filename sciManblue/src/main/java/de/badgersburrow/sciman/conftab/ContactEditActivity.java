package de.badgersburrow.sciman.conftab;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnDismissListener;
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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import de.badgersburrow.sciman.objects.Conference;
import de.badgersburrow.sciman.objects.Contact;
import de.badgersburrow.sciman.main.MainActivity;
import de.badgersburrow.sciman.utilities.PictureLink;
import de.badgersburrow.sciman.R;
import de.badgersburrow.sciman.objects.Topics;
import de.badgersburrow.sciman.utilities.VariousMethods;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


public class ContactEditActivity extends Activity {
		
	private Contact Contactitem;
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
	
	Topics allTopics;
	Topics Contacttopics;
	ListView topics_ListView;
	private String theme_color;
	private LinearLayout ll_topics;
	private int numberOfAddedTopics = 0;
	private int numberOfDeletedTopics = 0;
	static final int dialog_topics = 0;
	

    @Override
	public void onBackPressed() {    	
    	// do something on back.
    	Intent mIntent = new Intent();
        //mIntent.putExtras(bibAttributes);
		setResult(0,mIntent);//Activity.RESULT_OK,  mIntent);		
		super.onBackPressed();//finish();
	}
    
    public void deleteTopic(int id){
    	
    	View topic_v = findViewById(id);
    	ll_topics.removeView(topic_v);
    	Contacttopics.deleteTopic(numberOfAddedTopics-numberOfDeletedTopics-1);
    	numberOfDeletedTopics++;
    	
    }

	
	 /** Called when the activity is first created. */
	
    @Override
    public void onCreate(Bundle savedInstanceState) {  
    	
    	SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        theme_color = sp.getString("pref_color_theme_entries","Black");
        if (theme_color.equalsIgnoreCase("White")){
        	setTheme(android.R.style.Theme_Light_NoTitleBar);
        }
        // Obtain the sharedPreference, default to true if not available
        super.onCreate(savedInstanceState); 
        setContentView(R.layout.editcontact);

        
        if (Contacttopics==null){
        	Contacttopics = new Topics();
        } 
        
        Intent intentFromMenu = super.getIntent();//
        Bundle extras = intentFromMenu.getExtras();
        Conf = (Conference)extras.getSerializable("conf");
        allTopics = (Topics)extras.getSerializable("Topics");
        final Contact selectedContact = (Contact)extras.getSerializable("Contact");
        //Intent resultIntent = new Intent();
        
    	String firstname = selectedContact.getFirstName();
    	String lastname = selectedContact.getLastName();
    	String institute = selectedContact.getInstitute();
    	String organization = selectedContact.getOrganization();
    	String emailadress = selectedContact.getEmailAdress();
    	String phonenumber = selectedContact.getPhoneNumber();
    	String mobilenumber = selectedContact.getMobileNumber();
    	String picturefile = selectedContact.getPictureFile();
        Contacttopics = selectedContact.getResearchTopics();
        
        final EditText ed_name = (EditText)findViewById(R.id.ed_name);
        final EditText ed_institute = (EditText)findViewById(R.id.ed_institute);
        final EditText ed_organization = (EditText)findViewById(R.id.ed_organization);
        final EditText ed_emailadress = (EditText)findViewById(R.id.ed_emailadress);
        final EditText ed_phonenumber = (EditText)findViewById(R.id.ed_phonenumber);
        final EditText ed_mobilenumber = (EditText)findViewById(R.id.ed_mobilenumber);
        final EditText ed_topics = (EditText)findViewById(R.id.ed_topics);
        //final EditText ed_picturefile = (EditText)findViewById(R.id.ed_references);
        //add topics
        //final EditText ed_phonenumber = (EditText)findViewById(R.id.ed_references);
        
        			        
        //set text, from before
        //Bibitem = readBibItem();
        if (Contactitem instanceof Contact){
        	firstname = Contactitem.getFirstName();
        	lastname = Contactitem.getLastName();
        	institute = Contactitem.getInstitute();
        	organization = Contactitem.getOrganization();
        	emailadress = Contactitem.getEmailAdress();
        	phonenumber = Contactitem.getPhoneNumber();
        	mobilenumber = Contactitem.getMobileNumber();
        	picturefile = Contactitem.getPictureFile();
            Contacttopics = Contactitem.getResearchTopics();
        }   
        ed_name.setText(firstname + " " + lastname);
        ed_institute.setText(institute);
        ed_organization.setText(organization);
        ed_emailadress.setText(emailadress);
        ed_phonenumber.setText(phonenumber);
        ed_mobilenumber.setText(mobilenumber);
        //ed_picturefile.setText(picturefile);
        for (int i=0; i<Contacttopics.getNumberOfTopics();i++){
    	  	if (!allTopics.isItem(Contacttopics.getTopic(i ))){
				allTopics.addTopic(Contacttopics.getTopic(i ));
			}
  			View topic_v;
			LayoutInflater li = getLayoutInflater();
			topic_v = li.inflate(R.layout.topic, null);

			topic_v.setId(numberOfAddedTopics);
			TextView tv_topic = (TextView)topic_v.findViewById(R.id.tv_topic);
			tv_topic.setText(Contacttopics.getTopic(i ));
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
        
        if (thumbnailPath != null){
        	setThumbnail();
        }
        
        ImageView iv_list = (ImageView)findViewById(R.id.iv_list);
        iv_list.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View v) {
        		if (allTopics.getNumberOfTopics()>0){
        			showDialog(dialog_topics);
        		} else{
        			
        		}
        		
        	}
        });
        
ll_topics = (LinearLayout)findViewById(R.id.ll_topics);
        
        ImageView iv_addtopic = (ImageView)findViewById(R.id.iv_addtopic);
        iv_addtopic.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View v) {
        		String newTopic = ed_topics.getText().toString();
        		if (newTopic!=null && !newTopic.equalsIgnoreCase("") && !Contacttopics.isItem(newTopic))
        		//and not already added to Conftopics
        		{    
        			if (!allTopics.isItem(newTopic)){
        				allTopics.addTopic(newTopic);
        				MainActivity.allTopics = allTopics;
        				//TODO: maybe a preference for that
        				//maybe a complete category for topics
        				VariousMethods.saveTopics(allTopics, ContactEditActivity.this);
        			} 
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
        			Contacttopics.addTopic(newTopic)    ;    
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
				String firstname = getString(R.string.none);
				String lastname = getString(R.string.none);
				String institute = getString(R.string.none);
				String organization = getString(R.string.none);
				String emailadress = getString(R.string.none);
				String phonenumber = getString(R.string.none);
				String mobilenumber = getString(R.string.none);
				String picturefile = getString(R.string.none);
				Topics researchtopics = new Topics();
				//researchtopics[0] = getString(R.string.none);
				
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
				if (ed_institute.getText().toString()!=null){
					institute=ed_institute.getText().toString();
				}				
				if (ed_organization.getText().toString()!=null){
					organization=ed_organization.getText().toString();
				}
				if (ed_emailadress.getText().toString()!=null){
					emailadress=ed_emailadress.getText().toString();
				}
				if (ed_phonenumber.getText().toString()!=null){
					phonenumber=ed_phonenumber.getText().toString();
				}
				if (ed_mobilenumber.getText().toString()!=null){
					mobilenumber=ed_mobilenumber.getText().toString();
				}
				
				if (thumbnailPath != null){
					thumbnailFile = new File(thumbnailPath);
					Time now = new Time();
					now.setToNow();
					String imagePath = VariousMethods.getMainFolder(ContactEditActivity.this)+Conf.getConfFolder()+"/" + lastname + "_" + now.format2445() + ".jpg";
					File posterFile = new File(imagePath);
					try{
						VariousMethods.copy(thumbnailFile,posterFile);
						Contactitem=new Contact(firstname,lastname,institute,organization,emailadress,phonenumber,mobilenumber,lastname + "_" + now.format2445() + ".jpg",rotation,Contacttopics,0);
					} catch (IOException e){
						Contactitem=new Contact(firstname,lastname,institute,organization,emailadress,phonenumber,mobilenumber,null,rotation,Contacttopics,0);
						Toast.makeText(ContactEditActivity.this, "copying the picture to the Conference folder failed", Toast.LENGTH_LONG).show();
					}
					
				}else{
					Contactitem=new Contact(firstname,lastname,institute,organization,emailadress,phonenumber,mobilenumber,null,rotation,Contacttopics,0);
				}
				Bundle createdPoster = new Bundle();				
				createdPoster.putSerializable("contact", Contactitem);		
				createdPoster.putSerializable("topics", allTopics);
				Intent mIntent = new Intent();				
	            mIntent.putExtras(createdPoster);	            
				setResult(3,mIntent);//Activity.RESULT_OK,  mIntent);				
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
        		Toast.makeText(ContactEditActivity.this, "Picture not taken", Toast.LENGTH_LONG).show();
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
        		Toast.makeText(ContactEditActivity.this, "No picture selected", Toast.LENGTH_LONG).show();
        	}
        	break;
        }
        default: {
        	Toast.makeText(ContactEditActivity.this, "Picture not taken", Toast.LENGTH_LONG).show();
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
        final EditText ed_institute = (EditText)findViewById(R.id.ed_institute);
        final EditText ed_organization = (EditText)findViewById(R.id.ed_organization);
        final EditText ed_emailadress = (EditText)findViewById(R.id.ed_emailadress);
        final EditText ed_phonenumber = (EditText)findViewById(R.id.ed_phonenumber);
        final EditText ed_mobilenumber = (EditText)findViewById(R.id.ed_mobilenumber);
        final EditText ed_topics = (EditText)findViewById(R.id.ed_topics);
		String firstname = getString(R.string.none);
		String lastname = getString(R.string.none);
		String institute = getString(R.string.none);
		String organization = getString(R.string.none);
		String emailadress = getString(R.string.none);
		String phonenumber = getString(R.string.none);
		String mobilenumber = getString(R.string.none);
		String picturefile = getString(R.string.none);
		String topicInput = "";
		
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
		if (ed_institute.getText().toString()!=null){
			institute=ed_institute.getText().toString();
		}				
		if (ed_organization.getText().toString()!=null){
			organization=ed_organization.getText().toString();
		}
		if (ed_emailadress.getText().toString()!=null){
			emailadress=ed_emailadress.getText().toString();
		}
		if (ed_phonenumber.getText().toString()!=null){
			phonenumber=ed_phonenumber.getText().toString();
		}
		if (ed_mobilenumber.getText().toString()!=null){
			mobilenumber=ed_mobilenumber.getText().toString();
		}
		if (ed_topics.getText().toString()!=null){
			topicInput=ed_topics.getText().toString();
		}

		//return Confitem
		//change from single strings to conference 
		Contactitem=new Contact(firstname,lastname,institute,organization,emailadress,phonenumber,mobilenumber,null,0,Contacttopics,0);
		savedInstanceState.putString("tempcontactpic", thumbnailPath);
		savedInstanceState.putSerializable("tempcontact", Contactitem); 
		savedInstanceState.putString("temptopicinput", topicInput); 
		super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState)
    {
      super.onRestoreInstanceState(savedInstanceState);
      // EditTexts text
      Contactitem=(Contact)savedInstanceState.getSerializable("tempcontact");   
      Contacttopics=Contactitem.getResearchTopics();
      String topicinput = savedInstanceState.getString("temptopicinput");
      final EditText ed_topics = (EditText)findViewById(R.id.ed_topics);
      ed_topics.setText(topicinput);      
      thumbnailPath = savedInstanceState.getString("tempcontactpic");
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
    
    @Override
    protected Dialog onCreateDialog(int id) {
    
     Dialog dialog = null;
      
     switch(id) {
        case dialog_topics:
         dialog = new Dialog(ContactEditActivity.this);
         dialog.setContentView(R.layout.installedapps_dialog);
         dialog.setTitle("Choose topic");
          
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
         topics_ListView = (ListView)dialog.findViewById(R.id.dialoglist);
         ArrayAdapter<String> dialogAdapter
          = new ArrayAdapter<String>(this,
        		  R.layout.installedapps_list, allTopics.getTopics());
         topics_ListView.setAdapter(dialogAdapter);
         topics_ListView.setOnItemClickListener(new OnItemClickListener(){
    
       //@Override
       public void onItemClick(AdapterView<?> parent, View view,
         int position, long id) {
        // TODO Auto-generated method stub
        //Toast.makeText(NewBibActivity.this,parent.getItemAtPosition(position).toString() + " clicked",Toast.LENGTH_LONG).show();
    	final EditText ed_topics = (EditText)findViewById(R.id.ed_topics);
        ed_topics.setText(allTopics.getTopic(position));
        dismissDialog(dialog_topics);
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
        case dialog_topics:
         //
            break;
        }
      
    }

}
