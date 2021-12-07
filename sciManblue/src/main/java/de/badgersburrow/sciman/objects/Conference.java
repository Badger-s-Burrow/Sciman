package de.badgersburrow.sciman.objects;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import de.badgersburrow.sciman.utilities.VariousMethods;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;



@SuppressWarnings("serial") //with this annotation we are going to hide compiler warning
public class Conference implements Serializable{
	
	private String name;
	private String comment;
	private int[] startDate = new int[3];
    private int numDays;
	private String location; //means real place
	private Topics topics;
	private boolean showAtPoster;
	private boolean showAtContacts;

	private int ID;
	//private String savepath; //for poster pic files
	//private boolean extern;
	private String searchText;
	private int sortColumn;


	private boolean linked;
	private ArrayList<item> items = new ArrayList<item>();
	private int itemsCounter;
	private boolean isRecentConf;
	private boolean isFull;
	private int maxItems;
	private String folder;
	//private Bitmap custombitmap;

	//extend class for remote sources
	private String source;//local or dropbox or manualApp
	private String manualApp;
	private String manualAppName;

	private String colorPrimary;
	private String colorPrimaryDark;
	private String colorSecondary;
    private String colorSecondaryLight;
    private String colorAccent;

    private String confId;
    private String icon;
    private String headerBitmapFile;
    private boolean headerOnServer;
    private boolean headerLocal;



	public Conference(String Confname, String Location, int[] StartDate,int NumDays, Topics Topics, String Comment,
							boolean ShowAtPoster, boolean ShowAtContacts, int ID)
	{
		this.name = Confname;
		this.location = Location;
		this.comment = Comment;
		this.startDate = StartDate;
        this.numDays = NumDays;
		this.topics=Topics;
		this.showAtPoster=ShowAtPoster;
		this.showAtContacts=ShowAtContacts;
		this.ID = ID;
		this.searchText = "";
		this.sortColumn = 4;
		this.linked=false;
		this.isFull=false;
		this.itemsCounter=0;
		this.isRecentConf=false;
		this.maxItems=10000;
        this.setConfFolder();
        this.colorPrimary     = "#0000CC";
        this.colorPrimaryDark = "#000055";
        this.colorSecondary   = "#E8F4F8";
        this.colorSecondaryLight = "#FFFFFF";
        this.colorAccent      = "#CCCC00";

        this.confId           = null;
        this.headerOnServer   = false;
        this.headerBitmapFile = null;
        this.headerLocal      = false;

	}


	//getter Methoden
    public String getConfname(){
        return name;
    }
    public String getConfFolder(){
    	return folder;
    }

    public String getComment(){
        return comment;
    }
    public int getDay(){
        return startDate[2];
    }
    public int getMonth(){
        return startDate[1];
    }
    public int getYear(){
        return startDate[0];
    }
    public int getNumDays() { return numDays; }
    public String getDate(){
    	return String.valueOf(startDate[0])+"/"+String.valueOf(startDate[1])+"/"+String.valueOf(startDate[2]);
    }
    public String getLocation(){
        return location;
    }

    public Topics getTopics(){
        return topics;
    }
    public int getID(){
        return ID;
    }
    public int getSortColumn(){
    	return sortColumn;
    }
    public String getSearchText(){
    	return searchText;
    }
    /*public String getSavepath(){
    	return savepath;
    }
    public boolean getExtern(){
    	return extern;
    }*/
    public boolean getShowAtPoster(){
    	return showAtPoster;
    }
    public boolean getShowAtContacts(){
    	return showAtContacts;
    }
    public String getSource(){
    	return source;
    }
    public boolean getLinked(){
    	return linked;
    }
    public String getManualApp(){
    	return manualApp;
    }
    public String getManualAppName(){
    	return manualAppName;
    }
    public ArrayList<item> getItems(){
    	return items;
    }
    public boolean getIsFull(){
    	return isFull;
    }
    public int getMaxItems(){
    	return maxItems;
    }
    public int getItemsCounter(){
    	return itemsCounter;
    }
    public String getColorPrimary() {
        return this.colorPrimary;
    }
    public String getColorPrimaryDark() {
        return this.colorPrimaryDark;
    }
    public String getColorSecondary() {
        return this.colorSecondary;
    }
    public String getColorSecondaryLight() {
        return this.colorSecondaryLight;
    }
    public String getColorAccent() {
        return this.colorAccent;
    }

