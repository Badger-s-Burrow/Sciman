package de.badgersburrow.sciman.conftab;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import de.badgersburrow.sciman.objects.Conference;
import de.badgersburrow.sciman.objects.Poster;
import de.badgersburrow.sciman.R;
import de.badgersburrow.sciman.utilities.VariousMethods;

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


public class PosterViewActivity extends Activity {
	GridView gridView;
	
	// Keep all Images in array
    public Integer[] posterImagesIds = new Integer[100];
    public String[] posterImagesFiles = new String[100];
    private int requestCode =0;
    public String[] posterTexts = new String[100];
    
    public int numberOfItems =0;
    public int Posterid =0;
	private Poster[] posterItems = new Poster[100];
	private String theme_color;
	
	private Conference Conf;
	private String filePath;
	private GridViewAdapter mAdapter = new GridViewAdapter(this); 
	private int PictureWidth = 100;
	private int PictureHeight = 140;
	
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
        setContentView(R.layout.poster_view);

        //setContentView(R.layout.staticscrollview);
        ImageView add_button = (ImageView) findViewById(R.id.add_button);
        add_button.setOnClickListener(new View.OnClickListener() {            
            //@Override
            public void onClick(View v) {
        		Intent in = new Intent(getApplicationContext(), PosterNewActivity.class);
        		requestCode = 2;
        		 Bundle extras = new Bundle();
        	     extras.putSerializable("conf", Conf);
        	     in.putExtras(extras);
        		startActivityForResult(in,requestCode);
            }
         });


