package de.badgersburrow.sciman.conftab;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;

import de.badgersburrow.sciman.objects.Conference;
import de.badgersburrow.sciman.objects.Contact;
import de.badgersburrow.sciman.main.MainActivity;
import de.badgersburrow.sciman.R;
import de.badgersburrow.sciman.objects.Topics;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class ContactViewActivity extends Activity {
	
	private String theme_color;
	private Contact[] contactList = new Contact[100];
	public int numberOfItems =0;
	public boolean lastnameFirst = false;
    public int ItemId =0;
	private Conference Conf;
	private int requestCode;
	private String filePath;
	Topics allTopics = MainActivity.allTopics;
	private int PictureWidth = 60;
	private int PictureHeight = 60;
	private Topics ResearchTopics = new Topics();
	
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
        setContentView(R.layout.contacts_view);

        //setContentView(R.layout.staticscrollview);
        Intent intentFromMenu = super.getIntent();//
        Bundle extras = intentFromMenu.getExtras();
        Conf = (Conference)extras.getSerializable("conf");
        TextView tv_header = (TextView)findViewById(R.id.tv_header);
        tv_header.setText(getString(R.string.cv_header) + Conf.getConfname());

        ImageView add_button = (ImageView) findViewById(R.id.add_button);
        add_button.setOnClickListener(new View.OnClickListener() {            
            //@Override
            public void onClick(View v) {
        		Intent in = new Intent(getApplicationContext(), ContactNewActivity.class);
        		requestCode = 2;
        		 Bundle extras = new Bundle();
        	     extras.putSerializable("conf", Conf);
        	     extras.putSerializable("topics", allTopics);
        	     in.putExtras(extras);
        		startActivityForResult(in,requestCode);
            }
         });
        
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        switch(metrics.densityDpi){
        	//set column width
             case DisplayMetrics.DENSITY_LOW:
            	 
            	 PictureWidth = 40;
            	 PictureHeight = 40;
                        break;
             case DisplayMetrics.DENSITY_MEDIUM:
            
            	 PictureWidth = 50;
            	 PictureHeight = 50;
                         break;
             case DisplayMetrics.DENSITY_HIGH://changed other resolutions have to be adapted too
            	
            	 PictureWidth = 60;
            	 PictureHeight = 60;
                         break;
             case 320:
            
            	 PictureWidth = 80;
            	 PictureHeight = 80;
        	 			break;
        }
        for (int i=0;i<100;i++){
        	if (contactList[i] instanceof Contact){
        		addItem(contactList[i]);
        	}
        	
        }      
    } 
    
    public String getNameTag(Contact contact, boolean lastnameFirst){
    	if (lastnameFirst){
    		return contact.getLastName() + ", " + contact.getFirstName();
    	} else{
    		return contact.getFirstName() + " " + contact.getLastName();
    	}
    	
    }
    
    
    public boolean readContacts(String fileFolder){
    	File contactFile = new File(fileFolder,"contacts.xml");
    	if (contactFile.exists()){
    		
    		String FirstName  = "None"; 
			String LastName = "None"; 
			String Institute="None";
			String Organization="None";
			String Homepage = "None";     			
			String EmailAdress = "None";
			String PhoneNumber = "None";
			String MobileNumber = "None";
			String PictureFile = "None";
			int Rotation = 0;
			ResearchTopics = new Topics();
			int ID = 0;   		
    		
    		
    		try {
    			
    			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
    			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
    			Document doc = docBuilder.parse(contactFile);
    			
    			NodeList contactlist = doc.getElementsByTagName("contact");
    			for(int j = 0; j < 100; j++) {
    				Node contactNode = contactlist.item(j);
    				//String test2 = bib.getNodeValue();
    				//new Poster
    				if (contactNode!=null){
    					FirstName  = "None"; 
    					LastName = "None"; 
    					Institute="None";
    					Organization="None";
    					Homepage = "None";     			
    					EmailAdress = "None"; 
    					PhoneNumber = "None";
    					MobileNumber = "None";
    					PictureFile = "None";
    					Rotation = 0;
    					ResearchTopics = new Topics();
    					ID = 0; 
    					
    					NamedNodeMap attr = contactNode.getAttributes();
    					Node FirstNameNode = attr.getNamedItem("FirstName");
    					FirstName = FirstNameNode.getTextContent();
    					Node LastNameNode = attr.getNamedItem("LastName");
    					LastName = LastNameNode.getTextContent();
    					Node InstituteNode = attr.getNamedItem("Institute");
    					Institute = InstituteNode.getTextContent();
    					Node OrganizationNode = attr.getNamedItem("Organization");
    					Organization = OrganizationNode.getTextContent();
    					//Node HomepageNode = attr.getNamedItem("Homepage");
    					//Homepage = HomepageNode.getTextContent();
    					Node EmailAdressNode = attr.getNamedItem("EmailAdress");
    					EmailAdress = EmailAdressNode.getTextContent();
    					Node PhoneNumberNode = attr.getNamedItem("PhoneNumber");
    					PhoneNumber = PhoneNumberNode.getTextContent();
    					Node MobileNumberNode = attr.getNamedItem("MobileNumber");
    					MobileNumber = MobileNumberNode.getTextContent();
    					
    					//NodeList contactNodeChildren = contactNode.getFirstChild();
    					Node topicNode = contactNode.getFirstChild();
    					//for (int k=0;k<contactNodeChildren.getLength();k++){
    						//if (contactNodeChildren.item(k).getLocalName().equalsIgnoreCase("Topics")){    						
    							//NodeList TopicNodeList = contactNodeChildren.item(k).getChildNodes();
						NodeList TopicNodeList = topicNode.getChildNodes();
						for(int l = 0; l < TopicNodeList.getLength(); l++) {
    						Node Topic = TopicNodeList.item(j);
    						NamedNodeMap topicattr = Topic.getAttributes();
    						Node TopicNode = topicattr.getNamedItem("value");
    						ResearchTopics.addTopic(TopicNode.getTextContent());
    					}
    						//}
    					//}
    					    					
    					Node PictureFileNode = attr.getNamedItem("PictureFile");					
    					PictureFile = PictureFileNode.getTextContent();
    					Node RotationNode = attr.getNamedItem("Rotation");
    					Rotation = Integer.parseInt(RotationNode.getTextContent());
    					Node IDNode = attr.getNamedItem("ID");
    					ID = Integer.parseInt(IDNode.getTextContent());
    					
    					contactList[j]= new Contact(FirstName,LastName,Institute,Organization,
    							EmailAdress,PhoneNumber,MobileNumber,PictureFile,Rotation,ResearchTopics,ID);
    				} else {
    					contactList[j]=null;
    				}
    			}
    			
    	 
    			System.out.println("Done");
    	 
    	   } catch (ParserConfigurationException pce) {
    		pce.printStackTrace();
    	   //} catch (TransformerException tfe) {
    		//tfe.printStackTrace();
    	   } catch (IOException ioe) {
    		ioe.printStackTrace();
    	   } catch (SAXException sae) {
    		sae.printStackTrace();
    	   }
    		return true;
    	} else{
    		return false;
    	}   	
    }
    
    public void saveContacts(String fileFolder){
    	
	    try {
				 
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
	 
			// root elements
			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement("posteritems");
			doc.appendChild(rootElement);
			 
			for(int j = 0; j < 100; j++) {
					if (contactList[j] instanceof Contact){
						// staff elements
						Element contact = doc.createElement("Contact");
						contact.setAttribute("FirstName", contactList[j].getFirstName());
						contact.setAttribute("LastName",contactList[j].getLastName());
						contact.setAttribute("Institute",contactList[j].getInstitute());
						contact.setAttribute("Organization",contactList[j].getOrganization());
						contact.setAttribute("EmailAdress",contactList[j].getEmailAdress());
						contact.setAttribute("PhoneNumber",contactList[j].getPhoneNumber());
						contact.setAttribute("MobileNumber",contactList[j].getMobileNumber());
						contact.setAttribute("PictureFile",contactList[j].getPictureFile());
						contact.setAttribute("Rotation",String.valueOf(contactList[j].getRotation()));
						
						Element topicsElement = doc.createElement("Topics");
    					Topics Conftopics = contactList[j].getResearchTopics();
    					for (int i=0;i<Conftopics.getNumberOfTopics();i++){
    						Element topicElement = doc.createElement("topic");
    						topicElement.setAttribute("value", Conftopics.getTopic(i));
    						topicsElement.appendChild(topicElement);
    					}			
    					rootElement.appendChild(topicsElement);
												
    					contact.setAttribute("ID",String.valueOf(contactList[j].getId()));
						rootElement.appendChild(contact);						
						} 
					}
			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(fileFolder,"contacts.xml"));
	 
			// Output to console for testing
			// StreamResult result = new StreamResult(System.out);
	 
			transformer.transform(source, result);
	 
			System.out.println("File saved!");
	 
		  } catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		  } catch (TransformerException tfe) {
			tfe.printStackTrace();
		  }
    }
    
    public void addItem(Contact contact){
    	TableLayout table_contacts = (TableLayout)findViewById(R.id.table_contacts);
    	View v;
		LayoutInflater li = getLayoutInflater();
		v = li.inflate(R.layout.contact, null);
		TextView tv_contactname = (TextView)v.findViewById(R.id.contact_name);
		tv_contactname.setText(getNameTag(contact,false));
		//TODO: preference for display name
		//tv_contactname.setSingleLine();
		//ImageView iv_poster = (ImageView)v.findViewById(R.id.contact_image);
		v.setId(contact.getId()+1000);
		TableRow tr = new TableRow(this);
		tr.setId(contact.getId()+1000);
		tr.setLayoutParams(new LayoutParams(
    			LayoutParams.FILL_PARENT, 
    			LayoutParams.WRAP_CONTENT));
		tr.setBackgroundDrawable(getResources().getDrawable(android.R.drawable.list_selector_background));
		//iv_poster.setImageResource(posterImagesIds[position]);
		tr.setClickable(true);
		

		tr.setOnClickListener(new View.OnClickListener() {
            
            //@Override
            public void onClick(View v) {
               int rowid;
               rowid = v.getId();
               //String args = Integer.toString(rowid);
               Contact selectedContact = contactList[rowid-1000];
               //tr.setBackgroundColor(Color.GREEN);
               //function to be called when the row is clicked.  
               //TODO:
               //OpenContact(selectedContact);
               //tr.setBackgroundColor(Color.BLACK);
               //v.setBackgroundColor(Color.GRAY);
               //v.showContextMenu();
               //return true;
            }
         });
		
		//show context menu
    	tr.setOnLongClickListener(new View.OnLongClickListener() {
        
            //@Override
            public boolean onLongClick(View v) {
               // TODO Auto-generated method stub
               //v.setBackgroundDrawable(getResources().getDrawable(android.R.drawable.list_selector_background));
               ItemId = v.getId();
               v.showContextMenu();
               return true;
            }
        });
    	
		tr.addView(v);
		numberOfItems++;
		
		table_contacts.addView(tr,new TableLayout.LayoutParams(
                LayoutParams.FILL_PARENT,
                LayoutParams.WRAP_CONTENT));
    }
  
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		
	    
		AdapterContextMenuInfo info =  (AdapterContextMenuInfo) menuInfo;
		//View tile = (View) info.targetView;//This is how I get a grip on the view that got long pressed.
          Contact selectedContact = contactList[ItemId];
          menu.setHeaderTitle(getNameTag(selectedContact,false));
    		  
          MenuInflater inflater=getMenuInflater();
          inflater.inflate(R.menu.main_cm_local, menu);
    }
    
    @Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
			int id = item.getItemId();
			if (id == R.id.EditItem) {
				editItem(contactList[ItemId]);
				return true;
			} else if (id == R.id.DeleteItem) {
				deleteItem(ItemId);
				return true;
			} else {
				return super.onContextItemSelected(item);
			}
	}
    
    public void replaceItem(Contact contactItem,int id){//id = posterid+1 
    	View contact_view = findViewById(2000+contactItem.getId());
    	TextView tv_contactname = (TextView)contact_view.findViewById(R.id.contact_name);
    	tv_contactname.setText(getNameTag(contactItem,false));
    	
    }

    
    private void deleteItem(int id) {
    	TableLayout table_contacts = (TableLayout)findViewById(R.id.table_contacts);
		TableRow row = (TableRow)findViewById(1000+id);
		table_contacts.removeView(row);
		for(int j=id; j<numberOfItems; j++){
			contactList[j]=contactList[j+1];
			if (contactList[j] instanceof Contact){
				contactList[j].setId(j);
			}
			
			try{
				TableRow rowChange = (TableRow)findViewById(j+1001);
				rowChange.setId(1000+j);
				
			}catch(RuntimeException e){
				
			}
			
		}		
		numberOfItems=numberOfItems-1;    	
    	saveContacts(filePath);
	}

	private void editItem(Contact contact) {
		Intent in = new Intent(getApplicationContext(), PosterEditActivity.class);
		requestCode = 3;
		 Bundle extras = new Bundle();
	     extras.putSerializable("conf", Conf);
	     extras.putSerializable("contact", contact);
	     extras.putSerializable("topics", allTopics);
	     in.putExtras(extras);
		startActivityForResult(in,requestCode);		
	}
	
	public Contact[] sortContacts(Contact[] Contacts){
		Contact[] sortedContacts = new Contact[100];
		for(int j = 0; j < numberOfItems; j++) {
			String currentName = getNameTag(Contacts[j],lastnameFirst);
			boolean inserted=false;
			for(int k = 0; k < j; k++) {
				if (currentName.compareTo(getNameTag(Contacts[k],lastnameFirst))>0){
					for(int l = j; l > k; l--) {
						sortedContacts[l]=sortedContacts[l-1];
					}
					sortedContacts[k]=Contacts[j];
					//insertedItems++;
					inserted=true;
					break;
				}
				//insertLine = k;
			}
			if (!inserted){
				sortedContacts[j]=Contacts[j];
				//insertedItems++;
			}			
		}		
		return sortedContacts;
	}
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
      super.onActivityResult(requestCode, resultCode, data);
      
      //Toast.makeText(this, requestCode, Toast.LENGTH_LONG).show();
      switch(requestCode) {
        case 2: {//Activity.RESULT_OK: {
          if (resultCode == 2) {//Activity.RESULT_OK) {
            // TODO Extract the data returned from the child Activity.
        	  //data.getBundleExtra(name)
        	  Bundle extrasContItem = data.getExtras();        	  
        	  contactList[numberOfItems]= (Contact) extrasContItem.getSerializable("contact");
        	  allTopics = (Topics)extrasContItem.getSerializable("topics");
        	  contactList[numberOfItems].setId(numberOfItems+1000);
        	  addItem(contactList[numberOfItems]);
        	  contactList = sortContacts(contactList);
        	  //clearTempBib();
        	  saveContacts(filePath);
        	  //addButton(numberOfItems);  	  
        	  
          }
          break;
        }
        case 3: {
        	if (resultCode ==3) {
        		Bundle extrasContItem = data.getExtras();
        		//error oob bei array vermutung, wrong orientation restarts main resets confid to confid-1, 
        		contactList[ItemId]= (Contact) extrasContItem.getSerializable("contact");
        		allTopics = (Topics)extrasContItem.getSerializable("topics");
        		contactList[ItemId].setId(ItemId);
        		replaceItem(contactList[ItemId],ItemId);
        		//clearTempBib();
        		contactList = sortContacts(contactList);
        		saveContacts(filePath);
	        	//i+=1;
        	}
        	break;
        }
        
        default: {
        	saveContacts(filePath);
        	break;
        }
      }
    }
    
    @Override
	public void onBackPressed() {    	
    	// do something on back.
    	MainActivity.allTopics = allTopics;	
		super.onBackPressed();//finish();
	}
}