    public String getIdentifier() {return String.valueOf(this.confId);}
    public boolean getHeaderOnServer() {return this.headerOnServer;}
    public boolean getHeaderLocal() {return this.headerLocal;}
    public String getIconString() {
        return this.icon;
    }
    public Bitmap getIconBitmap() {

        byte[] decodedString = Base64.decode(this.icon, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return decodedByte;
    }
    public String getHeaderBitmapFile() {
        return this.headerBitmapFile;
    }

    //setter Methoden
    public void setID(int ID){
    	this.ID = ID;
    }
    public void setConfFolder(String foldername){
    	this.folder = folder;
    }
    public void setConfFolder(){
        this.folder = VariousMethods.getDirectoriable(String.valueOf(this.startDate[0])+String.valueOf(this.startDate[1])+String.valueOf(this.startDate[2])+"_" + this.name);
    }

    public void setSearchText(String searchText){
    	this.searchText = searchText;
    }
    public void setSortColumn(int sortColumn){
    	this.sortColumn = sortColumn;
    }
    public void setLinked(boolean Linked){
    	this.linked=Linked;
    }
    public void setItems(ArrayList<item> Items, int AnzahlItems){
    	this.items=Items;
    	this.itemsCounter=AnzahlItems;
    }

    public void setColors(String Prim, String PrimDark, String Secon, String SeconLight, String Accent){
        this.colorPrimary     = Prim;
        this.colorPrimaryDark = PrimDark;
        this.colorSecondary   = Secon;
        this.colorSecondaryLight = SeconLight;
        this.colorAccent      = Accent;
    }

    public void setStandard(String Identifier){
        // rest automatically generate from this.colorPrimary     = Prim;
        this.colorPrimaryDark    = VariousMethods.ColorLuminance(this.colorPrimary, -0.3);
        this.colorSecondary      = VariousMethods.ColorLuminance(this.colorPrimary,0.55);
        this.colorSecondaryLight = VariousMethods.ColorLuminance(this.colorPrimary,0.65);
        this.colorAccent         = VariousMethods.ColorComplementary(this.colorPrimary);
        this.confId = Identifier;

        this.searchText = "";
        this.sortColumn = 4;
        this.linked=false;
        this.isFull=false;
        this.itemsCounter=0;
        this.isRecentConf=false;
        this.maxItems=10000;
        this.showAtPoster=true;
        this.showAtContacts=true;
        this.topics = new Topics();
        this.setConfFolder();
    }

    public void setHeader(String encodedBitmap, Activity act){

        byte[] data = Base64.decode(encodedBitmap,0);
        File file = new File(VariousMethods.getMainFolder(act)+this.getConfFolder(),"header.png");
        try {
            FileOutputStream stream = new FileOutputStream(file);
            stream.write(data);
            stream.close();
            this.headerBitmapFile = VariousMethods.getMainFolder(act)+this.getConfFolder()+ "/header.png";
            this.headerLocal = true;
        }catch (IOException ex) {
            ex.printStackTrace();
            this.headerLocal = false;
        }


    }


    /*public void setCustombitmap(Bitmap custombitmap){
    	try{
			this.custombitmap.recycle();
        }catch(RuntimeException e){

        }
    	this.custombitmap=custombitmap;

    }*/

    //special methods for itemlist
    public void setItem(item Item, int numberOfItem){
    	this.items.set(numberOfItem,Item);
    }

    public item getItem(int numberOfItem){
    	return this.items.get(numberOfItem);
    }
    
    public void addItem(item Item){
    	if (isRecentConf){
    		if (this.itemsCounter==this.maxItems){
    			this.isFull=true;
    			this.itemsCounter=0;
    		}
    		
			this.items.set(this.itemsCounter,Item);
			this.items.get(this.itemsCounter).setId(this.itemsCounter);
    		this.itemsCounter+=1;
    	} else {
    		if (this.itemsCounter==this.maxItems){
    			this.isFull=true;
    		}else{
    			this.items.set(this.itemsCounter,Item);
        		this.itemsCounter+=1;
    		}
    		
    	}
    }
    
}
