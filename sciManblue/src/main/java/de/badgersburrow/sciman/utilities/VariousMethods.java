package de.badgersburrow.sciman.utilities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;

import com.google.gson.Gson;
import de.badgersburrow.sciman.R;
import de.badgersburrow.sciman.objects.Bib;
import de.badgersburrow.sciman.objects.Conference;
import de.badgersburrow.sciman.preferences.EditFolderPreferencesActivity;
import de.badgersburrow.sciman.objects.Topics;
import de.badgersburrow.sciman.objects.News;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

//import test.androidbib.Bib;

public class VariousMethods
{
	private static int sTheme;
	private static String Custom_pref = "custompref";
	private static String Custom_pref_path = "SavePath";
	private static String Custom_pref_extern = "SavePathExtern";
	private static String Custom_pref_syncmethod = "Syncmethod";
	private static String Custom_pref_manualapp = "manualApp";
	private static String Custom_pref_manualappname = "manualAppName";

    public static int NO_OPTIONS=0;

	public final static int THEME_DEFAULT = 0;
	public final static int THEME_WHITE = 1;
	//public final static int THEME_BLUE = 2;

	/**
	 * Set the theme of the Activity, and restart it by creating a new Activity
	 * of the same type.
	 */
	public static void changeToTheme(Activity activity, int theme)
	{
		sTheme = theme;
		activity.finish();

		activity.startActivity(new Intent(activity, activity.getClass()));
	}

	/** Set the theme of the activity, according to the configuration. */
	public static void onActivityCreateSetTheme(Activity activity)
	{
		switch (sTheme)
		{
		default:
		case THEME_DEFAULT:
			activity.setTheme(android.R.style.Theme_Black);
			break;
		case THEME_WHITE:
			activity.setTheme(android.R.style.Theme_Light);
			break;
		/*case THEME_BLUE:
			activity.setTheme(R.style.Theme_Blue);
			break;*/
		}
	}

	public static String getSource(Activity act){
		String selectedSource;
		SharedPreferences confpref = act.getSharedPreferences(Custom_pref, 0);
		selectedSource= confpref.getString(Custom_pref_syncmethod,"local");
		return selectedSource;
	}

	public static boolean checkDirectory(String directory){
		return !(directory.contains("/") || directory.contains("\\") || directory.contains("?") || directory.contains("%")
				|| directory.contains("*") || directory.contains(":") || directory.contains("<") || directory.contains(">")
				|| directory.contains(".") || directory.contains("#") || directory.contains("(") || directory.contains(")")
				|| directory.contains("\"") || directory.contains("|") || directory.contains(";") || directory.contains("&")
				|| directory.equalsIgnoreCase(""));

	}

	public static String getDirectoriable(String directory){
		if (directory.contains("/")) {
			return directory.replace("/", "_");
		}
		if (directory.contains("\\")) {
			return directory.replace("\\", "_");
		}
		if (directory.contains("?")) {
			return directory.replace("?", "_");
		}
		if (directory.contains("%")) {
			return directory.replace("%", "_");
		}
		if (directory.contains("*")) {
			return directory.replace("*", "_");
		}
		if (directory.contains(":")) {
			return directory.replace(":", "_");
		}
		if (directory.contains("<")) {
			return directory.replace("<", "_");
		}
		if (directory.contains(">")) {
			return directory.replace(">", "_");
		}
		if (directory.contains(".")) {
			return directory.replace(".", "_");
		}
		if (directory.contains("#")) {
			return directory.replace("#", "_");
		}
		if (directory.contains("(")) {
			return directory.replace("(", "_");
		}
		if (directory.contains(")")) {
			return directory.replace(")", "_");
		}
		if (directory.contains("\"")) {
			return directory.replace("\"", "_");
		}
		if (directory.contains("|")) {
			return directory.replace("|", "_");
		}
		if (directory.contains(";")) {
			return directory.replace(";", "_");
		}
		if (directory.contains("&")) {
			return directory.replace("&", "_");
		}
		return directory;

	}

	public static void copy(File src, File dst) throws IOException {
		InputStream in = new FileInputStream(src);
		OutputStream out = new FileOutputStream(dst);
		byte[] buf = new byte[1024];
		int len;
		while (( len = in.read(buf)) >0){
			out.write(buf,0,len);
		}
		in.close();
		out.close();
	}

