package de.badgersburrow.sciman.conftab;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import de.badgersburrow.sciman.objects.Conference;
import de.badgersburrow.sciman.main.MainActivity;
import de.badgersburrow.sciman.utilities.PictureLink;
import de.badgersburrow.sciman.R;
import de.badgersburrow.sciman.objects.Topics;
import de.badgersburrow.sciman.utilities.VariousMethods;

import java.io.File;


public class ConfNewActivity extends AppCompatActivity {
	private String filemanagerstring;	
	private Conference Confitem;
	private Topics Conftopics;
	private Topics allTopics;
	//ListView dialog_ListView;
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
    	Conftopics.deleteTopic(numberOfAddedTopics-numberOfDeletedTopics-1);
    	numberOfDeletedTopics++;
    	
    }
	
	 /** Called when the activity is first created. */
	
    @Override
    public void onCreate(Bundle savedInstanceState) {  

        super.onCreate(savedInstanceState); 

        setContentView(R.layout.conf_new_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(R.string.ncf_header);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        if (Conftopics==null){
        	Conftopics = new Topics();
        } 
        Intent intentFromMenu = super.getIntent();//
        //Bundle extras = intentFromMenu.getExtras();
        allTopics = (Topics)intentFromMenu.getSerializableExtra("Topics");
        
        final EditText ed_name = (EditText)findViewById(R.id.ed_name);
        final EditText ed_location = (EditText)findViewById(R.id.ed_location);
        final DatePicker dp_year = (DatePicker)findViewById(R.id.dp_year);
        final EditText ed_topics = (EditText)findViewById(R.id.ed_topics);
        final EditText ed_comment = (EditText)findViewById(R.id.ed_comment);
        final CheckBox cb_showAtPoster = (CheckBox)findViewById(R.id.cb_poster);
        final CheckBox cb_showAtContacts = (CheckBox)findViewById(R.id.cb_contacts);
        			        
        //set text, from before
        //Bibitem = readBibItem();
        if (Confitem instanceof Conference){
        	String bibName = Confitem.getConfname();
        	String location = Confitem.getLocation();
            Conftopics = Confitem.getTopics();
            String comment = Confitem.getComment();
            boolean showAtPoster = Confitem.getShowAtPoster();
            boolean showAtContacts = Confitem.getShowAtContacts();
            ed_name.setText(bibName);
            ed_location.setText(location);
            cb_showAtPoster.setChecked(showAtPoster);
            cb_showAtContacts.setChecked(showAtContacts);
            ed_comment.setText(comment);
            for (int i=0; i<Conftopics.getNumberOfTopics();i++){
        	  	if (!allTopics.isItem(Conftopics.getTopic(i ))){
    				allTopics.addTopic(Conftopics.getTopic(i ));
    			}
      			View topic_v;
    			LayoutInflater li = getLayoutInflater();
    			topic_v = li.inflate(R.layout.topic, null);

    			topic_v.setId(numberOfAddedTopics);
    			TextView tv_topic = (TextView)topic_v.findViewById(R.id.tv_topic);
    			tv_topic.setText(Conftopics.getTopic(i ));
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
        		if (newTopic!=null && !newTopic.equalsIgnoreCase("") && !Conftopics.isItem(newTopic))
        		//and not already added to Conftopics
        		{    
        			if (!allTopics.isItem(newTopic)){
        				allTopics.addTopic(newTopic);
        				MainActivity.allTopics = allTopics;
        				//TODO: maybe a preference for that
        				//maybe a complete category for topics
        				VariousMethods.saveTopics(allTopics, ConfNewActivity.this);
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
        			Conftopics.addTopic(newTopic)    ;    
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
				String name = getString(R.string.none);
				String location = getString(R.string.none);
				int day = dp_year.getDayOfMonth();
				int month = dp_year.getMonth();
				int year = dp_year.getYear();
				String comment =getString(R.string.none);
				String pdfdirectory =getString(R.string.none);

				if (ed_name.getText().toString()!=null){
					name=ed_name.getText().toString();
				}				
				if (ed_location.getText().toString()!=null){
					location=ed_location.getText().toString();
				}				
				if (ed_comment.getText().toString()!=null){
					comment=ed_comment.getText().toString();
				}
				boolean showAtPoster = cb_showAtPoster.isChecked();
				boolean showAtContacts = cb_showAtContacts.isChecked();
				
				//return Confitem
				//change from single strings to conference
				int [] startDate = new int[] {year,month,day};
				int numDays = 0;
				Confitem=new Conference(name,location,startDate,numDays,Conftopics,comment,showAtPoster,showAtContacts,0);
				File testfolder =new File(VariousMethods.getDirectoriable(Confitem.getConfFolder()));
				if (testfolder.exists()){
					AlertDialog alertDialog = new AlertDialog .Builder(ConfNewActivity.this).create();
		            alertDialog.setTitle(getString(R.string.alert));
		            alertDialog.setMessage(getString(R.string.nc_alreadyexists));
		            alertDialog.setButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
		                public void onClick(DialogInterface dialog, int which) {
		           
		                 //here you can add functions
		           
		              } }); 
		            alertDialog.setIcon(R.drawable.alerticon);
		        	alertDialog.show();
				} else {					
					VariousMethods.createConfFolder(ConfNewActivity.this, Confitem);
					Bundle createdConf = new Bundle();				
					createdConf.putSerializable("conf", Confitem);		
					createdConf.putSerializable("topics", allTopics);
					Intent mIntent = new Intent();				
		            mIntent.putExtras(createdConf);	            
					setResult(2,mIntent);//Activity.RESULT_OK,  mIntent);				
					finish(); 
				}				   			
			}			
		});      
    }      
   
    
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState)
    {
      // EditTexts text
		final EditText ed_name = (EditText)findViewById(R.id.ed_name);
		final EditText ed_location = (EditText)findViewById(R.id.ed_location);
		final DatePicker dp_year = (DatePicker)findViewById(R.id.dp_year);
		final EditText ed_topics = (EditText)findViewById(R.id.ed_topics);
		final EditText ed_comment = (EditText)findViewById(R.id.ed_comment);
		final CheckBox cb_showAtPoster = (CheckBox)findViewById(R.id.cb_poster);
		final CheckBox cb_showAtContacts = (CheckBox)findViewById(R.id.cb_contacts);
		String name =getString(R.string.none);
		String location =getString(R.string.none);
		int day = dp_year.getDayOfMonth();
		int month = dp_year.getMonth();
		int year = dp_year.getYear();
		String comment =getString(R.string.none);
		String topicInput = "";

		if (ed_name.getText().toString()!=null){
			name=ed_name.getText().toString();
		}				
		if (ed_location.getText().toString()!=null){
			location=ed_location.getText().toString();
		}				
		if (ed_topics.getText().toString()!=null){
			topicInput=ed_topics.getText().toString();
		}
		if (ed_comment.getText().toString()!=null){
			comment=ed_comment.getText().toString();
		}
		boolean showAtPoster = cb_showAtPoster.isChecked();
		boolean showAtContacts = cb_showAtContacts.isChecked();

		int [] startDate = new int[] {year,month,day};
		int numDays = 0;
		Confitem=new Conference(name,location,startDate,numDays,Conftopics,comment,showAtPoster,showAtContacts,0);
		savedInstanceState.putSerializable("tempconf", Confitem); 
		savedInstanceState.putString("temptopicinput", topicInput); 
		super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState)
    {      
      // EditTexts text
      Confitem=(Conference)savedInstanceState.getSerializable("tempconf"); 
      Conftopics=Confitem.getTopics();
      String topicinput = savedInstanceState.getString("temptopicinput");
      final EditText ed_topics = (EditText)findViewById(R.id.ed_topics);
      ed_topics.setText(topicinput);
      
      super.onRestoreInstanceState(savedInstanceState);
    }
    
    @Override
    protected Dialog onCreateDialog(int id) {
    
     Dialog dialog = null;
      
     switch(id) {
        case dialog_topics:
         dialog = new Dialog(ConfNewActivity.this);
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

