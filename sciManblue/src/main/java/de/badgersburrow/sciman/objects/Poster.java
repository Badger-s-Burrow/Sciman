package de.badgersburrow.sciman.objects;

import java.io.Serializable;

@SuppressWarnings("serial") //with this annotation we are going to hide compiler warning
public class Poster implements Serializable{
	
	private String mainauthorfirstname;
	private String mainauthorlastname;
	private String coauthor;
	private String title;
	private String abstracte;	
	private String[] references;
	private String imagefile;
	private int rotation;
	private int id;
	
	
	public Poster(String Mainauthorfirstname, String Mainauthorlastname, String Coauthor,String Title,String Abstract, String[] References, String Imagefile,int Rotation,int Id){
		this.mainauthorfirstname = Mainauthorfirstname;
		this.mainauthorlastname = Mainauthorlastname;
		this.coauthor = Coauthor;
		this.title = Title;
		this.abstracte = Abstract;
		this.references = References;
		this.imagefile = Imagefile;
		this.rotation = Rotation;
		this.id = Id;
	}
	
	//getter Methoden
	public String getMainauthorfirstname(){
        return mainauthorfirstname;
    }	
    public String getMainauthorlastname(){
        return mainauthorlastname;
    }
    public String getTitle(){
        return title;
    }
    public String getCoauthor(){
        return coauthor;
    }
    
    public String getAbstract(){
        return abstracte;
    }
    
    public String[] getReferences(){
        return references;
    }

    public String getImagefile(){
        return imagefile;
    }
    
    public int getRotation(){
        return rotation;
    }
    public int getId(){
        return id;
    }

    public void setId(int ID){
        this.id = ID;
    }
}

//in principal here setter methods are needed too, because it is created by hand,
//therefore errors might arise -> problem: recent posters are saved separately and not changed
//links would be better, even more important: posters linked to a contact, will not be changed too.