	private static int getExifOrientation(String mImagePath){
		ExifInterface exif;
		int orientation = 0;
		try{
			exif = new ExifInterface(mImagePath);
			orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
		} catch (IOException e){
			e.printStackTrace();
		}
		Log.d("orient log", "got orientation " + orientation );
		return orientation;
	}
	public static int getBitmapRotation(String mImagePath) {
		int rotation=0;
		switch ( getExifOrientation(mImagePath)){
		case 3:
			rotation = 180;
			break;
		case 6:
			rotation = 90;
			break;
		case 8:
			rotation = 270;
			break;
		}
		return rotation;
	}

	public static boolean isExternalAvaible(){
		String state = Environment.getExternalStorageState();

		if (Environment.MEDIA_MOUNTED.equals(state)) {
		    // We can read and write the media
		    return true;
		} else // We can only read the media
// Something else is wrong. It may be one of many other states, but all we need
//  to know is we can neither read nor write
			return Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
	}

	public static boolean isExternalWritable(){
		String state = Environment.getExternalStorageState();

		if (Environment.MEDIA_MOUNTED.equals(state)) {
		    // We can read and write the media
		    return true;
		} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
		    // We can only read the media

		    return false;
		} else {
		    // Something else is wrong. It may be one of many other states, but all we need
		    //  to know is we can neither read nor write
			return false;
		}
	}

	public static ArrayList<Bib> readBibliographies(Activity act)
	{ //this routine should be used generally
		ArrayList<Bib> Bibitems = new ArrayList<Bib>();
		if (checkPrefs(act)) {
			try{
	    		FileInputStream fis = null;
		    	ObjectInputStream in = null;
		    	fis = act.openFileInput("sciManBib.dat");
		    	in = new ObjectInputStream(fis);
		    	Bibitems = (ArrayList<Bib>) in.readObject();
		      	System.out.println("Object Read");
		    	in.close();
	    	} catch (IOException ex) {
	    		ex.printStackTrace();
	    	} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			/*if (Bibitems==null){
				Bibitems=recoverBibs(act);
			}*/
		}
		return Bibitems;
	}

	/*
	public static ArrayList<Bib> recoverBibs(Activity act)
	{
		ArrayList<Bib> Bibitems = new ArrayList<Bib>();
		String Bibname  = "None";
		String Source = "None";
		String manualApp="None";
		String manualAppName="None";
		String Comment = "None";
		boolean extern = false;
		String File = "None";
		String Pdfdirectory = "None";
		String Background = "None";
		boolean Chose_custom=false;
		String Custompath="None";
		int ID = 0;
		String searchText = "";
		int sortColumn = 4;
		boolean linked=false;
		boolean isFull=false;
		int itemsCounter=0;
		boolean isRecentBib=false;
		int maxItems=10000;

		//File confMainDirectory = new File();
		String filepath = getMainFolder(act) + "sciManBib.xml";
		File fXmlFile = new File(filepath);
		try {

			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(fXmlFile);
			//doc.
			// Get the root element
			//Node bibnodes = doc.getElementsByTagName("bibitems").item(0);//getFirstChild();


			//String test = bibitems.getNodeValue();
			// Get the staff element , it may not working if tag has spaces, or
			// whatever weird characters in front...it's better to use
			// getElementsByTagName() to get it directly.
			// Node staff = company.getFirstChild();
			NodeList biblist = doc.getElementsByTagName("bib");
			biblist.getLength();
			for(int j = 0; j < 100; j++) {
				Node bib = biblist.item(j);
				//String test2 = bib.getNodeValue();
				if (bib!=null){
					Bibname  = "None";
					Source = "None";
					manualApp="None";
					manualAppName="None";
					Comment = "None";
					extern = false;
					File = "None";
					Pdfdirectory = "None";
					Background = "None";
					Chose_custom=false;
					Custompath="None";
					ID = 0;
					searchText = "";
					sortColumn = 4;
					linked=false;
					itemsCounter=0;
					isFull=false;
					isRecentBib=false;
					maxItems=10000;
					NamedNodeMap attr = bib.getAttributes();
					Node BibnameNode = attr.getNamedItem("Bibname");
					Bibname = BibnameNode.getTextContent();
					Node CommentNode = attr.getNamedItem("Comment");
					Comment = CommentNode.getTextContent();
					Node FileNode = attr.getNamedItem("File");
					File = FileNode.getTextContent();
					Node externNode = attr.getNamedItem("extern");
					extern = Boolean.parseBoolean(externNode.getTextContent());
					Node IDNode = attr.getNamedItem("ID");
					ID = Integer.parseInt(IDNode.getTextContent());
					Node PdfdirectoryNode = attr.getNamedItem("Pdfdirectory");
					Pdfdirectory = PdfdirectoryNode.getTextContent();
					Node BackgroundNode = attr.getNamedItem("Background");
					Background = BackgroundNode.getTextContent();
					Node searchTextNode = attr.getNamedItem("searchText");
					searchText = searchTextNode.getTextContent();
					Node sortColumnNode = attr.getNamedItem("sortColumn");
					sortColumn = Integer.parseInt(sortColumnNode.getTextContent());
					Node chose_customNode = attr.getNamedItem("chose_custom");
					Chose_custom = Boolean.parseBoolean(chose_customNode.getTextContent());
					Node custompathNode = attr.getNamedItem("custompath");
					Custompath = custompathNode.getTextContent();
					Node sourceNode = attr.getNamedItem("source");
					Source = sourceNode.getTextContent();
					Node linkedNode = attr.getNamedItem("linked");
					linked = Boolean.parseBoolean(linkedNode.getTextContent());
					Node itemsCounterNode = attr.getNamedItem("itemsCounter");
					itemsCounter = Integer.parseInt(itemsCounterNode.getTextContent());
					//Node isRecentBibNode = attr.getNamedItem("isRecentBib");
					//isRecentBib = Boolean.parseBoolean(isRecentBibNode.getTextContent());
					Node isFullNode = attr.getNamedItem("isFull");
					isFull = Boolean.parseBoolean(isFullNode.getTextContent());
					Node maxItemsNode = attr.getNamedItem("maxItems");
					maxItems = Integer.parseInt(maxItemsNode.getTextContent());
					Node manualAppNode = attr.getNamedItem("manualApp");
					manualApp = manualAppNode.getTextContent();
					Node manualAppNameNode = attr.getNamedItem("manualAppName");
					manualAppName = manualAppNameNode.getTextContent();
					Bibitems.add(new Bib(Bibname,Source,manualApp,manualAppName,Comment,extern,File,Pdfdirectory,Background,Chose_custom,Custompath,ID));
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

		return Bibitems;
	}*/

	/*public static String convToString(boolean variable){
		return String.valueOf(value)variable
	}*/

	public static void saveBibliographies(ArrayList<Bib> Bibitems, Activity act)
	{ //this routine should be used generally
		FileOutputStream fos = null;
    	ObjectOutputStream out = null;
    	try {
	    	fos = act.openFileOutput("sciManBib.dat", Context.MODE_PRIVATE);
	    	out = new ObjectOutputStream(fos);
	    	out.writeObject(Bibitems);
	    	out.close();
	    	System.out.println("Object Persisted");
    	} catch (IOException ex) {
    		ex.printStackTrace();
    	} catch (NullPointerException ex) {
    		ex.printStackTrace();
    	}
    	/*//File confMainDirectory = new File(getMainFolder(act));
		String filepath = getMainFolder(act) + "sciManBib.xml";
	    try {

			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

			// root elements
			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement("bibitems");
			doc.appendChild(rootElement);

			for (Bib Bibitem :Bibitems) {
					// staff elements
					Element bib = doc.createElement("bib");


					// set attribute to staff element
					//Attr attr = doc.createAttribute("id");
					//attr.setValue(String.valueOf(j));
					//bib.setAttributeNode(attr);

					// shorten way
					// staff.setAttribute("id", "1");

					// Bibname elements
					//Attr Bibname = doc.createAttribute("Bibname");
					//Bibname.appendChild(doc.createTextNode());
					bib.setAttribute("Bibname", Bibitem.getBibname());

					// Comment elements
					bib.setAttribute("Comment",Bibitem.getComment());

					// File elements
					bib.setAttribute("File",Bibitem.getFile());

					// extern elements
					bib.setAttribute("extern",String.valueOf(Bibitem.getExtern()));

					// ID elements
					bib.setAttribute("ID",String.valueOf(Bibitem.getID()));

					// extern elements
					bib.setAttribute("Pdfdirectory",Bibitem.getPdfdirectory());

					// Background elements
					bib.setAttribute("Background",Bibitem.getBackground());

					// searchText elements
					bib.setAttribute("searchText",Bibitem.getSearchText());

					// extern elements
					bib.setAttribute("sortColumn",String.valueOf(Bibitem.getSortColumn()));

					// chose_custom elements
					bib.setAttribute("chose_custom",String.valueOf(Bibitem.getChose_custom()));

					// custompath elements
					bib.setAttribute("custompath",Bibitem.getCustompath());

					// extern elements
					bib.setAttribute("linked",String.valueOf(Bibitem.getLinked()));

					// extern elements
					bib.setAttribute("itemsCounter",String.valueOf(Bibitem.getItemsCounter()));

					// extern elements
					//bib.appendChild(isRecentBib);

					// extern elements
					bib.setAttribute("isFull",String.valueOf(Bibitem.getIsFull()));

					// maxItems elements
					bib.setAttribute("maxItems",String.valueOf(Bibitem.getMaxItems()));

					// source elements
					bib.setAttribute("source",Bibitem.getSource());

					// manualApp elements
					bib.setAttribute("manualApp",Bibitem.getManualApp());

					// manualAppName elements
					bib.setAttribute("manualAppName",Bibitem.getManualAppName());
					rootElement.appendChild(bib);


					}
			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(filepath));

			// Output to console for testing
			// StreamResult result = new StreamResult(System.out);

			transformer.transform(source, result);

			System.out.println("File saved!");

		  } catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		  } catch (TransformerException tfe) {
			tfe.printStackTrace();
		  }*/

	}

	public static Topics readTopics(Activity act)
	{
		Topics allTopics = new Topics();
		String topicvalue;

		//File confMainDirectory = new File();
		String filepath = getMainFolder(act) + "topics.xml";
		File fXmlFile = new File(filepath);

		try {

			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(fXmlFile);

			NodeList topiclist = doc.getElementsByTagName("topic");
			for(int j = 0; j < topiclist.getLength(); j++) {
				Node topic = topiclist.item(j);
				//String test2 = bib.getNodeValue();
				if (topic!=null){
					topicvalue  = "None";
					NamedNodeMap attr = topic.getAttributes();
					Node TopicNode = attr.getNamedItem("value");
					topicvalue = TopicNode.getTextContent();
					allTopics.addTopic(topicvalue);
				} else {

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

		//Bib Bibitem=(Bib) new Bib(Bibname,Source,manualApp,manualAppName,Comment,extern,File,Pdfdirectory,Background,Chose_custom,Custompath,ID);

		return allTopics;
	}

	/*public static String convToString(boolean variable){
		return String.valueOf(value)variable
	}*/

	public static void saveTopics(Topics allTopics, Activity act)
	{ //this routine should be used generally
    	//File confMainDirectory = new File(getMainFolder(act));
		String filepath = getMainFolder(act) + "topics.xml";
	    try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

			// root elements
			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement("topics");
			doc.appendChild(rootElement);

			for(int j = 0; j < allTopics.getNumberOfTopics(); j++) {
				// staff elements
				Element topic = doc.createElement("topic");
				topic.setAttribute("value", allTopics.getTopic(j));
				rootElement.appendChild(topic);
			}
			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(filepath));

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



    public static Conference readConference(String conferencePath)
    {
        Conference Conf = null;

        try{
            File confcfg = new File(conferencePath,"conference.gson");

            FileInputStream fis = new FileInputStream(confcfg);
            ObjectInputStream in = new ObjectInputStream(fis);
            String sb = (String) in.readObject();
            System.out.println("Object Read");
            in.close();
            /*InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader bufferedReader = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }*/

            String json = sb.toString();
            Gson gson = new Gson();
            Conf = gson.fromJson(json, Conference.class);

        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        /*
        File confcfg = new File(conferencePath,"conference.xml");
        try {

            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(confcfg);
            Node Confnode = doc.getElementsByTagName("conf").item(0);

            if (Confnode!=null){
                String confname  = "None";
                String location = "None";
                int day=1;
                int month =1;
                int year = 1900;
                Topics topics = new Topics();

                String comment = "None";
                boolean showAtPoster = true;
                boolean showAtContacts = true;
                int id = 0;

                NamedNodeMap attr = Confnode.getAttributes();
                Node ConfnameNode = attr.getNamedItem("Confname");
                confname = ConfnameNode.getTextContent();
                Node LocationNode = attr.getNamedItem("Location");
                location = LocationNode.getTextContent();
                Node DayNode = attr.getNamedItem("Day");
                day = Integer.parseInt(DayNode.getTextContent());
                Node MonthNode = attr.getNamedItem("Month");
                month = Integer.parseInt(MonthNode.getTextContent());
                Node YearNode = attr.getNamedItem("Year");
                year = Integer.parseInt(YearNode.getTextContent());
                NodeList TopicNodeList = doc.getElementsByTagName("topic");
                Topics Conftopics = new Topics();
                for(int j = 0; j < TopicNodeList.getLength(); j++) {
                    Node Topic = TopicNodeList.item(j);
                    NamedNodeMap topicattr = Topic.getAttributes();
                    Node TopicNode = topicattr.getNamedItem("value");
                    Conftopics.addTopic(TopicNode.getTextContent());
                }
                Node CommentNode = attr.getNamedItem("Comment");
                comment = CommentNode.getTextContent();
                Node ShowAtPosterNode = attr.getNamedItem("ShowAtPoster");
                showAtPoster = Boolean.parseBoolean(ShowAtPosterNode.getTextContent());
                Node ShowAtContactsNode = attr.getNamedItem("ShowAtContacts");
                showAtContacts = Boolean.parseBoolean(ShowAtContactsNode.getTextContent());

                Node IDNode = attr.getNamedItem("ID");
                id = Integer.parseInt(IDNode.getTextContent());

                Conf=new Conference(confname,location,day,month,year,Conftopics,comment,showAtPoster,showAtContacts,id);
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
        }*/
        return Conf;
    }

    public static ArrayList<Conference> readConferences(Activity act)
    {   //this routine should be used generally
        ArrayList<Conference> Confitems = new ArrayList<Conference>();

        if (checkPrefs(act)) {
			int count = 0;
            File confMainDirectory = new File(getMainFolder(act));
            File[] fileList= confMainDirectory.listFiles();
            for (int i=0;i<fileList.length;i++){
                if (fileList[i].isDirectory()){
                    Conference Confitem = readConference(fileList[i].getAbsolutePath());
                    Confitem.setID(count);
                    Confitems.add(Confitem);
                    count++;
                }
            }
        }
        return Confitems;
    }

    public static ArrayList<Conference> readConferences(String mainConfFolder)
    {   //this routine should be used generally
        ArrayList<Conference> Confitems = new ArrayList<Conference>();
		int count = 0;
        File confMainDirectory = new File(mainConfFolder);
        File[] fileList= confMainDirectory.listFiles();
        for (int i=0;i<fileList.length;i++){
            if (fileList[i].isDirectory()){
				Conference Confitem = readConference(fileList[i].getAbsolutePath());
				Confitem.setID(count);
                Confitems.add(Confitem);
				count++;
            }
        }
        return Confitems;
    }

    public static ArrayList<News> readNews(Activity act)
    { //this routine should be used generally
        ArrayList<News> Newsitems = new ArrayList<News>();
        if (checkPrefs(act)) {
            try{
                FileInputStream fis = null;
                ObjectInputStream in = null;
                fis = act.openFileInput("sciManNews.dat");
                in = new ObjectInputStream(fis);
                Newsitems = (ArrayList<News>) in.readObject();
                System.out.println("Object Read");
                in.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
			/*if (Bibitems==null){
				Bibitems=recoverBibs(act);
			}*/
        }
        return Newsitems;
    }

    public static void saveNews(ArrayList<News> Newsitems, Activity act) { //this routine should be used generally
        FileOutputStream fos = null;
        ObjectOutputStream out = null;
        try {
            fos = act.openFileOutput("sciManNews.dat", Context.MODE_PRIVATE);
            out = new ObjectOutputStream(fos);
            out.writeObject(Newsitems);
            out.close();
            System.out.println("Object Persisted");
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }

	public static boolean checkPrefs(Activity act){
		SharedPreferences sp = act.getSharedPreferences(Custom_pref, 0);
		//Editor edit = confpref.edit();
		//String confSavePath = confpref.getString(Conferences_pref_path,null);
		//SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(act);
        String savePath = sp.getString(Custom_pref_path, null);
		//boolean confSavePathExtern = confpref.getBoolean(Conferences_pref_extern,false);
		return (savePath != null) && (!savePath.equalsIgnoreCase(""));
	}

	public static void prefDialog(final Activity act){
		//show dialog custom vs. default
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(act);
        alertDialog.setTitle(act.getString(R.string.main_settings));
        alertDialog.setMessage(act.getString(R.string.main_settingsmessg));
        alertDialog.setPositiveButton(act.getString(R.string.default_string), new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface arg0, int arg1) {
				// do something when the Default button is clicked
				setDefaultPref(act);
				createMainFolder(act);
				//Intent newConfIn = new Intent(act, NewConfActivity.class);
				//	int requestCode = 2;//Activity.RESULT_OK;
				//act.startActivityForResult(newConfIn,requestCode);
			}
		});
        alertDialog.setNegativeButton(act.getString(R.string.custom), new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface arg0, int arg1) {
				// do something when the Manual button is clicked
				act.startActivity(new Intent(act, EditFolderPreferencesActivity.class));
				//Intent confPrefIn = new Intent(act, ConfPrefActivity.class);
				//int requestCode = 4;
				//act.startActivityForResult(confPrefIn,requestCode);
			}
		});
        alertDialog.show();
	}

	public static void setDefaultPref(Activity act){
		//SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(act);
        //String savePath = sp.getString("pref_savepath",null);
		SharedPreferences sp = act.getSharedPreferences(Custom_pref, 0);
		Editor edit = sp.edit();
		edit.putString(Custom_pref_path, "/" + act.getString(de.badgersburrow.sciman.R.string.app_name)+"/");
		//check if sdcard is available
		if (!isExternalAvaible()){
			edit.putBoolean(Custom_pref_extern, false);
        } else{
        	edit.putBoolean(Custom_pref_extern, true);
        }
        edit.putString(Custom_pref_syncmethod, "local");
        edit.putString(Custom_pref_manualapp, null);//getResources().getString(R.string.none));
        edit.putString(Custom_pref_manualappname, null);//getResources().getString(R.string.none));
		edit.commit();
	}

	public static String getMainFolder(Activity act){
		String preFix="";
		String sufFix="";
		//SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(act);
		SharedPreferences sp = act.getSharedPreferences(Custom_pref, 0);
		Editor edit = sp.edit();
		String confSavePath = sp.getString(Custom_pref_path,"/" + act.getString(R.string.app_name)+"/");
        if (!confSavePath.startsWith("/")){
			preFix = "/";
		}
		if (!confSavePath.endsWith("/")){
			sufFix = "/";
		}
		boolean confSavePathExtern = sp.getBoolean(Custom_pref_extern,false);
		if (confSavePathExtern){
			return Environment.getExternalStorageDirectory() + preFix + confSavePath + sufFix;
		} else{
			return preFix + confSavePath + sufFix;
		}
	}

	public static void createMainFolder(Activity act){
		File confMainDirectory = new File(getMainFolder(act));
		confMainDirectory.mkdirs();
	}

	public static boolean changeMainFolder(Activity act, String newName, boolean newExtern){
		String preFixOld="";
		String sufFixOld="";
		String preFixNew="";
		String sufFixNew="";
		File oldFile;
		File newFile;

		SharedPreferences confpref = act.getSharedPreferences(Custom_pref, 0);
		Editor edit = confpref.edit();
		String confSavePath = confpref.getString(Custom_pref_path,null);
		if (!confSavePath.startsWith("/")){
			preFixOld = "/";
		}
		if (!confSavePath.endsWith("/")){
			sufFixOld = "/";
		}
		if (!newName.startsWith("/")){
			preFixNew = "/";
		}
		if (!newName.endsWith("/")){
			sufFixNew = "/";
		}
		boolean oldExtern = confpref.getBoolean(Custom_pref_extern, false);
		if (oldExtern){
			oldFile = new File(Environment.getExternalStorageDirectory() + preFixOld + confSavePath + sufFixOld);
		} else{
			oldFile = new File(preFixOld + confSavePath + sufFixOld);
		}
		if (newExtern){
			newFile = new File(Environment.getExternalStorageDirectory() + preFixNew + newName + sufFixNew);
		} else{
			newFile = new File(preFixNew + newName + sufFixNew);
		}


		boolean success = oldFile.renameTo(newFile);
		return success;

	}

	public static void createConfFolder(Activity act, Conference Conf){
		String confFolder=Conf.getConfFolder();
		File confDirectory = new File(getMainFolder(act)+confFolder+"/");
		confDirectory.mkdirs();
	}

	public static boolean changeConfFolder(Activity act,String oldName, String newName){
		File oldFile;
		File newFile;

		oldFile = new File(getMainFolder(act) + oldName +"/");
		newFile = new File(getMainFolder(act) + newName +"/");

		boolean success = oldFile.renameTo(newFile);
		return success;
	}

	public static void saveConference(Conference Conf, Activity act)
	{ //this routine should be used generally
		//check if folder was defined
		String confFolder=Conf.getConfFolder();

        Gson gson = new Gson();
        String outputString = gson.toJson(Conf);

     	ObjectOutputStream out = null;
    	try {
    		File confcfg = new File(getMainFolder(act)+confFolder,"conference.gson");
	    	FileOutputStream fos = new FileOutputStream(confcfg);
	       	out = new ObjectOutputStream(fos);
	    	out.writeObject(outputString);
	    	out.close();
	    	System.out.println("Object Persisted");
    	} catch (IOException ex) {
    		ex.printStackTrace();
    	} catch (NullPointerException ex) {
    		ex.printStackTrace();
    	}
		/*
		//String filepath = getMainConfFolder(act)+confFolder + "/conference.xml";
	    try {

			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

			// root elements
			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement("conf");
			doc.appendChild(rootElement);

			rootElement.setAttribute("Confname", Conf.getConfname());
			rootElement.setAttribute("Location",Conf.getLocation());
			rootElement.setAttribute("Day",String.valueOf(Conf.getDay()));
			rootElement.setAttribute("Month",String.valueOf(Conf.getMonth()));
			rootElement.setAttribute("Year",String.valueOf(Conf.getYear()));
			Element topicsElement = doc.createElement("Topics");
			Topics Conftopics = Conf.getTopics();
			for (int i=0;i<Conftopics.getNumberOfTopics();i++){
				Element topicElement = doc.createElement("topic");
				topicElement.setAttribute("value", Conftopics.getTopic(i));
				topicsElement.appendChild(topicElement);
			}
			rootElement.appendChild(topicsElement);
			//rootElement.setAttribute("Topics",Conf.getTopics().getTopic(0));
			//TODO
			rootElement.setAttribute("Comment",Conf.getComment());
			rootElement.setAttribute("ShowAtPoster",String.valueOf(Conf.getShowAtPoster()));
			rootElement.setAttribute("ShowAtContacts",String.valueOf(Conf.getShowAtContacts()));
			rootElement.setAttribute("ID",String.valueOf(Conf.getID()));


			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(getMainFolder(act)+confFolder,"conference.xml"));

			// Output to console for testing
			// StreamResult result = new StreamResult(System.out);

			transformer.transform(source, result);

			System.out.println("File saved!");

		  } catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		  } catch (TransformerException tfe) {
			tfe.printStackTrace();
		  }*/
	}

	public static void saveConferences(ArrayList<Conference> Confitems, Activity act)
	{ //this routine should be used generally
		//get configuration
        int len = Confitems.size();
        for (int i=0;i<len;i++){
            saveConference(Confitems.get(i), act);
        }
	}

	public static Map<String,Conference> getConfDict(ArrayList<Conference> Confitems)
	{
        Map<String,Conference> ConfitemsDict = new HashMap<String,Conference>();

        int length = Confitems.size();
        Conference Confitem;
        for (int i=0;i<length;i++){
            Confitem = Confitems.get(i);
            if (Confitem.getIdentifier() != null){
                ConfitemsDict.put(Confitem.getIdentifier(),Confitem);
            }

        }


		return ConfitemsDict;
	}

    public static void deleteConference(Activity act, Conference Conf)
    { //this routine should be used generally
        String confFolder=Conf.getConfFolder();
        //FileOutputStream fos = null;
        try {
            File confFolderFile = new File(getMainFolder(act),confFolder);
            String[] children = confFolderFile.list();
            for (int i =0;i <children.length;i++){
                new File (confFolderFile,children[i]).delete();
            }
            confFolderFile.delete();
            System.out.println("Object Persisted");
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }



    public static String encodeTobase64(Bitmap image)
    {
        Bitmap immagex=image;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        immagex.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        String imageEncoded = Base64.encodeToString(b,Base64.DEFAULT);

        Log.e("LOOK", imageEncoded);
        return imageEncoded;
    }
    public static Bitmap decodeBase64(String input)
    {
        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }



    public static Map<String,Integer> getTopicDict(Activity act) {
		TypedArray myImageRes = act.getResources().obtainTypedArray(R.array.img_id_arr_topics_values);

		int len = myImageRes.length();

		int[] values = new int[len];


		for (int i = 0; i < len; i++){
			values[i] = myImageRes.getResourceId(i, 0);
		}
		myImageRes.recycle();

		String[] keys = act.getResources().getStringArray(R.array.img_id_arr_topics_keys);

		Map<String,Integer> dict = new HashMap<String,Integer>();
		for (int i = 0, l = keys.length; i < l; i++) {
		    dict.put(keys[i], values[i]);
		}

		return dict;
	}

	public static Map<String,Integer> getBibitemTypeDict(Activity act) {
		TypedArray myImageRes = act.getResources().obtainTypedArray(R.array.img_id_arr_types_values);

		int len = myImageRes.length();

		int[] values = new int[len];

		for (int i = 0; i < len; i++){
			values[i] = myImageRes.getResourceId(i, 0);
		}
		myImageRes.recycle();

		String[] keys = act.getResources().getStringArray(R.array.img_id_arr_types_keys);

		Map<String,Integer> dict = new HashMap<String,Integer>();
		for (int i = 0, l = keys.length; i < l; i++) {
			dict.put(keys[i], values[i]);
		}

		return dict;
	}

	public static String ColorLuminance(String hex, double lum) {


		// convert to decimal and change luminosity
		String rgb = "#";
        String sub;
		String temp;
		Long c;
		for (int i = 0; i < 3; i++) {
            sub = hex.substring(i * 2+1, i * 2 + 3);
			c = Long.parseLong(sub, 16);
			temp = Long.toHexString(Math.round(Math.min(Math.max(0, c + (c * lum)), 255)));
			rgb += ("00"+temp).substring(temp.length());
		}

		return rgb;
	}

	public static String ColorComplementary(String hex) {
		// convert to decimal and change to complementary color
		String rgb = "#";
		String temp;
		Long c;
		for (int i = 0; i < 3; i++) {
            c = Long.parseLong(hex.substring(i * 2+1, i * 2 + 3), 16);
            temp = Long.toString(255 - c);
			rgb += ("00"+temp).substring(temp.length());
		}

		return rgb;
	}

    public static String randomString(int length) {
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        char tempChar;
        for (int i = 0; i < length; i++){
            tempChar = (char) (generator.nextInt(26) + 97);
            randomStringBuilder.append(tempChar);
        }
        return randomStringBuilder.toString();
    }


    private static String convertToHex(byte[] data) throws java.io.IOException
    {


        StringBuffer sb = new StringBuffer();
        String hex=null;

        hex=Base64.encodeToString(data, 0, data.length, NO_OPTIONS);

        sb.append(hex);

        return sb.toString();
    }


	public static String computeSHAHash(String password)
	{
		MessageDigest mdSha1 = null;
        String SHAHash = null;
		try
		{
			mdSha1 = MessageDigest.getInstance("SHA-1");
		} catch (NoSuchAlgorithmException e1) {
			Log.e("myapp", "Error initializing SHA1 message digest");
		}
		try {
			mdSha1.update(password.getBytes("ASCII"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		byte[] data = mdSha1.digest();
		try {
			SHAHash=convertToHex(data);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return SHAHash;
	}




}