        gridView = (GridView) findViewById(R.id.gv_poster);
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
            	 gridView.setColumnWidth(174);
            	 PictureWidth = 174;
            	 PictureHeight = 260;
        	 			break;
        }
        Intent intentFromMenu = super.getIntent();//
        Bundle extras = intentFromMenu.getExtras();
        Conf = (Conference)extras.getSerializable("conf");
        TextView tv_header = (TextView)findViewById(R.id.tv_header);
        tv_header.setText(getString(R.string.pv_header) + Conf.getConfname());
        //addButton(0);
        filePath = VariousMethods.getMainFolder(this)+Conf.getConfFolder() + "/";
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
        gridView.setOnItemClickListener(new OnItemClickListener() {
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



            }
        });

        gridView.setOnItemLongClickListener(new OnItemLongClickListener() {
            //@Override
            public boolean onItemLongClick(AdapterView<?> parent, View v,
                    int position, long id) {
            		Posterid =position;
 	               	//v.showContextMenu();
 	               	return false;



            }
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
    	File posterFile = new File(fileFolder,"poster.xml");
    	if (posterFile.exists()){
    		/*try{
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
    		}    	*/
    		//Poster[] Posteritems = new Poster[100];
    		String Mainauthorfirstname  = "None";
			String Mainauthorlastname = "None";
			String Coauthor="None";
			String Title="None";
			String Abstract = "None";
			String[] References = new String[1];
			References[0] = "None";
			String Imagefile = "None";
			int Rotation = 0;
			int ID = 0;



    		try {

    			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
    			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
    			Document doc = docBuilder.parse(posterFile);

    			NodeList posterlist = doc.getElementsByTagName("poster");
    			for(int j = 0; j < 100; j++) {
    				Node poster = posterlist.item(j);
    				//String test2 = bib.getNodeValue();
    				//new Poster
    				if (poster!=null){
    					Mainauthorfirstname  = "None";
    					Mainauthorlastname = "None";
    					Coauthor="None";
    					Title="None";
    					Abstract = "None";
    					References[0] = "None";
    					Imagefile = "None";
    					Rotation = 0;
    					ID = 0;

    					NamedNodeMap attr = poster.getAttributes();
    					Node MainauthorfirstnameNode = attr.getNamedItem("Mainauthorfirstname");
    					Mainauthorfirstname = MainauthorfirstnameNode.getTextContent();
    					Node MainauthorlastnameNode = attr.getNamedItem("Mainauthorlastname");
    					Mainauthorlastname = MainauthorlastnameNode.getTextContent();
    					Node CoauthorNode = attr.getNamedItem("Coauthor");
    					Coauthor = CoauthorNode.getTextContent();
    					Node TitleNode = attr.getNamedItem("Title");
    					Title = TitleNode.getTextContent();
    					Node AbstractNode = attr.getNamedItem("Abstract");
    					Abstract = AbstractNode.getTextContent();
    					Node ReferencesNode = attr.getNamedItem("References");
    					References[0] = ReferencesNode.getTextContent();
    					Node ImagefileNode = attr.getNamedItem("Imagefile");
    					Imagefile = ImagefileNode.getTextContent();
    					Node RotationNode = attr.getNamedItem("Rotation");
    					Rotation = Integer.parseInt(RotationNode.getTextContent());
    					Node IDNode = attr.getNamedItem("ID");
    					ID = Integer.parseInt(IDNode.getTextContent());

    					posterItems[j]= new Poster(Mainauthorfirstname,Mainauthorlastname,Coauthor,Title,Abstract,References,Imagefile,Rotation,ID);
    				} else {
    					posterItems[j]=null;
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

    public void savePosters(String fileFolder){
    	//String confFolder=Conf.getConfFolder();
		//FileOutputStream fos = null;
    	/*ObjectOutputStream out = null;
    	try {
    		File postercfg = new File(fileFolder,"poster.cfg");
	    	FileOutputStream fos = new FileOutputStream(postercfg);
	       	out = new ObjectOutputStream(fos);
	    	out.writeObject(posterItems);
	    	out.close();
	    	System.out.println("Object Persisted");
    	} catch (IOException ex) {
    		ex.printStackTrace();
    	} catch (NullPointerException ex) {
    		ex.printStackTrace();
    	}*/
    	//String filepath = Environment.getExternalStorageDirectory() + "/androidbib.xml";
	    try {

			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

			// root elements
			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement("posteritems");
			doc.appendChild(rootElement);

			for(int j = 0; j < 100; j++) {
					if (posterItems[j] instanceof Poster){
						// staff elements
						Element poster = doc.createElement("poster");
						poster.setAttribute("Mainauthorfirstname", posterItems[j].getMainauthorfirstname());
						poster.setAttribute("Mainauthorlastname",posterItems[j].getMainauthorlastname());
						poster.setAttribute("Coauthor",posterItems[j].getCoauthor());
						poster.setAttribute("Title",posterItems[j].getTitle());
						poster.setAttribute("Abstract",posterItems[j].getAbstract());
						poster.setAttribute("References",posterItems[j].getReferences()[0]);
						poster.setAttribute("Imagefile",posterItems[j].getImagefile());
						poster.setAttribute("Rotation",String.valueOf(posterItems[j].getRotation()));
						poster.setAttribute("ID",String.valueOf(posterItems[j].getId()));
						rootElement.appendChild(poster);
						}
					}
			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(fileFolder,"poster.xml"));

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

    public void addItem(Poster posterItem,int id){
    	posterTexts[id] = posterItem.getTitle();
    	if (posterItem.getImagefile()!=null){
    		posterImagesFiles[id] = filePath + posterItem.getImagefile();
    		posterImagesIds[id] = 0;
    	}else{
    		posterImagesIds[id] = R.drawable.posterdefault;
    	}

    	numberOfItems++;
    	mAdapter.notifyDataSetChanged();
    }

    public void replaceItem(Poster posterItem,int id){//id = posterid+1
    	posterTexts[id] = posterItem.getTitle();
    	if (posterItem.getImagefile()!=null){
    		posterImagesFiles[id] = filePath + posterItem.getImagefile();
    		posterImagesIds[id] = 0;
    	}else{
    		posterImagesIds[id] = R.drawable.posterdefault;
    	}
    	mAdapter.notifyDataSetChanged();
    }

    public class GridViewAdapter extends BaseAdapter {
        private Context mContext;
        public static final int ACTIVITY_CREATE = 10;


        // Constructor
        public GridViewAdapter(Context c){
            mContext = c;
        }

        //@Override
        /*public int getCount() {
            return mThumbIds.length;
        }*/

        //@Override
    	public int getCount() {
    		// TODO Auto-generated method stub
    		return numberOfItems;
    	}

        //@Override
        /*public Object getItem(int position) {
            return mThumbIds[position];
        }*/

        //@Override
        public long getItemId(int position) {
            return 0;
        }

        //@Override
        public View getView(int position, View convertView, ViewGroup parent) {
        	// TODO Auto-generated method stub
    		View v;
    		if(convertView==null){
    			LayoutInflater li = getLayoutInflater();
    			v = li.inflate(R.layout.poster, null);


    		}
    		else
    		{
    			v = convertView;
    		}
    		TextView tv_poster = (TextView)v.findViewById(R.id.poster_text);
			tv_poster.setText(posterTexts[position]);
			tv_poster.setSingleLine();
			ImageView iv_poster = (ImageView)v.findViewById(R.id.poster_image);
			if (posterImagesIds[position]!=0){
				iv_poster.setImageResource(posterImagesIds[position]);
			} else {
				int rotation = posterItems[position].getRotation();
				if (rotation == 0 || rotation == 180){
					Bitmap bm = decodeSampledBitmapFromUri(posterImagesFiles[position], PictureWidth,PictureHeight);
    				iv_poster.setImageBitmap(bm);
				}else{
					Bitmap bm = decodeSampledBitmapFromUri(posterImagesFiles[position], PictureHeight,PictureWidth);
					Matrix matrix = new Matrix();
					matrix.postRotate(rotation);
					Bitmap rotatedBitmap = Bitmap.createBitmap(bm, 0, 0,
							PictureHeight, PictureWidth, matrix, true);
					iv_poster.setImageBitmap(rotatedBitmap);
				}
			}
    		return v;
        }

        public Bitmap decodeSampledBitmapFromUri(String path, int reqWidth, int reqHeight) {

    	   Bitmap bm = null;
    	   // First decode with inJustDecodeBounds=true to check dimensions
    	   final BitmapFactory.Options options = new BitmapFactory.Options();
    	   options.inJustDecodeBounds = true;
    	   BitmapFactory.decodeFile(path, options);

    	   // Calculate inSampleSize
    	   options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

    	   // Decode bitmap with inSampleSize set
    	   options.inJustDecodeBounds = false;
    	   bm = BitmapFactory.decodeFile(path, options);

    	   return bm;
    	}

    	public int calculateInSampleSize(

    	   BitmapFactory.Options options, int reqWidth, int reqHeight) {
    	   // Raw height and width of image
    	   final int height = options.outHeight;
    	   final int width = options.outWidth;
    	   int inSampleSize = 1;

    	   if (height > reqHeight || width > reqWidth) {
	    	    if (width > height) {
	    	     inSampleSize = Math.round((float)height / (float)reqHeight);
	    	    } else {
	    	     inSampleSize = Math.round((float)width / (float)reqWidth);
	    	    }
    	   }

    	   return inSampleSize;
    	}



		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}
	}

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);


		AdapterContextMenuInfo info =  (AdapterContextMenuInfo) menuInfo;
		//View tile = (View) info.targetView;//This is how I get a grip on the view that got long pressed.
          Poster selectedPoster = posterItems[Posterid];
          menu.setHeaderTitle(selectedPoster.getTitle());
    		  
          MenuInflater inflater=getMenuInflater();
          inflater.inflate(R.menu.main_cm_local, menu);
    }
    
    @Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
			int id = item.getItemId();
			if (id == R.id.EditItem) {
				editPoster(posterItems[Posterid]);
				return true;
			} else if (id == R.id.DeleteItem) {
				deletePoster(Posterid);
				return true;
			} else {
				return super.onContextItemSelected(item);
			}
	}

    
    private void deletePoster(int id) {
    	for (int i=id;i<numberOfItems-1;i++){
    		posterItems[i] = posterItems[i+1];
    		posterTexts[i] = posterItems[i].getTitle();
    		if (posterItems[i].getImagefile()!=null){
        		posterImagesFiles[i] = filePath + posterItems[i].getImagefile();
        		posterImagesIds[i] = 0;
        	}else{
        		posterImagesIds[i] = R.drawable.posterdefault;
        	}    		
    	}
    	posterItems[numberOfItems-1] = null;
    	posterTexts[numberOfItems-1] = null;
    	posterImagesIds[numberOfItems-1] = null;
    	numberOfItems--;
    	savePosters(filePath);
    	mAdapter.notifyDataSetChanged();
	}

	private void editPoster(Poster poster) {
		Intent in = new Intent(getApplicationContext(), PosterEditActivity.class);
		requestCode = 3;
		 Bundle extras = new Bundle();
	     extras.putSerializable("conf", Conf);
	     extras.putSerializable("poster", poster);
	     in.putExtras(extras);
		startActivityForResult(in,requestCode);
		
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
        	  Bundle extrasConfItem = data.getExtras();
        	  
        	  posterItems[numberOfItems]= (Poster) extrasConfItem.getSerializable("poster");
        	  posterItems[numberOfItems].setId(numberOfItems+1000);
        	  addItem(posterItems[numberOfItems],numberOfItems);
        	  //clearTempBib();
        	  savePosters(filePath);
        	  //addButton(numberOfItems);  	  
        	  
          }
          break;
        }
        case 3: {
        	if (resultCode ==3) {
        		Bundle extrasConfItem = data.getExtras();
        		//error oob bei array vermutung, wrong orientation restarts main resets confid to confid-1, 
        		posterItems[Posterid]= (Poster) extrasConfItem.getSerializable("poster");
        		posterItems[Posterid].setId(Posterid);
        		replaceItem(posterItems[Posterid],Posterid);
        		//clearTempBib();
	        	
        		savePosters(filePath);
	        	//i+=1;
        	}
        	break;
        }
        
        default: {
        	savePosters(filePath);
        	break;
        }
      }
    }

  
}