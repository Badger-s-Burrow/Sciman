package de.badgersburrow.sciman.objects;

import java.io.Serializable;

@SuppressWarnings("serial") //with this annotation we are going to hide compiler warning
public class item implements Serializable{
	
	private String author;
	private String title;
	private String year;
	private String journal;
	private String abstracte;
	private String type;
	private String bibtexkey;
	private String doi;
	private String file;
	private String timestamp;
	private int id;
	
	
public	item(String Bibtexkey, String Type, String Author,String Title,String Year,String Abstract, String Journal, String doi, String File,String Timestamp,int Id){
		this.bibtexkey = Bibtexkey;
		this.type = Type;
		this.author = Author;
		this.title = Title;
		this.year = Year;
		this.abstracte = Abstract;
		this.journal = Journal;
		this.file = File;
		this.timestamp = Timestamp;
		this.doi = doi;
		this.id = Id;
	}
	
	//getter Methoden
	public String getType(){
        return this.type;
    }	
    public String getAuthor(){
        return this.author;
    }
    public String getTitle(){
        return this.title;
    }
    public String getYear(){
        return this.year;
    }
    
    public String getAbstract(){
        return this.abstracte;
    }
    
    public String getBibtexkey(){
        return this.bibtexkey;
    }
    
    public String getJournal(){
        return this.journal;
    }
    public String getDoi(){
        return this.doi;
    }
    public String getFile(){
        return this.file;
    }
    public String getTimestamp(){
        return this.timestamp;
    }
    public int getId(){
        return this.id;
    }

    public void setId(int ID){
        this.id = ID;
    }
}
