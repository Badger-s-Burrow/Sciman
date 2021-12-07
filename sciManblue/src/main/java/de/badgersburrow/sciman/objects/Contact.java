package de.badgersburrow.sciman.objects;

import java.io.Serializable;

@SuppressWarnings("serial") //with this annotation we are going to hide compiler warning
public class Contact implements Serializable{
	
	private String firstName;
	private String lastName;
	private String institute;
	private String organization;
	private String homepage;
	private String emailAdress;
	private String phoneNumber;
	private String mobileNumber;
	private String pictureFile;
	private int rotation;
	private Topics researchTopics;
	private int id;
	
	
	public Contact(String FirstName, String LastName, String Institute, String Organization, String EmailAdress,
			String PhoneNumber, String MobileNumber, String PictureFile, int Rotation, Topics ResearchTopics, int Id){
		this.firstName = FirstName;
		this.lastName = LastName;
		this.institute = Institute;
		this.organization = Organization;
		this.emailAdress = EmailAdress;
		this.phoneNumber = PhoneNumber;
		this.mobileNumber = MobileNumber;
		this.pictureFile = PictureFile;
		this.rotation = Rotation;
		this.researchTopics = ResearchTopics;
		this.id = Id;
		
	}
	
	
	
	//getter Methoden
    public String getFirstName(){
        return firstName;
    }
    public String getLastName(){
        return lastName;
    }
    public String getInstitute(){
        return institute;
    }
    public String getOrganization(){
        return organization;
    }
    public String getHomepage(){
        return homepage;
    }
    public String getEmailAdress(){
        return emailAdress;
    }
    public String getPhoneNumber(){
        return phoneNumber;
    }
    public String getMobileNumber(){
        return mobileNumber;
    }
    public String getPictureFile(){
        return pictureFile;
    }
    public int getRotation(){
        return rotation;
    }
    public Topics getResearchTopics(){
        return researchTopics;
    }
    public int getId(){
        return id;
    }
    
    
    public void setId(int Id){
        this.id =Id;
    }
}
